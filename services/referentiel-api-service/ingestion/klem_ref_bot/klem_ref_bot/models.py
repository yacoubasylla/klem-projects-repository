"""Modèles de proposition extraits par l'agent — miroir minimal des entités JPA de
`referentiel-api-service` (com.klem.referentielapi.textereglementaire.domain.model.TexteReglementaire
et consorts), jamais les entités elles-mêmes : klem_ref_bot n'a aucune dépendance vers le code Java,
il écrit directement en base (specifications_techniques.md §4.2, §7.1 diagramme de composants).

Seul `ExtractedTexteReglementaire` est produit par une extraction réelle dans ce Sprint — voir
extraction/README pour la justification (le champ "titre, type, date, domaine" de la spec §4.2
décrit précisément ce type, pas les trois autres). Les modèles pour procedure_metier/
document_requis/operation_commerce existent ici pour que `db.py` expose une capacité d'insertion
complète dès maintenant, même si aucun extracteur ne les produit encore.
"""

from dataclasses import dataclass
from datetime import date
from typing import Optional


@dataclass
class ExtractedTexteReglementaire:
    titre: str
    type: str
    date_publication: Optional[date]
    reference: Optional[str]
    domaine: Optional[str]
    url_source: Optional[str]


@dataclass
class ExtractedProcedureMetier:
    nom: str
    code: str
    description: Optional[str]
    acteurs: Optional[str]


@dataclass
class ExtractedDocumentRequis:
    nom: str
    code: str
    description: Optional[str]
    regle_validation: Optional[str]


@dataclass
class ExtractedOperationCommerce:
    nom: str
    code: str
    type: str
    procedure_id: str  # UUID d'une ProcedureMetier déjà existante — pas produit par extraction seule
