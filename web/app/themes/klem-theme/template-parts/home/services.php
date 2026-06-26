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

        <?php
        /* ── Illustrations SVG inline — claires, high-tech, sans image externe ── */

        $svg_data_engineering = '
<svg viewBox="0 0 400 176" preserveAspectRatio="xMidYMid slice" class="w-full h-full" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
  <defs>
    <linearGradient id="svc-bg1" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#EFF6FF"/>
      <stop offset="100%" stop-color="#DBEAFE"/>
    </linearGradient>
  </defs>
  <rect width="400" height="176" fill="url(#svc-bg1)"/>
  <!-- Grille fine -->
  <g stroke="#BFDBFE" stroke-width="0.5">
    <line x1="0" y1="44" x2="400" y2="44"/><line x1="0" y1="88" x2="400" y2="88"/>
    <line x1="0" y1="132" x2="400" y2="132"/>
    <line x1="80" y1="0" x2="80" y2="176"/><line x1="160" y1="0" x2="160" y2="176"/>
    <line x1="240" y1="0" x2="240" y2="176"/><line x1="320" y1="0" x2="320" y2="176"/>
  </g>
  <!-- Traces de circuit -->
  <g stroke="#3B82F6" stroke-width="2" fill="none" opacity="0.55">
    <path d="M0 88 H100 V44 H240 V88 H400"/>
    <path d="M80 176 V132 H200 V88"/>
    <path d="M160 0 V44"/>
    <path d="M240 132 H320 V176"/>
    <path d="M320 44 H360 V88"/>
  </g>
  <!-- Nœuds principaux -->
  <g fill="#13294B">
    <circle cx="100" cy="88" r="6"/><circle cx="240" cy="88" r="6"/>
    <circle cx="80" cy="132" r="5"/><circle cx="200" cy="88" r="4"/>
    <circle cx="160" cy="44" r="5"/><circle cx="320" cy="44" r="5"/>
  </g>
  <!-- Nœuds accent rouge -->
  <g fill="#E42313">
    <circle cx="240" cy="44" r="6"/><circle cx="320" cy="176" r="5"/>
  </g>
  <!-- Paquets de données -->
  <rect x="168" y="41" width="10" height="7" rx="1.5" fill="#13294B" opacity="0.65"/>
  <rect x="306" y="173" width="10" height="7" rx="1.5" fill="#E42313" opacity="0.65"/>
  <!-- Label décoratif -->
  <rect x="16" y="16" width="52" height="9" rx="4.5" fill="#BFDBFE"/>
  <rect x="16" y="30" width="36" height="7" rx="3.5" fill="#93C5FD"/>
</svg>';

        $svg_apps = '
<svg viewBox="0 0 400 176" preserveAspectRatio="xMidYMid slice" class="w-full h-full" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
  <defs>
    <linearGradient id="svc-bg2" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#F5F3FF"/>
      <stop offset="100%" stop-color="#EDE9FE"/>
    </linearGradient>
  </defs>
  <rect width="400" height="176" fill="url(#svc-bg2)"/>
  <!-- Fenêtre navigateur -->
  <rect x="36" y="18" width="328" height="140" rx="9" fill="white" stroke="#C4B5FD" stroke-width="1.5" opacity="0.85"/>
  <!-- Barre de navigation -->
  <rect x="36" y="18" width="328" height="30" rx="9" fill="#DDD6FE"/>
  <rect x="36" y="34" width="328" height="14" fill="#DDD6FE"/>
  <!-- Boutons de contrôle -->
  <circle cx="56" cy="33" r="5" fill="#F87171"/>
  <circle cx="72" cy="33" r="5" fill="#FBBF24"/>
  <circle cx="88" cy="33" r="5" fill="#34D399"/>
  <!-- Barre URL -->
  <rect x="110" y="26" width="200" height="14" rx="7" fill="white" opacity="0.75"/>
  <rect x="118" y="30" width="90" height="6" rx="3" fill="#A78BFA" opacity="0.5"/>
  <!-- Lignes de code -->
  <rect x="52" y="60" width="50" height="7" rx="3.5" fill="#7C3AED" opacity="0.75"/>
  <rect x="110" y="60" width="90" height="7" rx="3.5" fill="#6366F1" opacity="0.6"/>
  <rect x="52" y="75" width="30" height="7" rx="3.5" fill="#A78BFA" opacity="0.8"/>
  <rect x="90" y="75" width="120" height="7" rx="3.5" fill="#6366F1" opacity="0.55"/>
  <rect x="52" y="90" width="70" height="7" rx="3.5" fill="#7C3AED" opacity="0.65"/>
  <rect x="130" y="90" width="60" height="7" rx="3.5" fill="#A78BFA" opacity="0.5"/>
  <rect x="52" y="105" width="140" height="7" rx="3.5" fill="#6366F1" opacity="0.6"/>
  <rect x="52" y="120" width="80" height="7" rx="3.5" fill="#7C3AED" opacity="0.75"/>
  <rect x="140" y="120" width="50" height="7" rx="3.5" fill="#A78BFA" opacity="0.5"/>
  <rect x="52" y="135" width="110" height="7" rx="3.5" fill="#6366F1" opacity="0.55"/>
  <!-- Curseur -->
  <rect x="242" y="73" width="2.5" height="16" rx="1" fill="#13294B" opacity="0.85"/>
  <!-- Bracket décoratif -->
  <text x="266" y="92" font-family="monospace" font-size="28" fill="#DDD6FE" font-weight="700">{}</text>
</svg>';

        $svg_erp = '
<svg viewBox="0 0 400 176" preserveAspectRatio="xMidYMid slice" class="w-full h-full" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
  <defs>
    <linearGradient id="svc-bg3" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#ECFDF5"/>
      <stop offset="100%" stop-color="#D1FAE5"/>
    </linearGradient>
  </defs>
  <rect width="400" height="176" fill="url(#svc-bg3)"/>
  <!-- Lignes de connexion -->
  <g stroke="#6EE7B7" stroke-width="2" fill="none">
    <path d="M60 88 C120 55 170 55 200 88"/>
    <path d="M200 88 C240 55 300 70 345 50"/>
    <path d="M200 88 C225 120 280 128 328 140"/>
    <path d="M60 88 C85 120 130 135 165 128"/>
    <path d="M165 128 C210 122 268 132 328 140"/>
    <path d="M60 88 L60 30"/>
    <path d="M345 50 L345 20"/>
  </g>
  <!-- Nœuds verts -->
  <g fill="#059669" stroke="white" stroke-width="2.5">
    <circle cx="60" cy="88" r="10"/>
    <circle cx="200" cy="88" r="13"/>
    <circle cx="345" cy="50" r="10"/>
    <circle cx="165" cy="128" r="9"/>
    <circle cx="328" cy="140" r="10"/>
    <circle cx="60" cy="30" r="7"/>
    <circle cx="345" cy="20" r="7"/>
  </g>
  <!-- Points relais rouges -->
  <g fill="#E42313">
    <circle cx="132" cy="68" r="5"/><circle cx="270" cy="65" r="5"/>
    <circle cx="247" cy="120" r="5"/>
  </g>
  <!-- Flèches directionnelles -->
  <g fill="#059669" opacity="0.6">
    <polygon points="130,61 138,68 130,75"/>
    <polygon points="268,58 276,65 268,72"/>
  </g>
  <!-- Labels mini -->
  <rect x="40" y="100" width="40" height="6" rx="3" fill="#10B981" opacity="0.35"/>
  <rect x="188" y="105" width="25" height="6" rx="3" fill="#10B981" opacity="0.35"/>
</svg>';

        $svg_hardware = '
<svg viewBox="0 0 400 176" preserveAspectRatio="xMidYMid slice" class="w-full h-full" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
  <defs>
    <linearGradient id="svc-bg4" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#F8FAFC"/>
      <stop offset="100%" stop-color="#E2E8F0"/>
    </linearGradient>
  </defs>
  <rect width="400" height="176" fill="url(#svc-bg4)"/>
  <!-- Rack serveur -->
  <rect x="70" y="14" width="260" height="148" rx="7" fill="white" stroke="#CBD5E1" stroke-width="1.5"/>
  <!-- Unités serveur -->
  <rect x="82" y="26" width="236" height="22" rx="3" fill="#F1F5F9" stroke="#E2E8F0" stroke-width="1"/>
  <rect x="82" y="54" width="236" height="22" rx="3" fill="#F1F5F9" stroke="#E2E8F0" stroke-width="1"/>
  <rect x="82" y="82" width="236" height="22" rx="3" fill="#F1F5F9" stroke="#E2E8F0" stroke-width="1"/>
  <rect x="82" y="110" width="236" height="22" rx="3" fill="#F1F5F9" stroke="#E2E8F0" stroke-width="1"/>
  <rect x="82" y="138" width="236" height="14" rx="3" fill="#F1F5F9" stroke="#E2E8F0" stroke-width="1"/>
  <!-- LEDs rangée 1 -->
  <circle cx="96" cy="37" r="3.5" fill="#34D399"/>
  <circle cx="108" cy="37" r="3.5" fill="#34D399"/>
  <circle cx="120" cy="37" r="3.5" fill="#FCD34D"/>
  <!-- LEDs rangée 2 -->
  <circle cx="96" cy="65" r="3.5" fill="#34D399"/>
  <circle cx="108" cy="65" r="3.5" fill="#34D399"/>
  <circle cx="120" cy="65" r="3.5" fill="#34D399"/>
  <!-- LEDs rangée 3 -->
  <circle cx="96" cy="93" r="3.5" fill="#E42313"/>
  <circle cx="108" cy="93" r="3.5" fill="#34D399"/>
  <circle cx="120" cy="93" r="3.5" fill="#34D399"/>
  <!-- LEDs rangée 4 -->
  <circle cx="96" cy="121" r="3.5" fill="#34D399"/>
  <circle cx="108" cy="121" r="3.5" fill="#34D399"/>
  <circle cx="120" cy="121" r="3.5" fill="#FCD34D"/>
  <!-- Baies disques -->
  <g fill="#CBD5E1">
    <rect x="240" y="30" width="32" height="14" rx="2"/>
    <rect x="277" y="30" width="32" height="14" rx="2"/>
    <rect x="240" y="58" width="32" height="14" rx="2"/>
    <rect x="277" y="58" width="32" height="14" rx="2"/>
    <rect x="240" y="86" width="32" height="14" rx="2"/>
    <rect x="277" y="86" width="32" height="14" rx="2"/>
    <rect x="240" y="114" width="32" height="14" rx="2"/>
    <rect x="277" y="114" width="32" height="14" rx="2"/>
  </g>
  <!-- Câbles côté droit décoratifs -->
  <g stroke="#94A3B8" stroke-width="1.5" fill="none" opacity="0.6">
    <path d="M340 37 C360 37 370 65 340 65"/>
    <path d="M340 93 C358 93 368 121 340 121"/>
  </g>
</svg>';

        $klem_services = [
            [
                'num'       => '01',
                'title'     => 'Ingénierie des Données',
                'desc'      => 'Pipelines Big Data, architectures temps réel (Kafka, Spark) et lacs de données pour transformer vos données brutes en avantage stratégique décisif.',
                'delay'     => '100',
                'img_alt'   => 'Illustration circuit data — ingénierie des données',
                'svg'       => $svg_data_engineering,
                'icon_path' => 'M20.25 6.375c0 2.278-3.694 4.125-8.25 4.125S3.75 8.653 3.75 6.375m16.5 0c0-2.278-3.694-4.125-8.25-4.125S3.75 4.097 3.75 6.375m16.5 0v11.25c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125V6.375m16.5 5.625c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125',
            ],
            [
                'num'       => '02',
                'title'     => 'Applications Sur-Mesure',
                'desc'      => "Développement d'applications web et mobiles d'envergure, ERP et logiciels métiers haute performance, 100 % adaptés à vos processus et à votre ambition.",
                'delay'     => '200',
                'img_alt'   => 'Illustration éditeur de code — applications sur-mesure',
                'svg'       => $svg_apps,
                'icon_path' => 'M17.25 6.75 22.5 12l-5.25 5.25m-10.5 0L1.5 12l5.25-5.25m7.5-3-4.5 16.5',
            ],
            [
                'num'       => '03',
                'title'     => 'Intégration ERP & FleetControl',
                'desc'      => "Orchestration de systèmes d'information complexes et déploiement de FleetControl, notre solution de gestion de flotte intelligente pour les opérateurs africains.",
                'delay'     => '300',
                'img_alt'   => 'Illustration réseau de nœuds — intégration ERP',
                'svg'       => $svg_erp,
                'icon_path' => 'M7.5 21 3 16.5m0 0 4.5-4.5M3 16.5h13.5m0-13.5L21 7.5m0 0-4.5 4.5M21 7.5H7.5',
            ],
            [
                'num'       => '04',
                'title'     => 'Matériel IT & Infrastructure',
                'desc'      => "Fourniture et déploiement d'équipements serveurs, réseaux et postes de travail de qualité entreprise pour des infrastructures critiques robustes et évolutives.",
                'delay'     => '400',
                'img_alt'   => 'Illustration rack serveur — infrastructure IT',
                'svg'       => $svg_hardware,
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
                <!-- Bandeau illustration SVG -->
                <div class="relative h-44 overflow-hidden flex-shrink-0" role="img" aria-label="<?php echo esc_attr($service['img_alt']); ?>">

                    <!-- Illustration SVG inline (claire, high-tech, sans overlay sombre) -->
                    <?php echo $service['svg']; // phpcs:ignore WordPress.Security.EscapeOutput -- SVG statique, non issu de l'utilisateur ?>

                    <!-- Icône centrée -->
                    <div class="absolute inset-0 flex items-center justify-center">
                        <div class="w-14 h-14 rounded-2xl bg-white shadow-md border border-gray-200 flex items-center justify-center group-hover:bg-klem-red group-hover:border-klem-red transition-all duration-300">
                            <svg
                                class="w-7 h-7 text-klem-blue group-hover:text-white transition-colors duration-300"
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

                    <!-- Trait rouge au bas du bandeau -->
                    <div class="absolute bottom-0 left-0 right-0 h-0.5 bg-klem-red scale-x-0 group-hover:scale-x-100 transition-transform duration-300 origin-left"></div>
                </div>

                <!-- Contenu texte -->
                <div class="p-6 flex flex-col flex-1">
                    <span class="text-xs font-extrabold tracking-widest text-klem-orange/40 group-hover:text-klem-red transition-colors duration-300 mb-2">
                        <?php echo esc_html($service['num']); ?>
                    </span>

                    <h3 class="text-base font-bold text-klem-blue mb-3 leading-snug">
                        <?php echo esc_html($service['title']); ?>
                    </h3>

                    <p class="text-gray-500 text-sm leading-relaxed flex-grow">
                        <?php echo esc_html($service['desc']); ?>
                    </p>

                    <div class="mt-6 pt-5 border-t border-gray-100 group-hover:border-klem-red/10 transition-colors duration-300">
                        <span class="inline-flex items-center gap-1.5 text-sm font-semibold text-gray-400 group-hover:text-klem-red transition-colors duration-300">
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
