# services/

**Microservices backend indépendants KLEM DataSphere** (Java 21 / Spring Boot 3.x, conformes à
`KLEM_MASTER_SYSTEM_DIRECTIVE.md`). N'est plus vide : trois services réels y vivent.

## Contenu actuel

| Service | Rôle | Statut |
|---|---|---|
| [`core-api/`](./core-api/README.md) | Socle transverse KLEM DataSphere — tenants, identité, autorisation, référentiels communs, audit, orchestration | 5 domaines implémentés, pont Kafka livré, synchronisation Keycloak livrée (voir README du service pour le détail et les points ouverts) |
| [`referentiel-api-service/`](./referentiel-api-service/README.md) | Référentiel réglementaire/procédural KLEM Trade-X + agent d'ingestion `klem_ref_bot` | 4 domaines implémentés, ingestion réelle douanes.ci/commerce.gouv.ci |
| [`transit-ops-service/`](./transit-ops-service/README.md) | Cœur MVP Hinterland-Track (suivi GPS, conteneurs, ETA) | Sprint 0 — squelette de démarrage, aucune logique métier encore |

Note de trajectoire : ces trois services sont nés directement sous `services/` (build neuf suivant
`KLEM_MASTER_SYSTEM_DIRECTIVE.md`), **pas** par extraction d'un domaine depuis `apps/backend-api` —
ce dernier reste un dossier séparé, toujours non démarré (voir `apps/backend-api/README.md`). La
trajectoire « extraire un domaine du monolithe existant vers `services/` » décrite plus bas reste
valide pour `apps/backend-api` le jour où il aura du code, mais n'est pas ce qui s'est passé ici.

## Convention de nommage

`services/<domaine>-service` (ex. `services/billing-service`, correspondant au nom
`billing-service` déjà utilisé dans
`klem-labs-repository/platform-devsecops/cycle-devsecops-complet.md` §1, maillon 10) — sauf
`core-api`, socle transverse sans domaine produit unique, nommé différemment à dessein.

## Si un domaine d'`apps/backend-api` doit un jour être extrait

Selon `shared_architecture/microservices_&_delivery/specifications_techniques.md` dans le dépôt
Labs :

1. `git mv apps/backend-api/<domain-package> services/<service-name>` (préserve l'historique).
2. `services/<service-name>` est déjà couvert par le glob `services/*` de `pnpm-workspace.yaml`,
   aucune modification supplémentaire du fichier de workspace n'est nécessaire.
3. Lui donner son propre `package.json` (Node) ou `pom.xml`/`build.gradle` (Java) et son propre job
   CI.
4. Mettre à jour les références d'`apps/backend-api` pour appeler le nouveau service via son API
   plutôt qu'en process.
