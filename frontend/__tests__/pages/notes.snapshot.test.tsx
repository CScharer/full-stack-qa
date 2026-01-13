/**
 * Snapshot tests for Notes page
 */
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import NotesPage from '@/app/notes/page';
import { mockNoteResponse } from '@/__mocks__/data';

const mockUseNotes = vi.fn();
const mockUseDeleteNote = vi.fn();

vi.mock('@/lib/hooks/use-notes', () => ({
  useNotes: () => mockUseNotes(),
  useDeleteNote: () => mockUseDeleteNote(),
}));

describe('NotesPage Snapshot Tests', () => {
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
    mockUseNotes.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <NotesPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for notes list', () => {
    mockUseNotes.mockReturnValue({
      data: mockNoteResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteNote.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <NotesPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for empty state', () => {
    mockUseNotes.mockReturnValue({
      data: { data: [], pagination: { page: 1, limit: 10, total: 0, pages: 1 } },
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteNote.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <NotesPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });
});
