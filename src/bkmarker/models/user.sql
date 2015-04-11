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

-- name: find-bookmarks
select b.title, b.url, u.username, b.updated_at, b.created_at
from users u, bookmarks b
where users.id = bookmarks.user_id 
order by updated_at DESC
limit :offset, :limit

