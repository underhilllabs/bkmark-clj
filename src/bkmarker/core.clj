(ns bkmarker.core
  (:require [compojure.core :refer :all]
            [bkmarker.models.model :refer :all]
            [korma.core :refer :all]
            [hiccup.core :as hiccup]
            [clojure.java.jdbc :as j]
            [org.httpkit.server :refer [run-server]])
  (:gen-class :main true))

(defn pr-bkmarks-user
  "Let's print all the users bookmarks!"
  [in-user lim]
  (apply str 
         (map #(hiccup/html [:h2 (:title %)])  
              (select bookmarks 
                      (with users) 
                      (where {:users.username in-user})
                      (order :updated_at :DESC)
                      (limit lim)))))

(defn pr-bkmarks
  "Let's print all the bookmarks!"
  [lim]
  (apply str
         (map #(hiccup/html [:h2 (:title %)])  
              (select bookmarks 
                      (with users)
                      (order :updated_at :DESC)
                      (limit lim)))))

(defroutes bkmark-routes 
  (GET "/" [] (pr-bkmarks 20))
  (GET "/user/:user" [user] (pr-bkmarks-user user 20)))

(defn -main []
  (run-server bkmark-routes {:port 5010}))
