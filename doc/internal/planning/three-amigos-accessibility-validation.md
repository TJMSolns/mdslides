# Three Amigos: Accessibility Validation (US-014)

**Feature**: WCAG 2.1 AA Compliance Validation
**Priority**: SHOULD HAVE (v1.4.0)
**Date**: 2025-12-27
**Participants**: Product Owner, Developer, QA

---

## Event Storming Reference

This session validates the analysis from [Event Storming: Accessibility Validation](event-storming-accessibility-validation.md).

---

## User Story Review

**US-014: Accessibility Validation**

> **As a** presentation author
> **I want** automatic accessibility validation
> **So that** my presentations meet WCAG 2.1 AA standards and are accessible to all users

**Acceptance Criteria:**
1. Color contrast ratios checked against WCAG 2.1 AA (4.5:1 for normal text, 3:1 for large text)
2. All images validated for non-empty alt text
3. Keyboard navigation audited (all features accessible via keyboard)
4. Semantic HTML checked (proper heading hierarchy, ARIA roles)
5. Accessibility report displayed in CLI with warnings (non-blocking)
6. Flag `--skip-accessibility` to bypass validation
7. Flag `--accessibility-report output.json` to save JSON report
8. All 4 built-in themes pass WCAG 2.1 AA

---

## Questions & Clarifications

### Q1: What happens if a theme fails contrast validation?

**Developer**: "Should we block rendering if contrast ratio is too low?"

**Product Owner**: "No. Display a WARNING but still generate HTML. Authors may have brand requirements that override accessibility."

**QA**: "Should we suggest alternative colors that would pass?"

**Product Owner**: "Not for v1.4.0. Just warn with actual ratio and required ratio. Color suggestions are v2.0+ feature."

**Decision**: Non-blocking warnings with contrast ratio details. No automatic fixes.

---

### Q2: How do we calculate contrast ratios?

**Developer**: "WCAG 2.1 has specific formula using relative luminance. Should we implement from scratch or use library?"

**Product Owner**: "Implement from scratch. It's just math, and we avoid external dependency."

**QA**: "How do we test it's correct?"

**Developer**: "Property-based testing + reference examples from WCAG docs."

**Decision**: Implement WCAG 2.1 contrast formula in Scala. No external libraries.

---

###  Q3: What about images without alt text?

**QA**: "Domain already validates alt text is non-empty (PDR-008). Why revalidate?"

**Developer**: "Good point. We could skip this check since domain enforces it."

**Product Owner**: "Still include it. Accessibility report should be comprehensive. Plus, it catches if validation was bypassed."

**Decision**: Include alt text validation even though domain already enforces it. Redundant but comprehensive.

---

### Q4: How granular should contrast checks be?

**Developer**: "Do we check every text/background pair, or just theme defaults?"

**Product Owner**: "Check theme defaults: text on background, heading on background, link on background, accent on background. Don't check syntax highlighting colors (those are from highlight.js)."

**QA**: "What about slide-specific backgrounds (background images)?"

**Product Owner**: "Can't check those automatically (images). Just check solid color backgrounds from theme."

**Decision**: Check theme color pairs only. Skip background images and syntax highlighting.

---

### Q5: What about keyboard navigation audit?

**Developer**: "MDSlides already has full keyboard nav (arrows, space, home, end, 's'). Do we just verify it's present?"

**Product Owner**: "Yes. Parse generated HTML, confirm keyboard event listeners exist. Should always pass (just a sanity check)."

**QA**: "Should we test actual keyboard functionality in browser?"

**Product Owner**: "Manual testing only. Automated browser testing (Selenium) is overkill for v1.4.0."

**Decision**: Check keyboard handlers are present in HTML. Manual browser testing for verification.

---

### Q6: Semantic HTML - what do we check?

**Developer**: "WCAG recommends proper heading hierarchy (h1 → h2, no skipping), landmarks, ARIA roles. How strict?"

**Product Owner**: "Check heading hierarchy. MDSlides uses h1 for title slides, h2 for content headings. Should be compliant already."

**QA**: "What about ARIA roles?"

**Developer**: "We could add `role='main'` on slides container, `role='navigation'` on controls. Easy enhancement."

**Product Owner**: "Yes, add those. Check for their presence in validation."

**Decision**:
- Check heading hierarchy (h1 → h2 only, no h3+ in MDSlides)
- Add ARIA roles to HTML renderer
- Validate ARIA roles are present

---

### Q7: CLI output format for warnings?

**QA**: "How should warnings look in CLI?"

**Product Owner**: "Color-coded:
- 🔴 RED for ERROR (fails WCAG AA)
- 🟡 YELLOW for WARNING (close to threshold or best practice)
- 🟢 GREEN for PASS (summary at end)"

**Developer**: "Should we show all warnings or limit to first N?"

**Product Owner**: "Show all. If there are 20 contrast violations, author needs to know."

**Decision**: Color-coded CLI output, show all warnings, summary at end.

---

### Q8: JSON report format?

**Developer**: "What should JSON structure look like?"

**Product Owner**: "Standard structure:
```json
{
  \"summary\": {
    \"errors\": 2,
    \"warnings\": 5,
    \"passes\": 10
  },
  \"contrastViolations\": [
    {
      \"colorPair\": [\"#333333\", \"#555555\"],
      \"ratio\": 2.1,
      \"required\": 4.5,
      \"location\": \"body text\",
      \"severity\": \"error\"
    }
  ],
  \"missingAltText\": [],
  \"keyboardWarnings\": [],
  \"semanticWarnings\": []
}
```"

**Decision**: JSON report with summary + detailed lists.

---

### Q9: Should we fix existing themes if they fail?

**QA**: "What if light/dark/corporate/retisio fail contrast checks?"

**Product Owner**: "We MUST fix them. Built-in themes should be exemplary."

**Developer**: "I'll audit all 4 themes during implementation."

**Decision**: All built-in themes must pass WCAG 2.1 AA. Fix any violations before v1.4.0 release.

---

### Q10: Performance impact?

**Developer**: "Contrast calculations are O(n) where n = color pairs (4-6 typically). Alt text check is O(m) where m = images per slide. Negligible."

**QA**: "Should we measure and warn if validation takes >100ms?"

**Product Owner**: "No need. It'll be instant."

**Decision**: No performance concerns. Don't optimize prematurely.

---

## Edge Cases Identified

### Edge Case 1: Theme with no background color

**Scenario**: Custom theme missing `background.color` field.

**Expected**: Validation skips contrast checks (can't check against undefined background).

**Warning**: "Contrast validation skipped: background color not defined."

**Test**: Verify graceful handling of missing theme fields.

---

### Edge Case 2: Transparent background

**Scenario**: Theme has `background.color: "transparent"`.

**Expected**: Can't calculate contrast. Skip with warning.

**Warning**: "Contrast validation skipped: transparent background."

**Test**: Verify transparent background handled.

---

### Edge Case 3: Invalid hex color

**Scenario**: Theme has malformed color like `"#GGG"`.

**Expected**: Validation fails gracefully, warns about invalid color.

**Warning**: "Invalid color '#GGG' in theme. Contrast check skipped."

**Test**: Verify error handling for invalid hex codes.

---

### Edge Case 4: Deck with no images

**Scenario**: Presentation has zero images.

**Expected**: Alt text validation passes (0 violations).

**Output**: "✓ All images have alt text (0 images checked)."

**Test**: Verify empty list handled correctly.

---

### Edge Case 5: Deck with 100+ images

**Scenario**: Presentation has many images, all with alt text.

**Expected**: Validation completes quickly, reports "✓ All images have alt text (100 images checked)."

**Test**: Verify performance with large image count.

---

### Edge Case 6: --skip-accessibility flag

**Scenario**: User runs `mdslides render deck --skip-accessibility`.

**Expected**: No accessibility checks run, no warnings shown.

**Output**: Normal rendering output without accessibility section.

**Test**: Verify flag bypasses all validation.

---

### Edge Case 7: --accessibility-report without checks

**Scenario**: User runs `mdslides render deck --skip-accessibility --accessibility-report out.json`.

**Expected**: Error - can't save report if validation skipped.

**Output**: "Error: --accessibility-report requires accessibility validation. Remove --skip-accessibility."

**Test**: Verify conflicting flags caught.

---

### Edge Case 8: Permission denied when saving JSON

**Scenario**: User specifies `--accessibility-report /root/report.json` without permission.

**Expected**: Accessibility checks run, but JSON save fails gracefully.

**Output**: Warnings displayed in CLI, error message about JSON save failure.

**Test**: Verify file write errors don't crash build.

---

## Concrete Examples (for Example Mapping)

### Example 1: Light theme passes contrast

**Given**: Light theme with:
- Text: `#212121` (dark gray)
- Background: `#FFFFFF` (white)

**When**: Contrast validation runs

**Then**:
- Contrast ratio calculated: 16.1:1
- Required: 4.5:1
- Result: ✓ PASS
- Output: "✓ Text contrast: 16.1:1 (required: 4.5:1)"

**Test Layer**: Infrastructure (ContrastCalculator)
**Test Type**: Unit test with known color pairs

---

### Example 2: Dark theme fails contrast

**Given**: Dark theme with:
- Text: `#888888` (medium gray)
- Background: `#333333` (dark gray)

**When**: Contrast validation runs

**Then**:
- Contrast ratio: 2.8:1
- Required: 4.5:1
- Result: ✗ ERROR
- Output: "✗ ERROR: Text contrast too low: 2.8:1 (required: 4.5:1)"

**Test Layer**: Infrastructure (AccessibilityValidator)
**Test Type**: Integration test

---

### Example 3: All images have alt text

**Given**: Deck with 3 images:
- `![Chart](chart.png)` - alt text: "Chart"
- `![Logo](logo.png)` - alt text: "Logo"
- `![Graph](graph.png)` - alt text: "Graph"

**When**: Alt text validation runs

**Then**:
- 3 images checked
- 0 violations
- Output: "✓ All images have alt text (3 images checked)"

**Test Layer**: Infrastructure (AccessibilityValidator)
**Test Type**: Unit test

---

### Example 4: Missing alt text (edge case - domain should prevent)

**Given**: Deck constructed manually (bypassing domain validation) with empty alt text

**When**: Alt text validation runs

**Then**:
- 1 violation found
- Output: "✗ WARNING: Image on slide 2 missing alt text: images/chart.png"

**Test Layer**: Infrastructure (AccessibilityValidator)
**Test Type**: Edge case test

---

### Example 5: Keyboard navigation present

**Given**: Rendered HTML with navigation.js

**When**: Keyboard audit runs

**Then**:
- Checks for `addEventListener('keydown'`
- Checks for arrow key handlers
- Checks for 'S' key handler
- Result: ✓ PASS
- Output: "✓ Keyboard navigation accessible"

**Test Layer**: Infrastructure (AccessibilityValidator)
**Test Type**: Unit test (HTML parsing)

---

### Example 6: Proper heading hierarchy

**Given**: Rendered HTML with h1 (title) and h2 (content headings)

**When**: Semantic HTML check runs

**Then**:
- Verify no h3, h4, h5, h6 (MDSlides doesn't generate these)
- Verify h1 precedes h2
- Verify ARIA roles present
- Result: ✓ PASS
- Output: "✓ Semantic HTML structure correct"

**Test Layer**: Infrastructure (AccessibilityValidator)
**Test Type**: Unit test (HTML parsing)

---

### Example 7: Full report with mixed results

**Given**: Deck with:
- 1 contrast violation
- 0 alt text issues
- Keyboard nav pass
- Semantic HTML pass

**When**: Accessibility report generated

**Then**: CLI output:
```
Accessibility Validation (WCAG 2.1 AA):
✗ ERROR: Link contrast too low: 3.2:1 (required: 4.5:1)
✓ All images have alt text (2 images checked)
✓ Keyboard navigation accessible
✓ Semantic HTML structure correct

Summary: 1 error, 0 warnings, 3 passes
```

**Test Layer**: Integration (full validation flow)
**Test Type**: End-to-end test

---

### Example 8: --skip-accessibility flag

**Given**: Command `mdslides render deck --skip-accessibility`

**When**: Rendering executes

**Then**:
- No accessibility checks run
- No warnings displayed
- Normal HTML output
- Faster build (skip validation overhead)

**Test Layer**: CLI
**Test Type**: Integration test

---

### Example 9: JSON report saved

**Given**: Command `mdslides render deck --accessibility-report report.json`

**When**: Rendering completes

**Then**:
- Accessibility checks run
- Warnings displayed in CLI
- JSON file written to `report.json`
- JSON contains full report structure

**Test Layer**: CLI + Infrastructure
**Test Type**: Integration test (file I/O)

---

## Acceptance Test Checklist

### Functional Tests
- [ ] Contrast ratio correctly calculated for valid hex colors
- [ ] Contrast violations detected when ratio < 4.5:1
- [ ] Large text threshold (3:1) correctly applied
- [ ] Alt text validation checks all images
- [ ] Keyboard navigation audit verifies handlers present
- [ ] Semantic HTML check verifies heading hierarchy
- [ ] Accessibility report aggregates all results

### Integration Tests
- [ ] Full validation runs after parsing, before rendering
- [ ] CLI displays color-coded warnings (red/yellow/green)
- [ ] `--skip-accessibility` bypasses all checks
- [ ] `--accessibility-report` saves JSON file
- [ ] Conflicting flags detected and reported
- [ ] File write errors handled gracefully

### Theme Tests
- [ ] Light theme passes WCAG 2.1 AA
- [ ] Dark theme passes WCAG 2.1 AA
- [ ] Corporate theme passes WCAG 2.1 AA
- [ ] Retisio theme passes WCAG 2.1 AA

### Edge Cases
- [ ] Transparent background handled gracefully
- [ ] Invalid hex color handled gracefully
- [ ] Empty image list handled correctly
- [ ] Large image count (100+) performs well
- [ ] Permission denied on JSON save handled

### Documentation
- [ ] README updated with accessibility section
- [ ] Tutorial demonstrates accessibility best practices
- [ ] CHANGELOG includes v1.4.0 accessibility features

---

## Open Questions for Example Mapping

1. Should we validate code block contrast (syntax highlighting colors)?
   - Recommendation: No. Highlight.js themes are pre-vetted. Skip for v1.4.0.

2. Should we check focus indicators for keyboard nav?
   - Recommendation: Yes, but as INFO-level suggestion, not WARNING.

3. Should we recommend ARIA live regions for speaker notes?
   - Recommendation: No. Static slides don't need live regions.

---

## Implementation Scope

### In Scope (v1.4.0)
- WCAG 2.1 AA contrast validation (4.5:1 / 3:1)
- Alt text validation (comprehensive check)
- Keyboard navigation audit (verify handlers)
- Semantic HTML checks (heading hierarchy, ARIA roles)
- CLI integration (`--skip-accessibility`, `--accessibility-report`)
- Fix all built-in themes to pass WCAG 2.1 AA
- Add ARIA roles to HTMLRenderer

### Out of Scope (Defer to v2.0+)
- Automated color correction (suggest alternative colors)
- Screen reader testing (manual only)
- AAA level compliance (7:1 contrast)
- Focus management enhancements
- Internationalization (lang attribute support)

---

## Risks & Mitigations

**Risk 1**: Built-in themes fail WCAG AA
- **Mitigation**: Audit themes early, fix colors before implementation complete

**Risk 2**: Contrast calculation bugs
- **Mitigation**: Property-based testing + reference examples from WCAG docs

**Risk 3**: False positives (warnings for valid cases)
- **Mitigation**: Manual testing with diverse presentations, tune thresholds

**Risk 4**: Performance impact on large decks
- **Mitigation**: Benchmark with 100-slide deck, optimize if >100ms overhead

---

## Next Steps

1. Proceed to Example Mapping session to formalize test scenarios
2. Begin TDD implementation:
   - RED: Write test for contrast calculation
   - GREEN: Implement WCAG 2.1 contrast formula
   - REFACTOR: Extract ContrastCalculator utility
3. Implement domain value objects (AccessibilityReport, ContrastViolation, etc.)
4. Implement infrastructure validators
5. Integrate with CLI
6. Audit and fix themes
7. Update documentation

---

**Sign-off**: Ready for Example Mapping
