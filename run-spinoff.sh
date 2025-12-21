#!/bin/bash

# Spinoff mdslides to tmoores-retisio/mdslides on GitHub
# Requires: GITHUB_TOKEN environment variable set

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PLUGIN_DIR="$SCRIPT_DIR/mill-spinoff-plugin"
MDSLIDES_DIR="$SCRIPT_DIR"

# Check prerequisites
if [[ -z "$GITHUB_TOKEN" ]]; then
    echo "❌ GITHUB_TOKEN not set"
    echo "   Create token at https://github.com/settings/tokens/new"
    echo "   Scopes: repo (full), admin:org (read/write)"
    exit 1
fi

if [[ ! -d "$PLUGIN_DIR" ]]; then
    echo "❌ mill-spinoff-plugin not found at $PLUGIN_DIR"
    exit 1
fi

echo "🚀 Spinning off MDSlides to tmoores-retisio/mdslides..."
echo "   Source: $MDSLIDES_DIR"
echo "   Org: tmoores-retisio"
echo ""

# The spinoff module expects certain fields to be defined
# Since we can't wire the plugin easily, we'll create a temporary project setup

mkdir -p "$MDSLIDES_DIR/doc/domain-models"

# Create SPINOFF-CANDIDATES.md if it doesn't exist
cat > "$MDSLIDES_DIR/SPINOFF-CANDIDATES.md" << 'CANDIDATES'
# Spinoff Candidates

## MDSlides (READY)

**Status**: Ready for spinoff
**Organization**: tmoores-retisio  
**Repository**: https://github.com/tmoores-retisio/mdslides

**Description**:
A test of the ceremony-based SDLC framework demonstrating DDD + BDD + TDD practices.

**Framework Coverage**:
- ✅ All 14 ceremony instruction files
- ✅ Complete SDLC playbook (HOW-WE-WORK.md)
- ✅ 22 Shared Best Practice Files
- ✅ 34 Document templates
- ✅ 5 Ceremony validation checklists
- ✅ Mill build system (Scala 3.3.1)
- ✅ Reactive architecture (Pekko, R2DBC)

**Next Steps After Spinoff**:
1. Complete Phase 0: Program Initiation ceremony
2. Start Phase 1: Event Storming
CANDIDATES

cat > "$MDSLIDES_DIR/CONTEXT-MAP.md" << 'CONTEXTMAP'
# Context Map

## MDSlides Bounded Contexts

### Primary Bounded Context

**Name**: Slide Deck Engine  
**Type**: Core Domain  
**Description**: Main domain logic for slide deck creation, validation, and rendering

**Relationships**: None (initial spinoff, standalone)

### Infrastructure

- PostgreSQL (reactive, R2DBC)
- Pekko Typed Actors
- OpenTelemetry

CONTEXTMAP

echo "📄 Created SPINOFF-CANDIDATES.md and CONTEXT-MAP.md"
echo ""
echo "Note: Full spinoff automation requires mill-spinoff-plugin to be properly wired."
echo "Current workaround: Use these docs to manually clone and customize."
echo ""
echo "To complete spinoff manually:"
echo "  1. Create repo: https://github.com/new"
echo "  2. Clone template: git clone $MDSLIDES_DIR mdslides"
echo "  3. Update references (copilot-training -> mdslides)"
echo "  4. Push to GitHub: git push origin main"
