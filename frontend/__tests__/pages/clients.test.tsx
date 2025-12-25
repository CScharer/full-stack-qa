/**
 * Tests for Clients page using Vitest and mock data
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import ClientsPage from '@/app/clients/page';
import { mockClientResponse } from '@/__mocks__/data';

// Mock the hooks
const mockUseClients = vi.fn();
const mockUseDeleteClient = vi.fn();

vi.mock('@/lib/hooks/use-clients', () => ({
  useClients: () => mockUseClients(),
  useDeleteClient: () => mockUseDeleteClient(),
}));

describe('ClientsPage', () => {
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

  it('renders clients list', async () => {
    mockUseClients.mockReturnValue({
      data: mockClientResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteClient.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <ClientsPage />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByText('Clients')).toBeInTheDocument();
      expect(screen.getByText('Client A')).toBeInTheDocument();
      expect(screen.getByText('Client B')).toBeInTheDocument();
    });
  });

  it('renders loading state', () => {
    mockUseClients.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <ClientsPage />
      </QueryClientProvider>
    );

    expect(screen.getByText(/loading clients/i)).toBeInTheDocument();
  });
});
