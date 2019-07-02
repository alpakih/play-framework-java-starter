# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table category (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_category primary key (id))
;

create table product (
  id                        bigint not null,
  image                     TEXT,
  name                      varchar(255),
  price                     float,
  description               varchar(255),
  created_at                timestamp,
  updated_at                timestamp,
  constraint pk_product primary key (id))
;

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

create sequence category_seq;

create sequence product_seq;

create sequence user_seq;




# --- !Downs

drop table if exists category cascade;

drop table if exists product cascade;

drop table if exists users cascade;

drop sequence if exists category_seq;

drop sequence if exists product_seq;

drop sequence if exists user_seq;

