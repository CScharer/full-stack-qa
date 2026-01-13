import '@testing-library/jest-dom';
import { cleanup } from '@testing-library/react';
import { afterEach } from 'vitest';

// Set consistent locale for tests to ensure toLocaleTimeString() produces consistent output
// This ensures snapshot tests are stable across different environments
if (typeof process !== 'undefined') {
  process.env.TZ = 'UTC';
  process.env.LC_ALL = 'en_US.UTF-8';
}

// Cleanup after each test
afterEach(() => {
  cleanup();
});
