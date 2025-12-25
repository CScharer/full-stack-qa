'use client';

import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

/**
 * Redirect to step 1 of the wizard
 * Industry standard: Multi-step wizard entry point
 */
export default function NewApplicationPage() {
  const router = useRouter();

  useEffect(() => {
    router.replace('/applications/new/step1');
  }, [router]);

  return (
    <div className="container py-3 py-md-4">
      <div className="text-center">
        <p>Redirecting to application wizard...</p>
      </div>
    </div>
  );
}
