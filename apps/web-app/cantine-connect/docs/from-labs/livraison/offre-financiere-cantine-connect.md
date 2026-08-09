# Offre Financière — Cantine Connect
### Solution digitale de gestion de cantine scolaire multi-établissements

**Préparé pour :** [Nom du client / établissement]
**Préparé par :** KLEM Technologies & Services
**Contact :** Yacouba SYLLA — ciyasyl@gmail.com
**Date :** [à compléter]
**Validité de l'offre :** 30 jours à compter de la date d'émission

---

## Sommaire

1. Résumé exécutif
2. Hypothèses de chiffrage
3. Tableau de cotation
4. Détail de l'investissement initial
5. Détail des intégrations
6. Abonnement annuel (maintenance, hébergement, évolutions)
7. Détail des paliers commerciaux
8. Modalités commerciales
9. Exclusions
10. Recommandation finale
11. Annexe A — Badges PVC élèves avec QR Code

---

## 1. Résumé exécutif

Cantine Connect est une plateforme digitale complète de gestion de restauration scolaire, pensée pour le contexte ivoirien : inscription des élèves, suivi des repas et passages en temps réel (contrôle d'accès au réfectoire par QR Code), paiement Mobile Money, et communication automatisée avec les parents.

**La plateforme est déjà conçue, développée et validée en conditions réelles** (MVP livré v1.0.0-beta, pilote en cours de généralisation sur un premier réseau scolaire — voir `cas_metier.md`). Cette offre ne chiffre donc pas une construction depuis zéro, mais l'**adaptation, l'intégration des paiements/notifications, le déploiement et l'accompagnement** de la plateforme existante pour votre réseau scolaire — ce qui permet un investissement nettement inférieur à un développement sur mesure complet, sans compromis sur le périmètre fonctionnel.

**Logique de tarification** : l'investissement est structuré en **paliers par taille de réseau** (nombre d'établissements et d'élèves), et non en heures d'ingénierie détaillées — le socle applicatif étant déjà construit, le coût marginal de déploiement d'un établissement supplémentaire au sein d'un même réseau est proche de zéro (paramétrage seul). Le coût réel se concentre sur l'intégration paiement, la formation présentielle et la logistique des badges PVC, qui augmentent avec le nombre d'établissements — d'où l'écart de prix entre paliers (détail section 3).

---

## 2. Hypothèses de chiffrage

**Périmètre fonctionnel retenu** (déjà livré et opérationnel sur le pilote — voir
`specifications_fonctionnelles.md`) :

- Page d'accueil, connexion, demande d'accès parent en libre-service (validation par l'établissement, génération d'identifiants, changement de mot de passe à la première connexion)
- Ajout d'enfants par le parent (matricule, identité, sexe, date de naissance, résidence) avec sélection cascade établissement → niveau → classe
- Paramétrage administrateur des établissements, niveaux et classes
- Recherche de parent par email **et** par numéro de téléphone
- Paiements **trimestriels et annuels uniquement** (pas de mensualisation)
- Délai de grâce paramétrable (7 jours par défaut, modifiable globalement ou par établissement)
- Gestion stricte des allergies : déclaration impossible sans certificat médical d'un allergologue
- Contrôle d'accès réfectoire par QR Code, avec cache local de secours (mode dégradé hors-ligne 24h)
- Paiement Mobile Money via agrégateur (Orange Money, MTN, Moov Money, Wave) et suivi des transactions
- Notifications automatiques SMS et email sur les événements clés (demande, validation, échéance, paiement, rappel)
- Traçabilité complète des actions (journal d'audit)
- Tableaux de bord et rapports d'activité exportables

**Hypothèses de chiffrage :**

| Paramètre | Valeur retenue |
|---|---|
| Base tarifaire | Paliers par nombre d'établissements/élèves (détail section 3) |
| Durée de déploiement | 4 phases sur 8 semaines (cadrage → adaptation → pilote → généralisation), déjà éprouvées sur le premier réseau |
| Équipe | 1 lead technique + support paramétrage/formation (temps partagé) |
| Niveau de service en garantie initiale | 30 jours de correctifs gratuits post-mise en production |
| Devise | Franc CFA (FCFA / XOF) |

> Cette offre constitue une estimation de bonne foi établie sur la base du périmètre décrit et du nombre d'établissements/élèves indiqués par le client. Toute évolution substantielle du périmètre en cours de projet (nouveau module, intégration tierce non prévue) fera l'objet d'un avenant chiffré séparément.

---

## 3. Tableau de cotation

L'investissement est structuré en **trois paliers par taille de réseau**, et non en heures
d'ingénierie détaillées : le socle applicatif (portail parents, back-office, contrôle d'accès QR
Code, paiement Mobile Money, notifications) est déjà construit et validé sur le premier réseau
pilote — l'investissement couvre l'adaptation, l'intégration paiement, l'infrastructure de la
première année, les badges PVC et la formation, pas une reconstruction du produit.

| Palier | Cible | Investissement initial (one-shot) | Abonnement annuel | Inclus |
|---|---|---:|---:|---|
| **École Pilote** | 1 établissement, jusqu'à 500 élèves | **1 500 000 FCFA HT** | **100 000 FCFA HT/an** | Portail parents Mobile Money, back-office, contrôle d'accès QR Code, badges PVC, formation sur site, support 5j/7 |
| **Réseau Scolaire** *(offre de référence)* | 5 à 15 établissements, jusqu'à 3 000 élèves | **2 500 000 – 3 000 000 FCFA HT** | **200 000 FCFA HT/an** | Idem École Pilote + pilotage consolidé multi-sites, déploiement en 4 phases/8 semaines, reporting comptable centralisé |
| **Groupe Multi-Sites** | 15+ établissements, 10 000+ élèves | **4 000 000 FCFA HT** | **300 000 FCFA HT/an** | Idem Réseau Scolaire + SLA renforcé (P1 < 2h), accompagnement dédié au déploiement par vague d'établissements |

### Totaux — palier de référence (Réseau Scolaire)

| | Montant |
|---|---:|
| **Total investissement ponctuel** | **2 500 000 – 3 000 000 FCFA HT** |
| **Total récurrent annuel** | **200 000 FCFA HT/an** |

*(Montants hors TVA le cas échéant, hors frais tiers refacturés à l'usage — commission Mobile
Money, SMS, badges de remplacement — listés section 9. Tarification affinée en phase de cadrage
selon le nombre exact d'établissements et d'élèves engagés par le client.)*

---

## 4. Détail de l'investissement initial

**Ce que couvre l'investissement one-shot, quel que soit le palier :**

| Poste | Description | Inclus |
|---|---|---|
| Adaptation du produit | Paramétrage établissements/niveaux/classes, tarifs, période de grâce, image de marque | ✓ |
| Intégration paiement | CinetPay/PayDunya — compte marchand, webhooks, tests de bout en bout | ✓ |
| Infrastructure (12 mois) | Hébergement cloud (Vercel + Railway), nom de domaine, monitoring | ✓ |
| Badges PVC | Conception du gabarit + impression et distribution initiale (effectif du palier) | ✓ |
| Formation & déploiement | Formation admin/gestionnaire, agents de cantine, guide parents — déplacements inclus | ✓ |
| Documentation | Guides PDF, vidéos tutoriels, documentation technique (architecture, API) | ✓ |

**Pourquoi le prix augmente avec le nombre d'établissements** : le socle applicatif est
multi-tenant et déjà construit — ajouter un établissement supplémentaire au sein d'un même réseau
ne demande aucun nouveau développement, seulement du paramétrage. Le coût réel se concentre sur
deux postes qui grandissent avec le réseau : la **formation présentielle** (un passage par site) et
la **logistique des badges PVC** (impression et distribution par établissement) — c'est ce qui
justifie l'écart de prix entre les paliers École Pilote, Réseau Scolaire et Groupe Multi-Sites
(section 3), et non une reconstruction du produit à chaque palier.

---

## 5. Détail des intégrations

Les intégrations CinetPay/PayDunya et SMS/Email sont **incluses dans l'investissement initial de
chaque palier** (section 3) — elles ne font pas l'objet d'une ligne de facturation séparée.

### 5.1 CinetPay / PayDunya

- Intégration technique (API, webhooks, gestion des statuts de transaction) — inclus.
- Configuration des comptes marchands (test/production) — inclus.
- Tests de paiement (scénarios succès, échec, relance) — inclus.

> **Les commissions de transaction CinetPay/PayDunya ne sont pas incluses dans ce devis.** Elles sont prélevées directement par l'agrégateur sur chaque paiement Mobile Money (Orange Money, MTN, Moov Money, Wave) et varient généralement entre **2,5 % et 3,5 %** du montant transigé selon l'opérateur. Ces frais sont contractualisés directement entre le client et l'agrégateur lors de l'ouverture du compte marchand — zéro marge KLEM dessus.

### 5.2 SMS / Email

- Intégration du fournisseur SMS (formatage, déclenchement) — inclus.
- Intégration Email (templates transactionnels HTML, déclenchement automatique) — inclus.

> **Les frais d'usage (consommation réelle de SMS) ne sont pas inclus.** Ils sont facturés au volume réellement envoyé, sur un compte prépayé au nom du client. Indicatif marché : **~25 FCFA par SMS** (fournisseur local). L'envoi d'email transactionnel via l'infrastructure standard n'engendre en général pas de coût significatif à ce volume d'usage.

---

## 6. Abonnement annuel (maintenance, hébergement, évolutions)

**Contenu inclus, à tous les paliers :**
- Correctifs de bugs (illimités sur anomalies bloquantes)
- Hotline support 5j/7, hébergement cloud (renouvellement, bande passante, sauvegardes automatiques)
- Mises à jour de sécurité des dépendances techniques
- Supervision applicative (disponibilité, erreurs critiques, anomalies de paiement)
- SLA : disponibilité 99,5 % minimum, incident critique (P1) traité sous 2h ouvrables, P2 sous 8h ouvrables (SLA renforcé pour le palier Groupe Multi-Sites, voir section 3)

**Coût annuel : selon palier (section 3)** — 100 000 FCFA/an (École Pilote), 200 000 FCFA/an (Réseau Scolaire), 300 000 FCFA/an (Groupe Multi-Sites). Facturation annuelle, trimestrielle possible sur demande.

**Limites et exclusions de l'abonnement :**
- N'inclut pas le développement de nouvelles fonctionnalités majeures — 1 à 2 évolutions substantielles par an peuvent être budgétées séparément, sur devis.
- N'inclut pas la reprise de données en cas de corruption imputable à un tiers (hébergeur, fournisseur externe).
- N'inclut pas les frais variables à l'usage (SMS, commissions de transaction, badges de remplacement — voir section 9).

---

## 7. Détail des paliers commerciaux

### École Pilote — *Démarrer rapidement, un seul établissement*

**Cible :** 1 établissement, jusqu'à 500 élèves.

**Inclus :** portail parents Mobile Money, back-office, contrôle d'accès QR Code, badges PVC, formation sur site, support 5j/7.

**Prix : 1 500 000 FCFA HT** (ponctuel) **+ 100 000 FCFA HT/an** (récurrent)

**Différence clé** : le périmètre fonctionnel complet dès ce palier (paiement Mobile Money réellement opérationnel, pas de version dégradée) — seule la taille du réseau change entre les paliers, pas les fonctionnalités.

---

### Réseau Scolaire — *Le profil de référence*

**Cible :** 5 à 15 établissements, jusqu'à 3 000 élèves.

**Inclus :** idem École Pilote + pilotage consolidé multi-sites, déploiement en 4 phases/8 semaines, reporting comptable centralisé.

**Prix : 2 500 000 – 3 000 000 FCFA HT** (ponctuel) **+ 200 000 FCFA HT/an** (récurrent)

**Différence clé** : c'est le profil du client pilote de référence (5 établissements, 600 élèves — voir `cas_metier.md`) — la formule recommandée pour un réseau scolaire multi-sites en conditions réelles.

---

### Groupe Multi-Sites — *À l'échelle, exigence de service*

**Cible :** 15+ établissements, 10 000+ élèves.

**Inclus :** idem Réseau Scolaire + SLA renforcé (incident critique P1 traité sous 2h ouvrables), accompagnement dédié au déploiement par vague d'établissements.

**Prix : 4 000 000 FCFA HT** (ponctuel) **+ 300 000 FCFA HT/an** (récurrent)

**Différence clé** : pensée pour un grand réseau ou une ambition de croissance rapide, avec un accompagnement de déploiement dimensionné pour de nombreux sites.

---

### Tableau comparatif synthétique

| | École Pilote | Réseau Scolaire | Groupe Multi-Sites |
|---|---|---|---|
| Établissements | 1 | 5 à 15 | 15+ |
| Élèves | Jusqu'à 500 | Jusqu'à 3 000 | 10 000+ |
| Paiement Mobile Money (CinetPay/PayDunya) | ✓ | ✓ | ✓ |
| Notifications SMS + Email | ✓ | ✓ | ✓ |
| Pilotage consolidé multi-sites | ✗ | ✓ | ✓ |
| Reporting comptable centralisé | ✗ | ✓ | ✓ |
| Accompagnement déploiement par vague | ✗ | ✗ | ✓ |
| SLA support (P1) | < 2h ouvrables | < 2h ouvrables | < 2h ouvrables renforcé |
| **Prix ponctuel** | 1,5 M FCFA | 2,5 – 3 M FCFA | 4 M FCFA |
| **Récurrent annuel** | 100 000 FCFA | 200 000 FCFA | 300 000 FCFA |

*(Tous les paliers partagent le même périmètre fonctionnel — voir section 2 — seuls la taille du réseau, l'effort de formation/logistique badges et le niveau d'accompagnement diffèrent.)*

---

## 8. Modalités commerciales

- **Acompte** : 50 % à la signature du contrat (démarrage du cadrage, phase 1)
- **Solde** : 50 % à la mise en production et validation finale (go-live, phase 4)
- **Durée de validité de l'offre** : 30 jours à compter de la date d'émission
- **Délai de démarrage** : sous 5 jours ouvrés après signature et réception de l'acompte
- **Durée estimée de déploiement** : 4 phases sur 8 semaines (cadrage → adaptation → pilote sur un établissement → généralisation), quel que soit le palier
- **Facturation de l'abonnement annuel** : annuelle, trimestrielle sur demande

---

## 9. Exclusions

Les éléments suivants **ne sont pas inclus** dans le présent devis et restent à la charge du client :

- **Commissions de transaction Mobile Money** (≈ 2,5 % à 3,5 % par transaction selon opérateur), prélevées directement par l'agrégateur CinetPay/PayDunya
- **Frais de consommation SMS** (facturés à l'envoi réel, ≈ 25 FCFA/SMS indicatif, selon fournisseur retenu)
- **Frais juridiques** (CGU, politique de confidentialité, mise en conformité réglementaire) si accompagnement souhaité
- **Licences ou services tiers additionnels** non mentionnés explicitement dans ce document
- **Badges PVC de remplacement** au-delà de l'émission initiale (perte/détérioration, nouveaux inscrits en cours d'année) — chiffrés à l'unité en Annexe A

*(L'hébergement cloud et le nom de domaine sont inclus la première année dans l'investissement initial — section 4 — puis couverts par l'abonnement annuel à partir de la 2ᵉ année — section 6.)*

---

## 10. Recommandation finale

**Palier conseillé : Réseau Scolaire.**

Il correspond au profil du client pilote de référence (5 établissements, 600 élèves) et couvre le périmètre fonctionnel complet — paiement Mobile Money réellement opérationnel, communication automatisée avec les familles, pilotage consolidé multi-sites — avec un déploiement en 4 phases/8 semaines déjà éprouvé. C'est le niveau qui permet un déploiement crédible auprès des parents dès la première rentrée scolaire.

**Angle de négociation possible avec le client :**
- Si le client ne compte qu'un seul établissement pour l'instant, démarrer en **École Pilote** est un choix cohérent et non-risqué : le socle applicatif étant déjà construit, l'extension vers Réseau Scolaire ne nécessite aucune reprise du travail déjà livré, seulement du paramétrage pour les établissements additionnels.
- Si l'ambition est un déploiement à grande échelle (15+ établissements) ou une franchise scolaire, orienter directement vers **Groupe Multi-Sites** évite une renégociation en cours de déploiement et donne un accompagnement dimensionné dès le départ.
- L'abonnement annuel peut être présenté comme un investissement de continuité plutôt qu'un coût : il sécurise la disponibilité du service pendant l'année scolaire, période où une interruption serait la plus coûteuse pour la confiance des familles.

---

## Annexe A — Badges PVC élèves avec QR Code

Chaque élève dispose déjà, dans le système, d'un identifiant QR Code unique (généré automatiquement à la création de sa fiche) utilisé pour le contrôle d'accès au réfectoire via l'application de scan. Cette annexe chiffre la **fabrication physique** du badge rigide qui porte ce QR Code, remis à l'élève.

### Spécifications proposées

- **Format** : carte PVC rigide CR80 (format carte bancaire, 85,6 × 54 mm), épaisseur 0,76 mm
- **Recto** : QR Code, photo de l'élève (optionnel), nom, prénom, classe, matricule
- **Verso** : logo et coordonnées de l'établissement, mentions utiles (contact urgence, consignes)
- **Finition** : impression couleur recto-verso + pelliculage brillant (résistance à l'usure et à l'humidité)
- **Portage** : cordon tour de cou avec clip **ou** porte-badge souple, au choix de l'établissement

### Tarification — impression via prestataire (sans investissement matériel)

| Poste | Description | Volume | Prix unitaire | Récurrence |
|---|---|---:|---:|---|
| Conception du gabarit | Mise en page badge (QR + identité + charte graphique établissement) | 1 forfait | 80 000 FCFA | Ponctuel |
| Impression PVC (1 – 100 badges) | Carte PVC couleur recto-verso + pelliculage | par carte | 1 200 FCFA | Par lot |
| Impression PVC (101 – 500 badges) | Idem, tarif dégressif volume | par carte | 950 FCFA | Par lot |
| Impression PVC (501 – 1000 badges) | Idem, tarif dégressif volume | par carte | 750 FCFA | Par lot |
| Impression PVC (1000+ badges) | Idem, tarif dégressif volume | par carte | 600 – 650 FCFA | Par lot |
| Cordon + clip | Accessoire de portage | par unité | 300 FCFA | Par lot |
| Porte-badge souple (alternative au cordon) | Protection additionnelle | par unité | 200 FCFA | Par lot |

**Exemple chiffré — établissement de 500 élèves (1ʳᵉ émission) :**

| Poste | Calcul | Total |
|---|---|---:|
| Conception du gabarit | Forfait | 80 000 FCFA |
| Impression 500 badges PVC (palier 101–500) | 500 × 950 FCFA | 475 000 FCFA |
| Cordons + clips (500 unités) | 500 × 300 FCFA | 150 000 FCFA |
| **Total 1ʳᵉ émission (500 élèves)** | | **705 000 FCFA** |

**Réédition annuelle (nouveaux inscrits + remplacements badges perdus/abîmés)** : sur la base d'un renouvellement estimé à 15–20 % de l'effectif par an, soit ~90 badges/an pour 500 élèves → **≈ 112 500 FCFA/an** (au même tarif unitaire, hors conception de gabarit déjà livrée).

> Le gabarit de conception est réutilisable indéfiniment — seul le coût d'impression est à reconduire chaque rentrée scolaire.

### Option alternative — impression en interne (établissements à gros effectif ou réseau multi-écoles)

| Poste | Montant indicatif |
|---|---:|
| Imprimante à cartes PVC (ex. gamme Evolis Zenius / Badgy, Zebra ZC100) | 850 000 – 1 800 000 FCFA (investissement matériel) |
| Consommables par carte (ruban couleur + carte PVC vierge) | ≈ 250 – 350 FCFA/carte |

**Point de rentabilité** : l'acquisition d'une imprimante en interne devient économiquement pertinente au-delà d'environ **1 500 à 2 000 badges cumulés** sur la durée de vie du matériel (typiquement un établissement à très gros effectif, ou un réseau de plusieurs écoles mutualisant l'équipement). En-dessous de ce volume, le recours à un prestataire d'impression externe reste la solution la plus économique et la plus simple à opérer.

---

*Document préparé par KLEM Technologies & Services — offre confidentielle, destinée exclusivement au destinataire mentionné en en-tête.*
