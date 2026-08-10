-- Pas de FK vers tenant(id) : le journal d'audit reste délibérément indépendant des autres
-- domaines, y compris au niveau de la contrainte d'intégrité référentielle (README.md §4 :
-- "audit ne dépend de rien d'autre que shared").
CREATE TABLE audit_entry (
    id           UUID PRIMARY KEY,
    event_id     UUID NOT NULL,
    event_type   VARCHAR(50) NOT NULL,
    tenant_id    UUID,
    aggregate_id UUID,
    occurred_at  TIMESTAMPTZ NOT NULL,
    recorded_at  TIMESTAMPTZ NOT NULL,
    payload      TEXT NOT NULL
);

CREATE UNIQUE INDEX ux_audit_entry_event_id ON audit_entry (event_id);
CREATE INDEX ix_audit_entry_tenant_id ON audit_entry (tenant_id);
