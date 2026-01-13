/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for Application Create/Edit Form
 * Note: The application form is a multi-step wizard, so this is a simplified version
 * that handles basic form interactions. For full wizard support, extend this class.
 */
export class ApplicationFormPage extends BasePage {
  // Selectors - use data-qa attributes where available
  private readonly title = '[data-qa="wizard-step2-title"], h1.h3, h1.h4, h1.h2, h1';
  private readonly positionInput = '[data-qa="application-position"]';
  private readonly statusSelect = '[data-qa="application-status"]';
  private readonly workSettingSelect = '[data-qa="application-work-setting"]';
  private readonly locationInput = '[data-qa="application-location"]';
  private readonly jobLinkInput = '[data-qa="application-job-link"]';
  private readonly submitButton = '[data-qa="wizard-step2-submit-button"], button[type="submit"]:has-text("Update"), button[type="submit"]:has-text("Create Application"), button[type="submit"]:has-text("Update Application")';
  private readonly cancelButton = '[data-qa="wizard-step2-back-button"], [data-qa^="application-edit-"][data-qa$="-cancel-button"], button:has-text("Cancel"), a:has-text("Cancel")';

  /**
   * Get position input for edit form
   * @param applicationId - Application ID
   */
  getPositionInput(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-edit-${applicationId}-position-input"]`);
  }

  /**
   * Get status select for edit form
   * @param applicationId - Application ID
   */
  getStatusSelect(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-edit-${applicationId}-status-select"]`);
  }

  /**
   * Get work setting select for edit form
   * @param applicationId - Application ID
   */
  getWorkSettingSelect(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-edit-${applicationId}-work-setting-select"]`);
  }

  /**
   * Get location input for edit form
   * @param applicationId - Application ID
   */
  getLocationInput(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-edit-${applicationId}-location-input"]`);
  }

  /**
   * Get job link input for edit form
   * @param applicationId - Application ID
   */
  getJobLinkInput(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-edit-${applicationId}-job-link-input"]`);
  }

  /**
   * Get submit button for edit form
   * @param applicationId - Application ID
   */
  getSubmitButton(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-edit-${applicationId}-submit-button"]`);
  }

  /**
   * Get cancel button for edit form
   * @param applicationId - Application ID
   */
  getCancelButton(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-edit-${applicationId}-cancel-button"], [data-qa="application-edit-${applicationId}-cancel-button-bottom"]`).first();
  }

  /**
   * Navigate to new application form
   */
  navigateToNew(): void {
    this.visit('/applications/new');
  }

  /**
   * Navigate to edit application form
   * @param applicationId - Application ID
   */
  navigateToEdit(applicationId: number): void {
    this.visit(`/applications/${applicationId}/edit`);
  }

  /**
   * Fill form with data (for wizard step2 - new application)
   * @param data - Form data
   */
  fillForm(data: {
    position?: string;
    status?: string;
    workSetting?: string;
    location?: string;
    jobLink?: string;
  }): void {
    if (data.position) {
      cy.get(this.positionInput).clear().type(data.position);
    }
    if (data.status) {
      cy.get(this.statusSelect).select(data.status);
    }
    if (data.workSetting) {
      cy.get(this.workSettingSelect).select(data.workSetting);
    }
    if (data.location) {
      cy.get(this.locationInput).clear().type(data.location);
    }
    if (data.jobLink) {
      cy.get(this.jobLinkInput).clear().type(data.jobLink);
    }
  }

  /**
   * Fill edit form with data
   * @param applicationId - Application ID
   * @param data - Form data
   */
  fillEditForm(applicationId: number, data: {
    position?: string;
    status?: string;
    workSetting?: string;
    location?: string;
    jobLink?: string;
  }): void {
    if (data.position) {
      this.getPositionInput(applicationId).clear().type(data.position);
    }
    if (data.status) {
      this.getStatusSelect(applicationId).select(data.status);
    }
    if (data.workSetting) {
      this.getWorkSettingSelect(applicationId).select(data.workSetting);
    }
    if (data.location) {
      this.getLocationInput(applicationId).clear().type(data.location);
    }
    if (data.jobLink) {
      this.getJobLinkInput(applicationId).clear().type(data.jobLink);
    }
  }

  /**
   * Submit the form (for wizard step2 - new application)
   */
  submit(): void {
    cy.get(this.submitButton).click();
  }

  /**
   * Submit the edit form
   * @param applicationId - Application ID
   */
  submitEdit(applicationId: number): void {
    this.getSubmitButton(applicationId).click();
  }

  /**
   * Cancel the form (for wizard step2 - new application)
   */
  cancel(): void {
    cy.get(this.cancelButton).click();
  }

  /**
   * Cancel the edit form
   * @param applicationId - Application ID
   */
  cancelEdit(applicationId: number): void {
    this.getCancelButton(applicationId).click();
  }

  /**
   * Verify form has loaded
   */
  verifyFormLoaded(): void {
    this.waitForPageLoad();
    cy.get(this.title).should('be.visible');
  }
}
