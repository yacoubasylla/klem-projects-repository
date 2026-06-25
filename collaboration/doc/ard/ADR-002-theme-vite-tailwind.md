# ADR-002 — Pipeline d'assets : Vite 5 + Tailwind CSS v3

**Date :** 2026-06-24  
**Statut :** Accepté  
**Décideur :** Équipe KLEM / Claude Code

---

## Contexte

Le thème `klem-theme` doit produire des assets CSS/JS optimisés pour la production. WordPress impose un système d'enqueue (`wp_enqueue_scripts`) qui doit pointer vers des fichiers compilés avec hash de cache-busting.

## Décision

Utiliser **Vite 5** comme bundler avec le plugin `@vitejs/plugin-legacy` désactivé (cible ES2020+) et **Tailwind CSS v3** comme framework utilitaire. Le `manifest.json` généré par Vite est lu dynamiquement dans `functions.php` pour enqueuer les bons fichiers hashés.

```
src/main.js   → dist/assets/main-[hash].js
src/main.css  → dist/assets/main-[hash].css
dist/.vite/manifest.json  → lu par klem_enqueue_assets()
```

La couleur de marque est déclarée une seule fois dans `tailwind.config.js` :
```js
klem-blue:   '#0B192C'
klem-orange: '#FF6500'
```

## Alternatives considérées

| Option | Raison du rejet |
|---|---|
| Webpack / Mix (Laravel Mix) | Moins performant, configuration verbose ; Vite offre HMR natif et build 10× plus rapide |
| CSS vanille + Gulp | Pas de purge automatique des classes inutilisées → bundle trop lourd |
| Tailwind v4 (alpha) | Instable en production au moment de la décision |

## Conséquences

- ✅ Build production < 30 kB CSS gzippé grâce à PurgeCSS intégré à Tailwind
- ✅ Mode dev avec HMR : `pnpm dev` → Vite sur `localhost:5173`, WordPress sur `:8080`
- ✅ Aucun CSS inline dans les templates PHP ; uniquement des classes utilitaires Tailwind
- ⚠️ Les classes dynamiques (ex: construites par concaténation PHP) doivent être listées dans `safelist` de `tailwind.config.js` pour ne pas être purgées
