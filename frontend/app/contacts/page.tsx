'use client';

import { useContacts, useDeleteContact } from '@/lib/hooks/use-contacts';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { StatusBar } from '@/components/StatusBar';
import Link from 'next/link';
import { useState } from 'react';

export default function ContactsPage() {
  const [page, setPage] = useState(1);
  const [companyIdFilter, setCompanyIdFilter] = useState<string>('');
  const [applicationIdFilter, setApplicationIdFilter] = useState<string>('');
  const [clientIdFilter, setClientIdFilter] = useState<string>('');
  const [contactTypeFilter, setContactTypeFilter] = useState<string>('');
  const { data, isLoading, error, refetch } = useContacts({ 
    page, 
    limit: 10,
    company_id: companyIdFilter ? parseInt(companyIdFilter) : undefined,
    application_id: applicationIdFilter ? parseInt(applicationIdFilter) : undefined,
    client_id: clientIdFilter ? parseInt(clientIdFilter) : undefined,
    contact_type: contactTypeFilter || undefined,
  });
  const deleteMutation = useDeleteContact();

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this contact? This will also delete all associated emails and phone numbers.')) {
      try {
        await deleteMutation.mutateAsync(id);
        refetch();
      } catch (err) {
        console.error('Delete failed:', err);
      }
    }
  };

  if (isLoading) return <Loading message="Loading contacts..." />;
  if (error) return <Error message="Failed to load contacts" onRetry={() => refetch()} />;

  return (
    <div className="container py-3 py-md-4" data-qa="contacts-page" style={{ paddingBottom: '60px' }}>
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h2 mb-0" data-qa="contacts-title">Contacts</h1>
        <Link href="/contacts/new">
          <Button className="w-100 w-md-auto" data-qa="contacts-new-button">Add</Button>
        </Link>
      </div>

      {/* Filters */}
      <div className="card shadow-sm mb-3 mb-md-4" data-qa="contacts-filters">
        <div className="card-body">
          <div className="row g-2">
            <div className="col-12 col-md-3">
              <label className="form-label small">Company ID</label>
              <input
                type="number"
                className="form-control form-control-sm"
                placeholder="Filter by company ID"
                value={companyIdFilter}
                onChange={(e) => {
                  setCompanyIdFilter(e.target.value);
                  setPage(1);
                }}
                data-qa="contacts-filter-company-id"
              />
            </div>
            <div className="col-12 col-md-3">
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
                data-qa="contacts-filter-application-id"
              />
            </div>
            <div className="col-12 col-md-3">
              <label className="form-label small">Client ID</label>
              <input
                type="number"
                className="form-control form-control-sm"
                placeholder="Filter by client ID"
                value={clientIdFilter}
                onChange={(e) => {
                  setClientIdFilter(e.target.value);
                  setPage(1);
                }}
                data-qa="contacts-filter-client-id"
              />
            </div>
            <div className="col-12 col-md-3">
              <label className="form-label small">Contact Type</label>
              <input
                type="text"
                className="form-control form-control-sm"
                placeholder="Filter by contact type"
                value={contactTypeFilter}
                onChange={(e) => {
                  setContactTypeFilter(e.target.value);
                  setPage(1);
                }}
                data-qa="contacts-filter-contact-type"
              />
            </div>
          </div>
        </div>
      </div>

      {data?.data && data.data.length > 0 ? (
        <>
          <div className="card shadow-sm" data-qa="contacts-list-card">
            <div className="table-responsive">
              <table className="table table-hover mb-0" data-qa="contacts-table">
                <thead className="table-light">
                  <tr>
                    <th>Name</th>
                    <th className="d-none d-md-table-cell">Title</th>
                    <th>Type</th>
                    <th className="d-none d-lg-table-cell">Company</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody data-qa="contacts-table-body">
                  {data.data.map((contact) => {
                    const fullName = contact.name || `${contact.first_name} ${contact.last_name}`.trim();
                    return (
                    <tr key={contact.id} data-qa={`contact-row-${contact.id}`}>
                      <td>
                        <Link
                          href={`/contacts/${contact.id}`}
                          className="text-decoration-none fw-medium"
                          data-qa={`contact-name-link-${contact.id}`}
                        >
                          {fullName}
                        </Link>
                        <div className="d-md-none mt-1">
                          <small className="text-muted">{contact.title || 'N/A'}</small>
                        </div>
                      </td>
                      <td className="d-none d-md-table-cell" data-qa={`contact-title-${contact.id}`}>{contact.title || 'N/A'}</td>
                      <td>
                        <span className="badge bg-info" data-qa={`contact-type-${contact.id}`}>
                          {contact.contact_type}
                        </span>
                      </td>
                      <td className="d-none d-lg-table-cell" data-qa={`contact-company-${contact.id}`}>{contact.company_id || 'N/A'}</td>
                      <td>
                        <div className="d-flex gap-1 gap-md-2">
                          <Link
                            href={`/contacts/${contact.id}/edit`}
                            className="btn btn-sm btn-outline-primary"
                            data-qa={`contact-edit-button-${contact.id}`}
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => handleDelete(contact.id)}
                            className="btn btn-sm btn-outline-danger"
                            data-qa={`contact-delete-button-${contact.id}`}
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                    );
                  })}
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
                data-qa="contacts-pagination-previous-button"
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
              >
                Next
              </Button>
            </div>
          )}
        </>
      ) : (
        <div className="card shadow-sm text-center py-5">
          <div className="card-body">
            <p className="text-muted mb-4" data-qa="contacts-empty-state">No contacts found.</p>
          </div>
        </div>
      )}
      <StatusBar />
    </div>
  );
}
