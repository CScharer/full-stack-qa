'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useCreateContact } from '@/lib/hooks/use-contacts';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { ContactCreate, ContactEmailCreate, ContactPhoneCreate } from '@/lib/types/contact';

export default function NewContactPage() {
  const router = useRouter();
  const createMutation = useCreateContact();
  const [formData, setFormData] = useState<ContactCreate>({
    first_name: '',
    last_name: '',
    title: '',
    linkedin: '',
    contact_type: 'Recruiter',
    company_id: undefined,
    application_id: undefined,
    client_id: undefined,
    emails: [],
    phones: [],
    created_by: 'current-user',
    modified_by: 'current-user',
  });
  const [emails, setEmails] = useState<ContactEmailCreate[]>([]);
  const [phones, setPhones] = useState<ContactPhoneCreate[]>([]);
  const [error, setError] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!formData.first_name.trim() || !formData.last_name.trim()) {
      setError('First name and last name are required');
      return;
    }

    try {
      const contactData: ContactCreate = {
        ...formData,
        emails: emails.length > 0 ? emails : undefined,
        phones: phones.length > 0 ? phones : undefined,
      };
      await createMutation.mutateAsync(contactData);
      router.push('/contacts');
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to create contact');
    }
  };

  const addEmail = () => {
    setEmails([...emails, { email: '', email_type: 'Work', is_primary: emails.length === 0 ? 1 : 0 }]);
  };

  const removeEmail = (index: number) => {
    setEmails(emails.filter((_, i) => i !== index));
  };

  const updateEmail = (index: number, field: keyof ContactEmailCreate, value: any) => {
    const updated = [...emails];
    updated[index] = { ...updated[index], [field]: value };
    setEmails(updated);
  };

  const addPhone = () => {
    setPhones([...phones, { phone: '', phone_type: 'Work', is_primary: phones.length === 0 ? 1 : 0 }]);
  };

  const removePhone = (index: number) => {
    setPhones(phones.filter((_, i) => i !== index));
  };

  const updatePhone = (index: number, field: keyof ContactPhoneCreate, value: any) => {
    const updated = [...phones];
    updated[index] = { ...updated[index], [field]: value };
    setPhones(updated);
  };

  return (
    <div className="container py-3 py-md-4" data-qa="contact-create-page">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0" data-qa="contact-create-title">New Contact</h1>
        <Button variant="secondary" onClick={() => router.back()} className="w-100 w-md-auto" data-qa="contact-create-cancel-button">
          Cancel
        </Button>
      </div>

      <div className="card shadow-sm" data-qa="contact-create-form-card">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">Contact Details</h5>
        </div>
        <div className="card-body">
          {error && (
            <div className="alert alert-danger" role="alert" data-qa="contact-create-error">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} data-qa="contact-create-form">
            <div className="row">
              <div className="col-md-6">
                <Input
                  label="First Name *"
                  value={formData.first_name}
                  onChange={(e) => setFormData({ ...formData, first_name: e.target.value })}
                  required
                  data-qa="contact-first-name"
                />
              </div>
              <div className="col-md-6">
                <Input
                  label="Last Name *"
                  value={formData.last_name}
                  onChange={(e) => setFormData({ ...formData, last_name: e.target.value })}
                  required
                  data-qa="contact-last-name"
                />
              </div>
            </div>

            <Input
              label="Title"
              value={formData.title || ''}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              data-qa="contact-create-title"
            />

            <Input
              label="LinkedIn URL"
              type="url"
              value={formData.linkedin || ''}
              onChange={(e) => setFormData({ ...formData, linkedin: e.target.value })}
              data-qa="contact-create-linkedin"
            />

            <div className="mb-3">
              <label className="form-label" data-qa="contact-create-type-label">Contact Type *</label>
              <select
                className="form-select"
                value={formData.contact_type}
                onChange={(e) => setFormData({ ...formData, contact_type: e.target.value })}
                required
                data-qa="contact-create-type"
              >
                <option value="Recruiter">Recruiter</option>
                <option value="Manager">Manager</option>
                <option value="Lead">Lead</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <Input
              label="Company ID"
              type="number"
              value={formData.company_id || ''}
              onChange={(e) => setFormData({ ...formData, company_id: e.target.value ? parseInt(e.target.value) : undefined })}
              data-qa="contact-create-company-id"
            />

            <Input
              label="Application ID"
              type="number"
              value={formData.application_id || ''}
              onChange={(e) => setFormData({ ...formData, application_id: e.target.value ? parseInt(e.target.value) : undefined })}
              data-qa="contact-create-application-id"
            />

            <Input
              label="Client ID"
              type="number"
              value={formData.client_id || ''}
              onChange={(e) => setFormData({ ...formData, client_id: e.target.value ? parseInt(e.target.value) : undefined })}
              data-qa="contact-create-client-id"
            />

            <div className="mb-4">
              <div className="d-flex justify-content-between align-items-center mb-2">
                <label className="form-label mb-0">Email Addresses</label>
                <Button type="button" variant="outline-primary" size="sm" onClick={addEmail} data-qa="contact-create-add-email-button">
                  Add Email
                </Button>
              </div>
              {emails.map((email, index) => (
                <div key={index} className="card mb-2" data-qa={`contact-create-email-${index}`}>
                  <div className="card-body">
                    <div className="row g-2">
                      <div className="col-md-6">
                        <Input
                          type="email"
                          placeholder="Email address"
                          value={email.email}
                          onChange={(e) => updateEmail(index, 'email', e.target.value)}
                          data-qa={`contact-create-email-${index}-address`}
                        />
                      </div>
                      <div className="col-md-4">
                        <select
                          className="form-select"
                          value={email.email_type}
                          onChange={(e) => updateEmail(index, 'email_type', e.target.value)}
                          data-qa={`contact-create-email-${index}-type`}
                        >
                          <option value="Work">Work</option>
                          <option value="Personal">Personal</option>
                        </select>
                      </div>
                      <div className="col-md-2">
                        <div className="d-flex gap-2">
                          <input
                            type="checkbox"
                            className="form-check-input mt-2"
                            checked={email.is_primary === 1}
                            onChange={(e) => updateEmail(index, 'is_primary', e.target.checked ? 1 : 0)}
                            data-qa={`contact-create-email-${index}-primary`}
                          />
                          <Button
                            type="button"
                            variant="danger"
                            size="sm"
                            onClick={() => removeEmail(index)}
                            data-qa={`contact-create-email-${index}-remove-button`}
                          >
                            Remove
                          </Button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            <div className="mb-4">
              <div className="d-flex justify-content-between align-items-center mb-2">
                <label className="form-label mb-0">Phone Numbers</label>
                <Button type="button" variant="outline-primary" size="sm" onClick={addPhone} data-qa="contact-create-add-phone-button">
                  Add Phone
                </Button>
              </div>
              {phones.map((phone, index) => (
                <div key={index} className="card mb-2" data-qa={`contact-create-phone-${index}`}>
                  <div className="card-body">
                    <div className="row g-2">
                      <div className="col-md-6">
                        <Input
                          type="tel"
                          placeholder="Phone number"
                          value={phone.phone}
                          onChange={(e) => updatePhone(index, 'phone', e.target.value)}
                          data-qa={`contact-create-phone-${index}-number`}
                        />
                      </div>
                      <div className="col-md-4">
                        <select
                          className="form-select"
                          value={phone.phone_type}
                          onChange={(e) => updatePhone(index, 'phone_type', e.target.value)}
                          data-qa={`contact-create-phone-${index}-type`}
                        >
                          <option value="Work">Work</option>
                          <option value="Home">Home</option>
                          <option value="Cell">Cell</option>
                        </select>
                      </div>
                      <div className="col-md-2">
                        <div className="d-flex gap-2">
                          <input
                            type="checkbox"
                            className="form-check-input mt-2"
                            checked={phone.is_primary === 1}
                            onChange={(e) => updatePhone(index, 'is_primary', e.target.checked ? 1 : 0)}
                            data-qa={`contact-create-phone-${index}-primary`}
                          />
                          <Button
                            type="button"
                            variant="danger"
                            size="sm"
                            onClick={() => removePhone(index)}
                            data-qa={`contact-create-phone-${index}-remove-button`}
                          >
                            Remove
                          </Button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            <div className="d-flex flex-column flex-sm-row gap-2">
              <Button type="submit" disabled={createMutation.isPending} className="w-100 w-sm-auto" data-qa="contact-create-submit-button">
                {createMutation.isPending ? 'Creating...' : 'Create Contact'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => router.back()} className="w-100 w-sm-auto" data-qa="contact-create-cancel-button-bottom">
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
