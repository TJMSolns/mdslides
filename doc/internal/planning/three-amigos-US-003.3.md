# Three Amigos: US-003.3 - Nested List Support

**Date:** 2024-12-27
**Participants:** Product Owner, Developer, QA Tester
**User Story:** US-003.3 - Nested List Support

## User Story Recap

**As a** presentation author
**I want** to use nested lists in my slide content
**So that** I can show hierarchical information and sub-points

**Priority:** SHOULD for v1.2
**Dependencies:** US-003.2 (List Support) - ✅ Complete in v0.4.0

## Acceptance Criteria

### AC1: Parse Nested Unordered Lists
**Given** markdown with nested unordered lists
**When** the presentation is rendered
**Then** the nested list structure is preserved in HTML
**And** each nesting level has distinct bullet styles

**Example:**
```markdown
- Level 1 item A
  - Level 2 item A.1
  - Level 2 item A.2
- Level 1 item B
```

**Expected HTML structure:**
```html
<ul>
  <li>Level 1 item A
    <ul>
      <li>Level 2 item A.1</li>
      <li>Level 2 item A.2</li>
    </ul>
  </li>
  <li>Level 1 item B</li>
</ul>
```

**Visual rendering:**
- Level 1 item A (disc bullet •)
  - Level 2 item A.1 (circle bullet ◦)
  - Level 2 item A.2 (circle bullet ◦)
- Level 1 item B (disc bullet •)

---

### AC2: Parse Nested Ordered Lists
**Given** markdown with nested ordered lists
**When** the presentation is rendered
**Then** the nested list structure is preserved
**And** each level uses appropriate numbering style

**Example:**
```markdown
1. First main point
   1. First sub-point
   2. Second sub-point
2. Second main point
```

**Expected HTML:**
```html
<ol>
  <li>First main point
    <ol>
      <li>First sub-point</li>
      <li>Second sub-point</li>
    </ol>
  </li>
  <li>Second main point</li>
</ol>
```

**Visual rendering:**
1. First main point (decimal: 1, 2, 3...)
   a. First sub-point (lower-alpha: a, b, c...)
   b. Second sub-point
2. Second main point

---

### AC3: Mixed Nesting (Ordered within Unordered)
**Given** markdown with ordered lists nested in unordered lists
**When** the presentation is rendered
**Then** both list types are correctly nested

**Example:**
```markdown
- Overview
  1. Step one
  2. Step two
- Conclusion
```

**Expected rendering:**
- Overview (disc •)
  1. Step one (decimal)
  2. Step two
- Conclusion (disc •)

---

### AC4: Mixed Nesting (Unordered within Ordered)
**Given** markdown with unordered lists nested in ordered lists
**When** the presentation is rendered
**Then** both list types are correctly nested

**Example:**
```markdown
1. Introduction
   - Point A
   - Point B
2. Body
```

**Expected rendering:**
1. Introduction (decimal)
   - Point A (circle ◦)
   - Point B
2. Body (decimal)

---

### AC5: Maximum Nesting Depth (3 Levels)
**Given** markdown with 3 levels of nesting
**When** the presentation is rendered
**Then** all 3 levels display correctly

**Example:**
```markdown
- Level 1
  - Level 2
    - Level 3 (maximum)
```

**Expected rendering:**
- Level 1 (disc •)
  - Level 2 (circle ◦)
    - Level 3 (square ▪)

---

### AC6: Exceeding Maximum Depth Fails Validation
**Given** markdown with 4 or more levels of nesting
**When** validation runs
**Then** validation fails with clear error message
**And** error indicates maximum depth is 3

**Example:**
```markdown
- Level 1
  - Level 2
    - Level 3
      - Level 4 (too deep!)
```

**Expected error:**
```
✗ Validation failed:
  - Slide 2: List nesting exceeds maximum depth of 3 (found: 4)
```

---

### AC7: Indentation Hierarchy
**Given** nested lists at any depth
**When** rendered in HTML
**Then** each level is indented 2em from its parent
**And** indentation is visually consistent

**Visual expectation:**
```
• Level 1 (0em indent)
  ◦ Level 2 (2em indent)
    ▪ Level 3 (4em indent)
```

---

### AC8: Bullet Style Hierarchy (Unordered)
**Given** nested unordered lists
**When** rendered with CSS
**Then** bullet styles follow this pattern:
- Level 1: disc (•)
- Level 2: circle (◦)
- Level 3: square (▪)

---

### AC9: Numbering Style Hierarchy (Ordered)
**Given** nested ordered lists
**When** rendered with CSS
**Then** numbering styles follow this pattern:
- Level 1: decimal (1, 2, 3...)
- Level 2: lower-alpha (a, b, c...)
- Level 3: lower-roman (i, ii, iii...)

---

### AC10: Empty Nested Lists
**Given** a list item with an empty nested list
**When** the presentation is rendered
**Then** the empty nested list is ignored gracefully

**Example:**
```markdown
- Item with content
  -
- Item without nesting
```

**Expected:** No error, empty nested item ignored

---

## Examples

### Example 1: Software Architecture Layers
```markdown
---
template: content
---
## System Architecture

- Presentation Layer
  - Web UI
  - Mobile App
- Business Logic Layer
  - Services
  - Domain Models
- Data Layer
  - Database
  - Cache
```

**Expected slide:**
- Clear 2-level hierarchy
- Disc bullets at level 1, circle at level 2
- 2em indentation for level 2

---

### Example 2: Project Phases
```markdown
---
template: content
---
## Project Plan

1. Planning Phase
   - Requirements gathering
   - Stakeholder interviews
2. Development Phase
   1. Sprint 1
   2. Sprint 2
3. Deployment Phase
```

**Expected slide:**
- 3-level hierarchy (ordered → unordered → ordered)
- Decimal (1,2,3) → circle bullets → lower-alpha (a,b)
- Proper indentation at each level

---

### Example 3: Maximum Depth
```markdown
---
template: content
---
## Deep Hierarchy

- Category
  - Subcategory
    - Item (max depth)
```

**Expected:** Valid, renders all 3 levels

---

### Example 4: Exceeding Depth (Validation Error)
```markdown
---
template: content
---
## Too Deep

- Level 1
  - Level 2
    - Level 3
      - Level 4
```

**Expected:**
```
✗ Validation failed:
  - Slide 1: List nesting exceeds maximum depth of 3 (found: 4)
```

---

### Example 5: Complex Mixed Nesting
```markdown
---
template: content
---
## Development Process

1. Design Phase
   - Create wireframes
   - Review with stakeholders
2. Implementation
   1. Backend development
      - API design
      - Database schema
   2. Frontend development
      - Component library
      - Integration
3. Testing
```

**Expected slide:**
- Mixed ordered/unordered at 3 levels
- Proper bullet/number styling
- No validation errors (max depth not exceeded)

---

## Non-Functional Requirements

### Performance
- Parsing nested lists adds <5% overhead to total parse time
- Rendering nested lists adds <10% to HTML generation time

### Browser Compatibility
- CSS list styles work in Chrome 90+, Firefox 88+, Safari 14+, Edge 90+
- Fallback to default bullets if CSS not supported

### Accessibility
- Nested lists maintain proper `<ul>`/`<ol>` semantic structure
- Screen readers correctly announce nesting levels
- Indentation visible in both light and dark themes

---

## Edge Cases & Error Conditions

### Edge Case 1: Malformed Markdown
**Given:** Inconsistent indentation
```markdown
- Item 1
  - Properly indented
 - Incorrectly indented
```
**Expected:** Flexmark parser handles gracefully (may flatten incorrect indent)

### Edge Case 2: Very Long Nested List
**Given:** 20+ items across 3 levels
**Expected:** Renders correctly but may violate density constraints (body line count)
**Validation:** Should fail if total lines exceed 12

### Edge Case 3: Nested List in Title Slide
**Given:** Title slide with nested list in subtitle/author slot
**Expected:** Validation fails (title slides don't support complex formatting)

---

## Out of Scope for v1.2

1. **Definition Lists** (`<dl>`, `<dt>`, `<dd>`) - defer to v1.3
2. **Task Lists** (GitHub-style `- [ ]` checkboxes) - defer to v1.3
3. **Custom Bullet Styles** (theme-configurable) - defer to v1.3
4. **Nested Lists in Speaker Notes** - notes remain plain text only

---

## Success Criteria Summary

**US-003.3 is complete when:**
1. ✅ Nested unordered lists parse and render correctly (AC1)
2. ✅ Nested ordered lists parse and render correctly (AC2)
3. ✅ Mixed nesting works (ordered in unordered, vice versa) (AC3, AC4)
4. ✅ 3 levels of nesting supported (AC5)
5. ✅ 4+ levels fail validation with clear error (AC6)
6. ✅ CSS styling shows distinct bullets/numbers per level (AC8, AC9)
7. ✅ Indentation hierarchy is visually correct (AC7)
8. ✅ All edge cases handled gracefully
9. ✅ Tutorial updated with nested list examples
10. ✅ All tests passing (existing + new nested list tests)

---

## Test Scenarios for Example Mapping

1. **Basic 2-level unordered** - AC1
2. **Basic 2-level ordered** - AC2
3. **3-level maximum depth** - AC5
4. **4-level validation failure** - AC6
5. **Mixed: unordered → ordered** - AC3
6. **Mixed: ordered → unordered** - AC4
7. **Mixed: ordered → unordered → ordered** (3 levels)
8. **Empty nested list items** - AC10
9. **Very long nested list** (density constraint)
10. **Visual verification** - bullet styles, indentation

---

**Status:** ✅ Complete - Ready for Example Mapping
**Next Step:** Create example-mapping-US-003.3.md with detailed test scenarios
