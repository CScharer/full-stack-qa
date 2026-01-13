/**
 * Snapshot tests for Job Search Sites page
 */
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import JobSearchSitesPage from '@/app/job-search-sites/page';
import { mockJobSearchSiteResponse } from '@/__mocks__/data';

const mockUseJobSearchSites = vi.fn();
const mockUseDeleteJobSearchSite = vi.fn();

vi.mock('@/lib/hooks/use-job-search-sites', () => ({
  useJobSearchSites: () => mockUseJobSearchSites(),
  useDeleteJobSearchSite: () => mockUseDeleteJobSearchSite(),
}));

describe('JobSearchSitesPage Snapshot Tests', () => {
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
    mockUseJobSearchSites.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <JobSearchSitesPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for job search sites list', () => {
    mockUseJobSearchSites.mockReturnValue({
      data: mockJobSearchSiteResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteJobSearchSite.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <JobSearchSitesPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for empty state', () => {
    mockUseJobSearchSites.mockReturnValue({
      data: { data: [], pagination: { page: 1, limit: 10, total: 0, pages: 1 } },
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteJobSearchSite.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <JobSearchSitesPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });
});
