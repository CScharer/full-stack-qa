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
    // Title selector - uses data-qa attribute
    this.title = page.locator('[data-qa="companies-title"]');
    // New company button
    this.newCompanyButton = page.locator('[data-qa="companies-new-button"]');
    // Filters
    this.filtersCard = page.locator('[data-qa="companies-filters"]');
    this.jobTypeFilter = page.locator('[data-qa="companies-filter-job-type"]');
    // Table - uses data-qa attribute
    this.companiesTable = page.locator('[data-qa="companies-table"]');
    // Empty state - uses data-qa attribute
    this.emptyState = page.locator('[data-qa="companies-empty-state"]');
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
   * Get company row by ID (preferred method using data-qa)
   * @param companyId - Company ID
   */
  getCompanyRow(companyId: number): Locator {
    return this.page.locator(`[data-qa="company-row-${companyId}"]`);
  }

  /**
   * Get company row by name (fallback method)
   * @param companyName - Company name
   */
  async getCompanyRowByName(companyName: string): Promise<Locator> {
    return this.companiesTable.locator(`tbody tr:has-text("${companyName}")`);
  }

  /**
   * Click company name link by ID (preferred method using data-qa)
   * @param companyId - Company ID
   */
  async clickCompany(companyId: number): Promise<void> {
    const link = this.page.locator(`[data-qa="company-name-link-${companyId}"]`);
    await link.click();
  }

  /**
   * Click company name link by name (fallback method)
   * @param companyName - Company name
   */
  async clickCompanyByName(companyName: string): Promise<void> {
    const row = await this.getCompanyRowByName(companyName);
    await row.locator('a').first().click();
  }

  /**
   * Click edit button for a company by ID (preferred method using data-qa)
   * @param companyId - Company ID
   */
  async clickEdit(companyId: number): Promise<void> {
    const editButton = this.page.locator(`[data-qa="company-edit-button-${companyId}"]`);
    await editButton.click();
  }

  /**
   * Click edit button for a company by name (fallback method)
   * @param companyName - Company name
   */
  async clickEditByName(companyName: string): Promise<void> {
    const row = await this.getCompanyRowByName(companyName);
    await row.locator('a:has-text("Edit")').click();
  }

  /**
   * Click delete button for a company by ID (preferred method using data-qa)
   * @param companyId - Company ID
   */
  async clickDelete(companyId: number): Promise<void> {
    const deleteButton = this.page.locator(`[data-qa="company-delete-button-${companyId}"]`);
    await deleteButton.click();
  }

  /**
   * Click delete button for a company by name (fallback method)
   * @param companyName - Company name
   */
  async clickDeleteByName(companyName: string): Promise<void> {
    const row = await this.getCompanyRowByName(companyName);
    await row.locator('button:has-text("Delete")').click();
  }

  /**
   * Check if company exists in the table by ID (preferred method using data-qa)
   * @param companyId - Company ID
   */
  async hasCompany(companyId: number): Promise<boolean> {
    const row = this.getCompanyRow(companyId);
    return await row.count() > 0;
  }

  /**
   * Check if company exists in the table by name (fallback method)
   * @param companyName - Company name
   */
  async hasCompanyByName(companyName: string): Promise<boolean> {
    const row = await this.getCompanyRowByName(companyName);
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
