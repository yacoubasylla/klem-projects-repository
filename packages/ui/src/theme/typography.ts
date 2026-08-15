/**
 * Police des titres du site vitrine KLEM (site-klem, thème WordPress `klem-theme`, classe
 * utilitaire `font-heading`) — à utiliser dans le `typography` de tout thème MUI `createTheme()`
 * d'une application KLEM pour aligner ses titres (h1-h6) sur l'identité visuelle du site vitrine.
 * L'application consommatrice doit charger la police elle-même (Questrial n'existe qu'en
 * graisse 400) : `pnpm --filter <app> add @fontsource/questrial`, puis
 * `import '@fontsource/questrial/400.css'` dans son point d'entrée (voir
 * `apps/web-app/cantine-connect/client-frontend/src/main.jsx`).
 */
export const klemHeadingFontFamily =
  '"Questrial", "Inter", "Roboto", "Helvetica", "Arial", sans-serif';

/** Overrides `typography` prêts à l'emploi (ou à spread) pour les variantes h1-h6 de MUI. */
export const klemHeadingTypography = {
  h1: { fontFamily: klemHeadingFontFamily },
  h2: { fontFamily: klemHeadingFontFamily },
  h3: { fontFamily: klemHeadingFontFamily },
  h4: { fontFamily: klemHeadingFontFamily },
  h5: { fontFamily: klemHeadingFontFamily, fontWeight: 700 },
  h6: { fontFamily: klemHeadingFontFamily, fontWeight: 700 },
} as const;
