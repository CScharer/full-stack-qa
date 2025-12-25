'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useCreateClient } from '@/lib/hooks/use-clients';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { ClientCreate } from '@/lib/types/client';

export default function NewClientPage() {
  const router = useRouter();
  const createMutation = useCreateClient();
  const [formData, setFormData] = useState<ClientCreate>({
    name: '',
    created_by: 'current-user',
    modified_by: 'current-user',
  });
  const [error, setError] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      await createMutation.mutateAsync(formData);
      router.push('/clients');
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to create client');
    }
  };

  return (
    <div className="container py-3 py-md-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0">New Client</h1>
        <Button variant="secondary" onClick={() => router.back()} className="w-100 w-md-auto" data-qa="client-create-cancel-button">
          Cancel
        </Button>
      </div>

      <div className="card shadow-sm">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">Client Details</h5>
        </div>
        <div className="card-body">
          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <Input
              label="Name"
              value={formData.name || ''}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            />

            <div className="d-flex flex-column flex-sm-row gap-2">
              <Button type="submit" disabled={createMutation.isPending} className="w-100 w-sm-auto" data-qa="client-create-submit-button">
                {createMutation.isPending ? 'Creating...' : 'Create Client'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => router.back()} className="w-100 w-sm-auto" data-qa="client-create-cancel-button-bottom">
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
