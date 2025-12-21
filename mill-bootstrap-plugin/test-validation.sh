#!/usr/bin/env bash
set -e

# Test script for mill-bootstrap-plugin
# Tests the validate command with various project names

echo "🧪 Testing mill-bootstrap-plugin validation"
echo "=========================================="
echo ""

cd /home/tjm/Cloud/GitHub/copilot-training/mill-bootstrap-plugin

# Test 1: Valid project name
echo "Test 1: Valid kebab-case project name"
mill millBootstrapPlugin.run com.retisio.mill.TestValidation test-project-123
echo ""

# Test 2: Invalid - uppercase
echo "Test 2: Invalid - uppercase letters"
mill millBootstrapPlugin.run com.retisio.mill.TestValidation Test-Project || true
echo ""

# Test 3: Invalid - too short
echo "Test 3: Invalid - name too short"
mill millBootstrapPlugin.run com.retisio.mill.TestValidation ab || true
echo ""

# Test 4: Invalid - underscore
echo "Test 4: Invalid - contains underscore"
mill millBootstrapPlugin.run com.retisio.mill.TestValidation test_project || true
echo ""

# Test 5: Invalid - consecutive hyphens
echo "Test 5: Invalid - consecutive hyphens"
mill millBootstrapPlugin.run com.retisio.mill.TestValidation test--project || true
echo ""

echo "✅ Validation tests complete!"
