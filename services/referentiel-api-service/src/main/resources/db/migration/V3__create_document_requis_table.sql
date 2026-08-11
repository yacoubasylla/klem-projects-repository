CREATE TABLE document_requis (
    id                  UUID PRIMARY KEY,
    nom                 VARCHAR(300) NOT NULL,
    code                VARCHAR(50) NOT NULL,
    description         TEXT,
    regle_validation    VARCHAR(1000),
    statut              VARCHAR(20) NOT NULL
                            CHECK (statut IN ('PROPOSEE', 'EN_REVISION', 'PUBLIEE', 'REJETEE')),
    created_by          VARCHAR(255) NOT NULL,
    validated_by        VARCHAR(255),
    validated_at        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL
);
