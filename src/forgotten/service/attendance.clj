(ns forgotten.service.attendance
  (:require [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.domain.repository.attendance :as attendance]
            [forgotten.domain.repository.attendance_master :as attendance_master]
            [forgotten.util.time :refer [current-period]]))


;;;; main
(defn get-attendance-list
  "勤怠一覧を取得する"
  [user_id]
  
  (try+
   
   (attendance/find-list user_id)
   
   (catch [:type :forgotten.domain.repository.attendance/fatal-sql-error] {:keys [error]}
     ;; SQLエラー
     (throw+ {:type ::fatal-sql-error :error error}))))



(defn get-attendance
  "勤怠を取得する"
  [user_id period]
  
  (try+
   
   (let [attendance_master (attendance_master/find-attendance-master user_id)]
     (attendance/find-attendance user_id attendance_master period))
   
   (catch [:type :forgotten.domain.repository.attendance/fatal-sql-error] {:keys [error]}
     ;; SQLエラー
     (throw+ {:type ::fatal-sql-error :error error}))))



(defn save-attendance!
  "勤怠情報保存"
  [user_id attendance]
  
  (try+
   
   (attendance/save-attendance! user_id attendance)

   (catch [:type :forgotten.domain.repository.attendance/fatal-sql-error] {:keys [error]}
     ;; SQLエラー
     (throw+ {:type ::fatal-sql-error :error error}))))
