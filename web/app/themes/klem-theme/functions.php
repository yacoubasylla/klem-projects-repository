<?php

declare(strict_types=1);

require_once get_template_directory() . '/inc/chatbot.php';

function klem_theme_setup(): void {
    add_theme_support('title-tag');
    add_theme_support('post-thumbnails');
    add_theme_support('html5', ['search-form', 'comment-form', 'comment-list', 'gallery', 'caption']);
    load_theme_textdomain('klem-theme', get_template_directory() . '/languages');

    register_nav_menus([
        'primary' => esc_html__('Menu Principal', 'klem-theme'),
        'footer'  => esc_html__('Menu Pied de Page', 'klem-theme'),
    ]);
}
add_action('after_setup_theme', 'klem_theme_setup');

function klem_enqueue_fonts(): void {
    wp_enqueue_style(
        'klem-fonts',
        'https://fonts.googleapis.com/css2?family=Archivo:wght@600;700;800&display=swap',
        [],
        null
    );
}
add_action('wp_enqueue_scripts', 'klem_enqueue_fonts');

function klem_enqueue_assets(): void {
    // Archivo chargée via klem_enqueue_fonts()
    $theme_uri = get_template_directory_uri();
    $theme_dir = get_template_directory();
    $manifest  = $theme_dir . '/dist/.vite/manifest.json';

    if (defined('WP_DEBUG') && WP_DEBUG && !file_exists($manifest)) {
        // Dev mode : assets servis par Vite HMR sur localhost:5173
        add_filter('script_loader_tag', function (string $tag, string $handle): string {
            if (in_array($handle, ['vite-client', 'klem-script'], true)) {
                return str_replace('<script ', '<script type="module" ', $tag);
            }
            return $tag;
        }, 10, 2);

        wp_enqueue_script('vite-client', 'http://localhost:5173/@vite/client', [], null, false);
        wp_enqueue_script('klem-script', 'http://localhost:5173/src/main.js', [], null, false);
        return;
    }

    if (file_exists($manifest)) {
        $data  = json_decode(file_get_contents($manifest), true);
        $entry = $data['src/main.js'] ?? null;
        if ($entry) {
            if (!empty($entry['css'])) {
                wp_enqueue_style('klem-style', $theme_uri . '/dist/' . $entry['css'][0], [], null);
            }
            wp_enqueue_script('klem-script', $theme_uri . '/dist/' . $entry['file'], [], null, true);
        }
    }
}
add_action('wp_enqueue_scripts', 'klem_enqueue_assets');

function klem_enqueue_ajax_config(): void {
    wp_localize_script('klem-script', 'klemAjax', [
        'url'   => admin_url('admin-ajax.php'),
        'nonce' => wp_create_nonce('klem_contact_nonce'),
    ]);
}
add_action('wp_enqueue_scripts', 'klem_enqueue_ajax_config', 20);

function klem_handle_contact(): void {
    if (!check_ajax_referer('klem_contact_nonce', 'klem_nonce', false)) {
        wp_send_json_error(['message' => __('Requête non autorisée.', 'klem-theme')], 403);
    }

    // ── Anti-spam 1 : honeypot ────────────────────────────────────────────────
    if (!empty($_POST['klem_website'])) {
        wp_send_json_error(['message' => __('Votre message a bien été envoyé. Nous vous répondons sous 24 h.', 'klem-theme')]);
    }

    // ── Anti-spam 2 : jeton horodaté (soumission < 3 s = bot) ────────────────
    $ts    = (int) sanitize_text_field(wp_unslash($_POST['klem_ts']    ?? '0'));
    $token = sanitize_text_field(wp_unslash($_POST['klem_token'] ?? ''));
    $elapsed = time() - $ts;
    if ($elapsed < 3 || $elapsed > 3600 || !hash_equals(wp_hash($ts . 'klem_contact_token'), $token)) {
        wp_send_json_error(['message' => __('Votre message a bien été envoyé. Nous vous répondons sous 24 h.', 'klem-theme')]);
    }

    // ── Anti-spam 3 : limite de débit — max 3 envois / IP / heure ─────────────
    $ip  = sanitize_text_field(wp_unslash($_SERVER['REMOTE_ADDR'] ?? ''));
    $key = 'klem_rate_' . md5($ip);
    $hits = (int) get_transient($key);
    if ($hits >= 3) {
        wp_send_json_error(['message' => __('Trop de tentatives. Merci de réessayer dans une heure.', 'klem-theme')], 429);
    }
    set_transient($key, $hits + 1, HOUR_IN_SECONDS);

    $name    = sanitize_text_field(wp_unslash($_POST['klem_name']    ?? ''));
    $company = sanitize_text_field(wp_unslash($_POST['klem_company'] ?? ''));
    $email   = sanitize_email(wp_unslash($_POST['klem_email']        ?? ''));
    $phone   = sanitize_text_field(wp_unslash($_POST['klem_phone']   ?? ''));
    $subject = sanitize_text_field(wp_unslash($_POST['klem_subject'] ?? ''));
    $message = sanitize_textarea_field(wp_unslash($_POST['klem_message'] ?? ''));

    if (!$name || !$email || !$subject || !$message) {
        wp_send_json_error(['message' => __('Veuillez remplir tous les champs obligatoires.', 'klem-theme')], 422);
    }

    if (!is_email($email)) {
        wp_send_json_error(['message' => __('Adresse e-mail invalide.', 'klem-theme')], 422);
    }

    $to = ['infos@klemtech.net', 'yacouba.sylla@klemtech.net', 'ciyasyl@gmail.com'];
    $headers = [
        'Content-Type: text/plain; charset=UTF-8',
        sprintf('Reply-To: %s <%s>', $name, $email),
    ];

    $body = sprintf(
        "Nom : %s\nSociété : %s\nE-mail : %s\nTéléphone : %s\nSujet : %s\n\nMessage :\n%s",
        $name, $company, $email, $phone, $subject, $message
    );

    $sent = wp_mail($to, sprintf('[KLEM] Contact – %s', $subject), $body, $headers);

    if ($sent) {
        wp_send_json_success(['message' => __('Votre message a bien été envoyé. Nous vous répondons sous 24 h.', 'klem-theme')]);
    } else {
        wp_send_json_error(['message' => __('Une erreur est survenue. Merci de réessayer ou de nous contacter directement par e-mail.', 'klem-theme')], 500);
    }
}
add_action('wp_ajax_klem_contact',        'klem_handle_contact');
add_action('wp_ajax_nopriv_klem_contact', 'klem_handle_contact');

/**
 * ── Actualités : catégories + page hub ──────────────────────────────────
 * Crée automatiquement (une seule fois, de façon idempotente) les 3
 * catégories utilisées par le hub Actualités ainsi que la page qui
 * l'affiche, pour que le lien de menu fonctionne dès l'activation du
 * thème sans intervention manuelle en base de données.
 */

/**
 * ── Fondations SEO techniques ────────────────────────────────────────────
 * Corrige, une seule fois et de façon idempotente, deux réglages WordPress
 * qui pénalisent le référencement s'ils restent sur leur valeur par défaut :
 * les permaliens « bruts » (?p=123, mauvais pour l'indexation et le partage)
 * et le nom de site mal casé (utilisé dans toutes les balises <title>).
 */
function klem_bootstrap_seo_settings(): void {
    if (get_option('permalink_structure') === '') {
        update_option('permalink_structure', '/%postname%/');
        flush_rewrite_rules();
    }

    if (html_entity_decode(get_option('blogname'), ENT_QUOTES) === 'Klem Technologies & Services') {
        update_option('blogname', 'KLEM Technologies & Services');
    }

    if (get_option('blogdescription') === '') {
        update_option('blogdescription', "Intégrateur numérique de référence en Afrique de l'Ouest");
    }
}
add_action('init', 'klem_bootstrap_seo_settings', 5);

function klem_actualites_categories(): array {
    return [
        'blog'       => 'Blog',
        'actus'      => 'Actualités',
        'evenements' => 'Événements',
    ];
}

function klem_bootstrap_actualites(): void {
    foreach (klem_actualites_categories() as $slug => $name) {
        if (!term_exists($slug, 'category')) {
            wp_insert_term($name, 'category', ['slug' => $slug]);
        }
    }

    if (!get_page_by_path('actualites')) {
        $page_id = wp_insert_post([
            'post_title'   => __('Actualités', 'klem-theme'),
            'post_name'    => 'actualites',
            'post_type'    => 'page',
            'post_status'  => 'publish',
            'post_content' => '',
        ]);

        if ($page_id && !is_wp_error($page_id)) {
            update_post_meta($page_id, '_wp_page_template', 'page-actualites.php');
        }
    }
}
add_action('init', 'klem_bootstrap_actualites', 20);

function klem_actualites_url(): string {
    $page = get_page_by_path('actualites');
    return $page ? get_permalink($page) : home_url('/');
}

/**
 * ── Page Cas Clients ─────────────────────────────────────────────────────
 * Crée, une seule fois et de façon idempotente, la page présentant des cas
 * d'usage illustratifs (KLEM étant une jeune structure, aucun cas client
 * réel n'est encore public — cf. DEC-012 et DEC-032).
 */
function klem_bootstrap_cas_clients(): void {
    if (!get_page_by_path('cas-clients')) {
        $page_id = wp_insert_post([
            'post_title'   => __('Cas Clients', 'klem-theme'),
            'post_name'    => 'cas-clients',
            'post_type'    => 'page',
            'post_status'  => 'publish',
            'post_content' => '',
        ]);

        if ($page_id && !is_wp_error($page_id)) {
            update_post_meta($page_id, '_wp_page_template', 'page-cas-clients.php');
        }
    }
}
add_action('init', 'klem_bootstrap_cas_clients', 20);

function klem_cas_clients_url(): string {
    $page = get_page_by_path('cas-clients');
    return $page ? get_permalink($page) : home_url('/');
}

/**
 * ── Contenu d'amorçage — Actualités ──────────────────────────────────────
 * Insère, une seule fois et de façon idempotente (vérification par
 * post_name avant insertion), 5 articles réels de fond sur les tendances
 * data/IA/commerce digital pour que le hub Actualités ne soit plus vide.
 * Tous classés en catégorie "Blog" (voir DEC-033 : on ne crée pas de
 * nouvelles catégories pour ce premier lot d'articles).
 */
function klem_bootstrap_seed_articles(): void {
    $blog_term  = get_term_by('slug', 'blog', 'category');
    $categories = $blog_term ? [(int) $blog_term->term_id] : [];

    $articles = [
        [
            'slug'    => 'temps-reel-data-streaming',
            'title'   => "Le temps réel s'impose : ce que le streaming de données change pour votre organisation",
            'excerpt' => "Le traitement par lots (batch) cède du terrain face aux flux de données en continu. Ce que cela signifie concrètement pour une organisation qui veut rester réactive.",
            'content' => "Pendant longtemps, la donnée d'entreprise a été traitée par lots : on collecte toute la journée, puis on lance un traitement la nuit pour produire les rapports du lendemain. Ce modèle a un coût caché — la décision arrive toujours avec un temps de retard sur la réalité du terrain.\n\nLes architectures de données en temps réel (souvent bâties autour d'outils comme Kafka ou Spark Streaming) changent cette équation : chaque événement — une vente, un capteur de flotte, une transaction — est traité au fil de l'eau, dès qu'il se produit. Concrètement, cela veut dire qu'un tableau de bord logistique reflète la position d'un véhicule il y a quelques secondes, pas celle d'hier soir.\n\nPour une organisation, l'intérêt n'est pas seulement technique. C'est un changement de posture : on passe d'une gestion a posteriori (« que s'est-il passé ? ») à une gestion en direct (« que se passe-t-il, et que dois-je faire maintenant ? »). Dans le transport, cela permet de détecter un écart d'itinéraire ou une surconsommation de carburant avant qu'elle ne devienne un problème budgétaire. Dans le commerce, cela permet d'ajuster un stock ou une promotion pendant qu'elle produit encore de l'effet.\n\nLe temps réel n'est cependant pas une fin en soi. Il faut l'aborder avec pragmatisme : toutes les données n'ont pas besoin d'être traitées à la seconde près, et une architecture temps réel mal dimensionnée coûte plus cher qu'elle ne rapporte. La bonne question n'est pas « avons-nous besoin de temps réel ? » mais « quelles décisions, dans notre activité, perdent réellement de la valeur si elles attendent un jour de plus ? »\n\nC'est cette question que nous posons systématiquement avant de concevoir un pipeline de données pour un client : identifier les flux qui méritent un traitement continu, et laisser les autres au traitement par lots, plus simple et moins coûteux à opérer. L'objectif reste le même — transformer la donnée brute en décision utile, au bon moment.",
        ],
        [
            'slug'    => 'gouvernance-donnees-ia-fiable',
            'title'   => "Gouvernance des données : la fondation invisible d'une IA fiable",
            'excerpt' => "Avant de parler d'intelligence artificielle, il faut parler de la qualité et de la gouvernance des données qui l'alimentent. Sans cette fondation, l'IA hérite de tous les défauts de vos données.",
            'content' => "L'intelligence artificielle fascine par ses promesses — automatisation, prédiction, assistants intelligents. Mais un principe simple reste trop souvent oublié : une IA ne peut pas être plus fiable que les données sur lesquelles elle a été construite. C'est le principe du « garbage in, garbage out » — des données de mauvaise qualité produisent des résultats de mauvaise qualité, quelle que soit la sophistication du modèle utilisé.\n\nLa gouvernance des données consiste précisément à s'assurer que les données d'une organisation sont fiables, cohérentes et traçables avant même d'envisager d'y appliquer de l'IA. Cela recouvre plusieurs pratiques concrètes : définir clairement qui est responsable de chaque donnée, standardiser les formats entre les différents outils utilisés (CRM, logiciel de caisse, ERP), détecter et corriger les doublons ou incohérences, et documenter d'où vient chaque donnée et comment elle a été transformée.\n\nC'est ce qu'on appelle le DataOps — l'équivalent, pour la donnée, de ce que les équipes techniques appliquent déjà à leurs logiciels : des processus rigoureux, automatisés autant que possible, pour garantir la qualité en continu plutôt que de la corriger après coup.\n\nPour une organisation qui démarre sa transformation numérique, la tentation est grande de vouloir aller directement à l'IA — un chatbot, un moteur de recommandation, un outil prédictif. Mais sans une base de données propre et gouvernée, ces projets échouent silencieusement : le chatbot répond à côté, la prédiction est fausse une fois sur deux, et la confiance dans l'outil s'effondre.\n\nNotre recommandation est simple : avant tout projet d'IA, investissez dans la qualité et la gouvernance de vos données existantes. C'est un travail moins spectaculaire qu'un assistant intelligent, mais c'est lui qui détermine si vos futurs projets d'IA tiendront leurs promesses ou décevront dès les premières semaines d'utilisation.",
        ],
        [
            'slug'    => 'agents-ia-automatisation-metier',
            'title'   => "Assistants et agents IA : de l'expérimentation à l'automatisation des tâches métier",
            'excerpt' => "Après les chatbots conversationnels, une nouvelle génération d'agents IA capables d'exécuter des tâches de bout en bout arrive dans les organisations. Comment distinguer l'effet de mode de l'usage réellement utile ?",
            'content' => "Les assistants conversationnels ont familiarisé les entreprises avec l'idée de dialoguer avec un outil d'intelligence artificielle. Mais une évolution plus profonde est en cours : celle des agents IA, capables non seulement de répondre à une question, mais d'exécuter une suite d'actions pour accomplir une tâche métier de bout en bout.\n\nConcrètement, la différence est la suivante. Un assistant conversationnel classique répond à une question posée. Un agent IA peut, par exemple, qualifier un contact commercial, vérifier une information dans une base de données, préparer un document et notifier la bonne personne — sans intervention humaine à chaque étape. C'est un changement d'échelle : on passe de l'assistance ponctuelle à l'automatisation d'un processus complet.\n\nPour une organisation, l'intérêt le plus immédiat se trouve dans les tâches répétitives à faible valeur ajoutée mais à fort volume : le tri et la première réponse aux demandes entrantes, la préqualification de prospects, la génération de rapports périodiques, ou le suivi de premier niveau d'une flotte ou d'un stock. Ce sont des tâches qui occupent du temps humain sans exiger de jugement complexe à chaque occurrence.\n\nLa vigilance reste toutefois de mise. Un agent IA doit être conçu avec des garde-fous clairs : quelles décisions peut-il prendre seul, et lesquelles doivent impérativement remonter à un humain ? Un agent qui capture les coordonnées d'un prospect peut agir seul ; un agent qui engage financièrement l'organisation ne le devrait pas, sans validation.\n\nC'est l'approche que nous privilégions lorsque nous déployons ce type de solution : commencer par un périmètre restreint et bien défini, mesurer les résultats, puis étendre progressivement l'autonomie de l'agent à mesure que la confiance s'installe. L'automatisation la plus utile n'est pas la plus spectaculaire, mais celle qui libère réellement du temps humain pour des tâches à plus forte valeur ajoutée.",
        ],
        [
            'slug'    => 'cloud-first-pme-africaines',
            'title'   => "Cloud-first : pourquoi les plateformes de données modernes profitent aussi aux PME africaines",
            'excerpt' => "Le cloud n'est plus réservé aux grands groupes. Les plateformes de données modernes rendent l'ingénierie des données accessible aux PME, sans investissement massif en infrastructure.",
            'content' => "Pendant longtemps, mettre en place une infrastructure de données performante supposait d'investir dans des serveurs physiques, du personnel dédié à leur maintenance, et des délais de mise en œuvre longs. Cette barrière à l'entrée écartait de fait la plupart des PME de ce type de projet, réservé aux grandes organisations disposant de budgets IT conséquents.\n\nL'approche cloud-first change cette réalité. Les plateformes de données modernes permettent de louer, à la demande, une puissance de calcul et de stockage qui s'ajuste à l'usage réel de l'organisation — sans investissement initial en matériel, sans équipe dédiée à la maintenance des serveurs, avec une mise en œuvre qui se compte en semaines plutôt qu'en mois.\n\nPour une PME africaine, cette évolution est particulièrement pertinente. Elle permet d'accéder aux mêmes briques technologiques qu'une grande entreprise — entrepôt de données, pipelines automatisés, outils d'analyse — sans devoir supporter les coûts fixes qui allaient historiquement avec. Le modèle de facturation à l'usage aligne aussi la dépense sur la valeur réellement produite : on paie pour ce qu'on utilise, pas pour une capacité dimensionnée « au cas où ».\n\nCela ne signifie pas que le cloud est une solution universelle sans réflexion préalable. Le choix du fournisseur, la localisation des données, et la maîtrise des coûts dans la durée restent des sujets à traiter sérieusement dès la conception du projet — un projet cloud mal dimensionné peut, à l'usage, coûter plus cher qu'anticipé.\n\nNotre conviction reste que le cloud-first démocratise l'accès à une ingénierie des données de qualité pour des organisations qui, il y a quelques années encore, en étaient exclues par la seule barrière du coût d'entrée. C'est une opportunité concrète pour les PME qui veulent structurer leurs données sans attendre d'avoir la taille d'un grand groupe.",
        ],
        [
            'slug'    => 'commerce-omnicanal-ia-personnalisation',
            'title'   => "Commerce omnicanal : comment l'IA personnalise l'expérience client à grande échelle",
            'excerpt' => "Le client d'aujourd'hui navigue entre boutique physique, site web et réseaux sociaux. L'IA permet de lui offrir une expérience cohérente et personnalisée, quel que soit le canal.",
            'content' => "Le parcours d'achat n'est plus linéaire. Un client peut découvrir un produit sur les réseaux sociaux, comparer les prix sur un site web, puis finaliser son achat en boutique physique — ou l'inverse. On appelle cela le commerce omnicanal : une même expérience de marque, cohérente, quel que soit le canal emprunté par le client.\n\nLe défi pour une organisation est de taille : comment offrir une expérience personnalisée et cohérente sur plusieurs canaux, sans dupliquer les efforts ni multiplier les incohérences ? C'est précisément là que l'intelligence artificielle apporte une valeur concrète. En croisant les données de navigation, d'achat et d'interaction d'un client sur l'ensemble des canaux, elle permet de construire une vision unifiée de ce client — et d'adapter en conséquence les recommandations, les promotions ou le contenu qui lui sont présentés, où qu'il se trouve dans son parcours.\n\nConcrètement, cela peut prendre la forme d'un moteur de recommandation qui tient compte de l'historique d'achat en boutique pour personnaliser le site web, ou d'un service client capable de reprendre une conversation entamée sur les réseaux sociaux sans que le client ait à tout réexpliquer. Le social commerce — l'achat directement depuis les réseaux sociaux — s'inscrit dans cette même logique : rencontrer le client là où il se trouve déjà, plutôt que de l'obliger à changer de canal pour acheter.\n\nPour les organisations qui démarrent leur transformation digitale, l'omnicanal ne nécessite pas de tout reconstruire d'un coup. Il commence par une étape plus modeste mais structurante : unifier les données client dispersées entre les différents outils utilisés, pour ensuite construire progressivement des expériences personnalisées et cohérentes sur les canaux qui comptent le plus pour l'activité concernée.",
        ],
    ];

    foreach ($articles as $article) {
        if (get_page_by_path($article['slug'], OBJECT, 'post')) {
            continue;
        }

        wp_insert_post([
            'post_title'    => $article['title'],
            'post_name'     => $article['slug'],
            'post_type'     => 'post',
            'post_status'   => 'publish',
            'post_content'  => $article['content'],
            'post_excerpt'  => $article['excerpt'],
            'post_category' => $categories,
        ]);
    }
}
add_action('init', 'klem_bootstrap_seed_articles', 21);

/**
 * URL vers une ancre des sections de la page d'accueil (front-page.php).
 * Toujours préfixée par home_url() pour fonctionner depuis n'importe quelle
 * page du site (Actualités, article...), pas seulement depuis l'accueil.
 */
function klem_home_anchor(string $anchor = ''): string {
    return home_url('/') . $anchor;
}

/**
 * Retourne le badge (nom + couleur) de la catégorie Actualités d'un article,
 * ou null si l'article n'appartient à aucune des 3 catégories du hub.
 */
function klem_actualites_badge(int $post_id): ?array {
    $colors = [
        'blog'       => 'bg-blue-600',
        'actus'      => 'bg-klem-blue',
        'evenements' => 'bg-klem-red',
    ];

    foreach (klem_actualites_categories() as $slug => $name) {
        if (has_category($slug, $post_id)) {
            return [
                'slug'  => $slug,
                'name'  => $name,
                'color' => $colors[$slug],
            ];
        }
    }

    return null;
}

function klem_add_favicon(): void {
    $uri = get_template_directory_uri();
    printf('<link rel="icon" type="image/png" sizes="32x32" href="%s">' . "\n", esc_url($uri . '/assets/favicon-32.png'));
    printf('<link rel="icon" type="image/svg+xml" href="%s">' . "\n", esc_url($uri . '/assets/favicon.svg'));
    printf('<link rel="shortcut icon" href="%s">' . "\n", esc_url($uri . '/assets/favicon-32.png'));
}
add_action('wp_head', 'klem_add_favicon', 1);

function klem_disable_emoji(): void {
    remove_action('wp_head', 'print_emoji_detection_script', 7);
    remove_action('wp_print_styles', 'print_emoji_styles');
    remove_action('admin_print_scripts', 'print_emoji_detection_script');
    remove_action('admin_print_styles', 'print_emoji_styles');
    remove_filter('the_content_feed', 'wp_staticize_emoji');
    remove_filter('comment_text_rss', 'wp_staticize_emoji');
    remove_filter('wp_mail', 'wp_staticize_emoji_for_email');
}
add_action('init', 'klem_disable_emoji');

/**
 * ── SEO : titre, meta description, canonical, Open Graph, Twitter Card, JSON-LD ──
 * Couvre la page d'accueil, le hub Actualités et chaque article individuel
 * (les seuls points d'entrée indexables du site à ce jour) via un contexte
 * unique construit par klem_seo_context(), pour éviter de dupliquer la
 * logique par type de page.
 */

function klem_seo_description(): string {
    return __(
        "KLEM Technologies & Services, intégrateur numérique basé à Abidjan : ingénierie Big Data, applications sur-mesure, ERP et infrastructures IT pour l'Afrique de l'Ouest.",
        'klem-theme'
    );
}

/**
 * Construit le contexte SEO (titre, description, url, image, type) de la
 * page actuellement affichée, ou null si aucune surcharge ne s'applique
 * (le thème laisse alors WordPress gérer le titre par défaut).
 */
function klem_seo_context(): ?array {
    $theme_uri     = get_template_directory_uri();
    $default_image = $theme_uri . '/assets/images/services/service-big-data.jpg';
    $site_name     = 'KLEM Technologies & Services';

    if (is_front_page()) {
        return [
            'title'       => __("KLEM Technologies & Services | Intégrateur Numérique à Abidjan – Big Data, ERP & Développement Sur-Mesure", 'klem-theme'),
            'description' => klem_seo_description(),
            'url'         => home_url('/'),
            'image'       => $default_image,
            'type'        => 'website',
        ];
    }

    if (is_singular('post')) {
        $excerpt = get_the_excerpt();
        $image   = has_post_thumbnail() ? get_the_post_thumbnail_url(get_the_ID(), 'large') : $default_image;

        return [
            'title'       => get_the_title() . ' – ' . $site_name,
            'description' => $excerpt ? wp_strip_all_tags($excerpt) : klem_seo_description(),
            'url'         => get_permalink(),
            'image'       => $image,
            'type'        => 'article',
        ];
    }

    if (is_page('actualites')) {
        return [
            'title'       => __('Actualités', 'klem-theme') . ' – ' . $site_name,
            'description' => __("Blog, actualités et événements de KLEM Technologies & Services : innovations, projets et rendez-vous de l'écosystème numérique ouest-africain.", 'klem-theme'),
            'url'         => klem_actualites_url(),
            'image'       => $default_image,
            'type'        => 'website',
        ];
    }

    if (is_page('cas-clients')) {
        return [
            'title'       => __('Cas Clients', 'klem-theme') . ' – ' . $site_name,
            'description' => __("Comment KLEM Technologies & Services aide les organisations à réussir leurs projets numériques : cas d'usage en logistique, éducation et commerce.", 'klem-theme'),
            'url'         => klem_cas_clients_url(),
            'image'       => $default_image,
            'type'        => 'website',
        ];
    }

    // Repli générique : toute autre page/article publié(e) reste couvert(e)
    // (titre + canonical propre) sans dupliquer la balise canonical de coeur.
    if (is_singular()) {
        $excerpt = get_the_excerpt();

        return [
            'title'       => get_the_title() . ' – ' . $site_name,
            'description' => $excerpt ? wp_strip_all_tags($excerpt) : klem_seo_description(),
            'url'         => get_permalink(),
            'image'       => $default_image,
            'type'        => 'website',
        ];
    }

    return null;
}

function klem_seo_title(string $title): string {
    $context = klem_seo_context();
    return $context['title'] ?? $title;
}
add_filter('pre_get_document_title', 'klem_seo_title');

// Déclaration de langue correcte : contenu 100 % français (corrige lang="en-US" par défaut)
add_filter('language_attributes', function (): string {
    return 'lang="fr-FR"';
});

// La balise canonical est gérée par klem_seo_meta_tags() pour tout le
// contexte couvert par klem_seo_context() : on retire celle du cœur pour
// éviter deux <link rel="canonical"> sur une même page.
remove_action('wp_head', 'rel_canonical');

function klem_seo_meta_tags(): void {
    $context = klem_seo_context();
    if (!$context) {
        return;
    }

    printf('<meta name="description" content="%s">' . "\n", esc_attr($context['description']));
    printf('<link rel="canonical" href="%s">' . "\n", esc_url($context['url']));

    printf('<meta property="og:type" content="%s">' . "\n", esc_attr($context['type']));
    printf('<meta property="og:site_name" content="KLEM Technologies & Services">' . "\n");
    printf('<meta property="og:locale" content="fr_FR">' . "\n");
    printf('<meta property="og:url" content="%s">' . "\n", esc_url($context['url']));
    printf('<meta property="og:title" content="%s">' . "\n", esc_attr($context['title']));
    printf('<meta property="og:description" content="%s">' . "\n", esc_attr($context['description']));
    printf('<meta property="og:image" content="%s">' . "\n", esc_url($context['image']));

    printf('<meta name="twitter:card" content="summary_large_image">' . "\n");
    printf('<meta name="twitter:title" content="%s">' . "\n", esc_attr($context['title']));
    printf('<meta name="twitter:description" content="%s">' . "\n", esc_attr($context['description']));
    printf('<meta name="twitter:image" content="%s">' . "\n", esc_url($context['image']));
}
add_action('wp_head', 'klem_seo_meta_tags', 2);

function klem_seo_structured_data(): void {
    if (!is_front_page()) {
        return;
    }

    $theme_uri = get_template_directory_uri();

    $data = [
        '@context'     => 'https://schema.org',
        '@type'        => 'ProfessionalService',
        'name'         => 'KLEM Technologies & Services',
        'alternateName' => 'KLEM',
        'url'          => home_url('/'),
        'logo'         => $theme_uri . '/assets/svg/klem-primary.svg',
        'image'        => $theme_uri . '/assets/images/services/service-big-data.jpg',
        'description'  => klem_seo_description(),
        'telephone'    => '+225 07 58 89 24 77',
        'email'        => 'infos@klemtech.net',
        'address'      => [
            '@type'           => 'PostalAddress',
            'streetAddress'   => 'Treichville Arras 1',
            'addressLocality' => 'Abidjan',
            'addressCountry'  => 'CI',
        ],
        'areaServed'   => ["Côte d'Ivoire", 'Afrique de l\'Ouest', 'Afrique'],
        'sameAs'       => [
            'https://www.linkedin.com/company/130474992/',
            'https://x.com/KLEMTechnology',
            'https://www.facebook.com/profile.php?id=61591353966112',
            'https://github.com/yacoubasylla/klem-opensource',
        ],
        'openingHoursSpecification' => [
            [
                '@type'    => 'OpeningHoursSpecification',
                'dayOfWeek' => ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
                'opens'    => '08:00',
                'closes'   => '18:00',
            ],
            [
                '@type'    => 'OpeningHoursSpecification',
                'dayOfWeek' => ['Saturday'],
                'opens'    => '09:00',
                'closes'   => '13:00',
            ],
        ],
        'knowsAbout'   => [
            'Ingénierie des données (Big Data)',
            'Intégration ERP',
            "Développement d'applications sur-mesure",
            'Infrastructures IT',
            'Gestion de flotte (FleetControl)',
            'Gestion de restauration scolaire (Cantine Connect)',
        ],
    ];

    echo '<script type="application/ld+json">' . wp_json_encode($data, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES) . '</script>' . "\n";
}
add_action('wp_head', 'klem_seo_structured_data', 3);

/**
 * JSON-LD Article : améliore l'apparence des articles du hub Actualités
 * dans les résultats de recherche (image, dates, auteur, fil d'ariane).
 */
function klem_seo_article_schema(): void {
    if (!is_singular('post')) {
        return;
    }

    $theme_uri = get_template_directory_uri();
    $image     = has_post_thumbnail() ? get_the_post_thumbnail_url(get_the_ID(), 'large') : ($theme_uri . '/assets/images/services/service-big-data.jpg');
    $excerpt   = get_the_excerpt();
    $badge     = klem_actualites_badge(get_the_ID());

    $data = [
        '@context'         => 'https://schema.org',
        '@type'            => 'Article',
        'headline'         => get_the_title(),
        'description'      => $excerpt ? wp_strip_all_tags($excerpt) : klem_seo_description(),
        'image'            => [$image],
        'datePublished'    => get_the_date('c'),
        'dateModified'     => get_the_modified_date('c'),
        'mainEntityOfPage' => ['@type' => 'WebPage', '@id' => get_permalink()],
        'author'           => ['@type' => 'Organization', 'name' => 'KLEM Technologies & Services'],
        'publisher'        => [
            '@type' => 'Organization',
            'name'  => 'KLEM Technologies & Services',
            'logo'  => ['@type' => 'ImageObject', 'url' => $theme_uri . '/assets/svg/klem-primary.svg'],
        ],
    ];

    if ($badge) {
        $data['articleSection'] = $badge['name'];
    }

    echo '<script type="application/ld+json">' . wp_json_encode($data, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES) . '</script>' . "\n";
}
add_action('wp_head', 'klem_seo_article_schema', 3);

/**
 * JSON-LD BreadcrumbList : fil d'ariane du hub Actualités et des articles.
 */
function klem_seo_breadcrumbs_schema(): void {
    if (is_singular('post')) {
        $items = [
            ['name' => __('Accueil', 'klem-theme'), 'url' => home_url('/')],
            ['name' => __('Actualités', 'klem-theme'), 'url' => klem_actualites_url()],
            ['name' => get_the_title(), 'url' => get_permalink()],
        ];
    } elseif (is_page('actualites')) {
        $items = [
            ['name' => __('Accueil', 'klem-theme'), 'url' => home_url('/')],
            ['name' => __('Actualités', 'klem-theme'), 'url' => klem_actualites_url()],
        ];
    } elseif (is_page('cas-clients')) {
        $items = [
            ['name' => __('Accueil', 'klem-theme'), 'url' => home_url('/')],
            ['name' => __('Cas Clients', 'klem-theme'), 'url' => klem_cas_clients_url()],
        ];
    } else {
        return;
    }

    $list_items = [];
    foreach ($items as $position => $item) {
        $list_items[] = [
            '@type'    => 'ListItem',
            'position' => $position + 1,
            'name'     => $item['name'],
            'item'     => $item['url'],
        ];
    }

    $data = [
        '@context'        => 'https://schema.org',
        '@type'           => 'BreadcrumbList',
        'itemListElement' => $list_items,
    ];

    echo '<script type="application/ld+json">' . wp_json_encode($data, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES) . '</script>' . "\n";
}
add_action('wp_head', 'klem_seo_breadcrumbs_schema', 4);

/**
 * ── Robots : pages fines / dupliquées exclues de l'index ─────────────────
 * Résultats de recherche, archives catégorie/auteur/date et pages d'erreur
 * dupliquent le contenu déjà indexé via le hub /actualites/ et les pages
 * dédiées — on les garde crawlables (follow) mais hors index pour ne pas
 * diluer le référencement sur des pages sans valeur propre pour un visiteur
 * venu des résultats de recherche.
 */
function klem_seo_robots(array $robots): array {
    if (is_search() || is_category() || is_tag() || is_date() || is_author() || is_404()) {
        $robots['noindex']  = true;
        $robots['follow']   = true;
    }

    return $robots;
}
add_filter('wp_robots', 'klem_seo_robots');

/**
 * Alt text de repli : le titre de l'article quand l'image à la une n'a pas
 * de texte alternatif renseigné dans la médiathèque (sinon <img alt=""> nuit
 * à l'accessibilité et au référencement image).
 */
function klem_default_attachment_alt(array $attr): array {
    if (empty($attr['alt']) && is_singular()) {
        $attr['alt'] = get_the_title();
    }

    return $attr;
}
add_filter('wp_get_attachment_image_attributes', 'klem_default_attachment_alt');

/**
 * ── Durcissement WordPress core ──────────────────────────────────────────
 * XML-RPC (surface de brute-force/DDoS inutile ici), version WP visible en
 * clair (facilite le ciblage d'exploits connus), et énumération de comptes
 * via l'API REST ou l'URL ?author=N.
 */
add_filter('xmlrpc_enabled', '__return_false');
remove_action('wp_head', 'wp_generator');
add_filter('the_generator', '__return_empty_string');

add_filter('rest_endpoints', function (array $endpoints): array {
    unset($endpoints['/wp/v2/users'], $endpoints['/wp/v2/users/(?P<id>[\d]+)']);
    return $endpoints;
});

add_action('template_redirect', function (): void {
    if (is_author() && !is_user_logged_in() && get_query_var('author_name') === '' && get_query_var('author') !== '') {
        wp_safe_redirect(home_url('/'), 301);
        exit;
    }
});

// Message de connexion générique : ne pas révéler si un identifiant existe
// ou non (empêche l'énumération de comptes via le formulaire wp-login.php).
add_filter('login_errors', function (): string {
    return __('Identifiants incorrects.', 'klem-theme');
});
