# 📋 Project Charter Template
## Dual-Purpose Documentation for Human Stakeholders & AI-Assisted Development

---

```yaml
# MACHINE-READABLE METADATA (For Copilot/AI parsing)
# Place this YAML frontmatter at the top for structured context
charter:
  id: PROJECT-ID-YYYY-QN                    # e.g., AUTH-RBAC-2026-Q1
  version: 1.0.0
  status: [draft|active|completed|archived]
  created_date: YYYY-MM-DD
  last_updated: YYYY-MM-DD
  
owners:
  product_owner: name@company.com
  tech_lead: name@company.com
  architect: name@company.com
  scrum_master: name@company.com
  
affected_domains:
  bounded_contexts: [Context1, Context2, Context3]    # DDD bounded contexts
  services: [Service1, Service2]                      # Microservices/modules
  teams: [TeamA, TeamB]                               # Owning teams
  
strategic_alignment:
  company_okr: "Q2-2026-OKR-03"                       # Link to company OKR
  product_initiative: "Initiative Name"
  
mandates:
  ubiquitous_language_keys: [CoreConcept1, CoreConcept2]  # DDD ubiquitous language
  architectural_constraints: [Constraint1, Constraint2]    # Non-negotiable constraints
  testing_requirements: [Unit, Integration, E2E, BDD, PropertyBased]
  security_requirements: [AuthN, AuthZ, DataEncryption]
  
dependencies:
  upstream_services: [ServiceA, ServiceB]             # Services we depend on
  downstream_services: [ServiceC, ServiceD]           # Services that depend on us
  infrastructure: [Postgres, Kafka, Redis]            # Infrastructure components
  
timeline:
  estimated_duration_weeks: 12
  milestones:
    - name: "Milestone 1"
      target_date: YYYY-MM-DD
    - name: "Milestone 2"
      target_date: YYYY-MM-DD
```

---

## 🎯 Executive Summary (At a Glance)

**In 3 sentences, what is this project?**
_[Write a concise summary that answers: What problem? What solution? What impact?]_

**Example:**
> The Authentication & Authorization system is currently tightly coupled with legacy LDAP, preventing modern OAuth2/OIDC integration and blocking mobile app development. This project decouples authentication by introducing a new Identity Service with JWT token-based auth and RBAC. Impact: Enable mobile apps, reduce auth latency by 60%, and unblock 3 product initiatives blocked by auth limitations.

| Key Metric | Current State | Target State | Success Criteria |
|------------|---------------|--------------|------------------|
| **Problem Impact** | [Quantify pain] | N/A | N/A |
| **Primary Goal** | Baseline | Target | Definition |
| **Timeline** | N/A | [X weeks/months] | Milestones hit on schedule |
| **Budget** | N/A | [Estimated cost] | Within 10% of estimate |

---

## I. 🧭 Conception: Problem & Goals

### 1.1 Problem Statement

**What is broken, missing, or inadequate?**

- **Current Situation:** _[Describe the as-is state with specific examples]_
  - Example: "Product search API returns results in 3-5 seconds for catalogs >10K products"
  - Example: "Manual deployment process requires 4 hours and has 20% failure rate"

- **Pain Points:** _[Who is affected and how?]_
  - **Users:** [Specific user impact with data]
  - **Business:** [Revenue/cost/opportunity impact]
  - **Engineering:** [Technical debt, scalability, maintainability]

- **Root Cause:** _[Why does this problem exist?]_
  - Technical: [Architecture limitations, tech debt]
  - Process: [Manual workflows, lack of automation]
  - Business: [Market changes, new requirements]

**Evidence:**
- Metrics: [Performance data, error rates, user complaints]
- Incidents: [Production issues, escalations]
- Feedback: [Stakeholder quotes, survey data]

---

### 1.2 Strategic Goals (OKRs)

**Map to company/product OKRs:**

#### Objective: _[What do we want to achieve? (Qualitative)]_
_Example: "Improve platform scalability to support 10x user growth"_

#### Key Results: _[How will we measure success? (Quantitative)]_

| # | Key Result | Baseline | Target | Measurement |
|---|------------|----------|--------|-------------|
| KR1 | [Specific outcome] | [Current value] | [Target value] | [How measured] |
| KR2 | [Specific outcome] | [Current value] | [Target value] | [How measured] |
| KR3 | [Specific outcome] | [Current value] | [Target value] | [How measured] |

**Example:**
| # | Key Result | Baseline | Target | Measurement |
|---|------------|----------|--------|-------------|
| KR1 | Reduce API P95 latency | 500ms | <200ms | Prometheus metrics |
| KR2 | Increase concurrent users supported | 1K | 10K | Load test results |
| KR3 | Reduce infrastructure cost per user | $5/user | $0.50/user | AWS billing data |

---

### 1.3 Stakeholder Impact

| Stakeholder Group | Current Pain | Expected Benefit | Success Metric |
|-------------------|--------------|------------------|----------------|
| **End Users** | [Problem they face] | [How this helps] | [User metric] |
| **Product Team** | [Blocker/limitation] | [Capability unlocked] | [Feature velocity] |
| **Engineering** | [Tech debt/toil] | [Developer experience] | [Dev productivity] |
| **Business** | [Revenue/cost impact] | [Financial benefit] | [$ saved/earned] |
| **Compliance/Legal** | [Risk/liability] | [Risk mitigation] | [Audit result] |

---

### 1.4 Scope Boundaries

**In Scope:**
- ✅ [Specific feature/capability 1]
- ✅ [Specific feature/capability 2]
- ✅ [Specific feature/capability 3]

**Out of Scope (Explicitly):**
- ❌ [Explicitly excluded item 1] - _Reason: [Why not now]_
- ❌ [Explicitly excluded item 2] - _Reason: [Why not now]_

**Future Scope (Parking Lot):**
- 🅿️ [Deferred item 1] - _Revisit: [When/condition]_
- 🅿️ [Deferred item 2] - _Revisit: [When/condition]_

---

## II. 🏗️ Design & Architecture

### 2.1 Design Approach

**High-Level Architecture:**

```
[Include architecture diagram or ASCII art]

Example:
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   Client    │──────▶│  API Gateway│──────▶│   Service   │
│   (Mobile)  │       │   (Auth)    │       │   (Core)    │
└─────────────┘       └─────────────┘       └─────────────┘
                             │                      │
                             ▼                      ▼
                      ┌─────────────┐       ┌─────────────┐
                      │  Identity   │       │  Database   │
                      │   Service   │       │ (Postgres)  │
                      └─────────────┘       └─────────────┘
```

**Key Design Decisions:**

| Decision | Rationale | Trade-offs | Alternatives Considered |
|----------|-----------|------------|-------------------------|
| [Architecture choice] | [Why chosen] | [Pros/Cons] | [What else was considered] |
| [Technology choice] | [Why chosen] | [Pros/Cons] | [What else was considered] |
| [Pattern choice] | [Why chosen] | [Pros/Cons] | [What else was considered] |

**Example:**
| Decision | Rationale | Trade-offs | Alternatives Considered |
|----------|-----------|------------|-------------------------|
| Event-Driven Architecture with Kafka | Decouple services, enable async processing | +Scalability, -Complexity, -Eventual consistency | REST APIs (too coupled), gRPC (synchronous) |
| PostgreSQL vs MongoDB | Need ACID transactions for financial data | +Data integrity, -Flexibility for unstructured data | MongoDB (eventual consistency risk) |

---

### 2.2 Domain-Driven Design (DDD)

#### Ubiquitous Language

**Core Domain Concepts:** _[Define key terms that appear in code and conversations]_

| Term | Definition | Usage in Code |
|------|------------|---------------|
| **[Concept1]** | [Business definition] | `class Concept1 { ... }` |
| **[Concept2]** | [Business definition] | `interface Concept2Service { ... }` |

**Example:**
| Term | Definition | Usage in Code |
|------|------------|---------------|
| **Order** | A customer's request to purchase products | `public class Order extends Aggregate { ... }` |
| **Fulfillment** | The process of delivering an order | `public interface FulfillmentService { ... }` |
| **Inventory Reservation** | Holding stock for a pending order | `public class InventoryReservation extends Entity { ... }` |

#### Bounded Contexts

**Context Map:**

```
┌─────────────────────┐
│  Context A          │
│  (e.g., Ordering)   │──── Shared Kernel ────┐
└─────────────────────┘                        │
         │                                     │
    Customer-Supplier                          │
         │                                     │
         ▼                              ┌─────────────────────┐
┌─────────────────────┐                │  Context C          │
│  Context B          │                │  (e.g., Billing)    │
│  (e.g., Inventory)  │                └─────────────────────┘
└─────────────────────┘
```

| Bounded Context | Responsibility | Team Owner | Integration Pattern |
|------------------|----------------|------------|---------------------|
| [Context1] | [What it owns] | [Team] | [Customer-Supplier/Shared Kernel/etc] |
| [Context2] | [What it owns] | [Team] | [Customer-Supplier/Shared Kernel/etc] |

---

### 2.3 Architectural Mandates (Non-Negotiable)

These are **hard constraints** that all code must follow:

1. **[Mandate 1]:** _[Specific requirement]_
   - **Validation:** [How this will be enforced (code review, linter, CI check)]
   - **Example:** "All API endpoints must validate user authentication before data access"
   - **Enforcement:** Pre-merge CI check runs security scanner

2. **[Mandate 2]:** _[Specific requirement]_
   - **Validation:** [How this will be enforced]
   - **Example:** "All database queries must include tenant_id in WHERE clause"
   - **Enforcement:** SQL linter fails on missing tenant_id filter

3. **[Mandate 3]:** _[Specific requirement]_
   - **Validation:** [How this will be enforced]

---

### 2.4 Technology Stack

| Layer | Technology | Version | Justification |
|-------|------------|---------|---------------|
| **Frontend** | [Framework] | vX.Y | [Why chosen] |
| **Backend** | [Language/Framework] | vX.Y | [Why chosen] |
| **Database** | [Database] | vX.Y | [Why chosen] |
| **Message Queue** | [Queue] | vX.Y | [Why chosen] |
| **Cache** | [Cache] | vX.Y | [Why chosen] |
| **Infrastructure** | [Platform] | vX.Y | [Why chosen] |

**Example:**
| Layer | Technology | Version | Justification |
|-------|------------|---------|---------------|
| **Backend** | Java + Pekko | 21 / 1.0.x | Actor model for concurrency, non-blocking I/O |
| **Database** | PostgreSQL | 16.x | ACID compliance, JSON support, reactive drivers |
| **Message Queue** | Apache Kafka | 3.6.x | High throughput, event sourcing, replayability |
| **Observability** | OpenTelemetry + Prometheus | 1.x | Vendor-neutral, free/open-source |

---

### 2.5 API Contracts

**Define API specifications for services:**

```yaml
# Example: OpenAPI snippet
paths:
  /orders/{orderId}:
    get:
      summary: Get order by ID
      parameters:
        - name: orderId
          in: path
          required: true
          schema:
            type: string
            format: uuid
        - name: X-Tenant-ID
          in: header
          required: true
          schema:
            type: string
            format: uuid
      responses:
        '200':
          description: Order found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Order'
        '403':
          description: Tenant isolation violation
        '404':
          description: Order not found
```

**Or link to full API spec:** `[Link to OpenAPI/GraphQL/Protobuf file]`

---

### 2.6 Data Models

**Database Schema Changes:**

```sql
-- Example: Schema migration
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE INDEX idx_orders_tenant ON orders(tenant_id);
CREATE INDEX idx_orders_customer ON orders(customer_id, tenant_id);
```

**Domain Models:**

```java
// Example: Aggregate root
public class Order extends AggregateRoot {
    private final OrderId id;
    private final TenantId tenantId;  // Ubiquitous language
    private final CustomerId customerId;
    private OrderStatus status;
    private Money total;
    
    // Business methods
    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException();
        }
        this.status = OrderStatus.CONFIRMED;
        registerEvent(new OrderConfirmed(id, tenantId));
    }
}
```

---

## III. 🚀 Execution & Validation

### 3.1 Behavior-Driven Development (BDD)

**Acceptance Criteria as Gherkin Scenarios:**

```gherkin
Feature: Order Placement with Tenant Isolation

  Scenario: Successfully place an order
    Given a valid customer with ID "CUST-123" in tenant "TENANT-A"
    And products are available in inventory
    When the customer submits an order with 2 items
    Then the order should be created with status "PENDING"
    And an OrderPlaced event should be published to Kafka
    And the inventory should be reserved for the order

  Scenario: Prevent cross-tenant data access
    Given an order with ID "ORDER-456" belongs to tenant "TENANT-B"
    When a user from tenant "TENANT-A" attempts to access order "ORDER-456"
    Then the API should return 403 Forbidden
    And no order data should be returned
    And a security event should be logged

  Scenario: Handle inventory depletion gracefully
    Given a product with only 1 unit in stock
    And two customers attempt to order that product simultaneously
    When both orders are submitted
    Then only one order should succeed
    And the other order should fail with "INSUFFICIENT_INVENTORY"
    And the failed customer should receive a notification
```

**Link Scenarios to Implementation:**
- Scenario 1 → Sub-Project 3, Task 2.3 → `src/test/resources/features/order-placement.feature`
- Scenario 2 → Sub-Project 1, Task 1.2 → `src/test/resources/features/tenant-isolation.feature`

---

### 3.2 Test-Driven Development (TDD)

**Testing Strategy:**

| Test Type | Coverage Target | Tooling | Responsibility |
|-----------|----------------|---------|----------------|
| **Unit Tests** | 80% line coverage | JUnit 5, Mockito | Developers (during implementation) |
| **Integration Tests** | All API endpoints | Karate, Testcontainers | Developers + QA |
| **Contract Tests** | All service boundaries | Pact, Spring Cloud Contract | Developers |
| **E2E Tests** | Critical user journeys | Karate, Selenium | QA |
| **Performance Tests** | SLA thresholds | Gatling, JMeter | Performance team |
| **Security Tests** | OWASP Top 10 | OAST, DAST | Security team |

**TDD Workflow:**

```
1. RED: Write failing test that defines expected behavior
   └─▶ Example: testOrderPlacement_ShouldFailForInsufficientInventory()

2. GREEN: Write minimal code to make test pass
   └─▶ Example: Add inventory check in OrderService.placeOrder()

3. REFACTOR: Improve code quality while keeping tests green
   └─▶ Example: Extract InventoryValidator, apply DDD patterns
```

---

### 3.3 Sub-Projects & Milestones

**Break down work into incremental deliverables:**

| # | Sub-Project | Scope | Dependencies | Est. Weeks | Deliverables |
|---|-------------|-------|--------------|------------|--------------|
| 1 | [Name] | [What's included] | [Pre-requisites] | [Duration] | [Outputs] |
| 2 | [Name] | [What's included] | [Pre-requisites] | [Duration] | [Outputs] |
| 3 | [Name] | [What's included] | [Pre-requisites] | [Duration] | [Outputs] |

**Example:**

| # | Sub-Project | Scope | Dependencies | Est. Weeks | Deliverables |
|---|-------------|-------|--------------|------------|--------------|
| 1 | Tenant Management Service | Create tenant CRUD APIs, lifecycle management | None | 3 | API deployed, Postman collection, E2E tests |
| 2 | Auth/RBAC Tenant Awareness | Add Tenant-ID to JWT tokens, RBAC policies | Sub-Project 1 | 4 | Token validation, RBAC rules, integration tests |
| 3 | Product Catalog Multi-Tenancy | Add Tenant-ID to catalog tables, API updates | Sub-Project 2 | 6 | Schema migration, API updates, BDD scenarios passing |

---

### 3.4 Definition of Done (DoD)

**Checklist for each sub-project:**

- [ ] **Code Complete:**
  - [ ] All user stories implemented and reviewed
  - [ ] Code follows architecture mandates (see Section 2.3)
  - [ ] No critical/high severity bugs

- [ ] **Testing Complete:**
  - [ ] Unit tests: 80%+ coverage, all passing
  - [ ] Integration tests: All BDD scenarios green
  - [ ] Performance tests: Meet SLA targets (see Section 4.2)
  - [ ] Security tests: No critical vulnerabilities

- [ ] **Documentation Complete:**
  - [ ] API documentation updated (OpenAPI/Swagger)
  - [ ] Runbooks created for operations
  - [ ] Architecture Decision Records (ADRs) written

- [ ] **Deployment Ready:**
  - [ ] CI/CD pipeline configured
  - [ ] Monitoring/alerting set up
  - [ ] Rollback plan documented

- [ ] **Acceptance:**
  - [ ] Product Owner sign-off
  - [ ] Stakeholder demo completed
  - [ ] Production deployment approved

---

## IV. 📊 Success Metrics & Governance

### 4.1 Key Performance Indicators (KPIs)

| KPI | Target | Measurement Frequency | Owner | Dashboard Link |
|-----|--------|----------------------|-------|----------------|
| [Metric 1] | [Target value] | [Daily/Weekly/Monthly] | [Team/Person] | [Grafana/etc link] |
| [Metric 2] | [Target value] | [Daily/Weekly/Monthly] | [Team/Person] | [Grafana/etc link] |

**Example:**

| KPI | Target | Measurement Frequency | Owner | Dashboard Link |
|-----|--------|----------------------|-------|----------------|
| API P95 Latency | <200ms | Real-time | Platform Team | [Grafana Dashboard] |
| Error Rate | <0.1% | Real-time | Platform Team | [Grafana Dashboard] |
| Tenant Provisioning Time | <1 hour | Per provisioning | DevOps | [Internal Tool] |
| Security Audit Score | 100% compliance | Monthly | Security Team | [Compliance Portal] |

---

### 4.2 Service Level Objectives (SLOs)

| SLO | SLI (Measurement) | Target | Time Window | Error Budget |
|-----|-------------------|--------|-------------|--------------|
| Availability | Successful requests / Total requests | 99.9% | 30 days | 0.1% (43 min downtime/month) |
| Latency | P95 response time < threshold | 95% < 200ms | 7 days | 5% can exceed |
| Throughput | Requests per second | 1000 rps | 1 day | - |

**SLO Monitoring:**
- Alerts trigger when error budget 50% consumed
- Post-mortem required when error budget fully consumed
- SLO review quarterly with stakeholders

---

### 4.3 Risk Management

| Risk | Likelihood | Impact | Mitigation Strategy | Owner | Status |
|------|------------|--------|---------------------|-------|--------|
| [Risk 1] | [H/M/L] | [H/M/L] | [How to prevent/mitigate] | [Person] | [Open/Mitigated] |
| [Risk 2] | [H/M/L] | [H/M/L] | [How to prevent/mitigate] | [Person] | [Open/Mitigated] |

**Example:**

| Risk | Likelihood | Impact | Mitigation Strategy | Owner | Status |
|------|------------|--------|---------------------|-------|--------|
| Data migration errors | Medium | High | 1) Test migrations in staging<br>2) Incremental rollout<br>3) Rollback plan | DevOps Lead | Mitigated |
| Performance degradation | High | Medium | 1) Load testing before launch<br>2) Auto-scaling configured<br>3) Caching layer | Backend Lead | Open |
| Security vulnerabilities | Low | High | 1) OWASP scanning in CI<br>2) Penetration testing<br>3) Security review | Security Team | Mitigated |

---

### 4.4 Dependencies & Blockers

**External Dependencies:**

| Dependency | Type | Status | ETA | Impact if Delayed | Mitigation |
|------------|------|--------|-----|-------------------|------------|
| [System/Team/Resource] | [Tech/Team/etc] | [On Track/At Risk/Blocked] | [Date] | [Impact description] | [Mitigation plan] |

**Example:**

| Dependency | Type | Status | ETA | Impact if Delayed | Mitigation |
|------------|------|--------|-----|-------------------|------------|
| Kafka upgrade to 3.6 | Infrastructure | On Track | 2026-02-15 | Cannot use new features | Use feature flags, backward compatibility |
| Legal approval for data schema | Compliance | At Risk | TBD | Cannot deploy to production | Parallel legal review process |

---

### 4.5 Decision Log (ADRs - Architecture Decision Records)

| ADR # | Decision | Date | Status | Consequences |
|-------|----------|------|--------|--------------|
| [ADR-001] | [Decision title] | YYYY-MM-DD | [Accepted/Rejected/Superseded] | [Key impacts] |

**Example:**

| ADR # | Decision | Date | Status | Consequences |
|-------|----------|------|--------|--------------|
| ADR-001 | Use Pekko Actors for business logic | 2026-01-10 | Accepted | +Supervision, -Learning curve |
| ADR-002 | Schema-per-tenant (not DB-per-tenant) | 2026-01-15 | Accepted | +Cost efficient, -Noisy neighbor risk |
| ADR-003 | Use Karate for API testing | 2026-01-20 | Accepted | +BDD alignment, -Scala expertise needed |

**Link to full ADRs:** `docs/adr/`

---

## V. 🤝 Collaboration & Communication

### 5.1 Team Structure

| Role | Name | Responsibility | Time Commitment |
|------|------|----------------|-----------------|
| Product Owner | [Name] | Requirements, prioritization, acceptance | [%/hours per week] |
| Tech Lead | [Name] | Architecture, code review, technical decisions | [%/hours per week] |
| Scrum Master | [Name] | Process facilitation, blocker removal | [%/hours per week] |
| Developers | [Names] | Implementation, testing, documentation | [%/hours per week] |
| QA Engineer | [Name] | Test strategy, automation, quality gates | [%/hours per week] |
| DevOps Engineer | [Name] | CI/CD, infrastructure, monitoring | [%/hours per week] |

---

### 5.2 Communication Plan

| Ceremony | Frequency | Duration | Participants | Purpose |
|----------|-----------|----------|--------------|---------|
| Sprint Planning | Every 2 weeks | 2 hours | Full team | Plan sprint work |
| Daily Standup | Daily | 15 min | Developers + SM | Sync progress, blockers |
| Sprint Review | Every 2 weeks | 1 hour | Team + Stakeholders | Demo completed work |
| Retrospective | Every 2 weeks | 1 hour | Full team | Process improvement |
| Tech Sync | Weekly | 30 min | Tech Lead + Architects | Architecture decisions |
| Stakeholder Update | Bi-weekly | 30 min | PO + Key Stakeholders | Status, risks, decisions |

---

### 5.3 RACI Matrix

| Activity | Product Owner | Tech Lead | Developers | QA | DevOps | Stakeholders |
|----------|---------------|-----------|------------|----|----- --|--------------|
| Requirements Definition | A | C | I | C | I | R |
| Architecture Design | C | A/R | C | I | C | I |
| Implementation | I | R | A/R | C | C | I |
| Testing | C | C | A/R | A/R | C | I |
| Deployment | A | C | C | C | A/R | I |
| Production Support | I | C | R | R | A/R | I |

**Legend:** R = Responsible, A = Accountable, C = Consulted, I = Informed

---

## VI. 📚 Appendices

### Appendix A: Glossary

| Term | Definition | Reference |
|------|------------|-----------|
| [Term 1] | [Definition] | [Section/Link] |
| [Term 2] | [Definition] | [Section/Link] |

---

### Appendix B: References

- **Existing Documentation:**
  - [Link to current architecture docs]
  - [Link to API specifications]
  - [Link to runbooks]

- **External Resources:**
  - [Link to relevant standards/frameworks]
  - [Link to vendor documentation]

- **Related Projects:**
  - [Link to related project charters]

---

### Appendix C: Code Pattern Examples

**For Copilot: Include reference implementations**

```java
// Example: Tenant Context Propagation Pattern
public class TenantContext {
    private static final ThreadLocal<TenantId> CURRENT_TENANT = new ThreadLocal<>();
    
    public static void setTenant(TenantId tenantId) {
        CURRENT_TENANT.set(tenantId);
    }
    
    public static TenantId getTenant() {
        TenantId tenant = CURRENT_TENANT.get();
        if (tenant == null) {
            throw new SecurityException("No tenant context available");
        }
        return tenant;
    }
    
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}

// Example: Repository with Tenant Isolation
public class OrderRepository {
    private final PgPool pgPool;
    
    public CompletionStage<Order> findById(OrderId orderId) {
        TenantId tenantId = TenantContext.getTenant();  // Enforce tenant context
        
        return pgPool.preparedQuery(
            "SELECT * FROM orders WHERE id = $1 AND tenant_id = $2"
        )
        .execute(Tuple.of(orderId.value(), tenantId.value()))
        .thenApply(rows -> {
            if (!rows.iterator().hasNext()) {
                throw new OrderNotFoundException(orderId);
            }
            return OrderMapper.fromRow(rows.iterator().next());
        });
    }
}
```

---

## VII. 📝 Change History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0.0 | YYYY-MM-DD | [Name] | Initial charter |
| 1.1.0 | YYYY-MM-DD | [Name] | Added sub-project 4 |
| 1.2.0 | YYYY-MM-DD | [Name] | Updated KPIs based on Q1 results |

---

## ✅ Charter Sign-Off

| Stakeholder | Role | Approval Date | Signature |
|-------------|------|---------------|-----------|
| [Name] | Product Owner | YYYY-MM-DD | _[Initials]_ |
| [Name] | Tech Lead | YYYY-MM-DD | _[Initials]_ |
| [Name] | Engineering Manager | YYYY-MM-DD | _[Initials]_ |
| [Name] | VP Engineering | YYYY-MM-DD | _[Initials]_ |

---

## 🎓 How to Use This Charter

### For Human Stakeholders:
1. **Start with Executive Summary** - Get the 30-second overview
2. **Read Section I** - Understand the problem and goals
3. **Skim Section II** - Get architectural context (deep dive as needed)
4. **Focus on Section III** - Track execution progress
5. **Monitor Section IV** - Review metrics and risks

### For Copilot/AI:
1. **Parse YAML frontmatter** - Extract structured metadata
2. **Index ubiquitous language** - Build domain vocabulary
3. **Extract BDD scenarios** - Generate test code
4. **Reference architectural mandates** - Validate code against constraints
5. **Use code patterns** - Suggest implementations based on examples
6. **Track dependencies** - Prioritize suggestions based on what's complete

### For Teams:
- **Version control** this charter in Git alongside code
- **Update regularly** (at least every sprint)
- **Link from JIRA/Confluence** for discoverability
- **Use as single source of truth** for project context
- **Refine iteratively** based on learnings

---

**This template combines:**
- ✅ Human readability (clear structure, visual tables, examples)
- ✅ Machine parseability (YAML metadata, structured data)
- ✅ DDD principles (ubiquitous language, bounded contexts)
- ✅ BDD/TDD alignment (Gherkin scenarios, test strategy)
- ✅ Agile practices (incremental delivery, DoD, RACI)
- ✅ Enterprise governance (ADRs, SLOs, risk management)

Use this as a starting point and adapt to your organization's needs!
