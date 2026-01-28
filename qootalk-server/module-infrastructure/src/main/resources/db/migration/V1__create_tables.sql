-- users
create table if not exists users (
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

-- chat_rooms
create table if not exists chat_rooms (
  id bigserial primary key,
  room_name varchar(255) not null,
  room_type varchar(255) not null,
  created_by bigint not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  deleted_at timestamp
);

-- room_participants
create table if not exists room_participants (
  id bigserial primary key,
  user_id bigint not null,
  room_id bigint not null,
  last_read_message_id bigint not null,
  role varchar(255) not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  deleted_at timestamp
);

-- messages
create table if not exists messages (
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

-- message_mentions (MessageEntity.mentions @ElementCollection)
create table if not exists message_mentions (
  message_id bigint not null,
  user_id bigint
);

-- file_attachments (includes @Embedded metadata + security columns)
create table if not exists file_attachments (
  id bigserial primary key,
  message_id bigint not null,
  uploader_id bigint not null,

  original_file_name varchar(255) not null,
  stored_file_name varchar(255) not null,
  content_type varchar(255) not null,
  file_size bigint not null,
  storage_path varchar(255) not null,
  storage_type varchar(255) not null,

  file_type varchar(255),

  visibility varchar(255) not null,
  download_policy varchar(255) not null,
  share_policy varchar(255) not null,
  scan_status varchar(255) not null,
  encryption varchar(255) not null,

  created_at timestamp not null,
  updated_at timestamp not null,
  deleted_at timestamp
);

create index if not exists idx_chat_rooms_created_by on chat_rooms(created_by);
create index if not exists idx_room_participants_user_id on room_participants(user_id);
create index if not exists idx_room_participants_room_id on room_participants(room_id);
create index if not exists idx_messages_room_id on messages(room_id);
create index if not exists idx_messages_user_id on messages(user_id);
create index if not exists idx_file_attachments_message_id on file_attachments(message_id);
create index if not exists idx_message_mentions_message_id on message_mentions(message_id);