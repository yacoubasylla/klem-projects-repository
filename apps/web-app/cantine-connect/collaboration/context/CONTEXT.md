# Contexte Métier et Vision Produit — Cantine Connect

> Document de référence stratégique. Source : Proposition commerciale CANTINE (Juin 2026), validée par M. Yacouba SYLLA.

---

## 🎯 Objectif Général

Développer une plateforme digitale intégrée, cloud-native et adaptée au contexte ivoirien pour automatiser la gestion complète de la restauration scolaire multi-établissements : inscription des élèves, paiement Mobile Money, contrôle d'accès QR Code en temps réel, et traçabilité légale exhaustive.

**Engagement contractuel** : livraison d'un MVP opérationnel sur le premier établissement pilote en **8 semaines** maximum à compter du démarrage du développement.

---

## 🏢 Contexte & Enjeux Opérationnels

| Problème identifié | Impact actuel | Risque si non traité |
|--------------------|--------------|----------------------|
| Paiements 100% manuels | Temps administratif élevé, erreurs de saisie | Pertes financières, litiges parents |
| Absence de traçabilité centralisée | Impossibilité d'audit fiable par élève | Non-conformité comptable |
| Aucun contrôle d'accès temps réel | Accès non autorisés à la cantine | Risque sanitaire, fraude |
| Gestion multi-sites non unifiée | Données fragmentées, pilotage difficile | Décisions mal informées |
| Communication parents réactive | Parents peu informés des impayés | Dégradation de la relation client |

**ROI estimé** (base : 5 établissements, 600 élèves) :
- Temps admin paiements : −87,5% (40h → 5h/mois)
- Taux de recouvrement : +22 pts (70% → 92%)
- Accès non autorisés : −95%
- Coût de gestion par élève/an : −62,5% (12 000 → 4 500 FCFA)

---

## 👥 Acteurs du Système & Matrice des Droits

| Acteur | Périmètre d'accès | Interface cible |
|--------|------------------|-----------------|
| **Super Administrateur** | Vision globale consolidée sur tous les établissements du réseau. Gestion des comptes utilisateurs, configuration des tarifs et périodes de grâce, rapports financiers, accès intégral aux logs d'audit. | Web Desktop |
| **Gestionnaire d'Établissement** | Périmètre strict de son établissement : enregistrement et modification des élèves, affectation aux classes, suivi des paiements, consultation des passages au réfectoire, envoi de notifications. | Web Desktop (écran compact) |
| **Agent de Scan (Personnel de Restauration)** | Validation des QR Codes à l'entrée du réfectoire. Interface épurée, opérabilité hors ligne (cache local 24h). Journalisation automatique de chaque passage. | Application Android (Smartphone existant) |
| **Parent / Tuteur** | Tableau de bord enfant : statut d'accès, solde, historique des passages et paiements. Initiation de paiement Mobile Money 24h/24. Téléchargement des reçus PDF. Réception des alertes avant échéance. | Web App Responsive (Mobile First) |

> **Note technique de mise en œuvre (2026-07-01)** : dans le code, les 4 acteurs ci-dessus correspondent aux rôles `ADMIN`, `GESTIONNAIRE`, `CAISSIER` et `PARENT`. Le rôle `PARENT` est restreint **côté serveur** — masqué des fonctionnalités Établissements/Élèves/Scan Réfectoire, et limité à ses propres enfants pour les Paiements et l'Historique des Passages (cf. ADR-011). Chaque compte utilisateur requiert désormais un numéro de cellulaire unique, en préparation des notifications SMS.

---

## 🏗️ Les 5 Modules Fonctionnels

### Module 1 : Portail Parents (Web Mobile First)
Interface d'accès public pour les parents et tuteurs.
- Authentification par email + mot de passe avec option OTP SMS.
- Tableau de bord par enfant : statut d'accès (AUTORISÉ / SUSPENDU / GRÂCE), solde cantine, prochaine échéance.
- Paiement en ligne : Orange Money, MTN MoMo, Wave, Moov Money via CinetPay / PayDunya. Carte Visa/Mastercard (+1% frais parent). Virement bancaire (confirmation manuelle).
- Confirmation instantanée par SMS et e-mail après paiement validé.
- Rappels automatiques à J−7, J−3 et J−1 avant échéance.
- Historique complet des paiements et téléchargement des reçus PDF.

### Module 2 : Back-Office Administration (Gestionnaire)
Interface de gestion opérationnelle quotidienne.
- Base de données centralisée des élèves par établissement et par classe.
- **Formulaire d'inscription à 3 onglets MUI** (ADR-002) : `Général` | `Cantine/Affectation` | `Contacts/Allergies` — zéro défilement vertical sur écran compact.
- Import en masse via fichiers Excel/CSV (modèle fourni).
- Gestion des profils spéciaux : boursiers, régimes alimentaires particuliers, situations exceptionnelles.
- Tableau de bord temps réel : inscrits, payants, en retard, suspendus.
- Alertes automatiques de retard configurables (J+1, J+3, J+7).
- Rapports financiers exportables (comptabilité, trésorerie, réconciliation bancaire).
- Envoi de notifications push, SMS et e-mail groupés ou ciblés.

### Module 3 : Système de Contrôle d'Accès Cantine
Validation des passages au réfectoire via QR Code sur badge PVC.
- Badge PVC personnalisé avec QR Code UUID unique par élève (résistant, remplaçable, coût ~200× inférieur au NFC).
- Application Android sur smartphone existant du personnel (pas de terminal dédié).
- Validation en **moins d'une seconde** : ✅ VERT (autorisé) / ❌ ROUGE (refusé) / 🟠 ORANGE (période de grâce).
- **Anti-passback** : un seul passage autorisé par élève par service (déjeuner).
- **Mode hors-ligne** : cache local chiffré de 24h, mis à jour à chaque connexion. Resynchronisation automatique au retour du réseau.
- Gestion des exceptions : boursiers, invités journaliers, validation superviseur manuelle.
- Journalisation complète de tous les passages (heure, statut, agent, opérateur).

### Module 4 : Moteur de Paiement (Backend)
Traitement sécurisé et asynchrone des flux financiers Mobile Money.
- Intégration API REST CinetPay / PayDunya (couverture maximale CI, KYC intégré).
- Opérateurs supportés : Orange Money (USSD/WebOTP), MTN MoMo (push), Moov Money (code marchand), Wave (deep link / QR).
- Cycle de vie : `PENDING` ➔ `SUCCESS` | `FAILED`.
- Traitement asynchrone des webhooks entrants pour mise à jour instantanée du statut d'accès de l'élève.
- Rapprochement automatique avec les relevés des agrégateurs.

### Module 5 : Table de Trace & Audit (ActionLog)
Journalisation immuable et transparente de toutes les opérations d'écriture.
- Table `action_log` alimentée via **Spring AOP** (`@Around`) sur les contrôleurs REST.
- Payload capturé : auteur (userId), horodatage, type d'action (CREATE / UPDATE / DELETE), entité cible, état avant/après.
- Conformité ARTCI et RGPD : données des mineurs chiffrées, accès restreint par rôle, droit à l'effacement.
- Données hébergées sur territoire africain (Côte d'Ivoire / région Afrique sub-saharienne).

---

## 🔄 Cycles de Vie Opérationnels Clés

### A. Inscription d'un Élève
```
[SAISIE] Formulaire 3 onglets (Général + Cantine/Affectation + Contacts/Allergies)
   │
[AFFECTATION] Élève rattaché à Établissement ➔ Niveau ➔ Classe
   │
[GÉNÉRATION QR] Token UUID unique généré et encodé dans le badge PVC
   │
[ACTIVATION] statut_acces = EN_ATTENTE_PAIEMENT
```

### B. Cycle d'un Paiement Mobile Money
```
[INITIATION] Parent déclenche le paiement via Portail
   │
[CRÉATION] Transaction créée en base : statut = PENDING
   │
[WEBHOOK] Notification asynchrone de CinetPay / PayDunya
   │
[MISE À JOUR] Solde élève crédité ➔ statut_acces = AUTORISÉ
   │
[LOG] Entrée immuable dans action_log + SMS/e-mail de confirmation parent
```

### C. Cycle de Passage au Réfectoire
```
[SCAN] Agent lit le QR Code (< 1 seconde)
   │
[VALIDATION] Cache local ou API temps réel :
   ├─ Solde OK ou dans grâce → ✅ VERT → Passage enregistré
   ├─ Hors période de grâce → ❌ ROUGE → Accès refusé
   └─ Déjà passé ce service → 🟠 ORANGE → Alerte anti-passback
   │
[TRACE] Enregistrement dans table passages_refectoire
```

---

## 📱 Contraintes d'Environnement (Contexte Ivoirien)

- **Écrans compacts** : Les gestionnaires travaillent sur des écrans 13" ou tablettes. Toute interface de saisie doit éviter le défilement vertical (formulaire à onglets obligatoire).
- **Connectivité dégradée** : Réseau mobile instable. L'application de scan doit être 100% opérationnelle offline avec cache 24h.
- **Mobile Money dominant** : Le paiement Mobile Money est le flux principal. Visa/Mastercard est secondaire.
- **Hébergement africain** : Cloud local africain (AWS Lagos ou Infomaniak) pour latence réduite et souveraineté des données.
- **Volumétrie cible** : 5 établissements, 600 élèves (MVP pilote) — scalable vers 10 000+ élèves.

---

## 🚀 Planning de Déploiement (8 Semaines)

| Phase | Durée | Livrables |
|-------|-------|-----------|
| **Phase 1** — Cadrage & Architecture | S1 | Spécifications détaillées, maquettes UI, infra validée |
| **Phase 2** — Développement MVP | S2–S6 | Portail parents, back-office, contrôle d'accès, paiements |
| **Phase 3** — Pilote | S7 | Déploiement 1 établissement, formation, ajustements |
| **Phase 4** — Généralisation | S8 | Déploiement tous établissements, go-live complet |

---

## 📋 SLA Contractuels

| Critère | Engagement |
|---------|-----------|
| Disponibilité plateforme | 99,5% hors maintenance planifiée |
| Temps de réponse API paiements | < 2 secondes (P95) |
| Temps de validation cantine | < 1 seconde par élève |
| Support P1 (critique) | < 2 heures ouvrables |
| Sauvegarde données | Backup automatique 24h, rétention 30 jours |
| Maintenance planifiée | Notifiée 72h à l'avance, hors heures scolaires |
