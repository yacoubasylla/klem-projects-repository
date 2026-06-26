<!DOCTYPE html>
<html <?php language_attributes(); ?> class="scroll-smooth">
<head>
    <meta charset="<?php bloginfo('charset'); ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <?php wp_head(); ?>
</head>
<body <?php body_class('bg-white text-klem-blue antialiased'); ?>>
<?php wp_body_open(); ?>

<header id="site-header" class="fixed top-0 left-0 right-0 z-50 bg-white border-b border-gray-200 transition-shadow duration-300">
    <div class="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between gap-6">

        <!-- Logo -->
        <a href="<?php echo esc_url(home_url('/')); ?>" class="flex-shrink-0 flex items-center gap-4 group" aria-label="<?php esc_attr_e('KLEM Technologies & Services — Accueil', 'klem-theme'); ?>">
            <!-- KlemMark — Neural Brain 3D -->
            <svg viewBox="0 0 48 48" width="48" height="48" xmlns="http://www.w3.org/2000/svg" aria-hidden="true" class="flex-shrink-0">
                <defs>
                    <radialGradient id="kmh-sp" cx="36%" cy="28%" r="70%">
                        <stop offset="0%"   stop-color="#FF7060"/>
                        <stop offset="38%"  stop-color="#E42313"/>
                        <stop offset="100%" stop-color="#6E0A04"/>
                    </radialGradient>
                    <radialGradient id="kmh-hl" cx="28%" cy="22%" r="44%">
                        <stop offset="0%"   stop-color="rgba(255,255,255,0.55)"/>
                        <stop offset="100%" stop-color="rgba(255,255,255,0)"/>
                    </radialGradient>
                    <filter id="kmh-sh" x="-25%" y="-25%" width="150%" height="150%">
                        <feDropShadow dx="0" dy="2.5" stdDeviation="3" flood-color="#1A0404" flood-opacity="0.40"/>
                    </filter>
                </defs>
                <circle cx="24" cy="24" r="22" fill="url(#kmh-sp)" filter="url(#kmh-sh)"/>
                <g stroke="white" stroke-linecap="round" fill="none">
                    <line x1="24" y1="9"  x2="12" y2="19" stroke-width="1.3" opacity="0.72"/>
                    <line x1="24" y1="9"  x2="36" y2="19" stroke-width="1.3" opacity="0.72"/>
                    <line x1="12" y1="19" x2="14" y2="31" stroke-width="1.3" opacity="0.72"/>
                    <line x1="36" y1="19" x2="34" y2="31" stroke-width="1.3" opacity="0.72"/>
                    <line x1="14" y1="31" x2="24" y2="40" stroke-width="1.3" opacity="0.72"/>
                    <line x1="34" y1="31" x2="24" y2="40" stroke-width="1.3" opacity="0.72"/>
                    <line x1="12" y1="19" x2="36" y2="19" stroke-width="1.0" opacity="0.48"/>
                    <line x1="14" y1="31" x2="34" y2="31" stroke-width="1.0" opacity="0.48"/>
                    <line x1="12" y1="19" x2="34" y2="31" stroke-width="0.9" opacity="0.36"/>
                    <line x1="36" y1="19" x2="14" y2="31" stroke-width="0.9" opacity="0.36"/>
                </g>
                <g fill="white">
                    <circle cx="24" cy="9"  r="2.5"/>
                    <circle cx="12" cy="19" r="2.5"/>
                    <circle cx="36" cy="19" r="2.5"/>
                    <circle cx="14" cy="31" r="2.5"/>
                    <circle cx="34" cy="31" r="2.5"/>
                    <circle cx="24" cy="40" r="2.5"/>
                </g>
                <ellipse cx="17" cy="14" rx="8.5" ry="5.5" fill="url(#kmh-hl)" transform="rotate(-30 17 14)"/>
            </svg>
            <!-- Wordmark -->
            <div class="flex flex-col leading-none gap-[5px]">
                <span class="font-logo font-bold text-[26px] tracking-[0.5px] leading-none text-klem-blue">KLEM</span>
                <span class="font-logo font-bold text-[9px] tracking-[1.2px] text-klem-slate uppercase leading-none"><?php esc_html_e('Technologies & Services', 'klem-theme'); ?></span>
            </div>
        </a>

        <!-- Navigation principale (desktop) -->
        <nav class="hidden lg:flex flex-1 items-center justify-center" aria-label="<?php esc_attr_e('Navigation principale', 'klem-theme'); ?>">
            <?php
            wp_nav_menu([
                'theme_location' => 'primary',
                'container'      => false,
                'menu_class'     => 'flex items-center gap-9',
                'link_before'    => '<span class="text-gray-700 hover:text-klem-orange transition-colors duration-150 text-base font-medium">',
                'link_after'     => '</span>',
                'fallback_cb'    => static function (): void {
                    $items = [
                        ['label' => 'Accueil',     'href' => '#',         'active' => true],
                        ['label' => 'Services',    'href' => '#services', 'active' => false],
                        ['label' => 'À Propos',    'href' => '#about',    'active' => false],
                        ['label' => 'Cas Clients', 'href' => '#clients',  'active' => false],
                        ['label' => 'Contact',     'href' => '#contact',  'active' => false],
                    ];
                    echo '<ul class="flex items-center gap-9">';
                    foreach ($items as $item) {
                        $cls = $item['active']
                            ? 'text-klem-orange font-semibold text-base'
                            : 'text-gray-700 hover:text-klem-orange transition-colors duration-150 text-base font-medium';
                        printf(
                            '<li><a href="%s" class="%s">%s</a></li>',
                            esc_url($item['href']),
                            esc_attr($cls),
                            esc_html($item['label'])
                        );
                    }
                    echo '</ul>';
                },
            ]);
            ?>
        </nav>

        <!-- Partie droite desktop : support + téléphone + CTA -->
        <div class="hidden lg:flex items-center gap-5 flex-shrink-0 self-center">

            <!-- Support + numéro (2 lignes bien centrées) -->
            <div class="flex flex-col items-end gap-0.5">
                <a href="#contact" class="flex items-center gap-1 text-gray-500 text-xs hover:text-klem-orange transition-colors duration-150 font-medium leading-none">
                    <?php esc_html_e('Support Client', 'klem-theme'); ?>
                    <span aria-hidden="true" class="text-klem-orange font-bold">→</span>
                </a>
                <p class="text-klem-blue font-extrabold text-sm leading-none">
                    <?php esc_html_e('+229 XX XX XX XX', 'klem-theme'); ?>
                </p>
            </div>

            <!-- Séparateur vertical -->
            <div class="h-8 w-px bg-gray-200 self-center"></div>

            <!-- Bouton CTA principal -->
            <a
                href="#contact"
                class="inline-flex items-center gap-1.5 bg-klem-orange text-white font-bold px-6 py-3 rounded-lg text-base hover:brightness-110 hover:-translate-y-px transition-all duration-150 whitespace-nowrap"
            >
                <?php esc_html_e('Contactez-nous', 'klem-theme'); ?>
            </a>
        </div>

        <!-- Burger mobile -->
        <button
            id="menu-toggle"
            type="button"
            class="lg:hidden p-2 rounded-lg text-gray-600 hover:bg-gray-100 transition-colors"
            aria-expanded="false"
            aria-controls="mobile-menu"
            aria-label="<?php esc_attr_e('Ouvrir le menu', 'klem-theme'); ?>"
        >
            <svg id="burger-open" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16"/>
            </svg>
            <svg id="burger-close" class="w-5 h-5 hidden" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/>
            </svg>
        </button>
    </div>

    <!-- Menu mobile déroulant -->
    <div id="mobile-menu" class="hidden lg:hidden bg-white border-t border-gray-100">
        <div class="max-w-7xl mx-auto px-6 py-4">
            <?php
            wp_nav_menu([
                'theme_location' => 'primary',
                'container'      => false,
                'menu_class'     => 'space-y-1',
                'link_before'    => '<span class="block py-2.5 px-3 text-gray-700 hover:text-klem-orange hover:bg-klem-orange/5 rounded-lg text-sm font-medium transition-colors">',
                'link_after'     => '</span>',
                'fallback_cb'    => static function (): void {
                    $items = [
                        ['label' => 'Accueil',     'href' => '#'],
                        ['label' => 'Services',    'href' => '#services'],
                        ['label' => 'À Propos',    'href' => '#about'],
                        ['label' => 'Cas Clients', 'href' => '#clients'],
                        ['label' => 'Contact',     'href' => '#contact'],
                    ];
                    echo '<ul class="space-y-1">';
                    foreach ($items as $item) {
                        printf(
                            '<li><a href="%s" class="block py-2.5 px-3 text-gray-700 hover:text-klem-orange hover:bg-klem-orange/5 rounded-lg text-sm font-medium transition-colors">%s</a></li>',
                            esc_url($item['href']),
                            esc_html($item['label'])
                        );
                    }
                    echo '</ul>';
                },
            ]);
            ?>
            <div class="pt-4 mt-3 border-t border-gray-100 space-y-2">
                <p class="text-klem-blue font-extrabold text-sm px-3">
                    <?php esc_html_e('+229 XX XX XX XX', 'klem-theme'); ?>
                </p>
                <a href="#contact" class="block bg-klem-orange text-white font-bold text-center px-5 py-3 rounded-lg text-sm hover:brightness-110 transition-all">
                    <?php esc_html_e('Contactez-nous', 'klem-theme'); ?>
                </a>
            </div>
        </div>
    </div>
</header>
