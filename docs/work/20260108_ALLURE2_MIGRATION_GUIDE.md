# Allure2 Migration Guide

**Date Created**: 2026-01-08  
**Status**: 📋 Migration Planning Document  
**Purpose**: Detailed guide for migrating from Allure3 CLI to Allure2 CLI  
**Current Setup**: Allure3 CLI 3.0.0 (TypeScript-based, npm install)  
**Target Setup**: Allure2 CLI 2.36.0 (Java-based, binary download)

---

## 📋 Executive Summary

This document outlines all changes required to migrate from **Allure3 CLI** to **Allure2 CLI** for report generation. The migration involves:

- **CLI Installation**: Switch from npm-based Allure3 to binary-based Allure2
- **Configuration Files**: Remove Allure3 config files, use Allure2 command-line options
- **History Format**: Allure2 uses different history file structure (JSON files vs JSONL)
- **Scripts**: Update all scripts that reference Allure3 CLI
- **CI/CD Pipelines**: Update GitHub Actions workflows
- **Report Generation**: Update command syntax and options

**Important Notes**:
- ✅ **Java Libraries Unchanged**: Maven dependencies (`allure-testng:2.32.0`) remain the same
- ✅ **Test Code Unchanged**: All Allure annotations work identically
- ✅ **Result Files Compatible**: Both Allure2 and Allure3 read the same result file format
- ⚠️ **History Format Different**: Allure2 uses individual JSON files, Allure3 uses JSONL

---

## 🔄 Key Differences: Allure2 vs Allure3

### CLI Installation

| Aspect | Allure3 | Allure2 |
|--------|---------|---------|
| **Type** | TypeScript-based | Java-based |
| **Installation** | `npm install -g allure` | Download binary from GitHub releases |
| **Version** | 3.0.0 | 2.36.0 (latest stable) |
| **Repository** | `allure-framework/allure3` | `allure-framework/allure2` |
| **Dependencies** | Requires Node.js/npm | Requires Java runtime |

### Configuration

| Aspect | Allure3 | Allure2 |
|--------|---------|---------|
| **Config File** | `allure.config.ts` or `allure.config.js` | No config file (command-line options) |
| **History Path** | `historyPath: "./history/history.jsonl"` | `--history-dir` flag |
| **Append History** | `appendHistory: true` in config | Automatic (default behavior) |

### History Format

| Aspect | Allure3 | Allure2 |
|--------|---------|---------|
| **Format** | `history.jsonl` (JSON Lines) | Individual `{md5-hash}.json` files |
| **Location** | `history/history.jsonl` | `history/{md5-hash}.json` |
| **Structure** | Single file with all history | Multiple files, one per test |
| **Trend Files** | `history-trend.json`, `duration-trend.json` | Generated automatically from history files |

### CLI Commands

| Command | Allure3 | Allure2 |
|---------|---------|---------|
| **Generate** | `allure generate <results> -o <report>` | `allure generate <results> -o <report>` |
| **Serve** | `allure serve <results>` | `allure serve <results>` |
| **Config** | `--config allure.config.ts` | `--history-dir <dir>` (if needed) |
| **Version** | `allure --version` | `allure --version` |

---

## 📝 Required Changes

### 1. Remove Allure3 Configuration Files

**Files to Remove**:
- `allure.config.ts`
- `allure.config.js`

**Reason**: Allure2 doesn't use configuration files. History is managed automatically.

**Action**:
```bash
rm allure.config.ts allure.config.js
```

---

### 2. Update CLI Installation Script

**File**: `scripts/ci/install-allure3-cli.sh`

**Current (Allure3)**:
```bash
#!/bin/bash
# Installs Allure3 CLI tool via npm
npm install -g allure@3.0.0
```

**New (Allure2)**: Create or update `scripts/ci/install-allure2-cli.sh`:
```bash
#!/bin/bash
# scripts/ci/install-allure2-cli.sh
# Installs Allure2 CLI tool (binary download)

set -e

ALLURE_VERSION="${1:-2.36.0}"

echo "📦 Installing Allure2 CLI version $ALLURE_VERSION..."

# Check if Java is available (required for Allure2)
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Allure2 requires Java runtime."
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -1)"

# Determine OS and architecture
OS="$(uname -s | tr '[:upper:]' '[:lower:]')"
ARCH="$(uname -m)"

case "$ARCH" in
    x86_64)
        ARCH="x64"
        ;;
    aarch64|arm64)
        ARCH="arm64"
        ;;
    *)
        echo "❌ Unsupported architecture: $ARCH"
        exit 1
        ;;
esac

# Download URL
DOWNLOAD_URL="https://github.com/allure-framework/allure2/releases/download/${ALLURE_VERSION}/allure-${ALLURE_VERSION}.tgz"

# Download and extract
TEMP_DIR=$(mktemp -d)
cd "$TEMP_DIR"

echo "📥 Downloading Allure2 CLI from GitHub releases..."
wget -q "$DOWNLOAD_URL" -O allure.tgz

echo "📦 Extracting Allure2 CLI..."
tar -xzf allure.tgz

# Install to /usr/local/bin
echo "📁 Installing to /usr/local/bin..."
sudo cp -r allure-${ALLURE_VERSION}/* /usr/local/
sudo ln -sf /usr/local/bin/allure /usr/local/bin/allure

# Verify installation
if ! command -v allure &> /dev/null; then
    echo "❌ Allure2 CLI not found in PATH after installation"
    exit 1
fi

echo "✅ Allure2 CLI installed successfully"
allure --version

# Show Allure2 specific information
echo ""
echo "📊 Allure2 Information:"
echo "   Type: Java-based CLI"
echo "   Installation: Binary download"
echo "   Compatibility: Reads Allure2 result files"
echo "   Repository: https://github.com/allure-framework/allure2"

# Cleanup
cd -
rm -rf "$TEMP_DIR"
```

**Action**:
1. Rename or create new script: `scripts/ci/install-allure2-cli.sh`
2. Update all references from `install-allure3-cli.sh` to `install-allure2-cli.sh`

---

### 3. Update Report Generation Script

**File**: `scripts/ci/generate-combined-allure-report.sh`

**Key Changes**:

#### 3.1 Remove Allure3 Config File Detection

**Remove** (lines ~196-210):
```bash
CONFIG_FLAG=""
CONFIG_FILE=""
# Try TypeScript config first, then JavaScript
if [ -f "allure.config.ts" ]; then
    echo "   ✅ Found allure.config.ts - using explicit --config flag"
    CONFIG_FILE="allure.config.ts"
    CONFIG_FLAG="--config allure.config.ts"
    ...
elif [ -f "allure.config.js" ]; then
    ...
fi
```

**Replace with**:
```bash
# Allure2 doesn't use config files - history is managed automatically
echo "   Using Allure2 CLI (no config file needed)"
```

#### 3.2 Update History Format Handling

**Current (Allure3)** - expects `history.jsonl`:
```bash
if [ -f "$RESULTS_DIR/history/history.jsonl" ]; then
    # Process history.jsonl
fi
```

**New (Allure2)** - expects individual JSON files:
```bash
if [ -d "$RESULTS_DIR/history" ] && [ "$(find "$RESULTS_DIR/history" -name "*.json" -type f | wc -l | tr -d ' ')" -gt 0 ]; then
    # Process individual history JSON files
    HISTORY_FILES=$(find "$RESULTS_DIR/history" -name "*.json" -type f | wc -l | tr -d ' ')
    echo "   Found $HISTORY_FILES history file(s)"
fi
```

#### 3.3 Remove history-trend.json Conversion

**Remove** (lines ~270-410):
- All `history-trend.json` conversion logic
- All `duration-trend.json` conversion logic
- Format verification steps

**Reason**: Allure2 generates trend files automatically from history JSON files.

#### 3.4 Update Report Generation Command

**Current (Allure3)**:
```bash
allure generate "$RESULTS_DIR" -o "$REPORT_DIR" $CONFIG_FLAG
```

**New (Allure2)**:
```bash
# Allure2 automatically handles history if present in results directory
allure generate "$RESULTS_DIR" -o "$REPORT_DIR"
```

**Optional**: If you need to specify history directory explicitly:
```bash
allure generate "$RESULTS_DIR" -o "$REPORT_DIR" --history-dir "$RESULTS_DIR/history"
```

---

### 4. Update History Download Script

**File**: `scripts/ci/download-allure-history.sh`

**Current (Allure3)** - downloads `history.jsonl`:
```bash
# Download history.jsonl
curl -s "$HISTORY_URL/history/history.jsonl" -o "$TARGET_DIR/history/history.jsonl"
```

**New (Allure2)** - downloads all history JSON files:
```bash
# Download all history JSON files
mkdir -p "$TARGET_DIR/history"
curl -s "$HISTORY_URL/history/" | grep -o 'href="[^"]*\.json"' | sed 's/href="//;s/"//' | while read file; do
    curl -s "$HISTORY_URL/history/$file" -o "$TARGET_DIR/history/$file"
done
```

**Better approach** - download entire history directory:
```bash
# Download entire history directory as tar/zip if available, or download all files
# For GitHub Pages, we'll need to download each file individually
mkdir -p "$TARGET_DIR/history"

# Get list of history files (if directory listing is available)
# Otherwise, we'll need to maintain a manifest file or download known files
HISTORY_FILES=$(curl -s "$HISTORY_URL/history/" | grep -o 'href="[^"]*\.json"' | sed 's/href="//;s/"//' || echo "")

if [ -n "$HISTORY_FILES" ]; then
    echo "$HISTORY_FILES" | while read file; do
        if [ -n "$file" ]; then
            echo "   Downloading history file: $file"
            curl -s "$HISTORY_URL/history/$file" -o "$TARGET_DIR/history/$file" || true
        fi
    done
else
    echo "   ⚠️  Could not list history files - may need manual download"
fi
```

---

### 5. Update GitHub Actions Workflows

**Files to Update**:
- `.github/workflows/env-fe.yml`
- `.github/workflows/env-be.yml`
- `.github/workflows/env-fs.yml`
- `.github/workflows/ci.yml`

#### 5.1 Update Installation Step

**Current (Allure3)**:
```yaml
- name: Install Allure3 CLI
  run: |
    chmod +x scripts/ci/install-allure3-cli.sh
    ./scripts/ci/install-allure3-cli.sh "3.0.0"
```

**New (Allure2)**:
```yaml
- name: Install Allure2 CLI
  run: |
    chmod +x scripts/ci/install-allure2-cli.sh
    ./scripts/ci/install-allure2-cli.sh "2.36.0"
```

#### 5.2 Update Report Generation Step

**Current (Allure3)**:
```yaml
- name: Generate Allure Report
  run: |
    allure generate allure-results -o allure-report-${{ inputs.environment }}
```

**New (Allure2)**:
```yaml
- name: Generate Allure Report
  run: |
    allure generate allure-results -o allure-report-${{ inputs.environment }}
    # Allure2 automatically processes history if present
```

**Note**: The command syntax is the same, but Allure2 handles history differently.

---

### 6. Update Documentation

**Files to Update**:
- `docs/guides/testing/ALLURE_REPORTING.md`
- `README.md`
- Any other documentation referencing Allure3

**Key Changes**:
1. Update version references: "Allure3 CLI 3.0.0" → "Allure2 CLI 2.36.0"
2. Update installation instructions: npm install → binary download
3. Remove references to `allure.config.ts` / `allure.config.js`
4. Update history format documentation
5. Update troubleshooting sections

---

### 7. History Migration (If Needed)

**If you have existing Allure3 history** (`history.jsonl`), you may need to convert it to Allure2 format.

**Allure3 Format** (`history.jsonl`):
```jsonl
{"buildOrder": 1, "reportName": "...", "data": [...]}
{"buildOrder": 2, "reportName": "...", "data": [...]}
```

**Allure2 Format** (`history/{md5-hash}.json`):
```json
{
  "uid": "md5-hash-of-test-uid",
  "history": [
    {"buildOrder": 1, "status": "passed", "time": {...}},
    {"buildOrder": 2, "status": "passed", "time": {...}}
  ]
}
```

**Migration Script** (if needed):
```bash
#!/bin/bash
# Convert Allure3 history.jsonl to Allure2 format
# This is a one-time migration script

if [ ! -f "history.jsonl" ]; then
    echo "❌ history.jsonl not found"
    exit 1
fi

mkdir -p history-allure2

# Read history.jsonl and convert to Allure2 format
while IFS= read -r line; do
    if [ -n "$line" ]; then
        # Extract data array from line
        data_array=$(echo "$line" | jq '.data // []')
        
        # For each test in data array, create/update history file
        echo "$data_array" | jq -c '.[]' | while read test_data; do
            uid=$(echo "$test_data" | jq -r '.uid')
            if [ -n "$uid" ] && [ "$uid" != "null" ]; then
                # Generate MD5 hash of uid
                md5_hash=$(echo -n "$uid" | md5sum | cut -d' ' -f1)
                
                # Create or update history file
                history_file="history-allure2/${md5_hash}.json"
                
                if [ -f "$history_file" ]; then
                    # Append to existing history
                    jq --argjson test "$test_data" '.history += [$test]' "$history_file" > "${history_file}.tmp"
                    mv "${history_file}.tmp" "$history_file"
                else
                    # Create new history file
                    jq -n --argjson test "$test_data" "{uid: \"$uid\", history: [$test]}" > "$history_file"
                fi
            fi
        done
    fi
done < history.jsonl

echo "✅ History converted to Allure2 format in history-allure2/"
```

**Note**: This migration may not be necessary if you're starting fresh or can regenerate history.

---

## ✅ Testing Checklist

After making changes, verify the following:

### 1. CLI Installation
- [ ] Allure2 CLI installs successfully
- [ ] `allure --version` shows correct version (2.36.0)
- [ ] Allure2 CLI is in PATH

### 2. Report Generation
- [ ] Reports generate successfully from test results
- [ ] Reports open correctly in browser
- [ ] All test data appears correctly

### 3. History Functionality
- [ ] History files are created in `history/` directory
- [ ] History files are individual JSON files (not JSONL)
- [ ] Trends appear in reports after 2+ runs
- [ ] History persists across pipeline runs

### 4. CI/CD Pipeline
- [ ] GitHub Actions workflows run successfully
- [ ] Reports are generated in CI
- [ ] History is downloaded and used correctly
- [ ] Reports are deployed to GitHub Pages

### 5. Scripts
- [ ] All scripts reference Allure2 (not Allure3)
- [ ] History download script works with Allure2 format
- [ ] Report generation script works correctly

---

## 🔍 Verification Steps

### 1. Verify Allure2 Installation

```bash
# Check version
allure --version
# Should show: 2.36.0

# Check Java dependency
java -version
# Should show Java version
```

### 2. Verify Report Generation

```bash
# Generate report
allure generate allure-results -o allure-report

# Check report structure
ls -la allure-report/
# Should contain: index.html, history/, widgets/, data/, etc.

# Check history format
ls -la allure-report/history/
# Should contain: {md5-hash}.json files (not history.jsonl)
```

### 3. Verify History Format

```bash
# Check history files
find allure-report/history -name "*.json" | head -3 | xargs cat
# Should show JSON files with uid and history array
```

### 4. Verify Trends

```bash
# Check if trend files exist
ls -la allure-report/widgets/ | grep trend
# Should show: history-trend.json, duration-trend.json, etc.
```

---

## 📊 Comparison: Before vs After

### Before (Allure3)

```bash
# Installation
npm install -g allure@3.0.0

# Configuration
# allure.config.ts with historyPath and appendHistory

# Report Generation
allure generate results -o report --config allure.config.ts

# History Format
history/history.jsonl (single file, JSON Lines)
```

### After (Allure2)

```bash
# Installation
wget https://github.com/allure-framework/allure2/releases/download/2.36.0/allure-2.36.0.tgz
tar -xzf allure-2.36.0.tgz
sudo cp -r allure-2.36.0/* /usr/local/

# Configuration
# No config file needed

# Report Generation
allure generate results -o report

# History Format
history/{md5-hash}.json (multiple files, one per test)
```

---

## ⚠️ Potential Issues & Solutions

### Issue 1: History Not Appearing

**Symptom**: Trends don't show in reports after multiple runs.

**Possible Causes**:
- History files not being downloaded correctly
- History files in wrong location
- History files have wrong format

**Solution**:
- Verify history files are in `results/history/` directory
- Check file format (should be JSON, not JSONL)
- Ensure history files are deployed to GitHub Pages

### Issue 2: CLI Not Found

**Symptom**: `allure: command not found`

**Possible Causes**:
- Allure2 not installed
- Allure2 not in PATH

**Solution**:
- Run installation script
- Verify `/usr/local/bin/allure` exists
- Add to PATH if needed

### Issue 3: Java Not Found

**Symptom**: Allure2 fails with Java error

**Possible Causes**:
- Java not installed
- Java not in PATH

**Solution**:
- Install Java runtime
- Verify `java -version` works

### Issue 4: History Migration Issues

**Symptom**: Existing Allure3 history not working with Allure2

**Possible Causes**:
- Format incompatibility
- Missing conversion

**Solution**:
- Use migration script (if provided)
- Or start fresh (let Allure2 create new history)

---

## 📚 Additional Resources

- **Allure2 GitHub**: https://github.com/allure-framework/allure2
- **Allure2 Releases**: https://github.com/allure-framework/allure2/releases
- **Allure2 Documentation**: https://docs.qameta.io/allure/
- **Allure2 Maven Plugin**: https://github.com/allure-framework/allure-maven

---

## 🎯 Migration Timeline

### Phase 1: Preparation (1-2 hours)
1. Review this guide
2. Backup current configuration
3. Create feature branch

### Phase 2: Code Changes (2-3 hours)
1. Remove Allure3 config files
2. Create/update Allure2 installation script
3. Update report generation script
4. Update history download script
5. Update GitHub Actions workflows

### Phase 3: Testing (2-4 hours)
1. Test locally
2. Test in CI/CD
3. Verify history functionality
4. Verify trends display

### Phase 4: Deployment (1 hour)
1. Merge to main
2. Monitor pipeline
3. Verify deployed reports

**Total Estimated Time**: 6-10 hours

---

## 📝 Summary

**What Changes**:
- ✅ CLI installation method (npm → binary download)
- ✅ Configuration files (removed)
- ✅ History format (JSONL → individual JSON files)
- ✅ Scripts and workflows

**What Stays the Same**:
- ✅ Java libraries (Maven dependencies)
- ✅ Test code and annotations
- ✅ Result file format
- ✅ Report structure and UI

**Benefits of Allure2**:
- ✅ More mature and stable
- ✅ Better documented
- ✅ Larger community
- ✅ Proven history functionality

**Trade-offs**:
- ⚠️ Requires Java runtime (not just Node.js)
- ⚠️ Different history format (may need migration)
- ⚠️ No TypeScript config files

---

## 🔧 Making Allure Version Configurable

Instead of choosing one version permanently, you can make the Allure reporting system configurable to support both Allure2 and Allure3 via a configuration file.

### Overview

**Goal**: Support both Allure2 and Allure3 via configuration in `config/environments.json`.

**Benefits**:
- ✅ Centralized configuration (single source of truth)
- ✅ Easy switching between versions
- ✅ Test both versions in parallel
- ✅ Gradual migration path
- ✅ A/B testing capabilities
- ✅ Version controlled configuration

### Implementation Strategy

#### 1. Configuration File

**File**: `config/environments.json` (already in use)

**Add Allure Configuration Section**:
```json
{
  "api": { ... },
  "database": { ... },
  "timeouts": { ... },
  "allure": {
    "reportVersion": 3,
    "cliVersion": {
      "2": "2.36.0",
      "3": "3.0.0"
    }
  },
  "environments": { ... }
}
```

**Configuration Values**:
- `reportVersion`: `2` or `3` (default: `3`)
- `cliVersion.2`: Allure2 CLI version (default: `2.36.0`)
- `cliVersion.3`: Allure3 CLI version (default: `3.0.0`)

**Reading from Config File**:
```bash
# Read Allure version from config file
ALLURE_REPORT_VERSION=$(jq -r '.allure.reportVersion // 3' config/environments.json)
ALLURE2_CLI_VERSION=$(jq -r '.allure.cliVersion."2" // "2.36.0"' config/environments.json)
ALLURE3_CLI_VERSION=$(jq -r '.allure.cliVersion."3" // "3.0.0"' config/environments.json)
```

**Fallback to Environment Variable**:
```bash
# Read from config file, fallback to environment variable, then default
ALLURE_REPORT_VERSION="${ALLURE_REPORT_VERSION:-$(jq -r '.allure.reportVersion // 3' config/environments.json)}"
```

**Usage**:
```bash
# Use value from config/environments.json (default)
./scripts/ci/generate-combined-allure-report.sh

# Override with environment variable (takes precedence)
export ALLURE_REPORT_VERSION=2
./scripts/ci/generate-combined-allure-report.sh

# Or set inline (highest precedence)
ALLURE_REPORT_VERSION=2 ./scripts/ci/generate-combined-allure-report.sh
```

**Priority Order** (highest to lowest):
1. Environment variable (`ALLURE_REPORT_VERSION`)
2. Config file (`config/environments.json`)
3. Default value (`3`)

#### 2. Unified Installation Script

**File**: `scripts/ci/install-allure-cli.sh` (new unified script)

```bash
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
    
    wget -q "$DOWNLOAD_URL" -O allure.tgz
    tar -xzf allure.tgz
    sudo cp -r allure-${ALLURE_CLI_VERSION}/* /usr/local/
    sudo ln -sf /usr/local/bin/allure /usr/local/bin/allure
    
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
else
    echo "   Type: TypeScript-based CLI"
    echo "   Installation: npm"
fi
```

#### 3. Update Report Generation Script

**File**: `scripts/ci/generate-combined-allure-report.sh`

**Add at the beginning**:
```bash
#!/bin/bash
# Generate Combined Allure Report (supports Allure2 and Allure3)
# Usage: ./scripts/ci/generate-combined-allure-report.sh [results-dir] [report-dir]
#
# Configuration:
#   Reads from config/environments.json (allure.reportVersion)
#   Can be overridden via ALLURE_REPORT_VERSION environment variable

set -e

# Read Allure version from config file, fallback to environment variable, then default
if [ -f "config/environments.json" ]; then
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-$(jq -r '.allure.reportVersion // 3' config/environments.json)}"
else
    # Fallback if config file doesn't exist
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-3}"
fi

if [ "$ALLURE_VERSION" != "2" ] && [ "$ALLURE_VERSION" != "3" ]; then
    echo "❌ Invalid ALLURE_REPORT_VERSION: $ALLURE_VERSION (must be 2 or 3)"
    exit 1
fi

echo "📊 Generating combined Allure report (Version: $ALLURE_VERSION)..."
echo "   Configuration source: $([ -f "config/environments.json" ] && echo "config/environments.json" || echo "default/environment variable")"
```

**Update history handling section**:
```bash
# Check RESULTS directory first (where historyPath points)
if [ "$ALLURE_VERSION" = "3" ]; then
    # Allure3: Check for history.jsonl
    if [ -f "$RESULTS_DIR/history/history.jsonl" ]; then
        HISTORY_CREATED=true
        HISTORY_SOURCE="results"
        echo ""
        echo "✅ Allure3 history found (history.jsonl format)"
        # ... existing Allure3 logic ...
    fi
else
    # Allure2: Check for individual JSON files
    if [ -d "$RESULTS_DIR/history" ] && [ "$(find "$RESULTS_DIR/history" -name "*.json" -type f | wc -l | tr -d ' ')" -gt 0 ]; then
        HISTORY_CREATED=true
        HISTORY_SOURCE="results"
        HISTORY_FILES=$(find "$RESULTS_DIR/history" -name "*.json" -type f | wc -l | tr -d ' ')
        echo ""
        echo "✅ Allure2 history found ($HISTORY_FILES file(s))"
        # ... Allure2 logic ...
    fi
fi
```

**Update report generation command**:
```bash
# Generate report
echo ""
echo "🔄 Generating Allure report (Version: $ALLURE_VERSION)..."
rm -rf "$REPORT_DIR"

if [ "$ALLURE_VERSION" = "3" ]; then
    # Allure3: Use config file if available
    CONFIG_FLAG=""
    if [ -f "allure.config.ts" ]; then
        CONFIG_FLAG="--config allure.config.ts"
        echo "   ✅ Using allure.config.ts"
    elif [ -f "allure.config.js" ]; then
        CONFIG_FLAG="--config allure.config.js"
        echo "   ✅ Using allure.config.js"
    fi
    
    allure generate "$RESULTS_DIR" -o "$REPORT_DIR" $CONFIG_FLAG 2>&1 | tee /tmp/allure-generate.log || {
        echo "⚠️  Allure3 generate had warnings/errors"
    }
else
    # Allure2: No config file needed
    echo "   Using Allure2 CLI (no config file needed)"
    allure generate "$RESULTS_DIR" -o "$REPORT_DIR" 2>&1 | tee /tmp/allure-generate.log || {
        echo "⚠️  Allure2 generate had warnings/errors"
    }
fi
```

**Update history conversion (Allure3 only)**:
```bash
# Convert history.jsonl to history-trend.json (Allure3 only)
if [ "$ALLURE_VERSION" = "3" ] && [ -f "$RESULTS_DIR/history/history.jsonl" ]; then
    echo ""
    echo "📊 Converting history.jsonl to history-trend.json for UI trends display..."
    # ... existing Allure3 conversion logic ...
fi
```

#### 4. Update History Download Script

**File**: `scripts/ci/download-allure-history.sh`

**Add version detection**:
```bash
#!/bin/bash
# Download Allure History (supports both Allure2 and Allure3)
# Usage: ./scripts/ci/download-allure-history.sh <target-dir> [method]

set -e

TARGET_DIR="${1:-allure-results-combined}"
METHOD="${2:-pages}"

# Read Allure version from config file, fallback to environment variable, then default
if [ -f "config/environments.json" ]; then
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-$(jq -r '.allure.reportVersion // 3' config/environments.json)}"
else
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-3}"
fi

echo "📥 Downloading Allure history (Version: $ALLURE_VERSION)..."

mkdir -p "$TARGET_DIR/history"

if [ "$ALLURE_VERSION" = "3" ]; then
    # Allure3: Download history.jsonl
    echo "   Downloading Allure3 history (history.jsonl)..."
    # ... existing Allure3 download logic ...
else
    # Allure2: Download individual JSON files
    echo "   Downloading Allure2 history (individual JSON files)..."
    # ... Allure2 download logic ...
fi
```

#### 5. Update GitHub Actions Workflows

**File**: `.github/workflows/env-fe.yml` (and other workflow files)

**Option A: Use Config File (Recommended)**:
```yaml
jobs:
  test:
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Install Allure CLI
        run: |
          chmod +x scripts/ci/install-allure-cli.sh
          # Reads from config/environments.json
          ./scripts/ci/install-allure-cli.sh
      
      - name: Generate Allure Report
        run: |
          # Reads from config/environments.json
          ./scripts/ci/generate-combined-allure-report.sh
```

**Option B: Override with Environment Variable**:
```yaml
env:
  ALLURE_REPORT_VERSION: ${{ inputs.allure_version || '' }}

jobs:
  test:
    steps:
      - name: Install Allure CLI
        run: |
          chmod +x scripts/ci/install-allure-cli.sh
          # Environment variable takes precedence over config file
          ./scripts/ci/install-allure-cli.sh
      
      - name: Generate Allure Report
        run: |
          # Environment variable takes precedence over config file
          ./scripts/ci/generate-combined-allure-report.sh
```

**Or make it configurable per workflow**:
```yaml
on:
  workflow_dispatch:
    inputs:
      allure_version:
        description: 'Allure version (2 or 3)'
        required: false
        default: '3'
        type: choice
        options:
          - '2'
          - '3'
```

#### 6. Configuration File Management

**Option A: Conditional Config Files**

Create separate config files:
- `allure.config.allure2.js` (if needed for Allure2)
- `allure.config.allure3.ts` (for Allure3)

**In script**:
```bash
if [ "$ALLURE_VERSION" = "3" ]; then
    if [ -f "allure.config.allure3.ts" ]; then
        cp allure.config.allure3.ts allure.config.ts
    fi
fi
```

**Option B: Dynamic Config Generation**

```bash
if [ "$ALLURE_VERSION" = "3" ]; then
    cat > allure.config.ts <<EOF
import { defineConfig } from 'allure';

export default defineConfig({
  historyPath: "./history/history.jsonl",
  appendHistory: true
});
EOF
fi
```

#### 7. Documentation Updates

**Update README.md**:
```markdown
## Allure Reporting

Allure reporting supports both Allure2 and Allure3.

**Set version via environment variable**:
```bash
export ALLURE_REPORT_VERSION=2  # Use Allure2
export ALLURE_REPORT_VERSION=3  # Use Allure3 (default)
```

**Default**: Allure3 (3.0.0)
```

**Update CI documentation**:
```markdown
### Allure Version Configuration

The Allure reporting version can be configured via the `ALLURE_REPORT_VERSION` environment variable:

- `ALLURE_REPORT_VERSION=2` - Use Allure2 CLI (2.36.0)
- `ALLURE_REPORT_VERSION=3` - Use Allure3 CLI (3.0.0, default)
```

### Testing Both Versions

#### Local Testing

```bash
# Test with Allure2
export ALLURE_REPORT_VERSION=2
./scripts/ci/install-allure-cli.sh
./scripts/ci/generate-combined-allure-report.sh

# Test with Allure3
export ALLURE_REPORT_VERSION=3
./scripts/ci/install-allure-cli.sh
./scripts/ci/generate-combined-allure-report.sh
```

#### CI/CD Testing

```yaml
# Test both versions in parallel
strategy:
  matrix:
    allure_version: [2, 3]

env:
  ALLURE_REPORT_VERSION: ${{ matrix.allure_version }}

steps:
  - name: Install Allure CLI
    run: ./scripts/ci/install-allure-cli.sh
  
  - name: Generate Report
    run: ./scripts/ci/generate-combined-allure-report.sh
```

### Migration Path

1. **Phase 1**: Implement configurable system (support both versions)
2. **Phase 2**: Test both versions in parallel
3. **Phase 3**: Switch default to preferred version
4. **Phase 4**: Remove support for unused version (optional)

### Benefits of Configurable Approach

- ✅ **Flexibility**: Easy to switch between versions
- ✅ **Testing**: Compare both versions side-by-side
- ✅ **Gradual Migration**: Migrate at your own pace
- ✅ **Rollback**: Easy to revert if issues arise
- ✅ **A/B Testing**: Test which version works better for your use case

### Example Usage

**Using Config File (Recommended)**:
```bash
# Edit config/environments.json to set "reportVersion": 2 or 3
# Then run scripts normally - they'll read from config file
./scripts/ci/generate-combined-allure-report.sh
```

**Overriding with Environment Variable**:
```bash
# Override config file value with environment variable
ALLURE_REPORT_VERSION=2 ./scripts/ci/generate-combined-allure-report.sh

# Or set globally
export ALLURE_REPORT_VERSION=2
./scripts/ci/generate-combined-allure-report.sh
```

**Reading Config Values**:
```bash
# Read current Allure version from config
jq -r '.allure.reportVersion' config/environments.json

# Read Allure2 CLI version
jq -r '.allure.cliVersion."2"' config/environments.json

# Read Allure3 CLI version
jq -r '.allure.cliVersion."3"' config/environments.json
```

---

## 🚀 Step-by-Step: Making Allure Configurable and Setting to Allure2

This section provides a complete step-by-step guide to make Allure reporting configurable and switch to Allure2.

### Prerequisites

- ✅ `jq` installed (for JSON parsing): `brew install jq` (macOS) or `apt-get install jq` (Linux)
- ✅ Access to edit configuration files
- ✅ Understanding of your current Allure setup

### Step 1: Update Configuration File ✅ COMPLETE

**File**: `config/environments.json`

**Status**: ✅ **COMPLETED** - Configuration file has been updated with Allure settings.

**Action**: Add or update the `allure` section:

```json
{
  "api": { ... },
  "database": { ... },
  "timeouts": { ... },
  "allure": {
    "reportVersion": 2,
    "cliVersion": {
      "2": "2.36.0",
      "3": "3.0.0"
    }
  },
  "environments": { ... }
}
```

**Verify**:
```bash
jq '.allure' config/environments.json
# Should show: {"reportVersion": 2, "cliVersion": {"2": "2.36.0", "3": "3.0.0"}}
```

**Verification Results**:
- ✅ `reportVersion`: 2 (Allure2)
- ✅ `cliVersion.2`: "2.36.0"
- ✅ `cliVersion.3`: "3.0.0"
- ✅ JSON syntax: Valid

---

### Step 2: Create Unified Installation Script ✅ COMPLETE

**File**: `scripts/ci/install-allure-cli.sh`

**Status**: ✅ **COMPLETED** - Unified installation script created that supports both Allure2 and Allure3.

**Action**: Create the unified installation script:

```bash
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
    wget -q "$DOWNLOAD_URL" -O allure.tgz
    
    echo "📦 Extracting Allure2 CLI..."
    tar -xzf allure.tgz
    
    # Install to /usr/local/bin
    echo "📁 Installing to /usr/local/bin..."
    sudo cp -r allure-${ALLURE_CLI_VERSION}/* /usr/local/
    sudo ln -sf /usr/local/bin/allure /usr/local/bin/allure
    
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
else
    echo "   Type: TypeScript-based CLI"
    echo "   Installation: npm"
fi
```

**Make executable**:
```bash
chmod +x scripts/ci/install-allure-cli.sh
```

**Verify**:
```bash
./scripts/ci/install-allure-cli.sh
# Should install Allure2 CLI version 2.36.0
```

**Verification Results**:
- ✅ Script created: `scripts/ci/install-allure-cli.sh`
- ✅ Script is executable
- ✅ Script syntax is valid
- ✅ Reads from `config/environments.json` (reportVersion: 2)
- ✅ Supports both Allure2 and Allure3 installation
- ✅ Falls back to environment variable if config file not found
- ✅ Allows command-line override

---

### Step 3: Update Report Generation Script ✅ COMPLETE

**File**: `scripts/ci/generate-combined-allure-report.sh`

**Status**: ✅ **COMPLETED** - Report generation script updated to support both Allure2 and Allure3.

**Action 3.1**: Update the beginning of the script to read from config:

**Find** (around line 1-10):
```bash
#!/bin/bash
# Generate Combined Allure Report
```

**Replace with**:
```bash
#!/bin/bash
# Generate Combined Allure Report (supports Allure2 and Allure3)
# Usage: ./scripts/ci/generate-combined-allure-report.sh [results-dir] [report-dir]
#
# Configuration:
#   Reads from config/environments.json (allure.reportVersion)
#   Can be overridden via ALLURE_REPORT_VERSION environment variable

set -e

# Read Allure version from config file, fallback to environment variable, then default
if [ -f "config/environments.json" ]; then
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-$(jq -r '.allure.reportVersion // 3' config/environments.json)}"
else
    # Fallback if config file doesn't exist
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-3}"
fi

if [ "$ALLURE_VERSION" != "2" ] && [ "$ALLURE_VERSION" != "3" ]; then
    echo "❌ Invalid ALLURE_REPORT_VERSION: $ALLURE_VERSION (must be 2 or 3)"
    exit 1
fi

echo "📊 Generating combined Allure report (Version: $ALLURE_VERSION)..."
echo "   Configuration source: $([ -f "config/environments.json" ] && echo "config/environments.json" || echo "default/environment variable")"
```

**Action 3.2**: Update history detection section:

**Find** (around line 246-260):
```bash
# Check RESULTS directory first (where historyPath points)
if [ -f "$RESULTS_DIR/history/history.jsonl" ]; then
```

**Replace with**:
```bash
# Check RESULTS directory first (where historyPath points)
if [ "$ALLURE_VERSION" = "3" ]; then
    # Allure3: Check for history.jsonl
    if [ -f "$RESULTS_DIR/history/history.jsonl" ]; then
        HISTORY_CREATED=true
        HISTORY_SOURCE="results"
        echo ""
        echo "✅ Allure3 history found (history.jsonl format)"
        # ... existing Allure3 logic ...
    fi
else
    # Allure2: Check for individual JSON files
    if [ -d "$RESULTS_DIR/history" ] && [ "$(find "$RESULTS_DIR/history" -name "*.json" -type f | wc -l | tr -d ' ')" -gt 0 ]; then
        HISTORY_CREATED=true
        HISTORY_SOURCE="results"
        HISTORY_FILES=$(find "$RESULTS_DIR/history" -name "*.json" -type f | wc -l | tr -d ' ')
        echo ""
        echo "✅ Allure2 history found ($HISTORY_FILES file(s))"
        # Allure2 history is automatically processed by allure generate
    fi
fi
```

**Action 3.3**: Update report generation command (✅ COMPLETE):

**Find** (around line 191-215):
```bash
# Generate Allure report with explicit --config flag
echo "   Running: allure generate \"$RESULTS_DIR\" -o \"$REPORT_DIR\""
CONFIG_FLAG=""
CONFIG_FILE=""
# Try TypeScript config first, then JavaScript
if [ -f "allure.config.ts" ]; then
    ...
fi

allure generate "$RESULTS_DIR" -o "$REPORT_DIR" $CONFIG_FLAG
```

**Replace with**:
```bash
# Generate report
echo ""
echo "🔄 Generating Allure report (Version: $ALLURE_VERSION)..."
rm -rf "$REPORT_DIR"

if [ "$ALLURE_VERSION" = "3" ]; then
    # Allure3: Use config file if available
    CONFIG_FLAG=""
    if [ -f "allure.config.ts" ]; then
        CONFIG_FLAG="--config allure.config.ts"
        echo "   ✅ Using allure.config.ts"
    elif [ -f "allure.config.js" ]; then
        CONFIG_FLAG="--config allure.config.js"
        echo "   ✅ Using allure.config.js"
    fi
    
    allure generate "$RESULTS_DIR" -o "$REPORT_DIR" $CONFIG_FLAG 2>&1 | tee /tmp/allure-generate.log || {
        echo "⚠️  Allure3 generate had warnings/errors"
    }
else
    # Allure2: No config file needed
    echo "   Using Allure2 CLI (no config file needed)"
    allure generate "$RESULTS_DIR" -o "$REPORT_DIR" 2>&1 | tee /tmp/allure-generate.log || {
        echo "⚠️  Allure2 generate had warnings/errors"
    }
fi
```

**Action 3.4**: Update history conversion (Allure3 only):

**Find** the section that converts `history.jsonl` to `history-trend.json` (around line 270-410):

**Wrap it in a version check**:
```bash
# Convert history.jsonl to history-trend.json (Allure3 only)
if [ "$ALLURE_VERSION" = "3" ] && [ -f "$RESULTS_DIR/history/history.jsonl" ]; then
    echo ""
    echo "📊 Converting history.jsonl to history-trend.json for UI trends display..."
    # ... existing Allure3 conversion logic ...
fi
```

**Note**: Allure2 automatically generates trend files from history, so this conversion is not needed.

**Verification Results**:
- ✅ Script reads from `config/environments.json` (reportVersion: 2)
- ✅ History detection supports both Allure2 (JSON files) and Allure3 (JSONL)
- ✅ Report generation command supports both versions
- ✅ History conversion (history-trend.json) only runs for Allure3
- ✅ Allure2 history is automatically copied to report directory
- ✅ Script syntax is valid

---

### Step 4: Update History Download Script ✅ COMPLETE

**File**: `scripts/ci/download-allure-history.sh`

**Status**: ✅ **COMPLETED** - History download script updated to support both Allure2 and Allure3.

**Action**: Update to support both versions:

**Find** (around line 1-25):
```bash
#!/bin/bash
# Download Allure History from GitHub Pages or Artifact
```

**Add version detection at the beginning**:
```bash
#!/bin/bash
# Download Allure History (supports both Allure2 and Allure3)
# Usage: ./scripts/ci/download-allure-history.sh <target-dir> [method]

set -e

TARGET_DIR="${1:-allure-results-combined}"
METHOD="${2:-pages}"

# Read Allure version from config file, fallback to environment variable, then default
if [ -f "config/environments.json" ]; then
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-$(jq -r '.allure.reportVersion // 3' config/environments.json)}"
else
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-3}"
fi

echo "📥 Downloading Allure history (Version: $ALLURE_VERSION)..."
```

**Update download logic**:

**Find** the section that downloads history:

**Replace with**:
```bash
mkdir -p "$TARGET_DIR/history"

if [ "$ALLURE_VERSION" = "3" ]; then
    # Allure3: Download history.jsonl
    echo "   Downloading Allure3 history (history.jsonl)..."
    # ... existing Allure3 download logic ...
else
    # Allure2: Download individual JSON files
    echo "   Downloading Allure2 history (individual JSON files)..."
    # Download all history JSON files from GitHub Pages via GitHub API
    # Allure2 uses individual JSON files for history
    # ... existing download logic adapted for Allure2 ...
fi
```

**Verification Results**:
- ✅ Script reads from `config/environments.json` (reportVersion: 2)
- ✅ Allure3: Downloads history.jsonl and individual history files
- ✅ Allure2: Downloads individual JSON files from history directory
- ✅ Both versions use GitHub API to list and download files
- ✅ Script syntax is valid

---

### Step 5: Update GitHub Actions Workflows ✅ COMPLETE

**Status**: ✅ **COMPLETED** - GitHub Actions workflows updated to use unified Allure CLI installation script.

**Files**: 
- `.github/workflows/env-fe.yml` ✅
- `.github/workflows/ci.yml` ✅

**Note**: `.github/workflows/env-be.yml` and `.github/workflows/env-fs.yml` do not use Allure, so no changes needed.

**Action**: Update Allure installation step:

**Find**:
```yaml
- name: Install Allure3 CLI
  run: |
    chmod +x scripts/ci/install-allure3-cli.sh
    ./scripts/ci/install-allure3-cli.sh "3.0.0"
```

**Replace with**:
```yaml
- name: Install Allure CLI
  run: |
    chmod +x scripts/ci/install-allure-cli.sh
    # Reads from config/environments.json (reportVersion: 2)
    ./scripts/ci/install-allure-cli.sh
```

**Note**: The script will automatically read `config/environments.json` and install Allure2.

**Verification Results**:
- ✅ `.github/workflows/env-fe.yml`: Updated to use `install-allure-cli.sh`
- ✅ `.github/workflows/ci.yml`: Updated to use `install-allure-cli.sh`
- ✅ Removed version parameter (now reads from config file)
- ✅ Both workflows will install Allure2 by default (reportVersion: 2)
- ✅ YAML syntax is valid

---

### Step 6: Remove Allure3-Specific Files (Optional)

**Files to consider removing** (if not needed):
- `allure.config.ts` (Allure3 config - not needed for Allure2)
- `allure.config.js` (Allure3 config - not needed for Allure2)
- `scripts/ci/install-allure3-cli.sh` (replaced by unified script)

**Action**: 
```bash
# Optional: Remove Allure3 config files (not needed for Allure2)
# rm allure.config.ts allure.config.js

# Optional: Remove old installation script (replaced by unified script)
# rm scripts/ci/install-allure3-cli.sh
```

**Note**: Keep these files if you plan to switch back to Allure3 later.

---

### Step 7: Test the Configuration

**Action 7.1**: Verify config is read correctly:
```bash
# Should output: 2
jq -r '.allure.reportVersion' config/environments.json
```

**Action 7.2**: Test installation:
```bash
# Should install Allure2
./scripts/ci/install-allure-cli.sh

# Verify version
allure --version
# Should show: 2.36.0
```

**Action 7.3**: Test report generation:
```bash
# Should use Allure2
./scripts/ci/generate-combined-allure-report.sh allure-results-combined allure-report-combined

# Check output - should mention "Version: 2"
```

**Action 7.4**: Verify history format:
```bash
# For Allure2, history should be individual JSON files
ls -la allure-report-combined/history/
# Should show: {md5-hash}.json files (not history.jsonl)
```

---

### Step 8: Update Documentation

**Files to update**:
- `README.md`
- `docs/guides/testing/ALLURE_REPORTING.md`

**Action**: Update version references:

**Find**:
```markdown
Allure3 CLI 3.0.0
```

**Replace with**:
```markdown
Allure2 CLI 2.36.0 (configurable via config/environments.json)
```

**Add note**:
```markdown
## Allure Reporting Configuration

Allure reporting version is configured in `config/environments.json`:

```json
"allure": {
  "reportVersion": 2,
  "cliVersion": {
    "2": "2.36.0",
    "3": "3.0.0"
  }
}
```

To switch versions, edit `config/environments.json` and change `reportVersion` to `2` or `3`.
```

---

### Step 9: Commit Changes

**Action**: Commit all changes:

```bash
git add config/environments.json
git add scripts/ci/install-allure-cli.sh
git add scripts/ci/generate-combined-allure-report.sh
git add scripts/ci/download-allure-history.sh
git add .github/workflows/*.yml
git commit -m "Make Allure reporting configurable and switch to Allure2

- Add Allure configuration to config/environments.json
- Create unified install-allure-cli.sh script (supports both Allure2 and Allure3)
- Update generate-combined-allure-report.sh to read from config
- Update download-allure-history.sh to support both versions
- Update GitHub Actions workflows to use unified script
- Set default version to Allure2 (2.36.0)"
```

---

### Step 10: Verify in CI/CD

**Action**: 
1. Push changes to repository
2. Monitor pipeline execution
3. Verify Allure2 CLI is installed
4. Verify reports are generated correctly
5. Check deployed reports on GitHub Pages

**Expected Results**:
- ✅ Allure2 CLI 2.36.0 installed
- ✅ Reports generated successfully
- ✅ History files are individual JSON files (not JSONL)
- ✅ Trends appear in reports after 2+ runs

---

### Troubleshooting

**Issue**: `jq: command not found`
- **Solution**: Install jq: `brew install jq` (macOS) or `apt-get install jq` (Linux)

**Issue**: `Java is not installed`
- **Solution**: Install Java: `brew install openjdk` (macOS) or `apt-get install default-jdk` (Linux)

**Issue**: Config file not found
- **Solution**: Ensure `config/environments.json` exists and has `allure` section

**Issue**: Wrong version installed
- **Solution**: Check `config/environments.json` has `"reportVersion": 2`

**Issue**: History not working
- **Solution**: 
  - For Allure2: Ensure history files are individual JSON files in `history/` directory
  - Verify history is downloaded correctly before report generation

---

### Summary Checklist

- [ ] Updated `config/environments.json` with Allure configuration (`reportVersion: 2`)
- [ ] Created `scripts/ci/install-allure-cli.sh` (unified script)
- [ ] Updated `scripts/ci/generate-combined-allure-report.sh` to read from config
- [ ] Updated `scripts/ci/download-allure-history.sh` to support both versions
- [ ] Updated GitHub Actions workflows to use unified script
- [ ] Tested installation locally
- [ ] Tested report generation locally
- [ ] Verified config is read correctly
- [ ] Updated documentation
- [ ] Committed changes
- [ ] Verified in CI/CD pipeline

---

**Last Updated**: 2026-01-08  
**Document Location**: `docs/work/20260108_ALLURE2_MIGRATION_GUIDE.md`  
**Status**: 📋 Ready for Implementation

