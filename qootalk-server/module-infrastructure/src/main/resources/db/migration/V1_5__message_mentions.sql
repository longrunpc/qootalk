-- message_mentions (MessageEntity.mentions @ElementCollection)
create table message_mentions (
  message_id bigint not null,
  user_id bigint
);

create index idx_message_mentions_message_id on message_mentions(message_id);
