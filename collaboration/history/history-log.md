# Historique des Sessions de Travail — Site KLEM Technologies

> Chronologie des tâches exécutées, fichiers modifiés et état du projet à chaque clôture de session.

---

## Session 09 — 2026-06-26

**Objectif :** Résoudre le chevauchement des cartes hero (mobile + desktop), adopter un design hero propre (référence image #60), réduire l'espace entre Services et À Propos.

### Tâches réalisées

#### 1. Refonte hero — suppression overlay/cartes → design "propre"
- Suppression overlay sombre `rgba(19,41,75,0.65)`, cartes flottantes (4 cartes) et halo orange
- Clip-path diagonal porté de `8%` à `20%` (plus prononcé, fidèle à la référence image fournie)
- Image : `lg:self-stretch` — s'étire à la hauteur naturelle de la colonne texte via CSS Grid
- Mobile : `min-height: 260px` conservé en inline style
- Suppression du JS `data-hero-panel` (gestion min-height dynamique devenue inutile)

#### 2. Ajout de 3 cartes métriques repositionnées
- Zigzag : Card 1 haut-gauche (`top:8%;left:26%`) · Card 2 milieu-droit (`top:40%;right:5%`) · Card 3 bas-gauche (`bottom:8%;left:12%`)
- Overlay léger réintroduit `rgba(10,20,45,0.45)` pour lisibilité des cartes
- Toutes les positions et transparences des cartes en **inline style** (résistance au cache LiteSpeed Hostinger)
- Fond cartes : `rgba(255,255,255,0.06)` · Bordure : `rgba(255,255,255,0.14)`

#### 3. Fix débordement carte 1 (clip-path)
- Card 1 à `left:8%` était clippée par le clip-path diagonal (visible à 20% en haut)
- Correction : `left:8%` → `left:26%` (marge de 6% au-delà de la frontière diagonale)
- Card 3 : `left:8%` → `left:12%` pour dégager les coins arrondis `rounded-2xl` sur mobile

#### 4. Gestion responsive des cartes (mobile vs desktop)
- **Problème** : 3 cartes × ~85px dans un panneau de 260px → chevauchement inévitable sur petit écran
- **Solution** : Card 2 (Apps Sur-Mesure) masquée sur mobile (`hidden lg:block`)
- **Mobile** : Card 1 (top 8%) + Card 3 (bottom 8%) → gap calculé ≈ 48px, zéro chevauchement
- **Desktop** : 3 cartes visibles

#### 5. Réduction espace entre sections Services et À Propos
- Espace excessif (~300px) identifié par capture d'écran annotée par l'utilisateur
- Section Services : `py-24 lg:py-32` → `pt-16 pb-10 lg:pt-24 lg:pb-14`
- Bloc CTA Services : `mt-20 pt-16` → `mt-12 pt-8`
- Section À Propos : `py-24` → `pt-12 pb-16`
- Espace résiduel : ~90px (raisonnable)

### Fichiers modifiés
| Fichier | Action |
|---|---|
| `template-parts/home/hero.php` | Refonte complète : suppression 4 cartes/overlay → 3 cartes inline-style, clip-path 20%, self-stretch |
| `src/main.js` | Suppression JS `data-hero-panel` min-height |
| `template-parts/home/services.php` | Réduction padding bas + CTA padding |
| `template-parts/home/about.php` | Réduction padding haut |
| `dist/` | 6 builds successifs committés |

### Commits de la session
| Hash | Description |
|---|---|
| `59616c0` | redesign(hero): image propre sans cartes, style référence |
| `c1d4bee` | feat(hero): 3 cartes en zigzag (positions inline style) |
| `f25107a` | fix(hero): correction débordement carte 1 + transparence augmentée |
| `e21d6b2` | fix(spacing): réduction espace Services → À Propos |
| `71489d2` | fix(hero/mobile): masquer cartes 2 et 3 sur mobile |
| `30c1d42` | feat(hero/mobile): afficher 2 cartes sur mobile |

### État du projet en clôture
- Hero desktop : image propre clip-path 20°, 3 cartes zigzag, overlay léger, hauteur auto via grid
- Hero mobile : 2 cartes (Pipeline Big Data + Disponibilité), zéro chevauchement
- Inter-sections : espacement naturel, plus de vide excessif entre Services et À Propos
- Toutes les valeurs critiques (positions, couleurs) en inline style — résistance au cache Hostinger

---

## Session 08 — 2026-06-26

**Objectif :** Déploiement complet sur Hostinger + ajustements visuels post-déploiement (polices, hero, logo, boutons).

### Tâches réalisées

#### 1. Déploiement production Hostinger
- **DB** : import `klem_production.sql` → `u987520216_KLEM_BD` (fix : `grep -v '^mysqldump:'` pour filtrer les warnings mélangés dans le dump)
- **Credentials** : `.env` sur le serveur mis à jour (`DB_NAME=u987520216_KLEM_BD`, `DB_USER=u987520216_KLEM`, `DB_PASSWORD=I@ndI2905`, URLs `https://www.klemtech.net`)
- **URLs DB** : `UPDATE klem_options SET option_value='https://www.klemtech.net'` sur `siteurl` et `home`
- **Composer** : `composer install --no-dev --optimize-autoloader` sur le serveur — vendor OK
- **Document root** : `~/domains/klemtech.net/public_html` → symlink vers `~/site-klem/web` (déjà en place)
- **PHP** : 8.2.30 — compatible Bedrock ✅

#### 2. Fix critique : assets `dist/` non déployés
- **Problème** : `dist/` dans `.gitignore` → `manifest.json` absent sur le serveur → CSS/JS non chargés
- **Solution** : décommenter `dist/` dans `.gitignore`, commiter les assets compilés
- Site s'affichait en HTML brut (sans styles) avant ce fix

#### 3. Refonte hero — grille 2 colonnes contenue
- Passage de panneau droit `absolute` bord-à-bord à une grille CSS 2 colonnes dans `max-w-6xl`
- Image droite avec `clip-path` diagonal contenu dans le container (style veone.net)
- Réduction gap + extension image gauche (`-ml-6`) pour supprimer l'espace blanc entre texte et image

#### 4. Ajustements typographiques et layout
- Hero H1 : `text-7xl` → `text-5xl` (desktop)
- Section H2 : `text-4xl/5xl` → `text-2xl/3xl` sur toutes les sections
- Container : `max-w-7xl` → `max-w-6xl` + padding responsive `px-4 sm:px-6 lg:px-8`
- Logo chevron : `width="44" height="36"` → `width="32" height="26"`
- Logo KLEM wordmark : `text-[38px]` → `text-[26px]` (desktop)

#### 5. Hero — 4 cartes métriques avec animation ping
- Passage de 3 cartes staggerées à 4 cartes (ajout "Apps Sur-Mesure" — point bleu)
- Animation `animate-ping` (effet radar) remplace `animate-pulse` — délais décalés 0 / 0.3s / 0.6s / 0.9s
- Positionnement absolu libre : haut-gauche, haut-droite, centre-gauche, bas-droite
- Transparence augmentée : `bg-white/10` → `bg-white/5`, `border-white/20` → `border-white/15`

#### 6. Boutons et numéro de téléphone
- Boutons : `justify-center` + `px-5 py-2` + `text-sm` (texte centré, taille réduite)
- Numéro reformaté : `+225 0758892477` → `(+225) 07 58 89 24 77`
- Numéro : `font-extrabold text-sm` → `font-medium text-xs`

### Fichiers modifiés
| Fichier | Action |
|---|---|
| `.gitignore` | `dist/` décommenté — assets committés pour hébergement mutualisé |
| `header.php` | Chevron réduit, KLEM wordmark réduit, bouton centré, numéro reformaté |
| `template-parts/home/hero.php` | Restructuration grille 2 col, 4 cartes, ping animation, boutons |
| `template-parts/home/services.php` | Container max-w-6xl + H2 réduit |
| `template-parts/home/about.php` | Container max-w-6xl + H2 réduit |
| `template-parts/home/clients.php` | Container max-w-6xl + H2 réduit |
| `template-parts/home/contact.php` | Container max-w-6xl + H2 réduit |
| `dist/` (assets compilés) | 8 builds successifs committés avec les changements visuels |

### État du projet en clôture
- Site en production sur `https://www.klemtech.net` — fonctionnel ✅
- Design ajusté : proportions veone-style, polices calibrées, hero contenu dans son container
- Workflow de déploiement établi : `pnpm build` → `git push` → `git pull` sur le serveur

---

## Session 07 — 2026-06-26

**Objectif :** Finaliser les coordonnées, redesign section services, repositionnement honnête "Cas Clients", logo et responsive.

### Tâches réalisées

#### 1. Finalisation coordonnées officielles KLEM
- `footer.php` : copyright `Copyright © KLEM 2026 – Tous droits réservés.`, liens légaux `Termes et conditions` + `Politique de confidentialité` (suppression CGU/Mentions légales)
- `header.php` : téléphone `+225 0758892477` (format sans espaces)
- `contact.php` : placeholder téléphone `+225 XX XX XX XX`

#### 2. Section Services — redesign complet (2 itérations)
- **v1** : remplacement des photos sombres par 4 illustrations SVG inline (circuit data, browser code, réseau, rack)
- **v2 (final)** : inspiration modèle de référence — layout icône illustrée + texte, fond blanc, sans cartes encadrées
  - 4 icônes SVG 2 couleurs strictes (`#13294B` + `#E42313`) : cylindre DB, moniteur `</>`, camion GPS, rack serveur
  - Grille 4 cols avec trait supérieur rouge au hover, numéro + titre + desc + lien

#### 3. Section "Cas Clients" → "Ce qui nous distingue"
- Remplacement des 3 faux témoignages clients (noms entreprises fictifs) par 3 piliers différenciateurs
- Piliers : Expertise Technique / Rigueur & Transparence / Ancrage Africain
- Icônes rouge dans badge, badge thématique en bas de chaque carte
- Label navigation : `Cas Clients` → `Notre Différence`

#### 4. Logo — espacement et position du chevron
- `gap-4` → `gap-2` sur le conteneur logo (rapprochement chevron/wordmark)
- Chevron : `class="-mt-2"` (légère remontée pour aligner visuellement avec "KLEM")
- KLEM wordmark : `text-[24px] sm:text-[32px] lg:text-[38px]` (responsive)

#### 5. Responsive
- Hero : `min-h-[480px] sm:min-h-[560px] lg:min-h-[620px]` (adapté mobile)
- Padding hero : `py-8 sm:py-10 lg:py-16` (respiration mobile)

### Fichiers modifiés
| Fichier | Action |
|---|---|
| `header.php` | Logo gap + chevron `-mt-2` + KLEM responsive + menu "Notre Différence" + tel format |
| `footer.php` | Copyright 2026, liens légaux, coordonnées complètes, site web |
| `template-parts/home/contact.php` | Coordonnées + placeholder tel |
| `template-parts/home/services.php` | Redesign complet — icônes 2 couleurs |
| `template-parts/home/clients.php` | "Ce qui nous distingue" — 3 piliers sans faux clients |
| `template-parts/home/hero.php` | Responsive min-h + padding |

### État du projet en clôture
- Site visuellement complet, coordonnées à jour, aucun faux client affiché
- Responsive amélioré (mobile, tablette, desktop)
- Prêt pour déploiement Vercel

---

## Session 06 — 2026-06-26

**Objectif :** Intégrer le logo officiel KLEM depuis Claude Design, harmoniser les couleurs de la charte graphique, centrer les cartes du hero.

### Tâches réalisées

#### 1. Logo intermédiaire — Sphère neurale 3D (itération)
- L'utilisateur a fourni son logo existant : "KLEM" navy + icône cerveau orange
- Redesign expert : sphère 3D avec `radialGradient` 3 stops (highlight `#FFAA5E` → rouge `#E5391E` → ombre `#6A0F08`), réseau neuronal blanc (6 nœuds + 10 connexions), reflet spéculaire, `feDropShadow`
- Intégration dans `header.php` et `footer.php` avec IDs SVG isolés (`kmh-*` / `kmf-*`)

#### 2. Harmonisation couleurs KLEM officielles
- Code couleur KLEM fourni par le client : **BLEU `#271C70`** / **ROUGE `#E42313`**
- `tailwind.config.js` : `klem-blue → #271C70`, `klem-orange → #E42313` (alias rouge), `klem-red → #E42313`
- Impact automatique sur tout le site (hero, sections sombres, CTA, footer) via classes Tailwind

#### 3. Import logo final depuis Claude Design — ChevronMark
- Source : `claude.ai/design/p/a2cd3486` — fichier `KLEM Logo - Chevron.dc.html`
- **ChevronMark** (viewBox `0 0 54 44`) : double chevron 3D avec 3 couches :
  - Ombre extrusion `#A5130A` (`translate(1.4, 1.7)`)
  - Face principale `#E42313`
  - Bevel supérieur `#F0654F`
- **Police Archivo** (Google Fonts) : poids 800 wordmark, 600 tagline, tracking `-0.02em`
- **klem-blue → `#13294B`** (couleur exacte du design spec, remplace `#271C70`)
- Tagline : `uppercase`, `letter-spacing: 0.23em`, `color: #13294B` (light) / `#c3c9d6` (dark)
- `functions.php` : ajout enqueue Google Fonts Archivo
- Tous les SVG assets mis à jour (`klem-primary`, `klem-mono-ink`, `klem-mono-white`, `klem-symbole-rouge`)

#### 4. Centrage des cartes métriques dans le hero
- **Problème :** cartes positionnées avec `left-[22%]` et `left-[28%]` → collées au bord diagonal du clip-path
- **Solution :** remplacement par un container `flex flex-col justify-center` avec `pl-[18%]` (dégage le clip) et alternance `self-start` / `self-end` pour le rythme visuel
- Résultat : groupe centré verticalement, zigzag gauche-droite lisible

#### 5. Build de production et vérification
- `pnpm build` → ✅ 0 erreur — `main-CuRtevpM.css` (26.65 kB / 5.54 kB gzip)
- Screenshots Playwright : header, hero, panneau visuel — conformité design vérifiée

### Fichiers modifiés / créés
| Fichier | Action |
|---|---|
| `tailwind.config.js` | Couleurs mises à jour (×3 itérations), police `logo` → Archivo |
| `functions.php` | Ajout `klem_enqueue_fonts()` — Google Fonts Archivo |
| `header.php` | Logo → ChevronMark 3D + Archivo 800 + `#13294B` |
| `footer.php` | Logo → ChevronMark 3D + Archivo 800 + blanc |
| `template-parts/home/hero.php` | Cartes métriques centrées via flex layout |
| `assets/svg/klem-primary.svg` | Mis à jour — ChevronMark + Archivo |
| `assets/svg/klem-mono-ink.svg` | Mis à jour |
| `assets/svg/klem-mono-white.svg` | Mis à jour |
| `assets/svg/klem-symbole-rouge.svg` | Mis à jour |

### État du projet en clôture
- Logo officiel KLEM ChevronMark 3D intégré sur tout le site (header + footer)
- Charte graphique entièrement harmonisée : `#13294B` / `#E42313`
- Cartes hero correctement centrées dans la zone visible
- Tous les commits pushés sur GitHub

---

## Session 05 — 2026-06-25

**Objectif :** Implémenter le nouveau système de logo KLEM importé depuis Claude Design.

### Tâches réalisées

#### 1. Import design depuis Claude Design (project `3006843e`)
- Fichier : `Klem Logo System.dc.html`
- Lecture via DesignSync MCP : KlemMark (2 chevrons), couleurs, typographie, variantes

#### 2. Mise à jour du système de design Tailwind
- `klem-blue : #16212E` (encre), ajout `klem-red : #E2241B` (symbole), `klem-slate : #5A6B7B` (tagline)
- `font-logo` : passage de Space Grotesk à **Verdana** (police système — suppression import Google Fonts)
- Suppression du `wp_enqueue_style` Space Grotesk dans `functions.php`

#### 3. Remplacement du logo dans header + footer
- **KlemMark** : 2 polygones SVG (viewBox `0 0 100 100`) en rouge `#E2241B`
- **Wordmark** : Verdana Bold, gradient `#F07A1E → #8A3C12` (`bg-clip-text`), tagline en `klem-slate`
- Variante footer : KLEM en blanc, tagline `white/40`

#### 4. Création des assets SVG
- `assets/svg/klem-primary.svg` — logo horizontal complet
- `assets/svg/klem-symbole-rouge.svg` — symbole seul
- `assets/svg/klem-mono-ink.svg` — version encre
- `assets/svg/klem-mono-white.svg` — version blanche

### Fichiers modifiés / créés
| Fichier | Action |
|---|---|
| `tailwind.config.js` | Couleurs + police logo |
| `functions.php` | Suppression Space Grotesk |
| `header.php` | Nouveau logo KlemMark 2 chevrons |
| `footer.php` | Variante fond sombre |
| `assets/svg/` (4 fichiers) | Créés |

---

## Session 04 — 2026-06-25

**Objectif :** Implémenter l'envoi d'emails via API REST Brevo et sécuriser les secrets hors du dépôt Git.

### Tâches réalisées

#### 1. Réécriture du mu-plugin en API REST (`web/app/mu-plugins/klem-smtp.php` v2)
- Remplacement du hook `phpmailer_init` par le filtre `pre_wp_mail` (court-circuite PHPMailer entièrement)
- `wp_remote_post()` vers `https://api.brevo.com/v3/smtp/email` (port 443 — aucun blocage réseau)
- Gestion automatique HTML vs texte brut, extraction `Reply-To` depuis les headers
- Fallback propre : si `KLEM_BREVO_API_KEY` absent → PHPMailer reprend la main
- Erreurs propagées via le hook `wp_mail_failed`

#### 2. Gestion des secrets via `.env`
- Premier push bloqué par GitHub Push Protection (clé API Brevo détectée en clair dans `wp-config.php`)
- Correction : secrets déplacés dans `.env` (non commité, listé dans `.gitignore`)
- `wp-config.php` : constantes lues via `getenv()` — aucune valeur sensible en dur
- `.env.example` créé et commité comme template de documentation
- `docker-compose.yml` : ajout de `env_file: .env` pour injecter les variables dans le conteneur

#### 3. Test d'envoi réel
- Email de test envoyé à `ciyasyl@gmail.com` avec succès : **ENVOI OK ✓**
- Objet : `[TEST] KLEM Brevo API REST`

### État du projet en clôture
- Formulaire de contact 100 % opérationnel (AJAX → WordPress → Brevo API REST → email livré)
- Aucun secret dans le dépôt Git
- Architecture secrets : `.env` local + `env_file` Docker + `getenv()` dans PHP

### Fichiers modifiés / créés
| Fichier | Action |
|---|---|
| `web/app/mu-plugins/klem-smtp.php` | Réécrit v2 (SMTP → API REST) |
| `web/wp-config.php` | Secrets remplacés par `getenv()` |
| `docker-compose.yml` | Ajout `env_file: .env` |
| `.env` | Créé (non commité — secrets réels) |
| `.env.example` | Créé (commité — template documenté) |

---

## Session 03 — 2026-06-25

**Objectif :** Configurer l'envoi d'emails du formulaire de contact via SMTP Brevo.

### Tâches réalisées

#### 1. Création du mu-plugin SMTP (`web/app/mu-plugins/klem-smtp.php`)
- Hook `phpmailer_init` pour configurer PHPMailer avec Brevo (smtp-relay.brevo.com:587, STARTTLS)
- Filtres `wp_mail_from` / `wp_mail_from_name` pour l'expéditeur `contact@klem.tech`
- Chargement automatique sans activation manuelle (mu-plugin)

#### 2. Ajout des constantes SMTP dans `wp-config.php`
- Bloc `KLEM_SMTP_*` : host, port, user, pass, from, from_name
- Credentials Brevo renseignés (ciyasyl@gmail.com + clé SMTP `xsmtpsib-...`)

#### 3. Diagnostic — ports SMTP bloqués en local
- Test d'envoi via `wp_mail()` : ÉCHEC — `Could not connect to SMTP host`
- Cause identifiée : ports 25, 465, 587 tous bloqués en sortie depuis le conteneur Docker (restriction FAI / réseau local habituelle)
- L'accès internet général fonctionne (port 443 OK)

#### 4. Décision : basculer sur l'API REST Brevo
- L'API HTTP de Brevo (port 443) contourne le blocage SMTP
- Nécessite une clé API `xkeysib-...` (différente de la clé SMTP)
- **Reporté à la prochaine session** — l'utilisateur génèrera la clé API Brevo

### État du projet en clôture
- Mu-plugin SMTP en place, configuration correcte pour la production
- Envoi local non fonctionnel (blocage réseau) → à résoudre via API REST Brevo
- `wp-config.php` contient les credentials SMTP Brevo (à remplacer par clé API lors de la prochaine session)

### Fichiers modifiés / créés
| Fichier | Action |
|---|---|
| `web/app/mu-plugins/klem-smtp.php` | Créé |
| `web/wp-config.php` | Modifié (bloc KLEM_SMTP_* ajouté + credentials renseignés) |

### Prochaine étape
- Générer une clé API Brevo (`xkeysib-...`) sur app.brevo.com → SMTP & API → API Keys
- Remplacer le mu-plugin SMTP par une implémentation via `wp_remote_post()` sur l'API REST Brevo

---

## Session 02 — 2026-06-25

**Objectif :** Compléter la page d'accueil avec les sections manquantes (`#clients`, `#contact`) et rendre le formulaire fonctionnel.

### Tâches réalisées

#### 1. Audit de l'état du projet au démarrage
- Vérification : conteneurs Docker `klem_site_app` et `klem_site_db` Up (18 h de fonctionnement)
- Constat : sections `#clients` et `#contact` absentes ; navigation et CTAs pointaient vers des ancres sans cible
- Assets précédents compilés et servis correctement (`main-BHj2_j35.css`)

#### 2. Création — Section Clients / Témoignages (`#clients`)
- **Fichier :** `web/app/themes/klem-theme/template-parts/home/clients.php`
- Fond `klem-blue`, 3 cartes de témoignages avec guillemet SVG et avatar initiales orange
- Témoignages fictifs représentatifs des 4 piliers : TransAfric Logistics (FleetControl), Groupe Energis Bénin (ERP), BancFin Togo (Big Data)
- Bandeau "Secteurs couverts" (6 secteurs : Logistique, Banque, Énergie, Commerce, Administrations, Télécoms)
- Animations d'entrée `data-animate` avec délais échelonnés

#### 3. Création — Section Contact avec formulaire AJAX (`#contact`)
- **Fichier :** `web/app/themes/klem-theme/template-parts/home/contact.php`
- Layout deux colonnes : bloc d'informations à gauche (adresse, tél, email, horaires) + formulaire à droite
- Formulaire : nom, société, email, téléphone, sujet (select), message
- Nonce WordPress `wp_nonce_field('klem_contact_nonce', 'klem_nonce')`
- Bouton avec état spinner (SVG `animate-spin`) pendant la soumission

#### 4. Handler AJAX — `functions.php`
- **Fichier :** `web/app/themes/klem-theme/functions.php`
- Ajout de `klem_enqueue_ajax_config()` → `wp_localize_script` injecte `klemAjax.url` et `klemAjax.nonce`
- Ajout de `klem_handle_contact()` : vérification nonce → sanitisation de tous les champs → validation email → `wp_mail()` avec `Reply-To` → `wp_send_json_success/error`
- Actions enregistrées : `wp_ajax_klem_contact` et `wp_ajax_nopriv_klem_contact`

#### 5. JS formulaire — `src/main.js`
- Ajout du bloc "Formulaire de contact" : `fetch()` vers `klemAjax.url`, gestion états loading/success/error, reset du formulaire après succès
- Feedback visuel : div `#klem-form-feedback` avec classes Tailwind conditionnelles (`bg-green-50` / `bg-red-50`)

#### 6. Mise à jour `front-page.php`
- Ajout des deux nouveaux `get_template_part()` après la section `about`

#### 7. Build de production
- Commande : `pnpm build`
- Résultat : ✅ 0 erreur — `main-CouXfYnk.css` (26.61 kB / 5.46 kB gzip), `main-C_JiWpxX.js` (2.57 kB / 1.04 kB gzip)

#### 8. Vérification visuelle (Playwright)
- Screenshots des 5 sections via Playwright Chromium headless (1440×900)
- ✅ Toutes les sections sont rendues correctement et la charte graphique est cohérente

### État du projet en clôture
- Page d'accueil complète : Hero → Services → À Propos → Clients → Contact → Footer (bande CTA + colonnes + barre légale)
- Navigation entièrement fonctionnelle (toutes les ancres résolues)
- Formulaire de contact opérationnel côté logique (SMTP à configurer en production)
- Assets compilés et servis par WordPress

### Fichiers modifiés / créés
| Fichier | Action |
|---|---|
| `template-parts/home/clients.php` | Créé |
| `template-parts/home/contact.php` | Créé |
| `front-page.php` | Modifié (2 lignes ajoutées) |
| `functions.php` | Modifié (+2 fonctions, +2 actions AJAX) |
| `src/main.js` | Modifié (+bloc formulaire contact, ~45 lignes) |
| `collaboration/doc/ard/ADR-001` à `ADR-004` | Créés |
| `collaboration/history/decision-log.md` | Créé et rempli |
| `collaboration/history/history-log.md` | Créé et rempli |

---

## Session 01 — 2026-06-24

**Objectif :** Amorcer le projet depuis zéro : environnement Docker, installation WordPress Bedrock, scaffold complet du thème.

### Tâches réalisées

#### 1. Diagnostic de l'état initial
- `web/` vide et appartenant à `root` → permissions bloquantes pour Composer
- `composer.json` vide
- `package.json` et `turbo.json` en JSON invalide

#### 2. Script d'amorçage `bootstrap.sh`
- `chown` récursif de `web/` pour débloquer Composer
- Création automatique de tous les fichiers manquants si absents
- Exécution de `composer install` pour télécharger le core WordPress

#### 3. Infrastructure Docker
- **`Dockerfile`** : image PHP 8.2 + Apache avec extensions `mysqli`, `pdo_mysql`, activation `mod_rewrite`, `AllowOverride All` sur le vhost
- **`docker-compose.yml`** : service `app` buildé depuis le Dockerfile + `depends_on: db` ; service `db` MySQL 8.0 avec volume persistant ; mapping ports `8080:80` et `3306:3306`

#### 4. Configuration Composer
- **`composer.json`** : dépendances `johnpbloch/wordpress ^6.5`, scripts d'installation post-Composer
- Installation réussie → `web/wp/` peuplé avec WordPress 6.5+

#### 5. Configuration Bedrock WordPress
- **`web/wp-config.php`** : constantes `DB_*`, `WP_CONTENT_DIR` → `web/app/`, `WP_CONTENT_URL`, `ABSPATH` → `web/wp/`
- **`web/index.php`** : point d'entrée unique qui charge `web/wp/wp-blog-header.php`

#### 6. Scaffold complet du thème `klem-theme`
| Fichier | Contenu |
|---|---|
| `style.css` | En-tête WordPress (Theme Name, Version…) |
| `functions.php` | `klem_theme_setup()`, `klem_enqueue_assets()` avec lecture du manifest Vite |
| `header.php` | Header fixe : logo SVG 4 losanges, nav desktop avec fallback, burger mobile, menu déroulant |
| `footer.php` | Bande CTA pré-footer + footer 4 colonnes (logo, Entreprise, Services, Nous joindre) + barre copyright |
| `front-page.php` | Orchestrateur `get_template_part()` pour hero, services, about |
| `template-parts/home/hero.php` | H1 massif, panneau visuel droit clip-path diagonal, 3 cartes métriques flottantes, bande stats |
| `template-parts/home/services.php` | Grille 4 cartes services avec photo + icône SVG + hover orange |
| `template-parts/home/about.php` | Texte gauche + panneau KPIs avec barres de progression animées |
| `vite.config.js` | Entrée `src/main.js`, sortie `dist/`, génération `manifest.json` |
| `tailwind.config.js` | Couleurs `klem-blue`/`klem-orange`, font `font-logo` (Space Grotesk), safelist animations |
| `postcss.config.js` | Plugins `tailwindcss` + `autoprefixer` |
| `package.json` | `name: showcase-website`, scripts `dev`/`build`, dépendances Vite + Tailwind |
| `src/main.css` | `@tailwind base/components/utilities` + styles `.scrolled` pour shadow header |
| `src/main.js` | Menu mobile toggle, ombre header au scroll, IntersectionObserver animations, barres de progression |

#### 7. Configuration monorepo
- **`pnpm-workspace.yaml`** : déclaration du workspace `web/app/themes/klem-theme`
- **`package.json` racine** : script `build` → `turbo run build --filter=showcase-website`
- **`turbo.json`** : pipeline `build` avec outputs `dist/**`
- Correction des JSON invalides dans `package.json` et `turbo.json`

#### 8. Résultat de clôture de session
- ✅ `docker compose up` → site accessible sur `localhost:8080`
- ✅ `pnpm build` → 0 erreur, assets hashés dans `dist/`
- ✅ Thème actif dans WordPress, `front-page.php` rendu (sections Hero, Services, À Propos)

### État du projet en clôture
- Infrastructure complète et opérationnelle
- 3 sections de la page d'accueil fonctionnelles (Hero, Services, À Propos)
- Sections `#clients` et `#contact` manquantes (navigation pointant vers des ancres vides)
