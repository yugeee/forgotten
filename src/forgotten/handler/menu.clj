(ns forgotten.handler.menu
  (:use [clojure.pprint])
  (:require [ring.util.response :as res]
            [ring.middleware.session :as session]
            [slingshot.slingshot :refer [try+]]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.handler.login :refer [login-check]]
            [forgotten.views.menu :as view]
            [forgotten.util.common-message :refer [FATAL-ERROR]]
            [forgotten.util.log :refer [write-log]]
            [forgotten.util.response :as util]))

;;;; handler

(defn menu
  "メニュー画面表示"
  [{:as req :keys [session]}]

  (try+
   
   ;; ログインチェック
   (login-check req)
  
   (-> (view/menu-page req)
       res/response
       (util/html))
   
   ;; exception
   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
    (-> (res/redirect (str ROOTDIR "/login"))
        (assoc :flash {:error error})))

   (catch Exception error
     ;; その他の予期せぬエラー
     (write-log error)
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))))
