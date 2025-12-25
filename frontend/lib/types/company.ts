/**
 * Company types matching backend API
 */

export interface Company {
  id: number;
  name: string;
  address?: string;
  city?: string;
  state?: string;
  zip?: string;
  country: string;
  job_type: string;
  is_deleted: number;
  created_on: string;
  modified_on: string;
  created_by: string;
  modified_by: string;
}

export interface CompanyCreate {
  name: string;
  address?: string;
  city?: string;
  state?: string;
  zip?: string;
  country?: string;
  job_type?: string;
  created_by: string;
  modified_by: string;
}

export interface CompanyUpdate {
  name?: string;
  address?: string;
  city?: string;
  state?: string;
  zip?: string;
  country?: string;
  job_type?: string;
  modified_by: string;
}
