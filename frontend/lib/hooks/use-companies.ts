/**
 * React Query hooks for Companies
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '../api/client';
import { Company, CompanyCreate, CompanyUpdate, ApiResponse } from '../types';

export const useCompanies = (params?: {
  page?: number;
  limit?: number;
  job_type?: string;
  sort?: string;
  order?: 'asc' | 'desc';
  include_deleted?: boolean;
}) => {
  return useQuery<ApiResponse<Company>>({
    queryKey: ['companies', params],
    queryFn: async () => {
      const { data } = await apiClient.get('/companies', { params });
      return data;
    },
  });
};

export const useCompany = (id: number) => {
  return useQuery<Company>({
    queryKey: ['company', id],
    queryFn: async () => {
      const { data } = await apiClient.get(`/companies/${id}`);
      return data;
    },
    enabled: !!id,
  });
};

export const useCreateCompany = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (company: CompanyCreate) => {
      const { data } = await apiClient.post('/companies', company);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['companies'] });
    },
  });
};

export const useUpdateCompany = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ id, company }: { id: number; company: CompanyUpdate }) => {
      const { data } = await apiClient.put(`/companies/${id}`, company);
      return data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['companies'] });
      queryClient.invalidateQueries({ queryKey: ['company', variables.id] });
    },
  });
};

export const useDeleteCompany = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (id: number) => {
      await apiClient.delete(`/companies/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['companies'] });
    },
  });
};
