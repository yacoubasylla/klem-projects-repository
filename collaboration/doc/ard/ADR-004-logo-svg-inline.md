# ADR-004 — Logo KLEM : SVG inline avec motif 4 losanges

**Date :** 2026-06-24  
**Statut :** ~~Accepté~~ **Remplacé par [ADR-005](ADR-005-logo-chevronmark-archivo.md)** (2026-06-26)  
**Décideur :** Équipe KLEM / Claude Code

---

## Contexte

Le logo KLEM doit être affiché dans le header fixe et dans le footer. Il doit rester net à toutes les résolutions (Retina, 4K) et respecter la charte graphique : `#FF6500` (orange) et `#0B192C` (bleu profond).

## Décision

Utiliser un **SVG inline** composé de 4 rectangles arrondis pivotés à 45° (losanges), combiné à un bloc typographique `KLEM / Technologies & Services` en Space Grotesk.

```svg
<svg viewBox="0 0 48 48" width="44" height="44">
  <rect ... fill="#FF6500" transform="rotate(45 11 10)"/>   <!-- haut-gauche : orange -->
  <rect ... fill="#0B192C" transform="rotate(45 37 10)"/>   <!-- haut-droit  : bleu -->
  <rect ... fill="#0B192C" fill-opacity="0.55" .../>        <!-- bas-gauche  : bleu atténué -->
  <rect ... fill="#FF6500" fill-opacity="0.35" .../>        <!-- bas-droit   : orange atténué -->
</svg>
```

Le SVG est dupliqué avec des opacités différentes pour la variante fond sombre (footer).

## Alternatives considérées

| Option | Raison du rejet |
|---|---|
| Fichier PNG/WebP | Flou sur écrans Retina, nécessite plusieurs tailles (1x, 2x, 3x) |
| Fichier SVG externe (`<img src="logo.svg">`) | Ne peut pas être stylé dynamiquement via CSS (couleur hover) |
| Font icon | Ne permet pas la composition multi-couleur du motif 4 losanges |

## Conséquences

- ✅ Rendu parfait à toute résolution sans octets supplémentaires
- ✅ Hover `group-hover:text-klem-orange` Tailwind fonctionne sur le lettrage grâce à `currentColor`
- ✅ Accessibilité : `aria-hidden="true"` sur le SVG, le texte alternatif est porté par l'`aria-label` du lien parent
- ⚠️ Le SVG est dupliqué (header + footer) avec des variantes de couleur → à extraire dans un partial `get_template_part('template-parts/ui/logo')` si le logo évolue
