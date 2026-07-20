# Example CLAUDE.md — Monorepo

This example shows how to structure CLAUDE.md files in a monorepo with multiple packages. Claude Code supports nested CLAUDE.md files — the root file provides global conventions while package-level files add specific instructions for each package.

## Repository Structure

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
│   │   └── CLAUDE.md            # Backend & API specific instructions
│   ├── mobile-app/              # Application mobile multiplateforme (Chauffeurs / Livreurs)
│   │   └── CLAUDE.md            # mobile-app specific instructions
│   ├── showcase-website/        # Site vitrine et infographies de KLEM Technologies
│   │   └── CLAUDE.md            # showcase & Website specific instructions
│   └── web-app/                 # Dashboard d'administration Enterprise ERP (React / MUI)
│       └── CLAUDE.md            # Web app-specific instructions
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
├── CLAUDE.md                    # Racine de conevntions et règles globales strictes pour l'IA
└── package.json                 # Racine de configuration des workspaces pnpm```

````markdown
# Klem Platform Monorepo

Turborepo monorepo. Node.js 20, pnpm workspaces.

## Commands (from repo root)

- `pnpm install` — install all dependencies
- `pnpm build` — build all packages (respects dependency order)
- `pnpm test` — run all tests across all packages
- `pnpm lint` — lint all packages
- `pnpm dev` — start all dev servers

### Per-package commands

- `pnpm --filter @klem/backen-api test` — run tests for a single package
- `pnpm --filter @klem/web-app dev` — start dev server for a single package
- `pnpm --filter @klem/mobile-app build` — build a single package

## Architecture

- `apps/backen-api` — API Core Spring Boot 3.x REST API(Spring Boot),Express.js REST API (Node.js backend)
- `apps/web-app` — React.js , Next.js frontend application
- `apps/showcase-website` — html javascript css, utilities
- `apps/mobile-app` — React native
- `infrastructure/` — Terraform IaC (not a Node package)

## Global Conventions

- TypeScript strict mode in all packages
- Named exports only — no default exports
- All packages use the shared ESLint and Prettier configs from the root
- Import from `@klem/shared` for shared types — never duplicate type definitions across packages
- Use workspace protocol for internal deps: `"@klem/shared": "workspace:*"`

## Git

- Conventional commits with scope: `feat(backend-api):`, `fix(web-app):`, `chore(mobile-app):`
- PR titles should include the affected package(s)
- Run `pnpm lint && pnpm typecheck` before committing

## Do NOT

- Do not install dependencies in the root unless they are truly shared tooling
- Do not import directly from another package's src/ — always use the package's public API
- Do not create circular dependencies between packages
````

## apps/backend-api/CLAUDE.md

````markdown
# @klem/backend-api

API Core Spring Boot 3.x REST API(Spring Boot), Express.js REST API. This CLAUDE.md supplements the root CLAUDE.md.

## Commands

- `pnpm --filter @klem/backend-api test` — run API tests
- `pnpm --filter @klem/backend-api dev` — start with hot reload (port 4000)
- `pnpm --filter @klem/backend-api test -- --grep "auth"` — run subset of tests

## Structure

- `src/routes/` — route handlers grouped by resource
- `src/middleware/` — Express middleware (auth, validation, error handling)
- `src/services/` — business logic
- `src/db/` — Prisma schema and migrations

## Conventions

- All routes use the async error wrapper from `src/middleware/asyncHandler.ts`
- Validate request bodies with Zod schemas in `src/schemas/`
- Use Prisma for all database access — no raw SQL
- Database migrations: `pnpm --filter @klem/api prisma migrate dev`
````

## apps/web-app/CLAUDE.md

````markdown
# @klem/web-app

React.js , Next.js 14 frontend with App Router.

## Commands

- `pnpm --filter @klem/web-app dev` — start dev server (port 3000)
- `pnpm --filter @klem/web-app test` — run Vitest tests
- `pnpm --filter @klem/web-app storybook` — start Storybook

## Structure

- `src/app/` — Next.js App Router pages and layouts
- `src/components/` — reusable UI components
- `src/hooks/` — custom React hooks
- `src/lib/` — API client, utilities

## Conventions

- Use Server Components by default — add 'use client' only when needed
- Styles: Tailwind CSS utility classes, no CSS Modules
- Data fetching: Server Components for initial data, TanStack Query for client-side
- Images: always use next/image
````

## apps/mobile-app/CLAUDE.md

````markdown
# @klem/mobile-app

Shared types, utilities, and constants. Imported by api, web, and cli.

## Important

- This package is a dependency of all other packages — breaking changes here affect everything
- Run `pnpm build` after any changes (other packages import the built output)
- Run the full repo test suite after changes: `pnpm test` from the root

## Structure

- `src/types/` — shared TypeScript interfaces and type definitions
- `src/utils/` — pure utility functions (must have zero external dependencies)
- `src/constants/` — shared constants and enums

## Conventions

- Every export must be re-exported from `src/index.ts`
- No runtime dependencies — this package should only export types and pure functions
- 100% test coverage on utility functions
````

## How Nested CLAUDE.md Files Work

When Claude Code operates on a file, it loads:
1. The **root CLAUDE.md** — always loaded, provides global conventions
2. The **nearest CLAUDE.md** in the file's directory ancestry — provides package-specific overrides

For example, when editing `apps/web-app/src/routes/users.ts`, Claude sees both the root conventions (TypeScript strict, conventional commits) and the API-specific conventions (use Prisma, Zod validation, async error wrapper).

Packages without their own CLAUDE.md (like `cli/`) inherit only the root file's instructions.
