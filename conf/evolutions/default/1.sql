# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table users (
  id                        bigint not null,
  name                      varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  phone_number              varchar(45) not null,
  image                     TEXT,
  created_at                timestamp,
  updated_at                timestamp,
  constraint uq_users_email unique (email),
  constraint pk_users primary key (id))
;

create sequence user_seq;




# --- !Downs

drop table if exists users cascade;

drop sequence if exists user_seq;

