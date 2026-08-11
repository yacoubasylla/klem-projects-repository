"""Extraction structurée par règles simples (regex/mots-clés) — pas d'appel LLM dans ce Sprint
(scope validé : "Scaffold + extraction heuristique simple"). La spec reconnaît elle-même que la
qualité d'extraction sur des PDF hétérogènes est le risque principal du MVP et que la validation
humaine reste le filet de sécurité (specifications_techniques.md §6) — ces heuristiques n'ont donc
pas besoin d'être exhaustives, seulement un point de départ raisonnable à corriger par un éditeur.

Fonctions pures : texte en entrée, aucune I/O ici (PDF/HTML sont convertis en texte brut par
pdf_extractor.py/html_extractor.py avant d'arriver ici).
"""

import re
from datetime import date
from typing import Optional

TYPE_KEYWORDS = {
    "circulaire": "circulaire",
    "décret": "décret",
    "decret": "décret",
    "arrêté": "arrêté",
    "arrete": "arrêté",
    "loi": "loi",
    "ordonnance": "ordonnance",
    "note de service": "note",
    "note d'information": "note",
    "avis": "avis",
}

DOMAINE_KEYWORDS = {
    "import": "import",
    "importation": "import",
    "export": "export",
    "exportation": "export",
    "transit": "transit",
    "change": "change",
    "devise": "change",
}

FRENCH_MONTHS = {
    "janvier": 1, "février": 2, "fevrier": 2, "mars": 3, "avril": 4, "mai": 5, "juin": 6,
    "juillet": 7, "août": 8, "aout": 8, "septembre": 9, "octobre": 10, "novembre": 11,
    "décembre": 12, "decembre": 12,
}

_NUMERIC_DATE_RE = re.compile(r"\b(\d{1,2})[/\-](\d{1,2})[/\-](\d{4})\b")
_TEXTUAL_DATE_RE = re.compile(
    r"\b(\d{1,2})\s+(" + "|".join(FRENCH_MONTHS.keys()) + r")\s+(\d{4})\b",
    re.IGNORECASE,
)
_REFERENCE_RE = re.compile(
    # (?![a-zéèêàâîôûï]) : empêche "réf"/"ref" de matcher comme préfixe de "référence"/
    # "reference" en laissant le reste du mot ("érence"/"erence") être happé par erreur dans le
    # groupe capturé — bug constaté par exécution réelle de ces heuristiques, pas anticipé.
    r"(?:n°|r[ée]f(?:érence|erence)?\.?)(?![a-zéèêàâîôûï])\s*[:\-]?\s*([A-Za-z0-9][A-Za-z0-9\-/\.]{2,})",
    re.IGNORECASE,
)


def detect_titre(text: str, max_length: int = 500) -> str:
    """Première ligne non vide — heuristique volontairement simple, à corriger par un éditeur si
    le document ne suit pas la convention "titre en première ligne" (ex. en-tête administratif)."""
    for line in text.splitlines():
        stripped = line.strip()
        if stripped:
            return stripped[:max_length]
    return ""


def detect_type(text: str) -> Optional[str]:
    lowered = text.lower()
    for keyword, normalized in TYPE_KEYWORDS.items():
        if keyword in lowered:
            return normalized
    return None


def detect_domaine(text: str) -> Optional[str]:
    lowered = text.lower()
    for keyword, normalized in DOMAINE_KEYWORDS.items():
        if keyword in lowered:
            return normalized
    return None


def detect_reference(text: str) -> Optional[str]:
    match = _REFERENCE_RE.search(text)
    if not match:
        return None
    candidate = match.group(1)
    # Une vraie référence contient toujours au moins un chiffre — sans cette garde, un texte comme
    # "Aucune référence mentionnée ici." capture "mentionn" (le mot suivant l'étiquette détectée)
    # comme si c'était une référence valide. Bug constaté par exécution réelle des tests.
    if not any(char.isdigit() for char in candidate):
        return None
    return candidate


def detect_date(text: str) -> Optional[date]:
    numeric_match = _NUMERIC_DATE_RE.search(text)
    if numeric_match:
        day, month, year = (int(part) for part in numeric_match.groups())
        try:
            return date(year, month, day)
        except ValueError:
            pass  # jour/mois hors bornes (ex. faux positif) — on retente la forme textuelle

    textual_match = _TEXTUAL_DATE_RE.search(text)
    if textual_match:
        day = int(textual_match.group(1))
        month = FRENCH_MONTHS[textual_match.group(2).lower()]
        year = int(textual_match.group(3))
        try:
            return date(year, month, day)
        except ValueError:
            return None

    return None
