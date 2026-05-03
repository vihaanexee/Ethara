# ============================================
# Stage 1: Build the application
# ============================================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper and POM first (for layer caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN chmod +x mvnw && ./mvnw dependency:resolve -B

# Copy source code and build
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# ============================================
# Stage 2: Run the application
# ============================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Set ownership
RUN chown -R appuser:appuser /app
USER appuser

# Expose port (configurable via PORT env var)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/login || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
