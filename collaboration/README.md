# Espace de Collaboration IA - Gouvernance

Ce répertoire est le système nerveux central de notre binôme de développeurs avec Claude. Il sert à maintenir une qualité "Premium/Expert" et à éviter la dérive de l'architecture.

## 📂 Rôle des Sous-Dossiers
- **`prompts/`** : Contient les commandes d'automatisation.
  - `/startup.md` : À exécuter en début de session pour synchroniser l'IA.
  - `/update.md` : À exécuter en fin de session pour forcer l'IA à documenter son travail.
- **`history/`** : Contient les traces tangibles du développement.
  - `history-log.md` : Le journal de bord chronologique des fonctionnalités livrées.
  - `decision-log.md` : Le registre des choix d'architecture (ADR). Empêche de revenir sur une décision technique validée sans justification.

## 🤖 Protocole d'Utilisation Obligatoire
Chaque développeur ouvrant un terminal doit exécuter le script de démarrage pour charger les fichiers de contexte présents dans ce dossier. La documentation des logs techniques en fin de tâche fait partie intégrante de notre définition du travail terminé (*Definition of Done*).