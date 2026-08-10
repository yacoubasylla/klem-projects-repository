CREATE TABLE role_assignment (
    id         UUID PRIMARY KEY,
    tenant_id  UUID NOT NULL REFERENCES tenant (id),
    user_id    UUID NOT NULL REFERENCES app_user (id),
    role_code  VARCHAR(20) NOT NULL CHECK (role_code IN
        ('ADMIN', 'OPERATEUR', 'CLIENT', 'MEDECIN', 'PHARMACIEN', 'CHAUFFEUR', 'PRESTATAIRE')),
    granted_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX ux_role_assignment_tenant_user_role ON role_assignment (tenant_id, user_id, role_code);
