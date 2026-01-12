/**
 * Snapshot tests for Error component
 */
import { describe, it, expect, vi } from 'vitest';
import { render } from '@testing-library/react';
import { Error } from '@/components/ui/Error';

describe('Error Snapshot Tests', () => {
  it('matches snapshot for basic error message', () => {
    const { container } = render(
      <Error message="Something went wrong" data-qa="test-error-basic" />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for error with retry button', () => {
    const handleRetry = vi.fn();
    const { container } = render(
      <Error
        message="Failed to load data"
        onRetry={handleRetry}
        data-qa="test-error-retry"
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for long error message', () => {
    const { container } = render(
      <Error
        message="This is a very long error message that might wrap to multiple lines and should be displayed correctly in the error component"
        data-qa="test-error-long"
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for short error message', () => {
    const { container } = render(
      <Error message="Error" data-qa="test-error-short" />
    );
    expect(container.firstChild).toMatchSnapshot();
  });
});
