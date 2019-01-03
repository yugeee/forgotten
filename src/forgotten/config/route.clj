(ns forgotten.config.route
  (:require [compojure.core :refer :all]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.handler.login :as login]
            [forgotten.handler.menu :as menu]
            [forgotten.handler.attendance :as attendance]
            [forgotten.handler.attendance_master :as attendance_master]
            [forgotten.handler.fare :as fare]
            [forgotten.handler.fare_master :as fare_master]
            [forgotten.handler.submit :as submit]))

(defroutes route
  (context ROOTDIR req

           ;; ログイン
           (GET  "/login"  req login/get-login)
           (POST "/login"  req login/post-login)

           ;; ログアウト
           (GET  "/logout" req login/get-logout)

           ;; メニュー
           (GET  "/index" req menu/menu)

           ;; 勤怠登録
           (GET  "/attendance" req attendance/get-attendance-list)
           (GET  "/attendance/period/:period" req attendance/get-attendance)
           (POST "/attendance" req attendance/post-attendance)

           ;; 勤怠マスタ管理
           (GET  "/attendance/master" req attendance_master/get-attendance-master)
           (POST "/attendance/master" req attendance_master/post-attendance-master)

           ;; 交通費管理
           (GET  "/fare" req fare/get-fare-list)
           (GET  "/fare/period/:period" req fare/get-fare)
           (POST "/fare" req fare/post-fare)
           (POST "/fare/:fare_id/delete" req fare/post-delete-fare)

           ;; 交通費管理
           (GET  "/fare/master" req fare_master/get-fare-master)
           (POST "/fare/master" req fare_master/post-fare-master)
           (POST "/fare/master/:fare_master_id/delete" req fare_master/delete-fare-master)

           ;; 提出
           (GET  "/submit" req submit/get-submit)
           (POST "/submit" req submit/post-submit)))