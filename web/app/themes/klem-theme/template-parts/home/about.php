<section id="about" class="pt-12 pb-16 bg-white">
    <div class="max-w-6xl mx-auto px-4 sm:px-6">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">

            <!-- Colonne gauche : texte -->
            <div>
                <p class="text-klem-orange font-semibold tracking-widest text-sm uppercase mb-3">
                    <?php esc_html_e('À Propos', 'klem-theme'); ?>
                </p>
                <h2 class="text-2xl lg:text-3xl font-heading text-klem-blue leading-tight mb-6">
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
                    <?php esc_html_e('Nos engagements', 'klem-theme'); ?>
                </p>

                <!-- Liste d'engagements -->
                <div class="space-y-4 relative z-10">

                    <?php
                    $commitments = [
                        ['icon' => 'M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z',                                            'label' => 'Réponse sous 24 à 48h ouvrées'],
                        ['icon' => 'M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z',                            'label' => 'Méthodologie agile, jalons clairs'],
                        ['icon' => 'M3 13.125c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v6.75c0 .621-.504 1.125-1.125 1.125h-2.25A1.125 1.125 0 013 19.875v-6.75zM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V8.625zM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 01-1.125-1.125V4.125z', 'label' => 'Reporting transparent à chaque étape'],
                        ['icon' => 'M9 12.75 11.25 15 15 9.75m-3-7.036A11.959 11.959 0 013.598 6 11.99 11.99 0 003 9.749c0 5.592 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285z', 'label' => 'Sécurité et sauvegardes par défaut'],
                    ];
                    foreach ($commitments as $item) :
                    ?>
                    <div class="flex items-center gap-3">
                        <div class="w-8 h-8 rounded-lg bg-klem-orange/10 flex items-center justify-center flex-shrink-0">
                            <svg class="w-4 h-4 text-klem-orange" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" aria-hidden="true">
                                <path stroke-linecap="round" stroke-linejoin="round" d="<?php echo esc_attr($item['icon']); ?>"/>
                            </svg>
                        </div>
                        <span class="text-sm font-semibold text-klem-blue"><?php echo esc_html($item['label']); ?></span>
                    </div>
                    <?php endforeach; ?>
                </div>

                <!-- Séparateur -->
                <div class="my-7 border-t border-gray-100 relative z-10"></div>

                <!-- Mini-chiffres clés -->
                <div class="grid grid-cols-3 gap-4 relative z-10">
                    <div class="text-center">
                        <p class="text-3xl font-extrabold text-klem-blue leading-none mb-1">6</p>
                        <p class="text-xs text-gray-400 font-medium uppercase tracking-wide"><?php esc_html_e('Secteurs', 'klem-theme'); ?></p>
                    </div>
                    <div class="text-center border-x border-gray-100">
                        <p class="text-3xl font-extrabold text-klem-blue leading-none mb-1">4</p>
                        <p class="text-xs text-gray-400 font-medium uppercase tracking-wide"><?php esc_html_e('Piliers', 'klem-theme'); ?></p>
                    </div>
                    <div class="text-center">
                        <p class="text-3xl font-extrabold text-klem-orange leading-none mb-1">100%</p>
                        <p class="text-xs text-gray-400 font-medium uppercase tracking-wide"><?php esc_html_e('Sur-mesure', 'klem-theme'); ?></p>
                    </div>
                </div>

            </div>
        </div>
    </div>
</section>
