-- ============================================================
-- V1 : Schéma initial — Cantine Connect
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-06-30
-- IF NOT EXISTS sur chaque objet : idempotent sur base existante
-- ============================================================

-- ── 1. utilisateurs ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS utilisateurs (
    id           BIGSERIAL    PRIMARY KEY,
    nom          VARCHAR(100) NOT NULL,
    prenom       VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe TEXT         NOT NULL,
    role         VARCHAR(20)  NOT NULL DEFAULT 'GESTIONNAIRE',
    actif        BOOLEAN      NOT NULL DEFAULT true,
    created_at   TIMESTAMP    NOT NULL
);

-- ── 2. etablissements ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS etablissements (
    id         BIGSERIAL    PRIMARY KEY,
    nom        VARCHAR(150) NOT NULL,
    adresse    VARCHAR(255),
    ville      VARCHAR(100) NOT NULL DEFAULT 'Abidjan',
    telephone  VARCHAR(20),
    actif      BOOLEAN      NOT NULL DEFAULT true,
    created_at TIMESTAMP    NOT NULL
);

-- ── 3. niveaux ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS niveaux (
    id               BIGSERIAL   PRIMARY KEY,
    etablissement_id BIGINT      NOT NULL REFERENCES etablissements(id),
    libelle          VARCHAR(50) NOT NULL,
    ordre            INTEGER     NOT NULL DEFAULT 0
);

-- ── 4. classes ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS classes (
    id             BIGSERIAL   PRIMARY KEY,
    niveau_id      BIGINT      NOT NULL REFERENCES niveaux(id),
    libelle        VARCHAR(50) NOT NULL,
    annee_scolaire VARCHAR(10) NOT NULL
);

-- ── 5. eleves ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS eleves (
    id                 BIGSERIAL    PRIMARY KEY,
    etablissement_id   BIGINT       NOT NULL REFERENCES etablissements(id),
    classe_id          BIGINT       NOT NULL REFERENCES classes(id),
    matricule          VARCHAR(30)  NOT NULL UNIQUE,
    nom                VARCHAR(100) NOT NULL,
    prenom             VARCHAR(100) NOT NULL,
    date_naissance     DATE,
    photo_url          VARCHAR(255),
    qr_code_token      UUID         NOT NULL UNIQUE,
    statut_acces       VARCHAR(30)  NOT NULL DEFAULT 'EN_ATTENTE_PAIEMENT',
    date_fin_grace     DATE,
    est_boursier       BOOLEAN      NOT NULL DEFAULT false,
    regime_alimentaire VARCHAR(30)  NOT NULL DEFAULT 'STANDARD',
    parent_nom         VARCHAR(150) NOT NULL,
    parent_telephone   VARCHAR(20)  NOT NULL,
    parent_email       VARCHAR(100),
    allergies          TEXT,
    notes_medicales    TEXT,
    actif              BOOLEAN      NOT NULL DEFAULT true,
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_eleves_etablissement ON eleves(etablissement_id);
CREATE INDEX IF NOT EXISTS idx_eleves_classe        ON eleves(classe_id);
CREATE INDEX IF NOT EXISTS idx_eleves_qr_code_token ON eleves(qr_code_token);
CREATE INDEX IF NOT EXISTS idx_eleves_statut_acces  ON eleves(statut_acces);

-- ── 6. transactions_paiement ─────────────────────────────────
CREATE TABLE IF NOT EXISTS transactions_paiement (
    id                   BIGSERIAL      PRIMARY KEY,
    eleve_id             BIGINT         NOT NULL REFERENCES eleves(id),
    reference_interne    VARCHAR(64)    NOT NULL UNIQUE,
    reference_plateforme VARCHAR(128),
    operator_tx_id       VARCHAR(128),
    operateur            VARCHAR(20)    NOT NULL,
    montant              NUMERIC(12, 2) NOT NULL,
    devise               VARCHAR(5)     NOT NULL DEFAULT 'XOF',
    telephone_payeur     VARCHAR(20),
    statut               VARCHAR(20)    NOT NULL DEFAULT 'EN_ATTENTE',
    date_creation        TIMESTAMP      NOT NULL,
    date_mise_a_jour     TIMESTAMP,
    metadonnees_webhook  TEXT
);

CREATE INDEX IF NOT EXISTS idx_tp_eleve       ON transactions_paiement(eleve_id);
CREATE INDEX IF NOT EXISTS idx_tp_reference   ON transactions_paiement(reference_interne);
CREATE INDEX IF NOT EXISTS idx_tp_operator_tx ON transactions_paiement(operator_tx_id);
CREATE INDEX IF NOT EXISTS idx_tp_statut      ON transactions_paiement(statut);

-- ── 7. passages_refectoire ───────────────────────────────────
CREATE TABLE IF NOT EXISTS passages_refectoire (
    id            BIGSERIAL   PRIMARY KEY,
    eleve_id      BIGINT      NOT NULL REFERENCES eleves(id),
    qr_code_token UUID        NOT NULL,
    date_passage  DATE        NOT NULL,
    heure_passage TIMESTAMP   NOT NULL,
    resultat      VARCHAR(10) NOT NULL,
    motif_refus   VARCHAR(40)
);

CREATE INDEX IF NOT EXISTS idx_pr_eleve_date ON passages_refectoire(eleve_id, date_passage);
CREATE INDEX IF NOT EXISTS idx_pr_date       ON passages_refectoire(date_passage);
CREATE INDEX IF NOT EXISTS idx_pr_qr_token   ON passages_refectoire(qr_code_token);

-- ── 8. action_logs ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS action_logs (
    id            BIGSERIAL    PRIMARY KEY,
    auteur        VARCHAR(255) NOT NULL,
    type_action   VARCHAR(20)  NOT NULL,
    entite        VARCHAR(255) NOT NULL,
    entite_id     VARCHAR(255),
    payload_avant TEXT,
    payload_apres TEXT,
    date_action   TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_al_entite_id ON action_logs(entite, entite_id);
CREATE INDEX IF NOT EXISTS idx_al_auteur    ON action_logs(auteur);
CREATE INDEX IF NOT EXISTS idx_al_date      ON action_logs(date_action);
