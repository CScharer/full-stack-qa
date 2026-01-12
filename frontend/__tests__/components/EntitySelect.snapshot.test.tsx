/**
 * Snapshot tests for EntitySelect component
 */
import { describe, it, expect, vi } from 'vitest';
import { render } from '@testing-library/react';
import { EntitySelect } from '@/components/EntitySelect';

const mockOptions = [
  { id: 1, name: 'Option 1' },
  { id: 2, name: 'Option 2' },
  { id: 3, name: 'Option 3' },
];

const mockOptionsWithDisplay = [
  { id: 1, name: 'John Doe', display: 'John Doe (Company A)' },
  { id: 2, name: 'Jane Smith', display: 'Jane Smith (Company B)' },
];

describe('EntitySelect Snapshot Tests', () => {
  const mockOnSelect = vi.fn();
  const mockOnCreate = vi.fn();

  it('matches snapshot for empty entity select', () => {
    const { container } = render(
      <EntitySelect
        label="Select Entity"
        options={[]}
        onSelect={mockOnSelect}
        onCreate={mockOnCreate}
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for entity select with options', () => {
    const { container } = render(
      <EntitySelect
        label="Select Entity"
        options={mockOptions}
        onSelect={mockOnSelect}
        onCreate={mockOnCreate}
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for entity select with selected value', () => {
    const { container } = render(
      <EntitySelect
        label="Select Entity"
        value={1}
        options={mockOptions}
        onSelect={mockOnSelect}
        onCreate={mockOnCreate}
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for entity select with display text', () => {
    const { container } = render(
      <EntitySelect
        label="Select Contact"
        value={1}
        options={mockOptionsWithDisplay}
        onSelect={mockOnSelect}
        onCreate={mockOnCreate}
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for required entity select', () => {
    const { container } = render(
      <EntitySelect
        label="Select Entity"
        required
        options={mockOptions}
        onSelect={mockOnSelect}
        onCreate={mockOnCreate}
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for loading entity select', () => {
    const { container } = render(
      <EntitySelect
        label="Select Entity"
        isLoading
        options={mockOptions}
        onSelect={mockOnSelect}
        onCreate={mockOnCreate}
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for entity select with custom placeholder', () => {
    const { container } = render(
      <EntitySelect
        label="Select Entity"
        placeholder="Choose an option..."
        options={mockOptions}
        onSelect={mockOnSelect}
        onCreate={mockOnCreate}
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });
});
