<?php

declare(strict_types=1);

/**
 * ── Comptes partenaires : page d'administration sur-mesure ──────────────
 * Écran wp-admin dédié pour créer/modifier/supprimer les comptes qui
 * accèdent aux « Cas d'usage » (cf. ADR-009). Utilise wp_insert_user() /
 * wp_update_user() / wp_delete_user() natifs (hashage de mot de passe géré
 * par le cœur WordPress) — aucune cryptographie maison. Les comptes créés
 * ici portent le rôle dédié `klem_partenaire`, distinct de tout autre
 * abonné éventuel, pour que cette liste ne montre jamais que des comptes
 * partenaires.
 */

function klem_partner_sectors(): array {
    return [
        'logistique' => __('Logistique & Transport', 'klem-theme'),
        'education'  => __('Éducation & Collectivités', 'klem-theme'),
        'commerce'   => __('Commerce & Services', 'klem-theme'),
        'douane'     => __('Douane & Commerce International', 'klem-theme'),
        'sante'      => __('Santé & Interopérabilité', 'klem-theme'),
        'autre'      => __('Autre', 'klem-theme'),
    ];
}

function klem_partner_error_messages(): array {
    return [
        'login'          => __('Identifiant invalide (lettres, chiffres, espaces, ., -, _, @ uniquement).', 'klem-theme'),
        'login_exists'   => __('Cet identifiant est déjà utilisé.', 'klem-theme'),
        'email'          => __('Adresse e-mail invalide.', 'klem-theme'),
        'email_exists'   => __('Cette adresse e-mail est déjà utilisée par un autre compte.', 'klem-theme'),
        'password_short' => __('Le mot de passe doit contenir au moins 8 caractères.', 'klem-theme'),
        'unknown'        => __('Une erreur est survenue. Merci de réessayer.', 'klem-theme'),
    ];
}

function klem_register_partner_role(): void {
    if (!get_role('klem_partenaire')) {
        add_role('klem_partenaire', __('Partenaire KLEM', 'klem-theme'), ['read' => true]);
    }
}
add_action('init', 'klem_register_partner_role');

function klem_partner_admin_menu(): void {
    add_menu_page(
        __('Comptes partenaires', 'klem-theme'),
        __('Partenaires', 'klem-theme'),
        'manage_options',
        'klem-partenaires',
        'klem_partner_admin_page',
        'dashicons-groups'
    );
}
add_action('admin_menu', 'klem_partner_admin_menu');

function klem_partner_admin_enqueue(string $hook): void {
    if ($hook !== 'toplevel_page_klem-partenaires') {
        return;
    }

    wp_enqueue_script(
        'klem-admin-partners',
        get_template_directory_uri() . '/assets/js/admin-partners.js',
        [],
        null,
        true
    );
}
add_action('admin_enqueue_scripts', 'klem_partner_admin_enqueue');

function klem_partner_admin_page(): void {
    if (!current_user_can('manage_options')) {
        wp_die(esc_html__("Vous n'avez pas les droits suffisants pour accéder à cette page.", 'klem-theme'));
    }

    $view = isset($_GET['view']) ? sanitize_key(wp_unslash($_GET['view'])) : 'list';

    echo '<div class="wrap">';
    echo '<h1 class="wp-heading-inline">' . esc_html__('Comptes partenaires', 'klem-theme') . '</h1> ';

    if ($view === 'new' || $view === 'edit') {
        printf(
            '<a href="%s" class="page-title-action">%s</a>',
            esc_url(admin_url('admin.php?page=klem-partenaires')),
            esc_html__('← Retour à la liste', 'klem-theme')
        );
    } else {
        printf(
            '<a href="%s" class="page-title-action">%s</a>',
            esc_url(admin_url('admin.php?page=klem-partenaires&view=new')),
            esc_html__('Ajouter un compte', 'klem-theme')
        );
    }

    echo '<hr class="wp-header-end">';

    klem_partner_admin_notices();

    if ($view === 'new' || $view === 'edit') {
        klem_partner_admin_form($view);
    } else {
        klem_partner_admin_list();
    }

    echo '</div>';
}

function klem_partner_admin_notices(): void {
    if (isset($_GET['succes'])) {
        $succes = sanitize_key(wp_unslash($_GET['succes']));
        $labels = [
            'cree'     => __('Compte partenaire créé avec succès.', 'klem-theme'),
            'modifie'  => __('Compte partenaire modifié avec succès.', 'klem-theme'),
            'supprime' => __('Compte partenaire supprimé.', 'klem-theme'),
        ];
        if (isset($labels[$succes])) {
            printf('<div class="notice notice-success is-dismissible"><p>%s</p></div>', esc_html($labels[$succes]));
        }
    }

    if (isset($_GET['erreur'])) {
        $codes    = explode(',', sanitize_text_field(wp_unslash($_GET['erreur'])));
        $messages = klem_partner_error_messages();
        foreach ($codes as $code) {
            if (isset($messages[$code])) {
                printf('<div class="notice notice-error"><p>%s</p></div>', esc_html($messages[$code]));
            }
        }
    }
}

function klem_partner_admin_list(): void {
    $users   = get_users(['role' => 'klem_partenaire', 'orderby' => 'registered', 'order' => 'DESC']);
    $sectors = klem_partner_sectors();
    ?>
    <table class="wp-list-table widefat fixed striped">
        <thead>
            <tr>
                <th><?php esc_html_e('Identifiant', 'klem-theme'); ?></th>
                <th><?php esc_html_e('E-mail', 'klem-theme'); ?></th>
                <th><?php esc_html_e("Secteur d'activité", 'klem-theme'); ?></th>
                <th><?php esc_html_e('Créé le', 'klem-theme'); ?></th>
                <th><?php esc_html_e('Actions', 'klem-theme'); ?></th>
            </tr>
        </thead>
        <tbody>
            <?php if (empty($users)) : ?>
            <tr>
                <td colspan="5"><?php esc_html_e('Aucun compte partenaire pour le moment.', 'klem-theme'); ?></td>
            </tr>
            <?php else : foreach ($users as $user) :
                $sector_key   = (string) get_user_meta($user->ID, 'klem_secteur', true);
                $sector_label = $sectors[$sector_key] ?? '—';

                $edit_url = add_query_arg(
                    ['page' => 'klem-partenaires', 'view' => 'edit', 'user_id' => $user->ID],
                    admin_url('admin.php')
                );
                $delete_url = wp_nonce_url(
                    add_query_arg(
                        ['action' => 'klem_partner_delete', 'user_id' => $user->ID],
                        admin_url('admin-post.php')
                    ),
                    'klem_partner_delete_' . $user->ID
                );
            ?>
            <tr>
                <td><?php echo esc_html($user->user_login); ?></td>
                <td><?php echo esc_html($user->user_email); ?></td>
                <td><?php echo esc_html($sector_label); ?></td>
                <td><?php echo esc_html(mysql2date(get_option('date_format'), $user->user_registered)); ?></td>
                <td>
                    <a href="<?php echo esc_url($edit_url); ?>"><?php esc_html_e('Modifier', 'klem-theme'); ?></a>
                    &nbsp;|&nbsp;
                    <a
                        href="<?php echo esc_url($delete_url); ?>"
                        class="submitdelete"
                        onclick="return confirm('<?php echo esc_js(__('Supprimer ce compte partenaire ? Cette action est irréversible.', 'klem-theme')); ?>');"
                    ><?php esc_html_e('Supprimer', 'klem-theme'); ?></a>
                </td>
            </tr>
            <?php endforeach; endif; ?>
        </tbody>
    </table>
    <?php
}

function klem_partner_admin_form(string $view): void {
    $user_id = isset($_GET['user_id']) ? (int) $_GET['user_id'] : 0;
    $user    = $view === 'edit' && $user_id > 0 ? get_userdata($user_id) : null;

    if ($view === 'edit' && (!$user || !in_array('klem_partenaire', $user->roles, true))) {
        echo '<p>' . esc_html__('Compte introuvable.', 'klem-theme') . '</p>';
        return;
    }

    $sectors        = klem_partner_sectors();
    $current_sector = $user ? (string) get_user_meta($user->ID, 'klem_secteur', true) : '';
    ?>
    <h2>
        <?php echo $view === 'edit'
            ? esc_html__('Modifier le compte', 'klem-theme')
            : esc_html__('Nouveau compte partenaire', 'klem-theme'); ?>
    </h2>

    <form method="post" action="<?php echo esc_url(admin_url('admin-post.php')); ?>">
        <input type="hidden" name="action" value="klem_partner_save">
        <input type="hidden" name="user_id" value="<?php echo esc_attr((string) ($user ? $user->ID : 0)); ?>">
        <?php wp_nonce_field('klem_partner_save'); ?>

        <table class="form-table" role="presentation">
            <tr>
                <th scope="row"><label for="klem_login"><?php esc_html_e('Identifiant', 'klem-theme'); ?></label></th>
                <td>
                    <?php if ($user) : ?>
                        <input type="text" value="<?php echo esc_attr($user->user_login); ?>" class="regular-text" disabled>
                        <p class="description"><?php esc_html_e("L'identifiant ne peut pas être modifié après création (limite WordPress).", 'klem-theme'); ?></p>
                    <?php else : ?>
                        <input type="text" id="klem_login" name="klem_login" class="regular-text" required autocomplete="off">
                    <?php endif; ?>
                </td>
            </tr>
            <tr>
                <th scope="row"><label for="klem_email"><?php esc_html_e('E-mail', 'klem-theme'); ?></label></th>
                <td>
                    <input
                        type="email"
                        id="klem_email"
                        name="klem_email"
                        class="regular-text"
                        required
                        value="<?php echo esc_attr($user ? $user->user_email : ''); ?>"
                    >
                </td>
            </tr>
            <tr>
                <th scope="row"><label for="klem-password-field"><?php esc_html_e('Mot de passe', 'klem-theme'); ?></label></th>
                <td>
                    <input
                        type="password"
                        id="klem-password-field"
                        name="klem_password"
                        class="regular-text"
                        autocomplete="new-password"
                        <?php echo $view === 'new' ? 'required' : ''; ?>
                    >
                    <button type="button" id="klem-generate-password" class="button">
                        <?php esc_html_e('Générer un mot de passe', 'klem-theme'); ?>
                    </button>
                    <p class="description">
                        <?php echo $view === 'edit'
                            ? esc_html__('Laisser vide pour ne pas modifier le mot de passe actuel.', 'klem-theme')
                            : esc_html__('Minimum 8 caractères. À communiquer au partenaire par e-mail après création.', 'klem-theme'); ?>
                    </p>
                </td>
            </tr>
            <tr>
                <th scope="row"><label for="klem_secteur"><?php esc_html_e("Secteur d'activité", 'klem-theme'); ?></label></th>
                <td>
                    <select id="klem_secteur" name="klem_secteur">
                        <?php foreach ($sectors as $key => $label) : ?>
                        <option value="<?php echo esc_attr($key); ?>" <?php selected($current_sector, $key); ?>><?php echo esc_html($label); ?></option>
                        <?php endforeach; ?>
                    </select>
                </td>
            </tr>
        </table>

        <?php submit_button($view === 'edit' ? __('Enregistrer les modifications', 'klem-theme') : __('Créer le compte', 'klem-theme')); ?>
    </form>
    <?php
}

function klem_partner_save(): void {
    if (!current_user_can('manage_options')) {
        wp_die(esc_html__('Action non autorisée.', 'klem-theme'));
    }
    check_admin_referer('klem_partner_save');

    $editing_id = isset($_POST['user_id']) ? (int) $_POST['user_id'] : 0;
    $is_edit    = $editing_id > 0;

    $email    = sanitize_email(wp_unslash($_POST['klem_email'] ?? ''));
    $sector   = sanitize_key(wp_unslash($_POST['klem_secteur'] ?? ''));
    $password = (string) wp_unslash($_POST['klem_password'] ?? '');

    if (!array_key_exists($sector, klem_partner_sectors())) {
        $sector = 'autre';
    }

    $errors    = [];
    $target_id = $editing_id;

    if (!is_email($email)) {
        $errors[] = 'email';
    }

    if ($is_edit) {
        $user = get_userdata($editing_id);
        if (!$user || !in_array('klem_partenaire', $user->roles, true)) {
            wp_die(esc_html__('Compte introuvable.', 'klem-theme'));
        }

        $existing = email_exists($email);
        if ($existing && (int) $existing !== $editing_id) {
            $errors[] = 'email_exists';
        }
        if ($password !== '' && strlen($password) < 8) {
            $errors[] = 'password_short';
        }

        if (empty($errors)) {
            $update = ['ID' => $editing_id, 'user_email' => $email];
            if ($password !== '') {
                $update['user_pass'] = $password;
            }
            wp_update_user($update);
            update_user_meta($editing_id, 'klem_secteur', $sector);
        }
    } else {
        $login = sanitize_user(wp_unslash($_POST['klem_login'] ?? ''), true);

        if ($login === '' || !validate_username($login)) {
            $errors[] = 'login';
        } elseif (username_exists($login)) {
            $errors[] = 'login_exists';
        }
        if (email_exists($email)) {
            $errors[] = 'email_exists';
        }
        if ($password === '' || strlen($password) < 8) {
            $errors[] = 'password_short';
        }

        if (empty($errors)) {
            $new_id = wp_insert_user([
                'user_login' => $login,
                'user_email' => $email,
                'user_pass'  => $password,
                'role'       => 'klem_partenaire',
            ]);
            if (is_wp_error($new_id)) {
                $errors[] = 'unknown';
            } else {
                update_user_meta($new_id, 'klem_secteur', $sector);
                $target_id = $new_id;
            }
        }
    }

    $base = admin_url('admin.php?page=klem-partenaires');

    if (!empty($errors)) {
        $redirect = add_query_arg(
            [
                'view'    => $is_edit ? 'edit' : 'new',
                'user_id' => $target_id ?: '',
                'erreur'  => implode(',', $errors),
            ],
            $base
        );
    } else {
        $redirect = add_query_arg('succes', $is_edit ? 'modifie' : 'cree', $base);
    }

    wp_safe_redirect($redirect);
    exit;
}
add_action('admin_post_klem_partner_save', 'klem_partner_save');

function klem_partner_delete(): void {
    if (!current_user_can('manage_options')) {
        wp_die(esc_html__('Action non autorisée.', 'klem-theme'));
    }

    $user_id = isset($_REQUEST['user_id']) ? (int) $_REQUEST['user_id'] : 0;
    check_admin_referer('klem_partner_delete_' . $user_id);

    $user = get_userdata($user_id);
    if ($user && in_array('klem_partenaire', $user->roles, true)) {
        require_once ABSPATH . 'wp-admin/includes/user.php';
        wp_delete_user($user_id);
    }

    wp_safe_redirect(add_query_arg('succes', 'supprime', admin_url('admin.php?page=klem-partenaires')));
    exit;
}
add_action('admin_post_klem_partner_delete', 'klem_partner_delete');
