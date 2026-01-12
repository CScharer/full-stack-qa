/**
 * Snapshot tests for Contacts page
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import ContactsPage from '@/app/contacts/page';
import { mockContactResponse } from '@/__mocks__/data';

const mockUseContacts = vi.fn();
const mockUseDeleteContact = vi.fn();

vi.mock('@/lib/hooks/use-contacts', () => ({
  useContacts: () => mockUseContacts(),
  useDeleteContact: () => mockUseDeleteContact(),
}));

describe('ContactsPage Snapshot Tests', () => {
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
    mockUseContacts.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ContactsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for contacts list', () => {
    mockUseContacts.mockReturnValue({
      data: mockContactResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteContact.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ContactsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for empty state', () => {
    mockUseContacts.mockReturnValue({
      data: { data: [], pagination: { page: 1, limit: 10, total: 0, pages: 1 } },
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteContact.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ContactsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });
});
