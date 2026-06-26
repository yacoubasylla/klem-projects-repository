<section class="bg-white">

    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-4 lg:gap-6 py-8 lg:py-24">

            <!-- IMAGE : en premier dans le HTML → haut sur mobile, droite sur desktop -->
            <div class="lg:col-start-2 lg:row-start-1 lg:self-stretch rounded-2xl overflow-hidden bg-cover bg-center lg:[clip-path:polygon(20%_0%,100%_0%,100%_100%,0%_100%)] lg:-ml-6"
                 style="background-image: url('<?php echo esc_url(get_template_directory_uri() . '/assets/images/hero-bg.jpg'); ?>'); background-color: #13294B; min-height: 260px;"
                 aria-hidden="true">
            </div>

            <!-- TEXTE : en second dans le HTML → bas sur mobile, gauche sur desktop -->
            <div class="lg:col-start-1 lg:row-start-1 flex flex-col justify-center">
                <p class="text-klem-orange font-bold tracking-widest text-xs uppercase mb-6">
                    <?php esc_html_e('Intégrateur Numérique · Afrique', 'klem-theme'); ?>
                </p>

                <h1 class="text-3xl sm:text-4xl lg:text-5xl font-bold text-klem-blue leading-tight mb-6">
                    <?php esc_html_e('Maîtrisez votre', 'klem-theme'); ?><br>
                    <?php esc_html_e('Souveraineté', 'klem-theme'); ?><br>
                    <?php esc_html_e('Numérique', 'klem-theme'); ?>
                </h1>

                <p class="text-gray-500 text-base leading-relaxed max-w-md mb-10">
                    <?php esc_html_e('Confiez-nous votre transformation digitale et concentrez-vous sur la croissance de votre organisation.', 'klem-theme'); ?>
                </p>

                <div class="flex flex-wrap gap-4">
                    <a
                        href="#services"
                        class="inline-flex items-center justify-center bg-klem-orange text-white font-bold px-5 py-2.5 rounded-lg text-sm hover:brightness-110 hover:-translate-y-px transition-all duration-200"
                    >
                        <?php esc_html_e('Nos services', 'klem-theme'); ?>
                    </a>
                    <a
                        href="#about"
                        class="inline-flex items-center justify-center border border-gray-300 text-gray-700 font-semibold px-5 py-2.5 rounded-lg text-sm hover:border-klem-orange hover:text-klem-orange transition-all duration-200"
                    >
                        <?php esc_html_e('Notre histoire', 'klem-theme'); ?>
                    </a>
                </div>
            </div>

        </div>
    </div>

    <!-- Bande de statistiques -->
    <div class="border-t border-gray-200">
        <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
            <div class="grid grid-cols-2 lg:grid-cols-4 gap-6 lg:gap-10">

                <div class="border-l-2 border-klem-blue pl-6">
                    <div class="flex items-baseline gap-2 mb-1">
                        <span class="text-3xl font-extrabold text-klem-blue leading-none">5+</span>
                        <span class="text-base font-bold text-klem-blue"><?php esc_html_e('Ans', 'klem-theme'); ?></span>
                    </div>
                    <p class="text-klem-orange text-sm font-semibold"><?php esc_html_e("d'expertise prouvée", 'klem-theme'); ?></p>
                </div>

                <div class="border-l-2 border-klem-blue pl-6">
                    <div class="flex items-baseline gap-2 mb-1">
                        <span class="text-3xl font-extrabold text-klem-blue leading-none">30+</span>
                        <span class="text-base font-bold text-klem-blue"><?php esc_html_e('Projets', 'klem-theme'); ?></span>
                    </div>
                    <p class="text-klem-orange text-sm font-semibold"><?php esc_html_e('livrés avec succès', 'klem-theme'); ?></p>
                </div>

                <div class="border-l-2 border-klem-blue pl-6">
                    <div class="flex items-baseline gap-2 mb-1">
                        <span class="text-3xl font-extrabold text-klem-blue leading-none">4</span>
                        <span class="text-base font-bold text-klem-blue"><?php esc_html_e('Piliers', 'klem-theme'); ?></span>
                    </div>
                    <p class="text-klem-orange text-sm font-semibold"><?php esc_html_e("d'expertise couverts", 'klem-theme'); ?></p>
                </div>

                <div class="border-l-2 border-klem-blue pl-6">
                    <div class="flex items-baseline gap-2 mb-1">
                        <span class="text-3xl font-extrabold text-klem-blue leading-none">100%</span>
                    </div>
                    <p class="text-klem-orange text-sm font-semibold"><?php esc_html_e('Sur-mesure, sans compromis', 'klem-theme'); ?></p>
                </div>

            </div>
        </div>
    </div>
</section>
