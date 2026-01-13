/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Clients List Page
 * Uses data-qa selectors consistent with frontend/app/clients/page.tsx
 */
export class ClientsPage extends BasePage {
  // Selectors - use data-qa attributes where available
  private readonly title = '[data-qa="clients-title"]';
  private readonly newClientButton = '[data-qa="clients-new-button"]';
  private readonly filtersCard = '[data-qa="clients-filters"]';
  private readonly clientsTable = '[data-qa="clients-table"]';
  private readonly emptyState = '[data-qa="clients-empty-state"]';
  private readonly paginationPreviousButton = '[data-qa="clients-pagination-previous-button"]';
  private readonly paginationNextButton = '[data-qa="clients-pagination-next-button"]';

  /**
   * Navigate to clients page
   */
  navigate(): void {
    this.visit('/clients');
  }

  /**
   * Verify page has loaded
   */
  verifyPageLoaded(): void {
    this.waitForPageLoad();
    cy.get(this.title).should('contain', 'Clients');
    cy.get('body').should('be.visible');
  }

  /**
   * Click New Client button
   */
  clickNewClient(): void {
    cy.get(this.newClientButton).click();
  }

  /**
   * Get client row by ID (preferred method using data-qa)
   * @param clientId - Client ID
   */
  getClientRow(clientId: number): Cypress.Chainable {
    return cy.get(`[data-qa="client-row-${clientId}"]`);
  }

  /**
   * Get client row by name (fallback method)
   * @param clientName - Client name
   */
  getClientRowByName(clientName: string): Cypress.Chainable {
    return cy.get(this.clientsTable).contains('tbody tr', clientName);
  }

  /**
   * Click client name link by ID (preferred method using data-qa)
   * @param clientId - Client ID
   */
  clickClient(clientId: number): void {
    cy.get(`[data-qa="client-name-link-${clientId}"]`).click();
  }

  /**
   * Click client name link by name (fallback method)
   * @param clientName - Client name
   */
  clickClientByName(clientName: string): void {
    this.getClientRowByName(clientName).find('a').first().click();
  }

  /**
   * Click edit button for a client by ID (preferred method using data-qa)
   * @param clientId - Client ID
   */
  clickEdit(clientId: number): void {
    cy.get(`[data-qa="client-edit-button-${clientId}"]`).click();
  }

  /**
   * Click edit button for a client by name (fallback method)
   * @param clientName - Client name
   */
  clickEditByName(clientName: string): void {
    this.getClientRowByName(clientName).find('a').contains('Edit').click();
  }

  /**
   * Click delete button for a client by ID (preferred method using data-qa)
   * @param clientId - Client ID
   */
  clickDelete(clientId: number): void {
    cy.get(`[data-qa="client-delete-button-${clientId}"]`).click();
  }

  /**
   * Click delete button for a client by name (fallback method)
   * @param clientName - Client name
   */
  clickDeleteByName(clientName: string): void {
    this.getClientRowByName(clientName).find('button').contains('Delete').click();
  }

  /**
   * Check if client exists in the table by ID (preferred method using data-qa)
   * @param clientId - Client ID
   */
  hasClient(clientId: number): Cypress.Chainable<boolean> {
    return this.getClientRow(clientId).then(($el) => {
      return $el.length > 0;
    });
  }

  /**
   * Check if client exists in the table by name (fallback method)
   * @param clientName - Client name
   */
  hasClientByName(clientName: string): Cypress.Chainable<boolean> {
    return this.getClientRowByName(clientName).then(($el) => {
      return $el.length > 0;
    });
  }

  /**
   * Verify clients table is visible
   */
  verifyTableVisible(): void {
    cy.get(this.clientsTable).should('be.visible');
  }

  /**
   * Verify empty state is visible
   */
  verifyEmptyState(): void {
    cy.get(this.emptyState).should('be.visible');
  }
}
