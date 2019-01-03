(ns forgotten.config.environment)

;;;; const

;; ルートディレクトリ
(def ^:const ROOTDIR "/forgotten")

;; メールの認証設定
(def ^:const MAIL_AUTH {:host ""
                        :user ""
                        :pass ""
                        :port 
                        :tsl true})

;; メール送信先
(def ^:const MAIL_TO "")