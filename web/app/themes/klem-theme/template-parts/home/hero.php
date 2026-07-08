<section class="relative overflow-hidden bg-white">

    <!-- Décors circulaires de fond (soft, cohérent avec la section "À Propos") -->
    <div class="absolute -top-24 -left-24 w-96 h-96 rounded-full bg-klem-orange/5 pointer-events-none" aria-hidden="true"></div>
    <div class="absolute top-1/3 -left-10 w-64 h-64 rounded-full bg-klem-blue/5 pointer-events-none" aria-hidden="true"></div>

    <div class="relative max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-4 lg:gap-6 py-8 lg:py-24">

            <!-- IMAGE : en premier dans le HTML → haut sur mobile, droite sur desktop -->
            <div class="lg:col-start-2 lg:row-start-1 lg:self-stretch relative rounded-2xl overflow-hidden bg-cover bg-center lg:[clip-path:polygon(20%_0%,100%_0%,100%_100%,0%_100%)] lg:-ml-6"
                 style="background-image: url('<?php echo esc_url(get_template_directory_uri() . '/assets/images/hero-bg.jpg'); ?>'); background-color: #13294B; min-height: 260px;"
                 data-animate data-delay="150"
                 aria-hidden="true">

                <!-- Overlay léger pour lisibilité des cartes -->
                <div class="absolute inset-0" style="background-color: rgba(10,20,45,0.45);"></div>

                <!-- Carte 1 : Pipeline Big Data — haut gauche (left ≥ 22% pour passer le clip-path 20%) -->
                <div class="absolute backdrop-blur-md rounded-2xl p-3.5 shadow-xl animate-float" style="top:8%;left:26%;width:168px;background-color:rgba(255,255,255,0.06);border:1px solid rgba(255,255,255,0.14);animation-delay:0s;">
                    <div class="flex items-center gap-2 mb-2">
                        <span class="relative flex h-2.5 w-2.5 flex-shrink-0">
                            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-klem-orange opacity-75"></span>
                            <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-klem-orange"></span>
                        </span>
                        <span class="text-white/70 text-[10px] font-bold uppercase tracking-wider">Pipeline Big Data</span>
                    </div>
                    <p class="text-white font-extrabold text-xl leading-none" data-count-target="4.2" data-count-suffix="M" data-count-decimals="1">0M</p>
                    <p class="text-white/50 text-xs mt-1"><?php esc_html_e('événements / jour', 'klem-theme'); ?></p>
                </div>

                <!-- Carte 2 : Apps Sur-Mesure — milieu droit (desktop only) -->
                <div class="hidden lg:block absolute backdrop-blur-md rounded-2xl p-3.5 shadow-xl animate-float" style="top:40%;right:5%;width:168px;background-color:rgba(255,255,255,0.06);border:1px solid rgba(255,255,255,0.14);animation-delay:1.3s;">
                    <div class="flex items-center gap-2 mb-2">
                        <span class="relative flex h-2.5 w-2.5 flex-shrink-0">
                            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75" style="animation-delay:0.3s"></span>
                            <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-blue-400"></span>
                        </span>
                        <span class="text-white/70 text-[10px] font-bold uppercase tracking-wider"><?php esc_html_e('Apps Sur-Mesure', 'klem-theme'); ?></span>
                    </div>
                    <p class="text-white font-extrabold text-xl leading-none" data-count-target="18" data-count-suffix="+">0+</p>
                    <p class="text-white/50 text-xs mt-1"><?php esc_html_e('solutions déployées', 'klem-theme'); ?></p>
                </div>

                <!-- Carte 3 : Disponibilité — bas gauche (mobile + desktop) -->
                <div class="absolute backdrop-blur-md rounded-2xl p-3.5 shadow-xl animate-float" style="bottom:8%;left:12%;width:168px;background-color:rgba(255,255,255,0.06);border:1px solid rgba(255,255,255,0.14);animation-delay:2.6s;">
                    <div class="flex items-center gap-2 mb-2">
                        <span class="relative flex h-2.5 w-2.5 flex-shrink-0">
                            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75" style="animation-delay:0.6s"></span>
                            <span class="relative inline-flex rounded-full h-2.5 w-2.5 bg-green-400"></span>
                        </span>
                        <span class="text-white/70 text-[10px] font-bold uppercase tracking-wider"><?php esc_html_e('Disponibilité', 'klem-theme'); ?></span>
                    </div>
                    <p class="text-white font-extrabold text-xl leading-none" data-count-target="99.97" data-count-suffix="%" data-count-decimals="2">0%</p>
                    <p class="text-white/50 text-xs mt-1"><?php esc_html_e('uptime infrastructure', 'klem-theme'); ?></p>
                </div>

            </div>

            <!-- TEXTE : en second dans le HTML → bas sur mobile, gauche sur desktop -->
            <div class="lg:col-start-1 lg:row-start-1 flex flex-col justify-center">
                <span class="inline-block w-fit text-klem-orange font-bold tracking-widest text-xs uppercase mb-6 px-4 py-1.5 bg-klem-orange/10 rounded-full" data-animate data-delay="0">
                    <?php esc_html_e('Intégrateur Numérique · Afrique', 'klem-theme'); ?>
                </span>

                <h1 class="text-3xl sm:text-4xl lg:text-5xl font-bold text-klem-blue leading-tight mb-6" data-animate data-delay="100">
                    <?php esc_html_e('Maîtrisez votre', 'klem-theme'); ?><br>
                    <span class="text-klem-orange"><?php esc_html_e('Souveraineté', 'klem-theme'); ?></span><br>
                    <?php esc_html_e('Numérique', 'klem-theme'); ?>
                </h1>

                <p class="text-gray-500 text-base leading-relaxed max-w-md mb-10" data-animate data-delay="200">
                    <?php esc_html_e('Confiez-nous votre transformation digitale et concentrez-vous sur la croissance de votre organisation.', 'klem-theme'); ?>
                </p>

                <div class="flex flex-wrap gap-4" data-animate data-delay="300">
                    <a
                        href="#services"
                        class="inline-flex items-center justify-center gap-2 bg-klem-orange text-white font-bold px-5 py-2.5 rounded-lg text-sm shadow-lg shadow-klem-orange/20 hover:brightness-110 hover:-translate-y-0.5 hover:shadow-xl hover:shadow-klem-orange/30 transition-all duration-200"
                    >
                        <?php esc_html_e('Nos services', 'klem-theme'); ?>
                        <svg class="w-4 h-4 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                        </svg>
                    </a>
                    <a
                        href="#about"
                        class="inline-flex items-center justify-center border border-gray-300 text-gray-700 font-semibold px-5 py-2.5 rounded-lg text-sm hover:border-klem-orange hover:text-klem-orange hover:-translate-y-0.5 transition-all duration-200"
                    >
                        <?php esc_html_e('Notre histoire', 'klem-theme'); ?>
                    </a>
                </div>
            </div>

        </div>
    </div>

    <!-- Bande de statistiques -->
    <div class="relative border-t border-gray-200 bg-gray-50/60">
        <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
            <?php
            $hero_stats = [
                [
                    'icon'    => 'M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z',
                    'target'  => '5',
                    'suffix'  => '+',
                    'label'   => 'Ans',
                    'caption' => "d'expertise prouvée",
                    'delay'   => '0',
                ],
                [
                    'icon'    => 'M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z',
                    'target'  => '30',
                    'suffix'  => '+',
                    'label'   => 'Projets',
                    'caption' => 'livrés avec succès',
                    'delay'   => '100',
                ],
                [
                    'icon'    => 'M3.75 6A2.25 2.25 0 0 1 6 3.75h2.25A2.25 2.25 0 0 1 10.5 6v2.25a2.25 2.25 0 0 1-2.25 2.25H6a2.25 2.25 0 0 1-2.25-2.25V6ZM3.75 15.75A2.25 2.25 0 0 1 6 13.5h2.25a2.25 2.25 0 0 1 2.25 2.25V18a2.25 2.25 0 0 1-2.25 2.25H6A2.25 2.25 0 0 1 3.75 18v-2.25ZM13.5 6a2.25 2.25 0 0 1 2.25-2.25H18A2.25 2.25 0 0 1 20.25 6v2.25A2.25 2.25 0 0 1 18 10.5h-2.25a2.25 2.25 0 0 1-2.25-2.25V6ZM13.5 15.75a2.25 2.25 0 0 1 2.25-2.25H18a2.25 2.25 0 0 1 2.25 2.25V18A2.25 2.25 0 0 1 18 20.25h-2.25A2.25 2.25 0 0 1 13.5 18v-2.25Z',
                    'target'  => '4',
                    'suffix'  => '',
                    'label'   => 'Piliers',
                    'caption' => "d'expertise couverts",
                    'delay'   => '200',
                ],
                [
                    'icon'    => 'M9.813 15.904 9 18.75l-.813-2.846a4.5 4.5 0 0 0-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 0 0 3.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 0 0 3.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 0 0-3.09 3.09Z M18.259 8.715 18 9.75l-.259-1.035a3.375 3.375 0 0 0-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 0 0 2.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 0 0 2.456 2.456L21.75 6l-1.035.259a3.375 3.375 0 0 0-2.456 2.456Z',
                    'target'  => '100',
                    'suffix'  => '%',
                    'label'   => '',
                    'caption' => 'Sur-mesure, sans compromis',
                    'delay'   => '300',
                ],
            ];
            ?>
            <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 lg:gap-6">
                <?php foreach ($hero_stats as $stat) : ?>
                <div
                    class="group bg-white rounded-2xl border border-gray-100 shadow-sm p-6 hover:-translate-y-1 hover:shadow-md hover:border-klem-orange/30 transition-all duration-300"
                    data-animate
                    data-delay="<?php echo esc_attr($stat['delay']); ?>"
                >
                    <div class="w-10 h-10 rounded-xl bg-klem-orange/10 flex items-center justify-center mb-4 group-hover:bg-klem-orange transition-colors duration-300">
                        <svg class="w-5 h-5 text-klem-orange group-hover:text-white transition-colors duration-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="<?php echo esc_attr($stat['icon']); ?>"/>
                        </svg>
                    </div>
                    <div class="flex items-baseline gap-2 mb-1">
                        <span
                            class="text-3xl font-extrabold text-klem-blue leading-none"
                            data-count-target="<?php echo esc_attr($stat['target']); ?>"
                            data-count-suffix="<?php echo esc_attr($stat['suffix']); ?>"
                        >0<?php echo esc_html($stat['suffix']); ?></span>
                        <?php if ($stat['label']) : ?>
                        <span class="text-base font-bold text-klem-blue"><?php echo esc_html($stat['label']); ?></span>
                        <?php endif; ?>
                    </div>
                    <p class="text-klem-orange text-sm font-semibold"><?php echo esc_html($stat['caption']); ?></p>
                </div>
                <?php endforeach; ?>
            </div>
        </div>
    </div>
</section>
