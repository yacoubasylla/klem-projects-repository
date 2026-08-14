# Cas Métier — Cantine-Connect

> **Code Projet :** CTN-SCOL
> **Statut R&D :** 🚀 Pilote (MVP livré v1.0.0-beta, déploiement premier établissement en cours)
> **Dernière mise à jour :** 2026-07-09
> **Dépôt applicatif :** `apps/web-app/cantine-connect` (monorepo `klem-projects-repository`)
> **Public visé :** Décideurs — validation stratégique initiale.

## Sommaire
1. [Pitch en une phrase](#1-pitch-en-une-phrase)
2. [Problème](#2-problème)
3. [Marché cible](#3-marché-cible)
4. [Proposition de valeur](#4-proposition-de-valeur)
5. [Concurrence / Alternatives existantes](#5-concurrence--alternatives-existantes)
6. [Facteurs différenciants KLEM](#6-facteurs-différenciants-klem)
7. [Risques business](#7-risques-business)
8. [Mode de facturation](#8-mode-de-facturation)
9. [Coûts d'exploitation prévisionnels](#9-coûts-dexploitation-prévisionnels)
10. [Partenariats de distribution potentiels](#10-partenariats-de-distribution-potentiels)
11. [Indicateurs de succès (R&D → Pilote → Production)](#11-indicateurs-de-succès-rd--pilote--production)
12. [Synthèse de viabilité](#12-synthèse-de-viabilité)

## 1. Pitch en une phrase
> Digitaliser le paiement Mobile Money et le contrôle d'accès au réfectoire des cantines
> scolaires multi-établissements, avec validation QR Code en moins d'une seconde.

## 2. Problème
Les réseaux scolaires gérant la restauration en interne pilotent aujourd'hui le paiement de la
cantine et le contrôle d'accès au réfectoire de façon manuelle : encaissement papier, absence de
traçabilité centralisée par élève, aucun contrôle d'accès temps réel à l'entrée du réfectoire, et
gestion multi-sites non unifiée. Conséquences concrètes : temps administratif élevé, erreurs de
saisie, litiges avec les parents, impossibilité d'audit comptable fiable, risque sanitaire et de
fraude par accès non autorisés, pilotage impossible sur plusieurs établissements à la fois.

## 3. Marché cible
- **Taille estimée** : hypothèse de travail du cas pilote — 5 établissements, 600 élèves inscrits,
  architecture scalable vers 10 000+ élèves.
- **Segment(s) ciblé(s)** : réseaux scolaires privés multi-établissements gérant la restauration en
  régie (le premier client de référence est désigné dans les documents internes sous le nom
  "CANTINE").
- **Zone géographique** : Côte d'Ivoire (Abidjan en priorité), extensible aux marchés francophones
  d'Afrique de l'Ouest à contraintes similaires (Mobile Money dominant, connectivité mobile
  dégradée).

## 4. Proposition de valeur
Cantine-Connect articule trois composantes interdépendantes : un **portail parents** (inscription,
paiement Mobile Money, historique, notifications), un **back-office de gestion** (élèves, classes,
suivi financier, alertes de retard, reporting), et un **module de contrôle d'accès** au réfectoire
par scan de QR Code depuis un smartphone Android existant du personnel — sans terminal dédié ni
badge NFC coûteux. La validation d'accès s'appuie sur le statut de paiement de l'élève, avec un
mode dégradé offline (cache 24h) pour absorber les coupures réseau côté cantine.

## 5. Concurrence / Alternatives existantes
| Solution | Forces | Faiblesses |
|---|---|---|
| Gestion manuelle (cahier, Excel, encaissement espèces) | Aucun coût technique | Aucune traçabilité, erreurs de saisie, aucun contrôle d'accès temps réel |
| Solutions de badge NFC importées | Fiables, matures | Coût d'équipement ~200× supérieur au QR code, terminal dédié requis |
| Logiciels de gestion scolaire généralistes (vie scolaire, notes) | Déjà en place dans certains établissements | Pas de module paiement Mobile Money natif ni de contrôle d'accès réfectoire |

## 6. Facteurs différenciants KLEM
- Identification par **QR Code sur badge PVC** plutôt que NFC : coût ~200× inférieur, lecture
  fiable avec tout smartphone Android existant du personnel (aucun terminal dédié à acheter).
- Mobile Money natif via agrégateurs (**CinetPay / PayDunya**) couvrant Orange Money, MTN MoMo,
  Moov Money, Wave — flux de paiement principal du marché ivoirien, carte bancaire en secours.
- Application de scan **offline-first** (cache chiffré 24h) : le contrôle d'accès reste opérationnel
  même en cas de coupure réseau côté cantine.
- Traçabilité exhaustive (table `action_log` alimentée par AOP Spring) répondant à l'exigence
  d'audit comptable et de conformité ARTCI sur les données de mineurs.

## 7. Risques business
- Dépendance à la fiabilité des agrégateurs de paiement (CinetPay/PayDunya) et à leurs délais de
  notification webhook.
- Résistance au changement du personnel de restauration vis-à-vis du scan QR (vs. contrôle visuel
  ou badge papier existant).
- Cycle de vente institutionnel (réseaux scolaires) potentiellement long, avec validation par
  plusieurs niveaux de direction.
- Sensibilité des données de mineurs (allergies, notes médicales, contacts parents) exigeant une
  vigilance réglementaire soutenue (ARTCI).

## 8. Mode de facturation
D'après la proposition commerciale initiale (`CONCEPTION.md`) :
- [x] Investissement initial one-shot (développement plateforme, intégration agrégateur,
  infrastructure 12 mois, badges PVC, formation) : **1 500 000 – 4 000 000 FCFA HT** estimés.
- [x] Abonnement annuel (maintenance, hébergement, évolutions fonctionnelles) : **100 000 –
  300 000 FCFA HT/an** estimés.
- [x] Frais variables à l'usage : commission sur transactions Mobile Money (≈ 2,5 – 3,5 % par
  transaction, prélevée directement par l'agrégateur CinetPay/PayDunya), SMS (~25 FCFA/unité),
  badges PVC de remplacement (500–1 000 FCFA/unité).

*Tarification indicative, à affiner en phase de cadrage selon le nombre d'établissements et
d'élèves réellement engagés par le client.*

## 9. Coûts d'exploitation prévisionnels
- Infrastructure : Vercel (frontend) + Railway (backend + PostgreSQL managée) en configuration
  PaaS — coût maîtrisé, sans gestion serveur, adapté à la phase pilote (voir ADR "Stratégie de
  déploiement production").
- Support & maintenance : hotline 5j/7, corrections de bugs, mises à jour de sécurité.
- Coût d'acquisition client (CAC) : cycle de vente institutionnel, démonstration terrain sur
  l'établissement pilote avant généralisation.

## 10. Partenariats de distribution potentiels
- Réseaux scolaires privés multi-établissements en Côte d'Ivoire.
- Agrégateurs de paiement Mobile Money (CinetPay, PayDunya) en tant que partenaires techniques.
- Fournisseurs de badges PVC personnalisés (économie d'échelle sur l'impression en volume).

## 11. Indicateurs de succès (R&D → Pilote → Production)
Estimations ROI du cas d'usage de référence (5 établissements, 600 élèves) :
- Temps administratif paiements : −87,5 % (40h → 5h/mois).
- Taux de recouvrement : +22 points (70 % → 92 %).
- Accès non autorisés au réfectoire : −95 % (15–20 cas/mois → 0–2 cas/mois).
- Retards de paiement > 30 jours : −68 % (25 % des élèves → 8 %).
- Coût de gestion par élève/an : −62,5 % (12 000 → 4 500 FCFA).

Conformément à la règle de gouvernance n°2 (`GLOBAL_README.md`), le passage au statut Production
nécessite au moins 2 validations écrites de clients terrain (le PV de recette du premier
établissement pilote peut constituer la première).

## 12. Synthèse de viabilité
> Détail complet : voir `viabilite_commerciale.md`.

- **Système d'acquisition** : vente terrain directe auprès des directions de réseaux scolaires, portée par le cas pilote "CANTINE" comme vitrine chiffrée.
- **Système d'exécution** : déploiement en 4 phases/8 semaines déjà éprouvé, exploitation quotidienne (paiement, accès, rappels) entièrement automatisée dès le MVP.
- **Système de suivi** : ROI pilote chiffré (−87,5 % temps administratif) transformé en étude de cas et en références vérifiables auprès des associations de chefs d'établissements.
- **Test d'indépendance** : OUI côté produit (exploitation 100 % automatisée) — pas encore côté entreprise tant que le support niveau 1 (runbook) et la vente (playbook) reposent sur le fondateur seul.
- **Verdict 4 questions de viabilité** : GO conditionnel — subordonné à l'obtention du PV de recette du pilote (2 validations écrites requises avant Production).
