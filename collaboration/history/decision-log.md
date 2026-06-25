# Journal des Décisions — Site KLEM Technologies

> Ce fichier trace toutes les décisions techniques et architecturales significatives prises au cours du projet.  
> Pour le détail argumenté de chaque décision, consulter les ARDs correspondants dans `doc/ard/`.

---

## [DEC-006] 2026-06-25 — Secrets dans `.env`, lus via `getenv()` dans `wp-config.php`

**ARD :** —  
**Contexte :** GitHub Push Protection a bloqué le push contenant la clé API Brevo committée en clair dans `wp-config.php`.  
**Décision :** Aucun secret ne réside dans le dépôt Git. Les credentials sont stockés dans `.env` (ignoré par `.gitignore`), injectés dans le conteneur Docker via `env_file: .env`, et lus dans `wp-config.php` via `getenv()`. Un fichier `.env.example` est commité comme template documenté.  
**Impact :** `wp-config.php`, `docker-compose.yml`, `.gitignore`, `.env` (non versionné), `.env.example`  
**Règle à suivre :** Toute nouvelle variable sensible (clé API, mot de passe) doit suivre ce même schéma — jamais en dur dans le code.

---

## [DEC-005] 2026-06-25 — Envoi d'emails : API REST Brevo plutôt que SMTP

**ARD :** À créer (ADR-005) lors de l'implémentation  
**Contexte :** Le SMTP Brevo (port 587) est bloqué depuis le conteneur Docker local (restriction FAI/réseau). La clé SMTP `xsmtpsib-...` est en place mais inutilisable en local.  
**Décision :** Basculer sur l'API REST Brevo (`https://api.brevo.com/v3/smtp/email`, port 443) via `wp_remote_post()` dans le mu-plugin. Contourne définitivement les blocages SMTP, fonctionne en local et en production.  
**Impact :** `web/app/mu-plugins/klem-smtp.php` (à réécrire), `web/wp-config.php` (remplacer constantes SMTP par `KLEM_BREVO_API_KEY`)  
**Statut :** ✅ Implémenté — Session 04 (2026-06-25). Test ENVOI OK.

---

## [DEC-004] 2026-06-25 — Formulaire de contact via AJAX natif WordPress

**ARD :** [ADR-003](../doc/ard/ADR-003-formulaire-contact-ajax-natif.md)  
**Contexte :** Besoin d'un formulaire de contact fonctionnel sans installer de plugin lourd.  
**Décision :** Implémentation 100 % native : `wp_ajax_nopriv_klem_contact` dans `functions.php`, nonce anti-CSRF, `sanitize_*` sur tous les champs, `wp_mail()` pour l'envoi, `fetch()` côté JS avec feedback UX.  
**Impact :** `functions.php`, `src/main.js`, `template-parts/home/contact.php`  
**Point de vigilance :** Configurer un plugin SMTP via `composer.json` en production pour garantir la délivrabilité des emails.

---

## [DEC-003] 2026-06-25 — Architecture de la page d'accueil en template parts modulaires

**ARD :** —  
**Contexte :** La page d'accueil doit couvrir 5 sections distinctes (Hero, Services, À Propos, Clients, Contact) conformément aux specs fonctionnelles et à la navigation.  
**Décision :** Chaque section est un fichier PHP indépendant dans `template-parts/home/`. `front-page.php` est réduit à un orchestrateur qui appelle `get_template_part()` pour chaque bloc.  
**Impact :** `front-page.php`, `template-parts/home/{hero,services,about,clients,contact}.php`  
**Avantage :** Chaque section est modifiable, testable et remplaçable sans toucher aux autres.

---

## [DEC-002] 2026-06-24 — Pipeline d'assets Vite 5 + Tailwind CSS v3

**ARD :** [ADR-002](../doc/ard/ADR-002-theme-vite-tailwind.md)  
**Contexte :** Besoin d'un pipeline CSS/JS moderne, performant, intégré à WordPress via `wp_enqueue_scripts`.  
**Décision :** Vite 5 avec lecture dynamique du `manifest.json` dans `functions.php`. Tailwind CSS v3 avec couleurs de marque (`klem-blue`, `klem-orange`) déclarées dans `tailwind.config.js`. Aucun style inline autorisé dans les templates PHP.  
**Impact :** `vite.config.js`, `tailwind.config.js`, `postcss.config.js`, `functions.php` (`klem_enqueue_assets`)  
**Résultat :** Bundle production : CSS 26 kB gzippé → 5.5 kB, JS 2.6 kB → 1 kB.

---

## [DEC-001] 2026-06-24 — Structure Bedrock WordPress + Docker

**ARD :** [ADR-001](../doc/ard/ADR-001-structure-bedrock-wordpress.md)  
**Contexte :** Besoin d'une installation WordPress sécurisée, versionnée, isolée via Docker.  
**Décision :** Structure Bedrock : `web/` = document root, `web/wp/` = core Composer, `web/app/` = thèmes/plugins. Image Docker custom avec `mysqli`, `mod_rewrite` et `AllowOverride All`. MySQL 8.0 sur le service `db`.  
**Impact :** `Dockerfile`, `docker-compose.yml`, `composer.json`, `web/wp-config.php`, `web/index.php`  
**Résultat :** Site opérationnel sur `localhost:8080` après `docker compose up`.
