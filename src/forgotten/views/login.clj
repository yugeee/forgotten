(ns forgotten.views.login
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :as hf]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.util.views :as util]))

(defn login-page
  "ログインページ"
  [req]
  
  (html5
   
   [:head
    [:title "ログイン|forgotten "]
    (include-css "/css/vendor/bootstrap.min.css"
                 "/css/login.css")
    (include-js "/js/vendor/jquery.min.js"
                "/js/vendor/bootstrap.bundle.min.js")]
   
   [:body
    [:div.form-signin.text-center
      [:div.card.card-body
       [:h3.card-title "ログイン"]

       (hf/form-to
        [:post (str ROOTDIR "/login")]
        
        ;; ID
        [:div
         [:legend {:style "text-align:left"} "ID: "
          [:input#inputID.form-control {:type :text :name :id}]]
        (when-let [id (:id (:error req))]
          (util/error-message id))]
        
        ;; パスワード
        [:div
         [:legend {:style "text-align:left"} "パスワード: "
          [:input#inputPassword.form-control {:type :password :name :pass}]]
         (when-let [pass (:pass (:error req))]
           (util/error-message pass))]
        
        [:button.btn.btn-lg.btn-primary.btn-block "ログイン"]

        [:br]
        
        [:a {:href "http://xxx.xxx.xx.xx/api/admin/users/"}
         [:button.btn.btn-lg.btn-success.btn-block {:type "button" :formaction "http://xxx.xxx.xx.xx/api/admin/users/login"} "ユーザ登録"]]

        [:br]
        
       ;; ログアウト成功のメッセージ
       (when-let [logout (:logout (:flash req))]
         [:div.alert.alert-success [:strong logout]])
       
       ;; ユーザが見つからなかったエラー
       (when-let [not_found (:not_found (:error req))]
         (util/error-message not_found))
       
       ;; ログインのタイムアウトのエラー
       (when-let [timeout (:timeout (:error (:flash req)))]
         (util/error-message timeout))

       ;; 深刻なエラーが出た場合
       (when-let [error (:fatal (:error (:flash req)))]
         (util/error-message error)))]]]))
