import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Job Search Sites List Page
 * Uses data-qa selectors consistent with frontend/app/job-search-sites/page.tsx
 */
export class JobSearchSitesPage extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly newSiteButton: Locator;
  readonly sitesTable: Locator;
  readonly emptyState: Locator;
  readonly paginationPreviousButton: Locator;
  readonly paginationNextButton: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector (fallback to h1.h2 if data-qa not available)
    this.title = page.locator('h1.h2:has-text("Job Search Sites")');
    // New site button
    this.newSiteButton = page.locator('[data-qa="job-search-sites-new-button"]');
    // Table (fallback to table.table if data-qa not available)
    this.sitesTable = page.locator('table.table');
    // Empty state
    this.emptyState = page.locator('text=No job search sites found');
    // Pagination
    this.paginationPreviousButton = page.locator('[data-qa="job-search-sites-pagination-previous-button"]');
    this.paginationNextButton = page.locator('[data-qa="job-search-sites-pagination-next-button"]');
  }

  /**
   * Navigate to job search sites page
   */
  async navigate(): Promise<void> {
    await super.navigate('/job-search-sites');
  }

  /**
   * Verify page has loaded
   */
  async verifyPageLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await expect(this.title).toContainText('Job Search Sites');
    await expect(this.page.locator('body')).toBeVisible();
  }

  /**
   * Click New Site button
   */
  async clickNewSite(): Promise<void> {
    await this.newSiteButton.click();
  }

  /**
   * Verify sites table is visible
   */
  async verifyTableVisible(): Promise<void> {
    await expect(this.sitesTable).toBeVisible();
  }

  /**
   * Verify empty state is visible
   */
  async verifyEmptyState(): Promise<void> {
    await expect(this.emptyState).toBeVisible();
  }
}
