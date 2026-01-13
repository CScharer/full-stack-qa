/**
 * Environment Configuration (DEPRECATED - Use shared config)
 * 
 * This file is kept for backward compatibility but now re-exports from
 * the shared config/port-config.ts to ensure consistency across all frameworks.
 * 
 * @deprecated Import directly from '../../config/port-config' instead
 * 
 * Usage (new):
 *   import { getPortsForEnvironment, getEnvironmentConfig } from '../../config/port-config';
 * 
 * Usage (old - still works):
 *   import { getPortsForEnvironment, getEnvironmentConfig } from './config/port-config';
 */

// Re-export everything from the shared config
export * from '../../config/port-config';
