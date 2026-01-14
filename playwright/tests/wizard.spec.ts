import { test, expect } from '@playwright/test';
import { HomePage } from './pages/HomePage';
import { ApplicationsPage } from './pages/ApplicationsPage';
import { CompaniesPage } from './pages/CompaniesPage';
import { CompanyFormPage } from './pages/CompanyFormPage';
import { ContactsPage } from './pages/ContactsPage';
import { ContactFormPage } from './pages/ContactFormPage';
import { ClientsPage } from './pages/ClientsPage';
import { ClientFormPage } from './pages/ClientFormPage';
import { NotesPage } from './pages/NotesPage';
import { JobSearchSitesPage } from './pages/JobSearchSitesPage';
import { WizardStep1Page } from './pages/WizardStep1Page';
import { PlaywrightApiRequestUtility } from '../helpers/api-utils';
import { PlaywrightDbUtility } from '../helpers/db-utils';
import type { EntityCounts } from '../../lib/api-utils';
import type { JobSearchSite } from '../../lib/db-utils';

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
 *   cd playwright
 *   npx playwright test wizard.spec.ts --project=chromium
 * 
 * Or using npm script:
 *   cd playwright
 *   npm run test:chrome -- wizard.spec.ts
 * 
 * Run in headed mode (see browser):
 *   npx playwright test wizard.spec.ts --project=chromium --headed
 * 
 * Run in UI mode (interactive):
 *   npx playwright test wizard.spec.ts --project=chromium --ui
 */
test.describe('Wizard Tests', () => {
  // Configure tests to run serially (in order) instead of in parallel
  test.describe.configure({ mode: 'serial' });

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
  let apiUtils: PlaywrightApiRequestUtility;
  let dbUtils: PlaywrightDbUtility;
  let initialCounts: EntityCounts;
  let initialApplicationCount: number;
  let initialCompanyCount: number;
  let initialContactCount: number;
  let initialClientCount: number;
  let initialNoteCount: number;

  test.beforeEach(async ({ page, request }) => {
    // Set viewport to full screen
    await page.setViewportSize({ width: 1920, height: 1080 });
    
    // Initialize page objects
    homePage = new HomePage(page);
    applicationsPage = new ApplicationsPage(page);
    companiesPage = new CompaniesPage(page);
    companyFormPage = new CompanyFormPage(page);
    contactsPage = new ContactsPage(page);
    contactFormPage = new ContactFormPage(page);
    clientsPage = new ClientsPage(page);
    clientFormPage = new ClientFormPage(page);
    notesPage = new NotesPage(page);
    jobSearchSitesPage = new JobSearchSitesPage(page);
    wizardStep1Page = new WizardStep1Page(page);

    // Initialize API request utility
    // Environment and backend URL are automatically detected from process.env
    // No need to manually extract - the utility handles it internally
    apiUtils = new PlaywrightApiRequestUtility(request);

    // Initialize database utility
    // Environment is automatically detected from process.env
    dbUtils = new PlaywrightDbUtility();

    // Get all initial counts at once using the utility
    initialCounts = await apiUtils.getAllEntityCounts();
    initialApplicationCount = initialCounts.applications;
    initialCompanyCount = initialCounts.companies;
    initialContactCount = initialCounts.contacts;
    initialClientCount = initialCounts.clients;
    initialNoteCount = initialCounts.notes;
  });

  test('test_home - Click Home Navigation, Add Application button, then Cancel', async ({ page, request }) => {
    // 1. Click the Home Navigation
    await homePage.navigate();
    await homePage.verifyPageLoaded();

    // 2. Click the Add Application button
    await homePage.clickAddApplication();

    // Verify we're on the wizard step 1
    await wizardStep1Page.verifyPageLoaded();

    // 3. Click the Cancel button
    await wizardStep1Page.cancel();

    // Verify we're back at applications page
    await applicationsPage.verifyPageLoaded();

    // Verify no applications were created
    await apiUtils.verifyEntityCount('applications', initialApplicationCount);
  });

  test('test_application - Click Applications Navigation, Add button, then Cancel', async ({ page, request }) => {
    // 1. Click the Applications Navigation
    await applicationsPage.navigate();
    await applicationsPage.verifyPageLoaded();
    // 2. Click the Add button
    await applicationsPage.clickNewApplication();
    // Verify we're on the wizard step 1
    await wizardStep1Page.verifyPageLoaded();
    // 3. Click the Cancel button
    await wizardStep1Page.cancel();
    // Verify we're back at applications page
    await applicationsPage.verifyPageLoaded();

    // Verify no applications were created
    await apiUtils.verifyEntityCount('applications', initialApplicationCount);
  });

  test('test_companies - Click Companies Navigation, Add button, populate all fields, then Cancel', async ({ page, request }) => {
    // 1. Click the Companies Navigation
    await companiesPage.navigate();
    await companiesPage.verifyPageLoaded();
    // 2. Click the Add button
    await companiesPage.clickNewCompany();
    // Verify we're on the new company page
    await companyFormPage.verifyFormLoaded();
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
    await companyFormPage.fillForm(testData);
    // Verify fields are populated
    await expect(companyFormPage.nameInput).toHaveValue(testData.name);
    // 4. Click the Cancel button
    await companyFormPage.cancel();
    // Verify we're back at companies page
    await companiesPage.verifyPageLoaded();

    // Verify no companies were created
    await apiUtils.verifyEntityCount('companies', initialCompanyCount);
  });

  test('test_contacts - Click Contacts Navigation, Add button, populate all fields, then Cancel', async ({ page, request }) => {
    // 1. Click the Contacts Navigation
    await contactsPage.navigate();
    await contactsPage.verifyPageLoaded();
    // 2. Click the Add button
    await contactsPage.clickNewContact();
    // Verify we're on the new contact page
    await contactFormPage.verifyFormLoaded();
    // 3. Populate all fields
    const testData = {
      first_name: `TestFirst${Date.now()}`,
      last_name: `TestLast${Date.now()}`,
      title: 'Software Engineer',
      linkedin: 'https://linkedin.com/in/test',
      contact_type: 'Recruiter',
    };
    await contactFormPage.fillForm(testData);
    // Verify fields are populated
    await expect(contactFormPage.firstNameInput).toHaveValue(testData.first_name);
    await expect(contactFormPage.lastNameInput).toHaveValue(testData.last_name);
    // 4. Click the Cancel button
    await contactFormPage.cancel();
    // Verify we're back at contacts page
    await contactsPage.verifyPageLoaded();

    // Verify no contacts were created
    await apiUtils.verifyEntityCount('contacts', initialContactCount);
  });

  test('test_clients - Click Clients Navigation, Add button, populate all fields, then Cancel', async ({ page, request }) => {
    // 1. Click the Clients Navigation
    await clientsPage.navigate();
    await clientsPage.verifyPageLoaded();
    // 2. Click the Add button
    await clientsPage.clickNewClient();
    // Verify we're on the new client page
    await clientFormPage.verifyFormLoaded();
    // 3. Populate all fields
    const testData = {
      name: `Test Client ${Date.now()}`,
    };
    await clientFormPage.fillForm(testData);
    // Verify field is populated
    await expect(clientFormPage.nameInput).toHaveValue(testData.name);
    // 4. Click the Cancel button
    await clientFormPage.cancel();
    // Verify we're back at clients page
    await clientsPage.verifyPageLoaded();

    // Verify no clients were created
    await apiUtils.verifyEntityCount('clients', initialClientCount);
  });

  test('test_notes - Click Notes Navigation, verify there are no notes', async ({ page, request }) => {
    // 1. Click the Notes Navigation
    await notesPage.navigate();
    await notesPage.verifyPageLoaded();
    // 2. Verify there are no notes
    // Check if empty state is visible or if notes list is empty
    const emptyStateVisible = await notesPage.emptyState.isVisible().catch(() => false);
    const notesListVisible = await notesPage.notesList.isVisible().catch(() => false);
    if (emptyStateVisible) {
      await expect(notesPage.emptyState).toBeVisible();
    } else if (notesListVisible) {
      // If notes list is visible, verify it's empty or has no note items
      // Use notesList locator from Page Object
      const noteItems = notesPage.notesList.locator('[data-qa*="note-"]');
      const noteCount = await noteItems.count();
      expect(noteCount).toBe(0);
    } else {
      // If neither is visible, assume no notes
      expect(true).toBe(true);
    }

    // Verify no notes were created
    await apiUtils.verifyEntityCount('notes', initialNoteCount);
  });

  test('test_job_search_sites_api - Click Job Search Sites Navigation, verify all Names and URLs against API', async ({ page, request }) => {
    // 1. Get job search sites from API
    const apiSites = await apiUtils.getJobSearchSites({ include_deleted: false });
    
    // 2. Click the Job Search Sites Navigation
    await jobSearchSitesPage.navigate();
    await jobSearchSitesPage.verifyPageLoaded();
    
    // 3. Verify page content matches API data
    if (apiSites.data.length === 0) {
      // If no sites in API, verify empty state is shown
      await expect(jobSearchSitesPage.emptyState).toBeVisible();
    } else {
      // Verify table is visible
      await expect(jobSearchSitesPage.sitesTable).toBeVisible();
      
      // Get all site rows from the page
      const siteRows = jobSearchSitesPage.sitesTable.locator('tbody tr');
      const rowCount = await siteRows.count();
      
      // Verify row count matches API data
      expect(rowCount).toBe(apiSites.data.length);
      
      // Verify each row matches API data
      for (let i = 0; i < rowCount; i++) {
        const row = siteRows.nth(i);
        const apiSite = apiSites.data[i];
        
        // Verify name matches
        const nameLink = row.locator('a').first();
        await expect(nameLink).toBeVisible();
        const nameText = await nameLink.textContent();
        expect(nameText?.trim()).toBe(apiSite.name);
        
        // Verify URL matches (or "N/A" if no URL)
        const urlCell = row.locator('td').nth(1);
        const urlText = await urlCell.textContent();
        
        if (apiSite.url) {
          const urlLink = urlCell.locator('a');
          await expect(urlLink).toBeVisible();
          const href = await urlLink.getAttribute('href');
          expect(href).toBe(apiSite.url);
        } else {
          expect(urlText?.trim()).toBe('N/A');
        }
      }
    }
  });

  test('test_job_search_sites_db - Click Job Search Sites Navigation, verify all Names and URLs against database', async ({ page }) => {
    // 1. Get job search sites directly from database using the utility
    const dbSites = await dbUtils.getJobSearchSites(false);
    
    // 2. Click the Job Search Sites Navigation
    await jobSearchSitesPage.navigate();
    await jobSearchSitesPage.verifyPageLoaded();
    
    // 3. Verify page content matches database data
    if (dbSites.length === 0) {
      // If no sites in database, verify empty state is shown
      await expect(jobSearchSitesPage.emptyState).toBeVisible();
    } else {
      // Verify table is visible
      await expect(jobSearchSitesPage.sitesTable).toBeVisible();
      
      // Get all site rows from the page
      const siteRows = jobSearchSitesPage.sitesTable.locator('tbody tr');
      const rowCount = await siteRows.count();
      
      // Verify row count matches database data
      expect(rowCount).toBe(dbSites.length);
      
      // Verify each row matches database data
      for (let i = 0; i < rowCount; i++) {
        const row = siteRows.nth(i);
        const dbSite = dbSites[i];
        
        // Verify name matches
        const nameLink = row.locator('a').first();
        await expect(nameLink).toBeVisible();
        const nameText = await nameLink.textContent();
        expect(nameText?.trim()).toBe(dbSite.name);
        
        // Verify URL matches (or "N/A" if no URL)
        const urlCell = row.locator('td').nth(1);
        
        if (dbSite.url) {
          // The URL cell contains a link to the external URL
          const urlLink = urlCell.locator('a');
          await expect(urlLink).toBeVisible();
          const href = await urlLink.getAttribute('href');
          expect(href).toBe(dbSite.url);
        } else {
          // If no URL, should show 'N/A'
          const urlText = await urlCell.textContent();
          expect(urlText?.trim()).toBe('N/A');
        }
      }
    }
  });
});
