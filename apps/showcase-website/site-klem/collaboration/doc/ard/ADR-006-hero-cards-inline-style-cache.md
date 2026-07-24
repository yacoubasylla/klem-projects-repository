# ADR-006 — Hero Cards : Inline Style pour Résistance au Cache LiteSpeed

**Date :** 2026-06-26  
**Statut :** Accepté  
**Décideur :** Équipe KLEM / Claude Code  
**Contexte :** Session 09 — debugging hébergement Hostinger

---

## Contexte

Le cache LiteSpeed de Hostinger (hébergement mutualisé) sert aux visiteurs anonymes une version HTML et CSS mise en cache. Après chaque `git pull` sur le serveur, le nouveau HTML référence le nouveau fichier CSS (ex: `main-BArGPgi0.css`), mais les navigateurs des visiteurs reçoivent encore l'ancien HTML avec l'ancienne URL CSS (`main-ApUCz-B4.css`) depuis le cache HTTP.

Ce décalage version-HTML / version-CSS a plusieurs conséquences :

- **Classes Tailwind arbitraires absentes** du vieux CSS : `top-[8%]`, `min-h-[260px]`, `bg-white/10`, `h-[260px]` → styles non appliqués
- **Positions incorrectes** : cartes flottantes sans position → en haut à gauche par défaut
- **Chevauchement** : `min-height` non appliqué → panneau de hauteur 0 → cartes empilées
- **Image invisible** : `hidden lg:block` présent dans les deux versions, mais `block` absent du vieux CSS si récemment ajouté

Le problème est particulièrement pernicieux car le site **apparaît correct pour l'administrateur connecté** (qui contourne le cache) mais est cassé pour les visiteurs anonymes.

---

## Décision

### Règle : inline style pour tout ce qui est critique et précis

Tout style dont l'absence ou l'approximation cause un bug visible (position, hauteur, couleur de fond, bordure) est écrit en attribut `style=""` directement sur l'élément HTML, **non en classe Tailwind**.

**En inline style :**
```html
<!-- Position -->
style="top:8%;left:26%;bottom:8%;right:5%;"

<!-- Dimensions -->
style="width:168px;min-height:260px;"

<!-- Couleurs critiques (transparence des cartes) -->
style="background-color:rgba(255,255,255,0.06);border:1px solid rgba(255,255,255,0.14);"

<!-- Overlay -->
style="background-color:rgba(10,20,45,0.45);"
```

**En classes Tailwind (utilitaires de base, toujours présents) :**
```html
class="absolute relative hidden lg:block rounded-2xl backdrop-blur-md shadow-xl p-3.5 flex items-center gap-2"
```

### Règle : classes Tailwind arbitraires interdites dans hero.php

Les classes avec valeurs arbitraires (syntaxe `[valeur]`) ne sont jamais garanties dans une version précédente du CSS compilé. Elles sont **interdites** pour les éléments critiques du hero.

| Interdit | Remplacé par |
|---|---|
| `top-[8%]` | `style="top:8%;"` |
| `min-h-[260px]` | `style="min-height:260px;"` |
| `bg-white/10` | `style="background-color:rgba(255,255,255,0.10);"` |
| `h-[460px]` | JS ou inline style |
| `lg:[clip-path:polygon(...)]` | Classe Tailwind — acceptable car non critique (dégradation gracieuse si absent) |

---

## Gestion responsive des cartes hero

### Problème : 3 cartes sur mobile 260px

3 cartes de ~85px dans un conteneur de 260px minimum → 255px de cartes + 5px max de gap → chevauchement.

### Solution retenue

| Écran | Cartes visibles | Raison |
|---|---|---|
| Mobile (`< 1024px`) | Card 1 + Card 3 | Positionnées aux extrémités, gap ≈ 48px garanti |
| Desktop (`≥ 1024px`) | Card 1 + Card 2 + Card 3 | Panneau ≥ 460px, gap ≈ 70px entre chaque |

Card 2 porte la classe `hidden lg:block` — classe de base Tailwind garantie dans tout build.

### Calcul de gap mobile (panneau 260px)

```
Card 1 : top 8% = 20.8px → bas à ≈ 106px
Card 3 : bottom 8% = 20.8px → haut à ≈ 154px
Gap = 154 - 106 = 48px ✅
```

### Calcul de non-chevauchement desktop (panneau 460px)

```
Card 1 : top 8% = 36.8px → bas à ≈ 122px
Card 2 : top 40% = 184px → bas à ≈ 269px   (gap C1→C2 = 62px ✅)
Card 3 : bottom 8% = 36.8px → haut à ≈ 338px  (gap C2→C3 = 69px ✅)
```

---

## Alternatives considérées

| Option | Raison du rejet |
|---|---|
| Vider le cache LiteSpeed manuellement à chaque déploiement | Pas fiable (oubli possible, accès hPanel requis) |
| Passer en mode "No Cache" pour les pages | Impacte les performances pour tous les visiteurs |
| Forcer le cache-bust via `?v=` sur les CSS | LiteSpeed cache l'HTML aussi — l'ancien HTML pointe vers l'ancienne URL CSS |
| Limiter à 2 cartes sur desktop | Perte de richesse visuelle sans nécessité |

---

## Conséquences

- ✅ Les cartes s'affichent correctement quelle que soit la version CSS servie par le cache
- ✅ Dégradation gracieuse : même sans CSS, les cartes sont positionnées et colorées
- ✅ Zéro chevauchement mobile garanti mathématiquement
- ⚠️ Les styles inline ne sont pas centralisés — toute modification de design des cartes nécessite d'éditer hero.php directement
- ⚠️ `backdrop-blur-md` reste une classe Tailwind — si elle est absente du vieux CSS, les cartes manquent de flou (dégradation acceptable, pas un bug bloquant)

---

## Fichiers impactés

| Fichier | Modification |
|---|---|
| `template-parts/home/hero.php` | Positions, couleurs et dimensions des cartes en inline style |
| `src/main.js` | Suppression du JS `data-hero-panel` (min-height géré en inline style statique) |
