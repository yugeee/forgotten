(ns forgotten.infrastructure.attendance_master
  (:require [clojure.java.jdbc :as jdbc]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.config.db :refer [db-conn]]))

;;;; main


(defn find-by-uid
  "user-id から勤怠マスタを取得"
  [user-id]
  (try+
   (jdbc/query db-conn
               ["SELECT * FROM attendance_master WHERE user_id = ?" user-id])
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))


(defn insert!
  "新しい勤怠情報を保存する"
  [attendance-master]
  (try+
   (jdbc/insert! db-conn :attendance_master attendance-master)
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))



(defn update!
  "勤怠情報を更新する"
  [attendance-master]
  (try+
   (jdbc/update! db-conn :attendance_master
                 {:user_id    (:user_id    attendance-master)
                  :summary    (:summary    attendance-master)
                  :start_time (:start_time attendance-master)
                  :end_time   (:end_time   attendance-master)
                  :rest_time  (:rest_time  attendance-master)
                  :work_time  (:work_time  attendance-master)}
                 ["user_id = ?" (:user_id  attendance-master)])
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))
