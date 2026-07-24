# ADR-014 : Latence Production Anormale — Logging TRACE Hibernate Non Désactivé en Profil `prod`

**Date :** 2026-07-01
**Statut :** Accepté
**Décideur :** Yacouba SYLLA

---

## Contexte

Signalement utilisateur : la page « Gestion des Utilisateurs » restait bloquée en chargement (skeleton) après actualisation, perçu comme un problème de requêtes non optimisées. Mesure directe contre la production (`curl` avec timing détaillé) :

| Endpoint | Temps observé |
|---|---|
| `/actuator/health` | 4s à 24s (variable) |
| `/api/v1/utilisateurs` | ~25s |
| `/api/v1/parents` | ~29s |
| `/api/v1/dashboard/stats` | ~44s |

**`/actuator/health` ne fait quasiment aucun traitement métier ni requête complexe** — sa lenteur (jusqu'à 24s) prouve que le problème n'est pas une requête SQL spécifique mal optimisée, mais une surcharge affectant **tous** les traitements de la JVM.

**Cause racine (confirmée via `railway logs`)** : `application.yml` active `org.hibernate.orm.jdbc.bind: TRACE` dans son bloc de configuration de base (hors bloc de profil). Le profil `prod` surcharge bien `com.klem.cantine` (→ `INFO`) et `org.hibernate.SQL` (→ `WARN`), **mais jamais `org.hibernate.orm.jdbc.bind`**, qui reste donc à `TRACE` même en production. Ce logger écrit une ligne par paramètre lié à chaque requête SQL exécutée (10 lignes pour une requête à 10 paramètres, sur *chaque* requête HTTP) — un volume d'I/O synchrone considérable sur un conteneur Railway aux ressources CPU/mémoire limitées, dégradant fortement le temps de réponse de tous les endpoints, y compris ceux sans lien avec les requêtes de recherche introduites récemment.

---

## Décision

Ajouter `org.hibernate.orm.jdbc.bind: WARN` dans le bloc `logging.level` du profil `prod` de `application.yml`, pour surcharger explicitement la valeur `TRACE` héritée du bloc de base.

---

## Options Envisagées

- **Retirer `org.hibernate.orm.jdbc.bind: TRACE` du bloc de base** — rejeté : ce logger reste utile en développement local pour déboguer les requêtes JPQL (c'est d'ailleurs ce qui a permis de diagnostiquer le bug `lower(bytea)` de l'ADR-007/013) ; le problème n'est pas sa présence en dev mais son absence de surcharge en prod.
- **Passer tout le bloc `logging.level` de base à des valeurs de prod par défaut, et l'élever uniquement en profil `dev`** — rejeté : inverserait la logique actuelle (base = dev-friendly, profils = spécialisation) sans bénéfice supplémentaire, pour un changement plus large que nécessaire.

---

## Conséquences

### ✅ Positives
- Réduction attendue drastique de la latence sur tous les endpoints (mesure à confirmer après déploiement — voir Suivi).
- Aucun changement de comportement applicatif : uniquement du logging, aucun risque de régression fonctionnelle.

### ⚠️ Négatives / Points de vigilance
- Ce type de bug (une clé de configuration présente dans le bloc de base mais oubliée dans la surcharge d'un profil) est facile à reproduire pour d'autres clés à l'avenir. **Règle à suivre** : toute nouvelle clé `logging.level` ajoutée au bloc de base doit être immédiatement accompagnée d'une réflexion explicite sur sa valeur souhaitée en profil `prod`.
- Aucun outil de supervision (Railway Metrics / APM) n'a été consulté avant ce correctif — le diagnostic s'est appuyé uniquement sur `railway logs` et des mesures `curl` manuelles. Un outil de monitoring (Railway Metrics, ou un dashboard applicatif) permettrait de détecter ce type de dégradation avant qu'un utilisateur ne la signale.

---
## Suivi et Validation
- [x] Configuration corrigée (`org.hibernate.orm.jdbc.bind: WARN` en profil `prod`).
- [x] `./mvnw test` (24/24) toujours vert — changement de configuration pure, aucun impact sur la logique métier.
- [ ] **À confirmer après déploiement** : mesure de latence sur `/actuator/health`, `/api/v1/utilisateurs`, `/api/v1/paiements`, `/api/v1/dashboard/stats` — doit repasser sous la seconde de façon stable.
