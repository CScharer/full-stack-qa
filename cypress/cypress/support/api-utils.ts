/**
 * Cypress API Request Utility
 * Adapter for making API requests in Cypress tests
 * 
 * Extends the base ApiRequestUtility class to provide Cypress-specific
 * implementation using cy.request().
 * 
 * Automatically detects environment and backend URL from Cypress.env().
 * 
 * Usage:
 *   import { CypressApiRequestUtility } from '../support/api-utils';
 *   import type { EntityCounts } from '../../../lib/api-utils';
 * 
 *   let apiUtils: CypressApiRequestUtility;
 *   let initialCounts: EntityCounts;
 * 
 *   beforeEach(() => {
 *     // No need to manually extract environment/backendUrl - handled automatically
 *     apiUtils = new CypressApiRequestUtility();
 *     
 *     apiUtils.getAllEntityCounts().then((counts) => {
 *       initialCounts = counts;
 *     });
 *   });
 */
import { ApiRequestUtility, EntityCounts, ApiRequestOptions, JobSearchSitesResponse } from '../../../lib/api-utils';

export class CypressApiRequestUtility extends ApiRequestUtility {
  /**
   * Constructor for Cypress API Request Utility
   * 
   * Automatically detects environment and backend URL from Cypress.env().
   * Options can be provided to override automatic detection.
   * 
   * @param options - Optional configuration (environment, backendUrl)
   *                  If not provided, automatically uses Cypress.env('ENVIRONMENT') and Cypress.env('BACKEND_URL')
   */
  constructor(options?: ApiRequestOptions) {
    // If options not provided, automatically detect from Cypress.env()
    if (!options) {
      const environment = (Cypress.env('ENVIRONMENT') || 'dev') as string;
      const backendUrl = Cypress.env('BACKEND_URL') as string | undefined;
      super({ environment, backendUrl });
    } else {
      // If options provided but environment/backendUrl not set, try to detect from Cypress.env()
      const environment = options.environment || (Cypress.env('ENVIRONMENT') || 'dev') as string;
      const backendUrl = options.backendUrl || (Cypress.env('BACKEND_URL') as string | undefined);
      super({ environment, backendUrl });
    }
  }
  /**
   * Get count for a specific entity type using cy.request()
   * 
   * @param entityType - The type of entity to get count for
   * @returns Promise resolving to the entity count
   */
  async getEntityCount(entityType: 'applications' | 'companies' | 'contacts' | 'clients' | 'notes'): Promise<number> {
    const endpoint = this.getEntityEndpoint(entityType);
    
    return new Promise((resolve) => {
      cy.request({
        method: 'GET',
        url: `${this.getBackendUrl()}${endpoint}`,
        failOnStatusCode: false,
      }).then((response) => {
        if (response.status === 200 && response.body) {
          resolve(response.body.total || 0);
        } else {
          resolve(0);
        }
      });
    });
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
  verifyEntityCount(entityType: 'applications' | 'companies' | 'contacts' | 'clients' | 'notes', expectedCount: number): void {
    this.getEntityCount(entityType).then((actualCount) => {
      expect(actualCount).to.equal(expectedCount);
    });
  }

  /**
   * Verify all entity counts match expected values
   * 
   * @param expectedCounts - The expected counts for all entities
   */
  verifyAllEntityCounts(expectedCounts: EntityCounts): void {
    this.verifyEntityCount('applications', expectedCounts.applications);
    this.verifyEntityCount('companies', expectedCounts.companies);
    this.verifyEntityCount('contacts', expectedCounts.contacts);
    this.verifyEntityCount('clients', expectedCounts.clients);
    this.verifyEntityCount('notes', expectedCounts.notes);
  }

  /**
   * Get job search sites from API using cy.request()
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
    
    return new Promise((resolve, reject) => {
      cy.request({
        method: 'GET',
        url,
        failOnStatusCode: false,
      }).then((response) => {
        if (response.status === 200 && response.body) {
          resolve(response.body);
        } else {
          reject(new Error(`Failed to get job search sites: ${response.status} ${response.statusText}`));
        }
      });
    });
  }
}
