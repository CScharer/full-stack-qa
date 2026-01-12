import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Contacts List Page
 * Uses data-qa selectors consistent with frontend/app/contacts/page.tsx
 */
export class ContactsPage extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly newContactButton: Locator;
  readonly filtersCard: Locator;
  readonly companyIdFilter: Locator;
  readonly applicationIdFilter: Locator;
  readonly clientIdFilter: Locator;
  readonly contactTypeFilter: Locator;
  readonly contactsTable: Locator;
  readonly contactsTableBody: Locator;
  readonly emptyState: Locator;
  readonly paginationPreviousButton: Locator;
  readonly paginationNextButton: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector (data-qa from frontend/app/contacts/page.tsx)
    this.title = page.locator('[data-qa="contacts-title"]');
    // New contact button
    this.newContactButton = page.locator('[data-qa="contacts-new-button"]');
    // Filters
    this.filtersCard = page.locator('[data-qa="contacts-filters"]');
    this.companyIdFilter = page.locator('[data-qa="contacts-filter-company-id"]');
    this.applicationIdFilter = page.locator('[data-qa="contacts-filter-application-id"]');
    this.clientIdFilter = page.locator('[data-qa="contacts-filter-client-id"]');
    this.contactTypeFilter = page.locator('[data-qa="contacts-filter-contact-type"]');
    // Table
    this.contactsTable = page.locator('[data-qa="contacts-table"]');
    this.contactsTableBody = page.locator('[data-qa="contacts-table-body"]');
    // Empty state - uses data-qa attribute
    this.emptyState = page.locator('[data-qa="contacts-empty-state"]');
    // Pagination
    this.paginationPreviousButton = page.locator('[data-qa="contacts-pagination-previous-button"]');
    this.paginationNextButton = page.locator('[data-qa="contacts-pagination-next-button"]');
  }

  /**
   * Navigate to contacts page
   */
  async navigate(): Promise<void> {
    await super.navigate('/contacts');
  }

  /**
   * Verify page has loaded
   */
  async verifyPageLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await expect(this.title).toContainText('Contacts');
    await expect(this.page.locator('body')).toBeVisible();
  }

  /**
   * Click New Contact button
   */
  async clickNewContact(): Promise<void> {
    await this.newContactButton.click();
  }

  /**
   * Get contact row by ID
   * @param contactId - Contact ID
   */
  getContactRow(contactId: number): Locator {
    return this.page.locator(`[data-qa="contact-row-${contactId}"]`);
  }

  /**
   * Click contact name link
   * @param contactId - Contact ID
   */
  async clickContact(contactId: number): Promise<void> {
    const link = this.page.locator(`[data-qa="contact-name-link-${contactId}"]`);
    await link.click();
  }

  /**
   * Click edit button for a contact
   * @param contactId - Contact ID
   */
  async clickEdit(contactId: number): Promise<void> {
    const editButton = this.page.locator(`[data-qa="contact-edit-button-${contactId}"]`);
    await editButton.click();
  }

  /**
   * Click delete button for a contact
   * @param contactId - Contact ID
   */
  async clickDelete(contactId: number): Promise<void> {
    const deleteButton = this.page.locator(`[data-qa="contact-delete-button-${contactId}"]`);
    await deleteButton.click();
  }

  /**
   * Check if contact exists in the table
   * @param contactId - Contact ID
   */
  async hasContact(contactId: number): Promise<boolean> {
    const row = this.getContactRow(contactId);
    return await row.count() > 0;
  }

  /**
   * Filter by company ID
   * @param companyId - Company ID to filter by
   */
  async filterByCompanyId(companyId: number): Promise<void> {
    await this.companyIdFilter.fill(companyId.toString());
  }

  /**
   * Filter by application ID
   * @param applicationId - Application ID to filter by
   */
  async filterByApplicationId(applicationId: number): Promise<void> {
    await this.applicationIdFilter.fill(applicationId.toString());
  }

  /**
   * Filter by client ID
   * @param clientId - Client ID to filter by
   */
  async filterByClientId(clientId: number): Promise<void> {
    await this.clientIdFilter.fill(clientId.toString());
  }

  /**
   * Filter by contact type
   * @param contactType - Contact type to filter by
   */
  async filterByContactType(contactType: string): Promise<void> {
    await this.contactTypeFilter.fill(contactType);
  }

  /**
   * Verify contacts table is visible
   */
  async verifyTableVisible(): Promise<void> {
    await expect(this.contactsTable).toBeVisible();
  }

  /**
   * Verify empty state is visible
   */
  async verifyEmptyState(): Promise<void> {
    await expect(this.emptyState).toBeVisible();
  }
}
