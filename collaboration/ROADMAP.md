# ROADMAP — Cantine Connect
> Inventaire complet des modules, fonctionnalités et tests de validation
> Mis à jour : 2026-07-01 | Responsable : Yacouba SYLLA

---

## Légende
| Icône | Statut |
|-------|--------|
| ✅ | Livré et testé |
| 🔄 | En cours |
| 🔲 | À faire |
| ❌ | Bloqué |

---

## PARTIE 1 — BACK-END (Spring Boot 3.x / Java 17)

### ✅ B-01 · Infrastructure & Fondations
**Périmètre :** Docker, Spring Boot, configuration multi-profils, CORS  
**Fichiers clés :** `docker-compose.yml`, `application.yml`, `CantineConnectApplication.java`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | `docker compose up -d` → PostgreSQL actif sur `localhost:5432` | ✅ |
| 2 | `./mvnw compile -q` → 0 erreur | ✅ |
| 3 | `curl http://localhost:8081/actuator/health` → `{"status":"UP"}` | ✅ |
| 4 | Requête depuis `localhost:5173` → pas d'erreur CORS | ✅ |

---

### ✅ B-02 · Gestion Structurelle (Établissements / Niveaux / Classes)
**Périmètre :** CRUD complet, cascade JPA, pagination  
**Fichiers clés :** `etablissement/` (entity, repository, service, controller, dto)

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | `POST /api/v1/etablissements` → 201 + objet créé | ✅ |
| 2 | `GET /api/v1/etablissements` → liste des établissements actifs | ✅ |
| 3 | `POST /api/v1/etablissements/{id}/niveaux` → niveau créé | ✅ |
| 4 | `POST /api/v1/etablissements/niveaux/{id}/classes` → classe créée | ✅ |
| 5 | `DELETE /api/v1/etablissements/niveaux/{id}` → cascade classes supprimées | ✅ |
| 6 | `DELETE /api/v1/etablissements/classes/{id}` → classe supprimée | ✅ |
| 7 | `GET /api/v1/etablissements/{id}/niveaux` → structure hiérarchique complète | ✅ |

---

### ✅ B-03 · Gestion des Élèves
**Périmètre :** CRUD, QR code UUID, pagination serveur, filtres multi-critères  
**Fichiers clés :** `eleve/` (entity, repository, service, controller, dto)

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | `POST /api/v1/eleves` → 201 + qrCodeToken UUID auto-généré | ✅ |
| 2 | `GET /api/v1/eleves?page=0&size=10` → page paginée | ✅ |
| 3 | `GET /api/v1/eleves?search=kouassi` → filtre par nom | ✅ |
| 4 | `GET /api/v1/eleves?etablissementId=1` → filtre par établissement | ✅ |
| 5 | `GET /api/v1/eleves?statut=AUTORISE` → filtre par statut | ✅ |
| 6 | `PUT /api/v1/eleves/{id}` → mise à jour complète | ✅ |
| 7 | `PATCH /api/v1/eleves/{id}/statut?statut=AUTORISE` → changement statut | ✅ |
| 8 | `DELETE /api/v1/eleves/{id}` → suppression logique (`actif=false`) | ✅ |
| 9 | `POST` avec matricule dupliqué → 400 avec message clair | ✅ |
| 10 | `POST` sans champs obligatoires → 400 + détail validation JSON | ✅ |

---

### ✅ B-04 · Authentification JWT Stateless
**Périmètre :** Login, JwtFilter, SecurityConfig, rôles ADMIN/GESTIONNAIRE/CAISSIER/PARENT  
**Fichiers clés :** `auth/` (entity, repository, service, controller, config, dto), `common/JwtAuthFilter.java`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | `POST /api/v1/auth/login` (admin@cantine.connect / admin@123) → 200 + token JWT | ✅ |
| 2 | `POST /api/v1/auth/login` (mauvais mdp) → 401 + message `UNAUTHORIZED` | ✅ |
| 3 | `GET /api/v1/eleves` sans token → 403 | ✅ |
| 4 | `GET /api/v1/eleves` avec token valide → 200 | ✅ |
| 5 | Comptes de seed (un par rôle) créés via migration `V6__reset_comptes_un_par_role.sql` (ADR-012) | ✅ |
| 6 | Token expiré → 403 et redirection /login côté frontend | ✅ |

---

### ✅ B-05 · ActionLog AOP (Traçabilité)
**Périmètre :** Aspect Spring AOP, table `action_logs`, capture avant/après, auteur JWT  
**Fichiers clés :** `actionlog/` (annotation, aspect, controller, dto, entity, repository, service), `common/AsyncConfig.java`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | `POST /api/v1/eleves` → ligne insérée dans `action_logs` avec `type=CREATE` | ✅ |
| 2 | `PATCH /api/v1/eleves/{id}/statut` → ligne avec `type=UPDATE` + payload avant/après | ✅ |
| 3 | `DELETE /api/v1/eleves/{id}` → ligne avec `type=DELETE` + payload `{"id":"2"}` | ✅ |
| 4 | Auteur = `admin@cantine.connect` (email extrait du SecurityContext JWT) | ✅ |
| 5 | Exécution asynchrone (`@Async`) → log créé sans bloquer la réponse HTTP | ✅ |

---

### ✅ B-06 · Moteur de Paiements & Webhooks
**Périmètre :** TransactionPaiement, CinetPay/PayDunya, webhooks, statuts Mobile Money  
**Fichiers clés :** `paiement/` (entity, dto, repository, service, controller, config)

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | `POST /api/v1/paiements/initier` → transaction EN_ATTENTE + paymentUrl CinetPay | ✅ |
| 2 | Webhook CinetPay `cpm_result=00` → transaction ACCEPTE + élève AUTORISE | ✅ |
| 3 | Webhook CinetPay `cpm_result=01/REFUSED` → transaction REFUSE, statut élève inchangé | ✅ |
| 4 | Signature vérifiée si `CINETPAY_VERIFY_SIGNATURE=true` (désactivée en dev) | ✅ |
| 5 | `GET /api/v1/paiements?eleveId={id}` → historique transactions paginé | ✅ |
| 6 | Webhook retourne HTTP 200 immédiatement, traitement `@Async` en arrière-plan | ✅ |

---

### ✅ B-07 · Contrôle d'Accès QR Code / Scan Réfectoire
**Périmètre :** PassageRefectoire, validation < 1s, cache offline 24h  
**Fichiers clés :** `scan/` (entity, dto, repository, service, controller)

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | `POST /api/v1/scan/{qrCodeToken}` → réponse en **240ms** (< 1 000ms) | ✅ |
| 2 | Token valide + statut `AUTORISE` → 200 `{"acces":"ACCORDÉ"}` + passageId | ✅ |
| 3 | Token valide + statut `SUSPENDU` → 200 `{"acces":"REFUSÉ"}` motif=STATUT_SUSPENDU | ✅ |
| 4 | Token inexistant → 404 | ✅ |
| 5 | Double passage même jour → `{"acces":"REFUSÉ"}` motif=DOUBLON_PASSAGE | ✅ |
| 6 | `GET /api/v1/scan/cache` → cache avec tous les élèves actifs + statuts | ✅ |
| 7 | `GET /api/v1/passages?date=2026-06-30` → liste 3 passages (ACCORDE + REFUSE) | ✅ |

---

### ✅ B-08 · Gestion des Utilisateurs (Admin)
**Périmètre :** CRUD utilisateurs, changement de rôle, désactivation  
**Fichiers clés :** `auth/dto/`, `auth/service/UtilisateurService.java`, `auth/controller/UtilisateurController.java`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | `GET /api/v1/utilisateurs` (ADMIN) → liste paginée | ✅ |
| 2 | `POST /api/v1/utilisateurs` (ADMIN) → création gestionnaire 201 | ✅ |
| 3 | `PATCH /api/v1/utilisateurs/{id}/role` → GESTIONNAIRE → CAISSIER | ✅ |
| 4 | `DELETE /api/v1/utilisateurs/{id}` → désactivation soft (actif=false) | ✅ |
| 5 | Désactiver dernier ADMIN → 409 CONFLICT avec message explicite | ✅ |
| 6 | `GET /api/v1/utilisateurs/{id}` → actif=false après désactivation | ✅ |
| 7 | Accès GESTIONNAIRE → 403 FORBIDDEN (ADMIN seulement) | ✅ |
| 8 | `PATCH /{id}/reactiver` → actif=true restauré | ✅ |

---

## PARTIE 2 — FRONT-END (React 18 / Vite / MUI v9)

### ✅ F-01 · Socle Technique & Layout
**Périmètre :** ThemeProvider, MainLayout (AppBar + Drawer), routing React Router v7  
**Fichiers clés :** `theme.js`, `layouts/MainLayout.jsx`, `App.jsx`, `main.jsx`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | `npm run dev` → démarrage sans erreur sur `localhost:5173` | ✅ |
| 2 | Drawer persistant → pas de chevauchement avec le contenu | ✅ |
| 3 | Navigation sidebar → changement de page sans rechargement | ✅ |
| 4 | AppBar affiche nom + rôle + bouton déconnexion | ✅ |

---

### ✅ F-02 · Authentification UI
**Périmètre :** LoginPage, AuthContext (localStorage), ProtectedRoute, intercepteur 401  
**Fichiers clés :** `pages/auth/LoginPage.jsx`, `context/AuthContext.jsx`, `components/ProtectedRoute.jsx`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | Accès à `/dashboard` sans login → redirect `/login` | ✅ |
| 2 | Login valide → redirect `/dashboard` + token en localStorage | ✅ |
| 3 | Login invalide → message d'erreur affiché | ✅ |
| 4 | Clic "Déconnexion" → token supprimé + redirect `/login` | ✅ |
| 5 | Refresh page authentifié → session restaurée (token localStorage) | ✅ |

---

### ✅ F-03 · Gestion Structurelle UI
**Périmètre :** EtablissementsPage, GestionStructureDialog (niveaux + classes)  
**Fichiers clés :** `pages/etablissements/`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | Créer un établissement → apparaît dans la liste | ✅ |
| 2 | Ouvrir "Gérer les classes" → dialog avec la structure | ✅ |
| 3 | Saisir `CP, CE1, CM1` → 3 niveaux créés en un clic | ✅ |
| 4 | Saisir `CP A, CP B` sous un niveau → 2 classes créées | ✅ |
| 5 | Supprimer un niveau avec classes → confirmation + cascade | ✅ |
| 6 | Supprimer une classe individuelle → confirmation | ✅ |

---

### ✅ F-04 · Gestion des Élèves UI
**Périmètre :** ElevesPage (tableau paginé), EleveFormDialog (3 onglets)  
**Fichiers clés :** `pages/eleves/`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | Tableau paginé → 10 lignes par défaut, navigation fonctionnelle | ✅ |
| 2 | Filtre recherche → résultats mis à jour côté serveur | ✅ |
| 3 | Filtre statut → filtre côté serveur | ✅ |
| 4 | Dialog 3 onglets → Général / Cantine+Affectation / Contacts+Allergies | ✅ |
| 5 | Select Classe → vide si aucun établissement sélectionné | ✅ |
| 6 | Select Classe → se peuple après sélection d'un établissement | ✅ |
| 7 | Validation formulaire → erreurs affichées sur le bon onglet | ✅ |
| 8 | Modifier un élève → formulaire pré-rempli | ✅ |
| 9 | Supprimer un élève → confirmation + disparition du tableau | ✅ |
| 10 | Badge StatutBadge → couleur correcte par statut | ✅ |

---

### ✅ F-05 · Dashboard (Stats Réelles)
**Périmètre :** Statistiques dynamiques depuis API (nb élèves, passages, répartition statuts)  
**Fichiers clés :** `services/dashboardService.js`, `hooks/useDashboard.js`, `pages/DashboardPage.jsx`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | Nombre d'établissements réel : 1 | ✅ |
| 2 | Élèves actifs, autorisés, suspendus, grâce | ✅ |
| 3 | Passages du jour en temps réel : 3 (accordés + refusés) | ✅ |
| 4 | Répartition 4 statuts avec chips colorés | ✅ |
| 5 | Skeletons MUI pendant chargement | ✅ |
| 6 | Bouton rafraîchir fonctionnel | ✅ |
| 7 | Table des derniers passages (heure + résultat + motif) | ✅ |

---

### ✅ F-06 · Interface Paiements
**Périmètre :** Liste transactions, initiation paiement, statuts Mobile Money  
**Fichiers clés :** `services/paiementService.js`, `hooks/usePaiements.js`, `pages/paiements/PaiementsPage.jsx`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | Table transactions paginée (dateCreation, élève, montant, statut, référence) | ✅ |
| 2 | Filtre par statut (chips : Tous / En attente / Accepté / Refusé / Annulé) | ✅ |
| 3 | Dialog "Initier un paiement" : Autocomplete élève (debounce 300ms) | ✅ |
| 4 | Dialog : Select opérateur (Orange/MTN/Moov/Wave) avec pastille couleur | ✅ |
| 5 | Soumission → transaction créée statut EN_ATTENTE + paymentUrl affiché | ✅ |
| 6 | paymentUrl cliquable → lien CinetPay checkout dans un onglet | ✅ |
| 7 | Skeletons MUI pendant chargement, bouton rafraîchir | ✅ |
| 8 | Filtres Date début/fin + Opérateur cumulables avec statut et recherche élève (2026-07-03) | ✅ |

---

### ✅ F-07 · Interface QR Code / Scan Mobile
**Périmètre :** Page scan mobile-friendly, résultat ACCORDÉ/REFUSÉ, historique passages, cache offline  
**Fichiers clés :** `services/scanService.js`, `services/cacheOfflineService.js`, `hooks/useScan.js`, `pages/scan/ScanPage.jsx`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | ScanPage : input QR token + bouton Scanner (compatible scanner USB barcode) | ✅ |
| 2 | Résultat ACCORDÉ : carte verte (élève, classe, heure) / REFUSÉ : carte rouge + motif | ✅ |
| 3 | Passages du jour (panel droit) rechargé après chaque scan | ✅ |
| 4 | Cache offline : GET /scan/cache → stockage localStorage 24h | ✅ |
| 5 | Fallback offline : scan validé contre le cache si réseau indisponible | ✅ |
| 6 | Chip statut En ligne / Hors ligne (navigator.onLine + events) | ✅ |
| 7 | QrCodeDialog sur ElevesPage : QRCodeSVG 220px + copier token + imprimer | ✅ |

---

### ✅ F-08 · Gestion des Utilisateurs UI (Admin)
**Périmètre :** Liste utilisateurs, création, changement rôle (ADMIN seulement)  
**Fichiers clés :** `services/utilisateurService.js`, `hooks/useUtilisateurs.js`, `pages/utilisateurs/UtilisateursPage.jsx`, `components/AdminRoute.jsx`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | Menu "Utilisateurs" filtré par rôle ADMIN dans la sidebar | ✅ |
| 2 | `AdminRoute` redirige vers /dashboard si non-ADMIN | ✅ |
| 3 | Créer un gestionnaire → apparaît dans la liste (3 → 4) | ✅ |
| 4 | Changer le rôle via Select inline → GESTIONNAIRE → CAISSIER | ✅ |
| 5 | Désactiver → HTTP 204 → login → 401 UNAUTHORIZED | ✅ |
| 6 | GESTIONNAIRE accède /utilisateurs → 403 FORBIDDEN | ✅ |
| 7 | Row "(vous)" + bouton Désactiver désactivé pour l'utilisateur connecté | ✅ |
| 8 | Bandeau de filtres (recherche email/nom/prénom/téléphone, rôle, statut, date de création) cumulables (2026-07-03) | ✅ |

---

### ✅ B-09 / F-09 · Module Parents (Rattachement Compte ↔ Enfants)
**Périmètre :** CRUD Parent (ADMIN), recherche parent par numéro/nom/prénom, recherche élève par matricule/nom/prénom
**Fichiers clés :** `parent/` (entity, repository, service, controller, dto), `pages/parents/ParentsPage.jsx`, `services/parentService.js`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | `POST /api/v1/parents` (ADMIN) → lie un utilisateur PARENT existant à des élèves | ✅ |
| 2 | `GET /api/v1/utilisateurs?role=PARENT&search=07...` → trouve le compte par numéro | ✅ |
| 3 | `GET /api/v1/eleves?search=` → trouve l'élève par matricule/nom/prénom | ✅ |
| 4 | `GET /api/v1/parents/moi` (PARENT) → retourne son profil + ses enfants | ✅ |
| 5 | `PUT /api/v1/parents/{id}/enfants` → modifie la liste des enfants associés | ✅ |
| 6 | Tentative de création sans compte PARENT existant → message explicite | ✅ |
| 7 | `GET /api/v1/parents?search=` → filtre la liste principale par email du compte parent (2026-07-03) | ✅ |

---

### ✅ B-10 / F-10 · RBAC Serveur du Rôle PARENT (ADR-011)
**Périmètre :** Restriction du rôle PARENT à ses propres enfants, masquage des fonctionnalités staff
**Fichiers clés :** `PaiementService`, `ScanService`, `EleveController`, `EtablissementController`, `ScanController`, `components/StaffRoute.jsx`

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | PARENT → `GET /api/v1/eleves` → 403 | ✅ |
| 2 | PARENT → `GET /api/v1/etablissements` → 403 | ✅ |
| 3 | PARENT → `GET /api/v1/scan/cache` → 403 | ✅ |
| 4 | PARENT → `GET /api/v1/paiements` → uniquement les transactions de ses enfants | ✅ |
| 5 | PARENT → `POST /api/v1/paiements/initier` pour un élève non-possédé → 403 | ✅ |
| 6 | PARENT → `GET /api/v1/paiements/{id}` d'un tiers → 404 | ✅ |
| 7 | PARENT → `GET /api/v1/passages` → uniquement les passages de ses enfants | ✅ |
| 8 | PARENT navigue vers `/eleves`, `/etablissements`, `/scan` → redirigé `/dashboard` | ✅ |

---

### 🔄 F-11 · Module Rapports (v1 exploratoire, 2026-07-03)
**Périmètre :** États financiers/statistiques, paiements, passages — export PDF/Excel, réservé GESTIONNAIRE/CAISSIER/ADMIN
**Fichiers clés :** `hooks/useRapports.js`, `services/rapportExportService.js`, `pages/rapports/RapportsPage.jsx` — voir ADR-016

| # | Test de validation | Résultat |
|---|-------------------|----------|
| 1 | GESTIONNAIRE/CAISSIER/ADMIN → menu « Rapports » visible, accès à `/rapports` | ✅ |
| 2 | PARENT → item de menu absent, navigation directe vers `/rapports` redirigée `/dashboard` | ✅ |
| 3 | « Générer le rapport » → onglets Résumé/Paiements/Passages peuplés à partir de la période choisie | ✅ |
| 4 | Onglet Résumé : montant encaissé, compteurs par statut, taux d'accès corrects | ✅ |
| 5 | « Exporter Excel » → fichier `.xlsx` téléchargé, 3 feuilles (Résumé/Paiements/Passages), contenu vérifié | ✅ |
| 6 | « Imprimer / PDF » par onglet → `window.print()` déclenché, zone imprimable isolée du reste de l'UI | ✅ |
| 7 | Filtre Établissement restreint les passages du rapport | ✅ |

---

## Récapitulatif de progression

| Module | Backend | Frontend | Priorité |
|--------|---------|----------|----------|
| B-01 Infrastructure | ✅ | — | — |
| B-02 / F-03 Structure | ✅ | ✅ | — |
| B-03 / F-04 Élèves | ✅ | ✅ | — |
| B-04 / F-02 Auth JWT | ✅ | ✅ | — |
| F-01 Layout | — | ✅ | — |
| B-05 ActionLog AOP | ✅ | — | — |
| B-06 / F-06 Paiements | ✅ | ✅ | ✅ Livré |
| B-07 / F-07 QR Scan | ✅ | ✅ | ✅ Livré |
| F-05 Dashboard | — | ✅ | ✅ Livré |
| B-08 / F-08 Utilisateurs | ✅ | ✅ | ✅ Livré |
| B-09 / F-09 Parents | ✅ | ✅ | ✅ Livré |
| B-10 / F-10 RBAC PARENT | ✅ | ✅ | ✅ Livré |
| F-11 Rapports | — | 🔄 | 🔄 v1 exploratoire |

**Avancement global : 18/18 modules cœur livrés (100%) 🎉 + 1 module exploratoire (Rapports)**

---

## Améliorations Post-Livraison

| # | Amélioration | Statut |
|---|-------------|--------|
| P1 | Tests unitaires JUnit 5 (23 tests — ScanService, UtilisateurService, EleveService, WebhookService) | ✅ Livré |
| P2 | Flyway migrations versionnées — V1__init_schema.sql + ddl-auto validate | ✅ Livré |
| P3 | Dockerfiles multi-stage (backend + frontend) + déploiement prod | ✅ Livré |
| P4 | CI/CD GitHub Actions (build + test + lint) | 🔲 À faire |
| P5 | Documentation API Swagger/OpenAPI | 🔲 À faire |
| P6 | Numéro de cellulaire obligatoire et unique sur les comptes utilisateurs (V5, préparation SMS) | ✅ Livré |
| P7 | Recherche + export CSV sur Élèves et Paiements (déjà présents sur l'Historique des Passages) | ✅ Livré |
| P8 | Réinitialisation des comptes : un compte de référence par rôle via migration Flyway (V6, ADR-012) | ✅ Livré |
| P9 | Suppression de l'indice « Compte par défaut » sur l'écran de connexion | ✅ Livré |
| P10 | Notifications SMS effectives aux parents (le numéro de cellulaire est en place, l'envoi reste à implémenter) | 🔲 À faire |

### Déploiement Production
| Composant | Plateforme | Statut |
|-----------|-----------|--------|
| Frontend React/Vite | Vercel | ✅ Online |
| Backend Spring Boot | Railway | ✅ Online |
| Base de données | Railway PostgreSQL | ✅ Online |
