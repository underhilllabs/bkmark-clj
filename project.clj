(defproject bkmarker "0.1.0"
  :description "Social Bookmarking"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [lib-noir "0.9.9" :exclusions [clout commons-codec joda-time]]
                 [mysql/mysql-connector-java "5.1.35"]
                 [korma "0.4.0"]
                 [blackwater "0.0.9"]
                 [enlive "1.1.5"]
                 [yesql "0.4.0"]
                 [compojure "1.3.3"]
                 [ragtime "0.3.8"]
                 [http-kit "2.1.19"]
                 [digest "1.4.4"] ;; for gravatar md5
                 [hiccup "1.0.5"]]
  :plugins [[ragtime/ragtime.lein "0.3.8"]]
  :jvm-opts ["-Xmx768M"]
  :ragtime {:migrations ragtime.sql.files/migrations
            :database "jdbc:mysql://localhost:3306/bkmarker_prod?user=root&password=23skidoo"}

  :main bkmarker.core)
