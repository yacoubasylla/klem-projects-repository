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

    $to      = get_option('admin_email');
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
