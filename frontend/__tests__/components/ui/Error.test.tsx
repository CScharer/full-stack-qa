/**
 * Tests for Error component using Vitest
 */
import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Error } from '@/components/ui/Error';

describe('Error', () => {
  it('renders error message', () => {
    render(<Error message="Something went wrong" />);
    expect(screen.getByText('Something went wrong')).toBeInTheDocument();
  });

  it('displays error alert with correct classes', () => {
    render(<Error message="Test error" />);
    const alert = screen.getByRole('alert');
    expect(alert).toBeInTheDocument();
    expect(alert).toHaveClass('alert', 'alert-danger');
  });

  it('shows retry button when onRetry provided', () => {
    const handleRetry = vi.fn();
    render(<Error message="Error occurred" onRetry={handleRetry} />);
    const retryButton = screen.getByText('Retry');
    expect(retryButton).toBeInTheDocument();
  });

  it('does not show retry button when onRetry not provided', () => {
    render(<Error message="Error occurred" />);
    expect(screen.queryByText('Retry')).not.toBeInTheDocument();
  });

  it('calls onRetry when retry button clicked', async () => {
    const user = userEvent.setup();
    const handleRetry = vi.fn();
    render(<Error message="Error occurred" onRetry={handleRetry} />);
    
    const retryButton = screen.getByText('Retry');
    await user.click(retryButton);
    
    expect(handleRetry).toHaveBeenCalledTimes(1);
  });
});
