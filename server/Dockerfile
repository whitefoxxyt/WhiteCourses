# Stage 1: Build optimisé
FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

# Copier les fichiers Gradle pour mise en cache
COPY gradle ./gradle
COPY gradlew ./
COPY gradle.properties ./
COPY settings.gradle.kts ./
COPY build.gradle.kts ./

# Télécharger les dépendances (layer cachée)
RUN ./gradlew dependencies --no-daemon || true

# Copier shared module
COPY shared ./shared

# Copier server module
COPY server ./server

# Build optimisé
RUN ./gradlew :server:build -x test --no-daemon --parallel

# Stage 2: Runtime optimisé
FROM eclipse-temurin:17-jre-alpine

# Ajouter curl pour healthcheck
RUN apk add --no-cache curl

WORKDIR /app

# Copier le JAR construit
COPY --from=build /app/server/build/libs/*.jar app.jar

EXPOSE 8080

# Health check optimisé
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

# Optimisations JVM
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-jar", "app.jar"]
