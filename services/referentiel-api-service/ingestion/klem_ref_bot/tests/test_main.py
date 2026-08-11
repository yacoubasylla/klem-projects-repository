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


def test_run_douanes_ci_page_fetches_extracts_and_proposes_all_entries():
    fake_html = "<html>fake listing</html>"
    fake_proposals = [
        ExtractedTexteReglementaire(titre="A", type="circulaire", date_publication=None,
                                     reference="2413", domaine=None, url_source="https://douanes.ci/a.pdf"),
        ExtractedTexteReglementaire(titre="B", type="note", date_publication=None,
                                     reference="115", domaine=None, url_source="https://douanes.ci/b.pdf"),
    ]

    with patch("klem_ref_bot.main.fetcher.fetch_douanes_ci_listing_page", return_value=fake_html) as mock_fetch, \
         patch("klem_ref_bot.main.douanes_ci_extractor.extract_from_listing_html", return_value=fake_proposals) as mock_extract, \
         patch("klem_ref_bot.main.checkpoint.load", return_value={}), \
         patch("klem_ref_bot.main.checkpoint.save") as mock_save, \
         patch("klem_ref_bot.main.propose_many", return_value=["id-1", "id-2"]) as mock_propose_many:
        exit_code = main.run(["--douanes-ci-page", "0"])

    assert exit_code == 0
    mock_fetch.assert_called_once_with(0)
    mock_extract.assert_called_once_with(fake_html)
    mock_propose_many.assert_called_once_with(fake_proposals)
    mock_save.assert_called_once()  # checkpoint mis à jour uniquement après un insert réussi


def test_run_douanes_ci_page_fails_cleanly_when_no_entries_found():
    with patch("klem_ref_bot.main.fetcher.fetch_douanes_ci_listing_page", return_value="<html></html>"), \
         patch("klem_ref_bot.main.douanes_ci_extractor.extract_from_listing_html", return_value=[]), \
         patch("klem_ref_bot.main.propose_many") as mock_propose_many:
        exit_code = main.run(["--douanes-ci-page", "0"])

    assert exit_code == 1
    mock_propose_many.assert_not_called()


def test_run_douanes_ci_page_skips_db_entirely_when_all_entries_already_known():
    fake_html = "<html>fake listing</html>"
    fake_proposals = [
        ExtractedTexteReglementaire(titre="A", type="circulaire", date_publication=None,
                                     reference="2413", domaine=None, url_source="https://douanes.ci/a.pdf"),
    ]

    with patch("klem_ref_bot.main.fetcher.fetch_douanes_ci_listing_page", return_value=fake_html), \
         patch("klem_ref_bot.main.douanes_ci_extractor.extract_from_listing_html", return_value=fake_proposals), \
         patch("klem_ref_bot.main.checkpoint.load", return_value={"circulaire": 2413}), \
         patch("klem_ref_bot.main.checkpoint.save") as mock_save, \
         patch("klem_ref_bot.main.propose_many") as mock_propose_many:
        exit_code = main.run(["--douanes-ci-page", "0"])

    assert exit_code == 0
    mock_propose_many.assert_not_called()
    mock_save.assert_not_called()  # rien de nouveau -> pas de raison de réécrire le checkpoint
