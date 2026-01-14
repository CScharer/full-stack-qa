/**
 * Playwright Database Utility
 * Adapter for querying the database directly in Playwright tests
 * 
 * Extends the base DbUtility class to provide Playwright-specific
 * implementation using Node.js child_process.exec() to run the Python helper script.
 * 
 * Automatically detects environment from process.env.
 * 
 * Usage:
 *   import { PlaywrightDbUtility } from '../helpers/db-utils';
 *   import type { JobSearchSite } from '../../lib/db-utils';
 * 
 *   let dbUtils: PlaywrightDbUtility;
 *   let sites: JobSearchSite[];
 * 
 *   beforeEach(async () => {
 *     // No need to manually extract environment - handled automatically
 *     dbUtils = new PlaywrightDbUtility();
 *     
 *     sites = await dbUtils.getJobSearchSites();
 *   });
 */
import { DbUtility, JobSearchSite, DbUtilityOptions } from '../../lib/db-utils';
import { exec } from 'child_process';
import { promisify } from 'util';
import path from 'path';

const execAsync = promisify(exec);

export class PlaywrightDbUtility extends DbUtility {
  /**
   * Constructor for Playwright Database Utility
   * 
   * Automatically detects environment from process.env.
   * Options can be provided to override automatic detection.
   * 
   * @param options - Optional configuration (environment, helperScriptPath)
   *                  If not provided, automatically uses process.env.ENVIRONMENT
   */
  constructor(options?: DbUtilityOptions) {
    // If options not provided, automatically detect from process.env
    if (!options) {
      const environment = process.env.ENVIRONMENT || 'dev';
      // Get absolute path to the helper script (from project root)
      const helperScriptPath = path.resolve(__dirname, '../../helpers/db-query-helper.py');
      super({ environment, helperScriptPath });
    } else {
      // If options provided but environment not set, try to detect from process.env
      const environment = options.environment || process.env.ENVIRONMENT || 'dev';
      const helperScriptPath = options.helperScriptPath || path.resolve(__dirname, '../../helpers/db-query-helper.py');
      super({ environment, helperScriptPath });
    }
  }

  /**
   * Get job search sites directly from database using Node.js exec()
   * 
   * @param includeDeleted - Whether to include deleted sites
   * @returns Promise resolving to array of job search sites
   */
  async getJobSearchSites(includeDeleted: boolean = false): Promise<JobSearchSite[]> {
    const includeDeletedFlag = includeDeleted ? '--include-deleted' : '';
    const command = `python3 "${this.helperScriptPath}" job-search-sites --environment ${this.environment} ${includeDeletedFlag}`.trim();
    
    try {
      const { stdout, stderr } = await execAsync(command);
      
      if (stderr) {
        console.warn('Database query warning:', stderr);
      }
      
      const sites = JSON.parse(stdout);
      return sites;
    } catch (error: any) {
      const errorMessage = error.message || String(error);
      throw new Error(`Database query failed: ${errorMessage}`);
    }
  }
}
