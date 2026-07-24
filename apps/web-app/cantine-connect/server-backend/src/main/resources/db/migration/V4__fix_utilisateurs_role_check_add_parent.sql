-- ============================================================
-- V4 : Correction de la contrainte CHECK sur utilisateurs.role
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-07-01
-- Contexte : la V3 a introduit la table parents (FK vers
-- utilisateurs) et le rôle PARENT côté application, mais la
-- contrainte CHECK héritée du schéma initial n'autorisait que
-- ADMIN / GESTIONNAIRE / CAISSIER. Toute tentative de création
-- d'un compte PARENT échouait donc au niveau base de données.
-- ============================================================

ALTER TABLE utilisateurs DROP CONSTRAINT IF EXISTS utilisateurs_role_check;

ALTER TABLE utilisateurs ADD CONSTRAINT utilisateurs_role_check
    CHECK (role::text = ANY (ARRAY['ADMIN', 'GESTIONNAIRE', 'CAISSIER', 'PARENT']::text[]));
