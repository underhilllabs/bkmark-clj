(defproject bkmarker "0.1.0-SNAPSHOT"
  :description "Social Bookmarking"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [korma "0.4.0"]
                 [enlive "1.1.5"]
                 [compojure "1.1.8"]
                 [com.cemerick/friend "0.2.1"]
                 [ragtime "0.3.8"]
                 [http-kit "2.1.16"]
                 [digest "1.4.4"] ;; for gravatar md5
                 [hiccup "1.0.5"]]
  :plugins [[ragtime/ragtime.lein "0.3.8"]]
  :ragtime {:migrations ragtime.sql.files/migrations
            :database "jdbc:mysql://localhost:3306/bkmarker_dev?user=db_user_name_here&password=db_user_password_here"}

  :main bkmarker.core)
