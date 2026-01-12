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
    // Title selector - wizard step2 uses data-qa="wizard-step2-title", edit form uses h1
    this.title = page.locator('[data-qa="wizard-step2-title"], h1.h3, h1.h4, h1.h2, h1');
    // Form inputs - wizard step2 uses data-qa attributes (no ID needed)
    // Edit form uses data-qa with application ID - use getter methods for edit form
    this.positionInput = page.locator('[data-qa="application-position"]');
    this.statusSelect = page.locator('[data-qa="application-status"]');
    this.workSettingSelect = page.locator('[data-qa="application-work-setting"]');
    this.locationInput = page.locator('[data-qa="application-location"]');
    this.jobLinkInput = page.locator('[data-qa="application-job-link"]');
    // Buttons - wizard step2 uses data-qa attributes
    this.submitButton = page.locator('[data-qa="wizard-step2-submit-button"], button[type="submit"]:has-text("Update"), button[type="submit"]:has-text("Create Application"), button[type="submit"]:has-text("Update Application")');
    this.cancelButton = page.locator('[data-qa="wizard-step2-back-button"], [data-qa^="application-edit-"][data-qa$="-cancel-button"], button:has-text("Cancel"), a:has-text("Cancel")');
  }

  /**
   * Get position input for edit form
   * @param applicationId - Application ID
   */
  getPositionInput(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-edit-${applicationId}-position-input"]`);
  }

  /**
   * Get status select for edit form
   * @param applicationId - Application ID
   */
  getStatusSelect(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-edit-${applicationId}-status-select"]`);
  }

  /**
   * Get work setting select for edit form
   * @param applicationId - Application ID
   */
  getWorkSettingSelect(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-edit-${applicationId}-work-setting-select"]`);
  }

  /**
   * Get location input for edit form
   * @param applicationId - Application ID
   */
  getLocationInput(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-edit-${applicationId}-location-input"]`);
  }

  /**
   * Get job link input for edit form
   * @param applicationId - Application ID
   */
  getJobLinkInput(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-edit-${applicationId}-job-link-input"]`);
  }

  /**
   * Get submit button for edit form
   * @param applicationId - Application ID
   */
  getSubmitButton(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-edit-${applicationId}-submit-button"]`);
  }

  /**
   * Get cancel button for edit form
   * @param applicationId - Application ID
   */
  getCancelButton(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-edit-${applicationId}-cancel-button"], [data-qa="application-edit-${applicationId}-cancel-button-bottom"]`);
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
   * Fill form with data (for wizard step2 - new application)
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
   * Fill edit form with data
   * @param applicationId - Application ID
   * @param data - Form data
   */
  async fillEditForm(applicationId: number, data: {
    position?: string;
    status?: string;
    workSetting?: string;
    location?: string;
    jobLink?: string;
  }): Promise<void> {
    if (data.position) {
      await this.getPositionInput(applicationId).fill(data.position);
    }
    if (data.status) {
      await this.getStatusSelect(applicationId).selectOption(data.status);
    }
    if (data.workSetting) {
      await this.getWorkSettingSelect(applicationId).selectOption(data.workSetting);
    }
    if (data.location) {
      await this.getLocationInput(applicationId).fill(data.location);
    }
    if (data.jobLink) {
      await this.getJobLinkInput(applicationId).fill(data.jobLink);
    }
  }

  /**
   * Submit the form (for wizard step2 - new application)
   */
  async submit(): Promise<void> {
    await this.submitButton.click();
  }

  /**
   * Submit the edit form
   * @param applicationId - Application ID
   */
  async submitEdit(applicationId: number): Promise<void> {
    await this.getSubmitButton(applicationId).click();
  }

  /**
   * Cancel the form (for wizard step2 - new application)
   */
  async cancel(): Promise<void> {
    await this.cancelButton.click();
  }

  /**
   * Cancel the edit form
   * @param applicationId - Application ID
   */
  async cancelEdit(applicationId: number): Promise<void> {
    await this.getCancelButton(applicationId).click();
  }

  /**
   * Verify form has loaded
   */
  async verifyFormLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await expect(this.title).toBeVisible();
  }
}
