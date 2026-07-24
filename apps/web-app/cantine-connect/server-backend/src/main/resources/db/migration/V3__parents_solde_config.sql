-- ============================================================
-- V3 : Comptes parents, solde élèves, nouvelles configurations
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-07-01
-- ============================================================

-- ── 1. Solde sur les élèves (mode crédits) ───────────────────
ALTER TABLE eleves ADD COLUMN IF NOT EXISTS solde DECIMAL(10,2) NOT NULL DEFAULT 0.00;

-- ── 2. Table parents ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS parents (
    id             BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT    NOT NULL UNIQUE REFERENCES utilisateurs(id),
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_parents_utilisateur ON parents(utilisateur_id);

-- ── 3. Liaison parents ↔ élèves ──────────────────────────────
CREATE TABLE IF NOT EXISTS parents_eleves (
    parent_id BIGINT NOT NULL REFERENCES parents(id) ON DELETE CASCADE,
    eleve_id  BIGINT NOT NULL REFERENCES eleves(id)  ON DELETE CASCADE,
    PRIMARY KEY (parent_id, eleve_id)
);

CREATE INDEX IF NOT EXISTS idx_pe_parent ON parents_eleves(parent_id);
CREATE INDEX IF NOT EXISTS idx_pe_eleve  ON parents_eleves(eleve_id);

-- ── 4. Nouvelles clés de configuration ──────────────────────
INSERT INTO configurations (cle, valeur, description) VALUES
  ('NOTIFICATIONS_EMAIL_ENABLED', 'false',       'Activer les notifications email aux parents'),
  ('NOTIFICATIONS_SMS_ENABLED',   'false',       'Activer les notifications SMS aux parents'),
  ('MODE_PAIEMENT',               'ABONNEMENT',  'Mode accès : ABONNEMENT (accès annuel) ou CREDITS (débit par repas)'),
  ('TARIF_REPAS',                 '500',         'Tarif unitaire par repas en FCFA (mode CREDITS)'),
  ('FOND_ECRAN_LOGIN',            '',            'URL image de fond pour la page de connexion')
ON CONFLICT (cle) DO NOTHING;
