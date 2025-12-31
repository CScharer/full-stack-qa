# Artillery + Playwright Load Testing

This directory contains Artillery configuration and scenarios for browser-based load testing using Playwright.

## Structure

```
artillery/
├── config/              # Environment-specific configurations
│   ├── dev.yml         # Development environment
│   ├── test.yml        # Test environment
│   └── prod.yml        # Production environment (use with caution)
├── scenarios/          # Load test scenario definitions
│   ├── homepage-load.yml
│   ├── applications-flow.yml
│   └── integration-flow.yml
└── processors/         # Playwright browser interaction scripts
    ├── homepage-processor.js
    └── applications-processor.js
```

## Usage

### Run a specific scenario:
```bash
cd playwright
npm run load:test -- artillery/scenarios/homepage-load.yml
```

### Run all scenarios:
```bash
npm run load:test:all
```

### Run with specific environment:
```bash
artillery run artillery/scenarios/homepage-load.yml --config artillery/config/dev.yml
```

## Configuration

Each environment config file (`config/*.yml`) defines:
- Target URL
- Load phases (warm-up, sustained load, cool-down)
- Browser settings
- Processor scripts

## Scenarios

Each scenario file (`scenarios/*.yml`) defines:
- Test flow
- User weights
- Think times
- Custom variables

## Processors

Each processor file (`processors/*.js`) contains:
- Playwright browser interactions
- Core Web Vitals tracking
- Custom metrics collection
- Error handling

## Integration with Existing Tests

This setup is designed to:
- ✅ Reuse existing Playwright page objects (from `tests/integration/pages/`)
- ✅ Maintain separation between functional and load tests
- ✅ Share test logic where appropriate
- ✅ Run independently or integrated with CI/CD

## Next Steps

1. Review the structure and configuration files
2. Install Artillery dependencies: `npm install`
3. Run a test scenario to verify setup
4. Integrate with CI/CD pipeline (optional)

