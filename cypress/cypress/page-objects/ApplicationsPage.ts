/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Applications List Page
 * Uses data-qa selectors consistent with frontend/app/applications/page.tsx
 */
export class ApplicationsPage extends BasePage {
  // Selectors - use data-qa attributes where available
  private readonly title = '[data-qa="applications-title"]';
  private readonly newApplicationButton = '[data-qa="applications-new-button"]';
  private readonly filtersCard = '[data-qa="applications-filters"]';
  private readonly statusFilter = '[data-qa="applications-filter-status"]';
  private readonly companyIdFilter = '[data-qa="applications-filter-company-id"]';
  private readonly clientIdFilter = '[data-qa="applications-filter-client-id"]';
  private readonly applicationsTable = '[data-qa="applications-table"]';
  private readonly applicationsTableBody = '[data-qa="applications-table-body"]';
  private readonly applicationsListCard = '[data-qa="applications-list-card"]';
  private readonly emptyState = '[data-qa="applications-empty-state"]';

  /**
   * Navigate to applications page
   */
  navigate(): void {
    this.visit('/applications');
  }

  /**
   * Verify page has loaded
   */
  verifyPageLoaded(): void {
    this.waitForPageLoad();
    cy.get(this.title).should('contain', 'Applications');
    cy.get('body').should('be.visible');
  }

  /**
   * Click New Application button
   */
  clickNewApplication(): void {
    cy.get(this.newApplicationButton).click();
  }

  /**
   * Get application row by ID
   * @param applicationId - Application ID
   */
  getApplicationRow(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-row-${applicationId}"]`);
  }

  /**
   * Get application row by position text
   * @param position - Position text
   */
  getApplicationRowByPosition(position: string): Cypress.Chainable {
    return cy.get(this.applicationsTableBody).contains('tr', position);
  }

  /**
   * Click application position link
   * @param applicationId - Application ID
   */
  clickApplication(applicationId: number): void {
    cy.get(`[data-qa="application-position-link-${applicationId}"]`).click();
  }

  /**
   * Click edit button for an application
   * @param applicationId - Application ID
   */
  clickEdit(applicationId: number): void {
    cy.get(`[data-qa="application-edit-button-${applicationId}"]`).click();
  }

  /**
   * Click delete button for an application
   * @param applicationId - Application ID
   */
  clickDelete(applicationId: number): void {
    cy.get(`[data-qa="application-delete-button-${applicationId}"]`).click();
  }

  /**
   * Check if application exists in the table
   * @param applicationId - Application ID
   */
  hasApplication(applicationId: number): Cypress.Chainable<boolean> {
    return this.getApplicationRow(applicationId).then(($el) => {
      return $el.length > 0;
    });
  }

  /**
   * Filter by status
   * @param status - Status to filter by
   */
  filterByStatus(status: string): void {
    cy.get(this.statusFilter).clear().type(status);
  }

  /**
   * Filter by company ID
   * @param companyId - Company ID to filter by
   */
  filterByCompanyId(companyId: number): void {
    cy.get(this.companyIdFilter).clear().type(companyId.toString());
  }

  /**
   * Filter by client ID
   * @param clientId - Client ID to filter by
   */
  filterByClientId(clientId: number): void {
    cy.get(this.clientIdFilter).clear().type(clientId.toString());
  }

  /**
   * Verify applications table is visible
   */
  verifyTableVisible(): void {
    cy.get(this.applicationsTable).should('be.visible');
  }

  /**
   * Verify empty state is visible
   */
  verifyEmptyState(): void {
    cy.get(this.emptyState).should('be.visible');
  }

  /**
   * Get application status badge
   * @param applicationId - Application ID
   */
  getApplicationStatus(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-status-${applicationId}"]`);
  }
}
