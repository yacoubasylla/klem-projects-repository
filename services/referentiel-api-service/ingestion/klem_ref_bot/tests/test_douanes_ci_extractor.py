import os
from datetime import date

from klem_ref_bot.extraction import douanes_ci_extractor

FIXTURES_DIR = os.path.join(os.path.dirname(__file__), "..", "fixtures")
LISTING_PATH = os.path.join(FIXTURES_DIR, "douanes_ci_textes_reglementaires_page0.html")


def test_extract_from_real_listing_page_finds_all_rows():
    proposals = douanes_ci_extractor.extract_from_listing_file(LISTING_PATH)

    # Capture réelle du 2026-08-11 de https://www.douanes.ci/info/textes-reglementaires — 10
    # documents visibles sur cette page (pas une valeur arbitraire, le compte réel de la capture).
    assert len(proposals) == 10


def test_extract_first_row_matches_real_captured_circulaire():
    proposals = douanes_ci_extractor.extract_from_listing_file(LISTING_PATH)
    first = proposals[0]

    assert first.titre == "Réaménagement du délai de délivrance du Bon A Enlever (BAE)."
    assert first.type == "circulaire"
    assert first.date_publication == date(2026, 7, 20)
    # "reference" = colonne "numero" de la source (identifiant du document lui-même), PAS la
    # colonne "référence" du site (qui pointe vers un AUTRE texte amendé) — voir le module.
    assert first.reference == "2413"
    assert first.url_source == "https://www.douanes.ci/sites/default/files/base_documentaire/c_2413.pdf"


def test_extract_normalizes_note_de_service_and_note_d_information_types():
    proposals = douanes_ci_extractor.extract_from_listing_file(LISTING_PATH)
    types_by_reference = {p.reference: p.type for p in proposals}

    assert types_by_reference["115"] == "note"  # NOTE DE SERVICE
    assert types_by_reference["110"] == "note"  # NOTE D INFORMATION


def test_extract_detects_domaine_from_keywords_when_present():
    proposals = douanes_ci_extractor.extract_from_listing_file(LISTING_PATH)
    by_reference = {p.reference: p for p in proposals}

    assert by_reference["2411"].domaine == "import"
    assert by_reference["2410"].domaine == "export"


def test_extract_from_listing_html_returns_empty_list_when_no_table_present():
    assert douanes_ci_extractor.extract_from_listing_html("<html><body>Rien ici</body></html>") == []
