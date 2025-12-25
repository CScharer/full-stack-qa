'use client';

import { useNote } from '@/lib/hooks/use-notes';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import Link from 'next/link';
import { use } from 'react';
import { formatDate } from '@/lib/utils/date';

export default function NoteDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const noteId = parseInt(id);
  const { data: note, isLoading, error, refetch } = useNote(noteId);

  if (isLoading) return <Loading message="Loading note..." />;
  if (error) return <Error message="Failed to load note" onRetry={() => refetch()} />;
  if (!note) return <Error message="Note not found" />;

  return (
    <div className="container py-3 py-md-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0">Note Details</h1>
        <div className="d-flex gap-2 w-100 w-md-auto">
          <Link href={`/notes/${note.id}/edit`} className="flex-grow-1 flex-md-grow-0">
            <Button variant="primary" className="w-100 w-md-auto" data-qa={`note-detail-${note.id}-edit-button`}>Edit</Button>
          </Link>
          <Link href="/notes" className="flex-grow-1 flex-md-grow-0">
            <Button variant="secondary" className="w-100 w-md-auto" data-qa={`note-detail-${note.id}-back-button`}>Back to List</Button>
          </Link>
        </div>
      </div>

      <div className="row g-3 g-md-4">
        <div className="col-12 col-md-8">
          <div className="card shadow-sm mb-3 mb-md-4">
            <div className="card-header bg-primary text-white">
              <h5 className="mb-0">Note Content</h5>
            </div>
            <div className="card-body">
              <p className="mb-0 text-break" style={{ whiteSpace: 'pre-wrap' }}>{note.note}</p>
            </div>
          </div>
        </div>

        <div className="col-12 col-md-4">
          <div className="card shadow-sm">
            <div className="card-header">
              <h5 className="mb-0">Metadata</h5>
            </div>
            <div className="card-body">
              <dl className="row g-2 mb-0">
                <dt className="col-12">Application ID</dt>
                <dd className="col-12">
                  <Link href={`/applications/${note.application_id}`}>
                    {note.application_id}
                  </Link>
                </dd>

                <dt className="col-12">Created</dt>
                <dd className="col-12">{formatDate(note.created_on)}</dd>

                <dt className="col-12">Modified</dt>
                <dd className="col-12">{formatDate(note.modified_on)}</dd>

                <dt className="col-12">Created By</dt>
                <dd className="col-12">{note.created_by}</dd>

                <dt className="col-12">Modified By</dt>
                <dd className="col-12">{note.modified_by}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
