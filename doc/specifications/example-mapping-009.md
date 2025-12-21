# Example Mapping: US-009 Create and Validate Custom Theme

**Session Date**: 2024-12-20
**Participants**: Tony Moores (Business/Dev/QA)
**Story**: US-009 - Create and Validate Custom Theme
**Status**: Complete

---

## 📖 Story Card

```
┌─────────────────────────────────────────────┐
│ 📘 USER STORY                               │
│                                             │
│ As a slide deck author                      │
│ I want to create custom themes with         │
│ validation                                  │
│ So that I can match my organization's       │
│ branding while maintaining accessibility    │
│                                             │
│ Priority: P0 (Blocker)                      │
│ Estimated Effort: 2 days                    │
└─────────────────────────────────────────────┘
```

---

## 🔵 Blue Cards: Business Rules

### Rule 1: All required fields must be present

Required fields: `name`, `colors`, `fonts`, `layout`

Optional fields: `metadata`, `content`, `accessibility`

**Error Type**: ThemeValidationError

---

### Rule 2: Color values must be valid hex codes

Valid formats: `#RRGGBB` (6-digit) or `#RRGGBBAA` (8-digit with alpha)

**Examples**: `#FFFFFF`, `#000000`, `#FF6900AA`

**Error Type**: ThemeValidationError

---

### Rule 3: Font sizes must be >= 18px (accessibility minimum)

Minimum font size for readability and WCAG compliance.

**Applies to**: bodySize, headingSize, titleSize, subtitleSize, codeSize

**Error Type**: ThemeValidationError

---

### Rule 4: Layout dimensions must be reasonable

- **Slide Width**: 800-3840 px (4K max)
- **Slide Height**: 600-2160 px (4K max)

**Rationale**: Practical display sizes

**Error Type**: ThemeValidationError

---

### Rule 5: Contrast ratio >= 4.5:1 (WCAG AA)

Background vs foreground contrast must meet WCAG AA standard.

**Calculation**: WCAG relative luminance formula

**Error Type**: AccessibilityWarning (non-blocking)

---

### Rule 6: Theme name must match filename

Theme JSON `"name"` field must match filename (without .json extension).

**Example**: File `corporate.json` → `"name": "corporate"`

**Rationale**: Prevents confusion

**Error Type**: ThemeValidationError

---

### Rule 7: All errors collected (not fail-fast)

Validation collects ALL errors before returning.

**Return Type**: `Either[NonEmptyList[ThemeValidationError], Theme]`

---

## 🟢 Green Cards: Examples

### ✅ EXAMPLE 1: Valid custom theme

```markdown
GIVEN theme file themes/corporate.json:
  {
    "name": "corporate",
    "colors": {
      "background": "#FFFFFF",
      "foreground": "#003366",
      "heading": "#001F3F",
      "accent": "#FF6900"
    },
    "fonts": {
      "family": "Helvetica, sans-serif",
      "bodySize": 28,
      "headingSize": 40
    },
    "layout": {
      "slideWidth": 1920,
      "slideHeight": 1080,
      "slidePadding": 40,
      "maxBodyLines": 12
    }
  }

WHEN validated

THEN:
  - Validation: SUCCESS
  - Theme loaded and available

Rules tested: 1, 2, 3, 4, 5, 6
```

---

### ✅ EXAMPLE 2: Minimal valid theme

```markdown
GIVEN theme with only required fields:
  {
    "name": "minimal",
    "colors": { "background": "#FFF", "foreground": "#000", "heading": "#000", "accent": "#00F" },
    "fonts": { "family": "Arial", "bodySize": 18, "headingSize": 24 },
    "layout": { "slideWidth": 1024, "slideHeight": 768, "slidePadding": 20, "maxBodyLines": 12 }
  }

WHEN validated

THEN:
  - Validation: SUCCESS
  - Optional fields (metadata, accessibility) use defaults

Rules tested: 1
```

---

### ❌ EXAMPLE 3: Missing required field

```markdown
GIVEN theme missing 'colors' field:
  {
    "name": "incomplete",
    "fonts": { ... },
    "layout": { ... }
  }

WHEN validated

THEN:
  - Validation: FAILED
  - Error: ThemeValidationError
  - Message: "Theme 'incomplete' missing required field 'colors'"

Rules tested: 1
```

---

### ❌ EXAMPLE 4: Invalid hex color (non-hex string)

```markdown
GIVEN theme with invalid color:
  {
    "colors": {
      "background": "white"  <-- not hex
    }
  }

WHEN validated

THEN:
  - Validation: FAILED
  - Error: ThemeValidationError
  - Message: "Invalid color value 'white' for field 'background' (must be hex code)"

Rules tested: 2
```

---

### ❌ EXAMPLE 5: Invalid hex color (bad hex digits)

```markdown
GIVEN theme with invalid hex:
  {
    "colors": {
      "background": "#GGGGGG"  <-- invalid hex
    }
  }

WHEN validated

THEN:
  - Validation: FAILED
  - Error: ThemeValidationError
  - Message: "Invalid hex color '#GGGGGG' for field 'background'"

Rules tested: 2
```

---

### ❌ EXAMPLE 6: Font size below minimum

```markdown
GIVEN theme with small font:
  {
    "fonts": {
      "bodySize": 12  <-- below 18px minimum
    }
  }

WHEN validated

THEN:
  - Validation: FAILED
  - Error: ThemeValidationError
  - Message: "Font size 'bodySize' is 12 (minimum 18 for accessibility)"

Rules tested: 3
```

---

### ✅ EXAMPLE 7: Font size at minimum (boundary)

```markdown
GIVEN theme with:
  {
    "fonts": {
      "bodySize": 18  <-- exactly at minimum
    }
  }

WHEN validated

THEN:
  - Validation: SUCCESS

Rules tested: 3
```

---

### ❌ EXAMPLE 8: Slide width exceeds maximum

```markdown
GIVEN theme with:
  {
    "layout": {
      "slideWidth": 5000  <-- exceeds 3840px max
    }
  }

WHEN validated

THEN:
  - Validation: FAILED
  - Error: ThemeValidationError
  - Message: "Slide width 5000 exceeds maximum 3840"

Rules tested: 4
```

---

### ❌ EXAMPLE 9: Slide height below minimum

```markdown
GIVEN theme with:
  {
    "layout": {
      "slideHeight": 500  <-- below 600px min
    }
  }

WHEN validated

THEN:
  - Validation: FAILED
  - Error: ThemeValidationError
  - Message: "Slide height 500 below minimum 600"

Rules tested: 4
```

---

### ⚠️ EXAMPLE 10: Poor contrast ratio (warning)

```markdown
GIVEN theme with:
  {
    "colors": {
      "background": "#FFFFFF",
      "foreground": "#AAAAAA"  <-- low contrast (2.3:1)
    }
  }

WHEN validated

THEN:
  - Validation: SUCCESS (warning, not error)
  - Warning: AccessibilityWarning
  - Message: "Contrast ratio 2.3:1 below WCAG AA minimum 4.5:1"

Rules tested: 5
```

---

### ✅ EXAMPLE 11: Good contrast ratio (WCAG AA)

```markdown
GIVEN theme with:
  {
    "colors": {
      "background": "#FFFFFF",
      "foreground": "#000000"  <-- high contrast (21:1)
    }
  }

WHEN validated

THEN:
  - Validation: SUCCESS
  - No accessibility warnings

Rules tested: 5
```

---

### ✅ EXAMPLE 12: Contrast exactly at minimum (boundary)

```markdown
GIVEN theme with contrast ratio exactly 4.5:1

WHEN validated

THEN:
  - Validation: SUCCESS
  - No accessibility warnings

Rules tested: 5
```

---

### ❌ EXAMPLE 13: Theme name mismatch

```markdown
GIVEN theme file themes/corporate.json with:
  {
    "name": "company"  <-- should be "corporate"
  }

WHEN validated

THEN:
  - Validation: FAILED
  - Error: ThemeValidationError
  - Message: "Theme name 'company' does not match filename 'corporate.json'"

Rules tested: 6
```

---

### ❌ EXAMPLE 14: Multiple validation errors

```markdown
GIVEN theme with:
  - Missing required field 'layout'
  - Invalid hex color for 'background'
  - Font size below minimum (14px)

WHEN validated

THEN:
  - Validation: FAILED
  - Errors: 3 ThemeValidationErrors
  - All errors collected in NonEmptyList
  - Error 1: "Missing required field 'layout'"
  - Error 2: "Invalid color value..."
  - Error 3: "Font size 'bodySize' is 14..."

Rules tested: 1, 2, 3, 7
```

---

## 🔴 Red Cards: Questions (All Resolved ✅)

### Q1: Is metadata required? ✅

**Answer**: No. `metadata` is optional (useful for documentation but not required for theme functionality).

---

### Q2: Is accessibility required? ✅

**Answer**: No. If missing, accessibility checks use calculated values (contrast ratio) and defaults.

---

### Q3: Support RGB/HSL colors? ✅

**Answer**: No for v1.0. Hex codes only (`#RRGGBB` or `#RRGGBBAA`). RGB/HSL deferred to v1.1.

---

### Q4: Font size units? ✅

**Answer**: v1.0 assumes px (numbers without units). Support for pt/em/rem deferred to v1.1.

---

### Q5: Why max 3840px width? ✅

**Answer**: 4K resolution (3840x2160) is maximum practical display size for presentations.

---

### Q6: Contrast error or warning? ✅

**Answer**: Warning (non-blocking). Author may have valid reason. Allows flexibility while informing author of accessibility concern.

---

### Q7: Which color pairs to check? ✅

**Answer**: v1.0 checks background vs foreground only. Additional pairs (heading vs background, accent vs background) deferred to v1.1.

---

### Q8: Enforce name/filename match? ✅

**Answer**: Yes. Theme name in JSON must match filename (without .json) to prevent confusion when loading themes.

---

## 🎯 Story Readiness Assessment

### Coverage Summary

| Metric | Count |
|--------|-------|
| Rules | 7 blue cards |
| Examples | 14 green cards (4 success, 10 failure/warning) |
| Questions | 8 red cards (all resolved ✅) |

### Rule → Example Coverage

```
Rule 1 (Required fields):
├── Example 1 ✅ (all fields present)
├── Example 2 ✅ (minimal required fields)
├── Example 3 ❌ (missing colors)
└── Example 14 ❌ (missing layout)

Rule 2 (Valid hex colors):
├── Example 1 ✅ (valid hex)
├── Example 4 ❌ (non-hex string)
├── Example 5 ❌ (invalid hex digits)
└── Example 14 ❌ (invalid color in multi-error)

Rule 3 (Font size >= 18):
├── Example 1 ✅ (28px, above minimum)
├── Example 6 ❌ (12px, below minimum)
├── Example 7 ✅ (18px, at minimum)
└── Example 14 ❌ (14px in multi-error)

Rule 4 (Layout dimensions):
├── Example 1 ✅ (1920x1080, valid)
├── Example 2 ✅ (1024x768, valid)
├── Example 8 ❌ (width exceeds max)
└── Example 9 ❌ (height below min)

Rule 5 (Contrast ratio):
├── Example 10 ⚠️ (poor contrast, warning)
├── Example 11 ✅ (good contrast)
└── Example 12 ✅ (exactly at minimum)

Rule 6 (Name/filename match):
└── Example 13 ❌ (name mismatch)

Rule 7 (Collect all errors):
└── Example 14 ❌ (3 errors collected)
```

### Confidence Level

**HIGH CONFIDENCE** ✅

**Reasons**:
- All 8 questions resolved
- 7 clear business rules identified
- 14 concrete examples cover success, errors, warnings
- Examples cover boundary conditions (exact minimums/maximums)
- Examples cover multiple errors (non-fail-fast pattern)
- Clear accessibility integration (contrast warnings)

**Risks Identified**:
- WCAG contrast calculation complexity (need reference implementation)
- Font availability validation deferred (accept any string for v1.0)
- Hex color parsing (handle malformed input gracefully)

**Ready for TDD**: YES ✅

---

## 📋 Acceptance Criteria Summary

### Must Have (Critical)

1. ✅ All required fields validated (name, colors, fonts, layout)
2. ✅ Color values validated (valid hex codes)
3. ✅ Font sizes validated (>= 18px)
4. ✅ Layout dimensions validated (800-3840 width, 600-2160 height)
5. ✅ Theme name matches filename
6. ✅ All errors collected (NonEmptyList pattern)

### Should Have (Important)

7. ✅ Contrast ratio >= 4.5:1 (WCAG AA)
8. ✅ Poor contrast → AccessibilityWarning (non-blocking)
9. ✅ Clear error messages (which field, why invalid)
10. ✅ Warnings collected separately from errors

### Nice to Have (Enhancement)

11. ✅ Theme validation < 20ms
12. ✅ Contrast calculation < 5ms

---

## 🎨 Visual Example Map

```
┌────────────────────────────────────────────────────────────┐
│                      📘 USER STORY                         │
│         Create and Validate Custom Theme (US-009)          │
└────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R1 │          │ 🔵 R2 │          │ 🔵 R3 │
    │Require│          │ Valid │          │ Font  │
    │Fields │          │  Hex  │          │ >=18  │
    └───┬───┘          └───┬───┘          └───┬───┘
        │                  │                  │
    ┌───┴──┬──────┐   ┌───┴──┬──────┐   ┌───┴──┬──────┐
    │✅ E1 │✅ E2 │   │✅ E1 │❌ E4 │   │✅ E1 │❌ E6 │
    │❌ E3 │❌E14 │   │❌ E5 │❌E14 │   │✅ E7 │❌E14 │
    └──────┴──────┘   └──────┴──────┘   └──────┴──────┘

    ┌───────┐          ┌───────┐          ┌───────┐
    │ 🔵 R4 │          │ 🔵 R5 │          │ 🔵 R6 │
    │Layout │          │Contras│          │ Name  │
    │Dimens │          │  >=   │          │Match  │
    └───┬───┘          │ 4.5:1 │          └───┬───┘
        │              └───┬───┘              │
    ┌───┴──┬──────┐   ┌───┴──┬──────┐   ┌───┴──┐
    │✅ E1 │✅ E2 │   │⚠️ E10│✅E11 │   │❌E13 │
    │❌ E8 │❌ E9 │   │✅E12 │      │   └──────┘
    └──────┴──────┘   └──────┴──────┘

    ┌───────┐
    │ 🔵 R7 │
    │Collect│
    │  All  │
    └───┬───┘
        │
    ┌───┴──┐
    │❌E14 │
    └──────┘

Questions (All ✅):
Q1-Q8: All resolved (see Red Cards section)

Error Types:
- ThemeValidationError (blocking)
- AccessibilityWarning (non-blocking)
```

---

## 🔄 Next Steps

1. ✅ **Three Amigos Complete**: All rules and examples documented
2. ✅ **Example Mapping Complete**: 14 examples with full coverage
3. 🔄 **Update BACKLOG-V3.md**: Mark US-009 as Ready for Implementation
4. ⏭️ **Proceed to US-016**: HTML Rendering (rendering + CLI ceremonies)

---

**Session Type**: Ceremony 2.2 - Example Mapping Workshop
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Story Status**: Ready for TDD ✅
