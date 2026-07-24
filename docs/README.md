# docs/

This is a thin index, not a new documentation tree. KLEM's actual documentation already lives in
two places inside this repo, and duplicating it here would just fragment it:

| Where | What |
|---|---|
| [`../collaboration/context/CONTEXT.md`](../collaboration/context/CONTEXT.md) | Product vision, business alignment, global KLEM rules |
| [`../collaboration/doc/architectures.md`](../collaboration/doc/architectures.md) | Node topology, network security, prod infrastructure |
| [`../collaboration/doc/specifications.md`](../collaboration/doc/specifications.md) | PostgreSQL schemas, API specifications |
| [`../collaboration/doc/workflows.md`](../collaboration/doc/workflows.md) | Operational state diagrams and lifecycles |
| [`../collaboration/history/`](../collaboration/history/) | Decision log, chronological history, ADRs |
| [`../knowledges/wiki/`](../knowledges/wiki/) | Internal knowledge base and procedures |
| [`../knowledges/raw/`](../knowledges/raw/) | Raw external inputs (PDFs, regulatory exports) to be indexed |

## Cross-repo documentation (Labs)

For the *business/architecture validation* behind each project (business case, viability, ADRs
predating any code) see the sibling repo one level up:
`../../klem-labs-repository/projects/<NN_nom>/` and `../../klem-labs-repository/GLOBAL_README.md`
for the governance rules that gate a project's promotion into this repo.

When a project is promoted via `klem-promote` (see
`klem-labs-repository/platform-devsecops/scripts/klem_promote.py`), a synced copy of its Labs
specs/ADRs lands at `<target>/docs/from-labs/` — that folder is generated, not hand-maintained.
