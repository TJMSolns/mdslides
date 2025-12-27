# Three Amigos Session #008
## Apply Theme to SlideDeck

---

```yaml
# MACHINE-READABLE METADATA
session:
  id: 3A-008-APPLY-THEME
  date: 2024-12-20
  user_story_id: US-008
  participants:
    - Tony Moores (Business/Dev/QA)
  duration_minutes: 45-60
  status: complete

story:
  title: Apply Theme to SlideDeck
  type: Feature
  priority: P0 (Blocker)
  epic: Theme System
```

---

## 📋 User Story

**As a** slide deck author
**I want to** specify a theme in deck metadata
**So that** my slides have consistent visual styling

**Business Value**: Enables branded presentations, visual consistency across decks, professional appearance

**Technical Scope**: Load theme from JSON file, apply CSS and layout settings to rendered HTML

---

## 🎭 Three Perspectives

### 👔 Business Perspective (Product Owner)

**What success looks like**:
- Author specifies theme in front matter: `theme: default`
- Theme controls visual styling (colors, fonts, spacing)
- Theme controls density limits (max lines, max words)
- Default theme provided (no configuration required)
- Custom themes supported (load from file)

**Theme Capabilities**:
- **Colors**: Background, text, heading, accent
- **Typography**: Font families, sizes, weights
- **Layout**: Slide dimensions, padding, margins
- **Density Limits**: Max body lines, max body words, max heading chars (configurable per theme)

**Acceptance criteria**:
- ✅ Theme specified via `theme:` in global front matter
- ✅ Default theme used if not specified
- ✅ Theme loaded from JSON file
- ✅ Theme CSS inlined into HTML output
- ✅ Theme layout settings applied during rendering
- ✅ Theme density limits used in Density Validation (US-012)
- ✅ Invalid theme name → error

---

### 💻 Development Perspective (Technical)

**Implementation approach**:
1. **Theme Model** (domain entity):
   ```scala
   case class Theme(
     name: String,
     metadata: ThemeMetadata,
     colors: ColorScheme,
     typography: Typography,
     layout: LayoutSettings
   )

   case class ColorScheme(
     background: String,      // "#FFFFFF"
     text: String,            // "#333333"
     heading: String,         // "#1a1a1a"
     accent: String           // "#007acc"
   )

   case class Typography(
     bodyFont: String,        // "Arial, sans-serif"
     headingFont: String,     // "Helvetica, sans-serif"
     baseFontSize: String,    // "24px"
     headingFontSize: String  // "36px"
   )

   case class LayoutSettings(
     slideWidth: String,      // "1024px"
     slideHeight: String,     // "768px"
     padding: String,         // "40px"
     maxBodyLines: Int,       // 12 (for Density Validation)
     maxBodyWords: Int,       // 150 (for Density Validation)
     maxHeadingChars: Int     // 80 (for Density Validation)
   )
   ```

2. **Theme Loading**:
   - Read theme name from SlideDeck front matter (default: "default")
   - Load theme JSON from `themes/<name>.json`
   - Parse JSON into Theme case class (using Circe)
   - Validate theme structure (all required fields present)

3. **Theme Application**:
   - During HTML rendering, inject theme CSS into `<style>` tag
   - Apply theme layout settings to slide containers
   - Use theme density limits in Density Validation

4. **Default Theme**:
   - Bundled with application as `themes/default.json`
   - Professional, accessible color scheme (WCAG AA compliant)
   - Reasonable density limits (12 lines, 150 words, 80 chars)

**Technical risks**:
- **Theme not found**: Handle missing theme file gracefully (error message)
- **Invalid JSON**: Handle malformed theme files (parsing error)
- **CSS injection**: Ensure theme CSS doesn't break HTML structure
- **Density integration**: Theme layout must be available during validation (before rendering)

**Dependencies**:
- Circe (JSON parsing)
- File system (load theme JSON files)
- Density Validation (US-012) - uses theme layout settings

---

### 🧪 Testing Perspective (Quality/Edge Cases)

**Happy path scenarios**:
1. No theme specified → default theme used
2. `theme: default` → default theme loaded
3. `theme: dark` → dark theme loaded (custom)
4. Theme CSS applied to HTML output
5. Theme density limits used in validation

**Edge cases (errors)**:
1. `theme: nonexistent` → error "Theme 'nonexistent' not found"
2. Theme JSON is malformed → parsing error
3. Theme JSON missing required fields → validation error
4. Theme file not readable → I/O error

**Boundary cases**:
1. Theme with minimal CSS (only colors) → valid
2. Theme with extensive CSS (custom animations) → valid
3. Theme density limits at extremes (max 1 line) → valid but impractical

**Non-functional requirements**:
- Theme loading < 10ms
- Theme CSS inlining < 5ms
- Theme JSON files < 50 KB (reasonable size)

---

## 🗂️ Example Mapping

### Rule 1: Theme specified via `theme:` in global front matter

**Examples**:
- ✅ **Valid**: Front matter with `theme: default`
  → Default theme loaded
- ✅ **Valid**: Front matter with `theme: dark`
  → Dark theme loaded from `themes/dark.json`
- ✅ **Valid**: No `theme:` field
  → Default theme used (implicit default)
- ❌ **Invalid**: `theme: nonexistent`
  → Error: "Theme 'nonexistent' not found. Available themes: default, dark"

**Questions**:
- Q1: Where are theme files stored?
  - **Decision**: `themes/` directory relative to application. Bundled themes + user themes.
- Q2: Can theme be overridden per slide?
  - **Decision**: No for v1.0. Global theme only. Per-slide themes deferred to v2.0.

---

### Rule 2: Default theme used if not specified

**Examples**:
- ✅ **Valid**: Markdown with no front matter
  → Default theme applied
- ✅ **Valid**: Front matter with `template: content` (no theme)
  → Default theme applied

**Questions**:
- Q3: What if default theme is missing?
  - **Decision**: Fatal error. Default theme must be bundled with application.

---

### Rule 3: Theme loaded from JSON file

**Examples**:
- ✅ **Valid**: `themes/default.json` exists
  → Theme loaded successfully
- ❌ **Invalid**: `themes/custom.json` malformed JSON
  → Parsing error: "Failed to parse theme 'custom': invalid JSON at line 5"

**Questions**:
- Q4: JSON schema validation?
  - **Decision**: Yes. Validate all required fields present (colors, typography, layout).

---

### Rule 4: Theme CSS inlined into HTML output

**Examples**:
- ✅ **Valid**: Theme has `colors.background: "#FFFFFF"`
  → HTML contains `<style> body { background-color: #FFFFFF; } </style>`

**Questions**:
- Q5: External CSS vs inline?
  - **Decision**: Inline for v1.0 (single-file HTML output). External CSS deferred to v1.1.

---

### Rule 5: Theme layout settings applied during rendering

**Examples**:
- ✅ **Valid**: Theme has `layout.slideWidth: "1024px"`
  → HTML slide containers have `width: 1024px`

**Questions**:
- Q6: Responsive design?
  - **Decision**: Not in v1.0. Fixed slide dimensions. Responsive deferred to v1.1.

---

### Rule 6: Theme density limits used in Density Validation

**Examples**:
- ✅ **Valid**: Theme has `layout.maxBodyLines: 10`
  → Density Validation uses 10 lines (not default 12)
- ⚠️ **Warning**: Body has 11 lines, theme max is 10
  → DensityWarning: "Body exceeds max 10 lines (found 11 lines)"

**Questions**:
- Q7: Theme loaded before validation?
  - **Decision**: Yes. Theme must be loaded early (after parsing, before validation) so Density Validation can use theme limits.

---

### Rule 7: Invalid theme name → error

**Examples**:
- ❌ **Invalid**: `theme: missing-theme`
  → Error: "Theme 'missing-theme' not found in themes/ directory"

**Questions**:
- Q8: List available themes in error?
  - **Decision**: Yes. Scan `themes/` directory, list available `.json` files.

---

## 📝 Concrete Examples (Given/When/Then)

### Example 1: Default Theme (No theme specified)

```gherkin
Feature: Theme Application

  Scenario: No theme specified, default theme applied
    Given I have a markdown file with:
      """
      ---
      template: title
      ---
      # My Presentation
      """
    When I parse and render the markdown
    Then the default theme is loaded
    And HTML output contains default theme CSS
    And slide background color is "#FFFFFF" (default theme)
```

---

### Example 2: Explicit Default Theme

```gherkin
  Scenario: Explicitly specify default theme
    Given I have a markdown file with:
      """
      ---
      theme: default
      template: title
      ---
      # My Presentation
      """
    When I parse and render the markdown
    Then the default theme is loaded
    And HTML output contains default theme CSS
```

---

### Example 3: Custom Theme (Dark)

```gherkin
  Scenario: Specify custom dark theme
    Given I have a theme file at "themes/dark.json" with:
      """
      {
        "name": "dark",
        "colors": {
          "background": "#1e1e1e",
          "text": "#d4d4d4",
          "heading": "#ffffff",
          "accent": "#569cd6"
        },
        ...
      }
      """
    And I have a markdown file with:
      """
      ---
      theme: dark
      template: title
      ---
      # My Presentation
      """
    When I parse and render the markdown
    Then the dark theme is loaded
    And HTML output contains dark theme CSS
    And slide background color is "#1e1e1e"
```

---

### Example 4: Theme Not Found ❌

```gherkin
  Scenario: Specified theme does not exist
    Given I have a markdown file with:
      """
      ---
      theme: nonexistent
      ---
      # Slide
      """
    When I parse and render the markdown
    Then rendering fails with error
    And error message is "Theme 'nonexistent' not found. Available themes: default, dark"
```

---

### Example 5: Theme JSON Malformed ❌

```gherkin
  Scenario: Theme JSON is invalid
    Given I have a theme file at "themes/broken.json" with malformed JSON
    And I have a markdown file with `theme: broken`
    When I parse and render the markdown
    Then rendering fails with error
    And error message contains "Failed to parse theme 'broken': invalid JSON"
```

---

### Example 6: Theme CSS Inlined into HTML

```gherkin
  Scenario: Theme CSS is inlined in HTML output
    Given I have a theme with colors and typography
    When I render a slide deck with that theme
    Then HTML output contains:
      """
      <style>
        body {
          background-color: #FFFFFF;
          color: #333333;
          font-family: Arial, sans-serif;
          font-size: 24px;
        }
        h1, h2 {
          color: #1a1a1a;
          font-family: Helvetica, sans-serif;
        }
        ...
      </style>
      """
```

---

### Example 7: Theme Density Limits Used in Validation

```gherkin
  Scenario: Theme defines custom density limits
    Given I have a theme with:
      """
      {
        "layout": {
          "maxBodyLines": 10,
          "maxBodyWords": 100,
          "maxHeadingChars": 60
        }
      }
      """
    And I have a markdown file with:
      """
      ---
      theme: custom
      ---
      ## Heading

      Line 1
      Line 2
      ...
      Line 11
      """
    When I validate the markdown
    Then density validation produces warning:
      "Body exceeds max 10 lines (found 11 lines)"
```

---

### Example 8: Theme Layout Settings Applied

```gherkin
  Scenario: Theme defines slide dimensions
    Given I have a theme with:
      """
      {
        "layout": {
          "slideWidth": "1280px",
          "slideHeight": "720px",
          "padding": "50px"
        }
      }
      """
    When I render a slide deck with that theme
    Then HTML slide containers have:
      - width: 1280px
      - height: 720px
      - padding: 50px
```

---

## 🚧 Open Questions

| ID | Question | Status | Decision |
|----|----------|--------|----------|
| Q1 | Where are theme files stored? | ✅ Resolved | `themes/` directory (bundled + user) |
| Q2 | Per-slide theme override? | ✅ Resolved | No for v1.0 (global only) |
| Q3 | Default theme missing? | ✅ Resolved | Fatal error (must be bundled) |
| Q4 | JSON schema validation? | ✅ Resolved | Yes, validate required fields |
| Q5 | External CSS vs inline? | ✅ Resolved | Inline for v1.0 (single-file output) |
| Q6 | Responsive design? | ✅ Resolved | Not in v1.0 (fixed dimensions) |
| Q7 | Theme loaded before validation? | ✅ Resolved | Yes (Density Validation needs theme limits) |
| Q8 | List available themes in error? | ✅ Resolved | Yes (scan `themes/` directory) |

---

## ✅ Acceptance Criteria (Definition of Done)

### Functional Criteria

1. ✅ **AC1**: Theme specified via `theme:` in global front matter
2. ✅ **AC2**: Default theme used if not specified
3. ✅ **AC3**: Theme loaded from JSON file
4. ✅ **AC4**: Theme CSS inlined into HTML output
5. ✅ **AC5**: Theme layout settings applied during rendering
6. ✅ **AC6**: Theme density limits used in Density Validation
7. ✅ **AC7**: Invalid theme name → error with available themes listed
8. ✅ **AC8**: Malformed theme JSON → parsing error

### Technical Criteria

9. ✅ **AC9**: Theme model with colors, typography, layout
10. ✅ **AC10**: Theme loading < 10ms
11. ✅ **AC11**: Theme CSS inlining < 5ms
12. ✅ **AC12**: Default theme bundled with application
13. ✅ **AC13**: Theme JSON files < 50 KB
14. ✅ **AC14**: Theme loaded early (before validation)
15. ✅ **AC15**: All domain terms from ubiquitous language used

### Non-Functional Criteria

16. ✅ **AC16**: Theme validation ensures all required fields present
17. ✅ **AC17**: Theme not found → clear error message
18. ✅ **AC18**: Pure functional theme loading (no side effects)

**Scenarios**: 8 concrete examples documented
- 3 success paths
- 5 error paths

**Dependencies**:
- Circe (JSON parsing)
- Density Validation (US-012) - uses theme layout settings
- HTML Rendering (US-016) - applies theme CSS

---

## 📚 Related Artifacts

- **User Story Tracker**: [BACKLOG-V3.md](../../BACKLOG-V3.md)
- **Domain Model**: [slide-deck-aggregate.md](../domain-models/aggregates/slide-deck-aggregate.md)
- **Ubiquitous Language**: [ubiquitous-language.md](../domain-models/ubiquitous-language.md)
- **Related Stories**: US-009 Custom Theme Validation, US-012 Density Validation, US-016 HTML Rendering

---

## 🎯 Next Steps

1. **Create default.json theme** (provide baseline theme)
2. **Create Example Mapping visual** (Ceremony 2.2)
3. **Document formal acceptance criteria** in BACKLOG-V3.md
4. **Proceed to Ceremony 2.2**: Example Mapping Workshop (refine scenarios)

---

**Session Type**: Ceremony 2.1 - Three Amigos Session
**Date**: 2024-12-20
**Facilitator**: Tony Moores (TJM Solutions)
**Next Review**: After Example Mapping Workshop (Ceremony 2.2)
