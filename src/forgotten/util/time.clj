(ns forgotten.util.time
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.local :as l]))

;;;; const

;; 曜日
(def ^:const weekday
  {"1" "月"
   "2" "火"
   "3" "水"
   "4" "木"
   "5" "金"
   "6" "土"
   "0" "日"})

;; 休日判断のFLG
(def ^:const SATURDAY "6")
(def ^:const SUNDAY   "0")
(def ^:const HOLIDAY  1)




;;;; regex

;; 00:00~99:59 まで可能
(def time-regex #"[0-9]{2}:[0-5][0-9]")



;;;; helper

(defn current-year
  "現在の年"
  []
  (f/unparse (f/formatter "yyyy") (l/local-now)))



(defn current-month
  "現在の月"
  []
  (f/unparse (f/formatter "MM") (l/local-now)))



(defn parse-year-month
  "yyyy-MM のフォーマットにする"
  [date]
  (f/unparse (f/formatter "yyyy-MM") date))



(defn current-period
  "現在の年と月を返す"
  []
  (parse-year-month (l/local-now)))



(defn last-period
  "前月の年と月を返す"
  []
  (parse-year-month (t/minus (l/local-now) (t/months 1))))



(defn next-period
  "次月の年と月を返す"
  []
  (parse-year-month (t/plus (l/local-now) (t/months 1))))



(defn today
  "本日の日付を返却"
  []
  (f/unparse (f/formatter "yyyy-MM-dd") (l/local-now)))



(defn weekday-reversal-kv
  "weekday の key value を入れ替える(エンティティするときに用いる)"
  []
  (reduce-kv #(assoc %1 %3 %2) {} weekday))



(defn holiday?
  "土日祝をチェックしてクラスを確定する"
  [one-day]
  (if (or
       (= (:weekday one-day) SATURDAY)
       (= (:weekday one-day) SUNDAY)
       (= (:holiday one-day) HOLIDAY))
    true
    false))
