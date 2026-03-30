-- message_mentions (MessageEntity.mentions @ElementCollection)
create table message_mentions (
  message_id bigint not null,
  user_id bigint not null,
  constraint uk_message_mentions_message_user unique (message_id, user_id)
);
