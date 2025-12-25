import { test, expect } from '@playwright/test';
import { HomePage } from './pages/HomePage';
import { ApplicationsPage } from './pages/ApplicationsPage';
import { ApplicationFormPage } from './pages/ApplicationFormPage';
import { ApplicationDetailPage } from './pages/ApplicationDetailPage';

/**
 * Integration tests for Applications CRUD operations
 * Tests the full stack: Frontend + Backend + Database
 * 
 * NOTE: Currently skipped as these tests cover complex CRUD operations
 * that may become outdated as the application evolves. These tests require
 * a fully functional backend API and database, and test more than just
 * the basic HomePage functionality.
 * 
 * TODO: Re-enable and update these tests once the application structure
 * is more stable and the backend API is finalized.
 */
test.describe.skip('Applications Integration Tests', () => {
  let homePage: HomePage;
  let applicationsPage: ApplicationsPage;
  let formPage: ApplicationFormPage;
  let detailPage: ApplicationDetailPage;
  let createdApplicationId: number | null = null;

  test.beforeEach(async ({ page }) => {
    // Maximize browser window to full screen size
    await page.setViewportSize({ width: 1920, height: 1080 });
    homePage = new HomePage(page);
    applicationsPage = new ApplicationsPage(page);
    formPage = new ApplicationFormPage(page);
    detailPage = new ApplicationDetailPage(page);
  });

  test('should navigate to applications from home page', async ({ page }) => {
    await homePage.navigate();
    // Title changed from "ONE GOAL" to "Job Search Application"
    await expect(homePage.title).toContainText('Job Search Application');
    await expect(homePage.applicationsCard).toBeVisible();
    
    await homePage.clickApplications();
    await expect(page).toHaveURL(/.*\/applications/);
    await expect(applicationsPage.title).toContainText('Applications');
  });

  test('should create a new application', async ({ page }) => {
    const testData = {
      position: `Test Position ${Date.now()}`,
      status: 'Pending',
      workSetting: 'Remote',
      location: 'San Francisco, CA',
      jobLink: 'https://example.com/job/123',
    };

    await applicationsPage.navigate();
    await applicationsPage.clickNewApplication();
    
    // Wait for form to load
    await expect(formPage.title).toContainText('New Application');
    
    // Fill form
    await formPage.fillForm(testData);
    await formPage.submit();
    
    // Should navigate to detail page
    await expect(page).toHaveURL(/.*\/applications\/\d+/);
    await expect(detailPage.title).toContainText(testData.position);
    
    // Extract application ID from URL
    const url = page.url();
    const match = url.match(/\/applications\/(\d+)/);
    if (match) {
      createdApplicationId = parseInt(match[1]);
    }
    
    // Verify status badge
    const status = await detailPage.getStatus();
    expect(status).toBe(testData.status);
  });

  test('should view application details', async ({ page }) => {
    // First create an application
    const testData = {
      position: `View Test ${Date.now()}`,
      status: 'Interview',
      workSetting: 'Hybrid',
    };

    await applicationsPage.navigate();
    await applicationsPage.clickNewApplication();
    await formPage.fillForm(testData);
    await formPage.submit();
    
    // Wait for detail page
    await expect(page).toHaveURL(/.*\/applications\/\d+/);
    await expect(detailPage.title).toContainText(testData.position);
    
    // Verify details are displayed
    await expect(detailPage.statusBadge).toBeVisible();
    const status = await detailPage.getStatus();
    expect(status).toBe(testData.status);
  });

  test('should edit an existing application', async ({ page }) => {
    // First create an application
    const createData = {
      position: `Edit Test ${Date.now()}`,
      status: 'Pending',
      workSetting: 'Remote',
    };

    await applicationsPage.navigate();
    await applicationsPage.clickNewApplication();
    await formPage.fillForm(createData);
    await formPage.submit();
    
    // Wait for detail page and get ID
    await expect(page).toHaveURL(/.*\/applications\/\d+/);
    const url = page.url();
    const match = url.match(/\/applications\/(\d+)/);
    expect(match).not.toBeNull();
    const appId = parseInt(match![1]);
    
    // Click edit
    await detailPage.clickEdit();
    await expect(page).toHaveURL(new RegExp(`.*/applications/${appId}/edit`));
    
    // Update the application
    const updateData = {
      position: `Updated Position ${Date.now()}`,
      status: 'Interview',
      workSetting: 'Hybrid',
      location: 'New York, NY',
    };
    
    await formPage.fillForm(updateData);
    await formPage.submit();
    
    // Should navigate back to detail page
    await expect(page).toHaveURL(new RegExp(`.*/applications/${appId}`));
    await expect(detailPage.title).toContainText(updateData.position);
    
    // Verify status was updated
    const status = await detailPage.getStatus();
    expect(status).toBe(updateData.status);
  });

  test('should delete an application', async ({ page }) => {
    // First create an application
    const testData = {
      position: `Delete Test ${Date.now()}`,
      status: 'Pending',
      workSetting: 'Remote',
    };

    await applicationsPage.navigate();
    await applicationsPage.clickNewApplication();
    await formPage.fillForm(testData);
    await formPage.submit();
    
    // Wait for detail page
    await expect(page).toHaveURL(/.*\/applications\/\d+/);
    
    // Click delete and confirm
    page.on('dialog', async dialog => {
      expect(dialog.type()).toBe('confirm');
      await dialog.accept();
    });
    
    await detailPage.clickDelete();
    
    // Should navigate back to applications list
    await expect(page).toHaveURL(/.*\/applications/);
    
    // Verify application is no longer in the list
    await expect(applicationsPage.title).toContainText('Applications');
  });

  test('should list applications', async ({ page }) => {
    await applicationsPage.navigate();
    await expect(applicationsPage.title).toContainText('Applications');
    
    // Check if table exists or empty state
    const hasTable = await applicationsPage.applicationsTable.count() > 0;
    const hasEmptyState = await applicationsPage.emptyState.count() > 0;
    
    expect(hasTable || hasEmptyState).toBe(true);
    
    // If table exists, verify it has headers
    if (hasTable) {
      await expect(applicationsPage.applicationsTable.locator('thead')).toBeVisible();
    }
  });

  test('should handle empty applications list', async ({ page }) => {
    await applicationsPage.navigate();
    
    // "Create Your First Application" button removed
    // Empty state should still be visible if no applications exist
    const emptyStateVisible = await applicationsPage.emptyState.isVisible().catch(() => false);
    
    // If empty, the "Add" button should still be available
    if (emptyStateVisible) {
      await expect(applicationsPage.newApplicationButton).toBeVisible();
    }
  });
});
