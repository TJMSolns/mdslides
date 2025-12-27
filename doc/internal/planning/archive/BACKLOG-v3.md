# Product Backlog: MDSlides (Version-Bucketed)
## User Stories & Acceptance Criteria

---

```yaml
# MACHINE-READABLE METADATA
backlog:
  product: MDSlides
  version: 3.0.0
  last_updated: 2024-12-20
  owner: Tony Moores, TJM Solutions
  phase: Phase 2 Complete - All v1.0 MVP stories Ready for TDD

status_legend:
  - status: 📋 Ready
    description: Story refined, acceptance criteria defined, ready for implementation
  - status: 🏗️ In Progress
    description: Currently being implemented
  - status: ✅ Done
    description: Implemented, tested, and accepted
  - status: 📝 Draft
    description: Initial draft, needs refinement
  - status: 🔮 Future
    description: Identified but not yet refined

version_legend:
  - version: v1.0 (MVP)
    description: Core DSL + Validation + HTML Render + CLI
    goal: "Can author → parse → validate → render → output"
  - version: v1.1
    description: Usability + More Layouts + Accessibility
    goal: "Professional-quality presentations"
  - version: v2.0
    description: Document Structure + Automation (LaTeX-inspired)
    goal: "Enterprise-grade structured presentations"
  - version: v3.0
    description: Advanced Academic/Enterprise Features
    goal: "Feature parity with LaTeX Beamer for technical presentations"
```

---

# 🟦 v1.0 MVP - "Does It Work?"

**Release Goal**: A user can author a slide deck in Markdown → parse it → validate it → bind templates → render to HTML → run from CLI.

**Definition of Done for MVP**: Demonstrate the complete functional pipeline with minimal feature surface.

**Story Count**: 12 stories (P0 blockers)

---

## Epic 1: Core Slide Deck Creation

**Goal**: Implement the foundational pipeline for parsing Markdown into validated slide decks

---

### 📋 US-001: Create Title Slide from Markdown

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker)
**Estimated Effort**: 2-3 days

**User Story**:
> **As a** slide deck author
> **I want to** create a title slide from Markdown with explicit template binding
> **So that** I can generate a properly validated title slide with title, subtitle, and author

**Business Value**: Foundation for all slide deck creation - every presentation needs a title slide

**Acceptance Criteria**: [See three-amigos-session-001.md](doc/specifications/three-amigos-session-001.md)

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-001.md)
- [Example Mapping](doc/specifications/example-mapping-001.md)
- [Template Definition](templates/title.yaml)

---

### 📋 US-002: Create Content Slide from Markdown

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** create a standard content slide with heading and body
> **So that** I can present structured information on a slide

**Business Value**: Most common slide type - needed for 80% of presentation content

**Acceptance Criteria**: [See three-amigos-session-002.md](doc/specifications/three-amigos-session-002.md)

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-002.md)
- [Example Mapping](doc/specifications/example-mapping-002.md)
- [Template Definition](templates/content.yaml)

---

### 📋 US-003: Parse Multi-Slide Markdown File

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** create multiple slides in one Markdown file
> **So that** I can author complete presentations in a single document

**Business Value**: Foundational for all presentations - single-file authoring is the core workflow

**Acceptance Criteria**: [See three-amigos-session-003.md](doc/specifications/three-amigos-session-003.md)

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-003.md)
- [Example Mapping](doc/specifications/example-mapping-003.md)

---

### 📋 US-004: Speaker Notes in Markdown

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** add speaker notes to slides
> **So that** I can remember what to say during presentations

**Business Value**: Essential for presentation preparation - required by Charter Phase 1

**Acceptance Criteria**: [See three-amigos-session-004.md](doc/specifications/three-amigos-session-004.md)

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-004.md)
- [Example Mapping](doc/specifications/example-mapping-004.md)

**v1.0 Scope**: Parse and validate only (no rendering)
**v1.1 Scope**: Render in speaker view (see US-034)

---

## Epic 2: Validation Framework

**Goal**: Provide clear, actionable validation errors

---

### 📋 US-011: Structure Validation

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** receive validation errors for structural issues
> **So that** I know my slide deck has all required elements

**Business Value**: Ensures slide deck is well-formed before rendering - catches structural errors early

**Acceptance Criteria**: [See three-amigos-session-011.md](doc/specifications/three-amigos-session-011.md)

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-011.md)
- [Example Mapping](doc/specifications/example-mapping-011.md)

**Error Type**: `StructureError`

---

### 📋 US-012: Density Validation ("Fits on Slide")

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker) - **Key Differentiator from Marp**
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** receive warnings when slide content is too dense
> **So that** my slides are readable and fit on screen

**Business Value**: Core differentiator from Marp - prevents "wall of text" slides that are unreadable

**Acceptance Criteria**: [See three-amigos-session-012.md](doc/specifications/three-amigos-session-012.md)

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-012.md)
- [Example Mapping](doc/specifications/example-mapping-012.md)

**Error Type**: `DensityWarning` (non-blocking warnings)
**Theme Integration**: Limits configurable via `theme.layout.*` fields

---

### 📋 US-013: Content Validation (Slot Constraints)

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** receive validation errors for constraint violations
> **So that** my slide content meets template requirements

**Business Value**: Ensures content fits template constraints before rendering - prevents broken slides

**Difference from US-012**:
- **Content Validation (US-013)**: Template-specific constraints (ERRORS - blocking)
- **Density Validation (US-012)**: General "fits on slide" heuristics (WARNINGS - non-blocking)

**Acceptance Criteria**: [See three-amigos-session-013.md]

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-013.md)
- [Example Mapping](doc/specifications/example-mapping-013.md)

**Key Constraints Validated**:
- Required slots must be non-empty (whitespace-only = empty)
- Slot content must not exceed max_lines (if constraint defined)
- Slot content must not exceed max_chars (if constraint defined)
- Slot content must not exceed max_words (if constraint defined)
- Image paths must exist (validated at validation time)
- Code languages must be valid (Flexmark supported list)

**Error Type**: `ContentError` (blocking errors)

**Technical Details**:
- Line counting: Count `\n` newline characters
- Word counting: Split on whitespace
- Char counting: Plain text only (exclude markdown syntax)
- Image paths: Relative paths resolved from markdown file directory
- All errors collected (NonEmptyList pattern)

---

### 📋 US-015: Collect All Validation Errors (No Fail-Fast)

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** see all validation errors at once
> **So that** I can fix all issues in one pass (not play "whack-a-mole")

**Business Value**: Better UX - author sees ALL problems at once, fixes all issues in one iteration

**Acceptance Criteria**: [See three-amigos-session-015.md]

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-015.md)
- [Example Mapping](doc/specifications/example-mapping-015.md)

**Technical Architecture**:
- Return type: `Either[NonEmptyList[ValidationError], SlideDeck]`
- Error ADT: `ValidationError` sealed trait with StructureError, DensityWarning, ContentError
- Errors accumulated across all slides using Cats NonEmptyList
- Errors grouped by slide ID
- Structure errors block Content validation (dependency)
- Density warnings non-blocking (separate return value)

**Error Context** (each error includes):
- Slide number and template name
- Validation type ([Structure], [Density], [Content])
- Slot name (if applicable)
- Constraint violated
- Actual vs expected values

**Error Formatting Example**:
```
Validation failed with 3 errors:

Slide 1 (template: title):
  [Structure] Title slot is required but missing

Slide 3 (template: content):
  [Density] Body exceeds max 12 lines (found 15 lines) [WARNING]
  [Content] Heading exceeds max 80 chars (found 95 chars)
```

**Key Design Decisions**:
- Structure validation runs first (blocking)
- If Structure passes, Density + Content run
- Density warnings collected separately (non-blocking)
- Errors sorted by slide number, then validation type

---

## Epic 3: Theme System (Minimal)

**Goal**: Apply visual styling to slides

---

### 📋 US-008: Apply Theme to SlideDeck

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** specify a theme in deck metadata
> **So that** my slides have consistent visual styling

**Business Value**: Enables branded presentations, visual consistency across decks, professional appearance

**Acceptance Criteria**: [See three-amigos-session-008.md]

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-008.md)
- [Example Mapping](doc/specifications/example-mapping-008.md)
- [Default Theme](themes/default.json) (already created)

**Front Matter Syntax**:
```yaml
---
theme: default  # or custom theme name
---
```

**Theme Capabilities**:
- **Colors**: Background, text, heading, accent
- **Typography**: Font families, sizes, weights
- **Layout**: Slide dimensions, padding, margins
- **Density Limits**: Max body lines/words, max heading chars (theme-configurable)

**Technical Architecture**:
- Theme JSON loaded from `themes/` directory
- Default theme used if not specified
- Theme CSS inlined into HTML `<style>` tag
- Theme layout settings applied to slide containers
- Theme density limits override defaults (used in Density Validation)

**Processing Order**:
1. Parse markdown → SlideDeck
2. Load theme (from `theme:` field, default if missing)
3. Run validation (Density uses theme limits)
4. Render HTML (with theme CSS)

---

### 📋 US-009: Create and Validate Custom Theme

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** create custom themes using JSON with validation
> **So that** I can match my organization's branding while maintaining accessibility

**Business Value**: Essential for corporate use - every organization needs branded slides. Validation ensures themes are well-formed and accessible.

**Acceptance Criteria**: [See three-amigos-session-009.md]

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-009.md)
- [Example Mapping](doc/specifications/example-mapping-009.md)
- [Default Theme Reference](themes/default.json)

**Validation Rules**:
- **Required Fields**: name, colors, fonts, layout
- **Color Values**: Valid hex codes (#RRGGBB or #RRGGBBAA)
- **Font Sizes**: >= 18px (accessibility minimum)
- **Layout Dimensions**: slideWidth 800-3840px, slideHeight 600-2160px
- **Contrast Ratio**: >= 4.5:1 (WCAG AA) - warning if below
- **Name Matching**: Theme name must match filename

**Validation Return Type**:
- Errors: `Either[NonEmptyList[ThemeValidationError], Theme]`
- Warnings: `List[AccessibilityWarning]` (non-blocking)

**v1.0 Scope**:
- Structural validation (required fields, data types)
- Hex color format validation
- Font size minimum (18px)
- Layout dimension ranges
- Contrast ratio calculation (background vs foreground)
- Theme name/filename matching

**v1.1 Enhancements**:
- Additional color pair contrast checks
- RGB/HSL color support
- Font availability validation
- Color blindness simulation

---

## Epic 4: HTML Rendering

**Goal**: Transform validated SlideDeck into HTML output

---

### 📋 US-016: Render SlideDeck to HTML

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker)
**Estimated Effort**: 4 days

**User Story**:
> **As a** slide deck author
> **I want to** generate HTML from my slide deck
> **So that** I can view my presentation in a browser

**Business Value**: Minimal render target for MVP - proves the entire pipeline works (parse → validate → theme → render)

**Acceptance Criteria**: [See three-amigos-session-016.md]

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-016.md)
- [Example Mapping](doc/specifications/example-mapping-016.md)

**Technology**: Scalatags for type-safe HTML generation, Flexmark for markdown rendering

**v1.0 Capabilities**:
- Render title slide template (title, subtitle, author)
- Render content slide template (heading, body)
- Apply theme CSS (colors, fonts, layout) - inlined into `<style>` tag
- Keyboard navigation (→, ←, Space, Home, End)
- Slide counter ("current / total")
- Markdown formatting (bold, italics, code, lists, code blocks)
- Standalone HTML file (no external dependencies, works offline)
- Valid HTML5 (W3C compliant)

**v1.0 Limitations** (deferred):
- ❌ Speaker notes rendering (v1.1 - US-034)
- ❌ PDF export (v2.0)
- ❌ Print styling (v1.1)
- ❌ Two-screen presenter mode (v1.1)
- ❌ Syntax highlighting for code blocks (v1.1)

**HTML Output Structure**:
```html
<!DOCTYPE html>
<html>
<head>
  <style>/* Theme CSS inlined */</style>
</head>
<body>
  <div class="slide" id="slide-1">
    <h1>Title Content</h1>
    <h2>Subtitle Content</h2>
    <p class="author">Author Content</p>
  </div>
  <div class="slide" id="slide-2">
    <h2>Heading Content</h2>
    <div class="body">Body Content</div>
  </div>
  <script>/* Navigation JS inlined */</script>
</body>
</html>
```

**Next Steps**: Run Three Amigos session

---

## Epic 5: CLI Interface

**Goal**: Provide command-line interface for MDSlides

---

### 📋 US-019: Generate Slides via CLI

**Status**: 📋 Ready for Implementation
**Version**: v1.0 MVP
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** run `mdslides input.md output.html`
> **So that** I can generate slides from the command line

**Business Value**: No CLI = no product. Essential user-facing interface for v1.0.

**Acceptance Criteria**: [See three-amigos-session-019.md]

**Ceremonies Completed**:
- ✅ Three Amigos Session
- ✅ Example Mapping

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-019.md)
- [Example Mapping](doc/specifications/example-mapping-019.md)

**Technology**: Decline for argument parsing, Cats Effect for IO

**CLI Syntax**:
```bash
mdslides <input.md> [output.html] [options]

Options:
  --theme <name>       Specify theme (default: default)
  --validate-only      Run validation without rendering
  --help               Show help message
  --version            Show version
```

**Key Scenarios**:
- `mdslides slides.md` → generates `slides.html`, exit 0
- `mdslides slides.md deck.html` → generates `deck.html`, exit 0
- `mdslides slides.md --theme dark` → uses dark theme, exit 0
- `mdslides slides.md --validate-only` → validates only (no HTML), exit 0
- `mdslides --version` → shows "mdslides v1.0.0", exit 0
- `mdslides --help` → shows help text, exit 0
- `mdslides missing.md` → "Error: File 'missing.md' not found", exit 1
- `mdslides invalid.md` → formatted validation errors, exit 1

**Full Pipeline**:
1. Parse CLI arguments (Decline)
2. Read input markdown file
3. Parse markdown → SlideDeck
4. Load theme
5. Run validation (Structure, Density, Content)
6. Render HTML (if not --validate-only)
7. Write output HTML file
8. Print success message / errors
9. Exit with appropriate code (0 or 1)

---

# 🟩 v1.1 - "Is It Usable?"

**Release Goal**: Make MDSlides professional-quality with more layouts, accessibility validation, and speaker notes rendering.

**Story Count**: 5 stories

---

### 📝 US-014: Accessibility Validation (WCAG AA/AAA)

**Status**: 📝 Draft
**Version**: v1.1
**Priority**: P1 (High)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** receive accessibility validation warnings
> **So that** my slides meet WCAG AA standards

**Validation Rules**:
- ✅ Contrast ratio >= 4.5:1 for normal text (WCAG AA)
- ✅ Contrast ratio >= 7:1 for normal text (WCAG AAA)
- ✅ Font size >= 24px (enforced)
- ✅ Images have alt text (if `requireAltText: true` in theme)
- ✅ Heading hierarchy enforced (H1 → H2 → H3, no skipping)

**Error Type**: `AccessibilityWarning` (not blocker)

---

### 📝 US-005: Create Two-Column Comparison Slide

**Status**: 📝 Draft
**Version**: v1.1
**Priority**: P1 (High)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** create a two-column comparison slide
> **So that** I can show side-by-side comparisons or contrasts

**Template**: `templates/two-column.yaml`

---

### 📝 US-006: Create Image Slide with Caption

**Status**: 📝 Draft
**Version**: v1.1
**Priority**: P1 (High)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** create an image-focused slide with caption
> **So that** I can present visual content effectively

**Template**: `templates/image.yaml`

---

### 📝 US-007: Create Code Snippet Slide

**Status**: 📝 Draft
**Version**: v1.1
**Priority**: P1 (High)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** create a code snippet slide with syntax highlighting
> **So that** I can show code examples in presentations

**Template**: `templates/code.yaml`

---

### 🔮 US-034: Speaker View with Notes

**Status**: 🔮 Future
**Version**: v1.1
**Priority**: P1 (High)
**Estimated Effort**: 3 days

**User Story**:
> **As a** presenter
> **I want to** view speaker notes alongside slides
> **So that** I can remember what to say during presentations

**Features**:
- Dual-pane view (current slide + speaker notes)
- Next slide preview
- Timer display
- Keyboard shortcuts (N for notes toggle)

---

# 🟧 v2.0 - "Is It Professional?"

**Release Goal**: Enterprise-grade structured presentations with LaTeX-inspired automation.

**Story Count**: 8 stories

---

## Epic 8: Document Structure & Automation (LaTeX-Inspired)

---

### 📝 US-026: Variable Substitution (Mustache-Style)

**Status**: 📝 Draft
**Version**: v2.0
**Priority**: P1 (High)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** use variables like `{{title}}` and `{{author}}` in my slides
> **So that** I can maintain consistency and avoid repetition

**Syntax**: `{{variableName}}` (NOT LaTeX `\variable`)

**Variable Sources**:
- Front matter: `{{title}}`, `{{author}}`, `{{date}}`
- Computed: `{{slideNumber}}`, `{{totalSlides}}`, `{{section}}`

---

### 📝 US-027: Section Hierarchy and Metadata

**Status**: 📝 Draft
**Version**: v2.0
**Priority**: P1 (High)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** organize slides into sections and subsections
> **So that** my presentation has clear structure for TOC and navigation

**Front Matter Syntax**:
```yaml
sections:
  - id: intro
    title: "Introduction"
    subsections:
      - "Background"
      - "Problem Statement"
```

---

### 📝 US-028: Auto-Generate Table of Contents

**Status**: 📝 Draft
**Version**: v2.0
**Priority**: P1 (High)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** auto-generate a TOC slide from section headings
> **So that** I don't have to manually maintain the agenda

**Template**: `template: toc`

---

### 📝 US-029: Section Divider Slides

**Status**: 📝 Draft
**Version**: v2.0
**Priority**: P2 (Medium)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** auto-generate section divider slides
> **So that** my presentation has clear visual breaks between sections

**Template**: `template: section-divider`

---

### 📝 US-030: Appendix Slides with Special Numbering

**Status**: 📝 Draft
**Version**: v2.0
**Priority**: P2 (Medium)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** mark slides as appendix with different numbering
> **So that** backup slides don't inflate main slide count

**Template**: `template: appendix-marker`

**Numbering**: Main slides (1, 2, 3...), Appendix slides (A1, A2, A3...)

---

### 🔮 US-017: Export SlideDeck to PDF

**Status**: 🔮 Future
**Version**: v2.0
**Priority**: P2 (Medium)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** export my slide deck to PDF
> **So that** I can share it as a static document

**Technology**: HTML → PDF converter (Puppeteer/Playwright via JVM)

---

### 🔮 US-022: Render Mermaid Diagrams

**Status**: 🔮 Future
**Version**: v2.0
**Priority**: P2 (Medium)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** embed Mermaid diagrams in slides
> **So that** I can show flowcharts and diagrams

---

### 🔮 US-024: Load Custom Templates from Directory

**Status**: 🔮 Future
**Version**: v2.0
**Priority**: P2 (Medium)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** create custom slide templates
> **So that** I can define my own slide layouts

---

# 🟥 v3.0 - "Is It Academic-Grade?"

**Release Goal**: Feature parity with LaTeX Beamer for technical/academic presentations.

**Story Count**: 7 stories

---

### 🔮 US-021: Math Typesetting (KaTeX/MathJax)

**Status**: 🔮 Future
**Version**: v3.0
**Priority**: P2 (Medium)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** include mathematical equations using LaTeX syntax
> **So that** I can present technical content with proper notation

**Syntax**: `$$E = mc^2$$` (block), `$x^2$` (inline)

---

### 🔮 US-023: Custom Slide Backgrounds

**Status**: 🔮 Future
**Version**: v3.0
**Priority**: P2 (Medium)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** set custom backgrounds per slide
> **So that** I can create visually distinctive slides

---

### 🔮 US-025: Template Inheritance

**Status**: 🔮 Future
**Version**: v3.0
**Priority**: P3 (Low)
**Estimated Effort**: 3 days

**User Story**:
> **As a** template designer
> **I want to** create templates that extend base templates
> **So that** I can reuse common slot definitions

---

### 🔮 US-031: Cross-References (Markdown Links)

**Status**: 🔮 Future
**Version**: v3.0
**Priority**: P2 (Medium)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** reference other slides by ID
> **So that** I can create hyperlinks between slides

**Syntax**: `[architecture overview](#arch-overview)`

---

### 🔮 US-032: References/Bibliography Slide

**Status**: 🔮 Future
**Version**: v3.0
**Priority**: P3 (Low)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** auto-generate a references slide
> **So that** I can cite sources without manual formatting

**Template**: `template: references`

---

### 🔮 US-010: Theme Directives (Per-Slide Overrides)

**Status**: 🔮 Future
**Version**: v3.0
**Priority**: P3 (Low)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** override theme properties per slide
> **So that** I can customize individual slides

---

### 🔮 US-020: Watch Mode (Auto-Reload)

**Status**: 🔮 Future
**Version**: v3.0
**Priority**: P3 (Low)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** auto-reload slides when markdown changes
> **So that** I can see updates in real-time

---

# 📊 Backlog Summary by Version

```
Total Stories: 32

v1.0 MVP (Core Pipeline):
├── Ready: 12 (ALL - US-001, 002, 003, 004, 008, 009, 011, 012, 013, 015, 016, 019)
├── Draft: 0
└── Story Count: 12 ✅ PHASE 2 COMPLETE

v1.1 (Usability):
├── Draft: 4 (US-005, 006, 007, 014)
├── Future: 1 (US-034)
└── Story Count: 5

v2.0 (Structure & Automation):
├── Draft: 5 (US-026, 027, 028, 029, 030)
├── Future: 3 (US-017, 022, 024)
└── Story Count: 8

v3.0 (Advanced):
├── Future: 7 (US-010, 020, 021, 023, 025, 031, 032)
└── Story Count: 7

Priority Breakdown:
├── P0 (Blocker): 12 stories (all in v1.0 MVP)
├── P1 (High): 9 stories (v1.1 + v2.0)
├── P2 (Medium): 8 stories (v2.0 + v3.0)
└── P3 (Low): 3 stories (v3.0)
```

---

# 🎯 v1.0 MVP Definition of Done

**MVP Capabilities**:
- ✅ Parse Markdown with YAML front matter
- ✅ Multi-slide parsing (slide separator: `---`)
- ✅ Template binding (title, content templates)
- ✅ Speaker notes parsing (no rendering)
- ✅ Multi-stage validation (structure, density, content)
- ✅ Collect all validation errors (no fail-fast)
- ✅ Theme application (default theme)
- ✅ Custom theme loading (structural validation only)
- ✅ HTML rendering (slide view only, no speaker notes)
- ✅ CLI interface (`mdslides input.md output.html`)
- ✅ Pure functional pipeline (Cats Effect, IO monad)

**MVP Exclusions** (deferred to v1.1+):
- ❌ Speaker notes rendering (parse only in v1.0)
- ❌ PDF export (v2.0)
- ❌ Accessibility validation (v1.1)
- ❌ Additional slide layouts (two-column, image, code) (v1.1)
- ❌ LaTeX-inspired features (variables, sections, TOC) (v2.0)
- ❌ Math typesetting (v3.0)

---

# 🚀 Implementation Path (Option A - Rigorous)

**Phase 2: Specification (Before Implementation)**

For each v1.0 MVP story (US-002 through US-019):

1. **Run Three Amigos Session**
   - Business perspective (value, success criteria)
   - Development perspective (implementation approach, risks)
   - Testing perspective (scenarios, edge cases)
   - Resolve all open questions
   - Document in `doc/specifications/three-amigos-session-{ID}.md`

2. **Create Example Mapping**
   - Identify business rules (blue cards)
   - Create concrete examples (green cards)
   - Document questions (red cards, must resolve)
   - Ensure rule → example coverage
   - Document in `doc/specifications/example-mapping-{ID}.md`

3. **Mark Story as Ready**
   - Update status to `📋 Ready`
   - All acceptance criteria defined
   - All questions resolved
   - Testable scenarios documented

**Phase 3: Implementation (TDD Red-Green-Refactor)**

For each Ready story:

1. **Red**: Write failing tests (BDD style with ScalaTest)
2. **Green**: Implement minimal code to pass tests
3. **Refactor**: Clean up, ensure pure FP, ubiquitous language
4. **Mark Done**: Update status to `✅ Done`

---

# 📅 Recommended Ceremony Schedule

**Week 1-2**: Three Amigos + Example Mapping for v1.0 stories
- Day 1-2: US-002 (Content slide)
- Day 3-4: US-003 (Multi-slide parsing)
- Day 5-6: US-004 (Speaker notes)
- Day 7-8: US-011 (Structure validation)
- Day 9-10: US-012 (Density validation)

**Week 3**: Three Amigos + Example Mapping continued
- Day 11-12: US-013 (Content validation)
- Day 13-14: US-015 (Collect all errors)
- Day 15-16: US-008 (Theme application)
- Day 17-18: US-009 (Custom theme validation)

**Week 4**: Three Amigos + Example Mapping final stories
- Day 19-20: US-016 (HTML rendering)
- Day 21-22: US-019 (CLI interface)

**Week 5+**: Begin TDD implementation of US-001 (already Ready)

---

## Definition of Ready (DoR)

A story is "Ready" when:
- ✅ User story format complete (As a/I want/So that)
- ✅ Business value clearly stated
- ✅ Acceptance criteria defined (functional + technical + non-functional)
- ✅ Three Amigos session completed
- ✅ Example Mapping done (all questions resolved)
- ✅ Dependencies identified
- ✅ Testable scenarios documented

---

## Definition of Done (DoD)

A story is "Done" when:
- ✅ All acceptance criteria met
- ✅ Unit tests written (BDD style with ScalaTest)
- ✅ Property-based tests (where applicable)
- ✅ Code follows ubiquitous language
- ✅ Pure functional (no side effects in domain)
- ✅ Documentation updated
- ✅ Peer review completed (self-review for solo dev)

---

**Last Updated**: 2024-12-20 (v3.1.0 - Phase 2 Complete: All v1.0 MVP stories Ready for TDD)
**Product Owner**: Tony Moores, TJM Solutions

**Change Log**:
- v1.0 (2024-12-19): Initial backlog with US-001 only
- v2.0 (2024-12-19): Added comprehensive backlog based on Marp + Google Slides patterns
- v2.1 (2024-12-19): Added 7 LaTeX-inspired stories for document structure (US-026 through US-032)
- v3.0 (2024-12-19): Version bucketing complete (v1.0 MVP, v1.1, v2.0, v3.0)
- **v3.1 (2024-12-20): PHASE 2 COMPLETE - All 12 v1.0 MVP stories ceremony-complete and Ready for TDD**

**Status**: ✅ All v1.0 MVP ceremonies complete (12/12 stories)
**Next Step**: Begin Phase 3 - TDD Implementation (start with US-001, then US-002, 003, 004 in dependency order)
