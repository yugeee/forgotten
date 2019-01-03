(ns forgotten.handler.submit
  (:require [ring.util.response :as res]
            [slingshot.slingshot :refer [try+]]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.handler.login :refer [login-check]]
            [forgotten.service.mail :as mail-service]
            [forgotten.service.attendance :as attendance-service]
            [forgotten.service.fare :as fare-service]
            [forgotten.views.submit :as view]
            [forgotten.views.attendance_pdf :refer [attendances->pdf]]
            [forgotten.views.fare_pdf :refer [fares->pdf]]
            [forgotten.validation.fare :refer [validate]]
            [forgotten.util.validate :refer [violate-format]]
            [forgotten.util.common-message :refer [FATAL-ERROR SEND-SUCCESS]]
            [forgotten.util.log :refer [write-log]]
            [forgotten.util.response :refer [html]]))


;;;; handler

(defn get-submit
  "提出画面表示"
  [{:as req :keys [session]}]

  (try+
   
   ;; ログインチェック
   (login-check req)

   (let [user_id     (:id (:user session))
         attendances (attendance-service/get-attendance-list user_id)
         fares       (fare-service/get-fare-list user_id)]
     
     ;; 画面表示
     (-> (view/submit-page req attendances fares)
         res/response
         (html)))
   
   ;; exception
   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error error})))))
  
  

(defn post-submit
  "提出処理"
  [{:as req :keys [session params]}]

  (try+
   
   ;; ログインチェック
   (login-check req)

   (let [user            (:user session)
         period          (:period params)
         attendances     (attendance-service/get-attendance (:id user) period)
         fares           (fare-service/get-fare (:id user) period)
         attendance-path (attendances->pdf attendances user period)
         fare-path       (fares->pdf fares user period)]
     
     (if (= (:code (mail-service/submit! user attendance-path fare-path)) 0)
       ;; 送信成功時に完了メッセージ表示
       (do
         (-> (res/redirect (str ROOTDIR "/submit"))
             (assoc :flash {:success SEND-SUCCESS})))))
   
   ;; exception
   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error error})))

   (catch [:type :forgotten.views.attendance_pdf/attendance-not-found] {:keys [error]}
     ;; 勤怠が見つからなかった場合
     (-> (res/redirect (str ROOTDIR "/submit"))
         (assoc :flash {:error error})))

   (catch [:type :forgotten.views.fare_pdf/fare-not-found] {:keys [error]}
     ;; 交通費が見つからなかった場合
     (-> (res/redirect (str ROOTDIR "/submit"))
         (assoc :flash {:error error})))

   (catch [:type :forgotten.views.attendance_pdf/pdf-fatal-error] {:keys [error]}
     ;; PDF作成エラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))

   (catch [:type :forgotten.views.fare_pdf/pdf-fatal-error] {:keys [error]}
     ;; PDF作成エラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))
   
   (catch [:type :forgotten.service.mail/mail-send-error] {:keys [error]}
     ;; メール送信エラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))))