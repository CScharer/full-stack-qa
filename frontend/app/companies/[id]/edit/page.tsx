'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useCompany, useUpdateCompany } from '@/lib/hooks/use-companies';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { CompanyUpdate } from '@/lib/types/company';

export default function EditCompanyPage({ params }: { params: { id: string } }) {
  const companyId = parseInt(params.id);
  const router = useRouter();
  const { data: company, isLoading, error, refetch } = useCompany(companyId);
  const updateMutation = useUpdateCompany();
  const [formData, setFormData] = useState<CompanyUpdate>({
    name: '',
    address: '',
    city: '',
    state: '',
    zip: '',
    country: '',
    job_type: '',
    modified_by: 'current-user',
  });
  const [formError, setFormError] = useState<string>('');

  useEffect(() => {
    if (company) {
      setFormData({
        name: company.name || '',
        address: company.address || '',
        city: company.city || '',
        state: company.state || '',
        zip: company.zip || '',
        country: company.country || '',
        job_type: company.job_type || '',
        modified_by: 'current-user',
      });
    }
  }, [company]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError('');

    if (!formData.name || !formData.name.trim()) {
      setFormError('Name is required');
      return;
    }

    try {
      await updateMutation.mutateAsync({ id: companyId, company: formData });
      router.push(`/companies/${companyId}`);
    } catch (err: any) {
      setFormError(err.response?.data?.error || 'Failed to update company');
    }
  };

  if (isLoading) return <Loading message="Loading company..." />;
  if (error) return <Error message="Failed to load company" onRetry={() => refetch()} />;
  if (!company) return <Error message="Company not found" />;

  return (
    <div className="container py-3 py-md-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0">Edit Company</h1>
        <Button variant="secondary" onClick={() => router.back()} className="w-100 w-md-auto" data-qa={`company-edit-${params.id}-cancel-button`}>
          Cancel
        </Button>
      </div>

      <div className="card shadow-sm">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">Company Details</h5>
        </div>
        <div className="card-body">
          {formError && (
            <div className="alert alert-danger" role="alert">
              {formError}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <Input
              label="Name *"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />

            <Input
              label="Address"
              value={formData.address || ''}
              onChange={(e) => setFormData({ ...formData, address: e.target.value })}
            />

            <Input
              label="City"
              value={formData.city || ''}
              onChange={(e) => setFormData({ ...formData, city: e.target.value })}
            />

            <Input
              label="State"
              value={formData.state || ''}
              onChange={(e) => setFormData({ ...formData, state: e.target.value })}
            />

            <Input
              label="ZIP"
              value={formData.zip || ''}
              onChange={(e) => setFormData({ ...formData, zip: e.target.value })}
            />

            <Input
              label="Country"
              value={formData.country || ''}
              onChange={(e) => setFormData({ ...formData, country: e.target.value })}
            />

            <Input
              label="Job Type"
              value={formData.job_type || ''}
              onChange={(e) => setFormData({ ...formData, job_type: e.target.value })}
            />

            <div className="d-flex flex-column flex-sm-row gap-2">
              <Button type="submit" disabled={updateMutation.isPending} className="w-100 w-sm-auto" data-qa={`company-edit-${params.id}-submit-button`}>
                {updateMutation.isPending ? 'Updating...' : 'Update Company'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => router.back()} className="w-100 w-sm-auto" data-qa={`company-edit-${params.id}-cancel-button-bottom`}>
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
