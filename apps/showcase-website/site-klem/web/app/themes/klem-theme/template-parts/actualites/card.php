<?php
/**
 * Carte article — grille du hub Actualités.
 *
 * @var array{delay?: string} $args
 */

$klem_badge = klem_actualites_badge(get_the_ID());
$klem_delay = $args['delay'] ?? '0';
?>
<article
    class="group flex flex-col bg-white border border-gray-200 rounded-xl overflow-hidden hover:border-klem-red hover:shadow-lg transition-all duration-300"
    data-animate
    data-delay="<?php echo esc_attr($klem_delay); ?>"
>
    <a href="<?php the_permalink(); ?>" class="block aspect-[16/10] overflow-hidden bg-gray-100" tabindex="-1" aria-hidden="true">
        <?php if (has_post_thumbnail()) : ?>
            <?php the_post_thumbnail('medium_large', ['class' => 'w-full h-full object-cover group-hover:scale-105 transition-transform duration-500']); ?>
        <?php else : ?>
            <div class="w-full h-full flex items-center justify-center bg-gradient-to-br from-klem-blue to-[#0a1830]">
                <svg class="w-10 h-10 text-white/20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z"/>
                </svg>
            </div>
        <?php endif; ?>
    </a>
    <div class="flex flex-col flex-grow p-6">
        <?php if ($klem_badge) : ?>
            <span class="inline-flex self-start items-center text-[11px] font-bold uppercase tracking-wider text-white px-3 py-1 rounded-full mb-4 <?php echo esc_attr($klem_badge['color']); ?>">
                <?php echo esc_html($klem_badge['name']); ?>
            </span>
        <?php endif; ?>

        <h3 class="text-lg font-bold text-klem-blue leading-snug mb-3 group-hover:text-klem-red transition-colors duration-200">
            <a href="<?php the_permalink(); ?>"><?php the_title(); ?></a>
        </h3>

        <p class="text-gray-500 text-sm leading-relaxed flex-grow mb-5">
            <?php echo esc_html(wp_trim_words(get_the_excerpt(), 20)); ?>
        </p>

        <a href="<?php the_permalink(); ?>" class="inline-flex items-center gap-1.5 text-sm font-semibold text-klem-red">
            <?php esc_html_e('Lire la suite', 'klem-theme'); ?>
            <svg class="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform duration-200" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
            </svg>
        </a>
    </div>
</article>
