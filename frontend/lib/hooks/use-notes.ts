/**
 * React Query hooks for Notes
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '../api/client';
import { Note, NoteCreate, NoteUpdate, ApiResponse } from '../types';

export const useNotes = (params?: {
  application_id?: number;
  page?: number;
  limit?: number;
  sort?: string;
  order?: 'asc' | 'desc';
  include_deleted?: boolean;
}) => {
  return useQuery<ApiResponse<Note>>({
    queryKey: ['notes', params],
    queryFn: async () => {
      const { data } = await apiClient.get('/notes', { params });
      return data;
    },
  });
};

export const useNote = (id: number) => {
  return useQuery<Note>({
    queryKey: ['note', id],
    queryFn: async () => {
      const { data } = await apiClient.get(`/notes/${id}`);
      return data;
    },
    enabled: !!id,
  });
};

export const useCreateNote = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (note: NoteCreate) => {
      const { data } = await apiClient.post('/notes', note);
      return data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['notes'] });
      queryClient.invalidateQueries({ queryKey: ['notes', { application_id: variables.application_id }] });
    },
  });
};

export const useUpdateNote = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ id, note }: { id: number; note: NoteUpdate }) => {
      const { data } = await apiClient.put(`/notes/${id}`, note);
      return data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['notes'] });
      queryClient.invalidateQueries({ queryKey: ['note', variables.id] });
    },
  });
};

export const useDeleteNote = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (id: number) => {
      await apiClient.delete(`/notes/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notes'] });
    },
  });
};
