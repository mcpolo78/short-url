# Build stage
FROM openjdk:21-jdk-slim as build

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM openjdk:21-jre-slim

WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the jar file from build stage
COPY --from=build /app/target/*.jar app.jar

# Create directory for QR codes
RUN mkdir -p qr-codes

# Expose port (Railway will override with $PORT)
EXPOSE ${PORT:-8080}

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application with Railway-compatible port binding
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
