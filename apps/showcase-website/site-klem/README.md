# 🌐 KLEM Technologies - Site Vitrine Professionnel

Ce sous-projet contient le site institutionnel de **KLEM Technologies & Services**.
Il est architecturé de manière moderne (Structure Bedrock) pour être géré proprement via Git, Docker et orchestré par le monorepo global.

## 🛠️ Stack Technique

- **CMS** : WordPress ^6.5 en structure **Bedrock** (Composer, `web/wp` pour le core, `web/app` pour le contenu/thème/plugins).
- **PHP** : >= 8.2 (géré via Composer, `composer.json`).
- **Thème** : `klem-theme` (`web/app/themes/klem-theme`), buildé avec Vite + Tailwind CSS (voir `CLAUDE.md` pour les standards de code).
- **Base de données locale** : MySQL 8.0 via Docker Compose (`docker-compose.yml`, conteneur `klem_site_db`).
- **Build système** : pnpm + Turborepo au niveau du sous-projet (`turbo.json`).

## 🚀 Démarrage Rapide (Local)

1. **Configurer l'environnement :**
   ```bash
   cp .env.example .env
   ```
2. **Installer les dépendances PHP (Composer) :**
   ```bash
   composer install
   ```
3. **Lancer la base de données locale :**
   ```bash
   docker compose up -d
   ```
4. **Installer les dépendances JS et lancer le thème en mode dev :**
   ```bash
   pnpm dev
   ```
   (installe automatiquement les dépendances du thème avant de lancer Vite — voir `package.json`.)
5. **Build de production du thème :**
   ```bash
   pnpm build
   ```

## 🔐 Accès & secrets

Les accès d'hébergement (Hostinger, base de données de production, wp-cli) sont documentés dans
`ACCESS.md`, **volontairement non versionné** (`.gitignore`) — fichier local à tenir à jour
manuellement, jamais à committer.
