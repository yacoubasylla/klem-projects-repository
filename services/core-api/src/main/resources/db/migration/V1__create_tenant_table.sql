CREATE TABLE tenant (
    id          UUID PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    sector      VARCHAR(100),
    status      VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'ACTIVE', 'SUSPENDED')),
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL
);
