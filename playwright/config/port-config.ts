/**
 * Port Configuration
 * Single source of truth for port assignments across all environments
 * 
 * This file is generated from config/ports.json to ensure consistency
 * between shell scripts and TypeScript/JavaScript code.
 * 
 * Usage:
 *   import { getPortsForEnvironment } from './config/port-config';
 *   const ports = getPortsForEnvironment('dev');
 *   console.log(ports.frontend.port); // 3003
 */

import portConfig from '../../config/ports.json';

export interface PortConfig {
  frontend: {
    port: number;
    url: string;
  };
  backend: {
    port: number;
    url: string;
  };
}

export type Environment = 'dev' | 'test' | 'prod';

/**
 * Get port configuration for a specific environment
 * @param environment - Environment name (dev, test, prod)
 * @param defaultEnv - Default environment if invalid (defaults to 'dev')
 * @returns Port configuration for the environment
 */
export function getPortsForEnvironment(
  environment: string = 'dev',
  defaultEnv: Environment = 'dev'
): PortConfig {
  const env = (environment || defaultEnv).toLowerCase() as Environment;
  
  if (env in portConfig) {
    return portConfig[env];
  }
  
  console.warn(`⚠️  Unknown environment: ${environment}, defaulting to ${defaultEnv}`);
  return portConfig[defaultEnv];
}

/**
 * Get all port configurations
 */
export function getAllPortConfigs(): Record<Environment, PortConfig> {
  return portConfig;
}

