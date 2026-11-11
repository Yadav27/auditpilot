CREATE TABLE tenant (
    id          uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    slug        varchar(63)  NOT NULL UNIQUE
                CHECK (slug ~ '^[a-z0-9]+(-[a-z0-9]+)*$'),
    name        varchar(200) NOT NULL,
    status      varchar(16)  NOT NULL DEFAULT 'ACTIVE'
                CHECK (status IN ('ACTIVE', 'SUSPENDED')),
    created_at  timestamptz  NOT NULL DEFAULT now()
);