# klem_ref_bot

Agent d'ingestion du référentiel Trade-X — voir
`klem-labs-repository/projects/08_klem_trade_x/specifications_techniques.md` §4.2 et
`specifications_fonctionnelles.md` US-10.

**Hors runtime applicatif** : ce script Python n'est ni appelé par, ni un client de, l'API REST de
`referentiel-api-service` (Spring Boot). Il écrit **directement** dans le schéma PostgreSQL
`klem_trade_x` via un compte technique à droits restreints — jamais via l'API, jamais en statut
autre que `PROPOSEE`, jamais de publication directe. Validation humaine systématique ensuite (un
éditeur reprend la fiche via l'API applicative : `PROPOSEE → EN_REVISION → PUBLIEE/REJETEE`).

## Statut réel de ce Sprint

- **Fait** : scaffold complet (extraction, insertion DB, CLI), extraction heuristique
  (regex/mots-clés, pas de LLM) pour `TexteReglementaire` à partir d'un PDF ou HTML **local**,
  garde-fou de liste blanche de sources (`klem_ref_bot/sources.py`).
- **Vérifié réellement**, pas seulement par des tests avec DB mockée : migrations Flyway V1-V5
  appliquées contre un vrai PostgreSQL (conteneur Docker manuel — Testcontainers reste bloqué dans
  ce bac à sable par la même limitation d'API Docker que le reste du service, voir `README.md`
  racine du service), trigger `enforce_proposee_on_insert` testé (rejette un INSERT non-PROPOSEE,
  autorise un UPDATE vers un autre statut), compte `klem_ref_bot_ingestion` provisionné et confirmé
  incapable de SELECT/UPDATE/DELETE, CLI exécuté de bout en bout contre cette base réelle.
- **Pas fait** : téléchargement réel depuis une source institutionnelle (GUCE CI, Douanes CI...) —
  le CLI attend un fichier déjà local (`--pdf`/`--html`). Extraction pour `ProcedureMetier`/
  `DocumentRequis`/`OperationCommerce` — seule `TexteReglementaire` correspond au triplet
  "titre, type, date, domaine" décrit par la spec §4.2 ; les trois autres entités auraient besoin
  d'heuristiques dédiées par source une fois une vraie source onboardée. Exécution planifiée réelle
  (le workflow CI ci-dessous est câblé mais nécessite des secrets DB non fournis ici).

## Installation

```bash
cd services/referentiel-api-service/ingestion/klem_ref_bot
python3 -m venv .venv && source .venv/bin/activate  # ou pip install --user si venv indisponible
pip install -r requirements.txt
```

## Utilisation

```bash
export KLEM_REF_BOT_DATABASE_URL="postgresql://klem_ref_bot_ingestion:<mot_de_passe>@<hote>:<port>/<base>"

python -m klem_ref_bot.main --pdf chemin/vers/texte.pdf --url-source https://douanes.ci/notes/2026-001
python -m klem_ref_bot.main --html chemin/vers/page.html --url-source https://guce.ci/notes/x
```

`--url-source` est validée contre `klem_ref_bot/sources.py::ALLOWED_SOURCE_DOMAINS` avant toute
extraction — une source hors liste blanche est rejetée immédiatement (code de sortie 1).

## Tests

```bash
PYTHONPATH=. pytest tests/ -v
```

27 tests unitaires (heuristiques, extracteurs PDF/HTML contre des fixtures réelles, liste blanche
des sources, insertion DB avec connexion mockée). Aucun ne nécessite de PostgreSQL réel.

## Provisionnement du compte technique restreint

À exécuter par les opérations (jamais de mot de passe en dur dans une migration versionnée — voir
`V5__enforce_proposee_on_insert.sql`) :

```sql
CREATE ROLE klem_ref_bot_ingestion LOGIN PASSWORD '<via secret manager>';
GRANT USAGE ON SCHEMA klem_trade_x TO klem_ref_bot_ingestion;
GRANT INSERT ON klem_trade_x.texte_reglementaire, klem_trade_x.procedure_metier,
    klem_trade_x.document_requis, klem_trade_x.operation_commerce TO klem_ref_bot_ingestion;
```

Volontairement pas de SELECT/UPDATE/DELETE — confirmé par test réel (voir "Statut réel" ci-dessus).

## Architecture

```
Source institutionnelle (PDF/HTML, déjà téléchargé localement dans ce Sprint)
        │
        ▼
  extraction/ (heuristiques regex — pas de LLM, pas de réseau)
        │
        ▼
  ExtractedTexteReglementaire (dataclass, models.py)
        │
        ▼
  db.py — INSERT direct, statut='PROPOSEE' forcé (+ trigger DB en filet de sécurité)
        │
        ▼
  klem_trade_x.texte_reglementaire (schéma partagé avec referentiel-api-service)
        │
        ▼
  Éditeur humain reprend via l'API applicative (jamais ce script)
```
