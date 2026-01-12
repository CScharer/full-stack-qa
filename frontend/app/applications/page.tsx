'use client';

import { useApplications, useDeleteApplication } from '@/lib/hooks/use-applications';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { StatusBar } from '@/components/StatusBar';
import Link from 'next/link';
import { useState } from 'react';

export default function ApplicationsPage() {
  const [page, setPage] = useState(1);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [companyIdFilter, setCompanyIdFilter] = useState<string>('');
  const [clientIdFilter, setClientIdFilter] = useState<string>('');
  const { data, isLoading, error, refetch } = useApplications({ 
    page, 
    limit: 10,
    status: statusFilter || undefined,
    company_id: companyIdFilter ? parseInt(companyIdFilter) : undefined,
    client_id: clientIdFilter ? parseInt(clientIdFilter) : undefined,
  });
  const deleteMutation = useDeleteApplication();

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this application? This will also delete all associated notes.')) {
      try {
        await deleteMutation.mutateAsync(id);
        refetch();
      } catch (err) {
        console.error('Delete failed:', err);
      }
    }
  };

  if (isLoading) return <Loading message="Loading applications..." />;
  if (error) return <Error message="Failed to load applications" onRetry={() => refetch()} />;

  return (
    <div className="container py-3 py-md-4" data-qa="applications-page" style={{ paddingBottom: '60px' }}>
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h2 mb-0" data-qa="applications-title">Applications</h1>
        <Link href="/applications/new">
          <Button className="w-100 w-md-auto" data-qa="applications-new-button">Add</Button>
        </Link>
      </div>

      {/* Filters */}
      <div className="card shadow-sm mb-3 mb-md-4" data-qa="applications-filters">
        <div className="card-body">
          <div className="row g-2">
            <div className="col-12 col-md-4">
              <label className="form-label small">Status</label>
              <input
                type="text"
                className="form-control form-control-sm"
                placeholder="Filter by status"
                value={statusFilter}
                onChange={(e) => {
                  setStatusFilter(e.target.value);
                  setPage(1);
                }}
                data-qa="applications-filter-status"
              />
            </div>
            <div className="col-12 col-md-4">
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
                data-qa="applications-filter-company-id"
              />
            </div>
            <div className="col-12 col-md-4">
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
                data-qa="applications-filter-client-id"
              />
            </div>
          </div>
        </div>
      </div>

      {data?.data && data.data.length > 0 ? (
        <>
          <div className="card shadow-sm" data-qa="applications-list-card">
            <div className="table-responsive">
              <table className="table table-hover mb-0" data-qa="applications-table">
                <thead className="table-light">
                  <tr>
                    <th>Position</th>
                    <th className="d-none d-md-table-cell">Status</th>
                    <th className="d-none d-lg-table-cell">Company</th>
                    <th className="d-none d-xl-table-cell">Client</th>
                    <th className="d-none d-xl-table-cell">Contact</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody data-qa="applications-table-body">
                  {data.data.map((application) => (
                    <tr key={application.id} data-qa={`application-row-${application.id}`}>
                      <td>
                        <Link
                          href={`/applications/${application.id}`}
                          className="text-decoration-none"
                          data-qa={`application-position-link-${application.id}`}
                        >
                          {application.position || 'N/A'}
                        </Link>
                        <div className="d-md-none mt-1">
                          <span className="badge bg-primary">
                            {application.status}
                          </span>
                        </div>
                      </td>
                      <td className="d-none d-md-table-cell">
                        <span className="badge bg-primary" data-qa={`application-status-${application.id}`}>
                          {application.status}
                        </span>
                      </td>
                      <td className="d-none d-lg-table-cell" data-qa={`application-company-${application.id}`}>
                        {application.company_name || application.company_id || 'N/A'}
                      </td>
                      <td className="d-none d-xl-table-cell" data-qa={`application-client-${application.id}`}>
                        {application.client_name || application.client_id || 'N/A'}
                      </td>
                      <td className="d-none d-xl-table-cell" data-qa={`application-contact-${application.id}`}>
                        {application.contact_name || 'N/A'}
                      </td>
                      <td>
                        <div className="d-flex gap-1 gap-md-2">
                          <Link
                            href={`/applications/${application.id}/edit`}
                            className="btn btn-sm btn-outline-primary"
                            data-qa={`application-edit-button-${application.id}`}
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => handleDelete(application.id)}
                            className="btn btn-sm btn-outline-danger"
                            data-qa={`application-delete-button-${application.id}`}
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
            <p className="text-muted mb-4" data-qa="applications-empty-state">No applications found.</p>
          </div>
        </div>
      )}
      <StatusBar />
    </div>
  );
}
