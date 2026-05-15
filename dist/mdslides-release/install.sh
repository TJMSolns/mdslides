#!/bin/bash

set -e

# MDSlides Installation Script (Unix/macOS)
# This script installs MDSlides to ~/.mdslides and adds it to PATH

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INSTALL_DIR="$HOME/.mdslides"
BIN_DIR="$INSTALL_DIR/bin"

echo "════════════════════════════════════════════════════════════════"
echo "  MDSlides Installation"
echo "════════════════════════════════════════════════════════════════"

# Check Java is available
echo "Checking Java installation..."
if ! command -v java &> /dev/null; then
  echo "✗ Java not found. Please install Java 21+ and try again."
  echo "  macOS: brew install java@21"
  echo "  Linux: apt install openjdk-21-jdk (or your package manager)"
  exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo "✓ Found: $JAVA_VERSION"

# Create install directory
echo ""
echo "Installing to $INSTALL_DIR..."
mkdir -p "$BIN_DIR"
mkdir -p "$INSTALL_DIR/templates"
mkdir -p "$INSTALL_DIR/examples"
mkdir -p "$INSTALL_DIR/config"

# Copy JAR
if [ -f "$SCRIPT_DIR/mdslides.jar" ]; then
  cp "$SCRIPT_DIR/mdslides.jar" "$BIN_DIR/"
  echo "✓ Copied mdslides.jar"
else
  echo "✗ mdslides.jar not found in $SCRIPT_DIR"
  exit 1
fi

# Copy wrapper script
if [ -f "$SCRIPT_DIR/mdslides" ]; then
  cp "$SCRIPT_DIR/mdslides" "$BIN_DIR/"
  chmod +x "$BIN_DIR/mdslides"
  echo "✓ Copied mdslides wrapper"
else
  echo "✗ mdslides wrapper not found in $SCRIPT_DIR"
  exit 1
fi

# Copy templates
if [ -d "$SCRIPT_DIR/templates" ]; then
  cp -r "$SCRIPT_DIR/templates/"* "$INSTALL_DIR/templates/" 2>/dev/null || true
  echo "✓ Copied templates"
else
  echo "⚠ templates directory not found"
fi

# Copy examples
if [ -d "$SCRIPT_DIR/examples" ]; then
  cp -r "$SCRIPT_DIR/examples/"* "$INSTALL_DIR/examples/" 2>/dev/null || true
  echo "✓ Copied examples"
else
  echo "⚠ examples directory not found"
fi

# Create default config if not exists
if [ ! -f "$INSTALL_DIR/config/mdslides.conf" ]; then
  cat > "$INSTALL_DIR/config/mdslides.conf" << 'EOF'
# MDSlides Configuration
# This is the global (system-level) configuration
# Project config and CLI flags override these settings

# Default theme: light, dark, or path to custom theme
theme=light

# Copy images to output directory
copy-images=true

# Skip accessibility validation
skip-accessibility=false

# Default output directory (relative to deck file)
output-dir=./

# Template search path (colon-separated on Unix, semicolon on Windows)
# Paths are relative to ~/.mdslides
template-path=./templates/light:./templates/dark
EOF
  echo "✓ Created default config"
else
  echo "✓ Config already exists"
fi

# Attempt to add to PATH
echo ""
echo "Configuring PATH..."

SHELL_CONFIG=""
if [ -f "$HOME/.zshrc" ]; then
  SHELL_CONFIG="$HOME/.zshrc"
elif [ -f "$HOME/.bashrc" ]; then
  SHELL_CONFIG="$HOME/.bashrc"
elif [ -f "$HOME/.bash_profile" ]; then
  SHELL_CONFIG="$HOME/.bash_profile"
fi

if [ -n "$SHELL_CONFIG" ]; then
  if grep -q "$BIN_DIR" "$SHELL_CONFIG" 2>/dev/null; then
    echo "✓ PATH already configured in $SHELL_CONFIG"
  else
    echo "export PATH=\"\$PATH:$BIN_DIR\"" >> "$SHELL_CONFIG"
    echo "✓ Added $BIN_DIR to PATH in $SHELL_CONFIG"
    echo "  Run: source $SHELL_CONFIG"
  fi
else
  echo "⚠ Could not find shell config file"
  echo "  Manually add to your shell profile:"
  echo "  export PATH=\"\$PATH:$BIN_DIR\""
fi

echo ""
echo "════════════════════════════════════════════════════════════════"
echo "  Installation Complete!"
echo "════════════════════════════════════════════════════════════════"
echo ""
echo "Next steps:"
echo "  1. Reload your shell: source $SHELL_CONFIG"
echo "  2. Test installation: mdslides --version"
echo "  3. View tutorial: open $INSTALL_DIR/examples/tutorial.md"
echo "     (Rendered examples in tutorial-light/ and tutorial-dark/)"
echo ""
echo "Configuration:"
echo "  Global config: $INSTALL_DIR/config/mdslides.conf"
echo "  Templates:     $INSTALL_DIR/templates/"
echo "  Examples:      $INSTALL_DIR/examples/"
echo ""
echo "Quick start:"
echo "  mdslides my-deck.md"
echo ""
