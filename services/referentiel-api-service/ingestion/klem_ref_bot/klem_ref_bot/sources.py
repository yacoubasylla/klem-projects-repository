"""Registre des sources institutionnelles autorisées — specifications_techniques.md §4.1 :
`klem_ref_bot` ne traite que des sources publiques ou explicitement conventionnées, jamais un accès
supposé à un système fermé (ex: SYDAM World). Cette liste blanche est le seul garde-fou technique de
ce Sprint, appliquée réellement par `extraction/douanes_ci_extractor.py` et `main.py` — toute future
implémentation d'ingestion par URL doit passer par `is_allowed_source` avant tout fetch.
"""

from urllib.parse import urlparse

# Domaines publics ou explicitement conventionnés — statut d'accès "public"/"partenaire"
# (analyse_strategique_evolution_v2.md §3). Toute extension de cette liste doit documenter le
# statut d'accès de la nouvelle source, pas seulement son nom.
#
# GUCE CI est volontairement ABSENT de cette liste, malgré sa mention en §4.1 de la spec comme
# source prévue : vérifié par requête réelle le 2026-08-11 que le vrai domaine est guce.gouv.ci
# (pas "guce.ci") et qu'il redirige immédiatement vers une authentification SSO (JOSSO), y compris
# pour /robots.txt à la racine — pas une source librement scrapable sans accès partenaire. À
# rajouter seulement si un accès public spécifique (hors SSO) est identifié, ou qu'un accès
# partenaire est négocié — jamais par supposition.
ALLOWED_SOURCE_DOMAINS = frozenset(
    {
        "douanes.ci",  # Douanes CI — vérifié public, structure réelle capturée (voir fixtures/)
        "commerce.gouv.ci",  # Vérifié public le 2026-08-11 (robots.txt ouvert), structure réelle
                              # capturée (extraction/commerce_gouv_ci_extractor.py, fixtures/)
        "bceao.int",  # BCEAO — taux de change, référentiels normatifs (source normative, pas table)
    }
)

# Systèmes explicitement fermés — jamais un accès supposé, même si un domaine ressemble à une
# source publique. Liste positive ci-dessus fait déjà foi ; celle-ci documente les cas
# explicitement écartés dans la spec pour qu'un futur contributeur ne les ajoute pas par erreur.
EXPLICITLY_CLOSED_SYSTEMS = frozenset({"sydam.ci", "sydamworld.ci"})


def is_allowed_source(url: str) -> bool:
    """True si le domaine de `url` fait partie de la liste blanche des sources publiques/
    conventionnées. Ne fait aucune requête réseau — vérification structurelle uniquement."""
    domain = urlparse(url).netloc.lower()
    # Tolère un sous-domaine (ex. "www.guce.ci", "portail.douanes.ci")
    return any(domain == d or domain.endswith("." + d) for d in ALLOWED_SOURCE_DOMAINS)
