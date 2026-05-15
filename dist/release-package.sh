#!/bin/bash

set -e

# MDSlides Release Package Script
# Builds the JAR, renders tutorial examples, and packages the distribution

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIST_DIR="$PROJECT_ROOT/dist"
RELEASE_DIR="$DIST_DIR/mdslides-release"
BUILD_OUTPUT="$PROJECT_ROOT/out"

echo "════════════════════════════════════════════════════════════════"
echo "  MDSlides Release Package Builder"
echo "════════════════════════════════════════════════════════════════"
echo ""

# Step 1: Build JAR
echo "[1/4] Building mdslides.jar..."
cd "$PROJECT_ROOT"
mill cli.assembly

# Find the JAR file (it's usually in out/cli/assembly.dest/)
JAR_FILE=$(find "$BUILD_OUTPUT" -name "*.jar" -type f | head -1)
if [ -z "$JAR_FILE" ]; then
  echo "Error: JAR file not found after build"
  exit 1
fi

echo "✓ JAR built: $JAR_FILE"

# Copy JAR to release directory
echo "Copying JAR to release directory..."
cp "$JAR_FILE" "$RELEASE_DIR/mdslides.jar"
echo "✓ Copied to $RELEASE_DIR/mdslides.jar"

# Step 2: Copy templates
echo ""
echo "[2/4] Copying templates..."
if [ -d "$PROJECT_ROOT/themes/light" ]; then
  rm -rf "$RELEASE_DIR/templates/light"/*
  cp -r "$PROJECT_ROOT/themes/light" "$RELEASE_DIR/templates/light/"
  echo "✓ Copied light theme"
fi

if [ -d "$PROJECT_ROOT/themes/dark" ]; then
  rm -rf "$RELEASE_DIR/templates/dark"/*
  cp -r "$PROJECT_ROOT/themes/dark" "$RELEASE_DIR/templates/dark/"
  echo "✓ Copied dark theme"
fi

# Step 3: Copy and render tutorial
echo ""
echo "[3/4] Rendering tutorial examples..."
echo "  Rendering with light theme..."
cd "$PROJECT_ROOT"
mill cli.run examples/mdslides-tutorial.md --output "$RELEASE_DIR/examples/tutorial-light" --theme light --no-copy-images
echo "  ✓ Light theme rendered"

echo "  Rendering with dark theme..."
mill cli.run examples/mdslides-tutorial.md --output "$RELEASE_DIR/examples/tutorial-dark" --theme dark --no-copy-images
echo "  ✓ Dark theme rendered"

# Copy tutorial source
cp "$PROJECT_ROOT/examples/mdslides-tutorial.md" "$RELEASE_DIR/examples/tutorial.md"
echo "✓ Tutorial rendered and copied"

# Step 4: Verify package contents
echo ""
echo "[4/4] Verifying package contents..."
CHECKS=0
PASSED=0

check_file() {
  ((CHECKS++))
  if [ -f "$1" ]; then
    echo "  ✓ $2"
    ((PASSED++))
  else
    echo "  ✗ Missing: $2"
  fi
}

check_dir() {
  ((CHECKS++))
  if [ -d "$1" ] && [ "$(ls -A "$1" 2>/dev/null)" ]; then
    echo "  ✓ $2"
    ((PASSED++))
  else
    echo "  ✗ Missing/empty: $2"
  fi
}

check_file "$RELEASE_DIR/mdslides.jar" "mdslides.jar"
check_file "$RELEASE_DIR/mdslides" "mdslides (Unix wrapper)"
check_file "$RELEASE_DIR/mdslides.bat" "mdslides.bat (Windows wrapper)"
check_file "$RELEASE_DIR/install.sh" "install.sh"
check_file "$RELEASE_DIR/install.bat" "install.bat"
check_file "$RELEASE_DIR/examples/tutorial.md" "tutorial.md (source)"
check_dir "$RELEASE_DIR/examples/tutorial-light" "tutorial-light/ (rendered)"
check_dir "$RELEASE_DIR/examples/tutorial-dark" "tutorial-dark/ (rendered)"
check_dir "$RELEASE_DIR/templates/light" "templates/light/"
check_dir "$RELEASE_DIR/templates/dark" "templates/dark/"

echo ""
echo "Verification: $PASSED/$CHECKS checks passed"

if [ $PASSED -eq $CHECKS ]; then
  echo ""
  echo "════════════════════════════════════════════════════════════════"
  echo "  Release Package Ready!"
  echo "════════════════════════════════════════════════════════════════"
  echo ""
  echo "Package location: $RELEASE_DIR"
  echo ""
  echo "To create a distribution archive:"
  echo "  cd $DIST_DIR"
  echo "  tar -czf mdslides-v0.2.0.tar.gz mdslides-release/"
  echo ""
  echo "Or for Windows:"
  echo "  cd $DIST_DIR"
  echo "  zip -r mdslides-v0.2.0.zip mdslides-release/"
  echo ""
  echo "Users can then:"
  echo "  1. Extract the archive"
  echo "  2. Run: bash install.sh (Unix/macOS) or install.bat (Windows)"
  echo ""
else
  echo ""
  echo "✗ Some checks failed. Review the output above."
  exit 1
fi
