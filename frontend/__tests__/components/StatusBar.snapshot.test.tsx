/**
 * Snapshot tests for StatusBar component
 */
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render } from '@testing-library/react';
import { StatusBar } from '@/components/StatusBar';

describe('StatusBar Snapshot Tests', () => {
  beforeEach(() => {
    // Mock Date to return a fixed timestamp
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-01-11T12:00:00Z'));
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('matches snapshot for default status bar', () => {
    const { container } = render(<StatusBar />);
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for status bar with custom message', () => {
    const { container } = render(<StatusBar message="Loading data..." />);
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for status bar with empty message', () => {
    const { container } = render(<StatusBar message="" />);
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for status bar with long message', () => {
    const { container } = render(
      <StatusBar message="This is a very long status message that might wrap to multiple lines" />
    );
    expect(container.firstChild).toMatchSnapshot();
  });
});
