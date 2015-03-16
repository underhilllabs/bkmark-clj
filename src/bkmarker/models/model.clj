(ns bkmarker.models.model
  (:require [bkmarker.db.dbconn :refer :all]
            [korma.core :refer :all]))

(declare users bookmarks tags)

(defentity users
  (has-many bookmarks {:fk :bookmark_id}))

(defentity bookmarks
  (belongs-to users {:fk :user_id})
  (has-many tags {:fk :tag_id}))

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

