package com.klem.referentielapi;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Frontières de packages de {@code referentiel-api-service} — voir {@code README.md} §« Modules du
 * MVP ». Graphe de dépendances réel entre les quatre domaines (déterminé par les jointures du
 * modèle métier, {@code specifications_techniques.md} §2.1) :
 * <pre>
 * textereglementaire ← procedure ← operationcommerce
 *                       documentrequis ← operationcommerce
 * </pre>
 * {@code textereglementaire} et {@code documentrequis} sont les deux racines (aucune dépendance
 * sortante) ; {@code operationcommerce} est la feuille (rien n'en dépend). Chaque dépendance
 * inter-domaine autorisée passe uniquement par la classe de service applicative de l'autre domaine
 * (ex. {@code TexteReglementaireService.exists(UUID)}) — jamais son {@code domain.model} ni son
 * {@code domain.exception}, même motif de « lecture peer-à-peer étroite » que sur {@code core-api}.
 */
@AnalyzeClasses(packages = "com.klem.referentielapi", importOptions = ImportOption.DoNotIncludeTests.class)
class PackageBoundaryRulesTest {

    // ── 1. Aucun cycle entre domaines ─────────────────────────────────────────────────────────
    @ArchTest
    static final ArchRule domains_are_free_of_cycles =
            slices().matching("com.klem.referentielapi.(*)..").should().beFreeOfCycles();

    // ── 2. textereglementaire est une racine : aucune dépendance vers les autres domaines ─────
    @ArchTest
    static final ArchRule textereglementaire_does_not_depend_on_other_domains =
            noClasses().that().resideInAPackage("..referentielapi.textereglementaire..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..referentielapi.procedure..", "..referentielapi.documentrequis..",
                            "..referentielapi.operationcommerce..")
                    .allowEmptyShould(true);

    // ── 3. documentrequis est une racine : aucune dépendance vers les autres domaines ─────────
    @ArchTest
    static final ArchRule documentrequis_does_not_depend_on_other_domains =
            noClasses().that().resideInAPackage("..referentielapi.documentrequis..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..referentielapi.textereglementaire..", "..referentielapi.procedure..",
                            "..referentielapi.operationcommerce..")
                    .allowEmptyShould(true);

    // ── 4. procedure ne dépend jamais de documentrequis/operationcommerce ─────────────────────
    @ArchTest
    static final ArchRule procedure_does_not_depend_on_documentrequis_or_operationcommerce =
            noClasses().that().resideInAPackage("..referentielapi.procedure..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..referentielapi.documentrequis..", "..referentielapi.operationcommerce..")
                    .allowEmptyShould(true);

    // ── 5. procedure → textereglementaire : uniquement via application.service, jamais
    //        domain.model/domain.exception/infrastructure/api (lecture peer-à-peer étroite,
    //        motif TexteReglementaireService#exists) ─────────────────────────────────────────
    @ArchTest
    static final ArchRule procedure_depends_on_textereglementaire_only_through_its_service =
            noClasses().that().resideInAPackage("..referentielapi.procedure..")
                    .should().dependOnClassesThat(
                            resideInAnyPackage("..referentielapi.textereglementaire..")
                                    .and(not(resideInAnyPackage("..textereglementaire.application.service.."))))
                    .allowEmptyShould(true);

    // ── 6. rien ne dépend d'operationcommerce : c'est la feuille du graphe ────────────────────
    @ArchTest
    static final ArchRule nothing_depends_on_operationcommerce =
            noClasses().that().resideOutsideOfPackage("..referentielapi.operationcommerce..")
                    .should().dependOnClassesThat().resideInAPackage("..referentielapi.operationcommerce..")
                    .allowEmptyShould(true);

    // ── 7. operationcommerce → procedure : uniquement via application.service ────────────────
    @ArchTest
    static final ArchRule operationcommerce_depends_on_procedure_only_through_its_service =
            noClasses().that().resideInAPackage("..referentielapi.operationcommerce..")
                    .should().dependOnClassesThat(
                            resideInAnyPackage("..referentielapi.procedure..")
                                    .and(not(resideInAnyPackage("..procedure.application.service.."))))
                    .allowEmptyShould(true);

    // ── 8. operationcommerce → documentrequis : uniquement via application.service ───────────
    @ArchTest
    static final ArchRule operationcommerce_depends_on_documentrequis_only_through_its_service =
            noClasses().that().resideInAPackage("..referentielapi.operationcommerce..")
                    .should().dependOnClassesThat(
                            resideInAnyPackage("..referentielapi.documentrequis..")
                                    .and(not(resideInAnyPackage("..documentrequis.application.service.."))))
                    .allowEmptyShould(true);

    // ── 9. shared ne dépend d'aucun domaine (utilitaire transverse, en amont de tout) ─────────
    @ArchTest
    static final ArchRule shared_does_not_depend_on_any_domain =
            noClasses().that().resideInAPackage("..referentielapi.shared..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..referentielapi.textereglementaire..", "..referentielapi.procedure..",
                            "..referentielapi.documentrequis..", "..referentielapi.operationcommerce..")
                    .allowEmptyShould(true);
}
