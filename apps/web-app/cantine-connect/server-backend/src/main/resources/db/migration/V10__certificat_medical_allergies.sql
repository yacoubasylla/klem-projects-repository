-- ============================================================
-- V10 : Justificatif médical pour les allergies déclarées
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-08-03
-- Contexte : une allergie ne peut désormais être déclarée que sur
-- présentation d'un certificat médical d'un allergologue. La
-- contrainte (allergie renseignée ⇒ certificat obligatoire) est
-- appliquée côté service (EleveService), pas en SQL, pour pouvoir
-- renvoyer un message métier clair.
-- ============================================================

ALTER TABLE eleves ADD COLUMN IF NOT EXISTS certificat_medical_url VARCHAR(255);
