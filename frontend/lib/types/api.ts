/**
 * Common API types
 */

export interface PaginationResponse {
  page: number;
  limit: number;
  total: number;
  pages: number;
}

export interface ApiResponse<T> {
  data: T[];
  pagination: PaginationResponse;
}

export interface ApiError {
  error: string;
  code: number;
  details?: {
    id?: number;
    field?: string;
    message?: string;
    [key: string]: any;
  };
}
