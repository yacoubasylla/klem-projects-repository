# transit-ops-service

Service cœur MVP de **Hinterland-Track** (suivi GPS, conteneurs, ETA, retards, risques de
surestaries — corridors Abidjan-Bamako et Abidjan-Ouagadougou).

> **Statut : Sprint 0 — squelette de démarrage.** Ce module ne contient aucune logique métier ni
> entité JPA. Il prouve que le build, la sécurité OAuth2 Resource Server, l'observabilité et
> l'OpenAPI sont correctement câblés avant la première tranche verticale (base de données →
> service → API → interface), conformément à la méthodologie décrite dans
> `klem-labs-repository/shared_architecture/standards/microservices_&_delivery/specifications_techniques.md`.

Référence fonctionnelle et technique complète :
`klem-labs-repository/projects/02_hinterland_track/specifications_techniques.md`.

Décision et périmètre d'application de la directive maître :
`klem-projects-repository/collaboration/history/adr/2026-08-08-adoption-directive-maitre-datasphere-perimetre.md`.

## Stack

Conforme à `MASTER_SYSTEM_DIRECTIVE.md` : Java 21 LTS, Spring Boot 3.3.x, Spring Security
OAuth2 Resource Server (JWT), Spring Data JPA, PostgreSQL, Flyway, springdoc-openapi, Actuator,
JUnit 5 + Testcontainers + ArchUnit.

## Modules du MVP (packages par domaine)

Chaque module suit la structure en couches `api/controller` → `application/service` →
`domain/model` → `infrastructure/persistence` (directive §5). Actuellement vides (placeholders
`.gitkeep`) — à remplir une tranche verticale à la fois :

- `enrollment` — enregistrement des acteurs (transitaires, transporteurs)
- `transitcase` — dossier de transit (`transit_case`, entité centrale du domaine)
- `control` — contrôles et checkpoints
- `apurement` — apurement des dossiers de transit
- `shipment` — conteneurs, camions, expéditions

Hors périmètre de ce scaffold (services séparés prévus par la spec MVP, non démarrés) :
`seal-gateway-service` (passerelle MQTT scellés), `geofencing-service`, `compliance-bridge-service`.

## Commandes

```bash
./mvnw clean verify        # build + tests (Testcontainers PostgreSQL, Docker requis)
./mvnw spring-boot:run     # démarrage local (profil "local")
curl http://localhost:8081/actuator/health
```

## Configuration

Variables d'environnement (aucun secret en dur, voir `application.yml`) :

| Variable | Rôle |
|---|---|
| `OIDC_ISSUER_URI` | Issuer du fournisseur d'identité OAuth2 (obligatoire) |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Connexion PostgreSQL |
| `SERVER_PORT` | Port HTTP (par défaut `8081`) |
