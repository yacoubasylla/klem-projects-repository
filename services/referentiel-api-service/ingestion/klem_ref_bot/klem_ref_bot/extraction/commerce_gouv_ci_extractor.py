"""Extracteur dédié pour les publications "Textes juridiques" de commerce.gouv.ci
(sous-catégories Lois et Ordonnances/34, Arrêtés/35, Décrets/36 — voir fetcher.py) — structure
vérifiée par capture réelle d'une page (fixtures/commerce_gouv_ci_decrets_page.html), pas supposée.

Contrairement à douanes.ci (tableau Drupal Views avec colonnes type/numéro/date/objet séparées),
cette source liste chaque document comme un unique lien PDF dont le texte visible CONCATÈNE type,
numéro, date et objet en une seule chaîne (ex. "DÉCRET N° 2022-601 DU 03 AOÛT 2022 PORTANT
ORGANISATION..."). Un parsing par colonnes structurées n'a donc pas de sens ici : on réutilise
`extraction/heuristics.py` (déjà conçu pour ce type de texte libre) directement sur le texte du
lien — vérifié réellement sur la page capturée, les 4 champs (type/numéro/date) s'en extraient
correctement (voir tests). Seul le `domaine` reste souvent vide, faute de mots-clés
import/export/transit/change dans ces titres à portée organisationnelle plutôt que douanière — un
éditeur complète ce champ à la validation, comme prévu par l'architecture (statut PROPOSEE).

Pagination : contrairement à douanes.ci, la pagination de cette page est faite côté client
(JavaScript qui affiche/masque des sous-ensembles) — TOUS les documents sont déjà présents dans le
HTML retourné par le serveur en un seul GET (vérifié réellement : `#pagination .item` contient
l'intégralité de la liste). Pas de paramètre `page`/`offset` à gérer côté extracteur.
"""

from typing import List, Optional

from bs4 import BeautifulSoup

from klem_ref_bot.extraction import heuristics
from klem_ref_bot.models import ExtractedTexteReglementaire


def _extract_item(link) -> Optional[ExtractedTexteReglementaire]:
    titre = link.get_text(strip=True)
    url_source = link.get("href", "").strip()
    if not titre or not url_source:
        return None  # lien mal formé — ignoré plutôt qu'en erreur

    raw_type = heuristics.detect_type(titre)
    if not raw_type:
        return None  # pas un texte juridique reconnu (type absent du titre) — ignoré

    return ExtractedTexteReglementaire(
        titre=titre,
        type=raw_type,
        date_publication=heuristics.detect_date(titre),
        reference=heuristics.detect_reference(titre),
        domaine=heuristics.detect_domaine(titre),
        url_source=url_source,
    )


def extract_from_listing_html(html: str) -> List[ExtractedTexteReglementaire]:
    soup = BeautifulSoup(html, "lxml")
    container = soup.find("div", id="pagination")
    if not container:
        return []
    links = container.find_all("a", class_="pdf-link")

    proposals = []
    for link in links:
        proposal = _extract_item(link)
        if proposal:
            proposals.append(proposal)
    return proposals


def extract_from_listing_file(path: str) -> List[ExtractedTexteReglementaire]:
    with open(path, "r", encoding="utf-8") as handle:
        return extract_from_listing_html(handle.read())
