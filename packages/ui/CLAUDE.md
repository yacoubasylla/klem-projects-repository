# KLEM UI Package Context

Ton rôle est de maintenir le Design System de KLEM.
- **Priorité :** Maintenabilité et pureté des composants.
- **Règle d'or :** Aucun composant ici ne doit contenir de logique métier (ex: appels API). C'est uniquement pour la présentation.
- **Export :** Tout nouveau composant doit impérativement être exporté dans `src/index.ts`.
- **Styling :** Utilise les standards CSS/JS définis dans le monorepo.
- **Thème (`src/theme/`) :** tokens de design partagés (ex. `klemHeadingFontFamily`,
  `klemHeadingTypography` — police Questrial des titres, alignée sur `font-heading` du thème
  WordPress `klem-theme` de site-klem). Ce package ne charge jamais la police lui-même
  (pas de dépendance `@fontsource/*` ici) : c'est à l'application consommatrice d'installer
  `@fontsource/questrial` et d'en importer le CSS dans son point d'entrée.