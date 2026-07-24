-- ============================================================
-- V5 : Numéro de cellulaire obligatoire et unique sur utilisateurs
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-07-01
-- Contexte : les parents seront notifiés par SMS — chaque compte
-- utilisateur doit désormais avoir un numéro de cellulaire.
-- ============================================================

ALTER TABLE utilisateurs ADD COLUMN IF NOT EXISTS telephone VARCHAR(20);

-- Backfill des comptes existants avec une valeur unique temporaire
-- (à corriger manuellement par un ADMIN via l'écran Utilisateurs)
UPDATE utilisateurs SET telephone = 'A-COMPLETER-' || id WHERE telephone IS NULL;

ALTER TABLE utilisateurs ALTER COLUMN telephone SET NOT NULL;

ALTER TABLE utilisateurs ADD CONSTRAINT utilisateurs_telephone_unique UNIQUE (telephone);
