'use client';

import { useJobSearchSites, useDeleteJobSearchSite } from '@/lib/hooks/use-job-search-sites';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { StatusBar } from '@/components/StatusBar';
import Link from 'next/link';
import { useState } from 'react';

export default function JobSearchSitesPage() {
  const [page, setPage] = useState(1);
  const { data, isLoading, error, refetch } = useJobSearchSites({ page, limit: 10 });
  const deleteMutation = useDeleteJobSearchSite();

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this job search site?')) {
      try {
        await deleteMutation.mutateAsync(id);
        refetch();
      } catch (err) {
        console.error('Delete failed:', err);
      }
    }
  };

  if (isLoading) return <Loading message="Loading job search sites..." />;
  if (error) return <Error message="Failed to load job search sites" onRetry={() => refetch()} />;

  return (
    <div className="container py-3 py-md-4" style={{ paddingBottom: '60px' }}>
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h2 mb-0">Job Search Sites</h1>
        <Link href="/job-search-sites/new">
          <Button className="w-100 w-md-auto" data-qa="job-search-sites-new-button">Add</Button>
        </Link>
      </div>

      {data?.data && data.data.length > 0 ? (
        <>
          <div className="card shadow-sm">
            <div className="table-responsive">
              <table className="table table-hover mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Name</th>
                    <th className="d-none d-md-table-cell">URL</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data.data.map((site) => (
                    <tr key={site.id}>
                      <td>
                        <Link
                          href={`/job-search-sites/${site.id}`}
                          className="text-decoration-none fw-medium"
                        >
                          {site.name}
                        </Link>
                        {site.url && (
                          <div className="d-md-none mt-1">
                            <a href={site.url} target="_blank" rel="noopener noreferrer" className="text-muted small text-break">
                              {site.url}
                            </a>
                          </div>
                        )}
                      </td>
                      <td className="d-none d-md-table-cell">
                        {site.url ? (
                          <a href={site.url} target="_blank" rel="noopener noreferrer" className="text-break">
                            {site.url}
                          </a>
                        ) : (
                          'N/A'
                        )}
                      </td>
                      <td>
                        <div className="d-flex gap-1 gap-md-2">
                          <Link
                            href={`/job-search-sites/${site.id}/edit`}
                            className="btn btn-sm btn-outline-primary"
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => handleDelete(site.id)}
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
                data-qa="job-search-sites-pagination-previous-button"
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
                data-qa="job-search-sites-pagination-next-button"
              >
                Next
              </Button>
            </div>
          )}
        </>
      ) : (
        <div className="card shadow-sm text-center py-5">
          <div className="card-body">
            <p className="text-muted mb-4">No job search sites found.</p>
          </div>
        </div>
      )}
      <StatusBar />
    </div>
  );
}
