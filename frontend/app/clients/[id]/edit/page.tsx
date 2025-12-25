'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useClient, useUpdateClient } from '@/lib/hooks/use-clients';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { ClientUpdate } from '@/lib/types/client';

export default function EditClientPage({ params }: { params: { id: string } }) {
  const clientId = parseInt(params.id);
  const router = useRouter();
  const { data: client, isLoading, error, refetch } = useClient(clientId);
  const updateMutation = useUpdateClient();
  const [formData, setFormData] = useState<ClientUpdate>({
    name: '',
    modified_by: 'current-user',
  });
  const [formError, setFormError] = useState<string>('');

  useEffect(() => {
    if (client) {
      setFormData({
        name: client.name || '',
        modified_by: 'current-user',
      });
    }
  }, [client]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError('');

    try {
      await updateMutation.mutateAsync({ id: clientId, client: formData });
      router.push(`/clients/${clientId}`);
    } catch (err: any) {
      setFormError(err.response?.data?.error || 'Failed to update client');
    }
  };

  if (isLoading) return <Loading message="Loading client..." />;
  if (error) return <Error message="Failed to load client" onRetry={() => refetch()} />;
  if (!client) return <Error message="Client not found" />;

  return (
    <div className="container py-3 py-md-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0">Edit Client</h1>
        <Button variant="secondary" onClick={() => router.back()} className="w-100 w-md-auto" data-qa={`client-edit-${params.id}-cancel-button`}>
          Cancel
        </Button>
      </div>

      <div className="card shadow-sm">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">Client Details</h5>
        </div>
        <div className="card-body">
          {formError && (
            <div className="alert alert-danger" role="alert">
              {formError}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <Input
              label="Name"
              value={formData.name || ''}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            />

            <div className="d-flex flex-column flex-sm-row gap-2">
              <Button type="submit" disabled={updateMutation.isPending} className="w-100 w-sm-auto" data-qa={`client-edit-${params.id}-submit-button`}>
                {updateMutation.isPending ? 'Updating...' : 'Update Client'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => router.back()} className="w-100 w-sm-auto" data-qa={`client-edit-${params.id}-cancel-button-bottom`}>
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
