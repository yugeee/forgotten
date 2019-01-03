(ns forgotten.infrastructure.mail
  (:require [slingshot.slingshot :refer [try+ throw+]]
            [postal.core :refer :all]
            [forgotten.config.environment :refer [MAIL_AUTH MAIL_TO]]))

;; メール本文(ヒアドキュメント)
(def mail-body "さんから月末報告がありました。
確認おねがいします。")

;;;; main
(defn send-mail!
  "メール送信 sakurasaku-corpのみ有効"
  [user attendance-path fare-path]
  
  (try+
   
   (let [user_name    (:name user)
         user_address (:mail user)]
     
     (send-message MAIL_AUTH
                   {:from user_address
                    :to MAIL_TO
                    :subject (str "【月末報告】" user_name)
                    :body [{:type "text/plain;charset=utf-8"
                            :content (str user_name mail-body)}
                          [{:type :attachment
                            :content (java.io.File. (str attendance-path ".pdf"))}
                           {:type :attachment
                            :content (java.io.File. (str fare-path ".pdf"))}]]}))
   
   (catch Exception e
     ;; メール送信エラー
     (throw+ {:type ::mail-send-error :error e}))))