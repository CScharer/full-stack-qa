import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for Application Wizard Step 1 (Contact Selection)
 * Uses data-qa selectors consistent with frontend/app/applications/new/step1/page.tsx
 */
export class WizardStep1Page extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly backLink: Locator;
  readonly contactSelect: Locator;
  readonly contactSelected: Locator;
  readonly nextButton: Locator;
  readonly cancelButton: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector
    this.title = page.locator('[data-qa="wizard-step1-title"]');
    // Back link
    this.backLink = page.locator('[data-qa="wizard-step1-back-link"]');
    // Contact select (EntitySelect component)
    this.contactSelect = page.locator('[data-qa="entity-select-contact"]');
    // Contact selected confirmation
    this.contactSelected = page.locator('[data-qa="wizard-step1-contact-selected"]');
    // Buttons
    this.nextButton = page.locator('[data-qa="wizard-step1-next-button"]');
    this.cancelButton = page.locator('[data-qa="wizard-step1-cancel-button"]');
  }

  /**
   * Navigate to wizard step 1
   */
  async navigate(): Promise<void> {
    await super.navigate('/applications/new/step1');
  }

  /**
   * Select a contact by clicking the first available option
   */
  async selectFirstContact(): Promise<void> {
    // Click on the EntitySelect input to open the dropdown
    const contactInput = this.contactSelect.locator('input').first();
    await contactInput.click();
    await this.page.waitForTimeout(500);

    // Wait for dropdown options to appear
    const firstContactOption = this.page.locator('[data-qa^="entity-select-contact-option-"]').first();
    const optionCount = await firstContactOption.count();
    
    if (optionCount > 0) {
      await firstContactOption.click();
      await this.page.waitForTimeout(500);
      
      // Verify contact is selected
      await expect(this.contactSelected).toBeVisible({ timeout: 5000 });
    }
  }

  /**
   * Click Next button
   */
  async clickNext(): Promise<void> {
    await this.nextButton.click();
  }

  /**
   * Click Cancel button
   */
  async cancel(): Promise<void> {
    // Wait for button to be visible and actionable
    await this.waitForVisible(this.cancelButton, 10000);
    await expect(this.cancelButton).toBeVisible();
    await expect(this.cancelButton).toBeEnabled();
    // Click and wait for navigation
    await this.cancelButton.click();
    // Wait for navigation to complete - use more specific URL pattern to avoid matching /applications/new/step1
    // Match /applications but not /applications/new or /applications/[id]
    await this.page.waitForURL(/\/applications$|\/applications\?/, { timeout: 20000 });
    await this.waitForPageLoad();
  }

  /**
   * Verify page has loaded
   */
  async verifyPageLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await expect(this.title).toBeVisible();
    await expect(this.title).toContainText('Step 1: Contact');
  }
}
