# MDSlides Tutorial - Theme Comparison

This directory contains the **MDSlides Tutorial** rendered with all 4 available themes for comparison.

## Generated Presentations

All presentations use the same source file: [../mdslides-tutorial.md](../mdslides-tutorial.md)

### 1. Light Theme
**File:** [tutorial-light.html](tutorial-light.html) (12 KB)

**Theme:** Clean, professional white background
**Colors:** White background, dark text (#333)
**Best for:** Professional presentations, high-contrast readability

```bash
# Regenerate
java -jar ../../out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar \
  ../mdslides-tutorial.md \
  tutorial-light.html \
  --theme light
```

### 2. Dark Theme
**File:** [tutorial-dark.html](tutorial-dark.html) (12 KB)

**Theme:** Modern dark mode
**Colors:** Dark background (#1e1e1e), light text (#d4d4d4)
**Best for:** Low-light environments, eye-friendly coding talks

```bash
# Regenerate
java -jar ../../out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar \
  ../mdslides-tutorial.md \
  tutorial-dark.html \
  --theme dark
```

### 3. TJM Solutions Theme
**File:** [tutorial-tjm-solutions.html](tutorial-tjm-solutions.html) (12 KB)

**Theme:** TJM Solutions corporate branding
**Colors:** Red accents (#C00000), Lato font
**Best for:** TJM Solutions branded presentations

```bash
# Regenerate
java -jar ../../out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar \
  ../mdslides-tutorial.md \
  tutorial-tjm-solutions.html \
  --theme tjm-solutions
```

### 4. Retisio Theme
**File:** [tutorial-retisio.html](tutorial-retisio.html) (14 KB)

**Theme:** Retisio corporate branding
**Colors:** Navy (#002C74), Gold (#FCC010), Green (#0B9655), Varela Round font
**Features:** Template-specific backgrounds (infrastructure ready)
**Best for:** Retisio branded presentations

```bash
# Regenerate
java -jar ../../out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar \
  ../mdslides-tutorial.md \
  tutorial-retisio.html \
  --theme retisio
```

## How to View

### Linux
```bash
xdg-open tutorial-light.html
xdg-open tutorial-dark.html
xdg-open tutorial-tjm-solutions.html
xdg-open tutorial-retisio.html
```

### macOS
```bash
open tutorial-*.html
```

### Windows
```powershell
start tutorial-light.html
start tutorial-dark.html
start tutorial-tjm-solutions.html
start tutorial-retisio.html
```

## Keyboard Navigation

All presentations support:
- **→** or **Space**: Next slide
- **←**: Previous slide
- **Home**: First slide
- **End**: Last slide

## Theme Comparison

| Feature | Light | Dark | TJM Solutions | Retisio |
|---------|-------|------|---------------|---------|
| Background | White | Dark Gray | White | White |
| Text Color | Dark | Light | Dark | Navy |
| Accent Color | Blue | Teal | Red | Gold |
| Font Family | Arial | Arial | Lato | Varela Round |
| File Size | 12 KB | 12 KB | 12 KB | 14 KB |
| Template Backgrounds | No | No | No | Yes (5 images) |

## Tutorial Content

The tutorial covers (15 slides):
1. What is MDSlides?
2. Installation
3. Basic Slide Structure
4. Text Formatting
5. Code Blocks
6. Lists and Bullets
7. Using Themes
8. Template-Specific Backgrounds
9. Directory-Based Themes
10. Creating Custom Themes
11. Image Assets
12. Best Practices
13. Thank You!

## Source Files

- **Tutorial Markdown:** [../mdslides-tutorial.md](../mdslides-tutorial.md)
- **Light Theme:** [../../themes/light/theme.json](../../themes/light/theme.json)
- **Dark Theme:** [../../themes/dark/theme.json](../../themes/dark/theme.json)
- **TJM Solutions Theme:** [../../themes/tjm-solutions/theme.json](../../themes/tjm-solutions/theme.json)
- **Retisio Theme:** [../../themes/retisio/theme.json](../../themes/retisio/theme.json)

---

**Generated:** 2024-12-26
**MDSlides Version:** v0.4.0 (US-011, US-012, US-016 implemented)
