#!/bin/bash
# Create Allure Environment Properties File
# Usage: ./scripts/ci/create-allure-env-properties.sh <environment> <browser> <hub-port> <base-url> <test-suite> [additional-properties...]
#
# Arguments:
#   environment    - Test environment (dev, test, prod)
#   browser        - Browser name (chrome, firefox, edge, etc.)
#   hub-port       - Selenium Hub port (default: 4444)
#   base-url       - Base URL for the environment
#   test-suite     - Test suite name (smoke, grid, mobile-browser, responsive, selenide)
#   additional-properties - Optional key=value pairs for additional properties
#
# Examples:
#   ./scripts/ci/create-allure-env-properties.sh test chrome 4444 http://localhost:3004 grid
#   ./scripts/ci/create-allure-env-properties.sh dev firefox 4444 http://localhost:3003 mobile-browser "Device.Type=Mobile Viewport"
#   ./scripts/ci/create-allure-env-properties.sh test chrome 4444 http://localhost:3004 selenide "Framework=Selenide"

set -e

# Parse arguments
ENVIRONMENT=${1}
BROWSER=${2:-chrome}
HUB_PORT=${3:-4444}
BASE_URL=${4}
TEST_SUITE=${5}
ADDITIONAL_PROPS="${@:6}"  # All remaining arguments

# Validate required arguments
if [ -z "$ENVIRONMENT" ] || [ -z "$BASE_URL" ] || [ -z "$TEST_SUITE" ]; then
  echo "❌ Error: Environment, base URL, and test suite are required"
  echo "Usage: $0 <environment> <browser> <hub-port> <base-url> <test-suite> [additional-properties...]"
  exit 1
fi

# Create directory if it doesn't exist
mkdir -p target/allure-results

# Create environment properties file
cat > target/allure-results/environment.properties << EOF
Environment=$ENVIRONMENT
Browser=$BROWSER
Selenium.Hub=localhost:$HUB_PORT
Base.URL=$BASE_URL
Test.Suite=$TEST_SUITE
Execution.Type=CI/CD
EOF

# Add additional properties if provided
if [ -n "$ADDITIONAL_PROPS" ]; then
  for prop in $ADDITIONAL_PROPS; do
    echo "$prop" >> target/allure-results/environment.properties
  done
fi

echo "✅ Created Allure environment properties file: target/allure-results/environment.properties"
echo "   Environment: $ENVIRONMENT"
echo "   Browser: $BROWSER"
echo "   Test Suite: $TEST_SUITE"
echo "   Base URL: $BASE_URL"

