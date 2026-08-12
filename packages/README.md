# Modules Partagés (`packages/`)

Ce dossier centralise le code, les types et les configurations transversaux à tout le monorepo. Il permet d'éviter la duplication de code et d'assurer une cohérence stricte entre nos services.

## 📁 Structure des Packages
- **`ui/`** (`@klem/ui`) : composants graphiques partagés — un seul composant réel à ce stade
  (`KlemButton`, exporté via `src/index.ts`), le reste du design system reste à construire.
- **`utils/`** (`@klem/utils`) : fonctions utilitaires partagées (structure prête, `src/index.ts`).
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