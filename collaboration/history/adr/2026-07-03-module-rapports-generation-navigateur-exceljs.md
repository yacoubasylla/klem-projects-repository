# ADR-016 : Module Rapports — Génération Côté Navigateur, `exceljs` au lieu de `xlsx`/SheetJS

**Date :** 2026-07-03
**Statut :** Accepté
**Décideur :** Yacouba SYLLA

---

## Contexte

Demande d'une première version d'un module « Rapports » réservé à GESTIONNAIRE/CAISSIER (et ADMIN, cohérent avec le reste de l'application — cf. discussion produit) permettant d'imprimer/exporter en PDF ou Excel des états financiers, statistiques, paiements et passages, explicitement présentée comme une version exploratoire destinée à recueillir des retours avant d'aller plus loin avec le client.

Aucune infrastructure de génération PDF/Excel n'existait dans le projet (ni côté backend — aucune dépendance iText/POI/JasperReports dans `pom.xml` —, ni côté frontend). L'endpoint `GET /paiements/{id}/recu` mentionné dans `specifications.md` n'a jamais été implémenté.

## Options Envisagées

- **Génération côté serveur** (nouvelle dépendance Maven type OpenPDF + Apache POI, nouveaux endpoints `/rapports/*/pdf` et `/xlsx`) : mise en page plus soignée, mais plus de travail pour une v1 exploratoire, et nécessite de paramétrer en dates les agrégations actuellement figées sur « aujourd'hui »/« ce mois » dans `DashboardService`.
- **Génération côté navigateur** à partir des données déjà exposées par les endpoints existants (`GET /paiements`, `GET /passages`) : aucune dépendance backend, réutilise le RBAC déjà en place sur ces endpoints, itération rapide — retenue pour cette v1.

Pour l'export Excel, deux bibliothèques JS ont été évaluées :
- **`xlsx` (SheetJS)** : standard de facto, mais la dernière version publiée sur le registre npm (`0.18.5`) contient deux vulnérabilités connues sans correctif disponible sur npm (Prototype Pollution `GHSA-4r6h-8v6p-xvw6`, ReDoS `GHSA-5pgg-2g8v-p4x9`) — SheetJS ne distribue ses versions corrigées (0.19+) que via son propre CDN, hors du registre npm. `npm audit` remonte cette vulnérabilité en sévérité *high* dès l'installation.
- **`exceljs`** : activement maintenu, publié proprement sur npm, `npm audit` ne remonte aucune vulnérabilité au moment de l'installation (2026-07-03).

## Décision Retenue

> **Génération 100% côté navigateur pour cette v1** : export Excel via `exceljs` (3 feuilles — Résumé / Paiements / Passages), export PDF via `window.print()` scoppé à une zone imprimable (`GlobalStyles` + classe `.print-area`, même principe que l'impression déjà utilisée pour les QR codes élèves). Les données sont récupérées en paginant automatiquement sur les endpoints `GET /paiements` et `GET /passages` déjà existants (garde-fou à 50 pages / 10 000 lignes pour éviter de bloquer le navigateur sur une période trop large).

`xlsx`/SheetJS est explicitement écarté du projet tant que ses correctifs de sécurité ne sont pas republiés sur le registre npm.

## Conséquences et Impacts

### ✅ Impacts Positifs (Gains)
- Aucune nouvelle dépendance backend, aucun nouvel endpoint — surface d'attaque et de maintenance minimale pour une v1 exploratoire.
- RBAC déjà correct par construction : les endpoints réutilisés (`/paiements`, `/passages`, `/etablissements`) appliquent déjà les restrictions existantes (ex. PARENT limité à ses enfants) ; le module Rapports est simplement bloqué au niveau route (`StaffRoute`) et menu (`STAFF_ROLES`) pour les rôles ADMIN/GESTIONNAIRE/CAISSIER, cohérent avec Établissements/Élèves/Scan.
- `npm audit` propre après l'ajout de `exceljs`.

### ⚠️ Impacts Négatifs ou Risques (Compromis acceptés)
- Bundle frontend alourdi (~+950 Ko avant gzip) — acceptable pour un pilote à faible volumétrie (5 établissements, 600 élèves), à code-splitter (`React.lazy`) si le module grossit.
- Le PDF via `window.print()` dépend du moteur d'impression du navigateur (mise en page moins maîtrisée qu'un PDF généré côté serveur) — accepté comme compromis de vitesse pour la v1.
- La récupération « toutes les pages » sur une période très large peut être tronquée (garde-fou 10 000 lignes) — un avertissement est affiché à l'utilisateur dans ce cas plutôt que de planter silencieusement.
- Les agrégats statistiques (montant encaissé, taux d'accès, etc.) sont recalculés côté client à partir des données récupérées plutôt que par une requête d'agrégation SQL dédiée — suffisant pour la volumétrie pilote, à revoir si le calcul doit un jour couvrir plusieurs années d'historique.

---
## Suivi et Validation
- [x] Code mis à jour selon l'ADR (`useRapports.js`, `rapportExportService.js`, `RapportsPage.jsx`).
- [x] Fichier `history-log.md` mis à jour après implémentation.
