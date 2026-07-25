# docs/

Ceci est un index léger, pas une nouvelle arborescence de documentation. La documentation réelle de
KLEM vit déjà à deux endroits dans ce dépôt, et la dupliquer ici ne ferait que la fragmenter :

| Où | Quoi |
|---|---|
| [`../collaboration/context/CONTEXT.md`](../collaboration/context/CONTEXT.md) | Vision produit, alignement métier, règles globales KLEM |
| [`../collaboration/doc/architectures.md`](../collaboration/doc/architectures.md) | Topologie des nœuds, sécurité réseau, infrastructure de production |
| [`../collaboration/doc/specifications.md`](../collaboration/doc/specifications.md) | Schémas PostgreSQL, spécifications d'API |
| [`../collaboration/doc/workflows.md`](../collaboration/doc/workflows.md) | Diagrammes d'états opérationnels et cycles de vie |
| [`../collaboration/history/`](../collaboration/history/) | Registre des décisions, historique chronologique, ADR |
| [`../knowledges/wiki/`](../knowledges/wiki/) | Base de connaissances interne et procédures |
| [`../knowledges/raw/`](../knowledges/raw/) | Renseignements externes bruts (PDF, exports réglementaires) à indexer |

## Documentation inter-dépôts (Labs)

Pour la *validation métier/architecture* derrière chaque projet (cas métier, viabilité, ADR
antérieurs à tout code), voir le dépôt frère un niveau au-dessus :
`../../klem-labs-repository/projects/<NN_nom>/` et `../../klem-labs-repository/GLOBAL_README.md`
pour les règles de gouvernance qui conditionnent la promotion d'un projet vers ce dépôt.

Lorsqu'un projet est promu via `klem-promote` (voir
`klem-labs-repository/platform-devsecops/scripts/klem_promote.py`), une copie synchronisée de ses
specs/ADR Labs atterrit dans `<target>/docs/from-labs/` — ce dossier est généré, pas maintenu à la
main.
