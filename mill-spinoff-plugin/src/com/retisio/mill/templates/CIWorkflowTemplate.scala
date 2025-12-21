package com.retisio.mill.templates

/**
 * Template generator for CI workflow (GitHub Actions).
 *
 * Generates .github/workflows/ci.yml with:
 * - Build and test on pull requests
 * - Lint and code quality checks
 * - Security scanning
 * - Test coverage reporting
 *
 * @param serviceName Name of service
 */
class CIWorkflowTemplate(serviceName: String) {

  def generate(): String = {
    val packageName = serviceName.toLowerCase
    
    s"""name: CI
       |
       |on:
       |  pull_request:
       |    branches: [ main, develop ]
       |  push:
       |    branches: [ main, develop ]
       |
       |jobs:
       |  build:
       |    runs-on: ubuntu-latest
       |    
       |    services:
       |      postgres:
       |        image: postgres:16-alpine
       |        env:
       |          POSTGRES_USER: ${packageName}
       |          POSTGRES_PASSWORD: ${packageName}
       |          POSTGRES_DB: ${packageName}_test
       |        options: >-
       |          --health-cmd pg_isready
       |          --health-interval 10s
       |          --health-timeout 5s
       |          --health-retries 5
       |        ports:
       |          - 5432:5432
       |      
       |      kafka:
       |        image: confluentinc/cp-kafka:7.5.3
       |        env:
       |          KAFKA_BROKER_ID: 1
       |          KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
       |          KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
       |          KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
       |        ports:
       |          - 9092:9092
       |    
       |    steps:
       |      - name: Checkout code
       |        uses: actions/checkout@v4
       |      
       |      - name: Set up JDK 21
       |        uses: actions/setup-java@v4
       |        with:
       |          distribution: 'temurin'
       |          java-version: '21'
       |          cache: 'mill'
       |      
       |      - name: Install Mill
       |        run: |
       |          curl -L https://github.com/com-lihaoyi/mill/releases/download/0.11.6/0.11.6 > mill
       |          chmod +x mill
       |          sudo mv mill /usr/local/bin/
       |      
       |      - name: Compile
       |        run: mill ${packageName}Service.compile
       |      
       |      - name: Run unit tests
       |        run: mill ${packageName}Service.test
       |        env:
       |          DATABASE_URL: postgresql://localhost:5432/${packageName}_test
       |          DATABASE_USER: ${packageName}
       |          DATABASE_PASSWORD: ${packageName}
       |          KAFKA_BOOTSTRAP_SERVERS: localhost:9092
       |      
       |      - name: Run integration tests
       |        run: mill ${packageName}Service.itest.test
       |        env:
       |          DATABASE_URL: postgresql://localhost:5432/${packageName}_test
       |          DATABASE_USER: ${packageName}
       |          DATABASE_PASSWORD: ${packageName}
       |          KAFKA_BOOTSTRAP_SERVERS: localhost:9092
       |      
       |      - name: Run BDD scenarios (Karate)
       |        run: |
       |          if [ -d "features" ]; then
       |            mill ${packageName}Service.itest.test -Dkarate.options="--tags @smoke"
       |          fi
       |      
       |      - name: Generate test coverage report
       |        run: mill ${packageName}Service.test.coverage
       |      
       |      - name: Upload coverage to Codecov
       |        uses: codecov/codecov-action@v3
       |        with:
       |          files: ./out/${packageName}Service/test/coverage/data/jacoco.xml
       |          flags: unittests
       |          name: codecov-${packageName}
       |      
       |      - name: Lint (Checkstyle)
       |        run: mill ${packageName}Service.checkstyle
       |        continue-on-error: true
       |      
       |      - name: Security scan (OWASP Dependency Check)
       |        run: mill ${packageName}Service.dependencyCheck
       |        continue-on-error: true
       |      
       |      - name: Build Docker image
       |        run: docker build -t ${packageName}-service:$$${{ github.sha }} .
       |      
       |      - name: Scan Docker image (Trivy)
       |        uses: aquasecurity/trivy-action@master
       |        with:
       |          image-ref: ${packageName}-service:$$${{ github.sha }}
       |          format: 'sarif'
       |          output: 'trivy-results.sarif'
       |      
       |      - name: Upload Trivy results to GitHub Security
       |        uses: github/codeql-action/upload-sarif@v2
       |        with:
       |          sarif_file: 'trivy-results.sarif'
       |""".stripMargin
  }
}
