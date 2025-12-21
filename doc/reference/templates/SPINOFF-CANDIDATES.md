# SPINOFF-CANDIDATES.md Template

Example template for documenting services ready for spinoff from training repository.

---

# Spinoff Candidates

## Overview

This document tracks bounded contexts within this service that are candidates for spinning off to independent production repositories under `RETISIO/*`.

**Spinoff Process**: See `doc/reference/SBPF/Service-Repository-Spinoff-Process.md`  
**Mill Plugin**: https://github.com/RETISIO/mill-spinoff-plugin

---

## Status Indicators

- 🟢 **Not Ready**: Service does not yet meet spinoff criteria (< 80% readiness)
- 🟡 **Monitoring**: Service meets most criteria, actively monitoring readiness (80-99%)
- 🔴 **Triggered**: Service is READY for spinoff (100% readiness, all 13 validation checks passed)
- ✅ **Complete**: Service has been spun off to production repository **AND source directory deleted from training repo**

---

## Post-Spinoff Lifecycle

After successful spinoff execution:

### Step 1: Verify Production Repository
```bash
curl -H "Authorization: token $GITHUB_TOKEN" \
  https://api.github.com/repos/RETISIO/${service-name}-service
```

### Step 2: Delete Source Directory (MANDATORY)
**⚠️ CRITICAL**: Delete source from training repository to avoid dual source of truth.

```bash
rm -rf services/${service-name}/
git add -A
git commit -m "Remove ${ServiceName} after spinoff to RETISIO/${service-name}-service"
git push origin main
```

**Rationale**: The training repository contains:
- ✅ Charters (business requirements)
- ✅ Ceremonies (process guides)
- ✅ Templates (reusable patterns)
- ✅ Tooling (mill-spinoff-plugin)
- ❌ **NOT** service implementations (those live in production repos)

### Step 3: Update This Document
Change status from 🔴 Triggered → ✅ Complete, add:
- **Repository**: Link to `RETISIO/${service-name}-service`
- **Spun Off**: Date of spinoff
- **Spinoff ADR**: Link to ADR-001 in production repo

**See**: [POL-029-mill-spinoff-plugin-usage.md](../../doc/governance/POL/POL-029-mill-spinoff-plugin-usage.md) - Section 5: Post-Spinoff Cleanup

---

## Spinoff Readiness Criteria (13 Checks)

**IMPORTANT**: These checks validate "ready to spin off" NOT "ready for production".
Ceremonies (Event Storming, Three Amigos, TDD) happen AFTER spinoff in the production repository.

Services must have STRUCTURE and SCAFFOLDING in place (even if marked TODO/scaffold):

1. ✅ **Charter exists** - Problem statement, success criteria, team assignment
2. ✅ **Domain model structure exists** - At least 1 aggregate-*.md file (can be marked "scaffold")
3. ✅ **Context map exists** - High-level upstream/downstream relationships (can be draft)
4. ✅ **BDD scenario structure exists** - At least 1 .feature file (can contain TODOs)
5. ✅ **Test structure exists** - At least 1 test class (can throw UnsupportedOperationException)
6. ✅ **Integration approach documented** - Context map defines integration points
7. ✅ **API approach defined in charter** - OpenAPI spec created during Phase 2 (post-spinoff)
8. ✅ **Event-driven approach noted** - Event schemas created during Phase 1 (post-spinoff)
9. ✅ **Database migration structure exists** - At least 1 V1__*.sql file (can be placeholder)
10. ✅ **Observability approach noted** - OpenTelemetry configured during Phase 3 (post-spinoff)
11. ✅ **Security approach noted** - AuthN/AuthZ documented during Phase 2 (post-spinoff)
12. ✅ **Deployment structure exists** - k8s/*.yaml files (can be placeholders)
13. ✅ **Team ownership assigned** - Specified in charter or CODEOWNERS

---

## Current Candidates

### 1. Tenant Management Service

- **Status**: 🔴 Triggered (Ready for spinoff)
- **Trigger Conditions**: All 13 validation checks passed - has scaffold structure, team assigned, boundaries defined
- **Charter**: [CHARTER-01-TENANT-MANAGEMENT.md](../../CHARTER-01-TENANT-MANAGEMENT.md)
- **Priority**: 🔴 Critical (Blocks: Authentication, Product Catalog, Activity Logger)
- **Aggregates** (draft): 3 (Tenant, Subscription, BillingAccount) - refined during Phase 1 in production repo
- **Entities** (draft): 2 (TenantAdmin, ServiceQuota) - refined during Phase 1 in production repo
- **Value Objects** (draft): 6 (TenantId, CompanyName, TenantStatus, SubscriptionTier, BillingCycle, QuotaLimits)
- **Domain Events** (draft): 5 (TenantProvisioned, TenantActivated, TenantSuspended, SubscriptionUpgraded, QuotaExceeded)
- **Readiness**: 100% (13/13 checks passed - scaffold structure complete)
- **Team**: @RETISIO/platform-team
- **Upstream Contexts**: None (foundation service)
- **Downstream Contexts**: Authentication (Conformist), Product Catalog (ACL), Activity Logger (OHS)
- **Post-Spinoff Ceremonies**: Phase 1-3 ceremonies executed in `RETISIO/tenant-management-service` repo
- **Duration**: 4 weeks (includes all ceremonies)
- **Phase**: 1 - Foundation
- **Repository**: (To be created by Mill Spinoff Plugin)
- **Spun Off**: (Pending execution)

**Next Steps**:
```bash
mill commerceCoreService.spinoffValidate TenantManagement
mill commerceCoreService.spinoffExecute TenantManagement
```

---

### 2. Authentication & RBAC Service

- **Status**: 🟡 Monitoring (Near ready, waiting for Tenant Management completion)
- **Trigger Conditions**: 12/13 validation checks passed (Tenant Management must be deployed first)
- **Charter**: [CHARTER-02-AUTHENTICATION-RBAC.md](../../CHARTER-02-AUTHENTICATION-RBAC.md)
- **Priority**: 🔴 Critical (Blocks: Product Catalog, Shopping Cart, all downstream services)
- **Aggregates**: 4 (User, Role, Permission, Session)
- **Entities**: 2 (TenantUser, APIKey)
- **Value Objects**: 7 (UserId, Email, PasswordHash, JWTToken, RoleName, PermissionScope, SessionId)
- **Domain Events**: 6 (UserAuthenticated, UserLoggedOut, RoleAssigned, PermissionGranted, SessionExpired, APIKeyRevoked)
- **Readiness**: 92% (12/13 checks passed)
- **Team**: @RETISIO/platform-team
- **Upstream Contexts**: Tenant Management (Conformist)
- **Downstream Contexts**: All services (Published Language - JWT tokens)
- **Duration**: 4 weeks
- **Phase**: 1 - Foundation
- **Repository**: (Not yet created)
- **Spun Off**: N/A

**Blocking Issues**:
- [ ] Wait for Tenant Management service deployment to production
- [ ] Complete integration contract with Tenant Management (tenant validation endpoint)

---

### 3. Digital Media Service

- **Status**: 🟢 Not Ready (Domain model in progress)
- **Trigger Conditions**: 8/13 validation checks passed
- **Charter**: [CHARTER-03-DIGITAL-MEDIA.md](../../CHARTER-03-DIGITAL-MEDIA.md)
- **Priority**: 🟡 Medium (Blocks: Content Service)
- **Aggregates**: 2 (DigitalAsset, MediaFolder)
- **Entities**: 1 (AssetMetadata)
- **Value Objects**: 5 (AssetId, S3Key, MimeType, FileSize, TenantStorageQuota)
- **Domain Events**: 4 (AssetUploaded, AssetDeleted, FolderCreated, QuotaExceeded)
- **Readiness**: 61% (8/13 checks passed)
- **Team**: @RETISIO/content-team
- **Upstream Contexts**: Tenant Management (ACL - quota enforcement)
- **Downstream Contexts**: Content (Conformist), Product Catalog (ACL - product images)
- **Duration**: 4 weeks
- **Phase**: 1 - Foundation (can run parallel with Tenant Management)
- **Repository**: (Not yet created)
- **Spun Off**: N/A

**Blocking Issues**:
- [ ] Complete domain model (add asset versioning, CDN integration)
- [ ] Write unit tests (currently 3 test classes, need ≥5)
- [ ] Create BDD scenarios (currently 2 `.feature` files, need ≥5)
- [ ] Define event schemas (Avro for Kafka events)
- [ ] Create database migrations (Flyway `V1__create_assets_table.sql`)

---

### 4. Product Catalog Service

- **Status**: 🟢 Not Ready (Most complex service, requires extensive modeling)
- **Trigger Conditions**: 5/13 validation checks passed (early stage)
- **Charter**: [CHARTER-04-PRODUCT-CATALOG.md](../../CHARTER-04-PRODUCT-CATALOG.md)
- **Priority**: 🔴 Critical (Blocks: Pricing, Inventory, Shopping Cart, Catalog Search, Activity Logger)
- **Aggregates**: 5 (Product, Category, Variant, AttributeSet, ProductLifecycle)
- **Entities**: 4 (SKU, CategoryHierarchy, AttributeValue, PriceGroup)
- **Value Objects**: 12 (ProductId, SKU, ProductName, Description, CategoryPath, VariantKey, AttributeName, AttributeType, WorkflowStatus, PublishDate, ExpirationDate, TenantCatalogScope)
- **Domain Events**: 10 (ProductCreated, ProductPublished, ProductRetired, VariantAdded, CategoryAssigned, AttributeUpdated, LifecycleTransitioned, ProductDuplicated, BulkImportCompleted, TenantCatalogSynced)
- **Readiness**: 38% (5/13 checks passed)
- **Team**: @RETISIO/commerce-core-team
- **Upstream Contexts**: Authentication (Conformist), Digital Media (ACL - product images)
- **Downstream Contexts**: Pricing (Published Language), Inventory (Shared Kernel - SKU), Shopping Cart (ACL), Catalog Search (OHS)
- **Duration**: 6 weeks (longest service)
- **Phase**: 3 - Core Commerce
- **Repository**: (Not yet created)
- **Spun Off**: N/A

**Blocking Issues**:
- [ ] Complete event storming (only 40% done)
- [ ] Model DRAFT→STAGE→LIVE workflow per tenant
- [ ] Write comprehensive unit tests (currently 0 test classes)
- [ ] Create BDD scenarios for complex workflows (currently no `.feature` files)
- [ ] Define all event schemas (10 domain events)
- [ ] Create database migrations (complex schema with 8+ tables)
- [ ] Complete OpenAPI specification (currently empty)
- [ ] Document integration contracts with 6 downstream services

---

## Completed Spinoffs

_(None yet - Program starts February 2026)_

### Template for Completed Spinoffs:

```markdown
### N. ${Service Name}

- **Status**: ✅ Complete
- **Repository**: [RETISIO/${service-name}-service](https://github.com/RETISIO/${service-name}-service)
- **Spun Off**: YYYY-MM-DD
- **Spinoff ADR**: [ADR-001](https://github.com/RETISIO/${service-name}-service/blob/main/doc/governance/ADR/ADR-001-spinoff-from-training-repository.md)
- **Team**: @RETISIO/${team-name}
- **Source Deleted**: ✅ Yes (removed from training repo on YYYY-MM-DD)
- **Charter**: Still maintained in training repo at [CHARTER-XX-${SERVICE-NAME}.md](../../CHARTER-XX-${SERVICE-NAME}.md)
```

---

## Validation Commands

### List all candidates:
```bash
mill commerceCoreService.spinoffList
```

### Validate specific service:
```bash
mill commerceCoreService.spinoffValidate TenantManagement
mill commerceCoreService.spinoffValidate Authentication
mill commerceCoreService.spinoffValidate DigitalMedia
mill commerceCoreService.spinoffValidate ProductCatalog
```

### Execute spinoff (after validation passed):
```bash
export GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxx
mill commerceCoreService.spinoffValidate TenantManagement   # Confirm 13/13 checks
mill commerceCoreService.spinoffExecute TenantManagement    # Create RETISIO/tenant-management-service
```

---

## Metrics

**Total Candidates**: 12 (from Multi-Tenancy Enablement Program Charter)  
**Triggered (Ready)**: 1 (Tenant Management)  
**Monitoring**: 1 (Authentication & RBAC - waiting on dependency)  
**Not Ready**: 10 (remaining services in various phases)  
**Completed**: 0 (program starts February 2026)

**Average Readiness**: 48% (across Phase 1 candidates)  
**Target**: 100% for all 12 services by October 2026 (38-week program)

---

## Review Cadence

This document is reviewed during:
- **Weekly**: Ceremony 3.2 (Red-Green-Refactor) - Update readiness percentages
- **Monthly**: Ceremony 4.2 (Domain Model Retrospective) - Assess spinoff triggers
- **Quarterly**: Ceremony 4.3 (Living Documentation Sync) - Archive completed spinoffs

---

## References

- **ADR-059**: One Repository Per Bounded Context
- **ADR-060**: Spinoff via Mill Plugin
- **POL-028**: Repository-Per-Service Mandate
- **POL-029**: Mill Spinoff Plugin Usage Policy
- **Mill Spinoff Plugin**: https://github.com/RETISIO/mill-spinoff-plugin
- **Service-Repository-Spinoff-Process**: `doc/reference/SBPF/Service-Repository-Spinoff-Process.md`
- **SERVICE-SPINOFF-TRACKING**: `doc/planning/SERVICE-SPINOFF-TRACKING.md`
