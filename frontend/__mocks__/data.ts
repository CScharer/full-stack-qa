/**
 * Mock data for frontend unit tests
 */

import { Application, ApplicationCreate } from '@/lib/types/application';
import { Company, CompanyCreate } from '@/lib/types/company';
import { Client, ClientCreate } from '@/lib/types/client';
import { Contact, ContactFull, ContactCreate, ContactEmail, ContactPhone } from '@/lib/types/contact';
import { Note, NoteCreate } from '@/lib/types/note';
import { JobSearchSite, JobSearchSiteCreate } from '@/lib/types/job-search-site';
import { ApiResponse } from '@/lib/types/api';

// Mock Applications
export const mockApplications: Application[] = [
  {
    id: 1,
    status: 'Applied',
    requirement: 'Full-time',
    work_setting: 'Remote',
    compensation: '$120,000',
    position: 'Senior Software Engineer',
    job_description: 'Build amazing products',
    job_link: 'https://example.com/job/1',
    location: 'San Francisco, CA',
    resume: 'resume.pdf',
    cover_letter: 'cover.pdf',
    entered_iwd: 0,
    date_close: undefined,
    company_id: 1,
    client_id: 1,
    is_deleted: 0,
    created_on: '2025-01-01T00:00:00Z',
    modified_on: '2025-01-01T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
  {
    id: 2,
    status: 'Interview',
    requirement: 'Full-time',
    work_setting: 'Hybrid',
    compensation: '$100,000',
    position: 'Frontend Developer',
    job_description: 'React and TypeScript',
    job_link: 'https://example.com/job/2',
    location: 'New York, NY',
    resume: undefined,
    cover_letter: undefined,
    entered_iwd: 0,
    date_close: undefined,
    company_id: 2,
    client_id: undefined,
    is_deleted: 0,
    created_on: '2025-01-02T00:00:00Z',
    modified_on: '2025-01-02T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
];

export const mockApplicationResponse: ApiResponse<Application> = {
  data: mockApplications,
  pagination: {
    page: 1,
    limit: 10,
    total: 2,
    pages: 1,
  },
};

// Mock Companies
export const mockCompanies: Company[] = [
  {
    id: 1,
    name: 'Tech Corp',
    address: '123 Main St',
    city: 'San Francisco',
    state: 'CA',
    zip: '94102',
    country: 'United States',
    job_type: 'Technology',
    is_deleted: 0,
    created_on: '2025-01-01T00:00:00Z',
    modified_on: '2025-01-01T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
  {
    id: 2,
    name: 'Startup Inc',
    address: '456 Market St',
    city: 'New York',
    state: 'NY',
    zip: '10001',
    country: 'United States',
    job_type: 'Technology',
    is_deleted: 0,
    created_on: '2025-01-02T00:00:00Z',
    modified_on: '2025-01-02T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
];

export const mockCompanyResponse: ApiResponse<Company> = {
  data: mockCompanies,
  pagination: {
    page: 1,
    limit: 10,
    total: 2,
    pages: 1,
  },
};

// Mock Clients
export const mockClients: Client[] = [
  {
    id: 1,
    name: 'Client A',
    is_deleted: 0,
    created_on: '2025-01-01T00:00:00Z',
    modified_on: '2025-01-01T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
  {
    id: 2,
    name: 'Client B',
    is_deleted: 0,
    created_on: '2025-01-02T00:00:00Z',
    modified_on: '2025-01-02T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
];

export const mockClientResponse: ApiResponse<Client> = {
  data: mockClients,
  pagination: {
    page: 1,
    limit: 10,
    total: 2,
    pages: 1,
  },
};

// Mock Contacts
export const mockContactEmails: ContactEmail[] = [
  {
    id: 1,
    contact_id: 1,
    email: 'john.doe@example.com',
    email_type: 'Work',
    is_primary: 1,
    is_deleted: 0,
    created_on: '2025-01-01T00:00:00Z',
    modified_on: '2025-01-01T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
  {
    id: 2,
    contact_id: 1,
    email: 'john.personal@example.com',
    email_type: 'Personal',
    is_primary: 0,
    is_deleted: 0,
    created_on: '2025-01-01T00:00:00Z',
    modified_on: '2025-01-01T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
];

export const mockContactPhones: ContactPhone[] = [
  {
    id: 1,
    contact_id: 1,
    phone: '555-0100',
    phone_type: 'Work',
    is_primary: 1,
    is_deleted: 0,
    created_on: '2025-01-01T00:00:00Z',
    modified_on: '2025-01-01T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
  {
    id: 2,
    contact_id: 1,
    phone: '555-0101',
    phone_type: 'Cell',
    is_primary: 0,
    is_deleted: 0,
    created_on: '2025-01-01T00:00:00Z',
    modified_on: '2025-01-01T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
];

export const mockContacts: Contact[] = [
  {
    id: 1,
    first_name: 'John',
    last_name: 'Doe',
    name: 'John Doe',
    title: 'Senior Recruiter',
    contact_type: 'Recruiter',
    company_id: 1,
    application_id: 1,
    client_id: undefined,
    is_deleted: 0,
    created_on: '2025-01-01T00:00:00Z',
    modified_on: '2025-01-01T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
  {
    id: 2,
    first_name: 'Jane',
    last_name: 'Smith',
    name: 'Jane Smith',
    title: 'Hiring Manager',
    contact_type: 'Manager',
    company_id: 2,
    application_id: 2,
    client_id: 1,
    is_deleted: 0,
    created_on: '2025-01-02T00:00:00Z',
    modified_on: '2025-01-02T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
];

export const mockContactFull: ContactFull = {
  ...mockContacts[0],
  emails: mockContactEmails,
  phones: mockContactPhones,
};

export const mockContactResponse: ApiResponse<Contact> = {
  data: mockContacts,
  pagination: {
    page: 1,
    limit: 10,
    total: 2,
    pages: 1,
  },
};

// Mock Notes
export const mockNotes: Note[] = [
  {
    id: 1,
    application_id: 1,
    note: 'Initial phone screen went well',
    is_deleted: 0,
    created_on: '2025-01-01T00:00:00Z',
    modified_on: '2025-01-01T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
  {
    id: 2,
    application_id: 1,
    note: 'Follow up scheduled for next week',
    is_deleted: 0,
    created_on: '2025-01-02T00:00:00Z',
    modified_on: '2025-01-02T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
];

export const mockNoteResponse: ApiResponse<Note> = {
  data: mockNotes,
  pagination: {
    page: 1,
    limit: 10,
    total: 2,
    pages: 1,
  },
};

// Mock Job Search Sites
export const mockJobSearchSites: JobSearchSite[] = [
  {
    id: 1,
    name: 'LinkedIn',
    url: 'https://linkedin.com/jobs',
    is_deleted: 0,
    created_on: '2025-01-01T00:00:00Z',
    modified_on: '2025-01-01T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
  {
    id: 2,
    name: 'Indeed',
    url: 'https://indeed.com',
    is_deleted: 0,
    created_on: '2025-01-02T00:00:00Z',
    modified_on: '2025-01-02T00:00:00Z',
    created_by: 'test-user',
    modified_by: 'test-user',
  },
];

export const mockJobSearchSiteResponse: ApiResponse<JobSearchSite> = {
  data: mockJobSearchSites,
  pagination: {
    page: 1,
    limit: 10,
    total: 2,
    pages: 1,
  },
};
