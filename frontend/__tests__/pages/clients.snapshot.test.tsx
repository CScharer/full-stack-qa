/**
 * Snapshot tests for Clients page
 */
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import ClientsPage from '@/app/clients/page';
import { mockClientResponse } from '@/__mocks__/data';

const mockUseClients = vi.fn();
const mockUseDeleteClient = vi.fn();

vi.mock('@/lib/hooks/use-clients', () => ({
  useClients: () => mockUseClients(),
  useDeleteClient: () => mockUseDeleteClient(),
}));

describe('ClientsPage Snapshot Tests', () => {
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
    mockUseClients.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ClientsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for clients list', () => {
    mockUseClients.mockReturnValue({
      data: mockClientResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteClient.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ClientsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for empty state', () => {
    mockUseClients.mockReturnValue({
      data: { data: [], pagination: { page: 1, limit: 10, total: 0, pages: 1 } },
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteClient.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ClientsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });
});
