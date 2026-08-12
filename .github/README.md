# `.github/`

## Workflows

- **`ci.yml`** — CI principale, sur push/PR vers `main`/`develop` : installe pnpm/Node 20 +
  Java 17 et 21 (deux JDK côte à côte, un service peut choisir l'un ou l'autre), vérifie qu'au
  moins un ADR existe dans `collaboration/history/adr/`, lance `pnpm turbo run lint test build`
  pour les packages Node, puis build/teste séparément en Maven `apps/web-app/cantine-connect/server-backend`
  (Java 17), `services/transit-ops-service` et `services/referentiel-api-service` (Java 21).
  **Ne couvre pas encore `services/core-api`** — absent de ce workflow malgré du code réel, à
  ajouter.
- **`klem-ref-bot-scheduled.yml`** — exécution planifiée quotidienne (cron `0 4 * * *`) de l'agent
  d'ingestion `klem_ref_bot` (voir `services/referentiel-api-service/ingestion/klem_ref_bot/README.md`)
  contre douanes.ci puis commerce.gouv.ci, avec persistance du checkpoint entre runs via
  `actions/cache`. No-op tant que le secret `KLEM_REF_BOT_DATABASE_URL` n'est pas configuré sur ce
  dépôt GitHub.

## Autres fichiers

- **`PULL_REQUEST_TEMPLATE.md`** — gabarit de PR (description, type de changement, checklist de
  validation).
