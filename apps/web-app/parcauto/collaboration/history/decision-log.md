# Registre des Décisions Architecturales (ADR)

## ADR #001 : Choix du Modèle de Données Centralisé PostgreSQL
- **Date** : 2026-06-20
- **Contexte** : L'application doit gérer des modules interconnectés (Location, Course, Livraison) qui s'appuient tous sur les mêmes véhicules physiques.
- **Décision** : Utilisation d'une table principale `vehicules` hautement indexée avec des tables de jointures spécifiques pour chaque type d'activité (ex: `contrats_location`, `courses_vtc`). Nous refusons la séparation en micro-bases pour cette version afin d'assurer l'intégrité référentielle native par clés étrangères.
- **Conséquences** : Facilité d'édition des rapports de revenus consolidés, mais nécessite une discipline stricte sur l'isolation des transactions au niveau des services Spring Boot.

## ADR #002 : Stratégie de Saisie des États des Lieux (Mobile-First UI)
- **Date** : 2026-06-20
- **Contexte** : Les chauffeurs feront les états des départs/retours depuis des smartphones sur le terrain.
- **Décision** : Le module frontend `web-app` intègrera des vues spécifiquement optimisées pour le format mobile en utilisant le système de grille responsive de Material UI (MUI), évitant le développement d'une application mobile native distincte dans la phase MVP.
- **Conséquences** : Gain de temps de développement de 40% sur la phase initiale.