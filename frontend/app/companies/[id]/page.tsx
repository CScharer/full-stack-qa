'use client';

import { useCompany, useDeleteCompany } from '@/lib/hooks/use-companies';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { Button } from '@/components/ui/Button';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

export default function CompanyDetailPage({ params }: { params: { id: string } }) {
  const router = useRouter();
  const companyId = parseInt(params.id);
  const { data: company, isLoading, error, refetch } = useCompany(companyId);
  const deleteMutation = useDeleteCompany();

  const handleDelete = async () => {
    if (confirm('Are you sure you want to delete this company? This will set company_id to NULL for linked applications and contacts.')) {
      try {
        await deleteMutation.mutateAsync(companyId);
        router.push('/companies');
      } catch (err) {
        console.error('Delete failed:', err);
      }
    }
  };

  if (isLoading) return <Loading message="Loading company..." />;
  if (error) return <Error message="Failed to load company" onRetry={() => refetch()} />;
  if (!company) return <Error message="Company not found" />;

  return (
    <div className="container py-3 py-md-4">
      <div className="mb-2 mb-md-3">
        <Link href="/companies" className="text-decoration-none">
          ← Back to Companies
        </Link>
      </div>

      <div className="card shadow-sm">
        <div className="card-header d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-2">
          <div>
            <h1 className="h4 h-md-3 mb-0">
              {company.name}
            </h1>
          </div>
          <div className="d-flex gap-2 w-100 w-md-auto">
            <Link href={`/companies/${company.id}/edit`} className="flex-grow-1 flex-md-grow-0">
              <Button variant="secondary" className="w-100 w-md-auto" data-qa={`company-detail-${company.id}-edit-button`}>Edit</Button>
            </Link>
            <Button variant="danger" onClick={handleDelete} className="flex-grow-1 flex-md-grow-0 w-100 w-md-auto" data-qa={`company-detail-${company.id}-delete-button`}>
              Delete
            </Button>
          </div>
        </div>
        <div className="card-body">
          <div className="row g-3 g-md-4">
            <div className="col-12 col-md-6">
              <h2 className="h5 h-md-5 mb-2 mb-md-3">Details</h2>
              <dl className="row g-2">
                <dt className="col-12 col-sm-4">Job Type</dt>
                <dd className="col-12 col-sm-8">{company.job_type}</dd>
                <dt className="col-12 col-sm-4">Country</dt>
                <dd className="col-12 col-sm-8">{company.country}</dd>
              </dl>
            </div>

            <div className="col-12 col-md-6">
              <h2 className="h5 h-md-5 mb-2 mb-md-3">Address</h2>
              <dl className="row g-2">
                {company.address && (
                  <>
                    <dt className="col-12 col-sm-4">Street</dt>
                    <dd className="col-12 col-sm-8">{company.address}</dd>
                  </>
                )}
                {company.city && (
                  <>
                    <dt className="col-12 col-sm-4">City</dt>
                    <dd className="col-12 col-sm-8">{company.city}</dd>
                  </>
                )}
                {company.state && (
                  <>
                    <dt className="col-12 col-sm-4">State</dt>
                    <dd className="col-12 col-sm-8">{company.state}</dd>
                  </>
                )}
                {company.zip && (
                  <>
                    <dt className="col-12 col-sm-4">ZIP</dt>
                    <dd className="col-12 col-sm-8">{company.zip}</dd>
                  </>
                )}
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
