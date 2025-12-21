# GitHub Copilot Instructions: Phase 3.2 - Red-Green-Refactor Cycle
## Role: Bench Developer

**Led by**: Bench Developer | **Supported by**: Architect (refactoring guidance)

## 🎯 Goals
Implement minimum code to pass tests, then refactor for design quality.

## 📥 Key Inputs
- Failing test (from Phase 3.1)
- Domain model
- Design patterns and principles

## 📤 Key Outputs
- Passing tests (Green)
- Refactored production code
- Git commits (small, frequent)

## 🔨 The Red-Green-Refactor Cycle

### RED Phase
1. Write failing test
2. Test MUST fail (if it passes, test is wrong)
3. Clear failure message

### GREEN Phase
1. Write **minimum** code to pass test
2. Can be "ugly"—that's okay, refactor comes next
3. Test passes? Move to Refactor

### REFACTOR Phase
1. Improve design without breaking tests
2. Extract methods, rename variables, simplify
3. Tests still passing? Commit

### Example
```java
// RED: Write failing test
@Test
void shouldRejectNegativeTenantProvisioningTime() {
    assertThatThrownBy(() -> new ProvisioningTime(-5))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Provisioning time must be positive");
}

// GREEN: Minimum code to pass (can be ugly)
public record ProvisioningTime(int seconds) {
    public ProvisioningTime {
        if (seconds < 0) throw new IllegalArgumentException("Provisioning time must be positive");
    }
}

// REFACTOR: Improve design
public record ProvisioningTime(int seconds) {
    private static final int MIN_SECONDS = 0;
    private static final String ERROR_NEGATIVE = "Provisioning time must be positive";
    
    public ProvisioningTime {
        if (seconds < MIN_SECONDS) throw new IllegalArgumentException(ERROR_NEGATIVE);
    }
}
```

## 🚨 Common Mistakes
- **Skipping RED**: Writing code before test fails
- **Over-engineering in GREEN**: Keep it simple, refactor later
- **Skipping REFACTOR**: Technical debt accumulates
- **Refactoring without tests**: Break tests, lose confidence
- **Not using Java Records for value objects (POL-033)**: Use Records for 68% code reduction and immutability

## ✅ Definition of Done (Per Cycle)
- [ ] Test fails (RED)
- [ ] Test passes with minimum code (GREEN)
- [ ] Code refactored for quality (REFACTOR)
- [ ] Committed with clear message

**Repeat**: Cycle continues until all scenarios pass
**Next**: Phase 3.3 - Property-Based Testing (for invariants)
