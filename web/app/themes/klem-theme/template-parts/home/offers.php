<section id="offres" class="py-16 lg:py-20 bg-gray-50/60 border-y border-gray-100">
    <div class="max-w-6xl mx-auto px-4 sm:px-6">

        <div class="max-w-2xl mb-12" data-animate data-delay="0">
            <span class="inline-block text-klem-orange font-bold tracking-widest text-xs uppercase mb-4">
                <?php esc_html_e('Premier pas', 'klem-theme'); ?>
            </span>
            <h2 class="text-2xl lg:text-3xl font-heading text-klem-blue leading-tight mb-4">
                <?php esc_html_e('Un point de départ clair, sans engagement flou', 'klem-theme'); ?>
            </h2>
            <p class="text-gray-500 text-lg leading-relaxed">
                <?php esc_html_e("Avant de parler de projet, nous cadrons un premier périmètre précis. Voici les formats d'entrée les plus demandés.", 'klem-theme'); ?>
            </p>
        </div>

        <?php
        $klem_offers = [
            [
                'badge_label' => __('Gratuit', 'klem-theme'),
                'badge_style' => 'bg-green-100 text-green-700',
                'title'       => __('Diagnostic initial (30 min)', 'klem-theme'),
                'scope'       => __("Un échange avec un expert pour clarifier votre besoin, identifier les priorités et vous dire honnêtement si nous sommes les bons interlocuteurs.", 'klem-theme'),
                'delay'       => '0',
                'icon'        => 'M8.25 4.5l7.5 7.5-7.5 7.5',
            ],
            [
                'badge_label' => __('Sur devis', 'klem-theme'),
                'badge_style' => 'bg-klem-blue/10 text-klem-blue',
                'title'       => __('Audit Data', 'klem-theme'),
                'scope'       => __('Cartographie de vos sources de données actuelles et feuille de route priorisée pour fiabiliser votre reporting.', 'klem-theme'),
                'delay'       => '100',
                'icon'        => 'M20.25 6.375c0 2.278-3.694 4.125-8.25 4.125S3.75 8.653 3.75 6.375m16.5 0c0-2.278-3.694-4.125-8.25-4.125S3.75 4.097 3.75 6.375m16.5 0v11.25c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125V6.375',
            ],
            [
                'badge_label' => __('Sur devis', 'klem-theme'),
                'badge_style' => 'bg-klem-blue/10 text-klem-blue',
                'title'       => __('Audit Cybersécurité', 'klem-theme'),
                'scope'       => __('Évaluation des risques prioritaires sur votre infrastructure et vos accès, avec un plan de remédiation concret.', 'klem-theme'),
                'delay'       => '200',
                'icon'        => 'M9 12.75L11.25 15 15 9.75m-3-7.036A11.959 11.959 0 013.598 6 11.99 11.99 0 003 9.749c0 5.592 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285z',
            ],
            [
                'badge_label' => __('Sur devis', 'klem-theme'),
                'badge_style' => 'bg-klem-blue/10 text-klem-blue',
                'title'       => __('Diagnostic Infrastructure & ERP', 'klem-theme'),
                'scope'       => __("État des lieux technique de votre parc, votre réseau ou votre ERP existant, et recommandations priorisées.", 'klem-theme'),
                'delay'       => '300',
                'icon'        => 'M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z',
            ],
        ];
        ?>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
            <?php foreach ($klem_offers as $offer) : ?>
            <div
                class="flex flex-col bg-white rounded-2xl border border-gray-100 p-6 shadow-sm hover:-translate-y-1 hover:shadow-md hover:border-klem-orange/30 transition-all duration-300"
                data-animate
                data-delay="<?php echo esc_attr($offer['delay']); ?>"
            >
                <div class="w-11 h-11 rounded-xl bg-klem-orange/10 flex items-center justify-center mb-5">
                    <svg class="w-5 h-5 text-klem-orange" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5" aria-hidden="true">
                        <path stroke-linecap="round" stroke-linejoin="round" d="<?php echo esc_attr($offer['icon']); ?>"/>
                    </svg>
                </div>
                <span class="inline-block w-fit text-[11px] font-extrabold uppercase tracking-widest mb-3 px-2.5 py-1 rounded-full <?php echo esc_attr($offer['badge_style']); ?>">
                    <?php echo esc_html($offer['badge_label']); ?>
                </span>
                <h3 class="text-klem-blue font-bold text-base leading-snug mb-2">
                    <?php echo esc_html($offer['title']); ?>
                </h3>
                <p class="text-gray-500 text-sm leading-relaxed flex-grow">
                    <?php echo esc_html($offer['scope']); ?>
                </p>
            </div>
            <?php endforeach; ?>
        </div>

        <div class="mt-10 text-center" data-animate data-delay="400">
            <p class="text-gray-400 text-xs mb-5">
                <?php esc_html_e("Chaque audit est cadré selon votre contexte : périmètre et délai de restitution confirmés avant démarrage.", 'klem-theme'); ?>
            </p>
            <a
                href="#contact"
                class="inline-flex items-center gap-2 bg-klem-blue text-white font-bold px-8 py-4 rounded-xl hover:opacity-90 hover:-translate-y-0.5 hover:shadow-lg transition-all duration-200"
            >
                <?php esc_html_e('Demander un devis', 'klem-theme'); ?>
                <svg class="w-4 h-4 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                </svg>
            </a>
        </div>
    </div>
</section>
