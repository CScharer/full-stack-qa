'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useNote, useUpdateNote } from '@/lib/hooks/use-notes';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { NoteUpdate } from '@/lib/types/note';

export default function EditNotePage({ params }: { params: { id: string } }) {
  const noteId = parseInt(params.id);
  const router = useRouter();
  const { data: note, isLoading, error, refetch } = useNote(noteId);
  const updateMutation = useUpdateNote();
  const [formData, setFormData] = useState<NoteUpdate>({
    note: '',
    modified_by: 'current-user',
  });
  const [formError, setFormError] = useState<string>('');

  useEffect(() => {
    if (note) {
      setFormData({
        note: note.note || '',
        modified_by: 'current-user',
      });
    }
  }, [note]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError('');

    if (!formData.note?.trim()) {
      setFormError('Note content is required');
      return;
    }

    try {
      await updateMutation.mutateAsync({ id: noteId, note: formData });
      router.push(`/notes/${noteId}`);
    } catch (err: any) {
      setFormError(err.response?.data?.error || 'Failed to update note');
    }
  };

  if (isLoading) return <Loading message="Loading note..." />;
  if (error) return <Error message="Failed to load note" onRetry={() => refetch()} />;
  if (!note) return <Error message="Note not found" />;

  return (
    <div className="container py-3 py-md-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0">Edit Note</h1>
        <Button variant="secondary" onClick={() => router.back()} className="w-100 w-md-auto" data-qa={`note-edit-${noteId}-cancel-button`}>
          Cancel
        </Button>
      </div>

      <div className="card shadow-sm">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">Note Details</h5>
        </div>
        <div className="card-body">
          {formError && (
            <div className="alert alert-danger" role="alert">
              {formError}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Note *</label>
              <textarea
                className="form-control"
                rows={10}
                value={formData.note || ''}
                onChange={(e) => setFormData({ ...formData, note: e.target.value })}
                required
              />
            </div>

            <div className="d-flex flex-column flex-sm-row gap-2">
              <Button type="submit" disabled={updateMutation.isPending} className="w-100 w-sm-auto" data-qa={`note-edit-${noteId}-submit-button`}>
                {updateMutation.isPending ? 'Updating...' : 'Update Note'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => router.back()} className="w-100 w-sm-auto" data-qa={`note-edit-${noteId}-cancel-button-bottom`}>
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
