<section class="bg-white">

    <!-- Bloc hero : texte gauche + visuel droit avec découpe diagonale -->
    <div class="relative overflow-hidden">

        <!-- Contenu texte (gauche) -->
        <div class="relative z-10 max-w-6xl mx-auto px-4 sm:px-6">
            <div class="flex items-center min-h-[420px] sm:min-h-[480px] lg:min-h-[540px] pt-16 pb-8">
                <div class="w-full lg:w-[54%] py-8 sm:py-10 lg:py-16 lg:pr-12">

                    <!-- Pastille supérieure -->
                    <p class="text-klem-orange font-bold tracking-widest text-xs uppercase mb-8">
                        <?php esc_html_e('Intégrateur Numérique · Afrique', 'klem-theme'); ?>
                    </p>

                    <!-- H1 massif style veone -->
                    <h1 class="text-3xl sm:text-4xl lg:text-5xl font-bold text-klem-blue leading-tight mb-6">
                        <?php esc_html_e('Maîtrisez votre', 'klem-theme'); ?><br>
                        <?php esc_html_e('Souveraineté', 'klem-theme'); ?><br>
                        <?php esc_html_e('Numérique', 'klem-theme'); ?>
                    </h1>

                    <!-- Sous-titre -->
                    <p class="text-gray-500 text-lg leading-relaxed max-w-md mb-10">
                        <?php esc_html_e('Confiez-nous votre transformation digitale et concentrez-vous sur la croissance de votre organisation.', 'klem-theme'); ?>
                    </p>

                    <!-- CTAs -->
                    <div class="flex flex-wrap gap-4">
                        <a
                            href="#services"
                            class="inline-flex items-center gap-2 bg-klem-orange text-white font-bold px-7 py-3.5 rounded-lg text-sm hover:brightness-110 hover:-translate-y-px transition-all duration-200"
                        >
                            <?php esc_html_e('Nos services', 'klem-theme'); ?>
                        </a>
                        <a
                            href="#about"
                            class="inline-flex items-center gap-2 border border-gray-300 text-gray-700 font-semibold px-7 py-3.5 rounded-lg text-sm hover:border-klem-orange hover:text-klem-orange transition-all duration-200"
                        >
                            <?php esc_html_e('Notre histoire', 'klem-theme'); ?>
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Panneau visuel droit — photo high-tech + découpe diagonale -->
        <div
            class="hidden lg:block absolute top-0 right-0 bottom-0 w-[49%] overflow-hidden [clip-path:polygon(10%_0%,100%_0%,100%_100%,0%_100%)] bg-klem-blue bg-cover bg-center"
            style="background-image: url('<?php echo esc_url(get_template_directory_uri() . '/assets/images/hero-bg.jpg'); ?>');"
            aria-hidden="true"
        >
            <!-- Overlay sombre pour lisibilité des cartes -->
            <div class="absolute inset-0 bg-klem-blue/65"></div>

            <!-- Halo orange en haut à droite -->
            <div class="absolute -top-24 right-0 w-80 h-80 rounded-full bg-klem-orange/25 blur-3xl"></div>

            <!-- Cartes métriques — centrées dans la zone visible (après le clip diagonal) -->
            <div class="absolute inset-0 flex flex-col items-center justify-center gap-5 pl-[18%] pr-8">

                <!-- Carte 1 : Pipeline Big Data (légèrement à gauche) -->
                <div class="w-52 self-start bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl p-4 shadow-xl">
                    <div class="flex items-center gap-2 mb-3">
                        <span class="w-2 h-2 rounded-full bg-klem-orange animate-pulse flex-shrink-0"></span>
                        <span class="text-white/70 text-xs font-bold uppercase tracking-wider">Pipeline Big Data</span>
                    </div>
                    <p class="text-white font-extrabold text-3xl leading-none">4.2M</p>
                    <p class="text-white/50 text-xs mt-1"><?php esc_html_e('événements / jour', 'klem-theme'); ?></p>
                </div>

                <!-- Carte 2 : FleetControl (décalée à droite pour le rythme visuel) -->
                <div class="w-56 self-end bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl p-4 shadow-xl">
                    <div class="flex items-center gap-2 mb-3">
                        <span class="w-2 h-2 rounded-full bg-green-400 animate-pulse flex-shrink-0"></span>
                        <span class="text-white/70 text-xs font-bold uppercase tracking-wider">FleetControl</span>
                    </div>
                    <p class="text-white font-extrabold text-3xl leading-none">124</p>
                    <p class="text-white/50 text-xs mt-1"><?php esc_html_e('véhicules en temps réel', 'klem-theme'); ?></p>
                </div>

                <!-- Carte 3 : Disponibilité (légèrement à gauche, symétrique avec carte 1) -->
                <div class="w-52 self-start ml-6 bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl p-4 shadow-xl">
                    <div class="flex items-center gap-2 mb-3">
                        <span class="w-2 h-2 rounded-full bg-green-400 flex-shrink-0"></span>
                        <span class="text-white/70 text-xs font-bold uppercase tracking-wider"><?php esc_html_e('Disponibilité', 'klem-theme'); ?></span>
                    </div>
                    <p class="text-white font-extrabold text-3xl leading-none">99.97%</p>
                    <p class="text-white/50 text-xs mt-1"><?php esc_html_e('uptime infrastructure', 'klem-theme'); ?></p>
                </div>

            </div>
        </div>
    </div>

    <!-- Bande de statistiques (style veone.net) -->
    <div class="border-t border-gray-200">
        <div class="max-w-6xl mx-auto px-4 sm:px-6 py-10">
            <div class="grid grid-cols-2 lg:grid-cols-4 gap-6 lg:gap-10">

                <div class="border-l-2 border-klem-blue pl-6">
                    <div class="flex items-baseline gap-2 mb-1">
                        <span class="text-3xl font-extrabold text-klem-blue leading-none">5+</span>
                        <span class="text-lg font-bold text-klem-blue"><?php esc_html_e('Ans', 'klem-theme'); ?></span>
                    </div>
                    <p class="text-klem-orange text-sm font-semibold"><?php esc_html_e("d'expertise prouvée", 'klem-theme'); ?></p>
                </div>

                <div class="border-l-2 border-klem-blue pl-6">
                    <div class="flex items-baseline gap-2 mb-1">
                        <span class="text-3xl font-extrabold text-klem-blue leading-none">30+</span>
                        <span class="text-lg font-bold text-klem-blue"><?php esc_html_e('Projets', 'klem-theme'); ?></span>
                    </div>
                    <p class="text-klem-orange text-sm font-semibold"><?php esc_html_e('livrés avec succès', 'klem-theme'); ?></p>
                </div>

                <div class="border-l-2 border-klem-blue pl-6">
                    <div class="flex items-baseline gap-2 mb-1">
                        <span class="text-3xl font-extrabold text-klem-blue leading-none">4</span>
                        <span class="text-lg font-bold text-klem-blue"><?php esc_html_e('Piliers', 'klem-theme'); ?></span>
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
