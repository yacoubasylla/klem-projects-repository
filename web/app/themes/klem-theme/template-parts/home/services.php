<section id="services" class="py-24 lg:py-32 bg-gray-50">
    <div class="max-w-7xl mx-auto px-6">

        <!-- En-tête de section -->
        <div class="max-w-2xl mb-16 lg:mb-20" data-animate data-delay="0">
            <span class="inline-block text-klem-orange font-bold tracking-widest text-xs uppercase mb-4 px-4 py-1.5 bg-klem-orange/10 rounded-full">
                <?php esc_html_e('Notre Expertise', 'klem-theme'); ?>
            </span>
            <h2 class="text-4xl lg:text-5xl font-extrabold text-klem-blue leading-tight mb-4">
                <?php esc_html_e("Quatre Piliers d'Excellence", 'klem-theme'); ?>
            </h2>
            <p class="text-gray-500 text-lg leading-relaxed">
                <?php esc_html_e(
                    "Des solutions technologiques de bout en bout, conçues pour les organisations qui misent sur l'innovation comme levier de compétitivité.",
                    'klem-theme'
                ); ?>
            </p>
        </div>

        <!-- Grille de cartes -->
        <?php
        $img_base      = get_template_directory_uri() . '/assets/images/services/';
        $klem_services = [
            [
                'num'       => '01',
                'title'     => 'Ingénierie des Données',
                'desc'      => 'Pipelines Big Data, architectures temps réel (Kafka, Spark) et lacs de données pour transformer vos données brutes en avantage stratégique décisif.',
                'delay'     => '100',
                'img'       => $img_base . 'service-big-data.jpg',
                'img_alt'   => 'Salle de serveurs data center',
                'icon_path' => 'M20.25 6.375c0 2.278-3.694 4.125-8.25 4.125S3.75 8.653 3.75 6.375m16.5 0c0-2.278-3.694-4.125-8.25-4.125S3.75 4.097 3.75 6.375m16.5 0v11.25c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125V6.375m16.5 5.625c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125',
            ],
            [
                'num'       => '02',
                'title'     => 'Applications Sur-Mesure',
                'desc'      => "Développement d'applications web et mobiles d'envergure, ERP et logiciels métiers haute performance, 100 % adaptés à vos processus et à votre ambition.",
                'delay'     => '200',
                'img'       => $img_base . 'service-apps.jpg',
                'img_alt'   => 'Développeur devant des écrans de code néon',
                'icon_path' => 'M17.25 6.75 22.5 12l-5.25 5.25m-10.5 0L1.5 12l5.25-5.25m7.5-3-4.5 16.5',
            ],
            [
                'num'       => '03',
                'title'     => 'Intégration ERP & FleetControl',
                'desc'      => "Orchestration de systèmes d'information complexes et déploiement de FleetControl, notre solution de gestion de flotte intelligente pour les opérateurs africains.",
                'delay'     => '300',
                'img'       => $img_base . 'service-erp.jpg',
                'img_alt'   => 'Autoroute de nuit — gestion de flotte',
                'icon_path' => 'M7.5 21 3 16.5m0 0 4.5-4.5M3 16.5h13.5m0-13.5L21 7.5m0 0-4.5 4.5M21 7.5H7.5',
            ],
            [
                'num'       => '04',
                'title'     => 'Matériel IT & Infrastructure',
                'desc'      => "Fourniture et déploiement d'équipements serveurs, réseaux et postes de travail de qualité entreprise pour des infrastructures critiques robustes et évolutives.",
                'delay'     => '400',
                'img'       => $img_base . 'service-hardware.jpg',
                'img_alt'   => 'Câbles réseau et infrastructure IT',
                'icon_path' => 'M9 17.25v1.007a3 3 0 0 1-.879 2.122L7.5 21h9l-.621-.621A3 3 0 0 1 15 18.257V17.25m6-12V15a2.25 2.25 0 0 1-2.25 2.25H5.25A2.25 2.25 0 0 1 3 15V5.25m18 0A2.25 2.25 0 0 0 18.75 3H5.25A2.25 2.25 0 0 0 3 5.25m18 0H3',
            ],
        ];
        ?>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <?php foreach ($klem_services as $service) : ?>
            <div
                class="group bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm hover:shadow-2xl hover:-translate-y-2 hover:border-klem-orange/20 transition-all duration-300 flex flex-col cursor-default"
                data-animate
                data-delay="<?php echo esc_attr($service['delay']); ?>"
            >
                <!-- Bandeau photo -->
                <div class="relative h-44 overflow-hidden bg-klem-blue flex-shrink-0">
                    <img
                        src="<?php echo esc_url($service['img']); ?>"
                        alt="<?php echo esc_attr($service['img_alt']); ?>"
                        class="w-full h-full object-cover opacity-90 group-hover:scale-105 transition-transform duration-700 ease-out"
                        loading="lazy"
                        width="400"
                        height="176"
                    >
                    <!-- Overlay dégradé du bas -->
                    <div class="absolute inset-0 bg-gradient-to-t from-klem-blue/75 via-klem-blue/25 to-transparent"></div>

                    <!-- Icône centrée sur la photo -->
                    <div class="absolute inset-0 flex items-center justify-center">
                        <div class="w-16 h-16 rounded-2xl bg-white/15 backdrop-blur-sm border border-white/30 flex items-center justify-center group-hover:bg-klem-orange group-hover:border-klem-orange transition-all duration-300 shadow-lg">
                            <svg
                                class="w-8 h-8 text-white"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                                stroke-width="1.5"
                                aria-hidden="true"
                            >
                                <path stroke-linecap="round" stroke-linejoin="round" d="<?php echo esc_attr($service['icon_path']); ?>"/>
                            </svg>
                        </div>
                    </div>

                    <!-- Trait orange au bas du bandeau -->
                    <div class="absolute bottom-0 left-0 right-0 h-0.5 bg-klem-orange scale-x-0 group-hover:scale-x-100 transition-transform duration-300 origin-left"></div>
                </div>

                <!-- Contenu texte -->
                <div class="p-6 flex flex-col flex-1">
                    <span class="text-xs font-extrabold tracking-widest text-klem-orange/40 group-hover:text-klem-orange transition-colors duration-300 mb-2">
                        <?php echo esc_html($service['num']); ?>
                    </span>

                    <h3 class="text-base font-bold text-klem-blue mb-3 leading-snug">
                        <?php echo esc_html($service['title']); ?>
                    </h3>

                    <p class="text-gray-500 text-sm leading-relaxed flex-grow">
                        <?php echo esc_html($service['desc']); ?>
                    </p>

                    <div class="mt-6 pt-5 border-t border-gray-100 group-hover:border-klem-orange/10 transition-colors duration-300">
                        <span class="inline-flex items-center gap-1.5 text-sm font-semibold text-gray-400 group-hover:text-klem-orange transition-colors duration-300">
                            <?php esc_html_e('En savoir plus', 'klem-theme'); ?>
                            <svg
                                class="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform duration-200"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                                stroke-width="2.5"
                                aria-hidden="true"
                            >
                                <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                            </svg>
                        </span>
                    </div>
                </div>
            </div>
            <?php endforeach; ?>
        </div>

        <!-- CTA bas de section -->
        <div class="mt-16 text-center" data-animate data-delay="500">
            <p class="text-gray-500 mb-6 text-base">
                <?php esc_html_e('Votre défi ne rentre dans aucune case ? Construisons ensemble une solution inédite.', 'klem-theme'); ?>
            </p>
            <a
                href="#contact"
                class="inline-flex items-center gap-2 bg-klem-blue text-white font-bold px-8 py-4 rounded-xl hover:opacity-90 hover:-translate-y-0.5 hover:shadow-lg transition-all duration-200"
            >
                <?php esc_html_e('Parler à un expert', 'klem-theme'); ?>
                <svg class="w-4 h-4 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                </svg>
            </a>
        </div>
    </div>
</section>
