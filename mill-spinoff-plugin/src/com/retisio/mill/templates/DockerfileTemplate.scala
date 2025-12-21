package com.retisio.mill.templates

/**
 * Template generator for Dockerfile.
 *
 * Generates multi-stage Dockerfile with:
 * - Build stage (Mill + JDK 21)
 * - Runtime stage (JRE 21 slim)
 * - Non-root user
 * - Health checks
 *
 * @param serviceName Name of service
 */
class DockerfileTemplate(serviceName: String) {

  def generate(): String = {
    val packageName = serviceName.toLowerCase
    
    s"""# Stage 1: Build
       |FROM eclipse-temurin:21-jdk-alpine AS builder
       |
       |# Install Mill
       |RUN apk add --no-cache curl bash
       |RUN curl -L https://github.com/com-lihaoyi/mill/releases/download/0.11.6/0.11.6 > /usr/local/bin/mill && \\
       |    chmod +x /usr/local/bin/mill
       |
       |# Set working directory
       |WORKDIR /app
       |
       |# Copy source code
       |COPY . .
       |
       |# Build JAR
       |RUN mill ${packageName}Service.assembly
       |
       |# Stage 2: Runtime
       |FROM eclipse-temurin:21-jre-alpine
       |
       |# Install curl for health checks
       |RUN apk add --no-cache curl
       |
       |# Create non-root user
       |RUN addgroup -g 1000 ${packageName} && \\
       |    adduser -D -u 1000 -G ${packageName} ${packageName}
       |
       |# Set working directory
       |WORKDIR /app
       |
       |# Copy JAR from builder
       |COPY --from=builder /app/out/${packageName}Service/assembly.dest/out.jar /app/${packageName}-service.jar
       |
       |# Copy configuration
       |COPY --from=builder /app/src/main/resources/application.conf /app/application.conf
       |
       |# Change ownership
       |RUN chown -R ${packageName}:${packageName} /app
       |
       |# Switch to non-root user
       |USER ${packageName}
       |
       |# Expose port
       |EXPOSE 8080
       |
       |# Health check
       |HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \\
       |  CMD curl -f http://localhost:8080/health || exit 1
       |
       |# Run application
       |ENTRYPOINT ["java"]
       |CMD ["-Xmx512m", \\
       |     "-Xms256m", \\
       |     "-XX:+UseG1GC", \\
       |     "-XX:MaxGCPauseMillis=100", \\
       |     "-XX:+UseStringDeduplication", \\
       |     "-Dconfig.file=/app/application.conf", \\
       |     "-jar", "/app/${packageName}-service.jar"]
       |""".stripMargin
  }
}
