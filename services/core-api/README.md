# core-api

Socle métier central de KLEM DataSphere — **pas** un agrégat de toutes les fonctionnalités de la
plateforme. Ce service porte l'identité, les tenants, les référentiels communs, l'audit et les
règles transverses consommés par les autres services DataSphere (Hinterland-Track, KLEM Trade-X,
KLEM Copilot) — jamais l'inverse.

> **Statut : Sprint 0 scaffoldé + les cinq tranches verticales cadrées implémentées — `tenant`,
> `identity`, `referential`, `authorization`, `workflow`.** Câblage (sécurité OAuth2 Resource
> Server, OpenAPI, Actuator) conforme au patron de `transit-ops-service`/`referentiel-api-service`.
> `tenant` : entité,
> migration Flyway, service, 3 endpoints (`POST/GET /api/v1/tenants`,
> `PATCH /api/v1/tenants/{id}/status`). `identity` : `User` + `TenantMembership` (adhésion
> multi-tenant avec statut), migration Flyway, service avec provisionnement « just-in-time » et
> liaison d'invitation en attente au premier login, 2 endpoints (`GET /api/v1/users/me`,
> `POST /api/v1/tenants/{tenantId}/users`). `referential` : `ReferentialList` + `ReferentialEntry`,
> migration Flyway avec données réelles du portefeuille (pays des corridors Hinterland-Track, devise
> BCEAO), lecture seule, 1 endpoint (`GET /api/v1/referentials/{code}`), accessible à tout
> utilisateur authentifié. `authorization` : `RoleAssignment` (socle de rôles transverses
> `shared_architecture/identity_&_iam/`), service avec vérification tenant + adhésion avant
> attribution, 3 endpoints (`GET .../roles`, `POST/DELETE .../users/{userId}/roles/{roleCode}`) —
> **système d'enregistrement côté core-api uniquement, la synchronisation vers Keycloak n'est PAS
> implémentée** (voir « Lacune connue » ci-dessous). `audit` : `AuditEntry` (journal append-only),
> `AuditService` écoute via `@TransactionalEventListener(AFTER_COMMIT)` les événements déjà publiés
> par `tenant`/`identity`/`authorization` (aucune modification de ces trois domaines), sérialise
> chaque événement en JSON, expose 1 endpoint paginé (`GET /api/v1/tenants/{tenantId}/audit-events`).
> `workflow` : `WorkflowService.onboardTenant` orchestre en une seule transaction
> `TenantService.createTenant` → `IdentityService.inviteUser` → `AuthorizationService.assignRole(ADMIN)`
> — si l'une des trois échoue, aucune n'est persistée. Pas de table propre (pas de
> {@code workflow_execution}) : l'atomicité vient entièrement de `@Transactional`, suffisant pour un
> enchaînement synchrone court. 1 endpoint (`POST /api/v1/workflows/tenant-onboarding`).
>
> **Pont Kafka livré** (`audit.infrastructure.messaging.PortfolioEventPublisher`) : écoute les six
> événements de domaine déjà publiés par `tenant`/`identity`/`authorization` — un second abonné
> indépendant d'`AuditService`, pas un appel entre les deux — et publie un message par événement sur
> le cluster Kafka partagé du portefeuille (enveloppe `shared_architecture/data_pipeline/specifications_techniques.md`
> §2.1 : `eventId`, `eventType`, `source`, `aggregateType`, `aggregateId`, `metadata.tenantId`,
> `schemaVersion`), après commit uniquement (`AFTER_COMMIT`, même garantie transactionnelle que la
> persistance d'audit). Topics : `tenant.created`, `tenant.status.changed`, `user.invited`,
> `user.activated`, `role.assigned`, `role.revoked` — noms de topics transverses, sans préfixe
> produit (§5). `eventId` est désormais généré une seule fois à la source (dans
> `TenantService`/`IdentityService`/`AuthorizationService`), partagé entre l'entrée d'audit et le
> message Kafka du même fait réel — corrigé à cette occasion (c'était auparavant régénéré séparément
> par chaque consommateur, rendant toute corrélation illusoire).
>
> 94 tests dont 85 exécutables et verts dans cet environnement (9 tests Testcontainers non
> exécutables ici — **confirmé être une limite de l'environnement, pas du code** : le client Docker
> de ce bac à sable expose l'API 1.32, Testcontainers exige au minimum 1.40, message d'erreur
> explicite à l'appui — voir `AGENTS.md`). Le point ouvert §4 est tranché par
> [`2026-08-10-autorisation-core-api-claims-jwt-vs-appel-synchrone.md`](../../collaboration/history/adr/2026-08-10-autorisation-core-api-claims-jwt-vs-appel-synchrone.md).
>
> **Raffinements d'architecture, découverts en implémentant, pas anticipés** — trois exceptions
> ciblées à la règle « chaque domaine reste isolé des autres », toutes vérifiées par
> `PackageBoundaryRulesTest` (14 règles) : (1) `audit` peut dépendre spécifiquement des classes
> `domain.event` des autres domaines (ex. `TenantCreatedEvent`) — un événement de domaine est le
> point d'intégration public intentionnel d'un domaine, contrairement à
> `domain.model`/`domain.exception`, qui restent strictement internes ; (2) `workflow`, en tant
> qu'orchestrateur privilégié (rien ne dépend de lui, règle 6), peut en plus lire `domain.model` de
> `tenant`/`identity`/`authorization` (ex. `Tenant.getId()`) — nécessaire pour enchaîner les étapes,
> à la différence du motif `tenantExists`/`isMemberOfTenant` (peer-à-peer, resté restreint à un
> booléen) ; (3) `authorization` peut, en plus de son accès peer-à-peer déjà établi à `identity`,
> écouter `identity.domain.event.UserActivatedEvent` — `KeycloakRoleSyncPublisher` en a besoin pour
> rattraper la synchronisation des rôles d'un utilisateur qui vient de lier son compte Keycloak.
>
> **Lacune connue (`authorization`)** : ce Sprint implémente uniquement le système d'enregistrement
> des rôles en base côté `core-api`. Il n'y a **aucune synchronisation vers Keycloak** — tant
> qu'elle n'existe pas, une attribution faite via `POST /api/v1/tenants/{tenantId}/users/{userId}/roles/{roleCode}`
> n'a aucun effet sur les autorisations réellement appliquées par les autres services DataSphere
> (qui valident le JWT localement, voir l'ADR). Cette synchronisation suppose un royaume Keycloak
> réel avec des identifiants d'administration — non construite ici faute de pouvoir la vérifier.
> `PLATFORM_ADMIN`, le rôle qui protège les endpoints d'administration de `core-api` lui-même, reste
> volontairement hors de ce système (accordé hors bande côté Keycloak) pour éviter un problème
> d'amorçage — voir Javadoc de `RoleCode`.
>
> **Client Keycloak Admin API livré** (`authorization.infrastructure.messaging`) :
> `KeycloakRoleSyncClient` (jeton client-credentials mis en cache, résolution de rôle par nom,
> attribution/retrait via `/admin/realms/{realm}/users/{userId}/role-mappings/realm`) et
> `KeycloakRoleSyncPublisher`, un abonné à `RoleAssignedEvent`/`RoleRevokedEvent` **et**
> `UserActivatedEvent` (identity) — nécessaire car un rôle peut être attribué à un utilisateur
> encore `INVITED`, sans compte Keycloak : la synchronisation est alors différée et rattrapée en
> bloc à l'activation. `RoleAssignmentRepository.findByUserId` et
> `IdentityService.getKeycloakSubject` ajoutés à cette occasion.
>
> **Important, à ne pas manquer avant toute mise en production** : `KeycloakRoleSyncClientTest` et
> `KeycloakRoleSyncPublisherTest` (mock/Mockito) sont vérifiés et verts. `KeycloakRoleSyncIntegrationTest`,
> lui, **n'a jamais pu être exécuté dans aucun environnement disponible** — contrairement aux autres
> tests Testcontainers du dépôt (patron identique déjà éprouvé sur PostgreSQL/Kafka, où seul le
> *démarrage du conteneur* échoue ici), celui-ci construit lui-même, par code, un royaume Keycloak de
> test complet (rôles, client de provisionnement, permission `manage-users`) à partir de la
> documentation de l'API Admin — jamais vérifié empiriquement. Et même une fois exécuté avec succès,
> ce royaume reste une **hypothèse** de la structure du futur royaume KLEM DataSphere réel, pas une
> copie de sa configuration effective (qui n'existe pas encore) — voir Javadoc de la classe. À
> exécuter et corriger en priorité dès qu'un Docker à jour (API ≥ 1.40) est disponible.
>
> **Lacune connue** (documentée dans la Javadoc de `IdentityService.getOrProvisionCurrentUser`) :
> si l'e-mail d'un JWT correspond à un utilisateur déjà lié à un autre `sub` Keycloak (changement
> d'e-mail côté IdP non synchronisé), le provisionnement JIT tente de créer un second enregistrement
> et échoue sur la contrainte d'unicité `email` plutôt que de fusionner les comptes — cas non géré
> par cette tranche, à traiter si un besoin réel apparaît.

## 1. Cas d'usage inclus

| Domaine | Ce que `core-api` porte |
|---|---|
| Authentification (support, pas réimplémentation) | Résolution du contexte tenant/utilisateur à partir du JWT émis par Keycloak (OAuth2 Resource Server) ; **ne réimplémente pas** le protocole OAuth2/OIDC lui-même, qui reste porté par Keycloak (`shared_architecture/identity_&_iam/`). |
| Tenants | Création, activation/désactivation, métadonnées (nom, secteur, statut) d'un tenant/organisation. |
| Utilisateurs & organisations | Profil utilisateur KLEM (au-delà du `sub` Keycloak), rattachement d'un utilisateur à un ou plusieurs tenants. |
| Rôles & permissions (RBAC) | Rôles transverses du socle IAM (`Admin`, `Opérateur`, `Client`, etc. — `shared_architecture/identity_&_iam/specifications_techniques.md` §2), attribution/révocation par tenant, exposition d'un service de vérification de permission réutilisable par les autres domaines de `core-api`. |
| Référentiels communs | Listes de valeurs partagées entre **plusieurs** produits (pays, devises, unités, statuts génériques) — pas les référentiels métier spécialisés d'un seul produit. |
| Audit | Journal des actions sensibles (qui, quoi, quand, sur quelle ressource) ; producteur canonique des topics transverses déjà nommés `audit.event.logged`/`audit.status.changed` (`shared_architecture/data_pipeline/specifications_techniques.md` §2.2). |
| Contrats API de base | Format d'erreur standard, pagination, `requestId`/`correlationId`, enveloppe de réponse — un « shared kernel » réutilisé par les autres domaines internes de `core-api`, pas une dépendance runtime imposée aux autres services. |
| Orchestration de workflows critiques transverses | Onboarding d'un tenant, invitation/activation d'un utilisateur, cycle d'attribution de rôle — uniquement les workflows **transverses**, pas les workflows métier d'un produit. |

## 2. Cas d'usage exclus — et où ils vivent réellement

| Exclu de `core-api` | Vit réellement dans |
|---|---|
| UI | `apps/admin` (Next.js back-office) — `core-api` est une API pure. |
| RAG / IA | Service Python spécialisé + KLEM Copilot (voir `../../../knowledge/08-ai-rag-governance.md`). |
| Analytics lourds | ClickHouse + services dédiés (voir `../../../knowledge/06-data-platform.md`). |
| Reporting spécialisé | Moteur hybride de rapports du service produit qui le sert (voir `../../../knowledge/04-backend-spring-boot.md`) — `core-api` ne fournit que le contrat d'erreur/pagination commun, pas la génération de rapports elle-même. |
| Intégrations métier non centrales | Connecteurs Mobile Money, télématique, EDI douanier, etc. — restent dans leur service produit (`shared_architecture/billing_&_payments/`, `data_pipeline/`). |
| Scripts R&D | `klem-labs-repository` exclusivement — jamais dans ce dépôt de production. |
| Infrastructure non liée au domaine | Provisioning Terraform/K8s, pipelines CI/CD génériques — `infra/`, `shared_architecture/deployment_ci_cd/`, pas un service applicatif. |
| Référentiels métier spécialisés d'un produit | TEC/codes SH/devises BCEAO → `referentiel-api-service` (KLEM Trade-X) ; référentiel réglementaire douanier → Clear-Comply. `core-api` ne les duplique jamais. |

## 3. Découpage des packages (package-by-feature, `com.klem.coreapi`)

```text
com.klem.coreapi/
├── CoreApiApplication.java
├── config/                          # configuration Spring transverse (OpenAPI, Jackson, CORS interne)
├── security/                        # OAuth2 Resource Server, résolution du contexte tenant depuis le JWT
├── shared/                          # noyau technique commun — pas un domaine métier
│   ├── api/                         # ApiError, PageResponse<T>, enveloppe de réponse standard
│   └── domain/                      # exceptions de base, value objects communs (TenantId, ActorRef)
├── tenant/
│   ├── api/{controller,request,response}/
│   ├── application/{service,port}/
│   ├── domain/{model,event,exception}/
│   └── infrastructure/persistence/
├── identity/                        # utilisateurs KLEM, rattachement tenant
│   ├── api/{controller,request,response}/
│   ├── application/{service,port}/
│   ├── domain/{model,event,exception}/
│   └── infrastructure/persistence/
├── authorization/                   # rôles, permissions, attributions RBAC
│   ├── api/{controller,request,response}/
│   ├── application/{service,port}/
│   ├── domain/{model,event,exception}/
│   └── infrastructure/persistence/
├── referential/                     # référentiels communs (pays, devises, unités, statuts génériques)
│   ├── api/{controller,request,response}/
│   ├── application/{service,port}/
│   ├── domain/model/
│   └── infrastructure/persistence/
├── audit/
│   ├── api/{controller,request,response}/
│   ├── application/{service,port}/
│   ├── domain/{model,event}/
│   └── infrastructure/{persistence,messaging}/
└── workflow/                        # orchestration cross-domaine (onboarding tenant, invitation utilisateur...)
    ├── api/{controller,request,response}/
    ├── application/{service,port}/
    ├── domain/{model,event}/
    └── infrastructure/messaging/
```

Chaque domaine expose une **API publique minimale** via `application/port` (interfaces) et
`api/controller` (HTTP) ; `domain` et `infrastructure` restent package-private/non exposés en
dehors du domaine — aucune classe de `identity.domain` ou `identity.infrastructure` ne doit être
importée depuis `authorization` ou `workflow`, uniquement `identity.application.port`.

Si une feature dépasse la taille d'un domaine ci-dessus (ex. l'onboarding tenant devient trop
complexe pour tenir dans `workflow` seul), la découper en vertical slice dédiée plutôt que de la
faire grossir dans un package existant — un sous-package nommé par le cas d'usage
(`workflow.tenantonboarding`), pas un fourre-tout.

## 4. Dépendances autorisées — unidirectionnelles

```text
shared            ← dépendance libre depuis tous les domaines
tenant             ← identity, authorization, workflow
identity            ← authorization, workflow          (identity ne dépend jamais de authorization)
authorization        ← workflow                          (jamais l'inverse)
referential          ← (aucun domaine interne n'en dépend au départ ; autonome)
audit                ← rien ne dépend d'audit ; audit ne dépend de rien d'autre que shared —
                        les autres domaines PUBLIENT vers audit via un port
                        (AuditPublisher), audit ne les appelle jamais
workflow             ← rien ne dépend de workflow (couche d'orchestration la plus haute)
```

Règles générales :

- **Aucune dépendance circulaire** entre domaines — vérifié par ArchUnit (§6).
- **`core-api` ne dépend d'aucun autre service `services/*`** (`transit-ops-service`,
  `referentiel-api-service`) — c'est un socle, la dépendance va toujours des services produit vers
  `core-api` (REST synchrone pour les lectures de référence, événements Kafka pour les faits
  asynchrones), jamais l'inverse.
- **Dépendances externes autorisées** : PostgreSQL (transactionnel), Kafka (publication des
  événements `tenant.*`/`user.*`/`role.*`/`audit.*`), Keycloak (validation JWT, `OIDC_ISSUER_URI`).
- **Dépendances externes interdites** : ClickHouse, service Python RAG/ML, tout service `services/*`
  d'un produit vertical.

**Point ouvert tranché par ADR** — les autres services DataSphere lisent les permissions via les
claims du JWT (option a), synchronisées depuis `core-api` vers Keycloak, **pas** d'appel synchrone à
`core-api` sur le chemin de requête d'un autre service. Décision complète, alternatives écartées et
risques assumés :
[`2026-08-10-autorisation-core-api-claims-jwt-vs-appel-synchrone.md`](../../collaboration/history/adr/2026-08-10-autorisation-core-api-claims-jwt-vs-appel-synchrone.md).
`SecurityConfig` implémente déjà le contrat côté `core-api` (claim `roles` → `ROLE_*`), à répliquer
côté Keycloak (protocol mapper par royaume) avant la mise en production du domaine `authorization`.

## 5. Premiers endpoints / événements (Sprint 0 → première tranche verticale)

### Sprint 0 (câblage, pas de logique métier — patron des services frères)

- `GET /actuator/health`, `/actuator/info`
- `GET /v3/api-docs`, `/swagger-ui.html`
- Sécurité OAuth2 Resource Server câblée (`OIDC_ISSUER_URI`), même sans endpoint métier protégé.

### Première tranche verticale candidate — domaine `tenant`

- `POST /api/v1/tenants` — création d'un tenant (rôle `Admin` plateforme uniquement).
- `GET /api/v1/tenants/{tenantId}` — lecture d'un tenant.
- `PATCH /api/v1/tenants/{tenantId}/status` — activation/désactivation.

### Domaine `identity` (dépend de `tenant`)

- `GET /api/v1/users/me` — profil de l'utilisateur authentifié courant + tenants rattachés.
- `POST /api/v1/tenants/{tenantId}/users` — invitation d'un utilisateur dans un tenant.

### Domaine `authorization` (dépend de `identity`) — **après** l'ADR du point ouvert (§4)

- `GET /api/v1/tenants/{tenantId}/roles`
- `POST /api/v1/tenants/{tenantId}/users/{userId}/roles/{roleCode}` — attribution de rôle.
- `DELETE /api/v1/tenants/{tenantId}/users/{userId}/roles/{roleCode}` — révocation.

### Domaine `referential`

- `GET /api/v1/referentials/{code}` — liste de valeurs partagée (ex. `countries`, `currencies`).

### Événements Kafka publiés (conformes à l'enveloppe standard, `metadata.tenantId` obligatoire)

- `tenant.created`, `tenant.status.changed`
- `user.invited`, `user.activated`, `user.deactivated`
- `role.assigned`, `role.revoked`
- `audit.event.logged` — topic transverse déjà nommé dans
  `shared_architecture/data_pipeline/specifications_techniques.md` §2.2 ; `core-api` en devient le
  producteur canonique pour les actions qu'il journalise, sans empêcher d'autres services de publier
  sur le même topic avec la même enveloppe.

## 6. Tests de frontière à ajouter (ArchUnit — avant toute logique métier)

- **Pas de cycle entre domaines** : `slices().matching("com.klem.coreapi.(*)..").should().beFreeOfCycles()`.
- **`audit` n'a aucune dépendance sortante vers un autre domaine** : seule `shared` est autorisée en
  import depuis `com.klem.coreapi.audit..`.
- **`identity` ne dépend jamais de `authorization`** (sens unique identity → authorization interdit
  dans l'autre sens) : `noClasses().that().resideInAPackage("..identity..").should().dependOnClassesThat().resideInAPackage("..authorization..")`.
- **`domain` et `infrastructure` d'un package ne sont importés que depuis leur propre domaine** :
  aucune classe hors `com.klem.coreapi.tenant..` ne doit importer `com.klem.coreapi.tenant.domain..`
  ou `com.klem.coreapi.tenant.infrastructure..` directement — seul `tenant.application.port` est
  une frontière autorisée.
- **Aucune entité JPA (`infrastructure.persistence`) n'est retournée par un `api.controller`** —
  vérifié par une règle ArchUnit sur les types de retour des méthodes annotées `@RestController`.
- **`core-api` n'importe aucune classe d'un autre service `services/*`** — vérifiable uniquement au
  niveau du build (aucune dépendance Maven vers `transit-ops-service`/`referentiel-api-service`),
  pas par ArchUnit intra-module ; à documenter comme contrainte de `pom.xml`.

## Prochaine étape

**Les cinq domaines métier cadrés dans ce document sont tous livrés** — `tenant`, `identity`,
`referential`, `authorization` (y compris la synchronisation Keycloak), `audit`, `workflow` — **et
le pont Kafka portefeuille l'est aussi désormais**. Le Sprint des tranches verticales prévu par ce
cadrage est complet, code compris pour les deux chantiers d'infrastructure externe. Ce qui reste :

1. **Exécuter `KeycloakRoleSyncIntegrationTest` pour de vrai** — priorité n°1 avant toute confiance
   dans `KeycloakRoleSyncClient`/`KeycloakRoleSyncPublisher`. Ce test n'a jamais tourné dans aucun
   environnement disponible pendant son écriture (voir en-tête, encadré) ; le bootstrap Keycloak
   qu'il exécute est écrit à partir de la documentation de l'API Admin, pas vérifié. Dès qu'un Docker
   à jour (API ≥ 1.40) est disponible, le lancer et corriger ce qui casse avant de considérer ce
   client comme fiable.
2. **Confronter le royaume de test hypothétique au vrai royaume KLEM DataSphere**, une fois qu'il
   existe — rôles réalistes au-delà des 7 valeurs de `RoleCode`, permissions du client de
   provisionnement au plus juste (`manage-users` est plus large que nécessaire, voir Javadoc de
   `KeycloakRoleSyncIntegrationTest`), royaume d'administration éventuellement distinct du royaume
   applicatif (hypothèse simplificatrice actuelle : un seul royaume, voir Javadoc de
   `KeycloakAdminProperties`).
3. **Réconcilier les deux enveloppes d'événement documentées** dans le dépôt —
   `PortfolioEvent`/`data_pipeline/specifications_techniques.md` §2.1 (retenue ici) vs la forme plus
   plate de `MASTER_SYSTEM_DIRECTIVE.md` §10 — avec Fleet-Advance et Hinterland-Track pour
   confirmer qu'un seul format doit prévaloir sur le cluster partagé (voir Javadoc de
   `PortfolioEvent`). Pas tranché unilatéralement ici : `core-api` est un nouveau venu sur le
   cluster, pas l'autorité qui a écrit `data_pipeline/specifications_techniques.md`.

Au-delà de ces trois points, toute extension relève d'une décision produit plutôt que d'un
prolongement mécanique du cadrage (nouveaux endpoints sur un domaine existant, nouveau domaine hors
périmètre §1/§2 — auquel cas repartir du cadrage en six points comme ce document l'a fait pour
`core-api` lui-même).

**Trois motifs de conception établis pendant ce Sprint, à réutiliser pour toute extension future**
(tous vérifiés par `PackageBoundaryRulesTest`, 14 règles) :

1. **Dépendance de lecture peer-à-peer** (`authorization → tenant`, `authorization → identity`) :
   une méthode étroite et intentionnelle de `application.service` du domaine dépendant
   (`TenantService.tenantExists`, `IdentityService.isMemberOfTenant`), jamais l'entité
   `domain.model` ni l'exception `domain.exception` de l'autre domaine.
2. **Écoute passive** (`audit ← domain.event` de `tenant`/`identity`/`authorization`) : dépendre
   uniquement de la classe d'événement via `@TransactionalEventListener`, jamais de
   `domain.model`/`application`/`infrastructure`.
3. **Orchestration privilégiée** (`workflow → tenant`/`identity`/`authorization`) : seul `workflow`
   peut lire directement `domain.model` d'un autre domaine (ex. `Tenant.getId()`) — parce que rien
   ne dépend de `workflow` en retour (règle 6), l'asymétrie ne crée pas de couplage circulaire.
