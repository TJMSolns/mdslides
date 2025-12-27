#!/bin/bash
# Generate MDSlides tutorial with all 4 themes for comparison
# Usage: ./generate-all-themes.sh

set -e

echo "MDSlides Tutorial - Generating all theme variations..."
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

JAR_PATH="$PROJECT_ROOT/out/cli/assembly.super/mill/scalalib/JavaModule/assembly.dest/out.jar"
INPUT="$SCRIPT_DIR/mdslides-tutorial.md"
OUTPUT_DIR="$SCRIPT_DIR/output"

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Change to project root so themes/ directory is found
cd "$PROJECT_ROOT"

# Generate Light theme
echo "🎨 Generating Light theme..."
java -jar "$JAR_PATH" \
  "$INPUT" \
  "$OUTPUT_DIR/tutorial-light.html" \
  --theme light
echo ""

# Generate Dark theme
echo "🎨 Generating Dark theme..."
java -jar "$JAR_PATH" \
  "$INPUT" \
  "$OUTPUT_DIR/tutorial-dark.html" \
  --theme dark
echo ""

# Generate TJM Solutions theme
echo "🎨 Generating TJM Solutions theme..."
java -jar "$JAR_PATH" \
  "$INPUT" \
  "$OUTPUT_DIR/tutorial-tjm-solutions.html" \
  --theme tjm-solutions
echo ""

# Generate Retisio theme
echo "🎨 Generating Retisio theme..."
java -jar "$JAR_PATH" \
  "$INPUT" \
  "$OUTPUT_DIR/tutorial-retisio.html" \
  --theme retisio
echo ""

echo "✅ All themes generated successfully!"
echo ""
echo "Output files:"
ls -lh "$OUTPUT_DIR"/*.html | awk '{print "  - " $9 " (" $5 ")"}'
echo ""
echo "View with:"
echo "  xdg-open $OUTPUT_DIR/tutorial-light.html"
echo "  xdg-open $OUTPUT_DIR/tutorial-dark.html"
echo "  xdg-open $OUTPUT_DIR/tutorial-tjm-solutions.html"
echo "  xdg-open $OUTPUT_DIR/tutorial-retisio.html"
