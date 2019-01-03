(ns forgotten.config.db)

;;;; const

;; DBのドライバ設定
(def ^:const db-conn "mysql://root@localhost:3306/forgotten?useSSL=false")
