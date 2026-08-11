-- Garantit au niveau base de données — pas seulement applicatif — qu'aucune fiche ne peut être
-- créée déjà "publiée" ou dans tout autre statut que PROPOSEE. Motivé par klem_ref_bot
-- (specifications_techniques.md §4.2) : l'agent d'ingestion écrit directement dans ce schéma via
-- un compte à droits restreints, en contournant complètement la couche applicative Java (donc les
-- factory methods TexteReglementaire.propose()/ProcedureMetier.propose()/etc., qui garantissent
-- déjà PROPOSEE côté Java, ne protègent pas cette voie d'insertion directe). Ce trigger s'applique
-- à TOUTE insertion, pas seulement à celle de klem_ref_bot : c'est un invariant du domaine
-- (une fiche naît toujours proposée), pas une règle spécifique à un seul compte technique.
CREATE FUNCTION enforce_proposee_on_insert() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.statut <> 'PROPOSEE' THEN
        RAISE EXCEPTION 'Toute nouvelle fiche doit être créée en statut PROPOSEE (reçu: %)', NEW.statut;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_texte_reglementaire_insert_proposee
    BEFORE INSERT ON texte_reglementaire
    FOR EACH ROW EXECUTE FUNCTION enforce_proposee_on_insert();

CREATE TRIGGER trg_procedure_metier_insert_proposee
    BEFORE INSERT ON procedure_metier
    FOR EACH ROW EXECUTE FUNCTION enforce_proposee_on_insert();

CREATE TRIGGER trg_document_requis_insert_proposee
    BEFORE INSERT ON document_requis
    FOR EACH ROW EXECUTE FUNCTION enforce_proposee_on_insert();

CREATE TRIGGER trg_operation_commerce_insert_proposee
    BEFORE INSERT ON operation_commerce
    FOR EACH ROW EXECUTE FUNCTION enforce_proposee_on_insert();

-- Compte technique attendu pour klem_ref_bot (à provisionner par les opérations — jamais de mot
-- de passe en dur dans une migration versionnée) :
--   CREATE ROLE klem_ref_bot_ingestion LOGIN PASSWORD '<via secret manager>';
--   GRANT USAGE ON SCHEMA klem_trade_x TO klem_ref_bot_ingestion;
--   GRANT INSERT ON texte_reglementaire, procedure_metier, document_requis, operation_commerce
--       TO klem_ref_bot_ingestion;
-- Volontairement PAS de SELECT/UPDATE/DELETE : l'agent ne relit jamais ses propres propositions
-- ni celles des autres (pas de logique de déduplication côté DB à ce stade), et ne peut jamais
-- corriger ou publier une fiche — seul un éditeur humain le peut, via l'API applicative.
