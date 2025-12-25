/**
 * Reusable Button component using Bootstrap
 */
import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'success' | 'warning' | 'info' | 'light' | 'dark' | 'link' | 'outline-primary' | 'outline-secondary' | 'outline-danger' | 'outline-success' | 'outline-warning' | 'outline-info' | 'outline-light' | 'outline-dark';
  size?: 'sm' | 'md' | 'lg';
  children: React.ReactNode;
  'data-qa'?: string;
}

export const Button: React.FC<ButtonProps> = ({
  variant = 'primary',
  size = 'md',
  className = '',
  children,
  ...props
}) => {
  const sizeClass = size === 'sm' ? 'btn-sm' : size === 'lg' ? 'btn-lg' : '';
  
  // Require explicit data-qa to ensure uniqueness - throw error in development if missing
  const dataQa = props['data-qa'];
  if (!dataQa && process.env.NODE_ENV === 'development') {
    console.error('Button component must have a unique data-qa attribute for testing. Please provide data-qa prop.');
  }
  
  return (
    <button
      className={`btn btn-${variant} ${sizeClass} ${className}`.trim()}
      data-qa={dataQa || `button-missing-qa-${variant}`}
      {...props}
    >
      {children}
    </button>
  );
};
