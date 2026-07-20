<?php
/**
 * Template Name: Connexion
 *
 * Page de connexion sur-mesure (habillage Tailwind du site), réservée aux
 * partenaires disposant déjà d'un compte. Poste vers admin-post.php
 * (action=klem_login → klem_handle_login() dans functions.php), qui
 * s'appuie sur wp_signon() natif — donc sur les mêmes protections
 * anti-brute-force que wp-login.php.
 */

$klem_redirect_to = isset($_GET['redirect_to']) ? esc_url_raw(wp_unslash($_GET['redirect_to'])) : '';

if (is_user_logged_in()) {
    wp_safe_redirect($klem_redirect_to !== '' ? $klem_redirect_to : home_url('/'));
    exit;
}

$klem_login_has_error = isset($_GET['erreur']) && $_GET['erreur'] === '1';

get_header();
?>

<main id="main-content" class="pt-24 pb-20 lg:pb-28 bg-gray-50 min-h-screen">
    <div class="max-w-md mx-auto px-4 sm:px-6 pt-10 lg:pt-16">

        <div class="text-center mb-8" data-animate>
            <span class="inline-block text-klem-orange font-bold tracking-widest text-xs uppercase mb-4">
                <?php esc_html_e('Espace partenaires', 'klem-theme'); ?>
            </span>
            <h1 class="text-2xl lg:text-3xl font-heading text-klem-blue leading-tight mb-3">
                <?php esc_html_e('Connectez-vous', 'klem-theme'); ?>
            </h1>
            <p class="text-gray-500 text-base leading-relaxed">
                <?php esc_html_e("Accédez à nos cas d'usage détaillés réservés aux partenaires de KLEM.", 'klem-theme'); ?>
            </p>
        </div>

        <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-8" data-animate data-delay="100">

            <?php if ($klem_login_has_error) : ?>
            <div class="mb-6 p-4 rounded-xl text-sm font-medium bg-red-50 text-red-700 border border-red-200" role="alert">
                <?php esc_html_e('Identifiants incorrects. Merci de réessayer.', 'klem-theme'); ?>
            </div>
            <?php endif; ?>

            <form
                method="post"
                action="<?php echo esc_url(admin_url('admin-post.php')); ?>"
                class="space-y-5"
                aria-label="<?php esc_attr_e('Formulaire de connexion', 'klem-theme'); ?>"
            >
                <input type="hidden" name="action" value="klem_login">
                <input type="hidden" name="redirect_to" value="<?php echo esc_attr($klem_redirect_to); ?>">
                <?php wp_nonce_field('klem_login', 'klem_login_nonce'); ?>

                <div>
                    <label for="klem-login-user" class="block text-xs font-semibold text-gray-500 uppercase tracking-wide mb-1.5">
                        <?php esc_html_e('Identifiant ou e-mail', 'klem-theme'); ?>
                    </label>
                    <input
                        type="text"
                        id="klem-login-user"
                        name="klem_user"
                        required
                        autocomplete="username"
                        class="w-full px-4 py-3 rounded-xl border border-gray-200 text-sm text-gray-800 placeholder-gray-300 focus:outline-none focus:ring-2 focus:ring-klem-orange/40 focus:border-klem-orange transition-colors"
                    >
                </div>

                <div>
                    <label for="klem-login-password" class="block text-xs font-semibold text-gray-500 uppercase tracking-wide mb-1.5">
                        <?php esc_html_e('Mot de passe', 'klem-theme'); ?>
                    </label>
                    <input
                        type="password"
                        id="klem-login-password"
                        name="klem_password"
                        required
                        autocomplete="current-password"
                        class="w-full px-4 py-3 rounded-xl border border-gray-200 text-sm text-gray-800 placeholder-gray-300 focus:outline-none focus:ring-2 focus:ring-klem-orange/40 focus:border-klem-orange transition-colors"
                    >
                </div>

                <button
                    type="submit"
                    class="w-full flex items-center justify-center gap-2 bg-klem-orange text-white font-bold px-8 py-4 rounded-xl hover:brightness-110 hover:-translate-y-0.5 transition-all duration-200 text-base"
                >
                    <?php esc_html_e('Connectez-vous', 'klem-theme'); ?>
                </button>

                <p class="text-center">
                    <a href="<?php echo esc_url(wp_lostpassword_url(klem_current_url())); ?>" class="text-sm text-gray-400 hover:text-klem-orange transition-colors">
                        <?php esc_html_e('Mot de passe oublié ?', 'klem-theme'); ?>
                    </a>
                </p>
            </form>
        </div>

        <p class="text-center text-gray-500 text-sm mt-8" data-animate data-delay="200">
            <?php esc_html_e("Pas encore partenaire ?", 'klem-theme'); ?>
            <a href="<?php echo esc_url(klem_partnership_contact_url()); ?>" class="text-klem-orange font-semibold hover:underline">
                <?php esc_html_e('Faites une demande de partenariat', 'klem-theme'); ?>
            </a>
        </p>
    </div>
</main>

<?php get_footer(); ?>
