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

(defn bkmarks-tag-query
  [my-tag lim off]
  (select tags
          (with bookmarks)
          (with users)
          (where {:tags.name my-tag})
          (order :updated_at :DESC)
          (limit lim)
          (offset off)))
