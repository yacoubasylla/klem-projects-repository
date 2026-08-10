package com.klem.coreapi;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Frontières de packages de core-api — README.md §6.
 * <p>
 * Ces règles sont écrites avant tout code métier (les packages de domaine ne contiennent que des
 * `.gitkeep` au moment de leur rédaction). ArchUnit échoue par défaut une règle qui ne trouve
 * aucune classe à vérifier ({@code failOnEmptyShould}) — les règles ci-dessous qui ciblent un
 * domaine encore vide utilisent donc explicitement {@code allowEmptyShould(true)} pour rester
 * vertes aujourd'hui et commencer à s'appliquer dès la première classe réelle. Ne jamais
 * l'appliquer à une règle pour faire passer une violation constatée sur du code existant — corriger
 * le code, ou documenter une dérogation par ADR.
 */
@AnalyzeClasses(packages = "com.klem.coreapi", importOptions = ImportOption.DoNotIncludeTests.class)
class PackageBoundaryRulesTest {

    // ── 1. Aucun cycle entre domaines (tenant, identity, authorization, referential, audit,
    //        workflow, shared) ────────────────────────────────────────────────────────────────
    @ArchTest
    static final ArchRule domains_are_free_of_cycles =
            slices().matching("com.klem.coreapi.(*)..").should().beFreeOfCycles();

    // ── 2. audit ne dépend d'aucun autre domaine, À UNE EXCEPTION PRÈS : leurs classes
    //        `domain.event` (ex. TenantCreatedEvent). Découvert en implémentant audit :
    //        AuditService écoute ces événements via @TransactionalEventListener, ce qui exige de
    //        référencer leur type — impossible sans cette exception. Un événement de domaine EST
    //        le point d'intégration public intentionnel d'un domaine (contrairement à
    //        domain.model/domain.exception, qui restent strictement internes, voir règle 7) :
    //        assouplir la règle ici est un choix délibéré, pas un relâchement de discipline.
    @ArchTest
    static final ArchRule audit_depends_only_on_domain_events_of_other_domains =
            noClasses().that().resideInAPackage("..coreapi.audit..")
                    .should().dependOnClassesThat(
                            resideInAnyPackage(
                                    "..coreapi.tenant..", "..coreapi.identity..",
                                    "..coreapi.authorization..", "..coreapi.referential..",
                                    "..coreapi.workflow..")
                                    .and(not(resideInAnyPackage("..domain.event.."))))
                    .allowEmptyShould(true);

    // ── 3. tenant est le domaine le plus en amont : aucune dépendance vers les autres ─────────
    @ArchTest
    static final ArchRule tenant_does_not_depend_on_downstream_domains =
            noClasses().that().resideInAPackage("..coreapi.tenant..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..coreapi.identity..", "..coreapi.authorization..",
                            "..coreapi.referential..", "..coreapi.audit..",
                            "..coreapi.workflow..")
                    .allowEmptyShould(true);

    // ── 4. identity ne dépend jamais de authorization (sens unique) ──────────────────────────
    @ArchTest
    static final ArchRule identity_does_not_depend_on_authorization =
            noClasses().that().resideInAPackage("..coreapi.identity..")
                    .should().dependOnClassesThat().resideInAPackage("..coreapi.authorization..")
                    .allowEmptyShould(true);

    // ── 5. referential reste autonome (aucun domaine interne n'en dépend au départ) ──────────
    @ArchTest
    static final ArchRule referential_does_not_depend_on_other_domains =
            noClasses().that().resideInAPackage("..coreapi.referential..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..coreapi.tenant..", "..coreapi.identity..",
                            "..coreapi.authorization..", "..coreapi.audit..",
                            "..coreapi.workflow..")
                    .allowEmptyShould(true);

    // ── 6. workflow est la couche d'orchestration la plus haute : rien ne doit en dépendre ───
    @ArchTest
    static final ArchRule nothing_depends_on_workflow =
            noClasses().that().resideOutsideOfPackage("..coreapi.workflow..")
                    .should().dependOnClassesThat().resideInAPackage("..coreapi.workflow..");

    // ── 7. domain (hors domain.event) et infrastructure d'un package ne sont accédés que
    //        depuis leur propre domaine — OU depuis workflow, l'orchestrateur privilégié qui
    //        « dépend librement des autres » par conception (README.md §4 ; rien ne dépend de
    //        workflow, règle 6). Découvert en implémentant workflow : orchestrer « créer un
    //        tenant → inviter son premier utilisateur → lui attribuer le rôle Admin » exige de
    //        lire l'id généré de chaque étape (Tenant, TenantMembership, RoleAssignment), pas
    //        seulement un booléen d'existence — contrairement au motif tenantExists/
    //        isMemberOfTenant utilisé par authorization (peer-à-peer, resté restreint) ────────
    @ArchTest
    static final ArchRule domain_internals_are_only_accessed_from_their_own_domain =
            classes().that().resideInAnyPackage(
                            "..coreapi.tenant.domain..", "..coreapi.tenant.infrastructure..")
                    .and().resideOutsideOfPackage("..coreapi.tenant.domain.event..")
                    .should().onlyBeAccessed().byAnyPackage("..coreapi.tenant..", "..coreapi.workflow..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule identity_internals_are_only_accessed_from_identity =
            classes().that().resideInAnyPackage(
                            "..coreapi.identity.domain..", "..coreapi.identity.infrastructure..")
                    .and().resideOutsideOfPackage("..coreapi.identity.domain.event..")
                    .should().onlyBeAccessed().byAnyPackage("..coreapi.identity..", "..coreapi.workflow..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule authorization_internals_are_only_accessed_from_authorization =
            classes().that().resideInAnyPackage(
                            "..coreapi.authorization.domain..", "..coreapi.authorization.infrastructure..")
                    .and().resideOutsideOfPackage("..coreapi.authorization.domain.event..")
                    .should().onlyBeAccessed().byAnyPackage("..coreapi.authorization..", "..coreapi.workflow..")
                    .allowEmptyShould(true);

    // ── 7bis. domain.event, lui, est accessible depuis son propre domaine ET depuis audit
    //          (unique consommateur cross-domaine légitime aujourd'hui) — pas depuis un autre
    //          domaine métier (referential, workflow, ou l'un envers l'autre) ─────────────────
    @ArchTest
    static final ArchRule tenant_events_are_only_accessed_from_tenant_or_audit =
            classes().that().resideInAPackage("..coreapi.tenant.domain.event..")
                    .should().onlyBeAccessed().byAnyPackage("..coreapi.tenant..", "..coreapi.audit..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule identity_events_are_only_accessed_from_identity_or_audit =
            classes().that().resideInAPackage("..coreapi.identity.domain.event..")
                    .should().onlyBeAccessed().byAnyPackage("..coreapi.identity..", "..coreapi.audit..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule authorization_events_are_only_accessed_from_authorization_or_audit =
            classes().that().resideInAPackage("..coreapi.authorization.domain.event..")
                    .should().onlyBeAccessed().byAnyPackage("..coreapi.authorization..", "..coreapi.audit..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule audit_internals_are_only_accessed_from_audit =
            classes().that().resideInAnyPackage(
                            "..coreapi.audit.domain..", "..coreapi.audit.infrastructure..")
                    .should().onlyBeAccessed().byAnyPackage("..coreapi.audit..")
                    .allowEmptyShould(true);

    // ── 8. Aucune entité JPA (infrastructure.persistence) exposée directement par un controller
    //        — un @RestController ne doit même pas dépendre d'un type d'infrastructure ────────
    @ArchTest
    static final ArchRule controllers_do_not_depend_on_persistence_internals =
            noClasses().that().areAnnotatedWith(RestController.class)
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure.persistence..")
                    .allowEmptyShould(true);

    // ── 9. core-api ne dépend d'aucun autre service services/* — non vérifiable par ArchUnit
    //        intra-module (aucune classe d'un autre service n'est sur le classpath de ce module) ;
    //        contrainte imposée par construction via l'absence de toute dépendance Maven vers
    //        transit-ops-service/referentiel-api-service dans pom.xml. Ne jamais l'ajouter.
}
