/**
 * Tests for Loading component using Vitest
 */
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Loading } from '@/components/ui/Loading';

describe('Loading', () => {
  it('renders loading spinner', () => {
    render(<Loading />);
    const spinner = screen.getByRole('status');
    expect(spinner).toBeInTheDocument();
    expect(spinner).toHaveClass('spinner-border');
  });

  it('displays default message', () => {
    render(<Loading />);
    const message = screen.getByText('Loading...', { selector: 'p' });
    expect(message).toBeInTheDocument();
  });

  it('displays custom message', () => {
    render(<Loading message="Fetching data..." />);
    expect(screen.getByText('Fetching data...')).toBeInTheDocument();
  });

  it('has correct Bootstrap classes', () => {
    const { container } = render(<Loading />);
    const mainDiv = container.querySelector('.d-flex.flex-column');
    expect(mainDiv).toBeInTheDocument();
    expect(mainDiv).toHaveClass('align-items-center', 'justify-content-center');
  });
});
