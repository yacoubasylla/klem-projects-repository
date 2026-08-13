-- Généralisation de transactions_paiement (apps/web-app/cantine-connect/server-backend
-- V1__init_schema.sql) au socle multi-tenant, multi-opérateur, multi-mode d'intégration décrit
-- dans shared_architecture/billing_&_payments/specifications_techniques.md §5/§6.

CREATE TABLE IF NOT EXISTS transactions (
    id                BIGSERIAL      PRIMARY KEY,
    uuid              UUID           NOT NULL UNIQUE,
    tenant_id         VARCHAR(64)    NOT NULL,
    idempotency_key   VARCHAR(128)   NOT NULL,
    operator          VARCHAR(30)    NOT NULL,
    aggregator        VARCHAR(20),
    integration_mode  VARCHAR(20)    NOT NULL,
    amount            NUMERIC(12, 2) NOT NULL,
    currency          VARCHAR(5)     NOT NULL DEFAULT 'XOF',
    payer_reference   VARCHAR(64),
    operator_tx_id    VARCHAR(128),
    status            VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    created_at        TIMESTAMP      NOT NULL,
    updated_at        TIMESTAMP      NOT NULL,

    CONSTRAINT uq_transactions_idempotency_key UNIQUE (idempotency_key),
    CONSTRAINT ck_transactions_aggregator_mode CHECK (
        (integration_mode = 'AGGREGATOR' AND aggregator IS NOT NULL) OR
        (integration_mode = 'DIRECT_API' AND aggregator IS NULL)
    )
);

CREATE INDEX IF NOT EXISTS idx_tx_tenant           ON transactions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tx_operator_tx_id   ON transactions(operator_tx_id);
CREATE INDEX IF NOT EXISTS idx_tx_status           ON transactions(status);

-- Journal d'audit append-only — spécifications_fonctionnelles.md §4.3, distinct de la ligne
-- `transactions` elle-même (qui, elle, porte l'état courant) pour garder un historique complet
-- de chaque transition, même déjà écrasée sur la ligne courante.
CREATE TABLE IF NOT EXISTS transaction_audit_log (
    id                BIGSERIAL   PRIMARY KEY,
    transaction_uuid  UUID        NOT NULL REFERENCES transactions(uuid),
    previous_status   VARCHAR(20),
    new_status        VARCHAR(20) NOT NULL,
    raw_webhook_payload TEXT,
    occurred_at       TIMESTAMP   NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_tal_transaction ON transaction_audit_log(transaction_uuid);
