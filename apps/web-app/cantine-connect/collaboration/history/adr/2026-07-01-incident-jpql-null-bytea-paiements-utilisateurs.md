# ADR-013 : Incident Production — JPQL Nullable + LIKE sur Paiements/Utilisateurs (Récidive du Bug ADR-007)

**Date :** 2026-07-01
**Statut :** Accepté
**Décideur :** Yacouba SYLLA

---

## Contexte

Suite au déploiement des fonctionnalités de recherche (Utilisateurs, Paiements), la production a renvoyé des `500 Internal Server Error` sur `GET /api/v1/utilisateurs`, `GET /api/v1/paiements` et `GET /api/v1/dashboard/stats` — rendant l'écran « Gestion des Utilisateurs » vide (« Aucun utilisateur ») bien que les 4 comptes de la migration V6 existaient réellement en base. L'incident a été signalé par capture d'écran (console navigateur : `Failed to load resource... 500`).

**Cause racine (confirmée par reproduction locale + stack trace)** : les nouvelles requêtes `@Query` JPQL de `UtilisateurRepository` et `TransactionPaiementRepository` utilisaient le motif `(:param IS NULL OR LOWER(champ) LIKE ...)` avec des paramètres optionnels (`search`, `statut`) — **exactement le bug documenté dans l'ADR-007** (`ERROR: function lower(bytea) does not exist`), que ce projet avait déjà rencontré et corrigé une première fois pour les élèves. La règle de l'ADR-007 n'a pas été appliquée aux nouvelles requêtes ajoutées cette session — d'où la récidive.

Un second bug, indépendant, a été découvert lors de la correction : `TransactionPaiementRepository.statsAcceptesPeriode()` déclarait un retour `Object[]` direct (au lieu de `List<Object[]>`) pour une requête d'agrégat sans `GROUP BY`, provoquant un `ClassCastException` dans `DashboardService` — présent depuis l'origine du projet mais non lié à cette session.

Un troisième problème est apparu **pendant la correction elle-même** : en convertissant les requêtes en natif avec un `ORDER BY` explicite, Spring Data continue d'ajouter le `Sort` du `Pageable` en toutes lettres à la requête native (ex. `t.dateCreation` au lieu de `t.date_creation`), provoquant une nouvelle erreur SQL. Ce problème était invisible sur `EleveRepository`/`UtilisateurRepository` car leur propriété de tri (`nom`) coïncide par hasard avec le nom de colonne — mais pas `dateCreation` vs `date_creation`.

---

## Décision

1. **`UtilisateurRepository.findByRoleAndActifTrueWithSearch` / `.findAllWithSearch`** : converties en `@Query(nativeQuery = true)` avec `CAST(:param AS varchar)`, à l'identique du pattern déjà validé sur `EleveRepository` (ADR-007).
2. **`TransactionPaiementRepository.findAllWithFilters` / `.findAllWithFiltersForEleves`** : même conversion, avec en plus `statut` passé en `String` (`.name()`) plutôt qu'en enum JPA pour éviter tout problème de binding de type sur un paramètre nullable.
3. **`TransactionPaiementRepository.statsAcceptesPeriode`** : retourne désormais `List<Object[]>` ; `DashboardService` prend `list.isEmpty() ? null : list.get(0)`.
4. **`PaiementService.lister()`** : construit un `Pageable` sans `Sort` (`PageRequest.of(page, size)`) avant d'appeler les requêtes natives, puisque celles-ci embarquent déjà leur propre `ORDER BY t.date_creation DESC` — évite la double injection de tri par Spring Data.
5. **`GlobalExceptionHandler.handleGeneric`** : journalise désormais la stack trace complète (`log.error(..., ex)`) avant de renvoyer le 500 générique au client. Auparavant, **aucune trace n'était loggée nulle part** pour les erreurs 500 génériques — ce qui a considérablement ralenti le diagnostic de cet incident.

---

## Règle Résultante (renforce l'ADR-007)

> **Toute requête `@Query` JPQL comportant un paramètre optionnel (nullable) combiné à `LOWER()`/`LIKE` DOIT être écrite en `nativeQuery = true` avec `CAST(:param AS ...)`, jamais en JPQL avec `(:param IS NULL OR ...)`.**
>
> Si la requête native comporte un `ORDER BY` explicite, le `Pageable` transmis par le service **doit être dépourvu de `Sort`** (`PageRequest.of(page, size)`), sous peine de double tri invalide en SQL généré.
>
> Les erreurs génériques (`@ExceptionHandler(Exception.class)`) doivent toujours être journalisées côté serveur avant d'être masquées au client.

---

## Conséquences

### ✅ Positives
- Les trois endpoints (`/utilisateurs`, `/paiements`, `/dashboard/stats`) fonctionnent de nouveau, vérifié par reproduction locale puis correction confirmée (200 sur tous les cas : sans filtre, avec recherche, avec statut, tri par date).
- Les futurs 500 non gérés seront désormais visibles dans les logs Railway avec la stack trace complète — réduit le temps de diagnostic d'un futur incident similaire.
- Renforce une règle déjà écrite (ADR-007) mais pas suivie systématiquement — donne un exemple concret et récent à relire avant d'écrire une nouvelle requête filtrée.

### ⚠️ Négatives / Leçon apprise
- Cet incident n'a **pas été détecté avant le déploiement production** : les tests manuels effectués pendant la session précédente n'exerçaient que le cas « recherche avec un terme non vide », jamais le cas par défaut (`search=null`), qui est pourtant le chemin le plus emprunté (chargement initial de la liste sans filtre). **Tout test manuel d'un endpoint de recherche doit désormais couvrir explicitement le cas sans filtre, pas seulement le cas filtré.**
- Les tests unitaires (Mockito) ne peuvent pas détecter ce type de bug — il se situe au niveau de la traduction JPQL → SQL par Hibernate, invisible tant que le repository est mocké. Seul un test manuel contre une vraie base PostgreSQL (ou un test d'intégration `@DataJpaTest`) l'aurait révélé avant la mise en production.

---
## Suivi et Validation
- [x] Code corrigé et vérifié en local contre PostgreSQL réel (tous les cas : filtré / non filtré / trié).
- [x] Suite de tests JUnit (24/24) toujours verte.
- [x] `history-log.md` mis à jour avec le récit de l'incident.
