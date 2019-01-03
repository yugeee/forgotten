(ns forgotten.util.log
  (:use [clojure.java.io])
  (:require [clojure.data.json :as json]
            [taoensso.timbre.appenders.core :as appenders]
            [taoensso.timbre :as timbre :refer [error]]))

;;;; const

;; ファイルパス
(def LOG-FILE "/var/www/html/yuge-dev/log/error.txt")



;;;; logger

;; error出力
(defn write-log [msg]
  (timbre/merge-config!
    {:appenders {:spit (merge (appenders/spit-appender {:fname LOG-FILE}))}})
  (error (str msg)))

