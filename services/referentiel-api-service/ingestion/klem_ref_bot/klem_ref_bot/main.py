"""Point d'entrée CLI de klem_ref_bot — orchestre extraction puis proposition en base.

Deux modes :
- Document local unique (--pdf/--html) : ne fait aucune requête réseau, extraction heuristique
  générique (extraction/heuristics.py).
- Liste réelle douanes.ci (--douanes-ci-page) : fait une vraie requête HTTP (klem_ref_bot.fetcher)
  contre https://www.douanes.ci/info/textes-reglementaires, extraction structurée dédiée
  (extraction/douanes_ci_extractor.py), propose TOUTES les fiches trouvées sur la page.

Usage :
    python -m klem_ref_bot.main --pdf chemin/vers/texte.pdf --url-source https://douanes.ci/x
    python -m klem_ref_bot.main --html chemin/vers/page.html --url-source https://commerce.gouv.ci/x
    python -m klem_ref_bot.main --douanes-ci-page 0
"""

import argparse
import sys
from typing import List, Optional

import psycopg2

from klem_ref_bot import checkpoint, db, fetcher
from klem_ref_bot.config import MissingConfigError, load_database_config
from klem_ref_bot.extraction import douanes_ci_extractor, html_extractor, pdf_extractor
from klem_ref_bot.models import ExtractedTexteReglementaire
from klem_ref_bot.sources import is_allowed_source


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="klem_ref_bot — ingestion de textes réglementaires Trade-X")
    source = parser.add_mutually_exclusive_group(required=True)
    source.add_argument("--pdf", metavar="CHEMIN", help="Chemin vers un PDF local déjà téléchargé")
    source.add_argument("--html", metavar="CHEMIN", help="Chemin vers un fichier HTML local déjà téléchargé")
    source.add_argument("--douanes-ci-page", metavar="N", type=int,
                         help="Récupère et propose toutes les fiches de la page N (0-indexée) de "
                              "https://www.douanes.ci/info/textes-reglementaires — vraie requête réseau")
    parser.add_argument("--url-source", metavar="URL", default=None,
                         help="URL d'origine du document (--pdf/--html uniquement), pour traçabilité "
                              "— validée contre la liste blanche des sources autorisées si fournie")
    return parser


def extract(args: argparse.Namespace) -> ExtractedTexteReglementaire:
    if args.pdf:
        return pdf_extractor.extract_texte_reglementaire(args.pdf, url_source=args.url_source)
    return html_extractor.extract_texte_reglementaire_from_file(args.html, url_source=args.url_source)


def propose(proposal: ExtractedTexteReglementaire) -> str:
    """Insère une proposition en base et retourne l'id généré. Sépare volontairement l'extraction
    (pure, testable sans DB) de l'écriture (nécessite une vraie connexion Postgres)."""
    config = load_database_config()
    with db.connect(config) as connection:
        return db.insert_texte_reglementaire(connection, proposal)


def propose_many(proposals: List[ExtractedTexteReglementaire]) -> List[str]:
    """Insère plusieurs propositions dans UNE connexion — tout ou rien : si une ligne échoue,
    aucune de la page n'est committée (voir db.connect). Un échec partiel silencieux serait pire
    qu'un nouvel essai complet de la page au prochain run planifié."""
    config = load_database_config()
    entry_ids = []
    with db.connect(config) as connection:
        for proposal in proposals:
            entry_ids.append(db.insert_texte_reglementaire(connection, proposal))
    return entry_ids


def run(argv: Optional[list] = None) -> int:
    parser = build_parser()
    args = parser.parse_args(argv)

    if args.douanes_ci_page is not None:
        return _run_douanes_ci_page(args.douanes_ci_page)
    return _run_single_document(args)


def _run_single_document(args: argparse.Namespace) -> int:
    if args.url_source and not is_allowed_source(args.url_source):
        print(f"Source non autorisée : {args.url_source} — voir klem_ref_bot.sources.ALLOWED_SOURCE_DOMAINS",
              file=sys.stderr)
        return 1

    proposal = extract(args)
    try:
        entry_id = propose(proposal)
    except MissingConfigError as error:
        print(str(error), file=sys.stderr)
        return 1
    except psycopg2.OperationalError as error:
        print(f"Connexion à la base impossible : {error}", file=sys.stderr)
        return 1

    print(f"Proposition créée en statut PROPOSEE : {entry_id} ({proposal.titre!r})")
    return 0


def _run_douanes_ci_page(page: int) -> int:
    try:
        html = fetcher.fetch_douanes_ci_listing_page(page)
    except fetcher.UnauthorizedSourceError as error:
        print(str(error), file=sys.stderr)
        return 1

    all_proposals = douanes_ci_extractor.extract_from_listing_html(html)
    if not all_proposals:
        print(f"Aucune fiche trouvée sur la page {page} — structure de page inattendue ?", file=sys.stderr)
        return 1

    state = checkpoint.load()
    proposals = checkpoint.filter_new(all_proposals, state)
    already_known = len(all_proposals) - len(proposals)

    if not proposals:
        print(f"{already_known} fiche(s) déjà proposée(s) lors d'un run précédent (checkpoint) — "
              f"rien de nouveau sur la page {page}.")
        return 0

    try:
        entry_ids = propose_many(proposals)
    except MissingConfigError as error:
        print(str(error), file=sys.stderr)
        return 1
    except psycopg2.OperationalError as error:
        print(f"Connexion à la base impossible : {error}", file=sys.stderr)
        return 1

    # Le checkpoint n'est mis à jour QU'APRÈS un insert réussi — sinon un document jamais
    # réellement inséré serait considéré comme déjà connu et perdu silencieusement.
    checkpoint.save(checkpoint.update(state, proposals))

    print(f"{len(entry_ids)} proposition(s) créée(s) en statut PROPOSEE (page {page}, "
          f"{already_known} déjà connue(s) ignorée(s)) :")
    for entry_id, proposal in zip(entry_ids, proposals):
        print(f"  {entry_id} — {proposal.type} {proposal.reference} : {proposal.titre!r}")
    return 0


def main() -> None:
    sys.exit(run())


if __name__ == "__main__":
    main()
