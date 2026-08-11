"""Récupération réseau réelle depuis une source institutionnelle autorisée — la seule partie de
klem_ref_bot qui fait de vraies requêtes HTTP. Volontairement isolée dans ce module : aucun test
automatisé ne l'exerce (pas d'accès réseau en CI), et aucun autre module ne fait de requête réseau
en dehors de celui-ci — pour que la surface "fait vraiment des appels externes" reste facile à
auditer en un seul endroit.

Respecte le `Crawl-delay: 10` du robots.txt de douanes.ci (vérifié réellement le 2026-08-11,
Drupal 7) — voir `DEFAULT_CRAWL_DELAY_SECONDS`.
"""

import time
from typing import Optional

import requests

from klem_ref_bot.sources import is_allowed_source

USER_AGENT = "KLEM-ref-bot/0.1 (+https://klemtech.net; ingestion referentiel Trade-X)"
DEFAULT_TIMEOUT_SECONDS = 20
DEFAULT_CRAWL_DELAY_SECONDS = 10  # robots.txt douanes.ci — ne pas descendre sous cette valeur

DOUANES_CI_TEXTES_REGLEMENTAIRES_BASE_URL = "https://www.douanes.ci/info/textes-reglementaires"


class UnauthorizedSourceError(RuntimeError):
    pass


def fetch_url(url: str, *, timeout: int = DEFAULT_TIMEOUT_SECONDS) -> str:
    """Récupère le HTML d'une URL — refuse tout domaine hors liste blanche
    (klem_ref_bot.sources.is_allowed_source) avant même d'émettre la requête."""
    if not is_allowed_source(url):
        raise UnauthorizedSourceError(f"Source non autorisée : {url}")

    response = requests.get(url, headers={"User-Agent": USER_AGENT}, timeout=timeout)
    response.raise_for_status()
    return response.text


def fetch_douanes_ci_listing_page(page: int = 0) -> str:
    """`page` suit la convention 0-indexée de Drupal (page=0 est la première page, déjà la valeur
    par défaut du site sans paramètre) — cohérent avec la pagination réellement observée
    (`?page=1` pour "page 2" dans l'IHM)."""
    url = DOUANES_CI_TEXTES_REGLEMENTAIRES_BASE_URL
    if page > 0:
        url = f"{url}?page={page}"
    return fetch_url(url)


def fetch_douanes_ci_listing_pages(*, max_pages: int, crawl_delay_seconds: int = DEFAULT_CRAWL_DELAY_SECONDS) -> list:
    """Récupère plusieurs pages de la liste, en respectant le délai minimal entre requêtes. À
    n'utiliser que pour un vrai run planifié — jamais en boucle serrée en développement/test."""
    pages_html = []
    for page in range(max_pages):
        pages_html.append(fetch_douanes_ci_listing_page(page))
        if page < max_pages - 1:
            time.sleep(crawl_delay_seconds)
    return pages_html
