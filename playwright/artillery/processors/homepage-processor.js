/**
 * Artillery Processor for Homepage Load Testing
 * Uses Playwright to load the homepage and track Core Web Vitals
 * 
 * This processor can be extended to reuse existing Playwright page objects
 * from tests/integration/pages/HomePage.ts
 */

const { chromium } = require('playwright');

module.exports = {
  /**
   * Load homepage and track performance metrics
   * @param {Object} context - Artillery context with variables
   * @param {Object} events - Artillery events emitter
   * @param {Function} done - Callback to signal completion
   */
  async loadHomepage(context, events, done) {
    const browser = await chromium.launch({ 
      headless: true,
      args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
    const page = await browser.newPage();
    
    try {
      const baseUrl = context.vars.baseUrl || 'http://localhost:3003';
      const startTime = Date.now();
      
      // Navigate to homepage with timeout and fallback
      // Use 'domcontentloaded' instead of 'networkidle' for more reliable loading
      // 'networkidle' can timeout if there are long-polling connections or WebSocket connections
      await page.goto(baseUrl, { 
        waitUntil: 'domcontentloaded',
        timeout: 30000  // 30 second timeout
      });
      
      const loadTime = Date.now() - startTime;
      
      // Track Core Web Vitals with timeout
      const metrics = await Promise.race([
        page.evaluate(() => {
          return new Promise((resolve) => {
            const metrics = {};
            let metricsCollected = 0;
            const totalMetrics = 4; // LCP, FCP, CLS, FID
            
            const checkComplete = () => {
              metricsCollected++;
              if (metricsCollected >= totalMetrics) {
                resolve(metrics);
              }
            };
            
            // LCP (Largest Contentful Paint)
            try {
              new PerformanceObserver((list) => {
                const entries = list.getEntries();
                const lastEntry = entries[entries.length - 1];
                metrics.lcp = lastEntry.renderTime || lastEntry.loadTime;
                checkComplete();
              }).observe({ entryTypes: ['largest-contentful-paint'] });
            } catch (e) {
              checkComplete();
            }
            
            // FCP (First Contentful Paint)
            try {
              new PerformanceObserver((list) => {
                const entries = list.getEntries();
                if (entries.length > 0) {
                  metrics.fcp = entries[0].startTime;
                }
                checkComplete();
              }).observe({ entryTypes: ['paint'] });
            } catch (e) {
              checkComplete();
            }
            
            // CLS (Cumulative Layout Shift)
            try {
              let clsValue = 0;
              new PerformanceObserver((list) => {
                for (const entry of list.getEntries()) {
                  if (!entry.hadRecentInput) {
                    clsValue += entry.value;
                  }
                }
                metrics.cls = clsValue;
              }).observe({ entryTypes: ['layout-shift'] });
              // Wait a bit for CLS to stabilize
              setTimeout(() => {
                metrics.cls = clsValue;
                checkComplete();
              }, 2000);
            } catch (e) {
              checkComplete();
            }
            
            // FID (First Input Delay) - requires user interaction
            // For load testing, we'll track potential FID by measuring time to interactive
            try {
              const navigation = performance.getEntriesByType('navigation')[0];
              if (navigation) {
                metrics.timeToInteractive = navigation.domInteractive - navigation.fetchStart;
              }
              checkComplete();
            } catch (e) {
              checkComplete();
            }
            
            // Timeout fallback - resolve after 3 seconds max
            setTimeout(() => {
              resolve(metrics);
            }, 3000);
          });
        }),
        new Promise((resolve) => setTimeout(() => resolve({}), 3000)) // 3 second timeout
      ]);
      
      // Emit standard metrics
      events.emit('counter', 'page.loadTime', loadTime);
      events.emit('histogram', 'page.loadTime', loadTime);
      
      // Emit Core Web Vitals
      if (metrics.lcp) {
        events.emit('counter', 'webVitals.lcp', metrics.lcp);
        events.emit('histogram', 'webVitals.lcp', metrics.lcp);
      }
      if (metrics.fcp) {
        events.emit('counter', 'webVitals.fcp', metrics.fcp);
        events.emit('histogram', 'webVitals.fcp', metrics.fcp);
      }
      if (metrics.cls !== undefined) {
        events.emit('counter', 'webVitals.cls', metrics.cls);
        events.emit('histogram', 'webVitals.cls', metrics.cls);
      }
      if (metrics.timeToInteractive) {
        events.emit('counter', 'webVitals.timeToInteractive', metrics.timeToInteractive);
        events.emit('histogram', 'webVitals.timeToInteractive', metrics.timeToInteractive);
      }
      
      // Track success
      events.emit('counter', 'success.pageLoad', 1);
      
      await browser.close();
      if (typeof done === 'function') {
        done();
      }
    } catch (error) {
      // Track errors
      events.emit('counter', 'errors.pageLoad', 1);
      events.emit('counter', 'errors.total', 1);
      
      console.error('Error loading homepage:', error.message);
      
      try {
        await browser.close();
      } catch (closeError) {
        // Ignore close errors
      }
      
      if (typeof done === 'function') {
        done(error);
      }
    }
  }
};

