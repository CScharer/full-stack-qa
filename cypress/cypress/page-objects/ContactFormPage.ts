/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for Contact Create Form
 * Uses data-qa selectors consistent with frontend/app/contacts/new/page.tsx
 */
export class ContactFormPage extends BasePage {
  // Selectors - use data-qa attributes where available
  private readonly title = 'h1[data-qa="contact-create-title"]';
  private readonly firstNameInput = '[data-qa="contact-first-name"]';
  private readonly lastNameInput = '[data-qa="contact-last-name"]';
  private readonly titleInput = '[data-qa="contact-create-title-input"]';
  private readonly linkedinInput = '[data-qa="contact-create-linkedin"]';
  private readonly contactTypeSelect = '[data-qa="contact-create-type"]';
  private readonly companyIdInput = '[data-qa="contact-create-company-id"]';
  private readonly applicationIdInput = '[data-qa="contact-create-application-id"]';
  private readonly clientIdInput = '[data-qa="contact-create-client-id"]';
  private readonly submitButton = '[data-qa="contact-create-submit-button"]';
  private readonly cancelButton = '[data-qa="contact-create-cancel-button"], [data-qa="contact-create-cancel-button-bottom"]';

  /**
   * Navigate to new contact form
   */
  navigate(): void {
    this.visit('/contacts/new');
  }

  /**
   * Fill form with data
   * @param data - Form data
   */
  fillForm(data: {
    first_name?: string;
    last_name?: string;
    title?: string;
    linkedin?: string;
    contact_type?: string;
    company_id?: number;
    application_id?: number;
    client_id?: number;
  }): void {
    if (data.first_name) {
      cy.get(this.firstNameInput).clear().type(data.first_name);
    }
    if (data.last_name) {
      cy.get(this.lastNameInput).clear().type(data.last_name);
    }
    if (data.title) {
      cy.get(this.titleInput).clear().type(data.title);
    }
    if (data.linkedin) {
      cy.get(this.linkedinInput).clear().type(data.linkedin);
    }
    if (data.contact_type) {
      cy.get(this.contactTypeSelect).select(data.contact_type);
    }
    if (data.company_id !== undefined) {
      cy.get(this.companyIdInput).clear().type(data.company_id.toString());
    }
    if (data.application_id !== undefined) {
      cy.get(this.applicationIdInput).clear().type(data.application_id.toString());
    }
    if (data.client_id !== undefined) {
      cy.get(this.clientIdInput).clear().type(data.client_id.toString());
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
    cy.get(this.cancelButton).first().click();
  }

  /**
   * Verify form has loaded
   */
  verifyFormLoaded(): void {
    this.waitForPageLoad();
    cy.get(this.title).should('be.visible');
  }
}
