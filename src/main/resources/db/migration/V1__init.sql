-- PostgreSQL initial schema for Stackoverflow clone
-- Tables use auditing fields created_at, updated_at and an optimistic lock version column

create table if not exists roles (
    id bigserial primary key,
    name varchar(50) not null unique,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0
);
create index if not exists ix_roles_name on roles(name);

create table if not exists users (
    id bigserial primary key,
    username varchar(50) not null unique,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    reputation int not null default 1,
    about text,
    last_login timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0
);
create index if not exists ix_users_username on users(username);
create index if not exists ix_users_email on users(email);

create table if not exists user_roles (
    user_id bigint not null references users(id) on delete cascade,
    role_id bigint not null references roles(id) on delete cascade,
    primary key (user_id, role_id)
);

create table if not exists tags (
    id bigserial primary key,
    name varchar(50) not null unique,
    description text,
    question_count int not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0
);
create index if not exists ix_tags_name on tags(name);

create table if not exists questions (
    id bigserial primary key,
    title varchar(200) not null,
    body text not null,
    vote_count int not null default 0,
    answer_count int not null default 0,
    views bigint not null default 0,
    closed boolean not null default false,
    deleted boolean not null default false,
    user_id bigint not null references users(id) on delete restrict,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0
);
create index if not exists ix_questions_title on questions(title);

create table if not exists answers (
    id bigserial primary key,
    body text not null,
    vote_count int not null default 0,
    is_accepted boolean not null default false,
    user_id bigint not null references users(id) on delete restrict,
    question_id bigint not null references questions(id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0
);

create table if not exists comments (
    id bigserial primary key,
    body text not null,
    vote_count int not null default 0,
    post_type varchar(20) not null,
    post_id bigint not null,
    user_id bigint not null references users(id) on delete restrict,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0
);
create index if not exists ix_comments_post on comments(post_type, post_id);

create table if not exists votes (
    id bigserial primary key,
    post_type varchar(20) not null,
    post_id bigint not null,
    user_id bigint not null references users(id) on delete cascade,
    vote_value int not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0,
    constraint uk_vote_post_user unique (post_type, post_id, user_id)
);

create table if not exists bookmarks (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    question_id bigint references questions(id) on delete cascade,
    answer_id bigint references answers(id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0,
    constraint ix_bookmark_user_question unique (user_id, question_id),
    constraint ix_bookmark_user_answer unique (user_id, answer_id)
);

create table if not exists badges (
    id bigserial primary key,
    name varchar(100) not null unique,
    description text,
    type varchar(20) not null,
    icon_url varchar(1024),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0
);
create index if not exists ix_badges_name on badges(name);

create table if not exists user_badges (
    user_id bigint not null references users(id) on delete cascade,
    badge_id bigint not null references badges(id) on delete cascade,
    awarded_at timestamptz not null default now(),
    primary key (user_id, badge_id)
);

create table if not exists question_tags (
    question_id bigint not null references questions(id) on delete cascade,
    tag_id bigint not null references tags(id) on delete cascade,
    primary key (question_id, tag_id)
);

