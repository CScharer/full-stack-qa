#!/bin/bash
# scripts/build/compile.sh
# Project Compilation Script
#
# Purpose: Compile the project (main and test code) without running tests
#
# Usage:
#   ./scripts/build/compile.sh
#
# Description:
#   This script compiles both main source code and test source code using Maven.
#   Tests are not executed, making this useful for quick compilation checks.
#
# Examples:
#   ./scripts/build/compile.sh
#
# Dependencies:
#   - Maven wrapper (./mvnw)
#   - Java 21+
#   - All project dependencies (downloaded automatically by Maven)
#
# Output:
#   - Compiled classes in target/classes/ (main code)
#   - Compiled classes in target/test-classes/ (test code)
#   - Exit code: 0 on success, non-zero on compilation failure
#
# Notes:
#   - Faster than running tests
#   - Useful for syntax checking and dependency validation
#   - Cleans previous build artifacts before compiling
#
# Last Updated: January 2026

set -e

echo "🔨 Compiling CJS QA Project"
echo "==========================="

./mvnw clean compile test-compile

echo ""
echo "✅ Compilation successful!"
