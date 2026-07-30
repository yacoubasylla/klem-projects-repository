<section id="services" class="pt-16 pb-10 lg:pt-24 lg:pb-14 bg-white">
    <div class="max-w-[1600px] mx-auto px-4 sm:px-6">

        <!-- En-tête de section -->
        <div class="max-w-2xl mb-16 lg:mb-20" data-animate data-delay="0">
            <span class="inline-block text-klem-red font-bold tracking-widest text-xs uppercase mb-4 px-4 py-1.5 bg-klem-red/8 rounded-full">
                <?php esc_html_e('Nos Services', 'klem-theme'); ?>
            </span>
            <h2 class="text-2xl lg:text-3xl font-heading text-klem-blue leading-tight mb-4">
                <?php esc_html_e("Quatre Piliers d'Excellence", 'klem-theme'); ?>
            </h2>
            <p class="text-gray-500 text-lg leading-relaxed">
                <?php esc_html_e(
                    "Des solutions technologiques complètes, pensées pour les organisations qui misent sur l'innovation.",
                    'klem-theme'
                ); ?>
            </p>
        </div>

        <?php
        /* ── Icônes SVG illustrées — palette 2 couleurs : #13294B + #D6301F ── */

        /* 01 — Ingénierie des Données : cylindre base de données */
        $icon_data = '<svg viewBox="0 0 72 72" width="72" height="72" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
  <!-- Corps du cylindre -->
  <rect x="8" y="22" width="56" height="32" fill="#EEF3F9"/>
  <line x1="8" y1="22" x2="8" y2="54" stroke="#13294B" stroke-width="2"/>
  <line x1="64" y1="22" x2="64" y2="54" stroke="#13294B" stroke-width="2"/>
  <!-- Ellipse inférieure -->
  <ellipse cx="36" cy="54" rx="28" ry="9" fill="#E2EAF4" stroke="#13294B" stroke-width="2"/>
  <!-- Strie milieu -->
  <path d="M8 37 Q8 46 36 46 Q64 46 64 37" stroke="#13294B" stroke-width="1" stroke-dasharray="3 2" fill="none" opacity="0.35"/>
  <!-- Ellipse supérieure — accent rouge -->
  <ellipse cx="36" cy="22" rx="28" ry="9" fill="#D6301F"/>
  <!-- Pointillés blancs sur ellipse rouge -->
  <circle cx="26" cy="22" r="2.5" fill="white" opacity="0.65"/>
  <circle cx="36" cy="22" r="2.5" fill="white" opacity="0.65"/>
  <circle cx="46" cy="22" r="2.5" fill="white" opacity="0.65"/>
  <!-- Petits disques empilés (sources de données) -->
  <ellipse cx="36" cy="13" rx="20" ry="6" fill="#F1F5FB" stroke="#13294B" stroke-width="1.5" opacity="0.7"/>
  <ellipse cx="36" cy="7" rx="14" ry="4.5" fill="#F7F9FC" stroke="#13294B" stroke-width="1.2" opacity="0.45"/>
</svg>';

        /* 02 — Applications Sur-Mesure : écran + code */
        $icon_apps = '<svg viewBox="0 0 72 72" width="72" height="72" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
  <!-- Écran moniteur -->
  <rect x="4" y="10" width="64" height="44" rx="5" fill="#EEF3F9" stroke="#13294B" stroke-width="2"/>
  <!-- Zone écran -->
  <rect x="10" y="16" width="52" height="32" rx="3" fill="#F7F9FC"/>
  <!-- Barre du haut (accent rouge) -->
  <rect x="10" y="16" width="52" height="9" rx="3" fill="#D6301F"/>
  <rect x="10" y="20" width="52" height="5" rx="0" fill="#D6301F"/>
  <!-- Boutons fenêtre -->
  <circle cx="17" cy="20" r="2.5" fill="white" opacity="0.5"/>
  <circle cx="24" cy="20" r="2.5" fill="white" opacity="0.5"/>
  <circle cx="31" cy="20" r="2.5" fill="white" opacity="0.5"/>
  <!-- Symbole < / > dessiné -->
  <path d="M17 33 L12 37 L17 41" stroke="#13294B" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
  <path d="M22 41 L28 31" stroke="#13294B" stroke-width="2" stroke-linecap="round"/>
  <path d="M31 33 L36 37 L31 41" stroke="#13294B" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
  <!-- Lignes de code (droite) -->
  <rect x="42" y="31" width="16" height="3" rx="1.5" fill="#13294B" opacity="0.25"/>
  <rect x="42" y="37" width="11" height="3" rx="1.5" fill="#13294B" opacity="0.18"/>
  <rect x="42" y="43" width="14" height="3" rx="1.5" fill="#D6301F" opacity="0.4"/>
  <!-- Pied moniteur -->
  <line x1="36" y1="54" x2="36" y2="63" stroke="#13294B" stroke-width="2"/>
  <line x1="24" y1="63" x2="48" y2="63" stroke="#13294B" stroke-width="2.5" stroke-linecap="round"/>
</svg>';

        /* 03 — ERP & FleetControl : camion + GPS */
        $icon_erp = '<svg viewBox="0 0 72 72" width="72" height="72" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
  <!-- Route (pointillée) -->
  <line x1="2" y1="62" x2="70" y2="62" stroke="#13294B" stroke-width="1.5" stroke-dasharray="5 4" opacity="0.25"/>
  <!-- Carrosserie camion -->
  <rect x="2" y="34" width="44" height="26" rx="3" fill="#EEF3F9" stroke="#13294B" stroke-width="2"/>
  <!-- Cabine -->
  <rect x="46" y="40" width="22" height="20" rx="6" fill="#E2EAF4" stroke="#13294B" stroke-width="2"/>
  <!-- Pare-brise -->
  <rect x="50" y="44" width="14" height="10" rx="3" fill="#D6E4F5" stroke="#13294B" stroke-width="1"/>
  <!-- Pare-chocs avant -->
  <rect x="66" y="52" width="4" height="6" rx="1.5" fill="#13294B" opacity="0.35"/>
  <!-- Roues -->
  <circle cx="16" cy="62" r="8" fill="#13294B" opacity="0.8"/>
  <circle cx="16" cy="62" r="4" fill="#EEF3F9"/>
  <circle cx="38" cy="62" r="8" fill="#13294B" opacity="0.8"/>
  <circle cx="38" cy="62" r="4" fill="#EEF3F9"/>
  <circle cx="57" cy="62" r="8" fill="#13294B" opacity="0.8"/>
  <circle cx="57" cy="62" r="4" fill="#EEF3F9"/>
  <!-- PIN GPS — accent rouge -->
  <path d="M65 4 C65 0 62 -2 59 -2 C56 -2 53 0 53 4 C53 10 59 18 59 18 C59 18 65 10 65 4 Z" fill="#D6301F" transform="translate(0 4)"/>
  <circle cx="59" cy="8" r="3.5" fill="white" opacity="0.85"/>
  <!-- Trait route vers pin -->
  <path d="M46 40 C50 30 54 22 58 16" stroke="#13294B" stroke-width="1.2" stroke-dasharray="3 3" fill="none" opacity="0.4"/>
</svg>';

        /* 04 — Matériel IT : rack serveur */
        $icon_hardware = '<svg viewBox="0 0 72 72" width="72" height="72" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
  <!-- Oreilles du rack (côtés) -->
  <rect x="3" y="6" width="7" height="60" rx="2.5" fill="#E2EAF4" stroke="#13294B" stroke-width="1.5"/>
  <rect x="62" y="6" width="7" height="60" rx="2.5" fill="#E2EAF4" stroke="#13294B" stroke-width="1.5"/>
  <!-- Châssis rack -->
  <rect x="10" y="6" width="52" height="60" rx="4" fill="#F1F5FB" stroke="#13294B" stroke-width="2"/>
  <!-- Unité serveur 1 -->
  <rect x="14" y="12" width="44" height="14" rx="3" fill="#EAF1F9" stroke="#13294B" stroke-width="1.2"/>
  <circle cx="21" cy="19" r="3.5" fill="#D6E4F5" stroke="#13294B" stroke-width="1"/>
  <circle cx="30" cy="19" r="3.5" fill="#D6E4F5" stroke="#13294B" stroke-width="1"/>
  <rect x="40" y="15" width="14" height="8" rx="2" fill="#D6E4F5" stroke="#13294B" stroke-width="0.8"/>
  <!-- Unité serveur 2 -->
  <rect x="14" y="30" width="44" height="14" rx="3" fill="#EAF1F9" stroke="#13294B" stroke-width="1.2"/>
  <circle cx="21" cy="37" r="3.5" fill="#D6E4F5" stroke="#13294B" stroke-width="1"/>
  <circle cx="30" cy="37" r="3.5" fill="#D6E4F5" stroke="#13294B" stroke-width="1"/>
  <rect x="40" y="33" width="14" height="8" rx="2" fill="#D6E4F5" stroke="#13294B" stroke-width="0.8"/>
  <!-- Unité serveur 3 — LED rouge (alerte) -->
  <rect x="14" y="48" width="44" height="14" rx="3" fill="#FDF2F2" stroke="#13294B" stroke-width="1.2"/>
  <circle cx="21" cy="55" r="3.5" fill="#D6301F"/>
  <circle cx="30" cy="55" r="3.5" fill="#D6E4F5" stroke="#13294B" stroke-width="1"/>
  <rect x="40" y="51" width="14" height="8" rx="2" fill="#D6E4F5" stroke="#13294B" stroke-width="0.8"/>
</svg>';

        $klem_services = [
            [
                'num'      => '01',
                'title'    => 'Ingénierie des Données',
                'desc'     => 'Pipelines Big Data et architectures temps réel (Kafka, Spark) pour transformer vos données en avantage stratégique.',
                'cta'      => 'Planifier un atelier data',
                'delay'    => '100',
                'icon_svg' => $icon_data,
            ],
            [
                'num'      => '02',
                'title'    => 'Applications Sur-Mesure',
                'desc'     => "Applications web, mobiles et ERP 100 % sur-mesure — comme Cantine Connect, notre solution de paiement pour la restauration scolaire.",
                'cta'      => 'Discuter de mon application',
                'delay'    => '200',
                'icon_svg' => $icon_apps,
            ],
            [
                'num'      => '03',
                'title'    => 'Intégration ERP & FleetControl',
                'desc'     => "Intégration de vos systèmes d'information et déploiement de FleetControl, notre solution de gestion de flotte pour l'Afrique.",
                'cta'      => 'Demander une démo FleetControl',
                'delay'    => '300',
                'icon_svg' => $icon_erp,
            ],
            [
                'num'      => '04',
                'title'    => 'Matériel IT & Infrastructure',
                'desc'     => "Serveurs, réseaux, postes de travail : nous déployons des infrastructures IT robustes et évolutives.",
                'cta'      => 'Obtenir une estimation infra',
                'delay'    => '400',
                'icon_svg' => $icon_hardware,
            ],
        ];
        ?>

        <!-- Grille 4 colonnes — style illustration + texte -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-y-12 gap-x-10">
            <?php foreach ($klem_services as $service) : ?>
            <div
                class="flex flex-col border-t-2 border-gray-100 pt-8 hover:border-klem-red transition-colors duration-300 group"
                data-animate
                data-delay="<?php echo esc_attr($service['delay']); ?>"
            >
                <!-- Icône illustrée -->
                <div class="mb-6">
                    <?php echo $service['icon_svg']; // phpcs:ignore WordPress.Security.EscapeOutput -- SVG statique ?>
                </div>

                <!-- Numéro -->
                <span class="text-klem-red text-[11px] font-extrabold tracking-widest uppercase mb-3">
                    <?php echo esc_html($service['num']); ?>
                </span>

                <!-- Titre -->
                <h3 class="text-xl font-bold text-klem-blue mb-4 leading-snug">
                    <?php echo esc_html($service['title']); ?>
                </h3>

                <!-- Description -->
                <p class="text-gray-500 text-sm leading-relaxed flex-grow">
                    <?php echo esc_html($service['desc']); ?>
                </p>

                <!-- CTA spécifique au pilier -->
                <div class="mt-8">
                    <a
                        href="#contact"
                        data-sector="<?php echo esc_attr($service['title']); ?>"
                        class="inline-flex items-center gap-1.5 text-sm font-semibold text-gray-400 group-hover:text-klem-red transition-colors duration-200"
                    >
                        <?php echo esc_html($service['cta']); ?>
                        <svg class="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform duration-200" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                        </svg>
                    </a>
                </div>
            </div>
            <?php endforeach; ?>
        </div>

        <!-- CTA bas de section -->
        <div class="mt-12 text-center border-t border-gray-100 pt-8" data-animate data-delay="500">
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
