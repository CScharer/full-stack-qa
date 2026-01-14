/**
 * Cypress Database Utility
 * Adapter for querying the database directly in Cypress tests
 * 
 * Extends the base DbUtility class to provide Cypress-specific
 * implementation using cy.exec() to run the Python helper script.
 * 
 * Automatically detects environment from Cypress.env().
 * 
 * Usage:
 *   import { CypressDbUtility } from '../support/db-utils';
 *   import type { JobSearchSite } from '../../../lib/db-utils';
 * 
 *   let dbUtils: CypressDbUtility;
 *   let sites: JobSearchSite[];
 * 
 *   beforeEach(() => {
 *     // No need to manually extract environment - handled automatically
 *     dbUtils = new CypressDbUtility();
 *     
 *     dbUtils.getJobSearchSites().then((sites) => {
 *       // Use sites...
 *     });
 *   });
 */
import { DbUtility, JobSearchSite, DbUtilityOptions } from '../../../lib/db-utils';

export class CypressDbUtility extends DbUtility {
  /**
   * Constructor for Cypress Database Utility
   * 
   * Automatically detects environment from Cypress.env().
   * Options can be provided to override automatic detection.
   * 
   * @param options - Optional configuration (environment, helperScriptPath)
   *                  If not provided, automatically uses Cypress.env('ENVIRONMENT')
   */
  constructor(options?: DbUtilityOptions) {
    // If options not provided, automatically detect from Cypress.env()
    if (!options) {
      const environment = (Cypress.env('ENVIRONMENT') || 'dev') as string;
      // Cypress runs from cypress/ directory, so go up one level to reach project root
      const helperScriptPath = '../helpers/db-query-helper.py';
      super({ environment, helperScriptPath });
    } else {
      // If options provided but environment not set, try to detect from Cypress.env()
      const environment = options.environment || (Cypress.env('ENVIRONMENT') || 'dev') as string;
      const helperScriptPath = options.helperScriptPath || '../helpers/db-query-helper.py';
      super({ environment, helperScriptPath });
    }
  }

  /**
   * Get job search sites directly from database using cy.exec()
   * 
   * Note: This method returns a Cypress chainable, not a Promise.
   * Use it within Cypress test context with .then() to get the results.
   * 
   * @param includeDeleted - Whether to include deleted sites
   * @returns Cypress chainable that resolves to array of job search sites
   */
  getJobSearchSites(includeDeleted: boolean = false): Cypress.Chainable<JobSearchSite[]> {
    const includeDeletedFlag = includeDeleted ? '--include-deleted' : '';
    const command = `python3 ${this.helperScriptPath} job-search-sites --environment ${this.environment} ${includeDeletedFlag}`.trim();
    
    return cy.exec(command).then((result) => {
      try {
        const sites = JSON.parse(result.stdout);
        return sites;
      } catch (error) {
        throw new Error(`Failed to parse database query result: ${error}`);
      }
    });
  }
}
