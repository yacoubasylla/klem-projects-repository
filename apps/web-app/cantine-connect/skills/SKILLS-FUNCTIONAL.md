# SKILLS-FUNCTIONAL.md - Compétences Métier & Gestion de Cantines Scolaires

Ce fichier définit le bagage fonctionnel et les règles métier que l'agent IA (Claude Code) doit maîtriser pour concevoir les modules de **Cantine Connect** selon le contexte ivoirien.

## 🏢 1. Cartographie Structurelle Scolaire
- **Modélisation Multi-Établissements** : Isolation stricte des données entre les différents établissements du réseau, tout en maintenant une centralisation au niveau du super-administrateur.
- **Hiérarchie Académique** : Association des élèves à un arbre logique unique : `Établissement ➔ Niveau (ex: Primaire, Collège) ➔ Classe (ex: CM2, 3ème A)`.

## 💳 2. Moteur de Paiement & FinTech Afrique de l'Ouest
- **Intégration d'Agrégateurs Locaux** : Maîtrise des cinétiques de paiement via API REST (CinetPay, PayDunya) pour la Côte d'Ivoire.
- **Gestion des Opérateurs Mobile Money** : Traitement et réconciliation des flux spécifiques à :
  - **Orange Money** (USSD / WebOTP)
  - **MTN MoMo** (Cinétique d'approbation push)
  - **Moov Money** (Paiement par code marchand)
  - **Wave** (Paiement par Deep Link ou Scan QR Code)
- **Cinétiques de Statuts** : Gestion rigoureuse des états de transaction : `PENDING` (En attente) ➔ `SUCCESS` (Validé) ou `FAILED` (Échec).
- **Rapprochement Bancaire** : Logique de traitement des webhooks asynchrones envoyés par l'agrégateur pour mettre à jour instantanément le solde de la cantine de l'élève.

## 🚷 3. Contrôle d'Accès Temps Réel & Résilience
- **Génération & Cycle de vie QR Code** : Sécurisation du token encodé dans le QR Code (UUID unique, éphémère ou révocable) pour empêcher la duplication des badges PVC.
- **Règles de passage au réfectoire** :
  - Un seul passage autorisé par élève par service (Midi) pour éviter la fraude au partage de badge (*Anti-passback* applicatif).
  - Gestion de la **période de grâce** : Autorisation de passage visuelle "Orange" si le paiement est en retard mais que l'élève est dans sa marge de tolérance de X jours.
- **Architecture Offline-First (Cache Local)** : Capacité à concevoir un mécanisme de synchronisation où l'application de scan télécharge un instantané chiffré des élèves autorisés pour tenir 24h sans connexion internet.

## 📜 4. Réglementation & Auditabilité
- **Protection des données des mineurs** : Alignement avec la législation locale de l'ARTCI (Côte d'Ivoire). Chiffrement des identités et restriction d'accès aux profils.
- **Journalisation Légale** : Immutabilité du registre des actions de modification de statut financier ou d'accès pour interdire toute manipulation humaine frauduleuse.
