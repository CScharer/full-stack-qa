import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Home Page
 * Uses data-qa selectors consistent with Cypress HomePage and frontend Sidebar.tsx
 */
export class HomePage extends BasePage {
  // Selectors - use data-qa attributes from Sidebar.tsx
  readonly title: Locator;
  readonly sidebar: Locator;
  readonly sidebarTitle: Locator;
  readonly sidebarNavigation: Locator;
  readonly sidebarNavHome: Locator;
  readonly applicationsCard: Locator;
  readonly companiesCard: Locator;
  readonly contactsCard: Locator;
  readonly clientsCard: Locator;
  readonly notesCard: Locator;
  readonly jobSearchSitesCard: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector (data-qa from frontend/app/page.tsx)
    this.title = page.locator('[data-qa="home-title"]');
    // Sidebar selectors (data-qa attributes)
    this.sidebar = page.locator('[data-qa="sidebar"]');
    this.sidebarTitle = page.locator('[data-qa="sidebar-title"]');
    this.sidebarNavigation = page.locator('[data-qa="sidebar-navigation"]');
    this.sidebarNavHome = page.locator('[data-qa="sidebar-nav-home"]');
    // Navigation cards (data-qa attributes from Sidebar.tsx)
    this.applicationsCard = page.locator('[data-qa="sidebar-nav-applications"]');
    this.companiesCard = page.locator('[data-qa="sidebar-nav-companies"]');
    this.contactsCard = page.locator('[data-qa="sidebar-nav-contacts"]');
    this.clientsCard = page.locator('[data-qa="sidebar-nav-clients"]');
    this.notesCard = page.locator('[data-qa="sidebar-nav-notes"]');
    this.jobSearchSitesCard = page.locator('[data-qa="sidebar-nav-job-search-sites"]');
  }

  /**
   * Navigate to home page
   */
  async navigate(): Promise<void> {
    await super.navigate('/');
  }

  /**
   * Verify page has loaded
   */
  async verifyPageLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await this.verifyTitleContains('Job Search Application');
    await expect(this.page.locator('body')).toBeVisible();
  }

  /**
   * Verify sidebar is visible
   */
  async verifySidebarVisible(): Promise<void> {
    await this.waitForVisible(this.sidebar, 10000);
    await expect(this.sidebar).toBeVisible();
  }

  /**
   * Verify navigation title contains text
   * @param text - Text to verify (default: 'Navigation')
   */
  async verifyNavigationTitle(text: string = 'Navigation'): Promise<void> {
    const navigationTitle = this.sidebarTitle.filter({ hasText: text });
    await this.waitForVisible(navigationTitle, 10000);
    await expect(navigationTitle).toBeVisible();
  }

  /**
   * Verify navigation elements are present
   */
  async verifyNavigationElements(): Promise<void> {
    await this.waitForVisible(this.sidebarNavigation, 10000);
    await this.waitForVisible(this.sidebarNavHome, 10000);
    await expect(this.sidebarNavigation).toBeVisible();
    await expect(this.sidebarNavHome).toBeVisible();
  }

  /**
   * Click Applications card
   */
  async clickApplications(): Promise<void> {
    await this.applicationsCard.click();
  }

  /**
   * Click Companies card
   */
  async clickCompanies(): Promise<void> {
    await this.companiesCard.click();
  }

  /**
   * Click Contacts card
   */
  async clickContacts(): Promise<void> {
    await this.contactsCard.click();
  }

  /**
   * Click Clients card
   */
  async clickClients(): Promise<void> {
    await this.clientsCard.click();
  }

  /**
   * Click Notes card
   */
  async clickNotes(): Promise<void> {
    await this.notesCard.click();
  }

  /**
   * Click Job Search Sites card
   */
  async clickJobSearchSites(): Promise<void> {
    await this.jobSearchSitesCard.click();
  }

  /**
   * Verify all navigation cards are visible
   */
  async verifyAllCardsVisible(): Promise<void> {
    await expect(this.applicationsCard).toBeVisible();
    await expect(this.companiesCard).toBeVisible();
    await expect(this.contactsCard).toBeVisible();
    await expect(this.clientsCard).toBeVisible();
    await expect(this.notesCard).toBeVisible();
    await expect(this.jobSearchSitesCard).toBeVisible();
  }
}
