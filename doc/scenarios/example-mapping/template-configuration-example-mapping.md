# Example Mapping: Template Configuration

**Story:** Theme authors can configure template-specific presentation defaults

**Date:** 2026-01-02
**Participants:** Architect, Product Owner, Bench Developer
**Status:** Draft

## Story Card (Yellow)

**As a** theme author
**I want** to configure presentation defaults per template type
**So that** slides automatically match my branded backgrounds and design system
**Without** requiring content authors to specify presentation details in every slide

## Rules (Blue)

### Rule 1: Configuration Hierarchy
Theme template config overrides theme defaults, global config overrides theme template config, slide frontmatter overrides everything

### Rule 2: Vertical Alignment
Templates can specify top, center, or bottom alignment for content positioning

### Rule 3: Layout Variants
Templates can specify single-column (default) or two-column layout

### Rule 4: Color Overrides
Templates can override theme colors for specific elements (heading, subtitle, body, author)

### Rule 5: Header/Footer Templates
Templates can define default header/footer templates with placeholders

### Rule 6: Column Configuration
Two-column layouts must specify column configuration (widths, colors per column)

### Rule 7: Validation
Template names must be valid, two-column requires column config, colors must be valid CSS

## Examples (Green)

### Rule 1: Configuration Hierarchy

**Example 1.1: Theme template overrides theme default**
- Theme default: `colors.heading: #002C74`
- Theme template config: `title.colors.heading: #FFFFFF`
- Result: Title slides have white headings, other templates use blue

**Example 1.2: Global config overrides theme template**
- Theme template config: `title.footer: "{{company}} Confidential"`
- Global config: `footer: "Public Domain"`
- Result: Title slide footer shows "Public Domain" (global config wins)

**Example 1.3: Slide frontmatter overrides everything**
- Theme template config: `title.footer: "{{company}} Confidential"`
- Global config: `footer: "{{pageNumber}}/{{totalPages}}"`
- Slide frontmatter: `footer: "Custom Footer"`
- Result: Slide shows "Custom Footer" (highest priority)

**Example 1.4: Fallback chain**
- Theme template config: `diagram.colors.heading: #FFFFFF`
- No global config
- No slide frontmatter
- Result: Diagram slide uses white heading from template config

### Rule 2: Vertical Alignment

**Example 2.1: Bottom-aligned title slide (the prior organization)**
- Theme template config: `title.verticalAlign: "bottom"`
- Background has branded graphics at top, whitespace at bottom
- Result: Title content positioned at bottom of slide

**Example 2.2: Center-aligned content (default)**
- No template config for vertical align
- Result: Content centered vertically (default behavior)

**Example 2.3: Slide override of template alignment**
- Theme template config: `title.verticalAlign: "bottom"`
- Slide frontmatter: `vertical-align: top`
- Result: Slide content positioned at top (frontmatter wins)

### Rule 3: Layout Variants

**Example 3.1: Section-title as two-column**
- Theme template config: `section-title.layout: "two-column"`
- Theme template config: `section-title.columnConfig: { leftColumn: ..., rightColumn: ... }`
- Result: Section-title slides render with two-column layout

**Example 3.2: Default single-column**
- No layout specified in template config
- Result: Template renders as single-column (default)

**Example 3.3: Two-column without columnConfig (validation error)**
- Theme template config: `section-title.layout: "two-column"`
- No columnConfig specified
- Result: Theme validation fails with error "Two-column layout requires columnConfig"

### Rule 4: Color Overrides

**Example 4.1: White headings on diagram slides**
- Theme default: `colors.heading: #002C74`
- Theme template config: `diagram.colors.heading: #FFFFFF`
- Result: Diagram slides have white headings, other templates blue

**Example 4.2: Multiple color overrides for title**
- Theme template config: `title.colors.heading: #FFFFFF`
- Theme template config: `title.colors.subtitle: #CCCCCC`
- Theme template config: `title.colors.author: #FFFFFF`
- Result: Title slide uses all three custom colors

**Example 4.3: Partial color override**
- Theme default: `colors.heading: #002C74`, `colors.body: #333333`
- Theme template config: `closing.colors.heading: #FFFFFF`
- No body color override
- Result: Closing slide has white heading, inherits theme default body color

### Rule 5: Header/Footer Templates

**Example 5.1: Branded footer on title slides**
- Theme template config: `title.footer: "{{company}} Confidential | {{date}}"`
- Slide renders with resolved placeholders: "the prior organization Confidential | 2026-01-02"

**Example 5.2: Page numbers on content slides**
- Theme template config: `content.footer: "{{pageNumber}}/{{totalPages}}"`
- Slide 5 of 10 renders footer: "5/10"

**Example 5.3: Timer in header**
- Theme template config: `diagram.header: "Elapsed: {{timer}}"`
- After 5 minutes, header shows: "Elapsed: 00:05:00"

**Example 5.4: Multi-element footer**
- Theme template config: `content.footer: "<span class='footer-left'>{{timer}}</span><span class='footer-right'>{{pageNumber}}/{{totalPages}}</span>"`
- Result: Timer on left, page number on right

**Example 5.5: No header/footer**
- No theme template config for header/footer
- No global config
- No slide frontmatter
- Result: No header or footer rendered

### Rule 6: Column Configuration

**Example 6.1: Section-title two-column with custom widths**
- Theme template config:
  ```json
  {
    "section-title": {
      "layout": "two-column",
      "columnConfig": {
        "leftColumn": { "width": "40%" },
        "rightColumn": { "width": "60%" }
      }
    }
  }
  ```
- Result: Left column 40% wide, right column 60% wide

**Example 6.2: Section-title two-column with color overrides**
- Theme template config:
  ```json
  {
    "section-title": {
      "layout": "two-column",
      "columnConfig": {
        "leftColumn": {
          "width": "40%",
          "colors": { "heading": "#FFFFFF" }
        },
        "rightColumn": {
          "width": "60%"
        }
      }
    }
  }
  ```
- Result: Left column heading is white, right column inherits theme default

**Example 6.3: Default column widths**
- Theme template config: `section-title.layout: "two-column"`
- Column config doesn't specify widths
- Result: Defaults to 50%/50% split

### Rule 7: Validation

**Example 7.1: Invalid template name**
- Theme template config: `custom-template.verticalAlign: "bottom"`
- Template "custom-template" doesn't exist
- Result: Theme validation error "Unknown template: custom-template"

**Example 7.2: Invalid color value**
- Theme template config: `title.colors.heading: "not-a-color"`
- Result: Theme validation error "Invalid CSS color: not-a-color"

**Example 7.3: Invalid vertical alignment**
- Theme template config: `title.verticalAlign: "middle"`
- Valid values: top, center, bottom
- Result: Theme validation error "Invalid vertical alignment: middle"

**Example 7.4: Two-column without column config**
- Already covered in Example 3.3

## Questions (Pink)

1. **Q:** Should column widths be required or have sensible defaults?
   **A:** Default to 50%/50% if not specified (see Example 6.3)

2. **Q:** What happens if column widths exceed 100%?
   **A:** Theme validation warning, but allow (CSS will handle overflow)

3. **Q:** Should we validate placeholder syntax in header/footer templates?
   **A:** No - invalid placeholders just render as-is (fail gracefully)

4. **Q:** Can themes specify different configs for the same template in different contexts?
   **A:** No - one config per template type. Context-specific needs use slide frontmatter.

5. **Q:** Should we support responsive layouts (different configs for mobile vs desktop)?
   **A:** Out of scope for v1 - add later if needed

6. **Q:** Should arbitrary CSS be supported via `customCSS` field?
   **A:** No - keep structured properties only. Authors can extend themes for custom CSS.

7. **Q:** What if theme specifies both templateBackgrounds and templateConfig for same template?
   **A:** Both apply - background image + template config are orthogonal concerns

8. **Q:** Should validation be strict (fail) or permissive (warn)?
   **A:** Strict for structure (template names, required fields), permissive for values (invalid colors warn but don't fail)

## Next Steps

- [ ] Three Amigos: Convert examples to Gherkin scenarios
- [ ] Acceptance Criteria Review: Validate scenarios with domain model
- [ ] Implementation: TDD with property-based testing for validation rules
