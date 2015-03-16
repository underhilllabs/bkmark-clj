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
  (let [my-url (get bkmark :url)
        my-title (get bkmark :title)
        my-username (get bkmark :username)
        my-tags (clojure.string/join ", " (map #(get % :name ) (get bkmark :tags)))
        my-updated (get bkmark :updated_at)]
  (html
   [:div {:class "bookmark-wrapper"}
    [:div {:class "bookmark-title"} 
     [:a {:href my-url} my-title]]
    [:div {:class "bookmark-tags"} my-tags]
    [:span (str my-updated " by " )
     [:a {:href (str "/user/" my-username)} my-username]]])))
