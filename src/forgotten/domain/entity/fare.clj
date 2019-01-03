(ns forgotten.domain.entity.fare)

;;;; entity

;; 交通費のエンティティ DBのカラムに合わせてsnakecase
(defrecord Fare [fare_id user_id date purpose transportation departure arrival round_trip fare])