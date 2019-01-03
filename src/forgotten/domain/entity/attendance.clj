(ns forgotten.domain.entity.attendance)


;;;; entity

;; 勤怠情報のエンティティ DBのカラムに合わせてsnakecase
(defrecord Attendance [date user_id weekday summary start_time end_time rest_time work_time holiday])
