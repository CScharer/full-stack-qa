/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for Company Create Form
 * Uses data-qa selectors consistent with frontend/app/companies/new/page.tsx
 */
export class CompanyFormPage extends BasePage {
  // Selectors - use data-qa attributes where available
  private readonly title = '[data-qa="company-create-title"]';
  private readonly nameInput = '[data-qa="company-create-name"]';
  private readonly addressInput = '[data-qa="company-create-address"]';
  private readonly cityInput = '[data-qa="company-create-city"]';
  private readonly stateInput = '[data-qa="company-create-state"]';
  private readonly zipInput = '[data-qa="company-create-zip"]';
  private readonly countrySelect = '[data-qa="company-create-country"]';
  private readonly jobTypeSelect = '[data-qa="company-create-job-type"]';
  private readonly submitButton = '[data-qa="company-create-submit-button"]';
  private readonly cancelButton = '[data-qa="company-create-cancel-button"]';

  /**
   * Navigate to new company form
   */
  navigate(): void {
    this.visit('/companies/new');
  }

  /**
   * Fill form with data
   * @param data - Form data
   */
  fillForm(data: {
    name?: string;
    address?: string;
    city?: string;
    state?: string;
    zip?: string;
    country?: string;
    job_type?: string;
  }): void {
    if (data.name) {
      cy.get(this.nameInput).clear().type(data.name);
    }
    if (data.address) {
      cy.get(this.addressInput).clear().type(data.address);
    }
    if (data.city) {
      cy.get(this.cityInput).clear().type(data.city);
    }
    if (data.state) {
      cy.get(this.stateInput).clear().type(data.state);
    }
    if (data.zip) {
      cy.get(this.zipInput).clear().type(data.zip);
    }
    if (data.country) {
      cy.get(this.countrySelect).select(data.country);
    }
    if (data.job_type) {
      cy.get(this.jobTypeSelect).select(data.job_type);
    }
  }

  /**
   * Submit the form
   */
  submit(): void {
    cy.get(this.submitButton).click();
  }

  /**
   * Cancel the form
   */
  cancel(): void {
    cy.get(this.cancelButton).click();
  }

  /**
   * Verify form has loaded
   */
  verifyFormLoaded(): void {
    this.waitForPageLoad();
    cy.get(this.title).should('be.visible');
  }
}
