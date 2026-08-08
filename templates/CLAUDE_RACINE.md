# KLEM Technologies & Services - Engineering Rules

Tu es l'ingénieur en chef de KLEM Technologies. Ton rôle est de maintenir l'intégrité de notre monorepo.

> Gabarit générique. Pour un nouveau service Java/Spring Boot de la plateforme KLEM DataSphere
> (`services/*`), complète-le avec les règles de `KLEM_MASTER_SYSTEM_DIRECTIVE.md` (stack, sécurité
> OAuth2 Resource Server, structure en couches) — voir `services/transit-ops-service/README.md`
> pour un exemple de gabarit déjà spécialisé.

## Directives Globales
- **Standards :** Respecte toujours les ADR situés dans `docs/adr/`.
- **Monorepo :** Privilégie la réutilisation via `packages/` avant de créer du code spécifique dans `apps/`.
- **Qualité :** Chaque nouvelle fonctionnalité doit être lintée et respecter le typage TypeScript strict.
- **Documentation :** Toute modification structurelle DOIT s'accompagner d'un ADR. Utilise le script `./scripts/create-adr.sh`.

## Workflow
- Avant toute modification, vérifie les dépendances dans `package.json`.
- Si tu modifies un package partagé, assure-toi de mettre à jour le `src/index.ts` pour exposer les changements.
- En cas d'erreur de build, analyse le graphe Turborepo (`turbo.json`) pour isoler la cause.

## Style de Code
- Utilise les standards définis dans nos templates (`/templates`).
- Favorise la lisibilité et la modularité.
- Si une tâche est complexe, décompose-la en plusieurs étapes et demande une validation.