import { Page, Locator } from '@playwright/test';

/**
 * Page Object Model for the Home Page
 */
export class HomePage {
  readonly page: Page;
  readonly title: Locator;
  readonly applicationsCard: Locator;
  readonly companiesCard: Locator;
  readonly contactsCard: Locator;
  readonly clientsCard: Locator;
  readonly notesCard: Locator;
  readonly jobSearchSitesCard: Locator;

  constructor(page: Page) {
    this.page = page;
    // Title changed to "Job Search Application" and centered
    this.title = page.locator('h1.display-5, h1.display-4, h1');
    this.applicationsCard = page.locator('a[href="/applications"]');
    this.companiesCard = page.locator('a[href="/companies"]');
    this.contactsCard = page.locator('a[href="/contacts"]');
    this.clientsCard = page.locator('a[href="/clients"]');
    this.notesCard = page.locator('a[href="/notes"]');
    this.jobSearchSitesCard = page.locator('a[href="/job-search-sites"]');
  }

  async navigate() {
    await this.page.goto('/');
  }

  async clickApplications() {
    await this.applicationsCard.click();
  }

  async clickCompanies() {
    await this.companiesCard.click();
  }

  async clickContacts() {
    await this.contactsCard.click();
  }

  async clickClients() {
    await this.clientsCard.click();
  }

  async clickNotes() {
    await this.notesCard.click();
  }

  async clickJobSearchSites() {
    await this.jobSearchSitesCard.click();
  }
}
