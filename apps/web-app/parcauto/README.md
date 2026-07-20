# Gestion de Parc Auto - KLEM Technologies & Services

## Présentation
Ce projet constitue le cœur métier de la gestion de flotte automobile pour KLEM Technologies. Il permet le pilotage des missions, le suivi de la maintenance des véhicules et l'automatisation des workflows via l'intégration d'agents IA (ai.koog).

Monorepo d'entreprise pour la gestion globale de parc automobile (Suivi, Course, Location, Livraison, Maintenance, Finances).

## 🚀 Prérequis Système
- **Node.js** : v18 ou supérieure
- **Java JDK** : 17 (Temurin recommandé)
- **Docker & Docker Compose**
- **Gestionnaire de packages** : `pnpm` (recommandé pour la gestion du monorepo)

## 📁 Architecture du Workspace
Le projet fait partie du monorepo KLEM-Core. Il consomme les packages partagés (`@klem/ui`, `@klem/utils`) pour garantir une cohérence visuelle et fonctionnelle avec les autres applications de l'entreprise.

- `apps/` : Les livrables et applications autonomes déployables.
- `packages/` : Les briques de code et configurations partagées entre les applications.
- `claude-collaboration/` : Gouvernance, mémoire et invites (prompts) de l'IA.

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

*   [ADR 001 - Choix de l'architecture de gestion de flotte](/docs/adr/2026-06-18-choix-gestion-parc-workflow.md)
*   [ADR 002 - Stratégie d'intégration de l'agent IA (ai.koog)](/docs/adr/2026-06-XX-integration-ia-koog.md)

*(Si vous prenez une nouvelle décision architecturale, veuillez créer un nouveau fichier ADR dans `docs/adr/` et mettre à jour cette liste).*

## Workflow de développement
- **Branches :** `feature/nom-fonctionnalité` -> `develop` -> `main`.
- **Règles :** 
    - Le typage TypeScript est strict.
    - Tout composant UI complexe doit être migré vers `packages/ui` s'il est réutilisable.
    - Consulter `.cursorrules` local avant de lancer Claude Code.

## Contact
- **Lead Ingénieur :** Yacouba SYLLA
- **Support technique :** Yacouba SYLLA
