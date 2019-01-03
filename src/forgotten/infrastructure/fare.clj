(ns forgotten.infrastructure.fare
  (:require [clojure.java.jdbc :as jdbc]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.config.db :refer [db-conn]]))


;;;; main
(defn find-list-by-uid
  "交通費入力月取得"
  [user_id]
  
  (try+
   
   (jdbc/query db-conn
               ["SELECT DISTINCT DATE_FORMAT(date,'%Y-%m') as date_formed FROM fare WHERE user_id = ? ORDER BY date_formed DESC" user_id])
   
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))



(defn find-by-uid-period
  "user_idと期間(年・月 yyyy-mm)でを交通費情報を取得"
  [user_id period]
  
  (try+
   
   (jdbc/query db-conn
               ["SELECT * FROM fare WHERE user_id = ? AND date LIKE ? ORDER BY DATE ASC" user_id (str period "%")])
   
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))



(defn fare-count
  "交通費情報の存在チェック、最初の1件だけ返す。"
  [fare_id]
  
  (try+
   
   (let [count (jdbc/query db-conn
                           ["SELECT count(*) as count FROM fare WHERE fare_id = ?" fare_id])]
     ;; listで返ってくるため最初の1件だけ取得
     (:count (first count)))
   
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error (ex-data e)}))))



(defn insert!
  "新しい交通費を保存する"
  [fare]
  
  (try+
   
   (jdbc/insert! db-conn :fare fare)
   
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))



(defn update!
  "交通費情報を更新する"
  [fare]
  
  (try+
   
   (jdbc/update! db-conn :fare fare ["fare_id = ?"(:fare_id fare)])
   
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))



(defn delete!
  "交通費削除をする"
  [fare_id]
  
  (try+
   
   (jdbc/delete! db-conn :fare ["fare_id = ?" fare_id])
   
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))