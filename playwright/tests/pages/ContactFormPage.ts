import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for Contact Create Form
 * Uses data-qa selectors consistent with frontend/app/contacts/new/page.tsx
 */
export class ContactFormPage extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly firstNameInput: Locator;
  readonly lastNameInput: Locator;
  readonly titleInput: Locator;
  readonly linkedinInput: Locator;
  readonly contactTypeSelect: Locator;
  readonly companyIdInput: Locator;
  readonly applicationIdInput: Locator;
  readonly clientIdInput: Locator;
  readonly submitButton: Locator;
  readonly cancelButton: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector - uses data-qa attribute on h1 element
    this.title = page.locator('h1[data-qa="contact-create-title"]');
    // Form inputs - using data-qa attributes
    this.firstNameInput = page.locator('[data-qa="contact-first-name"]');
    this.lastNameInput = page.locator('[data-qa="contact-last-name"]');
    this.titleInput = page.locator('[data-qa="contact-create-title-input"]');
    this.linkedinInput = page.locator('[data-qa="contact-create-linkedin"]');
    this.contactTypeSelect = page.locator('[data-qa="contact-create-type"]');
    this.companyIdInput = page.locator('[data-qa="contact-create-company-id"]');
    this.applicationIdInput = page.locator('[data-qa="contact-create-application-id"]');
    this.clientIdInput = page.locator('[data-qa="contact-create-client-id"]');
    // Buttons
    this.submitButton = page.locator('[data-qa="contact-create-submit-button"]');
    this.cancelButton = page.locator('[data-qa="contact-create-cancel-button"], [data-qa="contact-create-cancel-button-bottom"]').first();
  }

  /**
   * Navigate to new contact form
   */
  async navigate(): Promise<void> {
    await super.navigate('/contacts/new');
  }

  /**
   * Fill form with data
   * @param data - Form data
   */
  async fillForm(data: {
    first_name?: string;
    last_name?: string;
    title?: string;
    linkedin?: string;
    contact_type?: string;
    company_id?: number;
    application_id?: number;
    client_id?: number;
  }): Promise<void> {
    if (data.first_name) {
      await this.firstNameInput.fill(data.first_name);
    }
    if (data.last_name) {
      await this.lastNameInput.fill(data.last_name);
    }
    if (data.title) {
      await this.titleInput.fill(data.title);
    }
    if (data.linkedin) {
      await this.linkedinInput.fill(data.linkedin);
    }
    if (data.contact_type) {
      await this.contactTypeSelect.selectOption(data.contact_type);
    }
    if (data.company_id !== undefined) {
      await this.companyIdInput.fill(data.company_id.toString());
    }
    if (data.application_id !== undefined) {
      await this.applicationIdInput.fill(data.application_id.toString());
    }
    if (data.client_id !== undefined) {
      await this.clientIdInput.fill(data.client_id.toString());
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
