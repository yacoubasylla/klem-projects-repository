# ADR-019 : Contrat de paiement unifié multi-providers (Strategy Pattern) et intégration Orange Money

**Date :** 2026-08-18
**Statut :** Accepté
**Décideur :** Yacouba SYLLA (avec Claude Code)

---

## Contexte

Le module de paiement (`server-backend/.../paiement`) était monoprovider CinetPay en pratique
(PayDunya intégré aussi réellement, mais Orange/MTN/Moov Direct restaient des placeholders
levant `UnsupportedOperationException`), sélectionné par une config en base
(`PAIEMENT_PROVIDER_ACTIF`) via un contrat `PaymentProvider` minimal (une seule méthode
`initierPaiement`). L'obtention d'un accès marchand Orange Money (OAuth2 client_credentials)
a rendu nécessaire un contrat plus riche : initiation, vérification de statut, traitement de
webhook, validation de signature — ce que `PaymentProvider` ne portait pas.

## Options Envisagées

- **Étendre directement `PaymentProvider` avec les nouvelles méthodes** : rejetée — casse la
  signature de toutes les implémentations existantes (`CinetPayProvider`, `PayDunyaProvider`,
  3 placeholders Direct) d'un coup, contraire à la règle de non-régression du `CLAUDE.md` racine
  (§2.4) sur le code fonctionnel existant de `cantine-connect`.
- **Nouveau contrat `PaymentStrategy` additif**, implémenté par les nouvelles stratégies et,
  en plus de son contrat historique, par `CinetPayProvider` (méthodes ajoutées à la suite,
  aucune ligne existante modifiée) — retenue.
- **Réécrire `CinetPayProvider`/`PayDunyaProvider` en `WebClient`** pour homogénéiser le client
  HTTP : rejetée — ce sont des intégrations de paiement réelles en production ; les réécrire sans
  nécessité fonctionnelle aurait réintroduit un risque de régression pour un gain cosmétique.
  Elles restent en `java.net.http.HttpClient`, `WebClient` n'est utilisé que par les nouvelles
  stratégies (Orange Money).

## Décision Retenue

> **Nous implémenterons un contrat `PaymentStrategy` (package `paiement.strategy`) —
> `getProviderType`, `initiatePayment`, `handleWebhook`, `checkTransactionStatus`,
> `validateWebhookSignature` — routé par une nouvelle `PaymentStrategyFactory`
> (`paiement.service`), au-dessus d'un nouveau service façade `CanteenPaymentServiceImpl` exposé
> sous `/api/v2/canteen-payments/**`, coexistant avec `/api/v1/paiements` inchangé et partageant
> la même table `transactions_paiement`. `OrangeMoneyPaymentStrategy` est une intégration réelle
> (OAuth2 client_credentials, jeton mis en cache, appel Webpayment CI) via `WebClient`
> (dépendance `spring-boot-starter-webflux` ajoutée, utilisée uniquement comme client HTTP —
> `spring-boot-starter-web` déjà présent fait que Spring Boot déduit un type d'application
> `SERVLET` classique, l'application reste Spring MVC/Tomcat).**

Raison principale : ajouter un fournisseur devient un ajout de classe pur (principe Ouvert/Fermé),
sans jamais modifier `PaymentStrategyFactory` ni le service façade — structure documentée dans
`paiement.strategy.impl.package-info` pour `MtnMoMoPaymentStrategy`/`WavePaymentStrategy`.

Note de portée : le mandat de refactor initial ciblait à tort `services/core-api` (service KLEM
DataSphere sans rapport avec Cantine Connect, cf. `CLAUDE.md` racine §Portée) — corrigé vers ce
module réel après clarification avec le porteur du projet.

## Conséquences et Impacts

### ✅ Impacts Positifs (Gains)
- **Extensibilité sans régression** : `CinetPayProvider` implémente désormais les deux contrats
  sans qu'aucune de ses méthodes existantes ne soit modifiée — 44 tests existants toujours verts,
  suite portée à 51/51 avec les nouveaux tests (`PaymentStrategyFactoryTest`,
  `CanteenPaymentServiceImplTest`).
- **Fournisseur Orange Money réellement fonctionnel** dès que les identifiants marchands sont
  renseignés (`ORANGE_MONEY_CLIENT_ID`/`CLIENT_SECRET`/`MERCHANT_KEY`), même repli propre
  (message clair, pas de crash) sans clés réelles — cohérent avec CinetPay/PayDunya.

### ⚠️ Impacts Négatifs ou Risques (Compromis acceptés)
- **Deux contrats de paiement coexistent** (`PaymentProvider` historique et `PaymentStrategy`
  unifié) le temps d'une migration progressive — PayDunya et les 3 placeholders Direct
  n'implémentent encore que l'ancien contrat.
- **`PaymentProviderType` (passerelle) et `OperateurMobileMoney` (opérateur télécom du payeur)**
  sont deux axes distincts sans mapping 1:1 propre (`CanteenPaymentServiceImpl.toOperateurMobileMoney`
  choisit une valeur par défaut faute de champ dédié dans `PaymentRequestDto`) — à revisiter si un
  vrai besoin de distinction apparaît côté client.
- **Signature webhook Orange Money non confirmée** : squelette HMAC générique
  (`OrangeMoneyPaymentStrategy.validateWebhookSignature`), `verify-signature` à `false` par défaut
  tant que le schéma exact n'est pas confirmé auprès du support marchand Orange — même posture
  prudente que PayDunya déjà en place.
- **`MtnMoMoPaymentStrategy`/`WavePaymentStrategy` non implémentées** (pas d'accès marchand
  disponible à ce stade) — seule la structure d'ajout est documentée.

---
## Suivi et Validation
- [x] Code mis à jour selon l'ADR (`paiement.strategy.*`, `PaymentStrategyFactory`,
      `CanteenPaymentService(Impl)`, `CanteenPaymentController`, `CinetPayProvider` adapté).
- [x] Fichier `history-log.md` mis à jour après implémentation.
