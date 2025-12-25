/**
 * React Query hooks for Clients
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '../api/client';
import { Client, ClientCreate, ClientUpdate, ApiResponse } from '../types';

export const useClients = (params?: {
  page?: number;
  limit?: number;
  sort?: string;
  order?: 'asc' | 'desc';
  include_deleted?: boolean;
}) => {
  return useQuery<ApiResponse<Client>>({
    queryKey: ['clients', params],
    queryFn: async () => {
      const { data } = await apiClient.get('/clients', { params });
      return data;
    },
  });
};

export const useClient = (id: number) => {
  return useQuery<Client>({
    queryKey: ['client', id],
    queryFn: async () => {
      const { data } = await apiClient.get(`/clients/${id}`);
      return data;
    },
    enabled: !!id,
  });
};

export const useCreateClient = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (client: ClientCreate) => {
      const { data } = await apiClient.post('/clients', client);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clients'] });
    },
  });
};

export const useUpdateClient = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ id, client }: { id: number; client: ClientUpdate }) => {
      const { data } = await apiClient.put(`/clients/${id}`, client);
      return data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['clients'] });
      queryClient.invalidateQueries({ queryKey: ['client', variables.id] });
    },
  });
};

export const useDeleteClient = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (id: number) => {
      await apiClient.delete(`/clients/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clients'] });
    },
  });
};
