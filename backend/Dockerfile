# syntax=docker/dockerfile:1.7
# ============================================================
# Image generique d'un microservice Spring Boot.
# Le module a construire est choisi par l'argument SERVICE, ce qui evite
# neuf Dockerfile identiques a maintenir en parallele.
#   docker build --build-arg SERVICE=auth-service -f backend/Dockerfile backend
# ============================================================

# ---------- Etape de construction ----------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

ARG SERVICE
COPY pom.xml ./
COPY services/ services/

# Depot Maven partage entre les neuf images via un cache BuildKit : sans lui,
# chaque service retelechargeait l'integralite des dependances pour son compte.
# sharing=locked serialise les acces concurrents au depot local.
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    mvn -B -q -pl services/${SERVICE} -am package -DskipTests \
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
