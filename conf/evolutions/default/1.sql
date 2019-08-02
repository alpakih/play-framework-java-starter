# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table category (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_category primary key (id))
;

create table customer (
  id                        bigint not null,
  name                      varchar(255),
  address                   varchar(255),
  constraint pk_customer primary key (id))
;

create table details_order (
  id                        bigint not null,
  order_id                  bigint,
  product_id                bigint,
  price                     float,
  quantity                  integer,
  sub_total                 float,
  constraint pk_details_order primary key (id))
;

create table order_product (
  id                        bigint not null,
  no_transaksi              varchar(255),
  customer_id               bigint,
  created_at                timestamp,
  updated_at                timestamp,
  total                     float,
  constraint pk_order_product primary key (id))
;

create table product (
  id                        bigint not null,
  image                     TEXT,
  name                      varchar(255),
  price                     float,
  category_id               bigint,
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

create sequence customer_seq;

create sequence details_order_seq;

create sequence order_seq;

create sequence product_seq;

create sequence user_seq;

alter table details_order add constraint fk_details_order_order_1 foreign key (order_id) references order_product (id);
create index ix_details_order_order_1 on details_order (order_id);
alter table details_order add constraint fk_details_order_product_2 foreign key (product_id) references product (id);
create index ix_details_order_product_2 on details_order (product_id);
alter table order_product add constraint fk_order_product_customer_3 foreign key (customer_id) references customer (id);
create index ix_order_product_customer_3 on order_product (customer_id);
alter table product add constraint fk_product_category_4 foreign key (category_id) references category (id);
create index ix_product_category_4 on product (category_id);



# --- !Downs

drop table if exists category cascade;

drop table if exists customer cascade;

drop table if exists details_order cascade;

drop table if exists order_product cascade;

drop table if exists product cascade;

drop table if exists users cascade;

drop sequence if exists category_seq;

drop sequence if exists customer_seq;

drop sequence if exists details_order_seq;

drop sequence if exists order_seq;

drop sequence if exists product_seq;

drop sequence if exists user_seq;

