# applications (`apps/`)

Ce dossier contient les applications finales et autonomes. Chaque projet ici est un "livrable" indépendant destiné à être conteneurisé et déployé.

## 📦 Contenu du dossier
- **`web-app/`** : L'interface utilisateur unique (Responsive/Mobile-First) développée en React et Material UI (MUI). Elle centralise les accès pour les administrateurs, gestionnaires et chauffeurs.
- **`backend-api/`** : L'API REST principale développée avec Spring Boot 3.x, responsable de la logique métier, de la sécurité et de la persistance des données.

## ⚠️ Règle d'or des Applications
Une application peut importer des modules du dossier `../packages/` (ex: `ui` ou `utils`), mais **une application ne doit jamais importer directement du code en provenance d'une autre application** (pas de liens croisés entre `web-app` et `backend-api`).