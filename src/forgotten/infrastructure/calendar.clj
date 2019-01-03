(ns forgotten.infrastructure.calendar
  (:require [org.httpkit.client :as http]
            [clojure.data.csv :as csv]
            [forgotten.util.time :refer [current-year current-month ]]))


;;;; var

;; カレンダーAPIのURL

(def REQUEST-URL
  (format "http://calendar-service.net/cal?start_year=%s&start_mon=%s&end_year=&end_mon=&year_style=normal&month_style=numeric&wday_style=ja&format=csv&zero_padding=1"
          (current-year)    ;; start_year=%s に現在の西暦が入る
          (current-month))) ;; start_mon=%s に現在の月が入る



;;;; curl

(defn get-calendar
  "カレンダーをHTTPでget"
  []
  (let [cal (:body @(http/get REQUEST-URL))]
    (csv/read-csv cal)))