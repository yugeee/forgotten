(ns forgotten.views.attendance_master
  (:require [hiccup.core :as h]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :as hf]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.util.views :as util]
            [forgotten.views.layout :as layout]))


(defn attendance-master-page
  "勤怠マスタページ"
  [req attendance-master]
  
  (->> [:div
        [:h5 "勤怠マスタ"]

        ;; 勤怠保存の成功メッセージ
        (when-let [success (:success (:flash req))] 
          [:div.alert.alert-success [:strong success]])

        ;; バリデーションの失敗メッセージ
        (when-let [error (vals (:error (:flash req)))] 
          (util/error-message error))

        [:hr.mb-3]
        
        (hf/form-to
         [:post (str ROOTDIR "/attendance/master")]
         [:div.attendance
          
          [:div.row.justify-content-md-center
           [:div.form-group.mb-2 "作業内容："]
           [:div.form-group.mb-2
            [:input.summary.form-control.form-control-sm.input-group {:type "text" :name "summary" :value (:summary attendance-master)}]]]
          
          [:br]
          
          [:div.row.justify-content-md-center
           [:div.form-group.mb-2 "始業時間："]
           [:div.form-group.mb-2
            [:input.time.start-time.form-control.form-control-sm.input-group {:type "text" :name "start_time" :value (:start_time attendance-master)}]]]
          
          [:br]
          
          [:div.row.justify-content-md-center
           [:div.form-group.mb-2 "終業時間："]
           [:div.form-group.mb-2
            [:input.time.end-time.form-control.form-control-sm.input-group {:type "text" :name "end_time" :value (:end_time attendance-master)}]]]
          
          [:br]
          
          [:div.row.justify-content-md-center
           [:div.form-group.mb-2 "休憩時間："]
           [:div.form-group.mb-2
            [:input.time.rest-time.form-control.form-control-sm.input-group {:type "text" :name "rest_time" :value (:rest_time attendance-master)}]]]
          
          [:br]
          
          [:div.row.justify-content-md-center
           [:div.form-group.mb-2 "勤務時間："]
           [:div.form-group.mb-2
            [:input.time.work-time.form-control.form-control-sm.input-group {:type "text" :name "work_time" :value (:work_time attendance-master)}]]]]
        
        [:hr.mb-3]
        
        [:div.row.justify-content-md-center
         [:div.form-group.mb-2
          [:button.btn.btn-primary.btn-lg.btn-block {:type "submit" :align "center"} "保存"]]])]
       
        (layout/template req)))