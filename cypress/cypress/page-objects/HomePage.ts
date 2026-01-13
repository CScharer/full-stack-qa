/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Home Page
 */
export class HomePage extends BasePage {
  // Selectors
  private readonly sidebar = '[data-qa="sidebar"]';
  private readonly sidebarTitle = '[data-qa="sidebar-title"]';
  private readonly sidebarNavigation = '[data-qa="sidebar-navigation"]';
  private readonly sidebarNavHome = '[data-qa="sidebar-nav-home"]';
  private readonly applicationsCard = '[data-qa="sidebar-nav-applications"]';
  private readonly companiesCard = '[data-qa="sidebar-nav-companies"]';
  private readonly contactsCard = '[data-qa="sidebar-nav-contacts"]';
  private readonly clientsCard = '[data-qa="sidebar-nav-clients"]';
  private readonly notesCard = '[data-qa="sidebar-nav-notes"]';
  private readonly jobSearchSitesCard = '[data-qa="sidebar-nav-job-search-sites"]';

  /**
   * Navigate to home page
   */
  navigate(): void {
    this.visit('/');
  }

  /**
   * Verify page has loaded
   */
  verifyPageLoaded(): void {
    this.waitForPageLoad();
    this.verifyTitleContains('Job Search Application');
  }

  /**
   * Verify sidebar is visible
   */
  verifySidebarVisible(): void {
    cy.get(this.sidebar).should('be.visible');
  }

  /**
   * Verify navigation title contains text
   * @param text - Text to verify (default: 'Navigation')
   */
  verifyNavigationTitle(text: string = 'Navigation'): void {
    cy.get(this.sidebarTitle).contains(text).should('be.visible');
  }

  /**
   * Verify navigation elements are present
   */
  verifyNavigationElements(): void {
    cy.get(this.sidebarNavigation).should('be.visible');
    cy.get(this.sidebarNavHome).should('be.visible');
  }

  /**
   * Click Applications card
   */
  clickApplications(): void {
    cy.get(this.applicationsCard).click();
  }

  /**
   * Click Companies card
   */
  clickCompanies(): void {
    cy.get(this.companiesCard).click();
  }

  /**
   * Click Contacts card
   */
  clickContacts(): void {
    cy.get(this.contactsCard).click();
  }

  /**
   * Click Clients card
   */
  clickClients(): void {
    cy.get(this.clientsCard).click();
  }

  /**
   * Click Notes card
   */
  clickNotes(): void {
    cy.get(this.notesCard).click();
  }

  /**
   * Click Job Search Sites card
   */
  clickJobSearchSites(): void {
    cy.get(this.jobSearchSitesCard).click();
  }

  /**
   * Verify all navigation cards are visible
   */
  verifyAllCardsVisible(): void {
    cy.get(this.applicationsCard).should('be.visible');
    cy.get(this.companiesCard).should('be.visible');
    cy.get(this.contactsCard).should('be.visible');
    cy.get(this.clientsCard).should('be.visible');
    cy.get(this.notesCard).should('be.visible');
    cy.get(this.jobSearchSitesCard).should('be.visible');
  }

  /**
   * Click Add Application button on home page
   */
  clickAddApplication(): void {
    const addApplicationButton = '[data-qa="home-new-application-button"]';
    cy.get(addApplicationButton, { timeout: 10000 }).should('be.visible').should('be.enabled');
    cy.get(addApplicationButton).click();
  }
}
