-- SEQUENCE
create sequence nut_user_seq start with 1 increment by 1 no cycle;
create sequence nut_role_seq start with 1 increment by 1 no cycle;

-- TABLE
create table nut_user
(
    id       bigint       not null,
    username varchar(255) not null,
    email    varchar(255) not null,
    enabled  bool         not null default false,
    locked   bool         not null default false
);

create table nut_role
(
    id   bigint      not null,
    name varchar(16) not null
);

create table nut_user_role
(
    user_id bigint not null,
    role_id bigint not null
);

-- PK
alter table nut_user
    add primary key (id);

alter table nut_role
    add primary key (id);

-- UNIQUE
alter table nut_user
    add constraint u_user_login unique (username);
alter table nut_user
    add constraint u_user_email unique (email);

-- FK
alter table nut_user_role
    add foreign key (user_id) references nut_user (id);
alter table nut_user_role
    add foreign key (role_id) references nut_role (id);

-- DATA
insert into nut_role(id, name)
values (nextval('nut_role_seq'), 'ROLE_ADMIN');
insert into nut_role(id, name)
values (nextval('nut_role_seq'), 'ROLE_USER');
