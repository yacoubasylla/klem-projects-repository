# ADR-011 — Intégration Google Analytics 4 avec Consent Mode v2

**Date :** 2026-07-20
**Statut :** Accepté
**Décideur :** Équipe KLEM / Claude Code

---

## Contexte

L'utilisateur souhaite suivre les KPI du site (`www.klemtech.net`) via Google Analytics 4, avec un tableau de bord dans Google Looker Studio. Le site n'avait jusque-là aucun mécanisme de consentement cookies, alors que le thème est déjà 100 % code natif (pas d'extension lourde hors `composer.json`, cf. CLAUDE.md) et gère lui-même son SEO en PHP pur (`klem_seo_meta_tags` et consorts dans `functions.php`).

Deux questions de conception ont été posées à l'utilisateur avant l'implémentation :
1. Méthode d'intégration : tag `gtag.js` injecté directement (cohérent avec l'existant) vs Google Tag Manager (couche de gestion externe supplémentaire) → **gtag.js direct retenu**, aucune opposition exprimée sur ce point.
2. Gestion du consentement RGPD (aucun bandeau existant) : bandeau minimal + Consent Mode v2 (recommandé) vs chargement direct sans consentement vs Consent Mode v2 silencieux sans bandeau → **l'utilisateur a choisi le bandeau avec Consent Mode v2**.

## Décision

- **Propriété GA4** créée côté utilisateur (flux « KLEMTECH », `https://www.klemtech.net`, ID de mesure `G-TNR3CBT1NN`, ID de flux `15289565572`).
- **ID de mesure en variable d'environnement** `KLEM_GA_MEASUREMENT_ID`, suivant le même schéma que `KLEM_BREVO_API_KEY`/`KLEM_ANTHROPIC_API_KEY` (DEC-006) : déclarée dans `.env` (non versionné) et `.env.example` (placeholder documenté), lue via `getenv()` et exposée comme constante dans `web/wp-config.php`.
- **Tag chargé uniquement en production** (`getenv('WP_ENV') === 'production'`) — jamais en local/Docker/staging — et **jamais pour un visiteur connecté** (`is_user_logged_in()`, couvre à la fois l'administrateur et les comptes `klem_partenaire` créés en DEC-045), pour ne pas polluer les KPI avec le trafic interne.
- **Consent Mode v2** : `gtag('consent', 'default', …)` fixe `analytics_storage` à `denied` par défaut (et `ad_storage`/`ad_user_data`/`ad_personalization` à `denied` en permanence — aucun usage publicitaire prévu). L'état initial est déterminé côté PHP à partir du cookie `klem_consent` déjà présent, pour éviter tout flash de bandeau aux visiteurs qui ont déjà répondu.
- **Bandeau cookies** (`footer.php`) : rendu côté serveur uniquement quand le tag est actif (mêmes conditions que ci-dessus) et qu'aucun choix n'a encore été enregistré (`!isset($_COOKIE['klem_consent'])`). Deux boutons « Accepter » / « Refuser ».
- **Logique JS** (`src/main.js`, compilée par le pipeline Vite existant — pas de script inline en dehors du chargeur `gtag.js` lui-même, qui est un cas particulier de script de mesure tiers) : pose le cookie `klem_consent` (13 mois, recommandation CNIL, `SameSite=Lax; Secure`) et appelle `gtag('consent', 'update', { analytics_storage: 'granted' })` à l'acceptation. Le nom du cookie doit rester strictement identique entre `functions.php`, `footer.php` et `src/main.js` (documenté par commentaire aux trois endroits).

## Alternatives considérées

| Option | Raison du rejet |
|---|---|
| Google Tag Manager | Couche de gestion externe supplémentaire non nécessaire pour un seul tag GA4 ; moins cohérent avec l'approche « tout en code » déjà en place pour le SEO |
| Chargement GA direct sans bandeau de consentement | Non conforme RGPD/CNIL pour les visiteurs UE ; écarté par choix explicite de l'utilisateur |
| Consent Mode v2 silencieux (sans bandeau visible) | Zone grise juridique (mesure agrégée sans consentement explicite demandé) ; écarté au profit du bandeau explicite |
| Plugin d'analytics WordPress | Interdit par les règles du projet (composer.json uniquement) et redondant avec le code SEO déjà natif |

## Conséquences

- ✅ KPI mesurables dans GA4 puis Looker Studio, sans dépendance à un plugin
- ✅ Conforme RGPD/CNIL (consentement par défaut refusé, bandeau explicite, durée de cookie encadrée)
- ✅ Aucune donnée de trafic interne (admin/partenaires) ne pollue les statistiques
- ⚠️ Le bandeau cookies est minimal (accepter/refuser global, pas de granularité par finalité) — suffisant tant que seul GA4 est en jeu ; à revoir si un futur outil publicitaire/marketing est ajouté
- ⚠️ Procédure de création/rotation de la propriété GA4 documentée séparément dans `collaboration/doc/procedure-google-analytics-4.md` (étapes manuelles côté Google, hors du dépôt)
