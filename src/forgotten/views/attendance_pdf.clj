(ns forgotten.views.attendance_pdf
  (:require [clj-pdf.core :refer :all]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.util.time :refer [weekday]]
            [forgotten.util.pdf :refer [PDFDIR PDFMETA ->pdf]]
            [forgotten.util.data :refer [attendances-sum]]))



;;;; main

(defn ->attendance-pdf-vec
  "勤怠報告PDF"
  [attendances user_name period]

  (let [year  (subs period 0 4)
        month (subs period 5 7)]
  
    [PDFMETA
     
     [:heading (str user_name " " year "年" month "月度勤怠報告書")]
     
     ;; table作成
     (into
      [:table {:header  ["日" "曜日" "作業内容" "始業時間" "終業時間" "休憩時間" "勤務時間"]}]
      (for [attendance attendances]
        [(:date attendance)
         (weekday (str (:weekday attendance)))
         (:summary attendance)
         (:start_time attendance)
         (:end_time attendance)
         (:rest_time attendance)
         (:work_time attendance)]))
     
   ;; 交通費合計
     [:paragraph {:align :right} (str "合計： " (attendances-sum attendances))]]))



(defn attendances->pdf
  "勤怠報告のPDF出力"
  [attendances user period]

  (try+

   (if (empty? attendances)
     (throw+ {:type ::attendance-not-found :error "勤怠が入力されていません"}))
   
   (let [user_id   (:id user)
         user_name (:name user)
         file_name (str user_id "_attendace_report")]

     ;; PDF出力
     (->pdf (->attendance-pdf-vec attendances user_name period) file_name)
     
     (str PDFDIR "/" file_name))
   
   (catch Exception e
     ;; PDF作成エラー
     (throw+ {:type ::pdf-fatal-error :error e}))))