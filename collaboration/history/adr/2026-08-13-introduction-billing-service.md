# ADR : Introduction Billing Service

- **Date :** 2026-08-13
- **Statut :** Accepté
- **Auteurs :** Yacouba Sylla

## Contexte

`klem-labs-repository/shared_architecture/billing_&_payments/` documentait un module partagé
d'encaissement Mobile Money (Wave, Orange Money, MTN Mobile Money, Moov Money, via API directe ou
agrégateur CinetPay/Bizao/Fedapay/PayDunya) classé "brique runtime" au même titre qu'IAM (Keycloak),
mais sans code applicatif ni contrat technique concret : pas de machine à états de transaction, pas
de contrat d'API, pas de scope de conformité PCI-DSS, pas de jalons de mise en œuvre. Un audit du
2026-08-13 a par ailleurs révélé un écart réel entre cette classification "brique runtime" (un
service interrogé en exécution) et le seul projet consommateur déjà en production,
`03_cantine_connect`, qui porte sa propre intégration paiement embarquée
(`PaiementService`/`PaymentProvider`, table `transactions_paiement`).

## Décision

1. Confirmer `billing-service` comme un vrai service déployé séparément (pas un pattern
   réimplémenté par projet), documenté en détail (machine à états, contrat API, scope PCI-DSS,
   jalons MVP/V1/V2) dans
   `klem-labs-repository/shared_architecture/billing_&_payments/specifications_techniques.md`
   §4-8.
2. Créer `services/billing-service` (Spring Boot 3.3.5 / Java 21, layering domain/application/
   infrastructure/api conforme à `MASTER_SYSTEM_DIRECTIVE.md` §7 et au pattern déjà en place dans
   `services/core-api`), avec :
   - Un agrégat `Transaction` et une machine à états stricte (PENDING → CONFIRMED/FAILED,
     CONFIRMED → REFUND_INITIATED → REFUNDED, jamais CONFIRMED → FAILED direct).
   - Un contrat unique `PaymentProvider` (Adapter/Strategy) implémenté par les deux familles
     d'intégration : agrégateurs (CinetPay, Bizao, Fedapay, PayDunya) et API opérateur directe
     (Wave, Orange Money, MTN Mobile Money, Moov Money), résolues via `PaymentProviderRegistry`.
   - Une API REST (`POST /transactions`, `GET /transactions/{id}`,
     `POST /transactions/{id}/refund`, `POST /webhooks/{operator}` authentifié par vérification de
     signature HMAC propre à chaque provider) et une migration Flyway
     (`V1__init_billing_schema.sql`) généralisant le schéma `transactions_paiement` déjà éprouvé de
     `cantine_connect` (lu pour référence, jamais modifié).
3. **Ne pas migrer `cantine_connect`** vers `billing-service`. Dérogation formellement documentée
   dans `projects/03_cantine_connect/specifications_techniques.md` §4 (côté
   `klem-labs-repository`) : son intégration est déjà en production (v1.0.0-beta livrée, livraison
   client imminente), migrer une intégration de paiement en production vers un service tiers pas
   encore éprouvé est un risque jugé disproportionné à ce stade. Réévaluation seulement après la
   livraison client, sans échéance fixée.

## Alternatives envisagées

- **Pattern réimplémenté par projet** (chaque projet garde son propre module paiement suivant un
  standard technique commun) — écarté : contredirait la classification "brique runtime" déjà actée
  pour ce module et le principe DRY de la règle de gouvernance n°1 (`GLOBAL_README.md`) pour tout
  nouveau projet consommateur.
- **Migrer `cantine_connect` immédiatement** — écarté : projet déjà en production avec une
  livraison client imminente ; migrer une intégration de paiement fonctionnelle vers un service pas
  encore éprouvé en production expose le client à un risque non justifié à ce stade.
- **Solution de facturation SaaS occidentale** (Stripe Billing, Chargebee) — déjà écartée en amont
  dans `billing_&_payments/business_case.md` §3 (couverture Mobile Money Afrique de l'Ouest absente
  ou très limitée).

## Conséquences

- **Avantages :** un seul point de vérité pour l'intégration Mobile Money multi-opérateur/
  agrégateur pour les futurs projets consommateurs (`07_boutiki`, `01_clear_comply`,
  `05_fleet_advance`, `10_dispo_link`) ; schéma de données et contrat API dérivés d'une
  implémentation déjà éprouvée en production plutôt que reconçus depuis zéro.
- **Risques/Dettes :**
  - Le code des Jalons 1 (MVP) et 2 (V1) a été écrit d'un coup, avant validation par un pilote réel
    en production — aucun projet consommateur n'est encore intégré à `billing-service` à la date de
    cet ADR. Écart de séquencement assumé par rapport aux jalons décrits en
    `specifications_techniques.md` §8, à ne pas reproduire pour le Jalon 3.
  - Les champs exacts de payload/signature par opérateur/agrégateur (CinetPay, Bizao, Fedapay,
    PayDunya, Wave, Orange, MTN, Moov) sont des hypothèses structurellement correctes mais non
    validées contre une documentation API réelle ou un compte sandbox — à confirmer avant mise en
    production.
  - Le remboursement (`TransactionService.refund`) passe synchrone `REFUND_INITIATED → REFUNDED`
    juste après l'appel provider plutôt que d'attendre une confirmation par webhook — simplification
    à revoir si un opérateur/agrégateur confirme le remboursement de façon asynchrone.
  - `cantine_connect` reste durablement hors périmètre de `billing-service` sans échéance de
    migration ferme — deux implémentations de paiement coexistent dans le portefeuille tant que
    cette dérogation reste en vigueur.
