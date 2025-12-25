'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useContact, useUpdateContact } from '@/lib/hooks/use-contacts';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import { ContactUpdate, ContactEmailCreate, ContactPhoneCreate } from '@/lib/types/contact';
import { use } from 'react';

export default function EditContactPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const contactId = parseInt(id);
  const router = useRouter();
  const { data: contact, isLoading, error, refetch } = useContact(contactId);
  const updateMutation = useUpdateContact();
  const [formData, setFormData] = useState<ContactUpdate>({
    first_name: '',
    last_name: '',
    title: '',
    linkedin: '',
    contact_type: 'Recruiter',
    company_id: undefined,
    application_id: undefined,
    client_id: undefined,
    modified_by: 'current-user',
  });
  const [emails, setEmails] = useState<ContactEmailCreate[]>([]);
  const [phones, setPhones] = useState<ContactPhoneCreate[]>([]);
  const [formError, setFormError] = useState<string>('');

  useEffect(() => {
    if (contact) {
      setFormData({
        first_name: contact.first_name || '',
        last_name: contact.last_name || '',
        title: contact.title || '',
        linkedin: contact.linkedin || '',
        contact_type: contact.contact_type || 'Recruiter',
        company_id: contact.company_id,
        application_id: contact.application_id,
        client_id: contact.client_id,
        modified_by: 'current-user',
      });

      // Load emails and phones if available
      if ('emails' in contact && contact.emails) {
        setEmails(contact.emails.map(email => ({
          email: email.email,
          email_type: email.email_type,
          is_primary: email.is_primary,
        })));
      }

      if ('phones' in contact && contact.phones) {
        setPhones(contact.phones.map(phone => ({
          phone: phone.phone,
          phone_type: phone.phone_type,
          is_primary: phone.is_primary,
        })));
      }
    }
  }, [contact]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError('');

    if (!formData.first_name?.trim() || !formData.last_name?.trim()) {
      setFormError('First name and last name are required');
      return;
    }

    try {
      await updateMutation.mutateAsync({ id: contactId, contact: formData });
      router.push(`/contacts/${contactId}`);
    } catch (err: any) {
      setFormError(err.response?.data?.error || 'Failed to update contact');
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

  if (isLoading) return <Loading message="Loading contact..." />;
  if (error) return <Error message="Failed to load contact" onRetry={() => refetch()} />;
  if (!contact) return <Error message="Contact not found" />;

  return (
    <div className="container py-3 py-md-4" data-qa={`contact-edit-page-${contactId}`}>
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0" data-qa="contact-edit-title">Edit Contact</h1>
        <Button variant="secondary" onClick={() => router.back()} className="w-100 w-md-auto" data-qa="contact-edit-cancel-button">
          Cancel
        </Button>
      </div>

      <div className="card shadow-sm" data-qa="contact-edit-form-card">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">Contact Details</h5>
        </div>
        <div className="card-body">
          {formError && (
            <div className="alert alert-danger" role="alert" data-qa="contact-edit-error">
              {formError}
            </div>
          )}

          <form onSubmit={handleSubmit} data-qa="contact-edit-form">
            <div className="row">
              <div className="col-md-6">
                <Input
                  label="First Name *"
                  value={formData.first_name || ''}
                  onChange={(e) => setFormData({ ...formData, first_name: e.target.value })}
                  required
                  data-qa="contact-first-name"
                />
              </div>
              <div className="col-md-6">
                <Input
                  label="Last Name *"
                  value={formData.last_name || ''}
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
              data-qa={`contact-edit-${contactId}-title`}
            />

            <Input
              label="LinkedIn URL"
              type="url"
              value={formData.linkedin || ''}
              onChange={(e) => setFormData({ ...formData, linkedin: e.target.value })}
              data-qa={`contact-edit-${contactId}-linkedin`}
            />

            <div className="mb-3">
              <label className="form-label" data-qa={`contact-edit-${contactId}-type-label`}>Contact Type *</label>
              <select
                className="form-select"
                value={formData.contact_type}
                onChange={(e) => setFormData({ ...formData, contact_type: e.target.value })}
                required
                data-qa={`contact-edit-${contactId}-type`}
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
              data-qa={`contact-edit-${contactId}-company-id`}
            />

            <Input
              label="Application ID"
              type="number"
              value={formData.application_id || ''}
              onChange={(e) => setFormData({ ...formData, application_id: e.target.value ? parseInt(e.target.value) : undefined })}
              data-qa={`contact-edit-${contactId}-application-id`}
            />

            <Input
              label="Client ID"
              type="number"
              value={formData.client_id || ''}
              onChange={(e) => setFormData({ ...formData, client_id: e.target.value ? parseInt(e.target.value) : undefined })}
              data-qa={`contact-edit-${contactId}-client-id`}
            />

            <div className="mb-4">
              <div className="d-flex justify-content-between align-items-center mb-2">
                <label className="form-label mb-0">Email Addresses</label>
                <Button type="button" variant="outline-primary" size="sm" onClick={addEmail} data-qa={`contact-edit-${contactId}-add-email-button`}>
                  Add Email
                </Button>
              </div>
              {emails.map((email, index) => (
                <div key={index} className="card mb-2" data-qa={`contact-edit-${contactId}-email-${index}`}>
                  <div className="card-body">
                    <div className="row g-2">
                      <div className="col-md-6">
                        <Input
                          type="email"
                          placeholder="Email address"
                          value={email.email}
                          onChange={(e) => updateEmail(index, 'email', e.target.value)}
                          data-qa={`contact-edit-${contactId}-email-${index}-address`}
                        />
                      </div>
                      <div className="col-md-4">
                        <select
                          className="form-select"
                          value={email.email_type}
                          onChange={(e) => updateEmail(index, 'email_type', e.target.value)}
                          data-qa={`contact-edit-${contactId}-email-${index}-type`}
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
                            data-qa={`contact-edit-${contactId}-email-${index}-primary`}
                          />
                          <Button
                            type="button"
                            variant="danger"
                            size="sm"
                            onClick={() => removeEmail(index)}
                            data-qa={`contact-edit-${contactId}-email-${index}-remove-button`}
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
                <Button type="button" variant="outline-primary" size="sm" onClick={addPhone} data-qa={`contact-edit-${contactId}-add-phone-button`}>
                  Add Phone
                </Button>
              </div>
              {phones.map((phone, index) => (
                <div key={index} className="card mb-2" data-qa={`contact-edit-${contactId}-phone-${index}`}>
                  <div className="card-body">
                    <div className="row g-2">
                      <div className="col-md-6">
                        <Input
                          type="tel"
                          placeholder="Phone number"
                          value={phone.phone}
                          onChange={(e) => updatePhone(index, 'phone', e.target.value)}
                          data-qa={`contact-edit-${contactId}-phone-${index}-number`}
                        />
                      </div>
                      <div className="col-md-4">
                        <select
                          className="form-select"
                          value={phone.phone_type}
                          onChange={(e) => updatePhone(index, 'phone_type', e.target.value)}
                          data-qa={`contact-edit-${contactId}-phone-${index}-type`}
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
                            data-qa={`contact-edit-${contactId}-phone-${index}-primary`}
                          />
                          <Button
                            type="button"
                            variant="danger"
                            size="sm"
                            onClick={() => removePhone(index)}
                            data-qa={`contact-edit-${contactId}-phone-${index}-remove-button`}
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
              <Button type="submit" disabled={updateMutation.isPending} className="w-100 w-sm-auto" data-qa="contact-edit-submit-button">
                {updateMutation.isPending ? 'Updating...' : 'Update Contact'}
              </Button>
              <Button type="button" variant="secondary" onClick={() => router.back()} className="w-100 w-sm-auto" data-qa="contact-edit-cancel-button-bottom">
                Cancel
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
