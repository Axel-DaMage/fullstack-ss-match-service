# ============================================
# Build stage - Maven con cache de dependencias
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Copiar solo pom.xml primero para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================================
# Runtime stage - Imagen mínima de producción
# ============================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar JAR desde build stage
COPY --from=builder /build/target/match-service-0.0.1-SNAPSHOT.jar app.jar

# Cambiar a usuario no-root
USER appuser

EXPOSE 3003

# Healthcheck - verifica que el puerto esté escuchando
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD nc -z localhost 3003 || exit 1

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]