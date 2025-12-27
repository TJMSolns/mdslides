# MDSlides Theme System

MDSlides v0.2.0 introduces a powerful theme system that allows you to customize the visual appearance of your presentations.

## Quick Start

### Using Built-in Themes

MDSlides includes three professional themes out of the box:

```bash
# Light theme (default)
mill cli.run presentation.md output.html

# Dark theme
mill cli.run presentation.md output.html --theme dark

# Corporate theme
mill cli.run presentation.md output.html --theme corporate
```

### Using Custom Themes

Create a JSON theme file following the [theme schema](doc/governance/PDR/PDR-007-theme-schema.md):

```bash
mill cli.run presentation.md output.html --theme themes/custom.json
```

## Built-in Themes

### Light Theme (Default)

Professional, clean design with high contrast.

- **Background**: White (#ffffff)
- **Text**: Dark gray (#333333)
- **Headings**: Blue-gray (#2c3e50)
- **Accent**: Blue (#3498db)
- **Use Case**: General presentations, documentation

### Dark Theme

Modern dark mode styling, easy on the eyes.

- **Background**: Dark charcoal (#1e1e1e)
- **Text**: Light gray (#d4d4d4)
- **Headings**: Teal (#4ec9b0)
- **Accent**: Blue (#569cd6)
- **Use Case**: Low-light environments, developer talks

### Corporate Theme

Professional business styling with watermark support.

- **Background**: Off-white (#f8f9fa) with optional logo watermark
- **Text**: Dark (#212529)
- **Headings**: Blue (#0056b3)
- **Accent**: Blue (#007bff)
- **Use Case**: Business presentations, client meetings

## Custom Themes

### TJM Solutions Theme Example

The [TJM Solutions theme](themes/tjm-solutions.json) demonstrates a professional custom theme inspired by https://www.tjm.solutions:

```json
{
  "name": "tjm-solutions",
  "version": "1.0.0",
  "background": {
    "color": "#FFFFFF"
  },
  "colors": {
    "text": "#212121",
    "heading": "#C00000",
    "accent": "#C00000",
    "link": "#C00000",
    "linkHover": "#900000",
    "codeBackground": "#EBEBEB",
    "codeText": "#212121"
  },
  "fonts": {
    "body": "Lato, Arial, Helvetica, sans-serif",
    "heading": "Lato, Arial, Helvetica, sans-serif",
    "code": "Consolas, 'Courier New', monospace"
  },
  "spacing": {
    "slideMargin": "3rem",
    "headingMargin": "1.5rem 0 1rem 0",
    "paragraphMargin": "0.75rem 0",
    "lineHeight": "1.7"
  },
  "syntax": {
    "keyword": "#C00000",
    "string": "#212121",
    "comment": "#6c757d",
    "function": "#900000",
    "number": "#C00000",
    "operator": "#212121"
  },
  "slideCounter": {
    "color": "#FFFFFF",
    "background": "rgba(192, 0, 0, 0.9)",
    "fontSize": "0.9rem"
  }
}
```

**Key Features**:
- Signature TJM red (#C00000) for branding
- Lato font (matching TJM website)
- Professional spacing (3rem margins, 1.7 line-height)
- Red slide counter with white text

## Creating Custom Themes

### Minimum Required Fields

```json
{
  "name": "my-theme",
  "version": "1.0.0",
  "background": {
    "color": "#ffffff"
  },
  "colors": {
    "text": "#333333",
    "heading": "#000000",
    "accent": "#0066cc",
    "link": "#0066cc",
    "linkHover": "#0044aa",
    "codeBackground": "#f5f5f5",
    "codeText": "#333333"
  },
  "fonts": {
    "body": "Arial, sans-serif",
    "heading": "Arial, sans-serif",
    "code": "monospace"
  },
  "spacing": {
    "slideMargin": "2rem",
    "headingMargin": "1rem 0",
    "paragraphMargin": "0.5rem 0",
    "lineHeight": "1.6"
  },
  "syntax": {
    "keyword": "#0000ff",
    "string": "#00aa00",
    "comment": "#888888",
    "function": "#aa00aa",
    "number": "#aa5500",
    "operator": "#333333"
  },
  "slideCounter": {
    "color": "#666666",
    "background": "rgba(255, 255, 255, 0.9)",
    "fontSize": "0.9rem"
  }
}
```

### Background Images

Add corporate logos or watermarks:

```json
{
  "background": {
    "color": "#ffffff",
    "image": "./images/logo-watermark.png",
    "opacity": 0.05,
    "position": "bottom right",
    "size": "200px auto"
  }
}
```

### Color Accessibility

All themes must meet **WCAG AA** contrast requirements:

- Text vs background: ≥ 4.5:1 contrast ratio
- Headings vs background: ≥ 4.5:1 contrast ratio
- Links vs background: ≥ 4.5:1 contrast ratio

Use a [contrast checker](https://webaim.org/resources/contrastchecker/) to validate your colors.

## Theme Schema Reference

See [PDR-007: Theme JSON Schema](doc/governance/PDR/PDR-007-theme-schema.md) for complete documentation including:

- Full schema specification
- Field descriptions and defaults
- Validation rules
- Best practices

## Examples

All example presentations are available in three themes:

```bash
# Training presentation
examples/mdslides-training.html              # Light (default)
examples/mdslides-training-dark.html         # Dark theme
examples/mdslides-training-tjm.html          # TJM Solutions theme

# US-003 demo
examples/us-003-demo.html                    # Light (default)
examples/us-003-demo-tjm.html                # TJM Solutions theme
```

## Related Documentation

- [PDR-007: Theme JSON Schema](doc/governance/PDR/PDR-007-theme-schema.md) - Complete schema reference
- [US-008: Theme System](doc/governance/CEREMONIES-v0.2.0.md#us-008-theme-system) - User story and acceptance criteria
- [US-009: Built-in Themes](doc/governance/CEREMONIES-v0.2.0.md#us-009-built-in-themes) - Built-in theme requirements
- [PDR-005: Accessibility Requirements](doc/governance/PDR/PDR-005-accessibility-requirements.md) - WCAG AA guidelines

## Troubleshooting

### Theme file not found

```
Error: themes/custom.json (No such file or directory)
```

**Solution**: Ensure the path is correct relative to where you run the `mill` command.

### Invalid JSON

```
✗ Theme parse error: expected json value got 'invalid...' (line 1, column 1)
```

**Solution**: Validate your JSON with a tool like [JSONLint](https://jsonlint.com/).

### Missing required field

```
✗ Theme parse error: DecodingFailure at .name: Missing required field
```

**Solution**: Ensure all required fields are present. See the minimum schema above.

## Contributing

To add a new built-in theme:

1. Create the theme JSON in `themes/`
2. Add theme constant to `Theme` object in [domain/Theme.scala](domain/src/com/tjmsolutions/mdslides/domain/Theme.scala)
3. Add loader case to `loadTheme()` in [cli/Main.scala](cli/src/com/tjmsolutions/mdslides/cli/Main.scala)
4. Add tests to [ThemeSpec.scala](domain/test/src/com/tjmsolutions/mdslides/domain/ThemeSpec.scala)
5. Update this documentation

---

**MDSlides v0.2.0** - Markdown Slide Decks with Ceremony-Based Development
