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
    <div class="max-w-6xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between gap-6">

        <!-- Logo -->
        <a href="<?php echo esc_url(home_url('/')); ?>" class="flex-shrink-0 flex items-center gap-2 group" aria-label="<?php esc_attr_e('KLEM Technologies & Services — Accueil', 'klem-theme'); ?>">
            <!-- ChevronMark — double chevron 3D -->
            <svg viewBox="0 0 54 44" width="32" height="26" xmlns="http://www.w3.org/2000/svg" aria-label="KLEM — double chevron" class="block overflow-visible flex-shrink-0 -mt-1">
                <g fill="#A5130A">
                    <path d="M6 0 L18 0 L32 20 L13 40 L1 40 L17 20 Z" transform="translate(1.4,1.7)"/>
                    <path d="M23 0 L35 0 L49 20 L30 40 L18 40 L34 20 Z" transform="translate(1.4,1.7)"/>
                </g>
                <g fill="#E42313">
                    <path d="M6 0 L18 0 L32 20 L13 40 L1 40 L17 20 Z"/>
                    <path d="M23 0 L35 0 L49 20 L30 40 L18 40 L34 20 Z"/>
                </g>
                <g fill="#F0654F">
                    <path d="M6 0 L18 0 L19.5 2.2 L7.5 2.2 Z"/>
                    <path d="M23 0 L35 0 L36.5 2.2 L24.5 2.2 Z"/>
                </g>
            </svg>
            <!-- Wordmark -->
            <div class="flex flex-col leading-none gap-[8px]">
                <span class="font-logo font-extrabold text-[18px] sm:text-[22px] lg:text-[26px] tracking-[-0.02em] leading-none text-klem-blue">KLEM</span>
                <span class="font-logo font-semibold text-[8px] tracking-[0.23em] text-klem-blue uppercase leading-none"><?php esc_html_e('Technologies & Services', 'klem-theme'); ?></span>
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
                        ['label' => 'Notre Différence', 'href' => '#clients',  'active' => false],
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
                <p class="text-klem-blue font-medium text-xs leading-none">
                    <?php esc_html_e('+225 07 58 89 24 77', 'klem-theme'); ?>
                </p>
            </div>

            <!-- Séparateur vertical -->
            <div class="h-8 w-px bg-gray-200 self-center"></div>

            <!-- Bouton CTA principal -->
            <a
                href="#contact"
                class="inline-flex items-center justify-center bg-klem-orange text-white font-bold px-5 py-2 rounded-lg text-sm hover:brightness-110 hover:-translate-y-px transition-all duration-150 whitespace-nowrap"
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
                    <?php esc_html_e('+225 07 58 89 24 77', 'klem-theme'); ?>
                </p>
                <a href="#contact" class="block bg-klem-orange text-white font-bold text-center px-5 py-3 rounded-lg text-sm hover:brightness-110 transition-all">
                    <?php esc_html_e('Contactez-nous', 'klem-theme'); ?>
                </a>
            </div>
        </div>
    </div>
</header>
