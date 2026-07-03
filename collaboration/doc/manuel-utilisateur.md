# Manuel Utilisateur — Cantine Connect

> Guide fonctionnel de l'application, organisé par rôle. Reflète l'état de l'application au 2026-07-01.
> Pour les décisions techniques sous-jacentes, voir `collaboration/history/decision-log.md` et `collaboration/history/adr/`.

---

## 1. Connexion

L'accès se fait via l'écran de connexion (`/login`) avec une adresse email et un mot de passe. Il n'y a plus d'indication des identifiants par défaut sur cet écran (retiré pour des raisons de sécurité) — les identifiants doivent être communiqués séparément par un ADMIN.

Un jeton de session (JWT) est conservé côté navigateur ; la déconnexion (bouton en haut à droite) l'invalide côté client.

### Comptes de référence (un par rôle)

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| ADMIN | `admin@cantine.connect` | `admin@123` |
| GESTIONNAIRE | `gestionnaire@cantine.connect` | `gestionnaire@123` |
| CAISSIER | `caissier@cantine.connect` | `caissier@123` |
| PARENT | `parent@cantine.connect` | `parent@123` |

Chaque compte utilisateur doit avoir un **numéro de cellulaire unique** (obligatoire à la création — les parents seront notifiés par SMS).

---

## 2. Rôles et périmètre d'accès

| Fonctionnalité | ADMIN | GESTIONNAIRE | CAISSIER | PARENT |
|---|:---:|:---:|:---:|:---:|
| Tableau de bord | ✅ | ✅ | ✅ | ✅ |
| Établissements | ✅ | ✅ | ✅ | ❌ masqué |
| Élèves | ✅ | ✅ | ✅ | ❌ masqué |
| Paiements — voir/initier | ✅ (tous) | ✅ (tous) | ✅ (tous) | ✅ (**ses enfants uniquement**) |
| Paiements — modifier/supprimer | ✅ | ❌ | ❌ | ❌ |
| Scan Réfectoire | ✅ | ✅ | ✅ | ❌ masqué |
| Historique des Passages | ✅ (tous) | ✅ (tous) | ✅ (tous) | ✅ (**ses enfants uniquement**) |
| Rapports (v1 exploratoire) | ✅ | ✅ | ✅ | ❌ masqué |
| Utilisateurs | ✅ | ❌ | ❌ | ❌ |
| Parents (liaison enfants) | ✅ | ❌ | ❌ | ❌ |
| Configuration | ✅ | ❌ | ❌ | ❌ |

Ces restrictions sont appliquées **côté serveur** (`@PreAuthorize`, filtrage des requêtes en base) — masquer un menu côté navigateur ne suffirait pas à protéger les données ; un parent qui interrogerait directement l'API resterait bloqué. *Exception : le module Rapports lui-même n'a pas d'endpoint dédié — il réutilise `GET /paiements`/`GET /passages`, déjà protégés (un PARENT y reste limité à ses enfants) ; seul l'accès à la page `/rapports` est filtré côté navigateur (menu masqué + redirection automatique).*

---

## 3. Tableau de bord

Vue d'ensemble : nombre d'élèves, répartition par statut d'accès, paiements récents, résumé des passages du jour. Identique pour tous les rôles connectés.

---

## 4. Établissements (ADMIN / GESTIONNAIRE / CAISSIER)

- Liste des établissements actifs, création (ADMIN uniquement), modification, suppression logique.
- Dialogue « Gérer la structure » : création de niveaux et de classes en masse (ex. saisir `CP, CE1, CM1` crée 3 niveaux d'un coup), édition inline d'un niveau ou d'une classe.
- Non accessible au rôle PARENT (élément de menu masqué, endpoints API renvoient 403).

---

## 5. Élèves (ADMIN / GESTIONNAIRE / CAISSIER)

- Tableau paginé côté serveur (supporte de gros volumes).
- **Recherche** par nom, prénom ou matricule ; filtres Établissement et Statut d'accès.
- **Export CSV** de la page courante (bouton « CSV » dans l'en-tête).
- Formulaire de création/modification à 3 onglets (Général / Cantine-Affectation / Contacts-Allergies) — pas de défilement vertical, adapté aux écrans compacts des gestionnaires.
- QR Code par élève : affichage, copie du token, impression.
- Suppression réservée à l'ADMIN.
- Non accessible au rôle PARENT.

---

## 6. Paiements Mobile Money

- **ADMIN / GESTIONNAIRE / CAISSIER** : voient toutes les transactions, peuvent initier un paiement pour n'importe quel élève (recherche par nom/prénom/matricule). L'ADMIN seul peut modifier ou supprimer une transaction.
- **PARENT** : ne voit que les paiements de ses propres enfants ; le sélecteur d'élève du dialogue « Initier un paiement » ne propose que ses enfants (pas de recherche libre parmi tous les élèves).
- Opérateurs supportés : Orange Money, MTN Money, Moov Money, Wave.
- **Recherche** par nom/prénom/matricule de l'élève ; filtres par statut (En attente / Accepté / Refusé / Annulé), par plage de dates (Date début/fin) et par opérateur Mobile Money — tous cumulables.
- **Export CSV** de la page courante.
- Une tentative d'initier un paiement pour un élève qui n'est pas son enfant est refusée par le serveur (403).

---

## 7. Scan Réfectoire (ADMIN / GESTIONNAIRE / CAISSIER)

- Validation du QR Code d'un élève en moins d'une seconde : ✅ accordé / ❌ refusé (motif affiché).
- Fonctionne hors-ligne via un cache local de 24h (liste des élèves actifs + statut d'accès), resynchronisé à la reconnexion.
- Barre de statut : indicateur « En ligne »/« Hors ligne », état du cache (« Cache absent » ou « Cache : X élèves · âge »), bouton de téléchargement manuel du cache.
- **Rafraîchissement automatique du cache** : si activé (réglage par défaut, voir § Configuration), le cache est téléchargé silencieusement à l'ouverture de la page si une connexion est disponible — pas besoin de cliquer manuellement. Désactivable pour repasser en téléchargement manuel uniquement.
- Non accessible au rôle PARENT.

---

## 8. Historique des Passages

- **ADMIN / GESTIONNAIRE / CAISSIER** : historique complet, filtrable par date, établissement, résultat, recherche élève ; export CSV ; modification/suppression d'un passage réservées à l'ADMIN.
- **PARENT** : ne voit que les passages de ses propres enfants ; le filtre « Établissement » est masqué (non pertinent pour un parent).

---

## 9. Utilisateurs (ADMIN uniquement)

- Création d'un compte : nom, prénom, email, **numéro de cellulaire (obligatoire, unique)**, mot de passe, rôle (ADMIN, GESTIONNAIRE, CAISSIER ou PARENT).
- Modification, changement de rôle inline, désactivation/réactivation, suppression définitive.
- Impossible de désactiver ou supprimer le dernier compte ADMIN du système.
- Impossible de désactiver/supprimer son propre compte.
- **Recherche** par email, nom, prénom ou téléphone ; filtres par rôle, statut (Actif/Inactif) et plage de dates de création — tous cumulables.

---

## 10. Parents (ADMIN uniquement)

- Associe un compte utilisateur de rôle PARENT à un ou plusieurs élèves.
- **Sélection du compte parent** (dans le formulaire) : recherche par numéro de cellulaire, nom ou prénom (le compte PARENT doit déjà exister — le créer d'abord dans « Utilisateurs »).
- **Sélection des enfants** : recherche par matricule, nom ou prénom, sélection multiple.
- Modification des enfants associés, suppression du lien (les élèves eux-mêmes ne sont pas supprimés).
- **Recherche** sur la liste principale par email du compte parent.

---

## 11. Rapports (ADMIN / GESTIONNAIRE / CAISSIER) — v1 exploratoire

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

## 12. Configuration (ADMIN uniquement)

- Activation/désactivation des notifications email et SMS.
- Activation/désactivation du rafraîchissement automatique du cache hors-ligne (Scan Réfectoire) — activé par défaut.
- Mode de paiement : `ABONNEMENT` (accès annuel) ou `CREDITS` (débit par repas, avec tarif configurable).
- Image de fond personnalisée pour l'écran de connexion.

---

## 13. Support

**KLEM Technologies & Services** — 📞 +225 07 58 89 24 77 · 📧 infos@klemtech.net
