# klem_ref_bot

Agent d'ingestion du référentiel Trade-X — voir
`klem-labs-repository/projects/08_klem_trade_x/specifications_techniques.md` §4.2 et
`specifications_fonctionnelles.md` US-10.

**Hors runtime applicatif** : ce script Python n'est ni appelé par, ni un client de, l'API REST de
`referentiel-api-service` (Spring Boot). Il écrit **directement** dans le schéma PostgreSQL
`klem_trade_x` via un compte technique à droits restreints — jamais via l'API, jamais en statut
autre que `PROPOSEE`, jamais de publication directe. Validation humaine systématique ensuite (un
éditeur reprend la fiche via l'API applicative : `PROPOSEE → EN_REVISION → PUBLIEE/REJETEE`).

## Sources institutionnelles — statut réel, vérifié par requête réelle (2026-08-11)

- **Douanes CI — exploitable.** `https://www.douanes.ci/info/textes-reglementaires` est une vraie
  base documentaire consultable (Drupal 7, 457 pages, colonnes structurées type/numéro/date/objet/
  fichier). Extracteur dédié `extraction/douanes_ci_extractor.py` (parsing structuré par classes
  CSS, pas une heuristique regex — la structure est fiable et connue). `robots.txt` impose un
  `Crawl-delay: 10` secondes, respecté par `fetcher.py`.
- **commerce.gouv.ci — exploitable.** `robots.txt` entièrement ouvert (`Disallow:` vide, aucun
  `Crawl-delay`). 3 catégories "Textes juridiques" réellement identifiées (menu Publications) :
  Lois et Ordonnances (`/publications/sous-categorie/34`), Arrêtés (`/35`), Décrets (`/36`) —
  pagination client-side (tout le contenu tient dans le HTML d'un seul GET par catégorie, vérifié
  réellement). Contrairement à douanes.ci, chaque document est un unique lien PDF dont le texte
  concatène type/numéro/date/objet (ex. `"DÉCRET N° 2022-601 DU 03 AOÛT 2022 PORTANT..."`) — pas de
  colonnes séparées, donc extraction par les heuristiques génériques
  (`extraction/heuristics.py`) directement sur ce texte plutôt qu'un parsing par colonnes CSS
  (`extraction/commerce_gouv_ci_extractor.py`). Déduplication par URL de PDF plutôt que par numero
  monotone (voir `checkpoint.py` — les références de cette source ne sont pas des entiers
  strictement croissants par type comme sur douanes.ci).
- **GUCE CI — bloqué, volontairement absent de la liste blanche.** Le vrai domaine est
  `guce.gouv.ci` (pas `guce.ci`, supposition initiale erronée corrigée). Il redirige immédiatement
  vers une authentification SSO (JOSSO), y compris pour `/robots.txt` à la racine — pas une source
  librement scrapable sans accès partenaire. Ne pas retenter sans identifiants/accès négocié.

## Statut réel de ce Sprint

- **Fait et vérifié réellement** (pas seulement testé avec DB/réseau mockés) :
  - Migrations Flyway V1-V5 appliquées contre un vrai PostgreSQL (conteneur Docker manuel —
    Testcontainers reste bloqué dans ce bac à sable par la même limitation d'API Docker que le
    reste du service).
  - Trigger `enforce_proposee_on_insert` testé (rejette un INSERT non-PROPOSEE, autorise un UPDATE
    vers un autre statut).
  - Compte `klem_ref_bot_ingestion` provisionné et confirmé réellement incapable de
    SELECT/UPDATE/DELETE.
  - **Pipeline complet de bout en bout contre le vrai douanes.ci** :
    `python -m klem_ref_bot.main --douanes-ci-page 0` — vraie requête HTTP, extraction structurée,
    10 vraies circulaires/notes de service insérées dans un vrai PostgreSQL, toutes en `PROPOSEE`.
  - **Pipeline complet de bout en bout contre le vrai commerce.gouv.ci** :
    `python -m klem_ref_bot.main --commerce-ci` — vraies requêtes HTTP (3 catégories), extraction
    par heuristiques génériques sur le texte concaténé de chaque lien PDF, 14 vrais textes
    juridiques (6 arrêtés, 7 décrets, 1 loi) insérés dans un vrai PostgreSQL, toutes en `PROPOSEE`.
  - Extraction heuristique générique (regex/mots-clés, pas de LLM) pour `TexteReglementaire` à
    partir d'un PDF ou HTML local isolé (`--pdf`/`--html`) — utile pour une source dont la
    structure n'est pas encore connue/dédiée.
- **Déduplication — tranchée et implémentée, deux mécanismes** (`klem_ref_bot/checkpoint.py`),
  choisis selon ce que chaque source garantit réellement :
  - **douanes.ci** : un fichier de checkpoint retient, par type de document, le plus grand `numero`
    déjà proposé avec succès (les numéros croissent de façon monotone par type sur cette source,
    confirmé par capture réelle). **Vérifié réellement** : deux exécutions successives de
    `--douanes-ci-page 0` contre un vrai PostgreSQL — la première insère 10 fiches et écrit le
    checkpoint, la seconde les reconnaît toutes comme déjà connues et n'insère rien.
  - **commerce.gouv.ci** : cette source n'a pas de `numero` monotone exploitable (références
    composites type "2022-601", pas des entiers strictement croissants) — un fichier de checkpoint
    séparé retient l'ensemble des URLs de PDF déjà proposées (identifiant stable et unique par
    document sur cette source). **Vérifié réellement** : deux exécutions successives de
    `--commerce-ci` contre un vrai PostgreSQL — la première insère 14 fiches, la seconde les
    reconnaît toutes comme déjà connues et n'insère rien.
  - Choisi plutôt qu'une contrainte `UNIQUE(type, reference)` en DB (suppose que deux sources ne
    réutiliseront jamais le même couple type/numéro — hypothèse non vérifiée, et de toute façon
    inapplicable à commerce.gouv.ci où `reference` peut être absente) ou qu'un tri purement manuel
    par les éditeurs — cohérent avec le compte `klem_ref_bot_ingestion` qui n'a délibérément aucun
    droit SELECT (voir provisionnement ci-dessous) : la déduplication ne peut donc pas se faire
    côté DB avec ce compte, un checkpoint écrit par le process appelant est la seule option qui
    respecte ce modèle de sécurité.
- **Exécution planifiée réelle — câblée, secret manquant.** Le workflow
  (`.github/workflows/klem-ref-bot-scheduled.yml`) invoque désormais
  `python -m klem_ref_bot.main --douanes-ci-page 0` PUIS `--commerce-ci` pour de vrai, avec
  persistance des deux fichiers de checkpoint entre deux runs via `actions/cache` (clé unique par
  run + `restore-keys` en préfixe, puisqu'un cache GitHub Actions est immuable une fois écrit — pas
  de clé fixe réutilisable). Les étapes d'ingestion sont gardées par
  `if: secrets.KLEM_REF_BOT_DATABASE_URL != ''` : no-op silencieux tant que ce secret n'est pas
  configuré sur ce dépôt GitHub (seul point encore manquant, hors de portée de ce dépôt de code —
  voir "Provisionnement" ci-dessous).
- **Pas fait** : extraction pour `ProcedureMetier`/`DocumentRequis`/`OperationCommerce` — seule
  `TexteReglementaire` correspond au triplet "titre, type, date, domaine" décrit par la spec §4.2 ;
  les trois autres entités auraient besoin d'heuristiques ou d'un extracteur dédié par source une
  fois une source pertinente identifiée.

## Installation

```bash
cd services/referentiel-api-service/ingestion/klem_ref_bot
python3 -m venv .venv && source .venv/bin/activate  # ou pip install --user si venv indisponible
pip install -r requirements.txt
```

## Utilisation

```bash
export KLEM_REF_BOT_DATABASE_URL="postgresql://klem_ref_bot_ingestion:<mot_de_passe>@<hote>:<port>/<base>"

# Document local isolé (aucune requête réseau)
python -m klem_ref_bot.main --pdf chemin/vers/texte.pdf --url-source https://douanes.ci/notes/2026-001
python -m klem_ref_bot.main --html chemin/vers/page.html --url-source https://commerce.gouv.ci/notes/x

# Liste réelle douanes.ci (vraie requête HTTP, propose toutes les fiches de la page)
python -m klem_ref_bot.main --douanes-ci-page 0

# Textes juridiques réels commerce.gouv.ci (vraies requêtes HTTP, 3 catégories)
python -m klem_ref_bot.main --commerce-ci
```

`--url-source` (mode `--pdf`/`--html`) est validée contre
`klem_ref_bot/sources.py::ALLOWED_SOURCE_DOMAINS` avant toute extraction — une source hors liste
blanche est rejetée immédiatement (code de sortie 1). Les modes `--douanes-ci-page`/`--commerce-ci`
passent par la même liste blanche via `fetcher.fetch_url`.

## Tests

```bash
PYTHONPATH=. pytest tests/ -v
```

65 tests unitaires — heuristiques, extracteurs PDF/HTML/douanes.ci/commerce.gouv.ci contre des
fixtures **réelles** (pages effectivement capturées, pas fabriquées), liste blanche des sources,
checkpoint de déduplication — numero (douanes.ci) et URL (commerce.gouv.ci) — (fichiers réels via
`tmp_path`, isolés), insertion DB et fetch réseau avec connexion/requête mockées. Aucun ne
nécessite de PostgreSQL ni d'accès réseau réel — voir "Statut réel de ce Sprint" pour ce qui a été
vérifié manuellement en plus des tests.

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
Conséquence directe : voir la section déduplication ci-dessus, ce compte ne peut pas s'auto-vérifier.

## Architecture

```
douanes.ci (vraie requête HTTP, fetcher.py — respecte Crawl-delay: 10s de robots.txt)
   OU
commerce.gouv.ci (vraie requête HTTP, fetcher.py — 3 catégories, délai de politesse 3s)
   OU
Document local PDF/HTML déjà téléchargé (--pdf/--html)
        │
        ▼
  extraction/ — douanes_ci_extractor.py (structuré par colonnes CSS) ou
                commerce_gouv_ci_extractor.py (heuristiques sur texte concaténé) ou
                pdf_extractor.py/html_extractor.py (heuristiques regex génériques,
                extraction/heuristics.py)
        │
        ▼
  ExtractedTexteReglementaire (dataclass, models.py) — un ou plusieurs
        │
        ▼
  checkpoint.py — filtre les fiches déjà proposées lors d'un run précédent (douanes.ci et
                  commerce.gouv.ci ; sans effet en mode --pdf/--html, un seul document à la fois)
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
