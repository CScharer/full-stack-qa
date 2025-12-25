'use client';

import { useClients, useDeleteClient } from '@/lib/hooks/use-clients';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { StatusBar } from '@/components/StatusBar';
import Link from 'next/link';
import { useState } from 'react';

export default function ClientsPage() {
  const [page, setPage] = useState(1);
  const { data, isLoading, error, refetch } = useClients({ page, limit: 10 });
  const deleteMutation = useDeleteClient();

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this client? This will set client_id to NULL for linked applications and contacts.')) {
      try {
        await deleteMutation.mutateAsync(id);
        refetch();
      } catch (err) {
        console.error('Delete failed:', err);
      }
    }
  };

  if (isLoading) return <Loading message="Loading clients..." />;
  if (error) return <Error message="Failed to load clients" onRetry={() => refetch()} />;

  return (
    <div className="container py-3 py-md-4" style={{ paddingBottom: '60px' }}>
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h2 mb-0">Clients</h1>
        <Link href="/clients/new">
          <Button className="w-100 w-md-auto" data-qa="clients-new-button">Add</Button>
        </Link>
      </div>

      {/* Filters - Clients has no specific filters, but we'll add a placeholder for consistency */}
      <div className="card shadow-sm mb-3 mb-md-4" data-qa="clients-filters">
        <div className="card-body">
          <p className="text-muted small mb-0">No filters available for clients.</p>
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
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data.data.map((client) => (
                    <tr key={client.id}>
                      <td>
                        <Link
                          href={`/clients/${client.id}`}
                          className="text-decoration-none fw-medium"
                        >
                          {client.name || 'Unnamed Client'}
                        </Link>
                      </td>
                      <td>
                        <div className="d-flex gap-1 gap-md-2">
                          <Link
                            href={`/clients/${client.id}/edit`}
                            className="btn btn-sm btn-outline-primary"
                          >
                            Edit
                          </Link>
                          <button
                            onClick={() => handleDelete(client.id)}
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
                data-qa="clients-pagination-previous-button"
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
                data-qa="clients-pagination-next-button"
              >
                Next
              </Button>
            </div>
          )}
        </>
      ) : (
        <div className="card shadow-sm text-center py-5">
          <div className="card-body">
            <p className="text-muted mb-4">No clients found.</p>
          </div>
        </div>
      )}
      <StatusBar />
    </div>
  );
}
