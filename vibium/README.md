# Vibium Tests

This directory contains Vibium test framework examples and research documentation.

## 📋 Overview

**Vibium** is an AI-native test automation framework developed by Jason Huggins (creator of Selenium and Appium). The framework has been released and is available as `vibium@0.1.2`.

## 📦 Installation

```bash
cd vibium
npm install
```

This will install:
- `vibium@0.1.2` (released version)
- `@vibium/darwin-arm64@0.1.2` (clicker binary for macOS ARM64)
- TypeScript and type definitions

## 🧪 Running Tests

### Using the Test Script (Recommended)

```bash
# From project root
./scripts/run-vibium-tests.sh

# With options
./scripts/run-vibium-tests.sh --watch    # Watch mode
./scripts/run-vibium-tests.sh --ui       # UI mode
./scripts/run-vibium-tests.sh --coverage # Coverage report
```

### Direct npm Commands

```bash
cd vibium

# Run tests once
npm test

# Watch mode
npm run test:watch

# UI mode
npm run test:ui
```

**Note**: The test script automatically handles dependency installation and provides better error messages.

## 📝 Example Files

### `example_mock.js` - Mock Implementation

A fully functional mock implementation of the expected Vibium `browserSync` API. This file demonstrates the expected API structure and can be run immediately to see how the API will work.

**Features:**
- ✅ Self-contained mock implementation
- ✅ Demonstrates async and sync API patterns
- ✅ Shows expected method signatures
- ✅ Can be run without the full Vibium package

**How to Run:**

1. **Uncomment the function calls** at the bottom of the file:
   ```javascript
   // Change from:
   // asyncAPI().catch(console.error);
   // syncAPI();
   
   // To:
   asyncAPI().catch(console.error);
   syncAPI();
   ```

2. **Run the file:**
   ```bash
   node example_mock.js
   ```

   Or use npm script:
   ```bash
   npm run vibe
   ```

**Expected Output:**
```
📝 Running asyncAPI()...

🔵 [MOCK] Launching browser...
🔵 [MOCK] Navigating to: https://example.com
🔵 [MOCK] Finding element: button.submit
🔵 [MOCK] Clicking element: button.submit
🔵 [MOCK] Typing text: "hello"
🔵 [MOCK] Taking screenshot...
📸 Screenshot data length: 118 characters
🔵 [MOCK] Closing browser...

✅ asyncAPI() completed successfully!
```

### `example_handle.js` - Vibium Handler

An example that demonstrates how to work with the released Vibium framework.

**Features:**
- ✅ Works with `vibium@0.1.2` (released version)
- ✅ Uses the full browserSync API
- ✅ Demonstrates the framework structure

**How to Run:**

1. **Uncomment the function calls** at the bottom of the file:
   ```javascript
   // Change from:
   // asyncAPI();
   // syncAPI();
   
   // To:
   asyncAPI();
   syncAPI();
   ```

2. **Run the file:**
   ```bash
   node example_handle.js
   ```

**Expected Output:**
```
📝 asyncAPI() - Vibium Implementation

✅ Vibium package is available (v0.1.2)
📦 The browserSync API is available in npm
🔗 See: https://github.com/VibiumDev/vibium for the latest development

Welcome to Vibium!

💡 You can now use:
   import { browserSync } from "vibium";
```

## 🚀 Quick Start

### Run Mock Example (Recommended for Testing)

```bash
# 1. Edit example_mock.js and uncomment the function calls
# 2. Run it
node example_mock.js
```

### Run Vibium Handler

```bash
# 1. Edit example_handle.js and uncomment the function calls
# 2. Run it
node example_handle.js
```

### Using npm Scripts

```bash
# Run the vibe script (configured in package.json)
npm run vibe
```

## 📚 API Structure

Both example files demonstrate the expected Vibium API:

### Async API (Promise-based)
```javascript
import { browserSync } from "vibium";

async function asyncAPI() {
  const vibe = await browserSync.launch();
  await vibe.go("https://example.com");
  
  const el = await vibe.find("button.submit");
  await el.click();
  await el.type("hello");
  
  const png = await vibe.screenshot();
  await vibe.quit();
}
```

### Sync API (Blocking)
```javascript
import { browserSync } from "vibium";

function syncAPI() {
  const vibe = browserSync.launch();
  vibe.go("https://example.com");
  
  const el = vibe.find("button.submit");
  el.click();
  el.type("hello");
  
  const png = vibe.screenshot();
  vibe.quit();
}
```

## 📖 Documentation

- **Research Document**: `20251219_VIBIUM_FRAMEWORK_RESEARCH.md` - Comprehensive research and findings
- **Official Repository**: [https://github.com/VibiumDev/vibium](https://github.com/VibiumDev/vibium)
- **Official Website**: [https://vibium.com](https://vibium.com)

## ✅ Current Status

- **npm Package**: `vibium@0.1.2` (released)
- **Full API**: Available in npm
- **Status**: Framework has been released
- **Version**: 0.1.2 (latest)

## 🔮 Future Integration

When the full Vibium package is released:

1. Update `package.json` to use the new version
2. Replace mock implementations with real API calls
3. The structure in both example files will work with minimal changes
4. Add to CI/CD pipeline similar to Cypress and Playwright

## 📦 Project Structure

```
vibium/
├── README.md                          # This file
├── package.json                       # npm configuration
├── example_mock.js                    # Mock implementation (runnable)
├── example_handle.js                  # Placeholder handler (runnable)
├── 20251219_VIBIUM_FRAMEWORK_RESEARCH.md  # Research documentation
└── node_modules/                      # Dependencies (gitignored)
```

## 🛠️ Development

### Prerequisites
- Node.js >= 18.0.0
- npm >= 9.0.0

### TypeScript Support
TypeScript is installed as a dev dependency for future use when the full framework supports it.

### Cleaning Up Test Files

To clean up test output files (coverage, test-results, etc.):

```bash
npm run clean
```

This will remove:
- `dist/` - Compiled TypeScript output
- `coverage/` - Test coverage reports
- `test-results/` - Test result files
- `.vitest/` - Vitest cache

### Ignored Files

The following files/directories are ignored by git (see `.gitignore`):
- `node_modules/` - npm dependencies
- `test-results/` - Test output files
- `coverage/` - Coverage reports
- `dist/` - Build output
- `*.log` - Log files

## 📝 Notes

- The Vibium package (v0.1.2) is now available with the full API
- The example files demonstrate the expected API structure based on the GitHub repository
- Both files use ES modules (`import/export`) as configured in `package.json`
- The package is now available - update imports to use the full API
