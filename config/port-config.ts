/**
 * Shared Environment Configuration
 * Single source of truth for all environment configuration across all test frameworks
 * 
 * This file reads from config/environments.json to ensure consistency
 * between all test frameworks (Cypress, Playwright, Robot Framework, etc.).
 * 
 * Usage:
 *   // From Cypress
 *   import { getEnvironmentConfig, getBackendUrl } from '../../config/port-config';
 *   
 *   // From Playwright
 *   import { getEnvironmentConfig, getBackendUrl } from '../config/port-config';
 *   
 *   const config = getEnvironmentConfig('dev');
 *   const backendUrl = getBackendUrl('test');
 *   console.log(config.database.name); // "full_stack_qa_dev.db"
 */
// Import JSON files - paths are relative to this file's location (config/)
import envConfig from './environments.json';
import portConfig from './ports.json';  // Fallback for backward compatibility

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
 * Get backend URL for a specific environment
 * @param environment - Environment name (dev, test, prod)
 * @param defaultEnv - Default environment if invalid (defaults to 'dev')
 * @returns Backend URL
 */
export function getBackendUrl(
  environment: string = 'dev',
  defaultEnv: Environment = 'dev'
): string {
  const envConfig = getEnvironmentConfig(environment, defaultEnv);
  return envConfig.backend.url;
}

/**
 * Get frontend URL for a specific environment
 * @param environment - Environment name (dev, test, prod)
 * @param defaultEnv - Default environment if invalid (defaults to 'dev')
 * @returns Frontend URL
 */
export function getFrontendUrl(
  environment: string = 'dev',
  defaultEnv: Environment = 'dev'
): string {
  const envConfig = getEnvironmentConfig(environment, defaultEnv);
  return envConfig.frontend.url;
}

/**
 * Get API configuration
 */
export function getApiConfig(): ApiConfig {
  return envConfig.api;
}

/**
 * Get API base path (e.g., "/api/v1")
 * @returns API base path from config
 */
export function getApiBasePath(): string {
  return getApiConfig().basePath;
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
  const result: Partial<Record<Environment, PortConfig>> = {};
  // Iterate over known environments to ensure type safety
  const environments: Environment[] = ['dev', 'test', 'prod'];
  for (const env of environments) {
    if (env in envConfig.environments) {
      const envData = envConfig.environments[env as keyof typeof envConfig.environments];
      result[env] = {
        frontend: envData.frontend,
        backend: envData.backend,
      };
    }
  }
  return result as Record<Environment, PortConfig>;
}

/**
 * Get full configuration object
 */
export function getFullConfig(): FullConfig {
  return envConfig;
}
