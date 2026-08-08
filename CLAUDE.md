# 🛠️ CLAUDE.md : Guide de Développement et Commandes IA

> **Directive Système :** Ce fichier contient les règles de codage, les commandes de build et les critères de validation industriels de KLEM Technologies. Tu dois t'y conformer de manière absolue à chaque génération de code ou exécution de commande.

> **Portée vs `KLEM_MASTER_SYSTEM_DIRECTIVE.md` :** ce fichier fait autorité pour les apps clients
> existantes de ce monorepo (cantine-connect, parcauto, backend-api/FleetControl, clinic,
> pharmacie), notamment sa règle de sécurité par session/cookie `JSESSIONID` (§2.1) et Java 17.
> Pour tout **nouveau service KLEM DataSphere** créé sous `services/*` (Hinterland-Track,
> KLEM Trade-X, KLEM Copilot), c'est `KLEM_MASTER_SYSTEM_DIRECTIVE.md` §7 (OAuth2 Resource
> Server/JWT, Java 21 LTS) qui prévaut. Contexte et alternatives écartées :
> `collaboration/history/adr/2026-08-08-adoption-directive-maitre-datasphere-perimetre.md`.

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