import { Page, Locator } from '@playwright/test';

/**
 * Page Object Model for Application Create/Edit Form
 */
export class ApplicationFormPage {
  readonly page: Page;
  readonly title: Locator;
  readonly positionInput: Locator;
  readonly statusSelect: Locator;
  readonly workSettingSelect: Locator;
  readonly locationInput: Locator;
  readonly jobLinkInput: Locator;
  readonly submitButton: Locator;
  readonly cancelButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('h1.h3, h1.h4, h1.h2');
    this.positionInput = page.locator('input[placeholder*="Software Engineer"], label:has-text("Position") + input, label:has-text("Position") ~ input');
    this.statusSelect = page.locator('select').filter({ hasText: /Pending|Interview|Rejected|Accepted/ }).first();
    this.workSettingSelect = page.locator('select').filter({ hasText: /Remote|Hybrid|On-site/ }).first();
    this.locationInput = page.locator('input[placeholder*="San Francisco"], label:has-text("Location") + input, label:has-text("Location") ~ input');
    this.jobLinkInput = page.locator('input[type="url"], label:has-text("Job Link") + input, label:has-text("Job Link") ~ input');
    this.submitButton = page.locator('button[type="submit"]:has-text("Create"), button[type="submit"]:has-text("Update"), button:has-text("Create Application"), button:has-text("Update Application")');
    this.cancelButton = page.locator('button:has-text("Cancel"), a:has-text("Cancel")');
  }

  async navigateToNew() {
    await this.page.goto('/applications/new');
  }

  async navigateToEdit(id: number) {
    await this.page.goto(`/applications/${id}/edit`);
  }

  async fillForm(data: {
    position?: string;
    status?: string;
    workSetting?: string;
    location?: string;
    jobLink?: string;
  }) {
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

  async submit() {
    await this.submitButton.click();
  }

  async cancel() {
    await this.cancelButton.click();
  }
}
