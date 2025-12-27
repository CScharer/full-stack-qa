#!/bin/bash
# Install Robot Framework Dependencies
# Usage: ./scripts/ci/install-robot-framework.sh [python-exe]
#
# Arguments:
#   python-exe     - Python executable path (optional, defaults to python3 or python)
#
# Examples:
#   ./scripts/ci/install-robot-framework.sh
#   ./scripts/ci/install-robot-framework.sh /usr/bin/python3

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

