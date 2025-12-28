# Changelog

All notable changes to MDSlides will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.3.1] - 2025-12-27

### Fixed

#### BUG-001: List Rendering Order Incorrect
- **Critical bug fix**: Ordered and unordered lists now render in source markdown order
- **Root cause**: `FormattedContent` hardcoded unordered lists before ordered lists
- **Solution**: Added new `lists: List[ListElement]` field to preserve source order
  - Created `ListElement` sealed trait with `OrderedListElement` and `UnorderedListElement` cases
  - Modified `FlexmarkAdapter` to track lists in insertion order
  - Updated `HTMLRenderer` to render from new `lists` field (falls back to old fields for backward compatibility)
- **Regression test added**: Verifies ordered lists appear before unordered when that's the source order

#### BUG-002: 'S' Key Does Not Open Speaker View
- **Critical bug fix**: Pressing 'S' or 's' now opens speaker view as documented
- **Root cause**: Missing keyboard handler in navigation JavaScript
- **Solution**: Added `case 's':` and `case 'S':` handlers in navigation.js
  - Opens speaker.html via `window.open('speaker.html', 'speaker-view', 'width=1024,height=768')`
  - Prevents default browser behavior with `e.preventDefault()`
- **Regression test added**: Verifies 'S' key handler presence in generated HTML

### Technical

#### Modified Components
- `FormattedContent.scala`: Added `ListElement` sealed trait and `lists` field
- `FlexmarkAdapter.scala`: Track lists in source order via new `lists` buffer
- `HTMLRenderer.scala`: Render lists from `lists` field with backward compatibility
- `HTMLRendererSpec.scala`: Added regression tests for both bugs

#### Test Coverage
- Added 2 regression tests (BUG-001, BUG-002)
- Total test count: 324 tests (all passing)
  - 158 domain tests
  - 166 infrastructure tests

#### Backward Compatibility
- Old `unorderedLists` and `orderedLists` fields retained (deprecated)
- Renderer falls back to old fields if `lists` field is empty
- Existing tests continue to pass without modification

### Related Governance
- Bugs documented in: `doc/internal/planning/KNOWN-ISSUES.md`
- Roadmap: `doc/internal/planning/ROADMAP.md` (v1.3.1 Sprint)

---

## [1.3.0] - 2025-12-27

### Added

#### US-019: Syntax Highlighting for Code Blocks
- **Automatic code highlighting**: Code blocks with language hints now display syntax highlighting via highlight.js v11.9.0
- **190+ languages supported**: Including Scala, Java, Python, JavaScript, TypeScript, SQL, Bash, JSON, YAML, XML, and more
- **Theme-aware highlighting**:
  - Light themes (light, corporate, retisio) → GitHub theme (clean, familiar syntax colors)
  - Dark theme → Monokai Sublime theme (high contrast for dark backgrounds)
- **CDN-based**: Uses cdnjs.cloudflare.com for highlight.js library and CSS (no local dependencies)
- **Graceful degradation**: Code blocks remain readable even if CDN unavailable
- **Preserves auto-scaling**: Code blocks >20 lines still auto-scale font size (PDR-006)

### Changed

#### Enhancements
- Code blocks now render with syntax colors appropriate to their language
- Theme system automatically maps MDSlides themes to appropriate highlight.js themes
- README updated with syntax highlighting feature (322 tests)

### Technical

#### Modified Components
- `HTMLRenderer.scala`: Added highlight.js CDN integration
  - Added CSS link for theme-specific highlight.js stylesheet
  - Added JavaScript library from CDN
  - Added `hljs.highlightAll()` initialization on DOMContentLoaded
  - Added `highlightJsTheme()` helper for theme mapping
- Existing `renderCodeBlock()` already added language classes - no changes needed

#### Test Coverage
- Added 8 automated tests for syntax highlighting infrastructure
- Total test count: 322 tests (all passing)
  - 158 domain tests
  - 164 infrastructure tests

#### Implementation Approach
- Followed Event Storming → Three Amigos → Example Mapping → TDD cycle
- No domain changes required (CodeBlock already supported language hints)
- Infrastructure-only changes (CDN inclusion, theme mapping)
- Manual verification with light and dark themes

### Related Documentation
- Event Storming: `doc/internal/planning/event-storming-syntax-highlighting.md`
- Three Amigos: `doc/internal/planning/three-amigos-syntax-highlighting.md`
- Example Mapping: `doc/internal/planning/example-mapping-syntax-highlighting.md`

---

## [1.2.0] - 2024-12-27

### Added

#### US-003.3: Nested List Support
- **Nested lists**: Support up to 3 levels of nesting for both ordered and unordered lists
- **Visual hierarchy**: Distinct bullet styles per level
  - Level 1: disc (•)
  - Level 2: circle (◦)
  - Level 3: square (▪)
- **Numbering hierarchy**: Ordered lists use different numbering schemes per level
  - Level 1: decimal (1, 2, 3...)
  - Level 2: lower-alpha (a, b, c...)
  - Level 3: lower-roman (i, ii, iii...)
- **Mixed nesting**: Ordered lists within unordered and vice versa
- **CSS styling**: 2em indentation per nesting level with automatic hierarchy

### Fixed

- **Scalatags rendering bug**: Fixed fragment combining issue where nested lists concatenated text instead of preserving HTML structure
  - Changed from `Seq(...).flatten` to `++` concatenation with varargs
  - Nested `<ul>` and `<ol>` tags now render correctly
- **Retisio theme**: Removed confusing `futureTemplates` section from theme.json
  - Templates referenced (diagram, closing, section-title) are deferred to v2.0

### Changed

#### Documentation
- Updated tutorial with nested list demonstration (17 slides total)
- Added visual styling examples for nested list hierarchy

### Technical

#### New Components
- Enhanced `ListItem` domain model with `nestedUnorderedLists` and `nestedOrderedLists`
- Added `maxNestingDepth` methods to `FormattedContent`, `UnorderedList`, `OrderedList`, and `ListItem`
- Recursive parser logic in `FlexmarkAdapter.extractListItemContent`
  - Visits children of itemNode instead of itemNode itself
  - Handles nested `BulletList` and `OrderedList` within list items
  - Explicit `Paragraph` case for text extraction

#### Test Coverage
- Added 22 automated tests (3 domain + 10 parser + 9 renderer)
- Total test count: 290 tests (all passing)
  - 150 domain tests
  - 140 infrastructure tests

#### Implementation Approach
- Followed Event Storming → Three Amigos → Example Mapping → TDD cycle
- Fixed Scalatags bug discovered during integration testing
- Verified actual HTML output matches expected structure

### Related Documentation
- Event Storming: `doc/internal/planning/event-storming-US-003.3.md`
- Three Amigos: `doc/internal/planning/three-amigos-US-003.3.md`
- Example Mapping: `doc/internal/planning/example-mapping-US-003.3.md`

---

## [1.1.0] - 2024-12-27

### Added

#### US-034: Speaker Notes Rendering / Speaker View
- **Speaker View window**: Press 'S' during presentation to open dedicated speaker view
- **Dual-pane layout**: Current slide notes + next slide preview + elapsed timer
- **Bidirectional sync**: Navigate from either window, both stay synchronized via localStorage
- **Timer**: Auto-starts on first navigation, displays MM:SS format
- **Graceful degradation**: Either window works independently if the other closes
- **Keyboard shortcuts**: All navigation keys work in speaker view (←/→/Space/Home/End/Esc)
- **Output files**:
  - `speaker.html` - Speaker view interface with embedded slide data as JSON
  - `sync.js` - Cross-window synchronization module
- **Notes display**:
  - Shows "No notes for this slide" when notes are missing
  - Preserves newlines in multi-line notes
  - HTML-escaped for security (prevents XSS)
- **Preview**: Shows heading/title of next slide, or "End of presentation" on last slide

### Changed

#### Enhancements
- Updated tutorial with speaker view documentation
- CLI now outputs 3 files: `index.html`, `speaker.html`, `sync.js`
- Enhanced success message shows both main presentation and speaker view paths

### Technical

#### New Components
- `PresentationState.scala` - Domain model for presentation runtime state (17 tests)
  - Current slide tracking, timer management, navigation helpers
- `SpeakerViewRenderer.scala` - Speaker view HTML generation (14 tests)
  - Embedded JSON slide data, HTML escaping, responsive layout
- `sync.js` - JavaScript synchronization module (manual testing)
  - localStorage-based events, fallback to postMessage, heartbeat detection

#### Test Coverage
- Added 31 automated tests (17 domain + 14 infrastructure)
- Total test count: 292 tests (all passing)

#### Implementation Approach
- Followed Event Storming → Three Amigos → Example Mapping → TDD cycle
- Pure functional domain model (no I/O in PresentationState)
- Circe JSON encoding for automatic HTML escaping
- Resource loading for sync.js via `scala.io.Source.fromResource`

### Related Documentation
- Event Storming: `doc/internal/planning/event-storming-US-034.md`
- Three Amigos: `doc/internal/planning/three-amigos-US-034.md`
- Example Mapping: `doc/internal/planning/example-mapping-US-034.md`

---

## [1.0.0] - 2024-12-27

### Added

#### US-019: Improved CLI UX ⚠️ BREAKING CHANGE
- **Simple form**: `mdslides render DECK_NAME` infers input/output paths
  - Reads `DECK_NAME.md` (or `.markdown`)
  - Creates `DECK_NAME/` directory with `index.html`
  - Copies assets to `DECK_NAME/images/` and `DECK_NAME/backgrounds/`
- **Explicit form**: `mdslides render -i INPUT -o OUTPUT` for custom paths
- **Directory output**: Self-contained presentation directories
- **Relative paths**: All assets use relative URLs for portability
- **Path resolution**: Smart input file discovery with helpful error messages

#### US-004: Speaker Notes Parsing
- **Notes field** in slide frontmatter: `notes: "Speaker talking points"`
- **Multi-line support**: `notes: ["Point 1", "Point 2", "Point 3"]`
- **Array joining**: Multi-line arrays joined with newlines
- **Domain model**: Added `notes: Option[String]` field to Slide aggregate
- **Parser integration**: Enhanced frontmatter parser for YAML-like arrays
- **Note**: Notes are parsed but NOT rendered (rendering deferred to v1.1)

### Changed

#### Breaking Changes
- **CLI syntax changed**: Old `mdslides slides.md output.html` → New `mdslides render my-preso`
- **Output structure**: Single HTML file → Directory with index.html
- **Asset organization**: Images now in `OUTPUT_DIR/images/` instead of parent directory
- **Removed Decline library**: Custom argument parser for better UX

#### Enhancements
- Updated tutorial with v1.0 CLI examples
- Added speaker notes examples to tutorial
- Improved error messages with file suggestions
- Portable presentations (entire directory can be moved/shared)

### Technical

#### New Components
- `CLIArguments.scala` - Dual-mode argument parsing (10 tests)
- `PathResolver.scala` - Input file discovery and output directory creation (9 tests)
- `AssetPathResolver.scala` - Relative path generation (5 tests)

#### Modified Components
- `Main.scala` - Completely rewritten for directory output
- `Slide.scala` - Added `notes` field
- `MarkdownParser.scala` - Enhanced frontmatter parsing with array support

#### Test Coverage
- **Total**: 261 tests passing
- **New**: 24 CLI tests, 10 speaker notes tests
- **Coverage**: Domain (127), Infrastructure (110), CLI (24)

### Documentation
- Updated [INSTALL.md](INSTALL.md) with v1.0 usage
- Updated [examples/mdslides-tutorial.md](examples/mdslides-tutorial.md)
- Created governance documents:
  - PDR-015: CLI UX Design
  - Event Storming for US-019 and US-004
  - Three Amigos session notes
  - Example Mapping for both features

## [0.4.0] - 2024-12-26

### Added
- Directory-based theme system (US-016)
- Template-specific backgrounds (US-012)
- Per-slide background images (US-011)
- List support (unordered and ordered)
- Retisio theme with 5 template backgrounds

## [0.1.0-MVP] - 2024-12-21

### Added

#### Core Features
- **Title Slide Template** (US-001)
  - Title slot (required, max 2 lines)
  - Subtitle slot (optional, max 2 lines)
  - Author slot (optional, max 80 characters)

- **Content Slide Template** (US-002)
  - Heading slot (required, max 80 characters, single line)
  - Body slot (required, max 12 lines, max 150 words)

- **SlideDeck Aggregate**
  - Supports 1-200 slides (PDR-003)
  - Unique slide ID validation
  - Deck-level structure validation

#### Infrastructure
- **MarkdownParser**
  - Parses frontmatter-based slide structure
  - Extracts template type and slot content
  - Preserves markdown formatting (not rendered in MVP)

- **HTMLRenderer**
  - Standalone HTML output with inline CSS and JavaScript
  - Keyboard navigation (Arrow keys, Space, Home, End)
  - Responsive design
  - Slide counter display
  - WCAG AA compliant colors (PDR-005)

#### CLI
- **Command-line Interface**
  - Simple syntax: `mill cli.run input.md output.html`
  - Detailed progress output
  - Error reporting with validation messages

#### Architecture
- **3-Module Structure**
  - `domain`: Pure functional domain model (no I/O)
  - `infrastructure`: Parsers, renderers, adapters
  - `cli`: Command-line interface with Cats Effect

- **Validation Pipeline** (ADR-002)
  - Structure validation (required slots, template types)
  - Content validation (line/word/character limits)
  - Comprehensive error collection

#### Testing
- **81 Tests Total**
  - 61 domain tests (29 property-based, 32 example-based)
  - 20 infrastructure tests (10 parser, 10 renderer)

- **Property-Based Testing** (ADR-009, POL-004)
  - ScalaCheck generators for domain entities
  - Invariant verification across thousands of inputs
  - Edge case coverage

#### Documentation
- **Governance Documents**
  - 9 Architecture Decision Records (ADR-001 through ADR-009)
  - 5 Product Decision Records (PDR-001 through PDR-005)
  - 4 Policy Documents (POL-001 through POL-004)

- **User Documentation**
  - README-MVP.md with usage examples
  - Installation instructions
  - Architecture diagrams

#### Technology Stack
- Scala 3.3.1 (modern functional programming)
- Mill 0.11.6 (build tool)
- Cats Core 2.10.0 (functional programming primitives)
- Cats Effect 3.5.4 (effect system)
- Scalatags 0.12.0 (type-safe HTML generation)
- Decline 2.4.1 (CLI parsing)
- MUnit 0.7.29 (testing framework)
- ScalaCheck 1.17.0 (property-based testing)

### Design Decisions

#### ADR-001: Technology Stack Selection
- Chose Scala 3 for type safety and functional programming
- Selected Cats ecosystem for effect management
- Adopted Scalatags for HTML generation

#### ADR-002: Validation Pipeline Architecture
- Two-phase validation: structure → content
- Error collection with NonEmptyList
- Fail-fast for structure, comprehensive for content

#### ADR-006: Rendering Architecture
- Standalone HTML with inline CSS/JS
- No external dependencies
- Keyboard-first navigation

#### ADR-007: Anticorruption Layer
- Flexmark integration planned for future
- Parser abstractions isolate markdown library

#### ADR-008: Slot-Based Content Model
- Templates define required/optional slots
- Constraints per slot type
- Flexible for future template expansion

#### ADR-009: Property-Based Testing Strategy
- ScalaCheck for invariant testing
- Domain-specific generators
- Edge case coverage through frequency distribution

#### PDR-001: Density Validation Limits
- Title: 2 lines max
- Body: 12 lines, 150 words max
- Heading: 80 characters max
- Based on presentation best practices

#### PDR-003: Slide Deck Size Limits
- Minimum: 1 slide
- Maximum: 200 slides
- Prevents performance issues, enforces brevity

#### PDR-005: Accessibility Requirements
- WCAG AA color contrast compliance
- Keyboard navigation support
- Semantic HTML structure

#### POL-001: Ubiquitous Language Enforcement
- All code uses domain terminology
- No technical jargon in domain layer
- Consistent naming across layers

#### POL-003: Pure Functional Domain
- Domain layer has no I/O
- All domain functions are pure
- Side effects isolated to infrastructure/CLI

#### POL-004: Property-Based Testing Requirements
- All domain invariants tested with ScalaCheck
- Example-based tests for specific scenarios
- Minimum 100 test cases per property

### Known Limitations (MVP Scope)

The following features are **intentionally omitted** from v0.1.0 and planned for future releases:

#### Not Implemented
- ❌ Full markdown rendering (bold, italic, links, etc.)
  - Current: Markdown preserved as plain text
  - Planned: US-003 (Markdown Rendering)

- ❌ Code syntax highlighting
  - Planned: Future iteration

- ❌ Image support
  - Planned: Future iteration

- ❌ Custom themes
  - Current: Single default theme with inline CSS
  - Planned: US-008, US-009 (Theme System)

- ❌ Speaker notes
  - Planned: Future iteration

- ❌ PDF export
  - Planned: Future iteration

- ❌ Live preview / watch mode
  - Current: Manual conversion required
  - Planned: Future iteration

#### Technical Debt
- None identified (this is a clean MVP implementation)

### Migration Notes

This is the initial release. No migration required.

---

## [0.2.0] - 2025-12-22

### Added

#### Core Features
- **Full Markdown Rendering** (US-003)
  - Bold text: `**text**` or `__text__`
  - Italic text: `*text*` or `_text_`
  - Inline code: `` `text` ``
  - Hyperlinks: `[text](url)`
  - Flexmark-based markdown parser (ADR-010)

- **Code Block Support** (US-004)
  - Fenced code blocks: ````language`
  - Language hints for syntax (not highlighted yet)
  - Auto-scaling for long code blocks (>20 lines)
  - Code blocks excluded from line/word counts (PDR-006)
  - 39 tests for code block parsing and rendering

- **Image Embedding** (US-005)
  - Relative paths: `![alt](images/logo.svg)`
  - Absolute URLs: `![alt](https://example.com/img.png)`
  - Data URLs: `![alt](data:image/svg+xml;base64,...)`
  - Mandatory alt text for accessibility (PDR-005)
  - Visual density warnings (PDR-008): 3+ images = warning
  - Images excluded from line/word counts
  - 11 tests for image parsing and rendering
  - Example directory structure in `examples/image-demo/`

- **Theme System** (US-008, US-009)
  - JSON-based custom themes
  - Built-in themes: light, dark, corporate
  - Retisio corporate theme with Varela Round font
  - Theme schema with colors, fonts, spacing, syntax highlighting
  - 8 tests for theme JSON parsing

#### Infrastructure
- **Flexmark Integration** (ADR-010)
  - Anticorruption layer for Flexmark parser
  - FlexmarkAdapter isolates domain from library
  - AST-based parsing for precise control
  - 58 total tests for Flexmark integration

#### Examples
- `examples/image-demo/` - Recommended directory structure for images
- `examples/embedded-image-test.md` - Data URL example
- `examples/images-demo.md` - Comprehensive image feature demo
- `examples/images-demo-retisio.html` - Corporate theme example

#### Documentation
- **README.md** updated to v0.2.0
  - "Working with Images" section with workflow guide
  - Image path best practices
  - Directory structure recommendations
  - Test count updated to 171 tests
- **Governance Documents**
  - PDR-006: Code Block Rendering Limits
  - PDR-007: Theme JSON Schema
  - PDR-008: Image Policy
  - ADR-010: Markdown Library Selection (Flexmark)
  - US-006: Image Asset Copying (proposed for v0.3.0)
- `examples/image-demo/README.md` - Complete workflow guide

### Changed
- Test count: 81 → 171 tests (90 new tests)
- CLI now supports `--theme` flag for custom themes
- HTMLRenderer now renders full markdown formatting
- Validation pipeline now excludes code blocks and images from density checks

### Technical Details
- Added `FormattedContent` domain type with `TextSpan`, `Link`, `CodeBlock`, `ContentImage`
- Added `Theme` aggregate with `ColorScheme`, `FontScheme`, `Spacing`, `SyntaxColors`
- Added `FlexmarkAdapter` anticorruption layer
- Added `ThemeJsonAdapter` for JSON theme parsing
- Updated `HTMLRenderer` with code block and image rendering

### Breaking Changes
None. This release is fully backward compatible with v0.1.0.

### Bug Fixes
- Fixed JAR deployment: `mill cli.assembly` now requires manual copy to `mdslides.jar`
  ```bash
  cp out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar mdslides.jar
  ```

## [Unreleased]

### Planned for v0.3.0
- US-006: Image asset copying (auto-copy referenced images to output directory)
- Code syntax highlighting with highlight.js or Prism
- Google Fonts integration for custom themes
- Speaker notes support
- PDF export functionality
- Live preview mode
- Watch mode (auto-recompile on file change)

### Planned for v1.0.0
- Complete feature set from BACKLOG-V3.md
- Performance optimizations
- Comprehensive user documentation
- Tutorial and examples library

---

## Release Process

### Version Numbering
- **MAJOR.MINOR.PATCH-LABEL**
- MAJOR: Breaking changes
- MINOR: New features (backward compatible)
- PATCH: Bug fixes (backward compatible)
- LABEL: `MVP`, `RC`, or omitted for stable

### How to Release
1. Update CHANGELOG.md with release date and notes
2. Commit changes: `git commit -m "Release vX.Y.Z"`
3. Tag release: `git tag -a vX.Y.Z -m "Release vX.Y.Z"`
4. Push: `git push && git push --tags`

---

[0.1.0-MVP]: https://github.com/yourusername/mdslides/releases/tag/v0.1.0-MVP
[Unreleased]: https://github.com/yourusername/mdslides/compare/v0.1.0-MVP...HEAD
