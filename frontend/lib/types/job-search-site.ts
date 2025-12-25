/**
 * Job Search Site types matching backend API
 */

export interface JobSearchSite {
  id: number;
  name: string;
  url?: string;
  is_deleted: number;
  created_on: string;
  modified_on: string;
  created_by: string;
  modified_by: string;
}

export interface JobSearchSiteCreate {
  name: string;
  url?: string;
  created_by: string;
  modified_by: string;
}

export interface JobSearchSiteUpdate {
  name?: string;
  url?: string;
  modified_by: string;
}
