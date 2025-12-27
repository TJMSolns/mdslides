# Domain Ceremonies - v0.2.0 (Markdown Rendering)

**Date:** December 21, 2024
**Participants:** Development Team
**Facilitator:** Domain Expert

## Session Overview

This document captures the Event Storming and Domain Ceremonies for **v0.2.0**, which adds full markdown rendering capabilities to MDSlides.

## Key Decisions Made

### User Requirements (December 21, 2024):
1. **GitHub Flavored Markdown (GFM) Support**: Must support everything GitHub supports, including Mermaid diagrams
2. **Offline + Online**: Must work completely offline (no external CDN dependencies)
3. **Code Blocks**: 20-line guideline reasonable, but auto-scale font size to fit more lines on slide
4. **Images**:
   - 1 background image per slide/template
   - Multiple content images allowed (validated for density)
5. **Themes**: Must support background images in theme definition
6. **Word Counting**: Count only visible words (strip markdown syntax, URLs, alt text)

### Technical Decisions:
- **Markdown Library**: Flexmark (supports GFM + extensions)
- **Syntax Highlighting**: JavaScript-based (highlight.js, bundled inline)
- **Mermaid Diagrams**: JavaScript-based (mermaid.js, bundled inline)

## Changes from v0.1.0 MVP

**Current state (v0.1.0):**
- Markdown text is preserved as-is (no formatting)
- Body content rendered as plain text
- No bold, italic, links, code blocks, or images

**Target state (v0.2.0):**
- Full GitHub Flavored Markdown (GFM) rendering
- Code blocks with syntax highlighting (offline-capable)
- Mermaid diagram support
- Image embedding (background + content images)
- Theme system with background image support
- Built-in themes (light, dark, corporate)

## User Stories for v0.2.0

### US-003: Full Markdown Rendering
**As a** presenter
**I want** my markdown formatting (bold, italic, links) to be rendered in HTML
**So that** I can emphasize key points and include hyperlinks

**Acceptance Criteria:**
- Bold text (`**bold**` or `__bold__`) renders as `<strong>`
- Italic text (`*italic*` or `_italic_`) renders as `<em>`
- Links (`[text](url)`) render as `<a href="url">text</a>`
- Inline code (`` `code` ``) renders as `<code>`
- Lists (unordered `-`, ordered `1.`) render as `<ul>`/`<ol>`
- Validation constraints still apply to word/line counts (count raw text, not markup)

### US-004: Code Block Support
**As a** technical presenter
**I want** to include code examples with syntax highlighting
**So that** my audience can read code easily

**Acceptance Criteria:**
- Fenced code blocks (` ``` `) render as `<pre><code>`
- Language hints (` ```scala `) apply syntax highlighting via highlight.js (bundled, offline-capable)
- Supported languages: all highlight.js languages (180+)
- Code blocks have 20-line guideline, but auto-scale font size to fit more
- Validation warns (not fails) when code blocks exceed 20 lines
- Mermaid code blocks (` ```mermaid `) render diagrams via mermaid.js (bundled, offline-capable)

### US-005: Image Embedding
**As a** presenter
**I want** to embed images in slides
**So that** I can include diagrams, charts, and visual aids

**Acceptance Criteria:**
- Content images: Markdown images (`![alt](url)`) render as `<img>` in body
- Background images: Specified in theme or template frontmatter
- Supports relative paths and URLs
- Content images don't count toward word/line limits
- Alt text required for content images (accessibility)
- No hard limit on content images (validated for visual density)
- Max 1 background image per slide

### US-008: Theme System
**As a** developer/designer
**I want** to define custom themes via JSON
**So that** I can customize slide appearance

**Acceptance Criteria:**
- Theme defined in JSON file
- Specifies colors, fonts, spacing, background images
- Applied during rendering
- Default theme preserved from v0.1.0
- CLI accepts `--theme theme.json` option
- Background images can be set per-theme

### US-009: Built-in Themes
**As a** user
**I want** pre-built themes (light, dark, corporate)
**So that** I don't have to create themes from scratch

**Acceptance Criteria:**
- Three built-in themes: `light`, `dark`, `corporate`
- CLI accepts `--theme light|dark|corporate`
- Default is `light` (same as v0.1.0 default)
- Themes apply to all slide types

---

## Event Storming

### Domain Events

#### New Events for v0.2.0:
1. **MarkdownContentParsed** - Raw markdown text parsed into formatted elements
2. **InlineFormattingApplied** - Bold, italic, code applied to text spans
3. **LinkExtracted** - Hyperlink found and validated
4. **CodeBlockIdentified** - Fenced code block discovered with language hint
5. **ImageReferenceFound** - Image markdown discovered
6. **ThemeSelected** - User chose a theme for rendering
7. **SyntaxHighlightingApplied** - Code block colorized for language

#### Existing Events (unchanged):
- SlideCreated
- SlideDeckAssembled
- SlideValidated (structure)
- ContentValidated (density)
- HTMLRendered

### Commands

#### New Commands for v0.2.0:
1. **ParseMarkdownContent** - Convert markdown text to structured elements
2. **ApplyInlineFormatting** - Process bold, italic, code spans
3. **ExtractLinks** - Find and validate hyperlinks
4. **HighlightCodeBlock** - Apply syntax highlighting to code
5. **EmbedImage** - Process image reference
6. **SelectTheme** - Choose rendering theme
7. **ApplyTheme** - Apply theme colors/styles to HTML

#### Existing Commands (unchanged):
- CreateSlide
- AssembleSlideDeck
- ValidateStructure
- ValidateContent
- RenderHTML

### Aggregates

#### Updated Aggregates:
1. **Slide** (enhanced)
   - Still has: id, templateName, slots
   - **NEW**: Slots now contain structured content (not just raw strings)
   - **NEW**: Each slot has `SlotContent` which wraps `FormattedContent`

2. **SlideDeck** (unchanged structure)
   - No changes to deck-level logic
   - Still validates 1-200 slides, unique IDs

#### New Aggregates:
3. **FormattedContent** (new)
   - Represents parsed markdown with structure
   - Contains: text spans, formatting marks, links, code blocks, images
   - Validates: accessibility (alt text), density (image count)

4. **Theme** (new)
   - Defines: colors, fonts, spacing, code highlighting colors
   - Built-in themes: light, dark, corporate
   - Custom themes loaded from JSON

### Value Objects

#### New Value Objects:
1. **TextSpan** - A segment of text with optional formatting (bold, italic, code)
2. **Link** - URL and link text
3. **CodeBlock** - Code text, language hint, line count
4. **ImageReference** - URL/path, alt text
5. **ThemeName** - Opaque type for theme identifier
6. **ThemeColors** - Color palette (background, text, accent, code)

#### Existing Value Objects (unchanged):
- SlideId
- SlotContent (modified to wrap FormattedContent)

### Policies

#### New Policies:
1. **Markdown Rendering Policy**: When markdown text is provided, parse it to FormattedContent before validation
2. **Code Block Density Policy**: Code blocks can exceed body line limit (up to 20 lines) but must be marked explicitly
3. **Image Limit Policy**: Maximum 1 image per slide (density constraint)
4. **Theme Application Policy**: Theme selected at render time, doesn't affect validation

#### Existing Policies (unchanged):
- Density Validation Policy (still applies, but now to formatted content)
- Structure Validation Policy

---

## Domain Ceremonies

### Ceremony 1: Creating a Slide with Formatted Content

**Actors:** User, CLI, MarkdownParser, Slide, FormattedContent

**Scenario:** User creates a content slide with bold text and a link

**Input Markdown:**
```markdown
---
template: content
---
## Key Features

MDSlides is **domain-driven** and *well-tested*.

Learn more at [our docs](https://example.com).
```

**Flow:**
1. **User** provides markdown file to CLI
2. **CLI** reads file, passes raw markdown to MarkdownParser
3. **MarkdownParser** splits by `---` delimiters, extracts frontmatter and content
4. **MarkdownParser** for each slot:
   - Parses markdown text into FormattedContent
   - Identifies bold (`**domain-driven**`), italic (`*well-tested*`), link
   - Creates TextSpan objects with formatting flags
   - Creates Link object with URL and text
5. **Slide.validated()** receives slots with FormattedContent
6. **Slide** validates structure (required slots present)
7. **Slide** validates content density:
   - Counts raw text words (ignoring markdown syntax): "MDSlides is domain-driven and well-tested. Learn more at our docs." = 13 words ✓
   - Counts raw text lines: 3 lines ✓
   - Links validated: URL well-formed ✓
8. **Slide** returns validated slide with FormattedContent

**Output:** Valid Slide with structured FormattedContent ready for rendering

**Questions:**
- Q: How do we count words/lines for density validation?
- A: Count raw text only, strip markdown syntax. `**bold**` = 1 word, not 3.

- Q: Do links count toward word count?
- A: Yes, link *text* counts, but URL doesn't. `[docs](url)` = 1 word.

---

### Ceremony 2: Rendering a Code Block

**Actors:** User, Slide, CodeBlock, HTMLRenderer, SyntaxHighlighter

**Scenario:** User includes a Scala code example

**Input Markdown:**
```markdown
---
template: content
---
## Example Code

Here's a simple function:

```scala
def hello(name: String): String =
  s"Hello, $name!"
```

Clean and functional!
```

**Flow:**
1. **MarkdownParser** identifies fenced code block
2. **MarkdownParser** extracts language hint: `scala`
3. **MarkdownParser** creates CodeBlock value object:
   - code: `def hello(name: String): String = ...`
   - language: `scala`
   - lineCount: 2
4. **Slide.validated()** receives body with CodeBlock
5. **Slide** validates content:
   - Body text (excluding code): "Here's a simple function: Clean and functional!" = 6 words ✓
   - Body lines (excluding code): 2 lines (before and after code block) ✓
   - Code block lines: 2 lines (within 20-line code limit) ✓
6. **HTMLRenderer** processes CodeBlock:
   - Calls SyntaxHighlighter.highlight(code, "scala")
   - SyntaxHighlighter returns HTML with `<span>` tags for keywords, strings, etc.
   - Wraps in `<pre><code class="language-scala">...</code></pre>`
7. **HTMLRenderer** assembles final HTML

**Output:** HTML slide with syntax-highlighted code block

**Questions:**
- Q: Do code blocks count toward the 12-line body limit?
- A: No, code blocks are excluded from body line count, but have their own limit (20 lines).

- Q: What if language hint is unsupported?
- A: Render as plain `<code>` without highlighting. No validation error.

---

### Ceremony 3: Embedding an Image

**Actors:** User, Slide, ImageReference, HTMLRenderer

**Scenario:** User embeds a diagram

**Input Markdown:**
```markdown
---
template: content
---
## Architecture Diagram

Our three-layer design:

![MDSlides Architecture](./images/architecture.png)

Clean separation of concerns!
```

**Flow:**
1. **MarkdownParser** identifies image: `![alt](url)`
2. **MarkdownParser** creates ImageReference:
   - url: `./images/architecture.png`
   - altText: `MDSlides Architecture`
3. **Slide.validated()** receives body with ImageReference
4. **Slide** validates content:
   - Alt text present ✓ (accessibility requirement)
   - Image count: 1 ✓ (max 1 per slide)
   - Body text (excluding image): "Our three-layer design: Clean separation of concerns!" = 6 words ✓
   - Body lines (excluding image): 2 lines ✓
5. **HTMLRenderer** processes ImageReference:
   - Resolves relative path (or uses URL as-is)
   - Generates `<img src="./images/architecture.png" alt="MDSlides Architecture">`
6. **HTMLRenderer** assembles final HTML

**Output:** HTML slide with embedded image

**Questions:**
- Q: What if image file doesn't exist?
- A: No validation error at build time. Browser handles missing image at render time.

- Q: What if alt text is missing?
- A: Validation error: "Image missing required alt text (accessibility)"

- Q: Can we have multiple images?
- A: No, validation error: "Slide exceeds max 1 image (has 2)"

---

### Ceremony 4: Applying a Theme

**Actors:** User, CLI, Theme, HTMLRenderer

**Scenario:** User applies dark theme to presentation

**Input Command:**
```bash
mill cli.run presentation.md output.html --theme dark
```

**Flow:**
1. **User** specifies `--theme dark` flag
2. **CLI** parses flag, creates ThemeName("dark")
3. **CLI** loads built-in dark theme:
   - Background: #1e1e1e
   - Text: #d4d4d4
   - Accent: #569cd6
   - Code background: #2d2d2d
   - Code syntax colors: {...}
4. **CLI** passes Theme to HTMLRenderer
5. **HTMLRenderer** generates HTML with theme CSS:
   - Injects theme colors into `<style>` block
   - Applies background-color, color, accent colors
   - Applies code syntax highlighting colors
6. **HTMLRenderer** returns themed HTML

**Output:** HTML presentation with dark theme

**Questions:**
- Q: Can user override individual theme colors?
- A: Not in v0.2.0. Custom themes require full JSON file.

- Q: What if theme file is invalid JSON?
- A: CLI error: "Invalid theme file: {parse error}"

---

## Validation Rules (Updated for v0.2.0)

### Structure Validation (unchanged)
- Required slots present for template type
- Valid template names: `title`, `content`
- Deck size: 1-200 slides
- Unique slide IDs

### Content Validation (enhanced)

#### Text Density (applies to formatted text):
- **Word count**: Count raw text only, strip markdown syntax
  - `**bold**` = 1 word
  - `[link text](url)` = 2 words (count link text, not URL)
  - `` `code` `` = 1 word
- **Line count**: Count raw text lines, exclude code blocks and images
  - Code blocks: excluded from body line count, have separate 20-line limit
  - Images: excluded from line count

#### Code Blocks:
- Max 20 lines per code block
- Supported languages: scala, java, python, javascript, bash, sql, markdown, json, xml
- Unsupported languages: render without highlighting, no error

#### Images:
- Max 1 image per slide (density constraint)
- Alt text required (accessibility)
- URL/path can be relative or absolute

#### Links:
- URL must be well-formed (start with `http://`, `https://`, or relative path)
- Link text required (can't be empty)

---

## Ubiquitous Language Updates

### New Terms:
- **FormattedContent**: Parsed markdown with structure (text spans, links, code, images)
- **TextSpan**: Segment of text with formatting (bold, italic, code)
- **CodeBlock**: Fenced code block with language hint
- **ImageReference**: Image URL/path with alt text
- **Theme**: Color palette and styling rules
- **SyntaxHighlighting**: Code colorization based on language

### Updated Terms:
- **SlotContent**: Now wraps FormattedContent (instead of raw String)
- **Validation**: Now validates both structure and formatted content density

---

## Architecture Impact

### Domain Layer Changes:
1. **New**: FormattedContent value object (wraps parsed markdown)
2. **New**: TextSpan, Link, CodeBlock, ImageReference value objects
3. **New**: Theme aggregate
4. **Modified**: SlotContent now wraps FormattedContent
5. **Modified**: Validation logic counts raw text (not markdown syntax)

### Infrastructure Layer Changes:
1. **Modified**: MarkdownParser now parses markdown into FormattedContent (not raw String)
2. **New**: SyntaxHighlighter adapter (wraps external highlighting library)
3. **Modified**: HTMLRenderer renders FormattedContent (bold, italic, links, code, images)
4. **New**: ThemeLoader (loads JSON themes)

### CLI Layer Changes:
1. **New**: `--theme` flag for theme selection
2. **New**: Theme loading and validation

---

## Dependencies (New for v0.2.0)

### Markdown Parsing:
- **com.vladsch.flexmark:flexmark-all:0.64.8** - Markdown parser with extensions
- Provides: inline formatting, code blocks, images, links

### Syntax Highlighting:
- **de.neuland-bfi:jade4j:1.3.2** or similar - Code syntax highlighting
- Alternative: **highlight.js** (JavaScript, inline in HTML)
- **Decision needed**: JVM-based (Jade4j) vs JavaScript-based (highlight.js)

### Theme Loading:
- **io.circe:circe-core:0.14.6** - JSON parsing for themes
- **io.circe:circe-parser:0.14.6**

---

## Open Questions

### Q1: Markdown Library Selection
**Question:** Should we use Flexmark (JVM) or CommonMark (JVM) or another library?

**Options:**
- **Flexmark**: Full-featured, extensions, heavyweight (~5MB)
- **CommonMark-java**: Lightweight (~500KB), core spec only
- **Laika**: Pure Scala, but adds complexity

**Decision needed:** ADR-010 (next step)

### Q2: Syntax Highlighting Approach
**Question:** Should highlighting be JVM-based or JavaScript-based?

**Options:**
- **JVM (Jade4j, etc.)**: Highlighting at build time, larger JAR
- **JavaScript (highlight.js)**: Highlighting at render time (browser), smaller JAR, requires JavaScript

**Decision needed:** ADR-011 (next step)

### Q3: Code Block Line Limit
**Question:** Should code blocks have a line limit? If so, what?

**Current thinking:** Max 20 lines per code block (enforce readability)

**Decision needed:** PDR-006 (next step)

### Q4: Theme JSON Schema
**Question:** What should the theme JSON structure look like?

**Current thinking:**
```json
{
  "name": "dark",
  "colors": {
    "background": "#1e1e1e",
    "text": "#d4d4d4",
    "accent": "#569cd6",
    "link": "#4ec9b0",
    "codeBackground": "#2d2d2d",
    "codeText": "#d4d4d4"
  },
  "fonts": {
    "body": "Arial, sans-serif",
    "heading": "Helvetica, sans-serif",
    "code": "Consolas, monospace"
  },
  "spacing": {
    "slideMargin": "2rem",
    "lineHeight": "1.6"
  },
  "syntax": {
    "keyword": "#569cd6",
    "string": "#ce9178",
    "comment": "#6a9955"
  }
}
```

**Decision needed:** PDR-007 (next step)

---

## Next Steps

1. ✅ **Event Storming & Ceremonies** (this document)
2. ⏭️ **Create ADRs**:
   - ADR-010: Markdown Library Selection
   - ADR-011: Syntax Highlighting Approach
3. ⏭️ **Create PDRs**:
   - PDR-006: Code Block Line Limits
   - PDR-007: Theme JSON Schema
4. ⏭️ **Update POLs** (if needed):
   - POL-003: Pure Functional Domain (validate it still holds)
5. ⏭️ **Update Backlog**:
   - Refine US-003, US-004, US-005, US-008, US-009
6. ⏭️ **TDD Implementation**:
   - Start with US-003 (simplest: inline formatting)
   - Then US-004 (code blocks)
   - Then US-005 (images)
   - Then US-008/US-009 (themes)

---

**MDSlides v0.2.0 Ceremonies** - Markdown Rendering Foundation
December 21, 2024
