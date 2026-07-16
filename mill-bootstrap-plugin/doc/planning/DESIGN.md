# mill-bootstrap-plugin: Design Document

**Version**: 1.0.0  
**Date**: December 16, 2025  
**Status**: 🟡 Design Phase  
**Plugin Type**: Mill Build Tool Plugin  
**Language**: Scala 2.13.12

---

## Purpose

Create a Mill plugin that **bootstraps new project repositories** with the complete ceremony-based SDLC framework (SBPFs, templates, GitHub Copilot instructions, governance structure) from the the prior organization copilot-training repository.

**Key Distinction from mill-spinoff-plugin**:
- **mill-spinoff-plugin**: Extracts existing bounded context code → production service repo
- **mill-bootstrap-plugin**: Creates new empty project → training/project repo with full framework

---

## Use Cases

### Primary Use Case
**New Organization/Platform**: Create a new project repository following the prior organization's ceremony-based process

```bash
mill bootstrap.execute ecommerce-platform \
  --org Acme \
  --description "Multi-tenant ecommerce platform"
```

**Result**: `Acme/ecommerce-platform` repository with:
- ✅ Complete SBPFs (20+ guides)
- ✅ All templates (30+ files)
- ✅ HOW-WE-WORK.md (full process)
- ✅ GitHub Copilot instructions (14 ceremony files)
- ✅ Governance structure (ADR/PDR/POL frameworks)
- ✅ mill-spinoff-plugin (for later service spinoffs)
- ✅ Empty service directory (ready for bounded contexts)

### Secondary Use Cases
1. **Internal Projects**: New the prior organization projects (not part of main platform)
2. **Client Projects**: Deliver ceremony framework to consulting clients
3. **Open Source**: Share process framework with community
4. **Training**: Classroom/workshop environments

---

## Design Principles

### 1. Complete Framework Copy
**Not** cherry-picking - copy **entire** ceremony infrastructure:
- All SBPF guides (Mill, Maven, Testing, DDD, BDD, TDD, etc.)
- All templates (Charter, ADR, Domain Model, Event Storming, etc.)
- All GitHub Copilot ceremony instructions (14 files)
- Framework ADRs/POLs (technology choices, process decisions)

### 2. Project-Ready Stubs
Generate placeholder files for new project to fill in:
- `CHARTER.md` (with template structure)
- `ARCHITECTURE.md` (with template structure)
- `README.md` (project-specific)
- `GOVERNANCE-BACKLOG.md` (empty, ready for project docs)

### 3. Self-Sufficient Repository
Bootstrapped repo should be **independent**:
- Includes mill-spinoff-plugin source (for future service spinoffs)
- Includes mill-deploy-plugin source (for future deployments)
- No dependencies on copilot-training repo after creation

### 4. GitHub-First
Create GitHub repository atomically:
- Repository creation
- Initial commit and push
- Branch protection
- Topics/description
- Wiki disabled (docs in repo only)

---

## Architecture

### Plugin Structure

```
mill-bootstrap-plugin/
├── build.sc                           # Plugin build config
├── README.md                          # Usage documentation
├── src/com/retisio/mill/
│   ├── BootstrapModule.scala          # Main trait (commands)
│   ├── BootstrapValidator.scala       # 7 pre-flight checks
│   ├── FrameworkCopier.scala          # Copy SBPFs/templates/instructions
│   ├── ProjectStubGenerator.scala     # Generate CHARTER.md, etc.
│   ├── GitHubClient.scala             # Reuse from mill-spinoff-plugin
│   └── templates/
│       ├── RootBuildScTemplate.scala  # Generate root build.sc
│       ├── CharterTemplate.scala      # Generate CHARTER.md stub
│       ├── ArchitectureTemplate.scala # Generate ARCHITECTURE.md stub
│       ├── ReadmeTemplate.scala       # Generate README.md
│       └── GitignoreTemplate.scala    # Generate .gitignore
└── test/src/com/retisio/mill/
    └── (unit and integration tests)
```

### Component Responsibilities

#### 1. BootstrapModule (Main Trait)
```scala
trait BootstrapModule extends Module {
  // Configuration
  def targetOrg: T[String]           // GitHub organization
  def githubToken: T[String]         // From GITHUB_TOKEN env var
  def sourceRepoPath: T[Path]        // Path to copilot-training repo
  
  // Commands
  def bootstrapValidate(projectName: String): Command[Unit]
  def bootstrapExecute(projectName: String, description: String): Command[Unit]
  def bootstrapList(): Command[Unit]
}
```

**Commands**:
- `bootstrapValidate` - Run 7 prerequisite checks
- `bootstrapExecute` - Create repository with framework
- `bootstrapList` - Show recently bootstrapped projects (from GitHub API)

#### 2. BootstrapValidator
**7 Pre-Flight Checks**:

| # | Check | Severity | Category |
|---|-------|----------|----------|
| 1 | GitHub token valid (repo + admin:org scopes) | Blocking | Authentication |
| 2 | Target organization exists | Blocking | GitHub |
| 3 | Repository name available | Blocking | GitHub |
| 4 | Project name follows conventions (kebab-case) | Blocking | Naming |
| 5 | Git configured locally (user.name, user.email) | Blocking | Git |
| 6 | Mill 0.11.6+ installed | Blocking | Build Tool |
| 7 | Source repo path valid (copilot-training exists) | Blocking | Source |

**Validation Levels**: All checks are **Blocking** (no environment variance like mill-deploy-plugin)

#### 3. FrameworkCopier
**Copy Operations** (preserving directory structure):

```
Source: copilot-training/          Target: new-project/
├── HOW-WE-WORK.md            →    ├── HOW-WE-WORK.md (copy)
├── .github/                  →    ├── .github/
│   └── copilot-instructions* →    │   └── copilot-instructions* (all 14 files)
├── doc/reference/            →    ├── doc/reference/
│   ├── SBPF/                 →    │   ├── SBPF/ (all guides)
│   ├── templates/            →    │   ├── templates/ (all templates)
│   └── validation/           →    │   └── validation/ (ceremony checklists)
├── mill-spinoff-plugin/      →    ├── mill-spinoff-plugin/ (entire plugin)
└── mill-deploy-plugin/       →    └── mill-deploy-plugin/ (entire plugin)
```

**Copy Strategy**:
- **Recursive copy**: Use `os.copy.over()` for directories
- **Preserve structure**: Maintain exact paths
- **Filter exclusions**: Skip `.git/`, `out/`, `target/`, `.idea/`
- **Update references**: Replace "copilot-training" with new project name in copied files

#### 4. ProjectStubGenerator
**Generate Project-Specific Files**:

| File | Purpose | Template |
|------|---------|----------|
| `CHARTER.md` | Project charter with TODO sections | CharterTemplate |
| `ARCHITECTURE.md` | System architecture overview | ArchitectureTemplate |
| `README.md` | Project README with quickstart | ReadmeTemplate |
| `build.sc` | Root build configuration | RootBuildScTemplate |
| `.gitignore` | Mill/JVM ignore patterns | GitignoreTemplate |
| `GOVERNANCE-BACKLOG.md` | Empty governance tracking | Inline |
| `STATUS.md` | Project status dashboard | Inline |
| `services/.gitkeep` | Placeholder for future services | Empty file |

**Template Variables**:
- `projectName` - Kebab-case name (e.g., "ecommerce-platform")
- `projectTitle` - Title-case name (e.g., "Ecommerce Platform")
- `organization` - GitHub org (e.g., "Acme")
- `description` - Project description
- `createdDate` - Current date (ISO 8601)

#### 5. GitHubClient (Reused)
**Reuse from mill-spinoff-plugin** with minor extensions:

```scala
class GitHubClient(token: String) {
  // Existing methods (reused)
  def createRepository(org: String, name: String, description: String): String
  def configureBranchProtection(org: String, repo: String): Unit
  def setRepositoryTopics(org: String, repo: String, topics: List[String]): Unit
  
  // New method for bootstrap
  def disableWiki(org: String, repo: String): Unit
}
```

---

## Workflow

### Command: `bootstrapValidate <projectName>`

```
1. Parse project name
2. Run 7 validation checks
3. Display results (pass/fail with details)
4. Exit with code 0 (pass) or 1 (fail)
```

**Output Example**:
```
Validating bootstrap for: ecommerce-platform
================================================================================

✓ Check 1/7: GitHub token valid (repo, admin:org scopes)
✓ Check 2/7: Organization "Acme" exists
✓ Check 3/7: Repository "ecommerce-platform" available
✓ Check 4/7: Project name follows conventions (kebab-case)
✓ Check 5/7: Git configured (user: Tim Moore <tim@acme.com>)
✓ Check 6/7: Mill 0.11.6 installed
✓ Check 7/7: Source path valid (copilot-training found)

================================================================================
✅ ALL CHECKS PASSED - Ready to bootstrap
================================================================================
```

### Command: `bootstrapExecute <projectName> <description>`

```
1. Run validation checks (must pass)
2. Create GitHub repository
3. Clone empty repository locally (temp directory)
4. Copy framework files from source repo
5. Generate project-specific stubs
6. Create initial commit
7. Push to GitHub
8. Configure repository settings (branch protection, topics)
9. Display success message with next steps
```

**Output Example**:
```
Bootstrapping new project: ecommerce-platform
================================================================================

Step 1/9: Validating prerequisites...
  ✓ All 7 checks passed

Step 2/9: Creating GitHub repository...
  ✓ Repository created: Acme/ecommerce-platform
  ✓ URL: https://github.com/Acme/ecommerce-platform

Step 3/9: Cloning repository locally...
  ✓ Cloned to: /tmp/bootstrap-ecommerce-platform-abc123

Step 4/9: Copying framework files...
  ✓ Copied HOW-WE-WORK.md
  ✓ Copied .github/copilot-instructions (14 files)
  ✓ Copied doc/reference/SBPF (22 files)
  ✓ Copied doc/reference/templates (34 files)
  ✓ Copied doc/reference/validation (5 files)
  ✓ Copied mill-spinoff-plugin (source)
  ✓ Copied mill-deploy-plugin (source)

Step 5/9: Generating project stubs...
  ✓ Generated CHARTER.md
  ✓ Generated ARCHITECTURE.md
  ✓ Generated README.md
  ✓ Generated build.sc
  ✓ Generated .gitignore
  ✓ Generated GOVERNANCE-BACKLOG.md
  ✓ Generated STATUS.md

Step 6/9: Creating initial commit...
  ✓ Committed: "Initial project bootstrap from the prior organization/copilot-training"

Step 7/9: Pushing to GitHub...
  ✓ Pushed to main branch

Step 8/9: Configuring repository...
  ✓ Branch protection enabled (main)
  ✓ Topics set: ddd, bdd, tdd, ceremony-based, mill-build
  ✓ Wiki disabled

Step 9/9: Cleaning up...
  ✓ Removed temp directory

================================================================================
✅ BOOTSTRAP SUCCESSFUL
================================================================================

Next Steps:
1. Clone repository:   git clone https://github.com/Acme/ecommerce-platform.git
2. Fill in CHARTER.md: Define project goals, scope, stakeholders
3. Run Phase 0:        Complete program initiation ceremony
4. Start Phase 1:      Begin event storming for first bounded context

For help: See HOW-WE-WORK.md in the new repository

Repository: https://github.com/Acme/ecommerce-platform
Duration:   45 seconds
```

### Command: `bootstrapList()`

```
Query GitHub API for repositories created by this plugin
Display table: Name, Org, Created Date, Topics
```

**Output Example**:
```
Recently Bootstrapped Projects
================================================================================

Name                      Organization  Created              Topics
ecommerce-platform        Acme          2025-12-16 10:30 UTC ceremony-based, ddd, bdd
client-portal             the prior organization       2025-12-15 14:22 UTC ceremony-based, nextjs
internal-tools            the prior organization       2025-12-10 09:15 UTC ceremony-based, scala

Found 3 bootstrapped projects
```

---

## File Templates

### Template 1: RootBuildScTemplate

```scala
class RootBuildScTemplate(projectName: String) {
  def generate(): String = s"""
import mill._
import mill.scalalib._

/**
 * ${projectName}: Root Build Configuration
 * 
 * This is the root build.sc for the project. Services will be added
 * as modules following the ceremony-based SDLC process.
 * 
 * See: HOW-WE-WORK.md for the complete development process
 * See: doc/reference/SBPF/Mill-Build-Tool-Guide.md for Mill documentation
 */

// Services will be defined here following Phase 1-3 ceremonies
// Example:
// object tenantManagement extends JavaModule {
//   def scalaVersion = "2.13.12"
// }
"""
}
```

### Template 2: CharterTemplate

```scala
class CharterTemplate(
  projectName: String,
  projectTitle: String,
  description: String,
  organization: String,
  createdDate: String
) {
  def generate(): String = s"""
# ${projectTitle}: Project Charter

**Organization**: ${organization}  
**Created**: ${createdDate}  
**Status**: 🟡 Draft  
**Version**: 1.0.0

---

## Executive Summary

TODO: Brief (2-3 paragraphs) summary of project purpose, scope, and expected outcomes.

${description}

---

## Business Context

### Problem Statement

TODO: What business problem does this project solve?

### Opportunity

TODO: What market opportunity or strategic advantage does this enable?

### Success Criteria

TODO: How will we measure success? Define 3-5 measurable outcomes.

---

## Scope

### In Scope

TODO: What will this project deliver?
- [ ] Capability 1
- [ ] Capability 2
- [ ] Capability 3

### Out of Scope

TODO: What will this project explicitly NOT deliver?
- [ ] Future enhancement 1
- [ ] Related but separate system 2

---

## Stakeholders

| Role | Name | Responsibilities |
|------|------|------------------|
| Executive Sponsor | TODO | Decision authority, budget approval |
| Program Manager | TODO | Coordination, risk management, documentation |
| Product Owner | TODO | Requirements, acceptance criteria, domain expertise |
| Architect | TODO | Technical strategy, domain modeling, design decisions |
| Bench Developer | TODO | Implementation, testing, code quality |

---

## High-Level Architecture

TODO: Insert Mermaid C4 Context diagram

\`\`\`mermaid
graph TD
    User[User] --> System[${projectTitle}]
    System --> ExternalA[External System A]
    System --> ExternalB[External System B]
\`\`\`

---

## Bounded Contexts (Initial)

TODO: List anticipated bounded contexts (will be refined in Phase 1 Event Storming)

| Context | Description | Priority |
|---------|-------------|----------|
| Context 1 | TODO | High |
| Context 2 | TODO | Medium |
| Context 3 | TODO | Low |

---

## Timeline & Milestones

| Phase | Milestone | Target Date | Status |
|-------|-----------|-------------|--------|
| Phase 0 | Program Initiation Complete | TODO | 🟡 In Progress |
| Phase 1 | Discovery Complete (All Contexts) | TODO | ⚪ Not Started |
| Phase 2 | Requirements Complete (All Contexts) | TODO | ⚪ Not Started |
| Phase 3 | First Service MVP | TODO | ⚪ Not Started |

---

## Risks & Assumptions

### Key Risks

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| TODO | High | Medium | TODO |

### Key Assumptions

1. TODO: List critical assumptions this project depends on
2. TODO: Technical assumptions (infrastructure, tools, etc.)
3. TODO: Business assumptions (market, user behavior, etc.)

---

## Process & Ceremonies

This project follows the **ceremony-based SDLC** documented in \`HOW-WE-WORK.md\`.

**Phases**:
- **Phase 0**: Program Initiation (this charter)
- **Phase 1**: Discovery (Event Storming, Ubiquitous Language, Domain Modeling, Context Mapping)
- **Phase 2**: Requirements (Three Amigos, Example Mapping, Acceptance Criteria Review)
- **Phase 3**: Implementation (Test-First Pairing, Red-Green-Refactor, Property-Based Testing)
- **Phase 4**: Governance (Living Documentation Sync, Cross-Boundary Integration Testing)

**Ceremony Schedule**: TODO (Weekly/Bi-weekly cadence per ceremony)

---

## References

- **HOW-WE-WORK.md**: Complete SDLC process playbook
- **ARCHITECTURE.md**: System architecture overview
- **doc/reference/SBPF/**: Shared Best Practices & Frameworks
- **doc/reference/templates/**: Document templates for all ceremonies

---

## Approval

| Role | Name | Signature | Date |
|------|------|-----------|------|
| Executive Sponsor | TODO | | |
| Program Manager | TODO | | |
| Architect | TODO | | |

**Charter Status**: 🟡 Draft → Review → Approved
"""
}
```

### Template 3: ArchitectureTemplate

```scala
class ArchitectureTemplate(projectTitle: String) {
  def generate(): String = s"""
# ${projectTitle}: System Architecture

**Version**: 1.0.0  
**Last Updated**: ${java.time.LocalDate.now()}  
**Status**: 🟡 Draft

---

## Overview

TODO: High-level description of system architecture

---

## Technology Stack

### Backend
- **Language**: TODO (Java 21, Scala 3, etc.)
- **Build Tool**: Mill 0.11.6+
- **Concurrency**: TODO (Pekko Actors, etc.)
- **Messaging**: TODO (Kafka, etc.)
- **Database**: TODO (PostgreSQL, etc.)

### Frontend
- **Framework**: TODO (Next.js, etc.)
- **Language**: TypeScript
- **UI Library**: TODO (Ant Design, etc.)

### Infrastructure
- **Container**: Docker
- **Orchestration**: Kubernetes
- **Cloud**: TODO (AWS, GCP, Azure)
- **Observability**: TODO (OpenTelemetry, Prometheus, Grafana)

---

## System Context (C4 Model)

TODO: Insert C4 System Context diagram

\`\`\`mermaid
graph TD
    User[User] --> System[${projectTitle}]
    System --> ExternalA[External System A]
    System --> ExternalB[External System B]
\`\`\`

---

## Container Diagram (C4 Model)

TODO: Insert C4 Container diagram showing services

---

## Bounded Contexts

TODO: List bounded contexts from event storming

| Context | Relationship Type | Integration Pattern |
|---------|-------------------|---------------------|
| Context A | Upstream | Published Language |
| Context B | Downstream | Conformist |
| Context C | Partner | Shared Kernel |

See \`doc/domain-models/CONTEXT-MAP.md\` for detailed relationships.

---

## Architectural Decisions

See \`doc/governance/ADR/\` for all Architecture Decision Records.

**Key ADRs**:
- TODO: Link to key architectural decisions

---

## Non-Functional Requirements

### Performance
- TODO: Response time targets
- TODO: Throughput targets

### Scalability
- TODO: Load targets
- TODO: Horizontal scaling strategy

### Reliability
- TODO: Availability targets (e.g., 99.9%)
- TODO: Disaster recovery (RPO, RTO)

### Security
- TODO: Authentication (OAuth2, JWT, etc.)
- TODO: Authorization (RBAC, etc.)
- TODO: Data encryption (at rest, in transit)

---

## References

- **CHARTER.md**: Project charter and scope
- **doc/domain-models/CONTEXT-MAP.md**: Bounded context relationships
- **doc/governance/ADR/**: Architecture Decision Records
"""
}
```

### Template 4: ReadmeTemplate

```scala
class ReadmeTemplate(
  projectName: String,
  projectTitle: String,
  description: String,
  organization: String
) {
  def generate(): String = s"""
# ${projectTitle}

${description}

**Organization**: ${organization}  
**Repository**: [${organization}/${projectName}](https://github.com/${organization}/${projectName})

---

## Quick Start

### Prerequisites
- Java 21+
- Mill 0.11.6+
- Docker 24+
- kubectl 1.28+

### Build
\`\`\`bash
# Clone repository
git clone https://github.com/${organization}/${projectName}.git
cd ${projectName}

# Compile (when services exist)
mill __.compile
\`\`\`

### Test
\`\`\`bash
# Run all tests
mill __.test
\`\`\`

---

## Project Status

See [STATUS.md](STATUS.md) for current project status.

**Current Phase**: 🟡 Phase 0 - Program Initiation

---

## Documentation

| Document | Purpose |
|----------|---------|
| [CHARTER.md](CHARTER.md) | Project charter, scope, stakeholders |
| [ARCHITECTURE.md](ARCHITECTURE.md) | System architecture overview |
| [HOW-WE-WORK.md](HOW-WE-WORK.md) | Complete SDLC process (14 ceremonies) |
| [doc/reference/SBPF/](doc/reference/SBPF/) | Shared Best Practices & Frameworks |
| [doc/reference/templates/](doc/reference/templates/) | Document templates for ceremonies |

---

## Development Process

This project follows a **ceremony-based SDLC** with:
- **DDD** (Domain-Driven Design)
- **BDD** (Behavior-Driven Development)
- **TDD** (Test-Driven Development)

**4 Phases, 14 Ceremonies**:

### Phase 0: Program Initiation
- Charter creation (this document)

### Phase 1: Discovery (DDD-Led)
- Event Storming
- Ubiquitous Language Workshop
- Domain Modeling
- Context Mapping

### Phase 2: Requirements (BDD-Led)
- Three Amigos Session
- Example Mapping
- Acceptance Criteria Review

### Phase 3: Implementation (TDD-Led)
- Test-First Pairing
- Red-Green-Refactor Cycles
- Property-Based Testing

### Phase 4: Governance & Integration
- Scenario-to-Test Decomposition
- Domain Model Retrospective
- Living Documentation Sync
- Cross-Boundary Integration Testing
- Automated Deployment (mill-deploy-plugin)

See [HOW-WE-WORK.md](HOW-WE-WORK.md) for complete ceremony descriptions.

---

## Services

This project follows the **one repository per bounded context** pattern. Services will be listed here as they are identified in Phase 1 (Event Storming) and spun off using [mill-spinoff-plugin](mill-spinoff-plugin/).

**Planned Services** (from CHARTER.md):
- TODO: List services/bounded contexts

---

## Contributing

See [HOW-WE-WORK.md](HOW-WE-WORK.md) for the development process.

**For team members**:
1. Follow ceremony process (don't skip phases)
2. Use provided templates in \`doc/reference/templates/\`
3. Document decisions in ADRs (\`doc/governance/ADR/\`)
4. Use GitHub Copilot with ceremony instructions in \`.github/copilot-instructions*.md\`

---

## License

TODO: Choose license (Apache 2.0, MIT, Proprietary, etc.)

---

## Support

- **Issues**: [GitHub Issues](https://github.com/${organization}/${projectName}/issues)
- **Discussions**: [GitHub Discussions](https://github.com/${organization}/${projectName}/discussions)
- **Wiki**: Disabled (documentation in repository only)
"""
}
```

---

## Configuration

### Usage in build.sc (the prior organization/copilot-training)

```scala
import $ivy.`com.retisio::mill-bootstrap:1.0.0`
import com.retisio.mill.BootstrapModule

object bootstrap extends BootstrapModule {
  // Configuration with defaults
  def targetOrg = T { "the prior organization" }  // Can override per project
  def sourceRepoPath = T { os.pwd }  // Current repo
}
```

### Commands

```bash
# Validate prerequisites
mill bootstrap.validate ecommerce-platform

# Execute bootstrap (creates GitHub repo)
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx
mill bootstrap.execute ecommerce-platform "Multi-tenant ecommerce platform"

# Override organization
mill bootstrap.execute my-project "Description" --org Acme

# List bootstrapped projects
mill bootstrap.list
```

---

## Dependencies

### Runtime Dependencies
- Mill 0.11.6+
- os-lib 0.9.1 (file operations)
- requests 0.8.0 (GitHub API)
- upickle 3.1.3 (JSON parsing)

### Test Dependencies
- ScalaTest 3.2.17
- ScalaCheck 1.17.0 (property-based testing)

---

## Success Criteria

| Criterion | Target | Measurement |
|-----------|--------|-------------|
| Bootstrap time | <60 seconds | Time from execute to completion |
| Framework completeness | 100% | All SBPFs, templates, instructions copied |
| Repository readiness | 100% | Can start Phase 0 immediately |
| GitHub integration | 100% | Repository created, configured, pushed |
| Validation accuracy | 100% | All 7 checks detect issues correctly |
| Independence | 100% | Bootstrapped repo works without copilot-training |

---

## Risk Assessment

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| GitHub API rate limits | Medium | Low | Cache token, use conditional requests |
| Large file copy (slow) | Low | Medium | Progress indicators, parallel copy |
| Template generation errors | High | Low | Comprehensive unit tests |
| Broken references in copied files | High | Medium | Post-copy validation, replace "copilot-training" |
| Missing dependencies in bootstrapped repo | High | Low | Copy mill-*-plugin sources, not references |

---

## Open Questions

### 1. Plugin Versioning
**Question**: Should bootstrapped repos include specific plugin versions or latest?  
**Options**:
- A) Pin to current versions (e.g., mill-spinoff:1.0.0, mill-deploy:0.1.0)
- B) Use "latest" (requires publishing)
- C) Copy plugin source (self-sufficient)

**Recommendation**: **Option C** - Copy plugin sources so bootstrapped repos are independent

### 2. Framework Updates
**Question**: How do bootstrapped projects get framework updates (new SBPFs, templates)?  
**Options**:
- A) Manual (compare with copilot-training periodically)
- B) Semi-automatic (mill bootstrap.sync command)
- C) Automatic (GitHub Actions watches copilot-training)

**Recommendation**: **Option A** for v1.0.0 (manual), consider Option B for v2.0.0

### 3. Organization Customization
**Question**: Should templates support organization-specific branding/links?  
**Options**:
- A) Generic templates (no customization)
- B) Parameterized templates (org name, logo URL, etc.)
- C) Template overrides directory

**Recommendation**: **Option A** for v1.0.0 (keep simple), use search/replace post-bootstrap

---

## Design Decisions (Confirmed)

| Decision | Rationale | Date |
|----------|-----------|------|
| Copy plugin sources (not ivy dependencies) | Bootstrapped repos must be self-sufficient | 2025-12-16 |
| All validation checks are Blocking | No environment variance for project setup | 2025-12-16 |
| Reuse GitHubClient from mill-spinoff-plugin | DRY principle, proven code | 2025-12-16 |
| Generate stub files (not copy) | Project-specific content needs customization | 2025-12-16 |
| Framework files copied, not symlinked | Repo independence critical | 2025-12-16 |

---

## Next Steps

1. **Phase 1 (Documentation)**: Create governance docs (ADR-064, POL-031), README, templates
2. **Phase 2 (Build)**: Implement plugin components (validator, copier, generator, module)
3. **Phase 3 (Test)**: Write unit tests, integration tests, end-to-end tests
4. **Phase 4 (Publish)**: Publish to GitHub Packages, update copilot-training build.sc
5. **Phase 5 (Dogfood)**: Bootstrap a test project, verify completeness

---

## References

- **mill-spinoff-plugin**: Similar architecture, reuse GitHubClient
- **mill-deploy-plugin**: Similar validation pattern, ceremony-based
- **HOW-WE-WORK.md**: Process this plugin bootstraps
- **ADR-060**: Spinoff plugin decision (comparison)
