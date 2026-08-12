# Espace de Collaboration IA - Gouvernance

Ce répertoire est le système nerveux central de notre binôme de développeurs avec Claude. Il sert à maintenir une qualité "Premium/Expert" et à éviter la dérive de l'architecture.

## 📂 Rôle des Sous-Dossiers
- **`context/`** : `CONTEXT.md` — vision produit, alignement métier, règles globales KLEM.
- **`doc/`** : documentation technique — `architecture.md`, `specifications.md`, `workflows.md`.
- **`history/`** : Contient les traces tangibles du développement.
  - `history-log.md` : Le journal de bord chronologique des fonctionnalités livrées.
  - `decision-log.md` : Le registre des arbitrages stratégiques.
  - `adr/` : Architecture Decision Records au format standardisé (un fichier daté par décision).

Les commandes d'automatisation (`/startup`, `/update`) vivent à la racine du dépôt dans
`commands/`, pas dans `collaboration/` — ce dossier ne contient aucun sous-dossier `prompts/`.

## 🤖 Protocole d'Utilisation Obligatoire
Chaque développeur ouvrant un terminal doit exécuter le script de démarrage pour charger les fichiers de contexte présents dans ce dossier. La documentation des logs techniques en fin de tâche fait partie intégrante de notre définition du travail terminé (*Definition of Done*).