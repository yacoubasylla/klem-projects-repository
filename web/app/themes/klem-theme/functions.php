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
 * Le site est mono-page (front-page.php) : les balises sont volontairement
 * centrées sur la page d'accueil, seul point d'entrée indexable aujourd'hui.
 */

function klem_seo_description(): string {
    return __(
        "KLEM Technologies & Services, intégrateur numérique basé à Abidjan : ingénierie Big Data, applications sur-mesure, ERP et infrastructures IT pour l'Afrique de l'Ouest.",
        'klem-theme'
    );
}

function klem_seo_title(string $title): string {
    if (is_front_page()) {
        return __(
            "KLEM Technologies & Services | Intégrateur Numérique à Abidjan – Big Data, ERP & Développement Sur-Mesure",
            'klem-theme'
        );
    }
    return $title;
}
add_filter('pre_get_document_title', 'klem_seo_title');

// Déclaration de langue correcte : contenu 100 % français (corrige lang="en-US" par défaut)
add_filter('language_attributes', function (): string {
    return 'lang="fr-FR"';
});

function klem_seo_meta_tags(): void {
    if (!is_front_page()) {
        return;
    }

    $description = klem_seo_description();
    $url         = home_url('/');
    $site_name   = 'KLEM Technologies & Services';
    $theme_uri   = get_template_directory_uri();
    $image       = $theme_uri . '/assets/images/services/service-big-data.jpg';

    printf('<meta name="description" content="%s">' . "\n", esc_attr($description));
    printf('<link rel="canonical" href="%s">' . "\n", esc_url($url));

    printf('<meta property="og:type" content="website">' . "\n");
    printf('<meta property="og:site_name" content="%s">' . "\n", esc_attr($site_name));
    printf('<meta property="og:locale" content="fr_FR">' . "\n");
    printf('<meta property="og:url" content="%s">' . "\n", esc_url($url));
    printf('<meta property="og:title" content="%s">' . "\n", esc_attr($site_name . ' – Intégrateur Numérique Abidjan, Côte d\'Ivoire'));
    printf('<meta property="og:description" content="%s">' . "\n", esc_attr($description));
    printf('<meta property="og:image" content="%s">' . "\n", esc_url($image));
    printf('<meta property="og:image:width" content="800">' . "\n");
    printf('<meta property="og:image:height" content="460">' . "\n");

    printf('<meta name="twitter:card" content="summary_large_image">' . "\n");
    printf('<meta name="twitter:title" content="%s">' . "\n", esc_attr($site_name . ' – Intégrateur Numérique Abidjan, Côte d\'Ivoire'));
    printf('<meta name="twitter:description" content="%s">' . "\n", esc_attr($description));
    printf('<meta name="twitter:image" content="%s">' . "\n", esc_url($image));
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
