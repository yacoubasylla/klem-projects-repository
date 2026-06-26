<section id="clients" class="py-24 lg:py-32 bg-klem-blue overflow-hidden">
    <div class="max-w-7xl mx-auto px-6">

        <!-- En-tête de section -->
        <div class="max-w-2xl mb-16 lg:mb-20" data-animate data-delay="0">
            <span class="inline-block text-klem-orange font-bold tracking-widest text-xs uppercase mb-4 px-4 py-1.5 bg-klem-orange/20 rounded-full">
                <?php esc_html_e('Notre Différence', 'klem-theme'); ?>
            </span>
            <h2 class="text-4xl lg:text-5xl font-extrabold text-white leading-tight mb-4">
                <?php esc_html_e('Ce qui nous', 'klem-theme'); ?><br>
                <?php esc_html_e('distingue', 'klem-theme'); ?>
            </h2>
            <p class="text-white/50 text-lg leading-relaxed">
                <?php esc_html_e("Une équipe passionnée, des méthodes éprouvées et une ambition claire : accompagner les entreprises africaines dans leur transformation numérique.", 'klem-theme'); ?>
            </p>
        </div>

        <!-- Grille des piliers différenciateurs -->
        <?php
        $pillars = [
            [
                'delay'   => '100',
                'icon'    => 'M17.25 6.75 22.5 12l-5.25 5.25m-10.5 0L1.5 12l5.25-5.25m7.5-3-4.5 16.5',
                'title'   => 'Expertise Technique',
                'body'    => "Architectures robustes et scalables, standards d'ingénierie mondiaux, maîtrise des technologies Big Data, ERP et développement sur-mesure — appliqués aux réalités concrètes du marché local.",
                'badge'   => 'Architecture & Ingénierie',
            ],
            [
                'delay'   => '200',
                'icon'    => 'M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0z',
                'title'   => 'Rigueur & Transparence',
                'body'    => "Périmètre défini, jalons clairs, budget respecté. Chaque engagement est encadré par une méthodologie stricte : vous savez exactement où en est votre projet, à chaque étape.",
                'badge'   => 'Méthode & Fiabilité',
            ],
            [
                'delay'   => '300',
                'icon'    => 'M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0zm-9 8.25c0-3.314 2.686-6 6-6s6 2.686 6 6',
                'title'   => 'Ancrage Africain',
                'body'    => "Basés à Abidjan, nous comprenons les contraintes et les opportunités du marché ivoirien et ouest-africain. Nos solutions sont conçues pour fonctionner ici, pas seulement adaptées depuis ailleurs.",
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

        <!-- Bandeau "secteurs ciblés" -->
        <div class="border-t border-white/10 pt-12" data-animate data-delay="400">
            <p class="text-white/30 text-xs font-bold uppercase tracking-widest text-center mb-8">
                <?php esc_html_e('Secteurs ciblés', 'klem-theme'); ?>
            </p>
            <div class="flex flex-wrap items-center justify-center gap-4 lg:gap-6">
                <?php
                $sectors = [
                    'Logistique & Transport',
                    'Banque & Finance',
                    'Énergie & Utilities',
                    'Commerce & Retail',
                    'Administrations Publiques',
                    'Télécommunications',
                ];
                foreach ($sectors as $sector) :
                ?>
                <span class="px-5 py-2 bg-white/6 border border-white/10 rounded-full text-white/60 text-sm font-medium hover:border-klem-red/40 hover:text-white/80 transition-colors duration-200">
                    <?php echo esc_html($sector); ?>
                </span>
                <?php endforeach; ?>
            </div>
        </div>
    </div>
</section>
