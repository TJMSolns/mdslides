# How We Work: Team Playbook
## Complete SDLC Process for All Roles

**Version**: 1.0.0  
**Last Updated**: January 9, 2026  
**Audience**: Program Manager, Product Owner, Architect, Bench Developer

---

## 🎯 Philosophy

We build enterprise systems using a **ceremony-based approach** that blends:
- **DDD (Domain-Driven Design)**: Model the business domain accurately
- **BDD (Behavior-Driven Development)**: Specify behavior with examples
- **TDD (Test-Driven Development)**: Implement with test-first discipline

**Key Principle**: Documentation is code. Everything is Git-tracked, version-controlled, and executable where possible. No external SaaS tools (Miro, Mural, PowerPoint, Google Slides) - everything lives in this repository using:
- **Mermaid** for diagrams
- **Marp** (Markdown Presentation Ecosystem) for presentations/decks
- **Markdown** for all documentation

---

## 👥 Team Roles & Responsibilities

| Role | Primary Responsibility | Key Ceremonies Led |
|------|----------------------|-------------------|
| **Program Manager** | Coordination, dependencies, risk management, documentation sync | Living Documentation Sync, Cross-Boundary Integration Testing |
| **Product Owner** | Business value, requirements, acceptance criteria, domain expertise | Three Amigos, Example Mapping, Ubiquitous Language Workshop (co-lead) |
| **Architect** | Domain modeling, bounded contexts, technical strategy, design decisions | Event Storming, Domain Modeling, Context Mapping, Acceptance Criteria Review |
| **Bench Developer** | Implementation, testing, refactoring, code quality | Test-First Pairing, Red-Green-Refactor, Property-Based Testing |

**All roles** participate in **all ceremonies** - leadership means facilitation, not exclusivity.

---

## 🌀 Sprint Lifecycle Overview

We run a tight sprint cadence with lightweight, high‑signal ceremonies. Two bookend ceremonies frame each sprint, and two planning roles are covered explicitly.

### Pre‑Sprint
- Backlog Refinement (Product Owner): Curate, split, and sequence stories; ensure acceptance criteria are example‑driven and ready.

### Sprint Start
- Sprint Planning (Program Manager): Scope sprint goals, capacity, and Definition of Done alignment.
- Sprint Planning (Scrum Master): Facilitate plan, identify risks, confirm working agreements, and unblock logistics.

### During Sprint
- Execute ceremonies per phase (DDD/BDD/TDD); prioritize test‑first and living documentation. Use mill validation tasks as optional sanity checks after each phase (see Phase-specific validation sections below).

### Post‑Sprint
- Sprint Retrospective (Program Manager): Inspect outcomes, decide improvements, and capture 1–3 actionable items maximum.

References:
- doc/internal/ceremonies/sprint/backlog-refinement.md
- doc/internal/ceremonies/sprint/sprint-planning.md (Program Manager & Scrum Master)
- doc/internal/ceremonies/sprint/sprint-retrospective.md

---

## 🛠️ Ceremony Automation: Mill Tasks

This playbook describes **what we do** (the ceremonies). **How we do it** is accelerated by Mill tasks that automate repetitive validation and scaffolding work. The tools **support** the ceremonies but do not **replace** them.

### Mill Tasks Overview

We use [Mill](https://mill-build.com) as our build tool and SDLC orchestration platform. Mill tasks automate ceremony validation and project scaffolding:

| Task Namespace | Purpose | Ceremonies Supported |
|----------------|---------|---------------------|
| **bootstrap.*** | Project scaffolding, framework copy | Phase 0.1: Project Bootstrap |
| **spinoff.*** | Service extraction, bounded context separation | Phase 1: Context Mapping (service evolution) |
| **validate.specification** | BDD scenario validation (Gherkin, ubiquitous language) | Phase 2: Three Amigos, Example Mapping |
| **validate.domain** | Domain model validation (aggregates, DDD patterns, POL-033) | Phase 1: Event Storming, Domain Modeling, Context Mapping |
| **validate.testing** | TDD enforcement (test-first, coverage, architecture) | Phase 3: Test-First Pairing, Red-Green-Refactor |
| **validate.quality** | Code quality + security scanning | Phase 3: Implementation |
| **validate.release** | Semantic versioning, changelog, release readiness | Phase 4: Release |
| **validate.observability** | OpenTelemetry instrumentation validation | Phase 3: Implementation |
| **validate.deploy** | Kubernetes deployment automation, rollback | Phase 4: Automated Deployment |

**Implementation**: All tasks execute compiled Scala 3.3.1 libraries with non-blocking validation logic, per-check `T.task` targets for parallel execution, and zero dependency on external SaaS tools.

**Key Principle**: Ceremonies are human-first, validation is automation-first. Tools validate artifacts but don't generate them for you.

---

### Two Value Propositions: Ceremony Execution vs CI/CD Automation

Mill validation tasks serve two distinct contexts with different value propositions:

#### 1. Ceremony Execution (Manual-First, Validation Optional)

**Context**: During live ceremonies (Event Storming, Three Amigos, Test-First Pairing), teams focus on **human collaboration and domain understanding**.

**Validation Role**: OPTIONAL sanity check for ceremony outputs. Manual verification (team discussion, visual inspection, walkthrough) is PRIMARY quality gate.

**Why Optional**: Ceremonies proven to produce quality artifacts without automation (see reference projects: mdslides, tenant-management-service). Teams successfully completed all ceremonies using manual verification.

**When to Use**:
- Post-ceremony sanity check (optional)
- Learning tool for new team members
- Quick verification before commit

**Usage Pattern**:
```bash
# After Three Amigos session creates BDD scenarios...
# Manual verification: Team reviews scenarios together (PRIMARY)
# Automation: Optional sanity check (SECONDARY)
mill validate.specification  # Optional: Checks Gherkin syntax, ubiquitous language

# After Domain Modeling creates aggregate docs...
# Manual verification: Architect reviews aggregates with team (PRIMARY)
# Automation: Optional sanity check (SECONDARY)
mill validate.domain    # Optional: Checks aggregate completeness, DDD patterns
```

#### 2. CI/CD Automation (Validation Primary Value)

**Context**: Pre-commit hooks, pull request gates, release checks, deployment pipelines.

**Validation Role**: PRIMARY quality gate enforcing standards across team, preventing regressions, automating reviews.

**Why Valuable**: Catches issues before code review, ensures consistency across team members, automates repetitive checks, provides fast feedback.

**When to Use**:
- Pre-commit hooks (block commits with issues)
- Pull request gates (require checks pass before merge)
- Release gates (ensure quality before deployment)
- CI/CD pipelines (automated quality enforcement)

**Usage Pattern**:
```yaml
# .github/workflows/ci.yml
name: CI Quality Gates
on: [pull_request]

jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Domain Model Validation
        run: mill validate.domain
      - name: Specification Validation
        run: mill validate.specification
      - name: Testing Validation
        run: mill validate.testing
      - name: Code Quality Validation
        run: mill validate.quality
```

**ExecutionContext**:
- **Manual**: Non-blocking, informational feedback
- **PreCommit**: Blocking, prevents commits with violations
- **PullRequest**: Blocking, requires fixes before merge
- **Release**: Blocking, ensures production quality

---

**📖 Complete Reference**:
→ **[MILL-TASKS-REFERENCE.md](doc/internal/reference/MILL-TASKS-REFERENCE.md)** - Definitive guide for all tasks with full examples, usage patterns, and troubleshooting

**Additional Resources**:
- **Vision**: [MILL-AS-SDLC-ORCHESTRATION-PLATFORM.md](doc/internal/planning/MILL-AS-SDLC-ORCHESTRATION-PLATFORM.md) - Why Mill for SDLC
- **Governance**: [ADR-065](doc/internal/governance/ADR/ADR-065-mill-as-sdlc-orchestration-platform.md), [POL-032](doc/internal/governance/POL/POL-032-mill-module-development-standards.md)

**Example Ceremony Workflow** (Manual-First):
```bash
# Step 1: Do ceremony (human collaboration - PRIMARY)
# Event Storming workshop → doc/internal/domain-models/event-storming/context-events.md created

# Step 2: Manual verification (team discussion - PRIMARY QUALITY GATE)
# Team reviews event storming output together, identifies gaps

# Step 3: Optional automation (sanity check - SECONDARY)
mill validate.domain    # Checks technical completeness (optional)

# After Context Mapping creates context map...
mill contextMapValidate # Validates infrastructure vs docker-compose.yml (BUG-003 prevention)

# After TDD implementation...
mill testFirstValidate # Validates test-before-implementation, no production TODOs
```

---

### ⚡ Fast Validation & Performance

When speed matters, run validations in parallel and track performance over time.

- validateAll composition: Prefer the composed target to run domain, specification, testing, quality, and observability validations together in parallel.
- Parallelism: Use the jobs flag to leverage all cores (for example, --jobs 8 on an 8‑core machine).
- Profiling and regression guard: Profile any Mill target and record build metrics to catch slowdowns early.

Example:
```bash
# Run all validations for a service in parallel
mill <service>.validateAll --jobs 8

# Profile a specific target, then record and track build metrics
mill --profile <service>.domain.domainValidate
scripts/track-build-performance.sh  # writes out/build-metrics.csv and flags regressions
```

Notes:
- All validations are implemented as side‑effect‑free Scala 3.3.1 tasks with per‑check T.task targets, so they parallelize safely.
- Use CI to upload build-metrics.csv artifacts and fail the pipeline on regressions.

---

## 📚 SDLC Phases & Ceremonies

### Phase 0.1: Project Bootstrap (NEW)

#### What Happens
- New project repository created using **mill bootstrap.* tasks**
- Complete ceremony framework copied to new repository (~130+ files)
- Project stubs generated (CHARTER.md, ARCHITECTURE.md, README.md, build.sc, .gitignore)
- GitHub repository configured (branch protection, topics)
- Repository ready for Phase 0.0: Program Initiation

#### Prerequisites
- **GitHub Token**: Personal Access Token with `repo` (full) and `admin:org` (read/write) scopes
- **Mill 0.11.6+**: Installed via Coursier (`cs install mill`)
- **Git Configured**: `user.name` and `user.email` set
- **Target Organization**: GitHub organization exists and accessible
- **Project Name**: kebab-case, 3-50 characters, starts with letter

**New Developer Setup**: If this is your first time setting up the development environment, see **[doc/internal/reference/developer-setup.md](doc/internal/reference/developer-setup.md)** for complete installation instructions (Java, Mill, VS Code with Metals extension).

#### How It Works

**Training Repo Mode** (default - learning/exploration):
```bash
# Create in services/ subdirectory
cd copilot-training
mill bootstrap.bootstrapExecute --name my-service
cd services/my-service
```

**Standalone Mode** (production - separate repository):
```bash
# Local-only (no GitHub)
mill bootstrap.bootstrapExecute --name order-mgmt --path ~/projects

# With new GitHub repo
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx
mill bootstrap.bootstrapExecute --name order-mgmt --path ~/projects --create-repo --org RETISIO
git clone git@github.com:RETISIO/order-mgmt.git

# Link existing GitHub repo
mill bootstrap.bootstrapExecute --name order-mgmt --repo https://github.com/RETISIO/order-mgmt
```

**Key Differences**:
- **Training mode**: Uses shared Mill tasks from parent repo (../mill-*-plugin directories)
- **Standalone mode**: Copies all Mill task implementations to mill-modules/ directory (self-contained)
- **GitHub**: Optional in both modes (--create-repo or --repo flags)

#### What Gets Bootstrapped
- **HOW-WE-WORK.md** (this file - complete SDLC playbook)
- **27 Ceremony Instructions** (doc/internal/ceremonies/)
- **50+ Shared Best Practice Files** (doc/internal/reference/SBPF/)
- **33+ Document Templates** (doc/internal/reference/templates/)
- **3 Cross-Phase Validation Checklists** (doc/internal/reference/validation/)
- **All 9 Mill Tasks** (self-contained, bundled for complete autonomy):
  - mill-bootstrap (project scaffolding)
  - mill-domain (DDD validation)
  - mill-specification (BDD scenario validation)
  - mill-testing (TDD enforcement)
  - mill-quality (code quality & security)
  - mill-release (semantic versioning & release)
  - mill-observability (OpenTelemetry validation)
  - mill-deploy (Kubernetes deployment)
  - mill-spinoff (bounded context extraction)
- **8 Framework Governance Files** (doc/governance/ADR + POL):
  - 5 Framework ADRs
  - 3 Framework POLs
- **Project Stubs**: CHARTER.md, ARCHITECTURE.md, README.md, build.sc, .gitignore, GOVERNANCE-BACKLOG.md

#### Artifacts Created
| Template | Created By | Output Location |
|----------|-----------|------------------|
| `CharterTemplate` | bootstrap.bootstrapExecute | `CHARTER.md` (root, stub with TODOs) |
| `ArchitectureTemplate` | bootstrap.bootstrapExecute | `ARCHITECTURE.md` (root, stub) |
| `ReadmeTemplate` | bootstrap.bootstrapExecute | `README.md` (root, stub) |
| `RootBuildScTemplate` | bootstrap.bootstrapExecute | `build.sc` (root, Mill configuration) |
| `GitignoreTemplate` | bootstrap.bootstrapExecute | `.gitignore` (root, Mill/JVM patterns) |
| Framework files | bootstrap.bootstrapExecute | ~110 files copied from copilot-training |

#### Ceremonies
None - automated via tasks (see doc/internal/reference/MILL-TASKS-REFERENCE.md for bootstrap.*)

#### References
- **ADR-064**: Bootstrap via Mill Module (decision to use task-based approach)
- **MILL-TASKS-REFERENCE.md**: Complete task documentation including bootstrap (see doc/internal/reference/)
- **mill-bootstrap/README.md**: Task implementation details (see mill-modules/)

---

### Phase 0.0: Program Initiation

#### What Happens
- Program charter completed (`CHARTER.md` - fill in TODOs from bootstrap)
- Service breakdown identified
- High-level roadmap established
- Stakeholder alignment

#### Artifacts Created
| Template | Created By | Output Location |
|----------|-----------|-----------------|
| `PROJECT-CHARTER-TEMPLATE.md` | Program Manager + Architect | `CHARTER.md` (complete TODOs from bootstrap stub) |
| `PROJECT-CANVAS-TEMPLATE.md` | Program Manager | `PROJECT-CANVAS.md` (root) |

#### Ceremonies
None - this is planning, not discovery.

---

### Phase 1: Discovery (DDD-Led)

**Goal**: Understand the domain, establish shared vocabulary, identify boundaries.

#### 1.1 Event Storming Session

**Purpose**: Map domain's temporal flow to identify events, commands, aggregates, and hotspots.

**Led by**: Architect  
**Supported by**: Product Owner (domain expertise), Bench Developer (feasibility), Program Manager (dependencies)

**When**: Once per bounded context (initial), quarterly (refinement)

**How It Works**:
1. **Gather team** (all 4 roles + domain experts if available)
2. **Identify domain events** (things that happened): "TenantProvisioned", "OrderPlaced"
3. **Discover commands** (intentions): "ProvisionTenant", "PlaceOrder"
4. **Group events** into temporal flows
5. **Identify aggregates** (entities handling commands): "Tenant", "Order"
6. **Surface questions** and hotspots for deeper exploration
7. **Identify bounded contexts** (linguistic boundaries)

**Artifacts Created**:
- `doc/internal/domain-models/event-storming/[context-name]-events.md` (using `EVENT-STORMING-TEMPLATE.md`)
- Mermaid timeline diagram showing event flow
- Aggregate candidates list

**Common Pitfalls**:
- Modeling existing system instead of domain
- Skipping domain experts (Product Owner input critical)
- Jumping to technical solutions too early

---

#### 1.2 Ubiquitous Language Workshop

**Purpose**: Formalize shared vocabulary for code, scenarios, and conversation.

**Led by**: Architect + Product Owner (co-led)  
**Supported by**: Bench Developer (implementation constraints), Program Manager (cross-team conflicts)

**When**: Bi-weekly during active development, monthly during maintenance

**How It Works**:
1. **Extract terms** from event storming session
2. **Define each term** with examples (entities, value objects, commands, events)
3. **Resolve conflicts** (same term, different meanings across teams)
4. **Ban problematic terms** (e.g., "Account" → use "Tenant" or "BillingAccount" specifically)
5. **Commit to glossary** - these terms MUST appear in code and scenarios

**Artifacts Created**:
- `doc/internal/domain-models/ubiquitous-language.md` (using `UBIQUITOUS-LANGUAGE-TEMPLATE.md`)
- Terminology policy if cross-team conflicts resolved

**Common Pitfalls**:
- Technical jargon creep (DTO, repository, etc.)
- Creating glossary once, never updating it
- Synonym proliferation across teams

---

#### 1.3 Domain Modeling Workshop

**Purpose**: Define aggregates, entities, value objects, and business invariants.

**Led by**: Architect  
**Supported by**: Bench Developer (testability), Product Owner (business rules)

**When**: Once per epic, revisited when TDD reveals design friction

**How It Works**:
1. **Start with aggregate candidates** from event storming
2. **Define aggregate boundaries** (what's inside, what's outside)
3. **Identify entities vs value objects**
4. **Document invariants** (business rules that must always hold)
5. **Model state transitions** (Mermaid state machines)
6. **Define commands and events** per aggregate

**Artifacts Created**:
- `doc/internal/domain-models/aggregates/[aggregate-name]-aggregate.md` (using `AGGREGATE-TEMPLATE.md`)
- Mermaid class diagrams and state machines
- Business rules documentation

**Common Pitfalls**:
- Anemic domain models (just data holders)
- Aggregates too large (performance issues)
- Aggregates too small (consistency issues)

---

#### 1.4 Context Mapping

**Purpose**: Define relationships between bounded contexts and integration patterns.

**Led by**: Architect  
**Supported by**: Program Manager (dependencies), Bench Developer (integration complexity)

**When**: After domain modeling, revisited when boundaries change

**How It Works**:
1. **Identify all bounded contexts**
2. **Map relationships** (upstream/downstream)
3. **Choose integration patterns**:
   - Published Language (domain events)
   - Anticorruption Layer (protect from external models)
   - Conformist (small downstream adapts to upstream)
   - Shared Kernel (same team, shared code)
4. **Define contracts** between contexts

**Artifacts Created**:
- `doc/internal/domain-models/context-maps/system-context-map.md` (using `CONTEXT-MAP-TEMPLATE.md`)
- `doc/internal/architecture/integration-contracts/[upstream]-to-[downstream].md` (using `INTEGRATION-CONTRACT-TEMPLATE.md`)
- Mermaid context map diagram

**Common Pitfalls**:
- Contexts share database (breaks boundaries)
- Direct dependencies between contexts
- Unclear ownership

---

#### 1.5 Domain Model Governance Verification

**Purpose**: Validate domain model artifacts meet DDD standards and governance requirements before Phase 2.

**Led by**: Architect  
**Supported by**: Program Manager (governance audit), Bench Developer (implementation feasibility)

**When**: After Context Mapping (1.4), before Phase 2 ceremonies

**How It Works**:
1. **Run validation checklist**: Use [PHASE-1-TO-2-VALIDATION.md](doc/internal/reference/validation/PHASE-1-TO-2-VALIDATION.md)
2. **Verify DDD patterns**: All aggregates, entities, value objects per POL-033
3. **Verify ubiquitous language coverage**: All domain terms from CHARTER.md in glossary
4. **Verify Mermaid diagrams**: Event timelines, state machines, context maps present
5. **Document verification results**: Create retrospective artifact

**Artifacts Created**:
- `doc/internal/retrospectives/phase1-to-phase2-validation-[sprint-id].md`
- Updated domain model if gaps found

**Common Pitfalls**:
- Skipping validation under time pressure
- Not updating ubiquitous language when new terms discovered
- Approving domain model without CHARTER.md alignment check

---

### Phase 2: Specification (BDD-Led)

**Goal**: Convert domain knowledge into executable specifications with concrete examples.

#### Prerequisites for Phase 2 Ceremonies

**CRITICAL**: Before starting any Phase 2 ceremony, the team MUST:

1. **Read CHARTER.md Section 3** (Problem Statement, Solution Overview, Scope) to understand project boundaries
2. **Identify all entities and content types** mentioned in scope (products, guides, manuals, FAQs, etc.)
3. **Review relevant ADRs** from Phase 1 (especially data model, schema, and domain decisions)
4. **Validate ubiquitous language** includes all entities and content types from CHARTER.md

**Artifacts Required**:
- ✅ CHARTER.md completed and approved
- ✅ Ubiquitous Language glossary includes all domain terms from CHARTER.md
- ✅ Domain model aggregates cover all entities mentioned in CHARTER.md scope
- ✅ Use [CHARTER Validation Checklist](doc/internal/reference/validation/CHARTER-VALIDATION-CHECKLIST.md) to verify readiness

**Why This Matters**: Phase 2 ceremonies create executable specifications. If these specs don't align with CHARTER.md scope, all implementation work will be wasted. This prerequisite prevents scope drift and domain modeling errors.

---

#### 2.1 Three Amigos Specification Session

**Purpose**: Collaboratively write BDD scenarios before implementation.

**Led by**: Product Owner  
**Supported by**: Architect (domain alignment), Bench Developer (implementability)

**When**: Per user story, before implementation starts

**How It Works**:
0. **Review CHARTER.md scope** (NEW - CRITICAL) - Validate user story is within defined boundaries, identify all entities and content types that should appear in scenarios
1. **Review user story** together
2. **Write Given/When/Then scenarios** in plain language using all entities from CHARTER.md
3. **Use ubiquitous language** from glossary (validate all CHARTER.md terms are in glossary)
4. **Identify missing rules** or unclear acceptance criteria
5. **Validate with domain model** (Architect checks alignment)
6. **Validate with CHARTER.md scope** (NEW - CRITICAL) - Ensure scenarios cover all relevant entities and content types from scope

**Artifacts Created**:
- `features/[feature-name].feature` (using `FEATURE-TEMPLATE.feature`)
- Gherkin scenarios with Given/When/Then

**Optional Validation** (Quick Sanity Check):
- Run `mill validate.specification --feature features/[feature-name].feature` to check:
  - Gherkin syntax correctness
  - Ubiquitous language compliance (terms from glossary)
  - Scenario completeness (all Given/When/Then present)
- **Note**: This is a quick automated check, NOT a replacement for manual team review and CHARTER.md validation

**Common Pitfalls**:
- **Not checking CHARTER.md scope** (NEW) - leads to domain modeling errors and scope drift
- Writing scenarios without domain context
- UI-focused scenarios (test through domain, not UI)
- Vague scenarios without concrete examples
- Missing entities or content types defined in CHARTER.md

---

#### 2.2 Example Mapping

**Purpose**: Use concrete examples to discover business rules before writing scenarios.

**Led by**: Product Owner  
**Supported by**: Architect (rule consistency), Bench Developer (edge cases)

**When**: Per complex user story, before Three Amigos session

**How It Works**:
0. **Review CHARTER.md scope** (NEW - CRITICAL) - Understand feature boundaries and identify all entities/content types mentioned in scope that relate to this story
1. **Write user story** on a card (ensure it references CHARTER.md scope)
2. **Extract business rules** (blue cards) - include rules for all relevant entities from CHARTER.md
3. **Provide concrete examples** for each rule (green cards) - use all content types from CHARTER.md where applicable
4. **Surface questions** (pink cards) - especially questions about scope boundaries from CHARTER.md
5. **Resolve questions** with Product Owner (refer to CHARTER.md for answers)
6. **Prioritize scenarios** to implement
7. **Validate completeness** (NEW) - Check that all entities and content types from CHARTER.md scope are covered by examples

**Artifacts Created**:
- `doc/internal/scenarios/example-maps/[story-id]-examples.md` (using `EXAMPLE-MAP-TEMPLATE.md`)
- Rules → Examples → Questions mapping

**Optional Validation** (Quick Sanity Check):
- Run `mill validate.specification --example-maps doc/internal/scenarios/example-maps/[story-id]-examples.md` to check:
  - Example map structure completeness (story, rules, examples, questions sections)
  - Rule-to-example coverage (each rule has at least one example)
  - Question tracking (all questions marked as resolved or unresolved)
- **Note**: This is a quick automated check, NOT a replacement for manual team review and CHARTER.md validation

**Common Pitfalls**:
- **Not reviewing CHARTER.md first** (NEW) - leads to incomplete examples that miss entities or content types
- Skipping for "simple" stories (simple stories hide complex rules)
- Not surfacing questions early
- Insufficient examples per rule
- Examples don't cover all content types mentioned in CHARTER.md scope

---

#### 2.3 Acceptance Criteria Review

**Purpose**: Validate scenarios align with domain model and use correct language.

**Led by**: Architect  
**Supported by**: Product Owner (business validation), Bench Developer (implementation clarity)

**When**: After Three Amigos, before implementation

**How It Works**:
1. **Review draft scenarios** from Three Amigos
2. **Check ubiquitous language** usage (all terms from glossary)
3. **Validate against CHARTER.md scope** (NEW - CRITICAL) - Ensure all entities and content types from scope are covered
4. **Validate against domain model** (can model support this behavior?)
5. **Identify model gaps** requiring refinement
6. **Approve scenarios** for implementation OR send back for revision if scope misalignment found

**Artifacts Created**:
- `doc/internal/scenarios/acceptance-criteria/[story-id].md` (using `ACCEPTANCE-CRITERIA-TEMPLATE.md`)
- Approved `.feature` files
- Review notes if changes needed

**Optional Validation** (Quick Sanity Check):
- Run `mill validate.specification --acceptance-criteria doc/internal/scenarios/acceptance-criteria/[story-id].md` to check:
  - Acceptance criteria document structure (story, criteria, BDD scenario references)
  - Cross-references to feature files are valid (all scenario links exist)
  - Approval metadata present (approved_by, approved_date)
- **Note**: This is a quick automated check, NOT a replacement for manual Architect review and CHARTER.md validation

**Common Pitfalls**:
- **Not validating against CHARTER.md scope** (NEW) - approving scenarios that don't cover all entities/content types
- Rubber-stamp approval without checking language
- Late discovery of model misalignment
- Skipping under time pressure
- Approving scenarios before checking CHARTER validation checklist

---

#### 2.4 Specification Governance Verification

**Purpose**: Validate BDD scenarios meet specification standards and governance requirements before Phase 3.

**Led by**: Architect  
**Supported by**: Product Owner (business validation), Bench Developer (testability)

**When**: After Acceptance Criteria Review (2.3), before Phase 3 ceremonies

**How It Works**:
1. **Run validation checklist**: Use [PHASE-2-TO-3-VALIDATION.md](doc/internal/reference/validation/PHASE-2-TO-3-VALIDATION.md)
2. **Verify scenario coverage**: All examples from example maps have BDD scenarios
3. **Verify ubiquitous language usage**: All scenarios use terms from glossary
4. **Verify domain model alignment**: All aggregates tested in scenarios
5. **Document verification results**: Create retrospective artifact

**Artifacts Created**:
- `doc/internal/retrospectives/phase2-to-phase3-validation-[sprint-id].md`
- Updated BDD scenarios if gaps found

**Common Pitfalls**:
- Skipping validation to start coding faster
- Not resolving all example map questions before proceeding
- Approving scenarios without test data preparation

---

### Phase 3: Implementation (TDD-Led)

**Goal**: Implement domain behavior through test-first development.

#### 3.1 Test-First Pairing Session

**Purpose**: Write failing tests for domain invariants before implementing aggregates.

**Led by**: Bench Developer  
**Supported by**: Architect (domain model guidance), occasionally Product Owner (rule clarification)

**When**: Daily during active development

**How It Works**:
1. **Choose failing BDD scenario** to implement
2. **Identify aggregate** that needs changes
3. **Write failing unit test** for aggregate behavior
4. **Implement minimum code** to pass test
5. **Refactor** while maintaining passing tests
6. **Repeat** until BDD scenario passes

**Artifacts Created**:
- Unit tests in `src/test/`
- Implementation code in `src/main/`

**Common Pitfalls**:
- Testing implementation details, not behavior
- Test names don't use domain language
- Skipping TDD under pressure

---

#### 3.2 Red-Green-Refactor Cycle

**Purpose**: Implement minimum code to pass tests, then refactor for design.

**Led by**: Bench Developer  
**Supported by**: Architect (refactoring guidance)

**When**: Continuous during implementation

**How It Works**:
1. **Red**: Write failing test
2. **Green**: Write minimum code to pass (can be ugly)
3. **Refactor**: Improve design without breaking tests
4. **Commit**: Small, frequent commits

**Artifacts Created**:
- Git commits with clear messages
- Refactored production code

**Common Pitfalls**:
- Skipping refactor step (debt accumulates)
- Over-engineering during green phase
- Refactoring away domain concepts

---

#### 3.3 Property-Based Test Design Session

**Purpose**: Use generative testing to discover edge cases and validate invariants.

**Led by**: Bench Developer + Architect (co-led)  
**Supported by**: Product Owner (validate discovered edge cases)

**When**: Per aggregate after initial TDD tests pass

**How It Works**:
1. **Identify invariants** from domain model
2. **Write property-based tests** (e.g., with jqwik)
3. **Run tests** with random inputs
4. **Analyze failures** (shrinking reveals edge cases)
5. **Add edge cases** to example map and BDD scenarios

**Artifacts Created**:
- Property-based tests in `src/test/`
- Updates to example maps with new edge cases

**Common Pitfalls**:
- Testing framework APIs instead of domain rules
- Ignoring failures ("it's just random data")
- Over-constraining generators

---

#### 3.4 Implementation Governance Verification

**Purpose**: Validate implementation meets TDD standards and governance requirements before Phase 4.

**Led by**: Architect + Bench Developer (co-led)  
**Supported by**: Program Manager (governance audit)

**When**: After Property-Based Testing (3.3), before Phase 4 ceremonies

**How It Works**:
1. **Run validation checklist**: Use [PHASE-3-TO-4-VALIDATION.md](doc/internal/reference/validation/PHASE-3-TO-4-VALIDATION.md)
2. **Verify test coverage**: ≥80% line coverage, ≥90% for aggregates
3. **Verify all P0 scenarios passing**: All critical BDD scenarios GREEN
4. **Verify no production TODOs**: Clean production code
5. **Verify observability**: Metrics, logs, traces instrumented
6. **Document verification results**: Create retrospective artifact

**Artifacts Created**:
- `doc/internal/retrospectives/phase3-to-phase4-validation-[sprint-id].md`
- Fixed implementation if gaps found

**Common Pitfalls**:
- Skipping governance check to deploy faster
- Ignoring low test coverage on aggregates
- Not instrumenting observability before production

---

### Phase 4: Integration & Feedback

**Goal**: Close the loop, ensure DDD/BDD/TDD remain aligned.

#### 4.1 Scenario-to-Test Decomposition Workshop

**Purpose**: Trace how BDD scenarios decompose into TDD unit tests.

**Led by**: Architect  
**Supported by**: Bench Developer (test coverage), Product Owner (behavioral completeness)

**When**: Per complex scenario, or when acceptance tests fail unexpectedly

**How It Works**:
1. **Pick failing BDD scenario**
2. **Identify which aggregates** are involved
3. **Map unit tests** to scenario steps
4. **Identify gaps** (missing tests or wrong tests)
5. **Add missing unit tests**

**Artifacts Created**:
- Test coverage map
- Additional unit tests

**Common Pitfalls**:
- Unit tests pass but scenario fails (collaboration issue)
- Redundant testing
- Unclear test ownership

---

#### 4.2 Domain Model Retrospective

**Purpose**: Reflect on whether code matches domain model.

**Led by**: Architect  
**Supported by**: Bench Developer (pain points), Product Owner (business alignment)

**When**: End of sprint, or when TDD reveals persistent friction

**How It Works**:
1. **Review implemented code** vs domain model
2. **Identify friction** (hard-to-test code, unclear names)
3. **Propose model refinements**
4. **Update ubiquitous language** if needed
5. **Create refactoring backlog**

**Artifacts Created**:
- `doc/internal/retrospectives/[sprint-id]-domain-model-retro.md`
- Updates to domain model diagrams
- ADRs if major architectural changes

**Common Pitfalls**:
- Blaming the domain ("it's too complex")
- Ignoring test pain
- Skipping under deadline pressure

---

#### 4.3 Living Documentation Sync

**Purpose**: Keep scenarios, diagrams, and glossary synchronized with code.

**Led by**: Program Manager  
**Supported by**: Architect (diagrams), Bench Developer (scenarios), Product Owner (acceptance criteria)

**When**: Bi-weekly, or after major domain changes

**How It Works**:
1. **Review changed BDD scenarios**
2. **Update domain diagrams** if needed
3. **Update ubiquitous language** glossary
4. **Archive deprecated scenarios**
5. **Cross-reference documentation**

**Artifacts Created**:
- Git commits updating all affected docs
- Changelog summary
- Archived scenarios in `doc/internal/scenarios/deprecated/`

**Common Pitfalls**:
- Documentation drift
- Nobody owns the update process
- Version skew

---

#### 4.4 Cross-Boundary Integration Testing

**Purpose**: Validate anti-corruption layers and context contracts.

**Led by**: Program Manager + Architect (co-led)  
**Supported by**: Bench Developers from both contexts, Product Owner (end-to-end scenarios)

**When**: Per program increment, or when contracts change

**How It Works**:
1. **Review context map** and integration contracts
2. **Write integration tests** (not UI tests!)
3. **Validate ACLs** work correctly
4. **Test failure scenarios** (upstream down, timeout, etc.)
5. **Verify contract tests** pass on both sides

---

#### 4.5 Automated Deployment Ceremony

**Purpose**: Deploy validated services to target environments with automated checks and rollback.

**Led by**: Platform Team (Tech Lead for staging/production)  
**Supported by**: Bench Developer (deployment readiness), Product Owner (business approval for production), Program Manager (coordination)

**When**: Continuous for dev, scheduled for staging/production

**How It Works**:
1. **List deployment targets**: `mill <service>.deployList` (shows current/desired versions)
2. **Validate deployment readiness**: `mill <service>.deployValidate <Target>` (15 checks)
3. **Obtain approvals** (if required):
   - Staging: 1 Tech Lead approval via GitHub issue
   - Production: 1 Tech Lead + 1 Product Owner approval via GitHub issue
4. **Execute deployment**: `mill <service>.deployExecute <Target>`
   - Builds Docker image
   - Pushes to container registry
   - Applies Kubernetes manifests (via Kustomize)
   - Waits for rollout completion
   - Runs smoke tests
   - Monitors for 5 minutes (error rate, pod health, memory)
5. **Automatic rollback** if failures detected:
   - Smoke tests fail (HTTP non-200)
   - Pods crash (3+ restarts)
   - Error rate spike (>5% for 2 minutes)
   - Memory leak (>90% for 3 minutes)
6. **Record deployment** in `DEPLOY-TARGETS.md` (version, status, duration, approvals)

**Artifacts Created**:
- Updated `DEPLOY-TARGETS.md` with deployment history
- Docker image in container registry (ghcr.io/retisio/[service]:[version])
- Kubernetes manifests applied to cluster
- Deployment metrics (Prometheus, Grafana)
- Slack notifications (deployments channel)

**Validation Levels**:
- **Local** (4 checks): Tests, Docker build, coverage (warning)
- **Dev** (10 checks): + Security scan, K8s manifests, migrations, secrets, quotas
- **Staging** (15 checks): + Property tests, contract tests, load tests, observability, approval
- **Production** (15 checks + 2 approvals): Same as staging with stricter thresholds

**Common Pitfalls**:
- Deploying without validation (`mill <service>.deployValidate` first!)
- Missing approvals for staging/production (create GitHub issue)
- Non-backward-compatible database migrations (causes rollback)
- Skipping smoke tests (use `/health` endpoint)
- Manual `kubectl apply` (policy violation, use plugin only)

**Related Documentation**:
- **Policy**: [POL-030: Mill Deploy Plugin Usage Policy](doc/internal/governance/POL/POL-030-mill-deploy-usage.md)
- **Architecture**: [ADR-061: Deploy Validation Criteria Selection](doc/internal/governance/ADR/ADR-061-deploy-validation-criteria-selection.md)
- **Architecture**: [ADR-062: Kubernetes Integration Strategy](doc/internal/governance/ADR/ADR-062-kubernetes-integration-strategy.md)
- **Architecture**: [ADR-063: Deployment Rollback Mechanism](doc/internal/governance/ADR/ADR-063-deployment-rollback-mechanism.md)
- **Template**: [DEPLOY-TARGETS.md Template](doc/internal/reference/templates/DEPLOY-TARGETS-TEMPLATE.md)
- **Plugin Usage**: [mill-deploy README](mill-modules/mill-deploy/README.md)

**Target Metrics**:
- **Deployment Frequency**: Multiple per day (dev), daily (staging), weekly (production)
- **Deployment Duration**: <2 min (local), <5 min (dev), <10 min (staging/production)
- **Rollback Rate**: <5% (automatic rollback on failure)
- **MTTR (Mean Time to Recovery)**: <2 min (P95) via automatic rollback

**Artifacts Created**:
- Integration test suite
- Contract test specifications (Pact, Spring Cloud Contract)
- Updated integration contracts if issues found

**Common Pitfalls**:
- Testing through UI
- Tight coupling between contexts
- Flaky tests

---

## 🔄 Cross-Phase Validation

**Purpose**: Ensure continuity and traceability across SDLC phases, preventing artifacts from drifting out of sync.

**Why This Matters**: Each phase builds on the previous phase's artifacts. If Phase 2 (Specification) doesn't cover all aggregates from Phase 1 (Discovery), we'll have untested domain behavior. If Phase 3 (Implementation) doesn't implement all scenarios from Phase 2, we'll have incomplete features.

---

### Phase 1 → Phase 2 Validation

**Trigger**: Before starting Phase 2 ceremonies (Example Mapping, Three Amigos)

**Validation**:
1. **Example Map Coverage**
   - [ ] Example Map includes rules for ALL aggregates from Phase 1 domain model
   - [ ] Example Map covers ALL entities mentioned in CHARTER.md scope
   - [ ] Example Map references ALL content types (products, guides, manuals, FAQs, etc.)

2. **Ubiquitous Language Consistency**
   - [ ] All terms in Example Map exist in Phase 1 Ubiquitous Language glossary
   - [ ] No new domain terms introduced without updating glossary first

3. **CHARTER.md Alignment**
   - [ ] Use [CHARTER Validation Checklist](doc/internal/reference/validation/CHARTER-VALIDATION-CHECKLIST.md) Section 5.1
   - [ ] Verify all in-scope features from CHARTER.md have corresponding examples
   - [ ] Verify no out-of-scope features appear in Example Map

**How to Validate**:
- Cross-reference Example Map rules with `doc/internal/domain-models/aggregates/*.md`
- Cross-reference Example Map entities with CHARTER.md Section 3 (Scope)
- Optionally run: `mill validate.traceability --phase1-to-phase2`

**If Validation Fails**:
- **Missing aggregates**: Add rules/examples to Example Map for missing aggregates
- **Missing entities**: Add rules/examples for missing content types from CHARTER.md
- **New terms**: Update Ubiquitous Language glossary before proceeding

---

### Phase 2 → Phase 3 Validation

**Trigger**: Before starting Phase 3 (TDD Implementation)

**Validation**:
1. **BDD Scenario Coverage**
   - [ ] BDD scenarios cover ALL examples from Example Map
   - [ ] BDD scenarios test ALL rules from Example Map
   - [ ] BDD scenarios resolve ALL questions from Example Map

2. **Acceptance Criteria Completeness**
   - [ ] Acceptance Criteria document references ALL BDD scenarios
   - [ ] All P0 (critical) examples from Example Map have corresponding P0 BDD scenarios
   - [ ] All P1 (medium) examples from Example Map have corresponding P1 BDD scenarios

3. **Test Data Alignment**
   - [ ] BDD Background section includes test fixtures for ALL content types
   - [ ] Test data covers edge cases identified in Example Map questions

**How to Validate**:
- Cross-reference `features/*.feature` scenarios with Example Map examples
- Count scenarios by priority (P0, P1, P2) and compare to Example Map priorities
- Optionally run: `mill validate.traceability --phase2-to-phase3`

**If Validation Fails**:
- **Missing scenarios**: Add BDD scenarios for uncovered examples
- **Missing test data**: Add Background fixtures for missing content types
- **Unresolved questions**: Resolve Example Map questions before implementing

---

### Phase 3 → Phase 4 Validation

**Trigger**: During Phase 4.1 (Scenario-to-Test Decomposition Workshop)

**Validation**:
1. **Unit Test Coverage**
   - [ ] Unit tests cover ALL BDD scenarios (map each scenario to unit tests)
   - [ ] Unit tests cover ALL aggregates involved in BDD scenarios
   - [ ] Unit tests cover ALL invariants and business rules from domain model

2. **Test Pyramid Balance**
   - [ ] Unit tests (fast): 70-80% of total tests
   - [ ] Integration tests (medium): 15-20% of total tests
   - [ ] BDD acceptance tests (slow): 5-10% of total tests

3. **Behavior Implementation**
   - [ ] All P0 BDD scenarios are GREEN (passing)
   - [ ] All P1 BDD scenarios are GREEN or have documented deferral reason
   - [ ] All edge cases from Example Map are tested

**How to Validate**:
- Run test coverage report: `mill <module>.coverage`
- Cross-reference unit test file names with BDD scenario names
- Optionally run: `mill validate.traceability --phase3-to-phase4`

**If Validation Fails**:
- **Missing unit tests**: Add tests for uncovered scenarios (TDD Red-Green-Refactor)
- **Failing scenarios**: Fix implementation until BDD scenarios pass
- **Low coverage**: Add unit tests for untested aggregates

---

### Continuous Cross-Phase Validation

**During Sprint**:
- **Daily**: Check that new code doesn't break BDD scenarios (CI pipeline)
- **Weekly**: Review test coverage trends (are we maintaining 80%+ unit test coverage?)
- **Per Story**: Use this cross-phase validation as Definition of Done checklist

**Tools**:
- `mill validate.traceability` - Automated cross-phase validation
- `mill validate.specification` - BDD scenario validation
- `mill <module>.coverage` - Test coverage reports
- [CHARTER Validation Checklist](doc/internal/reference/validation/CHARTER-VALIDATION-CHECKLIST.md) - Manual validation

**Common Cross-Phase Drift Patterns**:
1. **Orphaned Aggregates**: Domain model has aggregates not covered by BDD scenarios
2. **Orphaned Scenarios**: BDD scenarios exist but have no unit tests
3. **Stale Examples**: Example Map not updated when domain model changes
4. **Test Duplication**: Unit tests duplicate BDD scenario logic (wrong abstraction level)

---

## 📋 Template Usage Guide

For every artifact we create, we use templates. See `doc/internal/reference/templates/` for full list.

### Charter vs Canvas

Every major artifact has TWO representations:

| Type | Length | Audience | Usage | Update Frequency |
|------|--------|----------|-------|------------------|
| **Charter** | 10-50 pages | Team members needing full context | Discovery, architecture reviews, onboarding | Major changes only |
| **Canvas** | 1-2 pages | Anyone needing quick answers | Standups, API lookups, quick decisions | Weekly or as metrics change |

### When to Use Which Template

| Task | Template | Role Responsible | Output Location |
|------|----------|------------------|------------------|
| Sprint kickoff | `SPRINT-KICKOFF-TEMPLATE.md` (Marp) | Program Manager | `doc/presentations/` |
| Sprint retrospective | `SPRINT-RETROSPECTIVE-TEMPLATE.md` (Marp) | Program Manager | `doc/presentations/` |
| Ceremony demo | `CEREMONY-DEMO-TEMPLATE.md` (Marp) | Product Owner / Architect | `doc/presentations/` |
| New program | `PROJECT-CHARTER-TEMPLATE.md` + `PROJECT-CANVAS-TEMPLATE.md` | Program Manager + Architect | Root |
| New service | `SERVICE-CHARTER-TEMPLATE.md` + `MICROSERVICE-CANVAS-TEMPLATE.md` | Architect | `doc/services/<service>/` |
| Event storming | `EVENT-STORMING-TEMPLATE.md` | Architect | `doc/internal/domain-models/event-storming/` |
| Domain aggregate | `AGGREGATE-TEMPLATE.md` | Architect | `doc/internal/domain-models/aggregates/` |
| Ubiquitous language | `UBIQUITOUS-LANGUAGE-TEMPLATE.md` | Architect + Product Owner | `doc/internal/domain-models/` |
| Context map | `CONTEXT-MAP-TEMPLATE.md` | Architect | `doc/internal/domain-models/context-maps/` |
| Integration contract | `INTEGRATION-CONTRACT-TEMPLATE.md` | Architect + Program Manager | `doc/internal/architecture/integration-contracts/` |
| Example mapping | `EXAMPLE-MAP-TEMPLATE.md` | Product Owner | `doc/internal/scenarios/example-maps/` |
| Acceptance criteria | `ACCEPTANCE-CRITERIA-TEMPLATE.md` | Product Owner | `doc/internal/scenarios/acceptance-criteria/` |
| BDD scenario | `FEATURE-TEMPLATE.feature` | Product Owner + Architect + Bench Developer | `features/` |
| Policy | `POL-TEMPLATE.md` | Program Manager | `doc/internal/governance/POL/` |
| Product decision | `PDR-TEMPLATE.md` | Product Owner | `doc/internal/governance/PDR/` |
| Architecture decision | `ADR-TEMPLATE.md` | Architect | `doc/internal/governance/ADR/` |
| Sprint retrospective (notes) | `RETROSPECTIVE-TEMPLATE.md` | Program Manager | `doc/internal/planning/sprints/` |

---

## 🔄 Typical Sprint Flow

### Week 1: Discovery & Specification

**Monday**: Event Storming (if new epic) or Ubiquitous Language Workshop  
**Tuesday**: Domain Modeling Workshop  
**Wednesday**: Three Amigos for Sprint stories  
**Thursday**: Example Mapping for complex stories  
**Friday**: Acceptance Criteria Review

**Deliverables**: Approved BDD scenarios, domain model updates

**Ceremony Artifacts**:
- Sprint Kickoff Deck: `doc/presentations/sprint-[X]-kickoff.md` (Marp)
- Event Storming: Mermaid timelines
- Domain Model: Mermaid class diagrams + state machines
- BDD Scenarios: Gherkin `.feature` files

---

### Week 2: Implementation

**Monday-Thursday**: Test-First Pairing (daily)  
**Daily**: Red-Green-Refactor cycles  
**Mid-week**: Property-Based Test Design (for critical aggregates)  
**Friday**: Scenario-to-Test Decomposition (if any scenarios fail)

**Deliverables**: Working software with passing tests

---

### Week 3: Integration & Feedback

**Monday**: Cross-Boundary Integration Testing (if multiple contexts)  
**Tuesday**: Domain Model Retrospective  
**Wednesday**: Living Documentation Sync  
**Thursday**: Sprint Review (demo to stakeholders - use `CEREMONY-DEMO-TEMPLATE.md`)  
**Friday**: Sprint Retrospective (use `SPRINT-RETROSPECTIVE-TEMPLATE.md` for presentation)

**Deliverables**: Updated documentation, retrospective action items

**Ceremony Artifacts**:
- Sprint Retrospective Deck: `doc/presentations/sprint-[X]-retrospective.md` (Marp)
- Demo Deck (if needed): `doc/presentations/[feature]-demo.md` (Marp)
- Retrospective Notes: `doc/internal/planning/sprints/sprint-[X]-retro.md` (detailed notes)
- Action Items: Tracked in project board with max 3 per retrospective (POL-036)

---

## 🛠️ Technology Stack (Java Enterprise)

### Core Stack (Non-Negotiable)
- **Java 21+** (Virtual Threads, Records, Pattern Matching)
- **Pekko** (Actors, HTTP, Streams, Cluster, Persistence)
- **Kafka** (Event Streaming)
- **PostgreSQL** (Reactive persistence)
- **Karate** (BDD API Testing)
- **JUnit 5** (Unit Tests)
- **OpenTelemetry** (Observability)

### Rejected Technologies
- ❌ Spring Boot (too complex, annotation-heavy)
- ❌ Redis (Postgres handles caching)
- ❌ Blocking I/O (non-blocking everything)

### Key Principles
1. **Non-blocking everything**: If it blocks, it's wrong
2. **Actor-based concurrency**: Use Pekko actors for business logic
3. **Event-driven architecture**: CQRS, Event Sourcing with Pekko Persistence
4. **Reactive persistence**: Reactive Postgres drivers, no JDBC
5. **API-first testing**: Karate for BDD scenarios

See `doc/exhibits/Java-Enterprise-Best-Practices-2.md` for detailed technical guidance.

---

## 📊 Governance & Metrics

### Decision Records

Use templates for all major decisions:
- **POL**: Policies (company-wide rules)
- **PDR**: Product Decisions (business choices)
- **ADR**: Architectural Decisions (technical choices)

### OKRs (Objectives & Key Results)

Track at program and team level:
- **Program OKRs**: Quarterly, set by Program Manager + Product Owner
- **Team OKRs**: Sprint-level, derived from program OKRs

### SLA/SLO Tracking

Every service defines:
- **SLA**: Service Level Agreement (external commitment)
- **SLO**: Service Level Objective (internal target)
- **SLI**: Service Level Indicator (actual measurement)

Example:
- SLO: 99.9% availability
- SLI: Measured via OpenTelemetry + Prometheus
- SLA: 99.5% (with buffer)

---

## 🚀 Getting Started

### New Team Member Onboarding

**Day 1-3: Read Core Docs**
1. This document (`HOW-WE-WORK.md`)
2. Master charter (`CHARTER.md`)
3. Architecture overview (`ARCHITECTURE.md`)
4. Ceremony guide (`doc/internal/reference/SBPF/Blending-DDD-BDD-TDD.md`)

**Week 1: Shadow Ceremonies**
- Attend all 4 ceremonies as observer
- Ask questions, take notes
- Review ceremony artifacts

**Week 2: Participate**
- Active participation in ceremonies
- Pair with Bench Developer (if dev) or shadow Product Owner (if PO)

**Week 3: Lead**
- Co-lead a ceremony with mentor
- Create first artifact using templates

### New Project Setup

1. **Program Manager**: Create `PROJECT-CHARTER` + `PROJECT-CANVAS`
2. **Architect**: Set up `doc/` structure, add templates
3. **Team**: Run Event Storming for first bounded context
4. **All**: Establish ubiquitous language
5. **Architect + Bench Developer**: Set up CI/CD, tech stack
6. **Product Owner**: Write first user stories
7. **Team**: Run Three Amigos for first story
8. **Bench Developer**: Implement with TDD

---

## 📚 Key References

- **Ceremony Guide**: `doc/internal/reference/SBPF/Blending-DDD-BDD-TDD.md`
- **Templates**: `doc/internal/reference/templates/`
- **Java Best Practices**: `doc/public/exhibits/Java-Enterprise-Best-Practices-2.md`
- **Charter/Canvas Guide**: `doc/internal/reference/templates/CHARTER-CANVAS-GUIDE.md`
- **Project Status**: Weekly status reports in `doc/internal/planning/status/` (ISO week format: `YYYY-WXX-status.md`)
- **Architecture**: `ARCHITECTURE.md`

---

## ❓ FAQ

**Q: Do we really need all these ceremonies?**  
A: Yes. Each ceremony addresses specific questions and produces specific artifacts. Skipping ceremonies leads to misalignment and rework.

**Q: Can we combine ceremonies?**  
A: Some ceremonies can be back-to-back (e.g., Three Amigos → Example Mapping), but don't merge distinct ceremonies (e.g., Event Storming + Domain Modeling).

**Q: What if Product Owner is unavailable?**  
A: Block specification ceremonies until PO is available. Developers should NOT make business decisions.

**Q: Can we use external tools like Miro?**  
A: No. Everything must be Git-tracked and in Mermaid. This ensures version control and portability.

**Q: What if TDD is slowing us down?**  
A: TDD only slows you down initially. It speeds you up over time by reducing bugs and enabling refactoring. If tests are hard to write, your design is wrong.

**Q: Do we need both charter AND canvas?**  
A: Yes. Charter for deep work, canvas for daily reference. They serve different audiences and update frequencies.

---
```java
// ============================================================================
// HOW WE WORK: Ceremony-Based SDLC Pseudocode
// ============================================================================

// ----------------------------------------------------------------------------
// PHASE 0: CONCEPTION (Program Manager + Product Owner)
// ----------------------------------------------------------------------------
Charter = ProgramManager.CreateProjectCharter()
Charter.problem = ProductOwner.DefineProblem()
Charter.solution = ProductOwner.DefineSolution()
Charter.goals = ProductOwner.DefineOKRs()
Charter.subProjects = ProductOwner.IdentifyBoundedContexts()

Canvas = ProgramManager.CreateProjectCanvas(Charter)  // Quick reference

Backlog.insert(Charter, Priority.CRITICAL)

// ----------------------------------------------------------------------------
// PHASE 1: DISCOVERY (Architect leads, all roles support)
// ----------------------------------------------------------------------------
For each boundedContext in Charter.subProjects:
    
    // Ceremony 1.1: Event Storming Session
    EventStormingOutput = Architect.FacilitateEventStorming(
        participants: [ProductOwner, Developer, ProgramManager],
        inputs: [Charter.problem, Charter.solution]
    )
    DomainEvents = EventStormingOutput.events
    Commands = EventStormingOutput.commands
    AggregateCandidates = EventStormingOutput.aggregates
    Hotspots = EventStormingOutput.unknowns
    
    // Ceremony 1.2: Ubiquitous Language Workshop
    ULGlossary = Architect.ExtractUbiquitousLanguage(
        participants: [ProductOwner, Developer],
        inputs: [DomainEvents, Commands, AggregateCandidates]
    )
    ULGlossary.resolveConflicts()
    ULGlossary.banTechnicalJargon()
    
    // Ceremony 1.3: Domain Modeling Workshop
    DomainModel = Architect.DefineAggregates(
        participants: [Developer, ProductOwner],
        inputs: [AggregateCandidates, ULGlossary, Hotspots]
    )
    For each aggregate in DomainModel.aggregates:
        aggregate.defineInvariants()
        aggregate.defineStateMachine()
        aggregate.defineCommandsAndEvents()
    
    // Ceremony 1.4: Context Mapping
    ContextMap = Architect.MapBoundedContexts(
        participants: [ProgramManager, Developer, ProductOwner],
        inputs: [DomainModel, boundedContext.dependencies]
    )
    ContextMap.defineIntegrationPatterns()  // ACL, Published Language, etc.
    ContextMap.defineUpstreamDownstream()
    
    // Create service charter and canvas
    ServiceCharter = Architect.CreateServiceCharter(
        context: boundedContext,
        domainModel: DomainModel,
        contextMap: ContextMap
    )
    ServiceCanvas = Architect.CreateServiceCanvas(ServiceCharter)
    
    // Document governance decisions
    ADRs = Architect.DocumentArchitectureDecisions(boundedContext)
    POLs = ProgramManager.DocumentPolicies(boundedContext)
    
    Projects.insert(ServiceCharter, Priority.calculate(dependencies, complexity))

// ----------------------------------------------------------------------------
// PHASE 2: SPECIFICATION (Product Owner leads, Architect reviews)
// ----------------------------------------------------------------------------
For each userStory in ProductOwner.Backlog:
    
    // Ceremony 2.1: Example Mapping Workshop
    ExampleMap = ProductOwner.MapExamples(
        participants: [Architect, Developer],
        inputs: [userStory, Charter.scope, DomainModel, ULGlossary]  // Added Charter.scope
    )
    Rules = ExampleMap.rules
    Examples = ExampleMap.examples
    Questions = ExampleMap.questions
    
    // Resolve questions before proceeding
    If !Questions.empty():
        ProductOwner.clarify(Questions)
        ExampleMap.update(clarifications)
    
    // Ceremony 2.2: Three Amigos Specification Session
    BDDScenarios = ProductOwner.WriteScenarios(
        participants: [Architect, Developer],
        inputs: [userStory, Charter.scope, ExampleMap, ULGlossary, DomainModel],  // Added Charter.scope, DomainModel
        format: "Gherkin"
    )
    For each scenario in BDDScenarios:
        scenario.validateUbiquitousLanguage(ULGlossary)
        scenario.validateCharterScope(Charter.scope)  // Added Charter validation
        scenario.validateDomainAlignment(DomainModel)
    
    // Ceremony 2.3: Acceptance Criteria Review
    ApprovedScenarios = Architect.ReviewScenarios(
        participants: [ProductOwner, Developer],
        inputs: [BDDScenarios, Charter.scope, DomainModel, ContextMap]  // Added Charter.scope
    )

    // Validate Charter scope compliance
    ApprovedScenarios.validateCharterScope(Charter.scope)

    If !ApprovedScenarios.allApproved() OR !ApprovedScenarios.coversCharterScope():
        // Trigger domain model refinement or scenario revision
        DomainModel = Architect.RefineDomainModel(misalignments)
        Goto Ceremony 2.2  // Re-write scenarios
    
    AcceptanceCriteria = ProductOwner.DocumentAcceptanceCriteria(
        scenarios: ApprovedScenarios,
        NFRs: userStory.nfrs
    )
    
    // Document product decisions
    PDRs = ProductOwner.DocumentProductDecisions(userStory)
    
    Tasks.insert(userStory, Priority.calculate(value, effort, risk))

// ----------------------------------------------------------------------------
// PHASE 3: IMPLEMENTATION (Developer leads, Architect supports)
// ----------------------------------------------------------------------------
While !Tasks.empty() AND !Bench.empty():
    
    task = Tasks.next()
    developer = Bench.next()
    developer.assign(task)
    
    // Ceremony 3.1: Test-First Pairing Session
    For each scenario in task.scenarios:
        
        // Write failing BDD scenario
        scenario.execute()  // Should fail (Red)
        
        // Identify affected aggregates
        affectedAggregates = Architect.IdentifyAggregates(scenario, DomainModel)
        
        // Write failing unit tests for each aggregate
        For each aggregate in affectedAggregates:
            
            // Ceremony 3.2: Red-Green-Refactor Cycle
            While !aggregate.tests.passing():
                
                // RED: Write failing test
                test = Developer.WriteFailingTest(
                    aggregate: aggregate,
                    behavior: scenario.behavior,
                    language: ULGlossary
                )
                test.execute()  // Should fail
                
                // GREEN: Implement minimum code
                Developer.Implement(
                    aggregate: aggregate,
                    test: test,
                    nonBlocking: true  // MANDATORY
                )
                test.execute()  // Should pass
                
                // REFACTOR: Improve design
                Developer.Refactor(
                    aggregate: aggregate,
                    preserving: test
                )
                test.execute()  // Should still pass
            
            // Ceremony 3.3: Property-Based Test Design
            PropertyTests = Developer.WritePropertyTests(
                participants: [Architect],
                aggregate: aggregate,
                invariants: aggregate.invariants
            )
            PropertyTests.execute()
            
            If PropertyTests.foundEdgeCases():
                ExampleMap.addExamples(PropertyTests.edgeCases)
                // May trigger new scenarios
        
        // Execute BDD scenario again
        scenario.execute()  // Should pass (Green)
    
    // Mark task complete
    task.markComplete()
    developer.release()

// ----------------------------------------------------------------------------
// PHASE 4: INTEGRATION & FEEDBACK (All roles)
// ----------------------------------------------------------------------------

// Ceremony 4.1: Scenario-to-Test Decomposition Workshop
For each complexScenario in BDDScenarios.complex():
    TestCoverageMap = Architect.DecomposeScenario(
        participants: [Developer, ProductOwner],
        inputs: [complexScenario, DomainModel.aggregates, unitTests]
    )
    TestCoverageMap.identifyGaps()
    If TestCoverageMap.hasGaps():
        Tasks.insert(TestCoverageMap.missingTests)

// Ceremony 4.2: Domain Model Retrospective
DomainModelRetro = Architect.RetrospectDomainModel(
    participants: [Developer, ProductOwner],
    inputs: [implementedCode, DomainModel, testFeedback]
)
If DomainModelRetro.hasRefinements():
    DomainModel.apply(DomainModelRetro.refinements)
    ULGlossary.update(DomainModelRetro.terminologyChanges)
    RefactoringBacklog.insert(DomainModelRetro.refactorings)

// Ceremony 4.3: Living Documentation Sync
ProgramManager.SyncDocumentation(
    participants: [Architect, Developer, ProductOwner],
    inputs: [
        changedScenarios: BDDScenarios.modified(),
        modelUpdates: DomainModelRetro.refinements,
        glossaryChanges: ULGlossary.changes()
    ]
)
ProgramManager.updateCharter(changes)
ProgramManager.updateCanvas(changes)
ProgramManager.archiveDeprecatedDocs()

// Ceremony 4.4: Cross-Boundary Integration Testing
For each contextIntegration in ContextMap.integrations():
    IntegrationTests = ProgramManager.TestCrossContextIntegration(
        participants: [Architect, upstreamDev, downstreamDev],
        inputs: [
            contextMap: ContextMap,
            contracts: contextIntegration.contracts,
            scenarios: contextIntegration.scenarios
        ]
    )
    IntegrationTests.execute()
    
    If IntegrationTests.hasCouplingIssues():
        ContextMap = Architect.RefineContextMap(couplingIssues)
        ADRs.document(refinements)

// ----------------------------------------------------------------------------
// SPRINT CEREMONIES (Every 2-3 weeks)
// ----------------------------------------------------------------------------

// Sprint Planning
Sprint.plan(
    backlog: Tasks.topPriority(velocity),
    capacity: Bench.size() * Sprint.durationDays,
    dependencies: ContextMap.dependencies
)

// Daily Standup
Daily:
    For each developer in Bench:
        developer.report(completed, inProgress, blockers)
    ProgramManager.updateKanban()
    ProgramManager.resolveBlockers()

// Sprint Review
Sprint.review(
    demo: Developer.DemoWorkingFeatures(),
    acceptanceCriteria: ProductOwner.ValidateAC(),
    feedback: Stakeholders.ProvideFeedback()
)

// Sprint Retrospective
Retrospective = ProgramManager.FacilitateRetrospective(
    participants: [ProductOwner, Architect, Bench],
    maxActions: 3  // POL: Max 3 action items
)
Retrospective.documentLearnings()
Retrospective.scheduleActions()

// ----------------------------------------------------------------------------
// CONTINUOUS ACTIVITIES
// ----------------------------------------------------------------------------

// Monitoring & Observability (All services)
For each service in Projects:
    service.emitMetrics()         // Prometheus
    service.emitTraces()          // OpenTelemetry
    service.emitStructuredLogs()  // JSON logs
    service.reportHealth()        // /health endpoint

// SLA/SLO Tracking
For each service in Projects:
    If service.errorBudget() < threshold:
        Alert.fire(service.slo.violations)
        ProgramManager.schedulePostmortem()

// Governance Compliance
ProgramManager.auditCompliance(
    policies: POLs.all(),
    architectureDecisions: ADRs.all(),
    productDecisions: PDRs.all()
)

// Documentation Maintenance
Weekly:
    ProgramManager.validateLivingDocs()
    ProgramManager.updateStatusReport()
    ProgramManager.syncContextMaps()

// ============================================================================
// END OF SDLC CYCLE
// ============================================================================
```

---

**Last Updated**: January 9, 2026 (v1.0.0 - Template Release)  
**Maintained By**: Program Manager + Architect  
**Review Cadence**: Quarterly
