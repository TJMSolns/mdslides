# MDSlides Examples

This directory contains example presentations demonstrating MDSlides features.

---

## Available Examples

### 1. MDSlides Tutorial (`mdslides-tutorial.md`)

**Description:** Comprehensive 15-slide tutorial covering all MDSlides features

**Topics:**
- Getting started with MDSlides
- Basic slide structure
- Text formatting and code blocks
- Working with themes
- Directory-based theme system
- Template-specific backgrounds
- Creating custom themes
- Image asset management
- Best practices

**How to Render:**
```bash
# Using the prior organization theme (recommended)
java -jar ../out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar \
  mdslides-tutorial.md \
  tutorial-output.html \
  --theme retisio

# Using default light theme
java -jar ../out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar \
  mdslides-tutorial.md \
  tutorial-output.html

# Using dark theme
java -jar ../out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar \
  mdslides-tutorial.md \
  tutorial-output.html \
  --theme dark
```

**Output:**
- 15 slides
- ~11.5 KB HTML
- Fully styled with chosen theme
- Keyboard navigation enabled
- Slide counter included

**View Result:**
```bash
# Linux
xdg-open tutorial-output.html

# macOS
open tutorial-output.html

# Windows
start tutorial-output.html
```

---

## Creating Your Own Presentation

### Step 1: Create Markdown File

Create a new file `my-presentation.md`:

```markdown
---
template: title
---

# My Presentation

## Subtitle Goes Here

**Author Name**

---
template: content
---

## First Content Slide

Your content goes here.

**Key Points:**
- Point 1
- Point 2
- Point 3

---
template: content
---

## Code Example

Show code with syntax highlighting:

\`\`\`python
def hello_world():
    print("Hello, MDSlides!")

hello_world()
\`\`\`

**Python code is automatically highlighted!**
```

### Step 2: Render to HTML

```bash
java -jar ../out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar \
  my-presentation.md \
  my-presentation.html \
  --theme retisio
```

### Step 3: Present

Open `my-presentation.html` in any web browser. Use keyboard controls:
- **→** or **Space**: Next slide
- **←**: Previous slide
- **Home**: First slide
- **End**: Last slide

---

## Available Templates

MDSlides currently supports these slide templates:

### Title Template (`template: title`)
**Slots:**
- Title (H1) - required
- Subtitle (H2) - optional
- Author (bold text) - optional

**Use for:** Title slide, cover slide

**Example:**
```markdown
---
template: title
---

# Presentation Title

## Optional Subtitle

**Your Name**
```

### Content Template (`template: content`)
**Slots:**
- Heading (H2) - required
- Body - required

**Use for:** Regular content slides

**Example:**
```markdown
---
template: content
---

## Slide Heading

Slide content goes here with **bold**, *italic*, and `code`.
```

---

## Available Themes

### Built-in Themes

#### Light (default)
```bash
--theme light
```
- Clean, professional
- High contrast for readability
- White background, dark text

#### Dark
```bash
--theme dark
```
- Modern dark mode
- Eye-friendly for low-light
- Dark background, light text

#### Corporate
```bash
--theme corporate
```
- Professional business styling
- Subtle background watermark
- Conservative color palette

### Custom Themes

#### the prior organization
```bash
--theme retisio
```
- Vibrant brand colors (navy #002C74, gold #FCC010, green #0B9655)
- 'Varela Round' font family
- Template-specific backgrounds (v0.4.0 feature)
- 5 background images for different slide types

**Directory structure:**
```
themes/retisio/
├── theme.json
└── backgrounds/
    ├── retisio-title-page.png
    ├── retisio-content-page.png
    ├── retisio-diagram-page.png
    ├── retisio-section-title-page.png
    └── retisio-end-page.png
```

---

## Markdown Features Supported

### Text Formatting
- **Bold:** `**bold text**`
- *Italic:* `*italic text*`
- `Inline code:` `` `code` ``
- Links: `[text](url)`

### Lists
**Unordered:**
```markdown
- Item 1
- Item 2
  - Nested item
```

**Ordered:**
```markdown
1. First
2. Second
3. Third
```

### Code Blocks
```markdown
\`\`\`language
code here
\`\`\`
```

**Supported languages:** Scala, Python, JavaScript, Java, SQL, Bash, and more

### Images
```markdown
![Alt text](path/to/image.png)
```

**Note:** Local images are automatically copied to output directory (v0.3.0+)

---

## Tips for Great Presentations

### Content Guidelines
1. **One idea per slide** - Keep focus clear
2. **12 lines max** - Body text constraint
3. **150 words max** - Readability guideline
4. **80 chars max** - Heading constraint

### Visual Guidelines
1. **Use code blocks** - Show, don't just tell
2. **Include alt text** - Accessibility matters
3. **Choose appropriate template** - Title vs content
4. **Leverage themes** - Consistent branding

### Navigation Tips
1. **Test keyboard controls** - Ensure they work
2. **Check slide counter** - Shows progress (e.g., "5 / 15")
3. **Practice transitions** - Smooth flow between slides

---

## Creating Custom Themes

See the the prior organization theme as an example:

### 1. Create Theme Directory
```bash
mkdir -p ../themes/mytheme/backgrounds
```

### 2. Create `theme.json`
```json
{
  "name": "My Theme",
  "version": "1.0.0",
  "background": {
    "color": "#FFFFFF"
  },
  "templateBackgrounds": {
    "title": "backgrounds/title.png",
    "content": "backgrounds/content.png"
  },
  "colors": {
    "text": "#333333",
    "heading": "#2c3e50",
    "accent": "#3498db",
    "link": "#3498db",
    "linkHover": "#2980b9",
    "codeBackground": "#f4f4f4",
    "codeText": "#333333"
  },
  "fonts": {
    "body": "Arial, sans-serif",
    "heading": "Georgia, serif",
    "code": "Consolas, monospace"
  },
  "spacing": {
    "slideMargin": "2rem",
    "headingMargin": "1rem 0",
    "paragraphMargin": "0.5rem 0",
    "lineHeight": "1.6"
  },
  "syntax": {
    "keyword": "#569cd6",
    "string": "#ce9178",
    "comment": "#6a9955",
    "function": "#dcdcaa",
    "number": "#b5cea8",
    "operator": "#d4d4d4"
  },
  "slideCounter": {
    "color": "#666666",
    "background": "rgba(255, 255, 255, 0.9)",
    "fontSize": "0.9rem"
  }
}
```

### 3. Add Background Images (optional)
```bash
cp your-backgrounds/*.png ../themes/mytheme/backgrounds/
```

### 4. Use Your Theme
```bash
java -jar ../mdslides.jar presentation.md output.html --theme mytheme
```

---

## Troubleshooting

### Theme not found
```
✗ Theme load error: Theme 'mytheme' not found in ./themes/
```

**Solution:** Check that `./themes/mytheme/theme.json` exists

### Invalid JSON
```
✗ Theme parse error: ...
```

**Solution:** Validate your theme.json with a JSON validator

### Template not found
```
✗ Parse error: Slide 2: Unknown template 'custom-template'
```

**Solution:** Use only `title` or `content` templates (more coming in future versions)

### Validation failed
```
✗ Validation failed:
  - Slide 3: Body exceeds 12 lines (15 lines)
```

**Solution:** Reduce content or split into multiple slides

---

## More Information

- **Main README:** `../README.md`
- **Demo Document:** `../DEMO.md`
- **Progress Report:** `../doc/internal/planning/v0.4.0-progress-report.md`
- **Documentation:** `../doc/`

---

**Happy Presenting!** 🎉
