# Vibium Tests

This directory contains Vibium test framework examples and research documentation.

## 📋 Overview

**Vibium** is an AI-native test automation framework developed by Jason Huggins (creator of Selenium and Appium). This repo consumes it as an npm dependency (**`vibium` ^26.3.18**; see [`package.json`](./package.json) for the exact range).

Optional platform packages (**`@vibium/darwin-arm64`**, **`@vibium/linux-x64`**, same major line) supply the clicker binary. The published `vibium` tarball may not include `dist/` until install/postinstall completes; **Vitest** and helpers avoid a static `import "vibium"` at load time so mock tests run in CI without a full binary layout.

## 📦 Installation

```bash
cd vibium
npm install
```

This installs:

- **`vibium`** (^26.3.18) — core package
- **`@vibium/darwin-arm64`** / **`@vibium/linux-x64`** (^26.3.18) — optional; clicker binary for local OS when applicable
- **TypeScript**, **Vitest**, **tsx**, and **`@types/node`**

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

## 📝 Example Code

### `helpers/example.ts` — Examples and mocks

TypeScript examples for the async (`browser`) and sync (`browserSync`) APIs, plus mock implementations that do not require the real package at import time. Real API entry points use **dynamic** `import("vibium")` inside the functions that need a browser.

**Run the helper script (tsx):**

```bash
cd vibium
npm run vibe
```

### `tests/example.spec.ts` — Vitest suite

Runs mock-based tests always; real-browser tests are **skipped** when the clicker binary or package layout is not available (see logs in CI/local).

Legacy **`example_mock.js`** / **`example_handle.js`** referenced in older docs are **not** in this tree anymore; use **`helpers/example.ts`** and **`tests/example.spec.ts`** instead.

## 📚 API Structure

Both patterns match the declarations in [`types/vibium.d.ts`](./types/vibium.d.ts):

### Async API (Promise-based)

```typescript
import { browser } from "vibium";

async function asyncAPI() {
  const vibe = await browser.launch({ headless: true });
  await vibe.go("https://example.com");

  const el = await vibe.find("button.submit");
  await el.click();
  await el.type("hello");

  const png = await vibe.screenshot();
  await vibe.quit();
}
```

In this repo, prefer **dynamic** `const { browser } = await import("vibium")` inside functions when Vitest or partial installs must not resolve `vibium` at module load time.

### Sync API (blocking)

```typescript
import { browserSync } from "vibium";

function syncAPI() {
  const vibe = browserSync.launch({ headless: true });
  vibe.go("https://example.com");

  const el = vibe.find("button.submit");
  el.click();
  el.type("hello");

  const png = vibe.screenshot();
  vibe.quit();
}
```

## 📖 Documentation

- **Research Document**: `20251219_VIBIUM_FRAMEWORK_RESEARCH.md` — research and findings
- **Official Repository**: [https://github.com/VibiumDev/vibium](https://github.com/VibiumDev/vibium)
- **Official Website**: [https://vibium.com](https://vibium.com)

## ✅ Current Status

- **npm Package**: `vibium` **^26.3.18** (see `package.json`; lockfile pins a concrete version)
- **API**: Async **`browser`** and sync **`browserSync`** (see `types/vibium.d.ts`)
- **CI**: Vibium Vitest job runs mock tests; real browser usage is optional when binary + `dist` are present

## 🔮 Future Integration

1. Add more specs under `tests/` as the app and Vibium evolve.
2. Optionally enable stricter real-browser checks in CI where runners install **`@vibium/linux-x64`** and a compatible environment.
3. Keep `import("vibium")` lazy in shared helpers if the npm layout remains “binary/dist after install” sensitive.

## 📦 Project Structure

```
vibium/
├── README.md                          # This file
├── package.json                       # npm configuration
├── tsconfig.json
├── vitest.config.ts
├── types/vibium.d.ts                  # Local module typings for ^26.x
├── helpers/example.ts                 # Examples + mocks (tsx: npm run vibe)
├── tests/example.spec.ts              # Vitest tests
├── config/port-config.ts
├── 20251219_VIBIUM_FRAMEWORK_RESEARCH.md
└── node_modules/                      # Dependencies (gitignored)
```

## 🛠️ Development

### Prerequisites

- **Node.js** >= 20.0.0
- **npm** >= 9.0.0

### TypeScript

TypeScript **7.x** and Vitest **4.x** are configured for this package; run **`npm run type-check`** for `tsc --noEmit`.

### Cleaning Up Test Files

```bash
npm run clean
```

Removes:

- `dist/` — compiled TypeScript output (if present)
- `coverage/` — coverage reports
- `test-results/` — Vitest JSON output
- `.vitest/` — Vitest cache

### Ignored Files

Ignored by git (see `.gitignore`):

- `node_modules/`
- `test-results/`
- `coverage/`
- `dist/`
- `*.log`

## 📝 Notes

- Version numbers in docs drift; **`package.json`** and the lockfile are the source of truth for **`vibium`** / **`@vibium/*`**.
- ES modules (`"type": "module"`) are used throughout; examples use `import` / `export`.
- Official docs and releases may move faster than this README; cross-check [VibiumDev/vibium](https://github.com/VibiumDev/vibium) for breaking changes.
