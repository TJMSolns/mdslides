# ADR-XXX: [Decision Title]

**Status**: [Draft | Accepted | Deprecated | Superseded]  
**Date**: [YYYY-MM-DD]  
**Authors**: [Architect name(s)]  
**Reviewers**: [Names of reviewers]  
**Approvers**: [Names of approvers]  
**Supersedes**: [ADR-XXX if applicable, or N/A]  
**Superseded By**: [ADR-XXX if applicable, or N/A]

---

## 📋 Summary

**One-sentence decision statement**: [Clear, concise statement of the technical decision made]

**Scope**: [What parts of the system this applies to]

**Impact Level**: [Critical | High | Medium | Low]

---

## 🎯 Decision

**What we decided**: [Technical decision in detail]

**Why we decided this**: [Technical rationale - performance, scalability, maintainability, etc.]

**Who is affected**: 
- **Developers**: [How this impacts development]
- **Operations**: [How this impacts deployment/operations]
- **Architecture**: [How this impacts system architecture]
- **End Users**: [If applicable, user-facing impact]

---

## 🤔 Context & Background

### Technical Problem

**Current state**: [What's the technical problem or opportunity?]

**Example scenario**: [Concrete example demonstrating the problem]

**Why this matters**: [Technical impact - performance bottleneck, scalability limit, maintainability issue, security risk, etc.]

### Technical Requirements

- **Performance**: [Performance requirements or constraints]
- **Scalability**: [Scalability requirements]
- **Reliability**: [Reliability/availability requirements]
- **Maintainability**: [Code maintainability considerations]
- **Security**: [Security considerations]
- **Cost**: [Infrastructure/operational cost considerations]

### Architectural Context

[How this decision fits into overall system architecture - reference bounded contexts, integration patterns, technology stack]

### Strategic Alignment

- **Technology Stack**: [How this aligns with chosen stack - Java 21+, Pekko, Kafka, PostgreSQL, etc.]
- **Architectural Principles**: [How this aligns with Reactive Manifesto, Actor Model, DDD, etc.]
- **Non-Functional Requirements**: [How this supports NFRs - latency, throughput, uptime, etc.]

---

## 📐 Requirements

### Functional Requirements

1. [Technical capability 1]
2. [Technical capability 2]
3. [Technical capability 3]

### Non-Functional Requirements

1. **Performance**: [Specific metrics - latency, throughput, etc.]
2. **Scalability**: [Horizontal scaling requirements]
3. **Reliability**: [Uptime, fault tolerance requirements]
4. **Security**: [Security requirements]
5. **Observability**: [Monitoring, logging, tracing requirements]

### Compliance Requirements

[If applicable - ArchUnit rules, coding standards, security policies]

### Acceptance Criteria for THIS Decision

1. [Criterion 1: How we verify this decision is properly implemented]
2. [Criterion 2: Code review checklist item]
3. [Criterion 3: Automated validation if possible]
4. [Criterion 4: Documentation requirement]
5. [Criterion 5: Testing requirement]

---

## 🔄 Alternatives Considered

### Alternative 1: [Alternative approach name]
**Description**: [What is this alternative?]  
**Technical Pros**: [Technical advantages]  
**Technical Cons**: [Technical disadvantages]  
**Why rejected**: [Technical reason for rejection]

### Alternative 2: [Alternative approach name]
**Description**: [What is this alternative?]  
**Technical Pros**: [Technical advantages]  
**Technical Cons**: [Technical disadvantages]  
**Why rejected**: [Technical reason for rejection]

### Alternative 3: [Alternative approach name]
**Description**: [What is this alternative?]  
**Technical Pros**: [Technical advantages]  
**Technical Cons**: [Technical disadvantages]  
**Why rejected**: [Technical reason for rejection]

---

## 📊 Technical Analysis

### Performance Analysis

**Latency**:
- Baseline: [Current or estimated latency]
- Target: [Target latency with this decision]
- Measurement: [How we measure]

**Throughput**:
- Baseline: [Current or estimated throughput]
- Target: [Target throughput]
- Measurement: [How we measure]

### Scalability Analysis

**Horizontal Scaling**:
- Can this solution scale horizontally? [Yes/No]
- Scaling characteristics: [Linear, logarithmic, etc.]
- Bottlenecks: [Any scaling bottlenecks]

**Resource Usage**:
- CPU: [CPU characteristics]
- Memory: [Memory footprint]
- Network: [Network usage]
- Storage: [Storage requirements]

### Reliability Analysis

**Failure Modes**:
- [Failure mode 1]: [How system behaves, mitigation]
- [Failure mode 2]: [How system behaves, mitigation]
- [Failure mode 3]: [How system behaves, mitigation]

**Recovery**:
- MTTR (Mean Time To Recovery): [Target]
- Data loss risk: [Assessment]
- Backup/restore strategy: [If applicable]

### Security Analysis

**Attack Vectors**:
- [Potential vulnerability 1]: [Mitigation]
- [Potential vulnerability 2]: [Mitigation]

**Compliance**:
- [Compliance requirement 1]: [How this decision supports it]
- [Compliance requirement 2]: [How this decision supports it]

---

## 🛠️ Implementation Plan

### Phase 1: [Phase Name] (Timeline)
**Timeline**: [Duration]  
**Activities**:
- [Activity 1]
- [Activity 2]
- [Activity 3]

**Deliverables**:
- [Deliverable 1]
- [Deliverable 2]

### Phase 2: [Phase Name] (Timeline)
**Timeline**: [Duration]  
**Activities**:
- [Activity 1]
- [Activity 2]

**Deliverables**:
- [Deliverable 1]
- [Deliverable 2]

### Phase 3: [Phase Name] (Timeline)
**Timeline**: [Duration]  
**Activities**:
- [Activity 1]
- [Activity 2]

**Deliverables**:
- [Deliverable 1]
- [Deliverable 2]

### Dependencies

- **Technical Dependencies**: [Libraries, frameworks, infrastructure]
- **Team Dependencies**: [Other teams or services]
- **External Dependencies**: [Third-party services, vendors]

### Risks & Mitigation

| Risk | Technical Impact | Likelihood | Mitigation Strategy |
|------|------------------|------------|---------------------|
| [Risk 1] | [Impact] | [High/Medium/Low] | [Mitigation] |
| [Risk 2] | [Impact] | [High/Medium/Low] | [Mitigation] |
| [Risk 3] | [Impact] | [High/Medium/Low] | [Mitigation] |

---

## 💰 Cost Analysis

### Infrastructure Costs

**Development Environment**:
- [Cost item 1]: $X/month
- [Cost item 2]: $Y/month
- **Total Dev**: $Z/month

**Production Environment**:
- [Cost item 1]: $X/month
- [Cost item 2]: $Y/month
- **Total Prod**: $Z/month

### Development Costs

**One-Time Costs**:
- Implementation: [Duration] × [Team size] × [Rate] = $X
- Testing: [Duration] × [Team size] × [Rate] = $Y
- Training: [Duration] × [Team size] × [Rate] = $Z
- **Total One-Time**: $X+Y+Z

**Ongoing Costs**:
- Maintenance: [Hours/month] × [Rate] = $X/month
- Monitoring: $Y/month
- **Total Ongoing**: $Z/month/year

### Benefits

**Performance Improvement**:
- [Benefit 1]: [Value]
- [Benefit 2]: [Value]

**Operational Efficiency**:
- [Benefit 1]: [Value]
- [Benefit 2]: [Value]

**Total Annual Benefit**: $X/year

### ROI Calculation

- **Total Investment**: $X (one-time) + $Y/year (ongoing) = $Z/year
- **Expected Return**: $A/year
- **Net Benefit**: $(A - Z)/year
- **ROI**: [(A - Z) / Z × 100] = X% annually
- **Payback Period**: X months

---

## 🔗 Related Decisions

### Related Architecture Decisions
- `ADR-XXX`: [Related decision title] ([How related])
- `ADR-XXX`: [Related decision title] ([How related])

### Related Policies
- `POL-XXX`: [Related policy] ([How this decision implements/complies with policy])
- `POL-XXX`: [Related policy] ([How this decision implements/complies with policy])

### Related Product Decisions
- `PDR-XXX`: [Related decision] ([How this decision supports product decision])
- `PDR-XXX`: [Related decision] ([How this decision supports product decision])

---

## 📚 References

### Internal Documentation
- `doc/architecture/[relevant-doc].md`
- `doc/domain-models/[relevant-model].md`
- `doc/reference/SBPF/[relevant-guide].md`

### External Resources
- [Technology documentation]: [URL]
- [Research paper/article]: [URL]
- [Best practices guide]: [URL]

---

## 🔄 Review History

| Date | Reviewer | Changes | Reason |
|------|----------|---------|--------|
| [YYYY-MM-DD] | [Name] | Initial version | [Reason] |

---

## 📝 Appendix

### Code Examples

**Example 1: [Scenario]**

```java
// Example demonstrating this decision
[Code example]
```

**Why this works**: [Explanation]

---

**Example 2: [Anti-pattern to avoid]**

```java
// ❌ Don't do this - violates this ADR
[Code example]
```

**Why this is wrong**: [Explanation]

---

### ArchUnit Rules

**Enforcement Rule** (if applicable):

```java
@ArchTest
public static final ArchRule [rule_name] = 
    classes()
        .that().[condition]
        .should().[requirement]
        .because("[reason - reference this ADR]");
```

**Example Violations**:

```java
// ❌ Violation example
[Code that violates]

// ✅ Compliant example
[Code that complies]
```

---

### Performance Benchmarks

**Benchmark Setup**: [How benchmark was run]

**Results**:

| Scenario | Baseline | With This Decision | Improvement |
|----------|----------|-------------------|-------------|
| [Scenario 1] | [Metric] | [Metric] | [%] |
| [Scenario 2] | [Metric] | [Metric] | [%] |
| [Scenario 3] | [Metric] | [Metric] | [%] |

---

### Migration Guide

**For existing code**:

1. [Step 1: Identify affected code]
2. [Step 2: Refactor approach]
3. [Step 3: Testing approach]
4. [Step 4: Deployment approach]

**Timeline**: [Expected migration timeline]

---

**Review Cadence**: [Quarterly | Annually | As needed]  
**Next Review Date**: [YYYY-MM-DD]  
**Decision Owner**: [Architect name]  
**Contact**: [Email or Slack channel]
