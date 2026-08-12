# klem-projects-repository — Agent Instructions

## Hiérarchie

Hérite de `../AGENTS.md`. Conflit : instruction utilisateur → `AGENTS.md` local au service/app →
cet `AGENTS.md` → `CLAUDE.md`/`SYSTEM_INSTRUCTIONS.md`/`MASTER_SYSTEM_DIRECTIVE.md` (selon la
strate) → `../knowledge/*`.

## Rôle de ce dépôt

Monorepo pnpm/Turborepo **de production**. Code réel uniquement une fois qu'un projet a atteint le
statut « Pilote/Livré » côté `klem-labs-repository` (règle de gouvernance n°7). Ne jamais démarrer
l'implémentation produit ici pour un projet encore en cadrage côté Labs.

## Trois strates — chacune a son document d'autorité (ne pas dupliquer, pointer vers)

| Strate | Document faisant autorité | Résumé |
|---|---|---|
| Services `services/*` neufs (KLEM DataSphere : Hinterland-Track, Trade-X, KLEM Copilot) | [`MASTER_SYSTEM_DIRECTIVE.md`](./MASTER_SYSTEM_DIRECTIVE.md) | Java 21 LTS, Spring Boot 3.x, OAuth2 Resource Server/JWT |
| Apps clients existantes (cantine-connect, parcauto, backend-api/FleetControl, clinic, pharmacie) | [`CLAUDE.md`](./CLAUDE.md) | Java 17, sessions `JSESSIONID`, React/MUI |
| Couche présentation mobile + web (`apps/web-app/*`, `apps/mobile-app/*`) | [`SYSTEM_INSTRUCTIONS.md`](./SYSTEM_INSTRUCTIONS.md) | Expo SDK 51+/Expo Router v3, Universal App First |

Identifier la strate du fichier concerné avant toute modification, puis lire le document
correspondant en entier. Détail inter-strates : [`../knowledge/03-architecture-principles.md`](../knowledge/03-architecture-principles.md).

## Commandes racine (pnpm + Turborepo — jamais npm/yarn)

```bash
pnpm install                          # toutes les dépendances du monorepo
pnpm dev / pnpm build / pnpm lint / pnpm format / pnpm test
pnpm --filter backend-api build       # filtrage Turbo par app/package
```

Vérifier `package.json`/`pom.xml`/`mvnw` du service avant de supposer une commande disponible —
un service `services/*` récent peut ne pas être scaffoldé (voir son `AGENTS.md` local).

## Processus de clôture de tâche (obligatoire, `CLAUDE.md` §3)

1. **Validation locale** — `pnpm lint && pnpm build`, aucun warning bloquant.
2. **ADR si applicable** (nouvelle lib, modèle de données, flux réseau) — `./scripts/create-adr.sh
   "titre"`, complété dans `collaboration/history/adr/`.
3. **Journalisation** — entrée datée dans `collaboration/history/history-log.md`.
4. **Commit Conventional Commits** — `feat(scope):`, `fix(scope):`, `docs(scope):`.

Tâche non terminée si une étape manque — voir aussi [`../knowledge/10-production-reliability.md`](../knowledge/10-production-reliability.md).

## Toujours

- identifier la strate technique avant d'appliquer une convention de code ;
- dériver le `tenant_id` du contexte serveur/JWT authentifié, jamais du frontend ;
- séparer DTO et entité JPA, valider les entrées, normaliser les erreurs ;
- couvrir les features backend par les tests minimums de leur strate (Testcontainers) ;
- exécuter le processus de clôture en 4 étapes avant de rendre la main.

## Demander confirmation avant

- suppression de données ou modification d'une migration déjà déployée ;
- changement de contrat d'API public, d'authentification, ou d'isolation tenant ;
- nouvelle base de données, Kafka, ou nouveau microservice ;
- modification d'un workflow CI/CD ou de la configuration production ;
- `git push --force`, rebase publié, ou rollback en production.

## Ne jamais

- committer un secret ou exposer un token en log (`EXPO_PUBLIC_*` uniquement côté Expo) ;
- accepter du SQL brut ou du JSON non validé venant du frontend ;
- faire confiance à un `tenant_id`/rôle/permission envoyé par le client ;
- exposer une entité JPA directement par l'API ;
- utiliser ClickHouse comme base transactionnelle, ou Redis comme source de vérité métier ;
- appliquer le standard JWT/DataSphere à une app cliente existante (ou l'inverse) sans ADR.

## Où trouver quoi

- Sécurité et multi-tenancy → [`../knowledge/07-security-multitenancy.md`](../knowledge/07-security-multitenancy.md).
- Data platform → [`../knowledge/06-data-platform.md`](../knowledge/06-data-platform.md).
- DevOps/CI-CD → [`../knowledge/09-devops-gitops.md`](../knowledge/09-devops-gitops.md).
- ADR de ce dépôt → [`collaboration/history/adr/`](./collaboration/history/adr/) — voir
  [`../knowledge/12-decision-records.md`](../knowledge/12-decision-records.md) pour les écarts connus.
