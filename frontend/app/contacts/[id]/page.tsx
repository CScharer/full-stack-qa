'use client';

import { useContact } from '@/lib/hooks/use-contacts';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import Link from 'next/link';
import { use } from 'react';
import { formatDate } from '@/lib/utils/date';

export default function ContactDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const contactId = parseInt(id);
  const { data: contact, isLoading, error, refetch } = useContact(contactId);

  if (isLoading) return <Loading message="Loading contact..." />;
  if (error) return <Error message="Failed to load contact" onRetry={() => refetch()} />;
  if (!contact) return <Error message="Contact not found" />;

  const fullName = contact.name || `${contact.first_name} ${contact.last_name}`.trim();

  return (
    <div className="container py-3 py-md-4" data-qa={`contact-detail-page-${contact.id}`}>
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0" data-qa="contact-detail-title">{fullName}</h1>
        <div className="d-flex gap-2 w-100 w-md-auto">
          <Link href={`/contacts/${contact.id}/edit`} className="flex-grow-1 flex-md-grow-0">
            <Button variant="primary" className="w-100 w-md-auto" data-qa="contact-detail-edit-button">Edit</Button>
          </Link>
          <Link href="/contacts" className="flex-grow-1 flex-md-grow-0">
            <Button variant="secondary" className="w-100 w-md-auto" data-qa="contact-detail-back-button">Back to List</Button>
          </Link>
        </div>
      </div>

      <div className="row g-3 g-md-4">
        <div className="col-12 col-md-8">
          <div className="card shadow-sm mb-4" data-qa="contact-detail-info-card">
            <div className="card-header bg-primary text-white">
              <h5 className="mb-0">Contact Information</h5>
            </div>
            <div className="card-body">
              <dl className="row">
                <dt className="col-sm-3">First Name</dt>
                <dd className="col-sm-9" data-qa="contact-detail-first-name">{contact.first_name}</dd>

                <dt className="col-sm-3">Last Name</dt>
                <dd className="col-sm-9" data-qa="contact-detail-last-name">{contact.last_name}</dd>

                <dt className="col-sm-3">Full Name</dt>
                <dd className="col-sm-9" data-qa="contact-detail-full-name">{fullName}</dd>

                <dt className="col-sm-3">Title</dt>
                <dd className="col-sm-9">{contact.title || 'N/A'}</dd>

                <dt className="col-sm-3">Type</dt>
                <dd className="col-sm-9">
                  <span className="badge bg-info">{contact.contact_type}</span>
                </dd>

                {contact.linkedin && (
                  <>
                    <dt className="col-sm-3">LinkedIn</dt>
                    <dd className="col-sm-9">
                      <a href={contact.linkedin} target="_blank" rel="noopener noreferrer">
                        {contact.linkedin}
                      </a>
                    </dd>
                  </>
                )}

                <dt className="col-sm-3">Company ID</dt>
                <dd className="col-sm-9">{contact.company_id || 'N/A'}</dd>

                <dt className="col-sm-3">Application ID</dt>
                <dd className="col-sm-9">{contact.application_id || 'N/A'}</dd>

                <dt className="col-sm-3">Client ID</dt>
                <dd className="col-sm-9">{contact.client_id || 'N/A'}</dd>
              </dl>
            </div>
          </div>

          {'emails' in contact && contact.emails && contact.emails.length > 0 && (
            <div className="card shadow-sm mb-4" data-qa={`contact-detail-${contact.id}-emails-card`}>
              <div className="card-header">
                <h5 className="mb-0">Email Addresses</h5>
              </div>
              <div className="card-body">
                <ul className="list-unstyled mb-0">
                  {contact.emails.map((email, index) => (
                    <li key={email.id} className="mb-2" data-qa={`contact-detail-${contact.id}-email-${index}`}>
                      <strong data-qa={`contact-detail-${contact.id}-email-${index}-address`}>{email.email}</strong>
                      <span className="badge bg-secondary ms-2" data-qa={`contact-detail-${contact.id}-email-${index}-type`}>{email.email_type}</span>
                      {email.is_primary === 1 && (
                        <span className="badge bg-primary ms-2" data-qa={`contact-detail-${contact.id}-email-${index}-primary`}>Primary</span>
                      )}
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          )}

          {'phones' in contact && contact.phones && contact.phones.length > 0 && (
            <div className="card shadow-sm mb-4" data-qa={`contact-detail-${contact.id}-phones-card`}>
              <div className="card-header">
                <h5 className="mb-0">Phone Numbers</h5>
              </div>
              <div className="card-body">
                <ul className="list-unstyled mb-0">
                  {contact.phones.map((phone, index) => (
                    <li key={phone.id} className="mb-2" data-qa={`contact-detail-${contact.id}-phone-${index}`}>
                      <strong data-qa={`contact-detail-${contact.id}-phone-${index}-number`}>{phone.phone}</strong>
                      <span className="badge bg-secondary ms-2" data-qa={`contact-detail-${contact.id}-phone-${index}-type`}>{phone.phone_type}</span>
                      {phone.is_primary === 1 && (
                        <span className="badge bg-primary ms-2" data-qa={`contact-detail-${contact.id}-phone-${index}-primary`}>Primary</span>
                      )}
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          )}
        </div>

        <div className="col-12 col-md-4">
          <div className="card shadow-sm">
            <div className="card-header">
              <h5 className="mb-0">Metadata</h5>
            </div>
            <div className="card-body">
              <dl className="row g-2 mb-0">
                <dt className="col-12">Created</dt>
                <dd className="col-12">{formatDate(contact.created_on)}</dd>

                <dt className="col-12">Modified</dt>
                <dd className="col-12">{formatDate(contact.modified_on)}</dd>

                <dt className="col-12">Created By</dt>
                <dd className="col-12">{contact.created_by}</dd>

                <dt className="col-12">Modified By</dt>
                <dd className="col-12">{contact.modified_by}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
