# Cahier de Recette — Cantine Connect

> Scénarios de validation fonctionnelle (UAT), organisés par module. Statut au 2026-07-03.
> Complète `collaboration/ROADMAP.md` (tests techniques par endpoint) avec une vue orientée métier/parcours utilisateur.

**Légende** : ✅ Validé · 🔲 À valider

---

## 1. Connexion et identifiants

| # | Scénario | Résultat attendu | Statut |
|---|----------|-------------------|--------|
| 1.1 | L'écran de connexion n'affiche aucun identifiant par défaut | Aucun encart « Compte par défaut » visible sous le formulaire | ✅ |
| 1.2 | Connexion avec chacun des 4 comptes de référence (ADMIN, GESTIONNAIRE, CAISSIER, PARENT) | Connexion réussie, redirection vers le tableau de bord, rôle correct affiché | ✅ |
| 1.3 | Connexion avec un mauvais mot de passe | Message d'erreur, pas de redirection | ✅ |
| 1.4 | Il n'existe qu'un seul compte par rôle après réinitialisation | `SELECT COUNT(*) FROM utilisateurs` = 4 | ✅ |

---

## 2. Création de compte utilisateur — numéro de cellulaire

| # | Scénario | Résultat attendu | Statut |
|---|----------|-------------------|--------|
| 2.1 | Créer un utilisateur sans renseigner le téléphone | Refusé (400) — « Le numéro de cellulaire est obligatoire » | ✅ |
| 2.2 | Créer un utilisateur avec un numéro déjà utilisé par un autre compte | Refusé (400) — message explicite avec le numéro en conflit | ✅ |
| 2.3 | Créer un utilisateur avec nom, prénom, email, téléphone unique, mot de passe et rôle valides | Compte créé (201), visible dans la liste avec son numéro | ✅ |
| 2.4 | Modifier le téléphone d'un compte existant vers un numéro déjà pris | Refusé (400) | ✅ |

---

## 3. Rattachement Parent ↔ Élèves

| # | Scénario | Résultat attendu | Statut |
|---|----------|-------------------|--------|
| 3.1 | Depuis « Parents », rechercher un compte parent par son numéro de cellulaire | Le compte correspondant apparaît dans la liste déroulante | ✅ |
| 3.2 | Rechercher un compte parent par nom ou prénom | Le compte correspondant apparaît | ✅ |
| 3.3 | Rechercher un élève par matricule | L'élève apparaît dans le sélecteur multiple | ✅ |
| 3.4 | Rechercher un élève par nom ou prénom | L'élève apparaît | ✅ |
| 3.5 | Associer un parent à plusieurs enfants puis enregistrer | Le lien est créé, visible dans le tableau des parents avec la liste des enfants | ✅ |
| 3.6 | Tenter de créer un compte parent sans compte PARENT existant au préalable | Message explicite invitant à créer le compte dans « Utilisateurs » d'abord | ✅ |

---

## 4. Périmètre du rôle PARENT

| # | Scénario | Résultat attendu | Statut |
|---|----------|-------------------|--------|
| 4.1 | Un compte PARENT connecté — menu latéral | « Établissements », « Élèves » et « Scan Réfectoire » ne sont pas affichés | ✅ |
| 4.2 | Un compte PARENT tente d'accéder directement à l'URL `/eleves`, `/etablissements` ou `/scan` | Redirection automatique vers `/dashboard` | ✅ |
| 4.3 | Un compte PARENT appelle directement l'API `GET /api/v1/eleves` (hors UI) | 403 Forbidden | ✅ |
| 4.4 | Un compte PARENT consulte « Paiements » | Ne voit que les transactions de ses propres enfants | ✅ |
| 4.5 | Un compte PARENT initie un paiement pour l'un de ses enfants | Transaction créée, URL de paiement affichée | ✅ |
| 4.6 | Un compte PARENT tente d'initier un paiement pour un élève qui n'est pas le sien (requête API forgée) | 403 Forbidden | ✅ |
| 4.7 | Un compte PARENT consulte le détail d'une transaction d'un autre parent (requête API forgée par ID) | 404 Not Found (aucune fuite d'information sur l'existence de la transaction) | ✅ |
| 4.8 | Un compte PARENT consulte « Historique des Passages » | Ne voit que les passages de ses propres enfants ; le filtre « Établissement » est masqué | ✅ |
| 4.9 | Un compte PARENT appelle `GET /api/v1/scan/cache` ou `POST /api/v1/scan/{token}` | 403 Forbidden | ✅ |

---

## 5. Élèves — recherche et export

| # | Scénario | Résultat attendu | Statut |
|---|----------|-------------------|--------|
| 5.1 | Rechercher un élève par nom, prénom ou matricule | Liste filtrée côté serveur | ✅ |
| 5.2 | Filtrer par établissement puis par statut d'accès | Liste combinée correctement filtrée | ✅ |
| 5.3 | Cliquer sur « CSV » avec des résultats affichés | Fichier `eleves_AAAA-MM-JJ.csv` téléchargé (matricule, nom, prénom, établissement, classe, statut) | ✅ |
| 5.4 | Cliquer sur « CSV » sans aucun résultat affiché | Bouton désactivé | ✅ |

---

## 6. Paiements — recherche et export

| # | Scénario | Résultat attendu | Statut |
|---|----------|-------------------|--------|
| 6.1 | Rechercher un paiement par nom, prénom ou matricule de l'élève | Liste filtrée côté serveur | ✅ |
| 6.2 | Recherche sur un terme absent de la base | 0 résultat, message « Aucune transaction » | ✅ |
| 6.3 | Combiner recherche élève + filtre statut | Résultats correctement combinés | ✅ |
| 6.4 | Cliquer sur « CSV » avec des résultats affichés | Fichier `paiements_AAAA-MM-JJ.csv` téléchargé (date, élève, opérateur, montant, téléphone, statut, référence) | ✅ |
| 6.5 | Filtrer par plage de dates (Date début/fin) et par opérateur, seuls ou combinés au statut/recherche élève (2026-07-03) | Résultats correctement combinés côté serveur | ✅ |

---

## 7. Historique des Passages — recherche et export (rappel — livré précédemment)

| # | Scénario | Résultat attendu | Statut |
|---|----------|-------------------|--------|
| 7.1 | Filtrer par plage de dates, établissement, résultat et recherche élève | Résultats combinés correctement | ✅ |
| 7.2 | Export CSV de la page courante | Fichier `passages_AAAA-MM-JJ.csv` téléchargé | ✅ |

---

## 8. Rapports — v1 exploratoire (2026-07-03)

| # | Scénario | Résultat attendu | Statut |
|---|----------|-------------------|--------|
| 8.1 | GESTIONNAIRE, CAISSIER et ADMIN → menu « Rapports » visible et accessible | ✅ |
| 8.2 | PARENT → menu « Rapports » absent, navigation directe vers `/rapports` → redirection `/dashboard` | ✅ |
| 8.3 | Choisir une période, cliquer « Générer le rapport » → onglets Résumé/Paiements/Passages peuplés | ✅ |
| 8.4 | Onglet Résumé : montant encaissé, compteurs par statut de paiement, compteurs par résultat de passage, taux d'accès | ✅ |
| 8.5 | « Exporter Excel » → fichier `rapport_<dateDebut>_<dateFin>.xlsx` téléchargé, 3 feuilles, données vérifiées | ✅ |
| 8.6 | « Imprimer / PDF » sur chaque onglet → `window.print()` déclenché | ✅ |
| 8.7 | Filtre Établissement → restreint uniquement les passages du rapport | ✅ |

---

## 9. Utilisateurs — recherche et filtres (2026-07-03)

| # | Scénario | Résultat attendu | Statut |
|---|----------|-------------------|--------|
| 9.1 | Rechercher un compte par email | Liste filtrée côté serveur | ✅ |
| 9.2 | Filtrer par rôle | Liste restreinte au rôle sélectionné | ✅ |
| 9.3 | Filtrer par statut (Actif/Inactif) | Liste restreinte au statut sélectionné | ✅ |
| 9.4 | Filtrer par plage de dates de création (Créé depuis/jusqu'au) | Liste restreinte à la plage | ✅ |
| 9.5 | Combiner recherche + rôle + statut + dates | Filtres cumulés correctement côté serveur | ✅ |
| 9.6 | Sélecteur de compte parent (page Parents) ne propose toujours que des comptes PARENT actifs | Comportement inchangé après le refactoring du filtre rôle | ✅ |

---

## 10. Parents — recherche par email (2026-07-03)

| # | Scénario | Résultat attendu | Statut |
|---|----------|-------------------|--------|
| 10.1 | Rechercher un compte parent par email exact ou partiel | Liste filtrée côté serveur | ✅ |
| 10.2 | Recherche sur un email absent de la base | 0 résultat, message « Aucun compte parent enregistré » | ✅ |

---

## 11. Non-régression

| # | Scénario | Résultat attendu | Statut |
|---|----------|-------------------|--------|
| 11.1 | Suite de tests backend (`./mvnw test`) | 38/38 tests passent | ✅ |
| 11.2 | Build frontend (`npm run build`) | Compile sans erreur | ✅ |
| 11.3 | Lint frontend (`npx eslint src/`) | Aucune nouvelle erreur par rapport à la base de référence | ✅ |
| 11.4 | Compte ADMIN peut toujours gérer Établissements, Élèves, Scan, Utilisateurs, Parents, Configuration sans restriction | Accès complet conservé | ✅ |
| 11.5 | Comptes GESTIONNAIRE et CAISSIER conservent l'accès à Établissements, Élèves, Scan, Paiements, Passages | Accès complet conservé (seuls Utilisateurs/Parents/Configuration restent ADMIN uniquement) | ✅ |
