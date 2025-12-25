'use client';

import { useCompanies, useDeleteCompany } from '@/lib/hooks/use-companies';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { StatusBar } from '@/components/StatusBar';
import Link from 'next/link';
import { useState } from 'react';

export default function CompaniesPage() {
  const [page, setPage] = useState(1);
  const [jobTypeFilter, setJobTypeFilter] = useState<string>('');
  const { data, isLoading, error, refetch } = useCompanies({ 
    page, 
    limit: 10,
    job_type: jobTypeFilter || undefined,
  });
  const deleteMutation = useDeleteCompany();

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this company? This will set company_id to NULL for linked applications and contacts.')) {
      try {
        await deleteMutation.mutateAsync(id);
        refetch();
      } catch (err) {
        console.error('Delete failed:', err);
      }
    }
  };

  if (isLoading) return <Loading message="Loading companies..." />;
  if (error) return <Error message="Failed to load companies" onRetry={() => refetch()} />;

  return (
    <div className="container py-3 py-md-4" style={{ paddingBottom: '60px' }}>
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h2 mb-0">Companies</h1>
        <Link href="/companies/new">
          <Button className="w-100 w-md-auto" data-qa="companies-new-button">Add</Button>
        </Link>
      </div>

      {/* Filters */}
      <div className="card shadow-sm mb-3 mb-md-4" data-qa="companies-filters">
        <div className="card-body">
          <div className="row g-2">
            <div className="col-12 col-md-4">
              <label className="form-label small">Job Type</label>
              <input
                type="text"
                className="form-control form-control-sm"
                placeholder="Filter by job type"
                value={jobTypeFilter}
                onChange={(e) => {
                  setJobTypeFilter(e.target.value);
                  setPage(1);
                }}
                data-qa="companies-filter-job-type"
              />
            </div>
          </div>
        </div>
      </div>

      {data?.data && data.data.length > 0 ? (
        <>
          <div className="card shadow-sm">
            <div className="table-responsive">
              <table className="table table-hover mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Name</th>
                    <th className="d-none d-md-table-cell">Location</th>
                    <th className="d-none d-lg-table-cell">Job Type</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data.data.map((company) => (
                    <tr key={company.id}>
                      <td>
                        <Link
                          href={`/companies/${company.id}`}
                          className="text-decoration-none fw-medium"
                        >
                          {company.name}
                        </Link>
                        <div className="d-md-none mt-1">
                          <small className="text-muted">
                            {company.city && company.state ? `${company.city}, ${company.state}` : 'N/A'}
                          </small>
                        </div>
                      </td>
                      <td className="d-none d-md-table-cell">
                        {company.city && company.state ? `${company.city}, ${company.state}` : 'N/A'}
                      </td>
                      <td className="d-none d-lg-table-cell">{company.job_type}</td>
                      <td>
                        <div className="d-flex gap-1 gap-md-2">
                          <Link
                            href={`/companies/${company.id}/edit`}
                            className="btn btn-sm btn-outline-primary"
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => handleDelete(company.id)}
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
                data-qa="companies-pagination-previous-button"
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
                data-qa="companies-pagination-next-button"
              >
                Next
              </Button>
            </div>
          )}
        </>
      ) : (
        <div className="card shadow-sm text-center py-5">
          <div className="card-body">
            <p className="text-muted mb-4">No companies found.</p>
          </div>
        </div>
      )}
      <StatusBar />
    </div>
  );
}
