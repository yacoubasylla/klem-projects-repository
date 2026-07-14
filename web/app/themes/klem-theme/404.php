<?php
/**
 * Page 404 — Contenu introuvable.
 */

get_header();
?>

<main id="main-content" class="pt-24">
    <section class="py-20 lg:py-28 bg-white">
        <div class="max-w-xl mx-auto px-4 sm:px-6 text-center" data-animate>
            <p class="text-klem-red font-extrabold text-sm tracking-widest uppercase mb-4">
                <?php esc_html_e('Erreur 404', 'klem-theme'); ?>
            </p>
            <h1 class="text-3xl lg:text-4xl font-extrabold text-klem-blue leading-tight mb-4">
                <?php esc_html_e('Cette page est introuvable', 'klem-theme'); ?>
            </h1>
            <p class="text-gray-500 text-lg leading-relaxed mb-10">
                <?php esc_html_e("La page que vous cherchez n'existe pas ou a été déplacée. Voici quelques liens utiles pour continuer votre visite.", 'klem-theme'); ?>
            </p>
            <div class="flex flex-wrap items-center justify-center gap-3">
                <a
                    href="<?php echo esc_url(home_url('/')); ?>"
                    class="inline-flex items-center justify-center bg-klem-orange text-white font-bold px-6 py-3 rounded-lg text-sm hover:brightness-110 transition-all"
                >
                    <?php esc_html_e("Retour à l'accueil", 'klem-theme'); ?>
                </a>
                <a
                    href="<?php echo esc_url(klem_actualites_url()); ?>"
                    class="inline-flex items-center justify-center border border-gray-200 text-klem-blue font-bold px-6 py-3 rounded-lg text-sm hover:border-klem-red/30 transition-all"
                >
                    <?php esc_html_e('Voir les actualités', 'klem-theme'); ?>
                </a>
            </div>
        </div>
    </section>
</main>

<?php get_footer(); ?>
