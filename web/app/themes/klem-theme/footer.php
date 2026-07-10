<?php
// Aucun traitement PHP avant la sortie HTML du footer.
?>

<!-- Bande CTA pré-footer -->
<div class="bg-klem-blue relative overflow-hidden">
    <!-- Décor orange droit -->
    <div class="absolute right-0 top-0 h-full w-1/3 bg-gradient-to-l from-klem-orange/10 to-transparent pointer-events-none" aria-hidden="true"></div>
    <div class="absolute -top-20 right-32 w-72 h-72 rounded-full bg-klem-orange/15 blur-3xl pointer-events-none" aria-hidden="true"></div>

    <div class="relative z-10 max-w-7xl mx-auto px-6 py-16 flex flex-col lg:flex-row items-center justify-between gap-8 text-center lg:text-left">
        <div>
            <h2 class="text-3xl lg:text-4xl font-extrabold text-white mb-3 leading-tight">
                <?php esc_html_e('Prêt à transformer votre organisation ?', 'klem-theme'); ?>
            </h2>
            <p class="text-white/55 text-lg max-w-xl">
                <?php esc_html_e('Obtenez une feuille de route sur-mesure pour votre transformation digitale.', 'klem-theme'); ?>
            </p>
        </div>
        <a
            href="<?php echo esc_url(klem_home_anchor('#contact')); ?>"
            class="flex-shrink-0 inline-flex items-center gap-2 bg-klem-orange text-white font-bold px-8 py-4 rounded-xl hover:brightness-110 hover:-translate-y-0.5 transition-all duration-200 text-base whitespace-nowrap"
        >
            <?php esc_html_e('Lancer mon projet', 'klem-theme'); ?>
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
            </svg>
        </a>
    </div>
</div>

<!-- Footer principal -->
<footer class="bg-[#060e1b] text-white">

    <!-- Colonnes d'information -->
    <div class="max-w-7xl mx-auto px-6 pt-16 pb-12">
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-12 gap-10 lg:gap-8">

            <!-- Col 1 : Logo + description + réseaux sociaux (4 colonnes larges) -->
            <div class="lg:col-span-4">
                <a href="<?php echo esc_url(home_url('/')); ?>" class="inline-flex items-center gap-2 mb-5 group" aria-label="<?php esc_attr_e('KLEM Technologies & Services — Accueil', 'klem-theme'); ?>">
                    <!-- ChevronMark — double chevron 3D (fond sombre) -->
                    <svg viewBox="0 0 65 56" width="29" height="25" xmlns="http://www.w3.org/2000/svg" aria-label="KLEM — double chevron" style="display:block; flex-shrink:0;">
                        <polygon points="0,0 22,0 40,28 22,56 0,56 18,28" fill="#E42313"/>
                        <polygon points="25,0 47,0 65,28 47,56 25,56 43,28" fill="#E42313"/>
                    </svg>
                    <!-- Wordmark -->
                    <div class="flex flex-col leading-none gap-[2px]">
                        <span class="font-logo font-extrabold text-[28px] tracking-[-0.02em] leading-none text-white group-hover:text-klem-red transition-colors duration-200"><?php esc_html_e('KLEM', 'klem-theme'); ?></span>
                        <span class="font-logo font-semibold text-[6px] tracking-[0.2em] text-[#c3c9d6] uppercase leading-none"><?php esc_html_e('Technologies & Services', 'klem-theme'); ?></span>
                    </div>
                </a>
                <p class="text-white/50 text-sm leading-relaxed mb-7 max-w-xs">
                    <?php esc_html_e('Intégrateur numérique de référence en Afrique. Nous concevons et déployons les architectures digitales des organisations qui construisent le continent de demain.', 'klem-theme'); ?>
                </p>

                <!-- Réseaux sociaux -->
                <div class="flex items-center gap-2.5">
                    <a href="https://www.linkedin.com/company/130474992/" target="_blank" rel="noopener noreferrer" class="w-9 h-9 rounded-lg bg-white/8 hover:bg-klem-orange flex items-center justify-center transition-colors duration-200" aria-label="LinkedIn">
                        <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433a2.062 2.062 0 01-2.063-2.065 2.064 2.064 0 112.063 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
                        </svg>
                    </a>
                    <a href="https://x.com/KLEMTechnology" target="_blank" rel="noopener noreferrer" class="w-9 h-9 rounded-lg bg-white/8 hover:bg-klem-orange flex items-center justify-center transition-colors duration-200" aria-label="Twitter / X">
                        <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/>
                        </svg>
                    </a>
                    <a href="https://www.facebook.com/profile.php?id=61591353966112" target="_blank" rel="noopener noreferrer" class="w-9 h-9 rounded-lg bg-white/8 hover:bg-klem-orange flex items-center justify-center transition-colors duration-200" aria-label="Facebook">
                        <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                        </svg>
                    </a>
                    <a href="https://github.com/yacoubasylla/klem-opensource" target="_blank" rel="noopener noreferrer" class="w-9 h-9 rounded-lg bg-white/8 hover:bg-klem-orange flex items-center justify-center transition-colors duration-200" aria-label="GitHub">
                        <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"/>
                        </svg>
                    </a>
                </div>
            </div>

            <!-- Col 2 : Entreprise (2 colonnes) -->
            <div class="lg:col-span-2">
                <h3 class="text-white font-bold text-xs uppercase tracking-widest mb-5 pb-2 border-b border-white/10">
                    <?php esc_html_e('Entreprise', 'klem-theme'); ?>
                </h3>
                <ul class="space-y-3">
                    <?php
                    $company_links = [
                        ['label' => 'À propos',    'href' => klem_home_anchor('#about')],
                        ['label' => 'Notre équipe', 'href' => '#'],
                        ['label' => 'Cas clients',  'href' => '#'],
                        ['label' => 'Carrières',    'href' => '#'],
                        ['label' => 'Contact',      'href' => klem_home_anchor('#contact')],
                    ];
                    foreach ($company_links as $link) :
                    ?>
                    <li>
                        <a href="<?php echo esc_url($link['href']); ?>" class="text-white/50 hover:text-klem-orange transition-colors duration-150 text-sm">
                            <?php echo esc_html($link['label']); ?>
                        </a>
                    </li>
                    <?php endforeach; ?>
                </ul>
            </div>

            <!-- Col 3 : Services (3 colonnes) -->
            <div class="lg:col-span-3">
                <h3 class="text-white font-bold text-xs uppercase tracking-widest mb-5 pb-2 border-b border-white/10">
                    <?php esc_html_e('Services', 'klem-theme'); ?>
                </h3>
                <ul class="space-y-3">
                    <?php
                    $service_links = [
                        ['label' => 'Ingénierie des Données',         'href' => klem_home_anchor('#services')],
                        ['label' => 'Applications Sur-Mesure',         'href' => klem_home_anchor('#services')],
                        ['label' => 'Intégration ERP & FleetControl',  'href' => klem_home_anchor('#services')],
                        ['label' => 'Matériel IT & Infrastructure',    'href' => klem_home_anchor('#services')],
                    ];
                    foreach ($service_links as $link) :
                    ?>
                    <li>
                        <a href="<?php echo esc_url($link['href']); ?>" class="text-white/50 hover:text-klem-orange transition-colors duration-150 text-sm">
                            <?php echo esc_html($link['label']); ?>
                        </a>
                    </li>
                    <?php endforeach; ?>
                </ul>

                <p class="text-white/30 font-bold text-[10px] uppercase tracking-widest mt-6 mb-3">
                    <?php esc_html_e('Nos Solutions SaaS', 'klem-theme'); ?>
                </p>
                <ul class="space-y-3">
                    <?php
                    $saas_links = [
                        ['label' => 'FleetControl',           'href' => '#'],
                        ['label' => 'Cantine Connect (démo)', 'href' => 'https://cantine-connect-swart.vercel.app/login', 'external' => true],
                    ];
                    foreach ($saas_links as $link) :
                        $is_external = !empty($link['external']);
                    ?>
                    <li>
                        <a
                            href="<?php echo esc_url($link['href']); ?>"
                            class="text-white/50 hover:text-klem-orange transition-colors duration-150 text-sm"
                            <?php echo $is_external ? ' target="_blank" rel="noopener noreferrer"' : ''; ?>
                        >
                            <?php echo esc_html($link['label']); ?>
                        </a>
                    </li>
                    <?php endforeach; ?>
                </ul>
            </div>

            <!-- Col 4 : Contact (3 colonnes) -->
            <div class="lg:col-span-3">
                <h3 class="text-white font-bold text-xs uppercase tracking-widest mb-5 pb-2 border-b border-white/10">
                    <?php esc_html_e('Nous joindre', 'klem-theme'); ?>
                </h3>
                <ul class="space-y-4">

                    <!-- Adresse -->
                    <li class="flex items-start gap-3">
                        <svg class="w-4 h-4 text-klem-orange flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z"/>
                            <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z"/>
                        </svg>
                        <span class="text-white/50 text-sm leading-relaxed">
                            <?php esc_html_e('Treichville Arras 1', 'klem-theme'); ?><br>
                            <?php esc_html_e('Abidjan, Côte d\'Ivoire', 'klem-theme'); ?>
                        </span>
                    </li>

                    <!-- Téléphone -->
                    <li class="flex items-center gap-3">
                        <svg class="w-4 h-4 text-klem-orange flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 6.75c0 8.284 6.716 15 15 15h2.25a2.25 2.25 0 002.25-2.25v-1.372c0-.516-.351-.966-.852-1.091l-4.423-1.106c-.44-.11-.902.055-1.173.417l-.97 1.293c-.282.376-.769.542-1.21.38a12.035 12.035 0 01-7.143-7.143c-.162-.441.004-.928.38-1.21l1.293-.97c.363-.271.527-.734.417-1.173L6.963 3.102a1.125 1.125 0 00-1.091-.852H4.5A2.25 2.25 0 002.25 6.75z"/>
                        </svg>
                        <a href="tel:+2250758892477" class="text-white/50 hover:text-klem-orange transition-colors duration-150 text-sm">
                            <?php esc_html_e('+225 07 58 89 24 77', 'klem-theme'); ?>
                        </a>
                    </li>

                    <!-- Email -->
                    <li class="flex items-center gap-3">
                        <svg class="w-4 h-4 text-klem-orange flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75"/>
                        </svg>
                        <a href="mailto:infos@klemtech.net" class="text-white/50 hover:text-klem-orange transition-colors duration-150 text-sm">
                            <?php esc_html_e('infos@klemtech.net', 'klem-theme'); ?>
                        </a>
                    </li>

                    <!-- Site web -->
                    <li class="flex items-center gap-3">
                        <svg class="w-4 h-4 text-klem-orange flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9S9.515 3 12 3m0 0a8.997 8.997 0 017.843 4.582M12 3a8.997 8.997 0 00-7.843 4.582m15.686 0A11.953 11.953 0 0112 10.5c-2.998 0-5.74-1.1-7.843-2.918m15.686 0A8.959 8.959 0 0121 12c0 .778-.099 1.533-.284 2.253"/>
                        </svg>
                        <a href="https://www.klemtech.net" target="_blank" rel="noopener noreferrer" class="text-white/50 hover:text-klem-orange transition-colors duration-150 text-sm">
                            <?php esc_html_e('www.klemtech.net', 'klem-theme'); ?>
                        </a>
                    </li>

                    <!-- Horaires -->
                    <li class="flex items-start gap-3">
                        <svg class="w-4 h-4 text-klem-orange flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z"/>
                        </svg>
                        <span class="text-white/50 text-sm leading-relaxed">
                            <?php esc_html_e('Lun – Ven : 8h00 – 18h00', 'klem-theme'); ?><br>
                            <?php esc_html_e('Sam : 9h00 – 13h00', 'klem-theme'); ?>
                        </span>
                    </li>
                </ul>
            </div>

        </div>
    </div>

    <!-- Barre inférieure : copyright + liens légaux -->
    <div class="border-t border-white/8">
        <div class="max-w-7xl mx-auto px-6 py-5 flex flex-col sm:flex-row items-center justify-between gap-3">
            <p class="text-white/30 text-xs">
                <?php esc_html_e('Copyright © KLEM', 'klem-theme'); ?> <?php echo esc_html(date('Y')); ?> <?php esc_html_e('– Tous droits réservés.', 'klem-theme'); ?>
            </p>
            <nav class="flex items-center gap-5" aria-label="<?php esc_attr_e('Liens légaux', 'klem-theme'); ?>">
                <a href="#" class="text-white/30 hover:text-white/60 transition-colors text-xs"><?php esc_html_e('Termes et conditions', 'klem-theme'); ?></a>
                <a href="#" class="text-white/30 hover:text-white/60 transition-colors text-xs"><?php esc_html_e('Politique de confidentialité', 'klem-theme'); ?></a>
            </nav>
        </div>
    </div>
</footer>

<?php wp_footer(); ?>
</body>
</html>
