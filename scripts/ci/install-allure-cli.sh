#!/bin/bash
# scripts/ci/install-allure-cli.sh
# Installs Allure CLI tool

set -e

ALLURE_VERSION="${1:-2.25.0}"

echo "📦 Installing Allure CLI version $ALLURE_VERSION..."

DOWNLOAD_URL_TGZ="https://github.com/allure-framework/allure2/releases/download/${ALLURE_VERSION}/allure-${ALLURE_VERSION}.tgz"
DOWNLOAD_URL_ZIP="https://github.com/allure-framework/allure2/releases/download/${ALLURE_VERSION}/allure-${ALLURE_VERSION}.zip"
ARCHIVE_NAME="allure-${ALLURE_VERSION}"
DOWNLOADED=false

# Try downloading .tgz first
echo "📥 Attempting to download ${ALLURE_VERSION}.tgz..."
if wget -q "$DOWNLOAD_URL_TGZ" -O "${ARCHIVE_NAME}.tgz" 2>&1; then
  if [ -f "${ARCHIVE_NAME}.tgz" ] && [ -s "${ARCHIVE_NAME}.tgz" ]; then
    echo "📦 Extracting ${ARCHIVE_NAME}.tgz..."
    tar -zxf "${ARCHIVE_NAME}.tgz"
    rm -f "${ARCHIVE_NAME}.tgz"
    DOWNLOADED=true
  fi
fi

# If .tgz failed, try .zip
if [ "$DOWNLOADED" = false ]; then
  echo "⚠️  .tgz download failed, trying .zip format..."
  echo "📥 Attempting to download ${ALLURE_VERSION}.zip..."
  if wget -q "$DOWNLOAD_URL_ZIP" -O "${ARCHIVE_NAME}.zip" 2>&1; then
    if [ -f "${ARCHIVE_NAME}.zip" ] && [ -s "${ARCHIVE_NAME}.zip" ]; then
      echo "📦 Extracting ${ARCHIVE_NAME}.zip..."
      unzip -q "${ARCHIVE_NAME}.zip"
      rm -f "${ARCHIVE_NAME}.zip"
      DOWNLOADED=true
    fi
  fi
fi

# Verify download and extraction succeeded
if [ "$DOWNLOADED" = false ] || [ ! -d "$ARCHIVE_NAME" ]; then
  echo "❌ Failed to download or extract Allure ${ALLURE_VERSION}"
  echo "💡 Check available versions at: https://github.com/allure-framework/allure2/releases"
  exit 1
fi

# Install Allure
echo "📦 Installing Allure to /opt/allure..."
sudo rm -rf /opt/allure
sudo mv "$ARCHIVE_NAME" /opt/allure
sudo ln -sf /opt/allure/bin/allure /usr/local/bin/allure

# Verify installation
if ! command -v allure &> /dev/null; then
  echo "❌ Allure CLI not found in PATH after installation"
  exit 1
fi

echo "✅ Allure CLI installed successfully"
allure --version
