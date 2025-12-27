# MDSlides Installation Guide

## v1.0.0 Release

MDSlides is a command-line tool for converting Markdown files into beautiful HTML presentations.

## Requirements

- **Java 11 or higher** (Java 17+ recommended)
- No additional dependencies required
- **Mill 0.11.6** (optional, only for building from source)

Check your Java version:
```bash
java -version
```

## Installation

### Step 1: Download the JAR

Download `mdslides.jar` from the [releases page](https://github.com/yourorg/mdslides/releases/tag/v1.0.0).

Or use the JAR in this repository:
```bash
cp mdslides.jar ~/bin/  # Or your preferred location
```

### Step 2: Test It Works

```bash
java -jar mdslides.jar render examples/mdslides-tutorial --theme retisio
open examples/mdslides-tutorial/index.html
```

### Step 3: Make it Executable (Optional)

Create a launcher script for convenience:

**Linux/macOS:**
```bash
# Create wrapper script
echo '#!/bin/bash' > mdslides
echo 'java -jar /path/to/mdslides.jar "$@"' >> mdslides
chmod +x mdslides

# Run it
./mdslides render my-presentation
```

**Windows:**
Create `mdslides.bat`:
```batch
@echo off
java -jar C:\path\to\mdslides.jar %*
```

Then run:
```cmd
mdslides.bat render my-presentation
```

### Step 4: (Optional) Add to PATH

**Linux/macOS:**
```bash
# Add to ~/.bashrc or ~/.zshrc
export PATH="/path/to/mdslides:$PATH"

# Or install system-wide
sudo cp mdslides /usr/local/bin/
sudo cp mdslides.jar /usr/local/bin/

# Then use from anywhere
mdslides render my-presentation
```

**Windows:**
1. Add the directory containing `mdslides.bat` to your system PATH
2. Use from anywhere: `mdslides render my-presentation`

## Usage

### Simple Form (Recommended)

```bash
# Converts my-presentation.md → my-presentation/index.html
mdslides render my-presentation

# With a theme
mdslides render my-presentation --theme dark
mdslides render my-presentation --theme retisio
```

### Explicit Form (Advanced)

```bash
# Custom paths
mdslides render -i slides.md -o output-dir --theme light
mdslides render --input talk.md --output dist
```

### Available Flags

- `--theme THEME` - light, dark, tjm-solutions, retisio (default: light)
- `--no-copy-images` - Skip automatic image copying
- `-i, --input FILE` - Explicit input file path
- `-o, --output DIR` - Explicit output directory

## Option 2: Build from Source

### Step 1: Install Mill

**macOS/Linux:**
```bash
curl -L https://github.com/com-lihaoyi/mill/releases/download/0.11.6/0.11.6 > mill
chmod +x mill
sudo mv mill /usr/local/bin/
```

**Windows:**
Download from https://github.com/com-lihaoyi/mill/releases

### Step 2: Clone Repository

```bash
git clone <repository-url>
cd mdslides
```

### Step 3: Build

```bash
# Compile all modules
mill __.compile

# Run tests (optional, to verify build)
mill __.test

# Create standalone JAR
mill cli.assembly

# Copy JAR to convenient location
cp out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar mdslides.jar
```

### Step 4: Run

```bash
# Using Mill (during development)
mill cli.run render my-presentation --theme light

# Using JAR
java -jar mdslides.jar render my-presentation

# Using launcher script
./mdslides render my-presentation
```

## Verification

Test the installation with the tutorial:

```bash
# Generate the tutorial presentation
java -jar mdslides.jar render examples/mdslides-tutorial --theme retisio

# Open in browser
open examples/mdslides-tutorial/index.html
# Or on Linux: xdg-open examples/mdslides-tutorial/index.html
```

You should see:
```
Loading theme: retisio
✓ Loaded theme: Retisio v1.0.0
Reading markdown from: ./examples/mdslides-tutorial.md
Parsing markdown...
✓ Parsed 16 slide(s)
Validating slide deck...
✓ Validation passed
Copying images...
✓ Copied 6 image(s) (767.1 KB total)
Rendering HTML with theme: Retisio
✓ Generated XXXXX characters of HTML
Writing HTML to: examples/mdslides-tutorial/index.html
✓ Successfully created presentation: examples/mdslides-tutorial/
```

Open the HTML file in your browser and verify:
- Title slide displays correctly
- Keyboard navigation works (→, ←, Space, Home, End)
- Slide counter shows "1 / 16"
- Images and backgrounds are visible
- Theme styling is applied

## Troubleshooting

### "java: command not found"

Install Java:

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

**macOS:**
```bash
brew install openjdk@11
```

**Windows:**
Download from https://adoptium.net/

### "Error: mdslides.jar not found"

Make sure you're in the correct directory or use the full path:
```bash
java -jar /full/path/to/mdslides.jar render my-presentation
```

###"Input file not found"

MDSlides looks for `DECK_NAME.md` in the current directory:
```bash
# Ensure you're in the directory containing your markdown
cd /path/to/presentations
mdslides render my-presentation

# Or use explicit paths
mdslides render -i /path/to/slides.md -o /path/to/output
```

### "Parse error: No slides found"

Check your markdown format:
- Each slide must start with `---`
- Include frontmatter: `template: title` or `template: content`
- See the tutorial: `examples/mdslides-tutorial.md`

### Permission Denied (Linux/macOS)

Make the launcher script executable:
```bash
chmod +x mdslides
```

### Validation Errors

MDSlides enforces density constraints. If you see validation errors:
- Title slides: max 2 lines per slot
- Content slides: max 12 lines, 150 words in body
- Headings: max 80 characters

See [README-MVP.md](README-MVP.md#validation) for full constraints.

## Upgrading

To upgrade to a newer version:

1. Download the new `mdslides.jar`
2. Replace your existing JAR
3. Check [CHANGELOG.md](CHANGELOG.md) for breaking changes

## Uninstalling

Simply delete:
- `mdslides` (launcher script)
- `mdslides.jar`
- Any PATH entries you added

## Next Steps

- Read the [Tutorial](examples/mdslides-tutorial.md) for comprehensive examples
- Explore the [Product Backlog](doc/internal/planning/product-backlog.md) for upcoming features
- Check [doc/governance/](doc/governance/) for design decisions

## What's New in v1.0

**US-019: Improved CLI UX** ⚠️ BREAKING CHANGE
- Simple form: `mdslides render my-preso` (infers input/output)
- Directory output: creates `my-preso/index.html` with all assets
- Portable presentations: self-contained directories

**US-004: Speaker Notes Parsing**
- Add notes to slides: `notes: "Remember to mention..."`
- Multi-line support: `notes: ["Point 1", "Point 2"]`
- Parsed but not rendered (rendering in v1.1)

## Getting Help

- **Issues**: https://github.com/yourorg/mdslides/issues
- **Documentation**: See examples/ and doc/ directories
- **Tutorial**: examples/mdslides-tutorial.md

---

**MDSlides v1.0.0** - Installation Complete!
