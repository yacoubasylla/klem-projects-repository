"""Déduplication locale pour l'ingestion — évite de reproposer les mêmes documents à chaque
exécution planifiée. Choix retenu (vs contrainte UNIQUE en DB ou tri manuel par les éditeurs, voir
README §"Statut réel de ce Sprint") : le compte `klem_ref_bot_ingestion` n'a délibérément aucun
droit SELECT (V5, testé réellement), donc la déduplication ne peut pas se faire côté DB avec ce
compte tel quel — un fichier de checkpoint local, écrit par ce process, est la seule option qui
respecte ce modèle de sécurité sans l'affaiblir.

Deux mécanismes, un par source, choisis selon ce que chaque source garantit réellement :

1. `load`/`save`/`filter_new`/`update` (douanes.ci) — retient, par type de document ("circulaire",
   "note", ...), le plus grand `numero` déjà proposé. Les numéros de cette source croissent de
   façon monotone par type (confirmé par capture réelle : circulaires 2413 > 2412 > 2411...), donc
   "numero <= dernier vu" est un filtre fiable POUR CETTE SOURCE PRÉCISE. En cas de doute (numero
   non numérique, type jamais vu), le filtre laisse passer plutôt que de bloquer silencieusement —
   un doublon occasionnel proposé deux fois est un moindre mal (l'éditeur le rejette) comparé à un
   document qui ne serait jamais proposé à cause d'une comparaison ratée.
2. `load_seen_urls`/`save_seen_urls`/`filter_new_by_url`/`update_seen_urls` (commerce.gouv.ci) —
   retient l'ensemble des `url_source` (liens PDF) déjà proposés. Cette source n'a PAS de numero
   monotone exploitable (références composites type "2022-601", pas des entiers strictement
   croissants par type — confirmé par capture réelle, voir
   extraction/commerce_gouv_ci_extractor.py) : réutiliser le mécanisme (1) échouerait à dédupliquer
   silencieusement. `url_source` est en revanche un identifiant stable et unique par document sur
   cette source (une URL PDF par document, jamais réutilisée) — fichier de checkpoint séparé,
   n'affecte pas (1).

Pas un mécanisme générique applicable à n'importe quelle future source sans vérification : le choix
entre les deux dépend de ce que la source garantit réellement sur ses identifiants, à vérifier au
cas par cas plutôt que supposé.
"""

import json
import os
from typing import Dict, List, Optional, Set

from klem_ref_bot.models import ExtractedTexteReglementaire

DEFAULT_CHECKPOINT_PATH = os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))), ".state", "douanes_ci_checkpoint.json"
)
DEFAULT_COMMERCE_CHECKPOINT_PATH = os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))), ".state", "commerce_gouv_ci_checkpoint.json"
)


def checkpoint_path() -> str:
    return os.environ.get("KLEM_REF_BOT_CHECKPOINT_PATH", DEFAULT_CHECKPOINT_PATH)


def load(path: str = None) -> Dict[str, int]:
    path = path or checkpoint_path()
    if not os.path.exists(path):
        return {}
    with open(path, "r", encoding="utf-8") as handle:
        data = json.load(handle)
    return {key: int(value) for key, value in data.items()}


def save(state: Dict[str, int], path: str = None) -> None:
    path = path or checkpoint_path()
    directory = os.path.dirname(path)
    if directory:
        os.makedirs(directory, exist_ok=True)
    with open(path, "w", encoding="utf-8") as handle:
        json.dump(state, handle, indent=2, sort_keys=True)


def filter_new(
    proposals: List[ExtractedTexteReglementaire], state: Dict[str, int]
) -> List[ExtractedTexteReglementaire]:
    kept = []
    for proposal in proposals:
        last_seen = state.get(proposal.type)
        if last_seen is None:
            kept.append(proposal)
            continue
        numero = _as_int(proposal.reference)
        if numero is None or numero > last_seen:
            kept.append(proposal)
    return kept


def update(state: Dict[str, int], proposals: List[ExtractedTexteReglementaire]) -> Dict[str, int]:
    """Nouvel état = `state` fusionné avec le numero max par type parmi `proposals` — à appeler
    uniquement avec les propositions REELLEMENT insérées avec succès, jamais avant."""
    new_state = dict(state)
    for proposal in proposals:
        numero = _as_int(proposal.reference)
        if numero is None:
            continue
        new_state[proposal.type] = max(numero, new_state.get(proposal.type, 0))
    return new_state


def _as_int(value) -> Optional[int]:
    try:
        return int(value)
    except (TypeError, ValueError):
        return None


def commerce_checkpoint_path() -> str:
    return os.environ.get("KLEM_REF_BOT_COMMERCE_CHECKPOINT_PATH", DEFAULT_COMMERCE_CHECKPOINT_PATH)


def load_seen_urls(path: str = None) -> Set[str]:
    """Déduplication par URL de document — commerce.gouv.ci n'a pas de `numero` monotone exploitable
    comme douanes.ci (références composites type "2022-601", pas des entiers strictement croissants
    par type, cf. `filter_new`/`update` ci-dessus) : `url_source` (le lien PDF) est en revanche un
    identifiant stable et unique par document sur cette source, vérifié réellement sur la page
    capturée (une URL par PDF, jamais réutilisée entre deux documents distincts)."""
    path = path or commerce_checkpoint_path()
    if not os.path.exists(path):
        return set()
    with open(path, "r", encoding="utf-8") as handle:
        return set(json.load(handle))


def save_seen_urls(urls: Set[str], path: str = None) -> None:
    path = path or commerce_checkpoint_path()
    directory = os.path.dirname(path)
    if directory:
        os.makedirs(directory, exist_ok=True)
    with open(path, "w", encoding="utf-8") as handle:
        json.dump(sorted(urls), handle, indent=2)


def filter_new_by_url(
    proposals: List[ExtractedTexteReglementaire], seen_urls: Set[str]
) -> List[ExtractedTexteReglementaire]:
    return [proposal for proposal in proposals if proposal.url_source not in seen_urls]


def update_seen_urls(
    seen_urls: Set[str], proposals: List[ExtractedTexteReglementaire]
) -> Set[str]:
    """Nouvel état = `seen_urls` fusionné avec les URLs des `proposals` — à appeler uniquement avec
    les propositions REELLEMENT insérées avec succès, jamais avant (même contrat que `update`)."""
    new_seen = set(seen_urls)
    for proposal in proposals:
        if proposal.url_source:
            new_seen.add(proposal.url_source)
    return new_seen
