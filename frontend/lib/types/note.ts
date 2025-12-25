/**
 * Note types matching backend API
 */

export interface Note {
  id: number;
  application_id: number;
  note: string;
  is_deleted: number;
  created_on: string;
  modified_on: string;
  created_by: string;
  modified_by: string;
}

export interface NoteCreate {
  application_id: number;
  note: string;
  created_by: string;
  modified_by: string;
}

export interface NoteUpdate {
  note?: string;
  modified_by: string;
}
