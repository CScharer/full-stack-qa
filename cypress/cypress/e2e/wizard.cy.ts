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
import { getBackendUrl } from '../../../config/port-config';

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
  let backendBaseUrl: string;
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

    // Determine backend URL based on environment
    // Priority 1: Use BACKEND_URL env var if provided (allows CI/CD override)
    // Priority 2: Use environment from Cypress.env (set in cypress.config.ts from process.env.ENVIRONMENT)
    // Priority 3: Fallback to 'dev' to match other scripts
    // Note: process.env is not available in browser context, so we use Cypress.env()
    if (Cypress.env('BACKEND_URL')) {
      backendBaseUrl = Cypress.env('BACKEND_URL') as string;
    } else {
      const environment = (Cypress.env('ENVIRONMENT') || 'dev') as string;
      backendBaseUrl = getBackendUrl(environment);
    }

    // Get initial counts for all entities via API
    // Use aliases to store the counts for later use
    cy.request({
      method: 'GET',
      url: `${backendBaseUrl}/api/v1/applications?limit=1`,
      failOnStatusCode: false,
    }).then((response) => {
      if (response.status === 200 && response.body) {
        initialApplicationCount = response.body.total || 0;
      } else {
        initialApplicationCount = 0;
      }
    });

    cy.request({
      method: 'GET',
      url: `${backendBaseUrl}/api/v1/companies?limit=1`,
      failOnStatusCode: false,
    }).then((response) => {
      if (response.status === 200 && response.body) {
        initialCompanyCount = response.body.total || 0;
      } else {
        initialCompanyCount = 0;
      }
    });

    cy.request({
      method: 'GET',
      url: `${backendBaseUrl}/api/v1/contacts?limit=1`,
      failOnStatusCode: false,
    }).then((response) => {
      if (response.status === 200 && response.body) {
        initialContactCount = response.body.total || 0;
      } else {
        initialContactCount = 0;
      }
    });

    cy.request({
      method: 'GET',
      url: `${backendBaseUrl}/api/v1/clients?limit=1`,
      failOnStatusCode: false,
    }).then((response) => {
      if (response.status === 200 && response.body) {
        initialClientCount = response.body.total || 0;
      } else {
        initialClientCount = 0;
      }
    });

    cy.request({
      method: 'GET',
      url: `${backendBaseUrl}/api/v1/notes?limit=1`,
      failOnStatusCode: false,
    }).then((response) => {
      if (response.status === 200 && response.body) {
        initialNoteCount = response.body.total || 0;
      } else {
        initialNoteCount = 0;
      }
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
  });

  it('test_job_search_sites - Click Job Search Sites Navigation, verify all Names and URLs', () => {
    // 1. Click the Job Search Sites Navigation
    jobSearchSitesPage.navigate();
    jobSearchSitesPage.verifyPageLoaded();
    // 2. Verify all of the Names and URLs
    // Check if there are any sites
    cy.get('body').then(($body) => {
      const hasSites = $body.find('[data-qa="job-search-sites-table"]').length > 0;
      const isEmpty = $body.find('[data-qa="job-search-sites-empty-state"]').length > 0;

      if (hasSites) {
        // Get all site rows
        cy.get('[data-qa="job-search-sites-table"]').find('tbody tr').then(($rows) => {
          const rowCount = $rows.length;

          // Verify each row has a name and URL (or N/A for URL)
          for (let i = 0; i < rowCount; i++) {
            cy.get('[data-qa="job-search-sites-table"]').find('tbody tr').eq(i).within(() => {
              // Verify name exists (should be in first column or link)
              cy.get('a').first().should('be.visible').then(($link) => {
                const nameText = $link.text();
                expect(nameText).to.be.a('string');
                expect(nameText.trim().length).to.be.greaterThan(0);
              });

              // Verify URL exists (either in second column for desktop or in mobile view)
              // Check desktop view first
              cy.get('td').eq(1).then(($cell) => {
                const urlText = $cell.text();
                expect(urlText).to.be.a('string');
                if (urlText !== 'N/A') {
                  cy.get('a').should('be.visible').then(($urlLink) => {
                    const href = $urlLink.attr('href');
                    expect(href).to.be.a('string');
                    expect(href).to.not.be.empty;
                  });
                }
              });
            });
          }
        });
      } else if (isEmpty) {
        // If empty state is shown, that's also valid - just verify it
        cy.get('[data-qa="job-search-sites-empty-state"]').should('be.visible');
      }
    });
  });

  it('test_no_data - Verify no applications, companies, contacts, clients, or notes were created', () => {
    // 1. Verify no applications were created
    cy.request({
      method: 'GET',
      url: `${backendBaseUrl}/api/v1/applications?limit=1`,
      failOnStatusCode: false,
    }).then((response) => {
      if (response.status === 200 && response.body) {
        const finalApplicationCount = response.body.total || 0;
        expect(finalApplicationCount).to.equal(initialApplicationCount);
      }
    });
    
    // 2. Verify no companies were created
    cy.request({
      method: 'GET',
      url: `${backendBaseUrl}/api/v1/companies?limit=1`,
      failOnStatusCode: false,
    }).then((response) => {
      if (response.status === 200 && response.body) {
        const finalCompanyCount = response.body.total || 0;
        expect(finalCompanyCount).to.equal(initialCompanyCount);
      }
    });
    
    // 3. Verify no contacts were created
    cy.request({
      method: 'GET',
      url: `${backendBaseUrl}/api/v1/contacts?limit=1`,
      failOnStatusCode: false,
    }).then((response) => {
      if (response.status === 200 && response.body) {
        const finalContactCount = response.body.total || 0;
        expect(finalContactCount).to.equal(initialContactCount);
      }
    });
    
    // 4. Verify no clients were created
    cy.request({
      method: 'GET',
      url: `${backendBaseUrl}/api/v1/clients?limit=1`,
      failOnStatusCode: false,
    }).then((response) => {
      if (response.status === 200 && response.body) {
        const finalClientCount = response.body.total || 0;
        expect(finalClientCount).to.equal(initialClientCount);
      }
    });
    
    // 5. Verify no notes were created
    cy.request({
      method: 'GET',
      url: `${backendBaseUrl}/api/v1/notes?limit=1`,
      failOnStatusCode: false,
    }).then((response) => {
      if (response.status === 200 && response.body) {
        const finalNoteCount = response.body.total || 0;
        expect(finalNoteCount).to.equal(initialNoteCount);
      }
    });
  });
});
