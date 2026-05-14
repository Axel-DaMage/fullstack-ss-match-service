# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/match-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 3003
ENTRYPOINT ["java", "-jar", "app.jar"]