> ⚠️ **BROUILLON — écart de tarification non résolu, ne pas envoyer au client tel quel.**
> Ce document chiffre le tier « Standard » à ~16,82M FCFA. Les documents de référence Labs
> (`cas_metier.md` §8, `offre_commerciale.md`, sourcés sur la proposition commerciale initiale
> `CONCEPTION.md`) retiennent des fourchettes très différentes : **1 500 000 – 4 000 000 FCFA HT**
> en investissement initial et **100 000 – 300 000 FCFA HT/an** en abonnement. Décision du
> 2026-08-04 (Yacouba SYLLA) : les fourchettes CONCEPTION.md font référence — ce document doit
> être recalé sur ces montants (ou justifier explicitement l'écart, ex. périmètre plus large)
> avant toute diffusion externe.

# Offre Financière — Cantine Connect
### Solution digitale de gestion de cantine scolaire multi-établissements

**Préparé pour :** [Nom du client / établissement]
**Préparé par :** KLEM Technologies & Services
**Contact :** Yacouba SYLLA — ciyasyl@gmail.com
**Date :** [à compléter]
**Validité de l'offre :** 30 jours à compter de la date d'émission

---

## 1. Résumé exécutif

Cantine Connect est une plateforme digitale complète de gestion de restauration scolaire, pensée pour le contexte ivoirien : inscription et suivi des élèves, paiement Mobile Money, contrôle d'accès au réfectoire par QR Code, et communication automatisée avec les parents.

Cette offre couvre l'intégralité du cycle de mise en œuvre : cadrage, conception, développement, intégration des paiements et des notifications, mise en production, et maintenance annuelle.

**Logique de tarification** : chaque poste est chiffré en heures d'ingénierie à un taux horaire différencié selon le niveau d'expertise requis (cadrage/architecture, développement, QA), complété par des forfaits pour les intégrations tierces (paiement, SMS, email) et un contrat de maintenance annuel. Trois formules commerciales (Essentielle, Standard, Premium) permettent d'ajuster le périmètre et l'investissement au budget et à l'ambition du projet.

---

## 2. Hypothèses de chiffrage

**Périmètre fonctionnel retenu** (base du chiffrage — correspond à la formule *Standard*, détaillée section 7) :

- Page d'accueil, connexion, demande d'accès parent en libre-service (validation par l'établissement, génération d'identifiants, changement de mot de passe à la première connexion)
- Ajout d'enfants par le parent (matricule, identité, sexe, date de naissance, résidence) avec sélection cascade établissement → niveau → classe
- Paramétrage administrateur des établissements, niveaux et classes
- Recherche de parent par email **et** par numéro de téléphone
- Paiements **trimestriels et annuels uniquement** (pas de mensualisation)
- Délai de grâce paramétrable (7 jours par défaut, modifiable globalement ou par établissement)
- Gestion stricte des allergies : déclaration impossible sans certificat médical d'un allergologue
- Contrôle d'accès réfectoire par QR Code, avec cache local de secours (mode dégradé hors-ligne 24h)
- Paiement Mobile Money via agrégateur (Orange Money, Moov Money) et suivi des transactions
- Notifications automatiques SMS et email sur les événements clés (demande, validation, échéance, paiement, rappel)
- Traçabilité complète des actions (journal d'audit)
- Tableaux de bord et rapports d'activité exportables

**Hypothèses de production :**

| Paramètre | Valeur retenue |
|---|---|
| Taux horaire moyen pondéré | 15 000 – 25 000 FCFA/h selon expertise (détail section 4) |
| Durée estimée de développement | 10 à 12 semaines (formule Standard) |
| Équipe | 1 lead technique + 1 à 2 développeurs + 1 QA (temps partagé) |
| Niveau de service en garantie initiale | 30 jours de correctifs gratuits post-mise en production |
| Devise | Franc CFA (FCFA / XOF) |

> ⚠️ Cette offre constitue une estimation de bonne foi établie sur la base du périmètre décrit. Toute évolution substantielle du périmètre en cours de projet fera l'objet d'un avenant chiffré séparément. Une marge d'ajustement de ±15 % peut s'appliquer au cadrage final après l'atelier de démarrage.

---

## 3. Tableau de cotation

| Poste | Description | Volume estimé | Unité | Prix unitaire | Total | Récurrence |
|---|---|---:|---|---:|---:|---|
| Cadrage / Analyse | Ateliers de cadrage, spécifications fonctionnelles, architecture technique | 40 | heure | 25 000 FCFA | **1 000 000 FCFA** | Ponctuel |
| Design UI/UX | Direction artistique, maquettes, système de design, parcours utilisateurs | 80 | heure | 20 000 FCFA | **1 600 000 FCFA** | Ponctuel |
| Développement Front-end | Interfaces parent/staff/admin, formulaires, tableaux, responsive | 220 | heure | 20 000 FCFA | **4 400 000 FCFA** | Ponctuel |
| Développement Back-end | API, règles métier, sécurité, authentification, journal d'audit | 240 | heure | 22 000 FCFA | **5 280 000 FCFA** | Ponctuel |
| Base de données | Modélisation, migrations, indexation, optimisation des requêtes | 40 | heure | 20 000 FCFA | **800 000 FCFA** | Ponctuel |
| QA / Tests | Tests unitaires, tests d'intégration, recette fonctionnelle | 60 | heure | 15 000 FCFA | **900 000 FCFA** | Ponctuel |
| Déploiement initial | Mise en production, environnements, CI/CD | 24 | heure | 20 000 FCFA | **480 000 FCFA** | Ponctuel |
| **Sous-total Développement** | | **704 h** | | | **14 460 000 FCFA** | |
| Intégration CinetPay | API de paiement, webhooks, gestion des statuts de transaction, tests | Forfait (≈ 50h) | forfait | — | **1 000 000 FCFA** | Ponctuel |
| Abonnement maintien CinetPay | Supervision incidents paiement, veille API/webhooks, ajustements | 1 | an | — | **400 000 FCFA** | Annuel |
| Intégration SMS | Connexion fournisseur SMS, formatage numéros, gestion des envois | Forfait (≈ 30h) | forfait | — | **600 000 FCFA** | Ponctuel |
| Intégration Email | Templates transactionnels, déclenchement automatique par événement | Forfait (≈ 20h) | forfait | — | **360 000 FCFA** | Ponctuel |
| Abonnement SMS/Email annuel | Maintien technique des canaux, gestion des templates, supervision | 1 | an | — | **350 000 FCFA** | Annuel |
| Maintenance annuelle | Correctifs, support, évolutions mineures, supervision, sécurité | 1 | an | — | **1 800 000 FCFA** | Annuel |

### Totaux

| | Montant |
|---|---:|
| **Total investissement ponctuel** (développement + intégrations) | **16 820 000 FCFA** |
| **Total récurrent annuel** (CinetPay + SMS/Email + Maintenance) | **2 550 000 FCFA / an** |

*(Montants hors TVA le cas échéant, hors frais tiers listés section 9)*

---

## 4. Détail du coût de développement

**Ventilation des 704 heures :**

| Lot | Heures | Justification |
|---|---:|---|
| Cadrage / Analyse | 40h | Recueil du besoin, ateliers, rédaction des spécifications, choix d'architecture |
| UI/UX | 80h | Refonte visuelle complète : accueil, connexion, inscription, tous les écrans métier |
| Front-end | 220h | ~12 écrans/modules (accueil, auth, dashboard, élèves, établissements, paiements, scan, utilisateurs, parents, demandes d'accès, espace parent, configuration, rapports) |
| Back-end | 240h | Auth sécurisée, moteur de règles métier, API REST complète, intégrations, audit |
| Base de données | 40h | Modélisation relationnelle, migrations versionnées, index de performance |
| QA / Tests | 60h | Couverture des parcours critiques (inscription, paiement, contrôle d'accès) |
| Déploiement | 24h | Mise en production, configuration environnements, vérifications post-déploiement |

**Justification du taux horaire** : les taux appliqués (15 000 – 25 000 FCFA/h) reflètent une prestation d'ingénierie logicielle professionnelle avec revue de code, tests automatisés et documentation — cohérents avec le marché des ESN/agences digitales structurées en Côte d'Ivoire pour un projet de cette complexité (multi-rôles, multi-établissements, paiement, temps réel).

**Total développement : 14 460 000 FCFA** (704 heures)

---

## 5. Détail des intégrations

### 5.1 CinetPay

| Élément | Montant |
|---|---:|
| Intégration technique (API, webhooks, gestion des statuts) | Inclus dans le forfait |
| Configuration (comptes marchands test/production) | Inclus dans le forfait |
| Tests de paiement (scénarios succès, échec, relance) | Inclus dans le forfait |
| Mise en production | Inclus dans le forfait |
| **Coût ponctuel d'intégration** | **1 000 000 FCFA** |
| **Abonnement annuel de maintien** | **400 000 FCFA / an** |

> ⚠️ **Les commissions de transaction CinetPay ne sont pas incluses dans ce devis.** Elles sont prélevées directement par CinetPay sur chaque paiement Mobile Money (Orange Money, Moov Money) et varient généralement entre **2,5 % et 3,5 %** du montant transigé selon l'opérateur. Ces frais sont contractualisés directement entre le client et CinetPay lors de l'ouverture du compte marchand.

### 5.2 SMS / Email

| Élément | Montant |
|---|---:|
| Intégration SMS (fournisseur, formatage, déclenchement) | Inclus dans le forfait |
| Intégration Email (templates HTML, déclenchement) | Inclus dans le forfait |
| **Coût ponctuel d'intégration (SMS + Email)** | **960 000 FCFA** |
| **Abonnement annuel de maintien technique** | **350 000 FCFA / an** |

> ⚠️ **Les frais d'usage (consommation réelle de SMS) ne sont pas inclus.** Ils sont facturés par le fournisseur SMS au volume réellement envoyé, sur un compte prépayé au nom du client. Indicatif marché : **25 à 45 FCFA par SMS** selon l'opérateur destinataire et le fournisseur retenu. L'envoi d'email transactionnel via l'infrastructure standard n'engendre en général pas de coût significatif à ce volume d'usage.

---

## 6. Contrat de maintenance annuel

**Contenu inclus :**
- Correctifs de bugs (illimités sur anomalies bloquantes)
- Support fonctionnel par email/WhatsApp, délai de réponse sous 48h ouvrées
- 10 heures/mois d'évolutions mineures (ajustements, petits ajouts)
- Supervision applicative (disponibilité, erreurs critiques)
- Mises à jour de sécurité des dépendances techniques
- Assistance fonctionnelle à l'équipe du client (prise en main, questions d'usage)

**Coût annuel : 1 800 000 FCFA** (soit 150 000 FCFA/mois, facturation trimestrielle possible)

**Limites et exclusions du contrat de maintenance :**
- N'inclut pas le développement de nouvelles fonctionnalités majeures (chiffré séparément)
- N'inclut pas la reprise de données en cas de corruption imputable à un tiers (hébergeur, fournisseur externe)
- Les heures d'évolution non consommées dans le mois ne sont pas reportables au-delà d'un trimestre
- N'inclut pas les coûts d'hébergement, de SMS ou de commissions de transaction (voir section 9)

---

## 7. Variantes commerciales

### 🟢 Version Essentielle — *Démarrer rapidement, budget maîtrisé*

**Périmètre :**
- Inscription parent, ajout d'enfants, paramétrage établissements/niveaux/classes
- Recherche parent par email et téléphone
- Paiements trimestriels/annuels avec **confirmation manuelle par le caissier** (pas d'intégration Mobile Money automatisée)
- Délai de grâce, gestion des allergies avec certificat
- Notifications **email uniquement** (pas de SMS)
- Contrôle d'accès QR Code

**Prix indicatif : 9 500 000 – 10 500 000 FCFA** (ponctuel)
**Maintenance : 900 000 FCFA/an** (correctifs + support, sans heures d'évolution incluses)

**Différence clé** : pas d'intégration paiement automatisée ni de SMS — l'architecture reste conçue pour évoluer vers le Standard sans reprise du travail déjà livré.

---

### 🔵 Version Standard — *Le périmètre complet du cahier des charges*

**Périmètre :** l'intégralité de ce qui est décrit dans ce document (sections 3 à 6), incluant l'intégration CinetPay réelle, SMS + email, et le contrat de maintenance complet.

**Prix : 16 820 000 FCFA** (ponctuel) **+ 2 550 000 FCFA/an** (récurrent)

**Différence clé** : paiement Mobile Money réellement opérationnel, communication automatisée complète, maintenance avec heures d'évolution incluses — la formule recommandée pour un déploiement en conditions réelles auprès des familles.

---

### 🟣 Version Premium — *Multi-établissements à l'échelle, exigence de service*

**Périmètre :** tout le Standard, plus :
- Intégration directe Orange Money / Moov Money (en complément de CinetPay, réduction potentielle des commissions à volume élevé)
- Tableaux de bord analytiques avancés multi-établissements (comparaison inter-écoles, prévisionnel de recouvrement)
- Application mobile compagnon (PWA installable ou app légère) pour les parents
- SLA renforcé : support prioritaire, astreinte week-end, délai de réponse 24h
- 20 heures/mois d'évolutions incluses dans la maintenance
- Formation approfondie sur site + supports vidéo pour les équipes du client

**Prix indicatif : 21 000 000 – 24 000 000 FCFA** (ponctuel)
**Maintenance : 3 600 000 FCFA/an**

**Différence clé** : pensée pour un réseau d'établissements ou une ambition de croissance rapide, avec un niveau de service et d'analytique adapté à une exploitation à grande échelle.

---

### Tableau comparatif synthétique

| | Essentielle | Standard | Premium |
|---|---|---|---|
| Inscription & gestion élèves | ✅ | ✅ | ✅ |
| Paiement Mobile Money automatisé | ❌ (manuel) | ✅ CinetPay | ✅ CinetPay + direct OM/Moov |
| Notifications SMS | ❌ | ✅ | ✅ |
| Notifications Email | ✅ | ✅ | ✅ |
| Application mobile | ❌ | ❌ | ✅ |
| Analytique multi-établissements | ❌ | Basique | Avancée |
| Heures d'évolution/mois incluses | 0 | 10h | 20h |
| SLA support | Standard | Standard | Prioritaire (24h) |
| **Prix ponctuel** | 9,5 – 10,5 M FCFA | 16,82 M FCFA | 21 – 24 M FCFA |
| **Récurrent annuel** | 0,9 M FCFA | 2,55 M FCFA | 3,6 M FCFA |

---

## 8. Modalités commerciales

- **Acompte** : 40 % à la signature du contrat (démarrage des travaux)
- **2ᵉ versement** : 30 % à la livraison de la version de recette (front + back fonctionnels)
- **Solde** : 30 % à la mise en production et validation finale
- **Durée de validité de l'offre** : 30 jours à compter de la date d'émission
- **Délai de démarrage** : sous 5 jours ouvrés après signature et réception de l'acompte
- **Durée estimée de réalisation** : 10 à 12 semaines (formule Standard), à ajuster selon la formule retenue
- **Facturation de la maintenance** : annuelle, trimestrielle sur demande

---

## 9. Exclusions

Les éléments suivants **ne sont pas inclus** dans le présent devis et restent à la charge du client :

- **Commissions de transaction CinetPay** (≈ 2,5 % à 3,5 % par transaction Mobile Money), prélevées directement par CinetPay
- **Frais de consommation SMS** (facturés à l'envoi réel, ≈ 25 à 45 FCFA/SMS selon fournisseur/opérateur)
- **Hébergement cloud** (serveur applicatif + base de données) — indicatif : 15 000 à 45 000 FCFA/mois selon dimensionnement
- **Nom de domaine** — indicatif : 8 000 à 15 000 FCFA/an selon extension
- **Frais juridiques** (CGU, politique de confidentialité, mise en conformité réglementaire) si accompagnement souhaité
- **Licences ou services tiers additionnels** non mentionnés explicitement dans ce document
- **Fabrication physique des badges PVC élèves** — non incluse dans le forfait de développement, chiffrée séparément en Annexe A

---

## 10. Recommandation finale

**Formule conseillée : Version Standard.**

Elle correspond exactement au périmètre fonctionnel que vous avez défini — paiement Mobile Money réellement opérationnel, communication automatisée avec les familles, et un contrat de maintenance qui inclut de la marge d'évolution mensuelle plutôt qu'un simple support réactif. C'est le niveau qui permet un déploiement crédible auprès des parents dès la première rentrée scolaire.

**Angle de négociation possible avec le client :**
- Si le budget initial est contraint, démarrer en **Essentielle** est un choix cohérent et non-risqué : l'architecture est construite pour absorber l'évolution vers le Standard (paiement automatisé, SMS) sans reprise du travail déjà livré — un investissement initial plus faible qui capitalise sur la suite.
- Si l'ambition est un déploiement multi-établissements ou une franchise scolaire, orienter directement vers **Premium** évite une double migration technique et donne un temps d'avance sur l'analytique et le support.
- La maintenance annuelle peut être présentée comme un investissement de continuité plutôt qu'un coût : elle sécurise la disponibilité du service pendant l'année scolaire, période où une interruption serait la plus coûteuse pour la confiance des familles.

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
