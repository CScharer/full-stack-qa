'use client';

import { useState, ReactNode } from 'react';
import { Button } from './ui/Button';
import { Input } from './ui/Input';

interface EntityCreateModalProps {
  title: string;
  isOpen: boolean;
  onClose: () => void;
  onSave: (data: any) => Promise<void>;
  children: ReactNode;
  isLoading?: boolean;
}

/**
 * Reusable modal component for creating entities inline
 * Industry standard: Modal dialog for quick entity creation
 */
export function EntityCreateModal({
  title,
  isOpen,
  onClose,
  onSave,
  children,
  isLoading = false,
}: EntityCreateModalProps) {
  if (!isOpen) return null;

  const dataQa = `entity-create-modal-${title.toLowerCase().replace(/\s+/g, '-')}`;
  
  return (
    <div
      className="modal show d-block"
      style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}
      onClick={onClose}
      data-qa={dataQa}
    >
      <div
        className="modal-dialog modal-dialog-centered"
        onClick={(e) => e.stopPropagation()}
        data-qa={`${dataQa}-dialog`}
      >
        <div className="modal-content" data-qa={`${dataQa}-content`}>
          <div className="modal-header" data-qa={`${dataQa}-header`}>
            <h5 className="modal-title" data-qa={`${dataQa}-title`}>{title}</h5>
            <button
              type="button"
              className="btn-close"
              onClick={onClose}
              aria-label="Close"
              data-qa={`${dataQa}-close-button`}
            />
          </div>
          <div className="modal-body" data-qa={`${dataQa}-body`}>
            {children}
          </div>
          <div className="modal-footer" data-qa={`${dataQa}-footer`}>
            <Button
              type="button"
              variant="secondary"
              onClick={onClose}
              disabled={isLoading}
              data-qa={`${dataQa}-cancel-button`}
            >
              Cancel
            </Button>
            <Button
              type="button"
              variant="primary"
              onClick={onSave}
              disabled={isLoading}
              data-qa={`${dataQa}-save-button`}
            >
              {isLoading ? 'Saving...' : 'Save'}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
