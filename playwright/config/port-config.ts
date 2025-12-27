/**
 * Environment Configuration
 * Single source of truth for all environment configuration
 * 
 * This file reads from config/environments.json to ensure consistency
 * between shell scripts and TypeScript/JavaScript code.
 * 
 * Usage:
 *   import { getPortsForEnvironment, getEnvironmentConfig } from './config/port-config';
 *   const ports = getPortsForEnvironment('dev');
 *   const config = getEnvironmentConfig('dev');
 *   console.log(ports.frontend.port); // 3003
 *   console.log(config.database.name); // "full_stack_qa_dev.db"
 */

import envConfig from '../../config/environments.json';
import portConfig from '../../config/ports.json';  // Fallback for backward compatibility

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

export interface DatabaseConfig {
  name: string;
  path: string;
}

export interface EnvironmentConfig {
  frontend: {
    port: number;
    url: string;
  };
  backend: {
    port: number;
    url: string;
  };
  database: DatabaseConfig;
  corsOrigins: string[];
}

export interface ApiConfig {
  basePath: string;
  healthEndpoint: string;
  docsEndpoint: string;
  redocEndpoint: string;
}

export interface TimeoutConfig {
  serviceStartup: number;
  serviceVerification: number;
  apiClient: number;
  webServer: number;
  checkInterval: number;
}

export interface FullConfig {
  api: ApiConfig;
  database: {
    directory: string;
    schemaDatabase: string;
    namingPattern: string;
  };
  timeouts: TimeoutConfig;
  environments: Record<string, EnvironmentConfig>;
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
  
  // Try environments.json first (comprehensive config)
  if (envConfig.environments && env in envConfig.environments) {
    const envData = envConfig.environments[env];
    return {
      frontend: envData.frontend,
      backend: envData.backend,
    };
  }
  
  // Fallback to ports.json (backward compatibility)
  if (env in portConfig) {
    return portConfig[env];
  }
  
  console.warn(`⚠️  Unknown environment: ${environment}, defaulting to ${defaultEnv}`);
  return portConfig[defaultEnv];
}

/**
 * Get full environment configuration (ports, database, CORS, etc.)
 * @param environment - Environment name (dev, test, prod)
 * @param defaultEnv - Default environment if invalid (defaults to 'dev')
 * @returns Full environment configuration
 */
export function getEnvironmentConfig(
  environment: string = 'dev',
  defaultEnv: Environment = 'dev'
): EnvironmentConfig {
  const env = (environment || defaultEnv).toLowerCase() as Environment;
  
  if (envConfig.environments && env in envConfig.environments) {
    return envConfig.environments[env];
  }
  
  console.warn(`⚠️  Unknown environment: ${environment}, defaulting to ${defaultEnv}`);
  return envConfig.environments[defaultEnv];
}

/**
 * Get API configuration
 */
export function getApiConfig(): ApiConfig {
  return envConfig.api;
}

/**
 * Get timeout configuration
 */
export function getTimeoutConfig(): TimeoutConfig {
  return envConfig.timeouts;
}

/**
 * Get database configuration
 */
export function getDatabaseConfig(): { directory: string; schemaDatabase: string; namingPattern: string } {
  return envConfig.database;
}

/**
 * Get all port configurations (backward compatibility)
 */
export function getAllPortConfigs(): Record<Environment, PortConfig> {
  const result: Record<string, PortConfig> = {};
  for (const env in envConfig.environments) {
    const envData = envConfig.environments[env];
    result[env] = {
      frontend: envData.frontend,
      backend: envData.backend,
    };
  }
  return result as Record<Environment, PortConfig>;
}

/**
 * Get full configuration object
 */
export function getFullConfig(): FullConfig {
  return envConfig;
}

