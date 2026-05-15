# MDSlides Installation & Setup Guide

Welcome to MDSlides! This guide walks you through installation and initial setup.

## System Requirements

- **Java 21+** (free, open-source)
- **macOS 10.12+**, **Linux**, or **Windows 10+**
- **~200MB disk space** (includes themes and tutorial)

## Installation

Choose your operating system below.

### macOS / Linux

1. **Extract the archive:**
   ```bash
   tar -xzf mdslides-v0.2.0.tar.gz
   cd mdslides-release
   ```

2. **Run the installer:**
   ```bash
   bash install.sh
   ```

3. **Reload your shell:**
   ```bash
   source ~/.zshrc    # or ~/.bashrc, depending on your shell
   ```

4. **Verify installation:**
   ```bash
   mdslides --version
   ```

### Windows

1. **Extract the archive:**
   - Right-click `mdslides-v0.2.0.zip`
   - Select "Extract All..."
   - Choose a location, click "Extract"

2. **Run the installer:**
   - Navigate to the extracted `mdslides-release` folder
   - Double-click `install.bat`
   - A command prompt will open—follow the prompts

3. **Restart your terminal:**
   - Close and reopen Command Prompt or PowerShell

4. **Verify installation:**
   ```cmd
   mdslides --version
   ```

## First Run

After installation, create your first presentation:

```bash
# Using the tutorial example
mdslides ~/.mdslides/examples/tutorial.md

# Using your own markdown file
mdslides my-presentation.md
```

The presentation opens in your default browser. Use arrow keys to navigate.

## Configuration

MDSlides uses a **4-tier configuration system**:

### 1. Global (System-Level) Config
**File:** `~/.mdslides/config/mdslides.conf` (macOS/Linux) or `%APPDATA%\mdslides\config\mdslides.conf` (Windows)

```properties
# Default theme
theme=light

# Copy images to output (true/false)
copy-images=true

# Skip accessibility validation (not recommended)
skip-accessibility=false

# Where to find templates
template-path=./templates/light:./templates/dark
```

### 2. Project Config
Create `mdslides.conf` in your project root:

```properties
theme=dark
output-dir=./presentations/
```

### 3. Deck Metadata
Front-matter in your markdown:

```markdown
---
theme: dark
copy-images: false
---

# Your Presentation Title
```

### 4. CLI Flags (Highest Priority)
```bash
mdslides my-deck.md --theme dark --output ./build/
```

**Priority (high to low):**
1. CLI flags
2. Deck metadata
3. Project config
4. Global config

## Templates

Two themes are included:

- **Light** - Professional light background (default)
- **Dark** - Dark background with light text

### Using Themes

Specify in your markdown or config:

```bash
mdslides deck.md --theme dark
```

Or in your config file:
```properties
theme=dark
```

## Tutorial

After installation, view the pre-rendered tutorial:

```bash
# Light theme
open ~/.mdslides/examples/tutorial-light/index.html

# Dark theme
open ~/.mdslides/examples/tutorial-dark/index.html
```

Source markdown: `~/.mdslides/examples/tutorial.md`

## Common Issues

### "mdslides: command not found"

**Cause:** Command is not in your PATH

**Solution:**
```bash
# Add to your shell profile (~/.zshrc, ~/.bashrc, etc.)
export PATH="$PATH:$HOME/.mdslides/bin"

# Then reload:
source ~/.zshrc
```

### "Java not found"

**Cause:** Java 21+ is not installed

**Solution:**

**macOS:**
```bash
brew install java@21
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

**Fedora/RHEL:**
```bash
sudo dnf install java-21-openjdk
```

**Windows:**
- Download from [jdk.java.net/21](https://jdk.java.net/21/)
- Or use Chocolatey: `choco install java17`

### "Cannot write to output directory"

**Cause:** No write permissions or directory doesn't exist

**Solution:**
```bash
# Create output directory
mkdir -p output/

# Check permissions
ls -l output/

# Or change theme/theme precedence in config
```

### Images not found

**Cause:** Image paths are relative to markdown file, but directory structure is wrong

**Solution:**
- Use paths relative to your markdown file
- Example: `![alt](./images/logo.png)` if `images/` is in the same folder
- Or use absolute paths: `![alt](/full/path/to/image.png)`

## Uninstall

### macOS / Linux
```bash
rm -rf ~/.mdslides
```

Then remove from your shell config (`~/.zshrc` or `~/.bashrc`):
```bash
# Remove this line:
export PATH="$PATH:$HOME/.mdslides/bin"
```

### Windows
1. Open Settings → System → Advanced system settings
2. Click "Environment Variables"
3. Edit `Path` and remove the mdslides bin directory
4. Delete `%APPDATA%\mdslides` folder

## Getting Help

- **Examples:** `~/.mdslides/examples/`
- **Rendered tutorial:** `~/.mdslides/examples/tutorial-light/` (light theme)
- **GitHub:** https://github.com/tjm/mdslides
- **Issues:** https://github.com/tjm/mdslides/issues

## Next Steps

1. ✅ Explore the tutorial examples
2. 📝 Create your first presentation
3. 🎨 Customize with themes and templates
4. 🚀 Share your presentation

Happy presenting!
