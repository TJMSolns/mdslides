# GitHub Copilot Instructions: Phase 1.4 - Context Mapping
## Role: Architect

**Led by**: Architect | **Supported by**: Program Manager (dependencies), Bench Developer (integration complexity)

## 🎯 Goals
Define relationships between bounded contexts and choose integration patterns.

## 📥 Key Inputs
- Bounded context candidates from event storming
- Domain models (aggregates)
- System dependencies

## 📤 Key Outputs
- Context map (`CONTEXT-MAP-TEMPLATE.md` → `doc/domain-models/context-maps/`)
- Integration contracts (`INTEGRATION-CONTRACT-TEMPLATE.md` → `doc/architecture/integration-contracts/`)
- Mermaid context diagram

## 🔨 Core Activities
1. **Identify All Bounded Contexts**: Typically 2-5 per system
2. **Map Relationships**: Upstream/downstream, customer/supplier
3. **Choose Integration Patterns**:
   - Published Language (domain events via Kafka)
   - Anticorruption Layer (protect domain from external models)
   - Conformist (small downstream adapts to large upstream)
   - Shared Kernel (same team, shared code - use sparingly)
4. **Define Contracts**: Event schemas, API contracts

## 🚨 Critical Rules
- **No shared databases** between contexts
- **No direct dependencies** between domain models
- Integration via **events or APIs only**

## 🔍 Infrastructure Scope Validation

**CRITICAL CHECK:** Validate that infrastructure references in context map match actual implementation.

### Validation Checklist

For each infrastructure technology mentioned in context map:

1. **Check build.sc dependencies:**
   ```bash
   grep -i "postgresql\|redis\|kafka\|elasticsearch" build.sc
   ```

2. **Check docker-compose.yml services:**
   ```bash
   grep -A 2 "services:" docker-compose.yml | grep -E "^  [a-z]"
   ```

3. **Cross-reference ADRs:**
   ```bash
   find doc/governance/ADR -name "*redis*" -o -name "*postgresql*" | xargs grep "Status: Accepted"
   ```

### Common Issue: Ghost Infrastructure

**Problem:** Context map references infrastructure NOT in implementation.

**Example (Tenant Management Service - BUG-003):**
- Context map referenced: PostgreSQL, Kafka, **Redis**, **S3**, Elasticsearch
- Actual implementation: PostgreSQL, Kafka, OpenSearch only
- ADR-042 "Redis for Caching": Status "Accepted" but Redis NOT in build.sc or docker-compose.yml

**Resolution:**
- Created ADR-072 (Elasticsearch → OpenSearch migration)
- Removed Redis and S3 from context map
- Updated infrastructure scope to match reality

### Validation Rule

**For each technology in context map:**
- ✅ **Valid:** Technology in (build.sc OR docker-compose.yml) AND ADR status "Accepted"
- ❌ **Invalid:** Technology in context map + ADR "Accepted" BUT NOT in build.sc or docker-compose.yml
  - **Action:** Either remove from context map OR implement (add dependencies)

**Why This Matters:**
- Prevents scope creep (teams implement undocumented infrastructure)
- Maintains governance integrity (ADRs match reality)
- Prevents misleading documentation

**Future:** `mill contextMappingValidate` command will automate this check (see [FRAMEWORK-ENHANCEMENTS.md](../doc/planning/FRAMEWORK-ENHANCEMENTS.md) ENH-003)

---

## ✅ Definition of Done
- [ ] All context relationships mapped
- [ ] Integration patterns chosen and documented
- [ ] Contracts defined for each integration
- [ ] Mermaid context map created
- [ ] ACL boundaries identified
- [ ] **Infrastructure scope validated** (context map vs build.sc vs docker-compose.yml)

**Next**: Phase 2.1 - Three Amigos (Product Owner leads specification)
