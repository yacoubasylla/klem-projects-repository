# ADR-009 — Accès aux Cas d'usage réservé aux partenaires authentifiés

**Date :** 2026-07-20
**Statut :** Accepté
**Décideur :** Équipe KLEM / Claude Code

---

## Contexte

KLEM veut réserver ses cas d'usage détaillés (`/cas-clients/`) aux partenaires plutôt qu'à tout visiteur. Le flux demandé : un visiteur non connecté qui tente d'accéder à cette page est redirigé vers le formulaire de contact avec le sujet « Demande de partenariat » présélectionné ; KLEM traite la demande manuellement, crée le compte, et communique les identifiants par e-mail. Il n'y a donc pas d'auto-inscription — uniquement une connexion pour des comptes déjà créés côté admin.

## Décision

Authentification 100 % native WordPress, sans plugin :

- **Comptes :** créés manuellement par KLEM depuis `/wp/wp-admin/` (rôle `subscriber`), aucune auto-inscription.
- **Connexion :** page sur-mesure `page-connexion.php` (habillage Tailwind du site) qui poste vers `admin-post.php?action=klem_login` → `klem_handle_login()` dans `functions.php`, lequel appelle `wp_signon()` natif. Ce choix (plutôt que de rediriger vers `wp-login.php`) a été fait pour offrir une page de connexion à l'identité visuelle du site plutôt que l'écran d'admin WordPress générique.
- **Sécurité :** `wp_signon()` déclenche les mêmes hooks core (`authenticate`, `wp_login_failed`, `wp_login`) que `wp-login.php` — le rate limiting anti-brute-force déjà en place (`klem_login_rate_limit_key()`, 5 tentatives / 15 min) s'applique donc automatiquement, sans duplication de logique. Nonce CSRF dédié (`klem_login`) sur le formulaire.
- **Gating de la page :** `template_redirect` redirige tout visiteur non connecté depuis `/cas-clients/` vers `/?sujet=partenariat#contact`.
- **Visibilité du lien "Cas d'usage" :** masqué (menu header desktop/mobile + footer) tant que l'utilisateur n'est pas connecté — double protection avec le gating serveur (un lien caché mais non gated serait contournable ; un lien visible mais gated serait une impasse UX).
- **Formulaire de contact :** nouvelle option `partenariat` dans le select Sujet, présélectionnée via `$_GET['sujet']`, avec un bandeau contextuel expliquant la démarche.
- **Durcissement complémentaire :** `show_admin_bar` filtré à `current_user_can('manage_options')` — un compte partenaire (`subscriber`) n'a aucun usage de la barre d'admin WordPress en façade.

## Alternatives considérées

| Option | Raison du rejet |
|---|---|
| Rediriger simplement vers `wp-login.php` | Casse l'identité visuelle du site (écran d'admin WordPress nu) pour une page destinée à des partenaires externes |
| Plugin de gestion de membres (MemberPress, etc.) | Plugin lourd, interdit par les règles du projet (`composer.json` uniquement, pas de plugin non déclaré) |
| Auto-inscription en libre-service | Hors périmètre demandé : KLEM veut qualifier chaque demande de partenariat avant de créer un compte |
| Filtrer les items de menu via `wp_nav_menu_objects` | Inutile ici : `nav_menu_locations` est vide en base, la navigation passe toujours par les `fallback_cb` de `header.php`/`footer.php`, modifiés directement |

## Conséquences

- ✅ Zéro dépendance externe, zéro nouveau plugin
- ✅ Réutilise entièrement le rate limiting et le durcissement `wp-login.php` déjà en place (DEC antérieures) — aucune nouvelle surface de brute-force
- ✅ Cohérence menu ↔ accès : le lien "Cas d'usage" n'apparaît jamais sans que l'accès direct soit aussi autorisé
- ⚠️ La création de compte reste 100 % manuelle (admin WordPress) — attendu par la demande, mais à surveiller si le volume de demandes de partenariat augmente (un formulaire d'admin dédié deviendrait alors pertinent)
