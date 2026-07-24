# ADR-010 — Page d'administration sur-mesure pour les comptes partenaires

**Date :** 2026-07-20
**Statut :** Accepté
**Décideur :** Équipe KLEM / Claude Code

---

## Contexte

Suite à ADR-009 (accès aux Cas d'usage réservé aux partenaires authentifiés), KLEM doit pouvoir créer/modifier/supprimer les comptes partenaires suite à une demande reçue par le formulaire de contact. Deux options ont été présentées à l'utilisateur : étendre l'écran natif *Utilisateurs* de wp-admin avec un simple champ « Secteur d'activité » (recommandation initiale, zéro nouveau code d'authentification), ou construire une page d'administration dédiée avec son propre formulaire identifiant/mot de passe/e-mail/secteur d'activité. L'utilisateur a choisi la seconde option.

## Décision

Nouvel écran wp-admin autonome (`inc/partner-accounts.php`, menu « Partenaires », slug `klem-partenaires`) :

- **Rôle dédié `klem_partenaire`** (capacités identiques à `subscriber`), enregistré idempotemment via `add_role()`. Isole les comptes partenaires de tout autre compte `subscriber` éventuel — la liste et les actions de cette page ne portent jamais que sur ce rôle.
- **Comptes créés via les API natives** `wp_insert_user()` / `wp_update_user()` / `wp_delete_user()` : le hashage du mot de passe reste entièrement géré par le cœur WordPress, aucune cryptographie maison.
- **Champ métier** : « Secteur d'activité » stocké en `user_meta` (clé `klem_secteur`), liste fermée reprenant les secteurs déjà utilisés dans `page-cas-clients.php` (cohérence éditoriale).
- **Mot de passe modifiable par le partenaire** : le champ n'est pas obligatoire à l'édition (laisser vide = inchangé) ; une fois connecté, le partenaire peut le changer lui-même via `wp_lostpassword_url()` déjà branché sur `page-connexion.php`, ou son profil WordPress natif.
- **Sécurité** : `current_user_can('manage_options')` vérifié explicitement dans chaque handler `admin_post_*` (le capability check d'`add_menu_page()` ne protège que l'écran, pas les endpoints POST/GET séparés), nonce dédié par action (`klem_partner_save`, `klem_partner_delete_{ID}`), sanitisation systématique (`sanitize_user()` strict, `sanitize_email()`, `is_email()`, `validate_username()`), mot de passe minimum 8 caractères. La suppression suit le même patron que WordPress core (lien GET nonce-protégé + confirmation JS) plutôt qu'un formulaire POST, pour rester cohérent avec les écrans natifs (Utilisateurs, Plugins…).
- **UX** : bouton « Générer un mot de passe » (JS vanilla, `crypto.getRandomValues`, aucune dépendance) pour faciliter la communication du mot de passe par e-mail au partenaire après création — seul petit script propre à cet écran d'admin, enqueue conditionnellement via `admin_enqueue_scripts` (pas de passage par le pipeline Vite du thème public, qui ne couvre que le frontend).

## Alternatives considérées

| Option | Raison du rejet |
|---|---|
| Étendre l'écran natif Utilisateurs (recommandation initiale) | Écartée par choix explicite de l'utilisateur — préférence pour un écran dédié et un rendu plus maîtrisé |
| Plugin de gestion de membres | Interdit par les règles du projet (composer.json uniquement) |
| Formulaire POST + confirmation pour la suppression | Écarté au profit du patron natif WordPress (lien GET nonce-protégé), plus simple et cohérent avec le reste de wp-admin |

## Conséquences

- ✅ KLEM dispose d'un écran unique, dédié, pour tout le cycle de vie d'un compte partenaire
- ✅ Aucune réinvention de la sécurité d'authentification (API natives WordPress)
- ⚠️ Plus de code applicatif à maintenir qu'avec l'extension de l'écran natif (formulaires, validations, nonces propres à cet écran) — compromis assumé par l'utilisateur en échange d'un rendu plus habillé
