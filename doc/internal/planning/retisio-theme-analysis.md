# Retisio Theme Analysis

## PowerPoint Analysis Summary

Analyzed: `doc/reference/copilot-training-20251219.pptx`

### Color Scheme Extracted
- **Primary Navy**: #002C74 (main text, headings)
- **Secondary Navy**: #1A468C (subtitle)
- **Yellow/Gold**: #FCC010 (signature accent - Retisio brand color)
- **Green**: #0B9655 (links, secondary accent)
- **Light Green**: #2DB14E (tertiary accent)
- **Red**: #EC1E23 (accent for emphasis)
- **Blue**: #203C70 (code syntax)
- **Light Gray**: #E7E6E6 (backgrounds)
- **White**: #FFFFFF (slide background)

### Typography
- **Primary Font**: Varela Round (Google Font)
- **Fallback**: Arial

### Logo Design
- Stylized "S" in yellow/gold (#FCC010)
- Green circle accent (top-right)
- Red circle accent (bottom-left)
- Navy blue circular frames

### Slide Layouts Observed
1. **Title Slide**: Large title with subtitle, corporate branding
2. **Content Slides**: Title at top (left-aligned), bulleted lists with multiple levels
3. **Two-Column Layouts**: Side-by-side content sections
4. **Consistent Header**: Title always top-positioned

## Features Missing from MDSlides v0.2.0

### 1. Google Fonts Support ⭐ HIGH PRIORITY
**Current State**: Theme system only supports web-safe font stacks
**Needed**:
- Add `googleFonts: []` array to Theme domain model
- Update HTMLRenderer to inject Google Fonts `<link>` tag in `<head>`
- Update ThemeJsonAdapter to parse googleFonts field

**Implementation**:
```scala
// Theme.scala
case class Theme(
  // ... existing fields ...
  googleFonts: List[String] = List.empty
)

// HTMLRenderer.scala - in <head>
theme.googleFonts.map { font =>
  link(
    rel := "stylesheet",
    href := s"https://fonts.googleapis.com/css2?family=${font.replace(" ", "+")}&display=swap"
  )
}
```

### 2. Embedded Images (US-005) ⭐ HIGH PRIORITY
**Current State**: Not implemented
**Needed**:
- ImageReference value object (domain)
- Markdown image parsing: `![alt text](path/to/image.png)`
- FlexmarkAdapter extension for image nodes
- HTMLRenderer `<img>` tag generation
- Image path resolution (relative to presentation file)

**Use Cases from PowerPoint**:
- Logo images in content
- Diagrams and visualizations
- Background network visualization (image1.png)

### 3. Per-Slide Headers/Footers
**Current State**: Only title slides have header/footer (title+subtitle at top, author at bottom)
**Needed**:
- Template extension: add `header` and `footer` slots to content template
- Consistent positioning across all slides (not just title slides)

**Use Cases from PowerPoint**:
- Every slide has consistent top positioning for titles
- Potential for footer content (page numbers handled by slide counter already)

### 4. Two-Column Layouts
**Current State**: Single-column body content only
**Needed**:
- New template: `two-column`
- Split body into `left` and `right` slots
- CSS flexbox layout for side-by-side content

**Use Cases from PowerPoint**:
- Slide 3: "Realities of the Past" vs "Realities of the Present" (side-by-side)
- Comparing concepts, before/after scenarios

### 5. Multi-Level Bullet Lists
**Current State**: Markdown lists render, but no specific styling for indentation levels
**Needed**:
- CSS for nested `<ul>` / `<ol>` with progressive indentation
- Different bullet styles per level (●, ○, ■)

**Use Cases from PowerPoint**:
- Nested bullet points (primary bullets with sub-bullets)
- Common in content-heavy slides

### 6. Custom Logo/Watermark Positioning
**Current State**: Background images work but apply to entire slide
**Needed**:
- Dedicated `logo` field in Theme for foreground logo placement
- CSS positioning options (top-left, top-right, bottom-right, etc.)
- Higher opacity than background watermarks

**Use Cases from PowerPoint**:
- Retisio logo as persistent branding element
- Different from full-slide background images

### 7. Slide-Specific Background Images
**Current State**: Background image applies to all slides (theme-level)
**Needed**:
- Per-slide background override in frontmatter
- Example: `background: path/to/image.png`

**Use Cases from PowerPoint**:
- Different backgrounds for title vs content slides
- Image-heavy presentations with varied visuals per slide

## Retisio Theme Implementation

### Created: `themes/retisio.json`

```json
{
  "name": "Retisio",
  "description": "Retisio corporate theme - navy blue with yellow/gold accents",
  "colors": {
    "background": "#FFFFFF",
    "text": "#002C74",
    "title": "#002C74",
    "subtitle": "#1A468C",
    "accent": "#FCC010",
    "codeBackground": "#F5F5F5",
    "codeText": "#002C74",
    "linkColor": "#0B9655"
  },
  "fonts": {
    "title": "'Varela Round', Arial, 'Helvetica Neue', Helvetica, sans-serif",
    "body": "'Varela Round', Arial, 'Helvetica Neue', Helvetica, sans-serif",
    "code": "'Courier New', Courier, monospace"
  },
  "googleFonts": ["Varela Round"],
  "spacing": {
    "titleMarginBottom": "40px",
    "paragraphMargin": "20px 0"
  },
  "syntax": {
    "keyword": "#203C70",
    "string": "#0B9655",
    "comment": "#6C757D",
    "function": "#EC1E23",
    "number": "#FCC010",
    "operator": "#002C74"
  }
}
```

**Note**: `googleFonts` field added to theme JSON but not yet supported in Theme domain model. This will require:
1. Updating Theme.scala to include `googleFonts: List[String]`
2. Updating ThemeJsonAdapter.scala to parse this field
3. Updating HTMLRenderer.scala to inject Google Fonts link

### Current Limitations
- Varela Round font won't load until Google Fonts support is implemented
- Will fallback to Arial (acceptable interim solution)
- No logo/watermark (would need dedicated logo support or background image)
- Two-column layouts not supported (PowerPoint slide 3)
- Embedded images not supported (PowerPoint has 5 images)

## Priority Recommendations

### For v0.2.0 (Current Release)
1. ✅ Retisio theme JSON created (using fallback fonts temporarily)
2. **US-005: Image Embedding** (already planned)
3. **Google Fonts Support** (small enhancement, high impact)

### For v0.3.0 (Future)
4. Two-column layouts (new template)
5. Multi-level bullet styling (CSS enhancement)
6. Per-slide headers/footers (template extension)
7. Logo/watermark positioning (theme enhancement)
8. Slide-specific backgrounds (frontmatter override)

## Test Plan

Once Google Fonts support is added:
1. Generate example presentation with Retisio theme
2. Verify Varela Round font loads correctly
3. Verify color scheme matches PowerPoint
4. Compare visual fidelity to PowerPoint slides

## Related Governance

- US-008: Theme System (completed)
- US-009: Built-in Themes (TJM Solutions theme completed)
- US-005: Image Embedding (in progress)
- ADR-006: Rendering Architecture
- PDR-007: Theme JSON Schema
