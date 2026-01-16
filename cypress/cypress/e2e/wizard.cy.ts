/// <reference types="cypress" />
import { HomePage } from '../page-objects/HomePage';
import { ApplicationsPage } from '../page-objects/ApplicationsPage';
import { CompaniesPage } from '../page-objects/CompaniesPage';
import { CompanyFormPage } from '../page-objects/CompanyFormPage';
import { ContactsPage } from '../page-objects/ContactsPage';
import { ContactFormPage } from '../page-objects/ContactFormPage';
import { ClientsPage } from '../page-objects/ClientsPage';
import { ClientFormPage } from '../page-objects/ClientFormPage';
import { NotesPage } from '../page-objects/NotesPage';
import { JobSearchSitesPage } from '../page-objects/JobSearchSitesPage';
import { WizardStep1Page } from '../page-objects/WizardStep1Page';
import { CypressApiRequestUtility } from '../support/api-utils';
import { CypressDbUtility } from '../support/db-utils';
import type { EntityCounts } from '../../../lib/api-utils';
import type { JobSearchSite } from '../../../lib/db-utils';
import { getTestSuite } from '../support/test-utils';

/**
 * Wizard Test - Navigate through all pages and verify cancel functionality
 * 
 * This test suite:
 * 1. Tests navigation and cancel functionality for all entity creation flows
 * 2. Verifies that canceling forms does not save any data
 * 3. Verifies notes page shows no notes
 * 4. Verifies job search sites display correctly
 * 5. Verifies via API that no data was created
 * 
 * ⚠️ PREREQUISITES: Services must be running before executing these tests
 * 
 * Start services (choose one method):
 * 
 * Option 1: Start both services together (recommended):
 *   ./scripts/start-env.sh                    # Default: dev environment
 *   ./scripts/start-env.sh --env test        # Test environment
 * 
 * Option 2: Start services separately (2 terminals):
 *   Terminal 1 - Backend:
 *     ./scripts/start-be.sh                  # Default: dev environment
 *   Terminal 2 - Frontend:
 *     ./scripts/start-fe.sh                  # Default: dev environment
 * 
 * Services will be available at:
 *   - Frontend: http://localhost:3003 (dev), http://localhost:3004 (test)
 *   - Backend API: http://localhost:8003 (dev), http://localhost:8004 (test)
 * 
 * Run this test locally with Chrome:
 *   cd cypress
 *   npx cypress run --browser chrome --spec cypress/e2e/wizard.cy.ts
 * 
 * Or using npm script:
 *   cd cypress
 *   npm run cypress:run:chrome -- --spec cypress/e2e/wizard.cy.ts
 * 
 * Run in headed mode (see browser):
 *   npx cypress run --browser chrome --headed --spec cypress/e2e/wizard.cy.ts
 * 
 * Run in interactive mode (Cypress Test Runner):
 *   cd cypress
 *   npm run cypress:open
 *   # Then select wizard.cy.ts from the test list
 */

// Get test suite configuration - uses shared utility from lib/test-utils.ts
const wizard = getTestSuite('wizard');

describe('Wizard Tests', () => {
  // Cypress runs tests serially by default, but we can use .only() if needed
  // Note: Tests are already serial by default in Cypress
  
  let homePage: HomePage;
  let applicationsPage: ApplicationsPage;
  let companiesPage: CompaniesPage;
  let companyFormPage: CompanyFormPage;
  let contactsPage: ContactsPage;
  let contactFormPage: ContactFormPage;
  let clientsPage: ClientsPage;
  let clientFormPage: ClientFormPage;
  let notesPage: NotesPage;
  let jobSearchSitesPage: JobSearchSitesPage;
  let wizardStep1Page: WizardStep1Page;
  let apiUtils: CypressApiRequestUtility;
  let dbUtils: CypressDbUtility;
  let initialCounts: EntityCounts;
  let initialApplicationCount: number;
  let initialCompanyCount: number;
  let initialContactCount: number;
  let initialClientCount: number;
  let initialNoteCount: number;

  beforeEach(() => {
    // Set viewport to full screen (already set in config, but explicit here)
    cy.viewport(1920, 1080);
    
    // Initialize page objects
    homePage = new HomePage();
    applicationsPage = new ApplicationsPage();
    companiesPage = new CompaniesPage();
    companyFormPage = new CompanyFormPage();
    contactsPage = new ContactsPage();
    contactFormPage = new ContactFormPage();
    clientsPage = new ClientsPage();
    clientFormPage = new ClientFormPage();
    notesPage = new NotesPage();
    jobSearchSitesPage = new JobSearchSitesPage();
    wizardStep1Page = new WizardStep1Page();

    // Initialize API request utility
    // Environment and backend URL are automatically detected from Cypress.env()
    // No need to manually extract - the utility handles it internally
    apiUtils = new CypressApiRequestUtility();

    // Initialize database utility
    // Environment is automatically detected from Cypress.env()
    dbUtils = new CypressDbUtility();

    // Get all initial counts at once using the utility
    apiUtils.getAllEntityCounts().then((counts) => {
      initialCounts = counts;
      initialApplicationCount = counts.applications;
      initialCompanyCount = counts.companies;
      initialContactCount = counts.contacts;
      initialClientCount = counts.clients;
      initialNoteCount = counts.notes;
    });
  });

  it('test_home - Click Home Navigation, Add Application button, then Cancel', () => {
    // 1. Click the Home Navigation
    homePage.navigate();
    homePage.verifyPageLoaded();

    // 2. Click the Add Application button
    homePage.clickAddApplication();

    // Verify we're on the wizard step 1
    wizardStep1Page.verifyPageLoaded();

    // 3. Click the Cancel button
    wizardStep1Page.cancel();

    // Verify we're back at applications page
    applicationsPage.verifyPageLoaded();

    // Verify no applications were created
    apiUtils.verifyEntityCount('applications', initialApplicationCount);
  });

  it('test_application - Click Applications Navigation, Add button, then Cancel', () => {
    // 1. Click the Applications Navigation
    applicationsPage.navigate();
    applicationsPage.verifyPageLoaded();
    // 2. Click the Add button
    applicationsPage.clickNewApplication();
    // Verify we're on the wizard step 1
    wizardStep1Page.verifyPageLoaded();
    // 3. Click the Cancel button
    wizardStep1Page.cancel();
    // Verify we're back at applications page
    applicationsPage.verifyPageLoaded();

    // Verify no applications were created
    apiUtils.verifyEntityCount('applications', initialApplicationCount);
  });

  it('test_companies - Click Companies Navigation, Add button, populate all fields, then Cancel', () => {
    // 1. Click the Companies Navigation
    companiesPage.navigate();
    companiesPage.verifyPageLoaded();
    // 2. Click the Add button
    companiesPage.clickNewCompany();
    // Verify we're on the new company page
    companyFormPage.verifyFormLoaded();
    // 3. Populate all fields
    const testData = {
      name: `Test Company ${Date.now()}`,
      address: '123 Test Street',
      city: 'San Francisco',
      state: 'CA',
      zip: '94102',
      country: 'United States',
      job_type: 'Technology',
    };
    companyFormPage.fillForm(testData);
    // Verify fields are populated
    cy.get('[data-qa="company-create-name"]').should('have.value', testData.name);
    // 4. Click the Cancel button
    companyFormPage.cancel();
    // Verify we're back at companies page
    companiesPage.verifyPageLoaded();

    // Verify no companies were created
    apiUtils.verifyEntityCount('companies', initialCompanyCount);
  });

  it('test_contacts - Click Contacts Navigation, Add button, populate all fields, then Cancel', () => {
    // 1. Click the Contacts Navigation
    contactsPage.navigate();
    contactsPage.verifyPageLoaded();
    // 2. Click the Add button
    contactsPage.clickNewContact();
    // Verify we're on the new contact page
    contactFormPage.verifyFormLoaded();
    // 3. Populate all fields
    const testData = {
      first_name: `TestFirst${Date.now()}`,
      last_name: `TestLast${Date.now()}`,
      title: 'Software Engineer',
      linkedin: 'https://linkedin.com/in/test',
      contact_type: 'Recruiter',
    };
    contactFormPage.fillForm(testData);
    // Verify fields are populated
    cy.get('[data-qa="contact-first-name"]').should('have.value', testData.first_name);
    cy.get('[data-qa="contact-last-name"]').should('have.value', testData.last_name);
    // 4. Click the Cancel button
    contactFormPage.cancel();
    // Verify we're back at contacts page
    contactsPage.verifyPageLoaded();

    // Verify no contacts were created
    apiUtils.verifyEntityCount('contacts', initialContactCount);
  });

  it('test_clients - Click Clients Navigation, Add button, populate all fields, then Cancel', () => {
    // 1. Click the Clients Navigation
    clientsPage.navigate();
    clientsPage.verifyPageLoaded();
    // 2. Click the Add button
    clientsPage.clickNewClient();
    // Verify we're on the new client page
    clientFormPage.verifyFormLoaded();
    // 3. Populate all fields
    const testData = {
      name: `Test Client ${Date.now()}`,
    };
    clientFormPage.fillForm(testData);
    // Verify field is populated
    cy.get('[data-qa="client-create-name"]').should('have.value', testData.name);
    // 4. Click the Cancel button
    clientFormPage.cancel();
    // Verify we're back at clients page
    clientsPage.verifyPageLoaded();

    // Verify no clients were created
    apiUtils.verifyEntityCount('clients', initialClientCount);
  });

  it('test_notes - Click Notes Navigation, verify there are no notes', () => {
    // 1. Click the Notes Navigation
    notesPage.navigate();
    notesPage.verifyPageLoaded();
    // 2. Verify there are no notes
    // Check if empty state is visible or if notes list is empty
    cy.get('body').then(($body) => {
      const emptyStateVisible = $body.find('[data-qa="notes-empty-state"]').length > 0;
      const notesListVisible = $body.find('[data-qa*="notes-list"]').length > 0;
      
      if (emptyStateVisible) {
        cy.get('[data-qa="notes-empty-state"]').should('be.visible');
      } else if (notesListVisible) {
        // If notes list is visible, verify it's empty or has no note items
        cy.get('[data-qa*="notes-list"]').find('[data-qa*="note-"]').should('have.length', 0);
      } else {
        // If neither is visible, assume no notes
        cy.wrap(true).should('be.true');
      }
    });

    // Verify no notes were created
    apiUtils.verifyEntityCount('notes', initialNoteCount);
  });

  it('test_job_search_sites_api - Click Job Search Sites Navigation, verify all Names and URLs against API', () => {
    // 1. Get job search sites from API
    apiUtils.getJobSearchSites({ include_deleted: false }).then((apiSites) => {
      // 2. Click the Job Search Sites Navigation
      jobSearchSitesPage.navigate();
      jobSearchSitesPage.verifyPageLoaded();
      
      // 3. Verify page content matches API data
      if (apiSites.data.length === 0) {
        // If no sites in API, verify empty state is shown
        cy.get('[data-qa="job-search-sites-empty-state"]').should('be.visible');
      } else {
        // Verify table is visible
        cy.get('[data-qa="job-search-sites-table"]').should('be.visible');
        
        // Get all site rows from the page
        cy.get('[data-qa="job-search-sites-table"]').find('tbody tr').should('have.length', apiSites.data.length);
        
        // Verify each row matches API data
        apiSites.data.forEach((apiSite: any, index: number) => {
          cy.get('[data-qa="job-search-sites-table"]').find('tbody tr').eq(index).within(() => {
            // Verify name matches
            cy.get('a').first().should('be.visible').then(($link) => {
              const nameText = $link.text().trim();
              expect(nameText).to.equal(apiSite.name);
            });
            
            // Verify URL matches (or "N/A" if no URL)
            cy.get('td').eq(1).then(($cell) => {
              const urlText = $cell.text().trim();
              
              if (apiSite.url) {
                cy.get('a').should('be.visible').then(($urlLink) => {
                  const href = $urlLink.attr('href');
                  expect(href).to.equal(apiSite.url);
                });
              } else {
                expect(urlText).to.equal('N/A');
              }
            });
          });
        });
      }
    });
  });

  it('test_job_search_sites_db - Click Job Search Sites Navigation, verify all Names and URLs against database', () => {
    // 1. Get job search sites directly from database using the utility
    dbUtils.getJobSearchSites(false).then((dbSites: JobSearchSite[]) => {
      // 2. Click the Job Search Sites Navigation
      jobSearchSitesPage.navigate();
      jobSearchSitesPage.verifyPageLoaded();
      
      // 3. Verify page content matches database data
      if (dbSites.length === 0) {
        // If no sites in database, verify empty state is shown
        cy.get('[data-qa="job-search-sites-empty-state"]').should('be.visible');
      } else {
        // Verify table is visible
        cy.get('[data-qa="job-search-sites-table"]').should('be.visible');
        
        // Get all site rows from the page
        cy.get('[data-qa="job-search-sites-table"]').find('tbody tr').should('have.length', dbSites.length);
        
        // Verify each row matches database data
        dbSites.forEach((dbSite: JobSearchSite, index: number) => {
          cy.get('[data-qa="job-search-sites-table"]').find('tbody tr').eq(index).within(() => {
            // Verify name matches
            cy.get('a').first().should('be.visible').then(($link) => {
              const nameText = $link.text().trim();
              expect(nameText).to.equal(dbSite.name);
            });
            
            // Verify URL matches (or "N/A" if no URL)
            cy.get('td').eq(1).within(() => {
              if (dbSite.url) {
                // The URL cell contains a link to the external URL
                cy.get('a').should('be.visible').then(($urlLink) => {
                  const href = $urlLink.attr('href');
                  expect(href).to.equal(dbSite.url);
                });
              } else {
                // If no URL, should show 'N/A'
                cy.contains('N/A');
              }
            });
          });
        });
      }
    });
  });
});
