/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Contacts List Page
 * Uses data-qa selectors consistent with frontend/app/contacts/page.tsx
 */
export class ContactsPage extends BasePage {
  // Selectors - use data-qa attributes where available
  private readonly title = '[data-qa="contacts-title"]';
  private readonly newContactButton = '[data-qa="contacts-new-button"]';
  private readonly filtersCard = '[data-qa="contacts-filters"]';
  private readonly companyIdFilter = '[data-qa="contacts-filter-company-id"]';
  private readonly applicationIdFilter = '[data-qa="contacts-filter-application-id"]';
  private readonly clientIdFilter = '[data-qa="contacts-filter-client-id"]';
  private readonly contactTypeFilter = '[data-qa="contacts-filter-contact-type"]';
  private readonly contactsTable = '[data-qa="contacts-table"]';
  private readonly contactsTableBody = '[data-qa="contacts-table-body"]';
  private readonly emptyState = '[data-qa="contacts-empty-state"]';
  private readonly paginationPreviousButton = '[data-qa="contacts-pagination-previous-button"]';
  private readonly paginationNextButton = '[data-qa="contacts-pagination-next-button"]';

  /**
   * Navigate to contacts page
   */
  navigate(): void {
    this.visit('/contacts');
  }

  /**
   * Verify page has loaded
   */
  verifyPageLoaded(): void {
    this.waitForPageLoad();
    cy.get(this.title).should('contain', 'Contacts');
    cy.get('body').should('be.visible');
  }

  /**
   * Click New Contact button
   */
  clickNewContact(): void {
    cy.get(this.newContactButton).click();
  }

  /**
   * Get contact row by ID
   * @param contactId - Contact ID
   */
  getContactRow(contactId: number): Cypress.Chainable {
    return cy.get(`[data-qa="contact-row-${contactId}"]`);
  }

  /**
   * Click contact name link
   * @param contactId - Contact ID
   */
  clickContact(contactId: number): void {
    cy.get(`[data-qa="contact-name-link-${contactId}"]`).click();
  }

  /**
   * Click edit button for a contact
   * @param contactId - Contact ID
   */
  clickEdit(contactId: number): void {
    cy.get(`[data-qa="contact-edit-button-${contactId}"]`).click();
  }

  /**
   * Click delete button for a contact
   * @param contactId - Contact ID
   */
  clickDelete(contactId: number): void {
    cy.get(`[data-qa="contact-delete-button-${contactId}"]`).click();
  }

  /**
   * Check if contact exists in the table
   * @param contactId - Contact ID
   */
  hasContact(contactId: number): Cypress.Chainable<boolean> {
    return this.getContactRow(contactId).then(($el) => {
      return $el.length > 0;
    });
  }

  /**
   * Filter by company ID
   * @param companyId - Company ID to filter by
   */
  filterByCompanyId(companyId: number): void {
    cy.get(this.companyIdFilter).clear().type(companyId.toString());
  }

  /**
   * Filter by application ID
   * @param applicationId - Application ID to filter by
   */
  filterByApplicationId(applicationId: number): void {
    cy.get(this.applicationIdFilter).clear().type(applicationId.toString());
  }

  /**
   * Filter by client ID
   * @param clientId - Client ID to filter by
   */
  filterByClientId(clientId: number): void {
    cy.get(this.clientIdFilter).clear().type(clientId.toString());
  }

  /**
   * Filter by contact type
   * @param contactType - Contact type to filter by
   */
  filterByContactType(contactType: string): void {
    cy.get(this.contactTypeFilter).clear().type(contactType);
  }

  /**
   * Verify contacts table is visible
   */
  verifyTableVisible(): void {
    cy.get(this.contactsTable).should('be.visible');
  }

  /**
   * Verify empty state is visible
   */
  verifyEmptyState(): void {
    cy.get(this.emptyState).should('be.visible');
  }
}
