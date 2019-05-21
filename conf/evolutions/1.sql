# --- !Ups

CREATE TABLE users
(
    id           serial,
    name         varchar(100) NOT NULL,
    email        varchar(50) NOT NULL,
    password     varchar(255) NOT NULL,
    phone_number varchar(20) NOT NULL,
    image        varchar(255),
    created_at   timestamp,
    updated_at   timestamp,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE users;