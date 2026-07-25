# React + Vite

Ce gabarit fournit une configuration minimale pour faire fonctionner React avec Vite, le HMR et
quelques règles ESLint.

Deux plugins officiels sont actuellement disponibles :

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) utilise [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) utilise [SWC](https://swc.rs/)

## React Compiler

Le React Compiler n'est pas activé dans ce gabarit en raison de son impact sur les performances de
dev et de build. Pour l'ajouter, voir [cette documentation](https://react.dev/learn/react-compiler/installation).

## Étendre la configuration ESLint

Si vous développez une application de production, nous recommandons d'utiliser TypeScript avec des
règles de lint conscientes des types activées. Consultez le [gabarit TS](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) pour savoir comment
intégrer TypeScript et [`typescript-eslint`](https://typescript-eslint.io) dans votre projet.
