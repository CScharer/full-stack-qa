/**
 * Snapshot tests for Loading component
 */
import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { Loading } from '@/components/ui/Loading';

describe('Loading Snapshot Tests', () => {
  it('matches snapshot for default loading message', () => {
    const { container } = render(<Loading data-qa="test-loading-default" />);
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for custom loading message', () => {
    const { container } = render(
      <Loading message="Loading applications..." data-qa="test-loading-custom" />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for short loading message', () => {
    const { container } = render(
      <Loading message="Loading..." data-qa="test-loading-short" />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for long loading message', () => {
    const { container } = render(
      <Loading
        message="Please wait while we load your data from the server"
        data-qa="test-loading-long"
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });
});
