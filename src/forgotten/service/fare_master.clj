(ns forgotten.service.fare_master
  (:require [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.domain.repository.fare_master :as repo]
            [forgotten.util.time :refer [current-period]]))

;;;; main

(defn get-fare-master
  "今月の交通費マスタを取得する"
  [user_id]
  
  (try+
   
   (let [fare-masters (repo/find-fare-master user_id)]
     fare-masters)
   
   (catch [:type :forgotten.domain.repository.fare_master/fatal-sql-error] {:keys [error]}
     ;; SQLエラー
     (throw+ {:type ::fatal-sql-error :error error}))))



(defn save-fare-master
  "交通費マスタを保存する"
  [fare-masters]
  
  (try+
   
   (repo/save-fare-master fare-masters)
   
   (catch [:type :forgotten.domain.repository.fare_master/fatal-sql-error] {:keys [error]}
     ;; SQLエラー
     (throw+ {:type ::fatal-sql-error :error error}))))



(defn delete-fare-master
  "交通費マスタを削除する"
  [fare_master_id]
  
  (try+
   
   (repo/delete-fare-master fare_master_id)
   
   (catch [:type :forgotten.domain.repository.fare_master/fatal-sql-error] {:keys [error]}
     ;; SQLエラー
     (throw+ {:type ::fatal-sql-error :error error}))))