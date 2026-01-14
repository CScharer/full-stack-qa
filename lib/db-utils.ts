/**
 * Shared Database Query Utility
 * Base class for querying the database directly in tests
 * 
 * This utility provides a common interface for both Cypress and Playwright
 * to query the database directly, bypassing the API layer. Framework-specific
 * adapters extend this class to implement framework-specific execution mechanisms.
 * 
 * Usage:
 *   // In Cypress
 *   import { CypressDbUtility } from '../support/db-utils';
 *   const dbUtils = new CypressDbUtility();
 *   const sites = await dbUtils.getJobSearchSites();
 * 
 *   // In Playwright
 *   import { PlaywrightDbUtility } from '../helpers/db-utils';
 *   const dbUtils = new PlaywrightDbUtility();
 *   const sites = await dbUtils.getJobSearchSites();
 */
import { getBackendUrl } from '../config/port-config';

/**
 * Job Search Site data structure (matches database schema)
 */
export interface JobSearchSite {
  id: number;
  name: string;
  url?: string;
  is_deleted: number;
  created_on: string;
  modified_on: string;
  created_by: string;
  modified_by: string;
}

/**
 * Options for database utility initialization
 */
export interface DbUtilityOptions {
  /** Environment name (dev, test, prod) - used to determine database */
  environment?: string;
  /** Path to the database query helper script */
  helperScriptPath?: string;
}

/**
 * Base Database Utility class
 * 
 * Provides common functionality for querying the database directly.
 * Framework-specific adapters must extend this class and implement the abstract methods.
 */
export class DbUtility {
  protected environment: string;
  protected helperScriptPath: string;

  constructor(options?: DbUtilityOptions) {
    // Priority 1: Explicit environment in options
    // Priority 2: Environment from process.env.ENVIRONMENT
    // Priority 3: Fallback to 'dev'
    this.environment = options?.environment || process.env.ENVIRONMENT || 'dev';
    
    // Default helper script path (can be overridden)
    this.helperScriptPath = options?.helperScriptPath || 'helpers/db-query-helper.py';
  }

  /**
   * Get the current environment
   */
  getEnvironment(): string {
    return this.environment;
  }

  /**
   * Get job search sites directly from database
   * 
   * Must be implemented by framework-specific adapters.
   * 
   * Note: Framework-specific adapters may return different types:
   * - Playwright: Promise<JobSearchSite[]>
   * - Cypress: Cypress.Chainable<JobSearchSite[]>
   * 
   * @param includeDeleted - Whether to include deleted sites
   * @returns Framework-specific return type (Promise or Chainable)
   */
  getJobSearchSites(includeDeleted: boolean = false): any {
    // Implementation will be framework-specific via adapters
    throw new Error('Must use framework-specific adapter (CypressDbUtility or PlaywrightDbUtility)');
  }
}
