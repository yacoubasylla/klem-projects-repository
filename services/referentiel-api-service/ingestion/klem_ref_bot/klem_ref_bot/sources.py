"""Registre des sources institutionnelles autorisées — specifications_techniques.md §4.1 :
`klem_ref_bot` ne traite que des sources publiques ou explicitement conventionnées, jamais un accès
supposé à un système fermé (ex: SYDAM World). Cette liste blanche est le seul garde-fou technique de
ce Sprint — aucun scraping réel n'est encore implémenté (voir README), mais toute future
implémentation d'ingestion par URL doit passer par `is_allowed_source` avant tout fetch.
"""

from urllib.parse import urlparse

# Domaines publics ou explicitement conventionnés — statut d'accès "public"/"partenaire"
# (analyse_strategique_evolution_v2.md §3). Toute extension de cette liste doit documenter le
# statut d'accès de la nouvelle source, pas seulement son nom.
ALLOWED_SOURCE_DOMAINS = frozenset(
    {
        "guce.ci",  # GUCE CI — guichet unique du commerce extérieur
        "douanes.ci",  # Douanes CI — guides et procédures officielles
        "commerce.gouv.ci",  # Portail économie & commerce extérieur ivoirien
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
