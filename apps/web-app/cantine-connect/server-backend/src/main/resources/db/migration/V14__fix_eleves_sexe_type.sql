-- ============================================================
-- V14 : Correction du type de eleves.sexe
-- Auteur  : Yacouba SYLLA
-- Date    : 2026-08-03
-- Contexte : la V13 a créé sexe en VARCHAR(1). Hibernate 6, avec
-- @Column(length = 1) sur un enum @Enumerated(STRING), valide le schéma
-- en attendant un type CHAR(1) et refuse de démarrer sur VARCHAR(1)
-- ("Schema-validation: wrong column type"). Élargi à VARCHAR(10),
-- aligné sur l'entité corrigée (@Column(length = 10)).
-- ============================================================

ALTER TABLE eleves ALTER COLUMN sexe TYPE VARCHAR(10);
