# --- !Ups

CREATE TABLE users (
    id serial,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,
    isAdmin boolean NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE users;