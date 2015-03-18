(ns bkmarker.helpers
  (:require [digest :refer [md5]]))

(defn get-gravatar-pic [email]
  (let [gravatar-id (md5 email)]
    (str "http://gravatar.com/avatar/"
         gravatar-id
         ".png?d=monsterid&s=80")))

 
