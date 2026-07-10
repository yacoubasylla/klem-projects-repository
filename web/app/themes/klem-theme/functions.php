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

function klem_enqueue_fonts(): void {
    wp_enqueue_style(
        'klem-fonts',
        'https://fonts.googleapis.com/css2?family=Archivo:wght@600;700;800&display=swap',
        [],
        null
    );
}
add_action('wp_enqueue_scripts', 'klem_enqueue_fonts');

function klem_enqueue_assets(): void {
    // Archivo chargée via klem_enqueue_fonts()
    $theme_uri = get_template_directory_uri();
    $theme_dir = get_template_directory();
    $manifest  = $theme_dir . '/dist/.vite/manifest.json';

    if (defined('WP_DEBUG') && WP_DEBUG && !file_exists($manifest)) {
        // Dev mode : assets servis par Vite HMR sur localhost:5173
        add_filter('script_loader_tag', function (string $tag, string $handle): string {
            if (in_array($handle, ['vite-client', 'klem-script'], true)) {
                return str_replace('<script ', '<script type="module" ', $tag);
            }
            return $tag;
        }, 10, 2);

        wp_enqueue_script('vite-client', 'http://localhost:5173/@vite/client', [], null, false);
        wp_enqueue_script('klem-script', 'http://localhost:5173/src/main.js', [], null, false);
        return;
    }

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

function klem_enqueue_ajax_config(): void {
    wp_localize_script('klem-script', 'klemAjax', [
        'url'   => admin_url('admin-ajax.php'),
        'nonce' => wp_create_nonce('klem_contact_nonce'),
    ]);
}
add_action('wp_enqueue_scripts', 'klem_enqueue_ajax_config', 20);

function klem_handle_contact(): void {
    if (!check_ajax_referer('klem_contact_nonce', 'klem_nonce', false)) {
        wp_send_json_error(['message' => __('Requête non autorisée.', 'klem-theme')], 403);
    }

    // ── Anti-spam 1 : honeypot ────────────────────────────────────────────────
    if (!empty($_POST['klem_website'])) {
        wp_send_json_error(['message' => __('Votre message a bien été envoyé. Nous vous répondons sous 24 h.', 'klem-theme')]);
    }

    // ── Anti-spam 2 : jeton horodaté (soumission < 3 s = bot) ────────────────
    $ts    = (int) sanitize_text_field(wp_unslash($_POST['klem_ts']    ?? '0'));
    $token = sanitize_text_field(wp_unslash($_POST['klem_token'] ?? ''));
    $elapsed = time() - $ts;
    if ($elapsed < 3 || $elapsed > 3600 || !hash_equals(wp_hash($ts . 'klem_contact_token'), $token)) {
        wp_send_json_error(['message' => __('Votre message a bien été envoyé. Nous vous répondons sous 24 h.', 'klem-theme')]);
    }

    // ── Anti-spam 3 : limite de débit — max 3 envois / IP / heure ─────────────
    $ip  = sanitize_text_field(wp_unslash($_SERVER['REMOTE_ADDR'] ?? ''));
    $key = 'klem_rate_' . md5($ip);
    $hits = (int) get_transient($key);
    if ($hits >= 3) {
        wp_send_json_error(['message' => __('Trop de tentatives. Merci de réessayer dans une heure.', 'klem-theme')], 429);
    }
    set_transient($key, $hits + 1, HOUR_IN_SECONDS);

    $name    = sanitize_text_field(wp_unslash($_POST['klem_name']    ?? ''));
    $company = sanitize_text_field(wp_unslash($_POST['klem_company'] ?? ''));
    $email   = sanitize_email(wp_unslash($_POST['klem_email']        ?? ''));
    $phone   = sanitize_text_field(wp_unslash($_POST['klem_phone']   ?? ''));
    $subject = sanitize_text_field(wp_unslash($_POST['klem_subject'] ?? ''));
    $message = sanitize_textarea_field(wp_unslash($_POST['klem_message'] ?? ''));

    if (!$name || !$email || !$subject || !$message) {
        wp_send_json_error(['message' => __('Veuillez remplir tous les champs obligatoires.', 'klem-theme')], 422);
    }

    if (!is_email($email)) {
        wp_send_json_error(['message' => __('Adresse e-mail invalide.', 'klem-theme')], 422);
    }

    $to = ['infos@klemtech.net', 'yacouba.sylla@klemtech.net', 'ciyasyl@gmail.com'];
    $headers = [
        'Content-Type: text/plain; charset=UTF-8',
        sprintf('Reply-To: %s <%s>', $name, $email),
    ];

    $body = sprintf(
        "Nom : %s\nSociété : %s\nE-mail : %s\nTéléphone : %s\nSujet : %s\n\nMessage :\n%s",
        $name, $company, $email, $phone, $subject, $message
    );

    $sent = wp_mail($to, sprintf('[KLEM] Contact – %s', $subject), $body, $headers);

    if ($sent) {
        wp_send_json_success(['message' => __('Votre message a bien été envoyé. Nous vous répondons sous 24 h.', 'klem-theme')]);
    } else {
        wp_send_json_error(['message' => __('Une erreur est survenue. Merci de réessayer ou de nous contacter directement par e-mail.', 'klem-theme')], 500);
    }
}
add_action('wp_ajax_klem_contact',        'klem_handle_contact');
add_action('wp_ajax_nopriv_klem_contact', 'klem_handle_contact');

/**
 * ── Actualités : catégories + page hub ──────────────────────────────────
 * Crée automatiquement (une seule fois, de façon idempotente) les 3
 * catégories utilisées par le hub Actualités ainsi que la page qui
 * l'affiche, pour que le lien de menu fonctionne dès l'activation du
 * thème sans intervention manuelle en base de données.
 */

/**
 * ── Fondations SEO techniques ────────────────────────────────────────────
 * Corrige, une seule fois et de façon idempotente, deux réglages WordPress
 * qui pénalisent le référencement s'ils restent sur leur valeur par défaut :
 * les permaliens « bruts » (?p=123, mauvais pour l'indexation et le partage)
 * et le nom de site mal casé (utilisé dans toutes les balises <title>).
 */
function klem_bootstrap_seo_settings(): void {
    if (get_option('permalink_structure') === '') {
        update_option('permalink_structure', '/%postname%/');
        flush_rewrite_rules();
    }

    if (html_entity_decode(get_option('blogname'), ENT_QUOTES) === 'Klem Technologies & Services') {
        update_option('blogname', 'KLEM Technologies & Services');
    }

    if (get_option('blogdescription') === '') {
        update_option('blogdescription', "Intégrateur numérique de référence en Afrique de l'Ouest");
    }
}
add_action('init', 'klem_bootstrap_seo_settings', 5);

function klem_actualites_categories(): array {
    return [
        'blog'       => 'Blog',
        'actus'      => 'Actualités',
        'evenements' => 'Événements',
    ];
}

function klem_bootstrap_actualites(): void {
    foreach (klem_actualites_categories() as $slug => $name) {
        if (!term_exists($slug, 'category')) {
            wp_insert_term($name, 'category', ['slug' => $slug]);
        }
    }

    if (!get_page_by_path('actualites')) {
        $page_id = wp_insert_post([
            'post_title'   => __('Actualités', 'klem-theme'),
            'post_name'    => 'actualites',
            'post_type'    => 'page',
            'post_status'  => 'publish',
            'post_content' => '',
        ]);

        if ($page_id && !is_wp_error($page_id)) {
            update_post_meta($page_id, '_wp_page_template', 'page-actualites.php');
        }
    }
}
add_action('init', 'klem_bootstrap_actualites', 20);

function klem_actualites_url(): string {
    $page = get_page_by_path('actualites');
    return $page ? get_permalink($page) : home_url('/');
}

/**
 * URL vers une ancre des sections de la page d'accueil (front-page.php).
 * Toujours préfixée par home_url() pour fonctionner depuis n'importe quelle
 * page du site (Actualités, article...), pas seulement depuis l'accueil.
 */
function klem_home_anchor(string $anchor = ''): string {
    return home_url('/') . $anchor;
}

/**
 * Retourne le badge (nom + couleur) de la catégorie Actualités d'un article,
 * ou null si l'article n'appartient à aucune des 3 catégories du hub.
 */
function klem_actualites_badge(int $post_id): ?array {
    $colors = [
        'blog'       => 'bg-blue-600',
        'actus'      => 'bg-klem-blue',
        'evenements' => 'bg-klem-red',
    ];

    foreach (klem_actualites_categories() as $slug => $name) {
        if (has_category($slug, $post_id)) {
            return [
                'slug'  => $slug,
                'name'  => $name,
                'color' => $colors[$slug],
            ];
        }
    }

    return null;
}

function klem_add_favicon(): void {
    $uri = get_template_directory_uri();
    printf('<link rel="icon" type="image/png" sizes="32x32" href="%s">' . "\n", esc_url($uri . '/assets/favicon-32.png'));
    printf('<link rel="icon" type="image/svg+xml" href="%s">' . "\n", esc_url($uri . '/assets/favicon.svg'));
    printf('<link rel="shortcut icon" href="%s">' . "\n", esc_url($uri . '/assets/favicon-32.png'));
}
add_action('wp_head', 'klem_add_favicon', 1);

function klem_disable_emoji(): void {
    remove_action('wp_head', 'print_emoji_detection_script', 7);
    remove_action('wp_print_styles', 'print_emoji_styles');
    remove_action('admin_print_scripts', 'print_emoji_detection_script');
    remove_action('admin_print_styles', 'print_emoji_styles');
    remove_filter('the_content_feed', 'wp_staticize_emoji');
    remove_filter('comment_text_rss', 'wp_staticize_emoji');
    remove_filter('wp_mail', 'wp_staticize_emoji_for_email');
}
add_action('init', 'klem_disable_emoji');

/**
 * ── SEO : titre, meta description, canonical, Open Graph, Twitter Card, JSON-LD ──
 * Couvre la page d'accueil, le hub Actualités et chaque article individuel
 * (les seuls points d'entrée indexables du site à ce jour) via un contexte
 * unique construit par klem_seo_context(), pour éviter de dupliquer la
 * logique par type de page.
 */

function klem_seo_description(): string {
    return __(
        "KLEM Technologies & Services, intégrateur numérique basé à Abidjan : ingénierie Big Data, applications sur-mesure, ERP et infrastructures IT pour l'Afrique de l'Ouest.",
        'klem-theme'
    );
}

/**
 * Construit le contexte SEO (titre, description, url, image, type) de la
 * page actuellement affichée, ou null si aucune surcharge ne s'applique
 * (le thème laisse alors WordPress gérer le titre par défaut).
 */
function klem_seo_context(): ?array {
    $theme_uri     = get_template_directory_uri();
    $default_image = $theme_uri . '/assets/images/services/service-big-data.jpg';
    $site_name     = 'KLEM Technologies & Services';

    if (is_front_page()) {
        return [
            'title'       => __("KLEM Technologies & Services | Intégrateur Numérique à Abidjan – Big Data, ERP & Développement Sur-Mesure", 'klem-theme'),
            'description' => klem_seo_description(),
            'url'         => home_url('/'),
            'image'       => $default_image,
            'type'        => 'website',
        ];
    }

    if (is_singular('post')) {
        $excerpt = get_the_excerpt();
        $image   = has_post_thumbnail() ? get_the_post_thumbnail_url(get_the_ID(), 'large') : $default_image;

        return [
            'title'       => get_the_title() . ' – ' . $site_name,
            'description' => $excerpt ? wp_strip_all_tags($excerpt) : klem_seo_description(),
            'url'         => get_permalink(),
            'image'       => $image,
            'type'        => 'article',
        ];
    }

    if (is_page('actualites')) {
        return [
            'title'       => __('Actualités', 'klem-theme') . ' – ' . $site_name,
            'description' => __("Blog, actualités et événements de KLEM Technologies & Services : innovations, projets et rendez-vous de l'écosystème numérique ouest-africain.", 'klem-theme'),
            'url'         => klem_actualites_url(),
            'image'       => $default_image,
            'type'        => 'website',
        ];
    }

    // Repli générique : toute autre page/article publié(e) reste couvert(e)
    // (titre + canonical propre) sans dupliquer la balise canonical de coeur.
    if (is_singular()) {
        $excerpt = get_the_excerpt();

        return [
            'title'       => get_the_title() . ' – ' . $site_name,
            'description' => $excerpt ? wp_strip_all_tags($excerpt) : klem_seo_description(),
            'url'         => get_permalink(),
            'image'       => $default_image,
            'type'        => 'website',
        ];
    }

    return null;
}

function klem_seo_title(string $title): string {
    $context = klem_seo_context();
    return $context['title'] ?? $title;
}
add_filter('pre_get_document_title', 'klem_seo_title');

// Déclaration de langue correcte : contenu 100 % français (corrige lang="en-US" par défaut)
add_filter('language_attributes', function (): string {
    return 'lang="fr-FR"';
});

// La balise canonical est gérée par klem_seo_meta_tags() pour tout le
// contexte couvert par klem_seo_context() : on retire celle du cœur pour
// éviter deux <link rel="canonical"> sur une même page.
remove_action('wp_head', 'rel_canonical');

function klem_seo_meta_tags(): void {
    $context = klem_seo_context();
    if (!$context) {
        return;
    }

    printf('<meta name="description" content="%s">' . "\n", esc_attr($context['description']));
    printf('<link rel="canonical" href="%s">' . "\n", esc_url($context['url']));

    printf('<meta property="og:type" content="%s">' . "\n", esc_attr($context['type']));
    printf('<meta property="og:site_name" content="KLEM Technologies & Services">' . "\n");
    printf('<meta property="og:locale" content="fr_FR">' . "\n");
    printf('<meta property="og:url" content="%s">' . "\n", esc_url($context['url']));
    printf('<meta property="og:title" content="%s">' . "\n", esc_attr($context['title']));
    printf('<meta property="og:description" content="%s">' . "\n", esc_attr($context['description']));
    printf('<meta property="og:image" content="%s">' . "\n", esc_url($context['image']));

    printf('<meta name="twitter:card" content="summary_large_image">' . "\n");
    printf('<meta name="twitter:title" content="%s">' . "\n", esc_attr($context['title']));
    printf('<meta name="twitter:description" content="%s">' . "\n", esc_attr($context['description']));
    printf('<meta name="twitter:image" content="%s">' . "\n", esc_url($context['image']));
}
add_action('wp_head', 'klem_seo_meta_tags', 2);

function klem_seo_structured_data(): void {
    if (!is_front_page()) {
        return;
    }

    $theme_uri = get_template_directory_uri();

    $data = [
        '@context'     => 'https://schema.org',
        '@type'        => 'ProfessionalService',
        'name'         => 'KLEM Technologies & Services',
        'alternateName' => 'KLEM',
        'url'          => home_url('/'),
        'logo'         => $theme_uri . '/assets/svg/klem-primary.svg',
        'image'        => $theme_uri . '/assets/images/services/service-big-data.jpg',
        'description'  => klem_seo_description(),
        'telephone'    => '+225 07 58 89 24 77',
        'email'        => 'infos@klemtech.net',
        'address'      => [
            '@type'           => 'PostalAddress',
            'streetAddress'   => 'Treichville Arras 1',
            'addressLocality' => 'Abidjan',
            'addressCountry'  => 'CI',
        ],
        'areaServed'   => ["Côte d'Ivoire", 'Afrique de l\'Ouest', 'Afrique'],
        'sameAs'       => [
            'https://www.linkedin.com/company/130474992/',
            'https://x.com/KLEMTechnology',
            'https://www.facebook.com/profile.php?id=61591353966112',
            'https://github.com/yacoubasylla/klem-opensource',
        ],
        'openingHoursSpecification' => [
            [
                '@type'    => 'OpeningHoursSpecification',
                'dayOfWeek' => ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
                'opens'    => '08:00',
                'closes'   => '18:00',
            ],
            [
                '@type'    => 'OpeningHoursSpecification',
                'dayOfWeek' => ['Saturday'],
                'opens'    => '09:00',
                'closes'   => '13:00',
            ],
        ],
        'knowsAbout'   => [
            'Ingénierie des données (Big Data)',
            'Intégration ERP',
            "Développement d'applications sur-mesure",
            'Infrastructures IT',
            'Gestion de flotte (FleetControl)',
            'Gestion de restauration scolaire (Cantine Connect)',
        ],
    ];

    echo '<script type="application/ld+json">' . wp_json_encode($data, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES) . '</script>' . "\n";
}
add_action('wp_head', 'klem_seo_structured_data', 3);

/**
 * JSON-LD Article : améliore l'apparence des articles du hub Actualités
 * dans les résultats de recherche (image, dates, auteur, fil d'ariane).
 */
function klem_seo_article_schema(): void {
    if (!is_singular('post')) {
        return;
    }

    $theme_uri = get_template_directory_uri();
    $image     = has_post_thumbnail() ? get_the_post_thumbnail_url(get_the_ID(), 'large') : ($theme_uri . '/assets/images/services/service-big-data.jpg');
    $excerpt   = get_the_excerpt();
    $badge     = klem_actualites_badge(get_the_ID());

    $data = [
        '@context'         => 'https://schema.org',
        '@type'            => 'Article',
        'headline'         => get_the_title(),
        'description'      => $excerpt ? wp_strip_all_tags($excerpt) : klem_seo_description(),
        'image'            => [$image],
        'datePublished'    => get_the_date('c'),
        'dateModified'     => get_the_modified_date('c'),
        'mainEntityOfPage' => ['@type' => 'WebPage', '@id' => get_permalink()],
        'author'           => ['@type' => 'Organization', 'name' => 'KLEM Technologies & Services'],
        'publisher'        => [
            '@type' => 'Organization',
            'name'  => 'KLEM Technologies & Services',
            'logo'  => ['@type' => 'ImageObject', 'url' => $theme_uri . '/assets/svg/klem-primary.svg'],
        ],
    ];

    if ($badge) {
        $data['articleSection'] = $badge['name'];
    }

    echo '<script type="application/ld+json">' . wp_json_encode($data, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES) . '</script>' . "\n";
}
add_action('wp_head', 'klem_seo_article_schema', 3);

/**
 * JSON-LD BreadcrumbList : fil d'ariane du hub Actualités et des articles.
 */
function klem_seo_breadcrumbs_schema(): void {
    if (is_singular('post')) {
        $items = [
            ['name' => __('Accueil', 'klem-theme'), 'url' => home_url('/')],
            ['name' => __('Actualités', 'klem-theme'), 'url' => klem_actualites_url()],
            ['name' => get_the_title(), 'url' => get_permalink()],
        ];
    } elseif (is_page('actualites')) {
        $items = [
            ['name' => __('Accueil', 'klem-theme'), 'url' => home_url('/')],
            ['name' => __('Actualités', 'klem-theme'), 'url' => klem_actualites_url()],
        ];
    } else {
        return;
    }

    $list_items = [];
    foreach ($items as $position => $item) {
        $list_items[] = [
            '@type'    => 'ListItem',
            'position' => $position + 1,
            'name'     => $item['name'],
            'item'     => $item['url'],
        ];
    }

    $data = [
        '@context'        => 'https://schema.org',
        '@type'           => 'BreadcrumbList',
        'itemListElement' => $list_items,
    ];

    echo '<script type="application/ld+json">' . wp_json_encode($data, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES) . '</script>' . "\n";
}
add_action('wp_head', 'klem_seo_breadcrumbs_schema', 4);
