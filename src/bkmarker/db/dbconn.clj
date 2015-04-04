(ns bkmarker.db.dbconn
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))  

(defdb prod (mysql {:db "bkmarker"
                    :port 3306
                    :host "localhost"
                    :delimiters "`"
                    :user "bkmarker"
                    :password "change-THIS-password"}))
