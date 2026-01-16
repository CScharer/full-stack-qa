#!/bin/bash
# scripts/ci/install-robot-framework.sh
# Robot Framework Installer
#
# Purpose: Install Robot Framework and dependencies for CI/CD testing
#
# Usage:
#   ./scripts/ci/install-robot-framework.sh [PYTHON_EXE]
#
# Parameters:
#   PYTHON_EXE   Python executable path (optional, defaults to python3 or python)
#
# Examples:
#   ./scripts/ci/install-robot-framework.sh
#   ./scripts/ci/install-robot-framework.sh /usr/bin/python3
#   ./scripts/ci/install-robot-framework.sh python3.13
#
# Description:
#   This script installs Robot Framework and required dependencies using pip.
#   Robot Framework is a keyword-driven test automation framework for acceptance
#   testing and acceptance test-driven development (ATDD).
#
# Dependencies:
#   - Python 3.13+ (or specified Python executable)
#   - pip (Python package manager)
#   - Internet connection (for pip package download)
#
# Output:
#   - Robot Framework installed
#   - Required dependencies installed
#   - Console output showing installation progress
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Used in CI/CD pipeline to install Robot Framework
#   - Installs Robot Framework and SeleniumLibrary
#   - Verifies installation after completion
#   - Fails if Python is not available
#
# Last Updated: January 2026

set -e

# Get Python executable path
PYTHON_EXE=${1:-$(which python3 || which python)}

if [ -z "$PYTHON_EXE" ]; then
  echo "❌ Error: Python executable not found"
  exit 1
fi

echo "Using Python: $PYTHON_EXE"
"$PYTHON_EXE" --version

# Install Robot Framework and libraries
echo "📦 Installing Robot Framework and dependencies..."
"$PYTHON_EXE" -m pip install --upgrade pip
"$PYTHON_EXE" -m pip install robotframework
"$PYTHON_EXE" -m pip install robotframework-seleniumlibrary
"$PYTHON_EXE" -m pip install robotframework-requests

# Verify installation
echo "✅ Verifying installation..."
echo "Installed Robot Framework packages:"
"$PYTHON_EXE" -m pip list | grep -i robot || echo "⚠️ Robot Framework packages not found"

# Test import
echo "🧪 Testing imports..."
"$PYTHON_EXE" -c "from robot.libraries.BuiltIn import BuiltIn; from SeleniumLibrary import SeleniumLibrary; print('✅ SeleniumLibrary imported successfully')" || {
  echo "❌ Failed to import SeleniumLibrary"
  echo "Python path: $("$PYTHON_EXE" -c 'import sys; print(\"\\n\".join(sys.path))')"
  exit 1
}

# Set Python path for Maven plugin (if needed)
echo "PYTHON_EXE=$PYTHON_EXE" >> "$GITHUB_ENV"

echo "✅ Robot Framework installation complete!"
echo "   Python executable: $PYTHON_EXE"
echo "   Python executable set in GITHUB_ENV: $PYTHON_EXE"

