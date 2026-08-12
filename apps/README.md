# applications (`apps/`)

Ce dossier contient les applications finales et autonomes. Chaque projet ici est un "livrable" indépendant destiné à être conteneurisé et déployé.

## 📦 Contenu du dossier
- **`web-app/`** : plusieurs applications web client distinctes (React/MUI), une par produit —
  `cantine-connect` (pilote, MVP livré), `parcauto`, `clinic`, `pharmacie` (ces deux derniers
  actuellement des dossiers vides, non démarrés).
- **`mobile-app/`** : applications mobile/PWA Expo/React Native, « Universal App First »
  (`SYSTEM_INSTRUCTIONS.md`) — `cantine-connect` (scaffold Expo Router/NativeWind livré).
- **`showcase-website/`** : sites vitrine institutionnels — `site-klem` (WordPress, réel) et
  `site-veone` (dossier vide, non démarré).
- **`backend-api/`** : **non démarré** — ne contient qu'un README décrivant l'architecture cible
  (voir `backend-api/README.md`), aucun code Spring Boot réel pour l'instant.

## ⚠️ Règle d'or des Applications
Une application peut importer des modules du dossier `../packages/` (ex: `ui` ou `utils`), mais **une application ne doit jamais importer directement du code en provenance d'une autre application** (pas de liens croisés entre `web-app` et `backend-api`).