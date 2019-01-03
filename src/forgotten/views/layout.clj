(ns forgotten.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :as hf]
            [forgotten.config.environment :refer [ROOTDIR]]))

;;;; view

;; 全ビュー共通となるパーツ
(defn common [req body]
  (html5
   [:head
    [:title "FORGOTTEN"]
    (include-css "/css/vendor/bootstrap.min.css"
                 "/css/common.css")]
   [:body body]

   ;; めんどくさいので全部読み込んでる
   (include-js "/js/vendor/jquery.min.js"
               "/js/vendor/bootstrap.bundle.min.js"
               "/js/attendance.js"
               "/js/attendance_list.js"
               "/js/fare.js"
               "/js/fare_list.js"
               "/js/fare_master.js")))


;; サイドバー
(defn side-bar []
  [:nav.sidebar.bg-light.col-md-1.navbar-light.bg-light
   [:ul.navbar-nav
    [:li.nav-item
     ;; 勤怠管理 
     [:a.nav-link {:href (str ROOTDIR "/attendance")} "勤怠管理"]

     ;; 勤怠マスタ管理
     [:a.nav-link {:href (str ROOTDIR "/attendance/master")} "勤怠マスタ"]

     ;; 交通費管理
     [:a.nav-link {:href (str ROOTDIR "/fare")} "交通費精算"]

     ;; 交通費マスタ管理
     [:a.nav-link {:href (str ROOTDIR "/fare/master")} "交通費マスタ"]

     ;; 提出
     [:a.nav-link {:href (str ROOTDIR "/submit")} "提出"]

     ;; ログアウト
     [:a.nav-link {:href (str ROOTDIR "/logout")} "ログアウト"]]]])


;; common + サイドバー
(defn template [req & body]
  (->> [:div
        ;; ヘッダ
        [:nav.navbar.navbar-dark.bg-dark
         [:a.navbar-brand {:href "/forgotten/index"} "FORGOTTEN"]]
        [:div.container-fluid
         [:div.row
          (side-bar)
          [:div.col-md-10.content body
           [:footer {:class "footer"}
            [:div.container
               [:p.text-center.text-muted "© since 2018"]]]]]]]
       (common req)))

;;;; view