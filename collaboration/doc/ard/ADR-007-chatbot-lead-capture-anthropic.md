# ADR-007 — Chatbot de capture de leads : proxy AJAX natif vers l'API Anthropic

**Date :** 2026-07-14
**Statut :** Accepté
**Décideur :** Équipe KLEM / Claude Code

---

## Contexte

Le client souhaite un assistant conversationnel sur le site pour accueillir les visiteurs, qualifier leur besoin et capturer un lead (nom, email, téléphone) pour un rappel humain sous 48 h. La règle du projet interdit les plugins lourds via l'admin WordPress ; tout ajout doit être natif ou déclaré dans `composer.json`.

## Décision

Reproduire le patron déjà validé pour le formulaire de contact (ADR-003) : un proxy AJAX natif WordPress, sans plugin, avec les adaptations suivantes propres à un LLM :

- **Backend (`inc/chatbot.php`)** : action `wp_ajax_nopriv_klem_chatbot_message` qui relaie les messages vers `https://api.anthropic.com/v1/messages` via `wp_remote_post()`. Clé API et modèle (`KLEM_ANTHROPIC_API_KEY`, `KLEM_ANTHROPIC_MODEL`) lus depuis `.env` (même schéma que DEC-006).
- **System prompt externalisé** (`inc/chatbot-system-prompt.md`) : tout le workflow conversationnel (accueil → qualification → capture → conclusion) vit dans ce fichier markdown, pas dans le PHP — modifiable sans toucher au code.
- **Capture de lead sécurisée côté serveur** : le modèle utilise un tool Anthropic `capture_lead` (tool use strict), mais l'email de notification et le message de confirmation final sont construits en PHP (`klem_chatbot_notify_lead()`), jamais laissés à la seule discrétion du modèle.
- **Conversation stateless côté client** : l'historique est renvoyé à chaque requête par le navigateur (pas de session serveur), mais borné et assaini avant l'appel API (`KLEM_CHATBOT_MAX_HISTORY = 30`, `KLEM_CHATBOT_MAX_CHARS = 4000`, rôles filtrés à `user`/`assistant`).
- **Rate limiting** : 40 messages / IP / heure via WordPress Transients (même mécanisme que le formulaire de contact, DEC-027).
- **Frontend (`footer.php` + `src/main.js`)** : widget flottant (bouton + panneau), `fetch()` vers `admin-ajax.php`, historique conversationnel maintenu en mémoire JS.

```php
if (!KLEM_ANTHROPIC_API_KEY) {
    error_log('[KLEM Chatbot] KLEM_ANTHROPIC_API_KEY manquante ou vide — vérifier le .env serveur');
    wp_send_json_error(['message' => __('Le chatbot est momentanément indisponible.', 'klem-theme')], 503);
}
```

## Alternatives considérées

| Option | Raison du rejet |
|---|---|
| Widget tiers (Intercom, Tidio, Crisp...) | Coût récurrent, dépendance externe, ne connaît pas le catalogue KLEM sans configuration lourde |
| Appel direct navigateur → API Anthropic | Exposerait la clé API côté client — inacceptable |
| Session serveur (transient PHP) pour l'historique | Complexité inutile pour un MVP ; le navigateur suffit tant que l'historique est assaini et borné à chaque requête |

## Conséquences

- ✅ Zéro dépendance externe, cohérent avec le reste du site (même patron que le formulaire de contact)
- ✅ Clé API jamais exposée côté client
- ✅ Le modèle ne peut jamais déclencher lui-même l'envoi d'email — seul le PHP valide et notifie
- ⚠️ Dépendance stricte au `.env` de production : si `KLEM_ANTHROPIC_API_KEY` est absente sur le serveur Hostinger (`~/site-klem/.env`), le chatbot répond 503 « momentanément indisponible » sans casser le reste du site
- ⚠️ Coût par appel API (facturation Anthropic à l'usage) — pas de mise en cache des réponses
