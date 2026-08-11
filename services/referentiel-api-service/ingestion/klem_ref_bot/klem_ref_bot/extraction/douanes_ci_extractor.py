"""Extracteur dédié pour la base documentaire "Textes réglementaires" de douanes.ci
(https://www.douanes.ci/info/textes-reglementaires) — structure vérifiée par capture réelle d'une
page (fixtures/douanes_ci_textes_reglementaires_page0.html), pas supposée.

Contrairement à pdf_extractor.py/html_extractor.py (heuristiques génériques pour une source de
structure inconnue), cette source est un tableau Drupal Views avec des classes CSS sémantiques
stables (`views-field-field-type`, `-numero`, `-date-de-signature`, `-objet`, `-fichier`...) — un
parsing structuré direct est donc bien plus fiable qu'une heuristique regex ici, et c'est ce qui est
fait. `détecter le domaine` reste heuristique (aucune colonne dédiée dans la source) et réutilise
extraction/heuristics.py.

Distinction importante : la colonne "référence" du site est une référence CROISÉE vers un AUTRE
texte (ex. la circulaire 2413 amende la circulaire n°1295/MEFB/DGD de 2005) — pas l'identifiant du
document courant. `ExtractedTexteReglementaire.reference` est donc rempli avec la colonne "numero"
(l'identifiant du document lui-même), pas la colonne "référence" du site. Le suivi du lien entre
deux textes (amendement) n'est pas modélisé dans ce Sprint — TexteReglementaire n'a pas de champ
pour ça.

Une page listing plusieurs documents, cette fonction retourne une LISTE de propositions,
contrairement aux extracteurs PDF/HTML génériques qui n'en retournent qu'une seule à la fois.
"""

from datetime import date
from typing import List, Optional

from bs4 import BeautifulSoup

from klem_ref_bot.extraction import heuristics
from klem_ref_bot.models import ExtractedTexteReglementaire

# Valeurs réellement observées dans la colonne "type" (capture du 2026-08-11) — fallback en
# minuscule pour toute valeur non répertoriée plutôt que d'échouer.
TYPE_NORMALIZATION = {
    "CIRCULAIRE": "circulaire",
    "NOTE DE SERVICE": "note",
    "NOTE D INFORMATION": "note",
    "NOTE D'INFORMATION": "note",
    "ARRETE": "arrêté",
    "ARRÊTÉ": "arrêté",
    "DECRET": "décret",
    "DÉCRET": "décret",
    "LOI": "loi",
    "ORDONNANCE": "ordonnance",
    "AVIS": "avis",
}


def _normalize_type(raw_type: str) -> str:
    return TYPE_NORMALIZATION.get(raw_type.strip().upper(), raw_type.strip().lower())


def _parse_signature_date(date_cell) -> Optional[date]:
    span = date_cell.find("span", attrs={"property": "dc:date"})
    if span and span.get("content"):
        try:
            return date.fromisoformat(span["content"][:10])
        except ValueError:
            return None
    return None


def _cell_text(row, css_class: str) -> Optional[str]:
    cell = row.find("td", class_=css_class)
    return cell.get_text(strip=True) if cell else None


def _extract_row(row) -> Optional[ExtractedTexteReglementaire]:
    raw_type = _cell_text(row, "views-field-field-type")
    titre = _cell_text(row, "views-field-field-objet")
    if not raw_type or not titre:
        return None  # ligne mal formée (ex. en-tête) — ignorée plutôt qu'en erreur

    numero = _cell_text(row, "views-field-field-numero")

    date_cell = row.find("td", class_="views-field-field-date-de-signature")
    date_publication = _parse_signature_date(date_cell) if date_cell else None

    url_source = None
    fichier_cell = row.find("td", class_="views-field-field-fichier")
    if fichier_cell:
        link = fichier_cell.find("a")
        if link and link.get("href"):
            url_source = link["href"].strip()

    domaine_source_text = " ".join(filter(None, [
        _cell_text(row, "views-field-field-mots-cl-s") or "",
        titre,
        _cell_text(row, "views-field-field-ocr") or "",
    ]))
    domaine = heuristics.detect_domaine(domaine_source_text)

    return ExtractedTexteReglementaire(
        titre=titre,
        type=_normalize_type(raw_type),
        date_publication=date_publication,
        reference=numero,
        domaine=domaine,
        url_source=url_source,
    )


def extract_from_listing_html(html: str) -> List[ExtractedTexteReglementaire]:
    soup = BeautifulSoup(html, "lxml")
    table = soup.find("table", class_="views-table")
    if not table:
        return []
    body = table.find("tbody")
    rows = body.find_all("tr") if body else table.find_all("tr")[1:]

    proposals = []
    for row in rows:
        proposal = _extract_row(row)
        if proposal:
            proposals.append(proposal)
    return proposals


def extract_from_listing_file(path: str) -> List[ExtractedTexteReglementaire]:
    with open(path, "r", encoding="utf-8") as handle:
        return extract_from_listing_html(handle.read())
