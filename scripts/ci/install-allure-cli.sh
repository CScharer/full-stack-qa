#!/bin/bash
# scripts/ci/install-allure-cli.sh
# Installs Allure CLI tool (supports both Allure2 and Allure3)
# Reads configuration from config/environments.json

set -e

# Read from config file, fallback to environment variable, then default
if [ -f "config/environments.json" ]; then
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-$(jq -r '.allure.reportVersion // 3' config/environments.json)}"
    ALLURE2_CLI_VERSION="${ALLURE2_CLI_VERSION:-$(jq -r '.allure.cliVersion."2" // "2.36.0"' config/environments.json)}"
    ALLURE3_CLI_VERSION="${ALLURE3_CLI_VERSION:-$(jq -r '.allure.cliVersion."3" // "3.0.0"' config/environments.json)}"
else
    # Fallback if config file doesn't exist
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-3}"
    ALLURE2_CLI_VERSION="${ALLURE2_CLI_VERSION:-2.36.0}"
    ALLURE3_CLI_VERSION="${ALLURE3_CLI_VERSION:-3.0.0}"
fi

# Allow override via command line argument
if [ -n "$1" ]; then
    ALLURE_VERSION="$1"
fi

echo "📦 Installing Allure CLI (Version: $ALLURE_VERSION)..."

if [ "$ALLURE_VERSION" = "2" ]; then
    # Install Allure2
    ALLURE_CLI_VERSION="${ALLURE_CLI_VERSION:-$ALLURE2_CLI_VERSION}"
    
    echo "📥 Installing Allure2 CLI version $ALLURE_CLI_VERSION..."
    
    # Check if Java is available
    if ! command -v java &> /dev/null; then
        echo "❌ Java is not installed. Allure2 requires Java runtime."
        exit 1
    fi
    
    echo "✅ Java version: $(java -version 2>&1 | head -1)"
    
    # Determine OS and architecture
    OS="$(uname -s | tr '[:upper:]' '[:lower:]')"
    ARCH="$(uname -m)"
    
    case "$ARCH" in
        x86_64) ARCH="x64" ;;
        aarch64|arm64) ARCH="arm64" ;;
        *) echo "❌ Unsupported architecture: $ARCH"; exit 1 ;;
    esac
    
    # Download and install Allure2
    DOWNLOAD_URL="https://github.com/allure-framework/allure2/releases/download/${ALLURE_CLI_VERSION}/allure-${ALLURE_CLI_VERSION}.tgz"
    TEMP_DIR=$(mktemp -d)
    cd "$TEMP_DIR"
    
    echo "📥 Downloading Allure2 CLI from GitHub releases..."
    DOWNLOADED=false
    
    # Try .tgz first
    if wget -q "$DOWNLOAD_URL" -O allure.tgz 2>/dev/null && [ -f "allure.tgz" ] && [ -s "allure.tgz" ]; then
        echo "📦 Extracting Allure2 CLI (.tgz)..."
        tar -xzf allure.tgz
        ARCHIVE_NAME="allure-${ALLURE_CLI_VERSION}"
        DOWNLOADED=true
    else
        # Try .zip as fallback
        echo "⚠️  .tgz download failed, trying .zip format..."
        DOWNLOAD_URL="https://github.com/allure-framework/allure2/releases/download/${ALLURE_CLI_VERSION}/allure-${ALLURE_CLI_VERSION}.zip"
        if wget -q "$DOWNLOAD_URL" -O allure.zip 2>/dev/null && [ -f "allure.zip" ] && [ -s "allure.zip" ]; then
            echo "📦 Extracting Allure2 CLI (.zip)..."
            unzip -q allure.zip
            ARCHIVE_NAME="allure-${ALLURE_CLI_VERSION}"
            DOWNLOADED=true
        fi
    fi
    
    if [ "$DOWNLOADED" = false ] || [ ! -d "$ARCHIVE_NAME" ]; then
        echo "❌ Failed to download or extract Allure2 ${ALLURE_CLI_VERSION}"
        echo "💡 Check available versions at: https://github.com/allure-framework/allure2/releases"
        cd -
        rm -rf "$TEMP_DIR"
        exit 1
    fi
    
    # Install to /opt/allure (consistent with existing script)
    echo "📁 Installing to /opt/allure..."
    sudo rm -rf /opt/allure
    sudo mv "$ARCHIVE_NAME" /opt/allure
    sudo ln -sf /opt/allure/bin/allure /usr/local/bin/allure
    
    cd -
    rm -rf "$TEMP_DIR"
    
    echo "✅ Allure2 CLI installed successfully"
    
elif [ "$ALLURE_VERSION" = "3" ]; then
    # Install Allure3
    ALLURE_CLI_VERSION="${ALLURE_CLI_VERSION:-$ALLURE3_CLI_VERSION}"
    
    echo "📥 Installing Allure3 CLI version $ALLURE_CLI_VERSION..."
    
    # Check if npm is available
    if ! command -v npm &> /dev/null; then
        echo "❌ npm is not installed. Installing Node.js and npm..."
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
    
    # Install Allure3 via npm
    if [ "$ALLURE_CLI_VERSION" = "latest" ] || [ -z "$ALLURE_CLI_VERSION" ]; then
        sudo npm install -g allure@latest
    else
        sudo npm install -g "allure@${ALLURE_CLI_VERSION}"
    fi
    
    echo "✅ Allure3 CLI installed successfully"
    
else
    echo "❌ Invalid ALLURE_REPORT_VERSION: $ALLURE_VERSION (must be 2 or 3)"
    exit 1
fi

# Verify installation
if ! command -v allure &> /dev/null; then
    echo "❌ Allure CLI not found in PATH after installation"
    exit 1
fi

echo "✅ Allure CLI installed successfully"
allure --version

echo ""
echo "📊 Allure Information:"
echo "   Version: $ALLURE_VERSION"
if [ "$ALLURE_VERSION" = "2" ]; then
    echo "   Type: Java-based CLI"
    echo "   Installation: Binary download"
    echo "   Repository: https://github.com/allure-framework/allure2"
else
    echo "   Type: TypeScript-based CLI"
    echo "   Installation: npm"
    echo "   Repository: https://github.com/allure-framework/allure3"
fi
