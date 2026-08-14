# 🛠️ CLAUDE.md : Guide de Développement et Commandes IA

> **Directive Système :** Ce fichier contient les règles de codage, les commandes de build et les critères de validation industriels de KLEM Technologies. Tu dois t'y conformer de manière absolue à chaque génération de code ou exécution de commande.

> **Portée vs `MASTER_SYSTEM_DIRECTIVE.md` :** ce fichier fait autorité pour les apps clients
> existantes de ce monorepo (cantine-connect, parcauto, backend-api/FleetControl, clinic,
> pharmacie), notamment sa règle de sécurité par session/cookie `JSESSIONID` (§2.1) et Java 17.
> Pour tout **nouveau service KLEM DataSphere** créé sous `services/*` (Hinterland-Track,
> KLEM Trade-X, KLEM Copilot), c'est `MASTER_SYSTEM_DIRECTIVE.md` §7 (OAuth2 Resource
> Server/JWT, Java 21 LTS) qui prévaut. Contexte et alternatives écartées :
> `collaboration/history/adr/2026-08-08-adoption-directive-maitre-datasphere-perimetre.md`.
>
> **UI mobile/client (Expo/React Native) :** pour la couche présentation des apps clients
> (`apps/web-app/*` + `apps/mobile-app/*`) ayant un besoin réel de parité native Android/iOS,
> `SYSTEM_INSTRUCTIONS.md` (Expo SDK 51+/Expo Router v3/NativeWind v4, « Universal App First »)
> prévaut sur la §2.2 ci-dessous, qui reste la référence pour les frontends web existants tant
> qu'ils n'ont pas été migrés. Contexte : `collaboration/history/adr/2026-08-10-adoption-system-instructions-universal-app-expo.md`.

---

## ⚡ 1. Commandes de l'Espace de Travail (Monorepo pnpm + Turbo)

L'ensemble du projet est orchestré par **Turborepo** et s'exécute depuis la racine via **pnpm**. N'utilise jamais `npm` ou `yarn`.

| Action | Commande Racine | Portée / Cible |
| :--- | :--- | :--- |
| **Initialisation** | `pnpm install` | Installe toutes les dépendances du monorepo |
| **Démarrage Dev** | `pnpm dev` | Lance toutes les applications en mode parallèle |
| **Build Global** | `pnpm build` | Compile toutes les applications et packages |
| **Linting** | `pnpm lint` | Vérifie la conformité du code (ESLint / Checkstyle) |
| **Formatage** | `pnpm format` | Aligne le code selon nos règles Prettier / Java |
| **Tests Unitaires** | `pnpm test` | Exécute la suite de tests (Vitest / JUnit 5) |

### 🎯 Commandes ciblées (Filtrage Turbo)
Pour agir sur une seule application sans impacter le reste du monorepo :
*   **Build uniquement le backend :** `pnpm --filter backend-api build`
*   **Lancer uniquement le web-app :** `pnpm --filter web-app dev`
*   **Ajouter une dépendance à un package précis :** `pnpm --filter @klem/ui add <package>`

---

## 📐 2. Standards de Code & Architecture par Couche

### 🟢 2.1 Backend (Java 17 / Spring Boot 3.x)
*   **Structure des Paquetages :** Respecter le découpage orienté domaine :
    `com.klem.api.[domaine].[controller|service|repository|model|dto]`
*   **Sécurité des Sessions :** Pas de stockage local de JWT. L'authentification utilise des sessions HTTP encapsulées dans des cookies sécurisés :
  ...java
    ResponseCookie cookie = ResponseCookie.from("JSESSIONID", session.getId())
        .httpOnly(true)
        .secure(true) // Uniquement en HTTPS
        .sameSite("Strict")
        .path("/")
        .maxAge(Duration.ofDays(7))
        .build();
    ```
*   **Couche Service :** Centralise 100% de la logique métier. Annoter avec `@Transactional(readOnly = true)` par défaut à l'échelle de la classe, et écraser avec `@Transactional` uniquement sur les méthodes d'écriture.
*   **Gestion des Exceptions :** Interdiction de faire des `try-catch` vides ou de renvoyer des stacktraces brutes au client. Utiliser le `GlobalExceptionHandler` (`@ControllerAdvice`) pour intercepter les exceptions métiers et renvoyer un format standard : `{ "timestamp": "...", "status": 400, "error": "BAD_REQUEST", "message": "..." }`.

### 🔵 2.2 Frontend (React.js / TypeScript / MUI)
*   **Typage Strict :** L'usage de `any` est passible d'un rejet immédiat par la CI. Déclare des interfaces TypeScript explicites pour chaque objet et contrat d'API.
*   **Gestion des Effets et APIs :** Pas de `useEffect` brut pour charger des données. Isoler systématiquement les appels API dans des **Custom Hooks** utilisant Axios ou une abstraction de requêtes.
*   **Composants graphiques (Material UI) :** Interdiction de surcharger les styles avec du CSS brut ou des fichiers `.css` externes. Utiliser la propriété `sx={{ ... }}` de MUI ou le système de thémisation globale (`ThemeProvider`).
*   **Conception Responsive :** Les layouts d'administration doivent être fluides. Le Drawer de navigation persistant doit utiliser des transitions MUI standard et décaler la zone de contenu principal (`main`) sans jamais créer de chevauchement de texte sur les résolutions intermédiaires.

### 🟡 2.3 Couche Données (PostgreSQL 16 & Big Data)
*   **Migrations :** Toute modification de structure de base de données doit être portée par un script SQL incrémental ordonné (via le système de migration du projet).
*   **Conventions SQL :** Tables et colonnes en `snake_case` (ex: `date_creation`, `statut_vehicule`). Les clés primaires techniques utilisent le type `BIGSERIAL` ou `UUID`.
*   **Pipelines Temps Réel :** Les schémas de messages transitant par Apache Kafka doivent être documentés. Les traitements Apache Spark doivent être optimisés pour éviter les *shuffles* mémoire inutiles.

### 🟣 2.4 Convention de Nommage : Code en Anglais / Explications en Français
*   **Identifiants de code en anglais.** Noms de classes, composants React, variables, fonctions,
    fichiers, entités JPA/ORM et endpoints API doivent être rédigés en anglais, conformes aux
    standards du langage concerné (Java, React/TypeScript, PostgreSQL). Exemple :
    `elevesPage` → `studentsPage`, `EtablissementService` → `SchoolService`.
*   **Explications en français.** Commentaires de code, documentation Markdown, messages de commit
    Git et texte affiché à l'utilisateur (UI) restent impérativement en français.
*   **Non-régression stricte — `cantine-connect` en particulier.** `cantine-connect` a été
    développé initialement avec des identifiants en français (packages `com.klem.cantine.eleve`,
    `com.klem.cantine.etablissement`, entités `Eleve`, `Etablissement`, `Utilisateur`,
    `PassageRefectoire`, pages `/eleves`, `/etablissements`, etc.). **Il est strictement interdit
    de modifier ce code existant qui fonctionne, ou d'effectuer des remplacements aveugles
    (rename en masse) qui risqueraient de casser l'application.** Cette règle de nommage
    s'applique uniquement :
    - aux **nouveaux fichiers** (nouvelle classe, nouveau composant, nouvelle entité) ;
    - aux **nouvelles fonctionnalités** ajoutées à un module existant ;
    - aux **refactorisations explicitement demandées** par un ticket ou l'utilisateur, jamais de
      façon incidente en marge d'une autre tâche.
*   **Portée.** S'applique à toutes les applications de ce dépôt, présentes et futures. Pour le
    dictionnaire français → anglais spécifique à `cantine-connect`, voir
    `klem-labs-repository/projects/03_cantine_connect/specifications_techniques.md`, section
    « Conventions de Nommage & Dictionnaire Technique ».

---

## 🟠 2.5 Architecture Monorepo : Règle Package-First (`packages/`)

**Interdiction** de développer un composant UI, une fonction utilitaire ou une configuration à
vocation générique/réutilisable directement dans un dossier applicatif (`apps/*/*` ou
`services/*`). **Obligation** de le créer, l'exporter et le maintenir dans `packages/` :
*   `packages/ui/` (`@klem/ui`) — Composants React / Material UI (boutons, modales, tables,
    layouts, badges de statut, notifications).
*   `packages/data-utils/` — Formateurs, parseurs, calculateurs métier, validateurs (Zod/Yup).
*   `packages/utils/` (`@klem/utils`) — Fonctions helper JS/TS pures (dates, chaînes, tokens).
*   `packages/config/` — Configurations partagées (TSConfig, ESLint, Tailwind, build scripts).

**Avant de créer** un composant UI, une fonction utilitaire ou un plugin dans une application
(`apps/`), vérifie s'il existe déjà dans `packages/`. S'il n'existe pas et qu'il est réutilisable
(aucune logique métier, aucun couplage à une API ou un state global spécifique), crée-le d'abord
dans le package concerné, exporte-le dans son `src/index.ts` (barrel export), puis importe-le
dans l'application cible via l'espace de nommage du workspace :
`import { KlemButton } from '@klem/ui';`.

**Règles pour tout nouvel élément de `packages/` :**
*   Fichiers/fonctions/props en **anglais** (`StudentCard.tsx`, `useStudentData.ts`) — PascalCase
    pour les composants React, camelCase pour les hooks/fonctions — cf. §2.4.
*   Export centralisé obligatoire via `packages/<nom>/src/index.ts`.
*   Typage TypeScript strict (`interface`), zéro couplage à une API ou un state global applicatif
    — données injectées via props/contextes configurables.
*   Documentation JSDoc/TSDoc en **français** au-dessus des interfaces et fonctions principales.

### 🔐 2.5.1 Vérification de licence (`@klem/license`)
*   Package dédié à la vérification de clé de licence KLEM (`KTS_LICENSE_KEY`, signature ECDSA
    P-256 via Web Crypto, aucun secret partagé côté client). Une application qui souhaite
    afficher son statut de licence s'encapsule avec
    `<KlemProvider licenseKey={...} appId="mon-app"><App /></KlemProvider>`.
*   **Mode avertissement seul (décision KLEM du 2026-08-12) :** `KlemProvider` ne bloque **jamais**
    le rendu, quel que soit le statut (absente/expirée/invalide/mauvaise app) — il journalise
    (`console.warn`) et affiche une bannière non bloquante uniquement en développement. Ne pas
    faire évoluer ce comportement vers un blocage sans validation explicite, tant que les
    applications existantes (`cantine-connect`, `parcauto`) n'ont pas de vraie clé configurée.
*   Les composants de `@klem/ui` restent indépendants de `@klem/license` (pas de logique métier
    dans `packages/ui`, cf. règle d'or de `packages/ui/CLAUDE.md`) : l'intégration se fait au
    niveau racine de l'application, pas à l'intérieur des composants UI eux-mêmes.
*   Namespace des packages : reste `@klem/*` pour l'instant (`@klem/ui`, `@klem/utils`,
    `@klem/license`…) — un éventuel rebranding vers `@kts/*`/`Kts*` a été explicitement écarté
    comme chantier séparé, à ne pas entreprendre sans nouvelle demande explicite.

---

## 🔄 3. Processus de Clôture et Livraisons de Tâches

Avant de déclarer une tâche comme "terminée" et de redonner la main à l'utilisateur, l'agent IA doit obligatoirement valider les 4 étapes suivantes :

### 1️⃣ Validation Technique locale
Exécuter un build complet et un contrôle qualité. Le code ne doit générer aucun warning bloquant.
  bash
pnpm lint && pnpm build

### 2️⃣ Documentation d'Arbitrage (Si applicable)
Si la tâche a nécessité l'introduction d'une nouvelle bibliothèque, d'un changement de modèle de données ou d'une modification de flux réseau, exécuter le script de génération d'ADR :

Bash
./scripts/create-adr.sh "titre-de-la-decision"
Compléter le fichier .md généré dans collaboration/history/adr/ en spécifiant le contexte, les alternatives et les conséquences.

### 3️⃣ Journalisation Chronologique
Ajouter un bloc au fichier collaboration/history/history-log.md respectant scrupuleusement la structure suivante :

Markdown
### [2026-06-22] - Implémentation du [Nom de la Fonctionnalité]
- **Statut :** Livré / Opérationnel
- **Fichiers Modifiés :** `apps/backend-api/...`, `apps/web-app/...`
- **Description :** Synthèse technique claire de ce qui a été codé et validé.

### 4️⃣ Message de Commit Standardisé
Formater la proposition de message de fin de session selon les spécifications Conventional Commits :

feat(scope): ... pour une nouvelle fonctionnalité.

fix(scope): ... pour la résolution d'un bug.

docs(scope): ... pour les modifications de documentation.

---

## 🔀 4. Workflow Git : Branche + Pull Request obligatoires (code)

**Règle (actée le 2026-08-14) :** tout changement sous `services/`, `apps/` ou `packages/` passe
par une branche puis une Pull Request vers `main` — plus de push direct sur `main` pour du code.
Portée : ce dépôt (`klem-projects-repository`) uniquement. `klem-labs-repository` reste purement
documentaire (pas de code applicatif, voir son propre `CLAUDE.md`) et continue en push direct sur
`master`.

1. **Créer une branche** depuis `main` à jour :
   - Rattachée à un ticket Redmine d'un projet : `feature/<CODE-PROJET>-<ID-Redmine>-description`
     ou `bugfix/<CODE-PROJET>-<ID-Redmine>-description` (`<CODE-PROJET>` = Code Projet du
     `cas_metier.md` concerné, ex. `CC-AUDIT`, `HT-CORRIDOR`) — format vérifié par le workflow
     réutilisable `branch-naming-check.yml` de `klem-labs-repository`
     (`platform-devsecops/adr/0008-verification-convention-branche-pr.md`).
   - Changement transverse sans ticket (governance, CI, package partagé, dépendances) :
     `chore/description` ou `docs/description`.
2. **Committer et pousser la branche** (jamais `main` directement) : `git push -u origin <branche>`.
3. **Ouvrir la Pull Request** : `gh pr create --title "..." --body "..."` — le corps résume ce qui
   change et pourquoi (pas juste quoi), suit le processus de clôture ci-dessus (§3) pour le
   contenu.
4. **Laisser passer la CI** (`ci.yml`, déjà déclenché sur `pull_request` vers `main`/`develop`) —
   ne jamais proposer de fusionner une PR dont la CI est rouge ou encore en cours.
5. **Fusion** : décision humaine explicite (ou confirmation explicite du demandeur), jamais
   automatique côté agent — squash-merge par défaut pour garder un historique `main` linéaire par
   PR. Supprimer la branche après fusion.

**Non automatisé** : forcer techniquement ce workflow (bloquer le push direct sur `main`, exiger la
CI avant fusion) nécessite d'activer la protection de branche GitHub (« Require pull request before
merging », « Require status checks to pass ») — un geste d'administration manuel sur ce dépôt, non
faisable depuis un fichier de règles. Tant que ce n'est pas activé, cette règle reste une
convention à respecter, pas une contrainte techniquement bloquée.