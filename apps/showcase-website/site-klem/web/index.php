<?php
/**
 * Point d'entrée WordPress - Architecture Bedrock KLEM
 * Le core WordPress est dans web/wp/, le contenu dans web/app/
 */
define('WP_USE_THEMES', true);
require __DIR__ . '/wp/wp-blog-header.php';
