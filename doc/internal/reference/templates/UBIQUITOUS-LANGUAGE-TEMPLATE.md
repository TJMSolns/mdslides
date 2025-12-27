# Ubiquitous Language: [CONTEXT-NAME]
## Living Glossary of Domain Terms

---

```yaml
# MACHINE-READABLE METADATA
glossary:
  bounded_context: ContextName
  version: 1.0.0
  created_date: YYYY-MM-DD
  last_updated: YYYY-MM-DD
  
ownership:
  domain_architect: architect@company.com
  product_owner: product.owner@company.com
```

---

## 📖 Purpose

This living glossary defines the **ubiquitous language** for the **[Context Name]** bounded context. All team members (product, dev, QA, stakeholders) must use these terms consistently in:

- Conversations
- User stories
- BDD scenarios (Given/When/Then)
- Code (class names, method names, variable names)
- Documentation

**Philosophy**: The language of the domain IS the language of the code.

---

## 🗂️ Glossary

### Core Entities

#### Tenant

**Definition**: A logically isolated instance of the system provisioned for a single customer organization.

**Synonyms** (AVOID): Account, Customer, Organization, Company

**Properties**:
- `TenantId`: Unique identifier (UUID)
- `CompanyName`: Human-readable unique name (slug format: `acme-corp`)
- `TenantStatus`: Lifecycle state (PROVISIONING, ACTIVE, SUSPENDED, TERMINATED)

**Examples**:
- ✅ "The tenant 'acme-corp' is in ACTIVE status"
- ❌ "The account 'ACME Corp' is enabled" (wrong term + wrong name format)

**Related Terms**: TenantConfiguration, TenantStatus

**Code References**:
```java
public class Tenant { // Class name matches domain term
    private TenantId id;
    private CompanyName companyName;
    private TenantStatus status;
}
```

**BDD Usage**:
```gherkin
Given a tenant with company name "acme-corp"
When the tenant is activated
Then the tenant status is "ACTIVE"
```

---

#### TenantConfiguration

**Definition**: The set of customizable settings that control behavior for a specific tenant.

**Synonyms** (AVOID): Settings, Preferences, Options

**Properties**:
- `ConfigId`: Unique identifier for the configuration
- `Settings`: Map of key-value pairs (e.g., `{ "maxUsers": "100", "features": "premium" }`)

**Examples**:
- ✅ "Update the tenant configuration to enable premium features"
- ❌ "Change the tenant settings"

**Related Terms**: Tenant

**Business Rules**:
- Configuration can only be modified when tenant is ACTIVE
- Invalid settings are rejected at command boundary

---

### Value Objects

#### CompanyName

**Definition**: A unique, URL-safe identifier representing the customer's company name.

**Synonyms** (AVOID): TenantName, OrganizationName, AccountName

**Format**: `^[a-z0-9]+(?:-[a-z0-9]+)*$` (lowercase alphanumeric with hyphens, 3-50 chars)

**Examples**:
- ✅ `acme-corp`, `big-tech-inc`, `startup123`
- ❌ `Acme Corp` (spaces), `ACME_CORP` (uppercase, underscore), `ab` (too short)

**Validation Rules**:
- Must be unique across all tenants
- Length: 3-50 characters
- Only lowercase letters, numbers, and hyphens
- Cannot start or end with hyphen

**Code References**:
```java
public class CompanyName {
    private final String value;
    
    public CompanyName(String value) {
        if (!value.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
            throw new InvalidCompanyNameException(value);
        }
        this.value = value;
    }
}
```

---

#### TenantStatus

**Definition**: The lifecycle state of a tenant.

**Allowed Values**:
- **PROVISIONING**: Tenant is being set up (validation in progress)
- **ACTIVE**: Tenant is operational and can process requests
- **SUSPENDED**: Tenant is temporarily disabled (e.g., non-payment)
- **TERMINATED**: Tenant is permanently deleted (irreversible)

**State Transitions**:
```
PROVISIONING → ACTIVE
PROVISIONING → TERMINATED (on failure)
ACTIVE → SUSPENDED
SUSPENDED → ACTIVE
SUSPENDED → TERMINATED
ACTIVE → TERMINATED
```

**Examples**:
- ✅ "Suspend the active tenant"
- ❌ "Disable the tenant" (use "suspend" for temporary, "terminate" for permanent)

---

### Commands (Operations)

#### ProvisionTenant

**Definition**: Create a new tenant with the given company name and configuration.

**Synonyms** (AVOID): CreateTenant, RegisterTenant, SetupTenant

**Input**:
- `CompanyName`: Unique company name
- `TenantConfiguration`: Initial configuration settings

**Output**:
- `TenantId`: ID of the newly created tenant

**Preconditions**:
- Company name must be unique
- Configuration must be valid

**Side Effects**:
- Publishes `TenantProvisioningRequested` event
- Initiates async validation

**Examples**:
- ✅ "Provision a tenant for company name 'acme-corp'"
- ❌ "Create an account for ACME Corp"

**Code References**:
```java
public TenantId provisionTenant(CompanyName companyName, TenantConfiguration config) {
    // Implementation
}
```

---

#### ActivateTenant

**Definition**: Transition a tenant from PROVISIONING or SUSPENDED state to ACTIVE state.

**Synonyms** (AVOID): EnableTenant, StartTenant

**Preconditions**:
- Tenant status must be PROVISIONING or SUSPENDED

**Side Effects**:
- Publishes `TenantActivated` event
- Notifies billing system

---

#### SuspendTenant

**Definition**: Temporarily disable a tenant (reversible).

**Synonyms** (AVOID): DisableTenant, PauseTenant

**Input**:
- `Reason`: Why the tenant is being suspended (e.g., "Non-payment")

**Preconditions**:
- Tenant status must be ACTIVE

**Side Effects**:
- Publishes `TenantSuspended` event
- Blocks all tenant requests (except admin operations)

---

#### TerminateTenant

**Definition**: Permanently delete a tenant (irreversible).

**Synonyms** (AVOID): DeleteTenant, RemoveTenant

**Preconditions**:
- Tenant status must be ACTIVE or SUSPENDED
- Tenant must have no active users (or use `forceTerminate` flag)

**Side Effects**:
- Publishes `TenantTerminated` event
- Triggers cleanup in downstream systems (billing, user management)

---

### Domain Events

#### TenantProvisioningRequested

**Definition**: A request has been made to provision a new tenant.

**Payload**:
- `tenantId`: ID of the new tenant
- `companyName`: Requested company name
- `config`: Initial configuration
- `timestamp`: When the request was made

**Consumers**:
- Validation Service (validates company name uniqueness)
- Audit Log (records provisioning attempts)

---

#### TenantActivated

**Definition**: A tenant has been transitioned to ACTIVE status.

**Payload**:
- `tenantId`: ID of the activated tenant
- `timestamp`: When activation occurred

**Consumers**:
- Billing System (start billing cycle)
- User Management (allow user creation)
- Notification Service (send welcome email)

---

### Business Rules

#### Rule: Tenant Name Uniqueness

**Statement**: A company name can only be used by one tenant in the system.

**Enforcement**: ProvisionTenant command

**Examples**:
- ✅ First tenant with "acme-corp" succeeds
- ❌ Second tenant with "acme-corp" is rejected with error: "Company name already in use"

**Rationale**: Company names are used in URLs (e.g., `https://acme-corp.saas.com`) and must be globally unique.

---

#### Rule: Configuration Changes Require ACTIVE State

**Statement**: A tenant's configuration can only be modified when the tenant is ACTIVE.

**Enforcement**: TenantConfiguration.update() method

**Examples**:
- ✅ Update config for ACTIVE tenant → succeeds
- ❌ Update config for PROVISIONING tenant → rejected with error: "Tenant must be ACTIVE to modify configuration"

**Rationale**: Ensures configuration changes don't interfere with provisioning process.

---

## 🚫 Banned Terms (Do NOT Use)

| Banned Term | Use Instead | Reason |
|-------------|-------------|--------|
| **Account** | Tenant | "Account" is overloaded (billing account, user account) |
| **Customer** | Tenant | "Customer" refers to the business entity, not the system instance |
| **Organization** | Tenant | Too generic, not specific to our domain |
| **Enable/Disable** | Activate/Suspend | Not clear if reversible or permanent |
| **Delete** | Terminate | "Delete" implies simple removal, not full cleanup |

---

## 📝 Terminology Conflicts & Resolutions

### Conflict: "Account" Ambiguity

**Problem**: "Account" was used to mean:
- Tenant instance (dev team)
- Billing account (finance team)
- User account (UX team)

**Resolution**: 
- Use **Tenant** for system instance
- Use **BillingAccount** for finance context
- Use **UserAccount** for authentication context

**Documented In**: [POL-XXX-terminology-policy.md](../../governance/POL/POL-XXX-terminology-policy.md)

---

### Conflict: "Provisioning" vs "Onboarding"

**Problem**: Product team used "onboarding", dev team used "provisioning"

**Resolution**: 
- **Provisioning**: Technical setup (infrastructure, database, config)
- **Onboarding**: User-facing process (welcome emails, tutorials)

**Rationale**: Separates technical concerns from UX concerns

---

## 🔄 Changelog

| Date | Change | Author | Rationale |
|------|--------|--------|-----------|
| 2025-12-01 | Initial glossary created | architect@company.com | Event storming session output |
| 2025-12-05 | Added TenantConfiguration entity | architect@company.com | Domain modeling workshop |
| 2025-12-08 | Clarified "Account" conflict | product.owner@company.com | Cross-team terminology alignment |

---

## 🔗 Related Documentation

- **Event Storming**: [doc/domain-models/event-storming/tenant-context-events.md](../event-storming/tenant-context-events.md)
- **Aggregates**: [doc/domain-models/aggregates/tenant-aggregate.md](../aggregates/tenant-aggregate.md)
- **BDD Scenarios**: [features/tenant-provisioning.feature](../../../features/tenant-provisioning.feature)
- **Service Charter**: [doc/services/tenant-provisioning/SERVICE-CHARTER.md](../../services/tenant-provisioning/SERVICE-CHARTER.md)
- **Terminology Policy**: [doc/governance/POL/POL-XXX-terminology-policy.md](../../governance/POL/POL-XXX-terminology-policy.md)

---

**Ceremony Type**: Ubiquitous Language Workshop (Phase 1: Discovery)  
**Last Updated**: YYYY-MM-DD  
**Domain Architect**: architect@company.com  
**Product Owner**: product.owner@company.com

---

## 📚 How to Use This Glossary

### For Developers
- Use these exact terms in class names, method names, variable names
- Validate code in code reviews: "Does this code speak the ubiquitous language?"
- When in doubt, check this glossary before naming things

### For Product Owners
- Use these terms in user stories and acceptance criteria
- Correct team members who use banned terms
- Propose new terms through Ubiquitous Language Workshop

### For QA/BDD Practitioners
- Use these terms in Gherkin scenarios (Given/When/Then)
- Validate scenario language during Acceptance Criteria Review
- Report language inconsistencies as defects

### For New Team Members
- Read this glossary during onboarding
- Ask questions when terms are unclear
- Suggest improvements if definitions are ambiguous
