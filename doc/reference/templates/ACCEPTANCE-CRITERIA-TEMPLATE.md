# Acceptance Criteria: [STORY-ID]
## Definition of Done for User Story

---

```yaml
# MACHINE-READABLE METADATA
acceptance_criteria:
  story_id: STORY-XXX
  story_title: Short story title
  created_date: YYYY-MM-DD
  approved_date: YYYY-MM-DD
  approved_by: product.owner@company.com
```

---

## 📋 User Story

**As a** [role]  
**I want** [capability]  
**So that** [business value]

**Story ID**: STORY-XXX  
**Priority**: High | Medium | Low  
**Sprint**: Sprint X

---

## ✅ Acceptance Criteria

### Criterion 1: [Criterion Title]

**Given** [preconditions]  
**When** [action]  
**Then** [expected outcome]

**Examples**:
- Example 1: Given company name "acme-corp" is not in use, When admin provisions tenant with "acme-corp", Then tenant is created with status PROVISIONING
- Example 2: Given company name "a" (too short), When admin provisions tenant with "a", Then provisioning is rejected with error "Name must be 3-50 characters"

**Verification Method**: [ ] Manual Test  [X] Automated Test (BDD scenario)

**BDD Scenario**: [Link to scenario in features/tenant-provisioning.feature]

---

### Criterion 2: [Another Criterion]

**Given** [preconditions]  
**When** [action]  
**Then** [expected outcome]

**Examples**:
- Example 1: ...

**Verification Method**: [X] Automated Test

**BDD Scenario**: [Link to scenario]

---

### Criterion 3: [Edge Case Criterion]

**Given** [preconditions]  
**When** [action]  
**Then** [expected outcome]

**Examples**:
- Example 1: ...

**Verification Method**: [X] Automated Test

**BDD Scenario**: [Link to scenario]

---

## 🚫 Out of Scope (Not Acceptance Criteria)

**What is NOT included in this story**:
- Feature X will be handled in STORY-YYY
- Edge case Y will be deferred to Sprint Z
- Integration with system Z is future work

---

## 📊 Non-Functional Requirements

| NFR | Target | Verification Method |
|-----|--------|---------------------|
| **Response Time** | Provisioning completes within 10 seconds | Performance test |
| **Availability** | 99.9% uptime | Monitoring dashboard |
| **Security** | Only admins can provision tenants | RBAC test |
| **Usability** | Error messages are user-friendly | UX review |

---

## 🧪 Test Cases

### Manual Test Cases

| Test # | Description | Steps | Expected Result | Status |
|--------|-------------|-------|-----------------|--------|
| TC-1 | Provision valid tenant | 1. Login as admin<br>2. Navigate to provisioning<br>3. Enter "acme-corp"<br>4. Submit | Tenant created | ⏸️ Pending |
| TC-2 | Reject duplicate name | 1. Provision "acme-corp"<br>2. Attempt to provision "acme-corp" again | Error: "Name already in use" | ⏸️ Pending |

### Automated Test Cases (BDD)

| Scenario | Feature File | Status |
|----------|--------------|--------|
| Provision tenant with valid name | [features/tenant-provisioning.feature](../../../features/tenant-provisioning.feature#L10) | ✅ Passing |
| Reject duplicate company name | [features/tenant-provisioning.feature](../../../features/tenant-provisioning.feature#L20) | ✅ Passing |
| Reject invalid name formats | [features/tenant-provisioning.feature](../../../features/tenant-provisioning.feature#L30) | 🔴 Failing |

---

## 📝 Definition of Done Checklist

- [ ] All acceptance criteria have passing BDD scenarios
- [ ] Code reviewed and approved
- [ ] Unit tests written (>80% coverage)
- [ ] Integration tests written (if applicable)
- [ ] Manual testing completed (if applicable)
- [ ] NFRs verified
- [ ] Documentation updated (API docs, user guide)
- [ ] Deployed to staging environment
- [ ] Product Owner approval

---

## 🔗 Related Documentation

- **User Story**: [Link to Jira/Linear]
- **Example Map**: [doc/scenarios/example-maps/STORY-XXX-examples.md](../example-maps/STORY-XXX-examples.md)
- **BDD Scenarios**: [features/tenant-provisioning.feature](../../../features/tenant-provisioning.feature)
- **Domain Model**: [doc/domain-models/aggregates/tenant-aggregate.md](../../domain-models/aggregates/tenant-aggregate.md)
- **Service Charter**: [doc/services/tenant-provisioning/SERVICE-CHARTER.md](../../services/tenant-provisioning/SERVICE-CHARTER.md)

---

**Ceremony Type**: Three Amigos Specification (Phase 2: Specification)  
**Created By**: Product Owner  
**Reviewed By**: Architect, Developer, QA  
**Approved**: YYYY-MM-DD
