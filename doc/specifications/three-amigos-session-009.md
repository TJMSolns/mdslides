# Three Amigos Session #009
## Create and Validate Custom Theme

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-009-CUSTOM-THEME
  date: 2024-12-20
  user_story_id: US-009
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 45-60
  status: complete

story:
  title: Create and Validate Custom Theme
  type: Feature
  priority: P0 (Blocker)
  epic: Theme System
```

---

## 📋 User Story

**As a** slide deck author
**I want to** create custom themes using JSON with validation
**So that** I can match my organization's branding while maintaining accessibility

**Business Value**: Essential for corporate use - every organization needs branded slides. Validation ensures themes are well-formed and accessible.

**Technical Scope**: Validate custom theme JSON structure, required fields, accessibility constraints (WCAG compliance)

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author creates custom theme JSON file
- Theme validation ensures:
  - All required fields present
  - Valid color values (hex codes)
  - Valid font sizes (readable)
  - Valid layout dimensions
  - Accessibility compliance (contrast ratios)
- Invalid themes rejected with clear error messages
- Valid themes work seamlessly

**Use Cases**:
- **Corporate Branding**: Company creates `corporate.json` with brand colors/fonts
- **Dark Mode**: Author creates `dark.json` for low-light presentations
- **High Contrast**: Accessibility-focused theme for vision-impaired users

**Acceptance criteria**:
- ✅ Custom theme JSON loaded from `themes/` directory
- ✅ All required fields validated (colors, typography, layout)
- ✅ Color values validated (valid hex codes)
- ✅ Font sizes validated (min 18px for accessibility)
- ✅ Layout dimensions validated (reasonable slide sizes)
- ✅ Accessibility validated (WCAG AA contrast ratios)
- ✅ Missing required field → ValidationError
- ✅ Invalid color value → ValidationError
- ✅ Invalid font size → ValidationError
- ✅ Poor contrast → AccessibilityWarning

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **Theme JSON Schema**:
   ```json
   {
     "name": "theme-name",
     "metadata": {
       "version": "1.0.0",
       "author": "Author Name",
       "created": "YYYY-MM-DD",
       "wcagLevel": "AA"
     },
     "colors": {
       "background": "#FFFFFF",
       "foreground": "#000000",
       "heading": "#1a1a1a",
       "accent": "#0066CC",
       "code_background": "#f5f5f5",
       "code_foreground": "#24292e"
     },
     "fonts": {
       "family": "Arial, sans-serif",
       "titleSize": 52,
       "subtitleSize": 40,
       "headingSize": 36,
       "bodySize": 28,
       "codeSize": 24,
       "codeFontFamily": "monospace"
     },
     "layout": {
       "slidePadding": 40,
       "maxBodyLines": 12,
       "slideWidth": 1920,
       "slideHeight": 1080,
       "lineHeight": 1.5
     },
     "content": {
       "preserveMarkdownInHeadings": true,
       "allowEmoji": true,
       "allowUnicodeSymbols": true
     },
     "accessibility": {
       "contrastRatio": 21.0,
       "minFontSize": 24,
       "requireAltText": true,
       "enforceHeadingHierarchy": true
     }
   }
   ```

2. **Validation Rules**:
   - **Required Fields**: name, colors, fonts, layout
   - **Color Values**: Must be valid hex codes (#RRGGBB or #RRGGBBAA)
   - **Font Sizes**: Must be >= 18 (accessibility minimum)
   - **Layout Dimensions**: slideWidth 800-3840, slideHeight 600-2160
   - **Contrast Ratios**: background vs foreground >= 4.5:1 (WCAG AA)

3. **Validation Return Type**:
   - Errors: `Either[NonEmptyList[ThemeValidationError], Theme]`
   - Warnings: `List[AccessibilityWarning]`

4. **Accessibility Checks** (v1.0):
   - Contrast ratio calculation (WCAG formula)
   - Minimum font size (18px)
   - Color blindness simulation (deferred to v1.1)

**Technical risks**:
- **Contrast Calculation**: Complex WCAG algorithm (relative luminance)
- **Font Size Units**: Validate px, pt, em, rem (v1.0: px only)
- **Custom Fonts**: Font family validation (is font available?)
  - **Decision**: v1.0 doesn't validate font availability, just syntax

**Dependencies**:
- Circe (JSON parsing)
- Theme loading (US-008)
- WCAG contrast ratio algorithm

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. Valid custom theme with all fields → SUCCESS
2. Theme with minimal required fields → SUCCESS
3. Theme with WCAG AA contrast → SUCCESS

**Edge cases (errors)**:
1. Missing required field (colors) → ThemeValidationError
2. Invalid hex color (#GGGGGG) → ThemeValidationError
3. Font size too small (12px, below 18px min) → ThemeValidationError
4. Slide width too large (5000px, above 3840px max) → ThemeValidationError
5. Missing contrast ratio data → default to calculated value

**Edge cases (warnings)**:
1. Contrast ratio below 4.5:1 → AccessibilityWarning (not blocking)
2. Font size 18px (at minimum) → valid, no warning
3. Contrast ratio exactly 4.5:1 → valid, no warning

**Boundary cases**:
1. Font size = 18px (minimum) → valid
2. Slide width = 3840px (maximum) → valid
3. Contrast ratio = 4.5:1 (minimum WCAG AA) → valid

**Non-functional requirements**:
- Theme validation < 20ms
- Contrast calculation < 5ms
- Clear error messages (which field, why invalid)

---

## 🗂️ Example Mapping

### Rule 1: All required fields must be present

**Required Fields**: name, colors, fonts, layout

**Examples**:
- ✅ **Valid**: Theme with all 4 required fields
  → Theme loads successfully
- ❌ **Invalid**: Theme missing `colors` field
  → ThemeValidationError: "Theme 'custom' missing required field 'colors'"

**Questions**:
- Q1: Is `metadata` required?
  - **Decision**: No. `metadata` is optional (useful for documentation but not required).
- Q2: Is `accessibility` required?
  - **Decision**: No. If missing, accessibility checks use calculated/default values.

---

### Rule 2: Color values must be valid hex codes

**Valid Formats**: `#RRGGBB` (6-digit) or `#RRGGBBAA` (8-digit with alpha)

**Examples**:
- ✅ **Valid**: `"background": "#FFFFFF"`
- ✅ **Valid**: `"background": "#FFFFFFff"` (with alpha)
- ❌ **Invalid**: `"background": "white"` (not hex code)
  → ThemeValidationError: "Invalid color value 'white' for field 'background' (must be hex code)"
- ❌ **Invalid**: `"background": "#GGGGGG"` (invalid hex)
  → ThemeValidationError: "Invalid hex color '#GGGGGG' for field 'background'"

**Questions**:
- Q3: Support RGB/RGBA/HSL?
  - **Decision**: No for v1.0. Hex codes only. Other formats deferred to v1.1.

---

### Rule 3: Font sizes must be >= 18px (accessibility minimum)

**Examples**:
- ✅ **Valid**: `"bodySize": 28`
- ✅ **Valid**: `"bodySize": 18` (at minimum)
- ❌ **Invalid**: `"bodySize": 12`
  → ThemeValidationError: "Font size 'bodySize' is 12 (minimum 18 for accessibility)"

**Questions**:
- Q4: Units (px vs pt vs em)?
  - **Decision**: v1.0 assumes px (numbers without units). Other units deferred to v1.1.

---

### Rule 4: Layout dimensions must be reasonable

**Slide Width**: 800-3840 px (4K max)
**Slide Height**: 600-2160 px (4K max)

**Examples**:
- ✅ **Valid**: `"slideWidth": 1920, "slideHeight": 1080` (Full HD)
- ✅ **Valid**: `"slideWidth": 3840, "slideHeight": 2160` (4K)
- ❌ **Invalid**: `"slideWidth": 5000`
  → ThemeValidationError: "Slide width 5000 exceeds maximum 3840"
- ❌ **Invalid**: `"slideHeight": 500`
  → ThemeValidationError: "Slide height 500 below minimum 600"

**Questions**:
- Q5: Why max 3840px?
  - **Decision**: 4K resolution (3840x2160) is maximum practical display size.

---

### Rule 5: Contrast ratio >= 4.5:1 (WCAG AA)

**Calculation**: WCAG relative luminance formula

**Examples**:
- ✅ **Valid**: background `#FFFFFF`, foreground `#000000` → contrast 21:1
- ⚠️ **Warning**: background `#FFFFFF`, foreground `#AAAAAA` → contrast 2.3:1
  → AccessibilityWarning: "Contrast ratio 2.3:1 below WCAG AA minimum 4.5:1"
- ✅ **Valid**: contrast exactly 4.5:1 → valid, no warning

**Questions**:
- Q6: Is poor contrast an error or warning?
  - **Decision**: Warning (non-blocking). Author may have valid reason (e.g., decorative elements). WCAG AAA (7:1) deferred to v1.1.
- Q7: Which color pairs to check?
  - **Decision**: v1.0 checks background vs foreground only. Additional pairs (heading vs background, accent vs background) deferred to v1.1.

---

### Rule 6: Theme name must match filename

**Examples**:
- ✅ **Valid**: File `themes/corporate.json` has `"name": "corporate"`
- ❌ **Invalid**: File `themes/corporate.json` has `"name": "company"`
  → ThemeValidationError: "Theme name 'company' does not match filename 'corporate.json'"

**Questions**:
- Q8: Why enforce name/filename match?
  - **Decision**: Prevents confusion. Theme name in JSON must match filename (without .json).

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Valid Custom Theme

```gherkin
Feature: Custom Theme Validation

  Scenario: Valid custom theme loads successfully
    Given I have a theme file themes/corporate.json:
      """
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
      """
    When I load and validate the theme
    Then theme validation succeeds
    And theme is available for use
```

---

### Example 2: Missing Required Field ❌

```gherkin
  Scenario: Theme missing required field 'colors'
    Given I have a theme file themes/incomplete.json:
      """
      {
        "name": "incomplete",
        "fonts": { ... },
        "layout": { ... }
      }
      """
    When I load and validate the theme
    Then validation fails with ThemeValidationError
    And error message is "Theme 'incomplete' missing required field 'colors'"
```

---

### Example 3: Invalid Hex Color ❌

```gherkin
  Scenario: Theme has invalid hex color
    Given I have a theme with:
      """
      {
        "colors": {
          "background": "white",  <-- not hex
          "foreground": "#000000"
        }
      }
      """
    When I load and validate the theme
    Then validation fails with ThemeValidationError
    And error message contains "Invalid color value 'white' for field 'background' (must be hex code)"
```

---

### Example 4: Font Size Too Small ❌

```gherkin
  Scenario: Theme has font size below accessibility minimum
    Given I have a theme with:
      """
      {
        "fonts": {
          "bodySize": 12  <-- below 18px minimum
        }
      }
      """
    When I load and validate the theme
    Then validation fails with ThemeValidationError
    And error message is "Font size 'bodySize' is 12 (minimum 18 for accessibility)"
```

---

### Example 5: Slide Width Exceeds Maximum ❌

```gherkin
  Scenario: Theme slide width exceeds 4K maximum
    Given I have a theme with:
      """
      {
        "layout": {
          "slideWidth": 5000  <-- exceeds 3840px max
        }
      }
      """
    When I load and validate the theme
    Then validation fails with ThemeValidationError
    And error message is "Slide width 5000 exceeds maximum 3840"
```

---

### Example 6: Poor Contrast Ratio ⚠️

```gherkin
  Scenario: Theme has poor contrast (warning, not error)
    Given I have a theme with:
      """
      {
        "colors": {
          "background": "#FFFFFF",
          "foreground": "#AAAAAA"  <-- low contrast
        }
      }
      """
    When I load and validate the theme
    Then theme validation succeeds (warning, not error)
    And accessibility warning produced:
      "Contrast ratio 2.3:1 below WCAG AA minimum 4.5:1"
```

---

### Example 7: Theme Name Mismatch ❌

```gherkin
  Scenario: Theme name doesn't match filename
    Given I have a theme file themes/corporate.json with:
      """
      {
        "name": "company"  <-- should be "corporate"
      }
      """
    When I load and validate the theme
    Then validation fails with ThemeValidationError
    And error message is "Theme name 'company' does not match filename 'corporate.json'"
```

---

### Example 8: Multiple Validation Errors ❌

```gherkin
  Scenario: Theme has multiple validation errors
    Given I have a theme with:
      - Missing required field 'layout'
      - Invalid hex color for 'background'
      - Font size below minimum (14px)
    When I load and validate the theme
    Then validation fails with 3 errors
    And all errors are collected in NonEmptyList[ThemeValidationError]
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | Is metadata required? | ✅ Resolved | No (optional) |
| Q2 | Is accessibility required? | ✅ Resolved | No (use calculated/default values) |
| Q3 | Support RGB/HSL colors? | ✅ Resolved | No for v1.0 (hex only) |
| Q4 | Font size units? | ✅ Resolved | v1.0 assumes px (numbers without units) |
| Q5 | Why max 3840px width? | ✅ Resolved | 4K resolution maximum |
| Q6 | Contrast error or warning? | ✅ Resolved | Warning (non-blocking) |
| Q7 | Which color pairs to check? | ✅ Resolved | v1.0 checks background vs foreground only |
| Q8 | Enforce name/filename match? | ✅ Resolved | Yes (prevents confusion) |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: All required fields validated (name, colors, fonts, layout)
2. ✅ **AC2**: Color values validated (valid hex codes)
3. ✅ **AC3**: Font sizes validated (>= 18px)
4. ✅ **AC4**: Layout dimensions validated (reasonable ranges)
5. ✅ **AC5**: Contrast ratio >= 4.5:1 (WCAG AA)
6. ✅ **AC6**: Theme name matches filename
7. ✅ **AC7**: Missing required field → ThemeValidationError
8. ✅ **AC8**: Invalid color → ThemeValidationError
9. ✅ **AC9**: Invalid font size → ThemeValidationError
10. ✅ **AC10**: Poor contrast → AccessibilityWarning (non-blocking)

### Technical Criteria

11. ✅ **AC11**: Return type `Either[NonEmptyList[ThemeValidationError], Theme]`
12. ✅ **AC12**: Warnings collected separately (List[AccessibilityWarning])
13. ✅ **AC13**: Theme validation < 20ms
14. ✅ **AC14**: Contrast calculation < 5ms
15. ✅ **AC15**: All errors collected (not fail-fast)
16. ✅ **AC16**: All domain terms from ubiquitous language used

### Non-Functional Criteria

17. ✅ **AC17**: Clear error messages (which field, why invalid)
18. ✅ **AC18**: WCAG relative luminance formula implemented correctly
19. ✅ **AC19**: Pure functional validation (no side effects)

**Scenarios**: 8 concrete examples documented
- 2 success paths (with warning)
- 6 error paths

**Dependencies**:
- Circe (JSON parsing)
- Theme loading (US-008)
- WCAG contrast ratio algorithm

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG-V3.md](../../BACKLOG-V3.md)
- **Domain Model**: [theme-aggregate.md](../domain-models/aggregates/theme-aggregate.md) (to be created)
- **Ubiquitous Language**: [ubiquitous-language.md](../domain-models/ubiquitous-language.md)
- **Related Stories**: US-008 Apply Theme, US-014 Accessibility Validation (v1.1)

---

## 🎯 Next Steps

1. **Create Example Mapping visual** (Ceremony 2.2)
2. **Implement WCAG contrast calculation** (technical reference needed)
3. **Document formal acceptance criteria** in BACKLOG-V3.md
4. **Proceed to Ceremony 2.2**: Example Mapping Workshop (refine scenarios)

---

**Session Type**: Ceremony 2.1 - Three Amigos Session
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Next Review**: After Example Mapping Workshop (Ceremony 2.2)
