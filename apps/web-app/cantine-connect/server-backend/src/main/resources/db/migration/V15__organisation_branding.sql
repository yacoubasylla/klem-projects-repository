-- ============================================================
-- V15 : Personnalisation par client (branding)
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-08-04
-- Contexte : chaque déploiement de Cantine Connect sert un seul client
-- (réseau scolaire) — logo, nom, coordonnées et numéro Mobile Money de
-- réception doivent être paramétrables sans redéploiement, au même titre
-- que les autres réglages de la table `configurations` (ex. FOND_ECRAN_LOGIN).
-- ============================================================

INSERT INTO configurations (cle, valeur, description) VALUES
  ('ORGANISATION_NOM', 'Cantine Connect', 'Nom du client affiché dans l''en-tête de l''application (remplace "Cantine Connect" par défaut)'),
  ('ORGANISATION_LOGO_URL', '', 'URL du logo du client, affiché dans l''en-tête de l''application (vide = logo par défaut)'),
  ('ORGANISATION_ADRESSE', '', 'Adresse / lieu du client (ville, commune)'),
  ('ORGANISATION_TELEPHONE', '', 'Numéro de téléphone / cellulaire de contact du client'),
  ('ORGANISATION_EMAIL', '', 'Adresse email de contact du client'),
  ('ORGANISATION_MOBILE_MONEY_NUMERO', '', 'Numéro Mobile Money du client pour la réception manuelle de paiements (affichage informatif — ne pilote pas le routage CinetPay/PayDunya)')
ON CONFLICT (cle) DO NOTHING;
