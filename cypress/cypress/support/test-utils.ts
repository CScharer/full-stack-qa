/**
 * Cypress Test Utilities Adapter
 * 
 * This adapter provides test utilities for Cypress tests.
 * 
 * ⚠️ IMPORTANT: This file must be kept in sync with lib/test-utils.ts
 * The data here is duplicated due to Cypress's webpack bundler limitations
 * with cross-project imports. When updating test names, update BOTH files.
 * 
 * Usage:
 *   import { getTestSuite } from '../support/test-utils';
 *   const wizard = getTestSuite('wizard');
 *   describe(wizard.suiteName, () => {
 *     it(wizard.tests.test_home, () => {
 *       // test implementation
 *     });
 *   });
 */

// Test names data - MUST match lib/test-utils.ts
// This duplication is necessary due to Cypress webpack bundler limitations
const testNamesData = {
  wizard: {
    suiteName: 'Wizard Tests',
    tests: {
      test_home: 'test_home - Click Home Navigation, Add Application button, then Cancel',
      test_application: 'test_application - Click Applications Navigation, Add button, then Cancel',
      test_companies: 'test_companies - Click Companies Navigation, Add button, populate all fields, then Cancel',
      test_contacts: 'test_contacts - Click Contacts Navigation, Add button, populate all fields, then Cancel',
      test_clients: 'test_clients - Click Clients Navigation, Add button, populate all fields, then Cancel',
      test_notes: 'test_notes - Click Notes Navigation, verify there are no notes',
      test_job_search_sites_api: 'test_job_search_sites_api - Click Job Search Sites Navigation, verify all Names and URLs against API',
      test_job_search_sites_db: 'test_job_search_sites_db - Click Job Search Sites Navigation, verify all Names and URLs against database',
    },
  },
} as const;

/**
 * Test suite configuration structure
 */
export interface TestSuite {
  /** Suite name for describe/test.describe blocks */
  suiteName: string;
  /** Dictionary of test names keyed by test ID */
  tests: Record<string, string>;
}

/**
 * Type for suite keys (e.g., 'wizard', 'homepage', etc.)
 */
export type SuiteKey = keyof typeof testNamesData;

/**
 * Get test suite configuration by suite name
 * 
 * @param suiteName - The suite key (e.g., 'wizard', 'homepage')
 * @returns Test suite configuration with suiteName and tests dictionary
 * @throws Error if suite name is not found
 */
export function getTestSuite(suiteName: SuiteKey): TestSuite {
  const suite = testNamesData[suiteName];
  if (!suite) {
    throw new Error(`Test suite '${suiteName}' not found. Available suites: ${Object.keys(testNamesData).join(', ')}`);
  }
  return suite;
}

/**
 * Get suite name by suite key
 * 
 * @param suiteName - The suite key (e.g., 'wizard')
 * @returns The suite name string (e.g., 'Wizard Tests')
 */
export function getSuiteName(suiteName: SuiteKey): string {
  return getTestSuite(suiteName).suiteName;
}

/**
 * Get all test names for a suite
 * 
 * @param suiteName - The suite key (e.g., 'wizard')
 * @returns Dictionary of test names keyed by test ID
 */
export function getTestNames(suiteName: SuiteKey): Record<string, string> {
  return getTestSuite(suiteName).tests;
}

/**
 * Get a specific test name by suite and test ID
 * 
 * @param suiteName - The suite key (e.g., 'wizard')
 * @param testId - The test ID (e.g., 'test_home')
 * @returns The test name string
 * @throws Error if suite or test ID is not found
 */
export function getTestName(suiteName: SuiteKey, testId: string): string {
  const tests = getTestNames(suiteName);
  const testName = tests[testId];
  if (!testName) {
    throw new Error(`Test '${testId}' not found in suite '${suiteName}'. Available tests: ${Object.keys(tests).join(', ')}`);
  }
  return testName;
}

/**
 * Legacy exports for backward compatibility
 * @deprecated Use getTestSuite('wizard') instead
 */
export const TEST_SUITES = {
  WIZARD_TESTS: getSuiteName('wizard'),
} as const;

/**
 * Legacy exports for backward compatibility
 * @deprecated Use getTestSuite('wizard').tests instead
 */
export const TEST_NAMES = {
  TEST_HOME: getTestName('wizard', 'test_home'),
  TEST_APPLICATION: getTestName('wizard', 'test_application'),
  TEST_COMPANIES: getTestName('wizard', 'test_companies'),
  TEST_CONTACTS: getTestName('wizard', 'test_contacts'),
  TEST_CLIENTS: getTestName('wizard', 'test_clients'),
  TEST_NOTES: getTestName('wizard', 'test_notes'),
  TEST_JOB_SEARCH_SITES_API: getTestName('wizard', 'test_job_search_sites_api'),
  TEST_JOB_SEARCH_SITES_DB: getTestName('wizard', 'test_job_search_sites_db'),
} as const;

export type TestSuiteName = typeof TEST_SUITES[keyof typeof TEST_SUITES];
export type TestName = typeof TEST_NAMES[keyof typeof TEST_NAMES];
