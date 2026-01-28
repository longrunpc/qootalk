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

create index if not exists idx_file_attachments_message_id on file_attachments(message_id);
