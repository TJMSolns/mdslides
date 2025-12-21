# Example Mapping: US-008 Apply Theme to SlideDeck

**Session Date**: 2024-12-20
**Participants**: Tony Moores (Business/Dev/QA)
**Story**: US-008 - Apply Theme to SlideDeck
**Status**: Complete

---

## 📖 Story Card

```
┌─────────────────────────────────────────────┐
│ 📘 USER STORY                               │
│                                             │
│ As a slide deck author                      │
│ I want to specify a theme in deck metadata  │
│ So that my slides have consistent visual    │
│ styling                                     │
│                                             │
│ Priority: P0 (Blocker)                      │
│ Estimated Effort: 2 days                    │
└─────────────────────────────────────────────┘
```

---

## 🔵 Blue Cards: Business Rules

### Rule 1: Theme specified via `theme:` in global front matter

Theme name set in global YAML front matter (applies to all slides).

**Default**: If not specified, use `default` theme.

**Format**: `theme: <name>` where `<name>` is JSON filename (without .json extension).

---

### Rule 2: Theme loaded from JSON file

Theme definition stored as JSON file in `themes/` directory.

**Location**: `themes/<name>.json`

**Default Theme**: Bundled with application (`themes/default.json`).

---

### Rule 3: Theme defines colors, typography, and layout

Theme JSON structure:
```json
{
  "name": "theme-name",
  "metadata": { ... },
  "colors": { "background", "text", "heading", "accent" },
  "typography": { "bodyFont", "headingFont", "baseFontSize", "headingFontSize" },
  "layout": { "slideWidth", "slideHeight", "padding", "maxBodyLines", "maxBodyWords", "maxHeadingChars" }
}
```

---

### Rule 4: Theme CSS inlined into HTML output

Theme colors and typography converted to CSS and injected into `<style>` tag in HTML.

**Inline CSS**: Single-file HTML output (no external stylesheets in v1.0).

---

### Rule 5: Theme layout settings applied during rendering

Theme layout controls slide dimensions, padding, margins.

**Applied to**: Slide container `<div>` elements.

---

### Rule 6: Theme density limits used in Density Validation

Theme `layout.maxBodyLines`, `layout.maxBodyWords`, `layout.maxHeadingChars` override default density limits.

**Integration**: Theme loaded before validation runs.

---

### Rule 7: Invalid theme name → error

If specified theme file doesn't exist, render fails with error.

**Error Message**: Lists available themes from `themes/` directory.

---

## 🟢 Green Cards: Examples

### ✅ EXAMPLE 1: No theme specified (default theme)

```markdown
GIVEN markdown with no theme field:
  ---
  template: title
  ---
  # My Presentation

WHEN parsed and rendered

THEN:
  - Theme: "default" (implicit)
  - Theme loaded from themes/default.json
  - HTML contains default theme CSS

Rules tested: 1, 2
```

---

### ✅ EXAMPLE 2: Explicit default theme

```markdown
GIVEN markdown with:
  ---
  theme: default
  template: title
  ---
  # My Presentation

WHEN parsed and rendered

THEN:
  - Theme: "default" (explicit)
  - Theme loaded from themes/default.json
  - HTML contains default theme CSS

Rules tested: 1, 2
```

---

### ✅ EXAMPLE 3: Custom theme (dark)

```markdown
GIVEN theme file themes/dark.json exists
AND markdown with:
  ---
  theme: dark
  ---
  # Slide

WHEN parsed and rendered

THEN:
  - Theme: "dark"
  - Theme loaded from themes/dark.json
  - HTML contains dark theme CSS (dark background, light text)

Rules tested: 1, 2, 3, 4
```

---

### ❌ EXAMPLE 4: Theme not found

```markdown
GIVEN theme file themes/custom.json does NOT exist
AND markdown with:
  ---
  theme: custom
  ---
  # Slide

WHEN parsed and rendered

THEN:
  - Rendering: FAILED
  - Error: "Theme 'custom' not found. Available themes: default, dark"

Rules tested: 7
```

---

### ❌ EXAMPLE 5: Theme JSON malformed

```markdown
GIVEN theme file themes/broken.json contains invalid JSON:
  {
    "name": "broken",
    "colors": {  <--- missing closing brace

AND markdown with:
  ---
  theme: broken
  ---
  # Slide

WHEN parsed and rendered

THEN:
  - Rendering: FAILED
  - Error: "Failed to parse theme 'broken': invalid JSON at line 4"

Rules tested: 2
```

---

### ❌ EXAMPLE 6: Theme JSON missing required fields

```markdown
GIVEN theme file themes/incomplete.json missing "layout" field:
  {
    "name": "incomplete",
    "colors": { ... },
    "typography": { ... }
    // "layout" missing
  }

AND markdown with:
  ---
  theme: incomplete
  ---
  # Slide

WHEN parsed and rendered

THEN:
  - Rendering: FAILED
  - Error: "Theme 'incomplete' is invalid: missing required field 'layout'"

Rules tested: 3
```

---

### ✅ EXAMPLE 7: Theme CSS inlined

```markdown
GIVEN theme with:
  colors.background: "#FFFFFF"
  colors.text: "#333333"
  typography.bodyFont: "Arial, sans-serif"

WHEN rendered

THEN HTML contains:
  <style>
    body {
      background-color: #FFFFFF;
      color: #333333;
      font-family: Arial, sans-serif;
    }
    ...
  </style>

Rules tested: 4
```

---

### ✅ EXAMPLE 8: Theme layout settings applied

```markdown
GIVEN theme with:
  layout.slideWidth: "1024px"
  layout.slideHeight: "768px"
  layout.padding: "40px"

WHEN rendered

THEN HTML contains:
  <div class="slide" style="width: 1024px; height: 768px; padding: 40px;">
    ...
  </div>

Rules tested: 5
```

---

### ⚠️ EXAMPLE 9: Theme density limits override defaults

```markdown
GIVEN theme with:
  layout.maxBodyLines: 10  (default is 12)
  layout.maxBodyWords: 100  (default is 150)

AND markdown with body containing 11 lines, 120 words

WHEN validated

THEN:
  - Density validation: WARNING
  - Warning 1: "Body exceeds max 10 lines (found 11 lines)"
  - Warning 2: "Body exceeds max 100 words (found 120 words)"

Rules tested: 6
```

---

### ✅ EXAMPLE 10: Theme loaded before validation

```markdown
GIVEN theme with custom density limits (max 10 lines)
AND markdown with body containing 11 lines

WHEN processing pipeline runs

THEN:
  - Step 1: Parse markdown
  - Step 2: Load theme (before validation)
  - Step 3: Run validation (uses theme limits)
  - Step 4: Density warning (11 > 10 lines)

Rules tested: 6
```

---

### ✅ EXAMPLE 11: Multiple slides, single theme

```markdown
GIVEN markdown with 5 slides:
  ---
  theme: dark
  ---
  # Slide 1
  ---
  ## Slide 2
  ---
  ## Slide 3
  ...

WHEN rendered

THEN:
  - All 5 slides use "dark" theme
  - HTML contains single <style> block (dark theme CSS)
  - All slide containers have dark theme styling

Rules tested: 1, 4, 5
```

---

## 🔴 Red Cards: Questions (All Resolved ✅)

### Q1: Where are theme files stored? ✅

**Answer**: `themes/` directory relative to application. Contains bundled themes (default) and user-provided themes.

---

### Q2: Per-slide theme override? ✅

**Answer**: No for v1.0. Global theme only (applies to all slides). Per-slide themes deferred to v2.0.

---

### Q3: Default theme missing? ✅

**Answer**: Fatal error. Default theme (`themes/default.json`) must be bundled with application.

---

### Q4: JSON schema validation? ✅

**Answer**: Yes. Validate all required fields present:
- `name` (string)
- `colors` (object with background, text, heading, accent)
- `typography` (object with bodyFont, headingFont, baseFontSize, headingFontSize)
- `layout` (object with slideWidth, slideHeight, padding, maxBodyLines, maxBodyWords, maxHeadingChars)

---

### Q5: External CSS vs inline? ✅

**Answer**: Inline for v1.0 (single-file HTML output). External CSS deferred to v1.1 (performance optimization).

---

### Q6: Responsive design? ✅

**Answer**: Not in v1.0. Fixed slide dimensions defined by theme. Responsive layout deferred to v1.1.

---

### Q7: Theme loaded before validation? ✅

**Answer**: Yes. Theme must be loaded early (after parsing, before validation) so Density Validation can use theme limits.

**Processing Order**:
1. Parse markdown → SlideDeck
2. Load theme (from front matter `theme:` field)
3. Run validation (Structure, Density with theme limits, Content)
4. Render HTML (with theme CSS)

---

### Q8: List available themes in error? ✅

**Answer**: Yes. Scan `themes/` directory for `.json` files, list in error message.

**Example**: "Theme 'custom' not found. Available themes: default, dark, light"

---

## 🎯 Story Readiness Assessment

### Coverage Summary

| Metric | Count |
|--------|-------|
| Rules | 7 blue cards |
| Examples | 11 green cards (7 success, 4 failure) |
| Questions | 8 red cards (all resolved ✅) |

### Rule → Example Coverage

```
Rule 1 (Theme in front matter):
├── Example 1 ✅ (no theme → default)
├── Example 2 ✅ (explicit default)
├── Example 3 ✅ (custom theme)
├── Example 4 ❌ (theme not found)
└── Example 11 ✅ (global theme for all slides)

Rule 2 (Load from JSON):
├── Example 2 ✅ (default.json)
├── Example 3 ✅ (dark.json)
├── Example 4 ❌ (file not found)
├── Example 5 ❌ (malformed JSON)
└── Example 6 ❌ (missing fields)

Rule 3 (Colors, typography, layout):
├── Example 3 ✅ (full theme structure)
├── Example 6 ❌ (missing layout field)
├── Example 7 ✅ (colors + typography)
└── Example 8 ✅ (layout settings)

Rule 4 (CSS inlined):
├── Example 3 ✅ (dark theme CSS)
├── Example 7 ✅ (CSS structure)
└── Example 11 ✅ (single <style> block)

Rule 5 (Layout applied):
├── Example 8 ✅ (slide dimensions + padding)
└── Example 11 ✅ (applied to all slides)

Rule 6 (Density limits):
├── Example 9 ⚠️ (custom limits override defaults)
└── Example 10 ✅ (theme loaded before validation)

Rule 7 (Invalid theme → error):
├── Example 4 ❌ (theme not found)
├── Example 5 ❌ (malformed JSON)
└── Example 6 ❌ (missing required fields)
```

### Confidence Level

**HIGH CONFIDENCE** ✅

**Reasons**:
- All 8 questions resolved
- 7 clear business rules identified
- 11 concrete examples cover success and failure paths
- Examples cover theme loading, validation, rendering
- Clear integration with Density Validation (theme limits)
- Processing order defined (load theme before validation)

**Risks Identified**:
- Default theme must be bundled (risk if missing)
- Theme JSON schema validation required (prevent runtime errors)
- Theme loading performance (mitigated by < 10ms requirement)

**Ready for TDD**: YES ✅

---

## 📋 Acceptance Criteria Summary

### Must Have (Critical)

1. ✅ Theme specified via `theme:` in front matter
2. ✅ Default theme if not specified
3. ✅ Theme loaded from JSON file
4. ✅ Theme CSS inlined into HTML
5. ✅ Theme layout applied to slides
6. ✅ Theme density limits override defaults
7. ✅ Invalid theme → error with available list

### Should Have (Important)

8. ✅ Theme JSON schema validation
9. ✅ Default theme bundled with app
10. ✅ Theme loading < 10ms
11. ✅ Error messages list available themes

### Nice to Have (Enhancement)

12. ✅ Theme CSS inlining < 5ms
13. ✅ Theme JSON files < 50 KB

---

## 🎨 Visual Example Map

```
┌────────────────────────────────────────────────────────────┐
│                      📘 USER STORY                         │
│              Apply Theme to SlideDeck (US-008)             │
└────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R1 │          │ 🔵 R2 │          │ 🔵 R3 │
    │ Theme │          │ Load  │          │Colors │
    │  In   │          │ JSON  │          │Typo   │
    │Front  │          │       │          │Layout │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴──┬──────┐   ┌───┴──┬──────┐   ┌───┴──┬──────┐
    │✅ E1 │✅ E2 │   │✅ E2 │✅ E3 │   │✅ E3 │✅ E7 │
    │✅ E3 │❌ E4 │   │❌ E4 │❌ E5 │   │✅ E8 │❌ E6 │
    │✅E11 │      │   │❌ E6 │      │   └──────┴──────┘
    └──────┴──────┘   └──────┴──────┘

    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R4 │          │ 🔵 R5 │          │ 🔵 R6 │
    │  CSS  │          │Layout │          │Density│
    │Inline │          │Applied│          │Limits │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴──┬──────┐   ┌───┴──┬──────┐   ┌───┴──┬──────┐
    │✅ E3 │✅ E7 │   │✅ E8 │✅E11 │   │⚠️ E9 │✅E10 │
    │✅E11 │      │   └──────┴──────┘   └──────┴──────┘
    └──────┴──────┘

    ┌───────┐
    │ 🔵 R7 │
    │Invalid│
    │ Error │
    └───┬───┘
        │
    ┌───┴──┬──────┐
    │❌ E4 │❌ E5 │
    │❌ E6 │      │
    └──────┴──────┘

Questions (All ✅):
Q1-Q8: All resolved (see Red Cards section)
```

---

## 🔄 Next Steps

1. ✅ **Three Amigos Complete**: All rules and examples documented
2. ✅ **Example Mapping Complete**: 11 examples with full coverage
3. 🔄 **Create default.json theme**: Provide baseline theme file
4. 🔄 **Update BACKLOG-V3.md**: Mark US-008 as Ready for Implementation
5. ⏭️ **Proceed to US-009**: Custom Theme Validation

---

**Session Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Story Status**: Ready for TDD ✅
