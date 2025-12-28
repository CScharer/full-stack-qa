import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    globals: true,
    environment: 'node',
    include: ['tests/**/*.spec.ts'],
    // Output test results as JSON for Allure conversion
    reporters: ['verbose', ['json', { outputFile: './test-results/vitest-results.json' }]],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'tests/',
        '*.config.ts',
        'types/'
      ]
    }
  }
});
