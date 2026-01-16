#!/bin/bash
# Compile project without running tests

set -e

echo "🔨 Compiling CJS QA Project"
echo "==========================="

./mvnw clean compile test-compile

echo ""
echo "✅ Compilation successful!"
