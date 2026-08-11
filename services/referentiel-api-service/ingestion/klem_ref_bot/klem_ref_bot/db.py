"""Écriture directe dans le schéma `klem_trade_x` — jamais via l'API REST de
`referentiel-api-service` (specifications_techniques.md §7.1 : "insère ses propositions
directement en base [...] sans jamais passer par le service applicatif ni publier directement").

Chaque fonction d'insertion force `statut='PROPOSEE'` et `created_by='klem_ref_bot'` — cohérent
avec le trigger DB `enforce_proposee_on_insert` (V5) qui rejetterait de toute façon toute autre
valeur, mais explicite ici plutôt que de compter uniquement sur le trigger comme filet de sécurité.
"""

import uuid
from contextlib import contextmanager
from datetime import datetime, timezone
from typing import Iterator

import psycopg2

from klem_ref_bot.config import DatabaseConfig
from klem_ref_bot.models import (
    ExtractedDocumentRequis,
    ExtractedOperationCommerce,
    ExtractedProcedureMetier,
    ExtractedTexteReglementaire,
)

CREATED_BY = "klem_ref_bot"


@contextmanager
def connect(config: DatabaseConfig) -> Iterator["psycopg2.extensions.connection"]:
    connection = psycopg2.connect(config.dsn)
    try:
        yield connection
        connection.commit()
    except Exception:
        connection.rollback()
        raise
    finally:
        connection.close()


def insert_texte_reglementaire(connection, proposal: ExtractedTexteReglementaire) -> str:
    entry_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)
    with connection.cursor() as cursor:
        cursor.execute(
            """
            INSERT INTO klem_trade_x.texte_reglementaire
                (id, titre, type, date_publication, reference, domaine, url_source, statut,
                 created_by, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, 'PROPOSEE', %s, %s, %s)
            """,
            (
                entry_id,
                proposal.titre,
                proposal.type,
                proposal.date_publication,
                proposal.reference,
                proposal.domaine,
                proposal.url_source,
                CREATED_BY,
                now,
                now,
            ),
        )
    return entry_id


def insert_procedure_metier(connection, proposal: ExtractedProcedureMetier) -> str:
    entry_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)
    with connection.cursor() as cursor:
        cursor.execute(
            """
            INSERT INTO klem_trade_x.procedure_metier
                (id, nom, code, description, acteurs, statut, created_by, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, 'PROPOSEE', %s, %s, %s)
            """,
            (entry_id, proposal.nom, proposal.code, proposal.description, proposal.acteurs,
             CREATED_BY, now, now),
        )
    return entry_id


def insert_document_requis(connection, proposal: ExtractedDocumentRequis) -> str:
    entry_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)
    with connection.cursor() as cursor:
        cursor.execute(
            """
            INSERT INTO klem_trade_x.document_requis
                (id, nom, code, description, regle_validation, statut, created_by, created_at,
                 updated_at)
            VALUES (%s, %s, %s, %s, %s, 'PROPOSEE', %s, %s, %s)
            """,
            (entry_id, proposal.nom, proposal.code, proposal.description,
             proposal.regle_validation, CREATED_BY, now, now),
        )
    return entry_id


def insert_operation_commerce(connection, proposal: ExtractedOperationCommerce) -> str:
    entry_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)
    with connection.cursor() as cursor:
        cursor.execute(
            """
            INSERT INTO klem_trade_x.operation_commerce
                (id, nom, code, type, procedure_id, statut, created_by, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, 'PROPOSEE', %s, %s, %s)
            """,
            (entry_id, proposal.nom, proposal.code, proposal.type, proposal.procedure_id,
             CREATED_BY, now, now),
        )
    return entry_id
