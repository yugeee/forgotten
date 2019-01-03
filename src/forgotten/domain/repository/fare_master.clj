(ns forgotten.domain.repository.fare_master
  (:require [digest :refer [md5]]
            [clj-time.local :refer [local-now]]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.infrastructure.fare_master :as db]
            [forgotten.domain.entity.fare_master :refer [map->Fare-master]]))


;;;; supports

(defn set-fare-id
  "fare_idがなければ新しくセットする"
  [fare-master-map]
  
  (if (= (:fare_master_id fare-master-map) "")
    
    (let [fare_master_id (md5 (str (local-now) (:user_id fare-master-map) (:purpose fare-master-map)))]
      (assoc fare-master-map :fare_master_id fare_master_id))
    
    fare-master-map))



(defn- ->entities
  "mapをエンティティに変換"
  [fare-master-maps]
  
  (map (fn [fare-master-map]
         (map->Fare-master (set-fare-id fare-master-map)))
       
       fare-master-maps))



(defn- create-fare-master
  "交通費マスタのデフォルト値"
  [user_id]
  
  (let [default (list {:fare_master_id ""
                       :user_id user_id
                       :purpose ""
                       :transportation ""
                       :departure ""
                       :arrival ""
                       :round_trip false})]
    
    (->entities default)))



;;;; main

(defn find-fare-master
  "交通費マスタを取得。なかったらデフォルトを返却"
  [user_id]
  
  (try+
   
   (let [result (db/find-by-uid user_id)]
     
     (if (empty? result)
       
       (create-fare-master user_id)
       
       (->entities result)))
   
   (catch [:type :forgotten.infrastructure.fare_master/fatal-sql-error] {:keys [error]}
     (throw+ {:type ::fatal-sql-error :error error}))))



(defn save-fare-master
  "交通費マスタ保存"
  [fare-masters]
  
  (let [entities (->entities fare-masters)]
    
    (try+
     
     (map (fn [entity]
            
            (let [fare_master_id (:fare_master_id entity)
                  result (db/fare-master-count fare_master_id)]
              
              ;; TODO 新規と更新が混ざる場合があるので1行ずつ行いたい
              (if (zero? result)
                ;; DB勤怠がなければinsert
                (db/insert! entity)
                
                ;; DBに勤怠があればupdate
                (db/update! entity))))
          
          entities)
     
     (catch [:type :forgotten.infrastructure.fare_master/fatal-sql-error] {:keys [error]}
       (throw+ {:type ::fatal-sql-error :error error})))))


(defn delete-fare-master
  "交通費マスタ削除"
  [fare_master_id]
  
  (try+
   
   (if-let [result (db/fare-master-count fare_master_id)]
     ;; DB勤怠があればdelete
     (db/delete! fare_master_id))
   
   (catch [:type :forgotten.infrastructure.fare_master/fatal-sql-error] {:keys [error]}
     (throw+ {:type ::fatal-sql-error :error error}))))