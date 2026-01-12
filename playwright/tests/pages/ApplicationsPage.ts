import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Applications List Page
 * Uses data-qa selectors consistent with frontend/app/applications/page.tsx
 */
export class ApplicationsPage extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly newApplicationButton: Locator;
  readonly filtersCard: Locator;
  readonly statusFilter: Locator;
  readonly companyIdFilter: Locator;
  readonly clientIdFilter: Locator;
  readonly applicationsTable: Locator;
  readonly applicationsTableBody: Locator;
  readonly applicationsListCard: Locator;
  readonly emptyState: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector (data-qa from frontend/app/applications/page.tsx)
    this.title = page.locator('[data-qa="applications-title"]');
    // New application button
    this.newApplicationButton = page.locator('[data-qa="applications-new-button"]');
    // Filters
    this.filtersCard = page.locator('[data-qa="applications-filters"]');
    this.statusFilter = page.locator('[data-qa="applications-filter-status"]');
    this.companyIdFilter = page.locator('[data-qa="applications-filter-company-id"]');
    this.clientIdFilter = page.locator('[data-qa="applications-filter-client-id"]');
    // Table
    this.applicationsTable = page.locator('[data-qa="applications-table"]');
    this.applicationsTableBody = page.locator('[data-qa="applications-table-body"]');
    this.applicationsListCard = page.locator('[data-qa="applications-list-card"]');
    // Empty state - uses data-qa attribute
    this.emptyState = page.locator('[data-qa="applications-empty-state"]');
  }

  /**
   * Navigate to applications page
   */
  async navigate(): Promise<void> {
    await super.navigate('/applications');
  }

  /**
   * Verify page has loaded
   */
  async verifyPageLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await expect(this.title).toContainText('Applications');
    await expect(this.page.locator('body')).toBeVisible();
  }

  /**
   * Click New Application button
   */
  async clickNewApplication(): Promise<void> {
    await this.newApplicationButton.click();
  }

  /**
   * Get application row by ID
   * @param applicationId - Application ID
   */
  getApplicationRow(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-row-${applicationId}"]`);
  }

  /**
   * Get application row by position text
   * @param position - Position text
   */
  async getApplicationRowByPosition(position: string): Promise<Locator> {
    return this.applicationsTableBody.locator(`tr:has-text("${position}")`);
  }

  /**
   * Click application position link
   * @param applicationId - Application ID
   */
  async clickApplication(applicationId: number): Promise<void> {
    const link = this.page.locator(`[data-qa="application-position-link-${applicationId}"]`);
    await link.click();
  }

  /**
   * Click edit button for an application
   * @param applicationId - Application ID
   */
  async clickEdit(applicationId: number): Promise<void> {
    const editButton = this.page.locator(`[data-qa="application-edit-button-${applicationId}"]`);
    await editButton.click();
  }

  /**
   * Click delete button for an application
   * @param applicationId - Application ID
   */
  async clickDelete(applicationId: number): Promise<void> {
    const deleteButton = this.page.locator(`[data-qa="application-delete-button-${applicationId}"]`);
    await deleteButton.click();
  }

  /**
   * Check if application exists in the table
   * @param applicationId - Application ID
   */
  async hasApplication(applicationId: number): Promise<boolean> {
    const row = this.getApplicationRow(applicationId);
    return await row.count() > 0;
  }

  /**
   * Filter by status
   * @param status - Status to filter by
   */
  async filterByStatus(status: string): Promise<void> {
    await this.statusFilter.fill(status);
  }

  /**
   * Filter by company ID
   * @param companyId - Company ID to filter by
   */
  async filterByCompanyId(companyId: number): Promise<void> {
    await this.companyIdFilter.fill(companyId.toString());
  }

  /**
   * Filter by client ID
   * @param clientId - Client ID to filter by
   */
  async filterByClientId(clientId: number): Promise<void> {
    await this.clientIdFilter.fill(clientId.toString());
  }

  /**
   * Verify applications table is visible
   */
  async verifyTableVisible(): Promise<void> {
    await expect(this.applicationsTable).toBeVisible();
  }

  /**
   * Verify empty state is visible
   */
  async verifyEmptyState(): Promise<void> {
    await expect(this.emptyState).toBeVisible();
  }

  /**
   * Get application status badge
   * @param applicationId - Application ID
   */
  getApplicationStatus(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-status-${applicationId}"]`);
  }
}
