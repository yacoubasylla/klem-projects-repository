# ADR-003 — Formulaire de contact : AJAX natif WordPress (sans plugin)

**Date :** 2026-06-25  
**Statut :** Accepté  
**Décideur :** Équipe KLEM / Claude Code

---

## Contexte

Le site a besoin d'un formulaire de contact fonctionnel (section `#contact`). La règle du projet interdit l'installation de plugins lourds via l'admin WordPress. Tout ajout doit être déclaré dans `composer.json`.

## Décision

Implémenter le formulaire entièrement en **natif WordPress** :

- **Frontend :** HTML form standard, soumission via `fetch()` (Fetch API) vers `admin-ajax.php`
- **Backend :** Action WordPress `wp_ajax_nopriv_klem_contact` dans `functions.php`
- **Sécurité :** Nonce WordPress (`wp_nonce_field` + `check_ajax_referer`)
- **Envoi :** `wp_mail()` avec header `Reply-To` pointant vers l'expéditeur
- **Config JS :** `wp_localize_script('klem-script', 'klemAjax', [...])` injecte l'URL AJAX et le nonce sans hardcoder de valeurs

```php
// Sanitisation systématique de chaque champ
$name    = sanitize_text_field(wp_unslash($_POST['klem_name'] ?? ''));
$email   = sanitize_email(wp_unslash($_POST['klem_email'] ?? ''));
$message = sanitize_textarea_field(wp_unslash($_POST['klem_message'] ?? ''));
```

## Alternatives considérées

| Option | Raison du rejet |
|---|---|
| Contact Form 7 | Plugin lourd, styles additionnels, surcharge le DOM ; interdit par les règles du projet |
| WPForms / Gravity Forms | Idem + licences payantes |
| Formulaire statique (mailto:) | Pas de contrôle des données, spam non filtrable |

## Conséquences

- ✅ Zéro dépendance externe, zéro plugin à maintenir
- ✅ UX fluide : spinner pendant l'envoi, feedback succès/erreur sans rechargement de page
- ✅ Sécurité : nonce anti-CSRF + sanitisation OWASP sur tous les champs
- ⚠️ `wp_mail()` dépend de la configuration SMTP du serveur ; en production, configurer un plugin SMTP minimal (ex: `wpackagist-plugin/smtp-mailer`) via `composer.json`
