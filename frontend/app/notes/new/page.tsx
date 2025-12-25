'use client';

import { useRouter } from 'next/navigation';
import { useEffect } from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/Button';

/**
 * Note creation is now only available from within application pages.
 * This page redirects users to the applications list.
 */
export default function NewNotePage() {
  const router = useRouter();

  useEffect(() => {
    // Redirect to applications list
    router.replace('/applications');
  }, [router]);

  return (
    <div className="container py-3 py-md-4">
      <div className="card shadow-sm text-center py-5">
        <div className="card-body">
          <h1 className="h3 h-md-2 mb-3">Note Creation</h1>
          <p className="text-muted mb-4">
            Notes can only be created from within an application page.
          </p>
          <Link href="/applications">
            <Button variant="primary" className="w-100 w-md-auto" data-qa="note-redirect-to-applications-button">
              View Applications
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
