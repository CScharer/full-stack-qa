#!/bin/bash
# scripts/ci/install-allure3-cli.sh
# Installs Allure3 CLI tool via npm

set -e

ALLURE_VERSION="${1:-3.0.0}"

echo "📦 Installing Allure3 CLI version $ALLURE_VERSION via npm..."

# Check if npm is available
if ! command -v npm &> /dev/null; then
    echo "❌ npm is not installed. Installing Node.js and npm..."
    # Install Node.js (which includes npm) using NodeSource repository
    curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
    sudo apt-get install -y nodejs
fi

# Verify npm is available
if ! command -v npm &> /dev/null; then
    echo "❌ Failed to install npm"
    exit 1
fi

echo "✅ npm version: $(npm --version)"
echo "✅ Node.js version: $(node --version)"

# Install Allure3 globally via npm
echo "📥 Installing Allure3 CLI version $ALLURE_VERSION..."
if [ "$ALLURE_VERSION" = "latest" ] || [ -z "$ALLURE_VERSION" ]; then
    sudo npm install -g allure@latest
else
    sudo npm install -g "allure@${ALLURE_VERSION}"
fi

# Verify installation
if ! command -v allure &> /dev/null; then
    echo "❌ Allure3 CLI not found in PATH after installation"
    exit 1
fi

echo "✅ Allure3 CLI installed successfully"
allure --version

# Show Allure3 specific information
echo ""
echo "📊 Allure3 Information:"
echo "   Type: TypeScript-based CLI"
echo "   Installation: npm"
echo "   Compatibility: Reads Allure2 result files"
echo "   Repository: https://github.com/allure-framework/allure3"

