# Context: Data Engineering
- **Stack :** Apache Spark, Kafka, Python/Scala, Airflow.
- **Standards :**
    - Pipelines : Idempotence obligatoire. Si le job plante, il doit pouvoir être relancé sans dupliquer les données.
    - Validation : Contrôle de schéma (Schema Registry) à chaque étape.
    - Performance : Monitoring des partitions Kafka et des ressources Spark (Executor Memory/Cores).
- **Spécificité :** Data Quality checks sur chaque pipeline (rejet des données corrompues dans une DLQ - Dead Letter Queue).