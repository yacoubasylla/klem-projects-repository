<!DOCTYPE html>
<html <?php language_attributes(); ?> class="scroll-smooth">
<head>
    <meta charset="<?php bloginfo('charset'); ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <?php wp_head(); ?>
</head>
<body <?php body_class('bg-white text-klem-blue antialiased'); ?>>
<?php wp_body_open(); ?>

<header id="site-header" class="fixed top-0 left-0 right-0 z-50 bg-white border-b border-gray-200 transition-shadow duration-300">
    <div class="max-w-[1600px] mx-auto px-4 sm:px-6 h-16 flex items-center justify-between gap-6">

        <!-- Logo -->
        <a href="<?php echo esc_url(home_url('/')); ?>" class="flex-shrink-0 flex items-center gap-1.5 group" aria-label="<?php esc_attr_e('KLEM Technologies & Services — Accueil', 'klem-theme'); ?>">
            <!-- ChevronMark — double chevron plat -->
            <svg viewBox="0 0 65 56" width="27" height="23" xmlns="http://www.w3.org/2000/svg" aria-label="KLEM — double chevron" class="block flex-shrink-0">
                <polygon points="0,0 22,0 40,28 22,56 0,56 18,28" fill="#E42313"/>
                <polygon points="25,0 47,0 65,28 47,56 25,56 43,28" fill="#E42313"/>
            </svg>
            <!-- Wordmark -->
            <div class="flex flex-col leading-none gap-[2px]">
                <span class="font-logo font-extrabold text-[18px] sm:text-[22px] lg:text-[26px] tracking-[-0.02em] leading-none text-klem-blue">KLEM</span>
                <span class="font-logo font-semibold text-[8px] tracking-[0.2em] text-klem-blue uppercase leading-none"><?php esc_html_e('Technologies & Services', 'klem-theme'); ?></span>
            </div>
        </a>

        <!-- Navigation principale (desktop) -->
        <nav class="hidden lg:flex flex-1 items-center justify-center" aria-label="<?php esc_attr_e('Navigation principale', 'klem-theme'); ?>">
            <?php
            wp_nav_menu([
                'theme_location' => 'primary',
                'container'      => false,
                'menu_class'     => 'flex items-center gap-7 whitespace-nowrap',
                'link_before'    => '<span class="text-gray-700 hover:text-klem-orange transition-colors duration-150 text-base font-medium whitespace-nowrap">',
                'link_after'     => '</span>',
                'fallback_cb'    => static function (): void {
                    $klem_on_actualites = is_page('actualites') || is_singular('post');
                    $items = [
                        ['label' => 'Accueil',     'href' => klem_home_anchor(),          'active' => is_front_page()],
                        ['label' => 'Services',    'href' => klem_home_anchor('#services'), 'active' => false],
                        ['label' => 'À propos',    'href' => klem_home_anchor('#about'),    'active' => false],
                        ['label' => 'Notre différence', 'href' => klem_home_anchor('#clients'), 'active' => false],
                    ];
                    if (is_user_logged_in()) {
                        $items[] = ['label' => "Cas d'usage", 'href' => klem_cas_clients_url(), 'active' => is_page('cas-clients')];
                    }
                    $items[] = ['label' => 'Actualités', 'href' => klem_actualites_url(), 'active' => $klem_on_actualites];
                    $items[] = ['label' => 'Contact',    'href' => klem_home_anchor('#contact'), 'active' => false];
                    echo '<ul class="flex items-center gap-7 whitespace-nowrap">';
                    foreach ($items as $item) {
                        $cls = $item['active']
                            ? 'text-klem-orange font-semibold text-base whitespace-nowrap'
                            : 'text-gray-700 hover:text-klem-orange transition-colors duration-150 text-base font-medium whitespace-nowrap';
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
                <a href="<?php echo esc_url(klem_home_anchor('#contact')); ?>" class="flex items-center gap-1 text-gray-500 text-xs hover:text-klem-orange transition-colors duration-150 font-medium leading-none">
                    <?php esc_html_e('Support Client', 'klem-theme'); ?>
                    <span aria-hidden="true" class="text-klem-orange font-bold">→</span>
                </a>
                <p class="text-klem-blue font-medium text-xs leading-none">
                    <?php esc_html_e('+225 07 58 89 24 77', 'klem-theme'); ?>
                </p>
            </div>

            <!-- Séparateur vertical -->
            <div class="h-8 w-px bg-gray-200 self-center"></div>

            <!-- Connexion / Compte -->
            <?php if (is_user_logged_in()) : ?>
                <div class="flex items-center gap-3">
                    <?php if (current_user_can('manage_options')) : ?>
                    <a
                        href="<?php echo esc_url(admin_url('admin.php?page=klem-partenaires')); ?>"
                        class="text-klem-orange text-sm font-semibold hover:opacity-80 transition-opacity whitespace-nowrap"
                    >
                        <?php esc_html_e('Administration', 'klem-theme'); ?>
                    </a>
                    <div class="h-4 w-px bg-gray-200"></div>
                    <?php endif; ?>
                    <span class="text-klem-blue text-sm font-medium whitespace-nowrap">
                        <?php echo esc_html(wp_get_current_user()->display_name); ?>
                    </span>
                    <a
                        href="<?php echo esc_url(wp_logout_url(home_url('/'))); ?>"
                        class="text-gray-500 text-sm font-medium hover:text-klem-orange transition-colors duration-150 whitespace-nowrap"
                    >
                        <?php esc_html_e('Déconnexion', 'klem-theme'); ?>
                    </a>
                </div>
            <?php else : ?>
                <a
                    href="<?php echo esc_url(klem_login_url(klem_current_url())); ?>"
                    class="inline-flex items-center justify-center bg-klem-orange text-white font-bold px-5 py-2 rounded-lg text-sm hover:brightness-110 hover:-translate-y-px transition-all duration-150 whitespace-nowrap"
                >
                    <?php esc_html_e('Connectez-vous', 'klem-theme'); ?>
                </a>
            <?php endif; ?>
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
        <div class="max-w-[1600px] mx-auto px-6 py-4">
            <?php
            wp_nav_menu([
                'theme_location' => 'primary',
                'container'      => false,
                'menu_class'     => 'space-y-1',
                'link_before'    => '<span class="block py-2.5 px-3 text-gray-700 hover:text-klem-orange hover:bg-klem-orange/5 rounded-lg text-sm font-medium transition-colors">',
                'link_after'     => '</span>',
                'fallback_cb'    => static function (): void {
                    $items = [
                        ['label' => 'Accueil',     'href' => klem_home_anchor()],
                        ['label' => 'Services',    'href' => klem_home_anchor('#services')],
                        ['label' => 'À propos',    'href' => klem_home_anchor('#about')],
                        ['label' => 'Notre différence', 'href' => klem_home_anchor('#clients')],
                    ];
                    if (is_user_logged_in()) {
                        $items[] = ['label' => "Cas d'usage", 'href' => klem_cas_clients_url()];
                    }
                    $items[] = ['label' => 'Actualités', 'href' => klem_actualites_url()];
                    $items[] = ['label' => 'Contact',    'href' => klem_home_anchor('#contact')];
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
                <?php if (is_user_logged_in()) : ?>
                    <?php if (current_user_can('manage_options')) : ?>
                    <p class="px-3">
                        <a href="<?php echo esc_url(admin_url('admin.php?page=klem-partenaires')); ?>" class="text-klem-orange font-semibold text-sm"><?php esc_html_e('Administration — Comptes partenaires', 'klem-theme'); ?></a>
                    </p>
                    <?php endif; ?>
                    <p class="px-3 text-sm text-gray-500">
                        <?php echo esc_html(wp_get_current_user()->display_name); ?>
                        · <a href="<?php echo esc_url(wp_logout_url(home_url('/'))); ?>" class="text-klem-orange font-semibold"><?php esc_html_e('Déconnexion', 'klem-theme'); ?></a>
                    </p>
                <?php else : ?>
                    <a href="<?php echo esc_url(klem_login_url(klem_current_url())); ?>" class="block bg-klem-orange text-white font-bold text-center px-5 py-3 rounded-lg text-sm hover:brightness-110 transition-all">
                        <?php esc_html_e('Connectez-vous', 'klem-theme'); ?>
                    </a>
                <?php endif; ?>
            </div>
        </div>
    </div>
</header>
