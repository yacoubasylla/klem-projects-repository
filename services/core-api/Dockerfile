# syntax=docker/dockerfile:1
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -q -B dependency:go-offline
COPY src ./src
RUN ./mvnw -q -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S klem && adduser -S klem -G klem
WORKDIR /app
COPY --from=build /build/target/core-api-*.jar app.jar
USER klem
EXPOSE 8083
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s \
  CMD wget -qO- http://localhost:8083/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
