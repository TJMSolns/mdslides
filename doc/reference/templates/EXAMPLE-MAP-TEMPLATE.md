# Example Map: [STORY-ID]
## Concrete Examples for Business Rules

---

```yaml
# MACHINE-READABLE METADATA
example_map:
  story_id: STORY-XXX
  story_title: Short story title
  created_date: YYYY-MM-DD
  session_date: YYYY-MM-DD
  participants:
    - product.owner@company.com
    - developer1@company.com
    - qa.engineer@company.com
```

---

## 📋 User Story

**As a** [role]  
**I want** [capability]  
**So that** [business value]

**Story ID**: STORY-XXX  
**Priority**: High | Medium | Low  
**Estimated Size**: S | M | L

---

## 🎯 Rules → Examples → Questions

Example Mapping uses a simple format: **Rules** (business rules), **Examples** (concrete scenarios), **Questions** (unknowns).

### Rule 1: [Business Rule Title]

**Statement**: Clear statement of the business rule.

**Examples**:

| Example # | Given | When | Then | Priority |
|-----------|-------|------|------|----------|
| 1.1 | Company name is "acme-corp" and not in use | Admin provisions tenant with "acme-corp" | Tenant is created with status PROVISIONING | 🔴 Critical |
| 1.2 | Company name is "a" (too short) | Admin provisions tenant with "a" | Provisioning is rejected: "Name must be 3-50 characters" | 🟡 Medium |
| 1.3 | Company name is "ACME-CORP" (uppercase) | Admin provisions tenant with "ACME-CORP" | Provisioning is rejected: "Name must be lowercase" | 🟡 Medium |
| 1.4 | Company name is "acme corp" (space) | Admin provisions tenant with "acme corp" | Provisioning is rejected: "Name can only contain lowercase, numbers, hyphens" | 🟡 Medium |
| 1.5 | Company name is 51 characters long | Admin provisions tenant with 51-char name | Provisioning is rejected: "Name must be 3-50 characters" | 🟢 Low |

**Questions**:
- Q1: Can company name contain numbers (e.g., "acme123")? → **Resolved**: Yes
- Q2: Can name start with a number (e.g., "123acme")? → **Unresolved** (ask Product Owner)

---

### Rule 2: [Another Business Rule]

**Statement**: Another rule from the story.

**Examples**:

| Example # | Given | When | Then | Priority |
|-----------|-------|------|------|----------|
| 2.1 | Tenant "acme-corp" already exists | Admin provisions tenant with "acme-corp" | Provisioning is rejected: "Company name already in use" | 🔴 Critical |
| 2.2 | Tenant "acme-corp" was terminated yesterday | Admin provisions tenant with "acme-corp" | ??? (see Question Q3) | 🔴 Critical |

**Questions**:
- Q3: Can we reuse a company name after termination? → **Unresolved** (business decision needed)

---

### Rule 3: [Edge Case Rule]

**Statement**: Edge case discovered during example mapping.

**Examples**:

| Example # | Given | When | Then | Priority |
|-----------|-------|------|------|----------|
| 3.1 | Validation service is down | Admin provisions tenant | Provisioning succeeds with status "PENDING_VALIDATION" (async validation) | 🟡 Medium |
| 3.2 | Validation service times out (>5s) | Admin provisions tenant | Provisioning succeeds with status "PENDING_VALIDATION" | 🟡 Medium |

**Questions**:
- Q4: How long do we wait before timing out validation? → **Resolved**: 5 seconds (ADR-XXX)
- Q5: What if validation service is down for 24 hours? → **Unresolved** (need fallback policy)

---

## ❓ Open Questions

| # | Question | Asked By | Priority | Resolution | Resolved By |
|---|----------|----------|----------|------------|-------------|
| Q2 | Can name start with a number? | Developer | Medium | Yes, allowed | Product Owner |
| Q3 | Can we reuse terminated tenant names? | QA Engineer | High | No, names are permanent | Product Owner |
| Q5 | What if validation is down 24 hours? | Developer | High | Manual admin approval after 1 hour | Product Owner |

---

## 🎬 Scenarios to Implement (Prioritized)

Based on example mapping, these scenarios will be written in Gherkin:

| Priority | Scenario | Examples Covered | Assigned To |
|----------|----------|------------------|-------------|
| 🔴 P0 | Provision tenant with valid unique name | 1.1 | Developer 1 |
| 🔴 P0 | Reject duplicate company name | 2.1 | Developer 1 |
| 🔴 P0 | Reject terminated tenant name reuse | 2.2 | Developer 1 |
| 🟡 P1 | Reject invalid name formats | 1.2, 1.3, 1.4, 1.5 | Developer 2 |
| 🟡 P1 | Handle validation service timeout | 3.1, 3.2 | Developer 2 |
| 🟢 P2 | Manual validation after 24-hour outage | Q5 resolution | Developer 2 (Sprint 2) |

---

## 📊 Example Mapping Summary

**Total Rules**: 3  
**Total Examples**: 9  
**Resolved Questions**: 3  
**Unresolved Questions**: 1 (Q5)

**Readiness**: 
- ✅ Ready to write scenarios for P0 and P1
- ⏸️ Blocked on Q5 for P2 scenarios

---

## 🔗 Related Documentation

- **User Story**: [Link to Jira/Linear/etc.]
- **BDD Scenarios**: [features/tenant-provisioning.feature](../../../features/tenant-provisioning.feature)
- **Acceptance Criteria**: [doc/scenarios/acceptance-criteria/STORY-XXX.md](../acceptance-criteria/STORY-XXX.md)
- **Ubiquitous Language**: [doc/domain-models/ubiquitous-language.md](../../domain-models/ubiquitous-language.md)
- **Aggregate**: [doc/domain-models/aggregates/tenant-aggregate.md](../../domain-models/aggregates/tenant-aggregate.md)

---

**Ceremony Type**: Example Mapping (Phase 2: Specification)  
**Session Date**: YYYY-MM-DD  
**Facilitator**: product.owner@company.com  
**Next Steps**: Write Gherkin scenarios for P0/P1, resolve Q5 before Sprint 2

---

## 📝 Example Mapping Cheatsheet

### Card Colors (if using physical/virtual board)

- 🟡 **Yellow**: User Story (1 card)
- 🟦 **Blue**: Business Rules (multiple cards)
- 🟢 **Green**: Concrete Examples (multiple cards per rule)
- 🔴 **Pink/Red**: Questions (track unknowns)

### Example Mapping Facilitation Tips

1. **Start with the story**: Read user story aloud
2. **Extract rules**: "What are the business rules for this story?"
3. **Provide examples**: "Give me an example of when this rule applies"
4. **Surface questions**: "What don't we know yet?"
5. **Prioritize**: Mark critical examples vs nice-to-have
6. **Stop when**: Too many questions (need PO input) or too many rules (story too big, split it)

### When to Stop

- ✅ **Good to proceed**: 1-3 rules, 3-10 examples, 0-2 questions
- ⏸️ **Need more input**: >5 unresolved questions → schedule follow-up with PO
- 🛑 **Story too big**: >5 rules → split into smaller stories
