# ADR : Scaffold services/, infrastructure/, docs/ et pont Labs -> Projects

- **Date :** 2026-07-24
- **Statut :** Accepté
- **Auteurs :** Claude Code (session d'unification du workspace KLEM)

## Contexte

Les deux dépôts KLEM (`klem-labs-repository` et `klem-projects-repository`) vivaient sans parent
commun et sans mécanisme explicite reliant un projet validé côté Labs à son implémentation réelle
côté Projects. Par ailleurs, `klem-projects-repository` n'avait pas encore de dossiers dédiés aux
futurs microservices indépendants, à l'infrastructure as-code, ni d'index documentaire unifié —
alors que la feuille de route enterprise-monorepo (apps/services/packages/infrastructure/docs) les
prévoit tous.

## Décision

1. Unifier les deux dépôts sous `~/Documents/klem-enterprise-workspace/` (dossier plat, pas un
   dépôt git de plus) sans toucher à l'historique ni aux remotes de chacun.
2. Ajouter `services/`, `infrastructure/{docker,terraform,kubernetes}/`, `docs/` en **scaffolding
   uniquement** — `apps/backend-api` n'est **pas** déplacé vers `services/` : c'est un monolithe
   modulaire, étape acceptée au stade Prototype par la règle de gouvernance n°4 du dépôt Labs, pas
   une destination finale. Le déplacer aujourd'hui casserait toutes les références documentaires
   existantes pour zéro gain fonctionnel (aucun second service indépendant ne le justifie encore).
3. `docs/` reste un index léger vers `collaboration/doc/` et `knowledges/wiki/`, pas une 3e
   arborescence dupliquée.
4. Ajouter `klem-labs-repository/platform-devsecops/scripts/klem_promote.py` (+ wrapper
   `bin/klem-promote`) : réutilise exactement la logique de détection de statut de
   `generate_dashboard_data.py::portfolio_projects()` (même regex, mêmes buckets) pour ne
   promouvoir un projet Labs que lorsque la règle de gouvernance n°7 est réellement satisfaite
   (statut `beta`/`pilote`). Copie (jamais un symlink -- ne survit pas à un clone séparé) les specs
   et un manifeste horodaté vers `<cible>/docs/from-labs/`.

## Alternatives envisagées

- **Déplacer `apps/backend-api` vers `services/backend-api` immédiatement** : écarté, cf. décision
  point 2 -- aucun découpage en microservices réel n'existe encore pour le justifier.
- **Symlinks réels entre les deux dépôts** (demande initiale) : écartés -- un lien symbolique
  relatif ou absolu entre deux dépôts git indépendamment clonés casse dès qu'un autre développeur ou
  la CI clone `klem-projects-repository` seul, sans `klem-labs-repository` au même endroit relatif.
  Une copie + manifeste horodaté, régénérable à la demande, donne le même résultat sans ce risque.
- **Un `docs/` complet avec migration de contenu** : écarté pour éviter de fragmenter la
  documentation entre trois arborescences (`docs/`, `collaboration/`, `knowledges/`).

## Conséquences

- **Avantages :** zéro régression sur l'existant (aucun fichier de code déplacé) ; le pont
  Labs->Projects est un script inspectable et idempotent, pas une automatisation opaque ; un seul
  endroit (`generate_dashboard_data.py`) fait toujours autorité sur la détection de statut.
- **Dettes techniques / Risques :** `services/` et `infrastructure/` restent vides tant qu'aucun
  besoin réel n'apparaît -- à surveiller pour ne pas devenir du scaffolding mort ; l'heuristique de
  `klem_promote.py` qui choisit `apps/` vs `services/` par mots-clés de stack est volontaire
  minimale et devra être revue si un projet backend autonome sans frontend est promu.
