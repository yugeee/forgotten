(ns forgotten.views.fare_pdf
  (:require [clj-pdf.core :refer :all]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.util.pdf :refer [PDFDIR PDFMETA ->pdf]]
            [forgotten.util.data :refer [fare-sum]]))


;;;; main

(defn ->fare-pdf-vec
  "交通費PDF"
  [fares user_name period]

  (let [year  (subs period 0 4)
        month (subs period 5 7)]
  
    [PDFMETA
   
     [:heading (str user_name " " year "年" month "月度交通費精算書")]

     ;; table作成
     (into
      [:table {:header  ["日付" "訪問先・目的" "交通手段" "出発" "到着" "片/往" "金額"]}]
      (for [fare fares]
        [(:date fare)
         (:purpose fare)
         (:transportation fare)
         (:departure fare)
         (:arrival fare)
         (if (:round_trip fare) "往復" "片道")
         (str (:fare fare))]))
     
     ;; 交通費合計
     [:paragraph {:align :right} (str "合計： " (fare-sum fares) "円")]]))
  

(defn fares->pdf
  "交通費精算のPDF出力"
  [fares user period]

  (try+

   (if (empty? fares)
     (throw+ {:type ::fare-not-found :error "交通費が入力されていません"}))
   
   (let [user_id   (:id user)
         user_name (:name user)
         year      (subs period 0 4)
         month     (subs period 5 7)
         file_name (str user_id "_fare_adjustment")]

     ;; PDF出力
     (->pdf (->fare-pdf-vec fares user_name period) file_name)
     
     (str PDFDIR "/" file_name))

   (catch Exception e
     ;; メール送信エラー
     (throw+ {:type ::pdf-fatal-error :error e}))))
