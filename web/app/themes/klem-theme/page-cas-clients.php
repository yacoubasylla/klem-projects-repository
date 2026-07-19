<?php
/**
 * Template Name: Cas d'usage
 *
 * Page « Cas d'usage » : KLEM étant une jeune structure en phase de
 * prospection, cette page présente des cas d'usage illustratifs (types de
 * projets menés), explicitement présentés comme tels — sans fausse
 * citation client ni résultat chiffré inventé (cf. DEC-012, DEC-032, DEC-038).
 */

$klem_use_cases = [
    [
        'sector'  => __('Logistique & Transport', 'klem-theme'),
        'title'   => __('Optimisation de flotte avec FleetControl', 'klem-theme'),
        'context' => __("De nombreux opérateurs de transport gèrent encore leur flotte sur des tableurs, sans visibilité en temps réel sur la localisation, la consommation ou l'état d'entretien des véhicules.", 'klem-theme'),
        'problem' => __("Coûts de carburant mal maîtrisés, pannes découvertes trop tard, reporting manuel chronophage pour les équipes d'exploitation.", 'klem-theme'),
        'solution' => __("Déploiement de FleetControl : géolocalisation en temps réel, alertes de maintenance prédictive et tableau de bord de consommation centralisé.", 'klem-theme'),
        'benefit' => __("Des décisions plus rapides, un entretien anticipé plutôt que subi, et une visibilité complète sur les coûts d'exploitation.", 'klem-theme'),
        'icon'    => 'M8.25 18.75a1.5 1.5 0 0 1-3 0m3 0a1.5 1.5 0 0 0-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 0 1-1.125-1.125V14.25m17.25 4.5a1.5 1.5 0 0 1-3 0m3 0a1.5 1.5 0 0 0-3 0m3 0h1.125c.621 0 1.129-.504 1.09-1.124a17.902 17.902 0 0 0-3.213-9.193 2.056 2.056 0 0 0-1.58-.86H14.25M16.5 18.75h-2.25m0-11.177v-.958c0-.568-.422-1.048-.987-1.106a48.554 48.554 0 0 0-10.026 0 1.106 1.106 0 0 0-.987 1.106v7.635m12-6.677v6.677m0 0h-12',
    ],
    [
        'sector'  => __('Éducation & Collectivités', 'klem-theme'),
        'title'   => __('Digitalisation de la restauration scolaire avec Cantine Connect', 'klem-theme'),
        'context' => __("La gestion des paiements de cantine scolaire reste souvent manuelle — espèces, cahiers de suivi — avec des files d'attente et un risque d'erreur ou de perte.", 'klem-theme'),
        'problem' => __("Faible traçabilité budgétaire, temps d'attente pour les élèves, charge administrative importante pour le personnel.", 'klem-theme'),
        'solution' => __("Déploiement de Cantine Connect : paiement dématérialisé et contrôle d'accès à l'entrée du réfectoire.", 'klem-theme'),
        'benefit' => __("Des paiements tracés en continu, un accès simplifié pour les élèves, et une charge administrative réduite pour les équipes.", 'klem-theme'),
        'icon'    => 'M4.26 10.147a60.436 60.436 0 0 0-.491 6.347A48.627 48.627 0 0 1 12 20.904a48.627 48.627 0 0 1 8.232-4.41 60.46 60.46 0 0 0-.491-6.347m-15.482 0a50.57 50.57 0 0 0-2.658-.813A59.905 59.905 0 0 1 12 3.493a59.902 59.902 0 0 1 10.399 5.831c-.896.248-1.783.52-2.658.814m-15.482 0A50.697 50.697 0 0 1 12 13.489a50.702 50.702 0 0 1 7.74-3.342M6.75 15a.75.75 0 1 0 0-1.5.75.75 0 0 0 0 1.5Zm0 0v-3.675A55.378 55.378 0 0 1 12 8.443m-7.007 11.55A5.981 5.981 0 0 0 6.75 15.75v-1.5',
    ],
    [
        'sector'  => __('Commerce & Services', 'klem-theme'),
        'title'   => __('Mise en place d\'un socle de données cloud', 'klem-theme'),
        'context' => __("Les données d'une PME sont souvent dispersées entre plusieurs outils — tableurs, logiciel de caisse, CRM — rendant le reporting long à produire et peu fiable.", 'klem-theme'),
        'problem' => __("Décisions prises sur des données incomplètes ou obsolètes, temps perdu chaque mois à consolider manuellement les chiffres.", 'klem-theme'),
        'solution' => __("Mise en place d'un entrepôt de données cloud alimenté par des pipelines d'ingénierie des données automatisés.", 'klem-theme'),
        'benefit' => __("Un reporting fiable, rapide à produire, et une vision unifiée de l'activité pour l'équipe dirigeante.", 'klem-theme'),
        'icon'    => 'M20.25 6.375c0 2.278-3.694 4.125-8.25 4.125S3.75 8.653 3.75 6.375m16.5 0c0-2.278-3.694-4.125-8.25-4.125S3.75 4.097 3.75 6.375m16.5 0v11.25c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125V6.375m16.5 0v3.75m-16.5-3.75v3.75m16.5 0v3.75C20.25 16.153 16.556 18 12 18s-8.25-1.847-8.25-4.125v-3.75m16.5 0c0 2.278-3.694 4.125-8.25 4.125s-8.25-1.847-8.25-4.125',
    ],
    [
        'sector'  => __('Douane & Commerce International', 'klem-theme'),
        'title'   => __('Pré-audit automatisé des déclarations douanières avec Clear-Comply', 'klem-theme'),
        'context' => __("Les commissionnaires en douane traitent chaque jour de nombreuses déclarations, avec un contrôle qualité réalisé manuellement par des agents expérimentés dont la disponibilité est limitée.", 'klem-theme'),
        'problem' => __("Erreurs de code SH ou de valorisation détectées trop tard, immobilisation de conteneurs, pénalités de redressement et surcoûts de magasinage.", 'klem-theme'),
        'solution' => __("Déploiement de Clear-Comply : un outil de pré-contrôle interne qui détecte les anomalies avant la soumission de la déclaration, plutôt qu'après contrôle.", 'klem-theme'),
        'benefit' => __("Un filtre de pré-contrôle interne qui réduit le risque de redressement, sans remplacer les systèmes de déclaration existants.", 'klem-theme'),
        'icon'    => 'M9 12.75 11.25 15 15 9.75M21 12c0 1.268-.63 2.39-1.593 3.068a3.745 3.745 0 0 1-1.043 3.296 3.745 3.745 0 0 1-3.296 1.043A3.745 3.745 0 0 1 12 21c-1.268 0-2.39-.63-3.068-1.593a3.746 3.746 0 0 1-3.296-1.043 3.745 3.745 0 0 1-1.043-3.296A3.745 3.745 0 0 1 3 12c0-1.268.63-2.39 1.593-3.068a3.745 3.745 0 0 1 1.043-3.296 3.746 3.746 0 0 1 3.296-1.043A3.746 3.746 0 0 1 12 3c1.268 0 2.39.63 3.068 1.593a3.746 3.746 0 0 1 3.296 1.043 3.746 3.746 0 0 1 1.043 3.296A3.745 3.745 0 0 1 21 12Z',
    ],
    [
        'sector'  => __('Santé & Interopérabilité', 'klem-theme'),
        'title'   => __("Réseau d'interopérabilité santé avec Med-Share", 'klem-theme'),
        'context' => __("Les systèmes d'information des cliniques, hôpitaux et pharmacies restent cloisonnés, sans échange standardisé entre établissements.", 'klem-theme'),
        'problem' => __("Perte de continuité clinique lors des références inter-établissements, et absence de visibilité inter-pharmacies sur les stocks de médicaments essentiels.", 'klem-theme'),
        'solution' => __("Déploiement de Med-Share : une plateforme de partage sécurisé d'informations entre établissements de santé partenaires, pensée pour répondre aux exigences de souveraineté des données de santé.", 'klem-theme'),
        'benefit' => __("Un accès plus rapide à l'historique clinique d'un patient référé, et une meilleure anticipation des ruptures de stock pharmaceutique en réseau.", 'klem-theme'),
        'icon'    => 'M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12Z',
    ],
];

$klem_case_labels = [
    'context'  => ['title' => __('Contexte', 'klem-theme'), 'icon' => 'M11.25 4.5l7.5 7.5-7.5 7.5m-6-15l7.5 7.5-7.5 7.5'],
    'problem'  => ['title' => __('Défi', 'klem-theme'), 'icon' => 'M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z'],
    'solution' => ['title' => __('Solution KLEM', 'klem-theme'), 'icon' => 'M12 18v-5.25m0 0a6.01 6.01 0 0 0 1.5-.189m-1.5.189a6.01 6.01 0 0 1-1.5-.189m3.75 7.478a12.06 12.06 0 0 1-4.5 0m3.75 2.383a14.406 14.406 0 0 1-3 0M14.25 18v-.192c0-.983.658-1.823 1.508-2.316a7.5 7.5 0 1 0-7.517 0c.85.493 1.509 1.333 1.509 2.316V18'],
    'benefit'  => ['title' => __('Ce que cela change', 'klem-theme'), 'icon' => 'M2.25 18L9 11.25l4.306 4.306a11.95 11.95 0 0 1 5.814-5.518l2.74-1.22m0 0l-5.94-2.281m5.94 2.28l-2.28 5.941'],
];

get_header();
?>

<main id="main-content" class="pt-24">

    <!-- Hero -->
    <section class="pt-16 pb-10 lg:pt-20 lg:pb-14 bg-white">
        <div class="max-w-4xl mx-auto px-4 sm:px-6 text-center" data-animate>
            <span class="inline-block text-klem-red font-bold tracking-widest text-xs uppercase mb-5 px-4 py-1.5 bg-klem-red/8 rounded-full">
                <?php esc_html_e("Cas d'usage & Exemples de projets", 'klem-theme'); ?>
            </span>
            <h1 class="text-3xl lg:text-5xl font-extrabold text-klem-blue leading-tight mb-4">
                <?php esc_html_e('Comment nous aidons nos organisations à réussir leurs projets numériques', 'klem-theme'); ?>
            </h1>
            <p class="text-gray-500 text-lg leading-relaxed max-w-2xl mx-auto">
                <?php esc_html_e("Voici des exemples concrets du type de projets que nous menons, pour donner une idée précise de notre expertise plutôt qu'un historique.", 'klem-theme'); ?>
            </p>
        </div>
    </section>

    <!-- Cas d'usage -->
    <section class="pb-20 lg:pb-28 bg-white">
        <div class="max-w-5xl mx-auto px-4 sm:px-6 space-y-10">
            <?php foreach ($klem_use_cases as $index => $case) : ?>
            <article class="grid lg:grid-cols-12 gap-6 lg:gap-10 bg-gray-50 rounded-2xl p-6 lg:p-10" data-animate data-delay="<?php echo esc_attr((string) ($index * 100)); ?>">

                <div class="lg:col-span-4">
                    <div class="w-14 h-14 rounded-2xl bg-klem-red/10 flex items-center justify-center mb-5">
                        <svg class="w-7 h-7 text-klem-red" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="<?php echo esc_attr($case['icon']); ?>"/>
                        </svg>
                    </div>
                    <span class="inline-block text-klem-red text-[11px] font-extrabold uppercase tracking-widest mb-3">
                        <?php echo esc_html($case['sector']); ?>
                    </span>
                    <h2 class="text-xl lg:text-2xl font-bold text-klem-blue leading-snug mb-2">
                        <?php echo esc_html($case['title']); ?>
                    </h2>
                    <p class="text-gray-400 text-xs font-semibold uppercase tracking-wide">
                        <?php esc_html_e("Exemple d'application", 'klem-theme'); ?>
                    </p>
                </div>

                <div class="lg:col-span-8 grid sm:grid-cols-2 gap-6">
                    <?php foreach (['context', 'problem', 'solution', 'benefit'] as $key) : $label = $klem_case_labels[$key]; ?>
                    <div>
                        <p class="flex items-center gap-1.5 text-klem-blue font-bold text-sm mb-1.5">
                            <svg class="w-4 h-4 text-klem-red flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" aria-hidden="true">
                                <path stroke-linecap="round" stroke-linejoin="round" d="<?php echo esc_attr($label['icon']); ?>"/>
                            </svg>
                            <?php echo esc_html($label['title']); ?>
                        </p>
                        <p class="text-gray-500 text-sm leading-relaxed"><?php echo esc_html($case[$key]); ?></p>
                    </div>
                    <?php endforeach; ?>
                </div>

                <div class="lg:col-span-12 pt-4 mt-2 border-t border-gray-100">
                    <p class="text-gray-400 text-xs">
                        <?php esc_html_e('Pour en savoir plus sur ce projet,', 'klem-theme'); ?>
                        <a href="<?php echo esc_url(klem_home_anchor('#contact')); ?>" class="text-klem-red font-semibold hover:underline"><?php esc_html_e('contactez-nous', 'klem-theme'); ?></a>.
                    </p>
                </div>
            </article>
            <?php endforeach; ?>

            <!-- CTA de clôture -->
            <div class="text-center border-t border-gray-100 pt-12" data-animate data-delay="300">
                <p class="text-gray-500 mb-6 text-base">
                    <?php esc_html_e('Un projet en tête ? Discutons-en.', 'klem-theme'); ?>
                </p>
                <a
                    href="<?php echo esc_url(klem_home_anchor('#contact')); ?>"
                    class="inline-flex items-center gap-2 bg-klem-orange text-white font-bold px-8 py-4 rounded-xl hover:brightness-110 hover:-translate-y-0.5 hover:shadow-lg transition-all duration-200"
                >
                    <?php esc_html_e('Parler à un expert', 'klem-theme'); ?>
                    <svg class="w-4 h-4 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" aria-hidden="true">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3"/>
                    </svg>
                </a>
            </div>
        </div>
    </section>

</main>

<?php get_footer(); ?>
