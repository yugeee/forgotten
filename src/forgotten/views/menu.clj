(ns forgotten.views.menu
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :as hf]
            [forgotten.util.views :as util]
            [forgotten.views.layout :as layout]))

(defn menu-page
  "メニューページ"
  [req]
  
  (->> [:div
        [:h5 "メニュー"]
        (when-let [error (:fatal (:error (:flash req)))]
         (util/error-message error))]
       (layout/template req)))