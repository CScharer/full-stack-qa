/// <reference types="cypress" />

/**
 * Base Page Object class
 * Contains common methods and properties shared across all pages
 */
export class BasePage {
  /**
   * Visit the page
   * @param path - Relative path from base URL (e.g., '/', '/applications')
   */
  visit(path: string = '/'): void {
    cy.visit(path);
    this.waitForPageLoad();
  }

  /**
   * Wait for page to load
   * Verifies body is visible as a basic page load check
   */
  waitForPageLoad(): void {
    cy.get('body').should('be.visible');
  }

  /**
   * Get page title
   */
  getTitle(): Cypress.Chainable<string> {
    return cy.title();
  }

  /**
   * Verify page title contains text
   * @param text - Text to verify in title
   */
  verifyTitleContains(text: string): void {
    cy.title().should('contain', text);
  }

  /**
   * Set viewport size
   * @param width - Viewport width
   * @param height - Viewport height
   */
  setViewport(width: number = 1920, height: number = 1080): void {
    cy.viewport(width, height);
  }
}
