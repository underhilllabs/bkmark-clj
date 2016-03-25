-- name: create-bookmark<!
insert into bookmarks
(title, url, `desc`, user_id, created_at, updated_at)
values
(:title, :url, :description, :user_id, now(), now())

-- name: create-tag!
insert into tags
(name, bookmark_id, user_id)
values
(:name, :bookmark_id, :user_id)

-- name: create-user<!
insert into users
(username, email, password_digest)
values
(:username, :email, :pass_digest)
