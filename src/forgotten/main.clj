(ns forgotten.main
  (:require [ring.adapter.jetty :as server]
            [forgotten.core :refer [app]])
  (:gen-class))


;; serverの状態
(defonce server (atom nil))


(defn start-server
  "jetty起動"
  []
  (when-not @server
        (reset! server (server/run-jetty app {:port 3000 :join? false}))))


(defn stop-server
  "jetty停止"
  []
  (when @server
    (.stop @server)
    (reset! server nil)))


(defn restart-server
  "jetty再起動"
  []
  (when @server
    (stop-server)
    (start-server)))

(defn -main
  "jarからアプリ起動"
  [& {:as args}]
  (start-server))