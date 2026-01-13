import '@testing-library/jest-dom';
import { cleanup } from '@testing-library/react';
import { afterEach, vi, beforeAll } from 'vitest';

// Set consistent locale for tests to ensure toLocaleTimeString() produces consistent output
// This ensures snapshot tests are stable across different environments
if (typeof process !== 'undefined') {
  process.env.TZ = 'UTC';
  process.env.LC_ALL = 'en_US.UTF-8';
}

// Mock Date.prototype.toLocaleTimeString to return a consistent value for snapshot tests
// This ensures timestamps in StatusBar are consistent across all environments
beforeAll(() => {
  const originalToLocaleTimeString = Date.prototype.toLocaleTimeString;
  Date.prototype.toLocaleTimeString = function (this: Date) {
    // Return a fixed time format: "12:00:00 PM" (UTC time for 2026-01-11T12:00:00Z)
    // This matches the mocked date used in snapshot tests
    const hours = this.getUTCHours();
    const minutes = this.getUTCMinutes().toString().padStart(2, '0');
    const seconds = this.getUTCSeconds().toString().padStart(2, '0');
    const ampm = hours >= 12 ? 'PM' : 'AM';
    const displayHours = hours % 12 || 12;
    return `${displayHours}:${minutes}:${seconds} ${ampm}`;
  };
  
  // Restore original after all tests
  return () => {
    Date.prototype.toLocaleTimeString = originalToLocaleTimeString;
  };
});

// Cleanup after each test
afterEach(() => {
  cleanup();
});
