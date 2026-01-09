import { Page, Locator } from '@playwright/test';
import { expect } from '@playwright/test';

/**
 * Base Page Object class
 * Contains common methods and properties shared across all pages
 */
export class BasePage {
  readonly page: Page;

  constructor(page: Page) {
    this.page = page;
  }

  /**
   * Navigate to a URL
   * @param path - Relative path from base URL (e.g., '/', '/applications')
   */
  async navigate(path: string = '/'): Promise<void> {
    await this.page.goto(path);
    await this.waitForPageLoad();
  }

  /**
   * Wait for page to load
   * Waits for network to be idle and body to be visible
   */
  async waitForPageLoad(): Promise<void> {
    await this.page.waitForLoadState('networkidle');
    await this.page.locator('body').waitFor({ state: 'visible' });
  }

  /**
   * Get page title
   */
  async getTitle(): Promise<string> {
    return await this.page.title();
  }

  /**
   * Verify page title contains text
   * @param text - Text to verify in title
   */
  async verifyTitleContains(text: string | RegExp): Promise<void> {
    await this.page.waitForLoadState('networkidle');
    if (typeof text === 'string') {
      await expect(this.page).toHaveTitle(new RegExp(text, 'i'));
    } else {
      await expect(this.page).toHaveTitle(text);
    }
  }

  /**
   * Set viewport size
   * @param width - Viewport width (default: 1920)
   * @param height - Viewport height (default: 1080)
   */
  async setViewport(width: number = 1920, height: number = 1080): Promise<void> {
    await this.page.setViewportSize({ width, height });
  }

  /**
   * Wait for element to be visible
   * @param locator - Element locator
   * @param timeout - Timeout in milliseconds (default: 10000)
   */
  async waitForVisible(locator: Locator, timeout: number = 10000): Promise<void> {
    await locator.waitFor({ state: 'visible', timeout });
  }
}
