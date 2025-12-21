# GitHub Copilot Instructions: Phase 1.2 - Ubiquitous Language Workshop
## Role: Architect (Co-Led with Product Owner)

---

## 🎯 Your Role in This Ceremony

As **Architect** (co-leading with Product Owner), you facilitate the **Ubiquitous Language Workshop**, transforming event storming outputs into a formal, shared vocabulary. This glossary becomes the single source of truth for all code, scenarios, and documentation—eliminating ambiguity and translation layers.

---

## 📋 Ceremony Overview

**Phase**: Phase 1 - Discovery (DDD-Led)  
**Ceremony**: 1.2 - Ubiquitous Language Workshop  
**Led by**: Architect + Product Owner (co-led)  
**Supported by**: Bench Developer (implementation constraints), Program Manager (cross-team conflicts)  
**When**: Bi-weekly during active development, monthly during maintenance  
**Duration**: 2-4 hours  
**Cadence**: Bi-weekly (active dev) or monthly (maintenance)

---

## 🎯 Ceremony Goals

1. Extract terms from event storming output
2. Define each term with clear meaning and examples
3. Resolve conflicts (same term, different meanings)
4. Ban problematic terms and enforce replacements
5. Commit glossary to Git—these terms MUST appear in code and scenarios
6. Create living glossary updated as domain understanding evolves

---

## 📥 Inputs Required

- **Event storming output**: Domain events, commands, aggregates
- **Product Owner**: Business definitions and examples
- **Existing codebase**: Terms already in use (if not greenfield)
- **Cross-team conflicts**: Terms with multiple meanings

---

## 📤 Outputs You'll Create

| Artifact | Template | Location | Purpose |
|----------|----------|----------|---------|
| Ubiquitous Language glossary | `UBIQUITOUS-LANGUAGE-TEMPLATE.md` | `doc/domain-models/ubiquitous-language.md` | Single source of truth for terminology |
| Terminology policy (if conflicts) | `POL-TEMPLATE.md` | `doc/governance/POL/POL-XXX-terminology.md` | Cross-team term resolution |

---

## 🔨 How to Execute This Ceremony

### Step 1: Extract Terms from Event Storming (30 minutes)

**Actions**:
1. Review event storming output
2. List all domain events, commands, and aggregates
3. Identify nouns (entities, value objects) and verbs (actions)
4. Extract business terms from Product Owner discussions
5. Flag terms needing clarification

**Copilot Prompt Examples**:
```
"Extract domain terms from these events: TenantProvisioned, TenantActivated, TenantSuspended"

"Identify entities and value objects from event storming: Tenant, CompanyName, TenantStatus"

"Create initial glossary entries for: Tenant, Provisioning, Activation, Suspension"
```

**Term Categories**:
- **Entities**: Tenant, Order, User (have identity, mutable)
- **Value Objects**: CompanyName, EmailAddress, Money (no identity, immutable)
- **Commands**: ProvisionTenant, ActivateTenant (actions)
- **Events**: TenantProvisioned, TenantActivated (facts)
- **Aggregates**: Tenant (consistency boundaries)

---

### Step 2: Define Each Term (90 minutes)

**Actions**:
1. For each term, ask Product Owner: "What does this mean in our business?"
2. Write clear, concise definition (1-3 sentences)
3. Provide concrete examples (2-3 per term)
4. Note any constraints or invariants
5. Link related terms

**Definition Template**:
```markdown
### Tenant
**Type**: Aggregate  
**Definition**: A customer organization that uses our SaaS platform. Each tenant has isolated data and configuration.  
**Examples**:
- "acme-corp" tenant has 50 users and uses EMEA region
- "globex-inc" tenant suspended due to payment failure  
**Invariants**:
- Tenant company name must be globally unique
- Active tenant must have valid subscription
**Related Terms**: CompanyName, TenantStatus, Subscription
```

**Copilot Prompt Examples**:
```
"Write a glossary entry for 'Tenant' including definition, examples, and invariants"

"Define 'CompanyName' as a value object with validation rules"

"Create glossary entry for 'TenantStatus' enum with all possible states"
```

---

### Step 3: Resolve Conflicts (60 minutes)

**Actions**:
1. Identify terms with multiple meanings across teams
2. Product Owner provides business context for each usage
3. Architect proposes resolution:
   - Rename to be specific: "Account" → "TenantAccount" vs "BillingAccount"
   - Choose one meaning, ban the other
   - Qualify with context: "Order (Sales)" vs "Order (Fulfillment)"
4. Document resolution in terminology policy (POL)
5. Update glossary with chosen terms

**Conflict Example**:
```markdown
## Conflict: "Account"

**Problem**: "Account" used by 3 teams with different meanings
- Tenant team: Account = Tenant (customer org)
- Billing team: Account = Payment method (credit card)
- Auth team: Account = User account (login)

**Resolution**: Ban "Account", use specific terms
- Tenant → Use "Tenant" (not Account)
- Payment method → Use "PaymentMethod" (not Account)
- User account → Use "User" (not Account)

**Policy**: POL-022 - Terminology Conflict Resolution
```

**Copilot Prompt Examples**:
```
"Identify conflicts when 'Account' is used in tenant, billing, and auth contexts"

"Generate alternative names for 'Account' in tenant management: Tenant, TenantAccount, CustomerAccount"

"Write a policy (POL) resolving the 'Account' terminology conflict"
```

---

### Step 4: Ban Problematic Terms (30 minutes)

**Actions**:
1. Identify generic technical jargon: "Manager", "Handler", "Service", "DTO", "Entity"
2. Ban these in domain code (allowed in infrastructure/adapters only)
3. Replace with domain-specific terms:
   - "TenantManager" → "TenantProvisioner" or "TenantRegistry"
   - "OrderHandler" → "OrderFulfillment" or "Order" (aggregate)
4. Document banned terms in glossary
5. Enforce via ArchUnit rules

**Banned Terms Examples**:
```markdown
## Banned Terms (Use Domain Language Instead)

| Banned Term | Why | Use Instead |
|-------------|-----|-------------|
| Manager | Generic, meaningless | Specific role: TenantProvisioner, OrderProcessor |
| Handler | Too technical | Aggregate name: Order, Tenant |
| Service | Overused, vague | Context: OrderFulfillment, BillingCalculation |
| DTO | Implementation detail | Command: ProvisionTenantCommand, Query: TenantView |
| Entity | JPA term, not domain | Aggregate: Tenant, Value Object: CompanyName |
```

**Copilot Prompt Examples**:
```
"Replace 'TenantManager' with domain-specific term for tenant provisioning"

"Generate ArchUnit rule to ban classes with 'Manager' suffix in domain package"

"Suggest domain language alternatives for OrderHandler, PaymentService, UserEntity"
```

---

### Step 5: Document in Ubiquitous Language Template (60 minutes)

**Actions**:
1. Use `doc/reference/templates/UBIQUITOUS-LANGUAGE-TEMPLATE.md`
2. Organize terms by category:
   - Aggregates
   - Entities
   - Value Objects
   - Commands
   - Events
   - Enums/States
3. Include definitions, examples, invariants
4. Link related terms
5. Add banned terms section

**Copilot Prompt Examples**:
```
"Populate UBIQUITOUS-LANGUAGE-TEMPLATE.md with these terms: Tenant, CompanyName, TenantStatus, ProvisionTenant, TenantProvisioned"

"Organize glossary into sections: Aggregates, Value Objects, Commands, Events"

"Generate Mermaid class diagram showing relationships between Tenant, CompanyName, and TenantStatus"
```

---

### Step 6: Validate with Code Examples (30 minutes)

**Actions**:
1. Show how terms appear in code (Java examples)
2. Validate naming conventions with Bench Developer
3. Ensure terms match code exactly (no translation)
4. Add code examples to glossary

**Code Example in Glossary**:
```markdown
### ProvisionTenant (Command)
**Definition**: Command to create a new tenant with specified company name.

**Java Code**:
```java
public record ProvisionTenant(CompanyName companyName, ActorRef<Response> replyTo) 
    implements TenantCommand {}
```

**Usage**:
```java
tenantActor.tell(new ProvisionTenant(
    CompanyName.of("acme-corp"), 
    getContext().getSelf()
));
```
```

**Copilot Prompt Examples**:
```
"Generate Java record for ProvisionTenant command using ubiquitous language"

"Create code example showing Tenant aggregate using CompanyName value object"

"Write Actor message definitions for TenantCommand hierarchy using sealed interfaces"
```

---

## ✅ Definition of Done

This ceremony is complete when:

- [ ] All domain terms extracted and documented (20-50 terms typical)
- [ ] Each term has clear definition + 2-3 examples
- [ ] Conflicts resolved and documented
- [ ] Banned terms list created
- [ ] Ubiquitous language glossary committed to Git
- [ ] Product Owner validates business accuracy
- [ ] Bench Developer confirms implementability
- [ ] Code examples added for key terms
- [ ] ArchUnit rules created to enforce terminology

---

## 🚨 Common Pitfalls to Avoid

### Pitfall 1: Technical Jargon Creep
**Problem**: Glossary full of "DTO", "repository", "manager", "handler"  
**Solution**: Ban technical terms in domain code. Use business language.

### Pitfall 2: One-Time Glossary
**Problem**: Glossary created once, never updated  
**Solution**: Bi-weekly updates during active dev. Review in Domain Model Retrospective.

### Pitfall 3: Synonym Proliferation
**Problem**: Same concept called 3 different names across teams  
**Solution**: Resolve in this ceremony. One term per concept. Document in POL.

### Pitfall 4: Vague Definitions
**Problem**: "Tenant is a customer" (not helpful)  
**Solution**: Be specific: "A customer organization with isolated data, unique company name, and subscription."

### Pitfall 5: Glossary Doesn't Match Code
**Problem**: Glossary says "CompanyName", code uses "tenantName"  
**Solution**: Glossary IS the contract. Code must match exactly. No translation.

---

## 🔄 Ceremony Relationships

### Upstream (Inputs to This Ceremony)
- **Phase 1.1 - Event Storming**: Domain events, commands, aggregates

### Downstream (This Ceremony Feeds Into)
- **Phase 1.3 - Domain Modeling**: Terms become class names
- **Phase 2.1 - Three Amigos**: Terms appear in BDD scenarios
- **Phase 3.1 - Test-First Pairing**: Terms appear in test names
- **All code**: Every class, method, variable uses these terms

---

## 📊 Success Metrics

| Metric | Target | How to Measure |
|--------|--------|----------------|
| Terms documented | 20-50 per context | Count in glossary |
| Conflicts resolved | 100% | Zero unresolved conflicts |
| Code alignment | 100% | ArchUnit tests pass |
| Update frequency | Bi-weekly | Git commit history |
| Team usage | Referenced daily | Team surveys |

---

## 🛠️ Tools & Templates

### Required Templates
- `doc/reference/templates/UBIQUITOUS-LANGUAGE-TEMPLATE.md`
- `doc/reference/templates/POL-TEMPLATE.md` (for terminology conflicts)

### ArchUnit Enforcement
```java
@ArchTest
static final ArchRule no_manager_suffix = 
    classes().that().resideInAPackage("..domain..")
    .should().haveSimpleNameNotEndingWith("Manager");
```

---

**Next Ceremony**: Phase 1.3 - Domain Modeling Workshop (Led by Architect)  
**Handoff**: Ubiquitous language terms become aggregate, entity, and value object names
