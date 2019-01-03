(ns forgotten.util.data
  (:require [clojure.string :as str]
            [slingshot.slingshot :refer [try+]]))

;; manipulation
(defn vector->map
  "ヘッダをkeyにmapへ変換する"
  [body header]
  
  (map zipmap
       (->> header
            (map keyword)
            repeat)
       body))


;;;; helper
(defn fare-sum
  "総額を計算"
  [fares]
  
  (try+
   
   (reduce (fn [sum fare]
             (+ sum (:fare fare)))
           0
           fares)

   ;; バリデーションは計算できない状態になるので特に何も表示させない
   (catch Exception e
     nil)))



(defn work-time-sum
  "総勤務時間をn分で算出"
  [attendances]
  
  (try+
   
   (reduce (fn [sum attendance]
             (let [work-time (str/split (:work_time attendance) #":")
                   hour      (* (Integer/parseInt (first work-time)) 60)
                   minuite   (Integer/parseInt (second work-time))]
               
               (+ sum hour minuite)))
           0
           attendances)

   ;; バリデーションは計算できない状態になるので特に何も表示させない
   (catch Exception e
     nil)))



(defn attendances-sum
  "総勤務時間をn時間で算出"
  [attendances]
  
  (try+
   
   (let [sum     (work-time-sum attendances)
         hour    (quot sum 60)
         minuite (- sum (* hour 60))]
     (str hour "：" minuite))
   
   (catch Exception e
     nil)))