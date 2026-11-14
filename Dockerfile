# ==============================================================================
# VOLTA MIS - Production Multi-Stage Dockerfile
# Fully Optimized for Railway, Render, Koyeb, Docker Compose & Cloud Containers
# ==============================================================================

# ----------------- STAGE 1: Build Application with Maven -----------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ----------------- STAGE 2: Lightweight Production Runtime --------------------
FROM eclipse-temurin:21-jre-alpine AS runner

LABEL maintainer="Volta Electrical Associates <contact@voltaelectricalassociates.com>"
LABEL description="VOLTA Electrical Associates Management Information System (MIS)"

WORKDIR /app

RUN mkdir -p /app/uploads/attendance /app/logs

COPY --from=builder /build/target/volta-mis-1.0.0.jar /app/app.jar

ENV PORT=8080
ENV UPLOAD_DIR=/app/uploads
ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Xss512k -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

CMD ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
