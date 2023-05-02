CREATE TABLE articles
(
    id                uuid PRIMARY KEY ,
    header            varchar(255) NOT NULL ,
    published_at      timestamp NOT NULL ,
    short_description varchar(1023) NOT NULL ,
    text              text NOT NULL
);