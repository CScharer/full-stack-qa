import { defineConfig } from 'cypress'

export default defineConfig({
  e2e: {
    baseUrl: process.env.BASE_URL || 'http://localhost:3003', // Default to DEV port per ONE_GOAL.md
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
      // Standardized to use BASE_URL (consistent with other frameworks)
      if (process.env.BASE_URL) {
        config.baseUrl = process.env.BASE_URL
      }
      
      // Make environment variables available to Cypress tests via Cypress.env()
      // This is necessary because process.env is not available in the browser context
      if (process.env.BACKEND_URL) {
        config.env.BACKEND_URL = process.env.BACKEND_URL
      }
      if (process.env.ENVIRONMENT) {
        config.env.ENVIRONMENT = process.env.ENVIRONMENT
      }
      
      // Task to read test-utils.json from Node.js context
      const fs = require('fs')
      const path = require('path')
      on('task', {
        readTestUtilsJson() {
          const jsonPath = path.join(__dirname, '../lib/test-utils.json')
          const jsonContent = fs.readFileSync(jsonPath, 'utf-8')
          return JSON.parse(jsonContent)
        }
      })
      
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
        // Handle both CypressRunResult and CypressFailedRunResult types
        const stats = {
          tests: ('totalTests' in results ? results.totalTests : 0) || 0,
          passes: ('totalPassed' in results ? results.totalPassed : 0) || 0,
          failures: ('totalFailed' in results ? results.totalFailed : 0) || 0,
          pending: ('totalPending' in results ? results.totalPending : 0) || 0,
          duration: ('totalDuration' in results ? results.totalDuration : 0) || 0
        }
        fs.writeFileSync(resultsFile, JSON.stringify({
          stats,
          results: ('runs' in results ? results.runs : []) || []
        }, null, 2))
      })
      
      return config
    },
  },
  component: {
    devServer: {
      framework: 'react',
      bundler: 'webpack',
    },
  },
})

