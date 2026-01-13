/**
 * Tests for Companies page using Vitest and mock data
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, within } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import CompaniesPage from '@/app/companies/page';
import { mockCompanyResponse } from '@/__mocks__/data';
import { getByQa } from '../utils/test-helpers';

// Mock the hooks
const mockUseCompanies = vi.fn();
const mockUseDeleteCompany = vi.fn();

vi.mock('@/lib/hooks/use-companies', () => ({
  useCompanies: () => mockUseCompanies(),
  useDeleteCompany: () => mockUseDeleteCompany(),
}));

describe('CompaniesPage', () => {
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

  it('renders companies list', async () => {
    mockUseCompanies.mockReturnValue({
      data: mockCompanyResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteCompany.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <CompaniesPage />
      </QueryClientProvider>
    );

    await waitFor(() => {
      // Use data-qa for title
      expect(getByQa('companies-title')).toHaveTextContent('Companies');
      
      // Query within the companies table container
      const companiesTable = getByQa('companies-table');
      expect(within(companiesTable).getByText('Tech Corp')).toBeInTheDocument();
      expect(within(companiesTable).getByText('Startup Inc')).toBeInTheDocument();
    });
  });

  it('renders loading state', () => {
    mockUseCompanies.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <CompaniesPage />
      </QueryClientProvider>
    );

    expect(screen.getByText(/loading companies/i)).toBeInTheDocument();
  });

  it('renders empty state when no companies', () => {
    mockUseCompanies.mockReturnValue({
      data: { data: [], pagination: { page: 1, limit: 10, total: 0, pages: 1 } },
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteCompany.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <CompaniesPage />
      </QueryClientProvider>
    );

    expect(screen.getByText(/no companies found/i)).toBeInTheDocument();
  });
});
