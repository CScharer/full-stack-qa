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
   * Waits for network to be idle (if possible) and verifies body is visible
   * Note: Cypress automatically waits for network requests, but we can enhance this
   */
  waitForPageLoad(): void {
    // Wait for body to be visible
    cy.get('body').should('be.visible');
    // Wait for network to be idle (Cypress waits automatically, but we can be explicit)
    cy.window().its('document.readyState').should('eq', 'complete');
  }

  /**
   * Get page title
   */
  getTitle(): Cypress.Chainable<string> {
    return cy.title();
  }

  /**
   * Verify page title contains text
   * @param text - Text to verify in title (string or RegExp)
   */
  verifyTitleContains(text: string | RegExp): void {
    if (typeof text === 'string') {
      cy.title().should('contain', text);
    } else {
      cy.title().should('match', text);
    }
  }

  /**
   * Set viewport size
   * @param width - Viewport width (default: 1920)
   * @param height - Viewport height (default: 1080)
   */
  setViewport(width: number = 1920, height: number = 1080): void {
    cy.viewport(width, height);
  }

  /**
   * Wait for element to be visible
   * @param selector - Element selector (string)
   * @param timeout - Timeout in milliseconds (default: 10000)
   */
  waitForVisible(selector: string, timeout: number = 10000): void {
    cy.get(selector, { timeout }).should('be.visible');
  }

  /**
   * Get element by selector
   * Generic helper method to get an element
   * @param selector - Element selector (string)
   * @param timeout - Optional timeout in milliseconds
   * @returns Cypress chainable for the element
   */
  getElement(selector: string, timeout?: number): Cypress.Chainable<JQuery<HTMLElement>> {
    if (timeout) {
      return cy.get(selector, { timeout });
    }
    return cy.get(selector);
  }

  /**
   * Click element by selector
   * Generic helper method to click an element
   * @param selector - Element selector (string)
   * @param options - Optional click options (force, multiple, etc.)
   */
  clickElement(selector: string, options?: Partial<Cypress.ClickOptions>): void {
    cy.get(selector).click(options);
  }

  /**
   * Fill input field by selector
   * Generic helper method to fill an input field
   * @param selector - Input field selector (string)
   * @param value - Value to fill
   * @param options - Optional type options (delay, force, etc.)
   */
  fillInput(selector: string, value: string, options?: Partial<Cypress.TypeOptions>): void {
    cy.get(selector).clear().type(value, options);
  }

  /**
   * Select option in dropdown/select element
   * Generic helper method to select an option
   * @param selector - Select element selector (string)
   * @param value - Value to select (can be text, value, or index)
   * @param options - Optional select options (force, etc.)
   */
  selectOption(selector: string, value: string | number, options?: Partial<Cypress.SelectOptions>): void {
    if (typeof value === 'number') {
      cy.get(selector).select(value, options);
    } else {
      cy.get(selector).select(value, options);
    }
  }
}
