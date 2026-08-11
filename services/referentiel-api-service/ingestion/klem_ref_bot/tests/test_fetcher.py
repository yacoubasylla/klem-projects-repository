from unittest.mock import MagicMock, patch

import pytest

from klem_ref_bot import fetcher
from klem_ref_bot.fetcher import UnauthorizedSourceError


def test_fetch_url_rejects_unauthorized_source_without_making_a_request():
    with patch("klem_ref_bot.fetcher.requests.get") as mock_get:
        with pytest.raises(UnauthorizedSourceError):
            fetcher.fetch_url("https://example.com/x")

    mock_get.assert_not_called()


def test_fetch_url_calls_requests_get_with_user_agent_and_timeout_for_authorized_source():
    mock_response = MagicMock()
    mock_response.text = "<html>ok</html>"

    with patch("klem_ref_bot.fetcher.requests.get", return_value=mock_response) as mock_get:
        result = fetcher.fetch_url("https://www.douanes.ci/info/textes-reglementaires")

    assert result == "<html>ok</html>"
    mock_response.raise_for_status.assert_called_once()
    _, kwargs = mock_get.call_args
    assert kwargs["headers"]["User-Agent"] == fetcher.USER_AGENT
    assert kwargs["timeout"] == fetcher.DEFAULT_TIMEOUT_SECONDS


def test_fetch_douanes_ci_listing_page_zero_uses_base_url_without_page_param():
    mock_response = MagicMock()
    mock_response.text = "<html></html>"

    with patch("klem_ref_bot.fetcher.requests.get", return_value=mock_response) as mock_get:
        fetcher.fetch_douanes_ci_listing_page(0)

    called_url = mock_get.call_args[0][0]
    assert called_url == fetcher.DOUANES_CI_TEXTES_REGLEMENTAIRES_BASE_URL


def test_fetch_douanes_ci_listing_page_nonzero_appends_page_param():
    mock_response = MagicMock()
    mock_response.text = "<html></html>"

    with patch("klem_ref_bot.fetcher.requests.get", return_value=mock_response) as mock_get:
        fetcher.fetch_douanes_ci_listing_page(2)

    called_url = mock_get.call_args[0][0]
    assert called_url == f"{fetcher.DOUANES_CI_TEXTES_REGLEMENTAIRES_BASE_URL}?page=2"


def test_fetch_douanes_ci_listing_pages_sleeps_between_but_not_after_last_page():
    mock_response = MagicMock()
    mock_response.text = "<html></html>"

    with patch("klem_ref_bot.fetcher.requests.get", return_value=mock_response), \
         patch("klem_ref_bot.fetcher.time.sleep") as mock_sleep:
        pages = fetcher.fetch_douanes_ci_listing_pages(max_pages=3, crawl_delay_seconds=10)

    assert len(pages) == 3
    assert mock_sleep.call_count == 2  # entre page 0->1 et 1->2, jamais après la dernière
    mock_sleep.assert_called_with(10)
