# Cypress Tests (TypeScript)

This directory contains Cypress end-to-end tests written in **TypeScript** for the full-stack-qa framework.

## Prerequisites

- Node.js >= 18.0.0
- npm >= 9.0.0

## Installation

```bash
cd cypress
npm install
```

## Running Tests

### Interactive Mode (Cypress Test Runner)
```bash
npm run cypress:open
```

### Headless Mode
```bash
npm run cypress:run
```

### Run in Specific Browser
```bash
npm run cypress:run:chrome
npm run cypress:run:firefox
npm run cypress:run:edge
```

### Using Helper Script
```bash
# From project root
./scripts/run-cypress-tests.sh run chrome
./scripts/run-cypress-tests.sh open
```

## Test Structure

- `cypress/e2e/` - End-to-end test files (`.cy.ts`)
- `cypress/support/` - Custom commands and configuration (`.ts`)
- `cypress/fixtures/` - Test data files
- `tsconfig.json` - TypeScript configuration

## TypeScript

All tests are written in TypeScript for type safety and better IDE support.

### Type Checking
```bash
npm run build  # Type check without running tests
```

## Configuration

Edit `cypress.config.ts` to modify:
- Base URL
- Viewport size
- Timeouts
- Screenshot/video settings

