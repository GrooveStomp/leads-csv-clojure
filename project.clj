(defproject hello-world "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data-json "0.2.4"]
                 [compojure "1.1.6"]
                 [korma "0.3.0-RC6"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [clojure-csv/clojure-csv "2.0.1"]]
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler app.main/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
