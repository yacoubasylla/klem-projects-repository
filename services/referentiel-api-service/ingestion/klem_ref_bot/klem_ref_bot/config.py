"""Configuration lue exclusivement depuis les variables d'environnement — jamais de secret en dur,
même convention que les services Java de ce dépôt (`application.yml` : ${DB_URL}/${DB_PASSWORD}).

Le compte de connexion attendu est le compte technique à droits restreints décrit dans la migration
`V5__enforce_proposee_on_insert.sql` (INSERT uniquement sur les 4 tables du référentiel) — jamais le
compte applicatif `referentiel_api` utilisé par le service Spring Boot.
"""

import os
from dataclasses import dataclass


class MissingConfigError(RuntimeError):
    pass


@dataclass(frozen=True)
class DatabaseConfig:
    dsn: str


def load_database_config() -> DatabaseConfig:
    dsn = os.environ.get("KLEM_REF_BOT_DATABASE_URL")
    if not dsn:
        raise MissingConfigError(
            "KLEM_REF_BOT_DATABASE_URL manquant — attendu au format "
            "postgresql://klem_ref_bot_ingestion:<mot_de_passe>@<hote>:<port>/<base>"
        )
    return DatabaseConfig(dsn=dsn)
