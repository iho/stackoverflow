-- Seed base roles; safe to re-run via ON CONFLICT DO NOTHING
insert into roles (name) values ('ROLE_USER') on conflict (name) do nothing;
insert into roles (name) values ('ROLE_MODERATOR') on conflict (name) do nothing;
insert into roles (name) values ('ROLE_ADMIN') on conflict (name) do nothing;

