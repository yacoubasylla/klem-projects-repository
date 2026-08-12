# Gestion de Parc Auto - KLEM Technologies & Services

## Présentation
Ce projet vise à devenir le cœur métier de la gestion de flotte automobile pour KLEM Technologies (pilotage des missions, suivi de la maintenance des véhicules, automatisation des workflows via l'intégration d'agents IA `ai.koog`) — **c'est aujourd'hui un scaffold non fonctionnel**, pas une application livrée : `src/App.tsx` est le seul fichier source, il n'existe ni `tsconfig.json` ni `vite.config.js`, et `package.json` désactive volontairement `build`/`lint` en attendant que le scaffold soit finalisé (voir les scripts du `package.json`).

Périmètre métier visé (non implémenté) : Suivi, Course, Location, Livraison, Maintenance, Finances.

## 🚀 Prérequis Système
- **Node.js** : v18 ou supérieure
- **Java JDK** : 17 (Temurin recommandé)
- **Docker & Docker Compose**
- **Gestionnaire de packages** : `pnpm` (recommandé pour la gestion du monorepo)

## 📁 Architecture du Workspace
Le projet fait partie du monorepo KLEM-Core. Il consomme les packages partagés (`@klem/ui`, `@klem/utils`) pour garantir une cohérence visuelle et fonctionnelle avec les autres applications de l'entreprise.

- `apps/` : Les livrables et applications autonomes déployables.
- `packages/` : Les briques de code et configurations partagées entre les applications.
- `collaboration/` : Gouvernance, mémoire et invites (prompts) de l'IA (le dossier réel s'appelle `collaboration/`, pas `claude-collaboration/`).

## 🛠️ Commandes Globales
Exécuter ces commandes depuis la racine du monorepo :
- Installer toutes les dépendances : `pnpm install`
- Lancer la base de données locale (PostgreSQL) : `docker-compose up -d`
- Lancer tout l'écosystème en mode de développement : `pnpm turbo run dev`
- Build complet pour la production : `pnpm turbo run build`
- Valider le code (Lint & Format) : `pnpm run lint`

## 🚦 Règle de Contribution (Rappel)
Toute modification doit faire l'objet d'une branche `feat/` ou `bugfix/` et d'une Pull Request (PR) validée par le second développeur avant fusion sur `develop`.

## Démarrage rapide
1. **Installation :** `pnpm install`
2. **Développement :** `pnpm dev`
3. **Variables d'environnement :** Copier `.env.example` en `.env` et configurer les accès API.

## Décisions Architecturales (ADR)
Toutes les décisions techniques majeures ayant impacté le développement de ce module sont documentées et accessibles à la racine du monorepo. Nous vous invitons à les consulter avant toute modification structurelle :

*   [ADR - Choix de l'architecture de gestion de flotte](../../../collaboration/history/adr/2026-06-18-choix-gestion-parc-workflow.md)
*   Stratégie d'intégration de l'agent IA (`ai.koog`) — pas encore rédigée (aucun fichier ADR
    correspondant n'existe à ce jour ; à créer avant d'implémenter réellement l'intégration).

*(Pour une nouvelle décision architecturale, créer un fichier ADR dans
`collaboration/history/adr/` — soit ici pour une décision propre à parcauto, soit à la racine du
monorepo pour une décision transverse — et mettre à jour cette liste).*

## Workflow de développement
- **Branches :** `feature/nom-fonctionnalité` -> `develop` -> `main`.
- **Règles :** 
    - Le typage TypeScript est strict.
    - Tout composant UI complexe doit être migré vers `packages/ui` s'il est réutilisable.
    - Consulter `.cursorrules` local avant de lancer Claude Code.

## Contact
- **Lead Ingénieur :** Yacouba SYLLA
- **Support technique :** Yacouba SYLLA
