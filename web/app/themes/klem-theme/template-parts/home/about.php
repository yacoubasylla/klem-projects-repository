<section id="about" class="py-24 bg-white">
    <div class="max-w-6xl mx-auto px-4 sm:px-6">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">

            <!-- Colonne gauche : texte -->
            <div>
                <p class="text-klem-orange font-semibold tracking-widest text-sm uppercase mb-3">
                    <?php esc_html_e('À Propos', 'klem-theme'); ?>
                </p>
                <h2 class="text-2xl lg:text-3xl font-bold text-klem-blue leading-tight mb-6">
                    <?php esc_html_e('Partenaire Stratégique de votre Croissance', 'klem-theme'); ?>
                </h2>
                <p class="text-gray-600 leading-relaxed mb-4">
                    <?php esc_html_e('KLEM Technologies & Services est un intégrateur numérique positionné sur les marchés privés et publics d\'Afrique. Notre mission : rendre accessible l\'excellence technologique aux organisations qui construisent le continent de demain.', 'klem-theme'); ?>
                </p>
                <a href="#contact" class="inline-block mt-4 text-klem-orange font-semibold border-b-2 border-klem-orange pb-1 hover:opacity-80 transition-opacity">
                    <?php esc_html_e('Parlons de votre projet →', 'klem-theme'); ?>
                </a>
            </div>

            <!-- Colonne droite : panneau de performances -->
            <div class="relative bg-gradient-to-br from-gray-50 to-white rounded-3xl p-8 border border-gray-100 shadow-sm overflow-hidden" data-animate data-delay="200">

                <!-- Décors circulaires de fond (soft) -->
                <div class="absolute -top-10 -right-10 w-48 h-48 rounded-full bg-klem-orange/5 pointer-events-none" aria-hidden="true"></div>
                <div class="absolute -bottom-8 -left-8 w-36 h-36 rounded-full bg-klem-blue/5 pointer-events-none" aria-hidden="true"></div>

                <!-- Label panneau -->
                <p class="text-xs font-bold tracking-widest text-gray-400 uppercase mb-7 relative z-10">
                    <?php esc_html_e('Performances clés', 'klem-theme'); ?>
                </p>

                <!-- Barres de KPI -->
                <div class="space-y-5 relative z-10">

                    <?php
                    $kpis = [
                        ['label' => 'Satisfaction clients',         'value' => 96,   'color' => 'bg-klem-orange'],
                        ['label' => 'Projets livrés dans les délais', 'value' => 98, 'color' => 'bg-klem-blue'],
                        ['label' => 'Disponibilité infrastructure',  'value' => 100,  'color' => 'bg-klem-orange'],
                        ['label' => 'Taux de fidélisation clients',  'value' => 88,   'color' => 'bg-klem-blue'],
                    ];
                    foreach ($kpis as $kpi) :
                    ?>
                    <div>
                        <div class="flex items-center justify-between mb-2">
                            <span class="text-sm font-semibold text-klem-blue"><?php echo esc_html($kpi['label']); ?></span>
                            <span class="text-sm font-extrabold text-klem-orange"><?php echo esc_html($kpi['value']); ?>%</span>
                        </div>
                        <div class="h-2 bg-gray-100 rounded-full overflow-hidden">
                            <div
                                class="h-full rounded-full w-0 transition-all duration-1000 ease-out <?php echo esc_attr($kpi['color']); ?>"
                                data-target-width="<?php echo esc_attr((string) $kpi['value']); ?>"
                                aria-label="<?php echo esc_attr($kpi['value'] . '%'); ?>"
                            ></div>
                        </div>
                    </div>
                    <?php endforeach; ?>
                </div>

                <!-- Séparateur -->
                <div class="my-7 border-t border-gray-100 relative z-10"></div>

                <!-- Mini-chiffres clés -->
                <div class="grid grid-cols-3 gap-4 relative z-10">
                    <div class="text-center">
                        <p class="text-3xl font-extrabold text-klem-blue leading-none mb-1">5+</p>
                        <p class="text-xs text-gray-400 font-medium uppercase tracking-wide"><?php esc_html_e('Années', 'klem-theme'); ?></p>
                    </div>
                    <div class="text-center border-x border-gray-100">
                        <p class="text-3xl font-extrabold text-klem-blue leading-none mb-1">30+</p>
                        <p class="text-xs text-gray-400 font-medium uppercase tracking-wide"><?php esc_html_e('Projets', 'klem-theme'); ?></p>
                    </div>
                    <div class="text-center">
                        <p class="text-3xl font-extrabold text-klem-orange leading-none mb-1">4</p>
                        <p class="text-xs text-gray-400 font-medium uppercase tracking-wide"><?php esc_html_e('Piliers', 'klem-theme'); ?></p>
                    </div>
                </div>

            </div>
        </div>
    </div>
</section>
