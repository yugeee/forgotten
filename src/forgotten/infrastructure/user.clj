(ns forgotten.infrastructure.user
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [slingshot.slingshot :refer [throw+]]))


;;;; support

(defn- post-request
  "POSTリクエストの内容"
  [id pass]
  
  {:headers
   {"Content-Type" "application/x-www-form-urlencoded"}
   :form-params {"id" id
                 "pass" pass}})




;;;; curl

(defn get-by-name-and-pass
  "id pass でユーザ検索"
  [id pass]
  
  (let [result @(http/post "http://xxx.xxx.xx.xx/api/v1/user/"
                           (post-request id pass))
        status (:status result)
        body (json/read-str (:body result) :key-fn keyword)]

    (case status
      200 body ;; 成功
      400 (throw+ {:type ::validation-error :error (:varidation_error body)}) ;; バリデーションエラー
      404 (throw+ {:type ::notfound-error :error body}) ;; ユーザーが見つからなかった場合
      (throw+ {:type ::fatal-error :error body})))) ;; 500エラーのとき(API側でなんかおかしい)


(defn get-by-token
  "token でユーザ検索"
  [token]
  
  (let [result @(http/get (str "http://xxx.xxx.xx.xx/api/v1/user?token=" token))
        status (:status result)
        body  (json/read-str (:body result) :key-fn keyword)]
    
    (case status
      200 body ;; 成功
      400 (throw+ {:type ::validation-error :error (:varidation_error body)}) ;; バリデーションエラー
      404 (throw+ {:type ::notfound-error :error body}) ;; ユーザーが見つからなかった場合
      (throw+ {:type ::fatal-error :error body})))) ;; 500エラーのとき(API側でなんかおかしい)
