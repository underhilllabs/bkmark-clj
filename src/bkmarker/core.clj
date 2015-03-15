(ns bkmarker.core
  (:require [compojure.core :refer :all]
            [bkmarker.models.model :refer :all]
            [korma.core :refer :all]
            [hiccup.core :as hiccup]
            [clojure.java.jdbc :as j]
            [org.httpkit.server :refer [run-server]])
  (:gen-class :main true))

(defn pr-users
  "Let's print all the user names!"
  [user]
  (apply str 
         (map #(hiccup/html [:h2 (:title %)])  
              (select bookmarks 
                      (with users) 
                      (where {:users.username user})
                      (limit 10)))))

(defroutes bkmark-routes 
  (GET "/" [] "hello, bkmarkr")
  (GET "/user/:user" [user] (pr-users user)))

(defn -main []
  (run-server bkmark-routes {:port 5010}))
