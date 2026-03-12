-- room_participants
create table room_participants (
  id bigserial primary key,
  user_id bigint not null,
  room_id bigint not null,
  last_read_message_id bigint not null,
  role varchar(255) not null,
  notification_enabled boolean not null default true,
  created_at timestamp not null,
  updated_at timestamp not null,
  deleted_at timestamp
);

create index idx_room_participants_user_id on room_participants(user_id);
create index idx_room_participants_room_id on room_participants(room_id);
