# Example Mapping: Header/Footer Enhancements

**Date**: 2025-12-29
**Feature**: Header/Footer Template Configuration (v3.0.0 - Feature 7 of 10)
**Participants**: Product Owner, Bench Developer, Architect
**Story**: As a presentation author, I want to configure header and footer elements with placeholders (timer, page numbers, title) so I can customize the presentation chrome and display runtime information.

---

## Business Rules (Yellow Cards)

### Rule 1: Footer Configuration (YAML Template)
**Statement**: Footer defined in template YAML with up to 3 positions: bottom-left, bottom-center, bottom-right. Each position has content and optional CSS class.

**Examples** (Green Cards ✅):
- **Ex 1.1**: Define bottom-left with timer → `footer.bottom-left.content: "{{timer}}"`
- **Ex 1.2**: Define bottom-center with text → `footer.bottom-center.content: "Confidential"`
- **Ex 1.3**: Define bottom-right with page numbers → `footer.bottom-right.content: "{{pageNumber}} / {{totalPages}}"`
- **Ex 1.4**: Define all three positions → All three render in footer row

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 1.5**: Define fourth position (bottom-middle) → Wrong, only 3 positions supported
- ❌ **Ex 1.6**: Footer without any positions → Empty footer, no error

**BDD Traceability**:
- `header-footer.feature`: "Footer with timer, custom text, and page numbers"
- `header-footer.feature`: "Footer with three elements (left, center, right)"

**YAML Structure**:
```yaml
footer:
  bottom-left:
    content: "{{timer}}"
    class: "footer-timer"
  bottom-center:
    content: "Internal Only"
  bottom-right:
    content: "{{pageNumber}} / {{totalPages}}"
```

---

### Rule 2: Placeholder Resolution (Dynamic vs. Static)
**Statement**: Placeholders resolved at render time (static) or runtime (dynamic). Timer placeholder is dynamic (updates every second), others are static.

**Examples** (Green Cards ✅):
- **Ex 2.1**: `{{pageNumber}}` resolved to "5" on slide 5 (static, set at render)
- **Ex 2.2**: `{{totalPages}}` resolved to "42" for 42-slide deck (static)
- **Ex 2.3**: `{{timer}}` shows "00:05:30" and updates to "00:05:31" (dynamic, updates at runtime)
- **Ex 2.4**: `{{title}}` resolved to "MDSlides v3.0.0" from deck metadata (static)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 2.5**: `{{pageNumber}}` updates dynamically on navigation → Wrong, static (data attribute)
- ❌ **Ex 2.6**: `{{timer}}` set once at render → Wrong, updates every second

**BDD Traceability**:
- `header-footer.feature`: "Placeholder resolution (static vs dynamic)"
- `header-footer.feature`: "Timer placeholder updates every second"

**Resolution Strategy**:
- Static: `{{pageNumber}}`, `{{totalPages}}`, `{{title}}` → Replaced at render time with data attributes
- Dynamic: `{{timer}}` → JavaScript reads data attribute, updates DOM every second

---

### Rule 3: Timer Placeholder Format
**Statement**: Timer placeholder displays elapsed time in hh:mm:ss format. Syncs with PresentationTimer state (pauses during break/goto).

**Examples** (Green Cards ✅):
- **Ex 3.1**: Timer shows "00:00:00" at session start
- **Ex 3.2**: After 5 minutes 30 seconds → "00:05:30"
- **Ex 3.3**: After 1 hour 2 minutes 5 seconds → "01:02:05"
- **Ex 3.4**: Break mode active → Timer paused, displays "00:05:30" (not incrementing)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 3.5**: Timer shows "5:30" (no leading zeros) → Wrong, must be hh:mm:ss
- ❌ **Ex 3.6**: Timer continues during break → Wrong, must pause with PresentationTimer

**BDD Traceability**:
- `header-footer.feature`: "Timer format (hh:mm:ss)"
- `header-footer.feature`: "Timer pauses during break mode"

**Integration**: Timer footer reads PresentationTimer.elapsedSeconds(), formats as hh:mm:ss.

---

### Rule 4: Page Number Placeholder (1-Indexed)
**Statement**: `{{pageNumber}}` is 1-indexed (user-facing). Slide 0 → "1", Slide 41 → "42". Updates on navigation via data attribute.

**Examples** (Green Cards ✅):
- **Ex 4.1**: On slide 0 → pageNumber displays "1"
- **Ex 4.2**: On slide 5 → pageNumber displays "6"
- **Ex 4.3**: On slide 41 (last of 42) → pageNumber displays "42"
- **Ex 4.4**: Navigate from slide 5 to 10 → pageNumber updates from "6" to "11"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 4.5**: On slide 0, pageNumber displays "0" → Wrong, 1-indexed
- ❌ **Ex 4.6**: pageNumber does not update on navigation → Wrong, must update

**BDD Traceability**:
- `header-footer.feature`: "Page number placeholder (1-indexed)"
- `header-footer.feature`: "Page number updates on navigation"

**Implementation**: Slide has `data-slide-index="5"`, footer reads attribute, displays index + 1.

---

### Rule 5: Total Pages Placeholder (Static)
**Statement**: `{{totalPages}}` is total slide count (static, does not change). Same on all slides.

**Examples** (Green Cards ✅):
- **Ex 5.1**: 42 slides → totalPages displays "42" on all slides
- **Ex 5.2**: 100 slides → totalPages displays "100" on all slides
- **Ex 5.3**: 1 slide → totalPages displays "1"

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 5.4**: totalPages changes on navigation → Wrong, static value
- ❌ **Ex 5.5**: totalPages is 0-indexed → Wrong, 1-indexed (1 to N)

**BDD Traceability**:
- `header-footer.feature`: "Total pages placeholder"

**Source**: Derived from slide count at render time.

---

### Rule 6: Title Placeholder (Deck Metadata)
**Statement**: `{{title}}` resolved from deck frontmatter or filename. If no title in frontmatter, use deck filename.

**Examples** (Green Cards ✅):
- **Ex 6.1**: Frontmatter `title: "MDSlides v3.0.0"` → title displays "MDSlides v3.0.0"
- **Ex 6.2**: No frontmatter title, filename "my-talk.md" → title displays "my-talk"
- **Ex 6.3**: Frontmatter title with special chars "Q&A Session" → title displays "Q&A Session" (HTML-encoded)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 6.4**: title is empty string → Wrong, fallback to filename
- ❌ **Ex 6.5**: title changes on slide navigation → Wrong, static per deck

**BDD Traceability**:
- `header-footer.feature`: "Title placeholder from deck metadata"
- `header-footer.feature`: "Title fallback to filename"

**Fallback Chain**: Frontmatter title → filename (without .md extension)

---

### Rule 7: Custom CSS Classes
**Statement**: Footer elements support optional CSS class for styling. Class applied to element container.

**Examples** (Green Cards ✅):
- **Ex 7.1**: `class: "footer-timer"` → HTML: `<div class="footer-left footer-timer">00:05:30</div>`
- **Ex 7.2**: No class specified → HTML: `<div class="footer-left">Content</div>` (default class only)
- **Ex 7.3**: Multiple elements with different classes → Each has independent styling

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 7.4**: Class replaces default class → Wrong, custom class is additional
- ❌ **Ex 7.5**: Invalid CSS class (with spaces) → Validation error

**BDD Traceability**:
- `header-footer.feature`: "Custom CSS classes for footer elements"

**HTML Structure**: Default class + optional custom class.

---

### Rule 8: Footer Overlay Positioning (Fixed)
**Statement**: Footer positioned with `position: fixed` at bottom of viewport. Overlays slide content, does not push content up.

**Examples** (Green Cards ✅):
- **Ex 8.1**: Footer always visible at bottom, regardless of slide content
- **Ex 8.2**: Slide with tall content → Footer overlays bottom, content scrollable
- **Ex 8.3**: z-index: 50 → Footer above slide content (z-index: 1)

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 8.4**: Footer pushes slide content up → Wrong, fixed overlay
- ❌ **Ex 8.5**: Footer hidden on tall slides → Wrong, always visible

**BDD Traceability**:
- `header-footer.feature`: "Footer overlay positioning"

**CSS**:
```css
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 50;
}
```

---

### Rule 9: Template Defaults (Fallback to None)
**Statement**: If no footer defined in template, no footer rendered. Graceful omission, not error.

**Examples** (Green Cards ✅):
- **Ex 9.1**: Template without footer section → No footer rendered
- **Ex 9.2**: Footer section exists but all positions empty → Empty footer row (no error)
- **Ex 9.3**: Only bottom-left defined → Center and right empty, left renders

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 9.4**: Missing footer causes render error → Wrong, graceful omission
- ❌ **Ex 9.5**: Default footer rendered → Wrong, no defaults (explicit config only)

**BDD Traceability**:
- `header-footer.feature`: "Template without footer (no footer rendered)"

**Design Decision**: Explicit configuration, no built-in defaults.

---

### Rule 10: 4-Layer Config Precedence
**Statement**: Footer config resolved via CLI arg > project config > template default > none. CLI can override template footer.

**Examples** (Green Cards ✅):
- **Ex 10.1**: CLI `--footer-text "Draft"`, template has footer → CLI text overrides template
- **Ex 10.2**: Project config defines footer, no CLI arg → Project config used
- **Ex 10.3**: Template defines footer, no overrides → Template footer used
- **Ex 10.4**: No footer anywhere → No footer rendered

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 10.5**: Template footer cannot be overridden → Wrong, CLI/project override template
- ❌ **Ex 10.6**: CLI arg requires template footer → Wrong, CLI can add footer to templateless deck

**BDD Traceability**:
- `header-footer.feature`: "Footer config precedence"

**Precedence**: CLI arg > project .mdslides.json > template YAML > none

---

### Rule 11: Placeholder Validation at Render
**Statement**: Invalid placeholders (typos, unknown) cause render error with actionable message.

**Examples** (Green Cards ✅):
- **Ex 11.1**: `{{timr}}` (typo) → Error: "Unknown placeholder '{{timr}}'. Valid: {{timer}}, {{pageNumber}}, {{totalPages}}, {{title}}"
- **Ex 11.2**: `{{unknown}}` → Error: "Unknown placeholder '{{unknown}}'"
- **Ex 11.3**: Valid placeholders → No error, render succeeds

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 11.4**: Invalid placeholder silently ignored → Wrong, must error
- ❌ **Ex 11.5**: Invalid placeholder renders as literal "{{timr}}" → Wrong, must error

**BDD Traceability**:
- `header-footer.feature`: "Invalid placeholder causes render error"

**Valid Placeholders**: `{{timer}}`, `{{pageNumber}}`, `{{totalPages}}`, `{{title}}`

---

### Rule 12: Footer Element Width Distribution
**Statement**: Three footer positions use CSS Grid with `grid-template-columns: 1fr 1fr 1fr`. Equal width distribution.

**Examples** (Green Cards ✅):
- **Ex 12.1**: All three positions defined → Each occupies 33.33% width
- **Ex 12.2**: Only bottom-left defined → Occupies left third, center and right empty
- **Ex 12.3**: Only bottom-center defined → Occupies center third, left and right empty

**Counter-Examples** (Red Cards ❌):
- ❌ **Ex 12.4**: bottom-left expands to fill entire width → Wrong, fixed 1/3 width
- ❌ **Ex 12.5**: Unequal width distribution → Wrong, equal 1fr for each position

**BDD Traceability**:
- `header-footer.feature`: "Footer grid layout (three columns)"

**CSS Grid**:
```css
.footer {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
}
```

---

## Questions (Pink Cards)

### Q1: Should we support header configuration?
**Status**: DEFERRED to v3.1.0
**Decision**: Footer only for v3.0.0
**Rationale**: Footer is more common use case. Header adds complexity (conflicts with slide titles).

### Q2: Should placeholders support custom formatting?
**Status**: DEFERRED to v3.1.0
**Decision**: Fixed formats for v3.0.0 (hh:mm:ss for timer, "N / M" for pages)
**Rationale**: Simple implementation, covers 90% of use cases.
**Future**: Custom formats like "Page N" or "mm:ss" in v3.1.0.

### Q3: Should footer support more than 3 positions?
**Status**: RESOLVED - No
**Decision**: Exactly 3 positions (left, center, right)
**Rationale**: Standard layout, covers most use cases. More positions complicate grid.

### Q4: Should timer placeholder support countdown mode?
**Status**: DEFERRED to v3.1.0
**Decision**: Elapsed time only for v3.0.0
**Rationale**: Countdown requires target duration config. Elapsed is simpler.

### Q5: Should footer be hideable during presentation?
**Status**: DEFERRED to v3.1.0
**Decision**: Always visible if configured
**Rationale**: No hide/show toggle in v3.0.0. Future: H key to toggle footer visibility.

### Q6: Should page numbers support "Slide N" format?
**Status**: DEFERRED to v3.1.0
**Decision**: Numbers only ("5", "5 / 42")
**Rationale**: Users can add text in config: `"Slide {{pageNumber}}"`.

---

## Design Decisions from Event Storming

1. **Footer Positions**:
   - **Decision**: Three positions (bottom-left, bottom-center, bottom-right)
   - **Rationale**: Standard layout, aligns with CSS Grid simplicity

2. **Dynamic Timer Updates**:
   - **Decision**: JavaScript updates timer every second via PresentationTimer API
   - **Rationale**: Timer must reflect pauses (break mode, goto popup)

3. **Static Placeholders (Data Attributes)**:
   - **Decision**: pageNumber, totalPages resolved via `data-slide-index` attribute
   - **Rationale**: Navigation updates attribute, footer reads reactively

4. **1-Indexed Page Numbers**:
   - **Decision**: User-facing page numbers start at 1 (not 0)
   - **Rationale**: Consistent with user expectations and goto popup

5. **No Default Footer**:
   - **Decision**: Footer only rendered if explicitly configured
   - **Rationale**: Clean slides by default, footer is opt-in

6. **Fixed Overlay Positioning**:
   - **Decision**: Footer overlays slide content (position: fixed)
   - **Rationale**: Always visible, does not affect slide layout

7. **Placeholder Validation**:
   - **Decision**: Unknown placeholders cause render error (fail fast)
   - **Rationale**: Prevents typos, ensures correct config

---

## Traceability Matrix

| Rule | BDD Scenarios | Event Storming Events | Acceptance Criteria |
|------|---------------|----------------------|---------------------|
| Rule 1 | header-footer.feature (4) | FooterConfigResolved | AC-1 |
| Rule 2 | header-footer.feature (4) | PlaceholderResolved | AC-2, AC-3 |
| Rule 3 | header-footer.feature (3) | TimerPlaceholderUpdated | AC-4 |
| Rule 4 | header-footer.feature (4) | PageNumberPlaceholderResolved | AC-5 |
| Rule 5 | header-footer.feature (2) | TotalPagesPlaceholderResolved | AC-6 |
| Rule 6 | header-footer.feature (3) | TitlePlaceholderResolved | AC-7 |
| Rule 7 | header-footer.feature (2) | FooterElementRendered | AC-8 |
| Rule 8 | header-footer.feature (2) | FooterRendered | AC-9 |
| Rule 9 | header-footer.feature (2) | FooterConfigResolved | AC-10 |
| Rule 10 | header-footer.feature (3) | FooterConfigResolved | AC-11 |
| Rule 11 | header-footer.feature (2) | PlaceholderValidated | AC-12 |
| Rule 12 | header-footer.feature (2) | FooterRendered | AC-13 |

**Total Coverage**: 33 BDD scenarios across 12 business rules

---

## Implementation Notes

### Domain Model Requirements
- `FooterConfig` value object with positions (left, center, right), each with content and class
- `ResolvePlaceholder` command validates and resolves placeholders
- `ValidatePlaceholder` command checks against allowed set
- `FooterConfigError` enum for validation errors

### Infrastructure Requirements
- YAML parser for footer configuration
- Placeholder resolver (static at render, dynamic at runtime)
- Timer integration (read PresentationTimer.elapsedSeconds() every second)
- Data attribute management (set slide index on slide element)
- CSS Grid layout for three-column footer

### UI Requirements
- Footer HTML structure:
  ```html
  <div class="footer">
    <div class="footer-left footer-timer" data-placeholder="timer">00:00:00</div>
    <div class="footer-center">Confidential</div>
    <div class="footer-right" data-placeholder="pageNumber">5 / 42</div>
  </div>
  ```
- JavaScript timer updater (setInterval 1 second)
- CSS: position fixed, bottom 0, z-index 50

---

## Footer Configuration Examples

### Example 1: Timer + Page Numbers
```yaml
template:
  footer:
    bottom-left:
      content: "{{timer}}"
    bottom-right:
      content: "{{pageNumber}} / {{totalPages}}"
```

### Example 2: Custom Text + Title
```yaml
template:
  footer:
    bottom-left:
      content: "© 2025 TJM Solutions"
    bottom-center:
      content: "{{title}}"
    bottom-right:
      content: "Confidential"
```

### Example 3: Timer Only with Custom Class
```yaml
template:
  footer:
    bottom-center:
      content: "{{timer}}"
      class: "timer-large"
```

---

## Placeholder Resolution Table

| Placeholder | Type | Resolved At | Example Value | Updates |
|-------------|------|-------------|---------------|---------|
| `{{timer}}` | Dynamic | Runtime | "00:05:30" | Every second |
| `{{pageNumber}}` | Static | Render | "5" | On navigation (data attr) |
| `{{totalPages}}` | Static | Render | "42" | Never (constant) |
| `{{title}}` | Static | Render | "MDSlides v3.0.0" | Never (constant) |

---

**Example Mapping Complete**: 2025-12-29
**Next Step**: Acceptance Criteria Review
**Ready for Implementation**: Pending AC approval
