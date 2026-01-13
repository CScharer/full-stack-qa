/**
 * Tests for Notes page using Vitest and mock data
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, within } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import NotesPage from '@/app/notes/page';
import { mockNoteResponse } from '@/__mocks__/data';
import { getByQa } from '../utils/test-helpers';

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
      // Use data-qa for title
      expect(getByQa('notes-title')).toHaveTextContent('Notes');
      
      // Query within the notes list card container
      const notesListCard = getByQa('notes-list-card');
      // Note text appears in both mobile and desktop views, so use getAllByText within container
      const noteTexts = within(notesListCard).getAllByText(/initial phone screen/i);
      expect(noteTexts.length).toBeGreaterThan(0);
    });
  });
});
