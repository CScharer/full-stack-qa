/**
 * Cypress Test Utilities Adapter
 * 
 * This adapter reads test names from the JSON file using cy.task()
 * to work around Cypress's webpack bundler limitations with JSON imports.
 * 
 * The JSON file is loaded once in the support file and cached globally.
 * 
 * Usage:
 *   import { getTestSuite } from '../support/test-utils';
 *   
 *   describe('Wizard Tests', () => {
 *     let wizard: ReturnType<typeof getTestSuite>;
 *     
 *     before(() => {
 *       cy.task('readTestUtilsJson').then((data: any) => {
 *         wizard = getTestSuite('wizard', data);
 *       });
 *     });
 *     
 *     it(() => wizard.tests.test_home, () => {
 *       // test implementation
 *     });
 *   });
 */

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
 * Get test suite configuration by suite name
 * 
 * @param suiteName - The suite key (e.g., 'wizard', 'homepage')
 * @param testNamesData - The loaded JSON data (from cy.readFile() or cy.task())
 * @returns Test suite configuration with suiteName and tests dictionary
 * @throws Error if suite name is not found
 */
export function getTestSuite(suiteName: string, testNamesData: any): TestSuite {
  const suite = testNamesData[suiteName];
  if (!suite) {
    throw new Error(`Test suite '${suiteName}' not found in test-utils.json. Available suites: ${Object.keys(testNamesData).join(', ')}`);
  }
  return suite;
}

/**
 * Get suite name by suite key
 * 
 * @param suiteName - The suite key (e.g., 'wizard')
 * @param testNamesData - The loaded JSON data
 * @returns The suite name string (e.g., 'Wizard Tests')
 */
export function getSuiteName(suiteName: string, testNamesData: any): string {
  return getTestSuite(suiteName, testNamesData).suiteName;
}

/**
 * Get all test names for a suite
 * 
 * @param suiteName - The suite key (e.g., 'wizard')
 * @param testNamesData - The loaded JSON data
 * @returns Dictionary of test names keyed by test ID
 */
export function getTestNames(suiteName: string, testNamesData: any): Record<string, string> {
  return getTestSuite(suiteName, testNamesData).tests;
}

/**
 * Get a specific test name by suite and test ID
 * 
 * @param suiteName - The suite key (e.g., 'wizard')
 * @param testId - The test ID (e.g., 'test_home')
 * @param testNamesData - The loaded JSON data
 * @returns The test name string
 * @throws Error if suite or test ID is not found
 */
export function getTestName(suiteName: string, testId: string, testNamesData: any): string {
  const tests = getTestNames(suiteName, testNamesData);
  const testName = tests[testId];
  if (!testName) {
    throw new Error(`Test '${testId}' not found in suite '${suiteName}'. Available tests: ${Object.keys(tests).join(', ')}`);
  }
  return testName;
}
