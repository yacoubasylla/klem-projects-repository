CREATE TABLE texte_reglementaire (
    id                  UUID PRIMARY KEY,
    titre               VARCHAR(500) NOT NULL,
    type                VARCHAR(50) NOT NULL,
    date_publication    DATE,
    reference           VARCHAR(200),
    domaine             VARCHAR(200),
    url_source          VARCHAR(500),
    statut              VARCHAR(20) NOT NULL
                            CHECK (statut IN ('PROPOSEE', 'EN_REVISION', 'PUBLIEE', 'REJETEE')),
    created_by          VARCHAR(255) NOT NULL,
    validated_by        VARCHAR(255),
    validated_at        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL
);
