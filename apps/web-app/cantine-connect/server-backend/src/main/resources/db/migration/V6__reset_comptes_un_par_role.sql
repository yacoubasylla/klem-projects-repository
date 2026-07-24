-- ============================================================
-- V6 : Réinitialisation des comptes utilisateurs (un par rôle)
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-07-01
-- Contexte : nettoyage des comptes de test avant mise en avant du
-- formulaire de connexion — un seul compte de référence par rôle,
-- avec numéro de cellulaire unique (obligatoire depuis la V5).
-- Mots de passe hashés avec BCrypt (identiques au format généré
-- par PasswordEncoderConfig / BCryptPasswordEncoder par défaut).
-- ============================================================

DELETE FROM parents_eleves;
DELETE FROM parents;
DELETE FROM utilisateurs;

INSERT INTO utilisateurs (nom, prenom, email, telephone, mot_de_passe, role, actif, created_at) VALUES
    ('admin',         'Agent', 'admin@cantine.connect',         '0707388678', '$2b$10$vtsaM/DNPIYwbVB9ZfCUzeN8JeyzLNbaGG1FmBSXDK/Ep3lybMtjy', 'ADMIN',        true, now()),
    ('gestionnaire',  'Agent', 'gestionnaire@cantine.connect',  '0707388679', '$2b$10$lKRSsxmItlxZzV7QJWiXSOzbWOnBNYkHCnag43Nj/awvgm8Q0AVKm', 'GESTIONNAIRE', true, now()),
    ('caissier',      'Agent', 'caissier@cantine.connect',      '0707388680', '$2b$10$KVZXkauHk7PJZwLll2Z7p.Q3lW8gLdI16/xY3dTyWVyZFTQNprd1G', 'CAISSIER',     true, now()),
    ('parent',        'Agent', 'parent@cantine.connect',        '0707388681', '$2b$10$0oRMO4eQt2uT9L3PDhaBLOWOvcaq/hZB5DEWW/NqgquMJ/5TNRr/m', 'PARENT',       true, now());
