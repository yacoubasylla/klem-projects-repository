-- Canal utilisé pour envoyer le code OTP au numéro du parent (WHATSAPP ou SMS) —
-- WhatsApp par défaut, paramétrable par un ADMIN (voir ConfigurationController).
-- L'envoi par email reste indépendant de ce choix (toujours tenté en parallèle).
INSERT INTO configurations (cle, valeur, description) VALUES
  ('PARENT_OTP_CANAL_TELEPHONE', 'WHATSAPP', 'Canal utilisé pour le code de vérification (OTP) parent : WHATSAPP ou SMS')
ON CONFLICT (cle) DO NOTHING;
