/**
 * Tests for Contacts page using Vitest and mock data
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import ContactsPage from '@/app/contacts/page';
import { mockContactResponse } from '@/__mocks__/data';

// Mock the hooks
const mockUseContacts = vi.fn();
const mockUseDeleteContact = vi.fn();

vi.mock('@/lib/hooks/use-contacts', () => ({
  useContacts: () => mockUseContacts(),
  useDeleteContact: () => mockUseDeleteContact(),
}));

describe('ContactsPage', () => {
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

  it('renders contacts list', async () => {
    mockUseContacts.mockReturnValue({
      data: mockContactResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteContact.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <ContactsPage />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByText('Contacts')).toBeInTheDocument();
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    });
  });
});
