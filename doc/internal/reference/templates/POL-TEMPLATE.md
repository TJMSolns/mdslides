# POL-XXX: [Policy Title]

**Status**: [Proposed | Active | Deprecated | Superseded]  
**Date**: YYYY-MM-DD  
**Authors**: [Name(s)]  
**Reviewers**: [Name(s)]  
**Approvers**: [Architect + Product Owner names]  
**Supersedes**: [POL-XXX if applicable, otherwise N/A]  
**Superseded By**: [POL-XXX if applicable, otherwise N/A]

---

## 📋 Summary

**One-sentence policy statement**: [What is the rule/standard?]

**Scope**: [Which teams/services/contexts does this apply to?]

**Compliance Level**: [Mandatory | Recommended | Optional]

---

## 🎯 Policy Statement

[Clear, actionable statement of the policy. Use MUST/SHOULD/MAY language per RFC 2119.]

Example:
- All services **MUST** validate X-Tenant-ID header on every request.
- Database queries **MUST NOT** span tenant boundaries.
- Tenant isolation violations **MUST** trigger immediate alerts.

---

## 🤔 Context & Rationale

### Problem
[What problem does this policy solve? What happens without it?]

### Why This Policy?
[Explain the reasoning. Link to incidents, security requirements, compliance needs, etc.]

### Scope & Applicability
[Which bounded contexts, services, or teams must follow this?]
- **Applies to**: [List contexts/services]
- **Exceptions**: [Any exceptions? Requires approval from whom?]

---

## 📐 Requirements

### Functional Requirements
[What must be implemented to comply?]

1. **Requirement 1**: [Specific, testable requirement]
2. **Requirement 2**: [Specific, testable requirement]
3. **Requirement 3**: [Specific, testable requirement]

### Non-Functional Requirements
[Performance, security, operational requirements]

1. **NFR 1**: [e.g., "Validation overhead must not exceed 5ms p99"]
2. **NFR 2**: [e.g., "Policy violations logged with ERROR severity"]

---

## ✅ Compliance & Enforcement

### Automated Enforcement
[How is this policy automatically enforced?]

- **Static Analysis**: [ArchUnit rules, linters, etc.]
- **Tests**: [Property-based tests, integration tests that validate policy]
- **CI/CD Gates**: [Pipeline checks that block non-compliant code]
- **Runtime Checks**: [Middleware, interceptors, validation logic]

#### Example Test
```java
// Example: ArchUnit rule to enforce policy
@ArchTest
static final ArchRule policy_xxx_enforcement = 
    classes().that().resideInAPackage("..domain..")
        .should().notDependOn("..infrastructure..")
        .because("POL-XXX: Domain layer must not depend on infrastructure");
```

### Manual Verification
[What requires code review or manual checks?]

- **Code Review Checklist**: [Items reviewers must verify]
- **Audit Procedures**: [Quarterly reviews, compliance reports, etc.]

### Exceptions Process
[How to request an exception? Who approves?]

1. **Request**: [Create ticket with justification]
2. **Review**: [Architect + Product Owner review]
3. **Approval**: [Requires sign-off from X]
4. **Documentation**: [Exception tracked in Y]

---

## 📊 Metrics & Monitoring

### Compliance Metrics
[How do we measure compliance?]

- **Metric 1**: [e.g., "% of services with passing policy tests"]
- **Metric 2**: [e.g., "# of policy violations detected in production per month"]

### Alerting
[When do we get notified of violations?]

- **Alert 1**: [e.g., "Tenant isolation violation → PagerDuty alert"]
- **Alert 2**: [e.g., "Policy test failures → Slack notification"]

### Dashboards
[Where can we see policy compliance status?]

- [Link to Grafana dashboard / compliance report]

---

## 🔗 Related Policies & Decisions

### Related Policies
- `POL-XXX`: [Related policy name]
- `POL-YYY`: [Related policy name]

### Related Architecture Decisions
- `ADR-XXX`: [Related architecture decision]
- `ADR-YYY`: [Related architecture decision]

### Related Product Decisions
- `PDR-XXX`: [Related product decision]

---

## 📚 References

### Internal Documentation
- [Link to relevant domain models, aggregates, integration contracts]
- [Link to ubiquitous language glossary]
- [Link to ceremony outputs (Event Storming, Context Mapping)]

### External Standards
- [Industry standards, RFCs, compliance regulations]
- [Security frameworks (OWASP, CIS, etc.)]
- [Legal/regulatory requirements (GDPR, SOC 2, etc.)]

### Training Materials
- [Link to onboarding docs, wiki pages, runbooks]

---

## 🔄 Review History

| Date | Reviewer | Changes | Reason |
|------|----------|---------|--------|
| YYYY-MM-DD | [Name] | Initial version | [Reason] |
| YYYY-MM-DD | [Name] | [What changed] | [Reason] |

---

## 📝 Appendix

### Example: Compliant Implementation
```java
// Example of code that follows this policy
public class TenantAwareService {
    public CompletionStage<Order> getOrder(TenantId tenantId, OrderId orderId) {
        // POL-XXX compliant: Validates tenant isolation
        return repository.findByTenantAndId(tenantId, orderId)
            .thenApply(order -> {
                if (!order.getTenantId().equals(tenantId)) {
                    throw new TenantIsolationViolationException(
                        "Order does not belong to tenant: " + tenantId
                    );
                }
                return order;
            });
    }
}
```

### Example: Non-Compliant Implementation
```java
// ❌ VIOLATES POL-XXX: No tenant validation
public class UnsafeService {
    public Order getOrder(OrderId orderId) {
        // Missing tenant isolation check - VIOLATES POLICY
        return repository.findById(orderId);
    }
}
```

### Migration Guide
[If this policy changes existing code, how do teams migrate?]

1. **Step 1**: [Action to take]
2. **Step 2**: [Action to take]
3. **Timeline**: [When must migration be complete?]
4. **Support**: [Who can help with migration?]

---

**Review Cadence**: Quarterly  
**Next Review Date**: [YYYY-MM-DD]  
**Policy Owner**: [Role/Name]  
**Contact**: [Email/Slack channel]
