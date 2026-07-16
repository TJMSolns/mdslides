# Feature Analysis Validation Checklist

This document provides a comprehensive checklist to validate that feature analysis work has been completed correctly per the instructions in `000-feature-preprocessing.md`.

---

## Processing Order Validation

**Rule**: Features MUST be processed in strict sequential order from Tier 0 through Tier 12.

### Validation Steps:

1. **Check git commit history**:
   ```bash
   git log --oneline --grep="feat(analysis)" | tac
   ```
   - Commits should appear in tier order: Tier 0 features, then Tier 1 features, etc.
   - Feature numbers should be sequential (1/62, 2/62, 3/62...)
   - No gaps in sequence numbers

2. **Check feature-gaps.md section order**:
   ```bash
   grep "^### [0-9]" doc/feature-gaps.md
   ```
   - Section numbers should be sequential: 1, 2, 3, 4...
   - Feature names should match tier order from 000-feature-preprocessing.md

3. **Check Status fields in order**:
   ```bash
   for tier in 0 1 2 3 4 5 6 7 8 9 10 11 12; do
     echo "=== Tier $tier ==="
     # Check Status fields for this tier's features
   done
   ```

**FAIL if**: Features processed out of order, commits out of sequence, gaps in feature numbers.

---

## Step 1 Validation: Complete Feature File Reading

**Rule**: The ENTIRE .feature file must be read and understood before proceeding.

### Validation Steps:

1. **For the feature being validated**: `doc/features/<feature-name>.feature`

2. **Check that feature-gaps.md section mentions ALL scenarios**:
   ```bash
   # Count scenarios in .feature file
   grep -c "^\s*Scenario:" doc/features/<feature-name>.feature
   
   # Check feature-gaps.md lists all scenarios
   # Look for "Scenarios Supported:" section
   ```
   - Every scenario from .feature should be listed in feature-gaps.md
   - Status (✅/⚠️/❌) should be assigned to each scenario

3. **Check that .md file Overview references feature intent**:
   - Read first paragraph of Overview section in `doc/features/<feature-name>.md`
   - Verify it accurately describes the feature purpose from .feature file

4. **Spot check**: Pick 3 random steps from .feature scenarios
   - Verify corresponding endpoints appear in sequence diagrams or gaps

**FAIL if**: 
- Scenario count mismatch between .feature and feature-gaps.md
- Overview doesn't reflect feature purpose
- Random scenario steps have no corresponding endpoint documentation

---

## Step 2 Validation: All 12 OpenAPI Specs Analyzed

**Rule**: ALL 12 service OpenAPI specifications must be read for EVERY feature.

### Validation Steps:

1. **Check feature-gaps.md "What Exists" section**:
   - Should list findings from checking all 12 services
   - Even if most services have nothing, should note "No related endpoints"
   - Example format:
     ```
     **What Exists**:
     - Platform Service (8080): 3 product endpoints
     - Order Service (8081): 5 order endpoints  
     - User Service (8082): No related endpoints
     - Scheduler Service (8083): No related endpoints
     - [... all 12 listed]
     ```

2. **Verify endpoint accuracy**:
   ```bash
   # For endpoints claimed to exist in feature-gaps.md
   # Verify they actually exist in the OpenAPI spec
   
   grep "POST /api/gifting/order/v1/scheduled" impl/gifting-order/src/main/resources/openapi.yaml
   ```
   - If feature-gaps.md says an endpoint exists, it MUST be in the spec
   - Check 5 random "existing" endpoints per feature

3. **Check for false negatives**:
   - Pick 2 services that feature-gaps.md says have "no related endpoints"
   - Read their OpenAPI specs to verify they truly have nothing relevant
   - Could the analyst have missed something?

**FAIL if**:
- "What Exists" section doesn't reference all 12 services
- Claimed existing endpoints aren't in specs
- Obvious relevant endpoints marked as "none"

---

## Step 3 Validation: All Required Endpoints Identified

**Rule**: EVERY endpoint inferable from feature scenarios must be identified.

### Validation Steps:

1. **Read the .feature file scenarios**:
   - For each Given/When/Then/And step, ask: "What API call makes this happen?"

2. **Check that feature-gaps.md documents all inferred endpoints**:
   - Compare your list of inferred endpoints vs documented endpoints
   - Look in both "What Exists" and "Gaps Identified" sections

3. **Common endpoint patterns to verify**:
   - **Authentication steps** → Session/auth endpoints
   - **"I select/choose/pick"** → GET endpoints to retrieve options
   - **"I create/add/schedule"** → POST endpoints
   - **"I update/modify/change"** → PUT/PATCH endpoints
   - **"I delete/remove/cancel"** → DELETE endpoints
   - **"I should see/receive"** → GET endpoints for retrieval
   - **"System sends notification"** → POST notification endpoints

4. **Check CRUD completeness**:
   - If feature creates a resource (POST), should GET/PUT/DELETE also be documented?
   - Check if CRUD gaps are noted

**FAIL if**:
- Obvious endpoints missing from documentation
- Feature says "I should see my orders" but no GET /orders endpoint documented
- CRUD operations incomplete without explanation

---

## Step 4 Validation: Gap Analysis Completeness

**Rule**: Every needed endpoint that doesn't exist in OpenAPI specs = documented gap.

### Validation Steps:

1. **Cross-reference required vs actual**:
   ```bash
   # List all endpoints mentioned in feature-gaps.md "What Exists"
   # List all endpoints mentioned in feature-gaps.md "Gaps Identified"
   # Together, these should cover all endpoints from Step 3
   ```

2. **Verify gap documentation format**:
   - Each gap should have: HTTP verb, full path, service name
   - Each gap should have: Purpose/description
   - Each gap should reference: Which scenario/step requires it
   - Example:
     ```
     POST /api/gifting/order/v1/scheduled
       Purpose: Create a scheduled gift order
       Request: {productId, recipientId, scheduledDate, ...}
       Response: {orderId, status, scheduledFor, ...}
       Required by: Scenario "Schedule birthday gift", step "When I schedule the order"
     ```

3. **Check gap service assignment**:
   - Gap endpoint assigned to correct service by domain?
   - Order operations → Order Service (8081)
   - User operations → User Service (8082)
   - Scheduling → Scheduler Service (8083)
   - Platform delegation → Platform Service (8080)

4. **Verify no duplicate gaps**:
   ```bash
   # Check if same endpoint listed multiple times
   grep "POST /api/gifting" doc/feature-gaps.md | sort | uniq -d
   ```

**FAIL if**:
- Required endpoints exist in specs but marked as gaps
- Gaps missing purpose/description
- Gaps missing scenario reference
- Gap assigned to wrong service
- Duplicate gap documentation

---

## Step 5 Validation: feature-gaps.md Updated Cumulatively

**Rule**: feature-gaps.md grows with each feature. Never replaced, always appended.

### Validation Steps:

1. **Check section numbering is sequential**:
   ```bash
   grep "^### [0-9]" doc/feature-gaps.md | head -20
   ```
   Output should be:
   ```
   ### 1. Feature Name 1
   ### 2. Feature Name 2
   ### 3. Feature Name 3
   ### 4. Feature Name 4
   ...
   ```
   No gaps, no duplicates

2. **Verify section count matches processed features**:
   ```bash
   # Count sections in feature-gaps.md
   grep -c "^### [0-9]" doc/feature-gaps.md
   
   # Should equal number of features processed
   # If 21 features done, should be 21 sections
   ```

3. **Check each section has required subsections**:
   - **Feature Status**: ✅ FULLY IMPLEMENTED / ⚠️ PARTIALLY IMPLEMENTED / ❌ NOT IMPLEMENTED
   - **What Exists**: List of services and their relevant endpoints
   - **Scenarios Supported**: List of scenarios with ✅/⚠️/❌ status
   - **Gaps Identified**: Missing endpoints (or "No gaps" if fully implemented)
   - **Implementation Notes**: Technical notes about gaps

4. **Verify no sections removed**:
   ```bash
   git log -p doc/feature-gaps.md | grep "^-### [0-9]"
   ```
   Should be empty (no sections deleted)

**FAIL if**:
- Section numbers not sequential
- Section count doesn't match features processed
- Missing required subsections
- Evidence of deleted sections in git history
- File replaced instead of appended

---

## Step 6 Validation: Feature .md File Complete and Correct

**Rule**: Each feature must have a complete .md file following the mandatory template.

### Validation Steps:

1. **Check file exists**:
   ```bash
   ls -l doc/features/<feature-name>.md
   ```

2. **Validate header metadata**:
   ```markdown
   **Feature File**: [...] ← Should be relative path to .feature file
   **Descriptor File**: [...] ← Should be relative path to this .md file
   **Status**: Complete ← Should be "Complete" when done
   **Last Updated**: 2025-11-13 ← Should be current date
   ```

3. **Validate Overview section has 4 paragraphs**:
   ```bash
   # Count paragraphs in Overview section (before "### Applicable ADRs")
   sed -n '/^## Overview/,/^### Applicable ADRs/p' doc/features/<feature-name>.md | grep -c "^[A-Z]"
   ```
   Should be ≥ 4 (RFP, Customer, Operator, the prior organization value propositions)

4. **Validate governance lists exist and have links**:
   ```bash
   # Check ADR list
   sed -n '/^### Applicable ADRs/,/^### Applicable PDRs/p' doc/features/<feature-name>.md | grep -c "ADR-"
   
   # Check PDR list  
   sed -n '/^### Applicable PDRs/,/^### Applicable POLs/p' doc/features/<feature-name>.md | grep -c "PDR-"
   
   # Check POL list
   sed -n '/^### Applicable POLs/,/^## Platform Delegation/p' doc/features/<feature-name>.md | grep -c "POL-"
   ```
   Should have at least 1 of each type

5. **Validate governance link format**:
   ```bash
   # Check that governance links use correct path format
   grep "governance/architecture/ADR-" doc/features/<feature-name>.md
   grep "governance/processes/PDR-" doc/features/<feature-name>.md
   grep "governance/policies/POL-" doc/features/<feature-name>.md
   ```
   Format: `[ADR-XXX: Name](../governance/architecture/ADR-XXX-filename.md)`

6. **Validate Platform Delegation section exists and has content**:
   ```bash
   sed -n '/^## Platform Delegation & Dependency/,/^## Sequence Diagrams/p' doc/features/<feature-name>.md | wc -l
   ```
   Should be > 10 lines (substantial content, not just header)

7. **Validate Sequence Diagrams section**:
   ```bash
   # Count sequence diagrams
   grep -c "^### Sequence [0-9]" doc/features/<feature-name>.md
   
   # Count mermaid blocks
   grep -c "^```mermaid" doc/features/<feature-name>.md
   ```
   - Should have at least 1 sequence diagram per major scenario
   - Each sequence should have a mermaid diagram

8. **Validate Mermaid diagram constraints**:
   - Check a random diagram:
   ```bash
   sed -n '/^```mermaid/,/^```/p' doc/features/<feature-name>.md | head -20
   ```
   - **Participants**: Only User, Host Platform, and the 12 service names
     - ✅ VALID: `participant Order Service`
     - ❌ INVALID: `participant Backend`, `participant API`, `participant Database`
   - **Endpoints**: Should include HTTP verbs and paths
     - ✅ VALID: `Order Service->>Platform Service: POST /api/gifting/platform/v1/orders`
     - ✅ VALID: `POST /api/gifting/order/v1/scheduled → 201 Created`
     - ❌ INVALID: `Order Service->>Platform Service: Create order`

9. **Validate Implementation Guidance section**:
   ```bash
   sed -n '/^## Implementation Guidance/,$p' doc/features/<feature-name>.md | wc -l
   ```
   Should be > 50 lines (detailed technical guidance, not brief notes)

10. **Check for governance compliance mentions**:
    ```bash
    # Implementation Guidance should reference specific ADRs/POLs/PDRs
    sed -n '/^## Implementation Guidance/,$p' doc/features/<feature-name>.md | grep -c "ADR-\|POL-\|PDR-"
    ```
    Should be ≥ 3 (multiple governance references)

**FAIL if**:
- File doesn't exist
- Missing required sections
- Overview has < 4 paragraphs
- No governance lists or empty lists
- Governance links wrong format
- Platform Delegation section too short
- No sequence diagrams
- Mermaid diagrams violate participant constraints
- Mermaid diagrams don't use actual endpoints
- Implementation Guidance too brief
- No governance compliance guidance

---

## Step 6 Final Validation: Status Field Updated Correctly

**Rule**: Status changed to "Complete" ONLY after ALL above work done.

### Validation Steps:

1. **Check Status field**:
   ```bash
   grep "^\*\*Status\*\*:" doc/features/<feature-name>.md
   ```
   Should show: `**Status**: Complete`

2. **Verify work completion before status change**:
   ```bash
   # Check git history - Status should be last change to file
   git log -p --follow doc/features/<feature-name>.md | grep "Status"
   ```
   - Status should change from "Todo" → "Complete" in final commit
   - No other substantive changes should happen after Status change

3. **Cross-check with feature-gaps.md**:
   - If .md file Status = Complete
   - Then feature-gaps.md should have a section for this feature
   - Section number should match feature sequence number

**FAIL if**:
- Status = "Complete" but validation steps 1-5 fail
- Status changed multiple times (indicates rework/shortcuts)
- Status = "Complete" but no feature-gaps.md section
- Status = "Complete" but section numbers don't align

---

## Overall Feature Analysis Quality Checks

### 1. Consistency Check: Endpoints Match Across Documents

**Validation**:
- Pick 5 endpoints mentioned in feature-gaps.md "Gaps Identified"
- Verify they appear in the .md file's Sequence Diagrams
- Verify they appear in the .md file's Implementation Guidance

**All three documents should have consistent endpoint documentation.**

### 2. Completeness Check: All Scenarios Covered

**Validation**:
- Count scenarios in .feature file: `grep -c "^\s*Scenario:" doc/features/<feature-name>.feature`
- Count sequence diagrams in .md file: `grep -c "^### Sequence" doc/features/<feature-name>.md`
- Ratio should be reasonable (1:1 or 1:2, not 1:10)

**Each major scenario should have at least one sequence diagram.**

### 3. Accuracy Check: Existing Endpoints Actually Exist

**Validation**:
- Pick 10 random endpoints from feature-gaps.md "What Exists"
- Verify each one in the actual OpenAPI spec:
  ```bash
  grep "<path>" impl/gifting-<service>/src/main/resources/openapi.yaml
  ```

**Zero false positives allowed - if it says it exists, it must exist.**

### 4. Governance Check: ADRs/POLs/PDRs Are Relevant

**Validation**:
- Read the feature purpose
- Check listed ADRs/POLs/PDRs actually apply
- Common mistakes:
  - Listing ADR-010 (Pekko) for features with no event sourcing
  - Listing ADR-040 (Delegation) for features with no platform interaction
  - Missing POL-020 (API Consistency) when feature has REST endpoints

**Only list governance that actually applies to this specific feature.**

### 5. Readability Check: Documentation Makes Sense

**Validation**:
- Read the Overview section - is it clear what the feature does?
- Read a Sequence Diagram - can you follow the flow?
- Read Implementation Guidance - could a developer build this?

**If reviewer can't understand it, it's incomplete.**

---

## Validation Workflow for Reviewers

**For each feature N (where N = 1 to 62):**

```bash
#!/bin/bash
FEATURE_NAME="<feature-name>"  # e.g., "occasion-setup-default"

echo "=== Validating Feature: $FEATURE_NAME ==="

# Step 1: Files exist
test -f "doc/features/${FEATURE_NAME}.feature" || echo "FAIL: .feature file missing"
test -f "doc/features/${FEATURE_NAME}.md" || echo "FAIL: .md file missing"

# Step 2: Status is Complete
grep "^\*\*Status\*\*: Complete" "doc/features/${FEATURE_NAME}.md" || echo "FAIL: Status not Complete"

# Step 3: feature-gaps.md has section
grep "${FEATURE_NAME}.feature" doc/feature-gaps.md || echo "FAIL: No feature-gaps.md section"

# Step 4: Required sections exist
grep "^## Overview" "doc/features/${FEATURE_NAME}.md" || echo "FAIL: Missing Overview"
grep "^### Applicable ADRs" "doc/features/${FEATURE_NAME}.md" || echo "FAIL: Missing ADRs"
grep "^## Platform Delegation" "doc/features/${FEATURE_NAME}.md" || echo "FAIL: Missing Platform Delegation"
grep "^## Sequence Diagrams" "doc/features/${FEATURE_NAME}.md" || echo "FAIL: Missing Sequence Diagrams"
grep "^## Implementation Guidance" "doc/features/${FEATURE_NAME}.md" || echo "FAIL: Missing Implementation Guidance"

# Step 5: Has mermaid diagrams
grep -q "^```mermaid" "doc/features/${FEATURE_NAME}.md" || echo "FAIL: No mermaid diagrams"

# Step 6: Governance references present
sed -n '/^## Implementation Guidance/,$p' "doc/features/${FEATURE_NAME}.md" | grep -q "ADR-" || echo "WARN: No ADR references in guidance"

echo "=== Validation complete for $FEATURE_NAME ==="
echo ""
```

Run this for each feature to quickly identify validation failures.

---

## Summary: What "Complete" Really Means

A feature analysis is **Complete** when:

✅ The entire .feature file has been read and understood  
✅ All 12 OpenAPI specs have been analyzed  
✅ Every required endpoint has been identified  
✅ Every gap has been documented in feature-gaps.md  
✅ The .md file exists with all required sections  
✅ The .md file has 4-paragraph Overview  
✅ The .md file lists applicable ADRs/PDRs/POLs with links  
✅ The .md file describes platform delegation  
✅ The .md file has sequence diagrams using actual endpoints  
✅ The .md file has detailed implementation guidance  
✅ All documents are consistent with each other  
✅ The Status field reads "Complete"  

**Zero shortcuts. Zero compromises. This is production-quality analysis documentation.**
