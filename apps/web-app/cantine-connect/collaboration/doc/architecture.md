# Architecture Technique — Cantine Connect

> Source : CONCEPTION.md (section 3 & 5), CLAUDE.md, decision-log.md ADR-001.

---

## 1. Vision Générale — Architecture 3-Tiers Découplée

```
[ Navigateur / Smartphone Android ]  (React SPA / App Scan)
              │
              ▼ (HTTPS / JWT Bearer Token)
     [ Nginx Reverse Proxy ]  (Point d'entrée unique)
              │
              ▼
    [ API Backend ]  (Spring Boot 3.x — Port 8080)
              │
        ┌─────┴──────┐
        ▼             ▼
[ PostgreSQL 16 ]  [ Agrégateurs Paiement ]
  (Données métier)   (CinetPay / PayDunya — Webhooks)
```

**Principes architecturaux** :
- Cloud-native, hébergement africain (AWS Lagos / Infomaniak) pour latence réduite et conformité ARTCI.
- Application web responsive — aucune installation requise côté parents.
- Base de données centralisée multi-établissements avec isolation par site au niveau applicatif.
- Chiffrement des données en transit (TLS 1.3) et au repos (AES-256).
- API RESTful documentée pour intégrations futures avec les SI des écoles.

---

## 2. Nœuds Techniques

### 2.1 Nœud Client — Frontend (React.js / Vite)
- **Technologie** : React.js 18+ propulsé par **Vite** (JavaScript pur `.js` / `.jsx`, pas de TypeScript).
- **Design System** : Material UI (MUI) v5+. Interdiction de CSS brut ou fichiers `.css` externes — uniquement `sx={{}}` et `ThemeProvider`.
- **State management** : Hooks React exclusifs (`useState`, `useReducer` pour formulaires complexes, `useContext` pour session et thème).
- **Performance** : Lazy Loading via `React.lazy()` + `Suspense` par module (Admin, Parents, Scan) pour alléger le bundle sur réseaux mobiles.
- **Client HTTP** : Instance Axios unique dans `services/` avec intercepteurs pour injection automatique du JWT et redirection vers login sur code 401.
- **Build** : Vite génère un artefact SPA statique servi par Nginx.
- **Hébergement** : Vercel (frontend) ou Nginx (image Alpine) selon l'environnement.

**Interfaces cibles** :
| Interface | Description |
|-----------|-------------|
| `/admin/*` | Back-office gestionnaire (MUI DataGrid, Tabs, Drawer persistent) |
| `/parents/*` | Portail parents Mobile First |
| `/scan/*` | Application de scan QR Code (offline-first, cache 24h) |

### 2.2 Nœud Application — Backend API (Spring Boot 3.x)
- **Technologie** : Java 17+ / Spring Boot 3.x.
- **Gestionnaire de dépendances** : Maven (`./mvnw`).
- **Artefact** : Executable JAR isolé.
- **Architecture interne** : Couches imperméables strictes :
  ```
  Controller ➔ Service ➔ Repository ➔ Entity
  ```
- **Composants internes clés** :

| Composant | Rôle |
|-----------|------|
| `Spring Security` | Filtre JWT stateless — authentification sans état via `Authorization: Bearer <token>` |
| `Spring Data JPA` | Abstraction ORM. FETCH JOIN obligatoire pour éliminer le N+1 Select |
| `Spring AOP` | Aspect `@Around` sur contrôleurs d'écriture — alimentation transparente de `action_log` |
| `Spring Scheduling` | Tâches planifiées (envoi rappels de paiement J−7, J−3, J−1) |

- **Packages** : Découpage orienté domaine :
  ```
  com.cantine.[domaine].[controller|service|repository|entity|dto]
  ```

### 2.3 Nœud de Données — PostgreSQL 16
- **Moteur** : PostgreSQL 16 (ou MySQL comme alternative).
- **Pool de connexions** : HikariCP (max 20 connexions simultanées).
- **Indexation stratégique obligatoire** sur :
  - `student_id` (recherche élève)
  - `qr_code_token` (validation scan < 1s)
  - `operator_tx_id` (réconciliation paiements Mobile Money)
- **Persistance** : Volumes Docker nommés externes pour éviter toute perte de données.

### 2.4 Intégration Paiement — CinetPay / PayDunya
- Appels sortants depuis le backend vers les API REST des agrégateurs.
- Réception asynchrone des webhooks entrants (notification de statut de transaction).
- Endpoint dédié et sécurisé (vérification de signature HMAC) pour réception des webhooks.

---

## 3. Sécurité des Flux

### 3.1 Authentification JWT Stateless
```
[Client] POST /api/auth/login { email, password }
   │
[Backend] Valide les credentials ➔ génère JWT signé
   │
[Réponse] { "token": "eyJ..." }
   │
[Requêtes suivantes] Header: Authorization: Bearer eyJ...
   │
[Filtre JWT] Intercepte, vérifie la signature, injecte le SecurityContext
```

> Aucun token stocké en localStorage. Le token JWT est géré en mémoire (state React) et injecté via l'intercepteur Axios.

### 3.2 Isolation Multi-Établissements
- Chaque entité métier porte un `etablissement_id`.
- Le `JwtAuthFilter` extrait le rôle et l'`etablissement_id` du token.
- Les `@Service` appliquent un filtre systématique par `etablissement_id` pour les rôles `GESTIONNAIRE` et `AGENT_SCAN`.
- Le `SUPER_ADMIN` n'a pas de restriction d'établissement.

### 3.3 Protection des Données des Mineurs (ARTCI / RGPD)
- Chiffrement AES-256 des données d'identité sensibles (nom, prénom, allergies, contacts).
- Accès aux profils d'élèves restreint par rôle.
- Journalisation immuable via `action_log` de toute modification d'un profil élève ou d'un statut de paiement.
- Hébergement sur territoire africain (CI ou région sub-saharienne).

---

## 4. Architecture Offline-First (Application de Scan)

```
[Connexion disponible]
   ├─ Téléchargement du snapshot chiffré des élèves autorisés
   ├─ Stockage local (IndexedDB ou SQLite Android)
   └─ TTL : 24 heures

[Scan QR Code]
   ├─ Lookup dans le cache local (< 1s garanti)
   ├─ Anti-passback : vérification des passages du jour dans le cache
   └─ Écriture différée dans la file de synchronisation locale

[Retour de connexion]
   └─ Flush de la file de passages différés vers l'API
   └─ Rechargement du cache
```

---

## 5. Structure des Dossiers du Projet

```
cantine-connect/
├── client-frontend/              ← React.js / Vite / MUI
│   └── src/
│       ├── pages/
│       │   ├── admin/            ← Back-office gestionnaire
│       │   ├── parents/          ← Portail parents
│       │   └── scan/             ← Application de scan QR
│       ├── components/           ← Composants MUI réutilisables
│       ├── hooks/                ← Custom hooks (useEleves, usePaiements...)
│       ├── services/             ← Client Axios + intercepteurs
│       └── context/              ← AuthContext, ThemeContext
│
├── server-backend/               ← Spring Boot 3.x / Maven
│   └── src/main/java/com/cantine/
│       ├── auth/                 ← JWT filter, AuthController, AuthService
│       ├── eleve/                ← EleveController, EleveService, EleveRepository, Eleve, EleveDTO
│       ├── etablissement/        ← Établissement, Niveau, Classe
│       ├── paiement/             ← Transaction, Webhook handlers
│       ├── scan/                 ← PassageController, QRCodeService
│       ├── audit/                ← ActionLog, AuditAspect (@Aspect)
│       └── common/               ← GlobalExceptionHandler, ApiResponse
│
├── collaboration/                ← Documentation de gouvernance
│   ├── context/CONTEXT.md
│   ├── doc/architecture.md       ← Ce fichier
│   ├── doc/specifications.md
│   ├── doc/workflows.md
│   └── history/
│       ├── history-log.md
│       ├── decision-log.md
│       └── adr/
│
├── skills/                       ← Référentiels de compétences pour l'IA
├── commands/                     ← Macros Claude Code (/startup, /update)
├── docker-compose.yml
└── CLAUDE.md
```

---

## 6. Conteneurisation & Déploiement (Docker Compose)

```yaml
# Schéma simplifié — docker-compose.yml
services:
  cantine-db:
    image: postgres:16-alpine
    volumes: [postgres_data:/var/lib/postgresql/data]
    healthcheck: pg_isready

  cantine-backend:
    image: klem/cantine-backend:latest
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://cantine-db:5432/cantine
    depends_on: [cantine-db]

  cantine-proxy:
    image: nginx:alpine
    ports: ["80:80", "443:443"]
    depends_on: [cantine-backend]
```

---

## 7. CI/CD (GitHub Actions)

1. **Validation** : Linting + tests unitaires sur chaque push vers `develop` / `main`.
2. **Build** : `./mvnw clean package` (backend) + `npm run build` dans `client-frontend/`.
3. **Livraison** : Génération des images Docker et push vers le registre privé.
4. **Déploiement** : Connexion SSH sécurisée au serveur, `docker compose pull` + redémarrage progressif.

**Sauvegarde** : `pg_dump` automatique chaque nuit à 02h00, chiffrement de l'archive, export vers stockage objet distant (S3 / équivalent), rétention 30 jours.

---

## 8. Extensions Phase 1 & 2 — Refonte Premium (livré)

> Voir ADR-017 et ADR-018 (`decision-log.md`). Livré et poussé sur `main`
> (commits `6305229`, `6d1f2c3`, `2f932b5`, `bb887ab`, `c3ae1c9`).

### 8.1 Demandes d'accès parent (self-service) — cycle complet
- Table `demandes_acces` (migration `V8`) + `AccesController`.
- `POST /api/v1/demandes-acces` (public) : le parent soumet sa demande (identité,
  téléphone principal + WhatsApp, résidence) — persiste uniquement la demande
  (statut `EN_ATTENTE`), ne crée aucun compte.
- `GET /api/v1/demandes-acces`, `PATCH .../valider`, `PATCH .../rejeter` (ADMIN) :
  écran `/demandes-acces` — à la validation, création du compte
  `Utilisateur`(rôle PARENT) + `Parent`, génération d'un mot de passe temporaire,
  notification (email si fourni, SMS sinon), `doitChangerMotDePasse=true`.
- Si le parent n'a pas fourni d'email, un identifiant de connexion synthétique
  est généré (`p<téléphone>@parent.cantine-connect.ci`) — jamais présenté comme
  un email réel, aucun envoi de mail tenté vers ce domaine.
- Changement de mot de passe forcé à la première connexion
  (`POST /api/v1/auth/changer-mot-de-passe`) — le front redirige automatiquement
  tant que `doitChangerMotDePasse` est vrai.

### 8.2 Espace parent self-service
- `POST /api/v1/parents/moi/enfants` (rôle PARENT) : ajout d'un enfant par le
  parent lui-même (matricule, identité, sexe, date de naissance, résidence),
  avec cascade établissement→niveau→classe. Les coordonnées parent sont
  dérivées du compte connecté (jamais saisies dans le formulaire).
- Lecture des référentiels établissements/niveaux/classes ouverte à tous les
  rôles authentifiés (dont PARENT) — restait bloquée par erreur en `403` avant
  correctif (voir ADR-017, bugs découverts).

### 8.3 Règles métier
- **Délai de grâce paramétrable** : `DELAI_GRACE_JOURS_DEFAUT` (défaut 7 j,
  config globale) + `etablissements.delai_grace_jours` en surcharge optionnelle
  (migration `V9`).
- **Certificat médical pour allergies** : `eleves.certificat_medical_url`
  (migration `V10`). Règle appliquée dans `EleveService` (pas en contrainte
  SQL) : `allergies` renseigné ⇒ certificat obligatoire, sinon
  `400 VALIDATION_ERROR`. Upload via `POST /eleves/{id}/certificat-medical`
  (multipart, staff), stockage disque local (`FileStorageService`).
- **Périodes d'abonnement** : `eleves.periode_abonnement`
  (`TRIMESTRIEL`/`ANNUEL`, migration `V11`) + clés de config `TARIF_TRIMESTRE` /
  `TARIF_ANNEE`. Le mode `CREDITS` (portefeuille prépayé) coexiste avec
  l'abonnement.
- **Recherche parent** étendue au téléphone (en plus de l'email) —
  `ParentRepository.findAllWithDetailsBySearch`.

### 8.4 Paiement & notifications — intégration réelle (ADR-018)
- Interface `PaymentProvider` : `CinetPayProvider`/`PayDunyaProvider` appellent
  réellement les API de checkout des agrégateurs (`java.net.http.HttpClient`),
  sélection via la clé de config `PAIEMENT_PROVIDER_ACTIF`. Placeholders
  `OrangeMoneyDirectProvider`/`MtnMoneyDirectProvider`/`MoovMoneyDirectProvider`
  prêts pour une intégration marchande directe future.
- Interface `NotificationSender` : `EmailNotificationSender` (SMTP existant) +
  `SmsNotificationSender` (API REST Twilio, Basic Auth) via
  `NotificationDispatcher`. Repli automatique en mode journal si les
  identifiants Twilio sont absents — aucun crash.
- Aucune clé réelle (CinetPay/PayDunya/Twilio) renseignée à ce stade : les
  appels échouent proprement (`409` côté paiement) tant qu'elles ne sont pas
  fournies par le client.

### 8.5 Thème visuel
- Nouveau thème par défaut « Premium » (chaleureux, orange/vert), avec
  sélecteur multi-thèmes réactivé (Premium / Corporatif / Moderne), choix
  mémorisé en `localStorage`.

---

## 9. Déploiement — État réel

| Composant | Plateforme | Statut | URL |
|---|---|---|---|
| Frontend (`client-frontend`) | Vercel (projet `cantine-connect`) | ✅ Déployé (production) | `https://cantine-connect-swart.vercel.app` |
| Backend (`server-backend`) | Railway (projet `celebrated-tenderness`) | ⚠️ **En échec** — voir ci-dessous | `https://cantine-connect-production.up.railway.app` |
| Base de données | Railway (service `Postgres`, même projet) | ⚠️ **Hors ligne** | — |

**Points d'attention connus (2026-08-04) :**
- La variable Vercel `VITE_API_URL` (Production/Preview) est **vide** — le frontend déployé se rabat sur `http://localhost:8081`, non fonctionnel pour un visiteur réel tant qu'elle n'est pas renseignée avec l'URL Railway.
- Le service Railway a été reconfiguré pour déployer depuis `klem-projects-repository` (root directory
  `apps/web-app/cantine-connect/server-backend`, `Dockerfile` — auparavant branché sur un dépôt
  `yacoubasylla/cantine-connect` distinct et désynchronisé). La reconfiguration de la source est confirmée
  (repo + root directory), mais **aucun déploiement n'a pu être déclenché** : le compte Railway a un essai
  expiré, qui bloque toute exécution de build/déploiement (CLI et API GraphQL bloquées identiquement).
- La base Postgres Railway est arrêtée (probablement mise en veille pour inactivité par la plateforme).

**Actions restantes pour un déploiement backend fonctionnel :**
1. Sélectionner un plan payant sur le compte Railway (`yacoubasylla`).
2. Redémarrer le service Postgres.
3. Déclencher le déploiement (automatique au prochain `git push` sur `main`, la source étant maintenant correctement branchée, ou manuellement via `railway redeploy`).
4. Renseigner `VITE_API_URL` sur Vercel avec l'URL Railway (`.../api/v1`) et redéployer le frontend.
