import { defineConfig } from 'cypress'

export default defineConfig({
  e2e: {
    baseUrl: process.env.CYPRESS_BASE_URL || 'http://localhost:3003', // Default to DEV port per ONE_GOAL.md
    viewportWidth: 1920,
    viewportHeight: 1080,
    video: false,
    screenshotOnRunFailure: true,
    defaultCommandTimeout: 15000,
    requestTimeout: 15000,
    responseTimeout: 15000,
    pageLoadTimeout: 30000,
    /* Retry configuration - global retry for all tests */
    retries: {
      runMode: 1,
      openMode: 0,
    },
    setupNodeEvents(on, config) {
      // implement node event listeners here
      // Allow baseUrl to be overridden by environment variable
      if (process.env.CYPRESS_BASE_URL) {
        config.baseUrl = process.env.CYPRESS_BASE_URL
      }
      
      // Generate JSON results for Allure conversion
      on('after:run', (results) => {
        // Write results summary to JSON file for Allure conversion
        const fs = require('fs')
        const path = require('path')
        const resultsDir = path.join(__dirname, 'cypress', 'results')
        if (!fs.existsSync(resultsDir)) {
          fs.mkdirSync(resultsDir, { recursive: true })
        }
        const resultsFile = path.join(resultsDir, 'cypress-results.json')
        fs.writeFileSync(resultsFile, JSON.stringify({
          stats: {
            tests: results.totalTests || 0,
            passes: results.totalPassed || 0,
            failures: results.totalFailed || 0,
            pending: results.totalPending || 0,
            duration: results.totalDuration || 0
          },
          results: results.runs || []
        }, null, 2))
      })
      
      return config
    },
  },
  component: {
    devServer: {
      framework: 'create-react-app',
      bundler: 'webpack',
    },
  },
})

