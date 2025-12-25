import { Page, Locator } from '@playwright/test';

/**
 * Page Object Model for the Applications List Page
 */
export class ApplicationsPage {
  readonly page: Page;
  readonly title: Locator;
  readonly newApplicationButton: Locator;
  readonly applicationsTable: Locator;
  readonly emptyState: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('h1.h2, h2');
    // Button text changed from "New Application" to "Add"
    this.newApplicationButton = page.locator('a[href="/applications/new"], button:has-text("Add")');
    this.applicationsTable = page.locator('table.table');
    this.emptyState = page.locator('text=No applications found');
    // "Create Your First Application" button removed
  }

  async navigate() {
    await this.page.goto('/applications');
  }

  async clickNewApplication() {
    await this.newApplicationButton.click();
  }

  async getApplicationRow(position: string) {
    return this.page.locator(`table tbody tr:has-text("${position}")`);
  }

  async clickApplication(position: string) {
    const row = await this.getApplicationRow(position);
    await row.locator('a').first().click();
  }

  async clickEdit(position: string) {
    const row = await this.getApplicationRow(position);
    await row.locator('a:has-text("Edit")').click();
  }

  async clickDelete(position: string) {
    const row = await this.getApplicationRow(position);
    await row.locator('button:has-text("Delete")').click();
  }

  async hasApplication(position: string): Promise<boolean> {
    const row = await this.getApplicationRow(position);
    return await row.count() > 0;
  }
}
