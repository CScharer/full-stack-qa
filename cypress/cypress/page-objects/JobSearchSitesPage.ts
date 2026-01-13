/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Job Search Sites List Page
 * Uses data-qa selectors consistent with frontend/app/job-search-sites/page.tsx
 */
export class JobSearchSitesPage extends BasePage {
  // Selectors - use data-qa attributes where available
  private readonly title = '[data-qa="job-search-sites-title"]';
  private readonly newSiteButton = '[data-qa="job-search-sites-new-button"]';
  private readonly sitesTable = '[data-qa="job-search-sites-table"]';
  private readonly emptyState = '[data-qa="job-search-sites-empty-state"]';
  private readonly paginationPreviousButton = '[data-qa="job-search-sites-pagination-previous-button"]';
  private readonly paginationNextButton = '[data-qa="job-search-sites-pagination-next-button"]';

  /**
   * Navigate to job search sites page
   */
  navigate(): void {
    this.visit('/job-search-sites');
  }

  /**
   * Verify page has loaded
   */
  verifyPageLoaded(): void {
    this.waitForPageLoad();
    cy.get(this.title).should('contain', 'Job Search Sites');
    cy.get('body').should('be.visible');
  }

  /**
   * Click New Site button
   */
  clickNewSite(): void {
    cy.get(this.newSiteButton).click();
  }

  /**
   * Verify sites table is visible
   */
  verifyTableVisible(): void {
    cy.get(this.sitesTable).should('be.visible');
  }

  /**
   * Verify empty state is visible
   */
  verifyEmptyState(): void {
    cy.get(this.emptyState).should('be.visible');
  }
}
