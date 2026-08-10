CREATE TABLE app_user (
    id                UUID PRIMARY KEY,
    keycloak_subject  VARCHAR(255),
    email             VARCHAR(255) NOT NULL,
    display_name      VARCHAR(200),
    created_at        TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX ux_app_user_email ON app_user (email);
CREATE UNIQUE INDEX ux_app_user_keycloak_subject ON app_user (keycloak_subject) WHERE keycloak_subject IS NOT NULL;

CREATE TABLE tenant_membership (
    id           UUID PRIMARY KEY,
    user_id      UUID NOT NULL REFERENCES app_user (id),
    tenant_id    UUID NOT NULL REFERENCES tenant (id),
    status       VARCHAR(20) NOT NULL CHECK (status IN ('INVITED', 'ACTIVE')),
    invited_at   TIMESTAMPTZ NOT NULL,
    activated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX ux_tenant_membership_user_tenant ON tenant_membership (user_id, tenant_id);
