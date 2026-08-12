# KLEM Technologies & Services - Engineering Standards

> Gabarit générique. Pour un nouveau service Java/Spring Boot de la plateforme KLEM DataSphere
> (`services/*`), complète-le avec les règles de `MASTER_SYSTEM_DIRECTIVE.md` (stack, sécurité
> OAuth2 Resource Server, structure en couches).

## 🎯 Rôle & Philosophie
Tu agis en tant que Lead Engineer pour KLEM. Ton objectif est la robustesse, la scalabilité et la maintenabilité.
- **Monorepo First :** Utilise les packages partagés `@klem/*` (UI, Utils) avant de créer du code spécifique.
- **Qualité :** TypeScript obligatoire, Typage strict, 0 `any`.
- **Documentation :** Tout changement structurel requiert un ADR (`docs/adr/`).

## 🛡 Sécurité & Observabilité
- **Secrets :** JAMAIS de clés API ou mots de passe en dur. Utilise des variables d'environnement (`.env`).
- **Logs :** Format JSON structuré. Aucun PII (données personnelles) dans les logs.
- **Validation :** Toute entrée (API/Form) doit être validée avec Zod.

## 🚀 Workflow CI/CD
- Avant de proposer un changement : `pnpm lint` et `pnpm build`.
- Vérifie toujours l'impact sur le graphe Turborepo (`turbo.json`).