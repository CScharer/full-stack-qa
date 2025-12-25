/**
 * Test Data Loader for Playwright
 * 
 * Utility functions to load test data from the centralized test-data directory
 */

import * as fs from 'fs';
import * as path from 'path';

/**
 * Get the project root directory
 */
function getProjectRoot(): string {
  // Go up from playwright/tests/utils/ to project root
  return path.resolve(__dirname, '../../../');
}

/**
 * Load test data from JSON file
 * @param filePath - Relative path from test-data directory (e.g., 'demoqa/practice-form.json')
 * @returns Test data object
 */
export function loadTestData(filePath: string): any {
  const projectRoot = getProjectRoot();
  const fullPath = path.join(projectRoot, 'test-data', filePath);
  
  if (!fs.existsSync(fullPath)) {
    throw new Error(`Test data file not found: ${fullPath}`);
  }
  
  const content = fs.readFileSync(fullPath, 'utf-8');
  return JSON.parse(content);
}

/**
 * Load test data asynchronously
 * @param filePath - Relative path from test-data directory
 * @returns Promise with test data object
 */
export async function loadTestDataAsync(filePath: string): Promise<any> {
  const projectRoot = getProjectRoot();
  const fullPath = path.join(projectRoot, 'test-data', filePath);
  
  return new Promise((resolve, reject) => {
    fs.readFile(fullPath, 'utf-8', (err, content) => {
      if (err) {
        reject(new Error(`Failed to load test data from ${fullPath}: ${err.message}`));
      } else {
        try {
          resolve(JSON.parse(content));
        } catch (parseErr) {
          reject(new Error(`Failed to parse JSON from ${fullPath}: ${parseErr}`));
        }
      }
    });
  });
}
