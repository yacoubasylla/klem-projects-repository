# referentiel-api-service

Service unique MVP de **KLEM Trade-X** — référentiel **réglementaire et procédural** du commerce
extérieur (textes réglementaires, procédures métier, opérations commerciales, documents requis,
workflow d'édition/validation). TEC/codes SH/devises BCEAO ne sont **pas** des tables de ce
service : ce sont des sources normatives externes consultées par le futur agent d'ingestion
`klem_ref_bot` (spec §4.1/§4.2), pas le contenu du modèle de données — corrigé ici après une
description antérieure erronée qui laissait croire à un tarif douanier au sens strict.

> **Statut : les quatre domaines sont implémentés** (entité, migration Flyway, service, API REST,
> tests unitaires + slice réels). Non vérifié : les tests Testcontainers (`*JpaRepositoryIntegrationTest`,
> un par domaine) n'ont jamais tourné avec succès dans ce bac à sable — le client Docker local
> expose l'API 1.32, Testcontainers exige au minimum 1.40 (même limitation documentée sur
> `core-api`/`transit-ops-service`). À faire tourner réellement dès qu'un environnement Docker à
> jour est disponible, avant de considérer la couche persistance validée en conditions réelles.

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
`domain/model` → `infrastructure/persistence` (directive §5). Workflow éditorial commun aux quatre
domaines (`shared.domain.StatutPublication`) : `PROPOSEE → EN_REVISION → (PUBLIEE | REJETEE)`,
transitions validées côté domaine — voir `PackageBoundaryRulesTest` pour le graphe de dépendances
complet et ses justifications.

- `textereglementaire` — textes réglementaires. Racine du graphe, aucune dépendance sortante.
- `documentrequis` — documents requis. Racine du graphe également, indépendant de tout.
- `procedure` — procédures métier + jointure `procedure_texte` vers `textereglementaire`
  (référencé par UUID simple, existence vérifiée via `TexteReglementaireService.exists`).
- `operationcommerce` — opérations commerciales (`procedure_id` FK) + jointure
  `operation_document` vers `documentrequis` (même motif de lecture peer-à-peer étroite via
  `ProcedureMetierService.exists`/`DocumentRequisService.exists`). Feuille du graphe : rien n'en
  dépend. Expose `GET /api/v1/operations/{code}/documents`, le contrat de consultation utilisé par
  CLEAR-COMPLY (spec §4.3) — ne pas renommer/déplacer sans coordonner avec cette équipe.

Rôles RBAC (`Admin`/`Editeur`/`Lecteur`, spec §3) : lecture ouverte aux trois rôles une fois
authentifié (référentiel public par nature, pas de `tenant_id`) ; proposition et transition de
statut restreintes à `Editeur`/`Admin`. Le contrôle de l'offre souscrite par un `Lecteur`
(spec §3, `cas_metier.md` §8) n'est pas implémenté — dépendrait d'un appel à `core-api`, hors
périmètre MVP tant qu'aucun besoin réel ne l'exige.

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
