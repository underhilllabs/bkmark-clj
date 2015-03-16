(ns bkmarker.views.bookmarks
  (:require [hiccup.core :refer :all]
            [hiccup.page :refer :all]))

(defn main-layout [content]
  (html
    [:head 
     [:title "Bkmarker: Social Bookmarking"]
     (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css")
     (include-css "/css/styles.css")]
    [:body 
     [:div {:class "navbar navbar-fixed-top"}
      [:div {:class "navbar-inner"}
       [:div {:class "container-fluid"}
        [:a {:class "brand" :href "#"} "Bkmarker"] 
      ]]]
     [:div {:class "container"}
      [:div {:class "main-wrapper"} 
       [:h2 "Welcome to Bkmarker!"]
       content]]]))

(defn view-bookmark [bkmark]
  (html
   [:div {:class "bookmark-wrapper"}
    [:div {:class "bookmark-title"} 
     [:a {:href (get bkmark :url)} (get bkmark :title)]]
    [:span (str 
                (get bkmark :updated_at) 
                " by " 
                (get bkmark :username))]]))
