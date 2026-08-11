import os
from unittest.mock import patch

import pytest

from klem_ref_bot import main
from klem_ref_bot.models import ExtractedTexteReglementaire

FIXTURES_DIR = os.path.join(os.path.dirname(__file__), "..", "fixtures")


def test_run_rejects_unauthorized_source_without_extracting_or_proposing():
    pdf_path = os.path.join(FIXTURES_DIR, "circulaire_import_vehicules.pdf")

    with patch("klem_ref_bot.main.extract") as mock_extract, \
         patch("klem_ref_bot.main.propose") as mock_propose:
        exit_code = main.run(["--pdf", pdf_path, "--url-source", "https://example.com/x"])

    assert exit_code == 1
    mock_extract.assert_not_called()
    mock_propose.assert_not_called()


def test_run_extracts_and_proposes_for_authorized_pdf_source():
    pdf_path = os.path.join(FIXTURES_DIR, "circulaire_import_vehicules.pdf")
    fake_proposal = ExtractedTexteReglementaire(
        titre="Titre", type="circulaire", date_publication=None,
        reference=None, domaine=None, url_source="https://douanes.ci/x",
    )

    with patch("klem_ref_bot.main.extract", return_value=fake_proposal) as mock_extract, \
         patch("klem_ref_bot.main.propose", return_value="fake-id-123") as mock_propose:
        exit_code = main.run(["--pdf", pdf_path, "--url-source", "https://douanes.ci/x"])

    assert exit_code == 0
    mock_extract.assert_called_once()
    mock_propose.assert_called_once_with(fake_proposal)


def test_run_rejects_both_pdf_and_html_given_together():
    pdf_path = os.path.join(FIXTURES_DIR, "circulaire_import_vehicules.pdf")
    html_path = os.path.join(FIXTURES_DIR, "procedure_export_cacao.html")

    with pytest.raises(SystemExit):
        main.run(["--pdf", pdf_path, "--html", html_path])


def test_extract_dispatches_to_pdf_extractor_when_pdf_given():
    args = main.build_parser().parse_args(
        ["--pdf", os.path.join(FIXTURES_DIR, "circulaire_import_vehicules.pdf")]
    )

    proposal = main.extract(args)

    assert proposal.type == "circulaire"


def test_extract_dispatches_to_html_extractor_when_html_given():
    args = main.build_parser().parse_args(
        ["--html", os.path.join(FIXTURES_DIR, "procedure_export_cacao.html")]
    )

    proposal = main.extract(args)

    assert proposal.domaine == "export"
