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
    // Title selector - uses data-qa attribute
    this.title = page.locator('[data-qa="clients-title"]');
    // New client button
    this.newClientButton = page.locator('[data-qa="clients-new-button"]');
    // Filters
    this.filtersCard = page.locator('[data-qa="clients-filters"]');
    // Table - uses data-qa attribute
    this.clientsTable = page.locator('[data-qa="clients-table"]');
    // Empty state - uses data-qa attribute
    this.emptyState = page.locator('[data-qa="clients-empty-state"]');
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
   * Get client row by ID (preferred method using data-qa)
   * @param clientId - Client ID
   */
  getClientRow(clientId: number): Locator {
    return this.page.locator(`[data-qa="client-row-${clientId}"]`);
  }

  /**
   * Get client row by name (fallback method)
   * @param clientName - Client name
   */
  async getClientRowByName(clientName: string): Promise<Locator> {
    return this.clientsTable.locator(`tbody tr:has-text("${clientName}")`);
  }

  /**
   * Click client name link by ID (preferred method using data-qa)
   * @param clientId - Client ID
   */
  async clickClient(clientId: number): Promise<void> {
    const link = this.page.locator(`[data-qa="client-name-link-${clientId}"]`);
    await link.click();
  }

  /**
   * Click client name link by name (fallback method)
   * @param clientName - Client name
   */
  async clickClientByName(clientName: string): Promise<void> {
    const row = await this.getClientRowByName(clientName);
    await row.locator('a').first().click();
  }

  /**
   * Click edit button for a client by ID (preferred method using data-qa)
   * @param clientId - Client ID
   */
  async clickEdit(clientId: number): Promise<void> {
    const editButton = this.page.locator(`[data-qa="client-edit-button-${clientId}"]`);
    await editButton.click();
  }

  /**
   * Click edit button for a client by name (fallback method)
   * @param clientName - Client name
   */
  async clickEditByName(clientName: string): Promise<void> {
    const row = await this.getClientRowByName(clientName);
    await row.locator('a:has-text("Edit")').click();
  }

  /**
   * Click delete button for a client by ID (preferred method using data-qa)
   * @param clientId - Client ID
   */
  async clickDelete(clientId: number): Promise<void> {
    const deleteButton = this.page.locator(`[data-qa="client-delete-button-${clientId}"]`);
    await deleteButton.click();
  }

  /**
   * Click delete button for a client by name (fallback method)
   * @param clientName - Client name
   */
  async clickDeleteByName(clientName: string): Promise<void> {
    const row = await this.getClientRowByName(clientName);
    await row.locator('button:has-text("Delete")').click();
  }

  /**
   * Check if client exists in the table by ID (preferred method using data-qa)
   * @param clientId - Client ID
   */
  async hasClient(clientId: number): Promise<boolean> {
    const row = this.getClientRow(clientId);
    return await row.count() > 0;
  }

  /**
   * Check if client exists in the table by name (fallback method)
   * @param clientName - Client name
   */
  async hasClientByName(clientName: string): Promise<boolean> {
    const row = await this.getClientRowByName(clientName);
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
