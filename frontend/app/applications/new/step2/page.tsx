'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useCreateApplication } from '@/lib/hooks/use-applications';
import { useContact, useUpdateContact } from '@/lib/hooks/use-contacts';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import Link from 'next/link';

/**
 * Step 2: Application Details
 * Industry standard: Multi-step wizard for complex forms
 */
export default function ApplicationWizardStep2() {
  const router = useRouter();
  const createMutation = useCreateApplication();
  const updateContactMutation = useUpdateContact();
  
  const [wizardData, setWizardData] = useState<{
    contactId: number;
    companyId?: number;
    clientId?: number;
  } | null>(null);

  const [formData, setFormData] = useState({
    status: 'Pending',
    work_setting: 'Remote',
    position: '',
    location: '',
    job_link: '',
    job_description: '',
    compensation: '',
    requirement: '',
    created_by: 'current-user',
    modified_by: 'current-user',
  });

  // Load wizard data from sessionStorage
  useEffect(() => {
    const stored = sessionStorage.getItem('applicationWizard');
    if (stored) {
      const data = JSON.parse(stored);
      setWizardData(data);
    } else {
      // No wizard data, redirect to step 1
      router.push('/applications/new/step1');
    }
  }, [router]);

  // Load contact details
  const { data: contact } = useContact(wizardData?.contactId || 0);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!wizardData) {
      alert('Missing contact information. Please go back to step 1.');
      return;
    }

    try {
      const applicationData = {
        ...formData,
        company_id: wizardData.companyId,
        client_id: wizardData.clientId,
      };
      
      const result = await createMutation.mutateAsync(applicationData);
      
      // Link contact to application (update contact with application_id)
      if (wizardData.contactId && result.id) {
        try {
          await updateContactMutation.mutateAsync({
            id: wizardData.contactId,
            contact: {
              application_id: result.id,
              modified_by: 'current-user',
            },
          });
        } catch (err) {
          console.warn('Could not link contact to application:', err);
          // Don't fail the whole operation if contact update fails
        }
      }
      
      // Clear wizard data
      sessionStorage.removeItem('applicationWizard');
      
      router.push(`/applications/${result.id}`);
    } catch (error) {
      console.error('Create failed:', error);
      alert('Failed to create application');
    }
  };

  if (!wizardData) {
    return (
      <div className="container py-3 py-md-4">
        <div className="alert alert-warning">
          No contact selected. <Link href="/applications/new/step1">Go back to step 1</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="container py-3 py-md-4" data-qa="application-wizard-step2-page">
      <div className="mb-2 mb-md-3">
        <Link href="/applications/new/step1" className="text-decoration-none" data-qa="wizard-step2-back-link">
          ← Back to Step 1
        </Link>
      </div>

      <div className="card shadow-sm" data-qa="wizard-step2-card">
        <div className="card-header bg-primary text-white">
          <h1 className="h4 h-md-3 mb-0" data-qa="wizard-step2-title">New Application - Step 2: Details</h1>
          <small className="text-white-50">Fill in the application details</small>
        </div>
        <div className="card-body">
          {/* Show selected contact info */}
          {contact && (
            <div className="alert alert-info mb-4" data-qa="wizard-step2-contact-info">
              <strong>Contact:</strong> {contact.name || `${contact.first_name} ${contact.last_name}`.trim()}
              {contact.title && ` (${contact.title})`}
            </div>
          )}

          <form onSubmit={handleSubmit} data-qa="wizard-step2-form">
            <Input
              label="Position"
              value={formData.position}
              onChange={(e) => setFormData({ ...formData, position: e.target.value })}
              placeholder="Software Engineer"
              data-qa="application-position"
            />

            <div className="mb-3">
              <label className="form-label" data-qa="application-status-label">Status</label>
              <select
                value={formData.status}
                onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                className="form-select"
                data-qa="application-status"
              >
                <option value="Pending">Pending</option>
                <option value="Interview">Interview</option>
                <option value="Rejected">Rejected</option>
                <option value="Accepted">Accepted</option>
              </select>
            </div>

            <div className="mb-3">
              <label className="form-label" data-qa="application-work-setting-label">Work Setting</label>
              <select
                value={formData.work_setting}
                onChange={(e) => setFormData({ ...formData, work_setting: e.target.value })}
                className="form-select"
                data-qa="application-work-setting"
              >
                <option value="Remote">Remote</option>
                <option value="Hybrid">Hybrid</option>
                <option value="On-site">On-site</option>
              </select>
            </div>

            <Input
              label="Location"
              value={formData.location}
              onChange={(e) => setFormData({ ...formData, location: e.target.value })}
              placeholder="San Francisco, CA"
              data-qa="application-location"
            />

            <Input
              label="Job Link"
              type="url"
              value={formData.job_link}
              onChange={(e) => setFormData({ ...formData, job_link: e.target.value })}
              placeholder="https://example.com/job/123"
              data-qa="application-job-link"
            />

            <Input
              label="Compensation"
              value={formData.compensation}
              onChange={(e) => setFormData({ ...formData, compensation: e.target.value })}
              placeholder="$120,000 - $150,000"
              data-qa="application-compensation"
            />

            <div className="mb-3">
              <label className="form-label" data-qa="application-requirements-label">Requirements</label>
              <textarea
                className="form-control"
                rows={3}
                value={formData.requirement}
                onChange={(e) => setFormData({ ...formData, requirement: e.target.value })}
                placeholder="Required skills, experience, etc."
                data-qa="application-requirements"
              />
            </div>

            <div className="mb-3">
              <label className="form-label" data-qa="application-job-description-label">Job Description</label>
              <textarea
                className="form-control"
                rows={6}
                value={formData.job_description}
                onChange={(e) => setFormData({ ...formData, job_description: e.target.value })}
                placeholder="Full job description..."
                data-qa="application-job-description"
              />
            </div>

            <div className="d-flex flex-column flex-sm-row gap-2 pt-3">
              <Link href="/applications/new/step1" className="w-100 w-sm-auto">
                <Button type="button" variant="secondary" className="w-100 w-sm-auto" data-qa="wizard-step2-back-button">← Back</Button>
              </Link>
              <Button
                type="submit"
                disabled={createMutation.isPending}
                className="w-100 w-sm-auto"
                data-qa="wizard-step2-submit-button"
              >
                {createMutation.isPending ? 'Creating...' : 'Create Application'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
