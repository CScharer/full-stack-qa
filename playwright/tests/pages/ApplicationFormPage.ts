import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for Application Create/Edit Form
 * Note: The application form is a multi-step wizard, so this is a simplified version
 * that handles basic form interactions. For full wizard support, extend this class.
 */
export class ApplicationFormPage extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly positionInput: Locator;
  readonly statusSelect: Locator;
  readonly workSettingSelect: Locator;
  readonly locationInput: Locator;
  readonly jobLinkInput: Locator;
  readonly submitButton: Locator;
  readonly cancelButton: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector (fallback to h1 if data-qa not available)
    this.title = page.locator('h1.h3, h1.h4, h1.h2, h1');
    // Form inputs (fallback to label-based selectors if data-qa not available)
    this.positionInput = page.locator('input[placeholder*="Position"], label:has-text("Position") + input, label:has-text("Position") ~ input, input[name="position"]');
    this.statusSelect = page.locator('select').filter({ hasText: /Pending|Interview|Rejected|Accepted/ }).first();
    this.workSettingSelect = page.locator('select').filter({ hasText: /Remote|Hybrid|On-site/ }).first();
    this.locationInput = page.locator('input[placeholder*="Location"], label:has-text("Location") + input, label:has-text("Location") ~ input, input[name="location"]');
    this.jobLinkInput = page.locator('input[type="url"], label:has-text("Job Link") + input, label:has-text("Job Link") ~ input, input[name="job_link"]');
    // Buttons
    this.submitButton = page.locator('button[type="submit"]:has-text("Create"), button[type="submit"]:has-text("Update"), button:has-text("Create Application"), button:has-text("Update Application"), button:has-text("Next")');
    this.cancelButton = page.locator('button:has-text("Cancel"), a:has-text("Cancel")');
  }

  /**
   * Navigate to new application form
   */
  async navigateToNew(): Promise<void> {
    await super.navigate('/applications/new');
  }

  /**
   * Navigate to edit application form
   * @param applicationId - Application ID
   */
  async navigateToEdit(applicationId: number): Promise<void> {
    await super.navigate(`/applications/${applicationId}/edit`);
  }

  /**
   * Fill form with data
   * @param data - Form data
   */
  async fillForm(data: {
    position?: string;
    status?: string;
    workSetting?: string;
    location?: string;
    jobLink?: string;
  }): Promise<void> {
    if (data.position) {
      await this.positionInput.fill(data.position);
    }
    if (data.status) {
      await this.statusSelect.selectOption(data.status);
    }
    if (data.workSetting) {
      await this.workSettingSelect.selectOption(data.workSetting);
    }
    if (data.location) {
      await this.locationInput.fill(data.location);
    }
    if (data.jobLink) {
      await this.jobLinkInput.fill(data.jobLink);
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
