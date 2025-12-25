/**
 * Client types matching backend API
 */

export interface Client {
  id: number;
  name?: string;
  is_deleted: number;
  created_on: string;
  modified_on: string;
  created_by: string;
  modified_by: string;
}

export interface ClientCreate {
  name?: string;
  created_by: string;
  modified_by: string;
}

export interface ClientUpdate {
  name?: string;
  modified_by: string;
}
