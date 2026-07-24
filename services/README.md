# services/

Reserved for **independent backend microservices** (Java Spring Boot, Python APIs) once a domain
is actually split out of the current monolith.

## Why this is empty today

Governance rule 4 in `klem-labs-repository/GLOBAL_README.md` is explicit: *"un monolithe modulaire
bien structuré (packages par domaine) est une étape de trajectoire acceptée au stade Prototype,
jamais une destination finale"*. Today's `apps/backend-api` is that accepted monolith stage — it
is **not** being moved here preemptively. Moving it now would rewrite every doc/config that
references `apps/backend-api` (README, CLAUDE.md, ADRs) for zero functional gain, since there is
no second independent service yet to justify the split.

## When to actually use this folder

When a domain inside `apps/backend-api` is decomposed into its own deployable service (per
`shared_architecture/microservices_&_delivery/specifications_techniques.md` in the Labs repo):

1. `git mv apps/backend-api/<domain-package> services/<service-name>` (preserves history).
2. Add `services/<service-name>` to `pnpm-workspace.yaml` — already covered by the `services/*`
   glob added alongside this scaffold, so no further workspace-file edit is needed.
3. Give it its own `package.json` (Node) or `pom.xml`/`build.gradle` (Java) and CI job.
4. Update `apps/backend-api`'s references to call the new service over its API instead of
   in-process.

## Naming convention

`services/<domain>-service` (e.g. `services/billing-service`, matching the `billing-service`
name already used in `klem-labs-repository/platform-devsecops/cycle-devsecops-complet.md` §1,
maillon 10).
