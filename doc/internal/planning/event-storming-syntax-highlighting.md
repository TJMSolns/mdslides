# Event Storming: Syntax Highlighting for Code Blocks

**Feature**: Syntax Highlighting
**Priority**: SHOULD HAVE (v1.2 carry-over)
**Date**: 2024-12-27
**Participants**: Development Team

---

## Business Context

**As a** presentation author
**I want** syntax-highlighted code blocks
**So that** code is easier to read and understand in presentations

**Business Value:**
- Professional code presentation
- Improved readability for technical content
- Matches expectations from modern developer tools
- Theme-aware color schemes

---

## Domain Events (Time-Ordered)

### 1. **Code Block Parsed**
- **Trigger**: Markdown parser encounters fenced code block
- **Data**: Code content, optional language hint (e.g., `scala`, `python`, `javascript`)
- **Existing**: Already implemented - CodeBlock domain model exists
- **Note**: No changes needed to domain

### 2. **Language Detected**
- **Trigger**: Code block has language hint
- **Data**: Language identifier (from fence: ```scala)
- **Existing**: Already captured in CodeBlock.language: Option[String]
- **Note**: Domain model already supports this

### 3. **Code Block Rendered to HTML**
- **Trigger**: HTMLRenderer processes CodeBlock
- **Current State**: Renders as `<pre><code class="language-{lang}">{code}</code></pre>`
- **Enhancement Needed**: Add highlight.js integration

### 4. **Highlight.js Library Loaded** (NEW)
- **Trigger**: HTML page loads in browser
- **Data**: CDN URL for highlight.js, theme CSS
- **Action**: Include `<script>` and `<link>` tags in HTML head

### 5. **Code Block Highlighted** (NEW)
- **Trigger**: Page DOMContentLoaded event
- **Data**: Code blocks with `language-*` class
- **Action**: highlight.js automatically highlights all `<pre><code>` blocks

### 6. **Theme Colors Applied** (EXISTING)
- **Trigger**: Code block rendered
- **Current State**: Theme.syntax colors defined but not used
- **Enhancement**: Map theme.syntax colors to highlight.js theme or use built-in theme

---

## Commands

### Existing Commands (No Changes)
1. **Parse Markdown** → Creates CodeBlock with language hint
2. **Validate Code Block** → Checks line count (PDR-006)

### Modified Commands
3. **Render Code Block to HTML**
   - Current: Basic `<pre><code>` rendering
   - Enhanced: Add proper classes for highlight.js
   - Add language class if provided
   - Keep existing auto-scaling logic (PDR-006)

### New Commands
4. **Include Highlight.js Library**
   - Add CDN `<script>` tag to HTML head
   - Add highlight.js CSS theme to HTML head
   - Choose theme that matches MDSlides theme (light/dark)

5. **Initialize Syntax Highlighting**
   - Add JavaScript to call `hljs.highlightAll()` on DOMContentLoaded
   - Alternative: Use `hljs.highlightElement()` for specific blocks

---

## Aggregates & Value Objects

### Existing (No Changes)
- **CodeBlock** (domain/FormattedContent.scala)
  - `code: String` - the source code
  - `language: Option[String]` - language hint from fence
  - Already has `lineCount`, `exceedsGuideline` methods

- **Theme** (domain/Theme.scala)
  - `syntax: SyntaxColors` - keyword, string, comment, function, number, operator colors
  - Currently defined but not used in rendering

### Potential Enhancement (Optional)
- **Theme.highlightJsTheme: Option[String]**
  - Allow themes to specify preferred highlight.js theme
  - Default: "default" for light, "monokai" for dark
  - Defer to future: Use built-in highlight.js themes initially

---

## Read Models

### HTMLRenderer (infrastructure)
- **Current**: Renders `<pre><code class="language-{lang}">{code}</code></pre>`
- **Enhanced**:
  - Include highlight.js CDN in `<head>`
  - Include highlight.js CSS theme in `<head>`
  - Add initialization script: `hljs.highlightAll()`
  - Keep existing code block rendering (already compatible with highlight.js)

---

## Policies / Business Rules

### Existing (Preserved)
1. **PDR-006: Code Block Auto-Scaling**
   - Code blocks >20 lines auto-scale font size
   - Preserved: This happens via existing JavaScript

2. **Language Detection**
   - Use language hint from fence if provided: ```scala
   - If no hint, highlight.js can auto-detect (less reliable)
   - Recommendation: Always use language hints for best results

### New Policies
3. **Highlight.js Integration**
   - Use CDN for simplicity (no local dependencies)
   - Use highlight.js v11.x (latest stable)
   - Theme selection: Match MDSlides theme (light → "default", dark → "monokai")

4. **Fallback Behavior**
   - If highlight.js fails to load, code still renders (graceful degradation)
   - Existing theme.syntax colors remain in CSS as fallback

5. **Theme Compatibility**
   - Built-in themes (light, dark, corporate, retisio) get appropriate highlight.js themes
   - Custom themes can override via CSS if needed

---

## Open Questions

### Q1: Which highlight.js themes to use?
- **Light theme**: "default" or "github"?
- **Dark theme**: "monokai" or "vs2015"?
- **Decision**: Use "github" for light, "monokai-sublime" for dark (popular, well-tested)

### Q2: Should we allow theme.json to specify highlight.js theme?
- **Defer**: Use built-in mapping for v1.3, allow override in v2.0
- **Rationale**: Keep theme.json simple, advanced users can override CSS

### Q3: Should we validate language hints?
- **No**: highlight.js handles unknown languages gracefully
- **No domain changes needed**

### Q4: CDN vs bundled?
- **CDN**: Simpler, smaller JAR, always up-to-date
- **Bundled**: Works offline, faster initial load
- **Decision**: Use CDN for v1.3 (simpler), revisit bundling in v2.0 if needed

---

## Implementation Strategy

### Phase 1: Infrastructure (HTMLRenderer)
1. Modify `HTMLRenderer.renderDeck` to include highlight.js in `<head>`
2. Add `<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js">`
3. Add `<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/{theme}.min.css">`
4. Add initialization script: `hljs.highlightAll()`

### Phase 2: Theme Mapping
1. Map theme name to highlight.js theme:
   - light → "github"
   - dark → "monokai-sublime"
   - corporate → "github"
   - retisio → "github" (light background)

### Phase 3: Testing
1. Verify code blocks render with syntax highlighting
2. Test multiple languages (scala, python, javascript, java, sql, bash)
3. Verify auto-scaling still works (PDR-006)
4. Test graceful degradation (CDN failure)

### Phase 4: Documentation
1. Update tutorial with syntax-highlighted code examples
2. Document supported languages in README
3. Add note about language hints in markdown syntax guide

---

## Non-Goals (Deferred)

1. **Custom syntax themes**: Use built-in highlight.js themes only
2. **Server-side highlighting**: Client-side only (simpler, no build step)
3. **Line numbering**: Not needed for presentations
4. **Copy-to-clipboard button**: Nice-to-have for v2.0

---

## Success Metrics

1. All code blocks with language hints are syntax-highlighted
2. Colors match highlight.js theme (github/monokai-sublime)
3. Auto-scaling still works for long code blocks
4. No impact on build time (CDN-based)
5. Tutorial examples render with proper highlighting

---

## Related Governance

- **PDR-006**: Code Block Rendering Limits (preserved)
- **US-004**: Code Block Support (existing)
- **Theme System**: US-016 Directory-Based Themes

---

**Next Steps:**
1. Three Amigos session to validate acceptance criteria
2. Example Mapping to identify test scenarios
3. TDD implementation (infrastructure only, no domain changes)
