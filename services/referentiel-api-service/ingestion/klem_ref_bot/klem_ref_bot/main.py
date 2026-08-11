"""Point d'entrée CLI de klem_ref_bot — orchestre extraction puis proposition en base pour un seul
document local à la fois. Ne fait aucune requête réseau : le téléchargement depuis une source
institutionnelle n'est pas implémenté dans ce Sprint (voir README, "Hors périmètre de ce Sprint").

Usage :
    python -m klem_ref_bot.main --pdf chemin/vers/texte.pdf --url-source https://douanes.ci/x
    python -m klem_ref_bot.main --html chemin/vers/page.html --url-source https://guce.ci/x
"""

import argparse
import sys
from typing import Optional

import psycopg2

from klem_ref_bot import db
from klem_ref_bot.config import MissingConfigError, load_database_config
from klem_ref_bot.extraction import html_extractor, pdf_extractor
from klem_ref_bot.models import ExtractedTexteReglementaire
from klem_ref_bot.sources import is_allowed_source


class UnauthorizedSourceError(RuntimeError):
    pass


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="klem_ref_bot — ingestion d'un texte réglementaire Trade-X")
    source = parser.add_mutually_exclusive_group(required=True)
    source.add_argument("--pdf", metavar="CHEMIN", help="Chemin vers un PDF local déjà téléchargé")
    source.add_argument("--html", metavar="CHEMIN", help="Chemin vers un fichier HTML local déjà téléchargé")
    parser.add_argument("--url-source", metavar="URL", default=None,
                         help="URL d'origine du document, pour traçabilité — validée contre la liste "
                              "blanche des sources autorisées (klem_ref_bot.sources) si fournie")
    return parser


def extract(args: argparse.Namespace) -> ExtractedTexteReglementaire:
    if args.pdf:
        return pdf_extractor.extract_texte_reglementaire(args.pdf, url_source=args.url_source)
    return html_extractor.extract_texte_reglementaire_from_file(args.html, url_source=args.url_source)


def propose(proposal: ExtractedTexteReglementaire) -> str:
    """Insère la proposition en base et retourne l'id généré. Sépare volontairement l'extraction
    (pure, testable sans DB) de l'écriture (nécessite une vraie connexion Postgres)."""
    config = load_database_config()
    with db.connect(config) as connection:
        return db.insert_texte_reglementaire(connection, proposal)


def run(argv: Optional[list] = None) -> int:
    parser = build_parser()
    args = parser.parse_args(argv)

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


def main() -> None:
    sys.exit(run())


if __name__ == "__main__":
    main()
