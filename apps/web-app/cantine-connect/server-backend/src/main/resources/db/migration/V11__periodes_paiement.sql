-- ============================================================
-- V11 : Périodes d'abonnement (trimestriel/annuel) & architecture
--       paiement multi-provider
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-08-03
-- Contexte : règle métier — les paiements en mode ABONNEMENT sont
-- désormais trimestriels ou annuels uniquement (pas de mensualisation).
-- Le mode CREDITS (portefeuille prépayé, débit par repas) reste
-- inchangé et coexiste avec l'abonnement. Ajout également de la
-- clé de sélection du provider de paiement actif (architecture
-- multi-rails Orange/MTN/Moov/CinetPay/PayDunya).
-- ============================================================

ALTER TABLE eleves ADD COLUMN IF NOT EXISTS periode_abonnement VARCHAR(20);

INSERT INTO configurations (cle, valeur, description) VALUES
  ('TARIF_TRIMESTRE',        '45000',   'Tarif de l''abonnement trimestriel en FCFA (mode ABONNEMENT)'),
  ('TARIF_ANNEE',            '150000',  'Tarif de l''abonnement annuel en FCFA (mode ABONNEMENT)'),
  ('PAIEMENT_PROVIDER_ACTIF','CINETPAY','Provider de paiement actif : CINETPAY, PAYDUNYA, ORANGE_MONEY_DIRECT, MTN_MONEY_DIRECT, MOOV_MONEY_DIRECT')
ON CONFLICT (cle) DO NOTHING;
