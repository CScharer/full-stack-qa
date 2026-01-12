# Frontend Application

This is a [Next.js](https://nextjs.org) project with TypeScript that provides the user interface for the job search application.

## Getting Started

### Prerequisites

- Node.js >= 18.0.0
- npm >= 9.0.0

### Development

First, run the development server:

```bash
npm run dev
```

Open [http://localhost:3003](http://localhost:3003) with your browser to see the result.

The page auto-updates as you edit the file.

## Testing

### Running Tests

```bash
# Run all tests (functional + snapshot)
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage

# Run tests in UI mode (interactive)
npm run test:ui
```

### Snapshot Tests

The frontend includes comprehensive snapshot tests to catch unintended UI changes.

#### Running Snapshot Tests

```bash
# Run all snapshot tests
npm test -- __tests__/**/*.snapshot.test.tsx

# Run specific snapshot test file
npm test -- __tests__/components/ui/Button.snapshot.test.tsx

# Update snapshots after intentional UI changes
npm test -- -u

# Update specific snapshot file
npm test -- __tests__/components/ui/Button.snapshot.test.tsx -u
```

#### Snapshot Test Coverage

- **UI Components**: 23 snapshot tests (Button, Input, Error, Loading)
- **Complex Components**: 19 snapshot tests (Sidebar, StatusBar, EntitySelect, EntityCreateModal)
- **Page Components**: 23 snapshot tests (all main pages)
- **Total**: 65 snapshot tests

#### Snapshot Files

- Snapshot files are auto-generated in `__snapshots__/` directories
- Commit snapshots to version control
- Review snapshot changes in PRs before accepting
- Update snapshots when making intentional UI changes

### Test Structure

```
frontend/
├── __tests__/
│   ├── components/
│   │   ├── ui/                    # UI component tests
│   │   └── __snapshots__/         # Snapshot files
│   ├── pages/                      # Page component tests
│   └── __mocks__/                  # Mock data
├── vitest.config.ts                # Vitest configuration
└── vitest.setup.ts                 # Test setup
```

## Learn More

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API
- [Vitest Documentation](https://vitest.dev/) - testing framework
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/) - component testing
