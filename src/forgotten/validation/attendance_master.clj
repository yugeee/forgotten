(ns forgotten.validation.attendance_master
  (:require [bouncer.validators :as v]
            [clj-time.format :as f]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.util.time :refer [time-regex]]
            [forgotten.util.validate :as uv]))

;;;; rules

;; バリデーションルール
(def attendance-master-validation
  {:user_id    [[v/required :message "ユーザーIDがありません。"]]
   
   :start_time [[v/matches time-regex :message (format uv/violate-format "始業時間")]]
   
   :end_time   [[v/matches time-regex :message (format uv/violate-format "終業時間")]]
   
   :rest_time  [[v/matches time-regex :message (format uv/violate-format "休憩時間")]]
   
   :work_time  [[v/matches time-regex :message (format uv/violate-format "勤務時間")]]})



;;;; validation


(defn validate
  "バリデーション実行"
  [record]
  
  (try+
   
   (uv/validate record attendance-master-validation)
   
   (catch [:type :forgotten.util.validate/validation-error] {:keys [error]}
     (throw+ {:type ::validation-error :error error}))))