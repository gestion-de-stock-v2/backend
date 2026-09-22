# ============================================================
# Image generique d'un microservice Spring Boot.
# Le module a construire est choisi par l'argument SERVICE, ce qui evite
# neuf Dockerfile identiques a maintenir en parallele.
#   docker build --build-arg SERVICE=auth-service -f backend/Dockerfile backend
# ============================================================

# ---------- Etape de construction ----------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Les descripteurs sont copies avant les sources : tant qu'aucun pom ne change,
# le telechargement des dependances est servi par le cache de couches Docker.
COPY pom.xml ./
COPY services/config-server/pom.xml        services/config-server/pom.xml
COPY services/discovery/pom.xml            services/discovery/pom.xml
COPY services/gateway/pom.xml              services/gateway/pom.xml
COPY services/auth-service/pom.xml         services/auth-service/pom.xml
COPY services/stock-service/pom.xml        services/stock-service/pom.xml
COPY services/customer-service/pom.xml     services/customer-service/pom.xml
COPY services/order-service/pom.xml        services/order-service/pom.xml
COPY services/payment-service/pom.xml      services/payment-service/pom.xml
COPY services/notification-service/pom.xml services/notification-service/pom.xml

ARG SERVICE
RUN mvn -B -pl services/${SERVICE} -am dependency:go-offline -DskipTests

COPY services/ services/
RUN mvn -B -pl services/${SERVICE} -am package -DskipTests \
 && cp services/${SERVICE}/target/*.jar /build/application.jar

# ---------- Image d'execution ----------
FROM eclipse-temurin:17-jre AS runtime

# curl est requis par les healthcheck declares dans docker-compose.yml
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# Execution sans privileges : un service compromis ne doit pas etre root.
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

WORKDIR /app
COPY --from=builder --chown=spring:spring /build/application.jar application.jar

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/application.jar"]
