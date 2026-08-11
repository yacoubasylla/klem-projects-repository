import os
from datetime import date

from klem_ref_bot.extraction import pdf_extractor

FIXTURES_DIR = os.path.join(os.path.dirname(__file__), "..", "fixtures")


def test_extract_texte_reglementaire_from_real_pdf():
    pdf_path = os.path.join(FIXTURES_DIR, "circulaire_import_vehicules.pdf")

    result = pdf_extractor.extract_texte_reglementaire(
        pdf_path, url_source="https://douanes.ci/notes/2026-001"
    )

    assert result.titre == "Circulaire relative a l'importation de vehicules d'occasion"
    assert result.type == "circulaire"
    assert result.date_publication == date(2026, 1, 15)
    assert result.reference == "2026-001"
    assert result.domaine == "import"
    assert result.url_source == "https://douanes.ci/notes/2026-001"
