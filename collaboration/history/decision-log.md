# Journal des Décisions — Site KLEM Technologies

> Ce fichier trace toutes les décisions techniques et architecturales significatives prises au cours du projet.  
> Pour le détail argumenté de chaque décision, consulter les ARDs correspondants dans `doc/ard/`.

---

## [DEC-027] 2026-06-30 — Anti-spam : honeypot + jeton signé + rate limit (sans CAPTCHA)

**Contexte :** Réception d'un email de prospection automatisée via le formulaire de contact (bot remplissant tous les champs visibles). Le formulaire n'avait aucune protection anti-bot.
**Décision :** Triple protection sans CAPTCHA (pas de friction pour les vrais visiteurs) :
1. **Honeypot** : champ `klem_website` caché — rejet silencieux si rempli
2. **Jeton horodaté signé** : `wp_hash($ts . 'klem_contact_token')` — rejet si < 3 s ou > 1 h ou token falsifié
3. **Rate limiting IP** : max 3 envois/heure via WordPress Transients
- Les bots bloqués reçoivent un **faux succès** pour ne pas révéler le mécanisme de détection.
**Impact :** `template-parts/home/contact.php`, `functions.php`
**Règle :** Si le spam persiste malgré ces protections, envisager reCAPTCHA v3 (invisible) ou Cloudflare Turnstile.

---

## [DEC-026] 2026-06-30 — Favicon : PNG 32×32 en priorité, SVG en fallback

**Contexte :** Le favicon SVG existait mais certains navigateurs (notamment Safari et anciens Chrome) mettent en cache l'icône aggressivement ou ne supportent pas bien les SVG favicon. Le kit branding fournit un PNG 32×32 officiel.
**Décision :** Servir le PNG `favicon-32.png` en `type="image/png" sizes="32x32"` en premier, puis le SVG en fallback. Les navigateurs modernes préfèrent SVG, les autres tombent sur le PNG.
**Impact :** `web/app/themes/klem-theme/functions.php`, `web/app/themes/klem-theme/assets/favicon-32.png`
**Règle :** Pour mettre à jour le favicon, remplacer `favicon-32.png` et/ou `favicon.svg` dans `assets/`. Pas de changement PHP nécessaire.

---

## [DEC-025] 2026-06-30 — Logo : chevrons plats SVG plutôt que ChevronMark 3D

**Contexte :** Le kit branding Facebook (Claude Design) utilise des chevrons plats 2 couleurs uniquement (`#E42313` rouge, sans bevel ni ombre). L'ancien logo 3D (3 couches SVG : ombre `#A5130A`, face `#E42313`, bevel `#F0654F`) ne correspondait plus à la charte officielle.
**Décision :** Remplacer par 2 polygones SVG plats dérivés du `clip-path:polygon(0 0,55% 0,100% 50%,55% 100%,0 100%,45% 50%)` du kit. ViewBox `0 0 65 56`. Suppression totale des groupes bevel/ombre.
**Impact :** `header.php`, `footer.php`, `assets/favicon.svg`
**Avantage :** Cohérence parfaite entre logo site, favicon et kit branding Facebook/LinkedIn/X.

---

## [DEC-024] 2026-06-29 — Réseaux sociaux : URLs directes en dur dans footer.php

**Contexte :** Les 4 icônes sociales du footer pointaient vers `#`. Les comptes LinkedIn, X/Twitter, Facebook et GitHub ont été créés et leurs URLs intégrées.
**Décision :** URLs stockées directement dans `footer.php` (pas de champ WordPress admin). Ce sont des constantes métier qui changent rarement.
- LinkedIn : `https://www.linkedin.com/company/130474992/`
- X/Twitter : `https://x.com/KLEMTechnology`
- Facebook : `https://www.facebook.com/profile.php?id=61591353966112`
- GitHub : `https://github.com/yacoubasylla/klem-opensource`
**Impact :** `web/app/themes/klem-theme/footer.php`
**Note :** LinkedIn et Facebook ont des URLs numériques (pas encore de slug personnalisé). À mettre à jour si un nom d'utilisateur propre est défini sur chaque plateforme.

---

## [DEC-023] 2026-06-27 — Authentification domaine Brevo : DKIM + SPF + DMARC sur klemtech.net

**Contexte :** Après activation du sender `infos@klemtech.net`, Brevo signalait DKIM "Par défaut" et DMARC "rua manquante" — risque de délivrabilité réduite (spam).
**Décision :** Configuration complète des enregistrements DNS d'authentification email sur `klemtech.net` :
- SPF étendu : `include:spf.brevo.com` ajouté à l'enregistrement existant Hostinger
- DKIM : 2 enregistrements CNAME Brevo (`brevo1._domainkey`, `brevo2._domainkey`)
- DMARC : `v=DMARC1; p=none; rua=mailto:rua@dmarc.brevo.com` (mode monitoring, non-rejet)
- Code vérification Brevo : TXT `brevo-code:54ba159c10b0deab8dd7851ddaf47571`
**Résultat :** Domaine authentifié le 2026-06-27 — tous les emails KLEM sont désormais signés DKIM et conformes SPF + DMARC.
**Impact :** Zone DNS Hostinger (hPanel), compte Brevo
**Règle :** Si la clé API Brevo est régénérée, relancer `PUT /v3/senders/domains/klemtech.net/authenticate` pour maintenir l'authentification.

---

## [DEC-022] 2026-06-27 — Formulaire de contact : 3 destinataires fixes plutôt que admin_email

**Contexte :** Le formulaire envoyait uniquement à `get_option('admin_email')`. Le client veut recevoir les demandes sur 3 boîtes : email pro, email personnel KLEM et Gmail de backup.
**Décision :** Tableau statique `['infos@klemtech.net', 'yacouba.sylla@klemtech.net', 'ciyasyl@gmail.com']` comme `$to` dans `wp_mail()`. Pas de configuration dynamique via l'admin WordPress — les destinataires sont des constantes métier.
**Impact :** `web/app/themes/klem-theme/functions.php`
**Règle :** Toute modification des destinataires se fait directement dans ce tableau dans `functions.php`.

---

## [DEC-021] 2026-06-27 — Email expéditeur officiel : infos@klemtech.net (vérifié Brevo)

**Contexte :** `ciyasyl@gmail.com` était un sender temporaire. `infos@klemtech.net` a été créé sur Hostinger et vérifié dans Brevo (OTP 406813).
**Décision :** `KLEM_SMTP_FROM=infos@klemtech.net` dans `.env`, `.env.example` et fallback `wp-config.php`. Le formulaire de contact envoie désormais depuis cette adresse professionnelle.
**Impact :** `.env`, `.env.example`, `web/wp-config.php`, `~/site-klem/.env` sur Hostinger
**À faire (délivrabilité) :** Ajouter `include:spf.brevo.com` au TXT SPF + configurer DKIM personnalisé + DMARC `rua`.

---

## [DEC-020] 2026-06-27 — Clé API Brevo : REST (`xkeysib-`) uniquement, pas SMTP (`xsmtpsib-`)

**Contexte :** La clé sur le serveur Hostinger était corrompue : `xkeysib-xsmtpsib-6cd2722d...` — une ancienne clé SMTP avait été mélangée avec la clé REST, rendant les deux inutilisables.
**Décision :** Seule la clé REST API (`xkeysib-...`) est utilisée dans `klem-smtp.php` (via `wp_remote_post` sur `api.brevo.com/v3/smtp/email`). Ne jamais mélanger les deux formats. En cas de doute, régénérer une nouvelle clé REST sur app.brevo.com → Settings → API Keys.
**Impact :** `web/app/mu-plugins/klem-smtp.php`, `.env` serveur

---

## [DEC-019] 2026-06-26 — Hero cards : inline style systématique pour résistance au cache Hostinger

**Contexte :** LiteSpeed Cache Hostinger sert l'ancien CSS (`main-ApUCz-B4.css`) aux visiteurs anonymes même après `git pull`. Les classes Tailwind arbitraires (`top-[8%]`, `bg-white/10`, `min-h-[260px]`) absentes du vieux fichier CSS ne s'appliquent pas, causant des positions incorrectes, des cartes invisibles ou chevauchées.
**Décision :** Toutes les valeurs de position (`top`, `bottom`, `left`, `right`), les dimensions (`width`, `min-height`) et les styles visuels des cartes (`background-color`, `border`) sont en **inline style** plutôt qu'en classes Tailwind arbitraires. Seuls les utilitaires de base garantis dans tout build CSS (`absolute`, `hidden`, `lg:block`, `rounded-2xl`, `backdrop-blur-md`) peuvent rester en classes.
**Impact :** `template-parts/home/hero.php`
**Règle à suivre :** Tant que l'hébergement est sur Hostinger LiteSpeed, tout positionnement précis va en inline style.

---

## [DEC-018] 2026-06-26 — Hero mobile : 2 cartes (Card 1 + Card 3) plutôt que 3

**Contexte :** Panneau image mobile `min-height: 260px`. 3 cartes × ~85px = 255px minimum, laissant 5px de gap — chevauchement systématique sur petit écran.
**Décision :** Card 2 (Apps Sur-Mesure, milieu-droit) masquée sur mobile via `hidden lg:block`. Card 1 (Pipeline Big Data, `top:8%`) + Card 3 (Disponibilité, `bottom:8%`) couvrent les extremités — gap calculé ≈ 48px, zéro chevauchement. Desktop : 3 cartes maintenues.
**Impact :** `template-parts/home/hero.php`

---

## [DEC-017] 2026-06-26 — Hero : design propre sans overlay sombre ni cartes superposées

**Contexte :** Après plusieurs itérations (4 cartes, 2 cartes mobile, cartes qui se chevauchent, overlay opaque), l'utilisateur a fourni une image de référence (site extérieur) montrant un hero 2 colonnes avec photo nette, découpe diagonale, aucune carte flottante.
**Décision :** Suppression de l'overlay sombre `rgba(19,41,75,0.65)`, du halo orange et des 4 cartes. Remplacement par 3 cartes en zigzag transparentes (`rgba(255,255,255,0.06)`) avec overlay léger `rgba(10,20,45,0.45)`. Clip-path diagonal porté à `polygon(20% 0%, 100% 0%, 100% 100%, 0% 100%)`. Image : `lg:self-stretch` — hauteur déterminée par le grid, pas par JS.
**Impact :** `template-parts/home/hero.php`, `src/main.js`

---

## [DEC-016] 2026-06-26 — Hero : grille 2 colonnes contenue vs panneau absolu bord-à-bord

**Contexte :** Le panneau droit du hero était `absolute inset-0 w-[49%]` — il s'étendait jusqu'au bord du viewport, empêchant d'avoir des marges visibles comme sur le site de référence veone.net.
**Décision :** Remplacement par une grille CSS `grid-cols-2` contenue dans `max-w-6xl mx-auto`. L'image droite est une colonne normale avec `clip-path` diagonal appliqué sur l'élément lui-même, et `lg:-ml-6` pour combler le gap.
**Impact :** `template-parts/home/hero.php`
**Résultat :** Marges gauche/droite visibles, image contenue, layout identique au modèle veone.

---

## [DEC-015] 2026-06-26 — Réduction typographie + container max-w-6xl

**Contexte :** Les polices étaient trop grandes sur tous les breakpoints (H1 hero à 72px desktop). Le container `max-w-7xl` (1280px) avec peu de padding donnait une impression d'étirement.
**Décision :** Réduction d'un step Tailwind sur tous les titres (H1 hero : `text-7xl` → `text-5xl` desktop ; H2 sections : `text-5xl` → `text-3xl`). Container `max-w-7xl` → `max-w-6xl` (1152px) avec padding responsive `px-4 sm:px-6 lg:px-8`.
**Impact :** Tous les template-parts + `header.php`

---

## [DEC-014] 2026-06-26 — Inclure `dist/` dans Git pour hébergement mutualisé

**Contexte :** Sur Hostinger (hébergement mutualisé), Node.js n'est pas disponible — `pnpm build` est impossible côté serveur. `dist/` était dans `.gitignore`, donc le `manifest.json` était absent du serveur, empêchant le chargement CSS/JS.
**Décision :** Décommenter `dist/` du `.gitignore` et commiter les assets compilés. Le build se fait en local avant chaque push.
**Règle à suivre :** Toujours lancer `pnpm build` avant `git push` pour que les assets soient à jour sur le dépôt.
**Impact :** `.gitignore`, workflow de déploiement

---

## [DEC-013] 2026-06-26 — Déploiement Hostinger via Git + symlink document root

**Contexte :** Premier déploiement sur le serveur de production `klemtech.net` (Hostinger Business Web Hosting).
**Décision :** Cloner le dépôt GitHub dans `~/site-klem/`, créer un symlink `~/domains/klemtech.net/public_html` → `~/site-klem/web`. Les mises à jour se font via `git pull` sur le serveur.
**Credentials DB production :** `DB_NAME=u987520216_KLEM_BD`, `DB_USER=u987520216_KLEM`, host=`localhost`.
**Impact :** Structure serveur, `.env` production
**Avantage :** Un seul `git pull` met à jour le site — pas de FTP, pas de rsync manuel.

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
