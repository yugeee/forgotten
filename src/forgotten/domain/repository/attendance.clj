(ns forgotten.domain.repository.attendance
  (:require [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.infrastructure.attendance :as db]
            [forgotten.infrastructure.calendar :refer [get-calendar]]
            [forgotten.domain.entity.attendance :refer [map->Attendance ->Attendance]]
            [forgotten.util.time :refer [holiday?]]
            [forgotten.util.data :refer [vector->map]]))

;;;; const

;; csvからmapへ変換させるために使うヘッダ。mapのkeyになる。
(def ^:const attendance-header-csv
  ["year" "month" "day" "era" "era-year" "weekday-jp" "weekday" "holiday"])

;; FLG
(def ^:const ORDINARY 0) ;; 平日FLG
(def ^:const HOLIDAY  1) ;; 休日FLG



;;;; supports

(defn- ->entity
  "mapから勤怠エンティティを作る"
  ([calendar]
     ;; DBから取ってきた場合
     (map (fn [oneday]
            (let [params {:date       (if (empty? (:date_formed oneday)) (:date oneday) (:date_formed oneday))
                          :user_id    (:user_id oneday)
                          :weekday    (:weekday oneday)
                          :summary    (:summary oneday)
                          :start_time (:start_time oneday)
                          :end_time   (:end_time  oneday)
                          :rest_time  (:rest_time oneday)
                          :work_time  (:work_time oneday)
                          :holiday    (:holiday oneday)}]
              ;; エンティティ作成
              (map->Attendance params)))
          calendar))
  
  ([user_id calendar attendance_master]
     ;; APIからカレンダーを取得した場合
     (map (fn [oneday]
            
            (let [params {:date       (str (:year oneday) "-" (:month oneday) "-" (:day oneday))
                          :user_id    user_id

                          ;; 曜日番号から曜日を割り当て :ex 0 => 日
                          :weekday    (:weekday oneday)

                          ;; 祝日なら祝日名を入れる, 土日なら空文字
                          :summary    (if (empty? (:holiday oneday))
                                        (do
                                          (if (holiday? oneday)
                                            ""
                                            (:summary attendance_master)))
                                        (:holiday oneday))
                          
                          :start_time (if (holiday? oneday) "00:00" (:start_time attendance_master))
                          :end_time   (if (holiday? oneday) "00:00" (:end_time   attendance_master))
                          :rest_time  (if (holiday? oneday) "00:00" (:rest_time  attendance_master))
                          :work_time  (if (holiday? oneday) "00:00" (:work_time  attendance_master))
                          :holiday    (if (= (:holiday oneday) "")
                                        ORDINARY
                                        HOLIDAY)}]
              ;; エンティティ作成
              (map->Attendance params)))
          calendar)))



(defn- create-attendance
  "新しい勤怠を生成"
  [user_id attendance_master period]
  
  (let [calendar (get-calendar)
        ;; calendarをmapへ変換　リストの最初は項目名（ヘッダ）なので取り除いておく
        calendar-map (vector->map (rest calendar) attendance-header-csv)]
    
    ;; エンティティ作成
    (->entity user_id calendar-map attendance_master)))




;;;; main
(defn find-list
  "勤怠一覧取得"
  [user_id]
  
  (try+
   
   (let [result (db/find-list-by-uid user_id)]
     (if (empty? result)

       ;; DBに勤怠がなければ新しく作成
       nil
       
       ;; DBに勤怠があればDBのレコードからエンティティ作成
       result))
     
   (catch [:type :forgotten.infrastructure.attendance/fatal-sql-error] {:keys [error]}
     (throw+ {:type ::fatal-sql-error :error error}))))


(defn find-attendance
  "勤怠取得"
  [user_id attendance_master period]
  (try+
   
   (let [result (db/find-by-uid-period user_id period)]
     (if (empty? result)

       ;; DBに勤怠がなければ新しく作成
       (create-attendance user_id attendance_master period)
       
       ;; DBに勤怠があればDBのレコードからエンティティ作成
       (->entity result)))
     
   (catch [:type :forgotten.infrastructure.attendance/fatal-sql-error] {:keys [error]}
     (throw+ {:type ::fatal-sql-error :error error}))))
  

(defn save-attendance!
  "勤怠保存"
  [user_id attendances]
  
  (try+
   (let [oneday (:date (first attendances))
         result (db/attendance-count user_id oneday)
         attendance-entities (->entity attendances)]
     
     (if (zero? result)
       ;; DB勤怠がなければinsert
       (db/insert! attendance-entities)

       ;; DBに勤怠があればupdate
       (db/update! attendance-entities)))
   
   (catch [:type :forgotten.infrastructure.attendance/fatal-sql-error] {:keys [error]}
     ;; SQL実行エラー
     (throw+ {:type ::fatal-sql-error :error error}))))
