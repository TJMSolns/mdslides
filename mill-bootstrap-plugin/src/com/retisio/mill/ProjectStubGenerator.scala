package com.retisio.mill

import os.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Generates project-specific stub files from templates.
 */
class ProjectStubGenerator(
  projectName: String,
  organization: String,
  description: String,
  targetRepoPath: Path
) {

  private val projectTitle = projectName
    .split("-")
    .map(_.capitalize)
    .mkString(" ")
  
  private val createdDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

  /**
   * Generate all project stubs.
   */
  def generateAllStubs(): Unit = {
    generateCharter()
    generateArchitecture()
    generateReadme()
    generateBuildSc()
    generateGitignore()
    generateGovernanceBacklog()
    generateStatus()
  }

  private def generateCharter(): Unit = {
    val content = CharterTemplate.generate(projectName, projectTitle, organization, description, createdDate)
    os.write(targetRepoPath / "CHARTER.md", content)
  }

  private def generateArchitecture(): Unit = {
    val content = ArchitectureTemplate.generate(projectName, projectTitle, organization, description, createdDate)
    os.write(targetRepoPath / "ARCHITECTURE.md", content)
  }

  private def generateReadme(): Unit = {
    val content = ReadmeTemplate.generate(projectName, projectTitle, organization, description, createdDate)
    os.write(targetRepoPath / "README.md", content)
  }

  private def generateBuildSc(): Unit = {
    val content = RootBuildScTemplate.generate(projectName, projectTitle, organization, description, createdDate)
    os.write(targetRepoPath / "build.sc", content)
  }

  private def generateGitignore(): Unit = {
    val content = GitignoreTemplate.generate(projectName, projectTitle, organization, description, createdDate)
    os.write(targetRepoPath / ".gitignore", content)
  }

  private def generateGovernanceBacklog(): Unit = {
    val content = s"""# Governance Documentation Backlog
      |
      |**Version**: 1.0.0  
      |**Last Updated**: $createdDate  
      |**Purpose**: Track ADRs, POLs, and PDRs for $projectTitle.
      |
      |---
      |
      |## 📊 Overview
      |
      |This document tracks all governance decisions for the $projectTitle project.
      |
      |**Total**: 0/0 (0%)
      |- **POLs**: 0/0 ✅
      |- **PDRs**: 0/0 ✅
      |- **ADRs**: 0/0 ✅
      |
      |---
      |
      |## 🏛️ Policies (POL)
      |
      || ID | Title | Description | Status | Priority | References |
      ||----|-------|-------------|--------|----------|------------|
      |
      |---
      |
      |## 🎯 Product Decisions (PDR)
      |
      || ID | Title | Description | Status | Priority | References |
      ||----|-------|-------------|--------|----------|------------|
      |
      |---
      |
      |## 🏗️ Architecture Decisions (ADR)
      |
      || ID | Title | Description | Status | Priority | References |
      ||----|-------|-------------|--------|----------|------------|
      |
      |---
      |
      |## References
      |
      |See `doc/reference/templates/` for governance document templates:
      |- `POL-TEMPLATE.md` - Policy template
      |- `PDR-TEMPLATE.md` - Product decision template
      |- `ADR-TEMPLATE.md` - Architecture decision template
      |""".stripMargin
    
    os.write(targetRepoPath / "GOVERNANCE-BACKLOG.md", content)
  }

  private def generateStatus(): Unit = {
    val content = s"""# $projectTitle - Status Dashboard
      |
      |**Last Updated**: $createdDate  
      |**Current Phase**: Phase 0a (Project Bootstrap) - COMPLETE ✅  
      |**Next Phase**: Phase 0b (Program Initiation)
      |
      |---
      |
      |## 📊 Project Status
      |
      |### Phase Completion
      |
      || Phase | Status | Completion % | Start Date | End Date |
      ||-------|--------|--------------|------------|----------|
      || 0a. Project Bootstrap | ✅ Complete | 100% | $createdDate | $createdDate |
      || 0b. Program Initiation | ⏳ Not Started | 0% | - | - |
      || 1. Strategic DDD (Discovery) | ⏳ Not Started | 0% | - | - |
      || 2. Tactical DDD (Scenarios) | ⏳ Not Started | 0% | - | - |
      || 3. Test-Driven Development | ⏳ Not Started | 0% | - | - |
      || 4. Feedback & Integration | ⏳ Not Started | 0% | - | - |
      |
      |---
      |
      |## 🎯 Next Steps
      |
      |### Immediate Actions (Phase 0b)
      |
      |1. **Complete CHARTER.md**:
      |   - Fill in TODO sections (vision, goals, scope, stakeholders, constraints, risks)
      |   - Follow `.github/copilot-instructions-phase0-program-initiation-program-manager.md`
      |   - Get stakeholder approval (Product Owner, Architect)
      |
      |2. **Verify framework files**:
      |   - HOW-WE-WORK.md present and readable ✅
      |   - All 14 copilot instructions present (.github/copilot-instructions*.md) ✅
      |   - All 22 SBPFs present (doc/reference/SBPF/) ✅
      |   - All 34 templates present (doc/reference/templates/) ✅
      |
      |3. **Verify build configuration**:
      |   - build.sc compiles: `mill resolve __`
      |   - Mill version constraint correct (0.11.6+)
      |   - Plugin sources present (mill-spinoff-plugin/, mill-deploy-plugin/) ✅
      |
      |4. **Configure team access**:
      |   - Add team members to GitHub repository
      |   - Verify branch protection rules
      |
      |---
      |
      |## 📚 Ceremonies Completed
      |
      |### Phase 0a: Project Bootstrap
      |- ✅ Repository created via mill-bootstrap-plugin
      |- ✅ Framework files copied (~110 files)
      |- ✅ Project stubs generated (CHARTER.md, ARCHITECTURE.md, README.md, build.sc, .gitignore)
      |- ✅ GitHub configured (branch protection, topics)
      |
      |---
      |
      |## 📈 Metrics
      |
      |### Documentation
      |- Governance Documents: 0/0 (0%)
      |- Domain Models: 0 aggregates
      |- BDD Scenarios: 0 features
      |
      |### Code
      |- Services: 0
      |- Test Coverage: N/A
      |- Lines of Code: 0
      |
      |---
      |
      |## 🔗 References
      |
      |- **HOW-WE-WORK.md**: Complete SDLC playbook
      |- **ADR-064**: Bootstrap via Mill Plugin
      |- **POL-031**: Mill Bootstrap Plugin Usage Policy
      |""".stripMargin
    
    os.write(targetRepoPath / "STATUS.md", content)
  }
}

/**
 * Template for CHARTER.md.
 */
object CharterTemplate {
  def generate(
    projectName: String,
    projectTitle: String,
    organization: String,
    description: String,
    createdDate: String
  ): String = {
    s"""# Project Charter: $projectTitle
      |
      |**Project Name**: $projectName  
      |**Organization**: $organization  
      |**Created**: $createdDate  
      |**Status**: Draft - Awaiting Completion
      |
      |---
      |
      |## Executive Summary
      |
      |**TODO**: Fill in 2-3 sentence executive summary.
      |
      |$description
      |
      |---
      |
      |## Vision
      |
      |**TODO**: What is the long-term vision for this project? What problem are we solving?
      |
      |---
      |
      |## Goals & Objectives
      |
      |**TODO**: List 3-5 specific, measurable goals.
      |
      |1. Goal 1: [TODO]
      |2. Goal 2: [TODO]
      |3. Goal 3: [TODO]
      |
      |---
      |
      |## Scope
      |
      |### In Scope
      |
      |**TODO**: What is explicitly included in this project?
      |
      |- [TODO]
      |
      |### Out of Scope
      |
      |**TODO**: What is explicitly excluded?
      |
      |- [TODO]
      |
      |---
      |
      |## Stakeholders
      |
      |**TODO**: List key stakeholders and their roles.
      |
      || Role | Name | Responsibility |
      ||------|------|----------------|
      || Program Manager | [TODO] | Coordination, dependencies, risk management |
      || Product Owner | [TODO] | Business value, requirements |
      || Architect | [TODO] | Domain modeling, technical strategy |
      || Bench Developer | [TODO] | Implementation, testing |
      |
      |---
      |
      |## Constraints
      |
      |**TODO**: What are the constraints (budget, time, technical, regulatory)?
      |
      |- **Budget**: [TODO]
      |- **Timeline**: [TODO]
      |- **Technical**: [TODO]
      |- **Regulatory**: [TODO]
      |
      |---
      |
      |## Assumptions
      |
      |**TODO**: What assumptions are we making?
      |
      |1. [TODO]
      |2. [TODO]
      |
      |---
      |
      |## Risks
      |
      |**TODO**: What are the top risks?
      |
      || Risk | Likelihood | Impact | Mitigation |
      ||------|------------|--------|------------|
      || [TODO] | High/Med/Low | High/Med/Low | [TODO] |
      |
      |---
      |
      |## Success Criteria
      |
      |**TODO**: How will we measure success?
      |
      |1. [TODO]
      |2. [TODO]
      |
      |---
      |
      |## Next Steps
      |
      |1. Complete this charter (fill in all TODO sections)
      |2. Get stakeholder approval
      |3. Begin Phase 1: Event Storming (see `.github/copilot-instructions-phase1-event-storming-architect.md`)
      |
      |---
      |
      |## References
      |
      |- **HOW-WE-WORK.md**: Complete SDLC process
      |- **Phase 0: Program Initiation**: `.github/copilot-instructions-phase0-program-initiation-program-manager.md`
      |""".stripMargin
  }
}

/**
 * Template for ARCHITECTURE.md.
 */
object ArchitectureTemplate {
  def generate(
    projectName: String,
    projectTitle: String,
    organization: String,
    description: String,
    createdDate: String
  ): String = {
    s"""# Architecture: $projectTitle
      |
      |**Project**: $projectName  
      |**Organization**: $organization  
      |**Last Updated**: $createdDate  
      |**Status**: Initial - To Be Completed
      |
      |---
      |
      |## Overview
      |
      |$description
      |
      |**TODO**: Expand with detailed architecture overview after Phase 1: Strategic DDD.
      |
      |---
      |
      |## System Context
      |
      |**TODO**: Add system context diagram (Mermaid) showing external systems and actors.
      |
      |\`\`\`mermaid
      |graph LR
      |    User[User] --> System[$projectTitle]
      |    System --> ExternalAPI[External API]
      |\`\`\`
      |
      |---
      |
      |## Bounded Contexts
      |
      |**TODO**: Document bounded contexts identified in Phase 1: Event Storming and Context Mapping.
      |
      || Context | Responsibility | Ubiquitous Language |
      ||---------|---------------|---------------------|
      || [TODO] | [TODO] | [TODO] |
      |
      |---
      |
      |## Technology Stack
      |
      |### Core Technologies
      |
      |- **Language**: Java 21+
      |- **Concurrency**: Apache Pekko Actors (ADR-001)
      |- **Messaging**: Apache Kafka (event streaming)
      |- **Database**: PostgreSQL (reactive drivers - ADR-003)
      |- **Build Tool**: Mill 0.11.6+ (ADR-056)
      |- **Testing**: Karate (BDD), JUnit 5 (TDD), jqwik (property-based)
      |- **Observability**: OpenTelemetry (tracing, metrics, logs)
      |
      |### Key Architectural Decisions
      |
      |- **ADR-001**: Use Pekko instead of Akka
      |- **ADR-002**: Reject Spring Boot Framework
      |- **ADR-003**: Reactive Postgres over JDBC
      |- **ADR-056**: Mill for JVM Builds
      |- **ADR-060**: Spinoff via Mill Plugin
      |
      |See `doc/governance/ADR/` for complete decisions.
      |
      |---
      |
      |## Integration Patterns
      |
      |**TODO**: Document integration patterns after Phase 1: Context Mapping.
      |
      |---
      |
      |## Deployment
      |
      |**TODO**: Document deployment architecture after services are implemented.
      |
      |- **Container Orchestration**: Kubernetes
      |- **CI/CD**: GitHub Actions
      |- **Deployment Automation**: mill-deploy-plugin
      |
      |---
      |
      |## Security
      |
      |**TODO**: Document security architecture.
      |
      |---
      |
      |## Observability
      |
      |**TODO**: Document observability strategy (OpenTelemetry, structured logging, metrics).
      |
      |---
      |
      |## Next Steps
      |
      |1. Complete Phase 1: Strategic DDD
      |   - Event Storming (`.github/copilot-instructions-phase1-event-storming-architect.md`)
      |   - Ubiquitous Language (`.github/copilot-instructions-phase1-ubiquitous-language-architect.md`)
      |   - Domain Modeling (`.github/copilot-instructions-phase1-domain-modeling-architect.md`)
      |   - Context Mapping (`.github/copilot-instructions-phase1-context-mapping-architect.md`)
      |2. Update this document with findings
      |3. Create ADRs for major architectural decisions
      |
      |---
      |
      |## References
      |
      |- **HOW-WE-WORK.md**: Complete SDLC process
      |- **CHARTER.md**: Project charter
      |- **doc/governance/ADR/**: Architecture decisions
      |- **doc/reference/SBPF/**: Shared best practices
      |""".stripMargin
  }
}

/**
 * Template for README.md.
 */
object ReadmeTemplate {
  def generate(
    projectName: String,
    projectTitle: String,
    organization: String,
    description: String,
    createdDate: String
  ): String = {
    s"""# $projectTitle
      |
      |**$description**
      |
      |This project follows a **ceremony-based SDLC** (DDD + BDD + TDD) as documented in `HOW-WE-WORK.md`.
      |
      |---
      |
      |## 🚀 Quick Start
      |
      |### Prerequisites
      |
      |- **Java 21+**: Install via SDKMAN (`sdk install java 21.0.1-tem`)
      |- **Mill 0.11.6+**: Install via Coursier (`cs install mill`)
      |- **Docker**: For local development (PostgreSQL, Kafka)
      |
      |### Build
      |
      |\`\`\`bash
      |# Verify build configuration
      |mill resolve __
      |
      |# Compile all modules
      |mill __.compile
      |
      |# Run tests
      |mill __.test
      |\`\`\`
      |
      |---
      |
      |## 📚 Documentation
      |
      |### Core Documents
      |
      |- **[HOW-WE-WORK.md](HOW-WE-WORK.md)**: Complete SDLC process (14 ceremonies, 4 phases)
      |- **[CHARTER.md](CHARTER.md)**: Project charter (vision, goals, stakeholders)
      |- **[ARCHITECTURE.md](ARCHITECTURE.md)**: System architecture (bounded contexts, technology stack)
      |- **[STATUS.md](STATUS.md)**: Current phase, next steps, metrics
      |- **[GOVERNANCE-BACKLOG.md](GOVERNANCE-BACKLOG.md)**: ADRs, POLs, PDRs tracking
      |
      |### Domain Models
      |
      |- **Ubiquitous Language**: `doc/domain-models/ubiquitous-language.md`
      |- **Event Storming**: `doc/domain-models/event-storming/`
      |- **Aggregates**: `doc/domain-models/aggregates/`
      |- **Context Map**: `doc/domain-models/context-map.md`
      |
      |### Scenarios
      |
      |- **Example Maps**: `doc/scenarios/example-maps/`
      |- **BDD Features**: `features/` (Gherkin scenarios)
      |
      |### Governance
      |
      |- **ADRs**: `doc/governance/ADR/` (architecture decisions)
      |- **POLs**: `doc/governance/POL/` (policies)
      |- **PDRs**: `doc/governance/PDR/` (product decisions)
      |
      |---
      |
      |## 🏗️ Project Structure
      |
      |\`\`\`
      |$projectName/
      |├── .github/
      |│   └── copilot-instructions*.md     # 14 ceremony instruction files
      |├── doc/
      |│   ├── domain-models/               # Event storming, aggregates, context maps
      |│   ├── governance/                  # ADRs, POLs, PDRs
      |│   ├── planning/                    # Project planning artifacts
      |│   ├── scenarios/                   # Example maps, acceptance criteria
      |│   └── reference/
      |│       ├── SBPF/                    # 22 shared best practice files
      |│       ├── templates/               # 34 document templates
      |│       └── validation/              # 5 ceremony checklists
      |├── features/                        # BDD scenarios (Gherkin)
      |├── mill-bootstrap-plugin/           # Project bootstrap automation
      |├── mill-spinoff-plugin/             # Service extraction automation
      |├── mill-deploy-plugin/              # Deployment automation
      |├── services/                        # Bounded context implementations
      |├── ARCHITECTURE.md                  # System architecture
      |├── build.sc                         # Mill build configuration
      |├── CHARTER.md                       # Project charter
      |├── GOVERNANCE-BACKLOG.md            # Governance tracking
      |├── HOW-WE-WORK.md                   # SDLC playbook
      |├── README.md                        # This file
      |└── STATUS.md                        # Status dashboard
      |\`\`\`
      |
      |---
      |
      |## 🎯 Current Status
      |
      |**Phase**: Phase 0a (Project Bootstrap) - COMPLETE ✅  
      |**Next**: Phase 0b (Program Initiation) - Complete CHARTER.md
      |
      |See [STATUS.md](STATUS.md) for details.
      |
      |---
      |
      |## 🤝 Contributing
      |
      |This project follows a **ceremony-based SDLC**. All contributions must follow the process documented in `HOW-WE-WORK.md`.
      |
      |### Development Workflow
      |
      |1. **Phase 1: Strategic DDD** - Understand domain (Event Storming → Ubiquitous Language → Domain Modeling → Context Mapping)
      |2. **Phase 2: Tactical DDD** - Define scenarios (Three Amigos → Example Mapping → Acceptance Criteria Review)
      |3. **Phase 3: TDD** - Implement (Test-First Pairing → Red-Green-Refactor → Property-Based Testing)
      |4. **Phase 4: Feedback & Integration** - Validate (Scenario-to-Test Decomposition → Domain Model Retrospective → Living Documentation Sync → Cross-Boundary Integration Testing)
      |
      |See `.github/copilot-instructions*.md` for detailed ceremony guides.
      |
      |---
      |
      |## 📞 Contact
      |
      |**Organization**: $organization  
      |**Repository**: https://github.com/$organization/$projectName
      |
      |---
      |
      |## 📄 License
      |
      |[TODO: Add license information]
      |""".stripMargin
  }
}

/**
 * Template for build.sc.
 */
object RootBuildScTemplate {
  def generate(
    projectName: String,
    projectTitle: String,
    organization: String,
    description: String,
    createdDate: String
  ): String = {
    s"""import mill._
      |import mill.scalalib._
      |
      |// Import vendored Mill plugins (copied during spinoff)
      |// These plugins enforce ceremony-based SDLC: TDD, BDD, DDD
      |// import $$file.`mill-plugins`.`mill-testing-plugin`.build
      |// import $$file.`mill-plugins`.`mill-specification-plugin`.build
      |// import $$file.`mill-plugins`.`mill-domain-plugin`.build
      |// import com.retisio.mill.{TestingModule, SpecificationModule, DomainModule}
      |
      |// Mill Build Configuration for $projectTitle
      |// Generated: $createdDate
      |
      |/**
      | * Root build configuration.
      | *
      | * Services (bounded contexts) should be defined as submodules in services/ directory.
      | *
      | * Example:
      | * object services extends Module {
      | *   object tenantManagement extends ServiceModule 
      | *     with TestingModule 
      | *     with SpecificationModule 
      | *     with DomainModule {
      | *     def scalaVersion = "2.13.12"
      | *   }
      | * }
      | */
      |object root extends RootModule {
      |  
      |  // Mill version constraint
      |  def millVersion = "0.11.6"
      |  
      |  // Verify Mill version at build time
      |  def checkMillVersion = T.command {
      |    val currentVersion = mill.BuildInfo.millVersion
      |    println(s"Mill version: $$currentVersion (required: $$millVersion+)")
      |  }
      |}
      |
      |/**
      | * Base trait for service modules (bounded contexts).
      | *
      | * Includes common dependencies and configuration.
      | */
      |trait ServiceModule extends ScalaModule {
      |  def scalaVersion = "2.13.12"
      |  
      |  // Common dependencies for all services
      |  override def ivyDeps = T {
      |    Agg(
      |      // TODO: Add common dependencies (Pekko, PostgreSQL, Kafka, etc.)
      |    )
      |  }
      |  
      |  override def scalacOptions = Seq(
      |    "-encoding", "UTF-8",
      |    "-feature",
      |    "-deprecation",
      |    "-unchecked",
      |    "-Xlint"
      |  )
      |  
      |  // Test configuration
      |  object test extends ScalaTests with TestModule.Munit {
      |    override def ivyDeps = T {
      |      Agg(
      |        ivy"org.scalameta::munit:0.7.29"
      |      )
      |    }
      |  }
      |}
      |
      |// TODO: Define service modules in services/ directory
      |// See HOW-WE-WORK.md for service creation workflow (mill-spinoff-plugin)
      |""".stripMargin
  }
}

/**
 * Template for .gitignore.
 */
object GitignoreTemplate {
  def generate(
    projectName: String,
    projectTitle: String,
    organization: String,
    description: String,
    createdDate: String
  ): String = {
    s"""# Mill Build Tool
      |out/
      |.mill-version
      |
      |# IDEs
      |.idea/
      |.vscode/
      |*.iml
      |*.ipr
      |*.iws
      |.bsp/
      |
      |# Scala
      |target/
      |*.class
      |*.log
      |
      |# Java
      |*.class
      |*.jar
      |*.war
      |*.ear
      |
      |# Mac
      |.DS_Store
      |
      |# Logs
      |logs/
      |*.log
      |
      |# Temporary files
      |tmp/
      |*.tmp
      |*.bak
      |*.swp
      |*~
      |
      |# Environment
      |.env
      |.env.local
      |
      |# Secrets
      |secrets/
      |*.pem
      |*.key
      |
      |# Docker
      |docker-compose.override.yml
      |
      |# Kubernetes
      |k8s/secrets/
      |
      |# Test reports
      |test-reports/
      |coverage/
      |
      |# Generated documentation
      |site/
      |""".stripMargin
  }
}
