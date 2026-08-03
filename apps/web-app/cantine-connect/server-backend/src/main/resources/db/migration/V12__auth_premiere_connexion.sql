-- ============================================================
-- V12 : Mot de passe temporaire — changement forcé à la 1ère connexion
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-08-03
-- Contexte : à la validation d'une demande d'accès parent, un mot de
-- passe temporaire est généré. Ce flag force son changement dès la
-- première connexion réussie.
-- ============================================================

ALTER TABLE utilisateurs ADD COLUMN IF NOT EXISTS doit_changer_mot_de_passe BOOLEAN NOT NULL DEFAULT false;
