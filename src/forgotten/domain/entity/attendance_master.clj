(ns forgotten.domain.entity.attendance_master)

;;;; entity

;; 勤怠情報のエンティティ DBのカラムに合わせてsnakecase
(defrecord Attendance-master [user_id summary start_time end_time rest_time work_time])
