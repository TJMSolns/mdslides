# GitHub Copilot Instructions: Phase 3.3 - Property-Based Test Design Session
## Role: Bench Developer

**Led by**: Bench Developer + Architect (co-led) | **Supported by**: Product Owner (validate edge cases)

## 🎯 Goals
Use generative testing (jqwik) to discover edge cases and validate aggregate invariants.

## 📥 Key Inputs
- Aggregate with invariants
- Domain model
- Example-based tests (from TDD)

## 📤 Key Outputs
- Property-based tests (jqwik)
- Discovered edge cases
- Updates to example maps

## 🔨 Core Activities
1. **Identify Invariants**: Business rules that must ALWAYS hold
2. **Write Property Tests**: Use jqwik to generate random inputs
3. **Run Tests**: Execute with 100s-1000s of random cases
4. **Analyze Failures**: Shrinking reveals minimal failing case
5. **Add Edge Cases**: Update example maps and BDD scenarios

## 📝 Example Property Test
```java
@Property
void tenantCompanyNameMustBeUnique(
    @ForAll @StringLength(min = 3, max = 50) String name1,
    @ForAll @StringLength(min = 3, max = 50) String name2
) {
    Assume.that(!name1.equals(name2));
    
    Tenant tenant1 = new Tenant(TenantId.generate(), CompanyName.of(name1));
    Tenant tenant2 = new Tenant(TenantId.generate(), CompanyName.of(name2));
    
    // Invariant: Different tenants, different names
    assertThat(tenant1.getCompanyName()).isNotEqualTo(tenant2.getCompanyName());
}

@Property
void tenantStatusTransitionsAreValid(@ForAll TenantStatus from, @ForAll TenantStatus to) {
    Tenant tenant = createTenantWith(from);
    
    try {
        tenant.transitionTo(to);
        // Assert valid transition
        assertThat(tenant.getStatus()).isEqualTo(to);
    } catch (IllegalStateTransitionException e) {
        // Invalid transition correctly rejected
    }
}
```

## 🚨 When to Use
- **After TDD tests pass**: Complement example-based tests
- **Critical aggregates**: Core business logic
- **Complex invariants**: Multiple interacting rules
- **Mandatory for all aggregates**: Per POL-XXX

## ✅ Definition of Done
- [ ] Property tests written for all invariants
- [ ] Tests execute 1000+ random inputs
- [ ] Edge cases discovered and documented
- [ ] Example maps updated with new cases
- [ ] BDD scenarios added if needed

**Next**: Phase 4.1 - Scenario-to-Test Decomposition (trace scenarios to tests)
