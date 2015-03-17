(ns bkmarker.core
  (:require [compojure.core :refer :all]
            [compojure.route :refer :all]
            [bkmarker.models.model :refer :all]
            [bkmarker.views.bookmarks :as v]
            [korma.core :refer :all]
            [hiccup.core :as hiccup]
            [clojure.java.jdbc :as j]
            [clojure.string :as s]
            [org.httpkit.server :refer [run-server]])
  (:gen-class :main true))

(defn pr-bkmarks-tag
  "Let's print all the tagged bookmarks!"
  [my-tag lim off]
  (v/main-layout 
   (str "Bookmarks tagged with: " (s/capitalize my-tag))
   (apply str 
          (map #(v/view-bookmark %)  
               (bkmarks-tag-query my-tag lim off)))))

(defn pr-bkmarks-user
  "Let's print all the users bookmarks!"
  [my-user lim off]
  (v/main-layout 
   (str (s/capitalize my-user) 
        "'s Bookmarks")
   (apply str 
          (map #(v/view-bookmark %)  
               (bkmarks-user-query my-user lim off)))))

(defn pr-bkmarks
  "Let's print all the bookmarks!"
  [lim off]
  (v/main-layout "Welcome to Bkmarker!"
   (apply str
          (map #(v/view-bookmark %)  
               (bkmarks-query lim off)))))

(def my-limit 20)

(defroutes bkmark-routes 
  (GET "/" [] (pr-bkmarks my-limit 0))
  (GET "/user/:user" [user] (pr-bkmarks-user user my-limit 0))
  (GET "/tag/name/:tagname" [tagname] (pr-bkmarks-tag tagname my-limit 0))
  (resources "/")
  (not-found "Page not found"))

(defn -main []
  (run-server bkmark-routes {:port 5010}))
