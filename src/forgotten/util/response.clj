(ns forgotten.util.response
  (:require [ring.util.response :as res]))

;;;; util

;; 
(defn html
  "content-typeの返却"
  [res]
  
  (res/content-type res "text/html; charset=utf-8"))

