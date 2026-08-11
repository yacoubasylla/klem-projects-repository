"""Extraction depuis un PDF — pdfplumber pour le texte brut, puis heuristiques partagées
(extraction/heuristics.py) pour la structuration. Ne fait aucune requête réseau : prend un chemin
de fichier local déjà téléchargé (le téléchargement depuis une source institutionnelle n'est pas
implémenté dans ce Sprint, voir README)."""

from typing import Optional

import pdfplumber

from klem_ref_bot.extraction import heuristics
from klem_ref_bot.models import ExtractedTexteReglementaire


def extract_text(pdf_path: str) -> str:
    with pdfplumber.open(pdf_path) as pdf:
        pages_text = [page.extract_text() or "" for page in pdf.pages]
    return "\n".join(pages_text)


def extract_texte_reglementaire(pdf_path: str, url_source: Optional[str] = None) -> ExtractedTexteReglementaire:
    text = extract_text(pdf_path)
    return ExtractedTexteReglementaire(
        titre=heuristics.detect_titre(text),
        type=heuristics.detect_type(text) or "note",
        date_publication=heuristics.detect_date(text),
        reference=heuristics.detect_reference(text),
        domaine=heuristics.detect_domaine(text),
        url_source=url_source,
    )
