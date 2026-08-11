import os
from datetime import date

from klem_ref_bot.extraction import html_extractor

FIXTURES_DIR = os.path.join(os.path.dirname(__file__), "..", "fixtures")


def test_extract_texte_reglementaire_from_real_html_file():
    html_path = os.path.join(FIXTURES_DIR, "procedure_export_cacao.html")

    result = html_extractor.extract_texte_reglementaire_from_file(
        html_path, url_source="https://commerce.gouv.ci/notes/export-cacao"
    )

    assert result.titre == "Note relative à la procédure d'exportation du cacao"
    assert result.date_publication == date(2026, 2, 3)
    assert result.reference == "EXP-2026-014"
    assert result.domaine == "export"
    assert result.url_source == "https://commerce.gouv.ci/notes/export-cacao"


def test_titre_prefers_h1_over_title_tag():
    html = "<html><head><title>Autre titre</title></head><body><h1>Titre principal</h1></body></html>"

    result = html_extractor.extract_texte_reglementaire_from_html(html)

    assert result.titre == "Titre principal"


def test_titre_falls_back_to_title_tag_when_no_h1():
    html = "<html><head><title>Titre de secours</title></head><body><p>Contenu</p></body></html>"

    result = html_extractor.extract_texte_reglementaire_from_html(html)

    assert result.titre == "Titre de secours"
