(ns forgotten.views.fare_master
  (:require [hiccup.core :as h]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :as hf]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.util.views :as util]
            [forgotten.views.layout :as layout]))


;;;; view

(defn fare-master-page
  "交通費精算画面"
  [req fare-masters]
  
  (->> [:div
        [:h5 "交通費マスタ"]
        (when-let [success (:success (:flash req))] ;; 保存成功・削除メッセージ
          [:div.alert.alert-success [:strong success]])

        (when-let [error (vals (:error (:flash req)))] ;; バリデーションの失敗メッセージ
          (util/error-message error))
        
        [:hr.mb-3
         [:div.container-fluid
          [:div.justify-content-center
           (hf/form-to
            [:post (str ROOTDIR "/fare/master")]

            [:table.table.table-hover.table-bordered.col-12

             [:thead.thead-light
              [:tr
               [:th]
               [:th "訪問先・目的"]
               [:th "交通手段"]
               [:th "出発"]
               [:th "到着"]
               [:th "片/往"]
               [:th "金額"]]]

             [:tbody#fare_masters

              (map (fn [fare-master]
              
                     [:tr.fare_master_record

                      [:td [:button.btn.btn-danger {:formmethod "post" :formaction (format "%s/fare/master/%s/delete" ROOTDIR (:fare_master_id fare-master))} "削除"]]

                      ;; 交通費ID
                      [:input {:type "hidden" :name "fare-master[][fare_master_id]" :value (h/h (:fare_master_id fare-master))}]
                      
                      ;; 訪問先・目的
                      [:td [:input.form-control.form-control-sm.input-group {:type "text" :name "fare-master[][purpose]" :value (h/h (:purpose fare-master))}]]
                      
                      ;; 交通手段
                      [:td [:input.form-control.form-control-sm.input-group {:type "text" :name "fare-master[][transportation]" :value (h/h (:transportation fare-master))}]]
                      
                      ;; 出発
                      [:td [:input.form-control.form-control-sm.input-group {:type "text" :name "fare-master[][departure]" :value (h/h (:departure fare-master))}]]
                      
                      ;; 到着
                      [:td [:input.form-control.form-control-sm.input-group {:type "text" :name "fare-master[][arrival]" :value (h/h (:arrival fare-master))}]]
                      
                      ;; 片/往
                      [:td [:input.round_trip {:type "checkbox" :checked (if (= (:round_trip fare-master) "1") "checked")}]] ;; 休日ならchecked
                      [:input.round_trip_value {:type "hidden" :name "fare-master[][round_trip]" :value (if (= (:round_trip fare-master) "1") 1 0)}]
                      
                      ;; 金額
                      [:td [:input.fare-master.form-control.form-control-sm.input-group {:type "text" :name "fare-master[][fare]" :value (h/h (:fare fare-master))}]]])
                   
                   fare-masters)]]

            [:br]
            
            [:div [:button.btn.btn-info {:id "add_row_fare_master" :type "button"} "行追加"]]

            [:br]
            
            [:div [:button.btn.btn-primary.btn-lg.btn-block {:type "submit" :align "center"} "保存"]])]]]]

        
       (layout/template req)))