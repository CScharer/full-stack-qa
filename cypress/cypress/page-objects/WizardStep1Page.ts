/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for Application Wizard Step 1 (Contact Selection)
 * Uses data-qa selectors consistent with frontend/app/applications/new/step1/page.tsx
 */
export class WizardStep1Page extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly titleSelector = '[data-qa="wizard-step1-title"]';
  readonly backLinkSelector = '[data-qa="wizard-step1-back-link"]';
  readonly contactSelectSelector = '[data-qa="entity-select-contact"]';
  readonly contactSelectedSelector = '[data-qa="wizard-step1-contact-selected"]';
  readonly nextButtonSelector = '[data-qa="wizard-step1-next-button"]';
  readonly cancelButtonSelector = '[data-qa="wizard-step1-cancel-button"]';

  /**
   * Navigate to wizard step 1
   */
  navigate(): void {
    this.visit('/applications/new/step1');
  }

  /**
   * Select a contact by clicking the first available option
   */
  selectFirstContact(): void {
    // Click on the EntitySelect input to open the dropdown
    cy.get(this.contactSelectSelector).find('input').first().click();
    cy.wait(500);

    // Wait for dropdown options to appear and click the first one
    cy.get('[data-qa^="entity-select-contact-option-"]').first().should('be.visible').click();
    cy.wait(500);

    // Verify contact is selected
    cy.get(this.contactSelectedSelector).should('be.visible', { timeout: 5000 });
  }

  /**
   * Click Next button
   */
  clickNext(): void {
    cy.get(this.nextButtonSelector).click();
  }

  /**
   * Click Cancel button
   */
  cancel(): void {
    // Wait for button to be visible and actionable
    cy.get(this.cancelButtonSelector, { timeout: 10000 }).should('be.visible').should('be.enabled');
    // Click and wait for navigation
    cy.get(this.cancelButtonSelector).click();
    // Wait for navigation to complete - use more specific URL pattern to avoid matching /applications/new/step1
    // Match /applications but not /applications/new or /applications/[id]
    cy.url({ timeout: 20000 }).should('match', /\/applications$|\/applications\?/);
    this.waitForPageLoad();
  }

  /**
   * Verify page has loaded
   */
  verifyPageLoaded(): void {
    this.waitForPageLoad();
    cy.get(this.titleSelector).should('be.visible');
    cy.get(this.titleSelector).should('contain.text', 'Step 1: Contact');
  }
}
