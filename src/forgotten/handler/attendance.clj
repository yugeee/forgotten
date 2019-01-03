(ns forgotten.handler.attendance
  (:require [ring.util.response :as res]
            [slingshot.slingshot :refer [try+]]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.handler.login :refer [login-check]]
            [forgotten.validation.attendance :refer [validate]]
            [forgotten.service.attendance :as service]
            [forgotten.views.attendance :as views]
            [forgotten.views.attendance_list :refer [attendance-list-page]]
            [forgotten.util.common-message :refer [FATAL-ERROR SAVE-SUCCESS]]
            [forgotten.util.time :refer [current-period weekday-reversal-kv]]
            [forgotten.util.log :refer [write-log]]
            [forgotten.util.response :refer [html]]))

;;;; const

;; mapを作るキーになるもの
(def ^:const attendance-keys [:date :user_id :weekday :summary :start_time :end_time :rest_time :work_time :holiday])


;;;; suport


(defn- ->map
  "リクエストボディからmapを作る"
  [user_id params]
  
  (let [dates       (params "attendance[][date]")
        weekdays    (params "attendance[][weekday]")
        summaries   (params "attendance[][summary]")
        start-times (params "attendance[][start-time]")
        end-times   (params "attendance[][end-time]")
        rest-times  (params "attendance[][rest-time]")
        work-times  (params "attendance[][work-time]")
        holidays    (params "attendance[][holiday]")
        weekmap     (weekday-reversal-kv)]
    
    (map (fn [date weekday summary start-time end-time rest-time work-time holiday]

           (let [values [date user_id weekday summary start-time end-time rest-time work-time holiday]]
           ;; mapへ変換
           (zipmap attendance-keys values)))
    
    dates weekdays summaries start-times end-times rest-times work-times holidays)))




;;;; handler
(defn get-attendance-list
  "勤怠選択画面表示"
  [{:as req :keys [session]}]

  (try+

   ;; ログインチェック
   (login-check req)
   
   (let [user_id (:id (:user session))
         list (service/get-attendance-list user_id)]
     
     ;; 画面表示
     (-> (attendance-list-page req list)
         res/response
         (html)))
   
   ;; exception
   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error error})))
   
   (catch [:type :forgotten.service.attendance/fatal-sql-error] {:keys [error]}
     ;; SQL実行エラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))

   (catch Exception error
     ;; その他の予期せぬエラー
     (write-log error)
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))))

(defn get-attendance
  "勤怠入力画面表示"
  [{:as req :keys [session params]}]
  
  (try+

   ;; ログインチェック
   (login-check req)
   
   (let [user_id  (:id (:user session))
         period   (if (empty? (:period params)) (current-period) (:period params))
         attendances (service/get-attendance user_id period)]
     
     ;; 画面表示
     (-> (views/attendance-page req attendances)
         res/response
         (html)))
   
   ;; exception
   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error error})))
   
   (catch [:type :forgotten.service.attendance/fatal-sql-error] {:keys [error]}
     ;; SQL実行エラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))

   (catch Exception error
     ;; その他の予期せぬエラー
     (write-log error)
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))))



(defn post-attendance
  "勤怠保存・保存後は入力画面へ"
  [{:as req :keys [session params]}]
  
  (let [user_id    (:id (:user session))
        attendances (->map user_id params)
        period   (-> (first attendances)
                     (:date)
                     (subs 0 7))]
    
    (try+

     ;; ログインチェック
     (login-check req)
     
     ;; バリデーション
     (validate attendances)
     
     ;; DB保存。成功なら入力画面へ
     (when (count (service/save-attendance! user_id attendances))
        (-> (res/redirect (str ROOTDIR "/attendance/period/" period))
            (assoc :flash {:success SAVE-SUCCESS})))
     
     ;; exception
     (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
       ;; ログインエラー
       (-> (res/redirect (str ROOTDIR "/login"))
           (assoc :flash {:error error})))
     
     (catch [:type :forgotten.validation.attendance/validation-error] {:keys [error]}
       ;; バリデーションエラー
       (-> (views/attendance-page (assoc req :flash {:error error}) (->map user_id params))
           res/response
           (html)))
     
     (catch [:type :forgotten.service.attendance/fatal-sql-error] {:keys [error]}
       ;; SQL実行エラー
       (-> (res/redirect (str ROOTDIR "/index"))
           (assoc :flash {:error {:fatal FATAL-ERROR}})))

     (catch Exception error
     ;; その他の予期せぬエラー
     (write-log error)
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}}))))))
