'use client';

import { useJobSearchSite } from '@/lib/hooks/use-job-search-sites';
import { Button } from '@/components/ui/Button';
import { Loading } from '@/components/ui/Loading';
import { Error } from '@/components/ui/Error';
import Link from 'next/link';
import { use } from 'react';
import { formatDate } from '@/lib/utils/date';

export default function JobSearchSiteDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const siteId = parseInt(id);
  const { data: site, isLoading, error, refetch } = useJobSearchSite(siteId);

  if (isLoading) return <Loading message="Loading job search site..." />;
  if (error) return <Error message="Failed to load job search site" onRetry={() => refetch()} />;
  if (!site) return <Error message="Job search site not found" />;

  return (
    <div className="container py-3 py-md-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 mb-md-4 gap-2">
        <h1 className="h3 h-md-2 mb-0">{site.name}</h1>
        <div className="d-flex gap-2 w-100 w-md-auto">
          <Link href={`/job-search-sites/${site.id}/edit`} className="flex-grow-1 flex-md-grow-0">
            <Button variant="primary" className="w-100 w-md-auto" data-qa={`job-search-site-detail-${site.id}-edit-button`}>Edit</Button>
          </Link>
          <Link href="/job-search-sites" className="flex-grow-1 flex-md-grow-0">
            <Button variant="secondary" className="w-100 w-md-auto" data-qa={`job-search-site-detail-${site.id}-back-button`}>Back to List</Button>
          </Link>
        </div>
      </div>

      <div className="row g-3 g-md-4">
        <div className="col-12 col-md-8">
          <div className="card shadow-sm mb-3 mb-md-4">
            <div className="card-header bg-primary text-white">
              <h5 className="mb-0">Site Information</h5>
            </div>
            <div className="card-body">
              <dl className="row g-2">
                <dt className="col-12 col-sm-3">Name</dt>
                <dd className="col-12 col-sm-9">{site.name}</dd>

                {site.url && (
                  <>
                    <dt className="col-12 col-sm-3">URL</dt>
                    <dd className="col-12 col-sm-9">
                      <a href={site.url} target="_blank" rel="noopener noreferrer" className="text-break">
                        {site.url}
                      </a>
                    </dd>
                  </>
                )}
              </dl>
            </div>
          </div>
        </div>

        <div className="col-12 col-md-4">
          <div className="card shadow-sm">
            <div className="card-header">
              <h5 className="mb-0">Metadata</h5>
            </div>
            <div className="card-body">
              <dl className="row g-2 mb-0">
                <dt className="col-12">Created</dt>
                <dd className="col-12">{formatDate(site.created_on)}</dd>

                <dt className="col-12">Modified</dt>
                <dd className="col-12">{formatDate(site.modified_on)}</dd>

                <dt className="col-12">Created By</dt>
                <dd className="col-12">{site.created_by}</dd>

                <dt className="col-12">Modified By</dt>
                <dd className="col-12">{site.modified_by}</dd>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
