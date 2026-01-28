-- audit_logs
create table if not exists audit_logs (
  id bigserial primary key,
  actor_type varchar(255) not null,
  actor_id bigint not null,
  action_type varchar(255) not null,
  target_type varchar(255) not null,
  target_id bigint not null,
  reason varchar(255),
  occurred_at timestamp not null
);
