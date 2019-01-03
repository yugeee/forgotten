(ns forgotten.infrastructure.fare_master
  (:require [clojure.java.jdbc :as jdbc]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.config.db :refer [db-conn]]))

;;; main

(defn find-by-uid
  "user_id から交通費マスタを取得"
  [user_id]
  
  (try+
   
   (jdbc/query db-conn
               ["SELECT * FROM fare_master WHERE user_id = ?" user_id])
   
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))



(defn fare-master-count
  "交通費情報の存在チェック、最初の1件だけ返す。"
  [fare_master_id]
  
  (try+
   
   (let [count (jdbc/query db-conn
                           ["SELECT count(*) as count FROM fare_master WHERE fare_master_id = ?" fare_master_id])]
     ;; listで返ってくるため最初の1件だけ取得
     (:count (first count)))

   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error (ex-data e)}))))


(defn insert!
  "新しい交通費マスタを保存する"
  [fare-master]
  
  (try+
   
   (jdbc/insert! db-conn :fare_master fare-master)
   
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))



(defn update!
  "交通費マスタを更新する"
  [fare-master]
  
  (try+
   
   (jdbc/update! db-conn :fare_master fare-master ["fare_master_id = ?"(:fare_master_id fare-master)])
   
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))



(defn delete!
  "交通費マスタ削除をする"
  [fare_master_id]
  
  (try+
   
   (jdbc/delete! db-conn :fare_master ["fare_master_id = ?" fare_master_id])
   
   (catch Exception e
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error e}))))