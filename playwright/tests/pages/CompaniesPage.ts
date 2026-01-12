import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Companies List Page
 * Uses data-qa selectors consistent with frontend/app/companies/page.tsx
 */
export class CompaniesPage extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly newCompanyButton: Locator;
  readonly filtersCard: Locator;
  readonly jobTypeFilter: Locator;
  readonly companiesTable: Locator;
  readonly emptyState: Locator;
  readonly paginationPreviousButton: Locator;
  readonly paginationNextButton: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector (fallback to h1.h2 if data-qa not available)
    this.title = page.locator('h1.h2:has-text("Companies")');
    // New company button
    this.newCompanyButton = page.locator('[data-qa="companies-new-button"]');
    // Filters
    this.filtersCard = page.locator('[data-qa="companies-filters"]');
    this.jobTypeFilter = page.locator('[data-qa="companies-filter-job-type"]');
    // Table (fallback to table.table if data-qa not available)
    this.companiesTable = page.locator('table.table');
    // Empty state
    this.emptyState = page.locator('text=No companies found');
    // Pagination
    this.paginationPreviousButton = page.locator('[data-qa="companies-pagination-previous-button"]');
    this.paginationNextButton = page.locator('[data-qa="companies-pagination-next-button"]');
  }

  /**
   * Navigate to companies page
   */
  async navigate(): Promise<void> {
    await super.navigate('/companies');
  }

  /**
   * Verify page has loaded
   */
  async verifyPageLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await expect(this.title).toContainText('Companies');
    await expect(this.page.locator('body')).toBeVisible();
  }

  /**
   * Click New Company button
   */
  async clickNewCompany(): Promise<void> {
    await this.newCompanyButton.click();
  }

  /**
   * Get company row by name
   * @param companyName - Company name
   */
  async getCompanyRow(companyName: string): Promise<Locator> {
    return this.companiesTable.locator(`tbody tr:has-text("${companyName}")`);
  }

  /**
   * Click company name link
   * @param companyName - Company name
   */
  async clickCompany(companyName: string): Promise<void> {
    const row = await this.getCompanyRow(companyName);
    await row.locator('a').first().click();
  }

  /**
   * Click edit button for a company
   * @param companyName - Company name
   */
  async clickEdit(companyName: string): Promise<void> {
    const row = await this.getCompanyRow(companyName);
    await row.locator('a:has-text("Edit")').click();
  }

  /**
   * Click delete button for a company
   * @param companyName - Company name
   */
  async clickDelete(companyName: string): Promise<void> {
    const row = await this.getCompanyRow(companyName);
    await row.locator('button:has-text("Delete")').click();
  }

  /**
   * Check if company exists in the table
   * @param companyName - Company name
   */
  async hasCompany(companyName: string): Promise<boolean> {
    const row = await this.getCompanyRow(companyName);
    return await row.count() > 0;
  }

  /**
   * Filter by job type
   * @param jobType - Job type to filter by
   */
  async filterByJobType(jobType: string): Promise<void> {
    await this.jobTypeFilter.fill(jobType);
  }

  /**
   * Verify companies table is visible
   */
  async verifyTableVisible(): Promise<void> {
    await expect(this.companiesTable).toBeVisible();
  }

  /**
   * Verify empty state is visible
   */
  async verifyEmptyState(): Promise<void> {
    await expect(this.emptyState).toBeVisible();
  }
}
