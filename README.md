# MDSlides v0.1.0-MVP

**A domain-driven, test-first presentation framework for converting Markdown to HTML slides.**

[![Tests](https://img.shields.io/badge/tests-81%20passing-brightgreen)]()
[![Coverage](https://img.shields.io/badge/coverage-domain%20%2B%20infrastructure-blue)]()
[![Version](https://img.shields.io/badge/version-0.1.0--MVP-orange)]()

## Overview

MDSlides is a command-line tool that converts structured Markdown files into standalone HTML presentations with keyboard navigation. Built using Domain-Driven Design principles with comprehensive test coverage (81 tests including property-based testing).

### Key Features

- ✅ **Two Slide Templates**: Title slides and content slides
- ✅ **Validation**: Enforces slide density constraints (max lines, words, characters)
- ✅ **Standalone Output**: Self-contained HTML with inline CSS and JavaScript
- ✅ **Keyboard Navigation**: Arrow keys, Space, Home, End
- ✅ **Responsive Design**: Works on all screen sizes
- ✅ **Type-Safe**: Pure functional domain model with comprehensive validation
- ✅ **Well-Tested**: 61 domain tests (29 property-based, 32 example-based) + 20 infrastructure tests

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

**81 Tests Total:**

**Domain Layer (61 tests):**
- `SlideSpec.scala`: 10 example-based tests for title slides
- `ContentSlideSpec.scala`: 11 example-based tests for content slides
- `SlideDeckSpec.scala`: 11 example-based tests for deck aggregates
- `SlideProperties.scala`: 10 property-based tests for slide invariants
- `ContentSlideProperties.scala`: 9 property-based tests for content slide invariants
- `SlideDeckProperties.scala`: 10 property-based tests for deck invariants

**Infrastructure Layer (20 tests):**
- `MarkdownParserSpec.scala`: 10 tests for parsing
- `HTMLRendererSpec.scala`: 10 tests for rendering

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
  - [ADR-001](doc/governance/ADR/ADR-001-technology-stack.md): Technology Stack Selection
  - [ADR-002](doc/governance/ADR/ADR-002-validation-pipeline.md): Validation Pipeline Architecture
  - [ADR-006](doc/governance/ADR/ADR-006-rendering-architecture.md): Rendering Architecture
  - [ADR-007](doc/governance/ADR/ADR-007-anticorruption-layer.md): Anticorruption Layer
  - [ADR-008](doc/governance/ADR/ADR-008-slot-based-content.md): Slot-Based Content Model
  - [ADR-009](doc/governance/ADR/ADR-009-property-based-testing.md): Property-Based Testing Strategy

- **PDR (Product Decision Records)**: Product decisions
  - [PDR-001](doc/governance/PDR/PDR-001-density-limits.md): Density Validation Limits
  - [PDR-003](doc/governance/PDR/PDR-003-deck-size-limits.md): Slide Deck Size Limits
  - [PDR-005](doc/governance/PDR/PDR-005-accessibility.md): Accessibility Requirements

- **POL (Policy Documents)**: Development policies
  - [POL-001](doc/governance/POL/POL-001-ubiquitous-language.md): Ubiquitous Language Enforcement
  - [POL-003](doc/governance/POL/POL-003-pure-functional-domain.md): Pure Functional Domain
  - [POL-004](doc/governance/POL/POL-004-property-based-testing.md): Property-Based Testing Requirements

See [doc/governance/](doc/governance/) for full documentation.

## Current Limitations (v0.1.0 MVP)

This MVP focuses on core functionality. Future iterations will add:

- ❌ Full markdown rendering (bold, italic, links) - text preserved as-is
- ❌ Code syntax highlighting
- ❌ Image support
- ❌ Custom themes
- ❌ Speaker notes
- ❌ PDF export
- ❌ Live preview

See [CHANGELOG.md](CHANGELOG.md) for planned features.

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
