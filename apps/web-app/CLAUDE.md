# Exemple de CLAUDE.md — Monorepo

Cet exemple montre comment structurer des fichiers CLAUDE.md dans un monorepo à plusieurs
packages. Claude Code prend en charge les fichiers CLAUDE.md imbriqués — le fichier racine fournit
les conventions globales tandis que les fichiers de niveau package ajoutent des instructions
spécifiques à chaque package.

## Structure du dépôt

```
KLEM-REPO/
├── .github/                     # Workflows d'intégration et de déploiement continus (CI/CD)
├── .claude/                     # Configuration avancée et extensions pour Claude Code
│   ├── commands/                # Macros et scripts de commandes personnalisées
│   │   ├── startup.md           # Commande /startup : Initialisation et alignement du contexte
│   │   ├── update.md            # Commande /update : Resynchronisation du contexte en cours de session
│   │   └── install.md           # Commande /morning : Routine de démarrage de journée des développeurs
│   └── skills/                  # Capacités et outils spécifiques exécutés par l'IA
│       └── recherche-actualites/# Outil autonome de veille technologique et légale
├── apps/                        # Applications autonomes et déployables (Exécutables)
│   ├── backend-api/             # API Core Spring Boot 3.x & Logique métier 
│   │   └── CLAUDE.md            # Instructions spécifiques Backend & API
│   ├── mobile-app/              # Application mobile multiplateforme (Chauffeurs / Livreurs)
│   │   └── CLAUDE.md            # Instructions spécifiques mobile-app
│   ├── showcase-website/        # Site vitrine et infographies de KLEM Technologies
│   │   └── CLAUDE.md            # Instructions spécifiques showcase & Website
│   └── web-app/                 # Dashboard d'administration Enterprise ERP (React / MUI)
│       └── CLAUDE.md            # Instructions spécifiques Web app
├── collaboration/               # Base de connaissances projet partagée humain/IA
│   ├── context/
│   │   └── CONTEXT.md           # Vision produit, alignement métier et règles globales KLEM
│   ├── doc/
│   │   ├── architectures.md     # Topologie des nœuds, sécurité des flux et infrastructure prod
│   │   ├── specifications.md    # Schémas de base de données PostgreSQL et spécifications API
│   │   └── workflows.md         # Diagrammes d'états et cycles de vie opérationnels du parc
│   └── history/
│       ├── decision-log.md      # Registre historique des arbitrages stratégiques
│       ├── history-log.md       # Journal chronologique des livraisons et des tâches accomplies
│       └── adr/                 # Architecture Decision Records (Format standardisé)
│           ├── 2026-06-18-adoption-monorepo.md
│           └── 2026-06-19-choix-strategie-securite.md
├── knowledges/                  # Renseignements externes et documentation brute
│   ├── output/                  # Rapports générés, logs consolidés et données nettoyées
│   ├── raw/                     # Documents bruts non structurés (PDFs, exports comptables, décrets)
│   └── wiki/                    # Base de connaissances interne et procédures de l'entreprise
├── packages/                    # Modules partagés et réutilisables au sein du monorepo
│   ├── config/                  # Configurations transverses partagées (ESLint, TSConfig, Prettier)
│   ├── ui/                      # Design System KLEM (Composants MUI packagés : KlemTable, KlemButton)
│   └── utils/                   # Librairies de fonctions utilitaires, validateurs et helpers communs
├── scripts/                     # Scripts d'automatisation de l'infrastructure locale
│   ├── README.md                # Guide d'utilisation des outils en ligne de commande
│   └── create-adr.sh            # Script Bash de génération automatisée de fichier ADR
├── templates/                   # Squelettes de code et modèles de fichiers standardisés
├── .cursorrules                 # Instructions de contextualisation pour l'éditeur Cursor
├── .turbo.json                  # Configuration du cache et des pipelines de build Turborepo
├── CLAUDE.md                    # Racine de conventions et règles globales strictes pour l'IA
└── package.json                 # Racine de configuration des workspaces pnpm
```

````markdown
# Klem Platform Monorepo

Monorepo Turborepo. Node.js 20, workspaces pnpm.

## Commandes (depuis la racine du dépôt)

- `pnpm install` — installe toutes les dépendances
- `pnpm build` — build tous les packages (respecte l'ordre des dépendances)
- `pnpm test` — exécute tous les tests sur tous les packages
- `pnpm lint` — lint tous les packages
- `pnpm dev` — démarre tous les serveurs de dev

### Commandes ciblées par package

- `pnpm --filter @klem/backen-api test` — exécute les tests d'un seul package
- `pnpm --filter @klem/web-app dev` — démarre le serveur de dev d'un seul package
- `pnpm --filter @klem/mobile-app build` — build un seul package

## Architecture

- `apps/backen-api` — API Core Spring Boot 3.x REST API (Spring Boot), Express.js REST API (backend Node.js)
- `apps/web-app` — application frontend React.js, Next.js
- `apps/showcase-website` — html javascript css, utilitaires
- `apps/mobile-app` — React Native
- `infrastructure/` — IaC Terraform (pas un package Node)

## Conventions globales

- Mode strict TypeScript dans tous les packages
- Exports nommés uniquement — pas d'export par défaut
- Tous les packages utilisent les configs ESLint et Prettier partagées de la racine
- Importer `@klem/shared` pour les types partagés — ne jamais dupliquer de définitions de type
  entre packages
- Utiliser le protocole workspace pour les dépendances internes :
  `"@klem/shared": "workspace:*"`

## Git

- Conventional commits avec scope : `feat(backend-api):`, `fix(web-app):`, `chore(mobile-app):`
- Les titres de PR doivent inclure le(s) package(s) concerné(s)
- Exécuter `pnpm lint && pnpm typecheck` avant de committer

## À NE PAS faire

- Ne pas installer de dépendances à la racine sauf s'il s'agit vraiment d'outillage partagé
- Ne pas importer directement depuis le `src/` d'un autre package — toujours passer par l'API
  publique du package
- Ne pas créer de dépendances circulaires entre packages
````

## apps/backend-api/CLAUDE.md

````markdown
# @klem/backend-api

API Core Spring Boot 3.x REST API (Spring Boot), Express.js REST API. Ce CLAUDE.md complète le
CLAUDE.md racine.

## Commandes

- `pnpm --filter @klem/backend-api test` — exécute les tests de l'API
- `pnpm --filter @klem/backend-api dev` — démarre avec hot reload (port 4000)
- `pnpm --filter @klem/backend-api test -- --grep "auth"` — exécute un sous-ensemble de tests

## Structure

- `src/routes/` — gestionnaires de routes groupés par ressource
- `src/middleware/` — middleware Express (auth, validation, gestion d'erreurs)
- `src/services/` — logique métier
- `src/db/` — schéma Prisma et migrations

## Conventions

- Toutes les routes utilisent le wrapper d'erreur asynchrone de `src/middleware/asyncHandler.ts`
- Valider les corps de requête avec des schémas Zod dans `src/schemas/`
- Utiliser Prisma pour tout accès base de données — pas de SQL brut
- Migrations de base de données : `pnpm --filter @klem/api prisma migrate dev`
````

## apps/web-app/CLAUDE.md

````markdown
# @klem/web-app

Frontend React.js, Next.js 14 avec App Router.

## Commandes

- `pnpm --filter @klem/web-app dev` — démarre le serveur de dev (port 3000)
- `pnpm --filter @klem/web-app test` — exécute les tests Vitest
- `pnpm --filter @klem/web-app storybook` — démarre Storybook

## Structure

- `src/app/` — pages et layouts App Router de Next.js
- `src/components/` — composants UI réutilisables
- `src/hooks/` — hooks React personnalisés
- `src/lib/` — client API, utilitaires

## Conventions

- Utiliser les Server Components par défaut — ajouter 'use client' seulement si nécessaire
- Styles : classes utilitaires Tailwind CSS, pas de CSS Modules
- Récupération de données : Server Components pour les données initiales, TanStack Query côté
  client
- Images : toujours utiliser next/image
````

## apps/mobile-app/CLAUDE.md

````markdown
# @klem/mobile-app

Types, utilitaires et constantes partagés. Importé par api, web et cli.

## Important

- Ce package est une dépendance de tous les autres packages — les changements cassants ici
  affectent tout
- Exécuter `pnpm build` après tout changement (les autres packages importent le résultat buildé)
- Exécuter la suite de tests complète du dépôt après tout changement : `pnpm test` depuis la racine

## Structure

- `src/types/` — interfaces et définitions de types TypeScript partagés
- `src/utils/` — fonctions utilitaires pures (doivent avoir zéro dépendance externe)
- `src/constants/` — constantes et enums partagés

## Conventions

- Chaque export doit être ré-exporté depuis `src/index.ts`
- Aucune dépendance runtime — ce package ne doit exporter que des types et des fonctions pures
- Couverture de test à 100 % sur les fonctions utilitaires
````

## Fonctionnement des fichiers CLAUDE.md imbriqués

Lorsque Claude Code opère sur un fichier, il charge :
1. Le **CLAUDE.md racine** — toujours chargé, fournit les conventions globales
2. Le **CLAUDE.md le plus proche** dans l'arborescence parente du fichier — fournit les
   surcharges spécifiques au package

Par exemple, lors de l'édition de `apps/web-app/src/routes/users.ts`, Claude voit à la fois les
conventions racine (TypeScript strict, conventional commits) et les conventions spécifiques à
l'API (utiliser Prisma, validation Zod, wrapper d'erreur asynchrone).

Les packages sans leur propre CLAUDE.md (comme `cli/`) héritent uniquement des instructions du
fichier racine.
