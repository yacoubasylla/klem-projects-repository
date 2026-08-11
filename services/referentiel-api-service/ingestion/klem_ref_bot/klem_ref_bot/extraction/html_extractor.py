"""Extraction depuis une page HTML — BeautifulSoup pour isoler le titre structurel (`<title>`/
`<h1>`, plus fiable que la première ligne de texte utilisée pour le PDF) et le texte du corps, puis
mêmes heuristiques partagées que pdf_extractor.py pour le reste. Ne fait aucune requête réseau :
prend du HTML déjà récupéré (le fetch depuis une source institutionnelle n'est pas implémenté dans
ce Sprint, voir README)."""

from typing import Optional

from bs4 import BeautifulSoup

from klem_ref_bot.extraction import heuristics
from klem_ref_bot.models import ExtractedTexteReglementaire


def _detect_titre_html(soup: BeautifulSoup) -> str:
    h1 = soup.find("h1")
    if h1 and h1.get_text(strip=True):
        return h1.get_text(strip=True)[:500]
    title_tag = soup.find("title")
    if title_tag and title_tag.get_text(strip=True):
        return title_tag.get_text(strip=True)[:500]
    return heuristics.detect_titre(soup.get_text())


def extract_texte_reglementaire_from_html(html: str, url_source: Optional[str] = None) -> ExtractedTexteReglementaire:
    soup = BeautifulSoup(html, "lxml")
    body_text = soup.get_text(separator="\n")
    return ExtractedTexteReglementaire(
        titre=_detect_titre_html(soup),
        type=heuristics.detect_type(body_text) or "note",
        date_publication=heuristics.detect_date(body_text),
        reference=heuristics.detect_reference(body_text),
        domaine=heuristics.detect_domaine(body_text),
        url_source=url_source,
    )


def extract_texte_reglementaire_from_file(html_path: str, url_source: Optional[str] = None) -> ExtractedTexteReglementaire:
    with open(html_path, "r", encoding="utf-8") as handle:
        html = handle.read()
    return extract_texte_reglementaire_from_html(html, url_source=url_source)
