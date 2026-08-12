# `knowledges/` — dropbox de documents externes (dépôt Projects)

> **À ne pas confondre avec [`knowledge/`](../../knowledge/00-index.md) à la racine du workspace**
> (singulier). Les deux noms sont proches mais désignent des choses différentes :
>
> - `knowledge/` (racine, singulier) : synthèse de lecture transverse aux deux dépôts — de la
>   doctrine déjà rédigée, à jour, qui pointe vers ses sources.
> - `knowledges/` (ici, dans `klem-projects-repository`, pluriel) : **dropbox d'import** — dépôt de
>   documents externes bruts (PDF, exports, transcriptions) que vous déposez pour qu'un agent les
>   lise, avant tout traitement ou synthèse.

## Sous-dossiers

- [`raw/`](./raw/README.md) — documents externes déposés tels quels (PDF, captures, CSV, etc.).
- `wiki/` — pour une synthèse structurée/organisée une fois qu'un document de `raw/` a été traité
  (à peupler à la demande ; vide par défaut).

Le sous-dossier `output/` qui existait précédemment ne contenait que des fichiers de test
(`conversation-gemini.txt`, `fondations.png`) sans rôle documenté — supprimé le 2026-08-12. Si un
usage réel apparaît (ex. artefacts générés par un agent à partir de `raw/`), recréer le dossier à
ce moment-là avec un README expliquant son rôle.
