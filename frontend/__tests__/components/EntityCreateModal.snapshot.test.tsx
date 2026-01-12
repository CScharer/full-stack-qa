/**
 * Snapshot tests for EntityCreateModal component
 */
import { describe, it, expect, vi } from 'vitest';
import { render } from '@testing-library/react';
import { EntityCreateModal } from '@/components/EntityCreateModal';

describe('EntityCreateModal Snapshot Tests', () => {
  const mockOnClose = vi.fn();
  const mockOnSave = vi.fn().mockResolvedValue(undefined);

  it('matches snapshot for closed modal', () => {
    const { container } = render(
      <EntityCreateModal
        title="Create Entity"
        isOpen={false}
        onClose={mockOnClose}
        onSave={mockOnSave}
      >
        <div>Modal content</div>
      </EntityCreateModal>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for open modal', () => {
    const { container } = render(
      <EntityCreateModal
        title="Create Entity"
        isOpen={true}
        onClose={mockOnClose}
        onSave={mockOnSave}
      >
        <div>Modal content</div>
      </EntityCreateModal>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for modal with loading state', () => {
    const { container } = render(
      <EntityCreateModal
        title="Create Entity"
        isOpen={true}
        isLoading={true}
        onClose={mockOnClose}
        onSave={mockOnSave}
      >
        <div>Modal content</div>
      </EntityCreateModal>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for modal with long title', () => {
    const { container } = render(
      <EntityCreateModal
        title="Create a New Entity with a Very Long Title"
        isOpen={true}
        onClose={mockOnClose}
        onSave={mockOnSave}
      >
        <div>Modal content</div>
      </EntityCreateModal>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for modal with complex content', () => {
    const { container } = render(
      <EntityCreateModal
        title="Create Contact"
        isOpen={true}
        onClose={mockOnClose}
        onSave={mockOnSave}
      >
        <div>
          <input type="text" placeholder="Name" />
          <input type="email" placeholder="Email" />
          <textarea placeholder="Notes" />
        </div>
      </EntityCreateModal>
    );
    expect(container).toMatchSnapshot();
  });
});
