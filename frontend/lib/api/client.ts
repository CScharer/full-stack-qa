/**
 * API Client for ONE GOAL Frontend
 * Handles all API communication with the backend
 */
import axios, { AxiosInstance, AxiosError } from 'axios';

// Note: API timeout is configured in config/environments.json (apiClient: 10000ms)
// This value should match the centralized configuration
const API_TIMEOUT = 10000; // 10 seconds (from config/environments.json)

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8003/api/v1';

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
