import { Page, Locator } from '@playwright/test';

/**
 * Page Object Model for Application Detail Page
 */
export class ApplicationDetailPage {
  readonly page: Page;
  readonly title: Locator;
  readonly backLink: Locator;
  readonly editButton: Locator;
  readonly deleteButton: Locator;
  readonly statusBadge: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('h1.h3, h1.h4, h1.h2');
    this.backLink = page.locator('a:has-text("Back"), a:has-text("←")');
    this.editButton = page.locator('a:has-text("Edit"), button:has-text("Edit")');
    this.deleteButton = page.locator('button:has-text("Delete")');
    this.statusBadge = page.locator('.badge.bg-primary');
  }

  async navigate(id: number) {
    await this.page.goto(`/applications/${id}`);
  }

  async clickEdit() {
    await this.editButton.click();
  }

  async clickDelete() {
    await this.deleteButton.click();
  }

  async clickBack() {
    await this.backLink.click();
  }

  async getStatus(): Promise<string | null> {
    return await this.statusBadge.textContent();
  }
}
