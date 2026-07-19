<section class="py-14 lg:py-16 bg-white border-t border-gray-100">
    <div class="max-w-[1600px] mx-auto px-4 sm:px-6">

        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-8">

            <div class="max-w-md" data-animate data-delay="0">
                <span class="inline-block text-klem-orange font-bold tracking-widest text-xs uppercase mb-3 px-4 py-1.5 bg-klem-orange/10 rounded-full">
                    <?php esc_html_e('Expertise certifiée', 'klem-theme'); ?>
                </span>
                <h2 class="text-xl lg:text-2xl font-heading text-klem-blue leading-tight mb-2">
                    <?php esc_html_e('Une équipe dirigeante formée aux standards internationaux', 'klem-theme'); ?>
                </h2>
                <p class="text-gray-500 text-sm leading-relaxed">
                    <?php esc_html_e("Certifications individuelles détenues par notre équipe dirigeante, garantes d'une méthode agile et d'une gouvernance IT rigoureuse sur chaque projet.", 'klem-theme'); ?>
                </p>
            </div>

            <?php
            $klem_certifications = [
                [
                    'name'  => __('Professional Scrum Master I', 'klem-theme'),
                    'org'   => __('Scrum.org', 'klem-theme'),
                    'icon'  => 'M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0 3.181 3.183a8.25 8.25 0 0 0 13.803-3.7M4.031 9.865a8.25 8.25 0 0 1 13.803-3.7l3.181 3.182m0-4.991v4.99',
                ],
                [
                    'name'  => __('ITIL® V4 Foundation', 'klem-theme'),
                    'org'   => __('Axelos / PeopleCert', 'klem-theme'),
                    'icon'  => 'M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.324.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 0 1 1.37.49l1.296 2.247a1.125 1.125 0 0 1-.26 1.431l-1.003.827c-.293.24-.438.613-.431.992a6.759 6.759 0 0 1 0 .255c-.007.378.138.75.43.99l1.005.828c.424.35.534.954.26 1.43l-1.298 2.247a1.125 1.125 0 0 1-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.57 6.57 0 0 1-.22.128c-.331.183-.581.495-.644.869l-.213 1.28c-.09.543-.56.941-1.11.941h-2.594c-.55 0-1.02-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 0 1-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 0 1-1.369-.49l-1.297-2.247a1.125 1.125 0 0 1 .26-1.431l1.004-.827c.292-.24.437-.613.43-.992a6.932 6.932 0 0 1 0-.255c.007-.378-.138-.75-.43-.99l-1.004-.828a1.125 1.125 0 0 1-.26-1.43l1.297-2.247a1.125 1.125 0 0 1 1.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.087.22-.128.332-.183.582-.495.644-.869l.214-1.281Z M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z',
                ],
                [
                    'name'  => __('ITIL® V3 Foundation', 'klem-theme'),
                    'org'   => __('Axelos / PeopleCert', 'klem-theme'),
                    'icon'  => 'M11.35 3.836c-.065.21-.1.433-.1.664 0 .414.336.75.75.75h4.5a.75.75 0 0 0 .75-.75 2.25 2.25 0 0 0-.1-.664m-5.8 0A2.251 2.251 0 0 1 13.5 2.25H15c1.152 0 2.243.696 2.7 1.816m-9.8-3.196A2.25 2.25 0 0 1 9.75 2.25H15a2.25 2.25 0 0 1 2.25 2.25v.75a2.25 2.25 0 0 1-2.25 2.25H9.75A2.25 2.25 0 0 1 7.5 5.25v-.75c0-.414.06-.813.17-1.19m5.03 4.19H8.25m8.25-4.19c.877.316 1.611.936 2.07 1.73M17.25 5.25a2.25 2.25 0 0 1-2.25 2.25H9.75A2.25 2.25 0 0 1 7.5 5.25m9.75 0h1.5a2.25 2.25 0 0 1 2.25 2.25v12a2.25 2.25 0 0 1-2.25 2.25H5.25a2.25 2.25 0 0 1-2.25-2.25v-12a2.25 2.25 0 0 1 2.25-2.25h1.5',
                ],
            ];
            ?>

            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 lg:gap-5 lg:flex-shrink-0">
                <?php foreach ($klem_certifications as $index => $cert) : ?>
                <div
                    class="flex items-center gap-3 bg-gray-50/80 border border-gray-100 rounded-xl px-4 py-3.5 min-w-[220px]"
                    data-animate
                    data-delay="<?php echo esc_attr((string) (100 + $index * 100)); ?>"
                >
                    <div class="w-9 h-9 rounded-lg bg-klem-blue/10 flex items-center justify-center flex-shrink-0">
                        <svg class="w-4.5 h-4.5 text-klem-blue" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="<?php echo esc_attr($cert['icon']); ?>"/>
                        </svg>
                    </div>
                    <div>
                        <p class="text-klem-blue font-bold text-sm leading-snug"><?php echo esc_html($cert['name']); ?></p>
                        <p class="text-gray-400 text-xs"><?php echo esc_html($cert['org']); ?></p>
                    </div>
                </div>
                <?php endforeach; ?>
            </div>

        </div>
    </div>
</section>
