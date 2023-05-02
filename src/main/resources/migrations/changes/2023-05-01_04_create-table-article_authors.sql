CREATE TABLE article_authors
(
    article_id uuid NOT NULL
        REFERENCES articles (id) ON UPDATE CASCADE ON DELETE CASCADE,
    author_id  uuid NOT NULL
        REFERENCES authors (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT article_author_pkey PRIMARY KEY (article_id, author_id)
);