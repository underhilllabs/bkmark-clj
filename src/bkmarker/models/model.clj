(ns bkmarker.models.model
  (:require [bkmarker.db.dbconn :refer :all]
            [korma.core :refer :all]))

(declare users bookmarks tags)

(defentity users
  (has-many bookmarks {:fk :user_id})
  (has-many tags {:fk :user_id}))

(defentity bookmarks
  (belongs-to users {:fk :user_id})
  (has-many tags {:fk :bookmark_id}))

(defentity tags
  (belongs-to users {:fk :user_id})
  (belongs-to bookmarks {:fk :bookmark_id}))
  

(defn pr-user-bkmarks
  "Let's print users"
  [name num]
  (select users
          (with bookmarks)
          (where {:name name})
          (limit num)))

(defn bkmarks-query
  [lim off]
  (select bookmarks 
          (with users)
          (with tags)
          (order :updated_at :DESC)
          (limit lim)
          (offset off)))

(defn bkmarks-user-query
  [my-username lim off]
  (select bookmarks 
          (with users)
          (with tags)
          (where {:users.username my-username})
          (order :updated_at :DESC)
          (limit lim)
          (offset off)))

(defn users-bookmark-count-query
  "Returns user details and their bookmark count"
  []
  (select users
          (fields :username :fullname :pic_url :email)
          (join bookmarks (= :bookmarks.user_id :id))
          (aggregate (count :bookmarks.id) :count)
          (group :bookmarks.user_id)
          (order :count :DESC)))

(defn tags-count-query
  "Returns user details and their bookmark count"
  []
  (select tags
          (fields :name)
          (aggregate (count :name) :count)
          (group :name)
          (order :count :DESC)
          (limit 50)))

(defn bkmarks-tag-query
  [my-tag lim off]
  (select tags
          (with bookmarks)
          (with users)
          (where {:tags.name my-tag})
          (order :updated_at :DESC)
          (limit lim)
          (offset off)))
