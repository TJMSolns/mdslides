# MDSlides v0.2.0

**A domain-driven, test-first presentation framework for converting Markdown to HTML slides.**

[![Tests](https://img.shields.io/badge/tests-171%20passing-brightgreen)]()
[![Coverage](https://img.shields.io/badge/coverage-domain%20%2B%20infrastructure-blue)]()
[![Version](https://img.shields.io/badge/version-0.2.0-orange)]()

## Overview

MDSlides is a command-line tool that converts structured Markdown files into standalone HTML presentations with keyboard navigation. Built using Domain-Driven Design principles with comprehensive test coverage (171 tests including property-based testing).

### Key Features

- ✅ **Two Slide Templates**: Title slides and content slides
- ✅ **Full Markdown Rendering**: Bold, italic, inline code, links (US-003)
- ✅ **Code Blocks**: Fenced code blocks with language hints (US-004)
- ✅ **Image Embedding**: Support for local images, external URLs, and data URLs (US-005)
- ✅ **Theme System**: Built-in themes (light, dark, corporate) and custom JSON themes (US-008, US-009)
- ✅ **Validation**: Enforces slide density constraints (max lines, words, characters)
- ✅ **Standalone Output**: Self-contained HTML with inline CSS and JavaScript
- ✅ **Keyboard Navigation**: Arrow keys, Space, Home, End
- ✅ **Responsive Design**: Works on all screen sizes
- ✅ **Type-Safe**: Pure functional domain model with comprehensive validation
- ✅ **Well-Tested**: 171 tests (property-based + example-based)

## Quick Start

### Installation

```bash
# Prerequisites: Mill build tool
# Install Mill if not already installed:
# macOS/Linux: sh -c "$(curl -L https://github.com/com-lihaoyi/mill/releases/download/0.11.6/install-completions)"

# Clone the repository
cd /path/to/mdslides

# Build the project
mill __.compile
```

### Basic Usage

```bash
# Convert markdown to HTML
mill cli.run input.md output.html

# Example
mill cli.run example.md presentation.html
```

### Example Markdown File

Create a file `my-presentation.md`:

```markdown
---
template: title
---
# Welcome to MDSlides
## A Modern Presentation Framework
John Doe

---
template: content
---
## What is MDSlides?

MDSlides converts markdown files into beautiful HTML slide decks.
It enforces best practices for slide density and readability.

Key features:
- Type-safe domain model
- Comprehensive validation
- Clean architecture

---
template: content
---
## Getting Started

Simply write your slides in markdown format.
Use the CLI to convert them to HTML.

Then open the HTML file in your browser and present!
```

Convert it:

```bash
mill cli.run my-presentation.md my-presentation.html
```

Open `my-presentation.html` in your browser and use:
- **→** or **Space**: Next slide
- **←**: Previous slide
- **Home**: First slide
- **End**: Last slide

## Slide Templates

### Title Slide

```markdown
---
template: title
---
# Main Title (required, max 2 lines)
## Subtitle (optional, max 2 lines)
Author Name (optional, max 80 chars)
```

**Constraints:**
- Title: Required, max 2 lines
- Subtitle: Optional, max 2 lines
- Author: Optional, max 80 characters

### Content Slide

```markdown
---
template: content
---
## Heading (required, max 80 chars, 1 line)

Body content goes here (required, max 12 lines, max 150 words).
You can include multiple paragraphs.

The content is validated to ensure readability.
```

**Constraints:**
- Heading: Required, max 80 characters, single line
- Body: Required, max 12 lines, max 150 words

## Working with Images

MDSlides supports embedding images in your presentations (US-005).

### Image Syntax

```markdown
![Alt text description](image-path-or-url)
```

### Supported Image Sources

- **Relative paths** (recommended): `![Logo](images/logo.svg)`
- **Absolute URLs**: `![Logo](https://example.com/logo.png)`
- **Data URLs**: `![Icon](data:image/svg+xml;base64,...)`

### Recommended Directory Structure

For presentations with images, use this structure:

```
my-presentation/
├── slides.md           # Your markdown source
├── index.html          # Generated presentation
└── images/             # Image assets
    ├── logo.svg
    └── diagram.png
```

**Workflow:**

```bash
# 1. Create directory structure
mkdir my-presentation
mkdir my-presentation/images

# 2. Add your images to images/
cp logo.svg my-presentation/images/

# 3. Reference images with relative paths in markdown
echo "![Logo](images/logo.svg)" >> my-presentation/slides.md

# 4. Generate HTML in the same directory
mdslides my-presentation/slides.md my-presentation/index.html

# 5. View with local web server (recommended)
cd my-presentation
python3 -m http.server 8000
# Open http://localhost:8000/
```

### Image Requirements

**Accessibility (PDR-005):**
- All images MUST have descriptive alt text
- Empty alt text `![]()` is rejected during validation

**Visual Density (PDR-008):**
- 1-2 images per slide: ✓ Optimal
- 3-4 images per slide: ⚠ Warning (high density)
- 5+ images per slide: ⚠ Warning (excessive density)

### Example

See [examples/image-demo/](examples/image-demo/) for a complete working example with:
- Recommended directory structure
- Sample SVG images
- Generated presentation
- Usage instructions

### Image Path Best Practices

✅ **DO**: Use relative paths
```markdown
![Architecture](images/architecture.svg)
![Screenshot](./screenshots/demo.png)
```

⚠️ **AVOID**: Absolute filesystem paths (not portable)
```markdown
![Logo](/home/user/my-images/logo.png)  # Won't work on other systems
```

⚠️ **CAUTION**: External URLs (requires internet)
```markdown
![Logo](https://example.com/logo.png)  # May not work with file:// protocol
```

**Note:** MDSlides generates `<img>` tags with the paths you provide. You are responsible for ensuring image files exist at those paths relative to the generated HTML file.

## Architecture

MDSlides follows a clean, layered architecture:

```
┌─────────────────────────────────────┐
│           CLI Layer                 │
│  (Cats Effect, Decline, I/O)        │
└─────────────────────────────────────┘
              ▼
┌─────────────────────────────────────┐
│      Infrastructure Layer           │
│  (Parsers, Renderers, Adapters)     │
│  - MarkdownParser                   │
│  - HTMLRenderer                     │
└─────────────────────────────────────┘
              ▼
┌─────────────────────────────────────┐
│         Domain Layer                │
│   (Pure Functional, No I/O)         │
│  - Slide, SlideDeck (Aggregates)    │
│  - SlideId, SlotContent (Values)    │
│  - Validation Pipeline              │
└─────────────────────────────────────┘
```

### Design Principles

- **Pure Functional Domain**: No side effects in the domain layer
- **Validation Pipeline**: Structure validation → Content validation
- **Error Collection**: All validation errors reported together
- **Type Safety**: Opaque types, smart constructors, validated aggregates
- **Test-Driven**: Property-based + example-based testing

## Validation

MDSlides performs comprehensive validation:

### Structure Validation
- Required slots present for template type
- Valid template names
- Deck size limits (1-200 slides)
- Unique slide IDs

### Content Validation
- Line count limits (title: 2, body: 12)
- Word count limits (body: 150)
- Character count limits (heading: 80, author: 80)

### Example Validation Output

```bash
✗ Validation failed:
  - Slide 1: Content Error in 'body' slot - exceeds max 12 lines (has 15)
  - Slide 2: Structure Error - Required slot 'heading' is missing
  - Slide 3: Content Error in 'body' slot - exceeds max 150 words (has 180)
```

## Testing

Run all tests:

```bash
mill __.test
```

### Test Coverage

**171 Tests Total (v0.2.0):**

**Domain Layer (61 tests):**
- `SlideSpec.scala`: 10 example-based tests for title slides
- `ContentSlideSpec.scala`: 11 example-based tests for content slides
- `SlideDeckSpec.scala`: 11 example-based tests for deck aggregates
- `SlideProperties.scala`: 10 property-based tests for slide invariants
- `ContentSlideProperties.scala`: 9 property-based tests for content slide invariants
- `SlideDeckProperties.scala`: 10 property-based tests for deck invariants

**Infrastructure Layer (110 tests):**
- `MarkdownParserSpec.scala`: 10 tests for parsing
- `HTMLRendererSpec.scala`: 10 tests for rendering
- `FlexmarkAdapterSpec.scala`: 32 tests for markdown formatting (bold, italic, code, links)
- `FlexmarkAdapterCodeBlockSpec.scala`: 16 tests for code block parsing
- `FlexmarkAdapterImageSpec.scala`: 10 tests for image parsing
- `HTMLRendererCodeBlockSpec.scala`: 23 tests for code block rendering
- `HTMLRendererImageSpec.scala`: 1 test for image rendering
- `ThemeJsonAdapterSpec.scala`: 8 tests for theme JSON parsing

### Property-Based Testing

Uses ScalaCheck to verify invariants hold across thousands of generated inputs:

```scala
property("validated body never exceeds 12 lines") {
  forAll(validContentSlideGen) { slide =>
    Slide.validated(slide.id, slide.templateName, slide.slots) match
      case Right(validSlide) =>
        validSlide.getSlot("body").map(body =>
          SlotContent(body).lineCount <= 12
        ).getOrElse(false)
      case Left(_) => true
  }
}
```

## Technology Stack

- **Scala 3.3.1**: Modern functional programming
- **Mill 0.11.6**: Build tool
- **Cats Core 2.10.0**: Functional programming primitives
- **Cats Effect 3.5.4**: Effect system for I/O
- **Scalatags 0.12.0**: Type-safe HTML generation
- **Decline 2.4.1**: Command-line parsing
- **MUnit 0.7.29**: Testing framework
- **ScalaCheck 1.17.0**: Property-based testing

## Project Structure

```
mdslides/
├── domain/                      # Pure functional domain
│   ├── src/
│   │   └── com/tjmsolutions/mdslides/domain/
│   │       ├── Slide.scala          # Slide aggregate
│   │       ├── SlideDeck.scala      # Deck aggregate
│   │       ├── SlideId.scala        # Value objects
│   │       ├── Template.scala       # Template definitions
│   │       └── ValidationError.scala # Error types
│   └── test/
│       └── com/tjmsolutions/mdslides/domain/
│           ├── SlideSpec.scala      # Example tests
│           ├── properties/          # Property tests
│           └── generators/          # ScalaCheck generators
├── infrastructure/              # I/O adapters
│   ├── src/
│   │   └── com/tjmsolutions/mdslides/infrastructure/
│   │       ├── parser/
│   │       │   └── MarkdownParser.scala
│   │       └── renderer/
│   │           └── HTMLRenderer.scala
│   └── test/
├── cli/                         # CLI entry point
│   └── src/
│       └── com/tjmsolutions/mdslides/cli/
│           └── Main.scala
├── doc/governance/              # Design documents
│   ├── ADR/                     # Architecture Decision Records
│   ├── PDR/                     # Product Decision Records
│   └── POL/                     # Policy Documents
├── build.sc                     # Mill build configuration
└── README-MVP.md                # This file
```

## Governance

This project follows strict governance with documented decisions:

- **ADR (Architecture Decision Records)**: Technical decisions
  - [ADR-001](doc/internal/governance/adr/ADR-001-technology-stack.md): Technology Stack Selection
  - [ADR-002](doc/internal/governance/adr/ADR-002-validation-pipeline.md): Validation Pipeline Architecture
  - [ADR-006](doc/internal/governance/adr/ADR-006-rendering-architecture.md): Rendering Architecture
  - [ADR-007](doc/internal/governance/adr/ADR-007-anticorruption-layer.md): Anticorruption Layer
  - [ADR-008](doc/internal/governance/adr/ADR-008-slot-based-content.md): Slot-Based Content Model
  - [ADR-009](doc/internal/governance/adr/ADR-009-property-based-testing.md): Property-Based Testing Strategy

- **PDR (Product Decision Records)**: Product decisions
  - [PDR-001](doc/internal/governance/pdr/PDR-001-density-limits.md): Density Validation Limits
  - [PDR-003](doc/internal/governance/pdr/PDR-003-deck-size-limits.md): Slide Deck Size Limits
  - [PDR-005](doc/internal/governance/pdr/PDR-005-accessibility.md): Accessibility Requirements

- **POL (Policy Documents)**: Development policies
  - [POL-001](doc/internal/governance/pol/POL-001-ubiquitous-language.md): Ubiquitous Language Enforcement
  - [POL-003](doc/internal/governance/pol/POL-003-pure-functional-domain.md): Pure Functional Domain
  - [POL-004](doc/internal/governance/pol/POL-004-property-based-testing.md): Property-Based Testing Requirements

See [doc/internal/governance/](doc/internal/governance/) for full documentation.

## v0.2.0 Status

**Completed:**
- ✅ Full markdown rendering (bold, italic, inline code, links) - US-003
- ✅ Code blocks with language hints - US-004
- ✅ Image embedding (local, URLs, data URLs) - US-005
- ✅ Custom themes via JSON - US-008
- ✅ Built-in themes (light, dark, corporate) - US-009

**Future Enhancements:**
- ❌ Image asset copying (auto-copy referenced images to output directory) - US-006
- ❌ Code syntax highlighting (currently just styled blocks)
- ❌ Speaker notes
- ❌ PDF export
- ❌ Live preview server

See [CHANGELOG.md](CHANGELOG.md) for version history and planned features.

## Development

### Build Commands

```bash
# Compile all modules
mill __.compile

# Run all tests
mill __.test

# Run specific test suite
mill domain.test.testOnly com.tjmsolutions.mdslides.domain.SlideSpec

# Run the CLI
mill cli.run input.md output.html

# Clean build artifacts
mill clean
```

### Adding Tests

Example-based test:
```scala
test("validate title slide") {
  val slide = Slide(SlideId.unsafe(1), "title", Map("title" -> "My Title"))
  val result = Slide.validated(slide.id, slide.templateName, slide.slots)

  result match
    case Right(validSlide) => assertEquals(validSlide.getSlot("title"), Some("My Title"))
    case Left(errors) => fail(s"Expected success, got: ${errors}")
}
```

Property-based test:
```scala
property("all validated slides have required slots") {
  forAll(validTitleSlideGen) { slide =>
    Slide.validated(slide.id, slide.templateName, slide.slots) match
      case Right(validSlide) => validSlide.hasSlot("title")
      case Left(_) => true
  }
}
```

## Contributing

1. Follow the existing architecture (pure domain, impure infrastructure)
2. Write tests first (TDD)
3. Add property-based tests for invariants
4. Document decisions in governance docs
5. All tests must pass (`mill __.test`)

---

**MDSlides v0.1.0-MVP** - Domain-Driven Presentations
