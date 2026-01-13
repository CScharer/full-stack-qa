/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for Client Create Form
 * Uses data-qa selectors consistent with frontend/app/clients/new/page.tsx
 */
export class ClientFormPage extends BasePage {
  // Selectors - use data-qa attributes where available
  private readonly title = '[data-qa="client-create-title"]';
  private readonly nameInput = '[data-qa="client-create-name"]';
  private readonly submitButton = '[data-qa="client-create-submit-button"]';
  private readonly cancelButton = '[data-qa="client-create-cancel-button"], [data-qa="client-create-cancel-button-bottom"]';

  /**
   * Navigate to new client form
   */
  navigate(): void {
    this.visit('/clients/new');
  }

  /**
   * Fill form with data
   * @param data - Form data
   */
  fillForm(data: {
    name?: string;
  }): void {
    if (data.name) {
      cy.get(this.nameInput).clear().type(data.name);
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
