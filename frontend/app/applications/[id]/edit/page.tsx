'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useApplication, useUpdateApplication } from '@/lib/hooks/use-applications';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { ApplicationUpdate } from '@/lib/types/application';

export default function EditApplicationPage({ params }: { params: { id: string } }) {
  const applicationId = parseInt(params.id);
  const router = useRouter();
  const { data: application, isLoading, error, refetch } = useApplication(applicationId);
  const updateMutation = useUpdateApplication();
  const [formData, setFormData] = useState<ApplicationUpdate>({
    status: '',
    requirement: '',
    work_setting: '',
    compensation: '',
    position: '',
    job_description: '',
    job_link: '',
    location: '',
    resume: '',
    cover_letter: '',
    entered_iwd: 0,
    date_close: '',
    company_id: undefined,
    client_id: undefined,
    modified_by: 'current-user',
  });
  const [formError, setFormError] = useState<string>('');

  useEffect(() => {
    if (application) {
      setFormData({
        status: application.status || '',
        requirement: application.requirement || '',
        work_setting: application.work_setting || '',
        compensation: application.compensation || '',
        position: application.position || '',
        job_description: application.job_description || '',
        job_link: application.job_link || '',
        location: application.location || '',
        resume: application.resume || '',
        cover_letter: application.cover_letter || '',
        entered_iwd: application.entered_iwd || 0,
        date_close: application.date_close || '',
        company_id: application.company_id,
        client_id: application.client_id,
        modified_by: 'current-user',
      });
    }
  }, [application]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError('');

    if (!formData.status || !formData.status.trim()) {
      setFormError('Status is required');
      return;
    }

    if (!formData.work_setting || !formData.work_setting.trim()) {
      setFormError('Work setting is required');
      return;
    }

    try {
      await updateMutation.mutateAsync({ id: applicationId, application: formData });
      router.push(`/applications/${applicationId}`);
    } catch (err: any) {
      setFormError(err.response?.data?.error || 'Failed to update application');
    }
  };

  if (isLoading) return <Loading message="Loading application..." />;
  if (error) return <Error message="Failed to load application" onRetry={() => refetch()} />;
  if (!application) return <Error message="Application not found" />;

  return (
    <div className="container py-3 py-md-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0">Edit Application</h1>
        <Button variant="secondary" onClick={() => router.back()} className="w-100 w-md-auto" data-qa={`application-edit-${params.id}-cancel-button`}>
          Cancel
        </Button>
      </div>

      <div className="card shadow-sm">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">Application Details</h5>
        </div>
        <div className="card-body">
          {formError && (
            <div className="alert alert-danger" role="alert">
              {formError}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Status *</label>
              <select
                className="form-select"
                value={formData.status}
                onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                required
              >
                <option value="Pending">Pending</option>
                <option value="Applied">Applied</option>
                <option value="Interview">Interview</option>
                <option value="Offer">Offer</option>
                <option value="Rejected">Rejected</option>
                <option value="Withdrawn">Withdrawn</option>
              </select>
            </div>

            <div className="mb-3">
              <label className="form-label">Work Setting *</label>
              <select
                className="form-select"
                value={formData.work_setting}
                onChange={(e) => setFormData({ ...formData, work_setting: e.target.value })}
                required
              >
                <option value="Remote">Remote</option>
                <option value="Hybrid">Hybrid</option>
                <option value="On-site">On-site</option>
              </select>
            </div>

            <Input
              label="Position"
              value={formData.position || ''}
              onChange={(e) => setFormData({ ...formData, position: e.target.value })}
            />

            <Input
              label="Requirement"
              value={formData.requirement || ''}
              onChange={(e) => setFormData({ ...formData, requirement: e.target.value })}
            />

            <Input
              label="Compensation"
              value={formData.compensation || ''}
              onChange={(e) => setFormData({ ...formData, compensation: e.target.value })}
            />

            <Input
              label="Location"
              value={formData.location || ''}
              onChange={(e) => setFormData({ ...formData, location: e.target.value })}
            />

            <Input
              label="Job Link"
              type="url"
              value={formData.job_link || ''}
              onChange={(e) => setFormData({ ...formData, job_link: e.target.value })}
            />

            <div className="mb-3">
              <label className="form-label">Job Description</label>
              <textarea
                className="form-control"
                rows={5}
                value={formData.job_description || ''}
                onChange={(e) => setFormData({ ...formData, job_description: e.target.value })}
              />
            </div>

            <Input
              label="Resume"
              value={formData.resume || ''}
              onChange={(e) => setFormData({ ...formData, resume: e.target.value })}
            />

            <Input
              label="Cover Letter"
              value={formData.cover_letter || ''}
              onChange={(e) => setFormData({ ...formData, cover_letter: e.target.value })}
            />

            <Input
              label="Company ID"
              type="number"
              value={formData.company_id || ''}
              onChange={(e) => setFormData({ ...formData, company_id: e.target.value ? parseInt(e.target.value) : undefined })}
            />

            <Input
              label="Client ID"
              type="number"
              value={formData.client_id || ''}
              onChange={(e) => setFormData({ ...formData, client_id: e.target.value ? parseInt(e.target.value) : undefined })}
            />

            <Input
              label="Entered IWD"
              type="number"
              value={formData.entered_iwd || 0}
              onChange={(e) => setFormData({ ...formData, entered_iwd: parseInt(e.target.value) || 0 })}
            />

            <Input
              label="Date Close"
              type="date"
              value={formData.date_close || ''}
              onChange={(e) => setFormData({ ...formData, date_close: e.target.value })}
            />

            <div className="d-flex flex-column flex-sm-row gap-2">
              <Button type="submit" disabled={updateMutation.isPending} className="w-100 w-sm-auto" data-qa={`application-edit-${params.id}-submit-button`}>
                {updateMutation.isPending ? 'Updating...' : 'Update Application'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => router.back()} className="w-100 w-sm-auto" data-qa={`application-edit-${params.id}-cancel-button-bottom`}>
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
