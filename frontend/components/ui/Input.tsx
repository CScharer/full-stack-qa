/**
 * Reusable Input component using Bootstrap
 */
import React from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  'data-qa'?: string;
}

export const Input: React.FC<InputProps> = ({
  label,
  error,
  className = '',
  'data-qa': dataQa,
  ...props
}) => {
  // Require explicit data-qa to ensure uniqueness - throw error in development if missing
  const inputDataQa = dataQa || (label ? `input-${label.toLowerCase().replace(/\s+/g, '-')}` : 'input-missing-qa');
  if (!dataQa && process.env.NODE_ENV === 'development') {
    console.error('Input component must have a unique data-qa attribute for testing. Please provide data-qa prop.');
  }
  
  return (
    <div className="mb-3" data-qa={`input-wrapper-${inputDataQa}`}>
      {label && (
        <label className="form-label" data-qa={`label-${inputDataQa}`}>
          {label}
        </label>
      )}
      <input
        className={`form-control ${error ? 'is-invalid' : ''} ${className}`}
        data-qa={inputDataQa}
        {...props}
      />
      {error && (
        <div className="invalid-feedback" data-qa={`error-${inputDataQa}`}>{error}</div>
      )}
    </div>
  );
};
