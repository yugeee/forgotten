(ns forgotten.views.attendance
  (:require [hiccup.core :as h]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :as hf]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.util.data :refer [attendances-sum]]
            [forgotten.util.time :refer [holiday? weekday]]
            [forgotten.util.views :as util]
            [forgotten.views.layout :as layout]))

;;;; view

(defn attendance-page
  "勤怠表"
  [req attendances]

  (let [attendance (first attendances)
        date       (:date attendance)
        year       (subs date 0 4)
        month      (subs date 5 7)]

    (->> [:div
          [:h5 "勤怠表"]
          [:h6 (str year "年" month "月度勤怠表")]
          (when-let [success (:success (:flash req))] ;; 勤怠保存の成功メッセージ
            [:div.alert.alert-success [:strong success]])
          
          (when-let [error (vals (:error (:flash req)))] ;; バリデーションの失敗メッセージ
            (util/error-message error))
          
          [:hr.mb-3
           [:div.container-fluid
            [:div.justify-content-center
             (hf/form-to
              [:post (str ROOTDIR "/attendance")]
              
              [:table.table.table-hover.table-bordered.col-12
               
               [:thead.thead-light
                [:tr
                 [:th "休日"]
                 [:th "日"]
                 [:th "曜日"]
                 [:th "作業内容"]
                 [:th "始業時間"]
                 [:th "終業時間"]
                 [:th "休憩時間"]
                 [:th "勤務時間"]]]
               
               [:tbody#attendances
                (map (fn [attendance]
                       
                       [:tr.attendance {:class (if (holiday? attendance) "table-secondary")}
                        
                        ;; 休日かどうかのチェック
                        [:td [:input.holiday {:type "checkbox" :checked (if (holiday? attendance) "checked")}]] ;; 休日ならchecked
                        [:input.holiday-value  {:type "hidden" :name "attendance[][holiday]" :value (if (holiday? attendance) 1 0)}]
                        
                        ;; 日付
                        [:td (subs (str (:date attendance)) 8)] ;;8文字目から抜き出し ex 2018-11-01 -> 01
                        [:input.date {:type "hidden" :name "attendance[][date]" :value (:date attendance) :readonly "true"}]
                        
                        ;; 曜日
                        [:td (h/h (weekday (str (:weekday attendance))))]
                        [:input.weekday.form-control.form-control-sm.input-group {:type "hidden" :name "attendance[][weekday]" :value (h/h (:weekday attendance))}]
                        
                        ;; 作業概要
                        [:td [:input.summary.form-control.form-control-sm.input-group {:type "text" :name "attendance[][summary]" :value (h/h (:summary attendance))}]]
                        
                        ;; 始業時間
                        [:td [:input.time.start-time.form-control.form-control-sm.input-group {:type "text" :name "attendance[][start-time]" :value (h/h (:start_time attendance)) :readonly (if (= (:start_time attendance) "00:00") "true")}]]
                        
                        ;; 終業時間
                        [:td [:input.time.end-time.form-control.form-control-sm.input-group {:type "text" :name "attendance[][end-time]" :value (h/h (:end_time attendance)) :readonly (if (= (:end_time attendance) "00:00") "true")}]]
                        
                        ;; 休憩
                        [:td [:input.time.rest-time.form-control.form-control-sm.input-group {:type "text" :name "attendance[][rest-time]" :value (h/h (:rest_time attendance)) :readonly (if (= (:rest_time attendance) "00:00") "true")}]]
                        
                        ;; 勤務時間
                        [:td [:input.time.work-time.form-control.form-control-sm.input-group {:type "text" :name "attendance[][work-time]" :value (h/h (:work_time attendance)) :readonly (if (= (:work_time attendance) "00:00") "true")}]]]) attendances)]
               
               [:tfoot
                [:tr.attendance
                 [:td {:colspan 1} "合計"]
                 [:td {:colspan 6}]
                 ;; 総額
                 [:td#work_time_sum (str (attendances-sum attendances))]]]]
              
              [:button.btn.btn-primary.btn-lg.btn-block {:type "submit" :align "center"} "保存"])]]]]
         
         (layout/template req))))
