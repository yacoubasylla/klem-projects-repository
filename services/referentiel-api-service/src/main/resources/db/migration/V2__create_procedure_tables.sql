CREATE TABLE procedure_metier (
    id                  UUID PRIMARY KEY,
    nom                 VARCHAR(300) NOT NULL,
    code                VARCHAR(50) NOT NULL,
    description         TEXT,
    acteurs             VARCHAR(500),
    statut              VARCHAR(20) NOT NULL
                            CHECK (statut IN ('PROPOSEE', 'EN_REVISION', 'PUBLIEE', 'REJETEE')),
    created_by          VARCHAR(255) NOT NULL,
    validated_by        VARCHAR(255),
    validated_at        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL
);

CREATE TABLE procedure_texte (
    id              UUID PRIMARY KEY,
    procedure_id    UUID NOT NULL REFERENCES procedure_metier (id),
    texte_id        UUID NOT NULL REFERENCES texte_reglementaire (id),
    created_at      TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_procedure_texte UNIQUE (procedure_id, texte_id)
);
