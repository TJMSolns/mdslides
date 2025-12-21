package com.retisio.mill.templates

/**
 * Template generator for spinoff ADR.
 *
 * Documents the decision to spin off a service from the training repository.
 *
 * @param subServiceName Name of spun-off service
 * @param sourceServiceName Name of parent service
 * @param targetOrg Target GitHub organization
 * @param repoName Repository name
 */
class SpinoffADRTemplate(
  subServiceName: String,
  sourceServiceName: String,
  targetOrg: String,
  repoName: String
) {

  def generate(): String = {
    val now = java.time.LocalDate.now()
    
    s"""# ADR-001: Spinoff from Training Repository
       |
       |**Status**: Accepted  
       |**Date**: ${now}  
       |**Deciders**: Architecture Team, Product Owner  
       |**Supersedes**: N/A  
       |**Superseded By**: N/A
       |
       |---
       |
       |## Context
       |
       |The **${subServiceName}** bounded context was originally developed within the `${sourceServiceName}` 
       |training repository as part of the RETISIO platform's **ceremony-based DDD+BDD+TDD** training framework.
       |
       |The training repository serves as a "factory" for bounded contexts:
       |- Event storming and domain modeling (Phase 1)
       |- BDD scenario development (Phase 2)
       |- Test-first implementation (Phase 3)
       |- Living documentation (Phase 4)
       |
       |Once a bounded context reaches production readiness (13/13 validation checks passed), it is 
       |**spun off** to its own repository for independent deployment and lifecycle management.
       |
       |### Spinoff Readiness Criteria
       |
       |The following 13 pre-flight checks were validated:
       |
       |1. ✅ **Charter exists** - Problem statement, goals, and success metrics defined
       |2. ✅ **Domain model complete** - Aggregates, entities, value objects, and ubiquitous language documented
       |3. ✅ **Context map defined** - Upstream/downstream relationships and integration patterns specified
       |4. ✅ **BDD scenarios exist** - Behavioral specifications in Gherkin (Karate)
       |5. ✅ **Unit tests exist** - >80% code coverage with jqwik property-based tests
       |6. ✅ **Integration contracts defined** - ACL/Conformist patterns for external integrations
       |7. ✅ **API specification complete** - OpenAPI 3.1 specification
       |8. ✅ **Event schemas defined** - Kafka event schemas (Avro or JSON Schema)
       |9. ✅ **Database migrations ready** - Flyway migrations for schema evolution
       |10. ✅ **Observability configured** - OpenTelemetry traces, metrics, and structured logs
       |11. ✅ **Security controls defined** - Authentication, authorization, and data protection
       |12. ✅ **Deployment manifests ready** - Dockerfile and Kubernetes manifests
       |13. ✅ **Team ownership assigned** - CODEOWNERS file with team responsibilities
       |
       |All checks passed, triggering automatic spinoff via **Mill Spinoff Plugin** (v1.0.0).
       |
       |---
       |
       |## Decision
       |
       |We have spun off the **${subServiceName}** bounded context from `${sourceServiceName}` to its own 
       |repository: **`${targetOrg}/${repoName}`**.
       |
       |### Repository Structure
       |
       |The new repository follows the **SERVICE-REPOSITORY-STRUCTURE** template:
       |
       |```
       |${repoName}/
       |├── src/
       |│   ├── main/java/com/retisio/${subServiceName.toLowerCase()}/
       |│   │   ├── domain/          # Aggregates, entities, value objects, domain events
       |│   │   ├── application/     # Use cases, commands, queries, application services
       |│   │   └── infrastructure/  # Repositories, adapters, messaging, web controllers
       |│   └── resources/
       |│       ├── application.conf # Typesafe Config
       |│       └── db/migration/    # Flyway migrations
       |├── src/test/               # Unit + integration tests
       |├── features/                # BDD scenarios (Karate)
       |├── doc/                     # Domain models, API specs, security docs
       |├── k8s/                     # Kubernetes manifests
       |├── .github/workflows/      # CI/CD pipelines
       |├── build.sc                 # Mill build configuration
       |├── Dockerfile               # Multi-stage Docker build
       |└── README.md                # Service overview
       |```
       |
       |### CI/CD Pipelines
       |
       |The repository includes three GitHub Actions workflows:
       |
       |1. **CI** (`ci.yml`): Runs on every pull request
       |   - Compile, test, lint
       |   - Security scanning (OWASP, Trivy)
       |   - Test coverage reporting (Codecov)
       |   - BDD scenario execution (Karate)
       |
       |2. **CD Staging** (`cd-staging.yml`): Deploys to staging on merge to `develop`
       |   - Build Docker image
       |   - Deploy to Kubernetes (staging namespace)
       |   - Run smoke tests
       |   - Slack notifications
       |
       |3. **CD Production** (`cd-production.yml`): Deploys to production on merge to `main`
       |   - Canary deployment (10% traffic)
       |   - Monitor canary metrics (5 minutes)
       |   - Full rollout (90% traffic)
       |   - Smoke tests
       |   - GitHub release creation
       |   - Automatic rollback on failure
       |
       |### Bounded Context Relationships
       |
       |Based on `CONTEXT-MAP.md`, the ${subServiceName} context has the following relationships:
       |
       |- **Upstream Contexts**: [List extracted from context map]
       |- **Downstream Contexts**: [List extracted from context map]
       |- **Shared Kernels**: [List extracted from context map]
       |
       |Integration patterns are enforced via **Anti-Corruption Layers (ACL)** to maintain bounded context 
       |autonomy (Reactive Manifesto: isolation + autonomy).
       |
       |---
       |
       |## Consequences
       |
       |### Positive
       |
       |✅ **Independent Deployment**: Service can be deployed independently without coordinating with other services  
       |✅ **Autonomous Lifecycle**: Team can evolve the service at its own pace (technology choices, release cadence)  
       |✅ **Clear Ownership**: Dedicated repository with CODEOWNERS ensures accountability  
       |✅ **Scalability**: Service can be scaled independently based on its own load characteristics  
       |✅ **Isolation**: Failures in this service do not directly impact other bounded contexts  
       |✅ **Repository History**: Clean Git history starting from spinoff (training artifacts remain in parent repo)  
       |
       |### Negative
       |
       |❌ **Cross-Boundary Testing**: Integration tests spanning multiple services require coordination  
       |❌ **Shared Library Management**: Shared utilities (domain primitives, common infrastructure) must be published as Mill artifacts  
       |❌ **Documentation Drift**: Living documentation in training repo must be manually synced to production repo  
       |❌ **Dependency Updates**: Breaking changes in upstream contexts require synchronized updates  
       |
       |### Mitigations
       |
       |- **Cross-Boundary Integration Testing**: Ceremony 4.4 (Cross-Boundary Integration Testing) validates interactions via contract tests  
       |- **Shared Library Versioning**: Use semantic versioning (SemVer 2.0) for shared artifacts, document breaking changes in CHANGELOG.md  
       |- **Living Documentation Sync**: Ceremony 4.3 (Living Documentation Sync) ensures training and production docs stay aligned  
       |- **Context Map Maintenance**: Update `CONTEXT-MAP.md` in training repo when relationships change, notify downstream teams via Slack  
       |
       |---
       |
       |## Compliance
       |
       |This spinoff adheres to:
       |
       |- **ADR-059**: One Repository Per Bounded Context  
       |- **POL-028**: Repository-Per-Service Mandate  
       |- **ADR-060**: Spinoff via Mill Plugin  
       |- **POL-029**: Mill Spinoff Plugin Usage Policy  
       |- **ADR-056**: Mill for JVM Builds (supersedes Maven)  
       |- **ADR-057**: Scala 3 LTS for Functional Services (if using Scala)  
       |- **ADR-058**: Next.js with Ant Design for Frontend (if applicable)  
       |- **POL-026**: Yarn-Only for Frontend Dependency Management (if applicable)  
       |- **POL-027**: JVM Language Choice (Java 17+ or Scala 3+ only)  
       |
       |---
       |
       |## Follow-Up Actions
       |
       |- [ ] Configure CI/CD secrets in GitHub (DOCKER_USERNAME, DOCKER_PASSWORD, KUBECONFIG_STAGING, KUBECONFIG_PRODUCTION, SLACK_WEBHOOK_URL)
       |- [ ] Deploy to staging environment and validate smoke tests
       |- [ ] Update cross-boundary integration tests in training repository (Ceremony 4.4)
       |- [ ] Notify upstream and downstream teams of spinoff (post in #architecture-updates Slack channel)
       |- [ ] Schedule first Domain Model Retrospective (Ceremony 4.2) within 2 weeks
       |- [ ] Update SPINOFF-CANDIDATES.md in training repo (mark ${subServiceName} as ✅ Complete)
       |
       |---
       |
       |## References
       |
       |- Training Repository: [Link to ${sourceServiceName}]
       |- Domain Model: `doc/domain-models/${subServiceName}/`
       |- BDD Scenarios: `features/${subServiceName.toLowerCase()}/`
       |- Context Map: `doc/domain-models/CONTEXT-MAP.md`
       |- Spinoff Process: `doc/reference/SBPF/Service-Repository-Spinoff-Process.md`
       |- Mill Spinoff Plugin: https://github.com/RETISIO/mill-spinoff-plugin
       |
       |---
       |
       |**Spun off by**: Mill Spinoff Plugin v1.0.0  
       |**Repository**: ${targetOrg}/${repoName}  
       |**Spinoff Date**: ${now}
       |""".stripMargin
  }
}
