# Modules Partagés (`packages/`)

Ce dossier centralise le code, les types et les configurations transversaux à tout le monorepo. Il permet d'éviter la duplication de code et d'assurer une cohérence stricte entre nos services.

## 📁 Structure des Packages
- **`ui/`** : Bibliothèque interne de composants graphiques Material UI personnalisés aux couleurs de Klem Technologies (Boutons types, cartes de statistiques, formulaires génériques).
- **`utils/`** : Fonctions utilitaires partagées, formateurs de devises (FCFA), calculateurs de dates et types TypeScript communs.
- **`config/`** : Configurations centralisées et partagées pour les outils de qualité de code (ESLint, Prettier, TypeScript base configs).

## 🛠️ Utilisation
Pour utiliser un package partagé dans une application (ex: importer un composant de `packages/ui` dans `apps/web-app`), ajoutez la dépendance locale dans le `package.json` de l'application cible :
```json
"dependencies": {
  "@fleetcontrol/ui": "workspace:*"
}