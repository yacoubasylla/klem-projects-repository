# services/core-api — Agent Instructions

## Statut réel — à revérifier avant toute action

Sprint 0 + six tranches verticales implémentées : `tenant`, `identity` (provisionnement JIT),
`referential` (lecture seule, seed Flyway), `authorization` (enregistrement des rôles),
`audit` (écoute les événements des autres domaines), `workflow` (orchestration transactionnelle,
ex. `onboardTenant`). Pont Kafka livré (`PortfolioEventPublisher`, événements de domaine → cluster
Kafka partagé). Client Keycloak Admin API livré (`KeycloakRoleSyncClient`/`KeycloakRoleSyncPublisher`)
mais **`KeycloakRoleSyncIntegrationTest` n'a jamais tourné avec succès dans un environnement réel**
— priorité n°1 avant de faire confiance à cette synchronisation en production. Ne pas se fier à ce
résumé seul : lire l'en-tête de [`README.md`](./README.md) et `git log` avant toute hypothèse sur
l'état d'un domaine.

## Périmètre — voir `README.md` pour le détail complet

`core-api` est le **socle métier central** de KLEM DataSphere — pas un agrégat de toutes les
fonctionnalités de la plateforme.

**Inclus** : authentification (support — résolution du contexte JWT, pas réimplémentation
OAuth2/OIDC qui reste Keycloak), tenants, utilisateurs/organisations, rôles/permissions (RBAC),
référentiels **communs** (pas les référentiels métier d'un seul produit), audit, contrats API de
base (erreur/pagination/`requestId`), orchestration des workflows critiques **transverses**.

**Exclu** : UI, RAG/IA, analytics lourds, reporting spécialisé, intégrations métier non centrales,
scripts R&D, infrastructure non liée au domaine, référentiels métier spécialisés d'un produit
(TEC/SH → `referentiel-api-service`).

`core-api` ne dépend d'aucun autre service `services/*` — la dépendance va toujours des services
produit vers `core-api`, jamais l'inverse. Les autres services lisent les permissions via les
claims JWT (synchronisées vers Keycloak), pas d'appel synchrone à `core-api` — voir ADR
[`2026-08-10-autorisation-core-api-claims-jwt-vs-appel-synchrone.md`](../../collaboration/history/adr/2026-08-10-autorisation-core-api-claims-jwt-vs-appel-synchrone.md).

## Patron déjà appliqué aux six domaines — le reproduire, ne pas en inventer un nouveau

Frères déjà scaffoldés au même patron `KLEM_MASTER_SYSTEM_DIRECTIVE.md` §5 :
`services/transit-ops-service` (Hinterland-Track), `services/referentiel-api-service` (Trade-X).

Trois motifs de dépendance inter-domaine établis, vérifiés par `PackageBoundaryRulesTest`
(14 règles — détail de chaque exemption dans README.md « Raffinements d'architecture ») :
- **Lecture peer-à-peer** (`authorization → tenant/identity`) : via une méthode étroite de
  `application.service` (ex. `TenantService.tenantExists`), jamais via `domain.model`/`domain.exception`.
- **Écoute passive** (`audit ← domain.event`) : dépendre uniquement de la classe d'événement via
  `@TransactionalEventListener`, jamais de `domain.model`/`application`/`infrastructure`.
- **Orchestration privilégiée** (`workflow → tenant/identity/authorization`) : seul `workflow`
  peut lire `domain.model` d'un autre domaine, car rien ne dépend de `workflow` en retour.

- **Stack** : Java 21 LTS, Spring Boot 3.3.x, Spring Security OAuth2 Resource Server (JWT), Spring
  Data JPA, PostgreSQL, Flyway, springdoc-openapi, Actuator, JUnit 5 + Testcontainers + ArchUnit.
- **Structure package-by-feature** : `api/{controller,request,response}`, `application/{service,port}`,
  `domain/{model,event,exception}`, `infrastructure/{persistence,messaging}`.
- **Configuration** : `OIDC_ISSUER_URI`, `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`, `SERVER_PORT` en
  variables d'environnement — jamais de secret en dur dans `application.yml`.

## Commandes

```bash
./mvnw clean verify        # build + tests (les tests Testcontainers exigent Docker API >= 1.40)
./mvnw -Dtest='!*ApplicationTests,!*IntegrationTest' test   # tests rapides sans Docker
./mvnw spring-boot:run     # démarrage local (profil "local"), port 8083
curl http://localhost:8083/actuator/health
```

## Priorités (rappel `../../../knowledge/03-architecture-principles.md` §23)

Sécurité > isolation tenant > exactitude réglementaire > intégrité transactionnelle >
observabilité > résilience > simplicité > performance mesurée > vitesse de livraison >
sophistication technique.

## Toujours

- controllers minces, logique métier en `application`/`domain`, DTOs séparés des entités JPA ;
- migrations Flyway versionnées, aucune modification manuelle non tracée en production ;
- `tenant_id` dérivé du JWT/contexte serveur, jamais accepté depuis le client ;
- tests Testcontainers pour toute dépendance réelle (PostgreSQL, Kafka, Keycloak selon le besoin) ;
- OpenAPI tenue à jour en même temps que le code.

## Demander confirmation avant

- d'introduire une dépendance à un autre service `services/*` ;
- de modifier le contrat OAuth2/JWT (`SecurityConfig`) hérité de `KLEM_MASTER_SYSTEM_DIRECTIVE.md` ;
- de déclarer la synchronisation Keycloak « fonctionnelle » sans avoir fait tourner
  `KeycloakRoleSyncIntegrationTest` dans un environnement Docker réel.

## Ne jamais

- dupliquer une responsabilité déjà couverte par `transit-ops-service` ou `referentiel-api-service`
  sans justification écrite ;
- exposer une entité JPA directement par l'API ;
- accepter un `tenant_id`, un rôle ou une permission transmis par le client sans vérification serveur.
