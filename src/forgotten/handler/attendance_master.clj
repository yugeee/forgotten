(ns forgotten.handler.attendance_master
  (:require [ring.util.response :as res]
            [ring.middleware.session :as session]
            [slingshot.slingshot :refer [try+]]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.handler.login :refer [login-check]]
            [forgotten.validation.attendance_master :refer [validate]]
            [forgotten.service.attendance_master :as service]
            [forgotten.views.attendance_master :as view]
            [forgotten.util.common-message :refer [FATAL-ERROR SAVE-SUCCESS]]
            [forgotten.util.log :refer [write-log]]
            [forgotten.util.response :refer [html]]))


;;;; suport

(defn- ->map
  "リクエストボディからmapを作る"
  [user_id params]
  
  {:user_id    user_id
   :summary    (params :summary)
   :start_time (params :start_time)
   :end_time   (params :end_time)
   :rest_time  (params :rest_time)
   :work_time  (params :work_time)})


;;;; handler


(defn get-attendance-master
  "勤怠マスタ表示"
  [{:as req :keys [session]}]
  
  (try+
   
   ;; ログインチェック
   (login-check req)
   
   (let [user_id (:id (:user session))
         attendance-master (service/get-attendance-master user_id)]
     
     ;; 画面表示
     (-> (view/attendance-master-page req attendance-master)
         res/response
         (html)))
   
   ;; exception
   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error error})))
   
   (catch [:type :forgotten.service.attendance_master/fatal-sql-error] {:keys [error]}
     ;; SQL実行エラー
     (-> (res/redirect (str ROOTDIR "/index"))
         (assoc :flash {:error FATAL-ERROR})))
   
   (catch Exception error
     ;; その他の予期せぬエラー
     (write-log error)
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error FATAL-ERROR})))))


(defn post-attendance-master
  "勤怠マスタ保存"
  [{:as req :keys [session params]}]
  
  (try+
   
   ;; ログインチェック
   (login-check req)
   
   (let [user_id (:id (:user session))
         attendance-master (->map user_id params)]
     
     ;; バリデーション
     (validate attendance-master)

     ;; DB保存
     (when (count (service/save-attendance-master! attendance-master))
       (-> (res/redirect (str ROOTDIR "/attendance/master"))
           (assoc :flash {:success SAVE-SUCCESS}))))
   
   ;; exception
   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error error})))
   
   (catch [:type :forgotten.validation.attendance_master/validation-error] {:keys [error]}
     ;; バリデーションエラー
     (-> (view/attendance-master-page (assoc req :flash {:error error}) params)
         res/response
         (html)))
   
   (catch [:type :forgotten.service.attendance_master/fatal-sql-error] {:keys [error]}
     ;; SQL実行エラー
     (-> (res/redirect (str ROOTDIR "/index"))
         (assoc :flash {:error FATAL-ERROR})))
   
   (catch Exception error
     ;; その他の予期せぬエラー
     (write-log error)
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error FATAL-ERROR})))))