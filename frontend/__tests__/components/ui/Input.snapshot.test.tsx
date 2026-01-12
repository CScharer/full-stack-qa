/**
 * Snapshot tests for Input component
 */
import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { Input } from '@/components/ui/Input';

describe('Input Snapshot Tests', () => {
  it('matches snapshot for basic input', () => {
    const { container } = render(<Input data-qa="test-input-basic" />);
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for input with label', () => {
    const { container } = render(
      <Input label="Name" data-qa="test-input-label" />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for input with error', () => {
    const { container } = render(
      <Input error="This field is required" data-qa="test-input-error" />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for input with label and error', () => {
    const { container } = render(
      <Input
        label="Email"
        error="Invalid email address"
        data-qa="test-input-label-error"
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for disabled input', () => {
    const { container } = render(
      <Input disabled label="Disabled Field" data-qa="test-input-disabled" />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for input with placeholder', () => {
    const { container } = render(
      <Input
        placeholder="Enter your name"
        label="Name"
        data-qa="test-input-placeholder"
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for input with value', () => {
    const { container } = render(
      <Input
        value="John Doe"
        onChange={() => {}}
        label="Name"
        data-qa="test-input-value"
      />
    );
    expect(container.firstChild).toMatchSnapshot();
  });
});
