"""Déduplication locale pour l'ingestion douanes.ci — évite de reproposer les mêmes documents à
chaque exécution planifiée. Choix retenu (vs contrainte UNIQUE en DB ou tri manuel par les
éditeurs, voir README §"Statut réel de ce Sprint") : le compte `klem_ref_bot_ingestion` n'a
délibérément aucun droit SELECT (V5, testé réellement), donc la déduplication ne peut pas se faire
côté DB avec ce compte tel quel — un fichier de checkpoint local, écrit par ce process, est la
seule option qui respecte ce modèle de sécurité sans l'affaiblir.

Le fichier retient, par type de document ("circulaire", "note", ...), le plus grand `numero` déjà
proposé — les numéros de la source croissent de façon monotone par type (confirmé par capture
réelle : circulaires 2413 > 2412 > 2411...), donc "numero <= dernier vu" est un filtre fiable POUR
CETTE SOURCE PRÉCISE. Pas un mécanisme générique de déduplication.

En cas de doute (numero non numérique, type jamais vu), le filtre laisse passer plutôt que de
bloquer silencieusement — un doublon occasionnel proposé deux fois est un moindre mal (l'éditeur le
rejette) comparé à un document qui ne serait jamais proposé à cause d'une comparaison ratée.
"""

import json
import os
from typing import Dict, List, Optional

from klem_ref_bot.models import ExtractedTexteReglementaire

DEFAULT_CHECKPOINT_PATH = os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))), ".state", "douanes_ci_checkpoint.json"
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
