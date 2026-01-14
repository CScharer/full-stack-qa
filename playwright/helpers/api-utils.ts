/**
 * Playwright API Request Utility
 * Adapter for making API requests in Playwright tests
 * 
 * Extends the base ApiRequestUtility class to provide Playwright-specific
 * implementation using the Playwright request context.
 * 
 * Automatically detects environment and backend URL from process.env.
 * 
 * Usage:
 *   import { PlaywrightApiRequestUtility } from '../helpers/api-utils';
 *   import type { EntityCounts } from '../../lib/api-utils';
 * 
 *   let apiUtils: PlaywrightApiRequestUtility;
 *   let initialCounts: EntityCounts;
 * 
 *   beforeEach(async ({ request }) => {
 *     // No need to manually extract environment/backendUrl - handled automatically
 *     apiUtils = new PlaywrightApiRequestUtility(request);
 *     
 *     initialCounts = await apiUtils.getAllEntityCounts();
 *   });
 */
import { APIRequestContext, expect } from '@playwright/test';
import { ApiRequestUtility, EntityCounts, ApiRequestOptions, JobSearchSitesResponse } from '../../lib/api-utils';

export class PlaywrightApiRequestUtility extends ApiRequestUtility {
  private request: APIRequestContext;

  /**
   * Constructor for Playwright API Request Utility
   * 
   * Automatically detects environment and backend URL from process.env.
   * Options can be provided to override automatic detection.
   * 
   * @param request - Playwright API request context (required)
   * @param options - Optional configuration (environment, backendUrl)
   *                  If not provided, automatically uses process.env.ENVIRONMENT and process.env.BACKEND_URL
   */
  constructor(request: APIRequestContext, options?: ApiRequestOptions) {
    // If options not provided, automatically detect from process.env
    if (!options) {
      const environment = process.env.ENVIRONMENT || 'dev';
      const backendUrl = process.env.BACKEND_URL;
      super({ environment, backendUrl });
    } else {
      // If options provided but environment/backendUrl not set, try to detect from process.env
      const environment = options.environment || process.env.ENVIRONMENT || 'dev';
      const backendUrl = options.backendUrl || process.env.BACKEND_URL;
      super({ environment, backendUrl });
    }
    this.request = request;
  }

  /**
   * Get count for a specific entity type using Playwright request context
   * 
   * @param entityType - The type of entity to get count for
   * @returns Promise resolving to the entity count
   */
  async getEntityCount(entityType: 'applications' | 'companies' | 'contacts' | 'clients' | 'notes'): Promise<number> {
    const endpoint = this.getEntityEndpoint(entityType);
    
    try {
      const response = await this.request.get(`${this.getBackendUrl()}${endpoint}`);
      if (response.ok()) {
        const data = await response.json();
        return data.total || 0;
      }
    } catch (error) {
      console.warn(`Failed to get ${entityType} count:`, error);
    }
    return 0;
  }

  /**
   * Get all entity counts at once
   * 
   * @returns Promise resolving to all entity counts
   */
  async getAllEntityCounts(): Promise<EntityCounts> {
    const [applications, companies, contacts, clients, notes] = await Promise.all([
      this.getEntityCount('applications'),
      this.getEntityCount('companies'),
      this.getEntityCount('contacts'),
      this.getEntityCount('clients'),
      this.getEntityCount('notes'),
    ]);

    return {
      applications,
      companies,
      contacts,
      clients,
      notes,
    };
  }

  /**
   * Verify entity count matches expected value
   * 
   * @param entityType - The type of entity to verify
   * @param expectedCount - The expected count
   */
  async verifyEntityCount(entityType: 'applications' | 'companies' | 'contacts' | 'clients' | 'notes', expectedCount: number): Promise<void> {
    const actualCount = await this.getEntityCount(entityType);
    expect(actualCount).toBe(expectedCount);
  }

  /**
   * Verify all entity counts match expected values
   * 
   * @param expectedCounts - The expected counts for all entities
   */
  async verifyAllEntityCounts(expectedCounts: EntityCounts): Promise<void> {
    await Promise.all([
      this.verifyEntityCount('applications', expectedCounts.applications),
      this.verifyEntityCount('companies', expectedCounts.companies),
      this.verifyEntityCount('contacts', expectedCounts.contacts),
      this.verifyEntityCount('clients', expectedCounts.clients),
      this.verifyEntityCount('notes', expectedCounts.notes),
    ]);
  }

  /**
   * Get job search sites from API using Playwright request context
   * 
   * @param options - Optional query parameters (page, limit, include_deleted)
   * @returns Promise resolving to job search sites response
   */
  async getJobSearchSites(options?: {
    page?: number;
    limit?: number;
    include_deleted?: boolean;
  }): Promise<JobSearchSitesResponse> {
    const apiVersion = this.getEntityApiVersion('applications'); // Use default v1
    let url = `${this.getBackendUrl()}/api/${apiVersion}/job-search-sites`;
    
    const params: string[] = [];
    if (options?.page) params.push(`page=${options.page}`);
    if (options?.limit) params.push(`limit=${options.limit}`);
    if (options?.include_deleted !== undefined) params.push(`include_deleted=${options.include_deleted}`);
    
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }
    
    try {
      const response = await this.request.get(url);
      if (response.ok()) {
        return await response.json();
      } else {
        throw new Error(`Failed to get job search sites: ${response.status()} ${response.statusText()}`);
      }
    } catch (error) {
      console.warn('Failed to get job search sites:', error);
      throw error;
    }
  }
}
