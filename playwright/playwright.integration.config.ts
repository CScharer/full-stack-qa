import { defineConfig, devices } from '@playwright/test';
import { exec } from 'child_process';
import { promisify } from 'util';

const execAsync = promisify(exec);

/**
 * Playwright configuration for integration tests
 * Tests the full stack: Frontend + Backend + Database
 */
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
    baseURL: process.env.FRONTEND_URL || 'http://127.0.0.1:3003',
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
      command: 'cd ../backend && source venv/bin/activate && python -m uvicorn app.main:app --host 0.0.0.0 --port 8008',
      url: 'http://localhost:8008/health',
      timeout: 120 * 1000,
      reuseExistingServer: !process.env.CI,
      stdout: 'pipe',
      stderr: 'pipe',
      env: {
        DATABASE_PATH: '../Data/Core/full_stack_testing.db',
        API_HOST: '0.0.0.0',
        API_PORT: '8008',
        CORS_ORIGINS: 'http://127.0.0.1:3003,http://localhost:3003',
      },
    },
    {
      command: 'cd ../frontend && PORT=3003 npm run dev',
      url: 'http://127.0.0.1:3003',
      timeout: 120 * 1000,
      reuseExistingServer: !process.env.CI,
      stdout: 'pipe',
      stderr: 'pipe',
      env: {
        PORT: '3003',
        NEXT_PUBLIC_API_URL: 'http://localhost:8008/api/v1',
      },
    },
  ],
});
