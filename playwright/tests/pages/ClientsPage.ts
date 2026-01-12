import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Clients List Page
 * Uses data-qa selectors consistent with frontend/app/clients/page.tsx
 */
export class ClientsPage extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly newClientButton: Locator;
  readonly filtersCard: Locator;
  readonly clientsTable: Locator;
  readonly emptyState: Locator;
  readonly paginationPreviousButton: Locator;
  readonly paginationNextButton: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector (fallback to h1.h2 if data-qa not available)
    this.title = page.locator('h1.h2:has-text("Clients")');
    // New client button
    this.newClientButton = page.locator('[data-qa="clients-new-button"]');
    // Filters
    this.filtersCard = page.locator('[data-qa="clients-filters"]');
    // Table (fallback to table.table if data-qa not available)
    this.clientsTable = page.locator('table.table');
    // Empty state
    this.emptyState = page.locator('text=No clients found');
    // Pagination
    this.paginationPreviousButton = page.locator('[data-qa="clients-pagination-previous-button"]');
    this.paginationNextButton = page.locator('[data-qa="clients-pagination-next-button"]');
  }

  /**
   * Navigate to clients page
   */
  async navigate(): Promise<void> {
    await super.navigate('/clients');
  }

  /**
   * Verify page has loaded
   */
  async verifyPageLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await expect(this.title).toContainText('Clients');
    await expect(this.page.locator('body')).toBeVisible();
  }

  /**
   * Click New Client button
   */
  async clickNewClient(): Promise<void> {
    await this.newClientButton.click();
  }

  /**
   * Get client row by name
   * @param clientName - Client name
   */
  async getClientRow(clientName: string): Promise<Locator> {
    return this.clientsTable.locator(`tbody tr:has-text("${clientName}")`);
  }

  /**
   * Click client name link
   * @param clientName - Client name
   */
  async clickClient(clientName: string): Promise<void> {
    const row = await this.getClientRow(clientName);
    await row.locator('a').first().click();
  }

  /**
   * Click edit button for a client
   * @param clientName - Client name
   */
  async clickEdit(clientName: string): Promise<void> {
    const row = await this.getClientRow(clientName);
    await row.locator('a:has-text("Edit")').click();
  }

  /**
   * Click delete button for a client
   * @param clientName - Client name
   */
  async clickDelete(clientName: string): Promise<void> {
    const row = await this.getClientRow(clientName);
    await row.locator('button:has-text("Delete")').click();
  }

  /**
   * Check if client exists in the table
   * @param clientName - Client name
   */
  async hasClient(clientName: string): Promise<boolean> {
    const row = await this.getClientRow(clientName);
    return await row.count() > 0;
  }

  /**
   * Verify clients table is visible
   */
  async verifyTableVisible(): Promise<void> {
    await expect(this.clientsTable).toBeVisible();
  }

  /**
   * Verify empty state is visible
   */
  async verifyEmptyState(): Promise<void> {
    await expect(this.emptyState).toBeVisible();
  }
}
