/**
 * React Query hooks for Applications
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '../api/client';
import { Application, ApplicationCreate, ApplicationUpdate, ApiResponse } from '../types';

export const useApplications = (params?: {
  page?: number;
  limit?: number;
  status?: string;
  company_id?: number;
  client_id?: number;
  sort?: string;
  order?: 'asc' | 'desc';
  include_deleted?: boolean;
}) => {
  return useQuery<ApiResponse<Application>>({
    queryKey: ['applications', params],
    queryFn: async () => {
      const { data } = await apiClient.get('/applications', { params });
      return data;
    },
  });
};

export const useApplication = (id: number) => {
  return useQuery<Application>({
    queryKey: ['application', id],
    queryFn: async () => {
      const { data } = await apiClient.get(`/applications/${id}`);
      return data;
    },
    enabled: !!id,
  });
};

export const useCreateApplication = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (application: ApplicationCreate) => {
      const { data } = await apiClient.post('/applications', application);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['applications'] });
    },
  });
};

export const useUpdateApplication = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ id, application }: { id: number; application: ApplicationUpdate }) => {
      const { data } = await apiClient.put(`/applications/${id}`, application);
      return data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['applications'] });
      queryClient.invalidateQueries({ queryKey: ['application', variables.id] });
    },
  });
};

export const useDeleteApplication = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (id: number) => {
      await apiClient.delete(`/applications/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['applications'] });
    },
  });
};
