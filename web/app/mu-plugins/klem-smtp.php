<?php
/**
 * Plugin Name: KLEM Brevo Mailer
 * Description: Envoie les emails via l'API REST Brevo (port 443). Remplace PHPMailer/SMTP.
 * Version: 2.0.0
 */

declare(strict_types=1);

if (!defined('ABSPATH')) {
    exit;
}

/**
 * Intercepte wp_mail() et route l'envoi vers l'API REST Brevo.
 * Retourner true/false court-circuite PHPMailer entièrement.
 */
add_filter('pre_wp_mail', static function ($return, array $atts): bool {
    if (!defined('KLEM_BREVO_API_KEY') || !KLEM_BREVO_API_KEY) {
        return false; // pas de clé → laisser PHPMailer gérer (fallback)
    }

    $to      = is_array($atts['to']) ? $atts['to'] : explode(',', $atts['to']);
    $subject = $atts['subject'];
    $message = $atts['message'];

    $recipients = [];
    foreach ($to as $email) {
        $email = trim($email);
        if (is_email($email)) {
            $recipients[] = ['email' => $email];
        }
    }

    if (empty($recipients)) {
        return false;
    }

    $from_email = defined('KLEM_SMTP_FROM') && KLEM_SMTP_FROM
        ? KLEM_SMTP_FROM
        : get_option('admin_email');

    $from_name = defined('KLEM_SMTP_FROM_NAME') && KLEM_SMTP_FROM_NAME
        ? KLEM_SMTP_FROM_NAME
        : get_option('blogname');

    // Détecter si le message est HTML
    $is_html = $message !== strip_tags($message);

    $body = [
        'sender'  => ['name' => $from_name, 'email' => $from_email],
        'to'      => $recipients,
        'subject' => $subject,
    ];

    if ($is_html) {
        $body['htmlContent'] = $message;
    } else {
        $body['textContent'] = $message;
    }

    // Extraire Reply-To des headers
    if (!empty($atts['headers'])) {
        $headers = is_array($atts['headers']) ? $atts['headers'] : explode("\n", $atts['headers']);
        foreach ($headers as $header) {
            if (stripos($header, 'Reply-To:') === 0) {
                $reply = trim(str_ireplace('Reply-To:', '', $header));
                if (is_email($reply)) {
                    $body['replyTo'] = ['email' => $reply];
                }
            }
        }
    }

    $response = wp_remote_post('https://api.brevo.com/v3/smtp/email', [
        'timeout' => 15,
        'headers' => [
            'api-key'      => KLEM_BREVO_API_KEY,
            'Content-Type' => 'application/json',
            'Accept'       => 'application/json',
        ],
        'body' => wp_json_encode($body),
    ]);

    if (is_wp_error($response)) {
        do_action('wp_mail_failed', new WP_Error(
            'klem_brevo_error',
            $response->get_error_message()
        ));
        return false;
    }

    $code = wp_remote_retrieve_response_code($response);

    if ($code < 200 || $code >= 300) {
        do_action('wp_mail_failed', new WP_Error(
            'klem_brevo_error',
            sprintf('Brevo API error %d : %s', $code, wp_remote_retrieve_body($response))
        ));
        return false;
    }

    return true;
}, 10, 2);

// Filtre expéditeur pour les cas où PHPMailer est quand même sollicité
add_filter('wp_mail_from', static function (string $email): string {
    return defined('KLEM_SMTP_FROM') && KLEM_SMTP_FROM ? KLEM_SMTP_FROM : $email;
});

add_filter('wp_mail_from_name', static function (string $name): string {
    return defined('KLEM_SMTP_FROM_NAME') && KLEM_SMTP_FROM_NAME ? KLEM_SMTP_FROM_NAME : $name;
});
