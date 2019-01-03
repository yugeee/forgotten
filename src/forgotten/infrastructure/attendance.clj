(ns forgotten.infrastructure.attendance
  (:require [clojure.java.jdbc :as jdbc]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.config.db :refer [db-conn]]))

;;;; main
(defn find-list-by-uid
  "勤怠一覧取得"
  [user-id]
  
  (try+
   
   (jdbc/query db-conn
               ["SELECT DATE_FORMAT(date,'%Y-%m') as date_formed FROM attendance WHERE user_id = ? AND date LIKE '%01' ORDER BY date DESC" user-id])
   
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))

(defn find-by-uid-period
  "user-idと期間(年・月 yyyy-mm)で勤怠を取得"
  [user-id period]
  (try+
   (jdbc/query db-conn
               ["SELECT *, DATE_FORMAT(date,'%Y-%m-%d') as date_formed FROM attendance WHERE user_id = ? AND date LIKE ?" user-id (str period "%")])
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))


(defn attendance-count
  "勤怠情報の存在チェック、最初の1件だけ返す。"
  [user-id date]
  (try+
   (let [count (jdbc/query db-conn
                           ["SELECT count(*) as count FROM attendance WHERE user_id = ? AND date = ?" user-id date])]
     (:count (first count))) ;; listで返ってくるため最初の1件だけ取得
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error (ex-data e)}))))


(defn insert!
  "新しい勤怠情報を保存する"
  [attendance]
  (try+
   (jdbc/insert-multi! db-conn :attendance attendance)
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))


(defn update!
  "勤怠情報を更新する"
  [attendance]
  (try+
   (map (fn [oneday]
          (jdbc/update! db-conn
                        :attendance
                        {:date       (:date oneday)
                         :user_id    (:user_id oneday)
                         :weekday    (:weekday oneday)
                         :summary    (:summary oneday)
                         :start_time (:start_time oneday)
                         :end_time   (:end_time oneday)
                         :rest_time  (:rest_time oneday)
                         :work_time  (:work_time oneday)
                         :holiday    (:holiday oneday)}
                        ["date = ? AND user_id = ?" (:date oneday) (:user_id oneday)]))
        attendance)
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))
