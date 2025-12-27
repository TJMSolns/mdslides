# MDSlides Documentation

This directory contains all project documentation, organized hierarchically for discoverability and maintainability.

## Directory Structure

```
doc/
├── public/                          # External-facing documentation
│   └── (Future: user guides, API docs)
│
└── internal/                        # Internal development documentation
    ├── ceremonies/                  # Version-organized Event Storming sessions
    │   └── v0.2.0.md               # Sprint ceremony for v0.2.0
    │
    ├── governance/                  # Decisions and policies
    │   ├── adr/                    # Architecture Decision Records
    │   ├── pdr/                    # Product Decision Records
    │   ├── pol/                    # Policy Documents
    │   └── GOVERNANCE-ASSESSMENT.md
    │
    ├── domain/                      # Domain modeling artifacts
    │   ├── aggregates/             # Aggregate specifications
    │   ├── event-storming/         # Event storming outputs
    │   └── ubiquitous-language.md  # Domain terminology
    │
    ├── features/                    # BDD specifications
    │   ├── example-mapping-*.md    # Example mapping sessions
    │   └── three-amigos-*.md       # Three Amigos sessions
    │
    ├── planning/                    # Sprint planning artifacts
    │   ├── ceremony-status-*.md    # Status reports
    │   ├── phase-*-checklist.md    # Phase readiness
    │   └── retisio-theme-analysis.md
    │
    └── reference/                   # Templates and guides
        ├── SBPF/                   # Software Best Practices Framework
        ├── templates/              # Document templates
        ├── validation/             # Validation guides
        └── tmp/                    # Temporary assets
```

## Quick Navigation

### For Developers

- **Start here**: [v0.2.0 Ceremony](internal/ceremonies/v0.2.0.md) - Current sprint Event Storming, user stories, and ceremony output
- **Architecture decisions**: [internal/governance/adr/](internal/governance/adr/)
- **Product decisions**: [internal/governance/pdr/](internal/governance/pdr/)
- **Development policies**: [internal/governance/pol/](internal/governance/pol/)
- **Domain model**: [internal/domain/](internal/domain/)
- **BDD specifications**: [internal/features/](internal/features/)

### For Product Owners

- **Current ceremony**: [v0.2.0 Ceremony](internal/ceremonies/v0.2.0.md)
- **Product decisions**: [internal/governance/pdr/](internal/governance/pdr/)
- **Sprint planning**: [internal/planning/](internal/planning/)

### For New Team Members

1. Read [v0.2.0 Ceremony](internal/ceremonies/v0.2.0.md) to understand current sprint
2. Review [ubiquitous language](internal/domain/ubiquitous-language.md) for domain terminology
3. Check [internal/governance/pol/](internal/governance/pol/) for development policies
4. Browse [internal/reference/templates/](internal/reference/templates/) for document templates

## Key Documents

### Ceremonies (Version-Organized)

- [v0.2.0 Ceremony](internal/ceremonies/v0.2.0.md) - Full markdown rendering, code blocks, images, themes

### Architecture Decision Records (ADR)

- [ADR-001](internal/governance/adr/ADR-001-technology-stack.md) - Technology Stack Selection
- [ADR-002](internal/governance/adr/ADR-002-validation-pipeline.md) - Validation Pipeline Architecture
- [ADR-006](internal/governance/adr/ADR-006-rendering-architecture.md) - Rendering Architecture
- [ADR-007](internal/governance/adr/ADR-007-pure-functional-domain.md) - Anticorruption Layer
- [ADR-008](internal/governance/adr/ADR-008-slot-based-content.md) - Slot-Based Content Model
- [ADR-009](internal/governance/adr/ADR-009-property-based-testing-strategy.md) - Property-Based Testing Strategy
- [ADR-010](internal/governance/adr/ADR-010-markdown-library-selection.md) - Markdown Library Selection (Flexmark)
- [ADR-011](internal/governance/adr/ADR-011-syntax-highlighting-approach.md) - Syntax Highlighting Approach

### Product Decision Records (PDR)

- [PDR-001](internal/governance/pdr/PDR-001-density-validation-limits.md) - Density Validation Limits
- [PDR-003](internal/governance/pdr/PDR-003-slide-deck-limits.md) - Slide Deck Size Limits
- [PDR-005](internal/governance/pdr/PDR-005-accessibility-requirements.md) - Accessibility Requirements
- [PDR-006](internal/governance/pdr/PDR-006-code-block-rendering.md) - Code Block Rendering Limits
- [PDR-007](internal/governance/pdr/PDR-007-theme-schema.md) - Theme JSON Schema
- [PDR-008](internal/governance/pdr/PDR-008-image-policy.md) - Image Policy

### Policy Documents (POL)

- [POL-001](internal/governance/pol/POL-001-ubiquitous-language.md) - Ubiquitous Language Enforcement
- [POL-002](internal/governance/pol/POL-002-banned-terms.md) - Banned Terms
- [POL-003](internal/governance/pol/POL-003-pure-functional-domain.md) - Pure Functional Domain
- [POL-004](internal/governance/pol/POL-004-property-based-testing.md) - Property-Based Testing Requirements
- [POL-005](internal/governance/pol/POL-005-code-documentation-standards.md) - Code Documentation Standards

### Domain Model

- [Ubiquitous Language](internal/domain/ubiquitous-language.md) - Domain terminology glossary
- [Slide Deck Aggregate](internal/domain/aggregates/slide-deck-aggregate.md) - Core aggregate
- [Template Aggregate](internal/domain/aggregates/template-aggregate.md) - Template definitions
- [Event Storming](internal/domain/event-storming/slide-deck-authoring-events.md) - Domain events

## Documentation Principles

### Public vs Internal

- **public/**: User-facing documentation (getting started, tutorials, API reference)
- **internal/**: Development documentation (governance, domain models, planning)

### Hierarchical Organization

- Top-level separation by audience (public/internal)
- Second-level by concern (ceremonies, governance, domain, features, planning, reference)
- Lowercase directory names for consistency

### Version Organization

- Ceremonies are organized by version: `ceremonies/v0.2.0.md`
- User stories are documented INSIDE ceremony documents, not as separate files
- Each ceremony contains: Event Storming → User Stories → ADR/PDR → Implementation notes

### Cross-References

- Use relative paths from doc root: `[text](internal/governance/adr/ADR-001.md)`
- Keep ceremony documents self-contained with inline user stories
- Link to external governance docs (ADR/PDR/POL) for details

## Contributing

When adding documentation:

1. **Choose the right location**:
   - User-facing? → `public/`
   - Development? → `internal/`
   - Governance decision? → `internal/governance/{adr,pdr,pol}/`
   - Sprint ceremony? → `internal/ceremonies/vX.Y.Z.md`
   - BDD spec? → `internal/features/`

2. **Follow naming conventions**:
   - Lowercase directories: `adr/`, not `ADR/`
   - Uppercase filenames: `ADR-001-technology-stack.md`
   - Version in ceremonies: `v0.2.0.md`, not `CEREMONIES-v0.2.0.md`

3. **Update cross-references**:
   - Use relative paths from doc root
   - Update this README if adding new sections
   - Keep ceremony docs self-contained

4. **Use templates**:
   - See [internal/reference/templates/](internal/reference/templates/)
   - ADR template: [ADR-TEMPLATE.md](internal/reference/templates/ADR-TEMPLATE.md)
   - PDR template: [PDR-TEMPLATE.md](internal/reference/templates/PDR-TEMPLATE.md)
   - POL template: [POL-TEMPLATE.md](internal/reference/templates/POL-TEMPLATE.md)

## Related Documents

- [Project README](../README.md) - Main project documentation
- [CHANGELOG](../CHANGELOG.md) - Version history
- [BACKLOG](../BACKLOG-V3.md) - Feature backlog

---

**Note**: This structure was reorganized on 2025-12-22 to improve discoverability and maintainability.
