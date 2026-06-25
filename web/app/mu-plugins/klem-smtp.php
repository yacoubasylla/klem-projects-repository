<?php
/**
 * Plugin Name: KLEM SMTP Mailer
 * Description: Configure PHPMailer pour envoyer via Brevo SMTP. Chargé automatiquement comme mu-plugin.
 * Version: 1.0.0
 */

declare(strict_types=1);

if (!defined('ABSPATH')) {
    exit;
}

add_action('phpmailer_init', static function (PHPMailer\PHPMailer\PHPMailer $mailer): void {
    if (
        !defined('KLEM_SMTP_HOST') ||
        !defined('KLEM_SMTP_USER') ||
        !defined('KLEM_SMTP_PASS')
    ) {
        return;
    }

    $mailer->isSMTP();
    $mailer->Host       = KLEM_SMTP_HOST;
    $mailer->SMTPAuth   = true;
    $mailer->Username   = KLEM_SMTP_USER;
    $mailer->Password   = KLEM_SMTP_PASS;
    $mailer->SMTPSecure = PHPMailer\PHPMailer\PHPMailer::ENCRYPTION_STARTTLS;
    $mailer->Port       = defined('KLEM_SMTP_PORT') ? (int) KLEM_SMTP_PORT : 587;

    if (defined('KLEM_SMTP_FROM') && KLEM_SMTP_FROM) {
        $mailer->setFrom(
            KLEM_SMTP_FROM,
            defined('KLEM_SMTP_FROM_NAME') ? KLEM_SMTP_FROM_NAME : 'KLEM Technologies'
        );
    }
});

// Filtre wp_mail_from pour cohérence si KLEM_SMTP_FROM est défini
add_filter('wp_mail_from', static function (string $email): string {
    return defined('KLEM_SMTP_FROM') && KLEM_SMTP_FROM ? KLEM_SMTP_FROM : $email;
});

add_filter('wp_mail_from_name', static function (string $name): string {
    return defined('KLEM_SMTP_FROM_NAME') && KLEM_SMTP_FROM_NAME ? KLEM_SMTP_FROM_NAME : $name;
});
