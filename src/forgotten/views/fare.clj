(ns forgotten.views.fare
  (:require [hiccup.core :as h]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :as hf]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.util.time :refer [today]]
            [forgotten.util.views :as util]
            [forgotten.util.data :refer [fare-sum]]
            [forgotten.views.layout :as layout]))


;;;; view

(defn fare-page
  "交通費精算画面"
  [req fares fare-masters period]

  (let [fare       (first fares)
        year       (subs period 0 4)
        month      (subs period 5 7)]
  
    (->> [:div
          [:h5 "交通費精算"]
          [:h6 (str year "年" month "月度交通費精算書")]
          
          (when-let [success (:success (:flash req))] ;; 交通費成功・削除メッセージ
            [:div.alert.alert-success [:strong success]])

          (when-let [error (vals (:error (:flash req)))] ;; バリデーションの失敗メッセージ
            (util/error-message error))
        
          [:hr.mb-3
           [:div.container-fluid
            [:div.justify-content-center
             
             [:div#fare_masters
              (map (fn [fare-master]
                     
                     [:div.fare_master
                      ;; 交通費マスタID
                      [:input.fare_master_id {:type "hidden" :value (h/h (:fare_master_id fare-master))}]
                      ;; 訪問先・目的
                      [:input.purpose        {:type "hidden" :value (h/h (:purpose fare-master))}]
                      ;; 交通手段
                      [:input.transportation {:type "hidden" :value (h/h (:transportation fare-master))}]
                      ;; 出発
                      [:input.departure      {:type "hidden" :value (h/h (:departure fare-master))}]
                      ;; 到着
                      [:input.arrival        {:type "hidden" :value (h/h (:arrival fare-master))}]
                      ;; 片/往
                      [:input.round_trip     {:type "hidden" :value (if (= (:round_trip fare-master) "1") 1 0)}]
                      ;; 金額
                      [:input.fare           {:type "hidden" :value (h/h (:fare fare-master))}]])
                   fare-masters)]
             
             (hf/form-to
              [:post (str ROOTDIR "/fare")]

               ;; 期間
              [:input {:type "hidden" :name "period" :value (h/h period)}]
              
              [:table.table.table-hover.table-bordered.col-13
               
               [:thead.thead-light
                [:tr
                 [:th]
                 [:th "交通費マスタ"]
                 [:th "日付"]
                 [:th "訪問先・目的"]
                 [:th "交通手段"]
                 [:th "出発"]
                 [:th "到着"]
                 [:th "片/往"]
                 [:th "金額"]]]
               
               [:tbody#fares
                
                (if (not (empty? fares))
                    (map (fn [fare]
                           
                           [:tr.fare_record
                            ;; 削除
                            [:td [:button.btn.btn-danger {:formmethod "post" :formaction (format "%s/fare/%s/delete" ROOTDIR (:fare_id fare))} "削除"]]
                            ;; 交通費ID
                            [:input {:type "hidden" :name "fare[][fare_id]" :value (h/h (:fare_id fare))}]
                            ;; 交通費マスタ
                            [:td [:select {:name "fare_master_select"}

                                  [:option.fare_master {:selected true :value ""} "勤怠マスタを選択"]
                                  
                                  (map (fn [fare-master]
                                         [:option.fare_master {:value (:fare_master_id fare-master)} (:purpose fare-master)])
                                       fare-masters)]]
                            ;; 日付
                            [:td [:input.date.form-control.form-control-sm.input-group {:type "date" :name "fare[][date]" :value (h/h (:date fare))}]]
                            ;; 訪問先・目的
                            [:td [:input.purpose.form-control.form-control-sm.input-group {:type "text" :name "fare[][purpose]" :value (h/h (:purpose fare))}]]
                            ;; 交通手段
                            [:td [:input.transportation.form-control.form-control-sm.input-group {:type "text" :name "fare[][transportation]" :value (h/h (:transportation fare))}]]
                            ;; 交通手段
                            [:td [:input.departure.form-control.form-control-sm.input-group {:type "text" :name "fare[][departure]" :value (h/h (:departure fare))}]]
                            ;; 到着
                            [:td [:input.arrival.form-control.form-control-sm.input-group {:type "text" :name "fare[][arrival]" :value (h/h (:arrival fare))}]]
                            ;; 片/往
                            [:td [:input.round_trip {:type "checkbox" :checked (if (:round_trip fare) "checked")}]] ;; 休日ならchecked
                            [:input.round_trip_value {:type "hidden" :name "fare[][round_trip]" :value (if (:round_trip fare) 1 0)}]
                            ;; 金額
                            [:td [:input.fare.form-control.form-control-sm.input-group {:type "text" :name "fare[][fare]" :value (h/h (:fare fare))}]]])
                         
                         fares)

                  ;; 交通費が空の場合は1行のみ表示させる
                  [:tr.fare_record
                   [:td [:button.deleteRow.btn.btn-danger "削除"]]
                   ;; 交通費ID
                   [:input {:type "hidden" :name "fare[][fare_id]" :value ""}]
                   ;; 交通費マスタ
                   [:td [:select {:name "fare_master_select"}

                         [:option.fare_master {:selected true :value ""} "勤怠マスタを選択"]
                         
                         (map (fn [fare-master]
                                [:option.fare_master {:value (:fare_master_id fare-master)} (:purpose fare-master)])
                              fare-masters)]]
                   ;; 日付
                   [:td [:input.date.form-control.form-control-sm.input-group {:type "date" :name "fare[][date]" :value (h/h (today))}]]
                   ;; 訪問先・目的
                   [:td [:input.purpose.form-control.form-control-sm.input-group {:type "text" :name "fare[][purpose]"}]]
                   ;; 交通手段
                   [:td [:input.transportation.form-control.form-control-sm.input-group {:type "text" :name "fare[][transportation]"}]]
                   ;; 出発
                   [:td [:input.departure.form-control.form-control-sm.input-group {:type "text" :name "fare[][departure]"}]]
                   ;; 到着
                   [:td [:input.arrival.form-control.form-control-sm.input-group {:type "text" :name "fare[][arrival]"}]]
                   ;; 片/往
                   [:td [:input.round_trip {:type "checkbox"}]]
                   [:input.round_trip_value {:type "hidden" :name "fare[][round_trip]" :value 0}]
                   ;; 金額
                   [:td [:input.fare.form-control.form-control-sm.input-group {:type "text" :name "fare[][fare]"}]]])]
               
               [:tfoot
                [:tr
                 [:td {:colspan 1} "合計"]
                 [:td {:colspan 7}]
                 ;; 総額
                 [:td#sum (fare-sum fares)]]]]
              
              [:br]
              
              [:div [:button#add_row_fare.btn.btn-info {:type "button"} "行追加"]]
              
              [:br]
              
              [:div [:button.btn.btn-primary.btn-lg.btn-block {:type "submit" :align "center"} "保存"]])]]]]
         
         (layout/template req))))