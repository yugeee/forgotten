(ns forgotten.service.login
  (:require [forgotten.infrastructure.user :as user]
            [clojure.data.json :as json]
            [slingshot.slingshot :refer [try+ throw+]]))

;;;; main

(defn exec-login
  "ログイン実行 例外はハンドラへそのまま投げ直す"
  [user_id pass]
  
  (try+
   
   (user/get-by-name-and-pass user_id pass)

   ;; エラーハンドリング
   (catch [:type :forgotten.infrastructure.user/validation-error] {:keys [error]}
     ;; バリデーションエラー
     (throw+ {:type ::validation-error :error error}))
   
   (catch [:type :forgotten.infrastructure.user/notfound-error] {:keys [error]}
     ;; ユーザーが見つからなかった場合
     (throw+ {:type ::notfound-error :error error}))
   
   (catch [:type :forgotten.infrastructure.user/fatal-error] {:keys [error]}
     ;; その他の予期せぬエラー
     (throw+ {:type ::fatal-error :error error}))))


;; 
(defn get-user-by-token
  "ログインチェック。トークンでユーザ取得"
  [token]

  (try+
   
   (user/get-by-token token)

   ;; エラーハンドリング
   (catch [:type :forgotten.infrastructure.user/validation-error] {:keys [error]}
     ;; バリデーションエラー
     (throw+ {:type ::validation-error :error error}))
   
   (catch [:type :forgotten.infrastructure.user/notfound-error] {:keys [error]}
     ;; ユーザーが見つからなかった場合
     (throw+ {:type ::notfound-error :error error}))
   
   (catch [:type :forgotten.infrastructure.user/fatal-error] {:keys [error]}
     ;; その他の予期せぬエラー
     (throw+ {:type ::fatal-error :error error}))))
