FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy maven wrapper et pom.xml
COPY backend/mvnw* ./
COPY backend/.mvn .mvn/
COPY backend/pom.xml ./

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies first (for better Docker layer caching)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY backend/src src/

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Expose port
EXPOSE $PORT

# Run the application
CMD ["java", "-Dserver.port=${PORT:-8080}", "-Dspring.profiles.active=railway", "-Xmx400m", "-jar", "target/*.jar"]
