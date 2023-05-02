CREATE TABLE tokens
(
    id      uuid PRIMARY KEY,
    value   varchar(255) NOT NULL,
    revoked boolean      NOT NULL,
    user_id uuid         NOT NULL REFERENCES users (id)
);