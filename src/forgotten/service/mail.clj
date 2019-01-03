(ns forgotten.service.mail
  (:require [slingshot.slingshot :refer [try+ throw+]]
            [forgotten.infrastructure.mail :refer [send-mail!]]))

;;;; main

(defn submit!
  "勤怠情報保存"
  [user attendance-path fare-path]
  
  (try+
   
   (send-mail! user attendance-path fare-path)
   (catch [:type :forgotten.infrastructure.mail/mail-send-error] {:keys [error]}
     
     ;; Sメール送信エラー
     (throw+ {:type ::mail-send-error :error error}))))