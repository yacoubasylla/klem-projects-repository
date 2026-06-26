# ADR-005 — Logo KLEM : ChevronMark 3D + Archivo

**Date :** 2026-06-26  
**Statut :** Accepté  
**Décideur :** Équipe KLEM / Claude Code  
**Remplace :** ADR-004 (motif 4 losanges + Space Grotesk)

---

## Contexte

Le client a fourni la charte graphique officielle KLEM via un projet Claude Design :  
`https://claude.ai/design/p/a2cd3486` — fichier `KLEM Logo - Chevron.dc.html`

Le logo précédent (4 losanges → 2 chevrons plats → sphère neurale 3D) ne correspondait pas au logo officiel validé par le client. Les codes couleur officiels sont **BLEU `#13294B`** et **ROUGE `#E42313`**.

---

## Décision

### Symbole — ChevronMark 3D

Double chevron vectoriel avec effet de profondeur 3 couches, viewBox `0 0 54 44` :

```svg
<!-- Couche 1 : ombre / extrusion (décalée de 1.4, 1.7) -->
<g fill="#A5130A">
  <path d="M6 0 L18 0 L32 20 L13 40 L1 40 L17 20 Z" transform="translate(1.4,1.7)"/>
  <path d="M23 0 L35 0 L49 20 L30 40 L18 40 L34 20 Z" transform="translate(1.4,1.7)"/>
</g>
<!-- Couche 2 : face principale -->
<g fill="#E42313">
  <path d="M6 0 L18 0 L32 20 L13 40 L1 40 L17 20 Z"/>
  <path d="M23 0 L35 0 L49 20 L30 40 L18 40 L34 20 Z"/>
</g>
<!-- Couche 3 : bevel supérieur (reflet de bord) -->
<g fill="#F0654F">
  <path d="M6 0 L18 0 L19.5 2.2 L7.5 2.2 Z"/>
  <path d="M23 0 L35 0 L36.5 2.2 L24.5 2.2 Z"/>
</g>
```

### Typographie — Archivo (Google Fonts)

| Élément | Propriétés |
|---|---|
| Wordmark "KLEM" | Archivo 800, `letter-spacing: -0.02em`, `line-height: 0.9` |
| Tagline | Archivo 600, `uppercase`, `letter-spacing: 0.23em` |
| Couleur clair | `#13294B` (wordmark + tagline) |
| Couleur sombre | `#FFFFFF` (KLEM) / `#c3c9d6` (tagline) |

Import via `wp_enqueue_style` dans `functions.php` :
```php
wp_enqueue_style('klem-fonts',
  'https://fonts.googleapis.com/css2?family=Archivo:wght@600;700;800&display=swap',
  [], null);
```

### Palette officielle

| Token Tailwind | Valeur | Usage |
|---|---|---|
| `klem-blue` | `#13294B` | Textes, fonds sombres, wordmark |
| `klem-red` | `#E42313` | Symbole, accents, CTAs |
| `klem-orange` | `#E42313` | Alias rouge (aucun orange en charte) |
| `klem-slate` | `#6B7280` | Textes secondaires |

### Tailles d'affichage

| Contexte | Symbole | "KLEM" |
|---|---|---|
| Header | `44×36 px` | `38px` |
| Footer | `34×28 px` | `28px` |
| Favicon / small | `24×20 px` | `21px` |

---

## Alternatives considérées

| Option | Raison du rejet |
|---|---|
| Sphère neurale 3D (itération précédente) | Bonne qualité visuelle mais ne correspond pas au logo officiel validé |
| 2 chevrons plats `#E2241B` (Session 05) | Lecture du premier design system — sans l'effet 3D final |
| Image raster (PNG) | Flou Retina, non stylab le |
| Icône font | Mono-couleur, pas de 3D possible |

---

## Conséquences

- ✅ Logo conforme à la charte officielle importée depuis Claude Design
- ✅ Effet 3D natif SVG sans dépendance externe (pas de bibliothèque)
- ✅ Deux variantes (clair / sombre) couvertes par les mêmes paths SVG avec couleurs différentes
- ✅ `overflow:visible` sur le SVG : l'extrusion dépasse légèrement le viewBox sans clip
- ⚠️ Archivo nécessite une requête Google Fonts (un seul appel, poids ~600 B WOFF2 subset latin)
- ⚠️ IDs SVG (`defs`, `filter`) non utilisés dans le ChevronMark final — pas de collision DOM

---

## Fichiers impactés

| Fichier | Modification |
|---|---|
| `header.php` | SVG ChevronMark inline + classes Archivo Tailwind |
| `footer.php` | Idem, variante fond sombre |
| `tailwind.config.js` | `font-logo → Archivo`, couleurs officielles |
| `functions.php` | `klem_enqueue_fonts()` — Google Fonts Archivo |
| `assets/svg/klem-primary.svg` | Logo horizontal complet (ChevronMark + Archivo) |
| `assets/svg/klem-mono-ink.svg` | Variante encre `#13294B` |
| `assets/svg/klem-mono-white.svg` | Variante blanche |
| `assets/svg/klem-symbole-rouge.svg` | Symbole seul (3 couches) |
