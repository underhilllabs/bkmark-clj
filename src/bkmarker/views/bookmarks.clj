(ns bkmarker.views.bookmarks
  (:require [hiccup.core :refer :all]))

(defn main-layout [content]
  (html
    [:head [:title "Bkmarker| Social Bookmarking"]]
    [:body content]))

(defn view-bookmark [bkmark]
  (html
   [:a {:href (get bkmark :url)} (get bkmark :title)]
   [:br]
   [:p [:span (str 
               (get bkmark :updated_at) 
               " by " 
               (get bkmark :username))]]))
