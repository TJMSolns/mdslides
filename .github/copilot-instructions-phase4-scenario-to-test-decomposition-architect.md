# GitHub Copilot Instructions: Phase 4.1 - Scenario-to-Test Decomposition Workshop
## Role: Architect

**Led by**: Architect | **Supported by**: Bench Developer (test coverage), Product Owner (behavioral completeness)

## 🎯 Goals
Trace how BDD scenarios decompose into TDD unit tests to identify coverage gaps.

## 📥 Key Inputs
- Failing or complex BDD scenario
- Domain model (aggregates)
- Existing unit tests

## 📤 Key Outputs
- Test coverage map
- Missing unit tests identified
- Gap analysis documentation

## 🔨 Core Activities
1. **Pick Failing Scenario**: BDD scenario that should pass but doesn't
2. **Identify Aggregates**: Which aggregates involved in scenario?
3. **Map Unit Tests**: Which unit tests cover scenario steps?
4. **Identify Gaps**: Missing tests or wrong tests?
5. **Add Missing Tests**: Developer writes needed unit tests

## 📝 Decomposition Example
```
BDD Scenario: Provision tenant with valid unique name
├── Given company name "acme-corp" is not in use
│   └── Unit Test: shouldCheckCompanyNameUniqueness()
├── When I provision a tenant with company name "acme-corp"
│   └── Unit Test: shouldProvisionTenantWithValidName()
│   └── Unit Test: shouldEmitTenantProvisionedEvent() ← MISSING!
└── Then the tenant is created with status "PROVISIONING"
    └── Unit Test: shouldSetInitialStatusToProvisioning()
```

## 🚨 Common Gaps
- **Collaboration not tested**: Unit tests pass, but aggregates don't work together
- **Events not validated**: Command succeeds but event not published
- **Side effects missed**: Cache invalidation, notifications forgotten

## ✅ Definition of Done
- [ ] All scenario steps mapped to unit tests
- [ ] Gaps identified and documented
- [ ] Missing unit tests added
- [ ] BDD scenario passes

**Next**: Phase 4.2 - Domain Model Retrospective (reflect on model)
