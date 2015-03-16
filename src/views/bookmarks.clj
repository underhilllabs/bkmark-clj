(ns bkmarker.views.bookmarks
  (:require [hiccup/core :refer :all]))

(defn layout [content]
  (html
    [:head [:title "Bkmarker| Social Bookmarking"]]
    [:body content]))

(defn view-bookmark [bkmark]
  (html
   [:a {:href (get bkmark :url)} {:title bkmark}]
   [:br]
   [:p [:span (str {:updated_at bkmark} " by " {:users.username bkmark})]]))
