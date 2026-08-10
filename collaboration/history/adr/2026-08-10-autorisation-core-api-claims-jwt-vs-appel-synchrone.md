# ADR : Propagation des permissions core-api — claims JWT vs appel synchrone

- **Date :** 2026-08-10
- **Statut :** Accepté
- **Auteurs :** Agent IA (Claude Code), sur délégation explicite — à challenger si le raisonnement
  ci-dessous ne tient pas, voir « Date de révision ».

## Contexte

`core-api` (`klem-projects-repository/services/core-api/`) est le système d'enregistrement des
tenants, utilisateurs et attributions de rôles/permissions (RBAC) pour toute la plateforme KLEM
DataSphere (`services/core-api/README.md`, cadrage §1). Les autres services DataSphere
(`transit-ops-service`/Hinterland-Track, `referentiel-api-service`/KLEM Trade-X, et les futurs
services) doivent, à chaque requête protégée, savoir si l'appelant a la permission d'agir.

`KLEM_MASTER_SYSTEM_DIRECTIVE.md` §7 a déjà acté que chaque service Spring Boot est un OAuth2
Resource Server **indépendant** qui valide lui-même le JWT (signature, issuer, audience,
expiration, `nbf`, scopes, rôles, permissions, contexte tenant) — sans préciser, au moment de ce
cadrage, **d'où viennent** les scopes/rôles/permissions présents dans ce JWT une fois que
`core-api` devient le système qui les gère. Deux mécanismes sont possibles pour combler cet écart,
et le choix engage l'implémentation du domaine `authorization` de `core-api` (voir
`services/core-api/README.md` §4, point ouvert signalé avant tout code sur ce domaine).

## Décision

**Option retenue : propagation par claims JWT, synchronisée depuis `core-api` vers Keycloak — pas
d'appel synchrone à `core-api` sur le chemin de requête d'un autre service.**

Mécanisme :

1. `core-api` (`authorization.application`) reste le système d'enregistrement des rôles/permissions
   par tenant. Toute attribution/révocation (`POST/DELETE /api/v1/tenants/{tenantId}/users/{userId}/roles/{roleCode}`)
   déclenche un appel à l'API d'administration Keycloak (Keycloak Admin REST API, via un client
   `core-api-provisioning` dédié à ce seul usage, credentials en gestionnaire de secrets — jamais en
   dur) pour refléter le changement de rôle realm/client côté Keycloak.
2. Des protocol mappers Keycloak (déjà la brique `shared_architecture/identity_&_iam/`) projettent
   les rôles/permissions de l'utilisateur dans l'access token JWT au moment de son émission.
3. Chaque service DataSphere (y compris `core-api` lui-même pour ses propres endpoints protégés)
   continue de valider le JWT **localement**, exactement comme déjà cadré par
   `KLEM_MASTER_SYSTEM_DIRECTIVE.md` §7 — aucun appel réseau à `core-api` n'est nécessaire pour
   qu'un autre service prenne une décision d'autorisation.
4. `core-api` expose malgré tout `GET /api/v1/users/me` en lecture — utilisé par les frontends pour
   afficher les rôles/permissions effectifs de l'utilisateur courant, **jamais** consulté par un
   autre service backend comme point de décision d'autorisation.

## Alternatives envisagées

- **Appel synchrone à `core-api` à chaque requête protégée d'un autre service** — rejeté. Introduit
  un point de défaillance unique pour toute la plateforme DataSphere (un incident sur `core-api`
  bloquerait Hinterland-Track et Trade-X), ajoute une latence réseau sur le chemin chaud de chaque
  requête, et contredit directement le modèle de Resource Server décentralisé déjà acté en §7. Pose
  aussi un dilemme fail-open/fail-closed non trivial en cas d'indisponibilité de `core-api`.
- **Cache de permissions par service, tenu à jour par abonnement aux événements Kafka
  `role.assigned`/`role.revoked`** — rejeté pour cette v1, pas définitivement écarté. Keycloak
  résout déjà la propagation via le token sans infrastructure supplémentaire, et seuls deux services
  produit consomment ce mécanisme à ce jour (Hinterland-Track, Trade-X) — la complexité d'un cache
  distribué par service n'est pas justifiée par le volume actuel (règle de proportionnalité,
  `knowledge/11-cto-operating-model.md`). À reconsidérer si le nombre de services grandit ou si un
  besoin de vérification fine (ABAC au niveau ressource, pas seulement RBAC au niveau rôle)
  dépasse ce qu'un claim JWT peut raisonnablement porter.

## Conséquences

- **Avantages :**
  - Cohérent avec le modèle déjà acté (`KLEM_MASTER_SYSTEM_DIRECTIVE.md` §7) — aucune dérogation à
    documenter côté services existants.
  - Aucun point de défaillance unique introduit par `core-api` sur le chemin de requête des autres
    services ; chaque service reste scalable horizontalement sans dépendance runtime croisée.
  - Compatible avec le besoin offline-first des apps clientes Expo (`SYSTEM_INSTRUCTIONS.md`) — les
    permissions voyagent avec le token, pas besoin de connectivité pour une vérification locale.

- **Risques/Dettes :**
  - **Latence de propagation d'une révocation** bornée à la durée de vie de l'access token
    (10-15 min, `shared_architecture/identity_&_iam/specifications_techniques.md` §1) — acceptable
    en fonctionnement normal, **pas suffisant pour une révocation d'urgence** (ex. compte
    compromis, fraude). Ce cas doit passer par l'invalidation de session Keycloak (déconnexion
    forcée), pas par ce mécanisme de claims — à documenter dans un runbook dédié avant la mise en
    production du domaine `authorization`, pas couvert par le Sprint 0 actuel.
  - Dépendance à la correcte configuration des protocol mappers Keycloak par royaume — une
    dérive de configuration entre royaumes est indétectable par `core-api` lui-même sans test
    d'intégration dédié.
  - Taille du token JWT qui grossit avec le nombre de permissions embarquées — préférer des rôles
    grossiers dans le token et réserver les vérifications fines à la logique applicative de chaque
    service, jamais en ajoutant plus de granularité dans le token par confort.
  - Le client `core-api-provisioning` (accès Keycloak Admin API) est un secret sensible à isoler
    strictement — sa fuite permettrait de modifier des rôles sur l'ensemble des royaumes.

## Date de révision

À revisiter si l'un de ces signaux apparaît : (1) un besoin réel de révocation quasi immédiate se
présente plus d'une fois en production, (2) le nombre de services DataSphere consommateurs dépasse
~6-8 et fait de l'API Admin Keycloak un goulot d'écriture, (3) un besoin d'autorisation fine
niveau-ressource (ABAC) dépasse ce qu'un rôle grossier dans un claim JWT peut raisonnablement
exprimer.
