# Three Amigos: MDSlides v2.0.0 Major Release

**Date**: 2025-12-27
**Participants**: Product Owner (PO), Developer (Dev), Tester (QA)
**Facilitator**: AI Assistant (Claude)
**Status**: IN PROGRESS

---

## Feature 1: Mermaid Diagram Support (US-022)

### User Story
**As a** technical presenter
**I want** to embed Mermaid diagrams in my slides
**So that** I can show flowcharts, sequence diagrams, and architecture diagrams without external tools

### Acceptance Criteria

**AC1: Mermaid Code Fence Recognition**
- GIVEN a markdown file with ```mermaid code fence
- WHEN the parser processes the markdown
- THEN the Mermaid code block is extracted as a diagram

**AC2: Client-Side Rendering**
- GIVEN a slide with Mermaid diagram
- WHEN the HTML is rendered
- THEN the Mermaid.js CDN script is included
- AND the diagram renders in the browser via mermaid.initialize()

**AC3: Graceful Degradation**
- GIVEN the Mermaid.js CDN is unavailable
- WHEN the slide loads in browser
- THEN the Mermaid source code is displayed in a `<pre>` tag
- AND a warning message is shown: "Diagram rendering unavailable. Install Mermaid.js locally or check internet connection."

**AC4: Diagram Type Detection**
- GIVEN Mermaid code for different diagram types
- WHEN the parser extracts the diagram
- THEN the diagram type is detected (flowchart, sequence, gantt, class, state, pie, etc.)
- AND stored in the domain model

**AC5: Alt Text Requirement**
- GIVEN a slide with Mermaid diagram
- WHEN accessibility validation runs
- THEN a warning is shown if no alt text is provided
- AND alt text can be specified via front matter: `diagramAlt: "Description"`

**AC6: Multiple Diagram Types Supported**
- GIVEN Mermaid code for any diagram type
- WHEN rendered
- THEN these diagram types work: flowchart, sequence, class, state, gantt, pie, ER, user journey

### Rules

1. **Client-side only**: No server-side SVG generation (keeps JAR small, no node.js dependency)
2. **CDN version**: Pin to Mermaid.js v10.x for stability (not @latest)
3. **Alt text warning**: Accessibility validation MUST warn if diagram lacks alt text
4. **One diagram per fence**: Each ```mermaid fence is a separate diagram
5. **No syntax validation**: Don't validate Mermaid syntax in Scala (Mermaid.js handles errors)

### Questions

**Q1: What if user wants dark mode diagrams?**
- A: Mermaid.js supports theme configuration via mermaid.initialize({theme: 'dark'})
- Decision: Match slide theme (light theme → default Mermaid theme, dark theme → dark Mermaid theme)

**Q2: How to specify alt text for diagrams?**
- A: Front matter option: `diagramAlt: "Flow showing user authentication"`
- Alternative: Mermaid comment syntax `%%{alt: "Description"}%%` (harder to parse)
- Decision: Use front matter for v2.0.0 (simpler, consistent with other metadata)

**Q3: What if slide has both Mermaid diagram AND regular code blocks?**
- A: Both should work. Mermaid fence becomes diagram, other fences become syntax-highlighted code
- Decision: Parser must distinguish ```mermaid from ```scala, ```python, etc.

**Q4: Fallback when CDN fails?**
- A: Show raw source code in `<pre>` with warning
- Decision: Good enough for v2.0.0. Future: option to bundle Mermaid.js locally

---

## Feature 2: Additional Slide Templates

### User Story 2A: Diagram Template
**As a** technical presenter
**I want** a template optimized for displaying diagrams
**So that** my diagrams are centered and easy to see

### Acceptance Criteria (Diagram Template)

**AC1: Template Selection**
- GIVEN a slide with `template: diagram` in front matter
- WHEN the slide is rendered
- THEN the diagram template layout is used

**AC2: Centered Diagram Layout**
- GIVEN a diagram template slide
- WHEN rendered
- THEN the diagram is centered horizontally and vertically
- AND the diagram has maximum space (minimal margins)

**AC3: Caption Support**
- GIVEN a diagram template with `caption: "System Architecture"` in front matter
- WHEN rendered
- THEN the caption appears below the diagram in smaller text

**AC4: Diagram Required**
- GIVEN a diagram template slide WITHOUT a Mermaid diagram
- WHEN validated
- THEN an error is shown: "Diagram template requires at least one Mermaid diagram"

---

### User Story 2B: Closing Template
**As a** presenter
**I want** a closing slide template
**So that** I can thank the audience and share contact information

### Acceptance Criteria (Closing Template)

**AC1: Template Selection**
- GIVEN a slide with `template: closing` in front matter
- WHEN rendered
- THEN the closing template layout is used

**AC2: Centered Layout**
- GIVEN a closing template slide
- WHEN rendered
- THEN the heading is centered and large (3rem font)
- AND the body text is centered

**AC3: Contact Links Styling**
- GIVEN contact info with URLs (email, GitHub, Twitter)
- WHEN rendered
- THEN URLs are styled as prominent links
- AND email addresses are clickable mailto: links

**AC4: Optional Fields**
- GIVEN a closing slide with only heading and body
- WHEN rendered
- THEN the slide works without errors (contact info is optional)

---

### User Story 2C: Section Title Template
**As a** presenter giving a long presentation
**I want** section divider slides
**So that** I can clearly separate major sections

### Acceptance Criteria (Section Title Template)

**AC1: Template Selection**
- GIVEN a slide with `template: section-title` in front matter
- WHEN rendered
- THEN the section title template layout is used

**AC2: Full-Bleed Layout**
- GIVEN a section title slide
- WHEN rendered
- THEN the heading is very large (4rem font)
- AND the heading is vertically centered
- AND the background fills the entire slide (no margins)

**AC3: Optional Subtitle**
- GIVEN a section title with body text
- WHEN rendered
- THEN the body text appears as a subtitle below the heading
- AND the subtitle is smaller (1.5rem font)

**AC4: Minimal Validation**
- GIVEN a section title template slide
- WHEN validated
- THEN only the heading is required (body is optional)

### Rules (All Templates)

1. **Template count**: 6 total templates (title, content, two-column, diagram, closing, section-title)
2. **Backward compatibility**: Existing decks without template field default to "content"
3. **Template-specific validation**: Each template has its own validation rules
4. **No custom templates**: User can't create custom templates in v2.0.0 (future feature)

---

## Feature 3: Configuration Management

### User Story
**As a** MDSlides user
**I want** to set default options in config files
**So that** I don't have to repeat CLI flags for every render

### Acceptance Criteria

**AC1: Global Config Loading**
- GIVEN a file at ~/.mdslides/config.json
- WHEN MDSlides starts
- THEN the global config is loaded and applied as defaults

**AC2: Project Config Loading**
- GIVEN a file at .mdslides/config.json in the deck directory (or parent)
- WHEN MDSlides starts
- THEN the project config is loaded
- AND it overrides global config settings

**AC3: CLI Override**
- GIVEN CLI arguments (--theme dark, --skip-accessibility)
- WHEN MDSlides runs
- THEN CLI arguments override project and global config

**AC4: Configuration Precedence**
- GIVEN all three config sources (CLI, project, global)
- WHEN a setting is defined in multiple places
- THEN the precedence is: CLI > project > global > hard-coded defaults

**AC5: Valid Settings**
- GIVEN a config file
- WHEN loaded
- THEN these settings are supported:
  - theme (string)
  - copyImages (boolean)
  - skipAccessibility (boolean)
  - accessibilityReportPath (string)
  - outputDir (string, project config only)
  - themesDir (string, global config only)
  - author.name (string, global config only)
  - author.email (string, global config only)

**AC6: Config Validation**
- GIVEN an invalid config file (wrong JSON, unknown field, wrong type)
- WHEN MDSlides loads config
- THEN a clear error message is shown
- AND the specific validation error is identified

**AC7: Missing Config Files**
- GIVEN no config files exist
- WHEN MDSlides starts
- THEN hard-coded defaults are used
- AND no error is shown (missing config is OK)

**AC8: Config Discovery**
- GIVEN .mdslides/config.json in a parent directory
- WHEN MDSlides runs from a subdirectory
- THEN the config is found by searching upward
- AND the search stops at the first .mdslides/ directory or home directory

### Rules

1. **JSON only**: Config files must be valid JSON (not YAML, TOML, etc.)
2. **Schema validation**: Reject unknown fields (strict mode, helps catch typos)
3. **No environment variables**: Not in v2.0.0 (future feature)
4. **No inline config**: Can't embed config in markdown front matter
5. **Project config scoped**: outputDir only in project config (not global)
6. **Global config scoped**: themesDir, author only in global config (not project)

### Questions

**Q1: What if project config is invalid but global config is valid?**
- A: Fail with error showing project config issue. Don't fall back to global.
- Decision: Fail fast - invalid config is a blocker

**Q2: Should config be optional for all settings?**
- A: Yes, all settings have defaults (theme: "light", copyImages: true, etc.)
- Decision: Config is purely for overriding defaults

**Q3: Can user specify .mdslides/config.json path via CLI?**
- A: No, always use automatic discovery (.mdslides/ search)
- Decision: Keep it simple, avoid --config flag complexity

---

## Feature 4: Custom Theme Generator

### User Story
**As a** MDSlides user
**I want** to quickly create a new theme
**So that** I can customize colors and fonts without starting from scratch

### Acceptance Criteria

**AC1: Generate Theme Command**
- GIVEN the command `mdslides generate-theme my-theme`
- WHEN executed
- THEN a new directory `themes/my-theme/` is created
- AND a `theme.json` file is scaffolded

**AC2: Base Theme Selection**
- GIVEN the command `mdslides generate-theme my-theme --base dark`
- WHEN executed
- THEN the dark theme is copied as a starting point
- AND theme.json has dark theme colors

**AC3: Default Base Theme**
- GIVEN the command `mdslides generate-theme my-theme` (no --base)
- WHEN executed
- THEN the light theme is used as the base

**AC4: Theme Validation After Generation**
- GIVEN a generated theme
- WHEN the command completes
- THEN theme.json is validated
- AND WCAG 2.1 AA accessibility is checked
- AND warnings are shown if contrast fails

**AC5: Usage Instructions**
- GIVEN a generated theme
- WHEN the command completes
- THEN instructions are shown:
  ```
  ✓ Theme 'my-theme' created at themes/my-theme/theme.json

  Next steps:
  1. Edit themes/my-theme/theme.json to customize colors
  2. Run: mdslides render deck.md --theme my-theme
  3. Check accessibility: mdslides validate-theme themes/my-theme
  ```

**AC6: Name Validation**
- GIVEN a theme name with invalid characters (spaces, slashes, etc.)
- WHEN generate-theme runs
- THEN an error is shown: "Theme name must be alphanumeric with hyphens only"

**AC7: Duplicate Detection**
- GIVEN a theme directory that already exists
- WHEN generate-theme runs
- THEN an error is shown: "Theme 'my-theme' already exists. Choose a different name or delete the existing theme."

### Rules

1. **Filesystem-safe names**: Theme names must match regex: `^[a-zA-Z0-9-]+$`
2. **No overwrite**: Don't overwrite existing themes (protect user work)
3. **Base theme must exist**: Base theme must be built-in (light, dark, corporate) or in themes/ directory
4. **Auto-validation**: Always validate generated theme and show warnings
5. **No interactive mode**: Just scaffold files, no CLI prompts (interactive mode in v2.1+)

### Questions

**Q1: Should generate-theme create a themes/ directory if missing?**
- A: Yes, create themes/ automatically
- Decision: Good UX, user doesn't need to mkdir first

**Q2: What if user wants to base on a custom theme (not built-in)?**
- A: Support it - `mdslides generate-theme new-theme --base themes/my-custom-theme`
- Decision: --base can be built-in name OR path to theme.json

**Q3: Should we provide theme templates beyond light/dark/corporate?**
- A: No, just copy existing themes in v2.0.0
- Decision: Future feature - curated theme templates marketplace

---

## Cross-Cutting Concerns

### Accessibility
- All new templates MUST meet WCAG 2.1 AA
- Mermaid diagrams MUST have alt text (enforced via warnings)
- Configuration docs MUST explain accessibility options

### Testing
- **Mermaid**: Test CDN script inclusion, fallback rendering, diagram type detection
- **Templates**: Test each template layout, validation rules, slot requirements
- **Config**: Test precedence (CLI > project > global), validation, discovery
- **Theme Generator**: Test scaffolding, validation, name rules, duplicate detection

### Documentation
- Tutorial: How to create Mermaid diagrams
- Tutorial: How to use new templates
- Tutorial: How to set up config files
- Tutorial: How to create custom themes
- Migration guide: v1.x → v2.0.0 (breaking changes: none expected)

---

## Non-Functional Requirements

### Performance
- Config loading: <10ms for typical config files
- Theme generation: <500ms to scaffold theme
- Mermaid rendering: Client-side (no impact on build time)

### Compatibility
- Backward compatible with all v1.x presentations
- Config files optional (existing users unaffected)
- Default behavior unchanged (config just adds options)

### Error Handling
- Clear error messages for invalid config
- Actionable guidance (show line number, field name, expected type)
- No silent failures (fail fast with explanation)

---

## Out of Scope (Future Features)

### v2.1+ Features
- Interactive theme generator (color picker, live preview)
- Custom template support (user-defined templates)
- Environment variable overrides ($MDSLIDES_THEME)
- YAML/TOML config formats
- Theme marketplace/sharing

### Not Planned
- Server-side Mermaid rendering (complexity, node.js dependency)
- WYSIWYG diagram editor (Mermaid syntax is the interface)
- Git-based theme distribution (manual copy for now)

---

## Risks & Mitigations

### Risk 1: Mermaid.js CDN Dependency
- **Impact**: Diagrams don't render if CDN is down
- **Probability**: Low (jsDelivr has 99.9% uptime)
- **Mitigation**: Graceful degradation (show source code with warning)
- **Future**: Option to bundle Mermaid.js locally

### Risk 2: Config Merging Complexity
- **Impact**: Bugs in precedence logic (CLI vs project vs global)
- **Probability**: Medium (4-layer precedence is complex)
- **Mitigation**: Comprehensive unit tests, clear documentation
- **Testing**: ~40 test scenarios for all precedence combinations

### Risk 3: Template Validation Scattered
- **Impact**: Validation logic in multiple places, hard to maintain
- **Probability**: Medium (6 templates, each with different rules)
- **Mitigation**: Template trait with validate() method, centralized validation
- **Testing**: Dedicated test suite for each template

---

## Definition of Done

**Feature is DONE when:**
1. ✅ All acceptance criteria met and verified
2. ✅ Unit tests written (TDD: RED → GREEN → REFACTOR)
3. ✅ Integration tests pass (end-to-end workflows)
4. ✅ Property-based tests for complex logic (config merging, validation)
5. ✅ Accessibility validation passes (WCAG 2.1 AA)
6. ✅ Documentation updated (tutorials, examples)
7. ✅ Example deck updated (showcase new features)
8. ✅ ROADMAP.md updated with release notes
9. ✅ Zero regressions in existing features
10. ✅ Code review complete (self-review for solo dev)

---

## Next Steps

1. **Example Mapping**: Create 50+ concrete scenarios for all features
2. **TDD Implementation**: RED-GREEN-REFACTOR for each scenario
3. **Integration Testing**: End-to-end validation
4. **Documentation**: Update all docs with v2.0.0 features

**Estimated Effort**: 4-6 weeks (part-time development)
**Target Test Count**: 450+ tests (current: 322)
**Priority**: Configuration → Mermaid → Templates → Theme Generator

---

**Session Duration**: 1.5 hours
**Status**: COMPLETE
**Follow-up**: Example Mapping session
