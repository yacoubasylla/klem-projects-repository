# Historique des Sessions de Travail — Site KLEM Technologies

> Chronologie des tâches exécutées, fichiers modifiés et état du projet à chaque clôture de session.

---

## Session 20 — 2026-07-15

**Objectif :** Ajustements de copy Hero, correction des images manquantes du hub Actualités en production, ajout de 3 cas d'usage R&D à la page Cas Clients, et mise en ordre de la gestion des accès/secrets.

### 1. Hero — baseline réécrite
- Texte remplacé par "Ingénierie de données • Applications sur-mesure • Intégration ERP • Infrastructures IT : nous concevons vos outils numériques pour l'Afrique, de la première idée à la mise en production.", partie liste en léger gras (`font-medium text-gray-700`), plusieurs itérations de ponctuation (deux-points, retour à la ligne ajouté puis retiré sur demande)
- `template-parts/home/hero.php`

### 2. Images Actualités manquantes en production
- Diagnostic : voir **DEC-034**. Cause réelle = médiathèque WordPress non synchronisée (pas un bug de code)
- Accès SSH Hostinger établi et documenté dans `ACCESS.md` (nouveau, non versionné — voir DEC-036)
- 5 images extraites du conteneur Docker local (`docker cp`), transférées par `scp`, importées et assignées via `wp media import --post_id=<ID> --featured_image` (wp-cli sur le serveur)
- Un mécanisme de fallback automatique côté code avait été ajouté puis **retiré** (`functions.php`) une fois la vraie cause identifiée — pas de code résiduel

### 3. Page Cas Clients — 3 nouveaux cas d'usage R&D
- Contenu source : `business_case.md` de `01_clear_comply`, `06_med_share`, `10_dispo_link` (`klem-labs-repository/projects/`)
- Ajout initial de Clear-Comply, Med-Share, Dispo-Link — voir **DEC-035** pour la suite :
  - Dispo-Link retiré (non lancé, statut R&D "Idéation")
  - Clear-Comply et Med-Share conservés mais texte de solution allégé (retrait des détails de mécanisme technique)
  - Ajout d'un lien "contactez-nous" sur chaque carte de cas d'usage (texte final : "Pour en savoir plus sur ce projet, contactez-nous.")
- `page-cas-clients.php`

### 4. Sécurité — secret DB exposé publiquement + convention d'accès
- Constat : mot de passe DB de production en clair dans `history-log.md` (Session 08), dépôt GitHub public — voir **DEC-036**
- Décision utilisateur : rotation différée, pas traitée dans cette session
- `ACCESS.md` créé (racine du dépôt, ajouté à `.gitignore`) pour centraliser localement les paramètres d'accès (SSH, DB, API, comptes) sans jamais les committer

### Fichiers modifiés / créés
| Fichier | Action |
|---|---|
| `template-parts/home/hero.php` | Baseline réécrite (4 itérations de copy) |
| `page-cas-clients.php` | +3 cas d'usage, puis -1 (Dispo-Link), contenu allégé, lien contact par carte |
| `functions.php` | Fallback image ajouté puis retiré (cause réelle identifiée entre-temps) |
| `.gitignore` | +`ACCESS.md`, +blocage `.log` |
| `ACCESS.md` | Créé — non versionné |
| `web/.htaccess` | Blocage `.log` |
| `collaboration/history/decision-log.md` | +DEC-034, DEC-035, DEC-036, DEC-037 |
| Médiathèque production (Hostinger) | +5 images (attachements 12–16), sans changement de code |

### 5. SEO local Afrique/CI + fermeture de 2 fuites d'énumération
- Voir **DEC-037** pour le détail complet
- `lang="fr-CI"`, `GeoCoordinates` + meta géo legacy dans le `<head>`
- Sitemap `users` retiré, archives auteur systématiquement redirigées, rate limiting anti-brute-force sur `wp-login.php`, nettoyage RSD/wlwmanifest/shortlink/X-Pingback
- `functions.php`

### Commits de la session
| Hash | Description |
|---|---|
| `07079c6` | copy(hero): reformule la baseline avec les 4 expertises en avant |
| `beb06de` | copy(hero): ajoute un deux-points et un saut de ligne dans la baseline |
| `4d84b62` | copy(hero): retire le retour à la ligne après les deux-points |
| `487ed78` | feat(cas-clients): ajoute 3 cas d'usage (Clear-Comply, Med-Share, Dispo-Link) |
| `4292c0f` | feat(cas-clients): ajoute un lien de contact sur chaque carte de cas d'usage |
| `f70a386` | copy(cas-clients): simplifie le texte de contact sur les cartes de cas d'usage |
| `b7cc4e5` | docs: journalise la Session 20 + centralise les accès hors dépôt (ACCESS.md) |
| `19cce16` | feat(seo,security): SEO local Afrique/CI + durcissement complémentaire |

### État du projet en clôture
- `php -l` sur tous les fichiers PHP modifiés : ✅
- `pnpm build` : ✅ sans erreur à chaque étape
- Vérification locale via `curl` (rendu hero, présence/absence des cas d'usage, lien de contact) : ✅
- Images de production vérifiées manquantes puis synchronisées avec succès (5/5 imports wp-cli réussis)
- Déploiement Hostinger du code (dernier `git push`, `f70a386`) : **en attente** — le mécanisme de déploiement serveur (pull git vs copie manuelle) n'a pas encore été confirmé côté production à la clôture de cette session

---

## Session 19 — 2026-07-14

**Objectif :** Exécuter un brief de refonte de contenu (`prompt-renovation.md.md`, généré via Perplexity) : rassurer des décideurs B2B alors que KLEM est une jeune structure en prospection active, et publier du contenu réel de fond (data/IA/commerce digital) sur le hub Actualités jusque-là vide.

### Contexte et diagnostic
- En explorant le code avant d'exécuter le brief (header.php, front-page.php, functions.php, page-actualites.php, hero.php, about.php, services.php, clients.php, contact.php), un problème plus large que le brief lui-même a été identifié : Hero et About affichaient des statistiques d'historique client entièrement inventées ("5+ ans d'expertise", "30+ projets livrés", "96% satisfaction clients", "4,2M événements/jour"...) — incompatibles avec le statut de jeune structure, et non traitées lors du retrait des faux témoignages en DEC-012.
- Vérification base de données : le hub Actualités ne contenait aucun article réel, seul le post "Hello world!" par défaut de WordPress (1 seul post publié). WP-CLI non disponible dans le conteneur.
- Deux clarifications obtenues avant exécution (voir DEC-032) : (1) retirer les statistiques inventées plutôt que les garder ou en inventer de nouvelles, (2) présenter les cas clients comme des scénarios illustratifs sans fausse citation ni résultat chiffré fabriqué.

### Tâches réalisées (exécutées en mode plan, plan approuvé avant implémentation)

#### 1. Honnêteté Hero + About
- Hero : bande de 4 stats et 3 cartes flottantes du panneau visuel réécrites (plus aucune métrique d'historique fabriquée) ; sous-titre et CTA principal resserrés ("Planifier un diagnostic gratuit de 30 min")
- About : panneau "Performances clés" (barres de %) remplacé par un panneau "Nos engagements" (promesses opérationnelles) ; mini-stats du bas alignées sur les mêmes faits honnêtes que le Hero

#### 2. Nouvelle page Cas Clients
- `page-cas-clients.php` : 3 cas d'usage illustratifs (FleetControl/logistique, Cantine Connect/éducation, data warehouse/commerce), explicitement présentés comme des exemples
- `functions.php` : `klem_bootstrap_cas_clients()` + `klem_cas_clients_url()` (mirror exact du patron `klem_bootstrap_actualites()`), branche SEO et breadcrumbs dédiées
- Nav (desktop + mobile, `header.php`) et footer (`footer.php` — le lien "Cas clients" existait déjà en placeholder `href="#"`, jamais câblé) mis à jour
- Teaser home : `template-parts/home/cas-clients-preview.php`

#### 3. Contenu réel pour Actualités
- `functions.php` : `klem_bootstrap_seed_articles()` — 5 articles réels rédigés et publiés de façon idempotente (streaming/temps réel, gouvernance des données, agents IA, cloud-first, commerce omnicanal), tous en catégorie "Blog" existante (pas de nouvelle taxonomie, cf. DEC-033)
- Teaser home : `template-parts/home/actualites-preview.php` (3 articles récents, filtrés par catégorie pour exclure le post "Hello world!" par défaut)

#### 4. Polish services / contact / chatbot
- `services.php` : les 4 CTA "En savoir plus" (qui n'étaient même pas de vrais liens — un `<span>` stylé) remplacés par de vrais liens avec CTA spécifique par pilier, réutilisant le mécanisme `data-sector` déjà câblé en JS
- `contact.php` : encadré "Besoin d'une réponse immédiate ?" expliquant le chatbot + bouton d'ouverture ; micro-copy "24h" → "24 à 48h ouvrées"
- `main.js` : `CustomEvent('klem:open-chat')` pour permettre à ce nouveau bouton d'ouvrir le widget chatbot déjà existant
- `front-page.php` : nouvel ordre des sections — `hero → services → about → clients → cas-clients-preview → actualites-preview → contact`

### Fichiers modifiés / créés
| Fichier | Action |
|---|---|
| `template-parts/home/hero.php` | Stats + cartes flottantes + sous-titre/CTA honnêtes |
| `template-parts/home/about.php` | Panneau engagements + mini-stats |
| `template-parts/home/services.php` | CTA réels par pilier + `data-sector` |
| `template-parts/home/contact.php` | Encadré chatbot + micro-copy |
| `template-parts/home/cas-clients-preview.php` | Créé — teaser home |
| `template-parts/home/actualites-preview.php` | Créé — teaser home |
| `page-cas-clients.php` | Créé — page complète |
| `header.php` | +entrée nav "Cas Clients" |
| `footer.php` | Lien "Cas clients" câblé |
| `front-page.php` | +2 sections |
| `functions.php` | +4 fonctions (bootstrap cas-clients, url helper, seed articles, branches SEO/breadcrumbs) |
| `src/main.js` | +event listener `klem:open-chat` |
| `collaboration/doc/ard/ADR-008-refonte-positionnement-honnete.md` | Créé |

### État du projet en clôture
- `php -l` sur tous les fichiers modifiés/créés : ✅
- `pnpm build` : ✅ sans erreur
- Vérification locale via `curl` : `grep` confirmant l'absence de toute ancienne statistique fabriquée, page `/cas-clients/` et hub `/actualites/` (5 articles + article individuel `/cloud-first-pme-africaines/`) tous fonctionnels, teasers home présents
- Changements non committés à la clôture — en attente de confirmation utilisateur avant commit/push

---

## Session 18 — 2026-07-14

**Objectif :** Corriger l'affichage illisible des réponses du chatbot (Markdown brut sur une seule ligne) et trancher le choix de modèle Anthropic entre Haiku et Sonnet.

### Contexte
- Capture d'écran utilisateur : la réponse de l'assistant s'affichait en un seul bloc de texte, avec les astérisques `**...**` et tirets `-` visibles tels quels, aucun retour à la ligne entre les points de la liste
- Cause : le widget affichait le texte de l'assistant via `textContent`, qui n'exécute aucun rendu Markdown, combiné au comportement par défaut du CSS qui collapse les retours à la ligne

### Décision modèle (Haiku vs Sonnet)
- L'utilisateur a d'abord demandé à repasser sur `claude-sonnet-5` ("réponses efficaces et non superflues")
- Recommandation donnée : rester sur **Haiku 4.5** — la tâche est scriptée et courte (accueil, 1 question, extraction structurée via tool use), la concision est déjà imposée par le system prompt (indépendante du modèle), et Haiku règle directement le problème de lenteur/timeout observé en Session 17
- Décision finale utilisateur : **conserver Haiku 4.5**, réévaluer un autre modèle plus tard si besoin

### Tâches réalisées
- `src/main.js` : ajout de `renderAssistantText()` — rendu Markdown minimal et sûr (échappement HTML systématique d'abord, puis réinjection de `<strong>`/`<ul>`/`<ol>`/`<p>` uniquement). Les messages utilisateur restent en `textContent` pur.
- Gestion correcte des listes à puces et numérotées séparées par des lignes vides (ne pas refermer la liste sur une ligne blanche, sinon la numérotation repart à 1 à chaque item)
- `src/main.css` : classes `.klem-chat-paragraph` / `.klem-chat-list` en CSS pur (pas d'utilitaires Tailwind, car `main.js` n'est pas dans le `content` scanné par `tailwind.config.js`)
- Vérification : test Node.js autonome de `renderAssistantText()` (liste numérotée continue + tentative d'injection `<script>` correctement échappée), `pnpm build` ✅

### Fichiers modifiés
| Fichier | Action |
|---|---|
| `web/app/themes/klem-theme/src/main.js` | Rendu Markdown minimal (`renderAssistantText`, `renderInline`, `escapeHtml`) |
| `web/app/themes/klem-theme/src/main.css` | Classes `.klem-chat-paragraph` / `.klem-chat-list` |
| `.env.example` | Commentaire modèle mis à jour (confirmation Haiku) |

### État du projet en clôture
- Changements testés en local (build + test Node.js), non committés à la clôture — en attente de confirmation utilisateur

---

## Session 17 — 2026-07-14

**Objectif :** Corriger la lenteur/erreurs du chatbot signalées lors du premier test réel en production, et accélérer le tunnel de capture de leads (accueil immédiat, coordonnées demandées plus vite, nouveaux champs).

### Contexte
- Capture d'écran fournie par l'utilisateur : après "Oui oui", le chatbot répond "Une erreur réseau est survenue" — symptomatique d'un timeout côté API (modèle `claude-opus-4-8`, le plus lent de la gamme, avec `max_tokens: 1024`).
- Nouvelle exigence produit : accueillir le visiteur dès son arrivée sur le site (pas seulement au clic), et capturer rapidement prénom/nom/email/secteur d'activité (téléphone optionnel), en plus d'enregistrer le besoin.

### Décisions utilisateur (clarifiées avant implémentation)
1. Téléphone : conservé en **champ optionnel** (jamais demandé activement, capturé seulement si spontanément fourni)
2. Auto-ouverture du chat : **après ~4 secondes**, une seule fois par session navigateur

### Tâches réalisées

#### 1. Performance — modèle et taille de réponse
- `KLEM_ANTHROPIC_MODEL` par défaut : `claude-opus-4-8` → `claude-haiku-4-5-20251001` (`wp-config.php`, `.env.example`)
- `max_tokens` : 1024 → 400 (cohérent avec la consigne "réponses courtes" déjà présente dans le system prompt)

#### 2. Workflow conversationnel accéléré
- `inc/chatbot-system-prompt.md` : réduit à 3 étapes (accueil+accroche immédiate → 1 seule question de qualification → capture groupée en un seul message), au lieu de 4 étapes avec capture champ par champ
- `agent-prompt.md` (brouillon racine, non utilisé par le code) resynchronisé avec la version fonctionnelle pour éviter toute divergence

#### 3. Nouveau schéma de capture de leads
- Tool `capture_lead` (`inc/chatbot.php`) : `nom_complet` + `entreprise` → `prenom` + `nom` + `secteur_activite` ; `telephone` conservé mais optionnel
- `klem_chatbot_notify_lead()` et la validation dans `klem_handle_chatbot_message()` mis à jour en conséquence

#### 4. Auto-ouverture du widget à l'arrivée du visiteur
- `src/main.js` : ouverture automatique du panneau 4 s après le chargement de la page, une seule fois par session (`sessionStorage.klemChatAutoOpened`) pour ne pas être intrusif sur les pages suivantes de la même visite

#### 5. Debug — diagnostic de la lenteur observée en local
- Premier test local après les changements : timeout complet (30 s, `cURL error 28`)
- **Cause 1 identifiée et corrigée :** l'édition de `chatbot-system-prompt.md` avait involontairement changé ses permissions à `600` (au lieu de `644`) côté conteneur Docker → `file_get_contents` échouait silencieusement (`Permission denied`) et le system prompt partait vide. Corrigé par `chmod 644`.
- **Cause 2 :** des appels `curl` bruts depuis le conteneur Docker de dev vers `api.anthropic.com` montrent eux-mêmes une latence très variable (1,4 s à 30 s) — confirmé indépendant du code applicatif (WordPress bootstrap mesuré à 0,3 s, hypothèse `Expect: 100-continue` testée et écartée). **Conclusion : réseau sortant du conteneur Docker local instable, sans rapport avec la production.** Le vrai test de performance doit se faire sur `klemtech.net` après déploiement.

### Fichiers modifiés
| Fichier | Action |
|---|---|
| `web/wp-config.php` | Modèle par défaut → Haiku 4.5 |
| `.env.example` | Modèle recommandé documenté |
| `web/app/themes/klem-theme/inc/chatbot.php` | `max_tokens` réduit, schéma `capture_lead` révisé, notification email mise à jour |
| `web/app/themes/klem-theme/inc/chatbot-system-prompt.md` | Workflow 3 étapes, capture groupée |
| `web/app/themes/klem-theme/src/main.js` | Auto-ouverture du widget (4 s, 1×/session) |
| `agent-prompt.md` | Resynchronisé avec la version fonctionnelle |
| `collaboration/doc/ard/ADR-007-chatbot-lead-capture-anthropic.md` | Créé (documente la Session 16 — patron du proxy AJAX chatbot) |

### État du projet en clôture
- Correctifs testés en local (permission + schéma + capture), mais performance à valider en conditions réelles sur `klemtech.net` — le réseau local n'est pas représentatif
- Changements non committés à la clôture de cette session — en attente de confirmation utilisateur avant commit/push/déploiement
- **Rappel opérationnel :** si `KLEM_ANTHROPIC_MODEL` est explicitement défini dans le `.env` de production (Hostinger), il doit être mis à jour manuellement vers `claude-haiku-4-5-20251001` — changer le seul fallback dans `wp-config.php` ne suffit pas

---

## Session 16 — 2026-07-14

**Objectif :** Terminer et mettre en production le chatbot de capture de leads (API Anthropic).

### Contexte
- Le backend AJAX (`inc/chatbot.php`, `inc/chatbot-system-prompt.md`) et les constantes `.env`/`wp-config.php` avaient déjà été rédigés (session non journalisée), mais aucun widget visiteur n'existait — la fonctionnalité était inutilisable.
- Note : les commits `bf94181`, `e9655a4`, `a3bed90`, `3ef997f`, `65dbcec` (SEO, hub Actualités, header, sécurité Vite, nav) sont antérieurs à cette session et n'ont pas été journalisés ici — à backfiller si besoin.

### Tâches réalisées

#### 1. Widget chatbot flottant (frontend)
- `footer.php` : bouton bascule + panneau de discussion (en-tête, liste de messages, formulaire de saisie), stylé avec les classes Tailwind existantes (`klem-blue`, `klem-orange`)
- `src/main.js` : ouverture/fermeture du panneau, historique de conversation maintenu en mémoire, envoi `fetch()` vers `klemChatbotAjax` (déjà localisé par `inc/chatbot.php`), rendu des bulles (`textContent`, jamais `innerHTML`), état de chargement (spinner) et gestion d'erreur réseau/API

#### 2. Vérification
- `pnpm build` : ✅ 0 erreur
- `php -l` sur `footer.php`, `functions.php`, `inc/chatbot.php` : ✅ aucune erreur de syntaxe
- Test end-to-end réel via `curl` (nonce extrait de la page, POST direct sur `admin-ajax.php`) : réponse Anthropic correcte, cohérente avec le system prompt (étapes accueil/qualification)

#### 3. Debug production — chatbot indisponible (503)
- **Symptôme :** premier test réel depuis un téléphone → « Le chatbot est momentanément indisponible »
- **Cause :** `.env` de production (Hostinger, `~/site-klem/.env`, hors dépôt) ne contenait pas encore `KLEM_ANTHROPIC_API_KEY` / `KLEM_ANTHROPIC_MODEL` — comportement voulu du garde-fou dans `inc/chatbot.php` (pas un bug)
- **Fix :** utilisateur a ajouté la clé directement sur le `.env` serveur (aucun redéploiement nécessaire, `wp-config.php` relit le `.env` à chaque requête)
- **Validation :** nouveau test `curl` sur `https://www.klemtech.net/wp/wp-admin/admin-ajax.php` → réponse Anthropic correcte, citant les vrais services KLEM (Big Data, Applications Sur-Mesure, ERP/FleetControl, Matériel IT)

### Fichiers modifiés
| Fichier | Action |
|---|---|
| `footer.php` | Widget chatbot flottant (bouton + panneau de discussion) |
| `src/main.js` | Logique JS du chatbot (ouverture, envoi AJAX, rendu, erreurs) |
| `web/wp-config.php` | Constantes `KLEM_ANTHROPIC_API_KEY` / `KLEM_ANTHROPIC_MODEL` |
| `.env.example` | Template documenté pour les 2 nouvelles variables |
| `web/app/themes/klem-theme/inc/chatbot.php` | Backend AJAX proxy Anthropic (déjà rédigé, committé cette session) |
| `web/app/themes/klem-theme/inc/chatbot-system-prompt.md` | System prompt du workflow conversationnel (déjà rédigé, committé cette session) |
| `dist/` | Build recompilé et committé |

### Commits de la session
| Hash | Description |
|---|---|
| `95ef35e` | feat(chatbot): ajoute le widget de capture de leads (API Anthropic) |

### État du projet en clôture
- Chatbot 100 % opérationnel en production : widget visible, appel API fonctionnel, réponses conformes au system prompt
- `.env` serveur Hostinger désormais complet : DB, Brevo, Anthropic
- **Règle établie (demande explicite du client) :** à partir de cette session, `history-log.md`, `decision-log.md` et les ADR concernés doivent être mis à jour à chaque changement significatif, en clôture de tâche

---

## Session 15 — 2026-07-01

**Objectif :** Intégrer Cantine Connect (solution digitale de gestion des paiements et de contrôle d'accès cantine scolaire) parmi les produits présentés sur le site, en suivant le même schéma d'intégration que FleetControl.

### Contexte
- Nouveau produit déployé sur URL de test Vercel : `https://cantine-connect-swart.vercel.app/login`
- FleetControl n'a jamais eu de fichier de données/CPT dédié : son nom est simplement inséré en dur dans 3 tableaux PHP (`services.php`, `footer.php`, `contact.php`) — le même schéma a été reproduit pour Cantine Connect
- Contrainte explicite du client : ne pas modifier la structure du site (pas de nouvelle section, pas de 5ᵉ pilier)

### Tâches réalisées

#### 1. Mention dans le pilier "Applications Sur-Mesure"
- `template-parts/home/services.php` — description du pilier 02 enrichie pour nommer Cantine Connect (gestion des paiements + contrôle d'accès pour la restauration scolaire)

#### 2. Lien réel vers la démo dans le footer
- `footer.php` — nouvelle entrée `Cantine Connect (démo)` dans `$service_links`, pointant vers l'URL Vercel réelle (contrairement au placeholder `#` de "FleetControl SaaS")
- Ajout d'une clé `external` optionnelle sur les entrées du tableau + rendu conditionnel de `target="_blank" rel="noopener noreferrer"` dans la boucle `foreach`, sans casser les liens internes existants

#### 3. Option dans le formulaire de contact
- `template-parts/home/contact.php` — nouvelle `<option value="cantine">Cantine Connect</option>` dans le `<select id="klem-subject">`

### Fichiers modifiés
| Fichier | Action |
|---|---|
| `template-parts/home/services.php` | Description pilier "Applications Sur-Mesure" enrichie (mention Cantine Connect) |
| `footer.php` | Lien démo Cantine Connect ajouté à `$service_links` + support lien externe dans la boucle |
| `template-parts/home/contact.php` | Nouvelle option "Cantine Connect" dans le select Sujet |

### État du projet en clôture
- `pnpm build` exécuté sans erreur (Vite + Tailwind recompilés)
- Aucune structure de fichier/section modifiée, conformément à la demande

---

## Session 14 — 2026-06-30

**Objectif :** Sécuriser le formulaire de contact contre le spam (suite à un message de prospection automatisé reçu via le formulaire).

### Contexte
- Réception d'un email spam via le formulaire : "Maxton / Sample Holdings" — prospection commerciale type cold email envoyée via Brevo mass mailing
- Le formulaire n'avait aucune protection anti-bot → n'importe quel script pouvait inonder les 3 boîtes

### Tâches réalisées

#### 1. Honeypot anti-bot
- Champ `klem_website` invisible pour les humains (inline style : `position:absolute;left:-9999px;width:1px;height:1px`)
- `tabindex="-1"`, `autocomplete="off"` pour éviter que les navigateurs humains le remplissent
- Si le champ contient une valeur → faux succès retourné au bot (pas d'email envoyé)

#### 2. Vérification temporelle + jeton signé
- À l'affichage du formulaire : timestamp `klem_ts` + token `wp_hash($ts . 'klem_contact_token')` injectés en champs cachés
- Côté serveur : rejet si soumission < 3 secondes (bot rapide) ou > 1 heure (token périmé)
- Rejet si le token est falsifié (`hash_equals` avec la clé secrète WordPress)

#### 3. Rate limiting par IP
- Max 3 envois par adresse IP par heure
- Stocké via `set_transient('klem_rate_' . md5($ip), $count, HOUR_IN_SECONDS)`
- Erreur 429 après dépassement : "Trop de tentatives. Merci de réessayer dans une heure."

#### 4. Stratégie silencieuse contre les bots
- Les bots (honeypot rempli ou token invalide) reçoivent un **faux succès** identique au vrai message de confirmation
- Évite qu'ils détectent le blocage et adaptent leur stratégie

### Fichiers modifiés
| Fichier | Action |
|---|---|
| `template-parts/home/contact.php` | Ajout honeypot + champs cachés `klem_ts` / `klem_token` |
| `web/app/themes/klem-theme/functions.php` | Ajout des 3 validations anti-spam avant traitement |

### Commits de la session
| Hash | Description |
|---|---|
| `d649e26` | feat(security): protection anti-spam formulaire (honeypot + temps + rate limit) |

### État du projet en clôture
- Formulaire de contact protégé contre les soumissions automatisées
- Aucun impact visuel pour les vrais visiteurs
- Les bots sont silencieusement bloqués sans feedback exploitable

---

## Session 13 — 2026-06-30

**Objectif :** Mettre à jour le logo et le favicon du site depuis le kit branding Facebook (Claude Design).

### Tâches réalisées

#### 1. Import du kit branding depuis Claude Design
- Projet : `https://claude.ai/design/p/52ce0b76-7710-4ae2-bdbe-f2da776d54fa`
- Fichier lu : `KLEM Facebook Branding.dc.html`
- Contenu : 3 couvertures Facebook, 3 photos de profil, favicons (16 à 512px), logos PNG, flyers de lancement

#### 2. Nouveau logo — chevrons plats (header + footer)
- **Avant :** ChevronMark 3D (3 couches SVG : ombre `#A5130A`, face `#E42313`, bevel `#F0654F`)
- **Après :** 2 polygones SVG plats rouge `#E42313` — fidèles au style du kit branding
  - Forme : `clip-path polygon(0 0,55% 0,100% 50%,55% 100%,0 100%,45% 50%)` convertie en points SVG
  - Premier chevron : `0,0 22,0 40,28 22,56 0,56 18,28`
  - Second chevron (décalé) : `25,0 47,0 65,28 47,56 25,56 43,28`
  - ViewBox : `0 0 65 56`
- Mis à jour dans `header.php` (width=32, height=28) et `footer.php` (width=34, height=29)
- Commit : `320e63f`

#### 3. Nouveau favicon SVG — carré marine + chevrons plats
- Fond : carré `#13294B` avec `rx=12` (coins arrondis)
- Chevrons plats rouges `#E42313` centrés — lisibles à 16px
- ViewBox carré `64×64` pour onglet navigateur
- Commit : `85bec29`

#### 4. Favicon PNG 32×32 — depuis les assets Claude Design
- Récupéré via DesignSync MCP : `Facebook KLEM/Favicon/favicon-32.png`
- Décodé depuis base64 → `assets/favicon-32.png` (711 octets, PNG 32×32 RGBA)
- `functions.php` mis à jour : PNG en priorité (`rel="icon" type="image/png" sizes="32x32"`), SVG en fallback
- Commit : `ba2c5d0`

### Fichiers modifiés
| Fichier | Action |
|---|---|
| `web/app/themes/klem-theme/header.php` | Logo — chevrons plats (viewBox 65×56, 2 polygones) |
| `web/app/themes/klem-theme/footer.php` | Logo — même mise à jour |
| `web/app/themes/klem-theme/assets/favicon.svg` | Favicon SVG — carré marine 64×64 + chevrons plats |
| `web/app/themes/klem-theme/assets/favicon-32.png` | Ajouté — PNG officiel 32×32 du kit branding |
| `web/app/themes/klem-theme/functions.php` | favicon : PNG en priorité + SVG en fallback |

### Commits de la session
| Hash | Description |
|---|---|
| `320e63f` | feat(logo): chevron plat (Facebook branding) — remplace le 3D bevel |
| `85bec29` | feat(favicon): carré marine + chevrons plats rouges (Facebook branding) |
| `ba2c5d0` | feat(favicon): PNG 32×32 depuis le kit branding Claude Design |

### État du projet en clôture
- Logo header + footer : design plat cohérent avec le kit Facebook
- Favicon : PNG 32×32 officiel + SVG fallback — lisible dans tous les navigateurs
- Kit branding complet disponible sur Claude Design (couvertures, profils, flyers, logos)

---

## Session 12 — 2026-06-29

**Objectif :** Intégrer les comptes et pages réseaux sociaux dans le footer du site (LinkedIn, X/Twitter, GitHub).

### Tâches réalisées

#### 1. Lien LinkedIn → page entreprise KLEM Technologies & Services
- URL fournie par l'utilisateur (dashboard admin) : `https://www.linkedin.com/company/130474992/admin/dashboard/`
- URL publique utilisée : `https://www.linkedin.com/company/130474992/`
- Attributs ajoutés : `target="_blank" rel="noopener noreferrer"`
- Commit : `5b77dca`

#### 2. Lien X/Twitter → @KLEMTechnology
- URL : `https://x.com/KLEMTechnology`
- Commit : `fc9cceb`

#### 3. Lien GitHub → dépôt klem-opensource
- URL fournie avec `.git` → nettoyée : `https://github.com/yacoubasylla/klem-opensource`
- Commit : `52e6f0c`

### Fichiers modifiés
| Fichier | Action |
|---|---|
| `web/app/themes/klem-theme/footer.php` | 3 liens sociaux : `#` → URLs réelles (LinkedIn, X, GitHub) |

### Commits de la session
| Hash | Description |
|---|---|
| `5b77dca` | feat(footer): lien LinkedIn → page KLEM Technologies & Services |
| `fc9cceb` | feat(footer): lien X/Twitter → @KLEMTechnology |
| `52e6f0c` | feat(footer): lien GitHub → klem-opensource |

### État du projet en clôture
- Footer : 4 icônes sociales toutes actives (LinkedIn, X, Facebook, GitHub)
- Facebook : intégré en session précédente (commit `76b440d`)
- Présence sociale KLEM entièrement wired dans le site

---

## Session 11 — 2026-06-27

**Objectif :** Test end-to-end du formulaire de contact en production après configuration DKIM/SPF/DMARC.

### Tâches réalisées

#### 1. Test automatisé du formulaire via curl
- Récupération du nonce WordPress depuis `https://www.klemtech.net/` (valeur : `d0b950c71a`)
- Soumission POST sur `admin-ajax.php` avec action `klem_contact`, tous les champs remplis
- Réponse : `{"success":true,"data":{"message":"Votre message a bien été envoyé..."}}` ✅

#### 2. Validation réception sur les 3 boîtes
- ✅ `infos@klemtech.net` — reçu
- ✅ `yacouba.sylla@klemtech.net` — reçu
- ✅ `ciyasyl@gmail.com` — reçu

### État du projet en clôture
- Configuration email KLEM **100% opérationnelle** en production
- Formulaire → 3 destinataires, signé DKIM, conforme SPF + DMARC
- Délivrabilité maximale confirmée par test réel

---

## Session 10 — 2026-06-27

**Objectif :** Corrections UI mineures (menu mobile, espacement), debug et résolution du formulaire de contact, configuration email Hostinger.

### Tâches réalisées

#### 1. Fix menu mobile — label "Cas Clients" → "Notre Différence"
- Le `fallback_cb` du menu mobile dans `header.php` n'avait pas été mis à jour lors du renommage de la section (Session 07)
- Correction : `['label' => 'Cas Clients']` → `['label' => 'Notre Différence']`

#### 2. Debug et résolution formulaire de contact
- **Symptôme :** "Une erreur est survenue" à chaque envoi depuis klemtech.net
- **Diagnostic :** Test direct API Brevo → clé valide. Seul sender vérifié : `ciyasyl@gmail.com`
- **Cause 1 :** `KLEM_SMTP_FROM=contact@klem.tech` (local) / `infos@klemtech.net` (serveur) — aucun des deux n'est vérifié dans Brevo
- **Cause 2 :** Clé API corrompue sur le serveur : `xkeysib-xsmtpsib-...` (mélange d'une ancienne clé SMTP avec la clé REST)
- **Fix code :** `KLEM_SMTP_FROM` → `ciyasyl@gmail.com` dans `.env`, `.env.example`, `wp-config.php` (fallback)
- **Fix serveur :** deux `sed -i` sur `~/site-klem/.env` pour corriger la clé et le sender
- **Fix debug :** ajout `error_log()` dans `klem-smtp.php` pour logger clé manquante et erreurs API
- **Résultat :** ✅ Formulaire opérationnel, email reçu sur `ciyasyl@gmail.com`

#### 3. Création des adresses email professionnelles
- Souscription au plan **Free Business Email** Hostinger (gratuit 12 mois, 1 GB/boîte, 5 boîtes)
- Création de `infos@klemtech.net` et `yacouba.sylla@klemtech.net` via hPanel → Sites web → klemtech.net → "Configurer un email gratuit"
- Les deux boîtes sont actives avec webmail Hostinger

#### 4. Vérification `infos@klemtech.net` comme sender Brevo
- Ajout du sender via API Brevo (`POST /v3/senders`) → email de vérification reçu dans la boîte `infos@klemtech.net`
- Code OTP `406813` saisi manuellement sur app.brevo.com → sender activé ✅
- Avertissements notés : DKIM "Par défaut" (non bloquant) + DMARC "rua manquante" (à configurer)
- SPF `klemtech.net` : `v=spf1 include:_spf.mail.hostinger.com ~all` — à compléter avec `include:spf.brevo.com`

#### 5. Sender formulaire → `infos@klemtech.net` + 3 destinataires
- `KLEM_SMTP_FROM` mis à jour : `ciyasyl@gmail.com` → `infos@klemtech.net` dans `.env`, `.env.example`, `wp-config.php`
- Formulaire de contact : destinataire unique `get_option('admin_email')` → tableau de 3 adresses
  - `infos@klemtech.net`, `yacouba.sylla@klemtech.net`, `ciyasyl@gmail.com`
- ✅ Confirmé fonctionnel en production

### Fichiers modifiés
| Fichier | Action |
|---|---|
| `header.php` | Fix label menu mobile "Notre Différence" |
| `web/wp-config.php` | Fallback `KLEM_SMTP_FROM` → `infos@klemtech.net` |
| `.env.example` | `KLEM_SMTP_FROM` → `infos@klemtech.net` |
| `web/app/mu-plugins/klem-smtp.php` | Ajout `error_log()` pour debug clé manquante et erreur API |
| `web/app/themes/klem-theme/functions.php` | Formulaire → 3 destinataires (tableau) |

### Commits de la session
| Hash | Description |
|---|---|
| `782409a` | fix(nav/mobile): renommer 'Cas Clients' → 'Notre Différence' |
| `e8edfc9` | fix(email): expéditeur Brevo → ciyasyl@gmail.com |
| `a7943cd` | debug(email): error_log sur clé manquante et erreur Brevo API |
| `a8b70fc` | fix(email): sender officiel → infos@klemtech.net |
| `edb5068` | feat(email): formulaire de contact → 3 destinataires |

#### 6. Configuration DKIM / SPF / DMARC — authentification domaine Brevo
- Récupération des enregistrements DNS via API Brevo (`GET /v3/senders/domains/klemtech.net`)
- Ajout dans hPanel → Zone DNS → klemtech.net :
  - **SPF** (édition existant) : ajout de `include:spf.brevo.com`
  - **DKIM1** CNAME : `brevo1._domainkey` → `b1.klemtech-net.dkim.brevo.com`
  - **DKIM2** CNAME : `brevo2._domainkey` → `b2.klemtech-net.dkim.brevo.com`
  - **Code Brevo** TXT `@` : `brevo-code:54ba159c10b0deab8dd7851ddaf47571`
  - **DMARC** TXT `_dmarc` : `v=DMARC1; p=none; rua=mailto:rua@dmarc.brevo.com`
- Problèmes résolus en cours de route : doublon DMARC (ajout au lieu d'édition), propagation DNS (~15 min)
- Authentification finale : `PUT /v3/senders/domains/klemtech.net/authenticate` → **succès** ✅
- Résultat : emails signés DKIM, conformes SPF + DMARC → délivrabilité maximale

### État du projet en clôture
- Formulaire de contact ✅ : envoie depuis `infos@klemtech.net` vers 3 boîtes simultanément
- Boîtes email professionnelles actives : `infos@klemtech.net` + `yacouba.sylla@klemtech.net`
- Domaine `klemtech.net` authentifié Brevo (DKIM + SPF + DMARC) ✅
- Menu mobile cohérent avec le menu desktop
- Délivrabilité email : maximale — emails signés, conformes aux standards anti-spam
- **Note :** DKIM opère au niveau domaine — `yacouba.sylla@klemtech.net` est couvert par les mêmes enregistrements que `infos@klemtech.net`, aucune config supplémentaire nécessaire

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
