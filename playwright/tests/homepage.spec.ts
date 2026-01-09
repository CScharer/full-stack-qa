import { test } from '@playwright/test';
import { HomePage } from './pages/HomePage';

/**
 * HomePage tests using Page Object Model
 * Uses HomePage page object with data-qa selectors
 */
test.describe('HomePage', () => {
  let homePage: HomePage;

  test.beforeEach(async ({ page }) => {
    homePage = new HomePage(page);
    // Maximize browser window to full screen size
    await homePage.setViewport(1920, 1080);
    // Navigate to home page
    await homePage.navigate();
  });

  test('should load the home page', async () => {
    await homePage.verifyPageLoaded();
  });

  test('should display the navigation panel', async () => {
    await homePage.verifySidebarVisible();
    await homePage.verifyNavigationTitle('Navigation');
    await homePage.verifyNavigationElements();
  });
});
