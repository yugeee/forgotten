(ns forgotten.core
  (:require [compojure.core :refer [defroutes routes context]]
            [ring.adapter.jetty :as server]
            [ring.util.response :as res]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.session  :refer [wrap-session]]
            [ring.middleware.session-timeout :refer [wrap-idle-session-timeout]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params   :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [forgotten.config.route :refer [route]]
            [forgotten.handler.login  :refer [login-check-for-time-out]]))

;; アプリケーションの中心。ルーティングやミドルウェアについての設定
(def app
  
  (-> (routes route site-defaults)
      (wrap-resource "public") ;; css,jsの置き場
      wrap-flash               ;; メッセージ表示
      wrap-cookies             ;; cookie利用
      (wrap-idle-session-timeout {:timeout 1800
                                  :timeout-handler login-check-for-time-out}) ;; sessionタイムアウト
      (wrap-session {:cookie-attrs {:max-age 3600}}) ;; セッション利用
      wrap-keyword-params ;; postされたインプットを :params で取得
      wrap-params))
