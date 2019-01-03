(ns forgotten.views.submit
  (:use [clojure.data])
  (:require [hiccup.core :as h]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :as hf]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.util.time :refer [current-year current-month holiday? weekday]]
            [forgotten.util.views :as util]
            [forgotten.views.layout :as layout]))


;;;; view

(defn submit-page
  "提出ページ"
  [req attendances fares]
  
  (->> [:div
        [:h5 "提出"]
        
        ;; 勤怠保存の成功メッセージ
        (when-let [success (:success (:flash req))] 
          [:div.alert.alert-success [:strong success]])
        
        ;; バリデーションの失敗メッセージ
        (when-let [error (:error (:flash req))] 
          (util/error-message error))

        [:hr.mb-3
         [:div.container-fluid
          [:div.justify-content-center

           (if (not (empty? attendances))
             
             (hf/form-to
              [:post (str ROOTDIR "/submit")]
              
              [:select.custom-select {:name "period"}
               (map (fn [attendance]
                      
                      [:option.period {:value (:date_formed attendance)} (h/h (:date_formed attendance))])
                    
                    attendances)]
              
              [:br]
              
              [:button.btn.btn-primary.btn-lg.btn-block  "提出"])


             [:div.alert.alert-warning {:role "alert"} "勤怠と交通費が何も入力されていません。"])]]]]
       
       (layout/template req)))