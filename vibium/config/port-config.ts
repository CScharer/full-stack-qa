/**
 * Environment Configuration for Vibium
 * Uses shared config/environments.json
 * 
 * This file re-exports from the shared config/port-config.ts to ensure consistency
 * across all TypeScript frameworks (Cypress, Playwright, Vibium).
 * 
 * Usage:
 *   import { getEnvironmentConfig, getBackendUrl, getFrontendUrl } from './config/port-config';
 *   const config = getEnvironmentConfig('dev');
 *   const backendUrl = getBackendUrl('test');
 */
// Re-export everything from the shared config
export * from '../../config/port-config';
