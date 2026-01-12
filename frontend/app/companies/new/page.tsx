'use client';

import { useState } from 'react';
import { useCreateCompany } from '@/lib/hooks/use-companies';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { useRouter } from 'next/navigation';
import Link from 'next/link';

export default function NewCompanyPage() {
  const router = useRouter();
  const createMutation = useCreateCompany();
  const [formData, setFormData] = useState({
    name: '',
    address: '',
    city: '',
    state: '',
    zip: '',
    country: 'United States',
    job_type: 'Technology',
    created_by: 'user@example.com',
    modified_by: 'user@example.com',
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const result = await createMutation.mutateAsync(formData);
      router.push(`/companies/${result.id}`);
    } catch (error) {
      console.error('Create failed:', error);
      alert('Failed to create company');
    }
  };

  return (
    <div className="container py-4">
      <div className="mb-3">
        <Link href="/companies" className="text-decoration-none">
          ← Back to Companies
        </Link>
      </div>

      <div className="card shadow-sm">
        <div className="card-header">
          <h1 className="h3 mb-0" data-qa="company-create-title">New Company</h1>
        </div>
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <Input
              label="Company Name"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="Acme Corporation"
              required
              data-qa="company-create-name"
            />

            <Input
              label="Address"
              value={formData.address}
              onChange={(e) => setFormData({ ...formData, address: e.target.value })}
              placeholder="123 Main St"
              data-qa="company-create-address"
            />

            <div className="row">
              <div className="col-md-6">
                <Input
                  label="City"
                  value={formData.city}
                  onChange={(e) => setFormData({ ...formData, city: e.target.value })}
                  placeholder="San Francisco"
                  data-qa="company-create-city"
                />
              </div>
              <div className="col-md-6">
                <Input
                  label="State"
                  value={formData.state}
                  onChange={(e) => setFormData({ ...formData, state: e.target.value })}
                  placeholder="CA"
                  data-qa="company-create-state"
                />
              </div>
            </div>

            <div className="row">
              <div className="col-md-6">
                <Input
                  label="ZIP Code"
                  value={formData.zip}
                  onChange={(e) => setFormData({ ...formData, zip: e.target.value })}
                  placeholder="94102"
                  data-qa="company-create-zip"
                />
              </div>
              <div className="col-md-6">
                <div className="mb-3">
                  <label className="form-label">Country</label>
                  <select
                    value={formData.country}
                    onChange={(e) => setFormData({ ...formData, country: e.target.value })}
                    className="form-select"
                    data-qa="company-create-country"
                  >
                    <option value="United States">United States</option>
                    <option value="Canada">Canada</option>
                    <option value="United Kingdom">United Kingdom</option>
                  </select>
                </div>
              </div>
            </div>

            <div className="mb-3">
              <label className="form-label">Job Type / Industry</label>
              <select
                value={formData.job_type}
                onChange={(e) => setFormData({ ...formData, job_type: e.target.value })}
                className="form-select"
                data-qa="company-create-job-type"
              >
                <option value="Technology">Technology</option>
                <option value="Finance">Finance</option>
                <option value="Healthcare">Healthcare</option>
                <option value="Education">Education</option>
              </select>
            </div>

            <div className="d-flex flex-column flex-sm-row gap-2 pt-3">
              <Button type="submit" disabled={createMutation.isPending} className="w-100 w-sm-auto" data-qa="company-create-submit-button">
                {createMutation.isPending ? 'Creating...' : 'Create Company'}
              </Button>
              <Link href="/companies" className="w-100 w-sm-auto">
                <Button type="button" variant="secondary" className="w-100 w-sm-auto" data-qa="company-create-cancel-button">Cancel</Button>
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
