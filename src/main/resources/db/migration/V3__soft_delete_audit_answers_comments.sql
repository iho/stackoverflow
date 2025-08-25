-- Add soft-delete and auditing columns to answers and comments
-- This aligns DB schema with entity changes (@SQLDelete/@Where and @CreatedBy/@LastModifiedBy)

-- answers: add deleted flag and auditing columns
alter table if exists answers
    add column if not exists deleted boolean not null default false,
    add column if not exists created_by varchar(100),
    add column if not exists updated_by varchar(100);

-- comments: add deleted flag and auditing columns
alter table if exists comments
    add column if not exists deleted boolean not null default false,
    add column if not exists created_by varchar(100),
    add column if not exists updated_by varchar(100);

