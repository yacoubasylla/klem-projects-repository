-- ============================================================
-- V7 : Configuration du rafraîchissement automatique du cache
--      offline (Scan Réfectoire)
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-07-01
-- Contexte : le cache local de secours (mode dégradé 24h) n'était
-- téléchargé que sur action manuelle de l'agent. Ajout d'une clé de
-- configuration permettant d'activer/désactiver le rafraîchissement
-- automatique à l'ouverture de la page Scan Réfectoire.
-- ============================================================

INSERT INTO configurations (cle, valeur, description) VALUES
  ('SCAN_CACHE_AUTO_REFRESH', 'true', 'Rafraîchir automatiquement le cache hors-ligne à l''ouverture de Scan Réfectoire (si en ligne)')
ON CONFLICT (cle) DO NOTHING;
