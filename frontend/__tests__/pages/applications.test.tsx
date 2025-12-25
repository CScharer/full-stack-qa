/**
 * Tests for Applications page using Vitest and mock data
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import ApplicationsPage from '@/app/applications/page';
import { mockApplicationResponse } from '@/__mocks__/data';

// Mock the hooks
const mockUseApplications = vi.fn();
const mockUseDeleteApplication = vi.fn();

vi.mock('@/lib/hooks/use-applications', () => ({
  useApplications: () => mockUseApplications(),
  useDeleteApplication: () => mockUseDeleteApplication(),
}));

describe('ApplicationsPage', () => {
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

  it('renders loading state', () => {
    mockUseApplications.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <ApplicationsPage />
      </QueryClientProvider>
    );

    expect(screen.getByText(/loading applications/i)).toBeInTheDocument();
  });

  it('renders error state', () => {
    mockUseApplications.mockReturnValue({
      data: undefined,
      isLoading: false,
      error: { message: 'Failed to load' },
      refetch: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <ApplicationsPage />
      </QueryClientProvider>
    );

    expect(screen.getByText(/failed to load applications/i)).toBeInTheDocument();
  });

  it('renders applications list', async () => {
    mockUseApplications.mockReturnValue({
      data: mockApplicationResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteApplication.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <ApplicationsPage />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByText('Applications')).toBeInTheDocument();
      expect(screen.getByText('Senior Software Engineer')).toBeInTheDocument();
      expect(screen.getByText('Frontend Developer')).toBeInTheDocument();
    });
  });

  it('renders empty state when no applications', () => {
    mockUseApplications.mockReturnValue({
      data: { data: [], pagination: { page: 1, limit: 10, total: 0, pages: 1 } },
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteApplication.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <ApplicationsPage />
      </QueryClientProvider>
    );

    expect(screen.getByText(/no applications found/i)).toBeInTheDocument();
  });
});
