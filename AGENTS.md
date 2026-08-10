# klem-projects-repository — Agent Instructions

## Hiérarchie

Hérite de la directive racine du workspace (`../AGENTS.md`). En cas de conflit :
instruction utilisateur → `AGENTS.md` local au service/app → cet `AGENTS.md` →
`CLAUDE.md`/`SYSTEM_INSTRUCTIONS.md`/`KLEM_MASTER_SYSTEM_DIRECTIVE.md` (selon la strate, voir
ci-dessous) → `../knowledge/*`.

## Rôle de ce dépôt

Monorepo pnpm/Turborepo **de production**. Code réel une fois qu'un projet a atteint le statut
« Pilote/Livré » côté `klem-labs-repository` (règle de gouvernance n°7). Ne jamais démarrer
l'implémentation de code produit ici pour un projet encore en cadrage côté Labs.

## Ce fichier ne duplique pas les standards de code — il pointe vers eux

Trois documents détaillés font déjà autorité chacun sur leur strate ; ce fichier ne les répète pas
(risque de divergence silencieuse) :

| Strate | Document faisant autorité | Résumé |
|---|---|---|
| Services `services/*` neufs (KLEM DataSphere : Hinterland-Track, Trade-X, KLEM Copilot) | [`KLEM_MASTER_SYSTEM_DIRECTIVE.md`](./KLEM_MASTER_SYSTEM_DIRECTIVE.md) | Java 21 LTS, Spring Boot 3.x, OAuth2 Resource Server/JWT |
| Apps clients existantes (cantine-connect, parcauto, backend-api/FleetControl, clinic, pharmacie) | [`CLAUDE.md`](./CLAUDE.md) | Java 17, sessions `JSESSIONID`, React/MUI |
| Couche présentation clients mobile + web (`apps/web-app/*`, `apps/mobile-app/*`) | [`SYSTEM_INSTRUCTIONS.md`](./SYSTEM_INSTRUCTIONS.md) | Expo SDK 51+/Expo Router v3, Universal App First |

Avant toute modification : identifier dans quelle ligne du tableau se trouve le fichier concerné,
puis lire le document correspondant en entier — pas seulement ce résumé.
Détail synthétique inter-strates : [`../knowledge/03-architecture-principles.md`](../knowledge/03-architecture-principles.md).

## Commandes racine (pnpm + Turborepo — jamais npm/yarn)

```bash
pnpm install                          # installe toutes les dépendances du monorepo
pnpm dev                              # lance toutes les apps en parallèle
pnpm build                            # build global
pnpm lint                             # ESLint / Checkstyle
pnpm format                           # Prettier / règles Java
pnpm test                             # Vitest / JUnit 5
```

### Commandes ciblées (filtrage Turbo)

```bash
pnpm --filter backend-api build
pnpm --filter web-app dev
pnpm --filter @klem/ui add <package>
```

Vérifier `package.json`/`pom.xml`/`mvnw` du service concerné avant de supposer une commande
disponible — un service `services/*` récent (ex. `core-api`) peut ne pas encore être scaffoldé ;
voir son `AGENTS.md` local le cas échéant.

## Processus de clôture de tâche (obligatoire, `CLAUDE.md` §3)

1. **Validation technique locale** — `pnpm lint && pnpm build`, aucun warning bloquant.
2. **ADR si applicable** — nouvelle bibliothèque, changement de modèle de données ou de flux
   réseau → `./scripts/create-adr.sh "titre-de-la-decision"`, complété (contexte, alternatives,
   conséquences) dans `collaboration/history/adr/`.
3. **Journalisation** — entrée datée dans `collaboration/history/history-log.md` (fonctionnalité,
   statut, fichiers modifiés, synthèse technique).
4. **Message de commit Conventional Commits** — `feat(scope):`, `fix(scope):`, `docs(scope):`.

Une tâche n'est pas terminée si l'une de ces quatre étapes manque — voir aussi les 9 critères de
finition dans [`../knowledge/10-production-reliability.md`](../knowledge/10-production-reliability.md).

## Toujours

- vérifier la strate technique concernée avant d'appliquer une convention de code ;
- dériver le `tenant_id` du contexte serveur/JWT authentifié, jamais du frontend ;
- séparer DTO et entité JPA, valider les entrées (Bean Validation), normaliser les erreurs ;
- couvrir toute feature backend par les tests minimums de sa strate (Testcontainers pour
  PostgreSQL/Kafka/Redis/ClickHouse concernés) ;
- exécuter le processus de clôture en 4 étapes ci-dessus avant de rendre la main.

## Demander confirmation avant

- de supprimer des données ou de modifier une migration déjà déployée en production ;
- de changer un contrat d'API public, l'authentification, ou une règle d'isolation tenant ;
- d'introduire une nouvelle base de données, Kafka, ou un nouveau microservice ;
- de modifier un workflow CI/CD ou la configuration de production ;
- de faire un `git push --force`, un rebase publié, ou tout rollback en production.

## Ne jamais

- committer un secret, exposer un token dans un log, ou stocker une clé privée dans un bundle
  client (`EXPO_PUBLIC_*` uniquement côté Expo) ;
- accepter du SQL brut ou du JSON non validé venant du frontend ;
- faire confiance à un `tenant_id`, un rôle ou une permission envoyés par le client ;
- exposer une entité JPA directement par l'API ;
- utiliser ClickHouse comme base transactionnelle, ou Redis comme unique source de vérité métier ;
- considérer une redirection frontend comme preuve d'un paiement réussi ;
- appliquer le standard JWT/DataSphere à une app cliente existante (ou l'inverse) sans ADR.

## Où trouver quoi

- Standards de code détaillés → tableau des trois strates ci-dessus.
- Sécurité et multi-tenancy → [`../knowledge/07-security-multitenancy.md`](../knowledge/07-security-multitenancy.md).
- Data platform (PostgreSQL/ClickHouse/Kafka/Redis) → [`../knowledge/06-data-platform.md`](../knowledge/06-data-platform.md).
- DevOps/CI-CD → [`../knowledge/09-devops-gitops.md`](../knowledge/09-devops-gitops.md).
- ADR de ce dépôt → [`collaboration/history/adr/`](./collaboration/history/adr/), voir aussi
  [`../knowledge/12-decision-records.md`](../knowledge/12-decision-records.md) pour les écarts
  connus (ADR vides malgré un nom de fichier renseigné — vérifier le contenu réel avant de citer).
