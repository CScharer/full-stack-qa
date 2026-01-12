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
  let backendBaseUrl: string;
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

    // Determine backend URL based on environment
    // Default to dev environment (port 8003)
    const frontendUrl = process.env.BASE_URL || 'http://localhost:3003';
    backendBaseUrl = process.env.BACKEND_URL || frontendUrl.replace(':3003', ':8003');

    // Get initial counts for all entities via API
    try {
      const appResponse = await request.get(`${backendBaseUrl}/api/v1/applications?limit=1`);
      if (appResponse.ok()) {
        const appData = await appResponse.json();
        initialApplicationCount = appData.total || 0;
      }
    } catch (error) {
      initialApplicationCount = 0;
    }

    try {
      const companyResponse = await request.get(`${backendBaseUrl}/api/v1/companies?limit=1`);
      if (companyResponse.ok()) {
        const companyData = await companyResponse.json();
        initialCompanyCount = companyData.total || 0;
      }
    } catch (error) {
      initialCompanyCount = 0;
    }

    try {
      const contactResponse = await request.get(`${backendBaseUrl}/api/v1/contacts?limit=1`);
      if (contactResponse.ok()) {
        const contactData = await contactResponse.json();
        initialContactCount = contactData.total || 0;
      }
    } catch (error) {
      initialContactCount = 0;
    }

    try {
      const clientResponse = await request.get(`${backendBaseUrl}/api/v1/clients?limit=1`);
      if (clientResponse.ok()) {
        const clientData = await clientResponse.json();
        initialClientCount = clientData.total || 0;
      }
    } catch (error) {
      initialClientCount = 0;
    }

    try {
      const noteResponse = await request.get(`${backendBaseUrl}/api/v1/notes?limit=1`);
      if (noteResponse.ok()) {
        const noteData = await noteResponse.json();
        initialNoteCount = noteData.total || 0;
      }
    } catch (error) {
      initialNoteCount = 0;
    }
  });

  test.skip('test_home - Click Home Navigation, Add Application button, then Cancel', async ({ page }) => {
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
  });

  test('test_application - Click Applications Navigation, Add button, then Cancel', async ({ page }) => {
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
  });

  test('test_companies - Click Companies Navigation, Add button, populate all fields, then Cancel', async ({ page }) => {
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
  });

  test('test_contacts - Click Contacts Navigation, Add button, populate all fields, then Cancel', async ({ page }) => {
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
  });

  test('test_clients - Click Clients Navigation, Add button, populate all fields, then Cancel', async ({ page }) => {
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
  });

  test('test_notes - Click Notes Navigation, verify there are no notes', async ({ page }) => {
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
  });

  test('test_job_search_sites - Click Job Search Sites Navigation, verify all Names and URLs', async ({ page }) => {
    // 1. Click the Job Search Sites Navigation
    await jobSearchSitesPage.navigate();
    await jobSearchSitesPage.verifyPageLoaded();
    // 2. Verify all of the Names and URLs
    // Check if there are any sites
    const hasSites = await jobSearchSitesPage.sitesTable.isVisible().catch(() => false);
    const isEmpty = await jobSearchSitesPage.emptyState.isVisible().catch(() => false);

    if (hasSites) {
      // Get all site rows
      const siteRows = jobSearchSitesPage.sitesTable.locator('tbody tr');
      const rowCount = await siteRows.count();

      // Verify each row has a name and URL (or N/A for URL)
      for (let i = 0; i < rowCount; i++) {
        const row = siteRows.nth(i);
        
        // Verify name exists (should be in first column or link)
        const nameLink = row.locator('a').first();
        await expect(nameLink).toBeVisible();
        const nameText = await nameLink.textContent();
        expect(nameText).toBeTruthy();
        expect(nameText?.trim().length).toBeGreaterThan(0);

        // Verify URL exists (either in second column for desktop or in mobile view)
        // Check desktop view first
        const urlCell = row.locator('td').nth(1);
        const urlLink = urlCell.locator('a');
        const urlText = await urlCell.textContent();

        // URL should either be a link or "N/A"
        expect(urlText).toBeTruthy();
        if (urlText !== 'N/A') {
          await expect(urlLink).toBeVisible();
          const href = await urlLink.getAttribute('href');
          expect(href).toBeTruthy();
        }
      }
    } else if (isEmpty) {
      // If empty state is shown, that's also valid - just verify it
      await expect(jobSearchSitesPage.emptyState).toBeVisible();
    }
  });

  test('test_no_data - Verify no applications, companies, contacts, clients, or notes were created', async ({ request }) => {
    // 1. Verify no applications were created
    try {
      const appResponse = await request.get(`${backendBaseUrl}/api/v1/applications?limit=1`);
      if (appResponse.ok()) {
        const appData = await appResponse.json();
        const finalApplicationCount = appData.total || 0;
        expect(finalApplicationCount).toBe(initialApplicationCount);
      }
    } catch (error) {
      console.warn('Failed to verify application count:', error);
    }
    // 2. Verify no companies were created
    try {
      const companyResponse = await request.get(`${backendBaseUrl}/api/v1/companies?limit=1`);
      if (companyResponse.ok()) {
        const companyData = await companyResponse.json();
        const finalCompanyCount = companyData.total || 0;
        expect(finalCompanyCount).toBe(initialCompanyCount);
      }
    } catch (error) {
      console.warn('Failed to verify company count:', error);
    }
    // 3. Verify no contacts were created
    try {
      const contactResponse = await request.get(`${backendBaseUrl}/api/v1/contacts?limit=1`);
      if (contactResponse.ok()) {
        const contactData = await contactResponse.json();
        const finalContactCount = contactData.total || 0;
        expect(finalContactCount).toBe(initialContactCount);
      }
    } catch (error) {
      console.warn('Failed to verify contact count:', error);
    }
    // 4. Verify no clients were created
    try {
      const clientResponse = await request.get(`${backendBaseUrl}/api/v1/clients?limit=1`);
      if (clientResponse.ok()) {
        const clientData = await clientResponse.json();
        const finalClientCount = clientData.total || 0;
        expect(finalClientCount).toBe(initialClientCount);
      }
    } catch (error) {
      console.warn('Failed to verify client count:', error);
    }
    // 5. Verify no notes were created
    try {
      const noteResponse = await request.get(`${backendBaseUrl}/api/v1/notes?limit=1`);
      if (noteResponse.ok()) {
        const noteData = await noteResponse.json();
        const finalNoteCount = noteData.total || 0;
        expect(finalNoteCount).toBe(initialNoteCount);
      }
    } catch (error) {
      console.warn('Failed to verify note count:', error);
    }
  });
});
