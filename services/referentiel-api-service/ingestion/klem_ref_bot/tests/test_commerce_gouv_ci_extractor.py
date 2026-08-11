import os
from datetime import date

from klem_ref_bot.extraction import commerce_gouv_ci_extractor

FIXTURES_DIR = os.path.join(os.path.dirname(__file__), "..", "fixtures")
DECRETS_PATH = os.path.join(FIXTURES_DIR, "commerce_gouv_ci_decrets_page.html")


def test_extract_from_real_decrets_page_finds_all_items():
    proposals = commerce_gouv_ci_extractor.extract_from_listing_file(DECRETS_PATH)

    # Capture réelle du 2026-08-11 de https://www.commerce.gouv.ci/publications/sous-categorie/36
    # — 7 décrets réellement présents sur cette page (pagination client-side, tout est dans le HTML
    # d'un seul GET), pas une valeur arbitraire.
    assert len(proposals) == 7


def test_extract_parses_type_numero_date_from_concatenated_title():
    proposals = commerce_gouv_ci_extractor.extract_from_listing_file(DECRETS_PATH)
    first = proposals[0]

    assert first.type == "décret"
    assert first.reference == "2022-601"
    assert first.date_publication == date(2022, 8, 3)
    assert first.url_source == "https://www.commerce.gouv.ci/uploads/publications/176399030555.pdf"
    assert "ORGANISATION" in first.titre  # "P0RTANT" (zéro) : coquille présente dans la source réelle


def test_extract_handles_title_without_any_numero():
    proposals = commerce_gouv_ci_extractor.extract_from_listing_file(DECRETS_PATH)
    by_url = {p.url_source: p for p in proposals}

    # "DÉCRET DU 21 JUIN 2017 PORTANT MODALITÉS..." — pas de "N°" dans le titre source réel.
    sans_numero = by_url["https://www.commerce.gouv.ci/uploads/publications/174585549190.pdf"]
    assert sans_numero.type == "décret"
    assert sans_numero.reference is None
    assert sans_numero.date_publication == date(2017, 6, 21)


def test_extract_from_listing_html_returns_empty_list_when_no_pagination_container():
    assert commerce_gouv_ci_extractor.extract_from_listing_html("<html><body>Rien ici</body></html>") == []
