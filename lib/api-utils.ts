/**
 * Shared API Request Utility
 * Base class for making API requests to get entity counts
 * 
 * This utility provides a common interface for both Cypress and Playwright
 * to interact with the backend API. Framework-specific adapters extend this
 * class to implement framework-specific request mechanisms.
 * 
 * Usage:
 *   // In Cypress
 *   import { CypressApiRequestUtility } from '../support/api-utils';
 *   const apiUtils = new CypressApiRequestUtility({ environment: 'dev' });
 *   const counts = await apiUtils.getAllEntityCounts();
 * 
 *   // In Playwright
 *   import { PlaywrightApiRequestUtility } from '../helpers/api-utils';
 *   const apiUtils = new PlaywrightApiRequestUtility(request, { environment: 'dev' });
 *   const counts = await apiUtils.getAllEntityCounts();
 */
import { getBackendUrl } from '../config/port-config';

/**
 * Default API version
 */
export const DEFAULT_API_VERSION = 'v1';

/**
 * API version mapping for each entity type
 * Allows different entities to use different API versions if needed
 */
export const ENTITY_API_VERSIONS: Record<string, string> = {
  applications: DEFAULT_API_VERSION,
  companies: DEFAULT_API_VERSION,
  contacts: DEFAULT_API_VERSION,
  clients: DEFAULT_API_VERSION,
  notes: DEFAULT_API_VERSION,
};

/**
 * Entity counts for all tracked entities
 */
export interface EntityCounts {
  applications: number;
  companies: number;
  contacts: number;
  clients: number;
  notes: number;
}

/**
 * Job Search Site data structure
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
 * API response for job search sites list
 */
export interface JobSearchSitesResponse {
  data: JobSearchSite[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    pages: number;
  };
}

/**
 * Options for API request utility initialization
 */
export interface ApiRequestOptions {
  /** Override backend URL (takes precedence over environment) */
  backendUrl?: string;
  /** Environment name (dev, test, prod) - used to determine backend URL */
  environment?: string;
}

/**
 * Base API Request Utility class
 * 
 * Provides common functionality for making API requests to get entity counts.
 * Framework-specific adapters must extend this class and implement the abstract methods.
 */
export class ApiRequestUtility {
  protected backendUrl: string;

  constructor(options?: ApiRequestOptions) {
    // Priority 1: Explicit backendUrl in options (allows CI/CD override)
    // Priority 2: Environment from options
    // Priority 3: Environment from process.env.ENVIRONMENT
    // Priority 4: Fallback to 'dev'
    const environment = options?.environment || process.env.ENVIRONMENT || 'dev';
    this.backendUrl = options?.backendUrl || getBackendUrl(environment);
  }

  /**
   * Get the base backend URL
   */
  getBackendUrl(): string {
    return this.backendUrl;
  }

  /**
   * Get count for a specific entity type
   * 
   * Must be implemented by framework-specific adapters
   * 
   * @param entityType - The type of entity to get count for
   * @returns Promise resolving to the entity count
   */
  async getEntityCount(entityType: 'applications' | 'companies' | 'contacts' | 'clients' | 'notes'): Promise<number> {
    // Implementation will be framework-specific via adapters
    throw new Error('Must use framework-specific adapter (CypressApiRequestUtility or PlaywrightApiRequestUtility)');
  }

  /**
   * Get all entity counts at once
   * 
   * Must be implemented by framework-specific adapters
   * 
   * @returns Promise resolving to all entity counts
   */
  async getAllEntityCounts(): Promise<EntityCounts> {
    // Implementation will be framework-specific via adapters
    throw new Error('Must use framework-specific adapter (CypressApiRequestUtility or PlaywrightApiRequestUtility)');
  }

  /**
   * Get API version for a specific entity type
   * 
   * @param entityType - The type of entity
   * @returns The API version (e.g., 'v1', 'v2')
   */
  protected getEntityApiVersion(entityType: string): string {
    return ENTITY_API_VERSIONS[entityType] || DEFAULT_API_VERSION;
  }

  /**
   * Get API endpoint for entity type
   * 
   * @param entityType - The type of entity
   * @returns The API endpoint path
   */
  protected getEntityEndpoint(entityType: string): string {
    const apiVersion = this.getEntityApiVersion(entityType);
    const endpoints: Record<string, string> = {
      applications: `/api/${apiVersion}/applications?limit=1`,
      companies: `/api/${apiVersion}/companies?limit=1`,
      contacts: `/api/${apiVersion}/contacts?limit=1`,
      clients: `/api/${apiVersion}/clients?limit=1`,
      notes: `/api/${apiVersion}/notes?limit=1`,
    };
    return endpoints[entityType] || '';
  }

  /**
   * Get job search sites from API
   * 
   * Must be implemented by framework-specific adapters
   * 
   * @param options - Optional query parameters (page, limit, include_deleted)
   * @returns Promise resolving to job search sites response
   */
  async getJobSearchSites(options?: {
    page?: number;
    limit?: number;
    include_deleted?: boolean;
  }): Promise<JobSearchSitesResponse> {
    // Implementation will be framework-specific via adapters
    throw new Error('Must use framework-specific adapter (CypressApiRequestUtility or PlaywrightApiRequestUtility)');
  }
}
