import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for Company Create Form
 * Uses data-qa selectors consistent with frontend/app/companies/new/page.tsx
 */
export class CompanyFormPage extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly nameInput: Locator;
  readonly addressInput: Locator;
  readonly cityInput: Locator;
  readonly stateInput: Locator;
  readonly zipInput: Locator;
  readonly countrySelect: Locator;
  readonly jobTypeSelect: Locator;
  readonly submitButton: Locator;
  readonly cancelButton: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector - uses data-qa attribute
    this.title = page.locator('[data-qa="company-create-title"]');
    // Form inputs - using data-qa attributes
    this.nameInput = page.locator('[data-qa="company-create-name"]');
    this.addressInput = page.locator('[data-qa="company-create-address"]');
    this.cityInput = page.locator('[data-qa="company-create-city"]');
    this.stateInput = page.locator('[data-qa="company-create-state"]');
    this.zipInput = page.locator('[data-qa="company-create-zip"]');
    this.countrySelect = page.locator('[data-qa="company-create-country"]');
    this.jobTypeSelect = page.locator('[data-qa="company-create-job-type"]');
    // Buttons
    this.submitButton = page.locator('[data-qa="company-create-submit-button"]');
    this.cancelButton = page.locator('[data-qa="company-create-cancel-button"]');
  }

  /**
   * Navigate to new company form
   */
  async navigate(): Promise<void> {
    await super.navigate('/companies/new');
  }

  /**
   * Fill form with data
   * @param data - Form data
   */
  async fillForm(data: {
    name?: string;
    address?: string;
    city?: string;
    state?: string;
    zip?: string;
    country?: string;
    job_type?: string;
  }): Promise<void> {
    if (data.name) {
      await this.nameInput.fill(data.name);
    }
    if (data.address) {
      await this.addressInput.fill(data.address);
    }
    if (data.city) {
      await this.cityInput.fill(data.city);
    }
    if (data.state) {
      await this.stateInput.fill(data.state);
    }
    if (data.zip) {
      await this.zipInput.fill(data.zip);
    }
    if (data.country) {
      await this.countrySelect.selectOption(data.country);
    }
    if (data.job_type) {
      await this.jobTypeSelect.selectOption(data.job_type);
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
