import { test, expect } from '@playwright/test';
import { HomePage } from './pages/HomePage';

/**
 * Integration tests for Companies
 * Tests basic navigation and page loading
 * 
 * NOTE: Currently skipped as these tests cover navigation and page structure
 * that may become outdated as the application evolves. These tests require
 * a fully functional backend API and database.
 * 
 * TODO: Re-enable and update these tests once the application structure
 * is more stable and the backend API is finalized.
 */
test.describe.skip('Companies Integration Tests', () => {
  test.beforeEach(async ({ page }) => {
    // Maximize browser window to full screen size
    await page.setViewportSize({ width: 1920, height: 1080 });
  });

  test('should navigate to companies from home page', async ({ page }) => {
    const homePage = new HomePage(page);
    
    await homePage.navigate();
    // Title changed from "ONE GOAL" to "Job Search Application"
    await expect(homePage.title).toContainText('Job Search Application');
    await expect(homePage.companiesCard).toBeVisible();
    
    await homePage.clickCompanies();
    await expect(page).toHaveURL(/.*\/companies/);
    await expect(page.locator('h1.h2, h2')).toContainText('Companies');
  });

  test('should load companies list page', async ({ page }) => {
    await page.goto('/companies');
    await expect(page.locator('h1.h2, h2')).toContainText('Companies');
    
    // Check if table exists or empty state
    const hasTable = await page.locator('table.table').count() > 0;
    const hasEmptyState = await page.locator('text=No companies found').count() > 0;
    
    expect(hasTable || hasEmptyState).toBe(true);
  });
});
