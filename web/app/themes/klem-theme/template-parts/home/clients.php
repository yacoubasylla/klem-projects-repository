<section id="clients" class="py-24 lg:py-32 bg-klem-blue overflow-hidden">
    <div class="max-w-7xl mx-auto px-6">

        <!-- En-tête de section -->
        <div class="max-w-2xl mb-16 lg:mb-20" data-animate data-delay="0">
            <span class="inline-block text-klem-orange font-bold tracking-widest text-xs uppercase mb-4 px-4 py-1.5 bg-klem-orange/20 rounded-full">
                <?php esc_html_e('Ils nous font confiance', 'klem-theme'); ?>
            </span>
            <h2 class="text-4xl lg:text-5xl font-extrabold text-white leading-tight mb-4">
                <?php esc_html_e('Des Résultats Concrets,', 'klem-theme'); ?><br>
                <?php esc_html_e('des Clients Satisfaits', 'klem-theme'); ?>
            </h2>
            <p class="text-white/50 text-lg leading-relaxed">
                <?php esc_html_e("Nos partenaires témoignent de l'impact de nos solutions sur leur transformation digitale.", 'klem-theme'); ?>
            </p>
        </div>

        <!-- Grille de témoignages -->
        <?php
        $testimonials = [
            [
                'quote'   => "KLEM a transformé notre chaîne logistique avec FleetControl. En trois mois, nous avons réduit nos coûts opérationnels de 22 % et gagné une visibilité totale sur notre flotte de 90 véhicules.",
                'name'    => 'Romuald A.',
                'role'    => 'Directeur des Opérations',
                'company' => 'TransAfric Logistics',
                'initials'=> 'RA',
                'delay'   => '100',
            ],
            [
                'quote'   => "L'équipe KLEM a livré notre ERP en avance sur le planning et dans le budget prévu. Leur rigueur technique et leur compréhension de nos métiers sont remarquables.",
                'name'    => 'Clarisse M.',
                'role'    => 'DSI',
                'company' => 'Groupe Energis Bénin',
                'initials'=> 'CM',
                'delay'   => '200',
            ],
            [
                'quote'   => "Notre pipeline Big Data traite désormais plus de 4 millions d'événements par jour avec une latence sub-seconde. L'architecture proposée par KLEM est robuste, scalable et parfaitement documentée.",
                'name'    => 'Ibrahim T.',
                'role'    => 'Head of Data & Analytics',
                'company' => 'BancFin Togo',
                'initials'=> 'IT',
                'delay'   => '300',
            ],
        ];
        ?>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-16">
            <?php foreach ($testimonials as $t) : ?>
            <div
                class="relative bg-white/6 border border-white/10 rounded-2xl p-7 flex flex-col hover:bg-white/10 hover:border-klem-orange/30 transition-all duration-300"
                data-animate
                data-delay="<?php echo esc_attr($t['delay']); ?>"
            >
                <!-- Guillemet décoratif -->
                <svg class="w-8 h-8 text-klem-orange/40 mb-4 flex-shrink-0" fill="currentColor" viewBox="0 0 32 32" aria-hidden="true">
                    <path d="M10 8C6.686 8 4 10.686 4 14v10h10V14H7.333C7.333 11.055 9.055 8.667 12 8H10zm14 0c-3.314 0-6 2.686-6 6v10h10V14h-6.667C21.333 11.055 23.055 8.667 26 8h-2z"/>
                </svg>

                <!-- Texte du témoignage -->
                <p class="text-white/70 text-sm leading-relaxed flex-grow mb-6">
                    <?php echo esc_html($t['quote']); ?>
                </p>

                <!-- Auteur -->
                <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-klem-orange flex items-center justify-center flex-shrink-0">
                        <span class="text-white font-bold text-xs"><?php echo esc_html($t['initials']); ?></span>
                    </div>
                    <div>
                        <p class="text-white font-semibold text-sm leading-none mb-0.5"><?php echo esc_html($t['name']); ?></p>
                        <p class="text-white/40 text-xs leading-none"><?php echo esc_html($t['role']); ?> · <?php echo esc_html($t['company']); ?></p>
                    </div>
                </div>
            </div>
            <?php endforeach; ?>
        </div>

        <!-- Bandeau "secteurs couverts" -->
        <div class="border-t border-white/10 pt-12" data-animate data-delay="400">
            <p class="text-white/30 text-xs font-bold uppercase tracking-widest text-center mb-8">
                <?php esc_html_e('Secteurs couverts', 'klem-theme'); ?>
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
                <span class="px-5 py-2 bg-white/6 border border-white/10 rounded-full text-white/60 text-sm font-medium hover:border-klem-orange/40 hover:text-white/80 transition-colors duration-200">
                    <?php echo esc_html($sector); ?>
                </span>
                <?php endforeach; ?>
            </div>
        </div>
    </div>
</section>
