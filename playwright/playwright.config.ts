import { defineConfig, devices } from '@playwright/test'
import { getFrontendUrl } from '../config/port-config'

/**
 * Read environment variables from file.
 * https://github.com/motdotla/dotenv
 */
// require('dotenv').config();

/**
 * Get default frontend URL from centralized config
 */
const getDefaultBaseUrl = (): string => {
  try {
    const env = (process.env.ENVIRONMENT || 'dev').toLowerCase()
    return getFrontendUrl(env)
  } catch (error) {
    // Fallback if config can't be read (shouldn't happen, but safe fallback)
    console.warn('Could not read frontend URL from config, using default:', error)
    return 'http://localhost:3003'
  }
}

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
  testDir: './tests',
  /* Run tests in files in parallel */
  fullyParallel: true,
  /* Fail the build on CI if you accidentally left test.only in the source code. */
  forbidOnly: !!process.env.CI,
  /* Retry configuration - global retry for all tests */
  retries: 1,
  /* Opt out of parallel tests on CI. */
  workers: process.env.CI ? 1 : undefined,
  /* Reporter to use. See https://playwright.dev/docs/test-reporters */
  reporter: [
    ['html'],
    ['list'],
    ['junit', { outputFile: 'test-results/junit.xml' }]
  ],
  /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    /* Base URL to use in actions like `await page.goto('/')`. */
    baseURL: process.env.BASE_URL || getDefaultBaseUrl(),
    /* Maximize viewport to full screen */
    viewport: null, // null means use full screen size
    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: 'retain-on-first-failure',
    screenshot: 'only-on-failure',
    video: 'off',
  },

  /* Configure projects for major browsers */
  /* In CI, only run Chromium to save time and resources */
  projects: process.env.CI ? [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ] : [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },

    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },

    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
  ],

  /* Run your local dev server before starting the tests */
  // webServer: {
  //   command: 'npm run start',
  //   url: 'http://127.0.0.1:3003', // Default to DEV port per ONE_GOAL.md
  //   reuseExistingServer: !process.env.CI,
  // },
})

