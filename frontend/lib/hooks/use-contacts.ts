/**
 * React Query hooks for Contacts
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '../api/client';
import { Contact, ContactFull, ContactCreate, ContactUpdate, ApiResponse } from '../types';

export const useContacts = (params?: {
  page?: number;
  limit?: number;
  company_id?: number;
  application_id?: number;
  client_id?: number;
  contact_type?: string;
  sort?: string;
  order?: 'asc' | 'desc';
  include_deleted?: boolean;
}) => {
  return useQuery<ApiResponse<Contact>>({
    queryKey: ['contacts', params],
    queryFn: async () => {
      const { data } = await apiClient.get('/contacts', { params });
      return data;
    },
  });
};

export const useContact = (id: number) => {
  return useQuery<ContactFull>({
    queryKey: ['contact', id],
    queryFn: async () => {
      const { data } = await apiClient.get(`/contacts/${id}`);
      return data;
    },
    enabled: !!id,
  });
};

export const useCreateContact = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (contact: ContactCreate) => {
      const { data } = await apiClient.post('/contacts', contact);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['contacts'] });
    },
  });
};

export const useUpdateContact = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ id, contact }: { id: number; contact: ContactUpdate }) => {
      const { data } = await apiClient.put(`/contacts/${id}`, contact);
      return data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['contacts'] });
      queryClient.invalidateQueries({ queryKey: ['contact', variables.id] });
    },
  });
};

export const useDeleteContact = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (id: number) => {
      await apiClient.delete(`/contacts/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['contacts'] });
    },
  });
};
