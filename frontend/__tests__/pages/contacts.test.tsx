/**
 * Tests for Contacts page using Vitest and mock data
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, within } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import ContactsPage from '@/app/contacts/page';
import { mockContactResponse } from '@/__mocks__/data';
import { getByQa } from '../utils/test-helpers';

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
      // Use data-qa for title
      expect(getByQa('contacts-title')).toHaveTextContent('Contacts');
      
      // Query within the contacts table body container
      const tableBody = getByQa('contacts-table-body');
      expect(within(tableBody).getByText('John Doe')).toBeInTheDocument();
      expect(within(tableBody).getByText('Jane Smith')).toBeInTheDocument();
    });
  });
});
