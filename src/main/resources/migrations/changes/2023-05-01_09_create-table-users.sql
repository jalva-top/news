CREATE TABLE users
(
    id       uuid PRIMARY KEY,
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    role varchar(20) NOT NULL,
    active   boolean      NOT NULL
);