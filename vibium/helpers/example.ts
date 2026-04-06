// Example demonstrating the Vibium browser automation framework.
// Do not import "vibium" at module load time: the npm package may omit dist/ until
// postinstall/platform binaries run, and Vitest fails resolving the package entry.
// Real API paths use dynamic import(); mock paths avoid the package entirely.

// Type definitions matching the actual Vibium API
interface VibiumElement {
  click(options?: { timeout?: number }): Promise<void>;
  type(text: string, options?: { timeout?: number }): Promise<void>;
  text(): Promise<string>;
}

interface VibiumElementSync {
  click(options?: { timeout?: number }): void;
  type(text: string, options?: { timeout?: number }): void;
  text(): string;
}

interface VibiumBrowser {
  go(url: string): Promise<void>;
  find(selector: string, options?: { timeout?: number }): Promise<VibiumElement>;
  screenshot(): Promise<Buffer>;
  quit(): Promise<void>;
}

interface VibiumBrowserSync {
  go(url: string): void;
  find(selector: string, options?: { timeout?: number }): VibiumElementSync;
  screenshot(): Buffer;
  quit(): void;
}

/**
 * Demonstrates the async API using the real Vibium package
 */
async function asyncAPIHandled(): Promise<void> {
  try {
    console.log('\n📝 Running asyncAPI() with real Vibium package...\n');

    const { browser } = await import("vibium");
    // Launch browser (headless by default in tests)
    const vibe = await browser.launch({ headless: true });
    
    try {
      await vibe.go("https://example.com");
      
      // Find an element (this will wait for it to exist)
      // Note: example.com may not have a button.submit, so we'll use a more common selector
      const link = await vibe.find("a", { timeout: 5000 });
      const linkText = await link.text();
      console.log(`✅ Found link with text: "${linkText}"`);
      
      // Take a screenshot
      const screenshot = await vibe.screenshot();
      console.log(`📸 Screenshot captured: ${screenshot.length} bytes`);
      
      console.log('✅ asyncAPI() completed successfully!\n');
    } finally {
      // Always quit the browser
      await vibe.quit();
    }
  } catch (error) {
    const err = error as Error;
    console.error('❌ Error in asyncAPI():', err.message);
    // Re-throw to let tests handle it
    throw error;
  }
}

/**
 * Demonstrates the sync API using the real Vibium package
 */
async function syncAPIHandled(): Promise<void> {
  try {
    console.log('\n📝 Running syncAPI() with real Vibium package...\n');

    const { browserSync } = await import("vibium");
    // Launch browser synchronously
    const vibe = browserSync.launch({ headless: true });
    
    try {
      vibe.go("https://example.com");
      
      // Find an element synchronously
      const link = vibe.find("a", { timeout: 5000 });
      const linkText = link.text();
      console.log(`✅ Found link with text: "${linkText}"`);
      
      // Take a screenshot synchronously
      const screenshot = vibe.screenshot();
      console.log(`📸 Screenshot captured: ${screenshot.length} bytes`);
      
      console.log('✅ syncAPI() completed successfully!\n');
    } finally {
      // Always quit the browser
      vibe.quit();
    }
  } catch (error) {
    const err = error as Error;
    console.error('❌ Error in syncAPI():', err.message);
    // Re-throw to let tests handle it
    throw error;
  }
}

// Mock browserSync object for testing without actual browser
// This is useful for unit tests that don't need a real browser
const mockBrowserSync = {
  launch: async function(): Promise<VibiumBrowser> {
    console.log('🔵 [MOCK] Launching browser...');
    return {
      go: async function(url: string): Promise<void> {
        console.log(`🔵 [MOCK] Navigating to: ${url}`);
      },
      find: async function(selector: string): Promise<VibiumElement> {
        console.log(`🔵 [MOCK] Finding element: ${selector}`);
        return {
          click: async function(): Promise<void> {
            console.log(`🔵 [MOCK] Clicking element: ${selector}`);
          },
          type: async function(text: string): Promise<void> {
            console.log(`🔵 [MOCK] Typing text: "${text}"`);
          },
          text: async function(): Promise<string> {
            return `[MOCK] Text for ${selector}`;
          }
        };
      },
      screenshot: async function(): Promise<Buffer> {
        console.log('🔵 [MOCK] Taking screenshot...');
        return Buffer.from('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==');
      },
      quit: async function(): Promise<void> {
        console.log('🔵 [MOCK] Closing browser...');
      }
    };
  }
};

/**
 * Mock async API for testing without browser
 */
async function asyncAPIMocked(): Promise<void> {
  try {
    console.log('\n📝 Running asyncAPI() with mock implementation...\n');
    const vibe = await mockBrowserSync.launch();
    await vibe.go("https://example.com");

    const el = await vibe.find("button.submit");
    await el.click();
    await el.type("hello");
    const text = await el.text();
    console.log(`📝 Element text: ${text}`);

    const png = await vibe.screenshot();
    console.log(`📸 Screenshot data length: ${png.length} bytes`);
    await vibe.quit();
    console.log('\n✅ asyncAPI() completed successfully!\n');
  } catch (error) {
    console.error('❌ Error in asyncAPI():', error);
    throw error;
  }
}

/**
 * Mock sync API for testing without browser
 */
async function syncAPIMocked(): Promise<void> {
  // Note: Even mock sync API needs to be async in this context
  console.log('\n📝 Running syncAPI() with mock implementation...\n');
  const vibe = await mockBrowserSync.launch();
  await vibe.go("https://example.com");

  const el = await vibe.find("button.submit");
  await el.click();
  await el.type("hello");
  const text = await el.text();
  console.log(`📝 Element text: ${text}`);

  const png = await vibe.screenshot();
  console.log(`📸 Screenshot data length: ${png.length} bytes`);
  await vibe.quit();
  console.log('\n✅ syncAPI() completed successfully!\n');
}

/**
 * Smoke log for tests that assert this helper loads without a static `vibium` import.
 * Kept for backward compatibility with tests.
 */
function handlePlaceholder(): void {
  console.log(
    '✅ Vibium is declared as an npm dependency (^26.x; see vibium/package.json).'
  );
  console.log(
    '📦 Real browser examples load `vibium` via dynamic import when dist and the clicker binary are available.\n'
  );
}

export {
  asyncAPIHandled,
  asyncAPIMocked,
  syncAPIHandled,
  syncAPIMocked,
  handlePlaceholder,
  type VibiumElement,
  type VibiumElementSync,
  type VibiumBrowser,
  type VibiumBrowserSync
};
