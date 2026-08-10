# services/core-api — Agent Instructions

## Statut réel de ce dossier — à vérifier avant toute action

Sprint 0 scaffoldé (câblage sécurité/OpenAPI/Actuator) **et les six tranches verticales cadrées
toutes implémentées** : `tenant`, `identity` (provisionnement JIT), `referential` (lecture seule,
seed Flyway), `authorization` (système d'enregistrement des rôles — **pas** de synchronisation
Keycloak, voir README.md « Lacune connue »), `audit` (écoute les événements des autres domaines,
journal append-only paginé), `workflow` (orchestration transactionnelle `tenant` → `identity` →
`authorization`, ex. `onboardTenant`). **Le pont Kafka portefeuille est également livré**
(`audit.infrastructure.messaging.PortfolioEventPublisher`). 85 tests, 77 exécutables et verts dans
cet environnement. Il ne reste qu'un seul chantier cadré non fait — le client Keycloak Admin API,
toujours bloqué faute de royaume réel — voir README.md « Prochaine étape ». Détail exact : voir
l'en-tête de [`README.md`](./README.md) — **ne pas se fier à ce résumé seul**, il peut dater ;
vérifier `git log`/le contenu réel des packages avant toute hypothèse sur l'état d'un domaine donné.

## Périmètre — tranché, voir `README.md` pour le détail complet

`core-api` est le **socle métier central** de KLEM DataSphere — pas un agrégat de toutes les
fonctionnalités de la plateforme. Périmètre exact, découpage en packages, dépendances autorisées,
premiers endpoints/événements et tests de frontière : [`README.md`](./README.md) (cadrage complet,
à lire avant tout scaffold).

**Inclus** : authentification (support — résolution du contexte JWT, pas réimplémentation
d'OAuth2/OIDC qui reste Keycloak), tenants, utilisateurs/organisations, rôles/permissions (RBAC),
référentiels **communs** (pas les référentiels métier d'un seul produit), audit, contrats API de
base (erreur/pagination/`requestId`), orchestration des workflows critiques **transverses**
(onboarding tenant, invitation utilisateur — pas les workflows métier d'un produit).

**Exclu** : UI, RAG/IA, analytics lourds, reporting spécialisé, intégrations métier non centrales
(Mobile Money, télématique, EDI douanier), scripts R&D, infrastructure non liée au domaine,
référentiels métier spécialisés d'un produit (TEC/SH → `referentiel-api-service`).

`core-api` ne dépend d'aucun autre service `services/*` — la dépendance va toujours des services
produit vers `core-api`, jamais l'inverse.

**Point ouvert tranché** : les autres services DataSphere lisent les permissions via les claims JWT
(synchronisées vers Keycloak), pas d'appel synchrone à `core-api` — voir ADR
[`2026-08-10-autorisation-core-api-claims-jwt-vs-appel-synchrone.md`](../../collaboration/history/adr/2026-08-10-autorisation-core-api-claims-jwt-vs-appel-synchrone.md).
Le domaine `authorization` est implémenté côté enregistrement (base de données) mais **la
synchronisation Keycloak elle-même reste à construire** — voir README.md « Lacune connue » et
« Prochaine étape ». Une attribution de rôle faite aujourd'hui via l'API n'a donc aucun effet réel
sur les autorisations vérifiées par les autres services tant que ce pont n'existe pas. Ne pas
confondre avec le pont **Kafka** (livré) — deux chantiers distincts, l'un bloqué par l'absence
d'environnement Keycloak réel, l'autre non (Testcontainers suffit, comme pour PostgreSQL).

## Deux services frères déjà scaffoldés — le patron déjà appliqué aux six domaines

`services/transit-ops-service` (Hinterland-Track) et `services/referentiel-api-service` (KLEM
Trade-X) sont deux squelettes « Sprint 0 » conformes à `KLEM_MASTER_SYSTEM_DIRECTIVE.md` §5. Les
six domaines de `core-api` (`tenant`, `identity`, `referential`, `authorization`, `audit`,
`workflow`) suivent déjà ce patron — le reproduire à l'identique pour toute extension future plutôt
que d'en inventer un nouveau.

Trois motifs de dépendance inter-domaine établis pendant ce Sprint :
- **Lecture peer-à-peer** (`authorization → tenant`, `authorization → identity`) : passer par une
  méthode étroite de `application.service` (ex. `TenantService.tenantExists`,
  `IdentityService.isMemberOfTenant`), jamais par `domain.model`/`domain.exception` de l'autre
  domaine.
- **Écoute passive** (`audit ← domain.event` de `tenant`/`identity`/`authorization`) : dépendre
  uniquement de la classe d'événement elle-même via `@TransactionalEventListener`, jamais de
  `domain.model`/`application`/`infrastructure`.
- **Orchestration privilégiée** (`workflow → tenant`/`identity`/`authorization`) : seul `workflow`
  peut lire directement `domain.model` d'un autre domaine (ex. `Tenant.getId()`), parce que rien ne
  dépend de `workflow` en retour (pas de couplage circulaire possible).

Les trois sont vérifiés par `PackageBoundaryRulesTest` (14 règles) — voir README.md « Raffinements
d'architecture » pour le détail de chaque exemption et pourquoi.

- **Stack** : Java 21 LTS, Spring Boot 3.3.x (voir `<parent>` de leur `pom.xml`), Spring Security
  OAuth2 Resource Server (JWT), Spring Data JPA, PostgreSQL, Flyway, springdoc-openapi, Actuator,
  JUnit 5 + Testcontainers + ArchUnit.
- **Structure package-by-feature** : `com.klem.<service>.<domaine>.{api/controller,
  application/service, domain/model, infrastructure/persistence}`, plus `config/` et `security/` à
  la racine du package service (voir l'arborescence réelle de `transit-ops-service/src/main/java`).
- **Sprint 0 = preuve de câblage, pas de logique métier** : le squelette initial ne contient aucune
  entité JPA ni règle métier — il prouve que build, sécurité OAuth2, observabilité et OpenAPI sont
  correctement câblés, avant la première tranche verticale (base de données → service → API →
  interface), conformément à
  `klem-labs-repository/shared_architecture/microservices_&_delivery/specifications_techniques.md`.
- **Configuration** : `OIDC_ISSUER_URI`, `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`, `SERVER_PORT` en
  variables d'environnement — jamais de secret en dur dans `application.yml`.

## Commandes

```bash
./mvnw clean verify        # build + tests (8 tests Testcontainers/contexte complet nécessitent
                            # Docker : CoreApiApplicationTests, WorkflowServiceIntegrationTest,
                            # PortfolioEventPublisherIntegrationTest (Kafka + PostgreSQL),
                            # {Tenant,Identity,Referential,Authorization,Audit}JpaRepositoryIntegrationTest)
./mvnw -Dtest='!*ApplicationTests,!*IntegrationTest' test   # tests rapides sans Docker (77/85)
./mvnw spring-boot:run     # démarrage local (profil "local"), port 8083
curl http://localhost:8083/actuator/health
```

## Priorités (rappel `../../../knowledge/03-architecture-principles.md` §23)

1. Sécurité
2. Isolation des tenants
3. Exactitude réglementaire
4. Intégrité transactionnelle
5. Observabilité
6. Résilience
7. Simplicité
8. Performance mesurée
9. Vitesse de livraison
10. Sophistication technique

## Toujours

- controllers minces, logique métier en `application`/`domain`, DTOs séparés des entités JPA ;
- migrations Flyway versionnées, aucune modification manuelle non tracée en production ;
- `tenant_id` dérivé du JWT/contexte serveur, jamais accepté depuis le client ;
- tests Testcontainers pour toute dépendance réelle (PostgreSQL, Kafka, Redis selon le besoin) ;
- OpenAPI tenue à jour en même temps que le code, pas après coup.

## Demander confirmation avant

- d'écrire un client Keycloak Admin API contre des identifiants/royaume simulés — sans royaume réel,
  ce code ne serait pas vérifiable et ne doit pas être présenté comme fonctionnel (voir README.md
  « Lacune connue (`authorization`) »). Contrairement au pont Kafka (livré, vérifié par
  Testcontainers), ce chantier-là reste réellement bloqué ;
- d'ajouter un nouveau topic Kafka ou de modifier l'enveloppe `PortfolioEvent` sans vérifier d'abord
  laquelle des deux formes documentées (`data_pipeline/specifications_techniques.md` §2.1 vs
  `KLEM_MASTER_SYSTEM_DIRECTIVE.md` §10) fait réellement foi sur le cluster partagé — voir README.md
  « Prochaine étape » ;
- d'introduire une dépendance à un autre service `services/*` ;
- de modifier le contrat OAuth2/JWT (`SecurityConfig`) hérité de `KLEM_MASTER_SYSTEM_DIRECTIVE.md`.

## Ne jamais

- dupliquer une responsabilité déjà couverte par `transit-ops-service` ou `referentiel-api-service`
  sans justification écrite ;
- exposer une entité JPA directement par l'API ;
- accepter un `tenant_id`, un rôle ou une permission transmis par le client sans vérification
  serveur.
