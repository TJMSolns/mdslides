# Image Demo - Recommended Directory Structure

This example demonstrates the **recommended workflow** for using images in MDSlides presentations.

## Directory Structure

```
image-demo/
├── presentation.md       # Source markdown
├── index.html           # Generated presentation
├── images/              # Image assets
│   ├── retisio-logo.svg
│   ├── architecture-diagram.svg
│   ├── component-a.svg
│   └── component-b.svg
└── README.md
```

## Workflow

1. **Create your directory structure**:
   ```bash
   mkdir my-presentation
   mkdir my-presentation/images
   ```

2. **Add your images** to the `images/` directory

3. **Write markdown** with relative image paths:
   ```markdown
   ![My Diagram](images/diagram.png)
   ```

4. **Generate HTML** in the same directory:
   ```bash
   mdslides my-presentation/slides.md my-presentation/index.html
   ```

5. **Images load correctly** because they're relative to `index.html`

## Viewing the Presentation

**Option 1: Local web server** (recommended)
```bash
cd examples/image-demo
python3 -m http.server 8000
# Open http://localhost:8000/
```

**Option 2: Direct file access**
Simply open `index.html` in your browser. Local SVG files work fine with `file://` protocol.

## Supported Image Formats

- **SVG** (recommended for diagrams, logos) - works offline
- **PNG/JPG** (photos, screenshots) - works offline
- **Data URLs** (base64 embedded) - single-file portability
- **External URLs** (https://...) - requires internet, may not work with `file://`

## Image Path Best Practices

✅ **DO**: Use relative paths
```markdown
![Logo](images/logo.svg)
![Diagram](./images/arch.png)
```

⚠️ **AVOID**: Absolute filesystem paths
```markdown
![Logo](/home/user/images/logo.png)  # Won't work on other systems
```

⚠️ **CAUTION**: External URLs
```markdown
![Logo](https://example.com/logo.png)  # Requires internet, may fail with file://
```

## Accessibility

All images MUST have descriptive alt text (PDR-005):
```markdown
![Architecture diagram showing three layers](images/arch.svg)
```

Empty alt text is rejected during validation.

## Related Governance

- **PDR-005**: Accessibility Requirements (alt text mandatory)
- **PDR-008**: Image Policy (visual density warnings)
- **US-005**: Image Embedding Support
