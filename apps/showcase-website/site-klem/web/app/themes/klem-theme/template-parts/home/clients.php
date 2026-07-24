<section id="clients" class="py-24 lg:py-32 bg-klem-blue overflow-hidden">
    <div class="max-w-[1600px] mx-auto px-4 sm:px-6">

        <!-- En-tête de section -->
        <div class="max-w-2xl mb-16 lg:mb-20" data-animate data-delay="0">
            <span class="inline-block text-klem-orange font-bold tracking-widest text-xs uppercase mb-4 px-4 py-1.5 bg-klem-orange/20 rounded-full">
                <?php esc_html_e('Notre Différence', 'klem-theme'); ?>
            </span>
            <h2 class="text-2xl lg:text-3xl font-heading text-white leading-tight mb-4">
                <?php esc_html_e('Ce qui nous', 'klem-theme'); ?><br>
                <?php esc_html_e('distingue', 'klem-theme'); ?>
            </h2>
            <p class="text-white/50 text-lg leading-relaxed">
                <?php esc_html_e("Une équipe passionnée, des méthodes éprouvées, une ambition claire : accompagner la transformation numérique des entreprises africaines.", 'klem-theme'); ?>
            </p>
        </div>

        <!-- Grille des piliers différenciateurs -->
        <?php
        $pillars = [
            [
                'delay'   => '100',
                'icon'    => 'M17.25 6.75 22.5 12l-5.25 5.25m-10.5 0L1.5 12l5.25-5.25m7.5-3-4.5 16.5',
                'title'   => 'Expertise Technique',
                'body'    => "Architectures robustes et scalables, standards d'ingénierie mondiaux : Big Data, ERP et développement sur-mesure, appliqués au marché local.",
                'badge'   => 'Architecture & Ingénierie',
            ],
            [
                'delay'   => '200',
                'icon'    => 'M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0z',
                'title'   => 'Rigueur & Transparence',
                'body'    => "Périmètre défini, jalons clairs, budget respecté : une méthodologie stricte pour savoir, à chaque étape, où en est votre projet.",
                'badge'   => 'Méthode & Fiabilité',
            ],
            [
                'delay'   => '300',
                'icon'    => 'M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0zm-9 8.25c0-3.314 2.686-6 6-6s6 2.686 6 6',
                'title'   => 'Ancrage Africain',
                'body'    => "Basés à Abidjan, nous connaissons le marché ivoirien et ouest-africain : nos solutions sont pensées pour fonctionner ici, pas juste adaptées d'ailleurs.",
                'badge'   => 'Côte d\'Ivoire & Afrique de l\'Ouest',
            ],
        ];
        ?>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-16">
            <?php foreach ($pillars as $p) : ?>
            <div
                class="relative bg-white/6 border border-white/10 rounded-2xl p-7 flex flex-col hover:bg-white/10 hover:border-klem-red/30 transition-all duration-300"
                data-animate
                data-delay="<?php echo esc_attr($p['delay']); ?>"
            >
                <!-- Icône -->
                <div class="w-12 h-12 rounded-2xl bg-klem-red/15 border border-klem-red/25 flex items-center justify-center mb-5 flex-shrink-0">
                    <svg class="w-6 h-6 text-klem-red" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5" aria-hidden="true">
                        <path stroke-linecap="round" stroke-linejoin="round" d="<?php echo esc_attr($p['icon']); ?>"/>
                    </svg>
                </div>

                <!-- Titre -->
                <h3 class="text-white font-bold text-lg mb-3 leading-snug">
                    <?php echo esc_html($p['title']); ?>
                </h3>

                <!-- Corps -->
                <p class="text-white/60 text-sm leading-relaxed flex-grow mb-5">
                    <?php echo esc_html($p['body']); ?>
                </p>

                <!-- Badge thématique -->
                <div class="flex items-center gap-2 pt-4 border-t border-white/10">
                    <span class="w-1.5 h-1.5 rounded-full bg-klem-red flex-shrink-0"></span>
                    <span class="text-white/35 text-xs font-medium tracking-wide"><?php echo esc_html($p['badge']); ?></span>
                </div>
            </div>
            <?php endforeach; ?>
        </div>

        <!-- Secteurs ciblés -->
        <div class="border-t border-white/10 pt-16" data-animate data-delay="400">
            <div class="max-w-2xl mx-auto text-center mb-12">
                <span class="inline-block text-klem-orange font-bold tracking-widest text-xs uppercase mb-4 px-4 py-1.5 bg-klem-orange/20 rounded-full">
                    <?php esc_html_e('Expertise Sectorielle', 'klem-theme'); ?>
                </span>
                <h3 class="text-xl lg:text-2xl font-extrabold text-white leading-tight mb-3">
                    <?php esc_html_e('Des secteurs que nous maîtrisons', 'klem-theme'); ?>
                </h3>
                <p class="text-white/50 text-base leading-relaxed">
                    <?php esc_html_e("Une compréhension fine des enjeux métiers, secteur par secteur, pour livrer des solutions qui collent au terrain.", 'klem-theme'); ?>
                </p>
            </div>

            <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4 lg:gap-5">
                <?php
                $sectors = [
                    [
                        'icon'  => 'M8.25 18.75a1.5 1.5 0 0 1-3 0m3 0a1.5 1.5 0 0 0-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 0 1-1.125-1.125V14.25m17.25 4.5a1.5 1.5 0 0 1-3 0m3 0a1.5 1.5 0 0 0-3 0m3 0h1.125c.621 0 1.129-.504 1.09-1.124a17.902 17.902 0 0 0-3.213-9.193 2.056 2.056 0 0 0-1.58-.86H14.25M16.5 18.75h-2.25m0-11.177v-.958c0-.568-.422-1.048-.987-1.106a48.554 48.554 0 0 0-10.026 0 1.106 1.106 0 0 0-.987 1.106v7.635m12-6.677v6.677m0 0h-12',
                        'name'  => 'Logistique & Transport',
                        'tag'   => 'Flux, supply chain & mobilité',
                    ],
                    [
                        'icon'  => 'M2.25 18.75a60.07 60.07 0 0 1 15.797 2.101c.727.198 1.453-.342 1.453-1.096V18.75M3.75 4.5v.75A.75.75 0 0 1 3 6h-.75m0 0v-.375c0-.621.504-1.125 1.125-1.125H20.25M2.25 6v9m18-10.5v.75c0 .414.336.75.75.75h.75m-1.5-1.5h.375c.621 0 1.125.504 1.125 1.125v9.75c0 .621-.504 1.125-1.125 1.125h-.375m1.5-1.5H21a.75.75 0 0 0-.75.75v.75m0 0H3.75m0 0h-.375a1.125 1.125 0 0 1-1.125-1.125V15m1.5 1.5v-.75A.75.75 0 0 0 3 15h-.75M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Zm3 0h.008v.008H18V10.5Zm-12 0h.008v.008H6V10.5Z',
                        'name'  => 'Banque & Finance',
                        'tag'   => 'Core banking & conformité',
                    ],
                    [
                        'icon'  => 'M3.75 13.5 10.5 3v7.5h9.75L13.5 21v-7.5H3.75z',
                        'name'  => 'Énergie & Utilities',
                        'tag'   => 'Smart grid & facturation',
                    ],
                    [
                        'icon'  => 'M15.75 10.5V6a3.75 3.75 0 1 0-7.5 0v4.5m-3 0h13.5l-1.125 12.375a2.25 2.25 0 0 1-2.243 2.025H8.618a2.25 2.25 0 0 1-2.243-2.025L5.25 10.5Z',
                        'name'  => 'Commerce & Retail',
                        'tag'   => 'E-commerce & fidélisation',
                    ],
                    [
                        'icon'  => 'M3 21h18M4.5 21V9.75l7.5-4.5 7.5 4.5V21M9.75 21v-6h4.5v6M9.75 12h.008v.008H9.75V12Zm4.5 0h.008v.008h-.008V12Z',
                        'name'  => 'Administrations Publiques',
                        'tag'   => 'Dématérialisation & services citoyens',
                    ],
                    [
                        'icon'  => 'M8.288 15.038a5.25 5.25 0 0 1 7.424 0M5.106 11.856c3.807-3.808 9.98-3.808 13.788 0M1.924 8.674c5.565-5.565 14.587-5.565 20.152 0M12 20.25h.008v.008H12v-.008Z',
                        'name'  => 'Télécommunications',
                        'tag'   => 'Réseaux & data',
                    ],
                ];
                foreach ($sectors as $s) :
                ?>
                <a
                    href="#contact"
                    data-sector="<?php echo esc_attr($s['name']); ?>"
                    aria-label="<?php echo esc_attr(sprintf(__('Discuter d\'un projet dans le secteur %s', 'klem-theme'), $s['name'])); ?>"
                    class="group relative bg-white/6 border border-white/10 rounded-2xl p-5 flex flex-col items-center text-center hover:bg-white/10 hover:border-klem-red/40 hover:-translate-y-1 transition-all duration-300 focus:outline-none focus-visible:ring-2 focus-visible:ring-klem-red/60"
                >
                    <!-- Pastille flèche — apparaît au survol pour signaler l'action -->
                    <span class="absolute top-3 right-3 w-6 h-6 rounded-full bg-klem-red flex items-center justify-center opacity-0 scale-75 group-hover:opacity-100 group-hover:scale-100 transition-all duration-300">
                        <svg class="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                        </svg>
                    </span>

                    <div class="w-11 h-11 rounded-xl bg-klem-red/15 border border-klem-red/25 flex items-center justify-center mb-4 flex-shrink-0 group-hover:bg-klem-red transition-colors duration-300">
                        <svg class="w-5 h-5 text-klem-red group-hover:text-white transition-colors duration-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="<?php echo esc_attr($s['icon']); ?>"/>
                        </svg>
                    </div>
                    <span class="text-white font-bold text-sm leading-snug mb-1.5">
                        <?php echo esc_html($s['name']); ?>
                    </span>
                    <span class="text-white/40 text-xs leading-relaxed group-hover:text-white/60 transition-colors duration-300">
                        <?php echo esc_html($s['tag']); ?>
                    </span>
                </a>
                <?php endforeach; ?>
            </div>

            <p class="text-white/30 text-xs text-center mt-8">
                <?php esc_html_e('Votre secteur n\'apparaît pas dans la liste ?', 'klem-theme'); ?>
                <a href="#contact" data-sector="Autre secteur" class="text-white/50 font-semibold underline decoration-white/20 hover:text-klem-red hover:decoration-klem-red/50 transition-colors duration-200">
                    <?php esc_html_e('Parlons-en directement.', 'klem-theme'); ?>
                </a>
            </p>
        </div>
    </div>
</section>
