package com.retisio.mill.templates

/**
 * Template generator for README.md.
 *
 * @param serviceName Name of service
 * @param sourceServiceName Name of parent service
 */
class READMETemplate(serviceName: String, sourceServiceName: String) {

  def generate(): String = {
    val packageName = serviceName.toLowerCase
    
    s"""# ${serviceName} Service
       |
       |${serviceName} Service - Spun off from `${sourceServiceName}` training repository.
       |
       |## Overview
       |
       |This service is a **bounded context** within the RETISIO platform, responsible for [DESCRIBE RESPONSIBILITY].
       |
       |**Key Responsibilities**:
       |- [Responsibility 1]
       |- [Responsibility 2]
       |- [Responsibility 3]
       |
       |**Bounded Context Relationships**:
       |- **Upstream**: [List upstream contexts and integration patterns (ACL, Conformist, etc.)]
       |- **Downstream**: [List downstream contexts and published languages]
       |- **Shared Kernel**: [List shared kernel dependencies]
       |
       |## Architecture
       |
       |This service follows **Hexagonal Architecture** (Ports & Adapters):
       |
       |```
       |src/
       |├── main/
       |│   ├── java/com/retisio/${packageName}/
       |│   │   ├── domain/          # Domain layer (aggregates, entities, value objects, domain events)
       |│   │   ├── application/     # Application layer (use cases, commands, queries, application services)
       |│   │   └── infrastructure/  # Infrastructure layer (repositories, adapters, messaging, web)
       |│   └── resources/
       |│       ├── application.conf # Typesafe Config
       |│       ├── logback.xml     # Logging configuration
       |│       └── db/migration/   # Flyway database migrations
       |└── test/
       |    ├── java/com/retisio/${packageName}/
       |    │   ├── domain/         # Unit tests for domain logic
       |    │   ├── application/    # Unit tests for use cases
       |    │   └── infrastructure/ # Integration tests for infrastructure
       |    └── resources/
       |```
       |
       |**Technology Stack**:
       |- **Language**: Java 21 LTS
       |- **Build Tool**: Mill 0.11.6+
       |- **Actor Framework**: Apache Pekko (Typed Actors)
       |- **Messaging**: Kafka (reactive streams via Pekko Connectors)
       |- **Database**: PostgreSQL (reactive via Vert.x SQL Client)
       |- **API**: Pekko HTTP (REST)
       |- **Observability**: OpenTelemetry (traces, metrics, logs)
       |- **Testing**: JUnit 5, AssertJ, Mockito, jqwik (property-based), Karate (BDD)
       |
       |## Getting Started
       |
       |### Prerequisites
       |
       |- **JDK**: 21+ (Temurin, Corretto, or Zulu)
       |- **Mill**: 0.11.6+ ([install instructions](https://mill-build.com/installation.html))
       |- **Docker**: For running PostgreSQL and Kafka locally
       |- **kubectl**: For deploying to Kubernetes
       |
       |### Local Development
       |
       |1. **Start infrastructure** (PostgreSQL + Kafka):
       |   ```bash
       |   docker-compose up -d
       |   ```
       |
       |2. **Compile code**:
       |   ```bash
       |   mill ${packageName}Service.compile
       |   ```
       |
       |3. **Run tests**:
       |   ```bash
       |   mill ${packageName}Service.test
       |   mill ${packageName}Service.itest.test
       |   ```
       |
       |4. **Run service**:
       |   ```bash
       |   mill ${packageName}Service.run
       |   ```
       |
       |5. **Build JAR**:
       |   ```bash
       |   mill ${packageName}Service.assembly
       |   ```
       |
       |6. **Build Docker image**:
       |   ```bash
       |   docker build -t ${packageName}-service:local .
       |   ```
       |
       |### Running BDD Scenarios
       |
       |```bash
       |# Run all scenarios
       |mill ${packageName}Service.itest.test -Dkarate.options="features/"
       |
       |# Run smoke tests only
       |mill ${packageName}Service.itest.test -Dkarate.options="--tags @smoke"
       |```
       |
       |## API Documentation
       |
       |OpenAPI specification: `doc/api/openapi.yaml`
       |
       |**Base URL** (local): `http://localhost:8080`
       |
       |**Key Endpoints**:
       |- `GET /health/live` - Liveness probe
       |- `GET /health/ready` - Readiness probe
       |- `GET /metrics` - Prometheus metrics
       |- [Add service-specific endpoints]
       |
       |## Deployment
       |
       |### CI/CD Pipelines
       |
       |- **CI**: Runs on every pull request (build, test, lint, security scan)
       |- **CD Staging**: Deploys to staging on merge to `develop` branch
       |- **CD Production**: Deploys to production on merge to `main` branch (with approval)
       |
       |### Kubernetes
       |
       |Deploy to staging:
       |```bash
       |kubectl apply -f k8s/ -n staging
       |```
       |
       |Deploy to production:
       |```bash
       |kubectl apply -f k8s/ -n production
       |```
       |
       |## Observability
       |
       |**Traces**: Exported to OpenTelemetry Collector → Jaeger
       |**Metrics**: Scraped by Prometheus → Grafana dashboards
       |**Logs**: Structured JSON logs → Elasticsearch → Kibana
       |
       |**Dashboards**:
       |- [Link to Grafana dashboard]
       |- [Link to Kibana logs]
       |- [Link to Jaeger traces]
       |
       |## Contributing
       |
       |See [HOW-WE-WORK.md](./HOW-WE-WORK.md) for SDLC process.
       |
       |**Development Workflow**:
       |1. **Phase 2.1**: Three Amigos (BDD scenario brainstorming)
       |2. **Phase 2.2**: Example Mapping (scenario refinement)
       |3. **Phase 2.3**: Acceptance Criteria Review (architecture approval)
       |4. **Phase 3.1**: Test-First Pairing (write failing tests)
       |5. **Phase 3.2**: Red-Green-Refactor (implement, pass tests, refactor)
       |6. **Phase 3.3**: Property-Based Testing (jqwik for invariants)
       |7. **Phase 4.1**: Scenario-to-Test Decomposition (map scenarios → tests)
       |8. **Phase 4.2**: Domain Model Retrospective (evolve domain model)
       |9. **Phase 4.3**: Living Documentation Sync (update docs)
       |
       |## Documentation
       |
       |- **Charter**: `doc/CHARTER.md`
       |- **Domain Model**: `doc/domain-models/`
       |- **BDD Scenarios**: `features/`
       |- **API Specification**: `doc/api/openapi.yaml`
       |- **Security**: `doc/security/`
       |- **Governance**: `doc/governance/` (ADRs, POLs, PDRs)
       |
       |## Team Ownership
       |
       |See `.github/CODEOWNERS` for team responsibilities.
       |
       |## License
       |
       |Apache License 2.0 - See [LICENSE](./LICENSE)
       |""".stripMargin
  }
}
