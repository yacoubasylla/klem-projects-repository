# 🛠️ CLAUDE.md : Règles Spécifiques WordPress / Showcase-Site

> **Règle de Portée :** Les directives de ce fichier prévalent sur le CLAUDE.md racine pour tout ce qui concerne le sous-dossier `apps/showcase-site/site-klem/`.

## ⚡ Commandes du Projet Local
Depuis la racine de ce sous-dossier :
- **Installer les dépendances PHP :** `composer install`
- **Compiler les assets du thème (Production) :** `pnpm build`
- **Lancer le compilateur du thème (Dev / Watch) :** `pnpm dev`

## 🛠️ Standards de Code WordPress & PHP
- **Sécurité :** Échapper systématiquement toutes les sorties dans les templates PHP via les fonctions natives WordPress (`esc_html()`, `esc_attr()`, `esc_url()`).
- **Pas de requêtes SQL brutes :** Utiliser exclusivement l'abstraction `WP_Query` ou les fonctions de l'API WordPress.
- **Thématisation :** Le thème `klem-theme` doit être modulaire. Découper les sections de la page d'accueil en fichiers distincts dans `template-parts/`.
- **CSS/JS :** Aucun style CSS ne doit être écrit en dur dans les fichiers PHP. Utiliser exclusivement les classes utilitaires **Tailwind CSS**. Tout le JavaScript moderne doit passer par le bundler Vite.
- **Extensions :** Interdiction d'installer des extensions lourdes via l'admin WordPress. Tout ajout de plugin doit être déclaré dans le fichier `composer.json`.

## 🔄 Clôture de Tâche
Avant de rendre la main, assure-toi que la compilation des assets (`pnpm build`) ne génère aucune erreur et que le code PHP respecte les standards PSR-12.