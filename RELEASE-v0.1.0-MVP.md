# MDSlides v0.1.0-MVP Release Notes

**Release Date:** December 21, 2024
**Git Tag:** `v0.1.0-MVP`
**Commit:** d3eb453

## What's New

MDSlides v0.1.0-MVP is the first release of a domain-driven, test-first presentation framework. This MVP delivers core functionality for converting Markdown files to HTML slide decks with comprehensive validation.

## Features

### Templates
- **Title Slide**: Title (required, max 2 lines), subtitle (optional, max 2 lines), author (optional, max 80 chars)
- **Content Slide**: Heading (required, max 80 chars), body (required, max 12 lines, max 150 words)

### Validation
- Structure validation (required slots, valid templates, deck size 1-200 slides)
- Content validation (line/word/character limits)
- Comprehensive error messages listing all violations

### Output
- Standalone HTML with inline CSS and JavaScript
- Keyboard navigation (→, ←, Space, Home, End)
- Responsive design
- Slide counter
- WCAG AA compliant colors

### CLI
- Simple command: `mill cli.run input.md output.html`
- Or use standalone JAR: `./mdslides input.md output.html`
- Detailed progress output
- Error reporting

### Architecture
- **3-Module Clean Architecture**: domain (pure), infrastructure (adapters), cli (effects)
- **Pure Functional Domain**: No I/O, immutable data structures
- **Validation Pipeline**: Two-phase (structure → content)
- **Type Safety**: Opaque types, smart constructors

### Testing
- **81 Tests Total**
  - 61 domain tests (29 property-based with ScalaCheck, 32 example-based)
  - 20 infrastructure tests (10 parser, 10 renderer)
- Property-based testing for invariants
- Edge case coverage

### Documentation
- **README-MVP.md**: Complete usage guide
- **INSTALL.md**: Installation instructions
- **CHANGELOG.md**: Detailed change history
- **Governance**: 9 ADRs, 5 PDRs, 4 POLs

## Installation

### Quick Start (Standalone JAR)
```bash
# Using the launcher script
./mdslides example.md output.html

# Or directly with Java
java -jar mdslides.jar example.md output.html
```

### Build from Source
```bash
mill __.compile
mill cli.assembly
cp out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar mdslides.jar
```

See [INSTALL.md](INSTALL.md) for detailed instructions.

## Usage

### Create a Presentation

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
## Key Features

MDSlides is built with Domain-Driven Design.
It enforces slide density constraints for readability.

Perfect for technical presentations!
```

### Convert to HTML

```bash
./mdslides my-presentation.md output.html
```

### Present

Open `output.html` in your browser and use:
- **→** or **Space**: Next slide
- **←**: Previous slide
- **Home**: First slide
- **End**: Last slide

## What's Not Included (By Design)

This MVP intentionally omits features planned for future releases:
- Full markdown rendering (bold, italic, links) - text preserved as-is
- Code syntax highlighting
- Image support
- Custom themes
- Speaker notes
- PDF export
- Live preview

See [CHANGELOG.md](CHANGELOG.md) for the roadmap.

## Technical Stack

- **Scala 3.3.1**: Modern functional programming
- **Mill 0.11.6**: Build tool
- **Cats Core 2.10.0** + **Cats Effect 3.5.4**: FP and effect system
- **Scalatags 0.12.0**: Type-safe HTML generation
- **Decline 2.4.1**: CLI parsing
- **MUnit 0.7.29** + **ScalaCheck 1.17.0**: Testing

## Files Included

### Executables
- `mdslides` - Launcher script (Linux/macOS)
- `mdslides.jar` - Standalone JAR (44MB, includes all dependencies)

### Documentation
- `README-MVP.md` - User guide
- `INSTALL.md` - Installation guide
- `CHANGELOG.md` - Version history
- `doc/governance/` - Architecture decisions (ADRs, PDRs, POLs)

### Source Code
- `domain/` - Pure functional domain model
- `infrastructure/` - Parsers and renderers
- `cli/` - Command-line interface
- `build.sc` - Mill build configuration

### Examples
- `example.md` - Sample presentation

## Verification

All 81 tests pass:
```bash
mill __.test
```

Expected output:
```
✓ 29 property-based tests (domain)
✓ 32 example-based tests (domain)
✓ 10 parser tests (infrastructure)
✓ 10 renderer tests (infrastructure)
```

## Known Issues

None. This is a clean MVP release with no known bugs.

## Upgrade Path

This is the initial release. Future versions will maintain backward compatibility for:
- Markdown format
- Validation rules
- CLI interface

## Support

- **Documentation**: See README-MVP.md and INSTALL.md
- **Examples**: Check example.md
- **Governance**: Read doc/governance/ for design decisions

## Contributors

Built using Domain-Driven Design and Test-Driven Development practices.

## Next Steps

See [CHANGELOG.md](CHANGELOG.md) for planned features in v0.2.0:
- Full markdown rendering
- Code syntax highlighting
- Theme system
- And more!

---

**MDSlides v0.1.0-MVP** - Domain-Driven Presentations
Released: December 21, 2024
