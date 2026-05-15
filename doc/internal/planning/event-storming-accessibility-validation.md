# Event Storming: Accessibility Validation (US-014)

**Feature**: WCAG 2.1 AA Compliance Validation
**Priority**: SHOULD HAVE (v1.4.0)
**Date**: 2025-12-27
**Participants**: Development Team

---

## Business Context

**As a** presentation author
**I want** automatic accessibility validation
**So that** my presentations are accessible to all users (including screen readers, keyboard-only navigation, color-blind users)

**Business Value:**
- Ensures presentations meet WCAG 2.1 AA standards
- Improves user experience for users with disabilities
- Provides actionable warnings to fix accessibility issues
- Builds trust in MDSlides as a professional tool

---

## Domain Events (Time-Ordered)

### 1. **Slide Deck Parsed** (EXISTING)
- **Trigger**: CLI parses markdown file
- **Data**: SlideDeck with all slides
- **Existing**: Already implemented
- **Note**: No changes needed

### 2. **Accessibility Validation Requested** (NEW)
- **Trigger**: After successful parsing, before rendering
- **Data**: SlideDeck, Theme
- **Action**: Run accessibility checks
- **Note**: Optional flag `--skip-accessibility` to bypass

### 3. **Color Contrast Checked** (NEW)
- **Trigger**: Accessibility validation runs
- **Data**: Theme colors (text, background, heading, accent, link)
- **Validation**: WCAG 2.1 AA requires 4.5:1 for normal text, 3:1 for large text
- **Output**: List of contrast ratio failures

### 4. **Alt Text Validated** (NEW)
- **Trigger**: Accessibility validation runs
- **Data**: ContentImages from all slides
- **Validation**: Every image must have non-empty alt text
- **Output**: List of images missing alt text (slide number + image URL)

### 5. **Keyboard Navigation Audited** (NEW)
- **Trigger**: Accessibility validation runs
- **Data**: Generated HTML navigation JavaScript
- **Validation**: All interactive elements accessible via keyboard
- **Output**: Warnings if navigation relies solely on mouse/touch

### 6. **Semantic HTML Checked** (NEW)
- **Trigger**: Accessibility validation runs
- **Data**: Generated HTML structure
- **Validation**: Proper heading hierarchy (h1 → h2 → h3, no skipping), landmarks, roles
- **Output**: Warnings for semantic issues

### 7. **Accessibility Report Generated** (NEW)
- **Trigger**: All checks complete
- **Data**: All validation results
- **Action**: Aggregate warnings and errors
- **Output**: Accessibility report (CLI warnings, optional JSON file)

### 8. **Warnings Displayed** (NEW)
- **Trigger**: Accessibility report generated
- **Data**: Warning messages
- **Action**: Print to CLI with severity (ERROR, WARNING, INFO)
- **Note**: Non-blocking - presentation still renders

### 9. **HTML Rendered with ARIA** (ENHANCEMENT)
- **Trigger**: After validation, rendering proceeds
- **Data**: SlideDeck with accessibility metadata
- **Enhancement**: Add ARIA labels, roles, landmarks to HTML
- **Output**: Accessible HTML with proper ARIA attributes

---

## Commands

### Existing Commands (No Changes)
1. **Parse Markdown** → Creates SlideDeck
2. **Validate Slide Deck** → Checks density, slide count, etc.

### New Commands
3. **Validate Accessibility**
   - Input: SlideDeck, Theme
   - Output: AccessibilityReport (warnings, errors, passes)
   - Checks: Contrast ratios, alt text, keyboard nav, semantic HTML

4. **Check Color Contrast**
   - Input: Theme colors
   - Output: List[ContrastViolation]
   - Algorithm: Calculate relative luminance, compute contrast ratio
   - Reference: WCAG 2.1 contrast formula

5. **Validate Image Alt Text**
   - Input: List[ContentImage]
   - Output: List[MissingAltTextWarning]
   - Check: altText.nonEmpty for all images

6. **Audit Keyboard Navigation**
   - Input: Navigation JavaScript
   - Output: List[KeyboardAccessibilityWarning]
   - Check: All nav actions accessible via keyboard (already true for MDSlides)

7. **Check Semantic HTML**
   - Input: Generated HTML
   - Output: List[SemanticWarning]
   - Check: Heading hierarchy, ARIA roles, landmarks

---

## Aggregates & Value Objects

### New Domain Objects

#### AccessibilityReport (Value Object)
```scala
case class AccessibilityReport(
  contrastViolations: List[ContrastViolation],
  missingAltText: List[MissingAltTextWarning],
  keyboardWarnings: List[KeyboardAccessibilityWarning],
  semanticWarnings: List[SemanticWarning]
):
  def hasErrors: Boolean = contrastViolations.exists(_.severity == Error)
  def hasWarnings: Boolean = contrastViolations.nonEmpty || missingAltText.nonEmpty
  def summary: String = // Human-readable summary
```

#### ContrastViolation (Value Object)
```scala
case class ContrastViolation(
  colorPair: (String, String), // (foreground, background)
  ratio: Double,
  required: Double, // 4.5 or 3.0
  location: String, // "body text", "heading", "link"
  severity: Severity // Error (fails WCAG) or Warning (close to threshold)
)
```

#### MissingAltTextWarning (Value Object)
```scala
case class MissingAltTextWarning(
  slideId: SlideId,
  imageUrl: String
)
```

#### KeyboardAccessibilityWarning (Value Object)
```scala
case class KeyboardAccessibilityWarning(
  element: String, // "slide navigation", "speaker view"
  issue: String // Description of keyboard access issue
)
```

#### SemanticWarning (Value Object)
```scala
case class SemanticWarning(
  issue: String, // "Skipped heading level", "Missing ARIA landmark"
  recommendation: String
)
```

### Enhancements to Existing Aggregates

#### Theme (domain/Theme.scala)
- No changes needed - colors already available
- May add helper methods:
  ```scala
  def allColorPairs: List[(String, String)] =
    List(
      (colors.text, background.color),
      (colors.heading, background.color),
      (colors.link, background.color),
      (colors.accent, background.color)
    )
  ```

---

## Read Models

### AccessibilityValidator (infrastructure)
- **Responsibility**: Perform all accessibility checks
- **Methods**:
  - `validateContrast(theme: Theme): List[ContrastViolation]`
  - `validateAltText(slides: List[Slide]): List[MissingAltTextWarning]`
  - `auditKeyboardNav(html: String): List[KeyboardAccessibilityWarning]`
  - `checkSemanticHTML(html: String): List[SemanticWarning]`
  - `generateReport(deck: SlideDeck, theme: Theme): AccessibilityReport`

### ContrastCalculator (infrastructure utility)
- **Responsibility**: Calculate WCAG 2.1 contrast ratios
- **Methods**:
  - `relativeLuminance(hexColor: String): Double`
  - `contrastRatio(color1: String, color2: String): Double`
  - `meetsWCAG_AA(ratio: Double, isLargeText: Boolean): Boolean`

---

## Policies / Business Rules

### New Policies

#### POL-009: Accessibility Validation Policy
- Accessibility validation runs automatically before rendering
- Validation is **non-blocking** (warnings only, never fails build)
- Flag `--skip-accessibility` bypasses all checks
- Flag `--accessibility-report output.json` saves JSON report

#### WCAG 2.1 AA Standards
1. **Contrast Ratios**:
   - Normal text (< 18pt): Minimum 4.5:1
   - Large text (≥ 18pt or ≥ 14pt bold): Minimum 3:1
   - UI components and graphics: Minimum 3:1

2. **Alt Text**:
   - Every `<img>` must have non-empty `alt` attribute
   - Decorative images can have `alt=""` (but MDSlides requires alt text via domain validation)

3. **Keyboard Navigation**:
   - All interactive elements accessible via keyboard
   - No keyboard traps
   - MDSlides already compliant (arrow keys, space, home, end, 's' for speaker view)

4. **Semantic HTML**:
   - Proper heading hierarchy (h1 → h2, no skipping levels)
   - Use semantic tags (`<nav>`, `<main>`, `<section>`)
   - ARIA roles and labels where appropriate

### Warning Levels
- **ERROR**: WCAG 2.1 AA violation (contrast < 4.5:1 for normal text)
- **WARNING**: Close to threshold (contrast 4.5-5.0:1), potential issue
- **INFO**: Best practice suggestion (e.g., add ARIA landmark)

---

## Open Questions

### Q1: Should accessibility validation be mandatory or optional?
**Answer**: Optional by default, but strongly recommended. Use `--skip-accessibility` to disable.

### Q2: Should failing accessibility block rendering?
**Answer**: No. MDSlides philosophy: warnings, not errors. Authors may have valid reasons (e.g., brand colors that slightly miss contrast ratio).

### Q3: Which WCAG level to target (A, AA, AAA)?
**Answer**: WCAG 2.1 AA (industry standard). AAA is too strict (7:1 contrast), rarely achieved.

### Q4: Should we validate rendered HTML or domain model?
**Answer**: Both:
- Domain model: Alt text, color definitions
- Rendered HTML: Semantic structure, ARIA attributes

### Q5: Should we add ARIA attributes automatically?
**Answer**: Yes, enhance HTMLRenderer to add:
- `role="main"` on slide container
- `role="navigation"` on slide counter
- `aria-label` on interactive elements

---

## Implementation Strategy

### Phase 1: Contrast Validation (Domain + Infrastructure)
1. Create `AccessibilityReport` and related value objects in domain
2. Implement `ContrastCalculator` utility (WCAG 2.1 formula)
3. Implement `AccessibilityValidator.validateContrast()`
4. Add tests for contrast calculations (property-based testing)

### Phase 2: Alt Text Validation (Infrastructure)
1. Implement `AccessibilityValidator.validateAltText()`
2. Extract all `ContentImage` from slides
3. Check `altText.nonEmpty` (already enforced by domain, but revalidate)
4. Add tests for alt text validation

### Phase 3: Keyboard & Semantic Validation (Infrastructure)
1. Implement `AccessibilityValidator.auditKeyboardNav()`
   - Check navigation.js for keyboard handlers (already compliant)
2. Implement `AccessibilityValidator.checkSemanticHTML()`
   - Parse generated HTML, check heading hierarchy
3. Add tests for semantic checks

### Phase 4: CLI Integration
1. Add `--skip-accessibility` flag to CLI
2. Add `--accessibility-report output.json` flag
3. Run `AccessibilityValidator.generateReport()` after parsing
4. Display warnings in CLI output (colored: red for ERROR, yellow for WARNING)

### Phase 5: HTML Enhancements (ARIA)
1. Add ARIA roles to `HTMLRenderer`:
   - `role="main"` on `.slides` container
   - `role="navigation"` on `.controls`
   - `aria-label="Slide X of Y"` on slide counter
2. Add `lang` attribute to `<html>` tag (default: "en")
3. Add tests for ARIA attribute presence

### Phase 6: Documentation & Theme Fixes
1. Audit all 4 built-in themes for WCAG 2.1 AA compliance
2. Fix any contrast ratio violations
3. Document accessibility best practices in README
4. Add accessibility examples to tutorial

---

## Non-Goals (Deferred)

1. **Automated color adjustments**: Don't auto-fix low contrast (just warn)
2. **Screen reader testing**: Manual testing only (not automated)
3. **ARIA-live regions**: Not needed for static slides
4. **Focus management**: Already handled by browser for keyboard nav

---

## Success Metrics

1. All 4 built-in themes pass WCAG 2.1 AA contrast checks
2. Accessibility report generated for every build
3. Clear, actionable warnings in CLI output
4. Zero false positives (warnings are real issues)
5. Documented accessibility guide for theme authors

---

## Related Governance

- **PDR-005**: Accessibility Requirements (existing - update with WCAG 2.1 AA specifics)
- **POL-009**: Accessibility Validation Policy (new)
- **US-005**: Image Embedding (alt text already validated in domain)

---

**Next Steps:**
1. Three Amigos session to validate acceptance criteria
2. Example Mapping to identify test scenarios
3. TDD implementation (domain value objects → infrastructure validation → CLI integration)
