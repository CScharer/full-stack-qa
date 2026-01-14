/**
 * API Client for ONE GOAL Frontend
 * Handles all API communication with the backend
 * 
 * Uses shared configuration from config/environments.json
 */
import axios, { AxiosInstance, AxiosError } from 'axios';

// Get environment from env var or default to 'dev'
const environment = (process.env.NEXT_PUBLIC_ENVIRONMENT || process.env.ENVIRONMENT || 'dev').toLowerCase();

// Read configuration directly from shared config JSON
// Note: We read JSON directly here because Next.js/Turbopack can't access files outside the frontend root
// The config is still the single source of truth (config/environments.json)
let API_TIMEOUT = 10000; // Default fallback

// Try to get API base path from config utility (works in Node.js/server-side)
let defaultApiBasePath = '/api/v1'; // Default fallback
try {
  // Only works server-side (Node.js), not in browser
  if (typeof window === 'undefined') {
    const { getApiBasePath } = require('../../../config/port-config');
    defaultApiBasePath = getApiBasePath();
  }
} catch (error) {
  // Config utility not available, use default
  // This is expected in browser context
}

let API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || `http://localhost:8003${defaultApiBasePath}`;

// Try to read from shared config (works at runtime, but not during Next.js build)
// For build-time, we'll use environment variables or defaults
if (typeof window === 'undefined') {
  // Server-side: can access filesystem
  try {
    const fs = require('fs');
    const path = require('path');
    const configPath = path.join(process.cwd(), '..', 'config', 'environments.json');
    if (fs.existsSync(configPath)) {
      const config = JSON.parse(fs.readFileSync(configPath, 'utf-8'));
      const envConfig = config.environments[environment] || config.environments['dev'];
      const apiConfig = config.api;
      const timeoutConfig = config.timeouts;
      
      API_TIMEOUT = timeoutConfig.apiClient;
      API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || `${envConfig.backend.url}${apiConfig.basePath}`;
    }
  } catch (error) {
    // Fallback to defaults if config can't be read
    console.warn('Could not read shared config, using defaults:', error);
  }
} else {
  // Client-side: use environment variables or defaults
  // The backend URL should be set via NEXT_PUBLIC_API_URL env var
  API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || API_BASE_URL;
}

/**
 * Create axios instance with base configuration
 */
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: API_TIMEOUT, // From config/environments.json
});

/**
 * Request interceptor
 * Add auth token or other headers if needed
 */
apiClient.interceptors.request.use(
  (config) => {
    // Add auth token if available
    // const token = localStorage.getItem('auth_token');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor
 * Handle errors globally
 */
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    // Handle different error types
    if (error.response) {
      // Server responded with error status
      const status = error.response.status;
      const data = error.response.data as any;
      
      switch (status) {
        case 400:
          console.error('Validation error:', data);
          break;
        case 404:
          console.error('Resource not found:', data);
          break;
        case 409:
          console.error('Conflict:', data);
          break;
        case 422:
          console.error('Unprocessable entity:', data);
          break;
        case 500:
          console.error('Server error:', data);
          break;
        default:
          console.error('API error:', data);
      }
    } else if (error.request) {
      // Request made but no response
      console.error('No response from server');
    } else {
      // Error setting up request
      console.error('Request error:', error.message);
    }
    
    return Promise.reject(error);
  }
);

export default apiClient;
