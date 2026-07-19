<?php
/**
 * Template Name: Actualités
 *
 * Hub Actualités : article vedette + grille filtrable (Tout / Blog /
 * Actualités / Événements) sur les 3 catégories dédiées créées dans
 * functions.php (klem_bootstrap_actualites).
 */

get_header();

$klem_tabs = ['' => __('Tout', 'klem-theme')] + klem_actualites_categories();

$klem_copy = [
    ''           => [
        'title'    => __('Actualités KLEM', 'klem-theme'),
        'subtitle' => __("L'actualité de KLEM Technologies & Services : innovations, actualités et rendez-vous de l'écosystème numérique.", 'klem-theme'),
    ],
    'blog'       => [
        'title'    => __('Blog', 'klem-theme'),
        'subtitle' => __('Analyses et articles techniques de nos experts — ingénierie des données, ERP, infrastructures et transformation numérique.', 'klem-theme'),
    ],
    'actus'      => [
        'title'    => __('Actualités', 'klem-theme'),
        'subtitle' => __('Les dernières nouvelles de KLEM Technologies & Services.', 'klem-theme'),
    ],
    'evenements' => [
        'title'    => __('Événements', 'klem-theme'),
        'subtitle' => __("KLEM sur le terrain : conférences, salons et rendez-vous de l'écosystème numérique.", 'klem-theme'),
    ],
];

$klem_filtre = isset($_GET['filtre']) ? sanitize_key(wp_unslash($_GET['filtre'])) : '';
if (!array_key_exists($klem_filtre, $klem_tabs)) {
    $klem_filtre = '';
}

$klem_cat_ids = [];
foreach (klem_actualites_categories() as $klem_slug => $klem_name) {
    $klem_term = get_term_by('slug', $klem_slug, 'category');
    if ($klem_term) {
        $klem_cat_ids[$klem_slug] = (int) $klem_term->term_id;
    }
}

$klem_query_args = [
    'post_type'           => 'post',
    'post_status'         => 'publish',
    'posts_per_page'      => 13,
    'ignore_sticky_posts' => true,
];

if ($klem_filtre !== '' && isset($klem_cat_ids[$klem_filtre])) {
    $klem_query_args['cat'] = $klem_cat_ids[$klem_filtre];
} elseif (!empty($klem_cat_ids)) {
    $klem_query_args['category__in'] = array_values($klem_cat_ids);
}

$klem_news    = new WP_Query($klem_query_args);
$klem_posts   = $klem_news->posts;
$klem_page    = $klem_copy[$klem_filtre];
$klem_featured = null;

if ($klem_filtre === '' && !empty($klem_posts)) {
    $klem_featured = array_shift($klem_posts);
}
?>

<main id="main-content" class="pt-24">

    <!-- Hero -->
    <section class="pt-16 pb-10 lg:pt-20 lg:pb-14 bg-white">
        <div class="max-w-4xl mx-auto px-4 sm:px-6 text-center" data-animate>
            <span class="inline-block text-klem-red font-bold tracking-widest text-xs uppercase mb-5">
                <?php esc_html_e('Blog & Actualités', 'klem-theme'); ?>
            </span>
            <h1 class="text-3xl lg:text-5xl font-heading text-klem-blue leading-tight mb-4">
                <?php echo esc_html($klem_page['title']); ?>
            </h1>
            <p class="text-gray-500 text-lg leading-relaxed max-w-2xl mx-auto">
                <?php echo esc_html($klem_page['subtitle']); ?>
            </p>

            <!-- Onglets filtres -->
            <nav class="flex flex-wrap items-center justify-center gap-2.5 mt-10" aria-label="<?php esc_attr_e('Filtrer les articles', 'klem-theme'); ?>">
                <?php foreach ($klem_tabs as $klem_slug => $klem_label) :
                    $klem_is_active = ($klem_slug === $klem_filtre);
                    $klem_url = ($klem_slug === '')
                        ? klem_actualites_url()
                        : add_query_arg('filtre', $klem_slug, klem_actualites_url());
                    $klem_btn_class = $klem_is_active
                        ? 'bg-klem-red text-white'
                        : 'bg-white text-gray-600 border border-gray-200 hover:border-klem-red hover:text-klem-red';
                ?>
                    <a
                        href="<?php echo esc_url($klem_url); ?>"
                        class="<?php echo esc_attr($klem_btn_class); ?> px-5 py-2 rounded-full text-sm font-semibold transition-colors duration-150"
                        <?php echo $klem_is_active ? 'aria-current="true"' : ''; ?>
                    >
                        <?php echo esc_html($klem_label); ?>
                    </a>
                <?php endforeach; ?>
            </nav>
        </div>
    </section>

    <!-- Contenu -->
    <section class="pb-20 lg:pb-28 bg-white">
        <div class="max-w-[1600px] mx-auto px-4 sm:px-6">

            <?php if (!$klem_featured && empty($klem_posts)) : ?>

                <div class="text-center py-20 border-t border-gray-100" data-animate>
                    <svg class="w-12 h-12 text-gray-300 mx-auto mb-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5" aria-hidden="true">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z"/>
                    </svg>
                    <p class="text-gray-500 text-lg">
                        <?php esc_html_e('Aucun article publié pour le moment. Revenez bientôt !', 'klem-theme'); ?>
                    </p>
                </div>

            <?php else :

                // ── Article vedette (onglet "Tout" uniquement) ──────────────────
                if ($klem_featured) :
                    global $post;
                    $post = $klem_featured; // phpcs:ignore WordPress.WP.GlobalVariablesOverride
                    setup_postdata($post);
                    $klem_badge = klem_actualites_badge(get_the_ID());
                    ?>
                    <a
                        href="<?php the_permalink(); ?>"
                        class="group grid lg:grid-cols-2 gap-8 lg:gap-12 items-center bg-gray-50 rounded-2xl p-6 lg:p-10 mb-12 lg:mb-16"
                        data-animate
                    >
                        <div>
                            <?php if ($klem_badge) : ?>
                                <span class="inline-flex items-center text-[11px] font-bold uppercase tracking-wider text-white px-3 py-1 rounded-full mb-5 <?php echo esc_attr($klem_badge['color']); ?>">
                                    <?php echo esc_html($klem_badge['name']); ?>
                                </span>
                            <?php endif; ?>
                            <h2 class="text-2xl lg:text-3xl font-heading text-klem-blue leading-tight mb-4 group-hover:text-klem-red transition-colors duration-200">
                                <?php the_title(); ?>
                            </h2>
                            <p class="text-gray-500 leading-relaxed mb-6">
                                <?php echo esc_html(wp_trim_words(get_the_excerpt(), 32)); ?>
                            </p>
                            <span class="inline-flex items-center gap-1.5 text-sm font-semibold text-klem-red">
                                <?php esc_html_e('En savoir plus', 'klem-theme'); ?>
                                <svg class="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform duration-200" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                                    <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                                </svg>
                            </span>
                        </div>
                        <div class="aspect-[4/3] rounded-xl overflow-hidden bg-gray-100">
                            <?php if (has_post_thumbnail()) : ?>
                                <?php the_post_thumbnail('large', ['class' => 'w-full h-full object-cover group-hover:scale-105 transition-transform duration-500']); ?>
                            <?php else : ?>
                                <div class="w-full h-full flex items-center justify-center bg-gradient-to-br from-klem-blue to-[#0a1830]">
                                    <svg class="w-14 h-14 text-white/20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5" aria-hidden="true">
                                        <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 15.75l5.159-5.159a2.25 2.25 0 013.182 0l5.159 5.159m-1.5-1.5l1.409-1.409a2.25 2.25 0 013.182 0l2.909 2.909m-18 3.75h16.5a1.5 1.5 0 001.5-1.5V6a1.5 1.5 0 00-1.5-1.5H3.75A1.5 1.5 0 002.25 6v12a1.5 1.5 0 001.5 1.5zm10.5-11.25h.008v.008h-.008V8.25zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z"/>
                                    </svg>
                                </div>
                            <?php endif; ?>
                        </div>
                    </a>
                <?php endif; ?>

                <!-- ── Grille d'articles ────────────────────────────────────────── -->
                <?php if (!empty($klem_posts)) : ?>
                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 lg:gap-8">
                        <?php
                        global $post;
                        foreach ($klem_posts as $klem_index => $klem_post) :
                            $post = $klem_post; // phpcs:ignore WordPress.WP.GlobalVariablesOverride
                            setup_postdata($post);
                            get_template_part('template-parts/actualites/card', null, [
                                'delay' => (string) min(($klem_index % 6) * 100, 500),
                            ]);
                        endforeach;
                        ?>
                    </div>
                <?php endif; ?>

            <?php endif; ?>

        </div>
    </section>

</main>

<?php
wp_reset_postdata();
get_footer();
