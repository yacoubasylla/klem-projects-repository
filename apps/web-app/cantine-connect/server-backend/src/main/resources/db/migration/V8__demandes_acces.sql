-- ============================================================
-- V8 : File d'attente des demandes d'accès parent (self-service)
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-08-03
-- Contexte : refonte premium — les parents soumettent une demande
-- publique (nom, contact, résidence) sans créer de compte. Un
-- ADMIN valide/rejette la demande (workflow d'approbation traité
-- en phase 2) ; cette migration ne pose que la table de file
-- d'attente.
-- ============================================================

CREATE TABLE IF NOT EXISTS demandes_acces (
    id                    BIGSERIAL    PRIMARY KEY,
    nom                   VARCHAR(100) NOT NULL,
    prenom                VARCHAR(100) NOT NULL,
    fonction              VARCHAR(100),
    telephone_principal   VARCHAR(20)  NOT NULL,
    telephone_whatsapp    VARCHAR(20),
    telephone_secondaire  VARCHAR(20),
    email                 VARCHAR(150),
    ville                 VARCHAR(100) NOT NULL,
    commune               VARCHAR(100) NOT NULL,
    quartier              VARCHAR(100),
    statut                VARCHAR(20)  NOT NULL DEFAULT 'EN_ATTENTE',
    date_soumission       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_traitement       TIMESTAMP,
    traite_par            VARCHAR(150),
    motif_rejet           TEXT
);

CREATE INDEX IF NOT EXISTS idx_da_telephone_principal ON demandes_acces(telephone_principal);
CREATE INDEX IF NOT EXISTS idx_da_email                ON demandes_acces(email);
CREATE INDEX IF NOT EXISTS idx_da_statut                ON demandes_acces(statut);
