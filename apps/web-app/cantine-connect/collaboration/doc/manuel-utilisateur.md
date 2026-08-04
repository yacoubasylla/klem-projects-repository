# Manuel Utilisateur — Cantine Connect

> Guide fonctionnel de l'application, organisé par rôle. Reflète l'état de l'application au 2026-08-04.
> Pour les décisions techniques sous-jacentes, voir `collaboration/history/decision-log.md` (ADR-017, ADR-018) et `collaboration/history/adr/`.

---

## 1. Page d'accueil (publique)

Avant toute connexion, `/` affiche une page d'accueil publique présentant l'application : mise en avant des atouts (paiement Mobile Money, contrôle d'accès QR Code, suivi des allergies, notifications parents), avec deux actions principales :
- **Se connecter** → `/login`
- **Demande d'accès** → `/demande-acces`

Ces deux liens sont également accessibles depuis la barre de navigation en haut de la page, sur toutes les pages publiques.

---

## 2. Connexion

L'accès se fait via l'écran de connexion (`/login`) avec une adresse email (ou l'identifiant généré pour les parents sans email — voir §3) et un mot de passe. Il n'y a plus d'indication des identifiants par défaut sur cet écran (retiré pour des raisons de sécurité) — les identifiants doivent être communiqués séparément par un ADMIN, ou reçus automatiquement à la validation d'une demande d'accès (§3).

Un jeton de session (JWT) est conservé côté navigateur ; la déconnexion (bouton en haut à droite) l'invalide côté client.

Un **sélecteur de thème** (icône palette 🎨, visible une fois connecté, dans la barre du haut) permet de choisir entre trois habillages visuels : **Premium** (par défaut, orange/vert chaleureux), **Corporatif** (dark, ardoise) et **Moderne** (blanc, dégradés bleu/orange). Le choix est mémorisé sur le navigateur.

### Comptes de référence (un par rôle)

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| ADMIN | `admin@cantine.connect` | `admin@123` |
| GESTIONNAIRE | `gestionnaire@cantine.connect` | `gestionnaire@123` |
| CAISSIER | `caissier@cantine.connect` | `caissier@123` |
| PARENT | `parent@cantine.connect` | `parent@123` |

Chaque compte utilisateur doit avoir un **numéro de cellulaire unique** (obligatoire à la création — les parents seront notifiés par SMS).

---

## 3. Demande d'accès parent (self-service)

Un parent qui n'a pas encore de compte peut soumettre une demande depuis `/demande-acces` (formulaire public en plusieurs étapes) :

1. **Identité** : nom, prénom, fonction (facultatif)
2. **Contact** : téléphone principal (obligatoire), case « Ce numéro est aussi mon WhatsApp » (sinon un second numéro WhatsApp peut être saisi), téléphone secondaire (facultatif), email (facultatif)
3. **Résidence** : ville, commune (obligatoires), quartier (facultatif)

À la soumission, un écran de confirmation s'affiche : **aucun compte n'est créé à ce stade** — la demande est mise en file d'attente (statut *En attente*) pour validation par l'établissement.

### Validation par l'administrateur

Écran `/demandes-acces` (ADMIN uniquement) : liste des demandes, filtrable par statut (En attente / Validées / Rejetées).

- **Valider** : crée le compte `Utilisateur` (rôle PARENT) et le profil `Parent` associé, génère un identifiant de connexion et un mot de passe temporaire. Si des notifications email/SMS sont activées (§13 Configuration), le parent les reçoit automatiquement ; **dans tous les cas**, une boîte de dialogue affiche l'identifiant et le mot de passe générés à l'écran, à communiquer manuellement si besoin (utile tant que les notifications réelles ne sont pas configurées).
  - Si le parent n'a fourni **aucun email**, un identifiant de connexion est généré automatiquement à partir de son numéro de téléphone (format `p<téléphone>@parent.cantine-connect.ci`) — ce n'est pas une adresse email fonctionnelle, seulement un identifiant de connexion interne.
- **Rejeter** : motif obligatoire, conserve la demande avec son statut et le motif pour traçabilité.

### Première connexion

Un parent dont le compte vient d'être créé par validation **doit changer son mot de passe temporaire** dès sa première connexion — l'application le redirige automatiquement vers cet écran et bloque l'accès au reste du site tant que ce n'est pas fait.

---

## 4. Rôles et périmètre d'accès

| Fonctionnalité | ADMIN | GESTIONNAIRE | CAISSIER | PARENT |
|---|:---:|:---:|:---:|:---:|
| Tableau de bord | ✅ | ✅ | ✅ | ✅ |
| Établissements | ✅ | ✅ | ✅ | ❌ masqué |
| Élèves | ✅ | ✅ | ✅ | ❌ masqué |
| **Mes enfants** (ajout self-service) | — | — | — | ✅ |
| Paiements — voir/initier | ✅ (tous) | ✅ (tous) | ✅ (tous) | ✅ (**ses enfants uniquement**) |
| Paiements — modifier/supprimer | ✅ | ❌ | ❌ | ❌ |
| Scan Réfectoire | ✅ | ✅ | ✅ | ❌ masqué |
| Historique des Passages | ✅ (tous) | ✅ (tous) | ✅ (tous) | ✅ (**ses enfants uniquement**) |
| Rapports (v1 exploratoire) | ✅ | ✅ | ✅ | ❌ masqué |
| Utilisateurs | ✅ | ❌ | ❌ | ❌ |
| Parents (liaison enfants) | ✅ | ❌ | ❌ | ❌ |
| **Demandes d'accès** (validation) | ✅ | ❌ | ❌ | ❌ |
| Configuration | ✅ | ❌ | ❌ | ❌ |

Ces restrictions sont appliquées **côté serveur** (`@PreAuthorize`, filtrage des requêtes en base) — masquer un menu côté navigateur ne suffirait pas à protéger les données ; un parent qui interrogerait directement l'API resterait bloqué. *Exception : le module Rapports lui-même n'a pas d'endpoint dédié — il réutilise `GET /paiements`/`GET /passages`, déjà protégés (un PARENT y reste limité à ses enfants) ; seul l'accès à la page `/rapports` est filtré côté navigateur (menu masqué + redirection automatique).*

---

## 5. Tableau de bord

Vue d'ensemble : nombre d'élèves, répartition par statut d'accès, paiements récents, résumé des passages du jour. Identique pour tous les rôles connectés.

---

## 6. Établissements (ADMIN / GESTIONNAIRE / CAISSIER)

- Liste des établissements actifs, création (ADMIN uniquement), modification, suppression logique.
- Dialogue « Gérer la structure » : création de niveaux et de classes en masse (ex. saisir `CP, CE1, CM1` crée 3 niveaux d'un coup), édition inline d'un niveau ou d'une classe.
- **Délai de grâce** : un champ optionnel permet de surcharger, pour cet établissement uniquement, le délai de grâce global (7 jours par défaut, réglable en Configuration §13). Laisser vide pour utiliser la valeur globale.
- Non accessible au rôle PARENT en direct depuis ce menu, mais la liste des établissements/classes est désormais lisible par un PARENT authentifié (nécessaire à la cascade du formulaire « Mes enfants », §8) — la création/modification reste réservée au staff.

---

## 7. Élèves (ADMIN / GESTIONNAIRE / CAISSIER)

- Tableau paginé côté serveur (supporte de gros volumes).
- **Recherche** par nom, prénom ou matricule ; filtres Établissement et Statut d'accès.
- **Export CSV** de la page courante (bouton « CSV » dans l'en-tête).
- Formulaire de création/modification à 3 onglets (Général / Cantine-Affectation / Contacts-Allergies) — pas de défilement vertical, adapté aux écrans compacts des gestionnaires.
- QR Code par élève : affichage, copie du token, impression — c'est ce même QR Code qui est destiné à être imprimé sur un badge PVC physique (voir l'offre commerciale, Annexe A, pour la fabrication des badges).
- Suppression réservée à l'ADMIN.
- Non accessible au rôle PARENT (le parent ajoute ses propres enfants via « Mes enfants », §8).

> ⚠️ **Limitation connue** : le champ *Allergies* de ce formulaire est un simple champ texte. Or la règle métier impose désormais qu'une allergie ne puisse être enregistrée que si un certificat médical d'allergologue est associé (`certificatMedicalUrl`). L'API le permet (`POST /eleves/{id}/certificat-medical`), **mais ce formulaire n'expose pas encore de bouton d'upload** — tant que ce n'est pas ajouté, saisir une allergie depuis cet écran sera refusé par le serveur (erreur de validation). Écran à compléter en priorité.

---

## 8. Mes enfants (PARENT — self-service)

Un parent connecté accède à `/mes-enfants` (menu « Mes enfants ») pour :
- **Consulter** la liste de ses enfants déjà rattachés (nom, matricule, statut d'accès).
- **Ajouter un enfant** lui-même, via un formulaire : établissement (liste déroulante), classe (se charge automatiquement une fois l'établissement choisi — cascade établissement → niveau → classe selon la configuration faite par l'ADMIN), matricule (communiqué par l'établissement à l'inscription), nom, prénom, sexe (facultatif), date de naissance (facultative), ville et commune (obligatoires), quartier (facultatif).
- Les coordonnées de contact (nom du parent, téléphone, email) affichées côté établissement pour cet enfant sont **automatiquement reprises du compte connecté** — le parent ne les ressaisit pas.
- Un enfant ajouté ainsi démarre avec le statut **En attente de paiement** — aucun accès cantine tant qu'un paiement n'a pas été enregistré.

---

## 9. Paiements Mobile Money

- **ADMIN / GESTIONNAIRE / CAISSIER** : voient toutes les transactions, peuvent initier un paiement pour n'importe quel élève (recherche par nom/prénom/matricule). L'ADMIN seul peut modifier ou supprimer une transaction.
- **PARENT** : ne voit que les paiements de ses propres enfants ; le sélecteur d'élève du dialogue « Initier un paiement » ne propose que ses enfants (pas de recherche libre parmi tous les élèves).
- **Périodes** : les paiements en mode Abonnement sont **trimestriels ou annuels uniquement** — pas de mensualisation.
- Opérateurs supportés : Orange Money, MTN Money, Moov Money, Wave (via les agrégateurs CinetPay/PayDunya — voir §14).
- **Recherche** par nom/prénom/matricule de l'élève ; filtres par statut (En attente / Accepté / Refusé / Annulé), par plage de dates (Date début/fin) et par opérateur Mobile Money — tous cumulables.
- **Export CSV** de la page courante.
- Une tentative d'initier un paiement pour un élève qui n'est pas son enfant est refusée par le serveur (403).

> ℹ️ L'intégration CinetPay/PayDunya appelle réellement les API des agrégateurs (voir §14) mais **aucune clé de production n'est encore configurée** — tant que ce n'est pas fait, une tentative de paiement renverra une erreur claire (« service de paiement indisponible ou mal configuré ») plutôt qu'un lien de paiement fonctionnel.

---

## 10. Scan Réfectoire (ADMIN / GESTIONNAIRE / CAISSIER)

- Validation du QR Code d'un élève en moins d'une seconde : ✅ accordé / ❌ refusé (motif affiché).
- Fonctionne hors-ligne via un cache local de 24h (liste des élèves actifs + statut d'accès), resynchronisé à la reconnexion.
- Barre de statut : indicateur « En ligne »/« Hors ligne », état du cache (« Cache absent » ou « Cache : X élèves · âge »), bouton de téléchargement manuel du cache.
- **Rafraîchissement automatique du cache** : si activé (réglage par défaut, voir §13 Configuration), le cache est téléchargé silencieusement à l'ouverture de la page si une connexion est disponible — pas besoin de cliquer manuellement. Désactivable pour repasser en téléchargement manuel uniquement.
- Non accessible au rôle PARENT.

---

## 11. Historique des Passages

- **ADMIN / GESTIONNAIRE / CAISSIER** : historique complet, filtrable par date, établissement, résultat, recherche élève ; export CSV ; modification/suppression d'un passage réservées à l'ADMIN.
- **PARENT** : ne voit que les passages de ses propres enfants ; le filtre « Établissement » est masqué (non pertinent pour un parent).

---

## 12. Utilisateurs (ADMIN uniquement)

- Création d'un compte : nom, prénom, email, **numéro de cellulaire (obligatoire, unique)**, mot de passe, rôle (ADMIN, GESTIONNAIRE, CAISSIER ou PARENT).
- Modification, changement de rôle inline, désactivation/réactivation, suppression définitive.
- Impossible de désactiver ou supprimer le dernier compte ADMIN du système.
- Impossible de désactiver/supprimer son propre compte.
- **Recherche** par email, nom, prénom ou téléphone ; filtres par rôle, statut (Actif/Inactif) et plage de dates de création — tous cumulables.

---

## 13. Parents (ADMIN uniquement)

- Associe un compte utilisateur de rôle PARENT à un ou plusieurs élèves.
- **Sélection du compte parent** (dans le formulaire) : recherche par numéro de cellulaire, nom ou prénom (le compte PARENT doit déjà exister — le créer d'abord dans « Utilisateurs », ou passer par une demande d'accès validée, §3).
- **Sélection des enfants** : recherche par matricule, nom ou prénom, sélection multiple.
- Modification des enfants associés, suppression du lien (les élèves eux-mêmes ne sont pas supprimés).
- **Recherche** sur la liste principale par **email OU numéro de téléphone** du compte parent (étendue au téléphone — auparavant email uniquement).

---

## 14. Rapports (ADMIN / GESTIONNAIRE / CAISSIER) — v1 exploratoire

> Première version, destinée à recueillir des retours avant amélioration avec le client. Non accessible au rôle PARENT.

- Choisir une période (date début/fin) et, optionnellement, un établissement (filtre les passages uniquement), puis cliquer sur **« Générer le rapport »**.
- Trois onglets une fois le rapport généré :
  - **Résumé** : montant total encaissé, répartition des paiements par statut, répartition des passages par résultat, taux d'accès.
  - **Paiements** : liste détaillée des transactions de la période.
  - **Passages** : liste détaillée des passages réfectoire de la période.
- **Exporter Excel** : télécharge un classeur `.xlsx` à 3 feuilles (Résumé / Paiements / Passages) couvrant toute la période sélectionnée.
- **Imprimer / PDF** (par onglet) : ouvre la boîte de dialogue d'impression du navigateur, limitée au contenu de l'onglet actif — utiliser « Enregistrer au format PDF » pour obtenir un fichier PDF.
- Sur une période très volumineuse, un avertissement s'affiche si le rapport a dû être tronqué (au-delà de 10 000 lignes) : réduire la plage de dates dans ce cas.

---

## 15. Configuration (ADMIN uniquement)

- Activation/désactivation des notifications email et SMS.
- Activation/désactivation du rafraîchissement automatique du cache hors-ligne (Scan Réfectoire) — activé par défaut.
- Mode de paiement : `ABONNEMENT` (accès trimestriel/annuel) ou `CREDITS` (débit par repas, avec tarif configurable) — les deux modes coexistent, un établissement peut utiliser l'un ou l'autre selon l'élève.
- Délai de grâce par défaut (7 jours), surchargeable par établissement (§6).
- Provider de paiement actif (CinetPay ou PayDunya).
- Image de fond personnalisée pour l'écran de connexion.

---

## 16. Paiement & notifications — état de l'intégration

- **Paiement** : les appels vers CinetPay et PayDunya sont réels (pas une simulation) — dès que le client fournit ses identifiants marchands réels (clé API, site ID, etc.), le paiement fonctionne sans nouveau développement. Sans ces identifiants, une tentative de paiement échoue avec un message clair plutôt qu'un lien invalide.
- **SMS** : intégration réelle avec le fournisseur Twilio. Sans compte Twilio configuré, les notifications SMS sont simplement journalisées (aucun envoi réel, aucune erreur visible pour l'utilisateur).
- **Email** : fonctionne dès que les identifiants SMTP réels sont renseignés (ex. compte Gmail avec mot de passe d'application).

---

## 17. Support

**KLEM Technologies & Services** — 📞 +225 07 58 89 24 77 · 📧 infos@klemtech.net
