# Changelog

All notable changes to MDSlides will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
