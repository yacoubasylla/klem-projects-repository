<?php
/**
 * Configuration WordPress — KLEM Technologies
 *
 * Compatible Docker (env vars système) ET hébergement partagé (fichier .env).
 * ABSPATH est défini par wp-load.php (= web/wp/) avant d'inclure ce fichier.
 */

// ── Chargeur .env natif (pas de bibliothèque requise) ────────────────────────
// Lis le fichier .env situé à la racine du projet (un niveau au-dessus de web/)
$_klem_env_path = dirname(__DIR__) . '/.env';
if (file_exists($_klem_env_path)) {
    foreach (file($_klem_env_path, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES) as $_klem_line) {
        $trimmed = trim($_klem_line);
        if (str_starts_with($trimmed, '#') || !str_contains($trimmed, '=')) {
            continue;
        }
        [$_kk, $_kv] = explode('=', $trimmed, 2);
        $_kk = trim($_kk);
        $_kv = trim($_kv);
        // Ne pas écraser les vars déjà définies par le système (ex. Docker env_file)
        if (!getenv($_kk)) {
            putenv("{$_kk}={$_kv}");
            $_ENV[$_kk] = $_kv;
        }
    }
}
unset($_klem_env_path, $_klem_line, $trimmed, $_kk, $_kv);

// ── Base de données ──────────────────────────────────────────────────────────
define('DB_NAME',     getenv('DB_NAME')     ?: 'klem_showcase_db');
define('DB_USER',     getenv('DB_USER')     ?: 'klem_user');
define('DB_PASSWORD', getenv('DB_PASSWORD') ?: 'klem_password');
define('DB_HOST',     getenv('DB_HOST')     ?: 'db:3306');  // db:3306 = conteneur Docker
define('DB_CHARSET',  'utf8mb4');
define('DB_COLLATE',  '');

// ── URLs ─────────────────────────────────────────────────────────────────────
$_klem_home = getenv('WP_HOME') ?: 'http://localhost:8080';
define('WP_HOME',    $_klem_home);
define('WP_SITEURL', getenv('WP_SITEURL') ?: 'http://localhost:8080/wp');

// ── Répertoire de contenu (Bedrock : web/app/ remplace wp-content/) ──────────
define('WP_CONTENT_DIR', dirname(ABSPATH) . '/app');
define('WP_CONTENT_URL', $_klem_home . '/app');
unset($_klem_home);

// ── Clés de sécurité ─────────────────────────────────────────────────────────
// En production : générer sur https://api.wordpress.org/secret-key/1.1/salt/
// et les stocker dans .env
define('AUTH_KEY',         getenv('AUTH_KEY')         ?: 'klem_dev_auth_key_a_remplacer');
define('SECURE_AUTH_KEY',  getenv('SECURE_AUTH_KEY')  ?: 'klem_dev_secure_auth_key_a_remplacer');
define('LOGGED_IN_KEY',    getenv('LOGGED_IN_KEY')    ?: 'klem_dev_logged_in_key_a_remplacer');
define('NONCE_KEY',        getenv('NONCE_KEY')        ?: 'klem_dev_nonce_key_a_remplacer');
define('AUTH_SALT',        getenv('AUTH_SALT')        ?: 'klem_dev_auth_salt_a_remplacer');
define('SECURE_AUTH_SALT', getenv('SECURE_AUTH_SALT') ?: 'klem_dev_secure_auth_salt_a_remplacer');
define('LOGGED_IN_SALT',   getenv('LOGGED_IN_SALT')   ?: 'klem_dev_logged_in_salt_a_remplacer');
define('NONCE_SALT',       getenv('NONCE_SALT')       ?: 'klem_dev_nonce_salt_a_remplacer');

$table_prefix = 'klem_';

// ── Email — API REST Brevo ────────────────────────────────────────────────────
define('KLEM_BREVO_API_KEY',  getenv('KLEM_BREVO_API_KEY')  ?: '');
define('KLEM_SMTP_FROM',      getenv('KLEM_SMTP_FROM')      ?: 'infos@klemtech.net');
define('KLEM_SMTP_FROM_NAME', getenv('KLEM_SMTP_FROM_NAME') ?: 'KLEM Technologies & Services');

// ── Chatbot — API Anthropic (Claude) ───────────────────────────────────────────
define('KLEM_ANTHROPIC_API_KEY', getenv('KLEM_ANTHROPIC_API_KEY') ?: '');
define('KLEM_ANTHROPIC_MODEL',   getenv('KLEM_ANTHROPIC_MODEL')   ?: 'claude-haiku-4-5-20251001');

// ── Analytics — Google Analytics 4 ─────────────────────────────────────────────
define('KLEM_GA_MEASUREMENT_ID', getenv('KLEM_GA_MEASUREMENT_ID') ?: '');

// ── Debug ─────────────────────────────────────────────────────────────────────
$_klem_env_name = getenv('WP_ENV') ?: 'development';
define('WP_DEBUG',         $_klem_env_name !== 'production');
define('WP_DEBUG_LOG',     $_klem_env_name !== 'production');
define('WP_DEBUG_DISPLAY', false);

// ── Garde-fou production ──────────────────────────────────────────────────────
// Refuse de démarrer si le .env du serveur est incomplet et que WordPress
// s'apprêterait à utiliser les valeurs de repli du dépôt (publiques, donc
// connues de quiconque lit ce fichier) pour signer les cookies d'authentification
// ou se connecter à la base de données.
if ($_klem_env_name === 'production') {
    $_klem_still_default = AUTH_KEY === 'klem_dev_auth_key_a_remplacer'
        || SECURE_AUTH_KEY === 'klem_dev_secure_auth_key_a_remplacer'
        || LOGGED_IN_KEY === 'klem_dev_logged_in_key_a_remplacer'
        || NONCE_KEY === 'klem_dev_nonce_key_a_remplacer'
        || DB_PASSWORD === 'klem_password';

    if ($_klem_still_default) {
        http_response_code(500);
        exit('Configuration serveur incomplète : clés de sécurité ou identifiants de base de données absents du .env de production.');
    }
    unset($_klem_still_default);
}
unset($_klem_env_name);

// ── Durcissement production ────────────────────────────────────────────────────
define('DISALLOW_FILE_EDIT', true); // pas d'édition de thème/plugin depuis wp-admin
define('DISALLOW_FILE_MODS', true); // pas d'installation de plugin/thème depuis wp-admin (cf. CLAUDE.md : ajouts via composer.json uniquement)
if ((getenv('WP_ENV') ?: 'development') === 'production') {
    define('FORCE_SSL_ADMIN', true);
}

if (!defined('ABSPATH')) {
    define('ABSPATH', __DIR__ . '/wp/');
}

require_once ABSPATH . 'wp-settings.php';
