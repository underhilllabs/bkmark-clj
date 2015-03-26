(defproject bkmarker "0.1.0-SNAPSHOT"
  :description "Social Bookmarking"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [korma "0.4.0"]
                 [compojure "1.1.8"]
                 [com.cemerick/friend "0.2.1"]
                 [http-kit "2.1.16"]
                 [digest "1.4.4"] ;; for gravatar md5
                 [hiccup "1.0.5"]]
  :main bkmarker.core)
