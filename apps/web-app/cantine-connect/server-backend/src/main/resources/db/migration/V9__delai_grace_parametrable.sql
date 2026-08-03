-- ============================================================
-- V9 : Délai de grâce paramétrable
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-08-03
-- Contexte : le délai de grâce (accès toléré après échéance de
-- paiement) était géré au cas par cas via eleves.date_fin_grace,
-- sans valeur de référence. Ajout d'une valeur globale par défaut
-- (7 jours) et d'un override optionnel par établissement.
-- ============================================================

ALTER TABLE etablissements ADD COLUMN IF NOT EXISTS delai_grace_jours INTEGER;

INSERT INTO configurations (cle, valeur, description) VALUES
  ('DELAI_GRACE_JOURS_DEFAUT', '7', 'Délai de grâce par défaut (en jours) accordé après échéance de paiement, si non surchargé par établissement')
ON CONFLICT (cle) DO NOTHING;
