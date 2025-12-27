# Example Mapping: US-003.3 - Nested List Support

**Date:** 2024-12-27
**User Story:** US-003.3 - Nested List Support
**Status:** Ready for TDD Implementation

## Story

**As a** presentation author
**I want** to use nested lists in my slide content
**So that** I can show hierarchical information and sub-points

## Rules

### R1: Maximum Nesting Depth
Lists cannot exceed 3 levels of nesting.

### R2: Mixed Nesting Allowed
Ordered lists can contain unordered, and vice versa.

### R3: Bullet Style Hierarchy
Unordered: disc (L1) → circle (L2) → square (L3)

### R4: Number Style Hierarchy
Ordered: decimal (L1) → lower-alpha (L2) → lower-roman (L3)

### R5: Indentation Scaling
Each level indents 2em from parent.

## Examples

### Example 1: Two-Level Unordered List
**Rule:** R3, R5

```markdown
---
template: content
---
## Features

- Authentication
  - Login
  - Logout
- Authorization
  - Roles
  - Permissions
```

**Expected HTML:**
```html
<ul>
  <li>Authentication
    <ul>
      <li>Login</li>
      <li>Logout</li>
    </ul>
  </li>
  <li>Authorization
    <ul>
      <li>Roles</li>
      <li>Permissions</li>
    </ul>
  </li>
</ul>
```

**Expected CSS rendering:**
```
• Authentication (disc, 0em indent)
  ◦ Login (circle, 2em indent)
  ◦ Logout (circle, 2em indent)
• Authorization (disc, 0em indent)
  ◦ Roles (circle, 2em indent)
  ◦ Permissions (circle, 2em indent)
```

**Test assertions:**
- Parse produces nested ListItem structures
- HTML contains `<ul><li>...<ul><li>` pattern
- CSS applies `list-style-type: disc` to outer `<ul>`
- CSS applies `list-style-type: circle` to nested `<ul>`
- CSS applies `margin-left: 2em` to nested `<ul>`

---

### Example 2: Three-Level Maximum Depth
**Rule:** R1, R3

```markdown
---
template: content
---
## System Layers

- Frontend
  - Components
    - Buttons
    - Forms
  - Pages
- Backend
  - API
    - REST
    - GraphQL
```

**Expected rendering:**
```
• Frontend (disc)
  ◦ Components (circle)
    ▪ Buttons (square)
    ▪ Forms (square)
  ◦ Pages (circle)
• Backend (disc)
  ◦ API (circle)
    ▪ REST (square)
    ▪ GraphQL (square)
```

**Test assertions:**
- Validation passes (depth = 3, within limit)
- All 3 bullet styles applied correctly
- Indentation: 0em, 2em, 4em

---

### Example 3: Exceeding Maximum Depth (Validation Error)
**Rule:** R1

```markdown
---
template: content
---
## Too Deep

- Level 1
  - Level 2
    - Level 3
      - Level 4 (INVALID)
```

**Expected:**
```
✗ Validation failed:
  - Slide 1: List nesting exceeds maximum depth of 3 (found: 4)
```

**Test assertions:**
- Parser extracts all 4 levels
- Validation detects depth = 4
- Validation returns Left("...exceeds maximum depth...")
- Error message includes actual depth (4) and max depth (3)

---

### Example 4: Ordered List Nesting
**Rule:** R4

```markdown
---
template: content
---
## Process Steps

1. Planning
   1. Requirements
   2. Design
2. Implementation
   1. Development
   2. Testing
3. Deployment
```

**Expected rendering:**
```
1. Planning (decimal)
   a. Requirements (lower-alpha)
   b. Design (lower-alpha)
2. Implementation (decimal)
   a. Development (lower-alpha)
   b. Testing (lower-alpha)
3. Deployment (decimal)
```

**Test assertions:**
- HTML contains `<ol><li>...<ol><li>` pattern
- CSS applies `list-style-type: decimal` to outer `<ol>`
- CSS applies `list-style-type: lower-alpha` to nested `<ol>`

---

### Example 5: Mixed Nesting (Unordered → Ordered)
**Rule:** R2, R3, R4

```markdown
---
template: content
---
## Development Workflow

- Design Phase
  1. Wireframes
  2. Mockups
- Implementation Phase
  1. Backend
  2. Frontend
```

**Expected rendering:**
```
• Design Phase (disc)
  1. Wireframes (decimal)
  2. Mockups (decimal)
• Implementation Phase (disc)
  1. Backend (decimal)
  2. Frontend (decimal)
```

**Test assertions:**
- HTML contains `<ul><li>...<ol><li>` pattern
- Outer list uses disc bullets
- Inner lists use decimal numbering

---

### Example 6: Mixed Nesting (Ordered → Unordered)
**Rule:** R2

```markdown
---
template: content
---
## Project Phases

1. Phase One
   - Milestone A
   - Milestone B
2. Phase Two
   - Milestone C
```

**Expected rendering:**
```
1. Phase One (decimal)
   - Milestone A (circle)
   - Milestone B (circle)
2. Phase Two (decimal)
   - Milestone C (circle)
```

**Test assertions:**
- HTML contains `<ol><li>...<ul><li>` pattern
- Outer list uses decimal
- Inner lists use circle bullets

---

### Example 7: Complex Mixed Nesting (3 Levels)
**Rule:** R1, R2, R3, R4

```markdown
---
template: content
---
## Architecture

1. Frontend
   - React Components
     1. Functional
     2. Class-based
   - State Management
2. Backend
   - API Layer
     1. REST
     2. GraphQL
```

**Expected rendering:**
```
1. Frontend (decimal)
   - React Components (circle)
     1. Functional (decimal)
     2. Class-based (decimal)
   - State Management (circle)
2. Backend (decimal)
   - API Layer (circle)
     1. REST (decimal)
     2. GraphQL (decimal)
```

**Wait - this is wrong!** Ordered at L3 should be lower-alpha, not decimal.

**Corrected expected:**
```
1. Frontend (decimal)
   - React Components (circle)
     a. Functional (lower-alpha)
     b. Class-based (lower-alpha)
   - State Management (circle)
2. Backend (decimal)
   - API Layer (circle)
     a. REST (lower-alpha)
     b. GraphQL (lower-alpha)
```

**Test assertions:**
- HTML pattern: `<ol><li>...<ul><li>...<ol><li>`
- CSS: decimal → circle → lower-alpha
- Validation passes (depth = 3)

---

### Example 8: Empty Nested List Item
**Rule:** Edge case handling

```markdown
---
template: content
---
## Items

- Item with content
  -
  - Another nested item
- Item two
```

**Expected:** Parser handles gracefully, empty item may be skipped

**Test assertions:**
- No parsing error
- Empty item either skipped or rendered as `<li></li>`
- Validation passes

---

### Example 9: Single Item Nested List
**Rule:** Edge case

```markdown
---
template: content
---
## Single Nested

- Parent
  - Only child
```

**Expected rendering:**
```
• Parent (disc)
  ◦ Only child (circle)
```

**Test assertions:**
- Single nested item renders correctly
- No special handling needed

---

### Example 10: Flat List (No Nesting)
**Rule:** Backwards compatibility

```markdown
---
template: content
---
## Simple List

- Item A
- Item B
- Item C
```

**Expected:** Works exactly as before (v0.4.0 behavior)

**Test assertions:**
- Existing tests still pass
- No regression

---

## Test Implementation Plan

### Phase 1: Domain Model (No Changes Needed)
**Module:** `domain/src/.../domain/FormattedContent.scala`

**Current state:** ListItem already supports nesting via TextSpan

**New tests:**
1. `calculateNestingDepth` - returns max depth of nested lists
2. Validation fails when depth > 3
3. Validation passes when depth ≤ 3

**TDD Cycle:** 3 tests

---

### Phase 2: Parser Enhancement
**Module:** `infrastructure/src/.../parser/FlexmarkAdapter.scala`

**Tests to write:**
1. Parse 2-level unordered list
2. Parse 2-level ordered list
3. Parse 3-level mixed list
4. Parse 4-level list (for validation testing)
5. Extract nesting depth correctly
6. Handle empty nested items
7. Preserve text content at all levels
8. Mixed ordered → unordered
9. Mixed unordered → ordered
10. Single nested item

**TDD Cycle:** 10 tests

---

### Phase 3: Renderer Enhancement
**Module:** `infrastructure/src/.../renderer/HTMLRenderer.scala`

**Tests to write:**
1. Render nested `<ul><ul>` structure
2. Render nested `<ol><ol>` structure
3. Render mixed `<ul><ol>` structure
4. Render mixed `<ol><ul>` structure
5. Apply CSS classes for nesting levels
6. Verify HTML structure matches expected
7. Indentation CSS applied correctly
8. Bullet style CSS applied correctly
9. Number style CSS applied correctly

**TDD Cycle:** 9 tests

---

### Phase 4: CSS Styling
**Module:** `infrastructure/src/.../renderer/HTMLRenderer.scala` (CSS generation)

**CSS rules to add:**
```css
.slide ul,
.slide ol {
  margin-left: 0;
  padding-left: 1.5em;
}

.slide ul ul,
.slide ol ol,
.slide ul ol,
.slide ol ul {
  margin-left: 2em;
}

/* Bullet styles */
.slide ul {
  list-style-type: disc;
}

.slide ul ul {
  list-style-type: circle;
}

.slide ul ul ul {
  list-style-type: square;
}

/* Number styles */
.slide ol {
  list-style-type: decimal;
}

.slide ol ol {
  list-style-type: lower-alpha;
}

.slide ol ol ol {
  list-style-type: lower-roman;
}
```

**Manual testing:** Visual verification in browser

---

### Phase 5: Integration Testing
**Module:** End-to-end test with complete slide deck

**Test scenario:**
```markdown
---
template: title
---
# Nested Lists Demo

---
template: content
---
## All List Types

- Unordered parent
  - Unordered child
    - Unordered grandchild

1. Ordered parent
   1. Ordered child
      1. Ordered grandchild

- Mixed: unordered
  1. Ordered child
     - Unordered grandchild

1. Mixed: ordered
   - Unordered child
      1. Ordered grandchild
```

**Verification:**
- Renders without errors
- All bullet/number styles correct
- Indentation visually correct
- Validation passes

---

## Total Test Count Estimate

| Phase | Test Type | Count |
|-------|-----------|-------|
| Phase 1 | Domain validation | 3 |
| Phase 2 | Parser | 10 |
| Phase 3 | Renderer | 9 |
| Phase 4 | CSS (manual) | 0 |
| Phase 5 | Integration (manual) | 1 |
| **TOTAL** | | **22 automated + 1 manual** |

---

## Implementation Order

1. ✅ Event Storming (completed)
2. ✅ Three Amigos (completed)
3. ✅ Example Mapping (completed)
4. **→ TDD Phase 1:** Domain validation (3 tests)
5. **→ TDD Phase 2:** Parser enhancement (10 tests)
6. **→ TDD Phase 3:** Renderer enhancement (9 tests)
7. **→ Phase 4:** CSS styling (manual)
8. **→ Phase 5:** Integration test (manual)
9. **→ Documentation:** Update tutorial with nested list examples
10. **→ Release:** v1.2.0 packaging

---

## Questions for TDD

### Q1: Should we add `maxNestingDepth` method to FormattedContent?
**Decision:** Yes - add helper method to calculate depth recursively

### Q2: How to represent nesting in ListItem?
**Decision:** Use existing structure - ListItem.content can contain nested lists via TextSpan

### Q3: Should CSS be theme-configurable?
**Decision:** No for v1.2 - hardcode bullet/number styles, make configurable in v1.3

### Q4: How to test CSS visually?
**Decision:** Manual verification in browser + screenshot for documentation

---

**Status:** ✅ Complete - Ready for TDD Implementation
**Next Step:** TDD Phase 1 - Domain model validation (3 tests)
