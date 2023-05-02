CREATE TABLE article_keywords
(
    article_id uuid NOT NULL
        REFERENCES articles (id) ON UPDATE CASCADE ON DELETE CASCADE,
    keyword_id uuid NOT NULL
        REFERENCES keywords (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT article_keyword_pkey PRIMARY KEY (article_id, keyword_id)
);