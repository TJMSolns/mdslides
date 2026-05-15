# Example Mapping: MDSlides v2.0.0 Major Release

**Date**: 2025-12-27
**Features**: Mermaid Diagrams, Templates, Configuration, Theme Generator
**Status**: IN PROGRESS
**Test Scenarios**: 60+ concrete examples

---

## Feature 1: Mermaid Diagram Support

### Rule 1: Mermaid Code Fence Recognition

**Scenario 1**: Parser recognizes ```mermaid code fence
```
Given: Markdown with ```mermaid\ngraph TD\n  A-->B\n```
When: Parser processes markdown
Then: Mermaid code block extracted
And: Source = "graph TD\n  A-->B"
```

**Scenario 2**: Parser ignores regular code fences
```
Given: Markdown with ```scala\nval x = 1\n```
When: Parser processes markdown
Then: Regular code block created (not Mermaid diagram)
```

**Scenario 3**: Multiple Mermaid blocks in one slide
```
Given: Slide with two ```mermaid fences
When: Parser processes markdown
Then: Two separate Mermaid diagrams extracted
And: Each has its own source
```

---

### Rule 2: Diagram Type Detection

**Scenario 4**: Flowchart diagram type detected
```
Given: Mermaid source starting with "graph TD" or "flowchart TD"
When: Diagram type is detected
Then: diagramType = "flowchart"
```

**Scenario 5**: Sequence diagram type detected
```
Given: Mermaid source starting with "sequenceDiagram"
When: Diagram type is detected
Then: diagramType = "sequence"
```

**Scenario 6**: All diagram types supported
```
Given: Mermaid source for each type:
  - flowchart: "graph TD"
  - sequence: "sequenceDiagram"
  - class: "classDiagram"
  - state: "stateDiagram-v2"
  - gantt: "gantt"
  - pie: "pie"
  - er: "erDiagram"
  - journey: "journey"
When: Diagram type is detected for each
Then: Correct type is identified
```

---

### Rule 3: Client-Side Rendering

**Scenario 7**: Mermaid.js CDN script included in HTML
```
Given: Slide with Mermaid diagram
When: HTML is rendered
Then: <script src="https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.min.js"></script> is in <head>
And: mermaid.initialize() is called after DOM load
```

**Scenario 8**: Diagram rendered in <div class="mermaid">
```
Given: Mermaid diagram with source "graph TD\n  A-->B"
When: HTML is rendered
Then: <div class="mermaid">graph TD\n  A-->B</div> is in HTML
```

**Scenario 9**: Theme-aware Mermaid rendering
```
Given: Slide with dark theme
When: HTML is rendered
Then: mermaid.initialize({theme: 'dark'}) is called
```

**Scenario 10**: Light theme uses default Mermaid theme
```
Given: Slide with light theme
When: HTML is rendered
Then: mermaid.initialize({theme: 'default'}) is called
```

---

### Rule 4: Graceful Degradation

**Scenario 11**: CDN unavailable fallback
```
Given: Mermaid.js CDN script fails to load
When: Diagram renders in browser
Then: Mermaid source code is shown in <pre> tag
And: Warning message displayed: "Diagram rendering unavailable..."
```

**Scenario 12**: Fallback preserves source code formatting
```
Given: Mermaid source with indentation and newlines
When: CDN fallback triggers
Then: Source code is displayed with original formatting in <pre>
And: Syntax highlighting applied (if highlight.js available)
```

---

### Rule 5: Alt Text Validation

**Scenario 13**: Mermaid diagram with alt text passes accessibility
```
Given: Slide with ```mermaid and front matter: diagramAlt: "User flow"
When: Accessibility validation runs
Then: No warnings for diagram alt text
```

**Scenario 14**: Mermaid diagram without alt text generates warning
```
Given: Slide with ```mermaid but no diagramAlt in front matter
When: Accessibility validation runs
Then: Warning: "Mermaid diagram missing alt text (diagramAlt field)"
And: WCAG criterion: "1.1.1"
```

**Scenario 15**: Multiple diagrams require multiple alt texts
```
Given: Slide with 2 Mermaid diagrams
When: Accessibility validation runs
Then: Warning if EITHER diagram lacks alt text
```

---

## Feature 2: Additional Templates

### Rule 6: Diagram Template

**Scenario 16**: Diagram template selection
```
Given: Front matter with template: diagram
When: Template is resolved
Then: Diagram template is selected
```

**Scenario 17**: Diagram template requires Mermaid diagram
```
Given: Slide with template: diagram but no ```mermaid fence
When: Validation runs
Then: Error: "Diagram template requires at least one Mermaid diagram"
```

**Scenario 18**: Diagram template with caption
```
Given: Diagram template with caption: "Architecture Overview"
When: HTML is rendered
Then: Caption appears below diagram
And: Caption is styled with smaller font (1rem) and centered
```

**Scenario 19**: Diagram template without caption
```
Given: Diagram template with no caption field
When: HTML is rendered
Then: No caption element is shown (optional field)
```

**Scenario 20**: Diagram template centered layout
```
Given: Diagram template slide
When: HTML is rendered
Then: Diagram is centered both horizontally and vertically
And: Margins are minimized for maximum diagram space
```

---

### Rule 7: Closing Template

**Scenario 21**: Closing template selection
```
Given: Front matter with template: closing
When: Template is resolved
Then: Closing template is selected
```

**Scenario 22**: Closing template large heading
```
Given: Closing template with heading: "Thank You"
When: HTML is rendered
Then: Heading font size is 3rem
And: Heading is centered
```

**Scenario 23**: Closing template with contact links
```
Given: Closing template with body containing email and URLs
When: HTML is rendered
Then: Email addresses are mailto: links
And: URLs are styled as prominent links
```

**Scenario 24**: Closing template minimal validation
```
Given: Closing template with only heading (no body)
When: Validation runs
Then: No errors (body is optional)
```

---

### Rule 8: Section Title Template

**Scenario 25**: Section title template selection
```
Given: Front matter with template: section-title
When: Template is resolved
Then: Section title template is selected
```

**Scenario 26**: Section title very large heading
```
Given: Section title template with heading: "Part 2"
When: HTML is rendered
Then: Heading font size is 4rem
And: Heading is vertically centered
```

**Scenario 27**: Section title with subtitle
```
Given: Section title with body: "Deep Dive into Architecture"
When: HTML is rendered
Then: Body appears as subtitle below heading
And: Subtitle font size is 1.5rem
```

**Scenario 28**: Section title without subtitle
```
Given: Section title with no body
When: HTML is rendered
Then: No subtitle element is shown (body is optional)
```

**Scenario 29**: Section title full-bleed background
```
Given: Section title template
When: HTML is rendered
Then: Background fills entire slide (no margins)
And: Content is centered in available space
```

---

### Rule 9: Template Backward Compatibility

**Scenario 30**: Slide without template field defaults to content
```
Given: Slide with no template: field in front matter
When: Template is resolved
Then: Content template is selected (default behavior)
```

**Scenario 31**: Invalid template name shows error
```
Given: Front matter with template: invalid-name
When: Validation runs
Then: Error: "Unknown template 'invalid-name'. Valid templates: title, content, two-column, diagram, closing, section-title"
```

---

## Feature 3: Configuration Management

### Rule 10: Global Config Loading

**Scenario 32**: Global config file loaded from ~/.mdslides/config.json
```
Given: File exists at ~/.mdslides/config.json with {"defaults": {"theme": "dark"}}
When: MDSlides starts
Then: Global config is loaded
And: Default theme is "dark"
```

**Scenario 33**: Missing global config is OK
```
Given: No file at ~/.mdslides/config.json
When: MDSlides starts
Then: No error is shown
And: Hard-coded defaults are used (theme: "light")
```

**Scenario 34**: Invalid JSON in global config fails
```
Given: ~/.mdslides/config.json with malformed JSON
When: MDSlides starts
Then: Error: "Invalid global config: JSON parse error at line X"
```

---

### Rule 11: Project Config Loading

**Scenario 35**: Project config loaded from .mdslides/config.json
```
Given: File at .mdslides/config.json with {"theme": "corporate"}
When: MDSlides runs from that directory
Then: Project config is loaded
And: Theme is "corporate"
```

**Scenario 36**: Project config discovery searches upward
```
Given: .mdslides/config.json in parent directory
And: MDSlides runs from subdirectory
When: Config discovery runs
Then: Parent .mdslides/config.json is found and loaded
```

**Scenario 37**: Project config search stops at home directory
```
Given: No .mdslides/ directory found up to home directory
When: Config discovery runs
Then: Search stops at home (doesn't search above ~/)
```

---

### Rule 12: Configuration Precedence

**Scenario 38**: CLI overrides project config
```
Given: Project config: {"theme": "dark"}
And: CLI argument: --theme light
When: MDSlides runs
Then: Theme is "light" (CLI wins)
```

**Scenario 39**: Project overrides global config
```
Given: Global config: {"defaults": {"theme": "light"}}
And: Project config: {"theme": "dark"}
When: MDSlides runs (no CLI theme argument)
Then: Theme is "dark" (project wins)
```

**Scenario 40**: Global overrides hard-coded defaults
```
Given: Global config: {"defaults": {"copyImages": false}}
And: No project config
And: No CLI --no-copy-images flag
When: MDSlides runs
Then: copyImages is false (global wins over hard-coded default of true)
```

**Scenario 41**: Full precedence chain
```
Given: Hard-coded default: theme = "light"
And: Global config: {"defaults": {"theme": "dark"}}
And: Project config: {"theme": "corporate"}
And: CLI argument: --theme retisio
When: MDSlides runs
Then: Theme is "retisio" (CLI > project > global > default)
```

---

### Rule 13: Configuration Validation

**Scenario 42**: Unknown field in config rejected
```
Given: Config with {"unknownField": "value"}
When: Config is loaded
Then: Error: "Unknown config field 'unknownField'. Valid fields: theme, copyImages, skipAccessibility, ..."
```

**Scenario 43**: Wrong type in config rejected
```
Given: Config with {"theme": 123} (number instead of string)
When: Config is loaded
Then: Error: "Config field 'theme' must be string, got number"
```

**Scenario 44**: Invalid theme name accepted (validated later)
```
Given: Config with {"theme": "nonexistent"}
When: Config is loaded
Then: Config loads successfully (theme existence checked at render time)
```

---

### Rule 14: Supported Configuration Fields

**Scenario 45**: All config fields supported
```
Given: Config with all fields:
  {
    "theme": "dark",
    "copyImages": false,
    "skipAccessibility": true,
    "accessibilityReportPath": "report.json",
    "outputDir": "dist",
    "themesDir": "~/my-themes",
    "author": {"name": "Tony", "email": "tony@example.com"}
  }
When: Config is loaded
Then: All fields are parsed and applied
```

**Scenario 46**: Project config can't set global-only fields
```
Given: Project config with {"themesDir": "~/themes"}
When: Config is loaded
Then: Error: "Field 'themesDir' only allowed in global config (~/.mdslides/config.json)"
```

**Scenario 47**: Global config can't set project-only fields
```
Given: Global config with {"outputDir": "dist"}
When: Config is loaded
Then: Error: "Field 'outputDir' only allowed in project config (.mdslides/config.json)"
```

---

## Feature 4: Custom Theme Generator

### Rule 15: Theme Generation

**Scenario 48**: Generate theme from light base
```
Given: Command: mdslides generate-theme my-theme
When: Command executes
Then: Directory created: themes/my-theme/
And: File created: themes/my-theme/theme.json
And: theme.json contains light theme colors as base
```

**Scenario 49**: Generate theme from dark base
```
Given: Command: mdslides generate-theme my-theme --base dark
When: Command executes
Then: themes/my-theme/theme.json contains dark theme colors
```

**Scenario 50**: Generate theme from custom base
```
Given: Command: mdslides generate-theme new-theme --base themes/custom-theme
And: themes/custom-theme/theme.json exists
When: Command executes
Then: themes/new-theme/theme.json is copy of themes/custom-theme/theme.json
And: name field is updated to "new-theme"
```

---

### Rule 16: Theme Name Validation

**Scenario 51**: Valid theme name accepted
```
Given: Command: mdslides generate-theme my-awesome-theme-2024
When: Validation runs
Then: Name is accepted (alphanumeric + hyphens OK)
```

**Scenario 52**: Theme name with spaces rejected
```
Given: Command: mdslides generate-theme "my theme"
When: Validation runs
Then: Error: "Theme name must be alphanumeric with hyphens only (no spaces)"
```

**Scenario 53**: Theme name with slashes rejected
```
Given: Command: mdslides generate-theme ../malicious
When: Validation runs
Then: Error: "Theme name must be alphanumeric with hyphens only"
```

---

### Rule 17: Duplicate Detection

**Scenario 54**: Existing theme not overwritten
```
Given: Directory themes/my-theme/ already exists
And: Command: mdslides generate-theme my-theme
When: Command executes
Then: Error: "Theme 'my-theme' already exists. Choose a different name or delete the existing theme."
And: Existing theme is NOT modified
```

---

### Rule 18: Theme Validation

**Scenario 55**: Generated theme validated for WCAG
```
Given: Command: mdslides generate-theme my-theme
When: Theme is generated
Then: WCAG 2.1 AA validation runs automatically
And: Contrast warnings are shown if any fail
```

**Scenario 56**: Generated theme with failing contrast shows warnings
```
Given: User edits generated theme.json to have low contrast
When: Validation runs
Then: Warning: "Low contrast ratio for text: X.XX:1 (requires 4.5:1)"
And: Theme is still created (warnings don't block creation)
```

---

### Rule 19: Usage Instructions

**Scenario 57**: Success message shows next steps
```
Given: Command: mdslides generate-theme my-theme completes successfully
When: Command finishes
Then: Message shown:
  "✓ Theme 'my-theme' created at themes/my-theme/theme.json

  Next steps:
  1. Edit themes/my-theme/theme.json to customize colors
  2. Run: mdslides render deck.md --theme my-theme
  3. Check accessibility: mdslides validate-theme themes/my-theme"
```

---

## Integration Scenarios

### Scenario 58: Mermaid + Diagram Template + Config
```
Given: Project config: {"theme": "dark"}
And: Slide with template: diagram and Mermaid flowchart
When: Rendered
Then: Dark theme applied
And: Diagram template layout used
And: Mermaid rendered with dark theme
```

### Scenario 59: Multiple templates in one presentation
```
Given: Deck with slides using all 6 templates
When: Rendered
Then: Each slide uses its specified template
And: Validation passes for each template type
```

### Scenario 60: Config + Theme Generator workflow
```
Given: Global config: {"themesDir": "~/my-themes"}
And: Command: mdslides generate-theme corporate-blue
When: Theme is generated
Then: Theme created at ~/my-themes/corporate-blue/theme.json
And: Can be used via: --theme corporate-blue
```

---

## Error Scenarios

### Scenario 61: Diagram template without Mermaid
```
Given: Slide with template: diagram but no ```mermaid fence
When: Validation runs
Then: Validation fails with clear error
And: Error message: "Diagram template requires at least one Mermaid diagram"
```

### Scenario 62: Invalid config stops rendering
```
Given: Project config with JSON syntax error
When: mdslides render runs
Then: Rendering aborted with config error
And: Error shows line number and syntax issue
```

### Scenario 63: Nonexistent base theme for generation
```
Given: Command: mdslides generate-theme my-theme --base nonexistent
When: Command executes
Then: Error: "Base theme 'nonexistent' not found. Valid bases: light, dark, corporate, or path to theme.json"
```

---

## Acceptance Criteria Traceability

### Mermaid Diagrams (14 scenarios)
- ✅ Scenarios 1-3: Code fence recognition
- ✅ Scenarios 4-6: Diagram type detection
- ✅ Scenarios 7-10: Client-side rendering
- ✅ Scenarios 11-12: Graceful degradation
- ✅ Scenarios 13-15: Alt text validation

### Templates (14 scenarios)
- ✅ Scenarios 16-20: Diagram template
- ✅ Scenarios 21-24: Closing template
- ✅ Scenarios 25-29: Section title template
- ✅ Scenarios 30-31: Backward compatibility

### Configuration (16 scenarios)
- ✅ Scenarios 32-34: Global config
- ✅ Scenarios 35-37: Project config
- ✅ Scenarios 38-41: Precedence
- ✅ Scenarios 42-44: Validation
- ✅ Scenarios 45-47: Supported fields

### Theme Generator (10 scenarios)
- ✅ Scenarios 48-50: Generation
- ✅ Scenarios 51-53: Name validation
- ✅ Scenario 54: Duplicate detection
- ✅ Scenarios 55-56: Validation
- ✅ Scenario 57: Instructions

### Integration (3 scenarios)
- ✅ Scenarios 58-60: Cross-feature integration

### Error Handling (3 scenarios)
- ✅ Scenarios 61-63: Clear error messages

**Total**: 60 concrete test scenarios

---

## Test Implementation Plan

### Phase 1: Domain Layer (TDD)
- MermaidDiagram aggregate (source, type, altText)
- Configuration aggregate (global, project, CLI, merged)
- Template validation (diagram requires Mermaid, etc.)
- ConfigMerger pure function (precedence logic)

**Tests**: ~150 tests
- Mermaid: 40 tests
- Config: 60 tests
- Templates: 40 tests
- Theme Generator: 10 tests

### Phase 2: Infrastructure Layer
- FlexmarkAdapter: Mermaid fence parsing
- HTMLRenderer: Mermaid.js CDN integration, template rendering
- ConfigLoader: JSON file reading, directory search
- ThemeGenerator: File scaffolding

**Tests**: ~100 tests
- Markdown parsing: 30 tests
- HTML rendering: 40 tests
- Config loading: 20 tests
- Theme generator: 10 tests

### Phase 3: CLI Layer
- generate-theme command
- Config file discovery and merging
- Validation error display

**Tests**: ~25 tests

### Phase 4: Integration Tests
- End-to-end workflows
- Browser validation (Mermaid rendering)
- Config precedence integration

**Tests**: ~25 tests

**Target Total**: ~450 tests (current: 322)

---

## Next Steps

1. **TDD Configuration Management** (foundation feature - do first)
2. **TDD Mermaid Support** (high user value)
3. **TDD Additional Templates** (extends Mermaid capability)
4. **TDD Theme Generator** (nice-to-have, can be v2.1 if time-constrained)

**Ready for Implementation**: All scenarios defined, acceptance criteria clear, TDD can begin!
