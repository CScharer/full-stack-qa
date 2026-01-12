/**
 * Snapshot tests for Button component
 */
import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { Button } from '@/components/ui/Button';

describe('Button Snapshot Tests', () => {
  it('matches snapshot for primary button', () => {
    const { container } = render(<Button data-qa="test-button-primary">Click me</Button>);
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for secondary variant', () => {
    const { container } = render(
      <Button variant="secondary" data-qa="test-button-secondary">Secondary</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for danger variant', () => {
    const { container } = render(
      <Button variant="danger" data-qa="test-button-danger">Delete</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for success variant', () => {
    const { container } = render(
      <Button variant="success" data-qa="test-button-success">Save</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for disabled button', () => {
    const { container } = render(
      <Button disabled data-qa="test-button-disabled">Disabled</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for small size', () => {
    const { container } = render(
      <Button size="sm" data-qa="test-button-small">Small</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for large size', () => {
    const { container } = render(
      <Button size="lg" data-qa="test-button-large">Large</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for outline variant', () => {
    const { container } = render(
      <Button variant="outline-primary" data-qa="test-button-outline">Outline</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });
});
