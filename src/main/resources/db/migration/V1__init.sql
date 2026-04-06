create table if not exists received_events (
  id bigserial primary key,
  event_id uuid not null unique,
  event_type varchar(200) not null,
  key_id varchar(64) not null,
  producer_id varchar(128) not null,
  aggregate_id varchar(256) not null,
  occurred_at timestamptz not null,
  payload jsonb not null,
  signature varchar(512) not null,
  received_at timestamptz not null default now()
);

create table if not exists rejected_events (
  id bigserial primary key,
  event_id uuid null,
  event_type varchar(200) null,
  key_id varchar(64) null,
  producer_id varchar(128) null,
  aggregate_id varchar(256) null,
  occurred_at timestamptz null,
  payload jsonb null,
  signature varchar(512) null,
  reason varchar(200) not null,
  details text null,
  rejected_at timestamptz not null default now()
);
