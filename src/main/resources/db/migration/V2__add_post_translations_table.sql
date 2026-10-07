CREATE TABLE post_translations (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    post_id    BIGINT NOT NULL,
    locale     VARCHAR(5) NOT NULL,
    title      VARCHAR(255) NOT NULL,
    slug       VARCHAR(255) NOT NULL,
    summary    VARCHAR(500),
    content    TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_post_translations_post
        FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT uq_post_translations_post_locale
        UNIQUE (post_id, locale),
    CONSTRAINT uq_post_translations_locale_slug
        UNIQUE (locale, slug),
    CONSTRAINT chk_post_translations_locale
        CHECK (locale IN ('en', 'pt')),
    CONSTRAINT chk_post_translations_slug
        CHECK (slug ~ '^[a-z0-9]+(-[a-z0-9]+)*$')
);
