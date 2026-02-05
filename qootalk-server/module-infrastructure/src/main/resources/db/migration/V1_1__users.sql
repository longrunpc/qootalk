-- users
create table users (
  id bigserial primary key,
  email varchar(255) not null unique,
  password varchar(255) not null,
  name varchar(255) not null,
  profile_image_url varchar(255),
  status_message varchar(255),
  role varchar(255) not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  deleted_at timestamp
);
