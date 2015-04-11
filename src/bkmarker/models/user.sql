-- name: create-user<!
insert into users
(username, email, password_digest)
values
(:username, :email, :pass_digest)

-- name: create-bookmark!
insert into bookmarks
(title, url, desc, user_id, created_at, updated_at)
values
(:title, :url, :description, :user_id, :created_at, :updated_at)

-- name: create-tag!
insert into tags
(name, bookmark_id, user_id)
values
(:name, :bookmark_id, :user_id)

-- name: find-user-by-email
select * from users 
where email = :email 
limit 1

-- name: find-user-by-id
select * from users 
where id = :user-id 
limit 1
      
-- name: find-users-bookmark-count
select username, count(*) as count
from users, bookmarks 
where users.id = bookmarks.user_id 
group by bookmarks.user_id;

-- name: find-tags-count
select name, count(name) as count
from tags
group by name
order by count
limit 50

-- name: find-bookmarks-all
select b.title, b.url, u.username, b.updated_at, b.created_at
from users u, bookmarks b
where users.id = bookmarks.user_id 
order by updated_at DESC
limit :offset, :limit

-- name: find-bookmarks-user
select b.title, b.url, b.updated_at, b.created_at
from users u, bookmarks b
where users.id = bookmarks.user_id 
and users.username = :username
order by updated_at DESC
limit :offset, :limit

-- name: find-bookmarks-search
select b.title, b.url, u.username
from bookmarks b, users u
where b.user_id = u.id
and title like (str "%" :phrase "%")
order by b.updated_at DESC
limit :offset, :limit
