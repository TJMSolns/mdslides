# Acceptance Criteria: Template Configuration

**Feature:** Theme authors can configure template-specific presentation defaults
**Version:** 3.0.0
**Status:** Ready for Implementation
**Date:** 2026-01-02

## Overview

This document validates the Gherkin scenarios against the domain model and provides acceptance criteria for the Template Configuration feature.

## Feature File Location

`features/template-configuration.feature`

## Domain Model Reference

`doc/domain-models/aggregates/template-configuration-aggregate.md`

## Acceptance Criteria Summary

### AC-1: Configuration Hierarchy (Business Rule BR-1)

**Criterion:** The system must resolve configuration in the following priority order:
1. Slide frontmatter (highest priority)
2. Theme template configuration
3. Global configuration
4. Theme defaults (lowest priority)

**Scenarios:**
- ✓ Theme template config overrides theme default
- ✓ Global config overrides theme template config
- ✓ Slide frontmatter overrides everything
- ✓ Fallback chain uses theme template config when no overrides

**Domain Model Validation:**
- Aligns with BR-1: Configuration Hierarchy
- Aligns with BR-4: Header/Footer Configuration Hierarchy
- Integration point: HTMLRenderer must implement resolution logic

### AC-2: Vertical Alignment (Business Rule BR-2)

**Criterion:** Templates can specify vertical alignment (top, center, bottom) for slide content positioning. Center is the default when not specified.

**Scenarios:**
- ✓ Bottom-aligned title slide for branded theme
- ✓ Center-aligned content by default
- ✓ Slide frontmatter overrides template alignment

**Domain Model Validation:**
- Aligns with `VerticalAlignment` enum (Top, Center, Bottom)
- Maps to CSS flexbox `justify-content` values
- Integration point: HTMLRenderer must apply alignment to slide wrapper

### AC-3: Layout Variants (Business Rule BR-2, BR-3)

**Criterion:** Templates can specify single-column (default) or two-column layout. Two-column requires column configuration.

**Scenarios:**
- ✓ Section-title rendered as two-column
- ✓ Default single-column layout when not specified
- ✓ Validation error for two-column without column config

**Domain Model Validation:**
- Aligns with `TemplateLayout` enum (SingleColumn, TwoColumn)
- Invariant: Two-column requires `columnConfig.isDefined`
- Validation occurs at theme load time (ThemeLoader)

### AC-4: Color Overrides (Business Rule BR-3)

**Criterion:** Templates can override theme colors for specific elements (heading, subtitle, body, author). Unspecified elements inherit from theme defaults.

**Scenarios:**
- ✓ White headings on diagram slides only
- ✓ Multiple color overrides for title template
- ✓ Partial color override inherits theme defaults

**Domain Model Validation:**
- Aligns with `TemplateColors` value object
- Color values must be valid CSS (hex, rgb, named colors)
- Integration point: HTMLRenderer must merge template colors with theme defaults

### AC-5: Header/Footer Templates (Business Rule BR-4)

**Criterion:** Templates can define default header/footer templates with placeholders. Resolution follows configuration hierarchy.

**Scenarios:**
- ✓ Branded footer on title slides with placeholders
- ✓ Page numbers on content slides
- ✓ Timer in header
- ✓ Multi-element footer with positioned spans
- ✓ No header or footer when not configured

**Domain Model Validation:**
- `header` and `footer` fields in TemplateConfiguration
- Placeholders: `{{company}}`, `{{date}}`, `{{pageNumber}}`, `{{totalPages}}`, `{{timer}}`
- Integration point: HTMLRenderer resolves placeholders at render time

### AC-6: Column Configuration (Business Rule BR-6)

**Criterion:** Two-column layouts must specify column configuration with widths and optional per-column color overrides. Widths default to 50%/50% if not specified.

**Scenarios:**
- ✓ Two-column layout with custom widths
- ✓ Two-column layout with per-column color overrides
- ✓ Default column widths when not specified

**Domain Model Validation:**
- Aligns with `ColumnConfiguration` and `ColumnSpec` value objects
- Widths must be valid CSS values
- Integration point: HTMLRenderer must apply column styles

### AC-7: Validation (Business Rule BR-5)

**Criterion:** Theme validation must enforce template name validity, color validity, alignment validity, and layout constraints.

**Scenarios:**
- ✓ Invalid template name fails validation
- ✓ Invalid CSS color fails validation
- ✓ Invalid vertical alignment value fails validation
- ✓ Column widths exceeding 100% generates warning (non-fatal)

**Domain Model Validation:**
- Invariant 1: Template name must be valid (title, content, diagram, closing, section-title, two-column)
- Invariant 2: Two-column requires column config
- Invariant 3: Color values must be valid CSS
- Invariant 4: Vertical alignment must be valid (Top, Center, Bottom)
- Integration point: ThemeLoader validates at load time

## Validation Matrix

| Business Rule | Domain Model Element | Acceptance Criteria | Scenarios | Status |
|---------------|---------------------|---------------------|-----------|--------|
| BR-1: Configuration Hierarchy | TemplateConfiguration | AC-1 | 4 | ✓ Validated |
| BR-2: Two-Column Layout Validation | TemplateLayout, ColumnConfiguration | AC-3, AC-6 | 6 | ✓ Validated |
| BR-3: Color Override Inheritance | TemplateColors | AC-4 | 3 | ✓ Validated |
| BR-4: Header/Footer Hierarchy | header, footer fields | AC-5 | 5 | ✓ Validated |
| BR-5: Template Name Validity | templateName field | AC-7 | 4 | ✓ Validated |

## Placeholder Resolution

The following placeholders must be supported in header/footer templates:

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `{{company}}` | Company name from config | "Retisio" |
| `{{date}}` | Current date | "2026-01-02" |
| `{{pageNumber}}` | Current slide number (1-indexed) | "5" |
| `{{totalPages}}` | Total number of slides | "10" |
| `{{timer}}` | Elapsed presentation time | "00:05:00" |

## Template Names

Valid template names (as defined in domain model):
- `title`
- `content`
- `diagram`
- `closing`
- `section-title`
- `two-column`

## JSON Schema (for theme.json)

```json
{
  "templateConfig": {
    "title": {
      "verticalAlign": "bottom",
      "colors": {
        "heading": "#FFFFFF",
        "subtitle": "#CCCCCC",
        "author": "#FFFFFF"
      },
      "footer": "{{company}} Confidential | {{date}}"
    },
    "section-title": {
      "layout": "two-column",
      "columnConfig": {
        "leftColumn": {
          "width": "40%",
          "colors": {
            "heading": "#FFFFFF"
          }
        },
        "rightColumn": {
          "width": "60%"
        }
      }
    },
    "diagram": {
      "colors": {
        "heading": "#FFFFFF"
      },
      "header": "Elapsed: {{timer}}"
    },
    "closing": {
      "colors": {
        "heading": "#FFFFFF"
      }
    },
    "content": {
      "footer": "{{pageNumber}}/{{totalPages}}"
    }
  }
}
```

## Integration Points Checklist

- [ ] **ThemeLoader**: Parse `templateConfig` from theme.json
- [ ] **ThemeLoader**: Validate template names, colors, alignments, layout constraints
- [ ] **ConfigLoader**: Read global header/footer from config.json
- [ ] **MarkdownParser**: Parse per-slide frontmatter (vertical-align, header, footer)
- [ ] **HTMLRenderer**: Resolve configuration hierarchy
- [ ] **HTMLRenderer**: Merge template colors with theme defaults
- [ ] **HTMLRenderer**: Apply vertical alignment CSS
- [ ] **HTMLRenderer**: Render two-column layouts with column widths
- [ ] **HTMLRenderer**: Resolve header/footer placeholders
- [ ] **HTMLRenderer**: Apply per-slide header/footer from frontmatter

## Success Metrics

1. **All 28 scenarios pass** in the feature file
2. **Theme validation** catches all invalid configurations
3. **Retisio theme** can be configured with:
   - Title slides bottom-aligned
   - Section-title slides as two-column with white left column headings
   - Diagram/closing slides with white headings
4. **Backward compatibility** maintained - themes without `templateConfig` continue to work

## Open Questions

None - all questions from Example Mapping were resolved.

## Next Steps

1. ✓ Domain Model created
2. ✓ Example Mapping completed
3. ✓ Three Amigos scenarios written
4. ✓ Acceptance Criteria validated
5. **→ Next: Phase 3 - Implementation (TDD)**
   - Test-First Pairing
   - Implement TemplateConfiguration value objects
   - Update Theme aggregate
   - Update ThemeLoader
   - Update HTMLRenderer
   - Property-based testing for validation rules

## Related Artifacts

- **Domain Model:** `doc/domain-models/aggregates/template-configuration-aggregate.md`
- **Example Mapping:** `doc/scenarios/example-mapping/template-configuration-example-mapping.md`
- **Feature File:** `features/template-configuration.feature`
- **Theme Aggregate:** `doc/domain-models/aggregates/theme-aggregate.md` (to be updated)
