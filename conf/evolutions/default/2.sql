
# --- !Ups

create table product (
  id                        bigint not null,
  image                     TEXT,
  name                      varchar(255),
  price                     varchar(255),
  description               varchar(255),
  created_at                timestamp,
  updated_at                timestamp,

  constraint pk_product primary key (id))
;

create sequence product_seq;




# --- !Downs

drop table if exists product cascade;

drop sequence if exists product_seq;

