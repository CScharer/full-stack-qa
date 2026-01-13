/**
 * Tests for Job Search Sites page using Vitest and mock data
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, within } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import JobSearchSitesPage from '@/app/job-search-sites/page';
import { mockJobSearchSiteResponse } from '@/__mocks__/data';
import { getByQa } from '../utils/test-helpers';

// Mock the hooks
const mockUseJobSearchSites = vi.fn();
const mockUseDeleteJobSearchSite = vi.fn();

vi.mock('@/lib/hooks/use-job-search-sites', () => ({
  useJobSearchSites: () => mockUseJobSearchSites(),
  useDeleteJobSearchSite: () => mockUseDeleteJobSearchSite(),
}));

describe('JobSearchSitesPage', () => {
  let queryClient: QueryClient;

  beforeEach(() => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false },
      },
    });
    vi.clearAllMocks();
  });

  it('renders job search sites list', async () => {
    mockUseJobSearchSites.mockReturnValue({
      data: mockJobSearchSiteResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteJobSearchSite.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <JobSearchSitesPage />
      </QueryClientProvider>
    );

    await waitFor(() => {
      // Use data-qa for title
      expect(getByQa('job-search-sites-title')).toHaveTextContent('Job Search Sites');
      
      // Query within the job search sites table container
      const jobSearchSitesTable = getByQa('job-search-sites-table');
      expect(within(jobSearchSitesTable).getByText('LinkedIn')).toBeInTheDocument();
      expect(within(jobSearchSitesTable).getByText('Indeed')).toBeInTheDocument();
    });
  });

  it('renders loading state', () => {
    mockUseJobSearchSites.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <JobSearchSitesPage />
      </QueryClientProvider>
    );

    expect(screen.getByText(/loading job search sites/i)).toBeInTheDocument();
  });
});
