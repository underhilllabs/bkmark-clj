-- name: create-user<!
insert into users
(username, email, password_digest)
values
(:username, :email, :pass_digest)

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

-- name: find-user-tags-count
select name, count(name) as count
from tags
where tags.user_id = :user_id
group by name
order by count DESC
limit :offset, :lim

-- name: find-bookmarks-all
select b.title, b.url, u.username, b.updated_at, b.created_at
from users u, bookmarks b
where u.id = b.user_id 
order by updated_at DESC
limit :offset, :limit


-- name: find-bookmarks-user
select b.title, b.url, b.updated_at, b.created_at
from users u, bookmarks b
where u.id = b.user_id 
and u.id = :user_id
order by updated_at DESC
limit :offset, :lim

-- name: find-bookmarks-search
select b.title, b.url, u.username
from bookmarks b, users u
where b.user_id = u.id
and title like (str "%" :phrase "%")
order by b.updated_at DESC
limit :offset, :limit
