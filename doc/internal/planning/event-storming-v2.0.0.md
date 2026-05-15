# Event Storming: MDSlides v2.0.0 Major Release

**Date**: 2025-12-27
**Facilitator**: AI Assistant (Claude)
**Participants**: Domain Expert (Tony Moores via user proxy)
**Status**: IN PROGRESS

---

## Session Goals

Explore domain events for v2.0.0 major features:
1. Mermaid diagram support (US-022)
2. Additional slide templates (US-005-007)
3. Configuration management
4. Custom theme generator

---

## Domain Events (Orange Stickies)

### Feature 1: Mermaid Diagram Support

**User Actions → Domain Events:**

1. **User writes Mermaid diagram in markdown**
   ```markdown
   ## Diagram Slide

   ```mermaid
   graph TD
       A[Start] --> B{Decision}
       B -->|Yes| C[Good]
       B -->|No| D[Bad]
   ```
   ```

2. **Markdown Parsed** ← Parser recognizes ```mermaid code fence
3. **Mermaid Code Block Extracted** ← FlexmarkAdapter extracts diagram source
4. **Diagram Type Detected** (flowchart, sequence, gantt, class, state, etc.)
5. **Mermaid Block Validated** ← Check syntax is valid (optional pre-render)
6. **Diagram Added to Content** ← ContentElement sealed trait extended
7. **HTML Rendered** ← Mermaid.js integration via CDN
8. **Diagram Rendered in Browser** ← Client-side rendering via mermaid.initialize()

**Questions:**
- Q: Server-side rendering or client-side?
  - A: **Client-side** (like highlight.js). CDN approach, simpler, no node.js dependency
- Q: Syntax validation before rendering?
  - A: **Optional**. Mermaid.js will show error in browser. Could add domain validation later.
- Q: Fallback if CDN unavailable?
  - A: Show source code in `<pre>` with warning message (graceful degradation)
- Q: Accessibility for diagrams?
  - A: Require alt text in front matter or via comment syntax: `%%{alt: "Diagram description"}%%`

**Domain Model:**
- New case class: `MermaidDiagram(source: String, diagramType: String, altText: Option[String])`
- Extend ContentElement sealed trait: `case class DiagramElement(diagram: MermaidDiagram) extends ContentElement`
- FlexmarkAdapter extracts mermaid blocks
- HTMLRenderer includes Mermaid.js CDN + initialization script

---

### Feature 2: Additional Slide Templates

**Template Types:**
1. **Diagram Template** - Centered diagram with caption
2. **Closing Template** - Thank you slide with contact info
3. **Section Title Template** - Section dividers for long presentations

**User Actions → Domain Events:**

**Diagram Template:**
1. **User creates slide with `template: diagram` in front matter**
   ```markdown
   ---
   template: diagram
   caption: "System Architecture"
   ---

   ## Architecture Overview

   ```mermaid
   graph LR
       UI --> API
       API --> DB
   ```
   ```

2. **Template Specified** ← Front matter parsed
3. **Diagram Template Selected** ← Template resolver
4. **Slots Validated** ← Must have: diagram (Mermaid), caption (optional)
5. **HTML Rendered** ← Centered layout, larger diagram, caption below

**Closing Template:**
1. **User creates slide with `template: closing`**
   ```markdown
   ---
   template: closing
   ---

   ## Thank You

   Questions?

   **Contact**: tony@tjmsolutions.com
   **GitHub**: github.com/tjmoores
   ```

2. **Template Specified**
3. **Closing Template Selected**
4. **Slots Validated** ← heading, body (contact info), optional: email, github, twitter, website
5. **HTML Rendered** ← Centered text, larger font, styled contact links

**Section Title Template:**
1. **User creates slide with `template: section-title`**
   ```markdown
   ---
   template: section-title
   ---

   ## Part 2: Implementation

   Deep dive into architecture
   ```

2. **Template Specified**
3. **Section Title Template Selected**
4. **Slots Validated** ← heading (large), optional subtitle
5. **HTML Rendered** ← Full-bleed layout, very large heading, minimal decoration

**Questions:**
- Q: How many built-in templates total?
  - A: 6 templates (title, content, two-column, **diagram, closing, section-title**)
- Q: Template-specific validation rules?
  - A: Yes - diagram template REQUIRES at least one Mermaid block
- Q: Custom template support?
  - A: **Not in v2.0.0**. Future feature (v2.1+)

**Domain Model:**
- Extend Template enum: `case Diagram, Closing, SectionTitle`
- Template-specific validation in Slide.scala
- New HTML templates in HTMLRenderer

---

### Feature 3: Configuration Management

**User Actions → Domain Events:**

**Global Configuration (~/.mdslides/config.json):**
1. **User creates global config file**
   ```json
   {
     "defaults": {
       "theme": "corporate",
       "copyImages": true,
       "skipAccessibility": false
     },
     "author": {
       "name": "Tony Moores",
       "email": "tony@tjmsolutions.com"
     },
     "paths": {
       "themesDir": "~/mdslides-themes"
     }
   }
   ```

2. **MDSlides Starts** ← CLI invoked
3. **Global Config Loaded** ← Read ~/.mdslides/config.json
4. **Defaults Applied** ← theme, copyImages, skipAccessibility set
5. **Author Info Cached** ← Used for auto-fill in title slides

**Project Configuration (.mdslides/config.json):**
1. **User creates project config in deck directory**
   ```json
   {
     "theme": "retisio",
     "copyImages": false,
     "skipAccessibility": true,
     "outputDir": "dist"
   }
   ```

2. **Project Config Loaded** ← Read .mdslides/config.json in deck directory
3. **Project Config Merged** ← Overrides global config
4. **CLI Arguments Applied** ← Final override (CLI > project > global)

**Configuration Precedence:**
```
CLI Arguments (highest)
   ↓
Project Config (.mdslides/config.json)
   ↓
Global Config (~/.mdslides/config.json)
   ↓
Hard-coded Defaults (lowest)
```

**Questions:**
- Q: Configuration validation?
  - A: Yes - JSON schema validation, reject invalid configs with clear error messages
- Q: Configuration discovery?
  - A: Search upward from current directory until .mdslides/config.json found or reach home
- Q: Which settings are configurable?
  - A: theme, copyImages, skipAccessibility, accessibilityReportPath, outputDir, themesDir, author (name, email)
- Q: Environment variable overrides?
  - A: **Not in v2.0.0**. Future feature.

**Domain Model:**
- New aggregate: `Configuration(global: GlobalConfig, project: Option[ProjectConfig], cli: CLIArguments)`
- ConfigLoader service in infrastructure
- ConfigMerger pure function in domain
- Validation via JSON schema (io.circe + schema lib)

---

### Feature 4: Custom Theme Generator

**User Actions → Domain Events:**

1. **User runs `mdslides generate-theme my-theme`**
   ```bash
   mdslides generate-theme corporate-blue --base light
   ```

2. **Theme Generation Requested**
3. **Base Theme Selected** ← Default: light, or --base flag
4. **Theme Copied** ← Copy base theme to themes/corporate-blue/
5. **theme.json Created** ← Scaffold with base theme colors
6. **Guide Displayed** ← Instructions for customization

3. **User edits theme.json**
   - Modify colors, fonts, spacing
   - Change background images

4. **Theme Saved**
5. **Theme Validated** ← Check JSON schema, validate colors
6. **Accessibility Checked** ← Automatic WCAG 2.1 AA validation
7. **Warnings Displayed** ← If low contrast, suggest alternatives

**Interactive Mode (Future):**
1. **User runs `mdslides generate-theme --interactive`**
2. **Color Picker Launched** ← CLI prompts for colors
3. **Live Preview Shown** ← Render sample slides with theme
4. **Contrast Validated** ← Real-time WCAG AA checks
5. **Theme Saved** ← When user confirms

**Questions:**
- Q: Interactive mode in v2.0.0?
  - A: **No**. Just basic scaffolding. Interactive mode in v2.1+
- Q: Color suggestions?
  - A: **No**. User manually edits JSON. Could add AI suggestions in v2.2+
- Q: Preview before saving?
  - A: **Yes** - `mdslides preview my-theme` renders example deck
- Q: Theme marketplace/sharing?
  - A: **Not in v2.0.0**. Future feature (v2.3+)

**Domain Model:**
- ThemeGenerator service in infrastructure
- ThemeScaffolder (copies base theme, creates directory structure)
- ThemeValidator (existing, reused)
- New CLI command: `generate-theme <name> [--base THEME]`

---

## Commands (Blue Stickies)

**Existing Commands:**
- `render DECK_NAME [options]` ← Main command
- `render -i INPUT -o OUTPUT [options]` ← Explicit form

**New Commands (v2.0.0):**
- `generate-theme <name> [--base THEME]` ← Create new theme from base
- `preview <theme-name>` ← Render example deck with theme (optional)
- `validate-config [PATH]` ← Check config.json validity (optional)
- `validate-theme <theme-dir>` ← Check theme.json + WCAG AA (optional)

---

## Queries (Green Stickies)

**New Queries:**
- "What themes are available?" → `list-themes` command
- "What templates are available?" → Built-in: title, content, two-column, diagram, closing, section-title
- "What configuration options exist?" → `--help` extended documentation
- "Does my theme pass accessibility?" → `validate-theme` command

---

## Policies (Lilac Stickies)

**Mermaid Diagrams:**
- **POL-008**: Client-side rendering only (no server-side SVG generation)
- **POL-009**: Graceful degradation if CDN unavailable (show source code)
- **POL-010**: Alt text REQUIRED for accessibility (front matter or comment)

**Templates:**
- **POL-011**: Diagram template MUST contain at least one Mermaid diagram
- **POL-012**: Template validation MUST happen before rendering
- **POL-013**: Invalid template → Clear error message with suggested fix

**Configuration:**
- **POL-014**: Configuration merging: CLI > project > global > defaults
- **POL-015**: Invalid config → Reject with schema validation errors
- **POL-016**: Missing config files → Use defaults (no error)

**Theme Generator:**
- **POL-017**: Generated themes MUST pass WCAG 2.1 AA validation
- **POL-018**: Theme names MUST be filesystem-safe (alphanumeric + hyphens)
- **POL-019**: Base theme MUST exist (built-in or in themes/ directory)

---

## External Systems (Pink Stickies)

**CDN Dependencies (NEW):**
- **Mermaid.js CDN**: https://cdn.jsdelivr.net/npm/mermaid@latest/dist/mermaid.min.js
  - Client-side diagram rendering
  - Fallback: Show source code if CDN unavailable

**File System:**
- Global config: `~/.mdslides/config.json`
- Project config: `.mdslides/config.json` (search upward from current directory)
- Themes directory: `./themes/` or configured via config.json
- Generated themes: `./themes/<theme-name>/theme.json`

**JSON Schema Libraries:**
- io.circe for JSON parsing
- circe-schema (or similar) for JSON schema validation

---

## Aggregates & Bounded Contexts

**New Aggregates:**
1. **MermaidDiagram**
   - source: String (Mermaid code)
   - diagramType: String (flowchart, sequence, gantt, etc.)
   - altText: Option[String] (accessibility)

2. **Configuration**
   - globalConfig: Option[GlobalConfig]
   - projectConfig: Option[ProjectConfig]
   - cliOverrides: CLIArguments
   - merged: MergedConfig (computed)

3. **GlobalConfig**
   - defaults: DefaultSettings (theme, copyImages, skipAccessibility)
   - author: Author (name, email)
   - paths: Paths (themesDir)

4. **ProjectConfig**
   - theme: Option[String]
   - copyImages: Option[Boolean]
   - skipAccessibility: Option[Boolean]
   - outputDir: Option[String]

**Extended Aggregates:**
1. **ContentElement** (sealed trait)
   - ParagraphElement
   - CodeBlockElement
   - ImageElement
   - ListElement
   - **DiagramElement** ← NEW

2. **Template** (enum)
   - Title, Content, TwoColumn
   - **Diagram** ← NEW
   - **Closing** ← NEW
   - **SectionTitle** ← NEW

**Bounded Contexts:**
- **Presentation Context** ← Core: slides, templates, content
- **Theme Context** ← Themes, colors, fonts, WCAG validation
- **Configuration Context** ← NEW: Config loading, merging, validation
- **Diagram Context** ← NEW: Mermaid integration, client-side rendering

---

## Hotspots & Risks

**Technical Risks:**
1. ⚠️ **Mermaid.js CDN dependency**
   - Risk: CDN outage breaks diagram rendering
   - Mitigation: Graceful degradation (show source code)
   - Alternative: Bundle Mermaid.js locally (increases JAR size ~500KB)

2. ⚠️ **Configuration file discovery**
   - Risk: Complex directory traversal logic
   - Mitigation: Simple upward search until `.mdslides/` found or home reached

3. ⚠️ **JSON schema validation**
   - Risk: No good Scala JSON schema library
   - Mitigation: Manual validation with clear error messages OR use circe-json-schema

**Domain Complexity:**
1. 🔥 **Template-specific validation**
   - Diagram template MUST have Mermaid diagram
   - Closing template MUST have contact info
   - Section title template just needs heading
   - Risk: Validation logic scattered across templates
   - Mitigation: Template trait with `validate()` method

2. 🔥 **Configuration merging**
   - 4 layers: CLI > project > global > defaults
   - Risk: Complex precedence rules, hard to debug
   - Mitigation: Pure function with clear merge strategy, unit tests

**User Experience:**
1. ❓ **Mermaid syntax errors**
   - User writes invalid Mermaid → Browser shows error
   - Risk: Confusing error messages from Mermaid.js
   - Mitigation: Add note in documentation: "Check browser console for Mermaid errors"

2. ❓ **Theme generator expectations**
   - Users might expect WYSIWYG editor
   - Reality: Just JSON file scaffolding
   - Mitigation: Clear documentation, future roadmap for interactive mode

---

## Open Questions

1. **Mermaid Versioning**: Pin to specific Mermaid.js version or use @latest?
   - **Decision Needed**: Pin for stability OR latest for new features
   - **Recommendation**: Pin to major version (e.g., mermaid@10) for stability

2. **Configuration Schema**: Which JSON schema library to use?
   - **Options**: circe-json-schema, manual validation, jsonschema4s
   - **Decision Needed**: Evaluate libraries

3. **Theme Preview**: Render full example deck or just single slide?
   - **Decision Needed**: Full deck (better preview) OR single slide (faster)
   - **Recommendation**: Full deck with all templates showcased

4. **Diagram Template**: Support multiple diagrams per slide?
   - **Decision Needed**: Yes (array of diagrams) OR no (single diagram only)
   - **Recommendation**: Single diagram for v2.0.0, multiple in v2.1+

5. **Configuration Validation**: Fail fast or collect all errors?
   - **Decision Needed**: Fail on first error OR collect all validation errors
   - **Recommendation**: Collect all errors (better UX)

---

## Next Steps

1. **Three Amigos Session** ← Define acceptance criteria for each feature
2. **Example Mapping** ← 50+ concrete scenarios for all features
3. **TDD Implementation** ← RED-GREEN-REFACTOR for each aggregate
4. **Integration Testing** ← End-to-end tests for all workflows

**Priority Order:**
1. Configuration Management (foundation for other features)
2. Mermaid Diagram Support (high user value)
3. Additional Templates (diagram, closing, section-title)
4. Theme Generator (nice-to-have, can be v2.1)

---

## Success Metrics

- ✅ All Mermaid diagram types supported (flowchart, sequence, gantt, etc.)
- ✅ 3 new templates added with validation
- ✅ Configuration system with 4-layer precedence working correctly
- ✅ Theme generator creates valid, accessible themes
- ✅ 100% test coverage maintained
- ✅ Zero regressions in existing features
- ✅ Documentation updated for all new features

**Target Test Count**: ~450 tests (from current 322)
- +50 tests for Mermaid diagram support
- +30 tests for templates
- +40 tests for configuration management
- +8 tests for theme generator

---

**Session Duration**: 1 hour
**Follow-up**: Three Amigos session (next)
