from klem_ref_bot.sources import is_allowed_source


def test_allows_exact_whitelisted_domain():
    assert is_allowed_source("https://douanes.ci/notes/2026-001") is True


def test_allows_subdomain_of_whitelisted_domain():
    assert is_allowed_source("https://portail.douanes.ci/notes/2026-001") is True


def test_rejects_unknown_domain():
    assert is_allowed_source("https://example.com/notes/2026-001") is False


def test_rejects_explicitly_closed_system_even_if_similar_looking():
    assert is_allowed_source("https://sydam.ci/dossier/123") is False


def test_rejects_domain_that_merely_contains_a_whitelisted_domain_as_substring():
    # ex. "guce.ci.attacker.example" ne doit pas passer parce qu'il contient "guce.ci"
    assert is_allowed_source("https://guce.ci.attacker.example/phishing") is False
