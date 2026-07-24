# ADR-015 : Latence Production (Suite ADR-014) — Dépassement Mémoire du Conteneur Railway

**Date :** 2026-07-01
**Statut :** Accepté
**Décideur :** Yacouba SYLLA

---

## Contexte

Après déploiement du correctif de l'ADR-014 (désactivation du logging `TRACE`), la latence en production restait sévère : `/actuator/health` répondait toujours en 12 à 42 secondes. `railway metrics --since 30m --json` a révélé la cause réelle :

```
"cpu":    { "utilization_pct": 0.0, "max": 0.73, "limit": 2.0 }
"memory": { "average_mb": 587.9, "max_mb": 1099.6, "limit_mb": 1024.0, "utilization_pct": 43.6 }
"http":   { "p50_ms": 13857, "p90_ms": 13857, "p95_ms": 13857, "p99_ms": 13857 }
```

Le CPU n'est quasiment jamais sollicité (0 % d'utilisation) — ce n'est donc pas un problème de calcul. En revanche, **la consommation mémoire maximale (1099,6 Mo) dépasse la limite du conteneur (1024 Mo)**, et la latence P50/P90/P95/P99 est **uniformément** de ~13,9 secondes sur toutes les requêtes de la fenêtre — signature classique d'un conteneur qui frôle ou dépasse sa limite mémoire cgroup : pression mémoire extrême, pagination/éviction agressive, pauses GC démesurées.

**Cause probable** : ni le `Dockerfile` ni `application.yml` (profil `prod`) ne fixaient de bornes explicites pour le JVM ou pour les threads Tomcat. Sans `-Xmx` explicite, le JVM (container-aware depuis Java 10+) borne le tas (`heap`) à 25 % de la mémoire détectée par défaut — mais le **Metaspace** (métadonnées de classes, hors segment heap) n'a **aucune limite par défaut** et peut croître indéfiniment. Le pool de threads Tomcat par défaut (200 threads, ~1 Mo de pile chacun) ajoute une réserve mémoire native significative si sollicité. Le pool HikariCP (`maximum-pool-size: 20`) ajoute également une charge mémoire par connexion (buffers, cache de requêtes préparées) disproportionnée pour le trafic réel d'un petit établissement scolaire.

---

## Décision

Bornes mémoire explicites, dimensionnées pour un conteneur ~1 Go :

1. **`Dockerfile`** — `ENTRYPOINT ["java", "-Xmx400m", "-Xms256m", "-XX:MaxMetaspaceSize=160m", "-Xss512k", "-jar", "app.jar"]` (conserve la forme tableau JSON, indispensable à la propagation des signaux d'arrêt — cf. ADR-008). **Mise à jour (voir Suivi)** : la première tentative utilisait `-XX:MaxRAMPercentage=60.0` ; mesurée après déploiement, la mémoire maximale dépassait toujours la limite du conteneur (1168 Mo observés). Remplacé par des valeurs absolues (`-Xmx`/`-Xms`), qui ne dépendent pas de la détection correcte de la limite cgroup par le JVM — cette détection s'est révélée peu fiable sur ce conteneur Railway.
2. **`application.yml` (profil `prod`)** :
   - `spring.datasource.hikari.maximum-pool-size` : `20` → `10`.
   - `spring.jpa.open-in-view` : `false` (corrige aussi l'avertissement `JpaBaseConfiguration$JpaWebConfiguration` présent depuis l'origine, qui laissait les connexions DB ouvertes plus longtemps que nécessaire pendant le rendu de vue).
   - `server.tomcat.threads.max` : `50` (au lieu de `200` par défaut), `min-spare` : `5`.

---

## Options Envisagées

- **Augmenter le plan Railway (plus de mémoire allouée)** — non écartée mais hors périmètre technique immédiat : c'est une décision budgétaire de l'utilisateur, pas un correctif de code. Le tuning ci-dessus réduit déjà fortement le risque à ressources constantes ; si la latence persiste après ce correctif, l'upgrade de plan devient la prochaine étape recommandée.
- **Ne rien fixer et laisser le JVM s'auto-dimensionner** — c'était la configuration précédente, à l'origine du dépassement observé.

---

## Conséquences

### ✅ Positives
- Réduit le risque de dépassement de la limite mémoire du conteneur en bornant les trois principales sources de croissance non contrôlée (heap, metaspace, piles de threads).
- Corrige au passage un avertissement de configuration présent depuis le début du projet (`open-in-view`).

### ⚠️ Négatives / Points de vigilance
- Un pool Hikari à 10 connexions et un pool Tomcat à 50 threads pourraient devenir un goulot d'étranglement si le trafic réel dépasse largement les hypothèses actuelles (un seul établissement pilote) — à surveiller via `railway metrics --http` lors de la montée en charge (plusieurs établissements).
- Cette configuration réduit le risque de dépassement mémoire mais **ne garantit pas l'absence totale de pics** si l'usage réel croît (nombre d'élèves, de paiements simultanés) au-delà du dimensionnement actuel. Si `railway metrics --memory` montre encore des pics proches de la limite après ce correctif, l'upgrade du plan Railway est l'étape suivante.

---
## Suivi et Validation
- [x] Configuration corrigée et testée localement (démarrage réussi avec les nouveaux flags JVM).
- [x] `./mvnw test` (24/24) toujours vert.
- [x] Première itération (`-XX:MaxRAMPercentage=60.0`) déployée et mesurée : latence P50 excellente (17ms) mais mémoire max toujours au-dessus de la limite (1168 Mo / 1024 Mo) — détection cgroup non fiable sur ce conteneur.
- [x] Corrigé avec des valeurs absolues (`-Xmx400m -Xms256m -XX:MaxMetaspaceSize=160m`), redéployé.
- [x] **Confirmé** — `railway metrics --since 2026-07-01T22:04:22` : `memory.max_mb` = 436-508 Mo, durablement sous `memory.limit_mb` (1024 Mo, ~45-50 % d'utilisation) ; `http.p50_ms` = 12-21 ms (contre ~13 857 ms uniforme avant correctif). Un P90/P95 résiduel (~2,2s) subsiste dans les toutes premières minutes suivant un déploiement (échauffement JIT / pool de connexions), sans rapport avec le problème initial.
- **Conclusion** : incident résolu. Aucune mise à niveau du plan Railway n'est nécessaire dans l'immédiat — l'application tient largement dans son allocation actuelle une fois correctement bornée. À réévaluer si l'usage réel croît significativement (plusieurs établissements, forte concurrence).
- Complète l'ADR-014 : le logging `TRACE` était un vrai problème (corrigé), mais pas la cause dominante de la latence observée après son correctif — la cause dominante est documentée ici.
