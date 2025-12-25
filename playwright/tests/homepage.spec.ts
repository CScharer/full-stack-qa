import { test, expect } from '@playwright/test';

/**
 * Simple test for the HomePage
 */
test.describe('HomePage', () => {
  test.beforeEach(async ({ page }) => {
    // Maximize browser window to full screen size
    await page.setViewportSize({ width: 1920, height: 1080 });
    // Navigate to home page
    await page.goto('/');
    // Wait for page to load
    await page.waitForLoadState('networkidle');
  });

  test('should load the home page', async ({ page }) => {
    // Verify the page title in the browser tab
    await expect(page).toHaveTitle(/Job Search Application/i);
    
    // Verify the page has loaded by checking for main content
    await expect(page.locator('body')).toBeVisible();
  });

  test('should display the navigation panel', async ({ page }) => {
    // Verify sidebar is visible
    await expect(page.locator('[data-qa="sidebar"]')).toBeVisible({ timeout: 10000 });
    
    // Verify navigation title is visible and contains "Navigation"
    const navigationTitle = page.locator('[data-qa="sidebar-title"]').filter({ hasText: 'Navigation' });
    await expect(navigationTitle).toBeVisible({ timeout: 10000 });
    
    // Verify navigation elements are present
    await expect(page.locator('[data-qa="sidebar-navigation"]')).toBeVisible({ timeout: 10000 });
    await expect(page.locator('[data-qa="sidebar-nav-home"]')).toBeVisible({ timeout: 10000 });
  });
});
