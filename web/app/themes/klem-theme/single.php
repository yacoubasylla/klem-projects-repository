<?php
/**
 * Affichage d'un article individuel (Blog / Actualités / Événements).
 */

get_header();

while (have_posts()) :
    the_post();

    $klem_badge = klem_actualites_badge(get_the_ID());
?>

<main id="main-content" class="pt-24">

    <!-- En-tête article -->
    <header class="pt-16 pb-10 lg:pt-20 lg:pb-12 bg-white">
        <div class="max-w-3xl mx-auto px-4 sm:px-6 text-center" data-animate>

            <a
                href="<?php echo esc_url(klem_actualites_url()); ?>"
                class="inline-flex items-center gap-1.5 text-sm font-semibold text-gray-400 hover:text-klem-red transition-colors duration-150 mb-6"
            >
                <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
                </svg>
                <?php esc_html_e('Retour aux actualités', 'klem-theme'); ?>
            </a>

            <?php if ($klem_badge) : ?>
                <span class="inline-flex items-center text-[11px] font-bold uppercase tracking-wider text-white px-3 py-1 rounded-full mb-5 <?php echo esc_attr($klem_badge['color']); ?>">
                    <?php echo esc_html($klem_badge['name']); ?>
                </span>
            <?php endif; ?>

            <h1 class="text-3xl lg:text-4xl font-extrabold text-klem-blue leading-tight mb-5">
                <?php the_title(); ?>
            </h1>

            <div class="flex items-center justify-center gap-3 text-sm text-gray-400">
                <span><?php echo esc_html(get_the_date()); ?></span>
                <span aria-hidden="true">&middot;</span>
                <span><?php echo esc_html(get_the_author()); ?></span>
            </div>
        </div>
    </header>

    <!-- Image à la une -->
    <?php if (has_post_thumbnail()) : ?>
        <div class="max-w-5xl mx-auto px-4 sm:px-6 mb-12 lg:mb-16" data-animate>
            <div class="aspect-[16/9] rounded-2xl overflow-hidden bg-gray-100">
                <?php the_post_thumbnail('large', ['class' => 'w-full h-full object-cover']); ?>
            </div>
        </div>
    <?php endif; ?>

    <!-- Contenu -->
    <article class="pb-20 lg:pb-28 bg-white">
        <div class="max-w-3xl mx-auto px-4 sm:px-6">
            <div class="klem-article-content prose prose-lg max-w-none prose-headings:text-klem-blue prose-headings:font-extrabold prose-a:text-klem-red prose-a:no-underline hover:prose-a:underline prose-img:rounded-xl" data-animate>
                <?php the_content(); ?>
            </div>

            <div class="mt-14 pt-8 border-t border-gray-100 text-center">
                <a
                    href="<?php echo esc_url(klem_actualites_url()); ?>"
                    class="inline-flex items-center gap-2 bg-klem-blue text-white font-bold px-8 py-4 rounded-xl hover:opacity-90 hover:-translate-y-0.5 hover:shadow-lg transition-all duration-200"
                >
                    <?php esc_html_e('Voir toutes les actualités', 'klem-theme'); ?>
                </a>
            </div>
        </div>
    </article>

</main>

<?php endwhile; ?>

<?php get_footer(); ?>
