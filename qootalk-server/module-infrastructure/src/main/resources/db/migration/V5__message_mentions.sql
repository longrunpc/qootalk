-- message_mentions (MessageEntity.mentions @ElementCollection)
create table if not exists message_mentions (
  message_id bigint not null,
  user_id bigint
);

create index if not exists idx_message_mentions_message_id on message_mentions(message_id);
