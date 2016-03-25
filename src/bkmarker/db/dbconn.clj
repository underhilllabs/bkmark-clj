(ns bkmarker.db.dbconn
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))  

(defdb prod (mysql {:db "bkmarker_prod"
                    :port 3306
                    :host "localhost"
                    :delimiters "`"
                    :user "root"
                    :password "23skidoo"}))

(def db-spec 
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/bkmarker_prod"
   :user "root"
   :password "23skidoo"})

