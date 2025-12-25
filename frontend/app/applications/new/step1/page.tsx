'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useContacts, useCreateContact } from '@/lib/hooks/use-contacts';
import { useCompanies, useCreateCompany } from '@/lib/hooks/use-companies';
import { useClients, useCreateClient } from '@/lib/hooks/use-clients';
import { EntitySelect, EntityOption } from '@/components/EntitySelect';
import { EntityCreateModal } from '@/components/EntityCreateModal';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { ContactCreate, ContactEmailCreate, ContactPhoneCreate } from '@/lib/types/contact';
import { CompanyCreate } from '@/lib/types/company';
import { ClientCreate } from '@/lib/types/client';
import Link from 'next/link';

/**
 * Step 1: Contact Selection/Creation
 * Industry standard: Multi-step wizard for complex forms
 */
export default function ApplicationWizardStep1() {
  const router = useRouter();
  
  // Hooks
  const { data: contactsData, isLoading: contactsLoading } = useContacts({ limit: 100 });
  const { data: companiesData, isLoading: companiesLoading } = useCompanies({ limit: 100 });
  const { data: clientsData, isLoading: clientsLoading } = useClients({ limit: 100 });
  
  const createContactMutation = useCreateContact();
  const createCompanyMutation = useCreateCompany();
  const createClientMutation = useCreateClient();

  // State
  const [selectedContactId, setSelectedContactId] = useState<number | null>(null);
  const [selectedCompanyId, setSelectedCompanyId] = useState<number | null>(null);
  const [selectedClientId, setSelectedClientId] = useState<number | null>(null);
  
  // Modal states
  const [showContactModal, setShowContactModal] = useState(false);
  const [showCompanyModal, setShowCompanyModal] = useState(false);
  const [showClientModal, setShowClientModal] = useState(false);
  
  // Contact creation form
  const [contactForm, setContactForm] = useState<ContactCreate>({
    first_name: '',
    last_name: '',
    title: 'Recruiter',
    contact_type: 'Recruiter',
    company_id: undefined,
    client_id: undefined,
    emails: [],
    phones: [],
    created_by: 'current-user',
    modified_by: 'current-user',
  });
  const [contactEmails, setContactEmails] = useState<ContactEmailCreate[]>([]);
  const [contactPhones, setContactPhones] = useState<ContactPhoneCreate[]>([]);
  
  // Company creation form
  const [companyForm, setCompanyForm] = useState<CompanyCreate>({
    name: '',
    country: 'United States',
    job_type: 'Technology',
    created_by: 'current-user',
    modified_by: 'current-user',
  });
  
  // Client creation form
  const [clientForm, setClientForm] = useState<ClientCreate>({
    name: '',
    created_by: 'current-user',
    modified_by: 'current-user',
  });

  // Prepare options for EntitySelect
  const contactOptions: EntityOption[] = (contactsData?.data || []).map(contact => {
    const fullName = contact.name || `${contact.first_name} ${contact.last_name}`.trim();
    return {
      id: contact.id,
      name: fullName,
      display: `${fullName}${contact.company_id ? ' (Company ID: ' + contact.company_id + ')' : ''}`,
    };
  });

  const companyOptions: EntityOption[] = (companiesData?.data || []).map(company => ({
    id: company.id,
    name: company.name,
    display: `${company.name}${company.city && company.state ? ' (' + company.city + ', ' + company.state + ')' : ''}`,
  }));

  const clientOptions: EntityOption[] = (clientsData?.data || []).map(client => ({
    id: client.id,
    name: client.name || 'Unnamed Client',
    display: client.name || 'Unnamed Client',
  }));

  // Handle company creation
  const handleCreateCompany = async () => {
    try {
      const company = await createCompanyMutation.mutateAsync(companyForm);
      setSelectedCompanyId(company.id);
      setContactForm({ ...contactForm, company_id: company.id });
      setShowCompanyModal(false);
      setCompanyForm({ name: '', country: 'United States', job_type: 'Technology', created_by: 'current-user', modified_by: 'current-user' });
    } catch (error) {
      console.error('Failed to create company:', error);
      alert('Failed to create company');
    }
  };

  // Handle client creation
  const handleCreateClient = async () => {
    try {
      const client = await createClientMutation.mutateAsync(clientForm);
      setSelectedClientId(client.id);
      setContactForm({ ...contactForm, client_id: client.id });
      setShowClientModal(false);
      setClientForm({ name: '', created_by: 'current-user', modified_by: 'current-user' });
    } catch (error) {
      console.error('Failed to create client:', error);
      alert('Failed to create client');
    }
  };

  // Handle contact creation
  const handleCreateContact = async () => {
    if (!contactForm.first_name.trim() || !contactForm.last_name.trim()) {
      alert('First name and last name are required');
      return;
    }

    try {
      const contactData: ContactCreate = {
        ...contactForm,
        emails: contactEmails.length > 0 ? contactEmails : undefined,
        phones: contactPhones.length > 0 ? contactPhones : undefined,
      };
      const contact = await createContactMutation.mutateAsync(contactData);
      setSelectedContactId(contact.id);
      setShowContactModal(false);
      // Reset form
      setContactForm({
        first_name: '',
        last_name: '',
        title: 'Recruiter',
        contact_type: 'Recruiter',
        company_id: selectedCompanyId || undefined,
        client_id: selectedClientId || undefined,
        emails: [],
        phones: [],
        created_by: 'current-user',
        modified_by: 'current-user',
      });
      setContactEmails([]);
      setContactPhones([]);
    } catch (error) {
      console.error('Failed to create contact:', error);
      alert('Failed to create contact');
    }
  };

  // Handle next step
  const handleNext = () => {
    if (!selectedContactId) {
      alert('Please select or create a contact');
      return;
    }
    // Store selected IDs in sessionStorage for step 2
    sessionStorage.setItem('applicationWizard', JSON.stringify({
      contactId: selectedContactId,
      companyId: selectedCompanyId,
      clientId: selectedClientId,
    }));
    router.push('/applications/new/step2');
  };

  const addEmail = () => {
    setContactEmails([...contactEmails, { email: '', email_type: 'Work', is_primary: contactEmails.length === 0 ? 1 : 0 }]);
  };

  const removeEmail = (index: number) => {
    setContactEmails(contactEmails.filter((_, i) => i !== index));
  };

  const updateEmail = (index: number, field: keyof ContactEmailCreate, value: any) => {
    const updated = [...contactEmails];
    updated[index] = { ...updated[index], [field]: value };
    setContactEmails(updated);
  };

  const addPhone = () => {
    setContactPhones([...contactPhones, { phone: '', phone_type: 'Work', is_primary: contactPhones.length === 0 ? 1 : 0 }]);
  };

  const removePhone = (index: number) => {
    setContactPhones(contactPhones.filter((_, i) => i !== index));
  };

  const updatePhone = (index: number, field: keyof ContactPhoneCreate, value: any) => {
    const updated = [...contactPhones];
    updated[index] = { ...updated[index], [field]: value };
    setContactPhones(updated);
  };

  return (
    <div className="container py-3 py-md-4" data-qa="application-wizard-step1-page">
      <div className="mb-2 mb-md-3">
        <Link href="/applications" className="text-decoration-none" data-qa="wizard-step1-back-link">
          ← Back to Applications
        </Link>
      </div>

      <div className="card shadow-sm" data-qa="wizard-step1-card">
        <div className="card-header bg-primary text-white">
          <h1 className="h4 h-md-3 mb-0" data-qa="wizard-step1-title">New Application - Step 1: Contact</h1>
          <small className="text-white-50">Select or create a contact for this application</small>
        </div>
        <div className="card-body">
          <div className="mb-4" data-qa="wizard-step1-contact-select">
            <EntitySelect
              label="Contact"
              value={selectedContactId}
              options={contactOptions}
              onSelect={setSelectedContactId}
              onCreate={() => setShowContactModal(true)}
              isLoading={contactsLoading}
              placeholder="Search for a contact..."
              required
            />

            {selectedContactId && (
              <div className="alert alert-success" data-qa="wizard-step1-contact-selected">
                ✓ Contact selected. You can proceed to the next step.
              </div>
            )}
          </div>

          <div className="d-flex flex-column flex-sm-row gap-2 pt-3">
            <Link href="/applications" className="w-100 w-sm-auto">
              <Button type="button" variant="secondary" className="w-100 w-sm-auto" data-qa="wizard-step1-cancel-button">Cancel</Button>
            </Link>
            <Button
              type="button"
              variant="primary"
              onClick={handleNext}
              disabled={!selectedContactId}
              className="w-100 w-sm-auto"
              data-qa="wizard-step1-next-button"
            >
              Next: Application Details →
            </Button>
          </div>
        </div>
      </div>

      {/* Contact Creation Modal */}
      <EntityCreateModal
        title="Create New Contact"
        isOpen={showContactModal}
        onClose={() => setShowContactModal(false)}
        onSave={handleCreateContact}
        isLoading={createContactMutation.isPending}
      >
        <div className="row">
          <div className="col-md-6">
            <Input
              label="First Name *"
              value={contactForm.first_name}
              onChange={(e) => setContactForm({ ...contactForm, first_name: e.target.value })}
              required
            />
          </div>
          <div className="col-md-6">
            <Input
              label="Last Name *"
              value={contactForm.last_name}
              onChange={(e) => setContactForm({ ...contactForm, last_name: e.target.value })}
              required
            />
          </div>
        </div>

        <div className="mb-3">
          <label className="form-label" data-qa="wizard-contact-title-label">Title</label>
          <input
            type="text"
            className="form-control"
            value={contactForm.title}
            onChange={(e) => setContactForm({ ...contactForm, title: e.target.value })}
            placeholder="Recruiter"
            data-qa="wizard-contact-title"
          />
        </div>

        <div className="mb-3">
          <label className="form-label" data-qa="wizard-contact-type-label">Contact Type</label>
          <select
            className="form-select"
            value={contactForm.contact_type}
            onChange={(e) => setContactForm({ ...contactForm, contact_type: e.target.value })}
            data-qa="wizard-contact-type"
          >
            <option value="Recruiter">Recruiter</option>
            <option value="Hiring Manager">Hiring Manager</option>
            <option value="HR">HR</option>
            <option value="Other">Other</option>
          </select>
        </div>

        <EntitySelect
          label="Company (Optional)"
          value={selectedCompanyId}
          options={companyOptions}
          onSelect={(id) => {
            setSelectedCompanyId(id);
            setContactForm({ ...contactForm, company_id: id || undefined });
          }}
          onCreate={() => setShowCompanyModal(true)}
          isLoading={companiesLoading}
        />

        <EntitySelect
          label="Client (Optional)"
          value={selectedClientId}
          options={clientOptions}
          onSelect={(id) => {
            setSelectedClientId(id);
            setContactForm({ ...contactForm, client_id: id || undefined });
          }}
          onCreate={() => setShowClientModal(true)}
          isLoading={clientsLoading}
        />

        <div className="mb-3">
          <div className="d-flex justify-content-between align-items-center mb-2">
            <label className="form-label mb-0">Emails</label>
            <Button type="button" variant="outline-primary" size="sm" onClick={addEmail} data-qa="wizard-step1-contact-add-email-button">
              Add Email
            </Button>
          </div>
          {contactEmails.map((email, index) => (
            <div key={index} className="card mb-2" data-qa={`wizard-step1-contact-email-${index}`}>
              <div className="card-body">
                <div className="row g-2">
                  <div className="col-md-6">
                    <Input
                      type="email"
                      placeholder="Email address"
                      value={email.email}
                      onChange={(e) => updateEmail(index, 'email', e.target.value)}
                      data-qa={`wizard-step1-contact-email-${index}-address`}
                    />
                  </div>
                  <div className="col-md-4">
                    <select
                      className="form-select"
                      value={email.email_type}
                      onChange={(e) => updateEmail(index, 'email_type', e.target.value)}
                      data-qa={`wizard-step1-contact-email-${index}-type`}
                    >
                      <option value="Work">Work</option>
                      <option value="Personal">Personal</option>
                    </select>
                  </div>
                  <div className="col-md-2">
                    <Button
                      type="button"
                      variant="danger"
                      size="sm"
                      onClick={() => removeEmail(index)}
                      data-qa={`wizard-step1-contact-email-${index}-remove-button`}
                    >
                      Remove
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="mb-3">
          <div className="d-flex justify-content-between align-items-center mb-2">
            <label className="form-label mb-0">Phone Numbers</label>
            <Button type="button" variant="outline-primary" size="sm" onClick={addPhone} data-qa="wizard-step1-contact-add-phone-button">
              Add Phone
            </Button>
          </div>
          {contactPhones.map((phone, index) => (
            <div key={index} className="card mb-2" data-qa={`wizard-step1-contact-phone-${index}`}>
              <div className="card-body">
                <div className="row g-2">
                  <div className="col-md-6">
                    <Input
                      type="tel"
                      placeholder="Phone number"
                      value={phone.phone}
                      onChange={(e) => updatePhone(index, 'phone', e.target.value)}
                      data-qa={`wizard-step1-contact-phone-${index}-number`}
                    />
                  </div>
                  <div className="col-md-4">
                    <select
                      className="form-select"
                      value={phone.phone_type}
                      onChange={(e) => updatePhone(index, 'phone_type', e.target.value)}
                      data-qa={`wizard-step1-contact-phone-${index}-type`}
                    >
                      <option value="Work">Work</option>
                      <option value="Home">Home</option>
                      <option value="Cell">Cell</option>
                    </select>
                  </div>
                  <div className="col-md-2">
                    <Button
                      type="button"
                      variant="danger"
                      size="sm"
                      onClick={() => removePhone(index)}
                      data-qa={`wizard-step1-contact-phone-${index}-remove-button`}
                    >
                      Remove
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </EntityCreateModal>

      {/* Company Creation Modal */}
      <EntityCreateModal
        title="Create New Company"
        isOpen={showCompanyModal}
        onClose={() => setShowCompanyModal(false)}
        onSave={handleCreateCompany}
        isLoading={createCompanyMutation.isPending}
      >
        <Input
          label="Company Name *"
          value={companyForm.name}
          onChange={(e) => setCompanyForm({ ...companyForm, name: e.target.value })}
          required
          data-qa="wizard-company-name"
        />
        <Input
          label="Address"
          value={companyForm.address || ''}
          onChange={(e) => setCompanyForm({ ...companyForm, address: e.target.value })}
          data-qa="wizard-company-address"
        />
        <div className="row">
          <div className="col-md-6">
            <Input
              label="City"
              value={companyForm.city || ''}
              onChange={(e) => setCompanyForm({ ...companyForm, city: e.target.value })}
              data-qa="wizard-company-city"
            />
          </div>
          <div className="col-md-3">
            <Input
              label="State"
              value={companyForm.state || ''}
              onChange={(e) => setCompanyForm({ ...companyForm, state: e.target.value })}
              data-qa="wizard-company-state"
            />
          </div>
          <div className="col-md-3">
            <Input
              label="ZIP"
              value={companyForm.zip || ''}
              onChange={(e) => setCompanyForm({ ...companyForm, zip: e.target.value })}
              data-qa="wizard-company-zip"
            />
          </div>
        </div>
        <div className="mb-3">
          <label className="form-label" data-qa="wizard-company-country-label">Country</label>
          <input
            type="text"
            className="form-control"
            value={companyForm.country || 'United States'}
            onChange={(e) => setCompanyForm({ ...companyForm, country: e.target.value })}
            data-qa="wizard-company-country"
          />
        </div>
        <div className="mb-3">
          <label className="form-label" data-qa="wizard-company-job-type-label">Job Type</label>
          <input
            type="text"
            className="form-control"
            value={companyForm.job_type || 'Technology'}
            onChange={(e) => setCompanyForm({ ...companyForm, job_type: e.target.value })}
            data-qa="wizard-company-job-type"
          />
        </div>
      </EntityCreateModal>

      {/* Client Creation Modal */}
      <EntityCreateModal
        title="Create New Client"
        isOpen={showClientModal}
        onClose={() => setShowClientModal(false)}
        onSave={handleCreateClient}
        isLoading={createClientMutation.isPending}
      >
        <Input
          label="Client Name *"
          value={clientForm.name || ''}
          onChange={(e) => setClientForm({ ...clientForm, name: e.target.value })}
          required
          data-qa="wizard-client-name"
        />
      </EntityCreateModal>
    </div>
  );
}
