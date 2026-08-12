# Modules Partagés (`packages/`)

Ce dossier centralise le code, les types et les configurations transversaux à tout le monorepo. Il permet d'éviter la duplication de code et d'assurer une cohérence stricte entre nos services.

## 📁 Structure des Packages
- **`ui/`** (`@klem/ui`) : composants graphiques partagés (`KlemButton`, `ErrorBoundary`,
  `SuccessSnackbar`, exportés via `src/index.ts`), le reste du design system reste à construire.
- **`utils/`** (`@klem/utils`) : fonctions utilitaires partagées (structure prête, `src/index.ts`).
- **`license/`** (`@klem/license`) : vérification de clé de licence KLEM (`KTS_LICENSE_KEY`),
  signature ECDSA P-256 via Web Crypto, exposé via `<KlemProvider licenseKey appId>` +
  `useKlemLicense()`. **Mode avertissement seul** : ne bloque jamais le rendu, journalise et
  affiche une bannière non bloquante en développement uniquement — voir `license/CLAUDE.md`.
- **`config/`** : **vide aujourd'hui** — prévu pour la configuration ESLint/Prettier/TSConfig
  partagée, mais pas encore peuplé. Conséquence directe : `ui/` et `utils/` n'ont aucun script
  `lint`/`build` dans leur `package.json` tant que cette config commune n'existe pas.
- **`data-utils/`** : **vide aujourd'hui**, glob de workspace réservé, pas encore de contenu.

## 🛠️ Utilisation
Pour utiliser un package partagé dans une application (ex: importer un composant de `packages/ui` dans `apps/web-app`), ajoutez la dépendance locale dans le `package.json` de l'application cible :
```json
"dependencies": {
  "@klem/ui": "workspace:*"
}