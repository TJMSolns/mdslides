# Product Backlog: MDSlides
## User Stories & Acceptance Criteria (Enhanced with Marp/Google Slides Patterns)

---

```yaml
# MACHINE-READABLE METADATA
backlog:
  product: MDSlides
  version: 2.0.0
  last_updated: 2024-12-19
  owner: Tony Moores, TJM Solutions
  inspiration:
    - Marp (markdown presentation ecosystem)
    - Google Slides (layout patterns)
    - Docusaurus (front matter compatibility)

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
```

---

## Epic 1: Core Slide Parsing & Template System

**Goal**: Implement foundational Markdown parsing with template-driven structure

**Marp Inspiration**: Directives, page splitting (`---`), front matter
**Charter Scope**: Phase 1 - Core DSL and template binding

---

### 📋 US-001: Create Title Slide from Markdown

**Status**: 📋 Ready for Implementation
**Priority**: P0 (Blocker)
**Estimated Effort**: 2-3 days
**Marp Equivalent**: Title directive + first slide

**User Story**:
> **As a** slide deck author
> **I want to** create a title slide from Markdown with explicit template binding
> **So that** I can generate a properly validated title slide with title, subtitle, and author

**Artifacts**:
- [Three Amigos Session](doc/specifications/three-amigos-session-001.md) ✅
- [Example Mapping](doc/specifications/example-mapping-001.md) ✅
- 20 Acceptance Criteria defined ✅

---

### 📝 US-002: Create Content Slide from Markdown

**Status**: 📝 Draft
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days
**Marp Equivalent**: Standard slide with heading + content

**User Story**:
> **As a** slide deck author
> **I want to** create a standard content slide with heading and body
> **So that** I can present structured information (most common slide type)

**Business Value**: 80% of slides are content slides with heading + bullets/paragraphs

**Template**: `templates/content.yaml`
- Heading (required, H2, max 1 line, heading level 2)
- Body (required, markdown_block, max 12 lines, max 150 words)

**Key Scenarios to Define**:
- Valid content slide with lists
- Valid content slide with paragraphs
- Body exceeds max lines (validation error)
- Body exceeds max words (validation error)
- Missing heading (validation error)

**Next Steps**: Three Amigos session needed

---

### 📝 US-003: Parse Multi-Slide Markdown File

**Status**: 📝 Draft
**Priority**: P0 (Blocker)
**Estimated Effort**: 3 days
**Marp Equivalent**: Page separator `---`

**User Story**:
> **As a** slide deck author
> **I want to** create multiple slides in one Markdown file separated by `---`
> **So that** I can author complete presentations in a single document

**Business Value**: Foundation for real presentations (1 slide is a demo, 10+ slides is useful)

**Slide Separator**: `---` (triple dash, Marp-compatible)

**Key Scenarios**:
- 2 slides with different templates
- 10 slides mixed templates
- Slide deck with > 200 slides (validation error)
- Empty slide between separators (validation error)
- Deck metadata in first front matter block
- Per-slide metadata in subsequent front matter

**Design Decision Needed**:
- Global front matter (deck level) vs per-slide front matter
- Marp pattern: Global at top, local directives override

**Next Steps**: Three Amigos session needed

---

### 📝 US-004: Support Speaker Notes

**Status**: 📝 Draft
**Priority**: P1 (High)
**Estimated Effort**: 2 days
**Marp Equivalent**: HTML comments `<!-- -->`
**Google Slides Equivalent**: Speaker notes panel

**User Story**:
> **As a** slide deck author
> **I want to** add speaker notes to slides that don't appear on the slide
> **So that** I can remember talking points during presentation

**Business Value**: Professional presentations need speaker notes for delivery

**Syntax Options** (need to decide):
- **Option A** (Marp style): HTML comments
  ```markdown
  # My Slide
  Content here

  <!-- Speaker notes go here -->
  ```
- **Option B**: Front matter field
  ```yaml
  ---
  template: content
  notes: |
    Remember to emphasize the key point
    Pause for questions here
  ---
  ```
- **Option C**: Special section marker
  ```markdown
  # My Slide
  Content here

  ---notes---
  Speaker notes here
  ```

**Recommendation**: Option B (front matter) for consistency with Docusaurus

**Key Scenarios**:
- Slide with speaker notes
- Slide without speaker notes
- Speaker notes > 500 chars (warning, not error)
- Speaker notes in HTML output (hidden, printable)

---

### 📝 US-005: Two-Column Comparison Layout

**Status**: 📝 Draft
**Priority**: P1 (High)
**Estimated Effort**: 2 days
**Google Slides Equivalent**: 2-column layout template

**User Story**:
> **As a** slide deck author
> **I want to** create side-by-side comparison slides
> **So that** I can show before/after, pros/cons, or contrasting concepts

**Business Value**: Common pattern in technical presentations (20% of slides)

**Template**: `templates/two-column.yaml`
- Heading (required, H2, max 1 line)
- Left Column (required, markdown_block, max 10 lines, max 75 words)
- Right Column (required, markdown_block, max 10 lines, max 75 words)

**Syntax** (need to decide):
- **Option A**: Horizontal rule separator
  ```markdown
  ---
  template: two-column
  ---
  ## Comparison

  ### Left Side
  Content for left column

  ---

  ### Right Side
  Content for right column
  ```
- **Option B**: Named sections
  ```markdown
  ---
  template: two-column
  ---
  ## Comparison

  ::left::
  Content for left column

  ::right::
  Content for right column
  ```

**Recommendation**: Option A (simpler, uses existing Markdown)

---

### 📝 US-006: Image Slide with Caption

**Status**: 📝 Draft
**Priority**: P1 (High)
**Estimated Effort**: 2 days
**Marp Equivalent**: `![bg](image.jpg)` background images

**User Story**:
> **As a** slide deck author
> **I want to** create image-focused slides with optional caption
> **So that** I can present visual content effectively

**Template**: `templates/image.yaml`
- Heading (optional, H2, max 1 line)
- Image (required, Image type, alt text required)
- Caption (optional, markdown_inline, max 120 chars)

**Key Scenarios**:
- Image with heading and caption
- Image without heading (image-only slide)
- Missing alt text (accessibility error)
- Image path relative to markdown file
- Image URL (external)
- Invalid image path (validation error)

**Accessibility Requirement**: Alt text always required (WCAG AA)

---

### 📝 US-007: Code Snippet Slide

**Status**: 📝 Draft
**Priority**: P1 (High)
**Estimated Effort**: 2-3 days
**Marp Equivalent**: Fenced code blocks with syntax highlighting

**User Story**:
> **As a** slide deck author
> **I want to** create slides with syntax-highlighted code
> **So that** I can show code examples in technical presentations

**Template**: `templates/code.yaml`
- Heading (required, H2, max 1 line)
- Code (required, Code type, max 20 lines, language specified)
- Notes (optional, markdown_block, max 5 lines, max 50 words)

**Key Scenarios**:
- Code block with language (```scala)
- Code block > 20 lines (validation error)
- Code block without language (default to plain text)
- Inline highlighting of specific lines (future enhancement)

**Technical Requirement**: Syntax highlighting in HTML output

---

## Epic 2: Theme System & Styling

**Goal**: Theme-driven visual customization without changing content structure

**Marp Inspiration**: Built-in themes (default, gaia, uncover), CSS customization
**Charter Scope**: Separation of structure (templates) from style (themes)

---

### 📝 US-008: Apply Theme to SlideDeck

**Status**: 📝 Draft
**Priority**: P1 (High)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** specify a theme in deck metadata
> **So that** my slides have consistent visual styling

**Business Value**: Enables branded presentations, visual consistency across decks

**Front Matter Syntax**:
```yaml
---
theme: default
---
```

**Key Scenarios**:
- Use default theme (built-in)
- Use custom theme from themes/ directory
- Use custom theme from absolute path
- Theme not found (validation error)
- Theme with invalid JSON (parsing error)
- Theme with missing required fields (validation error)
- Theme fails accessibility checks (validation warning)

**Theme Loading Strategy**:
1. Check built-in themes (default, dark, minimal)
2. Check `themes/` directory (relative to markdown file)
3. Check absolute path if specified
4. Fail with clear error if not found

**Built-in Themes** (Phase 1):
- `default` (high contrast, WCAG AA) ✅ Created
- `dark` (dark background, light text) - to create
- `minimal` (clean, lots of whitespace) - to create

**Next Steps**: Three Amigos session needed

---

### 📝 US-009: Create and Validate Custom Theme

**Status**: 📝 Draft
**Priority**: P1 (High) ← **PROMOTED from P2**
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** create custom themes using JSON with validation
> **So that** I can match my organization's branding while maintaining accessibility

**Business Value**: Essential for corporate use - every organization needs branded slides

**Theme File Location Options**:
1. **Project themes**: `./themes/corporate.json` (next to markdown)
2. **User themes**: `~/.mdslides/themes/corporate.json` (global)
3. **Absolute path**: `/path/to/themes/corporate.json`

**Theme Schema** (based on `themes/default.json`):

```json
{
  "name": "corporate",
  "description": "Corporate branding theme",
  "colors": {
    "background": "#FFFFFF",
    "foreground": "#000000",
    "accent": "#FF6600",
    "heading": "#1a1a1a",
    "code_background": "#f5f5f5",
    "code_foreground": "#24292e"
  },
  "fonts": {
    "family": "Roboto, sans-serif",
    "titleSize": 52,
    "subtitleSize": 40,
    "headingSize": 36,
    "bodySize": 28,
    "codeSize": 24,
    "codeFontFamily": "Fira Code, monospace"
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
    "allowEmoji": false,
    "allowUnicodeSymbols": true
  },
  "accessibility": {
    "contrastRatio": 4.5,
    "minFontSize": 24,
    "requireAltText": true,
    "enforceHeadingHierarchy": true
  },
  "metadata": {
    "version": "1.0.0",
    "author": "Acme Corp Design Team",
    "created": "2024-12-19",
    "wcagLevel": "AA"
  }
}
```

**Key Scenarios**:
- Create custom theme with valid JSON
- Create theme with custom colors
- Create theme with custom fonts
- Theme with invalid hex color codes (validation error)
- Theme with contrast ratio < 4.5:1 (accessibility warning)
- Theme with font size < 24px (accessibility error)
- Theme missing required fields (validation error)
- Theme with negative dimensions (validation error)

**Validation Rules**:
- ✅ All hex colors must be valid (#RRGGBB format)
- ✅ Font sizes must be positive integers
- ✅ Slide dimensions must be positive integers
- ✅ Contrast ratio calculated and compared to `contrastRatio` setting
- ✅ If `wcagLevel: "AA"`, enforce contrast >= 4.5:1
- ✅ If `wcagLevel: "AAA"`, enforce contrast >= 7:1
- ⚠️ Warning if custom fonts not web-safe (not an error)

**Design Decision**: Theme validation happens at load time (not parse time)

**Next Steps**: Three Amigos session needed

---

### 🔮 US-010: Theme Directives (Per-Slide Overrides)

**Status**: 🔮 Future
**Priority**: P3 (Low)
**Estimated Effort**: 2 days
**Marp Equivalent**: Local directives

**User Story**:
> **As a** slide deck author
> **I want to** override theme settings for specific slides
> **So that** I can emphasize important slides differently

**Example**:
```yaml
---
template: title
backgroundColor: "#FF0000"
---
# Important Announcement
```

**Scope**: Phase 2 (after base theme system works)

---

## Epic 3: Validation Framework

**Goal**: Multi-stage validation with clear error messages

**Charter Scope**: Structure, Density, Content, Accessibility validation

---

### 📝 US-011: Structure Validation

**Status**: 📝 Draft
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** see validation errors for structural issues
> **So that** I know if my deck has invalid structure

**Validations**:
- Deck has 1-200 slides
- Deck has a title (first slide or front matter)
- All slides have unique IDs (if specified)
- All templates exist in Template Library
- All required slots filled

**Error Format**:
```
StructureError: Deck has 0 slides (minimum 1 required)
StructureError: Template 'fancy' not found in Template Library
StructureError: Slide 'intro' missing required slot 'title'
```

---

### 📝 US-012: Density Validation ("Fits on Slide")

**Status**: 📝 Draft
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** see warnings when slides have too much content
> **So that** I can ensure slides are readable during presentation

**Heuristics** (from template constraints):
- Title max 2 lines
- Subtitle max 2 lines
- Body max 12 lines
- Body max 150 words
- Code max 20 lines

**Error Format**:
```
DensityError (slide 'overview'): Body has 180 words, max 150 allowed
DensityError (slide 'intro'): Title has 3 lines, max 2 allowed
```

---

### 📝 US-013: Content Validation (Slot Constraints)

**Status**: 📝 Draft
**Priority**: P0 (Blocker)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** see validation errors for content constraint violations
> **So that** I can fix my content to match template requirements

**Validations**:
- max_lines constraint enforced
- max_chars constraint enforced
- max_words constraint enforced
- required slots have content
- slot types match (markdown_block, markdown_inline, image, code)

**Error Format**:
```
ContentError (slide 'intro', slot 'author'): Content has 95 chars, max 80 allowed
ContentError (slide 'summary', slot 'heading'): Slot is required but empty
```

---

### 📝 US-014: Accessibility Validation (WCAG AA)

**Status**: 📝 Draft
**Priority**: P1 (High)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** ensure my slides meet accessibility standards
> **So that** my presentations are inclusive

**Validations**:
- Color contrast ratio >= 4.5:1 (theme colors)
- All images have alt text
- Heading hierarchy correct (H1 → H2, no skipping)
- Font size >= 24px minimum (theme setting)

**Error Format**:
```
AccessibilityError: Color contrast ratio 3.2:1 is below WCAG AA minimum 4.5:1
AccessibilityError (slide 'diagram'): Image missing alt text
AccessibilityError (slide 'summary'): Heading hierarchy violation (H1 → H3, skipped H2)
```

---

### 📝 US-015: Collect All Validation Errors (No Fail-Fast)

**Status**: 📝 Draft
**Priority**: P0 (Blocker)
**Estimated Effort**: 1 day

**User Story**:
> **As a** slide deck author
> **I want to** see ALL validation errors at once
> **So that** I can fix multiple issues in one pass

**Implementation**: `Either[NonEmptyList[ValidationError], SlideDeck]`

**Key Scenario**:
- Deck with 5 errors shows all 5 (not just first one)
- Errors grouped by category (Structure, Density, Content, Accessibility)

---

## Epic 4: Rendering Pipeline

**Goal**: Transform validated SlideDeck to HTML/PDF

**Marp Equivalent**: HTML export, PDF export, PowerPoint export
**Charter Scope**: HTML via Scalatags, PDF via HTML converter

---

### 🔮 US-016: Render SlideDeck to HTML

**Status**: 🔮 Future
**Priority**: P0 (Blocker for MVP)
**Estimated Effort**: 4-5 days

**User Story**:
> **As a** slide deck author
> **I want to** generate HTML from my validated slide deck
> **So that** I can view my presentation in a browser

**Technology**: Scalatags (type-safe HTML generation)

**Requirements**:
- Each slide is a `<section>` element
- Theme CSS applied
- Syntax highlighting for code blocks
- Speaker notes in hidden `<aside>` elements
- Responsive layout (1920x1080 default)

**Output Format**: Single HTML file with embedded CSS

---

### 🔮 US-017: Export to PDF

**Status**: 🔮 Future
**Priority**: P1 (High)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** export my slide deck to PDF
> **So that** I can share it as a static document

**Technology Options**:
- Flying Saucer (Java HTML → PDF)
- WeasyPrint (Python, external dependency)
- Headless Chrome (puppeteer pattern)

**Requirements**:
- Preserve visual fidelity from HTML
- One slide per PDF page
- Include speaker notes page after each slide (optional)

---

### 🔮 US-018: Print Speaker Notes

**Status**: 🔮 Future
**Priority**: P2 (Medium)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** generate a printable speaker notes document
> **So that** I can reference notes during presentation

**Format**: PDF with slide thumbnail + notes text

---

## Epic 5: CLI Interface

**Goal**: Command-line tool for slide generation

**Charter Scope**: Decline for argument parsing, os-lib for file I/O

---

### 🔮 US-019: Generate Slides via CLI

**Status**: 🔮 Future
**Priority**: P0 (Blocker for MVP)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** run `mdslides input.md output.html`
> **So that** I can generate slides from the command line

**CLI Arguments**:
```bash
mdslides generate input.md -o output.html --theme dark
mdslides validate input.md
mdslides export input.md -o output.pdf
```

**Technology**: Decline (type-safe CLI parsing)

---

### 🔮 US-020: Watch Mode (Live Reload)

**Status**: 🔮 Future
**Priority**: P2 (Medium)
**Estimated Effort**: 2 days
**Marp Equivalent**: Live preview in VS Code extension

**User Story**:
> **As a** slide deck author
> **I want to** run `mdslides watch input.md`
> **So that** my slides auto-regenerate when I save the file

**Implementation**: File watcher with debouncing

---

## Epic 6: Advanced Content Features

**Goal**: Rich content beyond basic markdown

**Marp Inspiration**: Extended syntax, auto-scaling, math typesetting

---

### 🔮 US-021: Math Typesetting (LaTeX)

**Status**: 🔮 Future
**Priority**: P2 (Medium)
**Estimated Effort**: 3 days

**User Story**:
> **As a** slide deck author
> **I want to** include mathematical formulas using LaTeX syntax
> **So that** I can create technical/academic presentations

**Syntax**: `$inline$` and `$$block$$` (standard)

**Technology**: KaTeX or MathJax rendering

---

### 🔮 US-022: Mermaid Diagrams

**Status**: 🔮 Future
**Priority**: P2 (Medium)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** embed Mermaid diagrams in slides
> **So that** I can show flowcharts, sequence diagrams, etc.

**Syntax**: Fenced code block with `mermaid` language

---

### 🔮 US-023: Slide Backgrounds (Images/Colors)

**Status**: 🔮 Future
**Priority**: P3 (Low)
**Estimated Effort**: 2 days
**Marp Equivalent**: `![bg](image.jpg)`

**User Story**:
> **As a** slide deck author
> **I want to** set custom backgrounds per slide
> **So that** I can create visually striking slides

**Front Matter Override**:
```yaml
---
template: content
background: url(hero.jpg)
backgroundSize: cover
---
```

---

## Epic 7: Template Library Management

**Goal**: Extensible template system

---

### 🔮 US-024: Load Custom Templates from Directory

**Status**: 🔮 Future
**Priority**: P1 (High)
**Estimated Effort**: 2 days

**User Story**:
> **As a** slide deck author
> **I want to** place custom template YAML files in `templates/`
> **So that** I can use project-specific slide layouts

**Loading Strategy**:
- Scan `templates/` recursively at startup
- Built-in templates (title, content, two-column, image, code)
- Custom templates override built-in if same ID

---

### 🔮 US-025: Template Inheritance

**Status**: 🔮 Future (Phase 2)
**Priority**: P3 (Low)
**Estimated Effort**: 3 days

**User Story**:
> **As a** template author
> **I want to** extend existing templates
> **So that** I can reuse slot definitions

**Example**:
```yaml
id: branded-title
extends: title
slots:
  - name: logo
    type: image
    required: true
```

---

## Epic 8: Document Structure & Automation (LaTeX-Inspired)

**Goal**: Auto-generated slides and structured content inspired by LaTeX Beamer

**LaTeX Patterns**: `\tableofcontents`, `\section{}`, `\appendix`, `\bibliography{}`
**Markdown-Native Syntax**: YAML front matter + templates (no backslash commands)

---

### 📝 US-026: Variable Substitution (Mustache-Style)

**Status**: 📝 Draft
**Priority**: P1 (High)
**Estimated Effort**: 2 days
**LaTeX Inspiration**: `\title`, `\author`, but using `{{}}` syntax

**User Story**:
> **As a** slide deck author
> **I want to** use variables like `{{title}}` and `{{author}}` in my slides
> **So that** I can maintain consistency and avoid repetition

**Business Value**: Reduces duplication, maintains consistency, enables dynamic footers

**Syntax**: Mustache-style `{{variableName}}` (NOT LaTeX `\variable`)

**Why NOT LaTeX syntax**:
- `\title` conflicts with LaTeX math escaping
- `{{}}` has no markdown conflicts
- Clear, unambiguous delimiters

**Variable Sources**:

1. **Front Matter Variables**:
```markdown
---
title: "Introduction to Functional Programming"
author: "Tony Moores"
date: "2024-12-19"
company: "TJM Solutions"
---

# {{title}}
By {{author}}, {{company}}
Presented on {{date}}
```

2. **Computed Variables** (auto-generated):
- `{{slideNumber}}` - Current slide number
- `{{totalSlides}}` - Total slide count
- `{{section}}` - Current section name
- `{{subsection}}` - Current subsection name

**Key Scenarios**:
- Title slide uses `{{title}}` and `{{author}}`
- Footer uses `{{section}} | Slide {{slideNumber}}/{{totalSlides}}`
- Variable not found → validation warning (show `{{unknownVar}}` as-is)
- Recursive variables → validation error
- Variable in front matter value → validation error (no nesting)

**Design Decisions**:
- Variables resolved at render time (not parse time)
- Unknown variables render as-is (not error)
- No variable nesting (keep it simple)

**Next Steps**: Three Amigos session needed

---

### 📝 US-027: Section Hierarchy and Metadata

**Status**: 📝 Draft
**Priority**: P1 (High)
**Estimated Effort**: 2 days
**LaTeX Inspiration**: `\section{}`, `\subsection{}` structure

**User Story**:
> **As a** slide deck author
> **I want to** organize slides into sections and subsections
> **So that** my presentation has clear structure for TOC and navigation

**Business Value**: Enables auto-TOC, section dividers, structured navigation

**Front Matter Syntax** (Global):
```markdown
---
title: "My Presentation"
sections:
  - id: intro
    title: "Introduction"
    subsections:
      - "Background"
      - "Problem Statement"
  - id: arch
    title: "Architecture"
    subsections:
      - "Component Design"
      - "Data Flow"
  - id: conclusion
    title: "Conclusion"
---
```

**Per-Slide Section Assignment**:
```markdown
---
template: content
section: intro
subsection: "Background"
---
## Historical Context
...
```

**Key Scenarios**:
- Define 3 sections with 2 subsections each
- Slide references non-existent section → validation error
- Section hierarchy used for TOC generation
- Section name used in footer via `{{section}}`
- No sections defined → all slides in default section

**Design Decisions**:
- **Explicit declaration** (LaTeX-style) in global front matter
- Per-slide assignment via `section:` field
- Section IDs used for cross-references

**Next Steps**: Three Amigos session needed

---

### 📝 US-028: Auto-Generate Table of Contents

**Status**: 📝 Draft
**Priority**: P1 (High)
**Estimated Effort**: 3 days
**LaTeX Inspiration**: `\tableofcontents` command

**User Story**:
> **As a** slide deck author
> **I want to** auto-generate a TOC slide from section headings
> **So that** I don't have to manually maintain the agenda

**Business Value**: Automatic agenda slide, always up-to-date with structure

**Template Syntax** (LaTeX `\tableofcontents` equivalent):
```markdown
---
template: toc
title: "Agenda"
maxDepth: 2
showSlideNumbers: true
---
<!-- Content auto-generated from sections -->
```

**Generated Output** (example):
```markdown
# Agenda

1. Introduction
   - Background (slide 3)
   - Problem Statement (slide 5)
2. Architecture
   - Component Design (slide 8)
   - Data Flow (slide 12)
3. Conclusion (slide 15)
```

**Key Scenarios**:
- TOC with maxDepth: 1 (sections only)
- TOC with maxDepth: 2 (sections + subsections)
- TOC with slide numbers
- TOC without slide numbers
- TOC in middle of deck (not just beginning)
- No sections defined → validation error
- Multiple TOC slides allowed

**Design Decisions**:
- Empty `template: toc` slide = auto-generated
- Manual content in TOC slide = used as-is (override)
- TOC generation requires `sections:` in global front matter

**Implementation**:
- **Multi-pass parsing**:
  1. First pass: Parse all slides, extract sections
  2. Second pass: Generate TOC content
  3. Third pass: Validation

**Next Steps**: Three Amigos session needed

---

### 📝 US-029: Section Divider Slides

**Status**: 📝 Draft
**Priority**: P2 (Medium)
**Estimated Effort**: 2 days
**LaTeX Inspiration**: Beamer `\section{}` auto-creates section title frames

**User Story**:
> **As a** slide deck author
> **I want to** auto-generate section divider slides
> **So that** my presentation has clear visual breaks between sections

**Business Value**: Professional presentation structure, visual navigation cues

**Template**:
```markdown
---
template: section-divider
section: intro
---
<!-- Auto-generated title slide for section -->
```

**Manual Override** (optional):
```markdown
---
template: section-divider
section: intro
---
# Introduction
Custom subtitle or image here
```

**Auto-Generation Option** (global front matter):
```yaml
---
sections:
  - id: intro
    title: "Introduction"
    autoGenerateDivider: true  # Creates divider slide automatically
---
```

**Key Scenarios**:
- Section divider with auto-generated title
- Section divider with custom content
- Auto-generate dividers for all sections
- Skip dividers for specific sections
- Divider slide not counted in main slide numbers (optional)

**Theme Styling**:
```json
{
  "sectionDivider": {
    "backgroundColor": "#0066CC",
    "foregroundColor": "#FFFFFF",
    "fontSize": 60,
    "centered": true
  }
}
```

**Next Steps**: Three Amigos session needed

---

### 📝 US-030: Appendix Slides with Special Numbering

**Status**: 📝 Draft
**Priority**: P2 (Medium)
**Estimated Effort**: 2 days
**LaTeX Inspiration**: `\appendix` command changes slide numbering

**User Story**:
> **As a** slide deck author
> **I want to** mark slides as appendix with different numbering
> **So that** backup slides don't inflate main slide count

**Business Value**: Professional presentations have backup/reference slides

**Template** (LaTeX `\appendix` equivalent):
```markdown
---
template: appendix-marker
---
<!-- Slides after this are numbered A1, A2, A3... -->
```

**Behavior**:
- Main slides: 1, 2, 3, ..., 15
- Appendix marker (unnumbered)
- Appendix slides: A1, A2, A3, ...

**Slide Count**:
- `{{totalSlides}}` = 15 (excludes appendix)
- `{{totalSlidesWithAppendix}}` = 18
- Footer in appendix: "Appendix A2"

**Key Scenarios**:
- Presentation with 15 main slides + 5 appendix slides
- Multiple appendix markers → validation error
- Appendix marker as first slide → validation error
- `{{slideNumber}}` in appendix returns "A1", "A2", etc.

**Theme Styling**:
```json
{
  "appendix": {
    "backgroundColor": "#F0F0F0",
    "showInTOC": false,
    "numberPrefix": "A"
  }
}
```

**Next Steps**: Three Amigos session needed

---

### 🔮 US-031: Cross-References (Markdown Links)

**Status**: 🔮 Future
**Priority**: P2 (Medium)
**Estimated Effort**: 2 days
**LaTeX Inspiration**: `\label{}` and `\ref{}`, but using markdown links

**User Story**:
> **As a** slide deck author
> **I want to** reference other slides by ID
> **So that** I can create hyperlinks between slides

**Syntax** (NOT LaTeX `\ref{}`):
```markdown
See [architecture overview](#arch-overview) for details.
```

**Slide IDs**:
```markdown
---
id: arch-overview
template: content
---
## Architecture Overview
...
```

**Generated Link**: `<a href="#slide-arch-overview">architecture overview</a>`

**Why NOT LaTeX `\ref`**:
- Markdown already has link syntax
- No conflicts with existing markdown
- HTML anchors are natural output

**Key Scenarios**:
- Link to slide by ID
- Link to non-existent slide → validation warning
- Link text auto-generated from slide title (optional)
- External links vs internal links (differentiate)

---

### 🔮 US-032: References/Bibliography Slide

**Status**: 🔮 Future
**Priority**: P3 (Low)
**Estimated Effort**: 2 days
**LaTeX Inspiration**: `\bibliography{}`, but simpler

**User Story**:
> **As a** slide deck author
> **I want to** auto-generate a references slide
> **So that** I can cite sources without manual formatting

**Simple Approach** (NOT BibTeX complexity):
```markdown
---
template: references
title: "References"
---
<!-- Auto-generated from citations in slides -->
```

**Citation Syntax** (in slides):
```markdown
According to recent studies [^smith2024].

[^smith2024]: Smith, J. (2024). "Functional Programming Patterns"
```

**Generated References Slide**:
```markdown
# References

1. Smith, J. (2024). "Functional Programming Patterns"
```

**Why NOT Full BibTeX**:
- Too complex for slides
- Simple footnote syntax is enough
- Most presentations need 5-10 references max

---

## Backlog Summary

```
Total Stories: 32 (added 7 LaTeX-inspired stories!)
├── Ready: 1 (US-001)
├── Draft: 19 (US-002 through US-015, US-026 through US-030)
└── Future: 12 (US-016 through US-025, US-031, US-032)

Priority Breakdown:
├── P0 (Blocker): 8 stories (foundational)
├── P1 (High): 12 stories (MVP + structure) ← Added US-026, US-027, US-028!
├── P2 (Medium): 8 stories (nice-to-have) ← Added US-029, US-030, US-031!
└── P3 (Low): 4 stories (future enhancements) ← Added US-032!

Phase 1 Scope (Core DSL + Themes):
- US-001: Title slide ✅ Ready
- US-002: Content slide 📝
- US-003: Multi-slide parsing 📝
- US-004: Speaker notes 📝
- US-008: Theme application 📝
- US-009: Custom themes 📝
- US-011-015: Validation framework 📝

Phase 2 Scope (Rendering + Structure):
- US-016: HTML rendering 🔮
- US-019: CLI interface 🔮
- US-026: Variable substitution 📝 ← NEW (LaTeX-inspired)!
- US-027: Section hierarchy 📝 ← NEW (LaTeX-inspired)!
- US-028: Auto-generate TOC 📝 ← NEW (LaTeX-inspired)!

Phase 3 Scope (Advanced + Polish):
- US-017: PDF export 🔮
- US-021: Math typesetting 🔮
- US-024: Custom templates 🔮
- US-029: Section dividers 📝 ← NEW (LaTeX-inspired)!
- US-030: Appendix slides 📝 ← NEW (LaTeX-inspired)!
- US-031: Cross-references 🔮 ← NEW (LaTeX-inspired)!
- US-032: Bibliography slide 🔮 ← NEW (LaTeX-inspired)!
```

---

## Recommended Sprint 1 (After US-001)

**Goal**: Complete core slide types with validation

**Stories**:
1. 🏗️ US-001: Title slide (in progress - ready to implement!)
2. 📝 US-002: Content slide (run Three Amigos)
3. 📝 US-011: Structure validation (run Three Amigos)
4. 📝 US-012: Density validation (run Three Amigos)
5. 📝 US-013: Content validation (run Three Amigos)

**Sprint Outcome**: Can create and validate single title/content slides

---

## Recommended Sprint 2

**Goal**: Multi-slide support + Theme system

**Stories**:
1. 📝 US-003: Multi-slide parsing (run Three Amigos)
2. 📝 US-004: Speaker notes (run Three Amigos)
3. 📝 US-008: Theme application (run Three Amigos)
4. 📝 US-009: Custom themes (run Three Amigos)
5. 📝 US-014: Accessibility validation (run Three Amigos)

**Sprint Outcome**: Can create full branded presentations with custom themes

---

## Recommended Sprint 3

**Goal**: Additional layouts + Rendering

**Stories**:
1. 📝 US-005: Two-column layout
2. 📝 US-006: Image slide
3. 📝 US-007: Code slide
4. 🔮 US-016: HTML rendering
5. 🔮 US-019: CLI interface

**Sprint Outcome**: Complete feature set with HTML output via CLI

---

## Sources

Based on research from:
- [Marp: Markdown Presentation Ecosystem](https://marp.app/)
- [Marp Directives Documentation](https://github.com/marp-team/marp/blob/main/website/docs/guide/directives.md)
- [Marp: A Markdown Presentation App That Simplifies Your Tech Talks](https://dev.to/rprabhu/marp-a-markdown-presentation-app-that-simplifies-your-tech-talks-37m4)

---

**Last Updated**: 2024-12-19 (v2.1 - Added LaTeX-inspired features)
**Product Owner**: Tony Moores, TJM Solutions

**Change Log**:
- v2.0 (2024-12-19): Added comprehensive backlog based on Marp + Google Slides patterns
- v2.1 (2024-12-19): Added 7 LaTeX-inspired stories for document structure (US-026 through US-032)

**Next Step**: Choose path - implement US-001 OR run more Three Amigos sessions
