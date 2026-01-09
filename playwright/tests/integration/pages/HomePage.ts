import { Page, Locator } from '@playwright/test';

/**
 * Page Object Model for the Home Page
 * Uses data-qa selectors from frontend/app/page.tsx and frontend/components/Sidebar.tsx
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
    // Title (data-qa from frontend/app/page.tsx)
    this.title = page.locator('[data-qa="home-title"]');
    // Navigation cards (data-qa attributes from Sidebar.tsx)
    this.applicationsCard = page.locator('[data-qa="sidebar-nav-applications"]');
    this.companiesCard = page.locator('[data-qa="sidebar-nav-companies"]');
    this.contactsCard = page.locator('[data-qa="sidebar-nav-contacts"]');
    this.clientsCard = page.locator('[data-qa="sidebar-nav-clients"]');
    this.notesCard = page.locator('[data-qa="sidebar-nav-notes"]');
    this.jobSearchSitesCard = page.locator('[data-qa="sidebar-nav-job-search-sites"]');
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
