/**
 * Snapshot tests for Applications page
 */
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import ApplicationsPage from '@/app/applications/page';
import { mockApplicationResponse } from '@/__mocks__/data';

const mockUseApplications = vi.fn();
const mockUseDeleteApplication = vi.fn();

vi.mock('@/lib/hooks/use-applications', () => ({
  useApplications: () => mockUseApplications(),
  useDeleteApplication: () => mockUseDeleteApplication(),
}));

describe('ApplicationsPage Snapshot Tests', () => {
  let queryClient: QueryClient;

  beforeEach(() => {
    // Mock Date to return a fixed timestamp for StatusBar
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-01-11T12:00:00Z'));
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false },
      },
    });
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('matches snapshot for loading state', () => {
    mockUseApplications.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ApplicationsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for error state', () => {
    mockUseApplications.mockReturnValue({
      data: undefined,
      isLoading: false,
      error: { message: 'Failed to load' },
      refetch: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ApplicationsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for applications list', () => {
    mockUseApplications.mockReturnValue({
      data: mockApplicationResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteApplication.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ApplicationsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for empty state', () => {
    mockUseApplications.mockReturnValue({
      data: { data: [], pagination: { page: 1, limit: 10, total: 0, pages: 1 } },
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteApplication.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ApplicationsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });
});
