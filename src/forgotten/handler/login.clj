(ns forgotten.handler.login
  (:require [ring.util.response :as res]
            [ring.middleware.session :as session]
            [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.views.login :as view]
            [forgotten.service.login :as service]
            [forgotten.util.common-message :refer [FATAL-ERROR]]
            [forgotten.util.log :refer [write-log]]
            [forgotten.util.response :as util]))

;;;; const

(def ^:const LOGOUT "ログアウトしました。")

(def ^:const SESSION-TIMEOUT
  {:timeout "ログインの有効期限が切れました。<br/>再度ログインしてください。"})



;;;; handler


(defn get-login
  "ログイン画面表示"
  [req]
  
  (-> (view/login-page req)
      res/response
      (util/html)))


(defn post-login
  "ログイン実行"
  [{:as req :keys [params session]}]
  
  (let [name (:id params)
        pass (:pass params)]
    
    (try+
     (let [user (service/exec-login name pass)]
       (-> (res/redirect (str ROOTDIR "/index"))
           
           ;; セッション開始
           (assoc :session (assoc session :user user))

           ;; cookieにtokenをセット
           (assoc :cookies {:token (:token user) :expires (:expire user)})))

     ;; exception
     (catch [:type :forgotten.service.login/validation-error] {:keys [error]}
       ;; バリデーションエラー
       (get-login (assoc req :error error)))
     
     (catch [:type :forgotten.service.login/notfound-error] {:keys [error]}
       ;; ユーザーが見つからなかった場合
       (get-login (assoc req :error error)))
     
     (catch [:type :forgotten.service.login/fatal-error] {:keys [error]}
       ;; その他の予期せぬエラー
       (write-log error)
       (get-login (assoc req :error {:fatal FATAL-ERROR}))))))



(defn get-logout
  "ログアウト"
  [req]
  
  (-> (res/redirect (str ROOTDIR "/login"))
      (assoc :flash {:logout LOGOUT})

      ;; session・cookie破棄
      (assoc :cookies nil)
      (assoc :session nil)
      
      (util/html)))



;;;; others

(defn login-check
  "ログインチェック。問題なければ処理を続行する。"
  [{:keys [cookies session] :as req}]
  
  ;; ログイン・ログアウトは処理続行
  (if (or (= (:uri req) (str ROOTDIR "/login"))
          (= (:uri req) (str ROOTDIR "/logout")))
    (res/redirect (:uri req)))

  (let [cookie-token  (:value (cookies "token"))
        session-token (:token (:user session))]

    (when (empty? cookie-token)
      ;; もしcookieがなければエラー
      (throw+ {:type ::authentication-error :error SESSION-TIMEOUT}))

    (when (empty? session-token)
      ;; sessionが切れていたらトークンからユーザを取得して処理続行
      (let [user (service/get-user-by-token cookie-token)]
        ;; セッション開始してリダイレクト
        (-> (res/redirect (:uri req))
            (assoc :session (assoc session :user user))
            (assoc :cookies {"token" {:value (:token user) :expires ""}}))))

    (if (and cookie-token session-token (= cookie-token session-token))
      ;; cookieとセッションのトークンが同値ならば正常
      true
      ;; cookieとセッションのトークンが違ったらエラー
      (throw+ {:type ::authentication-error :error SESSION-TIMEOUT}))))



(defn login-check-for-time-out
  "セッションタイムアウト時にcookieのトークンがあれば再度ユーザを取得し直して、セッション再開、処理を続行させる。"
  [{:keys [cookies session] :as req}]
  
  ;;ログイン・ログアウトは処理続行
  (if (or (= (:uri req) (str ROOTDIR "/login"))
          (= (:uri req) (str ROOTDIR "/logout")))
    (res/redirect (:uri req)))

  ;; TODO ここの分岐のところ、もっとうまくやれる？
  ;; tokenがあればユーザー取得
  (if-let [token (:value (cookies "token"))]
    (do
      ;; ユーザー取得
      (try+
       (let [user (service/get-user-by-token token)]
         
         ;; セッション開始してリダイレクト
         (-> (res/redirect (:uri req))
             (assoc :session (assoc session :user user))
             (assoc :cookies {"token" {:value (:token user) :expires ""}})))
       
       ;; エラーハンドリング
       (catch [:type :forgotten.service.login/notfound-error] {:keys [error]}
         ;; ユーザーが見つからなかった場合
         (get-login (assoc req :error error)))
       
       (catch [:type :forgotten.service.login/fatal-error] {:keys [error]}
         ;; その他の予期せぬエラー
         (write-log error)
         (get-login (assoc req :error error)))))
    
    ;; セッションもcookieも切れていたら再度ログインさせる。
    (-> (res/redirect (str ROOTDIR "/login"))
        (assoc :flash {:error SESSION-TIMEOUT}))))
