# Software Best Practice Framework (SBPF)

**Total Documents:** 40
**Last Updated:** December 21, 2024

---

## Purpose

This directory contains **reusable best practice guides** independent of any specific project. These documents serve as organizational knowledge and methodology references, curated from industry best practices and refined through real-world application.

---

## Quick Start

### For New Team Members
Start with **[Blending-DDD-BDD-TDD.md](Blending-DDD-BDD-TDD.md)** (87.8 KB) - The master integrative guide explaining how Domain-Driven Design, Behavior-Driven Development, and Test-Driven Development work together through a ceremony-based approach.

### For Practitioners
Reference methodology-specific guides during:
- **Discovery:** Event Storming, Context Mapping → DDD documents
- **Specification:** Three Amigos, Example Mapping → BDD documents
- **Implementation:** Red-Green-Refactor → TDD documents

### For Architects
Use architectural pattern guides when making technology decisions. Link to these documents in your ADRs (Architecture Decision Records).

---

## Document Index

### 📋 Methodology Integration (1 document)

| Document | Size | Description |
|----------|------|-------------|
| **[Blending-DDD-BDD-TDD.md](Blending-DDD-BDD-TDD.md)** ⭐ | 87.8 KB | **START HERE** - Master ceremony-based integration guide covering organizational roles, ceremony cycles, and artifact flows across discovery, specification, and implementation phases |

---

### 🎯 Domain-Driven Design (DDD) (3 documents)

| Document | Description |
|----------|-------------|
| [DDD-Principles.md](DDD-Principles.md) | Core DDD concepts: ubiquitous language, bounded contexts, entities, value objects, aggregates, repositories, domain events, domain services |
| [DDD-Best-Practices.md](DDD-Best-Practices.md) | Practical DDD guidance: collaborative modeling, iterative refinement, behavior-focused modeling, subdomain decoupling, documentation patterns |
| [Ubiquitous-Language.md](Ubiquitous-Language.md) | Building shared vocabulary between domain experts and developers, event storming, context mapping, glossary management |

---

### 🧪 Behavior-Driven Development (BDD) (2 documents)

| Document | Description |
|----------|-------------|
| [BDD-Best-Practices.md](BDD-Best-Practices.md) | BDD principles: Gherkin syntax, outcome focus, shared understanding, Three Amigos collaboration, Example Mapping, integration with DDD/TDD |
| [Gherkin-Syntax.md](Gherkin-Syntax.md) | Feature files, scenarios, Given-When-Then structure, backgrounds, data tables, scenario outlines with best practices |

---

### 🔴🟢🔵 Test-Driven Development (TDD) (3 documents)

| Document | Description |
|----------|-------------|
| [TDD-Best-Practices.md](TDD-Best-Practices.md) | TDD cycle (Red-Green-Refactor), immediate feedback, emergent design, maintaining fast test execution |
| [FV-PBT.md](FV-PBT.md) | Functional Verification and Property-Based Testing concepts, generators, shrinking, integration with traditional testing |
| [Property-Based-Testing-Implementation.md](Property-Based-Testing-Implementation.md) ⭐ **NEW** | Practical ScalaCheck implementation: building domain generators, writing properties, testing invariants, MDSlides examples (29 property tests) |

---

### 📐 Architecture Patterns (5 documents)

| Document | Description |
|----------|-------------|
| [Microservices-Architecture.md](Microservices-Architecture.md) | Service independence, DDD integration, API communication, decentralized data management, scalability, resilience patterns |
| [Event-Driven-Architecture.md](Event-Driven-Architecture.md) | Events, producers/consumers, event brokers (Kafka, RabbitMQ), topics, event sourcing, CQRS, schema evolution |
| [Reactive-Manifesto.md](Reactive-Manifesto.md) | Core principles (Responsive, Resilient, Elastic, Message-Driven), asynchronous communication, fault isolation, auto-scaling |
| [Stateless-Service-Design.md](Stateless-Service-Design.md) | Statelessness benefits, external state management, idempotency, JWT authentication, cloud-native scaling |
| [TOGAF-ADM.md](TOGAF-ADM.md) | Enterprise architecture methodology covering 9 phases from preliminary through change management, governance, stakeholder alignment |

---

### 🧩 Programming Paradigms (2 documents)

| Document | Description |
|----------|-------------|
| [Functional-Programming.md](Functional-Programming.md) | Pure functions, immutability, first-class/higher-order functions, recursion, declarative programming, referential transparency, lazy evaluation |
| [Non-Blocking-Programming.md](Non-Blocking-Programming.md) | Asynchronous operations, event loops, callbacks, promises, async/await, reactive programming with Node.js, RxJS, asyncio examples |

---

### 🎭 Concurrency & Actor Systems (6 documents)

| Document | Description |
|----------|-------------|
| [Actor-Model-Principles.md](Actor-Model-Principles.md) | Actors as fundamental units, message passing, supervision hierarchies, concurrency/distribution patterns |
| [Pekko-ZIO-CatsEffect.md](Pekko-ZIO-CatsEffect.md) | Comprehensive comparison: Pekko (actor-based), ZIO (type-safe concurrency), Cats Effect (lightweight pure-functional) |
| [Pekko-Java-vs-Scala3.md](Pekko-Java-vs-Scala3.md) | Language choice analysis for Pekko: Java advantages (familiarity, ecosystem) vs Scala3 (conciseness, functional features) |
| [ZIO.md](ZIO.md) | ZIO[R, E, A] type, ZManaged resource management, ZStreams, ZLayers dependency injection, typed error handling |
| [Cats-Effect.md](Cats-Effect.md) | IO Monad, Fibers for lightweight concurrency, composability with FS2, pure functional programming best practices |
| [Supervision-Strategies.md](Supervision-Strategies.md) | Hierarchical fault tolerance, supervision trees, restart strategies (One-for-One, One-for-All), exponential backoff, chaos testing |

---

### 🛡️ Resilience & Observability (5 documents)

| Document | Description |
|----------|-------------|
| [Circuit-Breakers-Bulkheads-Retries.md](Circuit-Breakers-Bulkheads-Retries.md) | Resilience patterns: circuit breaker state management, bulkhead isolation, exponential backoff retries, tools (Hystrix, Resilience4j, Polly) |
| [Chaos-Engineering-Resilience-Testing.md](Chaos-Engineering-Resilience-Testing.md) | Hypothesis-driven chaos experiments, blast radius limitation, observability integration, tools (Chaos Monkey, Gremlin, Litmus) |
| [Distributed-Tracing-Observability.md](Distributed-Tracing-Observability.md) | Traces, spans, context propagation, observability pillars (metrics, logs, traces), W3C Trace Context, tools (Jaeger, Zipkin) |
| [Responsiveness-Prioritization.md](Responsiveness-Prioritization.md) | SLAs/SLOs/SLIs, error budgets, continuous monitoring, incident response automation, tools (Prometheus, Grafana, PagerDuty) |
| [Resilience-in-Acceptance-Criteria.md](Resilience-in-Acceptance-Criteria.md) | Defining resilience requirements collaboratively, failure scenario testing, RTO/RPO metrics, chaos engineering integration |

---

### 🔧 Build & Development Tooling (2 documents)

| Document | Size | Description |
|----------|------|-------------|
| [Mill-Build-Tool-Guide.md](Mill-Build-Tool-Guide.md) | 31 KB | Comprehensive Mill build tool guide: comparison vs Maven/Gradle/sbt, installation, monorepo support for bounded contexts |
| [Mill-Scala3-Java21-Build-Guide.md](Mill-Scala3-Java21-Build-Guide.md) | 20.8 KB | Specialized Mill guide with Scala 3 and Java 21, aligned with DDD+BDD+TDD methodology, bounded context modularization |

---

### 🏗️ Component Engineering (1 document)

| Document | Description |
|----------|-------------|
| [CBSE-Best-Practices.md](CBSE-Best-Practices.md) | Component-Based Software Engineering: modularity, reusability, interoperability, version control, continuous integration, design patterns |

---

### 📊 Process & Planning (5 documents)

| Document | Description |
|----------|-------------|
| [Agile-Scrum-Frameworks.md](Agile-Scrum-Frameworks.md) | Agile principles, Scrum roles/events/artifacts, iterative development, cross-functional teams, Definition of Done, metrics (velocity, lead time) |
| [Lean-Agile-Principles.md](Lean-Agile-Principles.md) | Value delivery, waste elimination, continuous improvement, team empowerment, flow optimization, tools (Kanban, SAFe, Lean Startup) |
| [Prioritization-Models.md](Prioritization-Models.md) | RICE scoring, Weighted Scoring, MoSCoW method, Kano Model, ICE scoring, data-driven decision-making |
| [OKRs.md](OKRs.md) | Objectives & Key Results framework: ambitious goals, cascading alignment, transparency, regular reviews, avoiding overload |

---

### 📝 Documentation & Governance (4 documents) ⭐ **NEW**

| Document | Description |
|----------|-------------|
| [Architecture-Decision-Records.md](Architecture-Decision-Records.md) ⭐ **NEW** | ADR template, lifecycle, when to write ADRs vs PDRs vs POLs, integration with ceremonies, MDSlides examples (11 ADRs), best practices |
| [Product-Decision-Records.md](Product-Decision-Records.md) ⭐ **NEW** | PDR template for product/business decisions, distinction from ADRs (technical) and POLs (process), user impact metrics, MDSlides examples (8 PDRs) |
| [Policy-Documents.md](Policy-Documents.md) ⭐ **NEW** | POL template for team practices/standards, enforcement mechanisms (linters, CI), MUST/SHOULD/MAY guidelines, MDSlides examples (5 POLs) |
| [Anticorruption-Layer-Patterns.md](Anticorruption-Layer-Patterns.md) ⭐ **NEW** | ACL patterns (Adapter, Translator, Facade, Gateway), protecting domain from external libraries, MDSlides examples (Flexmark, Scalatags, Circe) |

---

## New Documents (December 2024)

Five critical documents were added based on gaps identified during the MDSlides project:

1. **[Architecture-Decision-Records.md](Architecture-Decision-Records.md)** (22 KB)
   - Practical ADR writing guide
   - Distinction between ADRs (technical), PDRs (product), POLs (process)
   - Integration with ceremony-based development
   - Real-world examples from MDSlides (11 ADRs documented)

2. **[Product-Decision-Records.md](Product-Decision-Records.md)** (24 KB)
   - Practical PDR writing guide for product/business decisions
   - When to use PDRs vs ADRs vs POLs
   - User impact and business metrics
   - Real-world examples from MDSlides (8 PDRs documented)

3. **[Policy-Documents.md](Policy-Documents.md)** (26 KB)
   - Practical POL writing guide for team practices/standards
   - Enforcement mechanisms (automated linters, CI checks, manual reviews)
   - MUST/SHOULD/MAY requirement levels (RFC 2119)
   - Real-world examples from MDSlides (5 POLs documented)

4. **[Property-Based-Testing-Implementation.md](Property-Based-Testing-Implementation.md)** (25 KB)
   - ScalaCheck practical implementation guide
   - Building domain-specific generators
   - Writing effective properties for invariants
   - MDSlides examples (29 property tests, custom generators)

5. **[Anticorruption-Layer-Patterns.md](Anticorruption-Layer-Patterns.md)** (21 KB)
   - Protecting pure functional domain from external dependencies
   - Four ACL patterns with implementation examples
   - Testing strategies for domain and ACL separately
   - MDSlides examples (Flexmark, Scalatags, Circe adapters)

These complement existing documents by providing **practical implementation guidance** and **governance frameworks** rather than just principles.

---

## Document Categories by Use Case

### 🚀 Starting a New Project
1. [Blending-DDD-BDD-TDD.md](Blending-DDD-BDD-TDD.md) - Understand the methodology
2. [DDD-Principles.md](DDD-Principles.md) - Learn domain modeling
3. [Mill-Build-Tool-Guide.md](Mill-Build-Tool-Guide.md) or [Mill-Scala3-Java21-Build-Guide.md](Mill-Scala3-Java21-Build-Guide.md) - Set up build
4. [Architecture-Decision-Records.md](Architecture-Decision-Records.md) - Document decisions

### 🎨 Domain Modeling
1. [DDD-Best-Practices.md](DDD-Best-Practices.md)
2. [Ubiquitous-Language.md](Ubiquitous-Language.md)
3. [Event-Driven-Architecture.md](Event-Driven-Architecture.md)
4. [Anticorruption-Layer-Patterns.md](Anticorruption-Layer-Patterns.md)

### 🧪 Writing Tests
1. [TDD-Best-Practices.md](TDD-Best-Practices.md)
2. [BDD-Best-Practices.md](BDD-Best-Practices.md)
3. [Gherkin-Syntax.md](Gherkin-Syntax.md)
4. [Property-Based-Testing-Implementation.md](Property-Based-Testing-Implementation.md)
5. [FV-PBT.md](FV-PBT.md)

### 🏗️ Architecture Decisions
1. [Microservices-Architecture.md](Microservices-Architecture.md)
2. [Pekko-ZIO-CatsEffect.md](Pekko-ZIO-CatsEffect.md) - Choose concurrency framework
3. [Functional-Programming.md](Functional-Programming.md)
4. [Reactive-Manifesto.md](Reactive-Manifesto.md)

### 🛡️ Building Resilience
1. [Circuit-Breakers-Bulkheads-Retries.md](Circuit-Breakers-Bulkheads-Retries.md)
2. [Chaos-Engineering-Resilience-Testing.md](Chaos-Engineering-Resilience-Testing.md)
3. [Supervision-Strategies.md](Supervision-Strategies.md)
4. [Resilience-in-Acceptance-Criteria.md](Resilience-in-Acceptance-Criteria.md)

### 📊 Team Practices
1. [Agile-Scrum-Frameworks.md](Agile-Scrum-Frameworks.md)
2. [Lean-Agile-Principles.md](Lean-Agile-Principles.md)
3. [Prioritization-Models.md](Prioritization-Models.md)
4. [OKRs.md](OKRs.md)

---

## Relationship to Project Artifacts

```
SBPF (Reusable Patterns) ← YOU ARE HERE
    ↓ Applied in
Exhibits (Concrete Examples)
    ↓ Implemented in
Services (Living Implementation)
```

- **SBPF** (`doc/reference/SBPF/`): "Here's how to do DDD/BDD/TDD"
- **Exhibits** (`doc/exhibits/`): "Here's an example (service charters showing best practices)"
- **Services** (`doc/services/`): "Here's our actual artifacts for this service"
- **Governance** (`doc/governance/`): "Here are the decisions we made for this project"

---

## MDSlides as SBPF Validation

The **MDSlides** project serves as a validation exhibit for SBPF practices:

### Applied Documents:
- ✅ Blending-DDD-BDD-TDD.md → Event Storming, Domain Ceremonies, TDD cycle
- ✅ DDD-Principles.md → Pure functional domain, aggregates, value objects
- ✅ Mill-Scala3-Java21-Build-Guide.md → 3-module clean architecture
- ✅ Property-Based-Testing-Implementation.md → 29 property tests, custom generators
- ✅ Architecture-Decision-Records.md → 11 ADRs documented
- ✅ Anticorruption-Layer-Patterns.md → Flexmark, Scalatags, Circe adapters
- ✅ Functional-Programming.md → Pure functions, immutability, Either for errors
- ✅ Cats-Effect.md → IO for CLI effects

### Artifacts Generated:
- **Governance:** 11 ADRs, 8 PDRs, 4 POLs
- **Tests:** 81 total (29 property-based, 32 example-based, 20 infrastructure)
- **Code:** 3-layer architecture (domain/infrastructure/cli)

---

## Contributing

To add or update SBPF documents:

1. **Identify Gap:** Use real project experience to find missing guidance
2. **Create PR:** Propose new document or update
3. **Discuss:** Architecture review or team meeting
4. **Document Rationale:** Explain why this knowledge is needed
5. **Add Examples:** Link to exhibits (e.g., MDSlides) demonstrating the pattern
6. **Update This README:** Add to appropriate category with description

### Quality Standards

- **Practical:** Include real-world examples, not just theory
- **Referenced:** Link to industry sources and exhibits
- **Testable:** Describe how to verify the practice works
- **Integrated:** Show how it fits with other SBPF documents

---

## Known Gaps (Future Work)

Based on MDSlides experience, these topics would add value:

**Tier 2 (High Value):**
- Pure-Functional-Domain-Implementation.md (3-layer architecture pattern)
- CLI-Application-Architecture.md (non-service architectures)

**Tier 3 (Good to Have):**
- Domain-Generator-Patterns.md (ScalaCheck composition)
- Test-Organization-Strategies.md (file structure for tests)
- Accessibility-As-First-Class-Requirement.md (WCAG in BDD)
- Offline-First-Application-Design.md (bundling resources, standalone apps)

These will be created as experience warrants.

---

## License & Attribution

These documents curate industry best practices from:
- Eric Evans (Domain-Driven Design)
- Kent Beck (Test-Driven Development)
- Dan North (Behavior-Driven Development)
- Reactive Manifesto authors
- Martin Fowler (Patterns of Enterprise Application Architecture)
- And many others

Adapted and refined through real-world application in projects like MDSlides.

---

## Related Documentation

- **MDSlides Governance:** `doc/governance/` (ADRs, PDRs, POLs from applying SBPF)
- **Templates:** `doc/reference/templates/` (scaffolding for new projects)
- **Standards:** `doc/reference/standards/` (org-specific adaptations of SBPF)

---

**Last Updated:** December 21, 2024
**Document Count:** 40 (35 original + 5 new in December 2024)
**Maintainer:** Development Team
