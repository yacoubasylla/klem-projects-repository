#!/usr/bin/env bash
# Script d'amorçage unique pour le projet KLEM Technologies
# Usage : bash bootstrap.sh
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "==> [1/5] Correction des permissions sur web/"
sudo chown -R "$USER:$USER" "$PROJECT_DIR/web"

echo "==> [2/5] Création de la structure de répertoires"
mkdir -p \
  "$PROJECT_DIR/web/app/themes/klem-theme/src" \
  "$PROJECT_DIR/web/app/themes/klem-theme/template-parts/home" \
  "$PROJECT_DIR/web/app/mu-plugins" \
  "$PROJECT_DIR/web/app/plugins"

# ---- web/.htaccess ----
cat > "$PROJECT_DIR/web/.htaccess" << 'HTACCESS'
# BEGIN WordPress
<IfModule mod_rewrite.c>
RewriteEngine On
RewriteBase /
RewriteRule ^index\.php$ - [L]
RewriteCond %{REQUEST_FILENAME} !-f
RewriteCond %{REQUEST_FILENAME} !-d
RewriteRule . /index.php [L]
</IfModule>
# END WordPress
HTACCESS

# ---- web/index.php ----
cat > "$PROJECT_DIR/web/index.php" << 'INDEXPHP'
<?php
/**
 * Point d'entrée WordPress - Architecture Bedrock KLEM
 * Le core WordPress est dans web/wp/, le contenu dans web/app/
 */
define('WP_USE_THEMES', true);
require __DIR__ . '/wp/wp-blog-header.php';
INDEXPHP

# ---- web/wp-config.php ----
cat > "$PROJECT_DIR/web/wp-config.php" << 'WPCONFIG'
<?php
/**
 * Configuration WordPress - KLEM Technologies
 *
 * ABSPATH est défini par wp-load.php (= web/wp/) avant d'inclure ce fichier.
 * WP_CONTENT_DIR et WP_CONTENT_URL pointent vers web/app/ (structure Bedrock).
 */

// === BASE DE DONNÉES (Environnement Docker) ===
define('DB_NAME',     'klem_showcase_db');
define('DB_USER',     'klem_user');
define('DB_PASSWORD', 'klem_password');
define('DB_HOST',     'db:3306');
define('DB_CHARSET',  'utf8mb4');
define('DB_COLLATE',  '');

// === RÉPERTOIRE DE CONTENU (Bedrock : web/app/ remplace wp-content/) ===
define('WP_CONTENT_DIR', dirname(ABSPATH) . '/app');
define('WP_CONTENT_URL', 'http://localhost:8080/app');

// === URLs ===
define('WP_HOME',    'http://localhost:8080');
define('WP_SITEURL', 'http://localhost:8080/wp');

// === CLÉS DE SÉCURITÉ (remplacer en production via wp-cli salts) ===
define('AUTH_KEY',         'klem_dev_auth_key_a_remplacer');
define('SECURE_AUTH_KEY',  'klem_dev_secure_auth_key_a_remplacer');
define('LOGGED_IN_KEY',    'klem_dev_logged_in_key_a_remplacer');
define('NONCE_KEY',        'klem_dev_nonce_key_a_remplacer');
define('AUTH_SALT',        'klem_dev_auth_salt_a_remplacer');
define('SECURE_AUTH_SALT', 'klem_dev_secure_auth_salt_a_remplacer');
define('LOGGED_IN_SALT',   'klem_dev_logged_in_salt_a_remplacer');
define('NONCE_SALT',       'klem_dev_nonce_salt_a_remplacer');

$table_prefix = 'klem_';

define('WP_DEBUG',         true);
define('WP_DEBUG_LOG',     true);
define('WP_DEBUG_DISPLAY', false);

if (!defined('ABSPATH')) {
    define('ABSPATH', __DIR__ . '/wp/');
}

require_once ABSPATH . 'wp-settings.php';
WPCONFIG

echo "==> [3/5] Fichiers racine web/ créés (.htaccess, index.php, wp-config.php)"

# ---- Thème : style.css (en-tête obligatoire WordPress) ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/style.css" << 'STYLECSS'
/*
Theme Name:   KLEM Theme
Theme URI:    https://klem.tech
Author:       KLEM Technologies & Services
Description:  Thème propriétaire sur-mesure pour le site institutionnel KLEM. Construit avec Vite et Tailwind CSS.
Version:      1.0.0
Text Domain:  klem-theme
*/
STYLECSS

# ---- Thème : functions.php ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/functions.php" << 'FUNCTIONS'
<?php

declare(strict_types=1);

function klem_theme_setup(): void {
    add_theme_support('title-tag');
    add_theme_support('post-thumbnails');
    add_theme_support('html5', ['search-form', 'comment-form', 'comment-list', 'gallery', 'caption']);
    load_theme_textdomain('klem-theme', get_template_directory() . '/languages');

    register_nav_menus([
        'primary' => esc_html__('Menu Principal', 'klem-theme'),
        'footer'  => esc_html__('Menu Pied de Page', 'klem-theme'),
    ]);
}
add_action('after_setup_theme', 'klem_theme_setup');

function klem_enqueue_assets(): void {
    $theme_uri = get_template_directory_uri();
    $theme_dir = get_template_directory();
    $manifest  = $theme_dir . '/dist/.vite/manifest.json';

    if (file_exists($manifest)) {
        $data  = json_decode(file_get_contents($manifest), true);
        $entry = $data['src/main.js'] ?? null;
        if ($entry) {
            if (!empty($entry['css'])) {
                wp_enqueue_style('klem-style', $theme_uri . '/dist/' . $entry['css'][0], [], null);
            }
            wp_enqueue_script('klem-script', $theme_uri . '/dist/' . $entry['file'], [], null, true);
        }
    }
}
add_action('wp_enqueue_scripts', 'klem_enqueue_assets');
FUNCTIONS

# ---- Thème : header.php ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/header.php" << 'HEADER'
<!DOCTYPE html>
<html <?php language_attributes(); ?> class="scroll-smooth">
<head>
    <meta charset="<?php bloginfo('charset'); ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <?php wp_head(); ?>
</head>
<body <?php body_class('bg-white text-klem-blue antialiased'); ?>>
<?php wp_body_open(); ?>

<header class="fixed top-0 left-0 right-0 z-50 bg-klem-blue/95 backdrop-blur-sm">
    <div class="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
        <a href="<?php echo esc_url(home_url('/')); ?>" class="text-white font-bold text-xl tracking-widest">
            KLEM
        </a>
        <nav>
            <?php
            wp_nav_menu([
                'theme_location' => 'primary',
                'container'      => false,
                'menu_class'     => 'flex items-center gap-8',
                'link_before'    => '<span class="text-white/80 hover:text-klem-orange transition-colors text-sm font-medium">',
                'link_after'     => '</span>',
                'fallback_cb'    => false,
            ]);
            ?>
        </nav>
    </div>
</header>
HEADER

# ---- Thème : footer.php ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/footer.php" << 'FOOTER'
<footer class="bg-klem-blue text-white/70 py-12 mt-24">
    <div class="max-w-7xl mx-auto px-6 text-center">
        <p class="text-sm">
            &copy; <?php echo esc_html(date('Y')); ?> KLEM Technologies &amp; Services. Tous droits réservés.
        </p>
    </div>
</footer>

<?php wp_footer(); ?>
</body>
</html>
FOOTER

# ---- Thème : index.php (template de secours) ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/index.php" << 'INDEXTEMPLATE'
<?php get_header(); ?>

<main class="pt-24 max-w-7xl mx-auto px-6 py-16">
    <?php if (have_posts()) : while (have_posts()) : the_post(); ?>
        <article>
            <h1 class="text-3xl font-bold text-klem-blue"><?php the_title(); ?></h1>
            <div class="mt-4 prose"><?php the_content(); ?></div>
        </article>
    <?php endwhile; endif; ?>
</main>

<?php get_footer(); ?>
INDEXTEMPLATE

# ---- Thème : front-page.php (page d'accueil) ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/front-page.php" << 'FRONTPAGE'
<?php get_header(); ?>

<main id="main-content">
    <?php get_template_part('template-parts/home/hero'); ?>
    <?php get_template_part('template-parts/home/services'); ?>
    <?php get_template_part('template-parts/home/about'); ?>
</main>

<?php get_footer(); ?>
FRONTPAGE

# ---- template-parts/home/hero.php ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/template-parts/home/hero.php" << 'HERO'
<section class="relative min-h-screen bg-klem-blue flex items-center overflow-hidden">
    <div class="absolute inset-0 bg-gradient-to-br from-klem-blue via-klem-blue/90 to-klem-orange/20"></div>
    <div class="relative z-10 max-w-7xl mx-auto px-6 pt-24">
        <p class="text-klem-orange font-semibold tracking-widest text-sm uppercase mb-4">
            <?php esc_html_e('Intégrateur Numérique de Référence en Afrique', 'klem-theme'); ?>
        </p>
        <h1 class="text-5xl md:text-7xl font-extrabold text-white leading-tight max-w-3xl">
            <?php esc_html_e('Accélérez Votre Transformation Digitale', 'klem-theme'); ?>
        </h1>
        <p class="mt-6 text-white/70 text-lg max-w-xl leading-relaxed">
            <?php esc_html_e('KLEM Technologies conçoit et déploie des architectures numériques sur-mesure pour les entreprises et les institutions publiques.', 'klem-theme'); ?>
        </p>
        <div class="mt-10 flex flex-wrap gap-4">
            <a href="#services" class="inline-block bg-klem-orange text-white font-semibold px-8 py-4 rounded-lg hover:bg-klem-orange/90 transition-colors">
                <?php esc_html_e('Découvrir nos services', 'klem-theme'); ?>
            </a>
            <a href="#contact" class="inline-block border border-white/30 text-white font-semibold px-8 py-4 rounded-lg hover:border-white/60 transition-colors">
                <?php esc_html_e('Nous contacter', 'klem-theme'); ?>
            </a>
        </div>
    </div>
</section>
HERO

# ---- template-parts/home/services.php ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/template-parts/home/services.php" << 'SERVICES'
<section id="services" class="py-24 bg-gray-50">
    <div class="max-w-7xl mx-auto px-6">
        <div class="text-center mb-16">
            <p class="text-klem-orange font-semibold tracking-widest text-sm uppercase mb-3">
                <?php esc_html_e('Notre Expertise', 'klem-theme'); ?>
            </p>
            <h2 class="text-4xl font-bold text-klem-blue">
                <?php esc_html_e('Quatre Piliers d\'Excellence', 'klem-theme'); ?>
            </h2>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            <?php
            $services = [
                [
                    'title' => 'Solutions Numériques & Intégration',
                    'desc'  => 'Transformation digitale, architectures sur-mesure et intégration de systèmes complexes.',
                ],
                [
                    'title' => 'Développement Web & Mobile',
                    'desc'  => 'Applications d\'envergure, ERP, logiciels métiers et plateformes digitales haute performance.',
                ],
                [
                    'title' => 'Ingénierie des Données',
                    'desc'  => 'Pipelines Big Data, architectures temps réel avec Kafka et Spark pour la prise de décision.',
                ],
                [
                    'title' => 'Fourniture de Matériel',
                    'desc'  => 'Équipements serveurs et réseaux de qualité entreprise pour vos infrastructures critiques.',
                ],
            ];
            foreach ($services as $i => $service) :
            ?>
            <div class="bg-white rounded-2xl p-8 shadow-sm hover:shadow-md transition-shadow border border-gray-100 group">
                <div class="w-12 h-1 bg-klem-orange rounded mb-6 group-hover:w-20 transition-all duration-300"></div>
                <h3 class="text-lg font-bold text-klem-blue mb-3"><?php echo esc_html($service['title']); ?></h3>
                <p class="text-gray-600 text-sm leading-relaxed"><?php echo esc_html($service['desc']); ?></p>
            </div>
            <?php endforeach; ?>
        </div>
    </div>
</section>
SERVICES

# ---- template-parts/home/about.php ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/template-parts/home/about.php" << 'ABOUT'
<section id="about" class="py-24 bg-white">
    <div class="max-w-7xl mx-auto px-6">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
            <div>
                <p class="text-klem-orange font-semibold tracking-widest text-sm uppercase mb-3">
                    <?php esc_html_e('À Propos', 'klem-theme'); ?>
                </p>
                <h2 class="text-4xl font-bold text-klem-blue leading-tight mb-6">
                    <?php esc_html_e('Partenaire Stratégique de votre Croissance', 'klem-theme'); ?>
                </h2>
                <p class="text-gray-600 leading-relaxed mb-4">
                    <?php esc_html_e('KLEM Technologies & Services est un intégrateur numérique positionné sur les marchés privés et publics d\'Afrique. Notre mission : rendre accessible l\'excellence technologique aux organisations qui construisent le continent de demain.', 'klem-theme'); ?>
                </p>
                <a href="#contact" class="inline-block mt-4 text-klem-orange font-semibold border-b-2 border-klem-orange pb-1 hover:opacity-80 transition-opacity">
                    <?php esc_html_e('Parlons de votre projet →', 'klem-theme'); ?>
                </a>
            </div>
            <div class="bg-klem-blue/5 rounded-3xl p-12 flex items-center justify-center min-h-64">
                <p class="text-klem-blue/30 text-center text-sm">
                    <?php esc_html_e('[Visuels / Statistiques clés]', 'klem-theme'); ?>
                </p>
            </div>
        </div>
    </div>
</section>
ABOUT

echo "==> [4/5] Templates du thème créés"

# ---- Thème : package.json ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/package.json" << 'PKGJSON'
{
  "name": "klem-theme",
  "version": "1.0.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build"
  },
  "devDependencies": {
    "autoprefixer": "^10.4.19",
    "postcss": "^8.4.38",
    "tailwindcss": "^3.4.4",
    "vite": "^5.3.1"
  }
}
PKGJSON

# ---- Thème : tailwind.config.js ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/tailwind.config.js" << 'TAILWINDCONFIG'
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './*.php',
    './template-parts/**/*.php',
  ],
  theme: {
    extend: {
      colors: {
        'klem-blue':   '#0B192C',
        'klem-orange': '#FF6500',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
TAILWINDCONFIG

# ---- Thème : postcss.config.js ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/postcss.config.js" << 'POSTCSS'
export default {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
  },
};
POSTCSS

# ---- Thème : vite.config.js ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/vite.config.js" << 'VITECONFIG'
import { defineConfig } from 'vite';

export default defineConfig({
  build: {
    outDir: 'dist',
    manifest: true,
    rollupOptions: {
      input: {
        main: 'src/main.js',
      },
    },
  },
});
VITECONFIG

# ---- Thème : src/main.css ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/src/main.css" << 'MAINCSS'
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer base {
  html {
    font-family: 'Inter', system-ui, sans-serif;
  }
}
MAINCSS

# ---- Thème : src/main.js ----
cat > "$PROJECT_DIR/web/app/themes/klem-theme/src/main.js" << 'MAINJS'
import './main.css';
MAINJS

echo "==> [5/5] Configs Vite + Tailwind créées"

echo ""
echo "==> Terminé. Lance maintenant dans l'ordre :"
echo "    composer install"
echo "    cd web/app/themes/klem-theme && pnpm install && pnpm build && cd -"
echo "    docker compose down && docker compose up --build -d"
