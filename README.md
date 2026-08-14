# Guide d'installation et de démarrage : KLEM-PROJECTS-REPOSITORY

Bienvenue dans l'espace de travail central de KLEM Technologies. Ce monorepo contient l'ensemble de nos briques logicielles, allant des applications métiers aux outils de traitement de données (Big Data).

## Structure du Workspace 

Ce monorepo est orchestré par **Turborepo** et structuré pour isoler strictement les applications clientes, la logique métier partagée, la documentation d'architecture et les configurations d'automatisation pour les agents IA.

### Arborescence du Répertoire

KLEM-PROJECTS-REPOSITORY/
├── .github/                     # Workflows CI/CD réels (ci.yml, klem-ref-bot-scheduled.yml) — voir .github/README.md
├── .claude/                     # settings.local.json uniquement (permissions locales)
├── commands/                    # Commandes /startup, /update, /prime (racine, pas dans .claude/)
├── apps/                        # Applications autonomes et déployables
│   ├── backend-api/             # Non démarré — ne contient qu'un README décrivant l'architecture cible
│   ├── mobile-app/               # Expo/React Native — cantine-connect (scaffold livré)
│   ├── showcase-website/        # site-klem (WordPress, réel), site-veone (vide)
│   └── web-app/                 # 4 apps distinctes : cantine-connect (pilote), parcauto, clinic (vide), pharmacie (vide)
├── services/                    # 3 services KLEM DataSphere réels — core-api, referentiel-api-service, transit-ops-service (voir services/README.md)
├── infrastructure/               # Réservé : Docker/Terraform/Kubernetes une fois le VPS provisionné (toujours vide, voir infrastructure/*/README.md)
├── docs/                        # Index léger vers collaboration/ et knowledges/ (pas une 3e arborescence)
├── collaboration/               # Base de connaissances projet partagée humain/IA
│   ├── context/
│   │   └── CONTEXT.md           # Vision produit, alignement métier et règles globales KLEM
│   ├── doc/
│   │   ├── architecture.md      # Topologie des nœuds, sécurité des flux et infrastructure prod
│   │   ├── specifications.md    # Schémas de base de données PostgreSQL et spécifications API
│   │   └── workflows.md         # Diagrammes d'états et cycles de vie opérationnels du parc
│   └── history/
│       ├── decision-log.md      # Registre historique des arbitrages stratégiques
│       ├── history-log.md       # Journal chronologique des livraisons et des tâches accomplies
│       └── adr/                 # Architecture Decision Records (Format standardisé, ~13 fichiers à ce jour)
├── knowledges/                  # Dropbox de documents externes (voir knowledges/README.md — ne pas confondre avec knowledge/ racine du workspace)
│   ├── raw/                     # Documents bruts non structurés (PDFs, exports comptables, décrets)
│   └── wiki/                    # Vide par défaut, peuplé à la demande
├── packages/                    # Modules partagés et réutilisables au sein du monorepo
│   ├── config/                  # Vide aujourd'hui — ESLint/TSConfig/Prettier partagés prévus mais pas encore écrits
│   ├── data-utils/               # Vide aujourd'hui
│   ├── ui/                      # @klem/ui — un seul composant réel (KlemButton) à ce stade
│   └── utils/                   # @klem/utils — structure prête
├── scripts/                     # Scripts d'automatisation de l'infrastructure locale
│   ├── README.md                # Guide d'utilisation des outils en ligne de commande
│   └── create-adr.sh            # Script Bash de génération automatisée de fichier ADR
├── templates/                   # Gabarits CLAUDE.md par type d'app + package.json/app.package.json
├── .cursorrules                 # Instructions de contextualisation pour l'éditeur Cursor
├── turbo.json                   # Configuration du cache et des pipelines de build Turborepo
├── CLAUDE.md                    # Guide de survie et règles de développement strictes pour l'IA
└── package.json                 # Racine de configuration des workspaces pnpm


### Matrice d'Utilité des Dossiers

| **`Composant / Dossier`**  | **`Rôle Fonctionnel & Technique`**  |
| :--- | :--- |
| **`apps/web-app/`** | 4 applications web client distinctes (React/MUI) : `cantine-connect` (pilote, MVP livré), `parcauto` (gestion de parc), `clinic` et `pharmacie` (dossiers vides, non démarrés). |
| **`apps/backend-api/`** | **Non démarré.** Ne contient qu'un README décrivant l'architecture cible (Spring Boot, PostgreSQL) pour le futur backend de `parcauto`/FleetControl — aucun code réel aujourd'hui. |
| **`apps/mobile-app/`** | Applications mobile/PWA Expo/React Native — `cantine-connect` (scaffold Expo Router/NativeWind livré) à ce stade. |
| **`apps/showcase-website/`** | Sites internet institutionnels — `site-klem` (WordPress, réel) et `site-veone` (dossier vide, non démarré). |
| **`collaboration/context/`** | Fichiers d'alignement stratégique. Contient la vision de l'entreprise et la culture métier indispensables à la bonne compréhension globale de l'IA. |
| **`collaboration/doc/`** | Documentation technique et fonctionnelle d'ingénierie (Spécifications de tables, contrats d'API, architecture réseau). |
| **`collaboration/history/`** | Traçabilité de l'évolution du projet. Stocke l'historique des modifications, les commits de clôture de tâches, et les dossiers d'arbitrage d'architecture (ADR). |
| **`knowledges/`** | Dropbox de documents externes bruts (lois de finances, documentations d'API externes) à faire analyser par un agent — pas une base de connaissances maintenue (voir `knowledges/README.md`). |
| **`packages/`** | Bibliothèque interne au monorepo permettant de mutualiser le code (le design system UI partagé, les types stricts TypeScript et les configurations de build). |
| **`scripts/`** | Outils de commodité pour les développeurs (génération de squelettes, automatisation de tâches Docker, sauvegardes DB). |
| **`templates/`** | Modèles de structures de fichiers imposés pour conserver une uniformité parfaite lors de l'ajout de nouvelles fonctionnalités. |
| **`.claude/`** | Permissions locales (`settings.local.json`) uniquement. Les commandes de session (`/startup`, `/update`, `/prime`) vivent dans `commands/` à la racine, pas ici. |


### Les 5 fichiers clés à comprendre

- **`README.md`**
  **Le guide d'accueil des humains** : C'est le point d'entrée technique du projet pour les développeurs. Il explique comment cloner le monorepo, installer les dépendances (via pnpm), lancer le serveur de développement local avec Turborepo, et comprendre l'organisation globale de l'espace de travail.

- **`CLAUDE.md`**
  **L'âme et le guide de survie de l'IA** : Ce fichier est lu automatiquement par l'agent Claude Code à l'ouverture de chaque session de travail. Il contient les instructions de formatage de code, les commandes système autorisées, les normes de développement strictes de l'entreprise (ex: typage strict TypeScript, architecture Spring Boot) et la routine de validation avant de livrer une tâche.

- **`/collaboration/context/CONTEXT.md`**
  **La carte d'identité métier de l'entreprise** : Ce document centralise la vision stratégique de KLEM Technologies & Services. Il décrit la culture métier, les objectifs des applications (comme l'ERP de gestion de flotte *FleetControl*), l'écosystème ciblé et les contraintes opérationnelles locales. Plus ce fichier est précis, plus les réponses de l'IA sont pertinentes face à vos enjeux réels.

- **`/collaboration/history/HISTORY.md`** (ou `history-log.md`)
  **Le journal de bord vivant du projet** : C'est la mémoire à long terme des sessions de développement. Il recense de manière chronologique toutes les avancées majeures, les tâches clôturées et l'état actuel du workspace. Complété rigoureusement à la fin de chaque sprint, il permet à l'IA de reprendre le travail exactement là où la session précédente s'était arrêtée, sans perte de contexte.

- **`MASTER_SYSTEM_DIRECTIVE.md`**
  **L'architecture cible de la plateforme KLEM DataSphere** : décrit la stack de référence (Java 21 LTS, Spring Boot 3.x, Next.js) et les standards (sécurité OAuth2 Resource Server, observabilité, tests) pour les produits pivots Hinterland-Track, KLEM Trade-X et KLEM Copilot, développés sous `services/`. Ne remplace pas les `CLAUDE.md` des apps clients existantes (`apps/web-app/*`, `apps/backend-api`) — voir `collaboration/history/adr/2026-08-08-adoption-directive-maitre-datasphere-perimetre.md` pour le périmètre exact.


## Démarrage Rapide (Onboarding)
1. **Prérequis :** Installer [pnpm](https://pnpm.io/) (gestionnaire de paquets).
Avant de commencer, assurez-vous d'avoir les outils suivants :

- Node.js : Version 18.0.0 ou supérieure.
- pnpm : Notre gestionnaire de paquets haute performance.
- Installation : npm install -g pnpm
- VS Code : Recommandé avec les extensions ESLint et Prettier

2. **Installer toutes les dépendances du monorepo :**

   ```bash
   pnpm install
   ```

3. **Vérifier que tout est correctement lié :**

   ```bash
   pnpm turbo run build
   ```

   Si cette commande se termine sans erreur, votre environnement est parfaitement configuré.

4. **Guide de développement quotidien :**
     **A. Lancer les projets**

      Pour démarrer tous vos projets simultanément en mode développement :

      ```bash
      pnpm dev
      ```

     **B. Ajouter une nouvelle fonctionnalité (Workflow)**

      Si vous développez une nouvelle brique :

      Créer un ADR : Avant de coder, documentez votre choix.

      ```bash
      ./scripts/create-adr.sh <nom-de-votre-feature>
      ```

      Créer votre package/app : Utilisez les templates situés dans /templates/.

Lier les dépendances : Si vous ajoutez un nouveau package, assurez-vous qu'il est bien déclaré dans package.json de votre application avec "nom-du-package": "workspace:*".


## Qualité et Standards de Développement
- Le .cursorrules : Ce fichier à la racine guide notre IA. Si vous utilisez Cursor ou Claude Code, il est automatiquement pris en compte pour respecter les standards KLEM.
- Monorepo : Nous utilisons Turborepo. Chaque nouvelle fonctionnalité doit privilégier la réutilisation de code via packages/.
- Gouvernance : Toute modification majeure de l'architecture doit être documentée par un ADR. Utilisez ./scripts/create-adr.sh <nom-du-sujet> pour créer un nouveau document.
- Pipeline CI/CD : À chaque git push, le système vérifie :
      La conformité du code (Linting).
      La présence d'un fichier ADR pour toute nouvelle structure.
      Rejette automatiquement tout code ne respectant pas les règles de linting ou sans ADR.
- Commande de vérification locale : Avant de soumettre votre travail, lancez ceci pour éviter les erreurs sur le serveur :

  ```bash
  pnpm turbo run lint build
  ```


## Collaboration
- IA Assistant : Ce projet est optimisé pour être utilisé avec Claude Code et VS Code. Veuillez respecter les directives définies dans le fichier .cursorrules à la racine.
- Workflow : feature/* -> develop -> main.

## Bonnes pratiques

- **Démarrez chaque session avec `/startup`** pour que votre Jarvis charge votre contexte complet
- **Soyez honnête dans vos réponses** lors de l'installation initiale
- **Mettez à jour régulièrement** vos fichiers quand votre situation évolue
- **Utilisez le dossier `knowledges/raw/`** pour déposer des documents externes que Claude doit analyser (PDFs, exports, etc.)
- **Validez toujours** les modifications que Claude propose sur vos fichiers

## Projets Actifs
- Gestion de Parc Auto
- Gestion de cantine scolaire
- Gestion de Pharmacie
- Data Plateforme Spark-Kafka


## 📌 En Résumé : L'Essentiel pour Démarrer

Pour l'équipe de développement et les assistants IA, voici ce qu'il faut retenir en un coup d'œil :

*   **L'Écosystème** : Un monorepo unique (propulsé par **Turborepo** et géré via **pnpm**) qui centralise toutes les applications de KLEM (Backend Spring Boot, Web React/MUI, Mobile, et pipelines Big Data Spark/Kafka).
*   **La Règle d'Or Technique** : Typage stricts **TypeScript** côté frontend, architecture découplée, et **zéro modification d'architecture sans document ADR** (généré via `./scripts/create-adr.sh`).
*   **Le Dual Humain-IA** : Un espace de travail optimisé pour la collaboration avec **Claude Code** et **Cursor**. L'IA s'aligne sur vos standards grâce au duo `CLAUDE.md` / `CONTEXT.md` et documente ses livraisons dans `history-log.md`.
*   **Démarrage Éclair** :

    ```bash
    pnpm install && pnpm dev
    ```

*   **Objectif Final** : Chaque brique de code produite ici doit servir notre mission : bâtir des solutions robustes, souveraines et prêtes pour le terrain afin de moderniser les infrastructures publiques et privées en Afrique.

## Pour toute question, contactez le Lead Développeur ou consultez le CLAUDE.md à la racine.

### Contact technique
- Lead Développeur: Yacouba SYLLA
- email/WhatsApp: ciyasyl@gmail.com / +225 0554025100