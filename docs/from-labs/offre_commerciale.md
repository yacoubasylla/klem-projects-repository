# Offre Commerciale — Cantine-Connect

> **Code Projet :** CTN-SCOL
> **Statut :** 🚀 Pilote (MVP v1.0.0-beta livré, déploiement du premier établissement en cours)
> **Dernière mise à jour :** 2026-07-15
> **Public visé :** Commercial & Financiers — argumentaire de vente et rentabilité attendue pour KLEM.

## Sommaire
1. [Accroche](#1-accroche)
2. [Packaging de l'offre](#2-packaging-de-loffre)
3. [Pourquoi cette offre est irrésistible](#3-pourquoi-cette-offre-est-irrésistible)
4. [Note interne — Rentabilité KLEM (usage commercial interne, ne pas diffuser au client)](#4-note-interne--rentabilité-klem-usage-commercial-interne-ne-pas-diffuser-au-client)
5. [Appel à l'action commercial](#5-appel-à-laction-commercial)

## 1. Accroche

Votre équipe administrative passe encore 40 heures par mois à courir après les paiements de
cantine sur cahier — nous le ramenons à 5 heures, avec un taux de recouvrement qui passe de 70 % à
92 %, mesuré sur un réseau scolaire réel. Une démonstration en direct sur votre propre liste
d'élèves suffit à le vérifier.

## 2. Packaging de l'offre

| Palier | Cible | Investissement initial (one-shot) | Abonnement annuel | Inclus |
|---|---|---|---|---|
| **École Pilote** | 1 établissement, jusqu'à 500 élèves | 1 500 000 FCFA HT | 100 000 FCFA HT/an | Portail parents Mobile Money, back-office, contrôle d'accès QR Code, badges PVC, formation sur site, support 5j/7 |
| **Réseau Scolaire** *(offre de référence — profil du pilote)* | 5 à 15 établissements, jusqu'à 3 000 élèves | 2 500 000 – 3 000 000 FCFA HT | 200 000 FCFA HT/an | Idem École Pilote + pilotage consolidé multi-sites, déploiement en 4 phases/8 semaines, reporting comptable centralisé |
| **Groupe Multi-Sites** | 15+ établissements, 10 000+ élèves | 4 000 000 FCFA HT | 300 000 FCFA HT/an | Idem Réseau Scolaire + SLA renforcé (P1 < 2h), accompagnement dédié au déploiement par vague d'établissements |

Frais variables identiques sur tous les paliers, refacturés à l'usage sans marge KLEM : commission
Mobile Money (~1 % parent + 0,5 % établissement, prélevée par l'agrégateur CinetPay/PayDunya), SMS
(~25 FCFA/unité), badge PVC de remplacement (500–1 000 FCFA/unité).

*Tarification affinée en phase de cadrage selon le nombre exact d'établissements et d'élèves —
cadre indicatif fondé sur la proposition commerciale du client pilote.*

## 3. Pourquoi cette offre est irrésistible

- **Preuve chiffrée, pas une promesse** : sur le réseau pilote (5 établissements, 600 élèves) —
  temps administratif −87,5 % (40h → 5h/mois), recouvrement +22 points (70 % → 92 %), accès non
  autorisés au réfectoire −95 %, coût de gestion par élève −62,5 % (12 000 → 4 500 FCFA/an). Le
  client peut demander à voir ces chiffres en démonstration, pas seulement les lire.
- **Risque de démarrage minimisé** : le déploiement suit un cycle en 4 phases (cadrage → adaptation
  → pilote sur un seul établissement → généralisation) — le réseau valide sur un site avant
  d'engager les autres, personne ne bascule tout d'un coup sans preuve locale.
- **Aucun matériel dédié à acheter** : badge PVC + QR Code lu par le smartphone Android déjà en
  poche du personnel de cantine — contre un coût d'équipement ~200× supérieur pour une solution NFC
  équivalente.
- **Le service continue même quand le réseau tombe** : contrôle d'accès 100 % opérationnel en
  offline (cache chiffré 24h), le réfectoire n'attend jamais une connexion pour ouvrir.
- **SLA engagé, pas suggéré** : 99,5 % de disponibilité, incident critique traité sous 2 heures
  ouvrables.
- **Déclencheur d'agenda honnête** : la décision budgétaire d'un réseau scolaire se prend avant la
  rentrée — un cadrage lancé maintenant permet un go-live avant le pic d'inscriptions et
  d'encaissement de rentrée, la période où la douleur du cahier papier est la plus aiguë.

## 4. Note interne — Rentabilité KLEM (usage commercial interne, ne pas diffuser au client)

- **Coût de livraison par palier** : le socle applicatif (architecture multi-tenant par
  `etablissement_id`) est déjà construit et validé sur le pilote — le coût marginal de déploiement
  d'un établissement supplémentaire au sein d'un même réseau est proche de zéro (paramétrage seul,
  pas de nouveau développement). Le coût réel se concentre sur la formation présentielle et la
  logistique badges PVC, qui augmentent avec le nombre d'établissements — d'où l'écart de prix entre
  paliers.
- **Marge attendue** : l'infrastructure PaaS (Vercel + Railway) a un coût fixe faible et ne
  progresse pas linéairement avec le nombre de clients, donc l'abonnement annuel (revenu récurrent)
  dégage une marge élevée dès le second exercice. L'investissement initial couvre l'effort de
  livraison (formation, badges, paramétrage) sans marge significative sur le premier cycle — la
  rentabilité du palier vient de l'abonnement annuel reconduit, pas de l'investissement initial.
  Les frais variables (commission Mobile Money, SMS, badges) transitent par l'agrégateur ou le
  fournisseur badge : zéro marge, zéro risque pour KLEM dessus.
- **Seuil de rentabilité** : atteint dès le premier client sur le palier Réseau Scolaire, à
  condition de sécuriser le paiement en deux échéances (acompte à la signature + solde au go-live)
  pour ne pas financer le développement sur trésorerie KLEM avant paiement — risque déjà identifié
  en `viabilite_commerciale.md` §5 Q3.
- **Point de vigilance sur la marge** : le cycle de vente institutionnel (validation multi-niveaux
  côté client) peut retarder l'encaissement de l'acompte de plusieurs semaines — à anticiper dans le
  calendrier de trésorerie plutôt qu'à découvrir en cours de négociation. Le support niveau 1 repose
  encore sur l'équipe restreinte KLEM : au-delà de 3-4 clients simultanés en phase de déploiement,
  la marge sur l'abonnement peut être absorbée par le temps de support si un runbook transférable
  n'est pas formalisé (chantier déjà identifié en `viabilite_commerciale.md` §4).

## 5. Appel à l'action commercial

Proposer une démonstration terrain sous 2 semaines à la direction du réseau scolaire ciblé, sur cas
réel (scan QR en direct, portail parent, chiffres ROI du pilote CANTINE), suivie d'une proposition
commerciale chiffrée sous 5 jours ouvrés adaptée au nombre exact d'établissements/élèves. Objectif :
signature et lancement du cadrage (Phase 1) au moins 8 semaines avant la rentrée scolaire visée,
pour un go-live aligné sur le pic d'inscriptions.
