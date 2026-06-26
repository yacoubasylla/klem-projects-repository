<section class="bg-white">

    <!-- Bloc hero : grille 2 colonnes contenue dans le container -->
    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-4 lg:gap-6 items-center py-8 lg:py-24">

            <!-- Colonne gauche : texte -->
            <div class="order-2 lg:order-1">
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

            <!-- Colonne droite : panneau image avec découpe et cartes -->
            <div class="order-1 lg:order-2 block relative rounded-2xl overflow-hidden min-h-[240px] lg:min-h-[420px] lg:-ml-6 bg-klem-blue bg-cover bg-center"
                 style="background-image: url('<?php echo esc_url(get_template_directory_uri() . '/assets/images/hero-bg.jpg'); ?>');
                        clip-path: polygon(8% 0%, 100% 0%, 100% 100%, 0% 100%);"
                 aria-hidden="true">

                <!-- Overlay sombre -->
                <div class="absolute inset-0 bg-klem-blue/65"></div>

                <!-- Halo orange -->
                <div class="absolute -top-16 right-0 w-64 h-64 rounded-full bg-klem-orange/25 blur-3xl"></div>

                <!-- Cartes métriques — positionnement absolu décalé -->
                <div class="absolute inset-0">

                    <!-- Carte 1 : Pipeline Big Data — haut gauche -->
                    <div class="absolute top-[10%] left-[14%] w-44 bg-white/5 backdrop-blur-md border border-white/15 rounded-2xl p-3.5 shadow-xl">
                        <div class="flex items-center gap-2 mb-2">
                            <span class="relative flex h-2.5 w-2.5 flex-shrink-0">
                                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-klem-orange opacity-75"></span>
                                <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-klem-orange"></span>
                            </span>
                            <span class="text-white/60 text-[10px] font-bold uppercase tracking-wider">Pipeline Big Data</span>
                        </div>
                        <p class="text-white font-extrabold text-xl leading-none">4.2M</p>
                        <p class="text-white/40 text-xs mt-1"><?php esc_html_e('événements / jour', 'klem-theme'); ?></p>
                    </div>

                    <!-- Carte 2 : Apps Sur-Mesure — haut droit (décalée vers le bas) -->
                    <div class="absolute top-[22%] right-[6%] w-44 bg-white/5 backdrop-blur-md border border-white/15 rounded-2xl p-3.5 shadow-xl">
                        <div class="flex items-center gap-2 mb-2">
                            <span class="relative flex h-2.5 w-2.5 flex-shrink-0">
                                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75" style="animation-delay:0.3s"></span>
                                <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-blue-400"></span>
                            </span>
                            <span class="text-white/60 text-[10px] font-bold uppercase tracking-wider"><?php esc_html_e('Apps Sur-Mesure', 'klem-theme'); ?></span>
                        </div>
                        <p class="text-white font-extrabold text-xl leading-none">18+</p>
                        <p class="text-white/40 text-xs mt-1"><?php esc_html_e('solutions déployées', 'klem-theme'); ?></p>
                    </div>

                    <!-- Carte 3 : FleetControl — centre gauche -->
                    <div class="absolute top-[50%] left-[18%] -translate-y-1/2 w-44 bg-white/5 backdrop-blur-md border border-white/15 rounded-2xl p-3.5 shadow-xl">
                        <div class="flex items-center gap-2 mb-2">
                            <span class="relative flex h-2.5 w-2.5 flex-shrink-0">
                                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75" style="animation-delay:0.6s"></span>
                                <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-green-400"></span>
                            </span>
                            <span class="text-white/60 text-[10px] font-bold uppercase tracking-wider">FleetControl</span>
                        </div>
                        <p class="text-white font-extrabold text-xl leading-none">124</p>
                        <p class="text-white/40 text-xs mt-1"><?php esc_html_e('véhicules en temps réel', 'klem-theme'); ?></p>
                    </div>

                    <!-- Carte 4 : Disponibilité — bas droit -->
                    <div class="absolute bottom-[10%] right-[6%] w-44 bg-white/5 backdrop-blur-md border border-white/15 rounded-2xl p-3.5 shadow-xl">
                        <div class="flex items-center gap-2 mb-2">
                            <span class="relative flex h-2.5 w-2.5 flex-shrink-0">
                                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75" style="animation-delay:0.9s"></span>
                                <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-green-400"></span>
                            </span>
                            <span class="text-white/60 text-[10px] font-bold uppercase tracking-wider"><?php esc_html_e('Disponibilité', 'klem-theme'); ?></span>
                        </div>
                        <p class="text-white font-extrabold text-xl leading-none">99.97%</p>
                        <p class="text-white/40 text-xs mt-1"><?php esc_html_e('uptime infrastructure', 'klem-theme'); ?></p>
                    </div>

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
