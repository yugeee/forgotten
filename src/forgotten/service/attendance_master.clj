(ns forgotten.service.attendance_master
  (:require [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.domain.repository.attendance_master :as repo]))


;;;; main


(defn get-attendance-master
  "勤怠マスタを取得する"
  [user_id]
  
  (try+
   
   (repo/find-attendance-master user_id)
   
     (catch [:type :forgotten.domain.repository.attendance_master/fatal-sql-error] {:keys [error]}
       ;; SQLエラー
       (throw+ {:type ::fatal-sql-error :error error}))))



(defn save-attendance-master!
  "勤怠マスタを保存する"
  [attendance-master]
  
  (try+
   
   (repo/save-attendance-master! attendance-master)
   
     (catch [:type :forgotten.domain.repository.attendance_master/fatal-sql-error] {:keys [error]}
       ;; SQLエラー
       (throw+ {:type ::fatal-sql-error :error error}))))
