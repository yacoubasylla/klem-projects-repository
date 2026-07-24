<?php
$klem_home_cat_ids = [];
foreach (klem_actualites_categories() as $klem_home_slug => $klem_home_name) {
    $klem_home_term = get_term_by('slug', $klem_home_slug, 'category');
    if ($klem_home_term) {
        $klem_home_cat_ids[] = (int) $klem_home_term->term_id;
    }
}

$klem_home_news = new WP_Query([
    'post_type'           => 'post',
    'post_status'         => 'publish',
    'posts_per_page'      => 3,
    'ignore_sticky_posts' => true,
    'category__in'        => $klem_home_cat_ids,
]);
?>

<?php if ($klem_home_news->have_posts()) : ?>
<section class="py-16 lg:py-20 bg-gray-50/60">
    <div class="max-w-[1600px] mx-auto px-4 sm:px-6">

        <div class="max-w-2xl mb-12" data-animate data-delay="0">
            <span class="inline-block text-klem-red font-bold tracking-widest text-xs uppercase mb-4">
                <?php esc_html_e('Actualités & Insights', 'klem-theme'); ?>
            </span>
            <h2 class="text-2xl lg:text-3xl font-heading text-klem-blue leading-tight mb-4">
                <?php esc_html_e('Data, IA et commerce digital : nos analyses', 'klem-theme'); ?>
            </h2>
            <p class="text-gray-500 text-lg leading-relaxed">
                <?php esc_html_e('Un regard pédagogique sur les tendances qui transforment les organisations africaines.', 'klem-theme'); ?>
            </p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 lg:gap-8 mb-10">
            <?php
            $klem_index = 0;
            while ($klem_home_news->have_posts()) :
                $klem_home_news->the_post();
                get_template_part('template-parts/actualites/card', null, [
                    'delay' => (string) ($klem_index * 100),
                ]);
                $klem_index++;
            endwhile;
            wp_reset_postdata();
            ?>
        </div>

        <div class="text-center" data-animate data-delay="300">
            <a
                href="<?php echo esc_url(klem_actualites_url()); ?>"
                class="inline-flex items-center gap-1.5 text-sm font-semibold text-klem-red hover:gap-2.5 transition-all duration-200"
            >
                <?php esc_html_e('Voir toutes les actualités', 'klem-theme'); ?>
                <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                </svg>
            </a>
        </div>
    </div>
</section>
<?php endif; ?>
