-- messages
create table messages (
  id bigserial primary key,
  room_id bigint not null,
  user_id bigint not null,
  content varchar(255),
  message_type varchar(255) not null,
  parent_message_id bigint,
  created_at timestamp not null,
  updated_at timestamp not null,
  deleted_at timestamp
);
