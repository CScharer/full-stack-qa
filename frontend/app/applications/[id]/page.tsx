'use client';

import { useApplication, useDeleteApplication } from '@/lib/hooks/use-applications';
import { useNotes, useCreateNote, useDeleteNote } from '@/lib/hooks/use-notes';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { Button } from '@/components/ui/Button';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { ContactFull } from '@/lib/types/contact';
import { useState, use } from 'react';
import { NoteCreate } from '@/lib/types/note';
import { formatDate } from '@/lib/utils/date';

export default function ApplicationDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const router = useRouter();
  const { id } = use(params);
  const applicationId = parseInt(id);
  const { data: application, isLoading, error, refetch } = useApplication(applicationId);
  const deleteMutation = useDeleteApplication();
  
  // Notes for this application
  const { data: notesData, isLoading: notesLoading, error: notesError, refetch: refetchNotes } = useNotes({ 
    application_id: applicationId,
    page: 1,
    limit: 100 // Get all notes for this application
  });
  const createNoteMutation = useCreateNote();
  const deleteNoteMutation = useDeleteNote();
  
  // Note creation form state
  const [showNoteForm, setShowNoteForm] = useState(false);
  const [noteText, setNoteText] = useState('');
  const [noteError, setNoteError] = useState('');

  const handleDelete = async () => {
    if (confirm('Are you sure you want to delete this application? This will permanently delete the application and all associated notes.')) {
      try {
        await deleteMutation.mutateAsync(applicationId);
        router.push('/applications');
      } catch (err) {
        console.error('Delete failed:', err);
      }
    }
  };

  const handleCreateNote = async (e: React.FormEvent) => {
    e.preventDefault();
    setNoteError('');

    if (!noteText.trim()) {
      setNoteError('Note content is required');
      return;
    }

    try {
      const noteData: NoteCreate = {
        application_id: applicationId,
        note: noteText.trim(),
        created_by: 'current-user',
        modified_by: 'current-user',
      };
      await createNoteMutation.mutateAsync(noteData);
      setNoteText('');
      setShowNoteForm(false);
      refetchNotes();
    } catch (err: any) {
      setNoteError(err.response?.data?.error || 'Failed to create note');
    }
  };

  const handleDeleteNote = async (noteId: number) => {
    if (confirm('Are you sure you want to delete this note?')) {
      try {
        await deleteNoteMutation.mutateAsync(noteId);
        refetchNotes();
      } catch (err) {
        console.error('Delete note failed:', err);
      }
    }
  };

  if (isLoading) return <Loading message="Loading application..." />;
  if (error) return <Error message="Failed to load application" onRetry={() => refetch()} />;
  if (!application) return <Error message="Application not found" />;

  return (
    <div className="container py-3 py-md-4">
      <div className="mb-2 mb-md-3">
        <Link href="/applications" className="text-decoration-none" data-qa="application-detail-back-link">
          ← Back to Applications
        </Link>
      </div>

      <div className="card shadow-sm">
        <div className="card-header d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-2">
          <div>
            <h1 className="h4 h-md-3 mb-1 mb-md-2" data-qa={`application-detail-${application.id}-title`}>
              {application.position || 'Application Details'}
            </h1>
            <span className="badge bg-primary" data-qa={`application-detail-${application.id}-status-badge`}>
              {application.status}
            </span>
          </div>
          <div className="d-flex gap-2 w-100 w-md-auto">
            <Link href={`/applications/${application.id}/edit`} className="flex-grow-1 flex-md-grow-0">
              <Button variant="secondary" className="w-100 w-md-auto" data-qa={`application-detail-${application.id}-edit-button`}>Edit</Button>
            </Link>
            <Button variant="danger" onClick={handleDelete} className="flex-grow-1 flex-md-grow-0 w-100 w-md-auto" data-qa={`application-detail-${application.id}-delete-button`}>
              Delete
            </Button>
          </div>
        </div>
        <div className="card-body">
          <div className="row g-3 g-md-4">
            <div className="col-12 col-md-6">
              <h2 className="h5 h-md-5 mb-2 mb-md-3">Details</h2>
              <dl className="row g-2">
                <dt className="col-12 col-sm-4">Work Setting</dt>
                <dd className="col-12 col-sm-8">{application.work_setting}</dd>
                {application.location && (
                  <>
                    <dt className="col-12 col-sm-4">Location</dt>
                    <dd className="col-12 col-sm-8">{application.location}</dd>
                  </>
                )}
                {application.compensation && (
                  <>
                    <dt className="col-12 col-sm-4">Compensation</dt>
                    <dd className="col-12 col-sm-8">{application.compensation}</dd>
                  </>
                )}
                {application.job_link && (
                  <>
                    <dt className="col-12 col-sm-4">Job Link</dt>
                    <dd className="col-12 col-sm-8">
                      <a href={application.job_link} target="_blank" rel="noopener noreferrer" className="text-decoration-none text-break">
                        {application.job_link}
                      </a>
                    </dd>
                  </>
                )}
              </dl>
            </div>

            <div className="col-12 col-md-6">
              <h2 className="h5 h-md-5 mb-2 mb-md-3">Additional Information</h2>
              <dl className="row g-2">
                {application.requirement && (
                  <>
                    <dt className="col-12 col-sm-4">Requirements</dt>
                    <dd className="col-12 col-sm-8">{application.requirement}</dd>
                  </>
                )}
                {application.job_description && (
                  <>
                    <dt className="col-12 col-sm-4">Description</dt>
                    <dd className="col-12 col-sm-8 text-break" style={{ whiteSpace: 'pre-wrap' }}>{application.job_description}</dd>
                  </>
                )}
              </dl>
            </div>
          </div>

          {/* Related Entities Section */}
          <div className="row g-3 g-md-4 mt-3">
            {application.company_name && (
              <div className="col-12 col-md-4">
                <div className="card border-primary">
                  <div className="card-header bg-primary text-white">
                    <h5 className="mb-0">Company</h5>
                  </div>
                  <div className="card-body">
                    <h6 className="card-title">{application.company_name}</h6>
                    {application.company_address && (
                      <p className="card-text small mb-1">{application.company_address}</p>
                    )}
                    {(application.company_city || application.company_state) && (
                      <p className="card-text small mb-1">
                        {[application.company_city, application.company_state, application.company_zip]
                          .filter(Boolean)
                          .join(', ')}
                      </p>
                    )}
                    {application.company_id && (
                      <Link href={`/companies/${application.company_id}`} className="btn btn-sm btn-outline-primary mt-2">
                        View Company
                      </Link>
                    )}
                  </div>
                </div>
              </div>
            )}

            {application.client_name && (
              <div className="col-12 col-md-4">
                <div className="card border-info">
                  <div className="card-header bg-info text-white">
                    <h5 className="mb-0">Client</h5>
                  </div>
                  <div className="card-body">
                    <h6 className="card-title">{application.client_name}</h6>
                    {application.client_id && (
                      <Link href={`/clients/${application.client_id}`} className="btn btn-sm btn-outline-info mt-2">
                        View Client
                      </Link>
                    )}
                  </div>
                </div>
              </div>
            )}

            {application.contacts && application.contacts.length > 0 && (
              <div className="col-12 col-md-4">
                <div className="card border-success">
                  <div className="card-header bg-success text-white">
                    <h5 className="mb-0">Contacts ({application.contacts.length})</h5>
                  </div>
                  <div className="card-body">
                    {application.contacts.map((contact) => {
                      const contactFullName = contact.name || '';
                      return (
                      <div key={contact.id} className="mb-2">
                        <h6 className="card-title">{contactFullName}</h6>
                        {contact.title && <p className="card-text small mb-1">{contact.title}</p>}
                        {contact.emails && contact.emails.length > 0 && (
                          <p className="card-text small mb-1">
                            📧 {contact.emails.find(e => e.is_primary === 1)?.email || contact.emails[0].email}
                          </p>
                        )}
                        {contact.phones && contact.phones.length > 0 && (
                          <p className="card-text small mb-1">
                            📞 {contact.phones.find(p => p.is_primary === 1)?.phone || contact.phones[0].phone}
                          </p>
                        )}
                        <Link href={`/contacts/${contact.id}`} className="btn btn-sm btn-outline-success mt-1">
                          View Contact
                        </Link>
                      </div>
                      );
                    })}
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Notes Section */}
          <div className="row g-3 g-md-4 mt-3">
            <div className="col-12">
              <div className="card border-warning">
                <div className="card-header bg-warning text-dark d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-2">
                  <h5 className="mb-0">Notes</h5>
                  <Button
                    variant="primary"
                    size="sm"
                    onClick={() => setShowNoteForm(!showNoteForm)}
                    data-qa={`application-detail-${applicationId}-add-note-button`}
                  >
                    {showNoteForm ? 'Cancel' : '+ Add Note'}
                  </Button>
                </div>
                <div className="card-body">
                  {/* Note Creation Form */}
                  {showNoteForm && (
                    <div className="mb-3 pb-3 border-bottom" data-qa={`application-detail-${applicationId}-note-form`}>
                      {noteError && (
                        <div className="alert alert-danger" role="alert" data-qa={`application-detail-${applicationId}-note-form-error`}>
                          {noteError}
                        </div>
                      )}
                      <form onSubmit={handleCreateNote}>
                        <div className="mb-3">
                          <label className="form-label" data-qa={`application-detail-${applicationId}-note-form-label`}>
                            Note *
                          </label>
                          <textarea
                            className="form-control"
                            rows={5}
                            value={noteText}
                            onChange={(e) => setNoteText(e.target.value)}
                            required
                            data-qa={`application-detail-${applicationId}-note-form-textarea`}
                          />
                        </div>
                        <div className="d-flex gap-2">
                          <Button
                            type="submit"
                            disabled={createNoteMutation.isPending}
                            size="sm"
                            data-qa={`application-detail-${applicationId}-note-form-submit-button`}
                          >
                            {createNoteMutation.isPending ? 'Creating...' : 'Create Note'}
                          </Button>
                          <Button
                            type="button"
                            variant="secondary"
                            size="sm"
                            onClick={() => {
                              setShowNoteForm(false);
                              setNoteText('');
                              setNoteError('');
                            }}
                            data-qa={`application-detail-${applicationId}-note-form-cancel-button`}
                          >
                            Cancel
                          </Button>
                        </div>
                      </form>
                    </div>
                  )}

                  {/* Notes List */}
                  {notesLoading ? (
                    <Loading message="Loading notes..." />
                  ) : notesError ? (
                    <Error message="Failed to load notes" onRetry={() => refetchNotes()} />
                  ) : notesData?.data && notesData.data.length > 0 ? (
                    <div data-qa={`application-detail-${applicationId}-notes-list`}>
                      {notesData.data.map((note) => (
                        <div key={note.id} className="mb-3 pb-3 border-bottom" data-qa={`application-detail-${applicationId}-note-${note.id}`}>
                          <div className="d-flex justify-content-between align-items-start mb-2">
                            <div className="flex-grow-1">
                              <p className="mb-1 text-break" style={{ whiteSpace: 'pre-wrap' }}>{note.note}</p>
                              <small className="text-muted" data-qa={`application-detail-${applicationId}-note-${note.id}-date`}>
                                {formatDate(note.created_on)} by {note.created_by}
                              </small>
                            </div>
                            <div className="d-flex gap-1 ms-2">
                              <Link
                                href={`/notes/${note.id}`}
                                className="btn btn-sm btn-outline-primary"
                                data-qa={`application-detail-${applicationId}-note-${note.id}-view-button`}
                              >
                                View
                              </Link>
                              <button
                                onClick={() => handleDeleteNote(note.id)}
                                className="btn btn-sm btn-outline-danger"
                                data-qa={`application-detail-${applicationId}-note-${note.id}-delete-button`}
                              >
                                Delete
                              </button>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <p className="text-muted mb-0" data-qa={`application-detail-${applicationId}-notes-empty`}>
                      No notes yet. Click "+ Add Note" to create one.
                    </p>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
