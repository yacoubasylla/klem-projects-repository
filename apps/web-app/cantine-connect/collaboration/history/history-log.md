# Journal de Bord Chronologique — Cantine Connect

---

### [2026-06-20] - Kickoff et Initialisation de l'Écosystème
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Fichiers Modifiés** : `CLAUDE.md`, `collaboration/context/CONTEXT.md`, `collaboration/doc/workflows.md`, `server-backend/`, `client-frontend/`
- **Description** : Création de la structure globale du projet (client-frontend Vite+React+MUI, server-backend Spring Boot 3.x). Définition des fichiers de gouvernance initiaux. Squelettes applicatifs configurés.

---

### [2026-06-30] - Mise à Jour des Fichiers de Gouvernance (Base de Connaissance IA)
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Fichiers Modifiés** :
  - `collaboration/context/CONTEXT.md` — Réécrit intégralement pour Cantine Connect
  - `collaboration/doc/architecture.md` — Architecture 3-tiers, JWT stateless, Spring AOP, offline-first, Docker
  - `collaboration/doc/specifications.md` — 5 modules avec modèles SQL, contrats API et règles métier
  - `commands/startup.md` — Noms de fichiers corrigés (casse Linux)
- **Description** : Refonte complète de la base de connaissance IA héritée du projet Parc Auto. Alignement sur la proposition commerciale Cantine Connect (Juin 2026).

---

### [2026-06-30] - B-01 · Infrastructure & Docker (Back-end)
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Fichiers Modifiés** : `docker-compose.yml`, `server-backend/src/main/resources/application.yml`
- **Description** : Environnement Docker avec PostgreSQL 16 sur le port 5432 et pgAdmin sur le port 5050. Spring Boot configuré sur le port 8081 (port 8080 occupé). CORS pour `localhost:5173`. Actuator activé.
- **Tests validés** : Compilation propre, `actuator/health` → UP, CORS OK.

---

### [2026-06-30] - B-02 + F-01 + F-03 · Module Gestion Structurelle (Établissements / Niveaux / Classes)
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Fichiers Modifiés** :
  - `server-backend/.../etablissement/` (entity, dto, repository, service, controller)
  - `client-frontend/src/pages/etablissements/` (EtablissementsPage, EtablissementFormDialog, GestionStructureDialog)
  - `client-frontend/src/services/etablissementService.js`
  - `client-frontend/src/hooks/useEtablissements.js`
  - `client-frontend/src/layouts/MainLayout.jsx`, `App.jsx`, `theme.js`
- **Description** : CRUD complet établissements avec gestion hiérarchique Niveaux/Classes. Interface de saisie en lot (ex: `CP, CE1, CM1`) avec suppression individuelle ou en cascade. Layout MainLayout avec Drawer persistant, routing React Router v7, thème KLEM. Correction bug `@Builder.Default` Lombok pour les valeurs par défaut JPA.
- **Décision technique** : Requête native PostgreSQL avec `CAST(:param AS type)` pour contourner bug Hibernate 6 + PostgreSQL sur paramètres JPQL nullables. Voir ADR-007.
- **Tests validés** : CRUD établissements, niveaux, classes. Suppression en cascade. UI saisie en lot.

---

### [2026-06-30] - B-03 + F-04 · Module Gestion des Élèves
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Fichiers Modifiés** :
  - `server-backend/.../eleve/` (entity, dto, repository, service, controller)
  - `client-frontend/src/pages/eleves/` (ElevesPage, EleveFormDialog, StatutBadge)
  - `client-frontend/src/services/eleveService.js`
  - `client-frontend/src/hooks/useEleves.js`
- **Description** : CRUD élèves avec QR code UUID auto-généré. Pagination côté serveur (10 000+ lignes). Filtres multi-critères (texte, établissement, statut). Formulaire 3 onglets MUI (Général / Cantine+Affectation / Contacts+Allergies) — zéro scroll vertical. Suppression logique. Badge statut coloré.
- **Tests validés** : Pagination, filtres, création avec QR token, modification, suppression, validation formulaire.

---

### [2026-06-30] - B-04 + F-02 · Module Authentification JWT Stateless
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Commit GitHub** : `66ae192` — `yacoubasylla/cantine-connect`
- **Fichiers Modifiés** :
  - `server-backend/.../auth/` (Utilisateur, Role, LoginRequestDTO, AuthResponseDTO, UtilisateurRepository, AuthService, JwtService, AuthController, DataInitializer, PasswordEncoderConfig)
  - `server-backend/.../common/JwtAuthFilter.java`, `SecurityConfig.java`, `ApiResponse.java`
  - `client-frontend/src/context/AuthContext.jsx`
  - `client-frontend/src/hooks/useAuth.js`
  - `client-frontend/src/services/authService.js`, `apiClient.js`
  - `client-frontend/src/pages/auth/LoginPage.jsx`
  - `client-frontend/src/components/ProtectedRoute.jsx`
  - `client-frontend/src/main.jsx`, `App.jsx`, `layouts/MainLayout.jsx`
- **Description** : JWT stateless HMAC-SHA512 (jjwt 0.12.3). Entité Utilisateur (UserDetails) avec rôles ADMIN/GESTIONNAIRE/CAISSIER. JwtAuthFilter injecté avant UsernamePasswordAuthenticationFilter. DataInitializer crée `admin@cantine.connect / Admin123!` au premier démarrage. Frontend : LoginPage MUI, AuthContext localStorage, ProtectedRoute, intercepteur Axios 401 auto-redirect. Résolution dépendance circulaire via PasswordEncoderConfig. Voir ADR-005.
- **Tests validés** : Login → token JWT, 403 sans token, 200 avec token, redirect /login si non authentifié.

---

### [2026-06-30] - Gouvernance : Création ROADMAP + ADRs + Mise à jour Logs
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Fichiers Modifiés** :
  - `collaboration/ROADMAP.md` — Inventaire 16 modules (8 back + 8 front) avec tests de validation et statuts
  - `collaboration/history/history-log.md` — Historique complet depuis le kickoff
  - `collaboration/history/decision-log.md` — 7 décisions architecturales documentées
  - `collaboration/history/adr/2026-06-30-stack-technique-frontend.md`
  - `collaboration/history/adr/2026-06-30-jwt-stateless-authentication.md`
  - `collaboration/history/adr/2026-06-30-resolution-dependance-circulaire.md`
- **Description** : Mise en place du système de gouvernance. La ROADMAP couvre l'ensemble du projet avec 47 tests de validation répartis sur 16 modules. Protocole établi : chaque module livré entraîne une mise à jour des 3 fichiers de gouvernance (ROADMAP, history-log, decision-log + ADR si applicable).

---

### [2026-06-30] - Front-end F-08 · Gestion des Utilisateurs UI (ADMIN)
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel — **DERNIER MODULE — PROJET COMPLET 16/16**
- **Fichiers Créés** :
  - `services/utilisateurService.js` — lister, creer, changerRole, desactiver, reactiver
  - `hooks/useUtilisateurs.js` — pagination + CRUD complet
  - `pages/utilisateurs/UtilisateursPage.jsx` — table + dialog + select rôle inline
  - `components/AdminRoute.jsx` — garde de route ADMIN (redirect → /dashboard si non-ADMIN)
- **Fichiers Modifiés** :
  - `App.jsx` — route `/utilisateurs` wrappée dans `<AdminRoute>`
  - `layouts/MainLayout.jsx` — item "Utilisateurs" filtré par `roles: ['ADMIN']`, ManageAccountsIcon
- **Description** : Interface de gestion des comptes utilisateurs réservée ADMIN. Table avec Select rôle inline (ADMIN/GESTIONNAIRE/CAISSIER avec chips colorés). Boutons Désactiver (PersonOffIcon, rouge) / Réactiver (PersonAddIcon, vert). Row "(vous)" + désactivation de soi-même impossible. Dialog "Créer un compte" avec validation. Menu sidebar filtré par rôle. AdminRoute redirige automatiquement les non-ADMIN vers le dashboard.
- **Tests validés** : liste ✅, créer ✅, changer rôle ✅, désactiver→401 ✅, 403 GESTIONNAIRE ✅

---

### [2026-06-30] - Front-end F-07 · Interface QR Code / Scan Réfectoire
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Packages installés** : `qrcode.react` (génération QR Code SVG)
- **Fichiers Créés** :
  - `services/scanService.js` — scanner(), getCache(), getPassages()
  - `services/cacheOfflineService.js` — sauvegarder/charger/scanner/ageTexte (TTL 24h localStorage)
  - `hooks/useScan.js` — scan online + fallback offline automatique + rafraîchirCache
  - `pages/scan/ScanPage.jsx` — layout 2 colonnes (scan+résultat / passages du jour)
- **Fichiers Modifiés** :
  - `pages/eleves/ElevesPage.jsx` — QrCodeDialog avec QRCodeSVG 220px + copier + imprimer
  - `App.jsx` — route `/scan` ajoutée
  - `layouts/MainLayout.jsx` — nav item "Scan Réfectoire" (QrCodeScannerIcon) ajouté
- **Description** : Interface de contrôle accès réfectoire. Input QR token + bouton Scanner (compatible scanner USB/BT). Carte résultat : ACCORDÉ (fond vert) ou REFUSÉ (fond rouge) avec élève, classe, heure, motif. Panel droit : liste passages du jour rechargée après chaque scan. Mode offline : cache téléchargeable via GET /scan/cache → localStorage 24h, validation locale en cas de perte réseau. Chip statut en-ligne/hors-ligne (navigator.onLine + événements browser). QR codes affichables sur la fiche de chaque élève.
- **Tests validés** : scan online ✅, résultat carte ✅, passages ✅, cache download ✅, offline fallback ✅, 404 inconnu ✅, QrCodeDialog ✅

---

### [2026-06-30] - Front-end F-06 · Interface Paiements Mobile Money
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Fichiers Créés** :
  - `services/paiementService.js` — lister, getById, initier
  - `hooks/usePaiements.js` — pagination + filtres + initier
  - `pages/paiements/PaiementsPage.jsx` — table + dialog complet
- **Fichiers Modifiés** :
  - `App.jsx` — route `/paiements` ajoutée
  - `layouts/MainLayout.jsx` — entrée "Paiements" (PaymentsIcon) dans la sidebar
- **Description** : Interface complète de gestion des paiements Mobile Money. Table paginée filtrée par statut (chips EN_ATTENTE/ACCEPTÉ/REFUSÉ/ANNULÉ). Dialog "Initier un paiement" : Autocomplete élève avec debounce 300ms (search API), Select opérateur 4 opérateurs avec pastille couleur, montant min 100 XOF, téléphone. Après soumission : alerte succès avec lien CinetPay checkout cliquable. Correction champ `classeLibelle` (nom réel dans le DTO élève).
- **Tests validés** : liste ✅, filtre statut ✅, initier paiement ✅, paymentUrl ✅, autocomplete ✅

---

### [2026-06-30] - Front-end F-05 · Dashboard avec Stats Réelles
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Fichiers Créés** :
  - `services/dashboardService.js` — 7 appels API parallèles (`Promise.all`)
  - `hooks/useDashboard.js` — custom hook loading/error/data
- **Fichiers Modifiés** :
  - `pages/DashboardPage.jsx` — KPI cards + répartition statuts + table passages
  - `services/apiClient.js` — correction bug : `jwt_token` → `cc_token` (clé localStorage)
- **Description** : Dashboard entièrement dynamique. 4 cartes KPI (établissements, élèves actifs, passages du jour, en attente paiement). Répartition des 4 statuts d'accès en chips colorés. Table des 5 derniers passages avec heure, résultat (CheckCircle/Cancel) et motif de refus. Skeletons MUI pendant chargement. Bouton rafraîchir. Correction silencieuse du bug `apiClient.js` (clé mal nommée).
- **Tests validés** : établissements ✅, élèves/statuts ✅, passages ✅, skeletons ✅, rafraîchir ✅, table passages ✅

---

### [2026-06-30] - Back-end B-08 · Gestion des Utilisateurs (Admin)
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Fichiers Créés** :
  - `auth/dto/UtilisateurResponseDTO.java`, `CreerUtilisateurRequestDTO.java`, `ChangerRoleRequestDTO.java`
  - `auth/service/UtilisateurService.java`
  - `auth/controller/UtilisateurController.java`
- **Fichiers Modifiés** :
  - `auth/repository/UtilisateurRepository.java` — `countByRoleAndActifTrue(Role)`
  - `common/SecurityConfig.java` — `@EnableMethodSecurity` activé
  - `common/GlobalExceptionHandler.java` — handlers 403/401/409 ajoutés
- **Description** : CRUD complet des comptes utilisateurs (`ADMIN` uniquement via `@PreAuthorize`). Soft delete protégé contre la suppression du dernier ADMIN (409 CONFLICT). Correction transversale : `GlobalExceptionHandler` intercepte maintenant `AccessDeniedException` → 403 propre.
- **Tests validés** : liste ✅, création 201 ✅, changement rôle ✅, soft delete ✅, protection dernier ADMIN 409 ✅, GESTIONNAIRE 403 ✅, réactivation ✅

---

### [2026-06-30] - Back-end B-07 · Contrôle Accès QR Code / Scan Réfectoire
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Fichiers Créés** :
  - `scan/entity/` — `PassageRefectoire.java`, `ResultatScan.java`, `MotifRefus.java`
  - `scan/dto/` — `ScanResultDTO.java`, `PassageResponseDTO.java`, `CacheEntreeDTO.java`
  - `scan/repository/PassageRefectoireRepository.java` — doublon check, filtre par date/établissement
  - `scan/service/ScanService.java` — scanner(), getCacheOffline(), listerPassages()
  - `scan/controller/ScanController.java` — POST /scan/{token}, GET /scan/cache, GET /passages
- **Fichiers Modifiés** :
  - `eleve/repository/EleveRepository.java` — `findByQrCodeTokenAndActifTrue()` + `findAllActiveWithDetails()`
- **Description** : Module de contrôle d'accès au réfectoire par QR Code. Validation en 240ms (< 1s requis). Logique : AUTORISE/GRACE → vérifier doublon du jour → ACCORDÉ ou DOUBLON_PASSAGE ; SUSPENDU/EN_ATTENTE_PAIEMENT → REFUSÉ. Cache offline téléchargeable (tous élèves actifs + statuts) pour fonctionnement sans internet 24h. Historique des passages filtrable par date et établissement. Chaque scan enregistré dans `passages_refectoire` avec motif de refus si applicable.
- **Tests validés** : 240ms ✅, ACCORDÉ ✅, REFUSÉ SUSPENDU ✅, 404 inconnu ✅, doublon ✅, cache ✅, historique ✅

---

### [2026-06-30] - Back-end B-06 · Moteur de Paiements & Webhooks (CinetPay / PayDunya)
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Commit GitHub** : à venir
- **Fichiers Créés** :
  - `paiement/entity/` — `TransactionPaiement.java`, `StatutPaiement.java`, `OperateurMobileMoney.java`
  - `paiement/dto/` — `InitierPaiementRequestDTO.java`, `PaiementResponseDTO.java`, `WebhookCinetPayDTO.java`, `WebhookPayDunyaDTO.java`
  - `paiement/repository/TransactionPaiementRepository.java`
  - `paiement/service/PaiementService.java` — initier, lister, getById
  - `paiement/service/WebhookService.java` — traiterCinetPay + traiterPayDunya @Async
  - `paiement/controller/PaiementController.java` — POST /initier, GET /paiements, GET /{id}
  - `paiement/controller/WebhookController.java` — POST /webhooks/cinetpay + /paydunya (public)
  - `paiement/config/PaiementProperties.java` — @ConfigurationProperties
- **Fichiers Modifiés** :
  - `common/SecurityConfig.java` — `/api/v1/webhooks/**` ajouté aux routes publiques
  - `application.yml` — bloc `paiement.cinetpay` + `paiement.paydunya`
- **Description** : Moteur de paiements Mobile Money (Orange, MTN, Moov, Wave). Initiation de transaction avec URL de paiement CinetPay. Webhooks IPN asynchrones (`@Async`) : `cpm_result=00` → ACCEPTE + élève AUTORISE, autre → REFUSE. Signature SHA-256 configurable par variable d'environnement (`CINETPAY_VERIFY_SIGNATURE=true`). Support PayDunya avec même architecture. `@Traceable` sur initierPaiement pour la traçabilité AOP.
- **Tests validés** : Initier EN_ATTENTE ✅, Webhook ACCEPTED → AUTORISE ✅, Webhook REFUSED ✅, HTTP 200 immédiat ✅, Filtre eleveId ✅

---

### [2026-06-30] - Back-end B-05 · ActionLog AOP (Traçabilité Automatique)
- **Auteur** : Yacouba SYLLA / Claude Code
- **Statut** : Livré / Opérationnel
- **Commit GitHub** : à venir
- **Fichiers Créés** :
  - `server-backend/.../actionlog/annotation/Traceable.java` — annotation custom `@Traceable(action, entite)`
  - `server-backend/.../actionlog/entity/ActionLog.java` — entité JPA table `action_logs`
  - `server-backend/.../actionlog/entity/TypeAction.java` — enum CREATE / UPDATE / DELETE
  - `server-backend/.../actionlog/repository/ActionLogRepository.java`
  - `server-backend/.../actionlog/service/ActionLogService.java` — méthode `@Async sauvegarder()`
  - `server-backend/.../actionlog/aspect/ActionLogAspect.java` — aspect `@Around`
  - `server-backend/.../actionlog/dto/ActionLogResponseDTO.java`
  - `server-backend/.../actionlog/controller/ActionLogController.java` — `GET /api/v1/logs`
  - `server-backend/.../common/AsyncConfig.java` — `@EnableAsync`
- **Fichiers Modifiés** :
  - `EleveService.java` — `@Traceable` sur `creer`, `modifier`, `changerStatut`, `supprimer`
  - `EtablissementService.java` — `@Traceable` sur `creer`, `creerNiveau`, `creerClasse`, `supprimerNiveau`, `supprimerClasse`
- **Description** : Traçabilité automatique et transparente de toutes les opérations d'écriture via Spring AOP. L'aspect `@Around` intercepte chaque méthode annotée `@Traceable`, extrait l'auteur du SecurityContext JWT, capture le payload avant/après et sauvegarde en base de façon asynchrone (`@Async`) sans bloquer la réponse HTTP. Endpoint `GET /api/v1/logs` avec filtres optionnels (entite, entiteId, auteur). Indexation stratégique sur `entite+entite_id`, `auteur` et `date_action`.
- **Tests validés** : CREATE log ✅, UPDATE log ✅, DELETE log ✅, auteur extrait JWT ✅, async ✅

---

## Statut Actuel — 2026-06-30

**Avancement : 8/16 modules livrés (50%)**

| Module | Statut |
|--------|--------|
| B-01 Infrastructure Docker | ✅ |
| B-02 Gestion Structurelle (back) | ✅ |
| B-03 Gestion Élèves (back) | ✅ |
| B-04 Auth JWT (back) | ✅ |
| F-01 Socle Layout | ✅ |
| F-02 Auth UI | ✅ |
| F-03 Gestion Structurelle (front) | ✅ |
| F-04 Gestion Élèves (front) | ✅ |
| B-05 ActionLog AOP | ✅ |
| B-06 Paiements + Webhooks | 🔲 |
| B-07 QR Code / Scan | 🔲 |
| B-08 Gestion Utilisateurs (back) | 🔲 |
| F-05 Dashboard Stats Réelles | 🔄 |
| F-06 Interface Paiements | 🔲 |
| F-07 Interface QR Scan | 🔲 |
| F-08 Gestion Utilisateurs (front) | 🔲 |

| B-05 ActionLog AOP | ✅ |

| B-06 Back-end Paiements & Webhooks | ✅ |

| B-07 Back-end QR Code / Scan | ✅ |

**Prochaine étape** : Back-end B-08 Gestion Utilisateurs ou Front-end (F-05 à F-08).

---

### [2026-06-30] - Amélioration P2 : Migrations Flyway versionnées
- **Statut :** Livré / Opérationnel
- **Fichiers Modifiés :**
  - `server-backend/pom.xml` — ajout `flyway-core` + `flyway-database-postgresql`
  - `server-backend/src/main/resources/application.yml` — `ddl-auto: update` → `validate`, bloc Flyway (`baseline-on-migrate: true`)
  - `server-backend/src/main/resources/db/migration/V1__init_schema.sql` — schéma complet (8 tables, index stratégiques, `CREATE IF NOT EXISTS`)
- **Description :** Remplacement du mécanisme fragile `ddl-auto: update` par des migrations SQL versionnées via Flyway. Le script `V1__init_schema.sql` crée les 8 tables dans l'ordre des FK (`utilisateurs` → `etablissements` → `niveaux` → `classes` → `eleves` → `transactions_paiement` / `passages_refectoire` / `action_logs`). Toutes les instructions utilisent `IF NOT EXISTS` pour rester idempotentes sur une base déjà existante. Le profil prod est maintenant fonctionnel : Flyway applique les migrations, Hibernate valide le schéma.
- **Tests validés :** `./mvnw compile` ✅ (exit 0)

---

### [2026-06-30] - Amélioration P1 : Tests unitaires JUnit 5 (23 tests)
- **Statut :** Livré / Opérationnel
- **Fichiers Créés :**
  - `server-backend/src/test/.../scan/service/ScanServiceTest.java` — 7 tests
  - `server-backend/src/test/.../auth/service/UtilisateurServiceTest.java` — 8 tests
  - `server-backend/src/test/.../eleve/service/EleveServiceTest.java` — 5 tests
  - `server-backend/src/test/.../paiement/service/WebhookServiceTest.java` — 3 tests
- **Description :** Première suite de tests automatisés du projet. Stratégie `@ExtendWith(MockitoExtension.class)` sans contexte Spring ni base de données, exécution en 1,4s. Cas critiques couverts : scan ACCORDÉ/REFUSÉ (statut + doublon du jour), protection dernier ADMIN (→ 409), webhook CinetPay accepté → élève AUTORISE, webhook refusé → élève inchangé, soft-delete, matricule dupliqué.
- **Tests validés :** `./mvnw test` → **23/23 ✅ BUILD SUCCESS**

---

### [2026-07-01] - Amélioration P3 : Déploiement production Vercel + Railway
- **Statut :** Livré / Opérationnel
- **Fichiers Créés/Modifiés :**
  - `client-frontend/src/services/apiClient.js` — `baseURL` via `import.meta.env.VITE_API_URL` (fallback `localhost:8081`)
  - `client-frontend/vercel.json` — build Vite + rewrites SPA React Router (`/(.*) → /index.html`)
  - `client-frontend/.env.example` — template variables d'environnement
  - `server-backend/Dockerfile` — multi-stage `eclipse-temurin:17-jdk-alpine` (build) → `eclipse-temurin:17-jre-alpine` (runtime)
  - `server-backend/railway.toml` — builder Dockerfile + healthcheck `/actuator/health`, timeout 120s
  - `server-backend/src/main/resources/application.yml` — `server.port: ${PORT:8081}` (Railway injecte `$PORT` dynamiquement)
- **Description :** Infrastructure de déploiement production opérationnelle. Frontend React/Vite déployé sur Vercel avec routing SPA. Backend Spring Boot containerisé via Dockerfile multi-stage déployé sur Railway avec PostgreSQL managée. 3 bugs corrigés en séquence : (1) port hardcodé → `${PORT:8081}`, (2) ENTRYPOINT shell expansion supprimée (Spring Boot lit `SPRING_PROFILES_ACTIVE` nativement), (3) `SPRING_DATASOURCE_URL` construite via variables atomiques PGHOST/PGPORT/PGDATABASE car Railway ne fournit pas de `JDBC_URL` (le `DATABASE_URL` natif est au format `postgresql://` incompatible avec HikariCP). Voir ADR-008 + ADR-009.
- **Architecture prod :** Frontend (Vercel) → Backend (Railway) → PostgreSQL (Railway managed)
- **Statuts finaux :** Vercel ✅ Online · Railway ✅ Online · PostgreSQL ✅ Online

---

### [2026-07-01] - Fix prod : CORS configurable via variable d'environnement
- **Statut :** Livré / Opérationnel
- **Commit :** `6fb3e2a`
- **Fichiers Modifiés :**
  - `server-backend/src/main/java/.../common/SecurityConfig.java` — `@Value("${cors.allowed-origins}")` + split virgule
  - `server-backend/src/main/resources/application.yml` — `cors.allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:5173}`
- **Description :** Les origines CORS étaient hardcodées à `localhost:5173`, bloquant les requêtes depuis le domaine Vercel. La configuration lit désormais `CORS_ALLOWED_ORIGINS` (variable d'environnement Railway), acceptant plusieurs domaines séparés par des virgules. Résolution du "Erreur réseau" sur la page de connexion après le premier déploiement.

---

### [2026-07-01] - Fix prod : Error Boundary — page blanche remplacée par message lisible
- **Statut :** Livré / Opérationnel
- **Commit :** `7980507`
- **Fichiers Créés :**
  - `client-frontend/src/components/ErrorBoundary.jsx` — composant classe React avec `getDerivedStateFromError`, bouton "Réessayer"
- **Fichiers Modifiés :**
  - `client-frontend/src/App.jsx` — toutes les routes wrappées dans `<ErrorBoundary>`
- **Description :** En React 19, toute erreur de rendu non capturée démontre la racine React entière (page blanche). L'ErrorBoundary intercepte les erreurs d'affichage par route et affiche un message d'erreur lisible avec bouton de rechargement, limitant l'impact à la route fautive.

---

### [2026-07-01] - Fix prod : MUI v9 — Autocomplete paiements (params.slotProps)
- **Statut :** Livré / Opérationnel
- **Commit :** `d55f242`
- **Fichiers Modifiés :**
  - `client-frontend/src/pages/paiements/PaiementsPage.jsx` — `params.InputProps` → `params.slotProps?.input`
  - `client-frontend/src/hooks/usePaiements.js` — `setData(result ?? { content: [], totalElements: 0 })` (null safety)
- **Description :** MUI v9 a supprimé `params.InputProps` du callback `renderInput` de l'Autocomplete. L'accès à `params.InputProps.endAdornment` lançait une TypeError qui, sans Error Boundary, causait une page blanche complète sur `/paiements`. Migré vers `params.slotProps?.input` conformément à l'API MUI v9.

---

### [2026-07-01] - Feat : Scanner caméra QR Code + page Configuration admin
- **Statut :** Livré / Opérationnel
- **Commit :** `86583af`
- **Packages installés :** `html5-qrcode`
- **Fichiers Créés :**
  - `client-frontend/src/components/QrCameraScanner.jsx` — composant Html5Qrcode (caméra arrière prioritaire, debounce 3s, cleanup propre)
  - `client-frontend/src/services/configService.js` — lister, getParCle, modifier
  - `client-frontend/src/hooks/useConfig.js` — `useConfigurations()`, `useConfigValeur(cle, default)`
  - `client-frontend/src/pages/configuration/ConfigurationPage.jsx` — Switch toggle par fonctionnalité (ADMIN only)
  - `server-backend/src/main/java/.../parametrage/` — entité Configuration, DTO, repository, service, controller (GET + PUT `@PreAuthorize ADMIN`)
  - `server-backend/src/main/resources/db/migration/V2__add_configurations_table.sql` — table `configurations` + seed `SCAN_CAMERA_ENABLED=false`
- **Fichiers Modifiés :**
  - `client-frontend/src/pages/scan/ScanPage.jsx` — bouton "Activer caméra" conditionnel (config `SCAN_CAMERA_ENABLED`), callback `handleCameraDetected`
  - `client-frontend/src/App.jsx` — route `/configuration` (AdminRoute)
  - `client-frontend/src/layouts/MainLayout.jsx` — item "Configuration" (TuneIcon, ADMIN only)
- **Description :** Scan par caméra smartphone/tablette comme alternative à la douchette USB. Activer/désactiver la fonctionnalité sans déploiement depuis la page `/configuration` (ADMIN). Le scanner utilise la caméra arrière par préférence, lance le scan automatiquement et intègre un debounce 3s pour éviter les doublons. La table `configurations` en base permet d'ajouter de futurs feature flags.

---

### [2026-07-01] - Feat : Historique des Passages — filtres multi-critères + export CSV
- **Statut :** Livré / Opérationnel
- **Commit :** `1440a2d`
- **Fichiers Créés :**
  - `server-backend/.../scan/repository/PassageSpecification.java` — Specification JPA Criteria (plage dates, établissement, résultat, recherche texte)
  - `client-frontend/src/hooks/usePassages.js` — hook pagination + filtres
  - `client-frontend/src/pages/passages/PassagesPage.jsx` — filtres, table paginée, compteurs, export CSV
- **Fichiers Modifiés :**
  - `server-backend/.../scan/repository/PassageRefectoireRepository.java` — `JpaSpecificationExecutor`
  - `server-backend/.../scan/service/ScanService.java` — `listerPassages()` étendu (dateDebut/dateFin/resultat/search) + délégation à la Specification
  - `server-backend/.../scan/controller/ScanController.java` — nouveaux query params (dateDebut, dateFin, resultat, search)
  - `client-frontend/src/App.jsx` — route `/passages`
  - `client-frontend/src/layouts/MainLayout.jsx` — item "Historique" (HistoryIcon)
- **Description :** Page dédiée à la consultation de l'historique complet des passages réfectoire. Filtres cumulables : plage de dates (initialisée à aujourd'hui), établissement, résultat (ACCORDÉ/REFUSÉ), recherche par nom/prénom/matricule. Table paginée (10/25/50/100 lignes) avec colonnes date, heure, matricule, élève, classe, établissement, résultat, motif. Compteurs temps réel (total / accordés / refusés). Export CSV UTF-8 BOM compatible Excel. Rétro-compatible avec la vue daily de ScanPage (paramètre `date` conservé).

---

### [2026-07-01] - Fix : Passages 500 — double ORDER BY + JOIN FETCH Hibernate 6
- **Statut :** Livré / Opérationnel
- **Commit :** `a1dbe16`
- **Fichiers Modifiés :**
  - `server-backend/.../scan/repository/PassageRefectoireRepository.java` — suppression `JOIN FETCH` + `ORDER BY` hardcodé
  - `server-backend/src/main/resources/application.yml` — `default_batch_fetch_size: 50`, `max-page-size: 200`
- **Description :** Deux causes de 500 identifiées : (1) `JOIN FETCH` + Pageable en Hibernate 6 applique la pagination en mémoire (HHH90003004) et peut lever une exception ; (2) `ORDER BY p.heurePassage DESC` codé dans `@Query` + `sort=heurePassage,desc` dans l'URL Pageable générait un double `ORDER BY` invalide. Fix : suppression du `JOIN FETCH` et du `ORDER BY` dans le `@Query`, activation du batch fetch size (N+1 → N/50+1 queries), `max-page-size` porté à 200.

---

### [2026-07-01] - Fix : Passages 500 — migration @Query JPQL → JPA Specifications (Criteria API)
- **Statut :** Livré / Opérationnel
- **Commit :** `ac2dbdc`
- **Fichiers Créés :**
  - `server-backend/.../scan/repository/PassageSpecification.java`
- **Fichiers Modifiés :**
  - `server-backend/.../scan/repository/PassageRefectoireRepository.java` — `JpaSpecificationExecutor`
  - `server-backend/.../scan/service/ScanService.java` — `findAll(spec, pageable)`
- **Description :** Le `@Query` JPQL avec `(:resultat IS NULL OR p.resultat = :resultat)` continuait à échouer en 500 — Hibernate 6 ne résout pas fiablement les types null pour les enums dans les paramètres JPQL. Remplacement complet par JPA Criteria API : `PassageSpecification.withFilters()` ajoute chaque prédicat conditionnellement en Java, sans jamais passer de type null à Hibernate. Le sort Pageable est nativement résolu par Spring Data Criteria. Voir ADR-010.

---

### [2026-07-01] - Feat : Interface entièrement responsive (≤1200px)
- **Statut :** Livré / Opérationnel
- **Commit :** `e81f78e`
- **Fichiers Modifiés :**
  - `client-frontend/src/layouts/MainLayout.jsx` — Drawer temporaire sur mobile (< lg), permanent sur desktop (≥ lg) ; hamburger MenuIcon ; padding responsif xs/sm/md
  - `client-frontend/src/pages/scan/ScanPage.jsx` — layout colonne sur mobile, 2 colonnes sur md+ ; suppression hauteur fixe
  - `client-frontend/src/pages/paiements/PaiementsPage.jsx` — en-tête `flexWrap="wrap"` pour éviter le débordement sur petits écrans
- **Description :** L'application était inutilisable sur écrans ≤1200px : le Drawer permanent de 240px écrasait la zone de contenu principale. Threshold MUI `lg` (1200px) : en dessous le Drawer devient temporaire (overlay) et un bouton hamburger apparaît dans l'AppBar pour l'ouvrir ; la navigation ferme automatiquement le Drawer. Sur desktop (≥1200px) le comportement précédent (Drawer fixe latéral) est préservé.

---

### [2026-07-01] - Feat : Gestion Utilisateurs — Modification & Suppression définitive (ADMIN)
- **Statut :** Livré / Opérationnel
- **Commit :** `d6fa644`
- **Fichiers Créés :**
  - `server-backend/.../auth/dto/ModifierUtilisateurRequestDTO.java` — nom, prenom, email, nouveauMotDePasse (optionnel)
- **Fichiers Modifiés :**
  - `server-backend/.../auth/service/UtilisateurService.java` — `modifier()` (unicité email, changement mot de passe optionnel min 8 car.) + `supprimerDefinitivement()` (protection dernier ADMIN)
  - `server-backend/.../auth/controller/UtilisateurController.java` — `PUT /{id}` + `DELETE /{id}/permanent`
  - `client-frontend/src/services/utilisateurService.js` — `modifier()` PUT + `supprimer()` DELETE `/permanent`
  - `client-frontend/src/hooks/useUtilisateurs.js` — `modifier()` + `supprimer()` ajoutés
  - `client-frontend/src/pages/utilisateurs/UtilisateursPage.jsx` — `ModifierDialog` (formulaire pré-rempli + champ nouveau mdp optionnel), `ConfirmSupprimerDialog` (alerte irréversible), boutons Edit et DeleteForever sur chaque ligne
- **Description :** Complétion du CRUD utilisateurs pour les administrateurs. Modification : dialog pré-rempli permettant de changer nom, prénom, email et optionnellement le mot de passe (vide = conserver l'actuel). Suppression définitive : dialog de confirmation avec alerte "irréversible", protection systématique contre la suppression du dernier ADMIN (409 CONFLICT). Les boutons modifier et supprimer sont désactivés sur la propre ligne de l'utilisateur connecté.

---

### [2026-07-01] - Documentation : Manuel Utilisateur & Cahier de Recette avec captures d'écran réelles
- **Statut :** Livré / Opérationnel
- **Fichiers Créés :**
  - `documentations/manuel-utilisateur.html` + `.pdf` (1,3 Mo) — 12 sections, 13 captures d'écran réelles intégrées
  - `documentations/cahier-de-recette.html` + `.pdf` — 49 cas de test (AUTH, DASH, ETB, ELV, PAI, SCN, HIS, USR, CFG, THM)
  - `documentations/assets/01-login.png` à `16-dashboard-ivoirien.png` — 16 captures Playwright (1280×800)
  - `README.md` — réécriture complète avec stack, modules, setup local, sécurité, contact
- **Contexte résolu :**
  - Création manuelle de la table `configurations` (V2 Flyway) dans PostgreSQL Docker
  - Correction import Playwright (CommonJS → `import pkg from index.js`) + flag `--no-sandbox`
  - Résolution du bug Spring Security (InMemoryUserDetailsManager) via restart DevTools après création table manquante
- **Description :** Génération de documentation complète avec captures d'écran automatisées par Playwright. Le script capture 16 écrans : connexion, dashboard (3 thèmes), sélecteur thème, établissements, liste élèves, formulaire (3 onglets), paiements, scan réfectoire, historique, utilisateurs, configuration, À Propos. Le manuel utilisateur en PDF (1,3 Mo) intègre toutes les captures avec captions. Le cahier de recette couvre 49 cas de test fonctionnels.

---

### [2026-07-01] - Feat : Statistiques Globales — Dashboard enrichi
- **Statut :** Livré / Opérationnel
- **Fichiers Créés :**
  - `server-backend/.../dashboard/dto/DashboardStatsDTO.java` — record complet (établissements, élèves, passages, tendance 7 jours, paiements du mois)
  - `server-backend/.../dashboard/dto/JourPassageDTO.java` — projection jour : date, accordes, refuses
  - `server-backend/.../dashboard/service/DashboardService.java` — agrège toutes les stats en une seule transaction read-only
  - `server-backend/.../dashboard/controller/DashboardController.java` — `GET /api/v1/dashboard/stats`
- **Fichiers Modifiés :**
  - `server-backend/.../eleve/repository/EleveRepository.java` — `countByActifTrue()`, `countByStatutAccesAndActifTrue()`
  - `server-backend/.../etablissement/repository/EtablissementRepository.java` — `countByActifTrue()`
  - `server-backend/.../scan/repository/PassageRefectoireRepository.java` — `findTop5ByDatePassageOrderByHeurePassageDesc()`, `countByDateRangeGrouped()` (JPQL groupé)
  - `server-backend/.../paiement/repository/TransactionPaiementRepository.java` — `countByStatut()`, `statsAcceptesPeriode()` (COUNT + SUM du mois)
  - `client-frontend/src/services/dashboardService.js` — remplace 7 appels parallèles par un unique `GET /dashboard/stats`
  - `client-frontend/src/pages/DashboardPage.jsx` — 4 KPI cards enrichies (sous-info, FCFA), répartition statuts, panneau accès/paiements, graphique tendance 7 jours (barres MUI), table derniers passages
- **Description :** Remplacement des 7 appels API indépendants du frontend par un endpoint dédié côté backend (`DashboardService`) qui agrège toutes les données en une seule transaction. Enrichissements UI : sous-informations dans les KPI cards (accordés/refusés, nb transactions), panneau "Accès réfectoire aujourd'hui" avec barre de progression et taux d'accès %, panneau "Paiements du mois" avec montant FCFA formaté et compteur en attente, graphique en barres empilées pour la tendance des 7 derniers jours (vert accordés / rouge refusés) sans dépendance externe — 100% MUI.

---

### [2026-07-01] - Feat : Système de Thèmes & Design KLEM + Fenêtre À Propos
- **Statut :** Livré / Opérationnel
- **Fichiers Créés :**
  - `client-frontend/src/theme/themes.js` — 3 thèmes MUI : Corporatif (dark navy), Moderne (blanc/bling), École Ivoirienne (orange/vert)
  - `client-frontend/src/context/ThemeContext.jsx` — `ThemeModeProvider` + `useThemeMode()` avec persistance localStorage `klem-theme`
  - `client-frontend/src/components/ThemeSwitcher.jsx` — bouton palette + Popover 3 thèmes avec swatches et CheckIcon
  - `client-frontend/src/components/AProposDialog.jsx` — dialog ℹ️ avec version, coordonnées KLEM, copyright 2026
- **Fichiers Modifiés :**
  - `client-frontend/src/main.jsx` — pattern `ThemedApp` + `ThemeModeProvider` wrapper
  - `client-frontend/src/pages/auth/LoginPage.jsx` — redesign complet (gradient dynamique thème, logo 🍽️, KLEM Technologies & Services)
  - `client-frontend/src/layouts/MainLayout.jsx` — ThemeSwitcher dans AppBar, bouton À Propos en bas drawer
  - `client-frontend/package.json` — version `1.0.0-beta`
- **Description :** Identité visuelle KLEM (bleu #1565C0, orange #FF6D00) déclinée en 3 thèmes persistés en localStorage. Corporatif : dark mode marine profond, premium et sobre. Moderne : fond blanc, gradients colorés, effets lift-on-hover, animations cubic-bezier. École Ivoirienne : orange primaire, vert secondaire, fond ivoire. Tous les composants MUI (boutons, Drawer, AppBar, TableCell, Tabs, Chips) respectent les couleurs primary/secondary du thème actif. Fenêtre À Propos avec téléphone +225 07 58 89 24 77, site www.klemtech.net, email infos@klemtech.net.

---

### [2026-07-01] - Feat : RBAC, Comptes Parents, Notifications, Crédits, Image Fond Login

- **Statut :** Livré / Opérationnel
- **Auteur :** Yacouba SYLLA / Claude Code

#### Backend

- **Fichiers Créés :**
  - `server-backend/.../parent/entity/Parent.java` — entité liant `Utilisateur` (rôle PARENT) à `Set<Eleve>` via ManyToMany
  - `server-backend/.../parent/repository/ParentRepository.java`
  - `server-backend/.../parent/dto/ParentRequestDTO.java`, `ParentResponseDTO.java`
  - `server-backend/.../parent/service/ParentService.java`
  - `server-backend/.../parent/controller/ParentController.java` — CRUD ADMIN + `GET /parents/moi` (PARENT)
  - `server-backend/.../notification/NotificationService.java` — envoi email async via `ObjectProvider<MailSender>`
  - `server-backend/src/main/resources/db/migration/V3__parents_solde_config.sql`

- **Fichiers Modifiés :**
  - `auth/entity/Role.java` — ajout rôle `PARENT`
  - `eleve/entity/Eleve.java` — ajout champ `solde DECIMAL(10,2)`
  - `scan/entity/MotifRefus.java` — ajout `SOLDE_INSUFFISANT`
  - `etablissement/controller/EtablissementController.java` — `@PreAuthorize("hasRole('ADMIN')")` sur POST/DELETE
  - `eleve/controller/EleveController.java` — `@PreAuthorize` sur POST/PUT/DELETE/PATCH-statut
  - `paiement/service/WebhookService.java` — crédit solde (mode CREDITS) + notification email parent
  - `scan/service/ScanService.java` — débit solde (mode CREDITS) + notification email passage
  - `common/SecurityConfig.java` — `FOND_ECRAN_LOGIN` config accessible sans auth
  - `pom.xml` — `spring-boot-starter-mail`
  - `application.yml` — config SMTP + `notification.from`

#### Frontend

- **Fichiers Créés :**
  - `client-frontend/src/pages/parents/ParentsPage.jsx` — CRUD parents (ADMIN)
  - `client-frontend/src/hooks/useParents.js`
  - `client-frontend/src/services/parentService.js`

- **Fichiers Modifiés :**
  - `pages/eleves/ElevesPage.jsx` — boutons Ajouter/Modifier/Supprimer cachés pour non-ADMIN
  - `pages/etablissements/EtablissementsPage.jsx` — bouton Ajouter caché pour non-ADMIN
  - `pages/configuration/ConfigurationPage.jsx` — nouvelles sections Fonctionnalités (toggles), Mode Paiement (select), Tarif repas, Image fond login
  - `pages/auth/LoginPage.jsx` — lecture `FOND_ECRAN_LOGIN` au montage → background-image dynamique
  - `layouts/MainLayout.jsx` — nav "Parents" (FamilyRestroomIcon, ADMIN uniquement), ROLE_LABELS + PARENT
  - `App.jsx` — route `/parents` wrappée dans `<AdminRoute>`

#### Base de données (V3 migration)

- `ALTER TABLE eleves ADD COLUMN solde DECIMAL(10,2) DEFAULT 0.00`
- `CREATE TABLE parents (id, utilisateur_id unique FK)`
- `CREATE TABLE parents_eleves (parent_id, eleve_id PK)`
- 5 nouvelles clés : `NOTIFICATIONS_EMAIL_ENABLED`, `NOTIFICATIONS_SMS_ENABLED`, `MODE_PAIEMENT`, `TARIF_REPAS`, `FOND_ECRAN_LOGIN`

- **Description :** 6 améliorations architecturales majeures. (1) RBAC : seul l'ADMIN peut créer/modifier/supprimer établissements, classes et élèves — les autres rôles sont en lecture seule. (2) Comptes parents : nouveau rôle PARENT lié à des élèves via ManyToMany ; CRUD ADMIN dans `/parents`. (3+4) Notifications : emails asynchrones aux parents via Spring Mail (optionnel, `ObjectProvider<MailSender>` pour tolérance gracieuse si SMTP non configuré). (5) Mode crédits : solde sur l'élève, crédité au paiement et débité au passage cantine (configurable via `MODE_PAIEMENT`). (6) Image de fond : URL configurée dans le panneau d'administration, appliquée dynamiquement sur la page de connexion.
- **Tests validés :** 23 tests unitaires ✅ (`./mvnw test`)

---

### [2026-07-01] - Feat CRUD : Modifier & Supprimer les Établissements, Classes, Niveaux, Paiements et Passages (ADMIN)
- **Statut :** Livré / Opérationnel
- **Commit :** `fb39a04`
- **Fichiers Créés :**
  - `server-backend/.../paiement/dto/ModifierPaiementRequestDTO.java` — statut, montant, operateur, telephonePayeur
  - `server-backend/.../scan/dto/ModifierPassageRequestDTO.java` — resultat, motifRefus
- **Fichiers Modifiés (Backend) :**
  - `etablissement/service/EtablissementService.java` — `modifier()`, `supprimer()` (soft), `modifierNiveau()`, `modifierClasse()` avec `@Traceable` + `@Transactional`
  - `etablissement/controller/EtablissementController.java` — `PUT /{id}`, `DELETE /{id}`, `PUT /niveaux/{id}`, `PUT /classes/{id}`, tous `@PreAuthorize("hasRole('ADMIN')")`
  - `paiement/service/PaiementService.java` — `modifier()`, `supprimer()` avec `@Traceable`
  - `paiement/controller/PaiementController.java` — `PUT /{id}`, `DELETE /{id}`
  - `scan/entity/PassageRefectoire.java` — ajout `@Setter` Lombok
  - `scan/service/ScanService.java` — `modifierPassage()`, `supprimerPassage()` avec `@Traceable`
  - `scan/controller/ScanController.java` — `PUT /passages/{id}`, `DELETE /passages/{id}`
- **Fichiers Modifiés (Frontend) :**
  - `services/etablissementService.js` — `modifier`, `supprimer`, `modifierClasse`, `modifierNiveau`
  - `hooks/useEtablissements.js` — `modifier`, `supprimer` (mise à jour état local optimiste)
  - `pages/etablissements/EtablissementsPage.jsx` — boutons Edit/Delete sur cartes + dialog confirmation suppression
  - `pages/etablissements/GestionStructureDialog.jsx` — édition inline sur lignes Classe et bannières Niveau
  - `services/paiementService.js` — `modifier`, `supprimer`
  - `hooks/usePaiements.js` — `modifier`, `supprimer`
  - `pages/paiements/PaiementsPage.jsx` — colonne Actions (ADMIN) + ModifierDialog (select statut/opérateur, montant, téléphone)
  - `services/scanService.js` — `modifierPassage`, `supprimerPassage`
  - `hooks/usePassages.js` — `modifier`, `supprimer`
  - `pages/passages/PassagesPage.jsx` — colonne Actions (ADMIN) + ModifierDialog (résultat + motifRefus) + dialog confirmation
- **Description :** CRUD complet sur les 5 entités modifiables. Pattern uniforme : `@Traceable` AOP sur chaque méthode d'écriture, `@PreAuthorize("hasRole('ADMIN')")` sur chaque endpoint, mise à jour optimiste du state React (pas de rechargement réseau inutile). Établissements : suppression logique (`actif = false`). Passages : le champ `motifRefus` peut être mis à null pour effacer un motif erroné. Élèves : déjà entièrement implémenté depuis la session précédente.
- **Tests validés :** `./mvnw clean package -DskipTests` ✅ · `npm run build` ✅ · Déploiement Railway ✅

---

### [2026-07-01] - Feat Parents : Sélection assistée par Autocomplete (compte parent + élèves) + Filtre rôle
- **Statut :** Livré / Opérationnel
- **Fichiers Modifiés (Backend) :**
  - `auth/controller/UtilisateurController.java` — `GET /utilisateurs` accepte un paramètre optionnel `role`
  - `auth/service/UtilisateurService.java` — `lister(Role role, Pageable pageable)` filtre via le repository si `role` est fourni
  - `auth/repository/UtilisateurRepository.java` — nouvelle requête dérivée `findByRoleAndActifTrue(Role, Pageable)`
- **Fichiers Modifiés (Frontend) :**
  - `pages/parents/ParentsPage.jsx` — remplace les champs texte d'ID bruts par des `Autocomplete` MUI : sélection du compte PARENT (liste préchargée via `utilisateurService.lister({ role: 'PARENT' })`) et recherche multi-select d'élèves avec debounce 300ms (`eleveService.lister({ search })`) ; remplace `window.confirm` par un `Dialog` de confirmation de suppression
- **Description :** Élimine la saisie manuelle d'identifiants numériques pour lier un compte parent à ses élèves, source d'erreurs pour les gestionnaires. Le filtre `role` sur `GET /utilisateurs` permet de ne présenter que les comptes PARENT dans le sélecteur. La recherche d'élèves réutilise l'endpoint existant `GET /eleves?search=`.
- **Tests validés :** `./mvnw -q compile` ✅ · `npm run build` ✅ · Régression lint vérifiée (3 nouveaux avertissements `react-hooks/set-state-in-effect`, cohérents avec le pattern déjà présent 24 fois ailleurs dans le code, non bloquants)

---

### [2026-07-01] - Fix Utilisateurs : rôle PARENT manquant à la création
- **Statut :** Livré / Opérationnel
- **Fichiers Modifiés (Frontend) :**
  - `pages/utilisateurs/UtilisateursPage.jsx` — ajout de `PARENT` dans `ROLES` et `ROLE_CONFIG` (dialog de création + sélecteur de rôle inline)
- **Description :** Le rôle `PARENT` existait déjà côté backend (`Role.java`) et n'était bloqué par aucune validation de `creer()`, mais la liste `ROLES` du frontend ne proposait que `ADMIN`, `GESTIONNAIRE`, `CAISSIER`. Résultat : impossible de créer un compte PARENT depuis la gestion des utilisateurs, donc impossible de l'associer ensuite à des élèves sur la page Parents. Complète la fonctionnalité livrée dans la session précédente (sélection assistée par Autocomplete).
- **Tests validés :** `npm run build` ✅

---

### [2026-07-01] - Feat Rôle PARENT : périmètre restreint (paiements et historique propres, accès masqué)
- **Statut :** Livré / Opérationnel
- **Bug critique découvert et corrigé :** la contrainte `CHECK` PostgreSQL héritée du schéma initial (`utilisateurs_role_check`) n'autorisait que `ADMIN`/`GESTIONNAIRE`/`CAISSIER` — la migration V3 (comptes parents) avait ajouté le rôle `PARENT` côté application sans jamais mettre à jour cette contrainte. Toute tentative de création d'un compte PARENT échouait donc silencieusement au niveau base de données (violation de contrainte), y compris après le fix frontend de la session précédente. Nouvelle migration `V4__fix_utilisateurs_role_check_add_parent.sql` corrigeant la contrainte.
- **Fichiers Créés :**
  - `server-backend/.../db/migration/V4__fix_utilisateurs_role_check_add_parent.sql`
  - `client-frontend/src/components/StaffRoute.jsx` — garde de route bloquant le rôle PARENT (redirection `/dashboard`)
- **Fichiers Modifiés (Backend) :**
  - `parent/repository/ParentRepository.java` — `findEnfantIdsByUtilisateurId()`
  - `paiement/repository/TransactionPaiementRepository.java` — `findAllWithFiltersForEleves()` (restriction par liste d'élèves)
  - `paiement/service/PaiementService.java` — `initierPaiement`, `lister`, `getById` prennent désormais l'`Utilisateur` connecté ; si rôle PARENT, restriction stricte aux enfants du parent (403 `AccessDeniedException` si élève non possédé, 404 si accès à la transaction d'un tiers)
  - `paiement/controller/PaiementController.java` — injection du principal via `@AuthenticationPrincipal`
  - `scan/repository/PassageSpecification.java` — filtre optionnel `eleveIdsRestriction`
  - `scan/service/ScanService.java` — `listerPassages` restreint aux enfants si PARENT
  - `scan/controller/ScanController.java` — `scanner()` et `cache()` bloqués pour PARENT (`@PreAuthorize("!hasRole('PARENT')")`)
  - `eleve/controller/EleveController.java`, `etablissement/controller/EtablissementController.java` — GET (liste + détail) bloqués pour PARENT
- **Fichiers Modifiés (Frontend) :**
  - `layouts/MainLayout.jsx` — items de nav Établissements/Élèves/Scan Réfectoire réservés à `['ADMIN','GESTIONNAIRE','CAISSIER']`
  - `App.jsx` — routes `/etablissements`, `/eleves`, `/scan` enveloppées dans `StaffRoute`
  - `services/parentService.js` — `getMoi()`
  - `hooks/useEtablissements.js` — paramètre `enabled` pour ne pas appeler l'endpoint bloqué quand PARENT
  - `pages/passages/PassagesPage.jsx` — filtre Établissement masqué pour PARENT ; fix bonus : `isAdmin` utilisait `user?.roles?.includes('ROLE_ADMIN')` (toujours faux) au lieu de `user?.role === 'ADMIN'`, ce qui masquait les actions Modifier/Supprimer à l'ADMIN
  - `pages/paiements/PaiementsPage.jsx` — dialogue d'initiation : pour PARENT, sélecteur d'élève limité à ses propres enfants (`parentService.getMoi()`) au lieu de la recherche libre sur `/eleves`
- **Description :** Le parent connecté ne voit et n'initie que ses propres paiements, ne consulte que l'historique de passage de ses propres enfants, et n'a plus accès aux fonctionnalités Établissements / Élèves / Scan Réfectoire (masquées côté nav+routes, bloquées côté API). Toutes les restrictions sont appliquées côté serveur (pas seulement UI) : un parent qui forge une requête directe reste bloqué.
- **Tests validés :** `./mvnw -q compile` ✅ · `./mvnw test` (23/23) ✅ · `npm run build` ✅ · lint sans régression (31 problèmes pré-existants, aucun nouveau) · vérification manuelle bout-en-bout via API réelle (compte PARENT de test créé/lié/supprimé en DB dev) : `/eleves`, `/etablissements`, `/scan/cache` → 403 ; `/parents/moi` → 200 ; `/paiements` et `/passages` → restreints aux enfants du parent ; `initier` paiement pour un élève non possédé → 403 ; `getById` sur une transaction d'un tiers → 404

---

### [2026-07-01] - Feat Téléphone obligatoire (SMS parents) + Recherche parent/élève + Export CSV Élèves & Paiements
- **Statut :** Livré / Opérationnel
- **Commits :** `bdbab80`, `7b4269a`
- **Fichiers Créés :**
  - `server-backend/.../db/migration/V5__add_telephone_utilisateurs.sql` — colonne `telephone` NOT NULL + UNIQUE, backfill des comptes existants avec un placeholder `A-COMPLETER-<id>` à corriger par un ADMIN
- **Fichiers Modifiés (Backend) :**
  - `auth/entity/Utilisateur.java` — champ `telephone` (unique, non null)
  - `auth/dto/CreerUtilisateurRequestDTO.java`, `ModifierUtilisateurRequestDTO.java` — `telephone` obligatoire (regex format), `UtilisateurResponseDTO` l'expose
  - `auth/repository/UtilisateurRepository.java` — `existsByTelephone`, `findByRoleAndActifTrueWithSearch`, `findAllWithSearch` (recherche nom/prénom/téléphone)
  - `auth/service/UtilisateurService.java` — unicité téléphone vérifiée à la création et à la modification ; `lister()` accepte un paramètre `search`
  - `auth/controller/UtilisateurController.java` — `GET /utilisateurs?search=`
  - `paiement/repository/TransactionPaiementRepository.java`, `paiement/service/PaiementService.java`, `paiement/controller/PaiementController.java` — paramètre `search` (nom/prénom/matricule élève) sur `GET /paiements`, y compris sur le chemin restreint PARENT
- **Fichiers Modifiés (Frontend) :**
  - `pages/utilisateurs/UtilisateursPage.jsx` — champ Téléphone obligatoire (création + modification), colonne dans le tableau
  - `pages/parents/ParentsPage.jsx` — sélection du compte parent : recherche serveur avec debounce 300ms par numéro ou nom/prénom (au lieu du préchargement de 100 comptes)
  - `pages/eleves/ElevesPage.jsx` — en-tête restylée (icône + titre, bouton CSV, actualiser) à l'identique de l'Historique des Passages ; export CSV de la page courante
  - `pages/paiements/PaiementsPage.jsx` — ajout d'un champ de recherche élève, en-tête restylée, filtres regroupés dans un encart, export CSV de la page courante
- **Description :** Les parents seront notifiés par SMS — chaque compte utilisateur doit désormais avoir un numéro de cellulaire unique. La recherche de l'élève se faisait déjà par matricule/nom/prénom (page Parents) ; le compte parent se recherche maintenant de la même façon par numéro ou nom/prénom. Les pages Élèves et Paiements gagnent le même export CSV et la même présentation de filtres que l'Historique des Passages.
- **Tests validés :** `./mvnw -q compile` ✅ · `./mvnw test` (24/24) ✅ · `npm run build` ✅ · lint sans régression (30 problèmes, aucun nouveau) · vérification manuelle via API réelle (DB dev) : création sans téléphone → 400 ; création avec téléphone en double → 400 ; recherche utilisateurs par numéro et par nom → OK ; recherche paiements par nom d'élève → OK (0 résultat sur terme absent)

---

### [2026-07-01] - Reset comptes (un par rôle) + Suppression de l'indice de connexion par défaut
- **Statut :** Livré / Opérationnel — ⚠️ **Action destructive confirmée par l'utilisateur, appliquée en local ET en production (Railway)**
- **Commits :** `c4f3500`, `7816389`
- **Fichiers Créés :**
  - `server-backend/.../db/migration/V6__reset_comptes_un_par_role.sql` — vide `parents_eleves`, `parents`, `utilisateurs`, puis recrée exactement 4 comptes (un par rôle) : `admin@cantine.connect` / `admin@123` / ADMIN, `gestionnaire@cantine.connect` / `gestionnaire@123` / GESTIONNAIRE, `caissier@cantine.connect` / `caissier@123` / CAISSIER, `parent@cantine.connect` / `parent@123` / PARENT — numéros de cellulaire incrémentés depuis `0707388678` (unicité oblige, cf. V5)
- **Fichiers Supprimés :**
  - `auth/config/DataInitializer.java` — son garde `count()==0` ne se déclenchera plus jamais une fois la V6 appliquée (toujours 4 lignes après migration), et son mot de passe par défaut (`Admin123!`) était devenu obsolète face au nouveau `admin@123` : code mort à retirer plutôt qu'à laisser trompeur
- **Fichiers Modifiés (Frontend) :**
  - `pages/auth/LoginPage.jsx` — suppression de l'encart « Compte par défaut : admin@cantine.connect / Admin123! » affiché sous le formulaire de connexion
- **Description :** Nettoyage des comptes de test avant présentation du produit — un seul compte de référence par rôle avec des identifiants prévisibles, et suppression de l'affichage en clair des identifiants admin sur l'écran de connexion. **Important :** la migration V6 s'exécute automatiquement au prochain déploiement Railway (Flyway) — tous les comptes utilisateurs existants en production seront supprimés et remplacés par ces 4 comptes de test.
- **Tests validés :** `./mvnw -q compile` ✅ · `./mvnw test` (24/24) ✅ · `npm run build` ✅ · lint sans régression · vérification manuelle DB dev : migration V6 appliquée, les 4 comptes existent avec les bons rôles, connexion réussie (`/api/v1/auth/login`) pour les 4 comptes avec leurs identifiants respectifs

---

### [2026-07-01] - Actualisation de la Documentation de Gouvernance
- **Statut :** Livré / Opérationnel
- **Fichiers Créés :**
  - `collaboration/doc/manuel-utilisateur.md` — guide fonctionnel par rôle (ADMIN, GESTIONNAIRE, CAISSIER, PARENT), reflète les restrictions RBAC PARENT et les nouvelles fonctionnalités (téléphone, recherche, export CSV)
  - `collaboration/doc/cahier-de-recette.md` — scénarios de recette (UAT) orientés métier, complète les tables de tests techniques du ROADMAP
  - `collaboration/history/adr/2026-07-01-rbac-parent-restriction-serveur.md` (ADR-011)
  - `collaboration/history/adr/2026-07-01-migrations-source-unique-comptes-seed.md` (ADR-012)
- **Fichiers Modifiés :**
  - `collaboration/context/CONTEXT.md` — note technique reliant les 4 acteurs métier aux rôles applicatifs réels et à la restriction PARENT
  - `collaboration/history/decision-log.md` — entrées ADR-011 et ADR-012
  - `collaboration/RECAP-FINAL.md` — correction des faits obsolètes (identifiants admin, référence à `DataInitializer` supprimé, rôle `PARENT` absent de l'énumération) + section « Évolutions Post-Clôture »
  - `collaboration/ROADMAP.md` — ajout des modules B-09/F-09 (Parents) et B-10/F-10 (RBAC PARENT), section Améliorations Post-Livraison étendue (P6–P10), correction des identifiants et de la référence DataInitializer dans B-04
  - `README.md` — comptes de référence (un par rôle) à jour, module Parents ajouté au tableau, pointeurs vers le manuel utilisateur et le cahier de recette
- **Description :** Mise à jour de l'ensemble de la documentation de gouvernance suite à la clôture du chantier RBAC PARENT + téléphone obligatoire + recherche/export CSV + réinitialisation des comptes. Objectif : qu'aucun document de référence n'affiche des identifiants, un rôle ou un composant (`DataInitializer`) qui n'existent plus dans le code.
- **Tests validés :** Relecture croisée code ↔ documentation (identifiants, rôles, endpoints, composants mentionnés vérifiés contre l'état actuel du dépôt)

---

### [2026-07-01] - Incident Production : 500 sur Utilisateurs/Paiements/Dashboard (Correctif Urgent)
- **Statut :** Livré / Opérationnel — incident corrigé
- **Signalement :** Capture d'écran utilisateur — écran « Gestion des Utilisateurs » vide (« Aucun utilisateur »), bannière « Une erreur interne est survenue », console navigateur montrant des `500` sur `/utilisateurs`, `/paiements` et `/dashboard/stats`. Diagnostic initial erroné côté utilisateur (« tu n'as pas créé les utilisateurs ») — **les 4 comptes de la migration V6 existaient bel et bien en base**, seul l'endpoint de liste était cassé.
- **Cause racine :** Récidive du bug documenté dans l'ADR-007 (`ERROR: function lower(bytea) does not exist`) — les nouvelles requêtes de recherche `UtilisateurRepository`/`TransactionPaiementRepository` de la session précédente utilisaient le motif JPQL `(:param IS NULL OR LOWER(...) LIKE ...)` sans `CAST` explicite, alors que la règle avait déjà été établie et documentée. Deux bugs additionnels découverts en corrigeant : `TransactionPaiementRepository.statsAcceptesPeriode` provoquait un `ClassCastException` dans `DashboardService` (retour `Object[]` mal déclaré, bug pré-existant non lié à cette session) ; les nouvelles requêtes natives avec `ORDER BY` explicite entraient en conflit avec le tri automatique ajouté par Spring Data à partir du `Pageable` (`t.dateCreation` au lieu de `t.date_creation`).
- **Fichiers Créés :**
  - `collaboration/history/adr/2026-07-01-incident-jpql-null-bytea-paiements-utilisateurs.md` (ADR-013)
- **Fichiers Modifiés (Backend) :**
  - `auth/repository/UtilisateurRepository.java` — requêtes de recherche converties en `@Query(nativeQuery = true)` + `CAST`, suppression de `findByRoleAndActifTrue` devenue morte
  - `auth/service/UtilisateurService.java` — passe `role.name()` (String) au lieu de l'enum au repository natif
  - `paiement/repository/TransactionPaiementRepository.java` — même conversion native + CAST ; `statsAcceptesPeriode` retourne `List<Object[]>` au lieu d'`Object[]`
  - `paiement/service/PaiementService.java` — passe `statut.name()` ; construit un `Pageable` sans `Sort` avant d'appeler les requêtes natives (qui embarquent déjà leur `ORDER BY`)
  - `dashboard/service/DashboardService.java` — dépile `List<Object[]>` au lieu de caster directement
  - `common/GlobalExceptionHandler.java` — `handleGeneric` journalise désormais la stack trace complète (`log.error`) avant de renvoyer le 500 générique — auparavant aucune trace n'était loggée pour les erreurs 500 génériques
- **Fichiers Modifiés (Documentation) :**
  - `collaboration/history/decision-log.md` — entrée ADR-013
- **Description :** Incident critique en production : 3 endpoints centraux (Utilisateurs, Paiements, Dashboard) renvoyaient systématiquement 500 dès qu'ils étaient appelés sans filtre — c'est-à-dire au chargement normal de chaque page. Corrigé en suivant scrupuleusement le pattern déjà validé par l'ADR-007 (requête native + CAST) plutôt qu'en réinventant une solution. Leçon retenue : tout futur endpoint de recherche doit être testé manuellement dans son état par défaut (sans filtre), pas seulement avec un terme de recherche renseigné.
- **Tests validés :** Reproduction locale confirmée (même erreur `lower(bytea)` que production) → correctif appliqué → `./mvnw test` (24/24) ✅ → vérification manuelle contre PostgreSQL réel : `/utilisateurs` (avec et sans filtre) → 200 ; `/utilisateurs?role=PARENT&search=...` → 200 ; `/paiements` (sans filtre, avec recherche, avec statut, trié par date) → 200 ; `/dashboard/stats` → 200 ; `/eleves` (non-régression) → 200 ; restriction PARENT re-vérifiée bout-en-bout après modification de `PaiementService` (paiements limités aux enfants du parent)

---

### [2026-07-01] - Latence Production Anormale : Logging TRACE Hibernate Actif en Prod (Correctif)
- **Statut :** Livré / Opérationnel
- **Signalement :** L'utilisateur signale que la page Utilisateurs affichait les données puis les a vues disparaître après actualisation, et suppose un problème de requêtes non optimisées côté « chargement des données ».
- **Diagnostic :** Installation du CLI Railway (`npm install -g @railway/cli`, déjà authentifié via `~/.railway/config.json`) pour inspecter la production directement. Mesures `curl` avec détail des phases (DNS/connect/TLS/TTFB) : `/actuator/health` (endpoint sans logique métier) répondait en 4 à 24 secondes selon les tentatives, `/api/v1/dashboard/stats` jusqu'à 44 secondes. La variabilité et la lenteur d'un endpoint trivial ont écarté l'hypothèse d'une requête SQL mal optimisée. `railway logs --deployment` a révélé un flot continu de lignes `TRACE ... org.hibernate.orm.jdbc.bind : binding parameter (...)`, une par paramètre lié sur chaque requête SQL de chaque requête HTTP.
- **Cause racine :** `application.yml` active `org.hibernate.orm.jdbc.bind: TRACE` dans son bloc de configuration de base (hors profil), utile en développement local. Le profil `prod` ne surchargeait que `com.klem.cantine` et `org.hibernate.SQL` — jamais ce logger spécifique, qui restait donc actif à `TRACE` en production malgré `SPRING_PROFILES_ACTIVE=prod` (confirmé actif via `railway variables`). Le volume d'I/O de logging synchrone induit dégradait la latence de tous les endpoints de façon uniforme, y compris ceux sans lien avec les correctifs de la session précédente.
- **Fichiers Créés :**
  - `collaboration/history/adr/2026-07-01-fix-latence-production-trace-logging.md` (ADR-014)
- **Fichiers Modifiés :**
  - `server-backend/src/main/resources/application.yml` — ajout de `org.hibernate.orm.jdbc.bind: WARN` dans le bloc `logging.level` du profil `prod`
  - `collaboration/history/decision-log.md` — entrée ADR-014
- **Description :** Le ralentissement perçu par l'utilisateur n'était pas un problème de requêtes non optimisées mais une erreur de configuration de logging : une clé présente dans le bloc de base n'avait pas été explicitement surchargée dans le profil de production. Correctif limité à la configuration, sans impact sur la logique métier.
- **Tests validés :** `./mvnw test` (24/24) ✅ — changement de configuration pure. **Mesure de latence post-déploiement à confirmer.**

---

### [2026-07-01] - Latence Production (Suite) : Dépassement Mémoire du Conteneur Railway (Correctif)
- **Statut :** Livré / Opérationnel
- **Signalement :** L'utilisateur confirme le déploiement du correctif précédent mais observe toujours des données qui « disparaissent » à l'actualisation, et suppose de nouveau un problème de chargement lent.
- **Diagnostic :** Installation du CLI Railway (`npm install -g @railway/cli`, déjà authentifié) pour aller au-delà des logs et consulter les métriques réelles du conteneur : `railway metrics --since 30m --json`. Résultat : CPU quasiment inutilisé (0 % d'utilisation, max 0,73 vCPU sur une limite de 2 vCPU) mais **mémoire maximale à 1099,6 Mo dépassant la limite du conteneur (1024 Mo)**, avec des latences HTTP P50/P90/P95/P99 uniformément à ~13,9 secondes sur toute la fenêtre — signature d'une pression mémoire extrême (pagination/GC), pas d'un problème CPU ou de requêtes SQL. Confirmé que le correctif TRACE précédent (ADR-014) était nécessaire mais pas suffisant.
- **Cause racine :** Aucune borne mémoire explicite n'existait : le JVM tourne sans `-Xmx`/`-XX:MaxMetaspaceSize` (le Metaspace croît sans limite par défaut, en mémoire native hors segment heap), le pool Tomcat par défaut (200 threads × ~1 Mo de pile chacun) et le pool HikariCP (20 connexions) sont surdimensionnés pour le trafic réel d'un établissement pilote — l'ensemble pouvant faire dépasser au conteneur sa limite de ~1 Go.
- **Fichiers Créés :**
  - `collaboration/history/adr/2026-07-01-fix-memoire-conteneur-railway.md` (ADR-015)
- **Fichiers Modifiés :**
  - `server-backend/Dockerfile` — `ENTRYPOINT` avec `-XX:MaxRAMPercentage=60.0 -XX:MaxMetaspaceSize=192m -Xss512k` (conserve la forme tableau JSON requise par l'ADR-008)
  - `server-backend/src/main/resources/application.yml` (profil `prod`) — `hikari.maximum-pool-size` 20→10, `spring.jpa.open-in-view: false` (corrige aussi un avertissement présent depuis l'origine), `server.tomcat.threads.max: 50` / `min-spare: 5`
  - `collaboration/history/decision-log.md` — entrée ADR-015, complète l'ADR-014
- **Description :** Le vrai goulot d'étranglement n'était ni une requête non optimisée ni le logging seul, mais un dimensionnement mémoire du conteneur jamais borné explicitement, faisant dépasser au processus sa limite Railway (~1 Go). Correctif de configuration/infrastructure, sans impact sur la logique métier.
- **Tests validés :** `./mvnw test` (24/24) ✅ ; démarrage local réussi avec les nouveaux flags JVM. **Mesure `railway metrics` post-déploiement à confirmer** (mémoire max sous la limite, latences P50/P90/P95 sous la seconde).

---

### [2026-07-01] - Latence Production (Suite) : `-XX:MaxRAMPercentage` Insuffisant, Passage en Valeurs Absolues
- **Statut :** Livré / Opérationnel
- **Mesure post-déploiement (ADR-015, 1ère itération) :** `railway metrics --since <déploiement>` montre une nette amélioration de la latence (P50 = 17ms) mais la mémoire maximale (1168 Mo) dépasse toujours la limite du conteneur (1024 Mo) — `-XX:MaxRAMPercentage=60.0` dépend d'une détection correcte de la limite cgroup par le JVM, qui s'avère peu fiable sur ce conteneur Railway.
- **Fichiers Modifiés :**
  - `server-backend/Dockerfile` — remplacement de `-XX:MaxRAMPercentage=60.0` par des valeurs absolues : `-Xmx400m -Xms256m -XX:MaxMetaspaceSize=160m -Xss512k`
  - `collaboration/history/adr/2026-07-01-fix-memoire-conteneur-railway.md` (ADR-015) — mis à jour en place plutôt qu'une nouvelle ADR, la précédente n'ayant pas encore franchi sa case « à confirmer après déploiement »
- **Description :** Les valeurs absolues éliminent toute dépendance à la détection cgroup, plus prévisible que la variante en pourcentage sur ce conteneur.
- **Tests validés :** Démarrage local réussi avec les nouveaux flags. **Nouvelle mesure `railway metrics` après ce second déploiement à confirmer.**

---

### [2026-07-01] - Latence Production : Incident Clôturé — Confirmation Finale
- **Statut :** Livré / Opérationnel — incident résolu et confirmé
- **Mesure finale (`railway metrics --since 2026-07-01T22:04:22`) :**
  - Mémoire max : 436-508 Mo, contre une limite conteneur de 1024 Mo (~45-50 % d'utilisation) — auparavant 1099-1168 Mo, au-dessus de la limite.
  - Latence HTTP P50 : 12-21 ms — auparavant ~13 857 ms de façon uniforme sur tous les percentiles (P50=P90=P95=P99).
  - Un résidu P90/P95 (~2,2s) observé dans les premières minutes suivant chaque déploiement, cohérent avec un échauffement JIT/pool de connexions normal, sans rapport avec la cause initiale.
- **Fichiers Modifiés :**
  - `collaboration/history/adr/2026-07-01-fix-memoire-conteneur-railway.md` (ADR-015) — case de suivi finale cochée, conclusion ajoutée
- **Description :** Clôture de la chaîne d'incidents de latence de la journée (ADR-013 500 JPQL, ADR-014 logging TRACE, ADR-015 dépassement mémoire). Cause dominante confirmée et corrigée : dimensionnement mémoire JVM/Tomcat/HikariCP jamais borné explicitement pour un conteneur Railway à ~1 Go. Aucune mise à niveau de plan Railway nécessaire dans l'immédiat.
- **Tests validés :** `railway metrics` confirmé à deux reprises sur des fenêtres temporelles distinctes après le déploiement final ; `./mvnw test` (24/24) ✅ ; `npm run build` ✅.

---

### [2026-07-01] - Thème "Ivoire" : Renommage et Passage en Thème par Défaut
- **Statut :** Livré / Opérationnel
- **Commits :** `e16569a`, `6a0cef0`
- **Fichiers Modifiés :**
  - `client-frontend/src/theme/themes.js` — libellé du thème `ivoirien` renommé de « École Ivoirienne » à « Ivoire »
  - `client-frontend/src/context/ThemeContext.jsx` — `DEFAULT_THEME` passé de `'modern'` à `'ivoirien'`
- **Description :** Le nouveau thème par défaut ne s'applique qu'aux utilisateurs sans préférence déjà enregistrée dans `localStorage` (`klem-theme`) — ceux ayant déjà choisi un thème conservent leur choix.
- **Tests validés :** `npm run build` ✅.

---

### [2026-07-01] - Responsive : Formulaires Élèves/Paiements et Filtres Élèves/Paiements/Historique
- **Statut :** Livré / Opérationnel
- **Fichiers Modifiés :**
  - `pages/eleves/EleveFormDialog.jsx` — dialogue plein écran sur mobile (`useMediaQuery` + `fullScreen`), champs Nom/Prénom empilés verticalement sous `sm`, onglets `scrollable` avec boutons de défilement mobiles
  - `pages/eleves/ElevesPage.jsx` — filtres (Recherche/Établissement/Statut) empilés pleine largeur sous `sm` ; en-tête (titre + boutons) empilé verticalement sous `sm`
  - `pages/paiements/PaiementsPage.jsx` — dialogue « Initier un paiement » plein écran sur mobile ; filtres (recherche + statuts) empilés ; en-tête empilé verticalement
  - `pages/passages/PassagesPage.jsx` — filtres (dates, établissement, résultat, recherche) empilés pleine largeur sous `sm` ; en-tête empilé verticalement
- **Bug découvert et corrigé en cours de route :** sur `PaiementsPage`, le titre « Paiements Mobile Money » et le bouton « Initier un paiement » ne tenaient pas sur une seule ligne à 375px de large — le bouton débordait hors écran au lieu de passer à la ligne, malgré `flexWrap="wrap"` sur le conteneur. Corrigé en rendant la direction de la Stack d'en-tête responsive (`column` sous `sm`) plutôt que de compter uniquement sur le retour à la ligne flexbox.
- **Description :** Vérifié visuellement via Playwright (Chromium headless, viewports 375×812 et 1280×900) contre l'application réelle (dev server + backend local) — captures avant/après confirmant l'absence de régression desktop et la correction du débordement mobile.
- **Tests validés :** `./mvnw test` (24/24) ✅ · `npm run build` ✅ · lint sans régression (30 problèmes, identique à la référence) · vérification visuelle Playwright sur Élèves (formulaire + filtres), Paiements (dialogue + filtres), Historique des Passages (filtres) en mobile et desktop

---

### [2026-07-01] - Responsive (Suite) : Suppression du Défilement Horizontal sur les Tableaux Élèves/Paiements/Historique
- **Statut :** Livré / Opérationnel
- **Signalement :** Après le premier passage responsive, l'utilisateur signale qu'il faut toujours défiler horizontalement pour voir certains composants/champs sur les listes.
- **Diagnostic :** Vérification Playwright par mesure `scrollWidth` vs `clientWidth` du `TableContainer` à 375px : Élèves déjà correct après le formulaire/filtres du tour précédent, mais Paiements (418px vs 341px) et Historique des Passages (455px vs 341px) débordaient toujours — les colonnes secondaires n'avaient pas encore été traitées, seuls les filtres et dialogues l'avaient été.
- **Fichiers Modifiés :**
  - `pages/eleves/ElevesPage.jsx` — colonnes Matricule/Établissement/Classe masquées sous `sm`, repliées en sous-titre dans la cellule Nom/Prénom ; QR fusionné dans la colonne Actions (une colonne de moins)
  - `pages/paiements/PaiementsPage.jsx` — colonnes Date/Référence masquées sous `sm`, Opérateur/Téléphone masquées sous `md`, repliées en sous-titre dans la cellule Élève ; padding des cellules resserré sous `xs`
  - `pages/passages/PassagesPage.jsx` — colonnes Date/Heure/Matricule masquées sous `sm`, Classe/Établissement masquées sous `md`, Motif de refus replié en Chip sous l'icône Résultat sur mobile ; padding des cellules resserré sous `xs`
- **Description :** Les informations masquées ne sont pas perdues : elles réapparaissent en sous-titre compact dans la cellule principale (Nom/Prénom, Élève) sur mobile, et restent en colonnes normales à partir de `sm`/`md`. Le premier essai de resserrement (colonnes masquées seules) laissait encore la colonne Actions déborder de 77px sur Paiements/Historique — résolu en resserrant le padding des cellules (`px: 0.75` au lieu de la valeur par défaut) sous `xs`.
- **Tests validés :** Mesure Playwright confirmée à trois largeurs : 375px (mobile, aucun défilement sur les 3 tableaux, avec données réelles), 800px (tablette, colonnes `md` cachées comme attendu), 1280px (desktop, aucune régression — toutes les colonnes visibles comme avant) ; `npm run build` ✅ · lint sans régression (30 problèmes, identique à la référence).

---

### [2026-07-01] - Feat Scan Réfectoire : Rafraîchissement Automatique du Cache Hors-Ligne (Configurable)
- **Statut :** Livré / Opérationnel
- **Contexte :** Suite à une question de l'utilisateur sur les indicateurs « En ligne »/« Cache absent » de Scan Réfectoire, discussion sur l'opportunité de rafraîchir automatiquement le cache de secours (24h) à l'ouverture de la page plutôt que de compter sur un clic manuel — ce qui correspond à l'intention déjà documentée dans `CONTEXT.md` (« cache mis à jour à chaque connexion ») mais jamais implémentée. Accepté avec un interrupteur de configuration pour garder le contrôle.
- **Fichiers Créés :**
  - `server-backend/.../db/migration/V7__add_scan_cache_auto_refresh_config.sql` — clé `SCAN_CACHE_AUTO_REFRESH`, valeur par défaut `true`
- **Fichiers Modifiés :**
  - `pages/scan/ScanPage.jsx` — lit `SCAN_CACHE_AUTO_REFRESH` via `useConfigValeur` ; effet déclenchant `rafraichirCache()` une seule fois à l'ouverture de la page si activé et si en ligne (guardé par une `ref`, attend la fin du chargement de la config avant de décider)
  - `pages/configuration/ConfigurationPage.jsx` — nouveau toggle « Rafraîchissement automatique du cache hors-ligne » dans la catégorie « Scan & Accès »
  - `collaboration/doc/manuel-utilisateur.md` (+ `.docx` régénéré) — sections Scan Réfectoire et Configuration mises à jour
- **Description :** Le téléchargement manuel via le bouton ☁️⬇️ reste disponible dans tous les cas ; le nouveau réglage ne fait qu'automatiser le premier téléchargement de la session si une connexion est disponible, réduisant le risque d'oubli avant une coupure réseau.
- **Tests validés :** `./mvnw test` (24/24) ✅ · migration V7 appliquée et vérifiée (`GET /configurations/SCAN_CACHE_AUTO_REFRESH` → `valeur: "true"`) · `npm run build` ✅ · lint sans régression · vérification Playwright bout-en-bout : cache vidé + réglage activé → téléchargement automatique confirmé (« Cache : 2 élèves · à l'instant ») ; réglage désactivé via la page Configuration + cache vidé → reste « Cache absent » (pas de téléchargement automatique), confirmant que l'interrupteur fonctionne dans les deux sens.

---

### [2026-07-02] - Notifications de Succès sur les Formulaires Élèves/Utilisateurs/Parents
- **Statut :** Livré / Opérationnel
- **Contexte :** Le formulaire Paiements affichait déjà une confirmation de succès (alerte inline avec l'URL de paiement) ; demande d'un retour équivalent sur les formulaires Élève, Utilisateur et Parent, qui se fermaient silencieusement après un ajout/modification réussi.
- **Fichiers Créés :**
  - `components/SuccessSnackbar.jsx` — composant partagé (`Snackbar` + `Alert` MUI, `severity="success"`, auto-masqué après 4s) réutilisé sur les 3 pages plutôt que de dupliquer le pattern à chaque endroit
- **Fichiers Modifiés :**
  - `pages/eleves/ElevesPage.jsx` — message « Élève créé/modifié avec succès » après `creer`/`modifier`
  - `pages/utilisateurs/UtilisateursPage.jsx` — nouveaux handlers `handleCreerSuccess`/`handleModifierSuccess` encapsulant `creer`/`modifier` (les dialogues appelaient les fonctions du hook directement, sans point d'accroche pour le message) → « Compte utilisateur créé/modifié avec succès »
  - `pages/parents/ParentsPage.jsx` — « Compte parent créé avec succès » / « Enfants associés mis à jour avec succès »
- **Description :** Contrairement au formulaire Paiements (qui garde le dialogue ouvert pour afficher l'URL CinetPay), ces 3 formulaires n'ont rien de spécifique à afficher après succès — un Snackbar bref après fermeture du dialogue convient mieux qu'une alerte inline bloquante.
- **Tests validés :** `npm run build` ✅ · lint sans régression (30 problèmes, identique à la référence) · vérification Playwright bout-en-bout sur les 3 pages (création utilisateur, modification élève, création parent) — snackbar de succès confirmé visible à chaque fois par capture d'écran ; données de test nettoyées après vérification.

---

### [2026-07-02] - Responsive (Suite) : Suppression du Défilement Horizontal sur les Tableaux Utilisateurs/Parents
- **Statut :** Livré / Opérationnel
- **Signalement :** Détecté pendant la vérification mobile des notifications de succès — les tableaux Utilisateurs et Parents n'avaient pas encore reçu le traitement responsive appliqué à Élèves/Paiements/Historique, débordant toujours horizontalement à 375px.
- **Fichiers Modifiés :**
  - `pages/parents/ParentsPage.jsx` — en-tête rendu responsive (`direction={{ xs: 'column', sm: 'row' }}`) ; colonnes `#` et `Email` masquées sous `sm`, repliées en sous-titre dans la cellule Parent (`wordBreak: 'break-word'` pour éviter qu'un email sans espace n'élargisse la colonne) ; padding des cellules resserré sous `xs`
  - `pages/utilisateurs/UtilisateursPage.jsx` — en-tête rendu responsive ; colonnes Email/Téléphone/Créé le/Statut masquées (Email/Créé le/Statut sous `sm`, Téléphone sous `md`), repliées en sous-titre dans la cellule Nom/Prénom (email, téléphone, statut Actif/Inactif) avec `wordBreak: 'break-word'` ; Chip du Rôle limité en largeur (`maxWidth: 84`) avec troncature par ellipse sur mobile ; actions « Désactiver/Réactiver » et « Supprimer définitivement » consolidées dans un menu contextuel (kebab `MoreVertIcon`) visible uniquement sous `sm`, l'action « Modifier » restant une icône autonome toujours visible — desktop/tablette inchangés (actions toujours affichées en icônes séparées à partir de `sm`)
- **Description :** Le tableau Utilisateurs combine une colonne fonctionnelle (`Select` de rôle) et 3 actions par ligne, ce qui débordait encore après le simple masquage de colonnes (485px→428px vs 341px) : résolu en deux étapes supplémentaires — troncature du Chip de rôle, puis regroupement des deux actions les moins fréquentes dans un menu kebab mobile. Un dernier débordement de 29px a été tracé à une légende contenant une adresse email sans point de rupture, corrigé par `wordBreak: 'break-word'` (appliqué par précaution au même endroit sur Parents).
- **Tests validés :** Mesure Playwright `scrollWidth`/`clientWidth` du `TableContainer` : 375px → Utilisateurs 341=341, Parents 343=343 (aucun débordement) ; 1280px → Utilisateurs 990=990, Parents 992=992 (desktop inchangé) ; menu kebab vérifié fonctionnel (ouverture + 2 actions présentes) par capture d'écran ; `npm run build` ✅ · lint sans régression après suppression d'une variable `cfg` devenue inutilisée (0 problème introduit sur `UtilisateursPage.jsx`, 30 problèmes au total identique à la référence).

---

### [2026-07-02] - Fix Critique : Page Blanche sur Firefox à l'Ouverture de l'Application (Accès `localStorage` Bloqué)
- **Statut :** Livré / Opérationnel
- **Signalement :** L'utilisateur rapporte que l'application ne fonctionne pas sur Firefox (page blanche sur `/login`) alors qu'elle fonctionne sur Chrome, avec `Uncaught NS_ERROR_FAILURE` dans la console pointant vers `useState`.
- **Diagnostic :** `ThemeModeProvider` (fournisseur React le plus externe de l'arbre applicatif) appelait `localStorage.getItem` de façon synchrone et non protégée dans l'initialiseur de son `useState`, exécuté dès le tout premier rendu. Lorsque Firefox bloque l'accès au stockage local (Protection renforcée contre le pistage en mode strict, cookies/données de site bloqués pour le site, navigation privée stricte...), cet appel lève une `SecurityError`/`NS_ERROR_FAILURE` avant même le premier rendu, faisant planter l'application entière avant qu'aucun composant ne s'affiche. Le même risque existait de façon non protégée dans `AuthContext.jsx`, `apiClient.js` (intercepteur Axios exécuté à chaque requête) et partiellement dans `cacheOfflineService.js`.
- **Fichiers Créés :**
  - `services/safeStorage.js` — wrapper partagé (`getItem`/`setItem`/`removeItem`) encapsulant tous les accès à `localStorage` dans un `try/catch`, avec repli silencieux (`null`/`false`/no-op) plutôt qu'une exception non gérée
- **Fichiers Modifiés :**
  - `context/ThemeContext.jsx` — lecture/écriture du thème via `safeStorage` (repli sur le thème par défaut « Ivoire » si le stockage est inaccessible)
  - `context/AuthContext.jsx` — lecture/écriture/suppression du token et de l'utilisateur via `safeStorage` ; `JSON.parse` du profil également protégé par `try/catch`
  - `services/apiClient.js` — intercepteurs de requête/réponse Axios utilisent `safeStorage`
  - `services/cacheOfflineService.js` — `sauvegarder()`/`supprimer()` protégés au même titre que `charger()` (déjà protégé)
- **Description :** Si le stockage est bloqué, l'application se dégrade proprement plutôt que de planter : thème par défaut appliqué, connexion fonctionnelle pour la session en cours (le jeton reste simplement en mémoire, sans persistance après rechargement — l'utilisateur devra se reconnecter), cache hors-ligne du Scan Réfectoire simplement inopérant plutôt que fatal.
- **Tests validés :** Reproduction fidèle du bug via Playwright (Chromium) en interceptant `window.localStorage` pour lever une `SecurityError` sur chaque appel, simulant exactement les conditions de blocage de Firefox : avant le correctif, ce scénario aurait fait planter le rendu initial (cohérent avec la stack trace `useState` du rapport utilisateur) ; après le correctif, la page `/login` s'affiche normalement et le flux de connexion complet (`/login` → `/dashboard`) fonctionne sans aucune erreur JS, avec un jeton non persistant comme seule dégradation attendue ; `npm run build` ✅ · lint sans régression (29 problèmes, aucun nouveau).

---

### [2026-07-02] - Fix : Le Statut d'Accès d'un Élève Restait Figé Après Modification (Scan Toujours Refusé)
- **Statut :** Livré / Opérationnel
- **Signalement :** L'utilisateur rapporte que le champ « Statut cantine » (Statut d'accès) d'un élève reste toujours figé à « En attente de paiement » même après l'avoir changé et enregistré depuis le formulaire, et que le scan réfectoire continue d'être refusé même après passage du statut à « Autorisé ».
- **Diagnostic :** Le formulaire élève (onglet Cantine/Affectation) envoie `statutAcces` dans le payload du bouton « Enregistrer », qui appelle `PUT /eleves/{id}` (`EleveService.modifier`). Or `EleveRequestDTO` (backend) ne déclare pas ce champ et `EleveService.modifier()` ne le lit jamais — Jackson ignore silencieusement la propriété inconnue (pas d'erreur, juste un no-op), donc la valeur en base ne change jamais par cette voie. Un endpoint dédié existe bien (`PATCH /eleves/{id}/statut` → `EleveService.changerStatut`, autorisé ADMIN ou GESTIONNAIRE) et une fonction `eleveService.changerStatut()` était déjà écrite côté frontend, mais n'était appelée nulle part dans l'UI — le sélecteur du formulaire était donc purement décoratif. `ScanService.scanner()` ne vérifie que `Eleve.statutAcces` (jamais le statut de paiement `TransactionPaiement.statut` directement) : `EN_ATTENTE_PAIEMENT` → motif `STATUT_EN_ATTENTE_PAIEMENT`, `SUSPENDU` → motif `STATUT_SUSPENDU`. Le lien entre les deux statuts est uniquement applicatif (`WebhookService.appliquerResultat` force `statutAcces = AUTORISE` quand un webhook de paiement est accepté), sans FK ni jointure — un statut cantine peut donc aussi être changé manuellement, sans paiement associé.
- **Fichiers Modifiés :**
  - `client-frontend/src/hooks/useEleves.js` — ajout d'une fonction `changerStatut(id, statut)` exposée par le hook, appelant le `PATCH` dédié
  - `client-frontend/src/pages/eleves/ElevesPage.jsx` (`handleSuccess`) — après `modifier()`/`creer()`, appelle en plus `changerStatut()` si `statutAcces` diffère de la valeur existante (édition) ou du défaut `EN_ATTENTE_PAIEMENT` (création)
- **Description :** Aucun changement backend nécessaire — l'endpoint dédié existait déjà avec la bonne matrice de permissions (ADMIN ou GESTIONNAIRE), il manquait uniquement le câblage frontend.
- **Tests validés :** `./mvnw test` (`EleveServiceTest`, `ScanServiceTest`) ✅ sans modification ; vérification Playwright bout-en-bout en local : élève « DDD » passé de `Autorisé` → `Suspendu` via le formulaire → `PUT` puis `PATCH /eleves/1/statut?statut=SUSPENDU` confirmés en réseau → rechargement de la page confirme `Suspendu` persisté (avant le correctif, il serait resté à `Autorisé`) ; repassage à `Autorisé` puis scan du QR code réel (`POST /api/v1/scan/{qrCodeToken}`) → `200 OK`, `resultat: ACCORDE`, `motifRefus: null` (précédemment refusé) ; `npm run build` ✅ · lint sans régression sur les fichiers modifiés.

---

### [2026-07-02] - Fix : Scan Refusé « Solde Insuffisant » Après Confirmation Manuelle d'un Paiement (Mode CRÉDITS)
- **Statut :** Livré / Opérationnel
- **Signalement :** L'utilisateur rapporte qu'en production, un scan réfectoire est refusé avec le motif « SOLDE_INSUFFISANT » pour un élève alors qu'un paiement avait été marqué « Accepté ».
- **Diagnostic :** En mode `MODE_PAIEMENT=CREDITS`, `ScanService.scanner()` exige `eleve.solde >= TARIF_REPAS`, alimenté uniquement par `WebhookService.appliquerResultat()` lors de la réception d'un webhook CinetPay/PayDunya accepté (crédite le solde + passe l'élève `AUTORISE`). Or `PaiementController`/`PaiementService` expose aussi `PUT /paiements/{id}` (ADMIN), utilisé depuis la page Paiements pour confirmer manuellement une transaction (ex. paiement en espèces, ou webhook jamais reçu) en changeant son `statut` à `ACCEPTE` directement — `PaiementService.modifier()` ne faisait que sauvegarder les champs de la transaction, sans jamais appliquer les effets côté élève (ni crédit du solde, ni passage `AUTORISE`, ni notification). Un paiement confirmé manuellement restait donc invisible pour le contrôle d'accès au scan.
- **Fichiers Modifiés :**
  - `server-backend/.../paiement/service/WebhookService.java` — extraction de la logique « effets d'un paiement accepté » (statut élève `AUTORISE`, crédit du solde en mode CREDITS, notification) dans une méthode dédiée `appliquerPaiementAccepte(TransactionPaiement)`, réutilisable hors du flux webhook
  - `server-backend/.../paiement/service/PaiementService.java` — `modifier()` appelle désormais `webhookService.appliquerPaiementAccepte(...)` quand le `statut` transitionne vers `ACCEPTE` (uniquement si l'ancien statut n'était pas déjà `ACCEPTE`, pour éviter un double crédit sur une simple correction de montant/opérateur)
- **Fichiers Créés :**
  - `server-backend/.../paiement/service/PaiementServiceTest.java` — 3 cas : transition vers `ACCEPTE` déclenche `appliquerPaiementAccepte` ; déjà `ACCEPTE` (modification annexe) ne le redéclenche pas ; transition vers `REFUSE` ne le déclenche pas
- **Description :** La confirmation manuelle d'un paiement produit maintenant exactement les mêmes effets côté élève qu'un webhook accepté — aucun changement de comportement pour le flux webhook existant (couvert par `WebhookServiceTest`), la logique est simplement partagée plutôt que dupliquée.
- **Tests validés :** `./mvnw test` (27/27, dont les 3 nouveaux `PaiementServiceTest`) ✅ ; reproduction fidèle du bug en local (mode `CREDITS` forcé, élève solde 0 → scan refusé `SOLDE_INSUFFISANT` confirmé) puis correctif vérifié bout-en-bout : `POST /paiements/initier` (5000 XOF) → `PUT /paiements/{id}` `statut=ACCEPTE` → solde élève `0 → 5000` confirmé en base → scan `POST /api/v1/scan/{qrCodeToken}` → `200 OK`, `resultat: ACCORDE`, solde débité du tarif (`5000 → 4500`) ; données de test nettoyées après vérification (élève de test désactivé, `MODE_PAIEMENT` restauré à sa valeur d'origine).

---

### [2026-07-02] - Exposition du Solde Élève (Mode CRÉDITS) sur l'API et la Page Élèves
- **Statut :** Livré / Opérationnel
- **Contexte :** Suite au fix sur le crédit manuel du solde, question de l'utilisateur sur l'emplacement et la mise à jour du champ `solde` — constat que la colonne existe en base et est utilisée par `ScanService`/`WebhookService`, mais n'était exposée nulle part côté API ni UI (angle mort en mode CREDITS : aucun moyen de connaître le solde d'un élève sans requêter la base directement).
- **Fichiers Modifiés :**
  - `server-backend/.../eleve/dto/EleveResponseDTO.java` — ajout du champ `BigDecimal solde` (mappé depuis `Eleve.getSolde()`)
  - `client-frontend/src/pages/eleves/ElevesPage.jsx` — nouvelle colonne « Solde » (formatée en XOF, alignée à droite), visible uniquement quand `MODE_PAIEMENT=CREDITS` (lu via `useConfigValeur`, déjà hors de propos en mode ABONNEMENT) ; masquée sous `md` pour ne pas réintroduire de défilement horizontal sur mobile/tablette (les colonnes `colSpan` des lignes vide/chargement s'ajustent en conséquence) ; ajoutée à l'export CSV dans les mêmes conditions
- **Description :** Aucune migration nécessaire (la colonne `solde` existait déjà) ; changement purement additif côté DTO (aucun appel existant du record n'utilisait la construction positionnelle ailleurs que `from()`).
- **Tests validés :** `./mvnw test` (27/27, aucune régression) ; `npm run build` ✅ · lint sans régression sur `ElevesPage.jsx` ; vérification Playwright bout-en-bout : mode `CREDITS` avec élève à solde 12 500 XOF → colonne « Solde » visible et correctement formatée (« 12 500 XOF ») à 1280px, colonne absente et `TableContainer` sans défilement horizontal à 375px (343=343) ; repassage en mode `ABONNEMENT` → colonne totalement absente du tableau, confirmant le masquage conditionnel.

---

### [2026-07-02] - Règles de Gestion : Interdiction de Suppression des Établissements/Niveaux/Classes Liés
- **Statut :** Livré / Opérationnel
- **Contexte :** Demande explicite de l'utilisateur : un établissement lié à des niveaux/classes/élèves, une classe liée à des élèves, ne doivent pas pouvoir être supprimés. Audit du code existant : `EtablissementService.supprimer()` (soft-delete) ne vérifiait aucune association ; `supprimerNiveau()`/`supprimerClasse()` faisaient un hard-delete (`deleteById`) sans aucune vérification — en pratique, `Niveau.classes`/`Etablissement.niveaux` portent `cascade = CascadeType.ALL`, donc un niveau avec des classes vides aurait été supprimé en cascade JPA silencieusement, et un niveau/classe avec des élèves aurait fait remonter une violation de contrainte FK brute (500 non géré) au lieu d'un message clair.
- **Décision (validée avec l'utilisateur) :** Pour l'Élève, la suppression reste inchangée (soft-delete `actif=false`, déjà non-destructif — l'historique paiements/passages est préservé intact) ; bloquer explicitement cette action aurait rendu le bouton « Supprimer » quasi inutilisable dès qu'un élève a payé ou été scanné une seule fois.
- **Fichiers Modifiés :**
  - `server-backend/.../etablissement/repository/NiveauRepository.java` — `existsByEtablissementId`
  - `server-backend/.../etablissement/repository/ClasseRepository.java` — `existsByNiveauId`, `existsByNiveau_EtablissementId`
  - `server-backend/.../eleve/repository/EleveRepository.java` — `existsByEtablissementIdAndActifTrue`, `existsByClasseIdAndActifTrue`
  - `server-backend/.../etablissement/service/EtablissementService.java` — `supprimer()` refuse (`IllegalStateException` → 409 via le `GlobalExceptionHandler` existant) si niveaux, classes ou élèves actifs associés ; `supprimerNiveau()` refuse si classes associées ; `supprimerClasse()` refuse si élèves actifs associés
  - `client-frontend/.../etablissements/EtablissementsPage.jsx` — l'erreur de suppression était silencieusement avalée (`catch (e) { setDeleteTarget(null) }`, aucun retour utilisateur) ; affichage désormais de l'erreur dans le dialogue de confirmation ; texte du dialogue corrigé (ne prétend plus désactiver les classes en cascade)
  - `client-frontend/.../etablissements/GestionStructureDialog.jsx` — suppression d'un niveau ayant des classes : alerte immédiate côté client (au lieu d'un `confirm()` qui promettait une suppression en cascade des classes, jamais réellement effectuée)
- **Fichiers Créés :**
  - `server-backend/.../etablissement/service/EtablissementServiceTest.java` — 8 cas couvrant les 3 nouvelles règles de blocage et leurs 3 chemins de succès correspondants
- **Description :** Aucune migration nécessaire ; les vérifications utilisent des `exists` dérivés (comptage indexé, pas de chargement d'entités). Le code de statut 409 CONFLICT était déjà géré génériquement par le `GlobalExceptionHandler` (`IllegalStateException`), aucune nouvelle classe d'exception nécessaire.
- **Tests validés :** `./mvnw test` (35/35, dont les 8 nouveaux `EtablissementServiceTest`) ✅ ; `npm run build` ✅ · lint sans régression (erreurs préexistantes non liées confirmées via `git diff`) ; vérification bout-en-bout en local : suppression d'un établissement avec niveaux → `409` + message clair (confirmé aussi visuellement dans le dialogue Playwright, l'erreur qui était auparavant avalée s'affiche maintenant) ; suppression d'un niveau avec classes → `409` ; suppression d'une classe avec élève actif → `409` ; création d'un établissement/niveau/classe vides puis suppression dans l'ordre (classe → niveau → établissement) → `200` à chaque étape, confirmant que le chemin nominal n'est pas cassé.

---

### [2026-07-02] - Suppression Définitive d'un Élève Bloquée si Paiements/Passages Associés
- **Statut :** Livré / Opérationnel
- **Contexte :** Précision de l'utilisateur sur la règle élève, suite à la clarification précédente (désactivation libre conservée) : une vraie suppression définitive doit rester possible pour un élève sans historique, mais doit être bloquée s'il a des paiements liés. Cette capacité n'existait pas du tout — le seul endpoint existant (`DELETE /eleves/{id}`) fait une désactivation (soft-delete), jamais une suppression réelle. Reproduction du modèle déjà en place pour `Utilisateur` (`DELETE /{id}` = désactiver, `DELETE /{id}/permanent` = supprimer définitivement).
- **Décision (validée avec l'utilisateur) :** Ajout du endpoint de suppression définitive côté backend uniquement pour l'instant — pas de nouveau bouton sur la page Élèves (le bouton « Supprimer » actuel continue de désactiver, comportement inchangé).
- **Fichiers Modifiés :**
  - `server-backend/.../paiement/repository/TransactionPaiementRepository.java` — `existsByEleveId`
  - `server-backend/.../scan/repository/PassageRefectoireRepository.java` — `existsByEleveId`
  - `server-backend/.../eleve/service/EleveService.java` — nouvelle méthode `supprimerDefinitivement(id)` : `IllegalStateException` (409) si des paiements OU des passages réfectoire sont associés, sinon `eleveRepository.delete(eleve)` (suppression réelle, contrairement à `supprimer()` qui reste un soft-delete inchangé)
  - `server-backend/.../eleve/controller/EleveController.java` — `DELETE /api/v1/eleves/{id}/permanent` (ADMIN)
- **Fichiers Créés :**
  - 3 nouveaux cas dans `EleveServiceTest.java` : refus si paiements associés, refus si passages associés, succès (suppression réelle vérifiée) si aucun des deux
- **Description :** Le contrôle couvre aussi les passages réfectoire (pas seulement les paiements comme littéralement demandé) car `passages_refectoire.eleve_id` est une FK `NOT NULL` sans `ON DELETE CASCADE` — sans ce contrôle, la suppression définitive d'un élève scanné au moins une fois (cas quasi systématique en usage réel) aurait échoué avec une violation de contrainte brute (500) au lieu d'un message clair.
- **Tests validés :** `./mvnw test` (38/38, dont les 3 nouveaux cas) ✅ ; vérification bout-en-bout en local : élève réel avec paiements → `DELETE /eleves/1/permanent` → `409` « paiements associés » ; élève de test frais (aucun paiement/passage) → désactivation (`DELETE /eleves/{id}`) toujours `204` sans restriction, puis suppression définitive (`DELETE /eleves/{id}/permanent`) → `204`, ligne confirmée absente de la table `eleves` en base (suppression réelle, pas juste `actif=false`).

---

### [2026-07-02] - UI : Bouton « Supprimer Définitivement » sur la Page Élèves
- **Statut :** Livré / Opérationnel
- **Contexte :** Suite à l'ajout de l'endpoint backend `DELETE /eleves/{id}/permanent`, l'utilisateur demande d'exposer aussi l'action côté UI, en laissant le choix de conception au développeur. Reprise du modèle déjà en place sur la page Utilisateurs (icônes distinctes désactiver/supprimer + repli en menu kebab sur mobile) pour rester cohérent avec le reste de l'application.
- **Fichiers Modifiés :**
  - `client-frontend/src/services/eleveService.js` — `supprimerDefinitivement(id)`
  - `client-frontend/src/hooks/useEleves.js` — action `supprimerDefinitivement` exposée par le hook
  - `client-frontend/src/pages/eleves/ElevesPage.jsx` — nouvelle icône rouge « Supprimer définitivement » (`DeleteForeverIcon`) à côté de l'icône orange existante « Désactiver » (`DeleteIcon`, tooltip clarifié) ; nouveau `ConfirmSupprimerDefinitivementDialog` affichant l'erreur serveur inline (409 si paiements/passages associés) au lieu d'un `alert()` brut ; les deux actions se replient dans un menu kebab (`MoreVertIcon`) sous `sm` pour ne pas réintroduire de débordement horizontal
- **Description :** Comportement inchangé pour « Désactiver » (toujours libre) ; la nouvelle action est réservée aux ADMIN, cohérente avec la restriction déjà appliquée côté backend.
- **Tests validés :** `npm run build` ✅ · lint sans régression ; vérification Playwright bout-en-bout : élève avec paiements → dialogue affiche l'erreur serveur exacte ; élève neuf sans historique → suppression définitive réussie, ligne disparaît de la liste (confirmée absente en base) ; mobile 375px → aucun débordement (343=343), menu kebab affiche « Désactiver » et « Supprimer définitivement ».

---

### [2026-07-02] - Responsive : Page Configuration (Cartes Fonctionnalités/Paiements/Apparence)
- **Statut :** Livré / Opérationnel
- **Signalement :** Capture d'écran utilisateur montrant les cartes de la page Configuration (Mode d'accès cantine, Tarif par repas, Image de fond) débordant horizontalement, texte tronqué en plein mot, barre de défilement horizontale visible.
- **Diagnostic :** Les `TextField`/`Select` de ces cartes utilisaient des `minWidth` fixes en pixels (300 pour les champs texte, 360 pour le sélecteur de mode de paiement) au lieu de valeurs responsives. Un `minWidth` fixe sur un enfant flex l'empêche de rétrécir sous cette valeur, forçant toute la ligne (icône + contenu) à dépasser la largeur de l'écran sur mobile/tablette — les `Box` de contenu n'avaient pas non plus `minWidth: 0`, aggravant le problème (comportement par défaut des enfants flex qui refusent de rétrécir sous la taille de leur contenu).
- **Fichiers Modifiés :**
  - `client-frontend/src/pages/configuration/ConfigurationPage.jsx` — `ToggleRow`, `TextRow` et le bloc « Mode d'accès cantine » : `minWidth` fixes remplacés par des largeurs responsives (`width: { xs: '100%', sm: 300|360 }`) ; ajout de `minWidth: 0` sur les `Box` de contenu ; `Stack` interne (champ + bouton) empilée en colonne sous `sm` ; description `Typography` avec `wordBreak: 'break-word'` et `maxWidth` responsive ; aperçu de l'image de fond plafonné à `maxWidth: '100%'` pour ne pas déborder avec une image très large
- **Description :** Comportement desktop inchangé (vérifié par capture à 1280px) ; sur mobile, les champs passent en pleine largeur et le bouton « Enregistrer » se place sous le champ plutôt qu'à côté.
- **Tests validés :** `npm run build` ✅ · lint sans régression ; vérification Playwright à 375px/600px/1280px : `document.documentElement.scrollWidth === clientWidth` à chaque largeur (aucun débordement) ; reproduction avec une vraie URL d'image de fond (aperçu affiché) → toujours aucun débordement à 375px.

---

### [2026-07-03] - Feat Paiements : Filtres Date et Opérateur dans la Recherche

- **Statut :** Livré / Opérationnel
- **Contexte :** La page Paiements ne filtrait que par statut (chips) et recherche élève (nom/prénom/matricule) — pas de moyen de restreindre par plage de dates ou par opérateur Mobile Money, contrairement à l'Historique des Passages qui dispose déjà d'un filtre de dates. Filtres appliqués côté serveur (pas seulement sur la page courante) pour rester cohérents avec la pagination.
- **Fichiers Modifiés (Backend) :**
  - `paiement/repository/TransactionPaiementRepository.java` — `findAllWithFilters`/`findAllWithFiltersForEleves` étendues avec `operateur` (`CAST(:operateur AS varchar)`, même pattern ADR-007/010/013) et `dateDebut`/`dateFin` (`CAST(:param AS date)`, comparaison sur `t.date_creation` avec borne haute `+ INTERVAL '1 day'` pour inclure la journée de fin)
  - `paiement/service/PaiementService.java` — `lister()` accepte `OperateurMobileMoney operateur`, `LocalDate dateDebut`, `LocalDate dateFin`, transmis aux deux variantes de requête (chemin normal et chemin restreint PARENT)
  - `paiement/controller/PaiementController.java` — `GET /paiements` accepte les paramètres optionnels `operateur`, `dateDebut`, `dateFin` (`@DateTimeFormat(iso = DATE)`)
- **Fichiers Modifiés (Frontend) :**
  - `pages/paiements/PaiementsPage.jsx` — ajout de deux `TextField type="date"` (Date début/Date fin, mêmes largeurs responsives que Historique des Passages) et d'un `TextField select` Opérateur (réutilise la liste `OPERATEURS` déjà utilisée par le dialogue Initier/Modifier) dans le bandeau de filtres, avant les chips de statut
- **Description :** Aucune migration nécessaire (colonnes `operateur`/`date_creation` déjà indexées ou natives). Comportement identique au filtre existant : chaque changement remet `page` à 0.
- **Tests validés :** `./mvnw test` (38/38, aucune régression) ✅ · `npm run build` ✅ · lint sans régression (mêmes 4 problèmes pré-existants, aucun nouveau) ; vérification Playwright bout-en-bout (dev server + backend local) : sélection Opérateur → requête réseau `GET /paiements?operateur=ORANGE_MONEY...` confirmée ; ajout Date début → requête `...&dateDebut=2026-07-01...` confirmée ; combinaison des deux filtres → liste restreinte au résultat attendu.

---

### [2026-07-03] - Feat Utilisateurs/Parents : Zones de Recherche (Email, Rôle, Statut, Date de Création)

- **Statut :** Livré / Opérationnel
- **Contexte :** Ni la page Utilisateurs ni la page Parents ne disposaient d'un bandeau de recherche/filtre — contrairement aux pages Élèves/Paiements/Historique. Demande explicite : recherche sur Utilisateurs par email/rôle/statut/date de création, et sur Parents par email du compte parent.
- **Fichiers Modifiés (Backend) :**
  - `auth/repository/UtilisateurRepository.java` — fusion de `findAllWithSearch`/`findByRoleAndActifTrueWithSearch` en une seule requête native `findAllFiltered(role, actif, dateDebut, dateFin, search)` : chaque critère optionnel suit le pattern `CAST(:param AS ...) IS NULL OR ...` déjà établi (ADR-007/010/013), `search` couvre désormais aussi l'email (auparavant nom/prénom/téléphone uniquement)
  - `auth/service/UtilisateurService.java` — `lister()` prend `Boolean actif`, `LocalDate dateDebut/dateFin` en plus de `role`/`search`
  - `auth/controller/UtilisateurController.java` — `GET /utilisateurs` accepte `actif`, `dateDebut`, `dateFin`
  - `parent/repository/ParentRepository.java` — nouvelle requête JPQL `findAllWithDetailsBySearch(search)` (email du parent), `:search` toujours non-null côté service pour éviter le bug Hibernate 6 sur paramètre JPQL nullable (ADR-007) — pas besoin de passer en requête native ici, un seul filtre non-enum
  - `parent/service/ParentService.java` — `lister(search, pageable)` bascule entre les deux requêtes selon que `search` est renseigné
  - `parent/controller/ParentController.java` — `GET /parents` accepte `search`
- **Fichiers Modifiés (Frontend) :**
  - `hooks/useUtilisateurs.js`, `hooks/useParents.js` — acceptent désormais un objet `filtres` (même pattern que `usePaiements`/`usePassages`)
  - `pages/utilisateurs/UtilisateursPage.jsx` — bandeau de filtres (Recherche email/nom/prénom/téléphone, Rôle, Statut Actif/Inactif, Créé depuis/jusqu'au)
  - `pages/parents/ParentsPage.jsx` — champ « Recherche par email parent »
- **Effet de bord corrigé (régression évitée) :** le filtrage par rôle sur `GET /utilisateurs` masquait auparavant silencieusement les comptes inactifs (`u.actif = true` codé en dur dans l'ancienne requête). Le sélecteur de compte PARENT du formulaire de la page Parents (`utilisateurService.lister({ role: 'PARENT', ... })`) s'appuyait sur ce comportement implicite pour ne proposer que des comptes actifs — corrigé en passant désormais `actif: true` explicitement depuis `ParentsPage.jsx`, pour ne pas dépendre d'un effet de bord caché dans le repository.
- **Tests validés :** `./mvnw test` (38/38, aucune régression) ✅ · `npm run build` ✅ · lint sans régression (mêmes erreurs pré-existantes `react-hooks/set-state-in-effect`, aucune nouvelle) ; vérification Playwright bout-en-bout (dev server + backend local) : recherche email + rôle + statut + date cumulés → requêtes réseau `GET /utilisateurs?search=...&role=CAISSIER&actif=true&dateDebut=...` confirmées, résultat filtré correct ; recherche email parent avec et sans correspondance (compte de test créé/supprimé en DB dev) → liste filtrée correctement dans les deux cas.

---

### [2026-07-03] - Feat : Module Rapports (v1 exploratoire) — États Financiers/Statistiques, Paiements, Passages — Export PDF/Excel

- **Statut :** Livré / Opérationnel — première version explicitement exploratoire, destinée à recueillir des retours avant évolution avec le client
- **Contexte :** Demande d'un module de reporting réservé à GESTIONNAIRE/CAISSIER pour imprimer en PDF ou Excel des états financiers/statistiques, l'état des passages et l'état des paiements. Décisions de cadrage validées avec l'utilisateur : accès étendu à ADMIN (cohérent avec Paiements/Scan/Historique, déjà ouverts aux 3 rôles staff) ; génération 100% côté navigateur pour cette v1 (aucune nouvelle dépendance backend) — voir ADR-016.
- **Fichiers Créés :**
  - `client-frontend/src/hooks/useRapports.js` — récupère « toutes » les pages de `GET /paiements` et `GET /passages` sur la période choisie (garde-fou 50 pages / 10 000 lignes), calcule les agrégats (montant encaissé, compteurs par statut/résultat, taux d'accès) côté client
  - `client-frontend/src/services/rapportExportService.js` — export Excel via `exceljs` (3 feuilles : Résumé, Paiements, Passages)
  - `client-frontend/src/pages/rapports/RapportsPage.jsx` — filtres (dates + établissement), 3 onglets (Résumé / Paiements / Passages), bouton « Générer le rapport », export Excel global, impression/PDF par onglet via `window.print()` scoppé à une zone imprimable (`GlobalStyles` + classe `.print-area`, même principe que l'impression déjà utilisée pour les QR codes élèves)
  - `collaboration/history/adr/2026-07-03-module-rapports-generation-navigateur-exceljs.md` (ADR-016)
- **Fichiers Modifiés :**
  - `client-frontend/src/App.jsx` — route `/rapports` enveloppée dans `StaffRoute` (bloque uniquement PARENT, comme Établissements/Élèves/Scan)
  - `client-frontend/src/layouts/MainLayout.jsx` — item de navigation « Rapports » (`AssessmentIcon`, `STAFF_ROLES`)
  - `client-frontend/package.json` — ajout `exceljs` ; **`xlsx`/SheetJS explicitement écarté** (dernière version publiée sur npm — `0.18.5` — non patchée face à 2 vulnérabilités connues, correctifs SheetJS distribués uniquement via leur CDN propre, hors npm)
- **Description :** Aucune modification backend — les endpoints `GET /paiements`/`GET /passages` déjà sécurisés (restriction PARENT en place côté service) sont réutilisés tels quels ; le module est simplement bloqué au niveau route/menu pour PARENT. Les statistiques (montant encaissé, taux d'accès, compteurs par statut/résultat) sont recalculées côté client à partir des données récupérées plutôt que par une requête d'agrégation SQL dédiée — suffisant pour la volumétrie pilote.
- **Tests validés :** `npm run build` ✅ · lint sans nouvelle erreur sur les fichiers créés/modifiés ; vérification Playwright bout-en-bout (dev server + backend local) pour les 4 rôles : GESTIONNAIRE/CAISSIER/ADMIN → accès complet (génération, 3 onglets peuplés, `window.print()` déclenché, export Excel téléchargé et contenu vérifié — 3 feuilles, données exactes) ; PARENT → item de menu absent, navigation directe vers `/rapports` redirigée automatiquement vers `/dashboard`.

---

### [2026-07-03] - Docs : Ajout du Manuel Utilisateur Illustré (user-guide.docx) — Corrections de Rôles

- **Statut :** Livré / Opérationnel
- **Commit :** `c5564c4`
- **Contexte :** `collaboration/doc/user-guide.docx` (version illustrée du manuel, captures d'écran réelles intégrées, 14 modules) existait sur le poste de travail depuis une session précédente mais n'avait jamais été commité — détecté en untracked à la reprise de session. Vérifié contre `manuel-utilisateur.md` (source de vérité texte) avant tout commit.
- **Anomalies trouvées et corrigées dans le `.docx` (édition directe des runs XML via `python-docx`, LibreOffice headless indisponible pour un remplacement fiable multi-run) :**
  - Modules Établissements, Élèves, Historique des Passages : rôle CAISSIER manquant dans l'encart « Rôles concernés » (n'affichait qu'ADMIN · GESTIONNAIRE).
  - Module Parents : encart « Rôles concernés » et titre de section affichaient « GESTIONNAIRE » alors que la fonctionnalité est réservée à l'ADMIN (contradiction interne avec le corps du texte du même module, qui mentionne bien « l'administrateur »).
- **Fichiers Créés :**
  - `collaboration/doc/user-guide.docx`
- **Description :** Aucune modification du contenu au-delà des 4 corrections de rôles ci-dessus — mise en page, captures d'écran et structure conservées à l'identique.
- **Tests validés :** Extraction texte (LibreOffice headless `--convert-to txt`) et relecture croisée avec `manuel-utilisateur.md` avant et après correction ; vérification `python-docx` que les 4 tableaux de rôles concernés reflètent exactement la matrice CUD du `.md`.

---

### [2026-07-03] - Docs : Règle de Mise à Jour Obligatoire du Manuel (CLAUDE.md) + PDF Initial

- **Statut :** Livré / Opérationnel
- **Commit :** `667ce76`
- **Contexte :** Demande explicite de l'utilisateur : à chaque livraison de fonctionnalité visible utilisateur, `user-guide.docx` doit être mis à jour (texte + captures si l'IHM change) puis reconverti en PDF, les deux commités et poussés dans la même session.
- **Fichiers Modifiés :**
  - `CLAUDE.md` (cantine-connect) — nouvelle règle n°5 sous « Règles Métier & Contraintes Spécifiques au Projet », avec la commande de conversion (`soffice --headless --convert-to pdf ...`) et le rappel de vérifier la cohérence des rôles contre `manuel-utilisateur.md`/`decision-log.md` avant de committer.
- **Fichiers Créés :**
  - `collaboration/doc/user-guide.pdf` — pendant PDF initial du `.docx` déjà commité (n'existait pas encore).
- **Description :** Règle consignée dans `CLAUDE.md` (lu à chaque session via `/startup`) plutôt qu'en mémoire de session, pour qu'elle s'applique de façon durable à toute session future sur ce dépôt, humaine ou IA.

---

### [2026-07-03] - Docs : Réorganisation du Manuel Utilisateur selon l'Ordre du Menu Latéral

- **Statut :** Livré / Opérationnel
- **Commit :** `dfcf121`
- **Contexte :** Demande de l'utilisateur : faire correspondre l'ordre de la table des matières et des sections de `user-guide.docx` à l'ordre réel des items du menu latéral (`client-frontend/src/layouts/MainLayout.jsx`) : Dashboard, Établissements, Élèves, Paiements, Scan Réfectoire, Historique, **Rapports**, Utilisateurs, Parents, Configuration.
- **Diagnostic :** Seul le module Rapports était mal placé — en dernière position (§14) au lieu d'juste après Historique (§9), Rapports ayant été ajouté en fin de document lors de sa livraison plutôt qu'inséré à sa place logique dans le menu. Thèmes et À Propos (hors menu latéral, fonctionnalités globales de l'interface) restent à la fin, après Configuration, comme avant.
- **Méthode :** Édition structurelle directe de l'arbre XML (`document.xml` via `python-docx`/`lxml`) plutôt qu'une reconversion depuis le Markdown (qui aurait perdu les captures d'écran) : déplacement du bloc de 22 éléments du corps du document (titre + tableau rôles + texte + captures + sous-sections 14.1–14.4) juste après la section Historique, renumérotation en cascade des sections 9→14 impactées (titres et sous-titres, en préservant la mise en forme des runs), et déplacement du seul paragraphe de table des matières concerné (la numérotation « 1. », « 2. »… de la table des matières est un champ de liste Word auto-calculé, pas du texte figé — aucune renumérotation manuelle nécessaire à cet endroit).
- **Fichiers Modifiés :**
  - `collaboration/doc/user-guide.docx`
  - `collaboration/doc/user-guide.pdf` — reconverti à l'identique après réorganisation
- **Description :** Contenu et captures d'écran strictement inchangés, seule la position et la numérotation du module Rapports (et le décalage en cascade des sections suivantes) ont changé.
- **Tests validés :** Vérification `python-docx` avant/après : ordre de la table des matières et des titres `Heading 1`/`Heading 2` conforme à l'ordre cible ; nombre d'images intégrées identique (24 avant, 24 après) ; relecture de la numérotation en cascade (9→14) section par section.

---

### [2026-08-08] - Conformité additive à KLEM_MASTER_SYSTEM_DIRECTIVE.md : requestId + OpenAPI

- **Statut :** Livré / Opérationnel
- **Contexte :** Actualisation du workspace selon `KLEM_MASTER_SYSTEM_DIRECTIVE.md` v2.0 (§6 « format d'erreur homogène avec requestId » et « OpenAPI obligatoire »). Voir
  `klem-projects-repository/collaboration/history/adr/2026-08-08-adoption-directive-maitre-datasphere-perimetre.md`
  pour le périmètre exact : cette app garde son propre `CLAUDE.md` comme référence de premier
  niveau (Java 17, JWT par en-tête) — seuls des ajouts sans changement de comportement existant
  ont été faits ici, pas de migration d'architecture.
- **Fichiers Modifiés :**
  - `server-backend/src/main/java/com/klem/cantine/common/GlobalExceptionHandler.java` — ajout
    d'un champ `requestId` (UUID par requête) au record `ErrorResponse` et à la réponse de
    validation.
  - `server-backend/src/main/java/com/klem/cantine/common/SecurityConfig.java` — ajout de
    `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html` à la liste des routes publiques
    (nécessaire pour que la documentation OpenAPI ajoutée ci-dessous soit effectivement accessible).
  - `server-backend/pom.xml` — ajout de `springdoc-openapi-starter-webmvc-ui` 2.6.0.
- **Description :** Aucun comportement existant modifié. Vérifié : les 44 tests unitaires/service
  existants passent toujours (`./mvnw clean test`) ; démarrage réel avec PostgreSQL (profil
  `prod`, conteneur Docker jetable) confirmé opérationnel — `/actuator/health` → `UP`,
  `/v3/api-docs` → 200, `/swagger-ui.html` → 302 (redirection normale springdoc) vers
  `/swagger-ui/index.html`. Point relevé en cours de vérification (pré-existant, non introduit par
  ce changement) : `AuthController.login`/`changerMotDePasse` construisent leur propre réponse
  d'erreur `ApiResponse` en `catch (BadCredentialsException)` sans passer par
  `GlobalExceptionHandler` — ces deux routes ne portent donc pas encore `requestId`. Non corrigé
  dans cette passe (changement de flux d'authentification, hors périmètre « additif »), documenté
  ici pour visibilité.
- **Explicitement non fait (dette documentée, pas silencieuse) :** bump Java 17→21, migration vers
  OAuth2 Resource Server, ajout ArchUnit/Testcontainers, refactor de couches — nécessitent un cycle
  de non-régression dédié sur un système de paiement Mobile Money en production.

### [2026-08-18] - Contrat de paiement unifié multi-providers (Strategy Pattern) + intégration Orange Money

- **Statut :** Livré / Opérationnel (51/51 tests verts, dont 44 existants inchangés — non-régression vérifiée)
- **Fichiers Modifiés :**
  - Nouveaux : `server-backend/src/main/java/com/klem/cantine/paiement/strategy/{PaymentStrategy,
    dto/PaymentRequestDto,dto/PaymentResponseDto,dto/WebhookPayloadDto,enums/PaymentProviderType,
    enums/PaymentStatus,exception/PaymentProviderException,impl/OrangeMoneyPaymentStrategy,
    impl/package-info}.java`, `paiement/service/{PaymentStrategyFactory,CanteenPaymentService,
    CanteenPaymentServiceImpl}.java`, `paiement/controller/CanteenPaymentController.java`,
    tests `PaymentStrategyFactoryTest`/`CanteenPaymentServiceImplTest`.
  - Modifiés (additifs uniquement, aucune méthode existante touchée) :
    `paiement/provider/CinetPayProvider.java` (implémente en plus `PaymentStrategy`),
    `paiement/config/PaiementProperties.java` (bloc `orangeMoney`), `common/SecurityConfig.java`
    (route publique du nouveau webhook), `application.yml` (config Orange Money +
    `klem.payment.default-provider`), `pom.xml` (dépendance `spring-boot-starter-webflux`).
- **Description :** Passage de l'intégration paiement (jusqu'ici CinetPay/PayDunya de facto,
  sélection par config `PAIEMENT_PROVIDER_ACTIF`) à une architecture Strategy Pattern
  multi-providers dynamique : `PaymentStrategy` (initiation, webhook, statut, signature),
  `PaymentStrategyFactory` (résolution par `PaymentProviderType`), nouveau service façade
  `CanteenPaymentServiceImpl` exposé sous `/api/v2/canteen-payments/**` (coexiste avec
  `/api/v1/paiements` inchangé, même table `transactions_paiement`). `OrangeMoneyPaymentStrategy`
  est une intégration réelle (OAuth2 client_credentials, jeton mis en cache, appel Webpayment CI
  via `WebClient`) ; structure d'ajout de `MtnMoMoPaymentStrategy`/`WavePaymentStrategy` sans
  modification du code existant documentée dans `paiement.strategy.impl.package-info`. Détail des
  alternatives écartées et de la correction de portée (le mandat de refactor initial ciblait à tort
  `services/core-api`, service KLEM DataSphere sans rapport avec Cantine Connect) :
  `collaboration/history/adr/2026-08-18-payment-strategy-multi-providers-orange-money.md`.

### [2026-08-18] - Connexion parent par OTP (WhatsApp/SMS/Email) et gestion complète des enfants

- **Statut :** Livré / Opérationnel (66/66 tests backend verts, dont 55 existants inchangés)
- **Fichiers Modifiés :**
  - Backend, nouveaux : `server-backend/src/main/java/com/klem/cantine/parent/otp/{OtpStore,
    InMemoryOtpStore,dto/ParentOtpRequestDto,dto/ParentOtpVerifyDto,service/ParentOtpService,
    controller/ParentOtpController}.java`, `eleve/service/MatriculeGenerator.java`,
    `eleve/dto/ModifierEnfantRequestDTO.java`, `notification/WhatsAppNotificationSender.java`,
    `db/migration/V16__matricule_sequences.sql`, tests (`MatriculeGeneratorTest`,
    `InMemoryOtpStoreTest`, `ParentOtpServiceTest`, cas de propriété dans `EleveServiceTest`).
  - Backend, modifiés : `eleve/service/EleveService.java` (matricule auto-généré à la création,
    immuable en modification, + `modifierViaParent`/`desactiverViaParent` avec vérification de
    propriété), `eleve/dto/{EleveRequestDTO,AjoutEnfantRequestDTO}.java` (matricule retiré),
    `parent/controller/ParentController.java` (+`PUT`/`DELETE /moi/enfants/{id}`),
    `parent/dto/ParentResponseDTO.java` (champs étendus pour pré-remplir l'édition + filtre actif),
    `auth/repository/UtilisateurRepository.java` (+`findByTelephoneAndRoleAndActifTrue`),
    `notification/NotificationDispatcher.java` (canal WHATSAPP), `common/SecurityConfig.java`
    (route publique OTP), `application.yml` (config Twilio WhatsApp).
  - Frontend, nouveau : `client-frontend/src/pages/acces/ParentOtpAccessPage.jsx` (wizard 2 étapes).
  - Frontend, modifiés : `pages/moi/MesEnfantsPage.jsx` (matricule retiré du formulaire, actions
    Modifier/Désactiver/QR Code par carte), `pages/auth/LoginPage.jsx` (lien vers l'accès OTP),
    `App.jsx` (route `/acces-otp`), `services/{authService,parentService}.js`.
- **Description :** Le mandat demandait un accès parent par OTP donnant un accès direct sans
  validation admin — réintroduisait un risque de faux comptes déjà explicitement écarté par une
  décision antérieure (`decision-log.md`). Retenu à la place : l'OTP authentifie un compte PARENT
  déjà approuvé (recherché par téléphone), un numéro inconnu est redirigé vers la demande d'accès
  existante — aucune création de compte/enfant à la volée. Ajout d'enfant self-service existait
  déjà (`POST /parents/moi/enfants`) ; modification et désactivation côté parent sont nouvelles,
  en étendant `EleveService`/`ParentController` existants plutôt qu'un module `Student` parallèle.
  Matricule (`E<ANNÉE><RANG 4 chiffres>`) généré automatiquement (upsert atomique PostgreSQL,
  thread-safe sans verrou applicatif) après confirmation qu'aucun matricule scolaire officiel
  n'existait à faire coïncider. Détail et alternatives écartées (portée initiale erronée vers
  `apps/03_cantine_connect`/`services/core-api`, `@kts/ui` vs `@klem/ui`, Redis vs mémoire) :
  `collaboration/history/adr/2026-08-18-connexion-parent-otp-gestion-enfants.md`.

### [2026-08-18] - L'OTP remplace "Demande d'accès" : plus de validation admin préalable

- **Statut :** Livré / Opérationnel (68/68 tests backend verts, `npm run lint`/`build` propres)
- **Fichiers Modifiés :**
  - Backend : `parent/otp/service/ParentOtpService.java` (réécrit — fonctionne pour un numéro
    inconnu, crée `Utilisateur`+`Parent` à la vérification si besoin), `parent/otp/OtpStore.java`
    + `InMemoryOtpStore.java` (conservent l'email soumis le temps de la vérification),
    `parent/otp/dto/ParentOtpRequestDto.java` (+`email`),
    `parent/otp/controller/ParentOtpController.java`, tests réécrits
    (`ParentOtpServiceTest`, `InMemoryOtpStoreTest`).
  - Frontend : `pages/acces/DemandeAccesPage.jsx` réécrit entièrement (l'ancien stepper Identité/
    Contact/Résidence retiré, remplacé par le flux OTP à 2 pages : numéro+email+code, puis
    `<MesEnfantsPage />` intégré directement) ; `pages/acces/ParentOtpAccessPage.jsx` **supprimé**
    (fusionné dans `DemandeAccesPage.jsx`) ; `App.jsx` (route `/acces-otp` retirée) ;
    `pages/auth/LoginPage.jsx` (liens consolidés vers `/demande-acces`) ;
    `services/authService.js` (`demanderOtpParent` prend désormais l'email).
- **Description :** Instruction explicite et directe du porteur du projet le même jour ("pour des
  questions de facilité") — remplace le choix pris quelques heures plus tôt (ADR-020, contrôle
  admin préservé) : vérifier le code OTP crée désormais le compte parent à la volée (aucun
  nom/prénom collecté, valeurs génériques modifiables ensuite par un ADMIN) et donne accès
  immédiat à la gestion des enfants — plus de redirection vers une validation admin. Risque
  anti-fraude anciennement écarté (`decision-log.md`) explicitement réassumé par le porteur du
  projet, pas une régression silencieuse. Le back-office de validation des demandes d'accès
  (`DemandeAccesService`/`AccesController`/`DemandesAccesPage.jsx`) n'est pas supprimé mais devient
  orphelin. Détail complet : `collaboration/history/adr/2026-08-18-otp-remplace-demande-acces-validation-admin.md`.

### [2026-08-19] - Canal OTP parent paramétrable (WhatsApp par défaut, bascule SMS)

- **Statut :** Livré / Opérationnel (68/68 tests backend verts, `npm run lint`/`build` propres,
  vérifié en local via API réelle : envoi par défaut sur WhatsApp confirmé par les logs, bascule
  vers SMS confirmée après changement de la configuration)
- **Fichiers Modifiés :**
  - Backend : `db/migration/V17__parent_otp_canal_telephone.sql` (nouveau, seed
    `PARENT_OTP_CANAL_TELEPHONE = 'WHATSAPP'`), `parent/otp/service/ParentOtpService.java`
    (sélectionne directement le `NotificationSender` du canal configuré au lieu de
    `NotificationDispatcher` — ne dépend plus de `NOTIFICATIONS_SMS_ENABLED`/
    `NOTIFICATIONS_WHATSAPP_ENABLED`), test `ParentOtpServiceTest` mis à jour.
  - Frontend : `pages/configuration/ConfigurationPage.jsx` (nouveau sélecteur « Canal du code de
    vérification (OTP) parent » dans la catégorie Notifications, même patron visuel que le
    sélecteur Mode de paiement existant).
- **Description :** Demande explicite : WhatsApp par défaut pour le code OTP, avec un
  paramétrage admin permettant de basculer vers SMS. L'email reste envoyé en parallèle,
  indépendamment de ce choix. Détail et alternatives écartées :
  `collaboration/history/adr/2026-08-19-canal-otp-parametrable-whatsapp-sms.md`.
