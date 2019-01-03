(ns forgotten.util.validate
  (:require [bouncer.core :as b]
            [slingshot.slingshot :refer [throw+]]))

;;;; const

;; デフォルトメッセージ

;; 必須項目に値がない
(def violate-required "%s が入力されていません。")

;; フォーマットにあっていない
(def violate-format "%s が正しくありません。")




;;;; util

;; 
(defn validate
  "勤怠1日分のバリデーション実行 エラー時はメッセージを返す。"
  [& args]
  
  (let [[error args] (apply b/validate args)]
    
    (if (nil? error)
      args
      (throw+ {:type ::validation-error :error error}))))
