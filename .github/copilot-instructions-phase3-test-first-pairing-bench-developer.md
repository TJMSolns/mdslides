# GitHub Copilot Instructions: Phase 3.1 - Test-First Pairing Session
## Role: Bench Developer

**Led by**: Bench Developer | **Supported by**: Architect (domain guidance), Product Owner (rule clarification)

## 🎯 Goals
Write failing tests for domain invariants before implementing aggregates.

## 📥 Key Inputs
- Approved BDD scenarios (from Phase 2.3)
- Domain model (aggregate definitions)
- Ubiquitous language glossary

## 📤 Key Outputs
- Failing BDD scenario (Karate test)
- Failing unit tests (JUnit 5)
- Implementation code (aggregates, value objects)
- Passing tests

## 🔨 Core TDD Workflow
1. **Choose Failing BDD Scenario**: Pick next approved scenario
2. **Execute Scenario**: Should fail (Red) - no implementation yet
3. **Identify Affected Aggregate**: Which aggregate handles this?
4. **Write Failing Unit Test**: Test aggregate behavior
5. **Implement Minimum Code**: Make test pass (Green)
6. **Refactor**: Improve design, keep tests passing
7. **Repeat**: Until BDD scenario passes

## 📝 Example Workflow
```java
// 1. Failing unit test
@Test
void shouldProvisionTenantWithUniqueCompanyName() {
    Tenant tenant = new Tenant(TenantId.generate(), CompanyName.of("acme-corp"));
    assertEquals(TenantStatus.PROVISIONING, tenant.getStatus());
}

// 2. Implement minimum code (Green)
public class Tenant {
    private final TenantId id;
    private final CompanyName companyName;
    private TenantStatus status;
    
    public Tenant(TenantId id, CompanyName companyName) {
        this.id = id;
        this.companyName = companyName;
        this.status = TenantStatus.PROVISIONING;
    }
}

// 3. Refactor if needed (e.g., use Java Records for value objects - POL-033)
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
```

## 🚨 Critical Rules
- **Test first, always**: No production code without failing test
- **Use ubiquitous language**: Test names, class names, method names
- **Non-blocking code**: Use CompletionStage, no blocking I/O
- **Test behavior, not implementation**: Test what it does, not how
- **Value objects use Java Records (POL-033)**: 68% code reduction, enforces immutability

## ✅ Definition of Done
- [ ] BDD scenario passes (Karate test green)
- [ ] Unit tests pass (JUnit 5 green)
- [ ] Code uses ubiquitous language
- [ ] Non-blocking implementation (no blocking I/O)
- [ ] Code committed with clear message

**Next**: Phase 3.2 - Red-Green-Refactor (continuous TDD cycle)
