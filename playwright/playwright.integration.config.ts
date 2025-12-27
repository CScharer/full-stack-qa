import { defineConfig, devices } from '@playwright/test';
import { exec } from 'child_process';
import { promisify } from 'util';
import { getPortsForEnvironment } from './config/port-config';

const execAsync = promisify(exec);

/**
 * Playwright configuration for integration tests
 * Tests the full stack: Frontend + Backend + Database
 * 
 * Environment Support:
 * - Defaults to 'dev' environment (ports 8003/3003)
 * - Can be overridden via ENVIRONMENT env var (dev, test, prod)
 * - Ports are automatically selected from centralized config (config/ports.json)
 */

// Get environment from env var, default to 'dev' to match other scripts
const environment = process.env.ENVIRONMENT || 'dev';

// Get ports from centralized configuration
const ports = getPortsForEnvironment(environment, 'dev');
const apiPort = ports.backend.port.toString();
const frontendPort = ports.frontend.port.toString();
const frontendUrl = ports.frontend.url.replace('localhost', '127.0.0.1');
const apiUrl = ports.backend.url;

console.log(`🔧 Playwright Integration Config: Using ${environment.toUpperCase()} environment`);
console.log(`   Backend: ${apiUrl} (port ${apiPort})`);
console.log(`   Frontend: ${frontendUrl} (port ${frontendPort})`);

export default defineConfig({
  testDir: './tests/integration',
  /* Run tests in files in parallel */
  fullyParallel: false, // Run sequentially to avoid database conflicts
  /* Fail the build on CI if you accidentally left test.only in the source code. */
  forbidOnly: !!process.env.CI,
  /* Retry configuration - global retry for all tests */
  retries: 1,
  /* Opt out of parallel tests on CI. */
  workers: 1, // Single worker to avoid database conflicts
  /* Reporter to use. See https://playwright.dev/docs/test-reporters */
  reporter: [
    ['html', { outputFolder: 'playwright-report-integration' }],
    ['list'],
    ['junit', { outputFile: 'test-results/integration-junit.xml' }]
  ],
  /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    /* Base URL to use in actions like `await page.goto('/')`. */
    baseURL: process.env.FRONTEND_URL || frontendUrl,
    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },

  /* Configure projects for major browsers */
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],

  /* Run your local dev server before starting the tests */
  webServer: [
    {
      command: `cd ../backend && source venv/bin/activate && python -m uvicorn app.main:app --host 0.0.0.0 --port ${apiPort}`,
      url: `${apiUrl}/health`,
      timeout: 120 * 1000,
      reuseExistingServer: !process.env.CI,
      stdout: 'pipe',
      stderr: 'pipe',
      env: {
        ENVIRONMENT: environment, // Uses environment-specific database (full_stack_qa_{env}.db)
        API_HOST: '0.0.0.0',
        API_PORT: apiPort,
        CORS_ORIGINS: `${frontendUrl},http://localhost:${frontendPort}`,
      },
    },
    {
      command: `cd ../frontend && PORT=${frontendPort} npm run dev`,
      url: frontendUrl,
      timeout: 120 * 1000,
      reuseExistingServer: !process.env.CI,
      stdout: 'pipe',
      stderr: 'pipe',
      env: {
        PORT: frontendPort,
        NEXT_PUBLIC_API_URL: `${apiUrl}/api/v1`,
      },
    },
  ],
});
