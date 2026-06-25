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

// === SMTP — Brevo (ex-Sendinblue) ============================================
// Clé SMTP Brevo : https://app.brevo.com/settings/keys/smtp
define('KLEM_SMTP_HOST',      'smtp-relay.brevo.com');
define('KLEM_SMTP_PORT',      587);
define('KLEM_SMTP_USER',      'VOTRE_EMAIL_BREVO@example.com');   // login Brevo
define('KLEM_SMTP_PASS',      'VOTRE_CLE_SMTP_BREVO');            // clé SMTP (≠ mot de passe compte)
define('KLEM_SMTP_FROM',      'contact@klem.tech');
define('KLEM_SMTP_FROM_NAME', 'KLEM Technologies & Services');

define('WP_DEBUG',         true);
define('WP_DEBUG_LOG',     true);
define('WP_DEBUG_DISPLAY', false);

if (!defined('ABSPATH')) {
    define('ABSPATH', __DIR__ . '/wp/');
}

require_once ABSPATH . 'wp-settings.php';
