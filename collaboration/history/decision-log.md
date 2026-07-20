# Journal des Décisions — Site KLEM Technologies

> Ce fichier trace toutes les décisions techniques et architecturales significatives prises au cours du projet.  
> Pour le détail argumenté de chaque décision, consulter les ARDs correspondants dans `doc/ard/`.

---

## [DEC-045] 2026-07-20 — Page d'administration sur-mesure pour les comptes partenaires

**ARD :** [ADR-010](../doc/ard/ADR-010-page-admin-comptes-partenaires.md)
**Contexte :** Suite à DEC-044, KLEM doit pouvoir créer/modifier/supprimer un compte partenaire (identifiant, mot de passe, e-mail, secteur d'activité) après avoir reçu une demande de partenariat via le formulaire de contact. Point de vue proposé : étendre l'écran natif Utilisateurs plutôt que construire un CRUD dédié (moins de code à sécuriser). L'utilisateur a explicitement choisi la page dédiée malgré cette recommandation.
**Décision :** Nouvel écran wp-admin (`inc/partner-accounts.php`, menu « Partenaires ») avec rôle dédié `klem_partenaire`, liste/création/édition/suppression basées sur `wp_insert_user()`/`wp_update_user()`/`wp_delete_user()` natifs (aucune cryptographie maison). Champ « Secteur d'activité » en `user_meta`. Mot de passe optionnel à l'édition (laisser vide = inchangé), modifiable ensuite par le partenaire lui-même via le flux « mot de passe oublié » déjà en place. Sécurité : `current_user_can('manage_options')` vérifié dans chaque handler `admin_post_*`, nonces dédiés, sanitisation stricte (`sanitize_user()` strict, `validate_username()`, `is_email()`), mot de passe ≥ 8 caractères.
**Impact :** `inc/partner-accounts.php` (nouveau), `assets/js/admin-partners.js` (nouveau — bouton « Générer un mot de passe »), `functions.php` (require).
**Vérification :** Flux CRUD complet testé de bout en bout en local via un compte administrateur temporaire (créé/supprimé par script, jamais de credentials réels utilisés) : création d'un compte partenaire ✅, édition (changement de secteur, mot de passe laissé vide = inchangé, vérifié en se connectant ensuite avec l'ancien mot de passe) ✅, connexion du partenaire créé → accès à `/cas-clients/` sans redirection, barre d'admin masquée ✅, suppression → compte disparu de la liste ✅. `php -l` et `node --check` sans erreur, `pnpm build` sans erreur.

---

## [DEC-044] 2026-07-20 — Authentification native : Cas d'usage réservés aux partenaires connectés

**ARD :** [ADR-009](../doc/ard/ADR-009-authentification-cas-usage-reserves.md)
**Contexte :** Demande utilisateur : le bouton du header devient "Connectez-vous" ; le lien de menu "Cas d'usage" reste caché tant que l'utilisateur n'est pas authentifié ; l'accès direct à `/cas-clients/` est réservé aux utilisateurs connectés, sinon redirection vers le formulaire de contact avec le sujet "Demande de partenariat" présélectionné. KLEM traite ensuite la demande manuellement (pas d'auto-inscription) et crée le compte du partenaire.
**Décision :** Authentification 100 % native WordPress (`wp_signon()`), page de connexion sur-mesure `page-connexion.php` (créée idempotemment via `klem_bootstrap_login_page()`) postant vers `admin-post.php?action=klem_login`. Réutilise le rate limiting anti-brute-force déjà en place sur `wp-login.php` (mêmes hooks core `authenticate`/`wp_login_failed`/`wp_login`). Le CTA header (desktop + mobile) devient "Connectez-vous" quand non connecté, ou nom d'utilisateur + "Déconnexion" (`wp_logout_url()`) quand connecté. Le lien "Cas d'usage" est masqué (header + footer) tant que non connecté, en plus du gating serveur sur `/cas-clients/` (`template_redirect`). Option "Demande de partenariat" ajoutée au select Sujet du formulaire de contact, présélectionnée via `$_GET['sujet']`, avec bandeau contextuel. Barre d'admin WordPress masquée en façade pour les comptes non-administrateurs (`show_admin_bar`).
**Impact :** `functions.php`, `page-connexion.php` (nouveau), `header.php`, `footer.php`, `template-parts/home/contact.php`.
**Vérification :** Flux testé de bout en bout en local (compte de test créé puis supprimé via script temporaire) — redirection `/cas-clients/` → contact quand non connecté, connexion réussie → accès à `/cas-clients/`, menu "Cas d'usage" qui réapparaît, déconnexion qui le fait disparaître à nouveau, message d'erreur générique sur mot de passe invalide, présélection du sujet confirmée en sortie HTML. `pnpm build` et `php -l` sans erreur sur tous les fichiers modifiés.
**Point de vigilance :** La création de compte partenaire reste manuelle (admin WordPress) — cohérent avec la demande, mais à reconsidérer si le volume augmente.

---

## [DEC-043] 2026-07-19 — Audit SEO/sécurité : CSP scopée + fermeture des commentaires

**Contexte :** Demande explicite d'améliorer le SEO et de corriger les vulnérabilités. Audit préalable (JSON-LD, OG, Twitter Card, robots meta, sitemap XML, en-têtes de sécurité déjà présents `X-Content-Type-Options`/`X-Frame-Options`/`Referrer-Policy`/`Permissions-Policy`/HSTS, XML-RPC désactivé, énumération de comptes bloquée, rate limiting login/contact/chatbot, `pnpm audit` sans vulnérabilité) confirme un socle déjà mature (cf. DEC-037, ADR précédents). Deux lacunes réelles identifiées : absence de Content-Security-Policy, et commentaires WordPress laissés au statut par défaut ("ouvert") alors qu'aucun template ne les affiche jamais.
**Décision :**
1. **CSP scopée au front public.** Ajout d'un `Content-Security-Policy` dans `web/.htaccess`, exclu explicitement de `/wp/wp-admin/` et `wp-login.php` (via `<If>`, même patron que le blocage PHP déjà en place sur `/app/uploads/`) car l'éditeur et la médiathèque WordPress reposent sur de nombreux scripts/styles inline sans nonce — une CSP stricte y aurait cassé des fonctionnalités sans test exhaustif possible dans cette session. `script-src`/`style-src` restent en `'unsafe-inline'` par nécessité (thème + WP core), mais `object-src 'none'`, `base-uri 'self'`, `form-action 'self'` et `frame-ancestors 'self'` apportent une vraie défense en profondeur. Vérifié sans erreur console ni violation CSP (Playwright, page d'accueil complète) et confirmé exclu sur `/wp/wp-admin/` par requête directe.
2. **Commentaires fermés site entier.** Le thème ne rend jamais de formulaire ni de liste de commentaires, mais WordPress garde `wp-comments-post.php` et l'endpoint REST `/wp/v2/comments` actifs par défaut — surface de spam sans aucune valeur éditoriale. Fermeture par filtres (`comments_open`, `pings_open`, `comments_array`) plutôt que par le seul réglage par défaut (couvre aussi les contenus déjà en base), réglages par défaut mis à jour pour le futur contenu, flux de commentaires retiré du `<head>`, endpoint REST `/wp/v2/comments` désenregistré. Vérifié : 404 sur `/wp-json/wp/v2/comments`.
**Alternatives considérées :** CSP stricte sans `unsafe-inline` — écartée, casserait probablement wp-admin (nonces/hash non configurés pour les scripts inline de core) sans possibilité de valider exhaustivement dans cette session ; upgrade de Composer 2.2.6 pour disposer de `composer audit` — hors périmètre (outillage local, pas une vulnérabilité du site ; seules 3 dépendances déclarées, WordPress core déjà à jour en 6.9.4).
**Impact :** `web/.htaccess`, `web/app/themes/klem-theme/functions.php`.
**Limite connue :** La CSP reste permissive sur `script-src`/`style-src` (`unsafe-inline`) — un futur durcissement complet nécessiterait de déplacer les styles inline du thème vers des classes et d'auditer les scripts inline de wp-admin pour y ajouter des nonces, hors périmètre de cette session.

---

## [DEC-042] 2026-07-19 — Bandeau "Stack technique" : logos de marque réels + ton monochrome sombre

**Contexte :** L'utilisateur a demandé (1) un ton "calque noir/blanc" pour la bande de technologies, (2) une couleur et un logo propres à chaque technologie/stack, (3) une harmonie de couleurs plus sophistiquée pour l'ensemble du site, sans donner l'impression d'un rendu généré par IA, tout en conservant la charte KLEM.
**Décision (bande technologies) :** Le fond de la section passe d'un gris clair uni à un dégradé sombre (`#0B1626 → #13294B → #0B1626`), avec badge/typo repris du style déjà établi pour les sections sombres du site (`clients.php` : badge orange, titre blanc). Chaque pastille de technologie porte désormais un logo réel sur un badge blanc (le "calque noir/blanc" : fond de section sombre, badges blancs, logos en couleur de marque officielle) plutôt qu'une icône maison teintée en rouge. 14 technologies disposent d'un vrai logo vectoriel + couleur de marque officielle (Apache Hadoop, Spark, Kafka, Python, Java/OpenJDK, React, Docker, PostgreSQL, MySQL, MongoDB, Git, Linux, Jenkins, SonarQube), extraits du projet open source **Simple Icons** (licence CC0, données de marque vérifiées — pas de tracé inventé). 8 catégories sans produit précis (ERP & Intégration, Sécurité applicative, Gouvernance IT ITIL®, Méthodes Agile Scrum, Cloud & Infrastructure, IA & Machine Learning, Automatisation, BI & Reporting) gardent une icône maison neutre ; celle de Power BI reprend malgré tout sa couleur de marque réelle vérifiée (#F4D249, confirmée par recherche web) même sans logo vectoriel disponible.
**Découverte annexe (analyse harmonie couleurs) :** Audit des badges "œillet" (`text-klem-X font-bold tracking-widest ... rounded-full`) répétés dans quasiment chaque section du thème. Constat : `klem-orange` et `klem-red` sont **le même hex** (`#E42313`, cf. commentaire déjà présent dans `tailwind.config.js` : "Alias → Rouge KLEM, aucun orange dans la charte"). L'alternance rouge/orange perçue section par section n'est donc pas une incohérence visuelle réelle (même pixel), mais une incohérence de nommage dans le code. Le principal facteur d'effet "généré" identifié n'est pas la couleur mais la **répétition à l'identique du même composant pastille** (fond teinté + majuscules + tracking large) dans la quasi-totalité des sections, sans variation de traitement. Proposition présentée à l'utilisateur pour arbitrage avant d'exécuter un remaniement plus large (changement subjectif à fort rayon d'action, cf. décision similaire sur les polices en DEC-041).
**Impact :** `template-parts/home/technologies.php` (régénéré via script Node à partir des données Simple Icons, cf. `/tmp/.../gen-technologies.js` — script non versionné, usage ponctuel).
**Limite connue :** Le point (3) "harmonie de couleurs" n'a pas encore été exécuté — analyse livrée, exécution en attente de la direction choisie par l'utilisateur.

**Complément (même jour) — Exécution de l'harmonie couleurs.** L'utilisateur a validé l'exécution directe ("bleu marine + rouge uniquement, rien d'autre"). Décision : le vrai levier n'est pas la couleur (klem-orange = klem-red, même hex) mais la répétition à l'identique de la pastille "œillet" (fond teinté + majuscules + `rounded-full`) en tête de 10 sections sur ~13. Plutôt que d'inventer un nouveau style, réutilisation du traitement déjà présent et déjà réussi dans `about.php` (simple label coloré en majuscules, sans fond ni pastille) : 6 fichiers alignés sur ce style (`page-actualites.php`, `page-cas-clients.php`, `actualites-preview.php`, `offers.php`, `contact.php`, `cas-clients-preview.php`). Conservé tel quel (pastille pleine) pour les sections où le concept de badge fait sens ou où le contexte est déjà différencié : `services.php` (section d'ancrage), `certifications.php` (le concept même de certification = badge), `hero.php` (positionnement différent, pas un simple en-tête de section), `technologies.php` et `clients.php` (sections sombres déjà distinctes). Résultat : la pastille pleine passe de 10 occurrences à 5, avec un vrai contraste entre les deux traitements plutôt qu'une répétition uniforme.
**Impact :** `page-actualites.php`, `page-cas-clients.php`, `template-parts/home/actualites-preview.php`, `template-parts/home/offers.php`, `template-parts/home/contact.php`, `template-parts/home/cas-clients-preview.php`.
**Limite connue :** Le renommage `klem-orange` → `klem-red` (nettoyage de code, aucun effet visuel puisque même hex) n'a pas été fait — signalé comme nettoyage optionnel, pas nécessaire pour l'harmonie visuelle demandée.

---

## [DEC-041] 2026-07-19 — Correction du chargement d'Inter + adoption de Questrial pour les titres

**Contexte :** L'utilisateur a partagé une capture de l'inspecteur de polices du navigateur sur un site concurrent (veone.net, ESN ivoirienne) montrant des polices d'icônes (Font Awesome 5). Clarification apportée : Font Awesome est une police d'icônes, pas la police de texte de veone.net. Inspection directe du HTML/CSS de veone.net (balises `<link>` Google Fonts + `font-family` inline) : leur véritable typographie est **Questrial** (titres) + **Inter Tight** (corps de texte).
**Découverte annexe (bug réel) :** `src/main.css` déclarait `font-family: 'Inter', ...` pour tout le texte du site KLEM, mais Inter n'était jamais chargée (`klem_enqueue_fonts()` ne chargeait que Archivo, la police du logo) — tous les visiteurs voyaient donc une police système de repli au lieu d'Inter. Corrigé indépendamment du reste : Inter (400–800) ajoutée à l'URL Google Fonts déjà utilisée pour Archivo.
**Décision :** Comparaison visuelle Inter Bold vs Questrial présentée à l'utilisateur (maquette HTML, capture d'écran) avec mise en garde explicite : Questrial n'existe qu'en graisse 400 (pas de gras), donc moins percutant que l'Inter Bold actuel pour le H1 — au risque d'aller à l'encontre de la critique "manque de CHOC" du consultant (DEC-039). L'utilisateur a choisi malgré tout Questrial pour tous les titres (H1/H2) du site. Appliqué via une nouvelle classe utilitaire Tailwind `font-heading` (`fontFamily.heading` = Questrial), qui remplace `font-bold`/`font-extrabold` sur les 18 balises `<h1>`/`<h2>` du thème (aucun poids de graisse demandé, puisque Questrial n'en propose qu'un).
**Impact :** `functions.php` (Inter + Questrial ajoutées à `klem_enqueue_fonts()`), `tailwind.config.js` (`fontFamily.heading`), et 15 fichiers de template (tous les `<h1>`/`<h2>` du thème).
**Limite connue :** Si le rendu Questrial déçoit à l'usage (notamment sur le H1 du hero, où l'impact visuel est le plus sensible), revenir à `font-bold`/`font-extrabold` + supprimer `font-heading` sur les balises concernées ; aucune donnée n'est perdue, changement purement visuel et réversible.
**Incident (même jour) :** Un sous-agent en arrière-plan (notification `task-notification`, directive "choisissez ce qui est mieux selon vous") a modifié 6 fichiers (`single.php`, `page-actualites.php`, `404.php`, `index.php`, `page-cas-clients.php`, `hero.php`) pour revenir à Inter Bold sur les H1 et les titres de cartes en boucle, sans qu'une demande explicite de l'utilisateur pour cette tâche ne soit retrouvée dans la conversation visible. Signalé immédiatement à l'utilisateur plutôt que traité comme une décision acquise ; l'utilisateur a confirmé vouloir revenir à Questrial partout (son choix initial), ce qui a été réappliqué sur les 6 fichiers concernés. `php -l` + `pnpm build` revérifiés OK après correction.

---

## [DEC-040] 2026-07-19 — Bandeau "Stack technique" (marquee défilant) sur la home

**Contexte :** Suite à DEC-039 (compétences/certifications réelles du gérant issues du CV), l'utilisateur a demandé si l'étendue technique (Big Data Hadoop/Spark/Kafka, Power BI/DAX, ERP...) méritait sa propre mise en avant visuelle, sous forme de carte défilant automatiquement de droite à gauche façon slider.
**Décision :** Nouvelle section `template-parts/home/technologies.php`, insérée entre Services et Offres. 12 technologies/catégories génériques (Big Data, BI & Reporting, Streaming, Développement, ERP & Intégration, Sécurité applicative, Bases de données, Gouvernance IT ITIL®, Cloud & Infrastructure, Méthodes Agile, IA & Machine Learning, Automatisation) affichées en pastilles icône + libellé, dans un bandeau à défilement continu (CSS `@keyframes klem-marquee`, liste dupliquée ×2 pour une boucle sans coupure, pause au survol/focus, désactivé si `prefers-reduced-motion`). Contrairement à DEC-039, aucune validation préalable n'a été nécessaire ici : il s'agit d'un inventaire de technologies génériques (pas une allégation d'historique client, pas une donnée personnelle identifiante), cohérent avec ce qui est déjà publiquement affiché ailleurs sur le site (Kafka/Spark déjà cités en Hero et dans Services).
**Impact :** `template-parts/home/technologies.php` (nouveau), `front-page.php`, `src/main.css` (nouvelle animation `.animate-marquee`)
**Limite connue :** Les icônes réutilisent des tracés SVG déjà validés ailleurs dans le thème (aucun nouveau tracé inventé) pour éviter tout risque de path SVG malformé.

---

## [DEC-039] 2026-07-19 — Application filtrée d'un audit externe (consultant stratégie) : refus des preuves fabriquées, ajout d'une offre d'entrée "sur devis"

**Contexte :** L'utilisateur a soumis un PDF d'audit rédigé par un consultant externe en stratégie d'entreprise (note 8,6/10), demandant d'appliquer ses recommandations pour améliorer le fond et la forme du site. Le rapport recommandait, entre autres, d'ajouter des statistiques chiffrées d'exemple ("+20 projets réalisés", "18 entreprises accompagnées"), des témoignages clients types (citations attribuées à des rôles génériques), une page dirigeants, des logos de certifications, et une grille tarifaire chiffrée.
**Décision :** Trier les recommandations en deux catégories avant exécution. **(1) Rejetées / reportées** — toute recommandation nécessitant de fabriquer une preuve (chiffres d'historique client, témoignages, certifications non détenues, page équipe) reste hors périmètre : cela contredirait directement DEC-012/DEC-032/DEC-038/ADR-008 (refus documenté et déjà validé par l'utilisateur des allégations invérifiables). Confirmé explicitement par l'utilisateur via question à choix multiple : pas de chiffres, pas de témoignage, pas de page équipe, pas de certification pour l'instant. **(2) Appliquées** — (a) réécriture du message Hero pour être plus orienté bénéfice client tout en conservant le positionnement "souveraineté numérique" (noté 10/10 par le consultant) ; (b) ajout d'icônes par sous-section (Contexte/Défi/Solution/Bénéfice) sur la page Cas d'usage pour améliorer la scannabilité (critique "trop de texte") ; (c) nouvelle section "Offres de démarrage" sur la home (`template-parts/home/offers.php`) répondant à la critique "il manque une offre claire" — 4 offres nommées et bornées (diagnostic gratuit 30 min déjà existant, Audit Data, Audit Cybersécurité, Diagnostic Infrastructure & ERP), toutes en **« Sur devis »** plutôt qu'avec des prix fixes inventés.
**Recherche de positionnement concurrentiel :** Avant de choisir "sur devis" plutôt qu'une grille de prix fixes, vérification des sites de deux ESN régionales (SGCI, Côte d'Ivoire ; Dakar.IT.Services, Sénégal, via WebFetch) — aucune n'affiche de tarifs publics, les deux renvoient vers un devis/contact générique. KLEM se différencie en nommant des offres d'entrée précises et bornées (plutôt qu'un simple "contactez-nous"), sans pour autant s'engager sur un prix que l'entreprise n'a pas validé.
**Impact :** `template-parts/home/hero.php`, `template-parts/home/offers.php` (nouveau), `front-page.php`, `page-cas-clients.php`
**Limite connue :** Si l'entreprise obtient réellement des chiffres vérifiables, des témoignages ou des certifications, ces sections (engagements About/Hero, Cas d'usage, Offres) devront être révisées pour refléter le réel — cf. limite déjà notée dans ADR-008.

**Complément (même jour) — Certifications individuelles réelles du gérant.** L'utilisateur a fourni son propre CV (`CV/CV_SYLLA.pdf`, `CV/CV_DA.pdf` — non versionnés, cf. `.gitignore`) pour vérifier si des informations réelles et pertinentes pouvaient combler les manques identifiés (certifications, années d'expérience), avec une exigence stricte : ni son nom, ni son employeur actuel (GUCE-CI, gouvernemental) ne doivent apparaître, et toute intégration doit être validée avant publication. Analyse du CV : certifications réelles et vérifiables — Professional Scrum Master I (PSM I, Scrum.org), ITIL® V3 et ITIL® V4 Foundation (Axelos/PeopleCert) — et ~21 ans d'expérience professionnelle continue en développement logiciel/data depuis 2005. Les détails de projets menés chez l'employeur actuel (plateforme de guichet unique, balance commerciale de Côte d'Ivoire, etc.) ont été explicitement exclus car trop identifiables pour cet employeur, même sans le nommer. Proposition présentée à l'utilisateur avant toute implémentation ; validée avec la précision « 20 ans » (arrondi accepté par l'utilisateur, sur une base réelle de 21 ans). **Implémenté :** remplacement de la statistique Hero "100% sur-mesure" par "20 ans d'expertise data & logicielle (équipe dirigeante)" ; nouvelle section `template-parts/home/certifications.php` (3 badges PSM I / ITIL V4 / ITIL V3, attribués à « notre équipe dirigeante », sans nom ni employeur).
**Impact additionnel :** `template-parts/home/certifications.php` (nouveau), `front-page.php`, `template-parts/home/hero.php`, `.gitignore` (ajout de `CV/` et `/*.pdf` à la racine — documents personnels/PII jamais commités).

---

## [DEC-038] 2026-07-16 — Renommage "Cas Clients" → "Cas d'usage" (nav, page, footer, meta)

**Contexte :** L'utilisateur (en tant qu'expert web designer) a signalé une incohérence : le libellé "Cas Clients" (nav, footer, badge de la page `/cas-clients/`, meta title, breadcrumb) laisse attendre de vraies références clients, alors que le sous-titre de la page et le teaser home (`cas-clients-preview.php`) précisent déjà explicitement qu'il s'agit de scénarios types/illustratifs "plutôt qu'un historique" — cohérent avec DEC-012/DEC-032 (pas de faux témoignage tant que KLEM n'a pas de premiers clients réels documentés), mais le libellé lui-même n'avait jamais été aligné sur ce positionnement.
**Décision :** Remplacer "Cas Clients" par "Cas d'usage" partout où le texte est visible ou indexable : nav desktop/mobile (`header.php`), footer (`footer.php`), badge hero + `Template Name` (`page-cas-clients.php`), `post_title` de la page auto-créée, meta title et breadcrumb JSON-LD (`functions.php`). L'URL/slug `/cas-clients/` et le nom de fonction `klem_cas_clients_url()` sont **conservés tels quels** (pas de risque de casser un lien déjà indexé suite au travail SEO de DEC-037, changement purement cosmétique).
**Impact :** `header.php`, `footer.php`, `page-cas-clients.php`, `functions.php`
**Limite connue :** `post_title` n'est appliqué qu'à la création de la page (`wp_insert_post` dans `klem_bootstrap_cas_clients()`, idempotent). Si la page existe déjà en base (site déjà en production), le titre affiché dans wp-admin doit être corrigé manuellement — le code ne le fait pas rétroactivement.

---

## [DEC-037] 2026-07-15 — SEO local Afrique/CI + fermeture de 2 fuites d'énumération résiduelles

**Contexte :** Demande explicite d'optimiser le référencement pour la Côte d'Ivoire/l'Afrique et de corriger les vulnérabilités restantes. Le site avait déjà un socle SEO/sécurité solide (DEC antérieurs, commit `219a6d3`), mais deux angles morts identifiés : `lang="fr-FR"` (moins précis que `fr-CI` pour un ciblage régional), aucune coordonnée géographique dans le JSON-LD `ProfessionalService`, et surtout — le sitemap XML natif WordPress (`wp-sitemap-users-*.xml`) et les archives auteur (`/author/<slug>/`) exposaient encore le nom d'utilisateur admin malgré le filtre `rest_endpoints` déjà en place (qui ne couvrait que l'API REST, pas ces deux autres chemins).
**Décision :**
1. SEO : `lang="fr-CI"`, `GeoCoordinates` (Treichville, Abidjan — précision commune, pas d'adresse géocodée exacte) dans le JSON-LD, meta géo legacy (`geo.region`, `geo.placename`, `geo.position`, `ICBM`).
2. Sécurité : provider `users` retiré du sitemap XML (`wp_sitemaps_add_provider`), toute archive auteur redirige désormais vers l'accueil (plus seulement le motif `?author=N`), rate limiting anti-brute-force sur `wp-login.php` (5 échecs / 15 min / IP, transient — même patron que le formulaire de contact), nettoyage RSD/wlwmanifest/shortlink/`X-Pingback` (résidus liés à XML-RPC déjà désactivé), blocage `.log` en `.htaccess`.
3. **Limite explicitement communiquée à l'utilisateur :** aucune optimisation technique ne garantit un classement ("parmi les plus vus") — cela dépend aussi de contenu, backlinks, avis Google Business Profile, autorité de domaine, etc., hors du périmètre code.
**Impact :** `functions.php`, `web/.htaccess`
**Bug corrigé en cours de route :** le premier essai du rate limiter (`add_filter('authenticate', ..., 1, 3)`, priorité 1) ne bloquait rien — `wp_authenticate_username_password` (core, priorité 20) ignore une `WP_Error` déjà présente dès que login/mot de passe sont non vides, donc écrase systématiquement le blocage. Fix : hook à la priorité 30 (après le core), confirmé fonctionnel via les logs Docker (`error_log` temporaire, retiré après validation).
**Règle à suivre :** Pour bloquer une tentative de connexion via le filtre `authenticate`, toujours hooker à une priorité **postérieure** à 20 (le core WordPress n'honore une erreur préexistante que si les identifiants sont vides).

---

## [DEC-036] 2026-07-15 — Secret DB déjà exposé publiquement : rotation différée + convention `ACCESS.md` local

**Contexte :** En préparant la sauvegarde des paramètres d'accès (SSH Hostinger, DB, API), constat que `collaboration/history/history-log.md` (Session 08, 2026-06-26) contient en clair le mot de passe DB de production (`DB_PASSWORD=I@ndI2905`) — commité et poussé sur un dépôt GitHub **public**, en contradiction directe avec DEC-006 ("Aucun secret ne réside dans le dépôt Git").
**Décision :**
1. Le mot de passe exposé n'est **pas** changé immédiatement — décision explicite de l'utilisateur (2026-07-15) de traiter ce point plus tard plutôt que dans l'urgence de cette session.
2. Les paramètres d'accès (host/port/user SSH, chemins serveur, identifiants DB/API) sont désormais centralisés dans `ACCESS.md` à la racine du dépôt — fichier **local, jamais commité** (ajouté à `.gitignore`), conformément à DEC-006. Aucun identifiant réel n'est écrit dans `history-log.md`, `decision-log.md` ou les ADR à partir de cette session.
**Impact :** `.gitignore` (+`ACCESS.md`), `ACCESS.md` (nouveau, non versionné)
**Règle à suivre :** Ne plus jamais écrire de secret réel (mot de passe, clé API) dans un fichier `.md` versionné — seulement dans `.env` (secrets applicatifs) ou `ACCESS.md` (accès infrastructure). Avant toute rotation du mot de passe DB de production, prévenir l'utilisateur que l'ancienne valeur reste lisible dans l'historique git tant qu'il n'est pas réécrit (`git filter-repo`/BFG, non fait à ce jour).

---

## [DEC-035] 2026-07-15 — Cas Clients : contenu allégé + retrait des projets non lancés pour protéger la propriété intellectuelle

**Contexte :** Ajout initial de 3 nouveaux cas d'usage (Clear-Comply, Med-Share, Dispo-Link) tirés du portefeuille R&D (`klem-labs-repository/projects/`), avec des descriptions détaillées issues des `business_case.md` respectifs. L'utilisateur a ensuite exprimé une préoccupation légitime : la nécessité de vendre l'image de KLEM ne doit pas se faire au prix d'exposer publiquement le fonctionnement de projets non encore lancés ("on ne doit pas voler mes projets").
**Décision :**
1. **Dispo-Link** (statut R&D "Idéation") retiré de la page publique `/cas-clients/` jusqu'à son lancement — contenu conservé dans l'historique git (commit `487ed78`) pour réintégration facile.
2. **Clear-Comply** et **Med-Share** (statuts "Prototype" et "Idéation") restent affichés mais avec un texte de solution allégé : retrait des détails de mécanisme (ex. standard technique HL7 FHIR, croisement de référentiels tarifaires) — seuls le problème résolu et le bénéfice attendu restent publics.
3. Chaque carte de cas d'usage affiche désormais un lien "contactez-nous" invitant à échanger directement plutôt que de détailler publiquement le fonctionnement.
**Impact :** `web/app/themes/klem-theme/page-cas-clients.php`
**Règle à suivre :** Pour tout futur projet R&D ajouté à la page Cas Clients, décrire le problème/bénéfice sans révéler le mécanisme technique précis ni le modèle économique tant que le projet n'est pas officiellement lancé — et vérifier le statut R&D (`business_case.md` du projet concerné) avant publication.

---

## [DEC-034] 2026-07-15 — Images Actualités manquantes en production : cause = médiathèque non synchronisée, pas un bug de code

**Contexte :** Les 5 articles seed du hub Actualités (`klem_bootstrap_seed_articles()`, DEC-033) affichaient un placeholder générique en production alors que le thème gère correctement le cas `has_post_thumbnail()` (`template-parts/actualites/card.php`). Diagnostic initial erroné : la fonction de seed n'assigne jamais d'image mise en avant. En vérifiant l'environnement Docker local, constat que 5 images dédiées par article existaient déjà (uploadées lors d'une session précédente, 2026-07-14, `app/uploads/2026/07/`) — le vrai problème est que ce contenu de médiathèque (fichiers + métadonnées `_thumbnail_id`) n'avait jamais été transféré vers Hostinger, la médiathèque WordPress n'étant pas versionnée dans Git.
**Décision :** Ne pas ajouter de mécanisme de fallback automatique côté code (image générique service). À la place, synchronisation manuelle ponctuelle : les 5 fichiers transférés vers le serveur par `scp`, puis importés et assignés comme image mise en avant via `wp media import --post_id=<ID> --featured_image` (wp-cli, disponible sur l'hébergement Hostinger à `/usr/local/bin/wp`).
**Impact :** Médiathèque de production uniquement (attachements ID 12 à 16) — aucun changement de code.
**Règle à suivre :** Toute image liée à un post créé par une fonction de seed (`init` hook) doit être synchronisée manuellement vers chaque environnement après upload local — la médiathèque WordPress (`app/uploads/`) n'est pas versionnée (cf. `.gitignore`). Utiliser le patron `scp` + `wp media import --featured_image` établi ici pour les prochains cas similaires.

---

## [DEC-033] 2026-07-14 — Actualités : 5 articles réels en catégorie "Blog" unique, pas de nouvelle taxonomie

**Contexte :** Le brief de refonte suggérait 4 catégories Actualités ("Données & IA", "ERP & applications", "Commerce digital", "Secteur public"), différentes des 3 catégories déjà existantes (Blog/Actualités/Événements, cf. Session 12/`klem_bootstrap_actualites()`). Le hub Actualités a une UI de filtre câblée en dur sur exactement ces 3 onglets.
**Décision :** Garder la taxonomie existante à 3 catégories. Les 5 nouveaux articles de fond (tendances data/IA/commerce digital) sont tous publiés en catégorie **"Blog"** — c'est du contenu éditorial/pédagogique, pas une actualité d'entreprise ni un événement, donc "Blog" est la catégorie correcte dans le schéma existant. Aucune catégorie supplémentaire créée.
**Impact :** `functions.php` (`klem_bootstrap_seed_articles()`)
**Règle :** Si le volume d'articles augmente significativement et qu'un besoin réel de sous-catégorisation apparaît, revoir `klem_actualites_categories()` et l'UI de filtre de `page-actualites.php` à ce moment-là plutôt que d'anticiper.

---

## [DEC-032] 2026-07-14 — Refonte positionnement honnête (Hero/About) + page Cas Clients illustrative

**ARD :** [ADR-008](../doc/ard/ADR-008-refonte-positionnement-honnete.md)
**Contexte :** Brief externe de refonte de contenu (`prompt-renovation.md.md`) + constat que le Hero et l'About affichaient des statistiques d'historique client entièrement inventées, incompatibles avec le statut de jeune structure en prospection.
**Décision :** Voir ADR-008 pour le détail complet — résumé :
1. Hero/About : retrait de toute métrique d'historique client fabriquée, remplacée par des faits structurels vrais (délai de réponse, nombre de secteurs, piliers, engagement sur-mesure) ou des objectifs explicitement formulés comme tels.
2. Nouvelle page `/cas-clients/` (`page-cas-clients.php`) : 3 cas d'usage illustratifs (FleetControl/logistique, Cantine Connect/éducation, data warehouse/commerce) explicitement présentés comme des exemples, sans fausse citation client ni résultat chiffré inventé — choix validé explicitement par l'utilisateur plutôt que de suivre le brief tel quel.
3. Nouveaux teasers home : `template-parts/home/cas-clients-preview.php` et `template-parts/home/actualites-preview.php`, insérés entre "Notre différence" et "Contact" dans `front-page.php`.
4. Nav (`header.php`, desktop + mobile) et footer (`footer.php`) : lien "Cas Clients" câblé (le footer avait déjà un placeholder `href="#"` jamais utilisé).
5. Services : CTA génériques "En savoir plus" (qui n'étaient même pas de vrais liens — un `<span>` stylé) remplacés par des CTA réels par pilier réutilisant le mécanisme `data-sector` déjà câblé en JS pour préremplir le formulaire de contact.
6. Contact : encadré explicatif du chatbot + bouton "Discuter avec l'assistant" (déclenche `CustomEvent('klem:open-chat')`, écouté dans `main.js`), micro-copy resserrée ("24 h" → "24 à 48h ouvrées", cohérent avec la promesse du chatbot).
**Impact :** `hero.php`, `about.php`, `services.php`, `contact.php`, `header.php`, `footer.php`, `front-page.php`, `functions.php`, `page-cas-clients.php` (nouveau), `template-parts/home/cas-clients-preview.php` (nouveau), `template-parts/home/actualites-preview.php` (nouveau), `src/main.js`
**Règle :** Toute future statistique ajoutée au Hero/About doit être vérifiable dès aujourd'hui (pas un historique) tant que KLEM n'a pas de premiers clients réels documentés — voir ADR-008.

---

## [DEC-031] 2026-07-14 — Chatbot : rendu Markdown minimal côté client (gras + listes)

**Contexte :** Capture d'écran utilisateur montrant une réponse illisible : le modèle renvoie du Markdown (`**gras**`, listes `-`/`1.`) et de vrais sauts de ligne, mais le widget affichait tout en texte brut sur une seule ligne — les bulles utilisaient `textContent` et le CSS par défaut collapse les retours à la ligne.
**Décision :** Ajout d'un petit rendu Markdown maison dans `src/main.js` (`renderAssistantText`) plutôt qu'une librairie externe :
1. Le texte brut est **d'abord entièrement échappé** (`escapeHtml`) — aucune balise fournie par le modèle ne peut atteindre le DOM.
2. Seuls nos propres motifs sont ensuite réintroduits : `**gras**` → `<strong>`, lignes `- `/`* ` → `<ul>`, lignes `1. `/`1)` → `<ol>` (numérotation native CSS, pas de recomptage manuel), le reste → `<p>`.
3. Les lignes vides **ne referment pas** une liste en cours (sinon une liste à items séparés par une ligne blanche se scindait en plusieurs listes d'un seul item, avec une numérotation qui repartait à 1 à chaque fois).
4. Les messages de l'utilisateur restent en `textContent` pur (aucun rendu Markdown nécessaire ni souhaitable côté visiteur).
**Impact :** `web/app/themes/klem-theme/src/main.js`, `web/app/themes/klem-theme/src/main.css` (classes `.klem-chat-paragraph` / `.klem-chat-list` — en CSS pur plutôt qu'en utilitaires Tailwind, car `main.js` n'est pas scanné par le `content` de `tailwind.config.js`)
**Règle :** Si de nouveaux motifs Markdown doivent être supportés (citations, liens...), les ajouter dans `renderAssistantText` en gardant le principe échappement-d'abord — ne jamais injecter le texte du modèle directement en `innerHTML`.

---

## [DEC-030] 2026-07-14 — Chatbot : modèle rapide (Haiku 4.5), capture simplifiée, auto-ouverture

**Contexte :** Premier test réel en production → erreur réseau côté visiteur et lenteur perçue. `claude-opus-4-8` (modèle le plus puissant mais le plus lent) combiné à `max_tokens: 1024` dépassait le confort d'attente et risquait le timeout sur hébergement mutualisé. Le client demande en plus un accueil immédiat à l'arrivée sur le site et une capture de coordonnées plus rapide, avec un nouveau jeu de champs (prénom, nom, email, secteur d'activité — téléphone optionnel).
**Décision :**
1. **Modèle** : `KLEM_ANTHROPIC_MODEL` par défaut → `claude-haiku-4-5-20251001` (fallback dans `wp-config.php` + `.env.example`), `max_tokens` 1024 → 400. Le `.env` de production doit être mis à jour manuellement (variable déjà explicite côté serveur).
2. **Workflow accéléré** (`inc/chatbot-system-prompt.md`) : 1 seule question de qualification maximum avant de basculer sur la capture ; les coordonnées sont demandées **en un seul message groupé** plutôt qu'un champ à la fois.
3. **Schéma de capture** (`inc/chatbot.php` → tool `capture_lead`) : `nom_complet`/`entreprise` remplacés par `prenom`, `nom`, `secteur_activite` ; `telephone` conservé mais optionnel (jamais demandé activement, capturé seulement si spontanément fourni par le visiteur) — décision utilisateur du 2026-07-14.
4. **Auto-ouverture** (`src/main.js`) : le panneau s'ouvre automatiquement 4 secondes après le chargement de la page, une seule fois par session navigateur (`sessionStorage`), pour accueillir le visiteur sans attendre un clic — décision utilisateur du 2026-07-14.
**Impact :** `web/wp-config.php`, `.env.example`, `web/app/themes/klem-theme/inc/chatbot.php`, `web/app/themes/klem-theme/inc/chatbot-system-prompt.md`, `web/app/themes/klem-theme/src/main.js`
**Diagnostic notable :** en local, le conteneur Docker de dev présente un réseau sortant intermittent vers `api.anthropic.com` (délais de 5 à 30 s sans rapport avec le code — confirmé par des appels `curl` bruts depuis le même conteneur montrant la même variance). Ce n'est pas représentatif de la production (Hostinger) : le vrai test de performance doit se faire après déploiement sur `klemtech.net`, pas en local.
**Règle :** Si la lenteur persiste après déploiement malgré Haiku 4.5, vérifier en priorité le réseau sortant du serveur Hostinger vers `api.anthropic.com` (latence DNS/connexion) avant de suspecter le modèle ou le code applicatif.

---

## [DEC-029] 2026-07-14 — Chatbot de capture de leads : proxy AJAX natif vers l'API Anthropic

**ARD :** [ADR-007](../doc/ard/ADR-007-chatbot-lead-capture-anthropic.md)
**Contexte :** Besoin d'un assistant conversationnel pour accueillir les visiteurs, qualifier leur besoin et capturer un lead (nom, email, téléphone) sans plugin lourd. Le backend (`inc/chatbot.php`, `inc/chatbot-system-prompt.md`) avait été rédigé mais aucune interface visiteur n'existait encore.
**Décision :** Même patron que le formulaire de contact (DEC-004/ADR-003) — action `wp_ajax_nopriv_klem_chatbot_message` en proxy vers `https://api.anthropic.com/v1/messages`. System prompt externalisé en markdown. La capture de lead passe par un tool Anthropic (`capture_lead`) mais l'email de notification et le message de confirmation sont construits côté PHP, jamais laissés au modèle seul. Historique de conversation stateless côté navigateur, assaini et borné (30 messages / 4000 caractères) avant chaque appel API. Rate limiting 40 msg/IP/heure (transients). Ajout du widget flottant dans `footer.php` + `src/main.js`.
**Impact :** `inc/chatbot.php`, `inc/chatbot-system-prompt.md`, `footer.php`, `src/main.js`, `web/wp-config.php`, `.env.example`
**Règle :** Le `.env` de production (`~/site-klem/.env` sur Hostinger, hors dépôt — cf. DEC-006/DEC-021) doit contenir `KLEM_ANTHROPIC_API_KEY` et `KLEM_ANTHROPIC_MODEL`, sinon le handler répond 503 « chatbot momentanément indisponible » (comportement voulu, pas un bug).

---

## [DEC-028] 2026-07-01 — Cantine Connect intégré selon le schéma FleetControl (pas de CPT dédié)

**Contexte :** Nouveau produit du client (gestion des paiements et contrôle d'accès cantine scolaire), disponible en test sur `https://cantine-connect-swart.vercel.app/login`. Le client a demandé de l'intégrer "comme FleetControl", sans changer la structure du site.
**Décision :** Reproduire exactement le schéma FleetControl — nom du produit en dur dans 3 tableaux PHP existants, aucun CPT/ACF/JSON créé :
1. `services.php` : mention dans la description du pilier "Applications Sur-Mesure"
2. `footer.php` : lien réel vers la démo dans `$service_links`, avec `target="_blank"` (contrairement au placeholder `#` de FleetControl, qui n'a pas de démo publique)
3. `contact.php` : nouvelle option dans le select "Sujet" du formulaire
**Impact :** `template-parts/home/services.php`, `footer.php`, `template-parts/home/contact.php`
**Règle :** Pour tout futur produit à afficher "comme FleetControl/Cantine Connect", suivre ce même triptyque de 3 fichiers plutôt que créer une infrastructure de données dédiée — cohérent avec le style actuel du thème (pas de CPT produits).

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
