-- ============================================================
-- V13 : Sexe et lieu d'habitation de l'élève
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-08-03
-- Contexte : nécessaires au formulaire d'ajout d'enfant self-service
-- par le parent (nom, prénom, sexe, âge/date de naissance, résidence).
-- ============================================================

ALTER TABLE eleves ADD COLUMN IF NOT EXISTS sexe     VARCHAR(1);
ALTER TABLE eleves ADD COLUMN IF NOT EXISTS ville     VARCHAR(100);
ALTER TABLE eleves ADD COLUMN IF NOT EXISTS commune   VARCHAR(100);
ALTER TABLE eleves ADD COLUMN IF NOT EXISTS quartier  VARCHAR(100);
