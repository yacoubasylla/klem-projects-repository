from datetime import date
from unittest.mock import MagicMock

from klem_ref_bot import db
from klem_ref_bot.models import (
    ExtractedDocumentRequis,
    ExtractedOperationCommerce,
    ExtractedProcedureMetier,
    ExtractedTexteReglementaire,
)


def _mock_connection():
    connection = MagicMock()
    cursor = MagicMock()
    connection.cursor.return_value.__enter__.return_value = cursor
    return connection, cursor


def test_insert_texte_reglementaire_forces_statut_proposee_and_returns_generated_id():
    connection, cursor = _mock_connection()
    proposal = ExtractedTexteReglementaire(
        titre="Circulaire", type="circulaire", date_publication=date(2026, 1, 15),
        reference="2026-001", domaine="import", url_source="https://douanes.ci/x",
    )

    entry_id = db.insert_texte_reglementaire(connection, proposal)

    assert entry_id  # UUID généré, non vide
    sql, params = cursor.execute.call_args[0]
    assert "PROPOSEE" in sql
    assert entry_id in params
    assert "circulaire" in params
    assert db.CREATED_BY in params


def test_insert_procedure_metier_forces_statut_proposee():
    connection, cursor = _mock_connection()
    proposal = ExtractedProcedureMetier(nom="Import véhicules", code="IMP-VEH",
                                         description=None, acteurs=None)

    db.insert_procedure_metier(connection, proposal)

    sql, params = cursor.execute.call_args[0]
    assert "PROPOSEE" in sql
    assert "IMP-VEH" in params


def test_insert_document_requis_forces_statut_proposee():
    connection, cursor = _mock_connection()
    proposal = ExtractedDocumentRequis(nom="Certificat d'origine", code="CERT-ORIG",
                                        description=None, regle_validation=None)

    db.insert_document_requis(connection, proposal)

    sql, params = cursor.execute.call_args[0]
    assert "PROPOSEE" in sql
    assert "CERT-ORIG" in params


def test_insert_operation_commerce_forces_statut_proposee():
    connection, cursor = _mock_connection()
    proposal = ExtractedOperationCommerce(nom="Import véhicules", code="IMP-VEH",
                                           type="IMPORT", procedure_id="a-procedure-id")

    db.insert_operation_commerce(connection, proposal)

    sql, params = cursor.execute.call_args[0]
    assert "PROPOSEE" in sql
    assert "a-procedure-id" in params


def test_connect_commits_on_success_and_closes_connection():
    connection = MagicMock()
    config = MagicMock(dsn="postgresql://user:pass@host/db")

    import klem_ref_bot.db as db_module

    original_connect = db_module.psycopg2.connect
    db_module.psycopg2.connect = MagicMock(return_value=connection)
    try:
        with db.connect(config):
            pass
    finally:
        db_module.psycopg2.connect = original_connect

    connection.commit.assert_called_once()
    connection.close.assert_called_once()
    connection.rollback.assert_not_called()


def test_connect_rolls_back_and_closes_on_exception():
    connection = MagicMock()
    config = MagicMock(dsn="postgresql://user:pass@host/db")

    import klem_ref_bot.db as db_module

    original_connect = db_module.psycopg2.connect
    db_module.psycopg2.connect = MagicMock(return_value=connection)
    try:
        try:
            with db.connect(config):
                raise ValueError("boom")
        except ValueError:
            pass
    finally:
        db_module.psycopg2.connect = original_connect

    connection.rollback.assert_called_once()
    connection.close.assert_called_once()
    connection.commit.assert_not_called()
