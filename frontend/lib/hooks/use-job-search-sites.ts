/**
 * React Query hooks for Job Search Sites
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '../api/client';
import { JobSearchSite, JobSearchSiteCreate, JobSearchSiteUpdate, ApiResponse } from '../types';

export const useJobSearchSites = (params?: {
  page?: number;
  limit?: number;
  sort?: string;
  order?: 'asc' | 'desc';
  include_deleted?: boolean;
}) => {
  return useQuery<ApiResponse<JobSearchSite>>({
    queryKey: ['job-search-sites', params],
    queryFn: async () => {
      const { data } = await apiClient.get('/job-search-sites', { params });
      return data;
    },
  });
};

export const useJobSearchSite = (id: number) => {
  return useQuery<JobSearchSite>({
    queryKey: ['job-search-site', id],
    queryFn: async () => {
      const { data } = await apiClient.get(`/job-search-sites/${id}`);
      return data;
    },
    enabled: !!id,
  });
};

export const useCreateJobSearchSite = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (site: JobSearchSiteCreate) => {
      const { data } = await apiClient.post('/job-search-sites', site);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['job-search-sites'] });
    },
  });
};

export const useUpdateJobSearchSite = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ id, site }: { id: number; site: JobSearchSiteUpdate }) => {
      const { data } = await apiClient.put(`/job-search-sites/${id}`, site);
      return data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['job-search-sites'] });
      queryClient.invalidateQueries({ queryKey: ['job-search-site', variables.id] });
    },
  });
};

export const useDeleteJobSearchSite = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (id: number) => {
      await apiClient.delete(`/job-search-sites/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['job-search-sites'] });
    },
  });
};
