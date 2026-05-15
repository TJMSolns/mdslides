# Example Mapping: Accessibility Validation (US-014)

**Feature**: WCAG 2.1 AA Compliance Validation
**Date**: 2025-12-27
**Status**: Ready for Implementation

---

## Story

> **As a** presentation author
> **I want** automatic accessibility validation
> **So that** my presentations meet WCAG 2.1 AA standards and are accessible to all users

---

## Rules (from Three Amigos)

1. **Non-Blocking Validation**: Warnings displayed, but HTML always generated
2. **Contrast Formula**: Implement WCAG 2.1 formula (no external libraries)
3. **Theme-Level Checks**: Validate theme color pairs, skip background images
4. **Comprehensive Alt Text**: Validate even though domain enforces (redundant but complete)
5. **Keyboard Nav Audit**: Verify keyboard handlers exist in generated HTML
6. **CLI Flags**: `--skip-accessibility` and `--accessibility-report output.json`
7. **Built-in Themes**: All 4 themes must pass WCAG 2.1 AA

---

## Examples

### Rule 1: Contrast Ratio Calculation

**Scenario 1**: Calculate relative luminance for pure black
```
Given: Color #000000 (black)
When: Calculate relative luminance
Then: Luminance = 0.0
```

**Scenario 2**: Calculate relative luminance for pure white
```
Given: Color #FFFFFF (white)
When: Calculate relative luminance
Then: Luminance = 1.0
```

**Scenario 3**: Calculate contrast ratio for black text on white background
```
Given: Foreground #000000 (black), Background #FFFFFF (white)
When: Calculate contrast ratio
Then: Ratio = 21.0 (maximum possible)
```

**Scenario 4**: Calculate contrast ratio for gray on gray (fails WCAG)
```
Given: Foreground #888888 (gray), Background #AAAAAA (light gray)
When: Calculate contrast ratio
Then: Ratio ≈ 1.89 (< 4.5:1, FAILS normal text)
```

**Scenario 5**: Calculate contrast ratio for large text threshold
```
Given: Foreground #767676, Background #FFFFFF
When: Calculate contrast ratio
Then: Ratio ≈ 4.54 (> 3:1 for large text, PASSES; < 4.5:1 for normal text, FAILS)
```

---

### Rule 2: Theme Contrast Validation

**Scenario 6**: Light theme passes WCAG 2.1 AA
```
Given: Theme "light" with:
  - text: #333333, background: #FFFFFF
  - heading: #2C3E50, background: #FFFFFF
  - link: #3498DB, background: #FFFFFF
When: Validate theme contrast
Then: All ratios ≥ 4.5:1
And: No warnings displayed
```

**Scenario 7**: Dark theme passes WCAG 2.1 AA
```
Given: Theme "dark" with:
  - text: #E0E0E0, background: #1E1E1E
  - heading: #FFFFFF, background: #1E1E1E
  - link: #64B5F6, background: #1E1E1E
When: Validate theme contrast
Then: All ratios ≥ 4.5:1
And: No warnings displayed
```

**Scenario 8**: Custom theme fails contrast (low contrast warning)
```
Given: Custom theme with:
  - text: #888888, background: #AAAAAA
When: Validate theme contrast
Then: Ratio ≈ 1.89 (< 4.5:1)
And: Warning displayed: "Low contrast ratio for text: 1.89:1 (requires 4.5:1 for normal text)"
And: HTML still generated
```

**Scenario 9**: Theme with only heading/background checked (not syntax colors)
```
Given: Theme with:
  - heading: #2C3E50, background: #FFFFFF (PASS)
  - syntax.keyword: #0000FF, syntax.comment: #808080 (SKIP - not checked)
When: Validate theme contrast
Then: Only heading/background validated
And: Syntax highlighting colors not validated
```

---

### Rule 3: Alt Text Validation

**Scenario 10**: Image with valid alt text passes
```
Given: Slide with image ![Architecture Diagram](arch.svg)
When: Validate accessibility
Then: No alt text warnings
```

**Scenario 11**: Image with empty alt text fails (redundant check)
```
Given: Slide with image ![](empty-alt.png)
When: Validate accessibility
Then: Warning: "Image missing alt text: empty-alt.png"
Note: Domain validation should already prevent this
```

**Scenario 12**: Multiple images all have alt text
```
Given: Slide with 3 images, all with non-empty alt text
When: Validate accessibility
Then: No alt text warnings
```

---

### Rule 4: Keyboard Navigation Audit

**Scenario 13**: Generated HTML has all required keyboard handlers
```
Given: Generated HTML for any presentation
When: Audit keyboard navigation
Then: Keyboard handlers found for: ArrowRight, ArrowLeft, Space, Home, End, 's', 'S'
And: No warnings displayed
```

**Scenario 14**: Missing 'S' key handler (should never happen after v1.3.1)
```
Given: Generated HTML missing 'S' key handler
When: Audit keyboard navigation
Then: Warning: "Missing keyboard handler for speaker view ('s' key)"
```

---

### Rule 5: Accessibility Report Generation

**Scenario 15**: Skip accessibility validation
```
Given: Command "mdslides render --skip-accessibility deck.md output/"
When: Render presentation
Then: No accessibility checks performed
And: No warnings displayed
And: HTML generated normally
```

**Scenario 16**: Generate JSON accessibility report
```
Given: Command "mdslides render --accessibility-report report.json deck.md output/"
When: Render presentation
Then: JSON report written to report.json
And: Report contains: theme validation, alt text checks, keyboard audit
And: Report structure:
  {
    "timestamp": "2025-12-27T10:30:00Z",
    "deck": "deck.md",
    "theme": "light",
    "wcag_level": "AA",
    "checks": {
      "contrast": {
        "text_background": {"ratio": 21.0, "passes": true},
        "heading_background": {"ratio": 18.5, "passes": true},
        "link_background": {"ratio": 5.2, "passes": true}
      },
      "alt_text": {
        "images_checked": 3,
        "images_missing_alt": 0
      },
      "keyboard": {
        "handlers_found": ["ArrowRight", "ArrowLeft", "Space", "Home", "End", "s", "S"],
        "handlers_missing": []
      }
    },
    "warnings": [],
    "passes": true
  }
```

**Scenario 17**: Report includes warnings for failed checks
```
Given: Theme with low contrast (text: #888, bg: #AAA)
And: Command "mdslides render --accessibility-report report.json deck.md output/"
When: Render presentation
Then: report.json contains:
  "warnings": [
    {
      "level": "WARNING",
      "category": "contrast",
      "message": "Low contrast ratio for text: 1.89:1 (requires 4.5:1 for normal text)",
      "wcag_criterion": "1.4.3"
    }
  ]
And: "passes": false
```

---

### Rule 6: CLI Output Display

**Scenario 18**: Successful validation shows summary
```
Given: Presentation with theme "light" (all checks pass)
When: Render with default options
Then: CLI output includes:
  "✓ Accessibility validation passed (WCAG 2.1 AA)"
  "  ✓ Contrast ratios: 3/3 passed"
  "  ✓ Alt text: 2/2 images validated"
  "  ✓ Keyboard navigation: All handlers present"
```

**Scenario 19**: Failed validation shows warnings
```
Given: Presentation with low contrast theme
When: Render with default options
Then: CLI output includes:
  "⚠ Accessibility validation completed with warnings"
  "  ✗ Contrast ratios: 2/3 passed"
  "    WARNING: Low contrast ratio for text: 1.89:1 (requires 4.5:1)"
  "  ✓ Alt text: 0/0 images validated"
  "  ✓ Keyboard navigation: All handlers present"
  ""
  "Note: Presentation generated despite warnings. Use --skip-accessibility to suppress."
```

---

## Questions

**Q**: What if background image makes text unreadable?
**A**: Can't validate automatically (image content unknown). Manual review required.

**Q**: Should we check code block contrast (syntax highlighting)?
**A**: No - highlight.js themes are external. Authors choose appropriate highlight.js theme.

**Q**: What about heading hierarchy (h1 → h2 → h3 without skipping)?
**A**: MDSlides templates already enforce correct hierarchy. No additional check needed.

**Q**: Should validation run on every render or only on demand?
**A**: Default ON. Use `--skip-accessibility` to disable.

---

## Out of Scope (for v1.4.0)

- Suggesting alternative colors that would pass
- Interactive contrast checker UI
- PDF accessibility validation (only HTML)
- Screen reader testing automation
- ARIA landmark validation beyond basic structure
- Color blindness simulation
- Animation/motion reduction checks
- Focus indicator validation

---

## Test Scenarios Summary

| # | Scenario | Category | Expected Result |
|---|----------|----------|-----------------|
| 1 | Black luminance | Contrast Calc | 0.0 |
| 2 | White luminance | Contrast Calc | 1.0 |
| 3 | Black/white ratio | Contrast Calc | 21.0 |
| 4 | Gray/gray ratio (fail) | Contrast Calc | ~1.89 |
| 5 | Large text threshold | Contrast Calc | 4.54 (pass large, fail normal) |
| 6 | Light theme validation | Theme Check | All pass |
| 7 | Dark theme validation | Theme Check | All pass |
| 8 | Low contrast theme | Theme Check | Warning, HTML generated |
| 9 | Syntax colors skipped | Theme Check | Only theme colors checked |
| 10 | Valid alt text | Alt Text | Pass |
| 11 | Empty alt text | Alt Text | Warning |
| 12 | Multiple images | Alt Text | All validated |
| 13 | All keyboard handlers | Keyboard | Pass |
| 14 | Missing handler | Keyboard | Warning |
| 15 | Skip validation flag | CLI | No checks, no warnings |
| 16 | JSON report generation | CLI | report.json created |
| 17 | Report with warnings | CLI | warnings array populated |
| 18 | Success CLI output | CLI | Green checkmarks |
| 19 | Warning CLI output | CLI | Yellow warnings |

**Total**: 19 concrete test scenarios

---

## Acceptance Criteria Traceability

1. ✅ Color contrast checked (Scenarios 1-9)
2. ✅ Alt text validated (Scenarios 10-12)
3. ✅ Keyboard nav audited (Scenarios 13-14)
4. ✅ Semantic HTML (covered by existing templates)
5. ✅ CLI warnings displayed (Scenarios 18-19)
6. ✅ `--skip-accessibility` flag (Scenario 15)
7. ✅ `--accessibility-report` flag (Scenarios 16-17)
8. ✅ Built-in themes pass (Scenarios 6-7, plus tjm-solutions and retisio to validate)

---

## Implementation Notes

### Domain Layer
- New aggregate: `AccessibilityReport`
- Value objects: `ContrastRatio`, `WCAGLevel`, `AccessibilityWarning`
- Pure functions for contrast calculation (no I/O)

### Infrastructure Layer
- `AccessibilityValidator` service
- HTML parsing to extract colors and check keyboard handlers
- JSON report writer

### CLI Layer
- New flags: `--skip-accessibility`, `--accessibility-report PATH`
- Warning display formatting
- Exit code remains 0 (warnings don't fail build)

---

## Ready for TDD

This Example Mapping provides 19 concrete test scenarios to drive TDD implementation. Each scenario maps to specific test cases with clear inputs and expected outputs.

**Next Steps:**
1. Create domain tests for contrast calculation (RED phase)
2. Implement WCAG 2.1 contrast formula (GREEN phase)
3. Refactor for clarity (REFACTOR phase)
4. Repeat for each component (theme validator, alt text checker, keyboard auditor, report generator)
