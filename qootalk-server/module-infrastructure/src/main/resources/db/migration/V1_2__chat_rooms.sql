-- chat_rooms
create table chat_rooms (
  id bigserial primary key,
  room_name varchar(255) not null,
  room_type varchar(255) not null,
  created_by bigint not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  deleted_at timestamp
);
