(ns forgotten.domain.repository.fare
  (:use [clojure.set])
  (:require [digest :refer [md5]]
            [clj-time.local :refer [local-now]]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.infrastructure.fare :as db]
            [forgotten.domain.entity.fare :refer [map->Fare ->Fare]]))

;;;; support

(defn set-fare-id
  "fare_idがなければ新しくセットする"
  [fare-map]
  
  (if (= (:fare_id fare-map) "")
    
    (let [fare_id (md5 (str (local-now) (:user_id fare-map) (:date fare-map) (:purpose fare-map)))]
      (assoc fare-map :fare_id fare_id))
    
    fare-map))


(defn ->entities
  "mapをエンティティに変換"
  [fare-maps]
  
  (map (fn [fare-map]
         (map->Fare (set-fare-id fare-map)))
       fare-maps))


;;;; main
(defn find-list
  "交通費入力月取得"
  [user_id]
  
  (try+
   
   (if-let [results (db/find-list-by-uid user_id)]
     ;; DBに勤怠があればDBのレコードからエンティティ作成
     results)
   
   (catch [:type :forgotten.infrastructure.fare/fatal-sql-error] {:keys [error]}
     (throw+ {:type ::fatal-sql-error :error error}))))



(defn find-fare
  "交通費情報取得"
  [user_id period]
  
  (try+
   
   (if-let [results (db/find-by-uid-period user_id period)]
     ;; DBに勤怠があればDBのレコードからエンティティ作成
     (->entities results))
   
   (catch [:type :forgotten.infrastructure.fare/fatal-sql-error] {:keys [error]}
     (throw+ {:type ::fatal-sql-error :error error}))))


(defn save-fare
  "交通費保存"
  [fares]
  
  (let [entities (->entities fares)]
    
    (try+
     
     (map (fn [entity]
            
            (let [fare_id (:fare_id entity)
                  result (db/fare-count fare_id)]
              
              (if (zero? result)
                ;; DB勤怠がなければinsert
                (db/insert! entity)
                
                ;; DBに勤怠があればupdate
                (db/update! entity))))
          
          entities)
     
     (catch [:type :forgotten.infrastructure.fare/fatal-sql-error] {:keys [error]}
       (throw+ {:type ::fatal-sql-error :error error})))))



(defn delete-fare
  "交通費削除"
  [fare_id]
  
  (try+
   
   (if-let [result (db/fare-count fare_id)]
     ;; DB勤怠があればdelete
     (db/delete! fare_id))
   
   (catch [:type :forgotten.infrastructure.fare/fatal-sql-error] {:keys [error]}
     (throw+ {:type ::fatal-sql-error :error error}))))