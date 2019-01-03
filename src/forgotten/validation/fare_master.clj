(ns forgotten.validation.fare_master
  (:require [bouncer.validators :as v]
            [clj-time.format :as f]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.util.validate :as uv]))

;;;; rules

;; バリデーションルール
(def rules
  {:user_id        [[v/required :message "ユーザーIDがありません。"]]
   
   :purpose        [[v/required :message (format uv/violate-required "訪問先・目的")]]
   
   :transportation [[v/required :message (format uv/violate-required "交通手段")]]
   
   :departure      [[v/required :message (format uv/violate-required "出発")]]
   
   :arrival        [[v/required :message (format uv/violate-required "到着")]]
   
   :round_trip     [[v/required :message (format uv/violate-required "片/往")]
                    [v/boolean  :message (format uv/violate-format   "片/往")]]
   
   :fare           [[v/required :message (format uv/violate-required "金額")]]})



;;;; validation

(defn validate
  "リストをループしてバリデーション実行"
  [fares]
  
  (doseq [fare fares]
    
    (try+
     
     (uv/validate fare rules)
     
     (catch [:type :forgotten.util.validate/validation-error] {:keys [error]}
       (throw+ {:type ::validation-error :error error})))))

