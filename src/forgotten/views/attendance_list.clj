(ns forgotten.views.attendance_list
    (:require [hiccup.core :as h]
              [hiccup.page :refer [html5 include-css include-js]]
              [hiccup.form :as hf]
              [forgotten.config.environment :refer [ROOTDIR]]
              [forgotten.util.time :refer [current-period]]
              [forgotten.views.layout :as layout]))

;;;; view

(defn attendance-list-page
  "勤怠選択画面"
  [req periods]
  
  (->> [:div
        [:h5 "勤怠選択"]
        
        [:hr.mb-3
         [:div.container-fluid
          [:div.justify-content-center

           ;; 今月分
           [:div
            [:a {:href (str ROOTDIR "/attendance/period/" (current-period))}
             [:button#current_period.btn-warning.btn-lg.btn-block "今月分を入力"]]]


           [:br]

           (if periods
             ;; 月選択
             [:div#periods
              "入力済みデータを読み込み"
              [:select.custom-select {:name "period"}
               (map (fn [period]
                      [:option.period {:value (str ROOTDIR "/attendance/period/" (:date_formed period))} (h/h (:date_formed period))])
                    periods)]
              
              [:div
               [:button#attendance_list.btn.btn-primary.btn-lg.btn-block {:align "center" :formmethod :get :formaction (str ROOTDIR "/attendance/period")} "ロード"]]])]]]]
  
       (layout/template req)))
