/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Companies List Page
 * Uses data-qa selectors consistent with frontend/app/companies/page.tsx
 */
export class CompaniesPage extends BasePage {
  // Selectors - use data-qa attributes where available
  private readonly title = '[data-qa="companies-title"]';
  private readonly newCompanyButton = '[data-qa="companies-new-button"]';
  private readonly filtersCard = '[data-qa="companies-filters"]';
  private readonly jobTypeFilter = '[data-qa="companies-filter-job-type"]';
  private readonly companiesTable = '[data-qa="companies-table"]';
  private readonly emptyState = '[data-qa="companies-empty-state"]';
  private readonly paginationPreviousButton = '[data-qa="companies-pagination-previous-button"]';
  private readonly paginationNextButton = '[data-qa="companies-pagination-next-button"]';

  /**
   * Navigate to companies page
   */
  navigate(): void {
    this.visit('/companies');
  }

  /**
   * Verify page has loaded
   */
  verifyPageLoaded(): void {
    this.waitForPageLoad();
    cy.get(this.title).should('contain', 'Companies');
    cy.get('body').should('be.visible');
  }

  /**
   * Click New Company button
   */
  clickNewCompany(): void {
    cy.get(this.newCompanyButton).click();
  }

  /**
   * Get company row by ID (preferred method using data-qa)
   * @param companyId - Company ID
   */
  getCompanyRow(companyId: number): Cypress.Chainable {
    return cy.get(`[data-qa="company-row-${companyId}"]`);
  }

  /**
   * Get company row by name (fallback method)
   * @param companyName - Company name
   */
  getCompanyRowByName(companyName: string): Cypress.Chainable {
    return cy.get(this.companiesTable).contains('tbody tr', companyName);
  }

  /**
   * Click company name link by ID (preferred method using data-qa)
   * @param companyId - Company ID
   */
  clickCompany(companyId: number): void {
    cy.get(`[data-qa="company-name-link-${companyId}"]`).click();
  }

  /**
   * Click company name link by name (fallback method)
   * @param companyName - Company name
   */
  clickCompanyByName(companyName: string): void {
    this.getCompanyRowByName(companyName).find('a').first().click();
  }

  /**
   * Click edit button for a company by ID (preferred method using data-qa)
   * @param companyId - Company ID
   */
  clickEdit(companyId: number): void {
    cy.get(`[data-qa="company-edit-button-${companyId}"]`).click();
  }

  /**
   * Click edit button for a company by name (fallback method)
   * @param companyName - Company name
   */
  clickEditByName(companyName: string): void {
    this.getCompanyRowByName(companyName).find('a').contains('Edit').click();
  }

  /**
   * Click delete button for a company by ID (preferred method using data-qa)
   * @param companyId - Company ID
   */
  clickDelete(companyId: number): void {
    cy.get(`[data-qa="company-delete-button-${companyId}"]`).click();
  }

  /**
   * Click delete button for a company by name (fallback method)
   * @param companyName - Company name
   */
  clickDeleteByName(companyName: string): void {
    this.getCompanyRowByName(companyName).find('button').contains('Delete').click();
  }

  /**
   * Check if company exists in the table by ID (preferred method using data-qa)
   * @param companyId - Company ID
   */
  hasCompany(companyId: number): Cypress.Chainable<boolean> {
    return this.getCompanyRow(companyId).then(($el) => {
      return $el.length > 0;
    });
  }

  /**
   * Check if company exists in the table by name (fallback method)
   * @param companyName - Company name
   */
  hasCompanyByName(companyName: string): Cypress.Chainable<boolean> {
    return this.getCompanyRowByName(companyName).then(($el) => {
      return $el.length > 0;
    });
  }

  /**
   * Filter by job type
   * @param jobType - Job type to filter by
   */
  filterByJobType(jobType: string): void {
    cy.get(this.jobTypeFilter).clear().type(jobType);
  }

  /**
   * Verify companies table is visible
   */
  verifyTableVisible(): void {
    cy.get(this.companiesTable).should('be.visible');
  }

  /**
   * Verify empty state is visible
   */
  verifyEmptyState(): void {
    cy.get(this.emptyState).should('be.visible');
  }
}
