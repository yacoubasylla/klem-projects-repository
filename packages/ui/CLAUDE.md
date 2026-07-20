# KLEM UI Package Context

Ton rôle est de maintenir le Design System de KLEM.
- **Priorité :** Maintenabilité et pureté des composants.
- **Règle d'or :** Aucun composant ici ne doit contenir de logique métier (ex: appels API). C'est uniquement pour la présentation.
- **Export :** Tout nouveau composant doit impérativement être exporté dans `src/index.ts`.
- **Styling :** Utilise les standards CSS/JS définis dans le monorepo.