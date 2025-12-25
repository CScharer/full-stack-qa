/**
 * Tests for Notes page using Vitest and mock data
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import NotesPage from '@/app/notes/page';
import { mockNoteResponse } from '@/__mocks__/data';

// Mock the hooks
const mockUseNotes = vi.fn();
const mockUseDeleteNote = vi.fn();

vi.mock('@/lib/hooks/use-notes', () => ({
  useNotes: () => mockUseNotes(),
  useDeleteNote: () => mockUseDeleteNote(),
}));

describe('NotesPage', () => {
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

  it('renders notes list', async () => {
    mockUseNotes.mockReturnValue({
      data: mockNoteResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteNote.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    render(
      <QueryClientProvider client={queryClient}>
        <NotesPage />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByText('Notes')).toBeInTheDocument();
      expect(screen.getByText(/initial phone screen/i)).toBeInTheDocument();
    });
  });
});
