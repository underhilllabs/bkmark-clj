(ns bkmarker.core
  (:require [compojure.core :refer :all]
            [compojure.route :refer :all]
            [bkmarker.models.model :refer :all]
            [bkmarker.views.bookmarks :as v]
            [korma.core :refer :all]
            [hiccup.core :as hiccup]
            [clojure.java.jdbc :as j]
            [org.httpkit.server :refer [run-server]])
  (:gen-class :main true))

(defn pr-bkmarks-user
  "Let's print all the users bookmarks!"
  [in-user lim]
  (apply str 
         (map #(v/view-bookmark %)  
              (select bookmarks 
                      (with users) 
                      (where {:users.username in-user})
                      (order :updated_at :DESC)
                      (limit lim)))))

(defn pr-bkmarks
  "Let's print all the bookmarks!"
  [lim]
  (v/main-layout
   (apply str
          (map #(v/view-bookmark %)  
               (select bookmarks 
                       (with users)
                       (order :updated_at :DESC)
                       (limit lim))))))

(defroutes bkmark-routes 
  (GET "/" [] (pr-bkmarks 20))
  (GET "/user/:user" [user] (pr-bkmarks-user user 20))
  (resources "/")
  (not-found "Page not found"))

(defn -main []
  (run-server bkmark-routes {:port 5010}))
