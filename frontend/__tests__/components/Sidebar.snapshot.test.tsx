/**
 * Snapshot tests for Sidebar component
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render } from '@testing-library/react';
import { Sidebar } from '@/components/Sidebar';

const mockUsePathname = vi.fn(() => '/');

// Mock Next.js router
vi.mock('next/navigation', () => ({
  usePathname: () => mockUsePathname(),
}));

describe('Sidebar Snapshot Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockUsePathname.mockReturnValue('/');
  });

  it('matches snapshot for default sidebar (closed on mobile)', () => {
    const { container } = render(<Sidebar />);
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for sidebar with active home route', () => {
    mockUsePathname.mockReturnValue('/');
    const { container } = render(<Sidebar />);
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for sidebar with active applications route', () => {
    mockUsePathname.mockReturnValue('/applications');
    const { container } = render(<Sidebar />);
    expect(container).toMatchSnapshot();
  });
});
