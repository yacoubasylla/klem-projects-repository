# ADR-008 : Stratégie de Déploiement Production — Vercel (Frontend) + Railway (Backend)

**Date :** 2026-07-01  
**Statut :** Accepté  
**Décideur :** Yacouba SYLLA

---

## Contexte

Le projet Cantine Connect est architecturé en deux tiers découplés : un frontend SPA React/Vite et un backend API REST Spring Boot avec une base PostgreSQL. L'équipe a besoin d'une infrastructure de déploiement production sans gestion serveur (PaaS), compatible avec le budget d'un projet de démarrage et activable en moins d'une journée.

---

## Décision

**Déploiement découplé** sur deux plateformes spécialisées :

| Tiers | Plateforme | Justification |
|-------|-----------|---------------|
| Frontend React/Vite | **Vercel** | CDN global, build Vite natif, zero-config SPA |
| Backend Spring Boot | **Railway** | Docker natif, PostgreSQL managé, variables d'env intégrées |
| Base de données | **Railway PostgreSQL** | Plugin intégré, sauvegardes automatiques, même réseau privé |

---

## Configurations techniques validées

### Frontend — Vercel
```json
// client-frontend/vercel.json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "rewrites": [{ "source": "/(.*)", "destination": "/index.html" }]
}
```
Le rewrite est indispensable pour React Router — sans lui, les routes `/eleves`, `/scan` etc. retournent 404 sur Vercel.

### Backend — Railway Dockerfile multi-stage
```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS builder
# ... build Maven
FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```
L'ENTRYPOINT en tableau JSON est obligatoire — la forme shell ne permet pas à Railway de propager les signaux d'arrêt correctement.

### Backend — railway.toml
```toml
[build]
builder = "DOCKERFILE"

[deploy]
healthcheckPath = "/actuator/health"
healthcheckTimeout = 120
restartPolicyType = "ON_FAILURE"
```
Timeout 120s nécessaire : le JVM + Spring Boot + Flyway migrations prennent ~45-60s au premier démarrage.

### Port dynamique
```yaml
server:
  port: ${PORT:8081}
```
Railway injecte la variable `$PORT` dynamiquement à chaque déploiement. Spring Boot la lit via `${PORT:8081}` (fallback 8081 en local).

---

## Problème JDBC URL (voir aussi ADR-009)

Railway fournit `DATABASE_URL` au format `postgresql://...` incompatible avec HikariCP. La solution validée est de construire l'URL JDBC manuellement depuis les variables atomiques du plugin :

```
SPRING_DATASOURCE_URL = jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
SPRING_DATASOURCE_USERNAME = ${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD = ${{Postgres.PGPASSWORD}}
```

---

## Alternatives rejetées

| Plateforme | Raison du rejet |
|-----------|----------------|
| Render.com | Cold start 30-60s sur tier gratuit (inacceptable pour un outil opérationnel) |
| Fly.io | Complexité réseau (VPN Wireguard), courbe d'apprentissage élevée |
| Heroku | Tier gratuit supprimé, coût mensuel élevé |
| AWS/GCP | Sur-dimensionné pour la phase initiale, IaC non encore en place |

---

## Conséquences

**Positives :**
- Déploiement continu automatique sur push GitHub (Vercel + Railway)
- Zero gestion serveur, SSL/TLS automatique
- PostgreSQL managé avec sauvegardes Railway

**Négatives :**
- Dépendance à deux fournisseurs cloud distincts (couplage faible acceptable)
- `SPRING_PROFILES_ACTIVE=prod` doit être défini manuellement dans Railway Variables
- Variables `SPRING_DATASOURCE_*` à maintenir manuellement (pas d'injection automatique JDBC)
