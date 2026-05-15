# Example Mapping: Two-Column Layout

**Date**: 2025-12-29
**Feature**: Two-Column Layout Template (v3.0.0 - Feature 6 of 10)
**Participants**: Product Owner, Bench Developer, Architect
**Story**: As a presentation author, I want to create slides with two-column layouts so I can display code alongside explanations, compare before/after states, or show diagrams side-by-side.

---

## Business Rules (Yellow Cards)

### Rule 1: Template Recognition (Frontmatter)
**Statement**: Two-column template recognized via frontmatter `template: two-column`. Triggers two-column parsing and rendering.

**Examples** (Green Cards ✅):
- **Ex 1.1**: Frontmatter `template: two-column` → Slide parsed as two-column
- **Ex 1.2**: No template frontmatter → Default single-column layout
- **Ex 1.3**: Frontmatter `template: default` → Single-column layout

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 1.4**: `template: two_column` (underscore) → Not recognized, validation error
- ❌ **Ex 1.5**: Two columns without frontmatter → Treated as single column (delimiter ignored)

**BDD Traceability**:
- `two-column-layout.feature`: "Two-column template recognized via frontmatter"

**Exact Value**: `template: two-column` (case-sensitive, hyphenated)

---

### Rule 2: Column Delimiter Parsing
**Statement**: Exactly one `---column---` delimiter required to split left and right columns. Delimiter must be on own line.

**Examples** (Green Cards ✅):
- **Ex 2.1**: Content, `---column---`, content → Valid, two columns
- **Ex 2.2**: Delimiter on line 10 → Lines 1-9 left column, lines 11+ right column
- **Ex 2.3**: Delimiter with blank lines around it → Valid, blank lines trimmed

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 2.4**: Two `---column---` delimiters → Error: "Only one separator allowed"
- ❌ **Ex 2.5**: No delimiter → Error: "Requires exactly one '---column---' separator"
- ❌ **Ex 2.6**: `--- column ---` (spaces) → Not recognized, validation error

**BDD Traceability**:
- `two-column-layout.feature`: "Valid two-column slide with column delimiter"
- `two-column-layout.feature`: "Exactly one column delimiter required"
- `two-column-layout.feature`: "No column delimiter raises error"

**Exact Delimiter**: `---column---` (case-sensitive, no spaces)

---

### Rule 3: Non-Empty Columns Validation
**Statement**: Both left and right columns must contain non-empty content. Empty column (blank lines only) raises validation error.

**Examples** (Green Cards ✅):
- **Ex 3.1**: Left has text, right has text → Valid
- **Ex 3.2**: Left has code block, right has list → Valid
- **Ex 3.3**: Minimal content: "Left" and "Right" → Valid (non-empty)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 3.4**: Left empty, right has content → Error: "Left column: 0 lines (EMPTY)"
- ❌ **Ex 3.5**: Left has content, right empty → Error: "Right column: 0 lines (EMPTY)"
- ❌ **Ex 3.6**: Both columns empty → Error: "Both columns must contain content"

**BDD Traceability**:
- `two-column-layout.feature`: "Both columns must be non-empty"
- `two-column-layout.feature`: "Empty left column raises error"
- `two-column-layout.feature`: "Empty right column raises error"

**Validation**: After trimming whitespace, column content.length > 0

---

### Rule 4: Equal Width Split (50/50)
**Statement**: Columns have equal 50/50 width using CSS Grid `grid-template-columns: 1fr 1fr`.

**Examples** (Green Cards ✅):
- **Ex 4.1**: Both columns render at 50% viewport width each
- **Ex 4.2**: Left column shorter than right → Both still 50% width (not dynamic)
- **Ex 4.3**: 1920px viewport → Each column is 960px wide

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 4.4**: Left column 60%, right 40% → Wrong, equal split
- ❌ **Ex 4.5**: Columns auto-size based on content → Wrong, fixed 50/50

**BDD Traceability**:
- `two-column-layout.feature`: "Columns have equal 50/50 width"

**CSS**:
```css
.two-column-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
}
```

---

### Rule 5: Column Gap (Fixed 2rem)
**Statement**: Fixed 2rem gap between columns using CSS Grid `gap: 2rem`.

**Examples** (Green Cards ✅):
- **Ex 5.1**: 16px base font → Gap is 32px
- **Ex 5.2**: 20px base font → Gap is 40px (2 × 20px)
- **Ex 5.3**: Visual spacing between columns is consistent

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 5.4**: No gap (columns touch) → Wrong, 2rem gap required
- ❌ **Ex 5.5**: Variable gap based on content → Wrong, fixed 2rem

**BDD Traceability**:
- `two-column-layout.feature`: "Fixed 2rem gap between columns"

**CSS**: `gap: 2rem;`

---

### Rule 6: Default Vertical Alignment (Center)
**Statement**: Column content vertically centered using Flexbox `justify-content: center`.

**Examples** (Green Cards ✅):
- **Ex 6.1**: Short text in left column → Vertically centered in 70vh space
- **Ex 6.2**: Code block in right column → Vertically centered
- **Ex 6.3**: Both columns different heights → Each centered independently

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 6.4**: Content top-aligned → Wrong, default is center
- ❌ **Ex 6.5**: Content bottom-aligned → Wrong, default is center

**BDD Traceability**:
- `two-column-layout.feature`: "Vertical alignment defaults to center"

**CSS**:
```css
.column {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
```

---

### Rule 7: Default Horizontal Alignment (Left)
**Statement**: Text within columns left-aligned using `text-align: left`.

**Examples** (Green Cards ✅):
- **Ex 7.1**: Paragraph text → Left-aligned within column
- **Ex 7.2**: Headings → Left-aligned
- **Ex 7.3**: Lists → Left-aligned (bullets at left edge)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 7.4**: Text centered within column → Wrong, default is left
- ❌ **Ex 7.5**: Text right-aligned → Wrong, default is left

**BDD Traceability**:
- `two-column-layout.feature`: "Horizontal alignment defaults to left"

**CSS**: `text-align: left;`

---

### Rule 8: Per-Column Density Validation
**Statement**: Each column validated against 70vh height limit independently. Exceeding limit raises density error.

**Examples** (Green Cards ✅):
- **Ex 8.1**: Left 50vh, right 60vh → Both valid
- **Ex 8.2**: Left 70vh, right 70vh → Both valid (at limit)
- **Ex 8.3**: Left 40vh, right 40vh → Both valid

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 8.4**: Left 75vh, right 60vh → Error: "Left column exceeds vertical limit (75vh > 70vh)"
- ❌ **Ex 8.5**: Left 60vh, right 80vh → Error: "Right column exceeds vertical limit"
- ❌ **Ex 8.6**: Both columns 75vh → Two errors (one per column)

**BDD Traceability**:
- `two-column-layout.feature`: "Each column validated against 70vh height limit"
- `two-column-layout.feature`: "Right column exceeds height limit"

**Validation**: `estimatedHeight(column) ≤ 70vh`

---

### Rule 9: Content Types Support (All)
**Statement**: Columns support all content types: text, code blocks, images, lists, mermaid diagrams, headings.

**Examples** (Green Cards ✅):
- **Ex 9.1**: Left has code block, right has text → Both render correctly
- **Ex 9.2**: Left has image, right has description → Image scales to column width
- **Ex 9.3**: Left has mermaid diagram, right has explanation → Diagram pre-rendered as SVG
- **Ex 9.4**: Left has nested list, right has numbered list → Both render with proper indentation

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 9.5**: Code blocks not supported in columns → Wrong, all content types supported
- ❌ **Ex 9.6**: Images break column layout → Wrong, images scale to fit

**BDD Traceability**:
- `two-column-layout.feature`: "Columns support all content types"
- `two-column-layout.feature`: "Left column with code, right column with explanation"

**No Restrictions**: All markdown content types supported.

---

### Rule 10: Image Scaling (Max 100% Column Width)
**Statement**: Images in columns scaled to fit column width (max 50vw). Maintains aspect ratio.

**Examples** (Green Cards ✅):
- **Ex 10.1**: 1920px image in left column → Scaled to 960px (50% of 1920px viewport)
- **Ex 10.2**: Small image (200px) → Displayed at 200px (not upscaled)
- **Ex 10.3**: Tall image → Width constrained to column, height auto (aspect ratio preserved)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 10.4**: Image exceeds column boundary → Wrong, max-width: 100% constrains it
- ❌ **Ex 10.5**: Image aspect ratio distorted → Wrong, height: auto preserves ratio

**BDD Traceability**:
- `two-column-layout.feature`: "Images in columns scaled to fit column width"

**CSS**:
```css
.column img {
  max-width: 100%;
  height: auto;
}
```

---

### Rule 11: Mermaid Diagram Scaling
**Statement**: Mermaid diagrams pre-rendered as SVG, scaled to fit column width (max 50vw). Aspect ratio preserved.

**Examples** (Green Cards ✅):
- **Ex 11.1**: Large diagram → SVG scaled to 50vw max-width
- **Ex 11.2**: Small diagram → SVG displayed at natural size
- **Ex 11.3**: Side-by-side diagrams → Each scaled to fit respective column

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 11.4**: Diagram exceeds column width → Wrong, max-width constrains it
- ❌ **Ex 11.5**: Diagram not pre-rendered → Wrong, all mermaid diagrams pre-rendered at render time

**BDD Traceability**:
- `two-column-layout.feature`: "Mermaid diagrams scaled to column width"
- `two-column-layout.feature`: "Side-by-side mermaid diagrams"

**Integration**: FlexmarkAdapter pre-renders mermaid, SVG output scaled via CSS.

---

### Rule 12: Accessibility (Reading Order)
**Statement**: Left column read before right column by screen readers. HTML structure: left `<section>` before right `<section>`.

**Examples** (Green Cards ✅):
- **Ex 12.1**: Screen reader reads left column completely, then right column
- **Ex 12.2**: DOM order: left section first, right section second
- **Ex 12.3**: ARIA attributes: role="region", aria-label="Left column" / "Right column"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 12.4**: Right column read first → Wrong, DOM order determines reading
- ❌ **Ex 12.5**: No ARIA labels → Wrong, accessibility requirement

**BDD Traceability**:
- `two-column-layout.feature`: "Left column read before right column by screen readers"

**HTML Structure**:
```html
<div class="two-column-layout" role="region" aria-label="Two-column slide">
  <section class="column column-left" aria-label="Left column">
    <!-- Left content -->
  </section>
  <section class="column column-right" aria-label="Right column">
    <!-- Right content -->
  </section>
</div>
```

---

## Questions (Pink Cards)

### Q1: Should we support custom column width ratios (e.g., 60/40)?
**Status**: DEFERRED to v3.1.0
**Decision**: Fixed 50/50 split for v3.0.0
**Rationale**: Simpler implementation, covers most use cases. Custom ratios add complexity.

### Q2: Should we support more than 2 columns (e.g., 3-column layout)?
**Status**: DEFERRED to v4.0.0
**Decision**: Exactly 2 columns for v3.0.0
**Rationale**: Two-column is most common. Three-column adds complexity, less readable.

### Q3: Should column alignment (vertical, horizontal) be configurable?
**Status**: DEFERRED to v3.1.0
**Decision**: Fixed alignment (vertical: center, horizontal: left)
**Rationale**: Covers 90% of use cases. Custom alignment in future release.

### Q4: Should delimiter be configurable (e.g., `===column===`)?
**Status**: RESOLVED - No
**Decision**: Fixed `---column---` delimiter
**Rationale**: Consistent with markdown conventions (--- for horizontal rule).

### Q5: Should we support nested columns (column within column)?
**Status**: RESOLVED - No
**Decision**: Flat two-column only (no nesting)
**Rationale**: Nesting is complex, rarely needed. Use multiple slides instead.

### Q6: Should whitespace around delimiter be trimmed?
**Status**: RESOLVED - Yes
**Decision**: Trim leading/trailing whitespace from column content
**Rationale**: User-friendly, prevents accidental blank line errors.

---

## Design Decisions from Event Storming

1. **Column Delimiter**:
   - **Decision**: `---column---` (matches horizontal rule syntax)
   - **Rationale**: Familiar to markdown users, visually clear

2. **Equal Width Split**:
   - **Decision**: CSS Grid `1fr 1fr` (50/50)
   - **Rationale**: Simple, predictable, covers most use cases

3. **Per-Column Validation**:
   - **Decision**: Each column validated independently (70vh limit)
   - **Rationale**: Prevents overflow, ensures readability

4. **Content Type Support**:
   - **Decision**: All markdown content types supported (no restrictions)
   - **Rationale**: Flexible, enables code-explanation, image-description, diagram comparisons

5. **Image/Diagram Scaling**:
   - **Decision**: `max-width: 100%` (column width constraint)
   - **Rationale**: Prevents overflow, maintains aspect ratio

6. **Accessibility**:
   - **Decision**: Semantic HTML (`<section>`), ARIA labels, DOM reading order
   - **Rationale**: WCAG compliance, screen reader support

7. **Non-Empty Validation**:
   - **Decision**: Both columns must have content (fail fast)
   - **Rationale**: Prevents accidental empty columns, clear error message

---

## Traceability Matrix

| Rule | BDD Scenarios | Event Storming Events | Acceptance Criteria |
|------|---------------|----------------------|---------------------|
| Rule 1 | two-column-layout.feature (1) | TwoColumnTemplateRecognized | AC-1 |
| Rule 2 | two-column-layout.feature (3) | ColumnDelimiterParsed | AC-2 |
| Rule 3 | two-column-layout.feature (3) | ColumnContentValidated | AC-3 |
| Rule 4 | two-column-layout.feature (2) | TwoColumnLayoutRendered | AC-4 |
| Rule 5 | two-column-layout.feature (1) | TwoColumnLayoutRendered | AC-5 |
| Rule 6 | two-column-layout.feature (2) | TwoColumnLayoutRendered | AC-6 |
| Rule 7 | two-column-layout.feature (2) | TwoColumnLayoutRendered | AC-7 |
| Rule 8 | two-column-layout.feature (3) | ColumnDensityValidated | AC-8 |
| Rule 9 | two-column-layout.feature (6) | ColumnContentRendered | AC-9 |
| Rule 10 | two-column-layout.feature (2) | ImageScaled | AC-10 |
| Rule 11 | two-column-layout.feature (2) | MermaidDiagramScaled | AC-11 |
| Rule 12 | two-column-layout.feature (1) | TwoColumnLayoutRendered | AC-12 |

**Total Coverage**: 28 BDD scenarios across 12 business rules

---

## Implementation Notes

### Domain Model Requirements
- `TwoColumnSlide` aggregate with state (leftColumn, rightColumn)
- `ParseTwoColumnContent` command splits markdown on delimiter
- `ValidateColumnContent` command checks non-empty requirement
- `ValidateColumnDensity` command checks 70vh limit per column
- `TwoColumnError` enum for validation errors

### Infrastructure Requirements
- Markdown splitter (split on `---column---`)
- Per-column markdown parser (FlexmarkAdapter)
- Density estimator (per column)
- CSS Grid layout generator
- Image/SVG scaling (max-width: 100%)

### UI Requirements
- HTML structure with semantic elements (`<section>`)
- CSS Grid layout: `grid-template-columns: 1fr 1fr; gap: 2rem;`
- Flexbox vertical centering: `justify-content: center;`
- ARIA attributes for accessibility
- Image/diagram scaling CSS

---

## Column Parsing Logic

```scala
def parseTwoColumnContent(markdown: String): Either[TwoColumnError, (String, String)] =
  val parts = markdown.split("---column---", -1)

  if parts.length != 2 then
    Left(TwoColumnError.InvalidDelimiterCount(s"Found ${parts.length - 1} delimiter(s), expected 1"))
  else
    val leftColumn = parts(0).trim
    val rightColumn = parts(1).trim

    if leftColumn.isEmpty then
      Left(TwoColumnError.EmptyColumn("Left column: 0 lines (EMPTY)"))
    else if rightColumn.isEmpty then
      Left(TwoColumnError.EmptyColumn("Right column: 0 lines (EMPTY)"))
    else
      Right((leftColumn, rightColumn))
```

---

## Validation Error Messages

### Missing Delimiter
```
✗ Slide 5: Two-column template requires exactly one '---column---' separator

  To fix: Add '---column---' between left and right content
```

### Empty Left Column
```
✗ Slide 5: Two-column template requires both columns to have content
  Left column: 0 lines (EMPTY)
  Right column: 45 lines

  Fix: Add content to left column or change to single-column template
```

### Column Exceeds Height
```
✗ Slide 8: Left column content exceeds vertical limit
  Estimated height: 75vh (max: 70vh)

  Suggestions:
  - Split content across multiple slides
  - Reduce font size or line spacing
  - Remove less critical content
```

---

## Use Case Examples

### Use Case 1: Code + Explanation
```markdown
---
template: two-column
---

```scala
def renderSlide(slide: Slide): Html =
  div(
    cls := "slide",
    renderSlots(slide.slots)
  )
```

---column---

## Explanation

This function renders a slide by:
1. Creating a div container
2. Adding the "slide" CSS class
3. Rendering all slot content
```

### Use Case 2: Before/After Comparison
```markdown
---
template: two-column
---

## Before

- Manual HTML
- No validation
- Inconsistent styling

---column---

## After

- Markdown simplicity
- WCAG validation
- Theme system
```

### Use Case 3: Image + Description
```markdown
---
template: two-column
---

![Architecture diagram](./arch.png)

---column---

## System Architecture

The diagram shows:
- **Client Layer**: Web/mobile apps
- **API Gateway**: Routing
- **Service Layer**: Business logic
- **Data Layer**: PostgreSQL
```

---

**Example Mapping Complete**: 2025-12-29
**Next Step**: Acceptance Criteria Review
**Ready for Implementation**: Pending AC approval
