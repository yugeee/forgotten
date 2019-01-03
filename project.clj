(defproject forgotten "0.1.0-SNAPSHOT"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.6.1"]
                 [ring/ring-core "1.7.0-RC1"]
                 [ring/ring-jetty-adapter "1.7.0-RC1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-session-timeout "0.2.0"]
                 [hiccup "1.0.5"]
                 [clj-time "0.14.4"]
                 [http-kit "2.2.0"]
                 [bouncer "1.0.1"]
                 [slingshot "0.12.2"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.csv "0.1.4"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [mysql/mysql-connector-java "8.0.12"]
                 [clj-time "0.14.4"]
                 [com.taoensso/timbre "4.10.0"]
                 [bouncer "1.0.1"]
                 [digest "1.4.8"]
                 [clj-pdf "2.2.33"]
                 [io.forward/clojure-mail "1.0.7"]
                 [com.draines/postal "2.0.3"]]
  :plugins [[lein-ring "0.12.4"]]
  ;;:ring {:handler forgotten.core/app
  ;;       :auto-reload? true
  ;;       :open-browser? false
  ;;       :reload-paths ["src/" "resources/" "log/"]}
  :uberjar-name "forgotten.jar"
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}
   :uberjar {:aot :all
            :main forgotten.main}})
