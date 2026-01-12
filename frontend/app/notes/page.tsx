'use client';

import { useNotes, useDeleteNote } from '@/lib/hooks/use-notes';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { StatusBar } from '@/components/StatusBar';
import Link from 'next/link';
import { useState } from 'react';
import { formatDateOnly } from '@/lib/utils/date';

export default function NotesPage() {
  const [page, setPage] = useState(1);
  const [applicationIdFilter, setApplicationIdFilter] = useState<string>('');
  const applicationId = applicationIdFilter ? parseInt(applicationIdFilter) : undefined;
  const { data, isLoading, error, refetch } = useNotes({ page, limit: 10, application_id: applicationId });
  const deleteMutation = useDeleteNote();

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this note?')) {
      try {
        await deleteMutation.mutateAsync(id);
        refetch();
      } catch (err) {
        console.error('Delete failed:', err);
      }
    }
  };

  if (isLoading) return <Loading message="Loading notes..." />;
  if (error) return <Error message="Failed to load notes" onRetry={() => refetch()} />;

  return (
    <div className="container py-3 py-md-4" style={{ paddingBottom: '60px' }}>
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h2 mb-0" data-qa="notes-title">Notes</h1>
        <p className="text-muted mb-0 small">
          Notes can only be created from within an application page.
        </p>
      </div>

      {/* Filters */}
      <div className="card shadow-sm mb-3 mb-md-4" data-qa="notes-filters">
        <div className="card-body">
          <div className="row g-2">
            <div className="col-12 col-md-4">
              <label className="form-label small">Application ID</label>
              <input
                type="number"
                className="form-control form-control-sm"
                placeholder="Filter by application ID"
                value={applicationIdFilter}
                onChange={(e) => {
                  setApplicationIdFilter(e.target.value);
                  setPage(1);
                }}
                data-qa="notes-filter-application-id"
              />
            </div>
          </div>
        </div>
      </div>

      {data?.data && data.data.length > 0 ? (
        <>
          <div className="card shadow-sm" data-qa="notes-list-card">
            <div className="table-responsive">
              <table className="table table-hover mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Application ID</th>
                    <th className="d-none d-md-table-cell">Note</th>
                    <th className="d-none d-lg-table-cell">Created</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data.data.map((note) => (
                    <tr key={note.id}>
                      <td>
                        <Link
                          href={`/applications/${note.application_id}`}
                          className="text-decoration-none"
                        >
                          {note.application_id}
                        </Link>
                        <div className="d-md-none mt-1">
                          <small className="text-muted text-break">
                            {note.note.substring(0, 50)}{note.note.length > 50 ? '...' : ''}
                          </small>
                        </div>
                      </td>
                      <td className="d-none d-md-table-cell">{note.note.substring(0, 100)}{note.note.length > 100 ? '...' : ''}</td>
                      <td className="d-none d-lg-table-cell">{formatDateOnly(note.created_on)}</td>
                      <td>
                        <div className="d-flex gap-1 gap-md-2">
                          <Link
                            href={`/notes/${note.id}`}
                            className="btn btn-sm btn-outline-primary"
                          >
                            View
                          </Link>
                          <button
                            onClick={() => handleDelete(note.id)}
                            className="btn btn-sm btn-outline-danger"
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {data.pagination && data.pagination.pages > 1 && (
            <div className="d-flex flex-column flex-sm-row justify-content-center align-items-center gap-2 mt-3 mt-md-4">
              <Button
                variant="secondary"
                size="sm"
                onClick={() => setPage(p => Math.max(1, p - 1))}
                disabled={page === 1}
                className="w-100 w-sm-auto"
                data-qa="notes-pagination-previous-button"
              >
                Previous
              </Button>
              <span className="px-2 px-sm-3 text-center">
                Page {data.pagination.page} of {data.pagination.pages}
              </span>
              <Button
                variant="secondary"
                size="sm"
                onClick={() => setPage(p => Math.min(data.pagination.pages, p + 1))}
                disabled={page === data.pagination.pages}
                className="w-100 w-sm-auto"
                data-qa="notes-pagination-next-button"
              >
                Next
              </Button>
            </div>
          )}
        </>
      ) : (
        <div className="card shadow-sm text-center py-5">
          <div className="card-body">
            <p className="text-muted mb-4" data-qa="notes-empty-state">No notes found.</p>
            <p className="text-muted small mb-3">
              Notes can only be created from within an application page.
            </p>
          </div>
        </div>
      )}
      <StatusBar />
    </div>
  );
}
