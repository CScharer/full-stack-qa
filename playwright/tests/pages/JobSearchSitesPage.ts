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
    // Title selector - uses data-qa attribute
    this.title = page.locator('[data-qa="job-search-sites-title"]');
    // New site button
    this.newSiteButton = page.locator('[data-qa="job-search-sites-new-button"]');
    // Table - uses data-qa attribute
    this.sitesTable = page.locator('[data-qa="job-search-sites-table"]');
    // Empty state - uses data-qa attribute
    this.emptyState = page.locator('[data-qa="job-search-sites-empty-state"]');
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
