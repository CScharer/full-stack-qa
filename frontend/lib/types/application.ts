/**
 * Application types matching backend API
 */

export interface Application {
  id: number;
  status: string;
  requirement?: string;
  work_setting: string;
  compensation?: string;
  position?: string;
  job_description?: string;
  job_link?: string;
  location?: string;
  resume?: string;
  cover_letter?: string;
  entered_iwd: number;
  date_close?: string;
  company_id?: number;
  client_id?: number;
  // Related entity names (from backend joins)
  company_name?: string;
  client_name?: string;
  contact_name?: string;
  // Related entities (from detail endpoint)
  contacts?: Array<{
    id: number;
    name: string;
    title?: string;
    contact_type: string;
    emails?: Array<{ email: string; email_type: string; is_primary: number }>;
    phones?: Array<{ phone: string; phone_type: string; is_primary: number }>;
  }>;
  company_address?: string;
  company_city?: string;
  company_state?: string;
  company_zip?: string;
  company_country?: string;
  is_deleted: number;
  created_on: string;
  modified_on: string;
  created_by: string;
  modified_by: string;
}

export interface ApplicationCreate {
  status: string;
  requirement?: string;
  work_setting: string;
  compensation?: string;
  position?: string;
  job_description?: string;
  job_link?: string;
  location?: string;
  resume?: string;
  cover_letter?: string;
  entered_iwd?: number;
  date_close?: string;
  company_id?: number;
  client_id?: number;
  created_by: string;
  modified_by: string;
}

export interface ApplicationUpdate {
  status?: string;
  requirement?: string;
  work_setting?: string;
  compensation?: string;
  position?: string;
  job_description?: string;
  job_link?: string;
  location?: string;
  resume?: string;
  cover_letter?: string;
  entered_iwd?: number;
  date_close?: string;
  company_id?: number;
  client_id?: number;
  modified_by: string;
}
