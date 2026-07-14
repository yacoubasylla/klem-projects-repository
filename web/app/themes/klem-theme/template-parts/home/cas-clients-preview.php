<section class="py-16 lg:py-20 bg-white">
    <div class="max-w-6xl mx-auto px-4 sm:px-6">

        <div class="max-w-2xl mb-12" data-animate data-delay="0">
            <span class="inline-block text-klem-red font-bold tracking-widest text-xs uppercase mb-4 px-4 py-1.5 bg-klem-red/8 rounded-full">
                <?php esc_html_e('Cas d\'usage', 'klem-theme'); ?>
            </span>
            <h2 class="text-2xl lg:text-3xl font-extrabold text-klem-blue leading-tight mb-4">
                <?php esc_html_e('Le type de projets que nous menons', 'klem-theme'); ?>
            </h2>
            <p class="text-gray-500 text-lg leading-relaxed">
                <?php esc_html_e("Voici, à titre d'exemple, des scénarios types qui illustrent l'étendue de notre expertise sur ces secteurs.", 'klem-theme'); ?>
            </p>
        </div>

        <?php
        $klem_case_previews = [
            [
                'sector' => __('Logistique & Transport', 'klem-theme'),
                'title'  => __('Optimisation de flotte avec FleetControl', 'klem-theme'),
                'body'   => __('Géolocalisation temps réel et maintenance prédictive plutôt que des tableurs et des pannes découvertes trop tard.', 'klem-theme'),
            ],
            [
                'sector' => __('Éducation & Collectivités', 'klem-theme'),
                'title'  => __('Cantine Connect en établissement scolaire', 'klem-theme'),
                'body'   => __('Paiement dématérialisé et contrôle d\'accès pour réduire les files d\'attente et la charge administrative.', 'klem-theme'),
            ],
            [
                'sector' => __('Commerce & Services', 'klem-theme'),
                'title'  => __('Socle de données cloud pour une PME', 'klem-theme'),
                'body'   => __('Un entrepôt de données et des pipelines automatisés pour un reporting enfin fiable et rapide.', 'klem-theme'),
            ],
        ];
        ?>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
            <?php foreach ($klem_case_previews as $index => $case) : ?>
            <div
                class="border border-gray-100 rounded-2xl p-6 hover:border-klem-red/30 hover:shadow-sm transition-all duration-300"
                data-animate
                data-delay="<?php echo esc_attr((string) ($index * 100)); ?>"
            >
                <span class="inline-block text-klem-red text-[11px] font-extrabold uppercase tracking-widest mb-3">
                    <?php echo esc_html($case['sector']); ?>
                </span>
                <h3 class="text-klem-blue font-bold text-base leading-snug mb-2">
                    <?php echo esc_html($case['title']); ?>
                </h3>
                <p class="text-gray-500 text-sm leading-relaxed">
                    <?php echo esc_html($case['body']); ?>
                </p>
            </div>
            <?php endforeach; ?>
        </div>

        <div class="text-center" data-animate data-delay="300">
            <a
                href="<?php echo esc_url(klem_cas_clients_url()); ?>"
                class="inline-flex items-center gap-1.5 text-sm font-semibold text-klem-red hover:gap-2.5 transition-all duration-200"
            >
                <?php esc_html_e('Voir tous nos cas d\'usage', 'klem-theme'); ?>
                <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                </svg>
            </a>
        </div>
    </div>
</section>
