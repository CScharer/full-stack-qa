'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useCreateJobSearchSite } from '@/lib/hooks/use-job-search-sites';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { JobSearchSiteCreate } from '@/lib/types/job-search-site';

export default function NewJobSearchSitePage() {
  const router = useRouter();
  const createMutation = useCreateJobSearchSite();
  const [formData, setFormData] = useState<JobSearchSiteCreate>({
    name: '',
    url: '',
    created_by: 'current-user',
    modified_by: 'current-user',
  });
  const [error, setError] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!formData.name.trim()) {
      setError('Name is required');
      return;
    }

    try {
      await createMutation.mutateAsync(formData);
      router.push('/job-search-sites');
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to create job search site');
    }
  };

  return (
    <div className="container py-3 py-md-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0">New Job Search Site</h1>
        <Button variant="secondary" onClick={() => router.back()} className="w-100 w-md-auto" data-qa="job-search-site-create-cancel-button">
          Cancel
        </Button>
      </div>

      <div className="card shadow-sm">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">Site Details</h5>
        </div>
        <div className="card-body">
          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
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
              label="URL"
              type="url"
              value={formData.url || ''}
              onChange={(e) => setFormData({ ...formData, url: e.target.value })}
            />

            <div className="d-flex flex-column flex-sm-row gap-2">
              <Button type="submit" disabled={createMutation.isPending} className="w-100 w-sm-auto" data-qa="job-search-site-create-submit-button">
                {createMutation.isPending ? 'Creating...' : 'Create Job Search Site'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => router.back()} className="w-100 w-sm-auto" data-qa="job-search-site-create-cancel-button-bottom">
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
