# Changelog

All notable changes to MDSlides will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

## [Unreleased]

### Planned for v0.2.0
- US-003: Full markdown rendering (bold, italic, links, images)
- US-004: Code block support with syntax highlighting
- US-005: Image embedding
- US-008: Theme system (JSON-based theme definitions)
- US-009: Built-in themes (light, dark, corporate)

### Planned for v0.3.0
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
