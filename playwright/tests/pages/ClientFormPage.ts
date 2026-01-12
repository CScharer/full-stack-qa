import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for Client Create Form
 * Uses data-qa selectors consistent with frontend/app/clients/new/page.tsx
 */
export class ClientFormPage extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly nameInput: Locator;
  readonly submitButton: Locator;
  readonly cancelButton: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector - uses data-qa attribute
    this.title = page.locator('[data-qa="client-create-title"]');
    // Form input - using data-qa attribute
    this.nameInput = page.locator('[data-qa="client-create-name"]');
    // Buttons
    this.submitButton = page.locator('[data-qa="client-create-submit-button"]');
    this.cancelButton = page.locator('[data-qa="client-create-cancel-button"], [data-qa="client-create-cancel-button-bottom"]').first();
  }

  /**
   * Navigate to new client form
   */
  async navigate(): Promise<void> {
    await super.navigate('/clients/new');
  }

  /**
   * Fill form with data
   * @param data - Form data
   */
  async fillForm(data: {
    name?: string;
  }): Promise<void> {
    if (data.name) {
      await this.nameInput.fill(data.name);
    }
  }

  /**
   * Submit the form
   */
  async submit(): Promise<void> {
    await this.submitButton.click();
  }

  /**
   * Cancel the form
   */
  async cancel(): Promise<void> {
    await this.cancelButton.click();
  }

  /**
   * Verify form has loaded
   */
  async verifyFormLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await expect(this.title).toBeVisible();
  }
}
