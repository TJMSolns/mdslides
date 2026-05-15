# Acceptance Criteria: Two-Column Layout

**Feature**: Two-Column Layout Template (v3.0.0 - Feature 6 of 10)
**Date**: 2025-12-29
**Status**: Pending Approval

---

## User Story

**As a** presentation author
**I want to** create slides with two-column layouts
**So that** I can display code alongside explanations, compare before/after states, or show diagrams side-by-side

---

## Acceptance Criteria

### AC-1: Template Recognition (Frontmatter)
**Given** frontmatter: `template: two-column`
**Then** slide parsed as two-column layout

**BDD Scenarios**: two-column-layout.feature (1 scenario)

---

### AC-2: Column Delimiter Parsing
**Given** content with exactly one `---column---` delimiter
**Then** left column = content before delimiter
**And** right column = content after delimiter

**Error if zero or two+ delimiters**

**BDD Scenarios**: two-column-layout.feature (3 scenarios)

---

### AC-3: Non-Empty Columns Validation
**Given** both columns have content (after trimming)
**Then** slide is valid

**If left or right column empty**:
**Then** render error: "Both columns must contain content"

**BDD Scenarios**: two-column-layout.feature (3 scenarios)

---

### AC-4: Equal Width Split (50/50)
**CSS Grid**: `grid-template-columns: 1fr 1fr`
**Result**: Each column 50% viewport width

**BDD Scenarios**: two-column-layout.feature (2 scenarios)

---

### AC-5: Column Gap (Fixed 2rem)
**CSS**: `gap: 2rem;`
**Result**: 32px gap at 16px base font

**BDD Scenarios**: two-column-layout.feature (1 scenario)

---

### AC-6: Default Vertical Alignment (Center)
**CSS**:
```css
display: flex;
flex-direction: column;
justify-content: center;
```

**BDD Scenarios**: two-column-layout.feature (2 scenarios)

---

### AC-7: Default Horizontal Alignment (Left)
**CSS**: `text-align: left;`

**BDD Scenarios**: two-column-layout.feature (2 scenarios)

---

### AC-8: Per-Column Density Validation
**Each column validated independently**:
**If estimatedHeight > 70vh**:
**Then** density error: "Left/Right column exceeds vertical limit"

**BDD Scenarios**: two-column-layout.feature (3 scenarios)

---

### AC-9: Content Types Support (All)
**Supported in columns**:
- Text paragraphs
- Code blocks (syntax-highlighted)
- Images (scaled to fit)
- Lists (bulleted, numbered)
- Mermaid diagrams (pre-rendered)
- Headings, mixed content

**BDD Scenarios**: two-column-layout.feature (6 scenarios)

---

### AC-10: Image Scaling (Max 100% Column Width)
**CSS**:
```css
max-width: 100%;
height: auto;
```

**Result**: Images constrained to column width (max 50vw)

**BDD Scenarios**: two-column-layout.feature (2 scenarios)

---

### AC-11: Mermaid Diagram Scaling
**Pre-rendered as SVG**:
**Then** SVG has max-width: 50vw
**And** aspect ratio preserved

**BDD Scenarios**: two-column-layout.feature (2 scenarios)

---

### AC-12: Accessibility (Reading Order)
**HTML Structure**:
```html
<div role="region" aria-label="Two-column slide">
  <section aria-label="Left column">...</section>
  <section aria-label="Right column">...</section>
</div>
```

**Screen reader**: Reads left column first, then right

**BDD Scenarios**: two-column-layout.feature (1 scenario)

---

## Definition of Done

- [ ] All 12 acceptance criteria implemented
- [ ] 28 BDD scenarios passing
- [ ] Domain model: TwoColumnSlide aggregate
- [ ] UI: CSS Grid layout with semantic HTML
- [ ] Documentation updated

**Approval**:
- Product Owner: ________________ Date: ________
