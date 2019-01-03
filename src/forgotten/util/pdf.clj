(ns forgotten.util.pdf
  (:require [clj-pdf.core :refer [pdf]]))

;;;; const
;; path
(def ^:const PDFDIR "out")

;; encoding
(def ^:const PDFMETA {:font {:encoding "UniJIS-UCS2-H"
                             :ttf-name "HeiseiKakuGo-W5"}})

;;;; main
(defn ->pdf
  "PDF出力"
  [vec file_name]
  
  (pdf vec (format "%s/%s.pdf" PDFDIR file_name)))