<?php

declare(strict_types=1);

if (!defined('ABSPATH')) {
    exit;
}

/**
 * ── Chatbot de capture de leads (API Messages Anthropic) ─────────────────────
 * Le workflow (accueil, qualification, capture, conclusion) est entièrement
 * porté par le system prompt dans chatbot-system-prompt.md — ce fichier ne
 * fait qu'acheminer les messages et sécuriser la capture du lead côté serveur
 * (l'outil capture_lead n'est jamais laissé à la seule discrétion du modèle :
 * l'e-mail de notification et le message de confirmation sont construits en PHP).
 */

const KLEM_CHATBOT_MAX_HISTORY  = 30;
const KLEM_CHATBOT_MAX_CHARS    = 4000;
const KLEM_CHATBOT_RATE_LIMIT   = 40; // messages / IP / heure

function klem_chatbot_system_prompt(): string {
    static $prompt = null;

    if ($prompt === null) {
        $path   = get_template_directory() . '/inc/chatbot-system-prompt.md';
        $prompt = file_exists($path) ? (string) file_get_contents($path) : '';
    }

    return $prompt;
}

function klem_chatbot_tools(): array {
    return [[
        'type'        => 'custom',
        'name'        => 'capture_lead',
        'description' => "Enregistre le lead une fois que le prénom et nom, l'e-mail et le téléphone du visiteur ont été obtenus (étape 3 du workflow). Le nom de l'entreprise est optionnel. N'appelle cet outil qu'une seule fois, après avoir recueilli ces informations.",
        'strict'      => true,
        'input_schema' => [
            'type'       => 'object',
            'properties' => [
                'nom_complet' => ['type' => 'string', 'description' => 'Prénom et nom du visiteur'],
                'email'       => ['type' => 'string', 'description' => "Adresse e-mail du visiteur"],
                'telephone'   => ['type' => 'string', 'description' => 'Numéro de téléphone du visiteur'],
                'entreprise'  => ['type' => 'string', 'description' => "Nom de l'entreprise du visiteur, chaîne vide si non fourni"],
                'besoin'      => ['type' => 'string', 'description' => 'Résumé en une phrase du besoin exprimé par le visiteur'],
            ],
            'required'             => ['nom_complet', 'email', 'telephone', 'entreprise', 'besoin'],
            'additionalProperties' => false,
        ],
    ]];
}

function klem_enqueue_chatbot_config(): void {
    wp_localize_script('klem-script', 'klemChatbotAjax', [
        'url'   => admin_url('admin-ajax.php'),
        'nonce' => wp_create_nonce('klem_chatbot_nonce'),
    ]);
}
add_action('wp_enqueue_scripts', 'klem_enqueue_chatbot_config', 21);

/**
 * Ne garde que les tours user/assistant valides envoyés par le client, en
 * bornant leur nombre et leur longueur — la conversation transite par le
 * navigateur (API Anthropic sans état) mais ne doit jamais être injectée
 * telle quelle dans la requête sortante.
 */
function klem_chatbot_sanitize_messages(array $raw): array {
    $messages = [];

    foreach (array_slice($raw, -KLEM_CHATBOT_MAX_HISTORY) as $entry) {
        if (!is_array($entry)) {
            continue;
        }

        $role    = sanitize_key($entry['role'] ?? '');
        $content = sanitize_textarea_field((string) ($entry['content'] ?? ''));
        $content = mb_substr($content, 0, KLEM_CHATBOT_MAX_CHARS);

        if (!in_array($role, ['user', 'assistant'], true) || $content === '') {
            continue;
        }

        $messages[] = ['role' => $role, 'content' => $content];
    }

    return $messages;
}

function klem_chatbot_notify_lead(array $lead): void {
    $to = ['infos@klemtech.net', 'yacouba.sylla@klemtech.net', 'ciyasyl@gmail.com'];
    $headers = ['Content-Type: text/plain; charset=UTF-8'];
    if (is_email($lead['email'])) {
        $headers[] = sprintf('Reply-To: %s <%s>', $lead['nom_complet'], $lead['email']);
    }

    $body = sprintf(
        "Nom : %s\nSociété : %s\nE-mail : %s\nTéléphone : %s\nBesoin exprimé :\n%s",
        $lead['nom_complet'],
        $lead['entreprise'] !== '' ? $lead['entreprise'] : '—',
        $lead['email'],
        $lead['telephone'],
        $lead['besoin']
    );

    wp_mail($to, '[KLEM] Nouveau lead — Chatbot', $body, $headers);
}

function klem_handle_chatbot_message(): void {
    if (!check_ajax_referer('klem_chatbot_nonce', 'nonce', false)) {
        wp_send_json_error(['message' => __('Requête non autorisée.', 'klem-theme')], 403);
    }

    $ip   = sanitize_text_field(wp_unslash($_SERVER['REMOTE_ADDR'] ?? ''));
    $key  = 'klem_chat_rate_' . md5($ip);
    $hits = (int) get_transient($key);
    if ($hits >= KLEM_CHATBOT_RATE_LIMIT) {
        wp_send_json_error(['message' => __('Trop de messages envoyés. Merci de réessayer plus tard.', 'klem-theme')], 429);
    }
    set_transient($key, $hits + 1, HOUR_IN_SECONDS);

    if (!KLEM_ANTHROPIC_API_KEY) {
        error_log('[KLEM Chatbot] KLEM_ANTHROPIC_API_KEY manquante ou vide — vérifier le .env serveur');
        wp_send_json_error(['message' => __('Le chatbot est momentanément indisponible.', 'klem-theme')], 503);
    }

    $raw = json_decode((string) wp_unslash($_POST['messages'] ?? '[]'), true);
    if (!is_array($raw)) {
        wp_send_json_error(['message' => __('Requête invalide.', 'klem-theme')], 422);
    }

    $messages = klem_chatbot_sanitize_messages($raw);
    if (empty($messages) || end($messages)['role'] !== 'user') {
        wp_send_json_error(['message' => __('Requête invalide.', 'klem-theme')], 422);
    }

    $response = wp_remote_post('https://api.anthropic.com/v1/messages', [
        'timeout' => 25,
        'headers' => [
            'x-api-key'         => KLEM_ANTHROPIC_API_KEY,
            'anthropic-version' => '2023-06-01',
            'content-type'      => 'application/json',
        ],
        'body' => wp_json_encode([
            'model'      => KLEM_ANTHROPIC_MODEL,
            'max_tokens' => 1024,
            'system'     => klem_chatbot_system_prompt(),
            'tools'      => klem_chatbot_tools(),
            'messages'   => $messages,
        ]),
    ]);

    if (is_wp_error($response)) {
        error_log('[KLEM Chatbot] Erreur réseau : ' . $response->get_error_message());
        wp_send_json_error(['message' => __('Une erreur est survenue. Merci de réessayer.', 'klem-theme')], 500);
    }

    $code = wp_remote_retrieve_response_code($response);
    $data = json_decode(wp_remote_retrieve_body($response), true);

    if ($code < 200 || $code >= 300 || !is_array($data)) {
        error_log(sprintf('[KLEM Chatbot] API Anthropic erreur %d : %s', $code, wp_remote_retrieve_body($response)));
        wp_send_json_error(['message' => __('Une erreur est survenue. Merci de réessayer.', 'klem-theme')], 500);
    }

    if (($data['stop_reason'] ?? '') === 'refusal') {
        wp_send_json_success(['reply' => __("Je ne peux pas répondre à cela, mais je reste à votre disposition pour toute question sur nos services.", 'klem-theme')]);
    }

    $reply_text  = '';
    $lead_capture = null;

    foreach ((array) ($data['content'] ?? []) as $block) {
        if (($block['type'] ?? '') === 'text') {
            $reply_text .= $block['text'];
        } elseif (($block['type'] ?? '') === 'tool_use' && ($block['name'] ?? '') === 'capture_lead') {
            $lead_capture = (array) ($block['input'] ?? []);
        }
    }

    if ($lead_capture !== null) {
        $lead = [
            'nom_complet' => sanitize_text_field((string) ($lead_capture['nom_complet'] ?? '')),
            'email'       => sanitize_email((string) ($lead_capture['email'] ?? '')),
            'telephone'   => sanitize_text_field((string) ($lead_capture['telephone'] ?? '')),
            'entreprise'  => sanitize_text_field((string) ($lead_capture['entreprise'] ?? '')),
            'besoin'      => sanitize_textarea_field((string) ($lead_capture['besoin'] ?? '')),
        ];

        if ($lead['nom_complet'] !== '' && is_email($lead['email']) && $lead['telephone'] !== '') {
            klem_chatbot_notify_lead($lead);
            wp_send_json_success([
                'reply'         => __("Merci ! Vos coordonnées ont bien été transmises à notre équipe, qui reviendra vers vous sous 48 heures. Avez-vous une autre question sur nos services en attendant ?", 'klem-theme'),
                'lead_captured' => true,
            ]);
        }
    }

    if ($reply_text === '') {
        $reply_text = __("Je n'ai pas bien compris, pourriez-vous reformuler ?", 'klem-theme');
    }

    wp_send_json_success(['reply' => $reply_text]);
}
add_action('wp_ajax_klem_chatbot_message',        'klem_handle_chatbot_message');
add_action('wp_ajax_nopriv_klem_chatbot_message', 'klem_handle_chatbot_message');
