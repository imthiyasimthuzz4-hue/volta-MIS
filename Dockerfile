# ==============================================================================
# VOLTA MIS - Multi-Stage Production Dockerfile
# Optimized for Cloud Deployment (Render, Railway, VPS, Docker Compose)
# Base Image: Eclipse Temurin Java 21 (Alpine Linux - minimal footprint ~160MB)
# ==============================================================================

# ----------------- STAGE 1: Build Application with Maven -----------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy Maven Project Descriptor first for effective layer caching
COPY pom.xml .

# Download dependencies in a separate layer to speed up rebuilds
RUN mvn dependency:go-offline -B

# Copy project source code
COPY src ./src

# Package production executable JAR without running test suites
RUN mvn clean package -DskipTests -B

# ----------------- STAGE 2: Lightweight Production Runtime --------------------
FROM eclipse-temurin:21-jre-alpine AS runner

LABEL maintainer="Volta Electrical Associates <contact@voltaelectricalassociates.com>"
LABEL description="VOLTA Electrical Associates Management Information System (MIS)"

# Install curl for container health check
RUN apk add --no-cache curl tzdata

# Set system timezone to Indian Standard Time (IST)
ENV TZ=Asia/Kolkata
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

# Create a dedicated non-root application user for hardened security
RUN addgroup -S volta && adduser -S volta -G volta

# Create persistent storage directories for uploads and application logs
RUN mkdir -p /app/uploads/attendance /app/logs && chown -R volta:volta /app

# Copy the built standalone JAR from builder stage
COPY --from=builder --chown=volta:volta /build/target/volta-mis-1.0.0.jar /app/app.jar

# Copy sample site proof photo to uploads
COPY --chown=volta:volta uploads/attendance/sample_site_proof.png /app/uploads/attendance/sample_site_proof.png

# Switch to non-root user
USER volta

# Cloud configurable environment variables
ENV PORT=8080
ENV UPLOAD_DIR=/app/uploads
ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Xss512k -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom"

# Expose default HTTP port
EXPOSE 8080

# Persistent volume for worker attendance photos
VOLUME ["/app/uploads"]

# Container healthcheck probe
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/login || exit 1

# Launch Spring Boot with optimized container flags
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
