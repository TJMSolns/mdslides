# MDSlides

> A ceremony-based test project using DDD + BDD + TDD with Scala 3, Pekko, and reactive technologies

**Status**: Phase 2 Complete - Ready for TDD | **Version**: 1.4.0

## Quick Links

- **CHARTER.md** - Program charter and success criteria
- **HOW-WE-WORK.md** - Complete SDLC playbook (14 ceremonies across 4 phases)
- **.github/copilot-instructions-*** - Role-specific ceremony guidance (14 files)
- **doc/reference/** - Templates, SBPFs, validation checklists
- **doc/domain-models/** - Domain modeling artifacts (to be created in Phase 1)

## Project Overview

MDSlides is a test of the **mill-bootstrap-plugin** and the **ceremony-based SDLC framework** from RETISIO/copilot-training. It demonstrates how a greenfield project integrates:

- ✅ **Domain-Driven Design** - Bounded contexts, aggregates, value objects
- ✅ **Behavior-Driven Development** - Karate BDD scenarios, example mapping
- ✅ **Test-Driven Development** - Red-Green-Refactor, property-based testing
- ✅ **Ceremony-Based Workflow** - 14 ceremonies in 4 phases

### Technology Stack

- **Language**: Scala 3.3.1 LTS
- **Build System**: Mill 0.11.6 + Scala plugins
- **Runtime**: Pekko (typed actors, streams)
- **Persistence**: PostgreSQL (reactive driver, no JDBC)
- **Testing**: Karate BDD + ScalaTest + jqwik
- **Telemetry**: OpenTelemetry + Jaeger

### Key Principles

1. **Non-Blocking Everything** - CompletionStage, reactive drivers, Pekko streams
2. **Rich Domain Models** - Business logic in aggregates, not anemic DTOs
3. **Ubiquitous Language** - Exact terms from domain glossary everywhere
4. **Test-First** - BDD scenarios → failing tests → implementation
5. **Documentation as Code** - Templates, Mermaid diagrams, living docs

## Getting Started

### ✅ Phase 0: Program Initiation (COMPLETE)

1. ✅ Reviewed **CHARTER.md** - Vision, scope, success criteria defined
2. ✅ Bootstrap completed with mill-bootstrap-plugin
3. ✅ Development environment ready

### ✅ Phase 1: Domain Modeling (COMPLETE)

1. ✅ **Event Storming** → [event-storming.md](doc/domain-models/event-storming.md)
2. ✅ **Domain Modeling** → [aggregates/](doc/domain-models/aggregates/)
   - [slide-deck-aggregate.md](doc/domain-models/aggregates/slide-deck-aggregate.md)
   - [template-aggregate.md](doc/domain-models/aggregates/template-aggregate.md)
3. ✅ **Context Mapping** → Bounded contexts identified
4. ✅ **Ubiquitous Language** → [ubiquitous-language.md](doc/domain-models/ubiquitous-language.md)

### ✅ Phase 2: Specification (COMPLETE - 100%)

1. ✅ **Three Amigos** → All 12 v1.0 MVP stories complete (see [doc/specifications/](doc/specifications/))
2. ✅ **Example Mapping** → All 12 stories with full rule-to-example coverage
3. ✅ **Acceptance Criteria** → All documented in BACKLOG-V3.md
4. ✅ **Ceremony Artifacts** → ~220 KB documentation created

**Ready for Phase 3!** All specifications complete with HIGH confidence.

### 🔄 Phase 3: Test-Driven Implementation (NEXT)

1. **Setup TDD Environment** → ScalaTest with BDD syntax
2. **Implement Stories** → Red-Green-Refactor in dependency order
3. **Start with Parsing** → US-001, 002, 003, 004 (foundation)
4. **Validation Framework** → US-011, 012, 013, 015
5. **Theme System** → US-008, 009
6. **Rendering & CLI** → US-016, 019

### Phase 4: Integration & Release

1. **Integration Testing** → Cross-boundary contracts
2. **Production Spinoff** → Extract to RETISIO GitHub org
3. **Deployment** → Kubernetes via mill-deploy-plugin

## Mill Commands

### Validation Commands

```bash
# Domain model validation
mill mdslides.domain.domainValidate

# Specification (BDD) validation
mill mdslides.specification.specificationValidate

# Testing (TDD) validation
mill mdslides.testing.testingValidate

# Code quality gates
mill mdslides.quality.qualityValidate

# All validations
mill mdslides.validateAll
```

### Build Commands

```bash
# Compile
mill mdslides.compile

# Run tests
mill mdslides.test

# Package
mill mdslides.jar

# Watch mode (auto-recompile)
mill -w mdslides.compile
```

### Ceremony Commands (Phase 3+)

```bash
# Once Phase 3 begins, additional ceremony-specific commands appear
# See .github/copilot-instructions-phase3-test-first-pairing-bench-developer.md
```

## Directory Structure

```
mdslides/
├── CHARTER.md                              # Program charter (Phase 0)
├── ARCHITECTURE.md                         # System architecture (Phase 1+)
├── HOW-WE-WORK.md                         # 14-ceremony SDLC playbook
├── README.md                               # This file
├── .github/
│   ├── copilot-instructions-phase0-*.md   # Program Initiation
│   ├── copilot-instructions-phase1-*.md   # Domain Modeling
│   ├── copilot-instructions-phase2-*.md   # Acceptance Criteria
│   ├── copilot-instructions-phase3-*.md   # Implementation
│   └── copilot-instructions-phase4-*.md   # Integration & Release
├── doc/
│   ├── domain-models/                      # DDD artifacts
│   │   ├── ubiquitous-language.md
│   │   ├── event-storming.md
│   │   ├── aggregates/
│   │   ├── context-maps/
│   │   └── repositories/
│   ├── reference/
│   │   ├── templates/                      # Document templates (34 files)
│   │   ├── SBPF/                          # Shared Best Practice Files (22 files)
│   │   └── validation/                    # Ceremony checklists (5 files)
│   ├── governance/                         # ADRs, POLs, PDRs
│   ├── scenarios/                          # Acceptance criteria, example maps
│   └── status/                             # Phase tracking
├── mill-spinoff-plugin/                    # Service extraction automation
├── mill-deploy-plugin/                     # Kubernetes deployment automation
├── mill-bootstrap-plugin/                  # Project bootstrap automation
├── src/
│   ├── main/scala/com/example/mdslides/   # Domain code (Phase 3)
│   └── test/scala/com/example/mdslides/   # Tests (Phase 3)
├── features/                               # Karate BDD scenarios (Phase 3)
└── build.sc                                # Mill build configuration
```

## Documentation

- **CHARTER.md** - Start here (program charter)
- **HOW-WE-WORK.md** - Complete SDLC playbook with 14 ceremonies
- **.github/copilot-instructions-*** - Role-specific guidance per ceremony
- **doc/reference/SBPF/** - Shared Best Practice Files (agile, ceremonies, code)
- **doc/reference/templates/** - Document templates for all artifacts
- **doc/reference/validation/** - Ceremony validation checklists

## Non-Negotiable Rules

1. **Ubiquitous Language**: All code/docs use exact domain terms (no "DTO", "Manager", "Handler", "Service", "Helper")
2. **Non-Blocking I/O**: CompletionStage, reactive drivers, Pekko actors only (no Thread.sleep, JDBC)
3. **Rich Domain Models**: Business logic in aggregates, not anemic models
4. **Test-First**: BDD scenarios → failing tests → implementation
5. **Value Objects as Records**: Use Java Records for all value objects (POL-033)

## Support & Questions

For questions about ceremonies, architecture, or patterns:

1. Check the appropriate copilot instruction file (`.github/copilot-instructions-*.md`)
2. Review HOW-WE-WORK.md for the complete ceremony workflow
3. See doc/reference/ for templates and best practices
4. Check ARCHITECTURE.md (Phase 1+) for system design decisions

## License

This project is bootstrapped from RETISIO/copilot-training and follows the same license.

---

**Bootstrap Date**: December 18, 2024  
**Framework Version**: 1.0.0  
**Mill Version**: 0.11.6  
**Scala Version**: 3.3.1 LTS
