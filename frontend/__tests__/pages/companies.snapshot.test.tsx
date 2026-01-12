/**
 * Snapshot tests for Companies page
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import CompaniesPage from '@/app/companies/page';
import { mockCompanyResponse } from '@/__mocks__/data';

const mockUseCompanies = vi.fn();
const mockUseDeleteCompany = vi.fn();

vi.mock('@/lib/hooks/use-companies', () => ({
  useCompanies: () => mockUseCompanies(),
  useDeleteCompany: () => mockUseDeleteCompany(),
}));

describe('CompaniesPage Snapshot Tests', () => {
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

  it('matches snapshot for loading state', () => {
    mockUseCompanies.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <CompaniesPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for companies list', () => {
    mockUseCompanies.mockReturnValue({
      data: mockCompanyResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteCompany.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <CompaniesPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for empty state', () => {
    mockUseCompanies.mockReturnValue({
      data: { data: [], pagination: { page: 1, limit: 10, total: 0, pages: 1 } },
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteCompany.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <CompaniesPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });
});
