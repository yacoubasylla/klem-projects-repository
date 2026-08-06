# Viabilité Commerciale — Cantine-Connect

> **Code Projet :** CTN-SCOL
> **Statut :** 🚀 Pilote (MVP v1.0.0-beta livré, déploiement du premier établissement en cours)
> **Dernière mise à jour :** 2026-07-15
> **Public visé :** Décideurs & Financiers — validation du modèle économique avant lancement.

## Sommaire
1. [Système d'acquisition](#1-système-dacquisition)
2. [Système d'exécution](#2-système-dexécution)
3. [Système de suivi](#3-système-de-suivi)
4. [Test ultime d'indépendance](#4-test-ultime-dindépendance)
5. [Viabilité de l'offre – 4 questions](#5-viabilité-de-loffre--4-questions)

## 1. Système d'acquisition

**Segment(s) cible(s)**
- Cible primaire : réseaux scolaires privés multi-établissements en Côte d'Ivoire qui gèrent la
  restauration scolaire **en régie** (pas de prestataire externe) — ce sont eux qui subissent la
  double douleur paiement papier + contrôle d'accès manuel. Le premier client de référence
  ("CANTINE" dans les documents internes) correspond exactement à ce profil : 5 établissements,
  600 élèves.
- Cible secondaire, une fois le pilote validé : groupes scolaires confessionnels ou associatifs
  gérant plusieurs sites, et grandes écoles individuelles à forte population (>500 élèves) où le
  gain administratif justifie seul l'investissement.
- Décideur économique : direction générale du réseau scolaire (validation budgétaire) ; utilisateur
  clé côté opérations : le/la responsable administratif/financier de l'établissement.

**Canaux d'acquisition concrets**
- Vente terrain directe par le fondateur/l'équipe technique KLEM auprès des directions
  d'établissements — cohérent avec le positionnement KLEM de PME technique qui vend par la
  démonstration plutôt que par un budget marketing.
- Le pilote "CANTINE" lui-même comme vitrine : dès le PV de recette du premier établissement
  disponible, il devient l'argument commercial principal (cas concret chiffré, pas une promesse).
- Réseau de proximité : associations de chefs d'établissements privés, réunions de direction
  d'écoles (bouche-à-oreille institutionnel, très efficace dans ce secteur en Côte d'Ivoire).
- Partenariats techniques indirects : agrégateurs de paiement Mobile Money (CinetPay, PayDunya)
  et fournisseurs de badges PVC, qui peuvent recommander KLEM à leurs propres clients scolaires.
- Présence web minimale (page produit sur klemtech.net) en soutien de crédibilité, pas comme canal
  d'acquisition principal — le cycle de vente est institutionnel, pas self-service.

**Processus d'acquisition**
1. Contact initial (recommandation, salon éducatif, démarchage direct) avec la direction du
   réseau scolaire.
2. Démonstration terrain sur cas réel : scan QR Code en direct, portail parent, back-office —
   idéalement en s'appuyant sur les chiffres ROI déjà mesurés au pilote (voir indicateurs
   ci-dessous).
3. Proposition commerciale chiffrée (`CONCEPTION.md` comme trame) : investissement initial
   (développement/intégration/infrastructure/badges/formation, 1,5M–4M FCFA HT) + abonnement
   annuel (100k–300k FCFA HT) + frais variables (commission Mobile Money, SMS, badges de
   remplacement) — adaptée au nombre d'établissements et d'élèves du client.
4. Validation multi-niveaux côté client (direction générale, souvent direction financière) —
   cycle plus long que pour une PME classique, à anticiper dans le calendrier commercial.
5. Signature et cadrage (phase 1 du déploiement en 4 phases sur 8 semaines).

**Indicateurs à suivre**
- Nombre de démonstrations réalisées / mois.
- Taux de conversion démonstration → signature.
- Durée du cycle de vente (contact → signature), avec suivi séparé du délai de validation
  institutionnelle multi-niveaux.
- Coût d'acquisition client (CAC) : temps commercial fondateur + déplacements terrain, à ramener
  au nombre d'élèves du réseau signé (métrique pertinente vu la tarification par volume).
- Nombre de références actives utilisables en démonstration (cas clients avec PV de recette).

## 2. Système d'exécution

**Cycle de livraison**
Déploiement en 4 phases sur 8 semaines, déjà défini et éprouvé sur le pilote :
1. Cadrage (établissements, niveaux, classes, tarifs, période de grâce à paramétrer).
2. Développement/adaptation MVP si besoin spécifique client.
3. Pilote sur un établissement du réseau : formation des équipes, ajustements à chaud.
4. Généralisation aux autres établissements du réseau : formation étendue, go-live complet.

**Checklists indispensables**
- Technique : migration Flyway du schéma appliquée sans erreur, webhook agrégateur (CinetPay/
  PayDunya) testé en environnement réel avec vérification de signature HMAC, cache offline de
  l'app de scan validé sur le réseau mobile réel de la cantine (pas seulement en labo), sauvegarde
  `pg_dump` nocturne opérationnelle dès le jour 1 de production.
- Métier : jeu de données initial (élèves, classes, tarifs) importé et validé par le gestionnaire
  d'établissement avant ouverture aux parents ; badges PVC imprimés et distribués ; période de
  grâce configurée selon la politique du réseau.
- Conformité : suppression logique uniquement activée (jamais de suppression physique de fiche
  élève, exigence ARTCI) ; consentement/traçabilité sur les données sensibles (allergies, contacts)
  formalisé auprès du réseau scolaire.
- Formation : guide parents (PDF + vidéo) diffusé, session présentielle gestionnaires réalisée,
  guide de poche plastifié remis à chaque agent de cantine.

**Automatisable**
- CI/CD déjà en place (GitHub Actions : lint, tests, build, déploiement continu Vercel + Railway)
  — chaque nouvel établissement du même client ne nécessite aucun redéploiement applicatif, juste
  du paramétrage (multi-tenant par `etablissement_id`).
- Rappels d'échéance de paiement automatisés (J-7/J-3/J-1) déjà livrés — zéro effort humain
  récurrent.
- Génération automatique de reçus PDF et export Excel des états financiers.
- Sauvegarde et rotation des backups automatisées (`pg_dump` nocturne + rétention 30 jours).
- Alerte proactive à activer : détection automatique d'anomalies de paiement (webhook en échec
  répété, agrégateur indisponible) notifiée à l'équipe support avant que le client ne la signale.

**Indicateurs de qualité**
- Temps de mise en service par établissement additionnel (cible : proche de zéro, paramétrage
  seul, pas de nouveau développement).
- Nombre d'incidents P1/P2 par mois vs. SLA engagé (P1 < 2h, P2 < 8h ouvrables).
- Taux de disponibilité effectif vs. 99,5 % engagé.
- Taux d'échec/anomalie sur les webhooks de paiement (réconciliation `operator_tx_id`).
- Satisfaction du gestionnaire d'établissement à l'issue du go-live (enquête courte post-phase 4).

## 3. Système de suivi

**Onboarding et support**
Onboarding déjà formalisé et livré : guide utilisateur parents (PDF + vidéo tutoriel), guide
administrateur/gestionnaire (PDF + formation présentielle), guide opérateur cantine (guide de
poche plastifié + formation sur site). Support déclaré 5j/7 avec hotline, SLA différenciés par
criticité (P1 < 2h, P2 < 8h ouvrables).

**Collecte de feedback**
- Le PV de recette du premier établissement pilote (déjà prévu par la gouvernance R&D interne,
  règle n°2 de `GLOBAL_README.md`) constitue un point de feedback structuré formel.
- Métriques d'usage natives déjà disponibles côté produit : taux de recouvrement, retards de
  paiement, volume de passages réfectoire, cas de mode manuel superviseur (signal de friction sur
  le scan) — à exploiter comme feedback silencieux plutôt que de dépendre uniquement de sondages.
- Entretien court avec le gestionnaire d'établissement à la fin de chaque phase de déploiement
  (cadrage, pilote, généralisation) pour ajuster avant le déploiement du client suivant.

**Transformer les clients satisfaits en ambassadeurs**
- Le cas ROI du pilote (−87,5 % temps administratif, +22 points de recouvrement, −95 % accès non
  autorisés, −62,5 % coût de gestion par élève) est l'atout commercial le plus fort : à transformer
  en étude de cas chiffrée dès disponibilité du PV de recette, réutilisable auprès de tout nouveau
  réseau scolaire.
- Demander une lettre de recommandation/témoignage de la direction du réseau pilote une fois la
  généralisation multi-établissements réussie.
- Mettre en relation les futurs prospects avec le client pilote (visite terrain, référence
  vérifiable) — particulièrement efficace dans le secteur scolaire où les directions se connaissent
  entre elles via les associations professionnelles.

**Indicateurs de fidélisation**
- Taux de renouvellement de l'abonnement annuel (maintenance/hébergement/évolutions).
- Taux d'établissements additionnels activés dans un réseau déjà client (signal d'expansion
  organique, sans nouveau cycle de vente complet).
- Taux de recouvrement des paiements cantine (indicateur d'usage réel, pas seulement d'adoption).
- Nombre de recommandations entrantes issues d'un client existant.
- Taux d'incidents remontés en baisse dans le temps (maturité opérationnelle du client).

## 4. Test ultime d'indépendance

Cantine-Connect est conçu, dès le MVP, pour fonctionner sans intervention humaine KLEM au
quotidien côté usage : paiement, contrôle d'accès et notifications sont entièrement automatisés
(webhooks, rappels d'échéance, scan offline-first). Ce qui reste dépendant du fondateur aujourd'hui,
c'est la **vente** et le **support niveau 2**, pas l'exploitation courante.

Pour que la réponse soit OUI à "existe-t-il encore quand tu n'es plus là ?" et "si tu retires ton
nom, continue-t-il à tourner ?" :
- **Support niveau 1 documenté et transférable** : les guides déjà livrés (parents, gestionnaires,
  opérateurs cantine) doivent être complétés par une procédure de support interne KLEM (qui répond
  à quoi, sous quel délai) ne reposant pas sur une seule personne — un runbook d'incidents
  (webhook agrégateur en échec, resynchronisation offline en échec) permettrait à tout technicien
  KLEM de prendre le relais.
- **Vente reproductible** : documenter la trame de démonstration et la grille tarifaire
  (`CONCEPTION.md`) comme un playbook commercial standard, pour qu'un second commercial ou
  partenaire puisse vendre sans réinventer l'argumentaire à chaque fois.
- **Infrastructure sans opération manuelle** : déploiement continu déjà automatisé (push → Vercel +
  Railway), sauvegardes automatiques, migrations Flyway versionnées — aucune étape manuelle
  récurrente ne dépend d'un individu précis.
- **Gouvernance de montée en charge déjà écrite** (feuille de route technique section 7 du
  `specifications_techniques.md` : OTP SMS systématique, Row-Level Security, révocation de token) : suivre
  ces jalons évite que la qualité ne se dégrade silencieusement en l'absence de supervision directe
  du fondateur.
- Risque restant à traiter : le produit repose sur deux fournisseurs cloud (Vercel + Railway) et
  deux agrégateurs de paiement — la continuité de service ne dépend donc pas d'une personne, mais
  la **relation contractuelle** avec ces partenaires (négociation, facturation) doit être formalisée
  au nom de KLEM Technologies & Services (SARL), pas au nom d'un individu.

**Verdict : OUI** aux deux questions ("existe-t-il encore quand tu n'es plus là ?" / "si tu retires
ton nom, continue-t-il à tourner ?") — mais pas encore acquis en l'état. Le produit lui-même est
déjà indépendant (exploitation 100 % automatisée) ; ce qui doit être fait avant de pouvoir répondre
OUI sans réserve, ce sont les deux chantiers ci-dessus : formaliser le support niveau 1 dans un
runbook, et documenter la vente comme un playbook transférable. Sans ces deux éléments, le OUI est
vrai côté produit mais pas encore côté entreprise.

## 5. Viabilité de l'offre – 4 questions

**1) Qui a ce problème, qui est prêt à payer, et comment ?**
Qui a le problème : le/la responsable administratif/financier de l'établissement, qui perd
40h/mois en saisie et encaissement papier (mesuré au pilote), et subit les litiges de recouvrement
et les accès non autorisés au réfectoire. Qui signe et paie : la direction générale du réseau
scolaire — c'est elle qui valide le budget, pas le personnel administratif qui subit le problème
au quotidien. Le décideur économique et l'utilisateur clé sont donc deux personnes distinctes, ce
qui conditionne le processus de vente (démonstration à l'un, validation budgétaire par l'autre).

Clients potentiels concrets à démarcher en priorité : réseaux scolaires privés multi-établissements
gérant la restauration en régie (5 à 15 établissements) — le profil exact du pilote "CANTINE" (5
établissements, 600 élèves). Cible d'extension une fois la référence validée : groupes
confessionnels/associatifs multi-sites, grandes écoles individuelles à forte population (>500
élèves) où le gain justifie seul l'investissement.

Comment ils paient (mode déjà défini en `cas_metier.md` §8, pas un modèle à inventer) :
- Un **investissement initial one-shot** à la signature (1,5M à 4M FCFA HT selon le nombre
  d'établissements/élèves), couvrant développement, intégration agrégateur, infrastructure 12 mois,
  badges PVC et formation — payé cash ou en 2 échéances (acompte à la signature + solde au go-live)
  pour sécuriser la trésorerie KLEM avant déploiement complet.
- Un **abonnement annuel** de maintenance/hébergement/évolutions (100k à 300k FCFA HT/an) —
  revenu récurrent, facturé à la reconduction.
- Des **frais variables refacturés à l'usage** : commission sur transaction Mobile Money (≈ 2,5 –
  3,5 % par transaction, prélevée directement par l'agrégateur), SMS (~25 FCFA/unité), badges de
  remplacement (500-1 000 FCFA/unité) — ne pèsent pas sur la marge KLEM, ils transitent via
  l'agrégateur.
Le budget existe déjà chez le client (12 000 FCFA/élève/an de coût de gestion actuel, mesuré au
pilote) : l'enjeu commercial n'est pas de créer un besoin, mais de faire signer la direction sur un
transfert de ce budget de temps perdu/pertes vers un investissement logiciel chiffré.

**2) Pourquoi la solution KLEM est meilleure que ce qui existe (point par point) ?**
- vs. gestion manuelle (cahier/Excel) : traçabilité totale (`action_log` immuable), zéro double
  saisie, contrôle d'accès temps réel — le pilote mesure −87,5 % de temps administratif.
- vs. badge NFC importé : coût d'équipement ~200x inférieur (QR Code sur badge PVC + smartphone
  Android déjà possédé par le personnel, aucun terminal dédié à acheter).
- vs. logiciels de gestion scolaire généralistes (vie scolaire, notes) : ceux-ci n'ont ni paiement
  Mobile Money natif ni contrôle d'accès réfectoire — Cantine-Connect adresse un besoin qu'ils ne
  couvrent pas, sans les remplacer.
- Spécificité terrain ouest-africaine assumée : Mobile Money natif (Orange/MTN/Moov/Wave via
  CinetPay/PayDunya) comme flux principal et non secondaire, mode offline-first pour absorber les
  coupures réseau côté cantine — deux choix qu'une solution importée générique ne fait pas.

**3) Comment assurer une livraison rentable dès le premier client ?**
Oui, dès le premier client : le modèle est déjà structuré pour ça. Investissement initial one-shot (1,5M–4M FCFA HT selon
taille) couvre le développement/intégration/infrastructure/formation, donc le coût de mise en
service n'est pas absorbé par KLEM ; l'abonnement annuel (100k–300k FCFA HT) couvre la maintenance
récurrente ; les frais variables (commission transaction ≈ 2,5 – 3,5 %, SMS, badges de remplacement)
sont refacturés à l'usage et ne pèsent pas sur la marge KLEM. L'infrastructure PaaS (Vercel +
Railway) minimise le coût d'exploitation et l'effort d'administration serveur, ce qui est cohérent
avec une équipe technique restreinte. Point de vigilance : le cycle de vente institutionnel étant
plus long, il faut sécuriser le premier acompte à la signature (phase cadrage) pour ne pas
financer le développement sur trésorerie KLEM avant paiement.

**4) Quel est le chemin concret pour acquérir les 10 premiers clients ?**
Oui, le chemin est clair et déjà engagé — pas une hypothèse à valider, un plan en cours d'exécution.
- Segment prioritaire : réseaux scolaires privés multi-établissements en Côte d'Ivoire (5-15
  établissements), gérant la restauration en régie — le profil exact du pilote "CANTINE".
- Étape 0 (en cours) : finaliser la généralisation chez le client pilote et obtenir le PV de
  recette formel (2 validations écrites requises par la gouvernance R&D pour passer en
  Production) — c'est la condition avant toute démarche commerciale élargie.
- Étape 1 : transformer le pilote en étude de cas chiffrée (ROI mesuré) et l'utiliser dans 5 à 10
  démonstrations ciblées auprès de réseaux scolaires similaires à Abidjan, via démarchage direct et
  réseau des associations de chefs d'établissements.
- Étape 2 : s'appuyer sur les agrégateurs Mobile Money (CinetPay, PayDunya) et fournisseurs de
  badges PVC comme apporteurs d'affaires indirects.
- Calendrier réaliste compte tenu du cycle de vente institutionnel (validation multi-niveaux) :
  viser 2-3 signatures dans les 6 mois suivant la validation Production, puis accélération une fois
  2-3 références vérifiables disponibles.
- Ressources : l'équipe technique actuelle suffit pour le développement/paramétrage (architecture
  multi-tenant déjà prête à scaler) ; la vente reste portée par le fondateur en direct tant qu'un
  playbook commercial documenté n'a pas été délégué à un second commercial.
