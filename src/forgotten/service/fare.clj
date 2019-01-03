(ns forgotten.service.fare
  (:require [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.domain.repository.fare :as repo]
            [forgotten.util.time :refer [current-period]]))

;;;; main
(defn get-fare-list
  "交通費入力月を取得する"
  [user_id]
  
  (try+
   
   (repo/find-list user_id)
   
   (catch [:type :forgotten.domain.repository.attendance/fatal-sql-error] {:keys [error]}
     ;; SQLエラー
     (throw+ {:type ::fatal-sql-error :error error}))))


(defn get-fare
  "今月の交通費情報を取得する"
  [user_id period]
  (try+
   (let [fares (repo/find-fare user_id period)]
     fares)
   (catch [:type :forgotten.domain.repository.fare/fatal-sql-error] {:keys [error]}
     ;; SQLエラー
     (throw+ {:type ::fatal-sql-error :error error}))))


(defn save-fare
  "交通費情報を保存する"
  [fares]
  (try+
   (repo/save-fare fares)
   (catch [:type :forgotten.domain.repository.fare/fatal-sql-error] {:keys [error]}
     ;; SQLエラー
     (throw+ {:type ::fatal-sql-error :error error}))))


(defn delete-fare
  "交通費情報を削除する"
  [fare_id]
  (try+
   (repo/delete-fare fare_id)
   (catch [:type :forgotten.domain.repository.fare/fatal-sql-error] {:keys [error]}
     ;; SQLエラー
     (throw+ {:type ::fatal-sql-error :error error}))))