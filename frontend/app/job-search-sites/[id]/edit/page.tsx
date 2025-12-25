'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useJobSearchSite, useUpdateJobSearchSite } from '@/lib/hooks/use-job-search-sites';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { JobSearchSiteUpdate } from '@/lib/types/job-search-site';

export default function EditJobSearchSitePage({ params }: { params: { id: string } }) {
  const siteId = parseInt(params.id);
  const router = useRouter();
  const { data: site, isLoading, error, refetch } = useJobSearchSite(siteId);
  const updateMutation = useUpdateJobSearchSite();
  const [formData, setFormData] = useState<JobSearchSiteUpdate>({
    name: '',
    url: '',
    modified_by: 'current-user',
  });
  const [formError, setFormError] = useState<string>('');

  useEffect(() => {
    if (site) {
      setFormData({
        name: site.name || '',
        url: site.url || '',
        modified_by: 'current-user',
      });
    }
  }, [site]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError('');

    if (!formData.name?.trim()) {
      setFormError('Name is required');
      return;
    }

    try {
      await updateMutation.mutateAsync({ id: siteId, site: formData });
      router.push(`/job-search-sites/${siteId}`);
    } catch (err: any) {
      setFormError(err.response?.data?.error || 'Failed to update job search site');
    }
  };

  if (isLoading) return <Loading message="Loading job search site..." />;
  if (error) return <Error message="Failed to load job search site" onRetry={() => refetch()} />;
  if (!site) return <Error message="Job search site not found" />;

  return (
    <div className="container py-3 py-md-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0">Edit Job Search Site</h1>
        <Button variant="secondary" onClick={() => router.back()} className="w-100 w-md-auto" data-qa={`job-search-site-edit-${params.id}-cancel-button`}>
          Cancel
        </Button>
      </div>

      <div className="card shadow-sm">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">Site Details</h5>
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
              value={formData.name || ''}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />

            <Input
              label="URL"
              type="url"
              value={formData.url || ''}
              onChange={(e) => setFormData({ ...formData, url: e.target.value })}
            />

            <div className="d-flex flex-column flex-sm-row gap-2">
              <Button type="submit" disabled={updateMutation.isPending} className="w-100 w-sm-auto" data-qa={`job-search-site-edit-${params.id}-submit-button`}>
                {updateMutation.isPending ? 'Updating...' : 'Update Job Search Site'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => router.back()} className="w-100 w-sm-auto" data-qa={`job-search-site-edit-${params.id}-cancel-button-bottom`}>
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
