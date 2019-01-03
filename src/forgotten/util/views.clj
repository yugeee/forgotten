(ns forgotten.util.views)

;;;; util

(defn error-message
  "エラーメッセージ出力"
  [msg]
  [:div {:class "alert alert-danger", :role "alert"} msg])
