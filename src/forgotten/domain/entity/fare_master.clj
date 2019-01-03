(ns forgotten.domain.entity.fare_master)

;;;; entity

;; 交通費マスタのエンティティ DBのカラムに合わせてsnakecase
(defrecord Fare-master [fare_master_id user_id purpose transportation departure arrival round_trip fare])