# Journal des Décisions — Site KLEM Technologies

> Ce fichier trace toutes les décisions techniques et architecturales significatives prises au cours du projet.  
> Pour le détail argumenté de chaque décision, consulter les ARDs correspondants dans `doc/ard/`.

---

## [DEC-012] 2026-06-26 — Repositionnement section "Cas Clients" en "Ce qui nous distingue"

**Contexte :** KLEM est en phase de démarrage — afficher de faux témoignages clients (noms d'entreprises fictifs) nuit à la crédibilité.
**Décision :** Remplacer la section témoignages par 3 piliers différenciateurs (Expertise Technique / Rigueur & Transparence / Ancrage Africain). Navigation renommée "Notre Différence".
**Impact :** `template-parts/home/clients.php`, `header.php`
**Résultat :** Positionnement honnête adapté au stade de l'entreprise.

---

## [DEC-011] 2026-06-26 — Section Services : layout icône illustrée + texte (vs bandeaux photo)

**Contexte :** Les bandeaux photo sombres puis SVG multi-couleurs donnaient un rendu brouillon. L'utilisateur a fourni un modèle de référence à 4 colonnes icon+texte sur fond blanc.
**Décision :** Refonte en icônes SVG illustrées (72×72, palette `#13294B`+`#E42313` uniquement), grille ouverte sans cartes encadrées, trait rouge au hover.
**Impact :** `template-parts/home/services.php`
**Résultat :** Rendu professionnel, cohérent, zéro dépendance image externe.

---

## [DEC-010] 2026-06-26 — Logo ChevronMark : espacement et position responsive

**Contexte :** L'espace `gap-4` entre chevron et wordmark "KLEM" était trop large ; le chevron apparaissait trop bas par rapport au texte.
**Décision :** `gap-4` → `gap-2`, ajout `class="-mt-2"` sur le SVG (remontée visuelle de 8px), KLEM responsive `text-[24px] sm:text-[32px] lg:text-[38px]`.
**Impact :** `header.php`

---

## [DEC-009] 2026-06-26 — Centrage des cartes métriques hero via flex layout

**ARD :** —  
**Contexte :** Les cartes flottantes (Pipeline Big Data, FleetControl, Disponibilité) étaient positionnées en `absolute` avec `left-[22%]` et `left-[28%]`, les plaçant contre le bord diagonal du clip-path — résultat visuellement déséquilibré.  
**Décision :** Remplacement par un container `absolute inset-0 flex flex-col justify-center gap-5 pl-[18%]`. Chaque carte utilise `self-start` ou `self-end` pour créer un zigzag lisible. Le `pl-[18%]` dégage le clip-path diagonal (qui coupe les 10% du haut).  
**Impact :** `template-parts/home/hero.php`  
**Résultat :** Groupe de cartes verticalement centré, contenu visible sans clip.

---

## [DEC-008] 2026-06-26 — Logo officiel KLEM : ChevronMark 3D + Archivo

**ARD :** [ADR-005](../doc/ard/ADR-005-logo-chevronmark-archivo.md)  
**Contexte :** Le client a fourni la charte graphique via Claude Design (projet `a2cd3486` — `KLEM Logo - Chevron.dc.html`). Le logo définitif utilise un double chevron 3D et la police Archivo.  
**Décision :** Adopter le ChevronMark (viewBox `54×44`, 3 couches : ombre `#A5130A`, face `#E42313`, bevel `#F0654F`) et la typographie Archivo 800 (`tracking: -0.02em`) + Archivo 600 pour la tagline (`uppercase`, `tracking: 0.23em`). Couleur bleue officielle du design : `#13294B`.  
**Impact :** `header.php`, `footer.php`, `tailwind.config.js`, `functions.php`, tous les SVG assets  
**Remplace :** DEC-007 (logo 4 losanges), logo KlemMark 2 chevrons plats (Session 05), logo sphère neurale (itération Session 06)

---

## [DEC-007] 2026-06-25 — Charte couleurs KLEM officielle

**ARD :** —  
**Contexte :** Le client a communiqué les codes couleur officiels KLEM : BLEU `#271C70` / ROUGE `#E42313`. L'ancien bleu `#16212E` (navy sombre) et l'orange `#FF6500` (hérité de l'amorçage) ne correspondent pas à la charte.  
**Décision :** Mettre à jour `tailwind.config.js` : `klem-blue → #13294B` (couleur du logo design, affine le `#271C70` client), `klem-red → #E42313`, `klem-orange → #E42313` (alias rouge — aucun orange dans la charte KLEM).  
**Impact :** Tout le site via les classes Tailwind — hero, CTA, sections sombres, footer, navigation  
**Résultat :** Site entièrement en charte `#13294B` (bleu) + `#E42313` (rouge).

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
