# Installation Guide - MDSlides v0.1.0-MVP

## Prerequisites

- **Java 11 or higher** (for running the standalone JAR)
- **Mill 0.11.6** (optional, only for building from source)

Check your Java version:
```bash
java -version
```

## Option 1: Download Pre-built JAR (Recommended)

### Step 1: Download the JAR

Download `mdslides.jar` from the releases page or use the pre-built JAR in this repository.

### Step 2: Make it Executable (Optional)

Create a launcher script for convenience:

**Linux/macOS:**
```bash
# Download or copy the mdslides launcher script
chmod +x mdslides

# Run it
./mdslides input.md output.html
```

**Windows:**
Create `mdslides.bat`:
```batch
@echo off
java -jar mdslides.jar %*
```

Then run:
```cmd
mdslides.bat input.md output.html
```

### Step 3: (Optional) Add to PATH

**Linux/macOS:**
```bash
# Add to ~/.bashrc or ~/.zshrc
export PATH="/path/to/mdslides:$PATH"

# Or install system-wide
sudo cp mdslides /usr/local/bin/
sudo cp mdslides.jar /usr/local/bin/

# Then use from anywhere
mdslides input.md output.html
```

**Windows:**
1. Add the directory containing `mdslides.bat` to your system PATH
2. Use from anywhere: `mdslides input.md output.html`

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
# Using Mill
mill cli.run input.md output.html

# Using JAR
java -jar mdslides.jar input.md output.html

# Using launcher script
./mdslides input.md output.html
```

## Verification

Test the installation:

```bash
# Create a test file
cat > test.md <<'EOF'
---
template: title
---
# Test Presentation
## Verifying MDSlides Installation
Test User
EOF

# Convert it
./mdslides test.md test.html

# Open test.html in your browser
```

You should see:
```
Reading markdown from: test.md
Parsing markdown...
✓ Parsed 1 slide(s)
Validating slide deck...
✓ Validation passed
Rendering HTML...
✓ Generated XXXX characters of HTML
Writing HTML to: test.html
✓ Successfully created presentation: test.html
```

Open `test.html` in your browser and verify:
- Title slide displays correctly
- Keyboard navigation works (→, ←, Space, Home, End)
- Slide counter shows "1 / 1"

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
java -jar /full/path/to/mdslides.jar input.md output.html
```

### "Parse error: No slides found"

Check your markdown format:
- Each slide must start with `---`
- Include frontmatter: `template: title` or `template: content`
- See [README-MVP.md](README-MVP.md) for examples

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

- Read [README-MVP.md](README-MVP.md) for usage examples
- See [example.md](example.md) for a sample presentation
- Check [doc/governance/](doc/governance/) for design decisions

## Getting Help

- **Issue Tracker**: Report bugs or request features
- **Documentation**: See README-MVP.md and CHANGELOG.md
- **Examples**: Check example.md for reference

---

**MDSlides v0.1.0-MVP** - Installation Complete!
