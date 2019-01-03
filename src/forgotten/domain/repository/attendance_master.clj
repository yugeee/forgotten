(ns forgotten.domain.repository.attendance_master
  (:require [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.infrastructure.attendance_master :as db]
            [forgotten.domain.entity.attendance_master :refer [map->Attendance-master ->Attendance-master]]))

;;;; supports

(defn create-attendance-master
  "勤怠マスタのデフォルト値"
  [user_id]
  
  (let [default {:user_id    user_id
                 :summary    "作業"
                 :start_time "09:00"
                 :end_time   "18:00"
                 :rest_time  "01:00"
                 :work_time  "08:00"}]
    
    (map->Attendance-master default)))



;;;; main

(defn find-attendance-master
  "勤怠マスタを取得。なかったらデフォルトを返却"
  [user_id]
  
  (try+
   
   (let [result (db/find-by-uid user_id)]
     
     (if (empty? result)
       
       ;; DBに勤怠がなければ新しく作成
       (create-attendance-master user_id)
       
       ;; DBに勤怠があればDBのレコードからエンティティ作成
       (map->Attendance-master (first result))))
   
   (catch [:type :forgotten.infrastructure.attendance_master/fatal-sql-error] {:keys [error]}
     (throw+ {:type ::fatal-sql-error :error error}))))



(defn save-attendance-master!
  "勤怠マスタを保存"
  [attendance-master]
  
  (try+
   
   (let [result (db/find-by-uid (:user_id attendance-master))]
     
     (if (empty? result)
       
       ;; DB勤怠がなければinsert
       (db/insert! attendance-master)
       
       ;; DBに勤怠があればupdate
       (db/update! attendance-master)))
   
   (catch [:type :forgotten.infrastructure.attendance_master/fatal-sql-error] {:keys [error]}
     (throw+ {:type ::fatal-sql-error :error error}))))
