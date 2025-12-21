# GitHub Copilot Instructions: Phase 2.1 - Three Amigos Specification Session
## Role: Product Owner

**Led by**: Product Owner | **Supported by**: Architect (domain alignment), Bench Developer (implementability)

## 🎯 Goals
Collaboratively write BDD scenarios in Gherkin (Given/When/Then) before implementation.

## 📥 Key Inputs
- User story
- Ubiquitous language glossary
- Domain model
- Example maps (if complex story)

## 📤 Key Outputs
- Gherkin scenarios (`FEATURE-TEMPLATE.feature` → `features/`)
- Acceptance criteria document (`ACCEPTANCE-CRITERIA-TEMPLATE.md`)

## 🔨 Core Activities
1. **Review User Story**: All 3 roles understand goal
2. **Validate CRUD/CPQ Coverage**: Cross-check scenarios against CHARTER service responsibilities (see below)
3. **Write Given/When/Then**: Use ubiquitous language exactly
4. **Happy Path First**: Main success scenario
5. **Add Error Cases**: 2-3 alternative scenarios
6. **Validate Domain Alignment**: Architect checks against model

### CRUD/CPQ Coverage Validation

**BEFORE writing scenarios**, review CHARTER.md service responsibilities and verify coverage:

- [ ] **Create operations**: Do we have scenarios for entity creation?
- [ ] **Read/Query operations**: Do we have scenarios for queries? ⚠️ **These are easy to miss!**
- [ ] **Update/Patch operations**: Do we have scenarios for field updates?
- [ ] **Delete/Archive operations**: Do we have scenarios for removal?

**Common Gap:** State-machine scenarios (provisioning, activation, etc.) are captured well, but **query scenarios are often forgotten**.

**Example (Tenant Management):**
- ✅ Captured: 22 provisioning scenarios (state transitions)
- ❌ Initially missed: 34 query scenarios (get tenant, list tenants, filter by status)
- ✅ Fixed: Created separate `tenant-metadata-queries.feature` file

**Action:** If queries are missing, return to Phase 1.1 Event Storming to add Read Models section, then create scenarios here.

## 📝 Gherkin Best Practices
```gherkin
Feature: Tenant Provisioning

Scenario: Provision tenant with valid unique name
  Given the company name "acme-corp" is not in use
  When I provision a tenant with company name "acme-corp"
  Then the tenant is created with status "PROVISIONING"
  And a "TenantProvisioningRequested" event is published
```

**Key Rules**:
- Use ubiquitous language terms exactly as in glossary
- Test through domain (not UI)
- Concrete examples (no variables like "valid name")
- One scenario = one business rule

## ✅ Definition of Done
- [ ] Happy path scenario written
- [ ] 2-3 error scenarios written
- [ ] All scenarios use ubiquitous language
- [ ] Architect validates domain alignment
- [ ] Developer confirms implementability
- [ ] Scenarios committed to `features/`

**Next**: Phase 2.3 - Acceptance Criteria Review (Architect validates)
