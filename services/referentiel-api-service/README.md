# referentiel-api-service

Service unique MVP de **KLEM Trade-X** (référentiel TEC, codes SH, devises BCEAO, frais, règles
douanières, workflow d'édition/validation).

> **Statut : Sprint 0 — squelette de démarrage.** Ce module ne contient aucune logique métier ni
> entité JPA. Il prouve que le build, la sécurité OAuth2 Resource Server, l'observabilité et
> l'OpenAPI sont correctement câblés avant la première tranche verticale (base de données →
> service → API → interface), conformément à la méthodologie décrite dans
> `klem-labs-repository/shared_architecture/microservices_&_delivery/specifications_techniques.md`.

Référence fonctionnelle et technique complète :
`klem-labs-repository/projects/08_klem_trade_x/specifications_techniques.md`.

Décision et périmètre d'application de la directive maître :
`klem-projects-repository/collaboration/history/adr/2026-08-08-adoption-directive-maitre-datasphere-perimetre.md`.

## Stack

Conforme à `KLEM_MASTER_SYSTEM_DIRECTIVE.md` : Java 21 LTS, Spring Boot 3.3.x, Spring Security
OAuth2 Resource Server (JWT), Spring Data JPA, PostgreSQL (schéma `klem_trade_x`), Flyway,
springdoc-openapi, Actuator, JUnit 5 + Testcontainers + ArchUnit. Pas de FastAPI — Trade-X est
explicitement exclu du périmètre Python Data/IA (`GLOBAL_README.md` règle de gouvernance 3).

## Modules du MVP (packages par domaine)

Chaque module suit la structure en couches `api/controller` → `application/service` →
`domain/model` → `infrastructure/persistence` (directive §5). Actuellement vides (placeholders
`.gitkeep`) — à remplir une tranche verticale à la fois :

- `textereglementaire` — textes réglementaires (statuts proposée/en_revision/publiee/rejetee)
- `procedure` — procédures métier
- `operationcommerce` — opérations de commerce extérieur
- `documentrequis` — documents requis par opération/procédure

Le frontend BFF Next.js et l'agent d'ingestion `klem_ref_bot` (batch offline) sont hors périmètre
de ce scaffold backend.

## Commandes

```bash
./mvnw clean verify        # build + tests (Testcontainers PostgreSQL, Docker requis)
./mvnw spring-boot:run     # démarrage local (profil "local")
curl http://localhost:8082/actuator/health
```

## Configuration

Variables d'environnement (aucun secret en dur, voir `application.yml`) :

| Variable | Rôle |
|---|---|
| `OIDC_ISSUER_URI` | Issuer du fournisseur d'identité OAuth2 (obligatoire) |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Connexion PostgreSQL |
| `SERVER_PORT` | Port HTTP (par défaut `8082`) |
