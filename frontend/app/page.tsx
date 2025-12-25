'use client';

import { useApplications, useDeleteApplication } from '@/lib/hooks/use-applications';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { StatusBar } from '@/components/StatusBar';
import Link from 'next/link';
import { useState } from 'react';
import { formatDateOnly } from '@/lib/utils/date';

export default function Home() {
  const [page, setPage] = useState(1);
  const { data, isLoading, error, refetch } = useApplications({ page, limit: 10 });
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
    <div className="min-vh-100 bg-light" data-qa="home-page" style={{ paddingBottom: '60px' }}>
      <div className="container py-4 py-md-5">
        <div className="mb-4 mb-md-5 text-center">
          <h1 className="h2 h-md-1 fw-bold text-primary mb-2 mb-md-3" data-qa="home-title">
            Job Search Application
          </h1>
        </div>

        {/* Applications Table Section */}
        <div className="mb-4 mb-md-5">
          <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
            <h2 className="h4 mb-0" data-qa="home-applications-section-title">Applications</h2>
            <Link href="/applications/new">
              <Button className="w-100 w-md-auto" data-qa="home-new-application-button">Add Application</Button>
            </Link>
          </div>

          {data?.data && data.data.length > 0 ? (
            <>
              <div className="card shadow-sm" data-qa="home-applications-table-card">
                <div className="table-responsive">
                  <table className="table table-hover mb-0" data-qa="home-applications-table">
                    <thead className="table-light">
                      <tr>
                        <th>Position</th>
                        <th className="d-none d-md-table-cell">Status</th>
                        <th className="d-none d-md-table-cell">Work Setting</th>
                        <th className="d-none d-lg-table-cell">Company</th>
                        <th className="d-none d-lg-table-cell">Client</th>
                        <th className="d-none d-lg-table-cell">Contact</th>
                        <th className="d-none d-xl-table-cell">Location</th>
                        <th className="d-none d-xl-table-cell">Compensation</th>
                        <th className="d-none d-xl-table-cell">Job Link</th>
                        <th className="d-none d-xl-table-cell">Date Close</th>
                        <th className="d-none d-xl-table-cell">Created</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody data-qa="home-applications-table-body">
                      {data.data.map((application) => (
                        <tr key={application.id} data-qa={`home-application-row-${application.id}`}>
                          <td>
                            <Link
                              href={`/applications/${application.id}`}
                              className="text-decoration-none fw-medium"
                              data-qa={`home-application-position-link-${application.id}`}
                            >
                              {application.position || 'N/A'}
                            </Link>
                            <div className="d-md-none mt-1">
                              <span className="badge bg-primary me-1">
                                {application.status}
                              </span>
                              {application.work_setting && (
                                <span className="badge bg-secondary">
                                  {application.work_setting}
                                </span>
                              )}
                            </div>
                          </td>
                          <td className="d-none d-md-table-cell">
                            <span className="badge bg-primary" data-qa={`home-application-status-${application.id}`}>
                              {application.status}
                            </span>
                          </td>
                          <td className="d-none d-md-table-cell" data-qa={`home-application-work-setting-${application.id}`}>
                            {application.work_setting || 'N/A'}
                          </td>
                          <td className="d-none d-lg-table-cell" data-qa={`home-application-company-${application.id}`}>
                            {application.company_name || application.company_id || 'N/A'}
                          </td>
                          <td className="d-none d-lg-table-cell" data-qa={`home-application-client-${application.id}`}>
                            {application.client_name || application.client_id || 'N/A'}
                          </td>
                          <td className="d-none d-lg-table-cell" data-qa={`home-application-contact-${application.id}`}>
                            {application.contact_name || 'N/A'}
                          </td>
                          <td className="d-none d-xl-table-cell" data-qa={`home-application-location-${application.id}`}>
                            {application.location || 'N/A'}
                          </td>
                          <td className="d-none d-xl-table-cell" data-qa={`home-application-compensation-${application.id}`}>
                            {application.compensation || 'N/A'}
                          </td>
                          <td className="d-none d-xl-table-cell" data-qa={`home-application-job-link-${application.id}`}>
                            {application.job_link ? (
                              <a 
                                href={application.job_link} 
                                target="_blank" 
                                rel="noopener noreferrer"
                                className="text-decoration-none text-break"
                                onClick={(e) => e.stopPropagation()}
                              >
                                Link
                              </a>
                            ) : (
                              'N/A'
                            )}
                          </td>
                          <td className="d-none d-xl-table-cell" data-qa={`home-application-date-close-${application.id}`}>
                            {application.date_close || 'N/A'}
                          </td>
                          <td className="d-none d-xl-table-cell" data-qa={`home-application-created-${application.id}`}>
                            {application.created_on ? formatDateOnly(application.created_on) : 'N/A'}
                          </td>
                          <td>
                            <div className="d-flex gap-1 gap-md-2">
                              <Link
                                href={`/applications/${application.id}`}
                                className="btn btn-sm btn-outline-primary"
                                data-qa={`home-application-view-button-${application.id}`}
                              >
                                View
                              </Link>
                              <Link
                                href={`/applications/${application.id}/edit`}
                                className="btn btn-sm btn-outline-secondary"
                                data-qa={`home-application-edit-button-${application.id}`}
                              >
                                Edit
                              </Link>
                              <button
                                onClick={() => handleDelete(application.id)}
                                className="btn btn-sm btn-outline-danger"
                                data-qa={`home-application-delete-button-${application.id}`}
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
                    data-qa="home-applications-pagination-previous-button"
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
                    data-qa="home-applications-pagination-next-button"
                  >
                    Next
                  </Button>
                </div>
              )}
            </>
          ) : (
            <div className="card shadow-sm text-center py-5">
              <div className="card-body">
                <h3 className="h5 mb-3" data-qa="home-no-applications-title">No Applications Yet</h3>
                <p className="text-muted mb-4">Get started by creating your first job application.</p>
              </div>
            </div>
          )}
        </div>
      </div>
      <StatusBar />
    </div>
  );
}
