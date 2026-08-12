# ADR : Adoption de MASTER_SYSTEM_DIRECTIVE.md et périmètre d'application

- **Date :** 2026-08-08
- **Statut :** Accepté
- **Auteurs :** Claude Code (session d'actualisation du workspace selon la directive maître)

## Contexte

`MASTER_SYSTEM_DIRECTIVE.md` v2.0 a été introduit dans le workspace (copies identiques à la
racine, dans `klem-labs-repository`, dans `klem-projects-repository`, dans
`klem-projects-repository/apps`, et dans `apps/web-app/cantine-connect`). Il décrit
l'architecture cible d'une plateforme unifiée « KLEM DataSphere » (produits pivots
Hinterland-Track, KLEM Trade-X, KLEM Copilot) : Java 21 LTS + Spring Boot 3.x, sécurité OAuth2
Resource Server/JWT (`issuer-uri`), structure de service en couches
`api/application/domain/infrastructure`.

Un audit du workspace montre que la réalité est plus large et plus hétérogène que ce que la
directive décrit seule :

- `klem-labs-repository` est purement documentaire (specs/wireframes) pour 9 des 10 projets
  répertoriés. Hinterland-Track et Trade-X sont au statut « Validé R&D, prêt pour MVP » (specs
  techniques complètes, noms de services déjà actés dans les specs : `transit-ops-service`,
  `referentiel-api-service`) mais aucun code n'existe encore — `services/` est vide.
- `klem-projects-repository/apps` héberge en réalité un portefeuille de **projets clients
  indépendants**, pas uniquement KLEM DataSphere : FleetControl (`backend-api`, coquille sans
  code), cantine-connect, parcauto, clinic, pharmacie, chacun avec son propre `CLAUDE.md`.
- **Trois modèles d'authentification différents et déjà en production/prévus** coexistent :
  cookie `JSESSIONID` (règle racine `klem-projects-repository/CLAUDE.md` et gabarits
  `templates/template_CLAUDE(maitre).md` / `templates/CLAUDE_RACINE.md`), JWT par en-tête
  (`apps/web-app/cantine-connect/CLAUDE.md`, effectivement implémenté avec `jjwt`), JWT en cookie
  httpOnly (`apps/web-app/parcauto/CLAUDE.md`, backend pas encore écrit). Aucun des trois n'est un
  OAuth2 Resource Server au sens de la directive §7.
- Seul cantine-connect a du code backend réel (125 fichiers Java, Java 17, Spring Boot 3.3.5) et
  gère des paiements Mobile Money en production.
- La gouvernance déjà en place (`GLOBAL_README.md` règle 7, `klem_promote.py`, voir ADR
  `2026-07-24-scaffold-services-infra-docs-et-pont-labs-projects.md`) gate la *promotion*
  labs → projects au stade pilote/livré ; elle ne gate pas le *démarrage* du code d'un projet
  déjà « Validé R&D, prêt pour MVP ».

## Décision

1. `MASTER_SYSTEM_DIRECTIVE.md` fait autorité pour les **nouveaux services KLEM DataSphere**
   créés sous `services/*` (à commencer par `transit-ops-service` et `referentiel-api-service`,
   scaffoldés dans la foulée de cette ADR — squelettes buildables sans logique métier, une
   tranche verticale à la fois, conformément à la méthodologie de
   `shared_architecture/standards/microservices_&_delivery/specifications_techniques.md`).
2. Les apps clients existantes (cantine-connect, parcauto, backend-api/FleetControl, clinic,
   pharmacie) **gardent leur `CLAUDE.md` propre** comme référence de premier niveau. La directive
   maître n'y est pas appliquée rétroactivement par écrasement — toute convergence future
   (Java 21, OAuth2 Resource Server) sera une migration dédiée, testée et tracée par app, jamais
   un remplacement silencieux d'une architecture en production.
3. Les contradictions entre `MASTER_SYSTEM_DIRECTIVE.md` et les règles racine
   (`CLAUDE.md`, gabarits `templates/template_CLAUDE(maitre).md` et `templates/CLAUDE_RACINE.md`)
   sont résolues **par portée, pas par écrasement** : une note y renvoie vers la directive maître
   §7 comme référence pour tout nouveau service `services/*` de type KLEM DataSphere, la règle
   cookie existante restant la référence pour les apps clients déjà construites dessus.
4. Amélioration additive et sans risque de régression sur cantine-connect (seule app avec du code
   de production) : ajout d'un `requestId` dans le format d'erreur (§6) et de springdoc-openapi
   (§6 « OpenAPI obligatoire »). Aucun changement de comportement existant.

## Alternatives envisagées

- **Migration globale immédiate** (Java 21 partout, OAuth2 Resource Server partout, refonte des
  couches) : écartée — risque disproportionné sur un système de paiement Mobile Money en
  production (cantine-connect) sans cycle de non-régression dédié ; contredirait la règle de la
  directive elle-même (§4) de ne pas remplacer une architecture existante sans justification.
- **Ignorer la directive pour tout l'existant et ne l'appliquer qu'à du code futur hypothétique** :
  écartée — laisserait une dérive documentaire non tracée (aucun fichier ne référençait encore la
  directive) et retarderait indéfiniment le scaffolding des produits pivots déjà « prêts pour
  MVP » depuis la phase Labs.
- **Faire de `MASTER_SYSTEM_DIRECTIVE.md` la seule source de vérité et supprimer les règles
  contradictoires des `CLAUDE.md` existants** : écartée — ces règles sont directement implémentées
  dans du code en production (ex. filtre JWT de cantine-connect) ; les supprimer sans migration de
  code correspondante créerait un CLAUDE.md mensonger par rapport au comportement réel.

## Conséquences

- **Avantages :** la directive maître devient effectivement discoverable (référencée depuis les
  README racine et les `CLAUDE.md`/gabarits) au lieu d'exister sans lien vers le reste du
  workspace ; le scaffolding des deux produits pivots peut démarrer sans attendre une clarification
  de gouvernance ; aucune régression sur cantine-connect (seules additions, aucune modification de
  comportement).
- **Risques/Dettes :** trois modèles d'authentification distincts restent en place (dette connue,
  documentée ici plutôt que cachée) ; Java 17 reste la version en production sur cantine-connect
  tant que la migration Java 21 n'est pas planifiée et testée séparément ; `transit-ops-service` et
  `referentiel-api-service` sont des squelettes sans logique métier — à ne pas confondre avec un
  MVP fonctionnel, le README de chacun le rappelle explicitement.
