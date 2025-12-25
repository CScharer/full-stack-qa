/**
 * Contact types matching backend API
 */

export interface ContactEmail {
  id: number;
  contact_id: number;
  email: string;
  email_type: string;
  is_primary: number;
  is_deleted: number;
  created_on: string;
  modified_on: string;
  created_by: string;
  modified_by: string;
}

export interface ContactPhone {
  id: number;
  contact_id: number;
  phone: string;
  phone_type: string;
  is_primary: number;
  is_deleted: number;
  created_on: string;
  modified_on: string;
  created_by: string;
  modified_by: string;
}

export interface Contact {
  id: number;
  first_name: string;
  last_name: string;
  name?: string; // Computed field: first_name + ' ' + last_name (for backward compatibility)
  title: string;
  linkedin?: string;
  contact_type: string;
  company_id?: number;
  application_id?: number;
  client_id?: number;
  is_deleted: number;
  created_on: string;
  modified_on: string;
  created_by: string;
  modified_by: string;
}

export interface ContactFull extends Contact {
  emails: ContactEmail[];
  phones: ContactPhone[];
}

export interface ContactEmailCreate {
  email: string;
  email_type?: string;
  is_primary?: number;
}

export interface ContactPhoneCreate {
  phone: string;
  phone_type?: string;
  is_primary?: number;
}

export interface ContactCreate {
  first_name: string;
  last_name: string;
  title?: string;
  linkedin?: string;
  contact_type: string;
  company_id?: number;
  application_id?: number;
  client_id?: number;
  emails?: ContactEmailCreate[];
  phones?: ContactPhoneCreate[];
  created_by: string;
  modified_by: string;
}

export interface ContactUpdate {
  first_name?: string;
  last_name?: string;
  title?: string;
  linkedin?: string;
  contact_type?: string;
  company_id?: number;
  application_id?: number;
  client_id?: number;
  modified_by: string;
}
