# ── Stage 1 : Build ──────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:resolve -q --no-transfer-progress

COPY src ./src
RUN ./mvnw clean package -DskipTests -q --no-transfer-progress

# ── Stage 2 : Runtime ────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

# Laisse 45s au JVM/Spring pour démarrer avant de compter les échecs
HEALTHCHECK --interval=10s --timeout=5s --start-period=45s --retries=3 \
  CMD wget -qO- "http://localhost:${PORT:-8081}/actuator/health" || exit 1

# Bornes mémoire explicites — le conteneur Railway est limité à ~1 Go.
# Valeurs absolues (-Xmx) plutôt que -XX:MaxRAMPercentage : ce dernier
# dépend d'une détection correcte de la limite cgroup par le JVM, qui
# s'est révélée peu fiable sur ce conteneur (mémoire encore observée
# au-dessus de la limite malgré MaxRAMPercentage=60 — voir ADR-015/016).
# Sans -XX:MaxMetaspaceSize, le Metaspace (hors segment heap) peut aussi
# croître sans limite et faire dépasser au conteneur son quota mémoire.
ENTRYPOINT ["java", "-Xmx400m", "-Xms256m", "-XX:MaxMetaspaceSize=160m", "-Xss512k", "-jar", "app.jar"]
