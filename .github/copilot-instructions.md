# GitHub Copilot Instructions

## Project Overview

**Enterprise Java microservices** using ceremony-based **DDD + BDD + TDD**  
**Stack**: Java 21+, Pekko actors, Kafka, PostgreSQL (reactive), Karate BDD, OpenTelemetry

**Roles**: Program Manager, Product Owner, Architect, Bench Developer  
**Process**: 14 ceremonies across 4 phases (see `HOW-WE-WORK.md`)

---

## Non-Negotiable Rules

### 1. Ubiquitous Language Everywhere
- Code/docs/scenarios use **exact terms** from `doc/domain-models/ubiquitous-language.md`
- **Ban**: "DTO", "Manager", "Handler", "Service", "Helper" in domain code
- Infrastructure layer (ACL/adapters) can use technical terms

### 2. Non-Blocking Everything
- **Use**: `CompletionStage`, reactive Postgres drivers, Pekko actors/streams
- **Ban**: `Future.get()`, `Thread.sleep()`, JDBC, blocking I/O
- **If it blocks, it's wrong**

### 3. Test-First Always
- **BDD scenarios** → failing unit test → implement (Red-Green-Refactor)
- Use **jqwik** for property-based testing of invariants
- Test names use domain language

### 4. Documentation as Code

### 5. Scala 3 Patterns (mandatory in plugins)
 - Use **Scala 3.3.1**; avoid cross-build with 2.13.
 - Prefer **enums** over sealed traits for validation checks and ADTs; use `label`/`value` fields for display.
 - Use **given/using** instead of implicits; avoid deprecated syntax.
 - Expose per-check **`T.task`** targets for parallel validation; keep logic side-effect free and non-blocking.
 - Remove **ScalaMock** (no Scala 3 artifact); rely on ScalaTest 3.2.18 or MUnit.


---

## Code Patterns

### Domain Layer
```java
// ✅ Rich domain model
public class Tenant { // Aggregate root
    private final TenantId id;
    private CompanyName companyName;
    private TenantStatus status;
    
    public void activate() {
        if (!canTransitionTo(ACTIVE)) {
            throw new IllegalStateTransitionException(...);
        }
        this.status = ACTIVE;
    }
}

// ❌ Anemic model - NO
public class Tenant {
    private UUID id;
    private String companyName;
    // Just getters/setters
}
```

### Pekko Actors
```java
// Use Typed Actors with sealed interfaces
public sealed interface Command {}
public record ProvisionTenant(CompanyName name, ActorRef<Response> replyTo) implements Command {}
```

### Value Objects (POL-033)
```java
// ✅ Java Records for all value objects
public record TenantId(UUID value) {
    public TenantId {
        if (value == null) throw new IllegalArgumentException("ID required");
    }
}

public record CompanyName(String value) {
    public CompanyName {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Name required");
    }
}

// ❌ Traditional classes - NO (68% more code)
public class CompanyName {
    private final String value;
    public CompanyName(String value) { ... }
    public String getValue() { return value; }
    @Override public boolean equals(Object o) { ... }
    @Override public int hashCode() { ... }
}
```

### Reactive Persistence
```java
// ✅ Non-blocking
public CompletionStage<Order> save(Order order) {
    return pgPool.preparedQuery("INSERT INTO...").execute(...);
}

// ❌ Blocking JDBC - NEVER
```

---

## Validation Commands

Use Mill plugin commands to validate ceremony outputs:

### Domain Model Validation
```bash
# Validate domain model (10 checks)
mill yourService.domain.domainValidate

# Checks:
# - Aggregates documented
# - Value objects use Java Records (POL-033)
# - Domain events exist
# - Repositories present
# - Aggregate size (1-7 entities)
# - Aggregates are Pekko actors
# - Immutability enforced
# - No cyclic dependencies
# - Entity identity
# - Invariants validated
```

### Context Map Validation
```bash
# Validate context map (3 checks)
mill contextMapValidate

# Checks:
# - Infrastructure in context map matches docker-compose.yml (BUG-003 prevention)
# - Contexts in map match build.sc modules
# - ADR references valid
```

### Complete Validation
```bash
# All domain validations (25 checks)
mill yourService.domain.domainAll
```

**Documentation**: [mill-domain-plugin/README.md](../mill-domain-plugin/README.md)

---

## Documentation Templates

Use from `doc/reference/templates/`:
- Event storming → `EVENT-STORMING-TEMPLATE.md`
- Aggregates → `AGGREGATE-TEMPLATE.md` (with Mermaid diagrams)
- BDD scenarios → `FEATURE-TEMPLATE.feature`
- Integration → `INTEGRATION-CONTRACT-TEMPLATE.md`
- Decisions → `ADR-TEMPLATE.md`, `PDR-TEMPLATE.md`, `POL-TEMPLATE.md`

**Always include Mermaid diagrams**: class diagrams, state machines, context maps

---

## Ceremony-Specific Guidance

For detailed ceremony instructions, see:
- `.github/copilot-instructions-phase0-program-initiation-program-manager.md`
- `.github/copilot-instructions-phase1-event-storming-architect.md`
- `.github/copilot-instructions-phase1-ubiquitous-language-architect.md`
- `.github/copilot-instructions-phase1-domain-modeling-architect.md`
- `.github/copilot-instructions-phase1-context-mapping-architect.md`
- `.github/copilot-instructions-phase2-three-amigos-product-owner.md`
- `.github/copilot-instructions-phase2-example-mapping-product-owner.md`
- `.github/copilot-instructions-phase2-acceptance-criteria-review-architect.md`
- `.github/copilot-instructions-phase3-test-first-pairing-bench-developer.md`
- `.github/copilot-instructions-phase3-red-green-refactor-bench-developer.md`
- `.github/copilot-instructions-phase3-property-based-testing-bench-developer.md`
- `.github/copilot-instructions-phase4-scenario-to-test-decomposition-architect.md`
- `.github/copilot-instructions-phase4-domain-model-retrospective-architect.md`
- `.github/copilot-instructions-phase4-living-documentation-sync-program-manager.md`
- `.github/copilot-instructions-phase4-cross-boundary-integration-testing-program-manager.md`

---

## Quick Reference

**Key Files**:
- `HOW-WE-WORK.md` - Complete SDLC playbook
- `CHARTER.md` - Program charter
- `ARCHITECTURE.md` - System architecture
- `doc/domain-models/ubiquitous-language.md` - Terminology glossary

**Key Directories**:
- `doc/reference/templates/` - All templates
- `doc/domain-models/` - Event storming, aggregates, context maps
- `doc/scenarios/` - Example maps, acceptance criteria
- `doc/governance/` - POLs, PDRs, ADRs
- `features/` - BDD scenarios (Gherkin)

**Key Policies**:
- **POL-033**: Use Java Records for all value objects (68% code reduction)
- **POL-032**: Mill plugin development standards
- **POL-028**: Non-blocking I/O everywhere

**Red Flags**:
- ❌ Blocking I/O, JDBC, `Future.get()`
- ❌ Anemic domain models
- ❌ Technical jargon in domain code
- ❌ Tests written after implementation
- ❌ Docs without templates/Mermaid diagrams
- ❌ Skipping ceremonies
- ❌ Traditional classes for value objects (should use Java Records)

**Before Suggesting Code/Docs**:
- [ ] Uses ubiquitous language from glossary
- [ ] Non-blocking (CompletionStage, reactive drivers)
- [ ] Test-first (failing test exists)
- [ ] Value objects use Java Records (POL-033)
- [ ] Correct template used
- [ ] Mermaid diagrams included
- [ ] Links to related artifacts
