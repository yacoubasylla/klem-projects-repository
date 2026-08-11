CREATE TABLE operation_commerce (
    id                  UUID PRIMARY KEY,
    nom                 VARCHAR(300) NOT NULL,
    code                VARCHAR(50) NOT NULL UNIQUE,
    type                VARCHAR(20) NOT NULL CHECK (type IN ('IMPORT', 'EXPORT', 'TRANSIT', 'CHANGE')),
    procedure_id        UUID NOT NULL REFERENCES procedure_metier (id),
    statut              VARCHAR(20) NOT NULL
                            CHECK (statut IN ('PROPOSEE', 'EN_REVISION', 'PUBLIEE', 'REJETEE')),
    created_by          VARCHAR(255) NOT NULL,
    validated_by        VARCHAR(255),
    validated_at        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL
);

CREATE TABLE operation_document (
    id                          UUID PRIMARY KEY,
    operation_id                UUID NOT NULL REFERENCES operation_commerce (id),
    document_id                 UUID NOT NULL REFERENCES document_requis (id),
    condition_applicabilite     VARCHAR(500),
    created_at                  TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_operation_document UNIQUE (operation_id, document_id)
);
