# GitHub Copilot Instructions: Phase 4.4 - Cross-Boundary Integration Testing
## Role: Program Manager

**Led by**: Program Manager + Architect (co-led) | **Supported by**: Bench Developers (both contexts), Product Owner (end-to-end scenarios)

## 🎯 Goals
Validate anti-corruption layers and context contracts work correctly across bounded context boundaries.

## 📥 Key Inputs
- Context map (integration relationships)
- Integration contracts
- ACL implementations
- Cross-context scenarios

## 📤 Key Outputs
- Integration test suite
- Contract tests (Pact, Spring Cloud Contract)
- Updated integration contracts (if issues found)
- Integration testing report

## 🔨 Core Activities
1. **Review Context Map**: Which contexts integrate? How?
2. **Write Integration Tests**: NOT UI tests—test through domain APIs
3. **Validate ACLs**: Does ACL protect domain from external model changes?
4. **Test Failure Scenarios**: Upstream down, timeout, invalid data
5. **Verify Contract Tests**: Both producer and consumer sides pass

## 📝 Integration Patterns to Test

### Published Language (Kafka Events)
```java
// Producer test: Publish TenantActivated event
kafka.send("tenant-events", tenantActivatedEvent);

// Consumer test: Receive and handle event
@Test
void shouldHandleTenantActivatedEvent() {
    // ACL translates external event to internal model
    InternalTenantModel tenant = acl.translate(externalEvent);
    assertThat(tenant.isActive()).isTrue();
}
```

### Anti-Corruption Layer
```java
// Test ACL protects internal model from external changes
@Test
void shouldTranslateExternalModelToInternalModel() {
    ExternalTenant externalTenant = /* from upstream */;
    InternalTenant internalTenant = tenantACL.translate(externalTenant);
    
    // Internal model uses our ubiquitous language
    assertThat(internalTenant.getCompanyName()).isNotNull();
}
```

## 🚨 Integration Anti-Patterns to Avoid
- **Testing through UI**: Too slow, too brittle
- **Tight coupling**: Direct database access between contexts
- **No ACL**: Internal model breaks when external model changes
- **Flaky tests**: Network timeouts, race conditions

## ✅ Definition of Done
- [ ] Integration tests written for all context integrations
- [ ] ACLs validated
- [ ] Failure scenarios tested (circuit breakers, timeouts)
- [ ] Contract tests pass on producer and consumer
- [ ] Integration report published

**Cadence**: Per program increment or when contracts change  
**Cycle Complete**: Return to Phase 1 for new features or refinement
