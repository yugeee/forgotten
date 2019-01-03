(ns forgotten.validation.attendance
  (:require [bouncer.validators :as v]
            [clj-time.format :as f]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.util.time :refer [time-regex]]
            [forgotten.util.validate :as uv]))

;;;; rules

;; バリデーションルール
(def rules
  {:date       [[v/required :message (format uv/violate-required "日付")]
                [v/datetime :message (format uv/violate-format   "日付")]]
   
   :user_id    [[v/required :message "ユーザーIDがありません。"]]
   
   :weekday    [[v/required :message (format uv/violate-required "曜日")]]
   
   :start_time [[v/matches time-regex :message (format uv/violate-format "始業時間")]]
   
   :end_time   [[v/matches time-regex :message (format uv/violate-format "終業時間")]]
   
   :rest_time  [[v/matches time-regex :message (format uv/violate-format "休憩時間")]]
   
   :work_time  [[v/matches time-regex :message (format uv/violate-format "勤務時間")]]
   
   :holiday    [[v/required :message (format uv/violate-required "休日")]]})



;;;; validation

(defn validate
  "リストをループしてバリデーション実行"
  [records]
  
  (doseq [record records]
    
    (try+
     
     (uv/validate record rules)
     
     (catch [:type :forgotten.util.validate/validation-error] {:keys [error]}
       (throw+ {:type ::validation-error :error error})))))
