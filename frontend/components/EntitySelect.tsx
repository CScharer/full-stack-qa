'use client';

import { useState, useEffect, useRef } from 'react';
import { Input } from './ui/Input';
import { Button } from './ui/Button';

export interface EntityOption {
  id: number;
  name: string;
  display?: string; // Optional display text (e.g., "Name (Company)")
}

interface EntitySelectProps {
  label: string;
  value?: number | null;
  options: EntityOption[];
  onSelect: (id: number | null) => void;
  onCreate: () => void;
  isLoading?: boolean;
  placeholder?: string;
  searchPlaceholder?: string;
  required?: boolean;
}

/**
 * Reusable entity select component with search/autocomplete
 * Industry standard: Searchable dropdown with "Create New" option
 */
export function EntitySelect({
  label,
  value,
  options,
  onSelect,
  onCreate,
  isLoading = false,
  placeholder = 'Select or search...',
  searchPlaceholder = 'Search...',
  required = false,
}: EntitySelectProps) {
  const [searchTerm, setSearchTerm] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState<EntityOption | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Find selected option
  useEffect(() => {
    if (value && options.length > 0) {
      const option = options.find(opt => opt.id === value);
      setSelectedOption(option || null);
    } else {
      setSelectedOption(null);
    }
  }, [value, options]);

  // Filter options based on search
  const filteredOptions = options.filter(option =>
    option.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    option.display?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
        setSearchTerm('');
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
      return () => document.removeEventListener('mousedown', handleClickOutside);
    }
  }, [isOpen]);

  const handleSelect = (option: EntityOption) => {
    setSelectedOption(option);
    onSelect(option.id);
    setIsOpen(false);
    setSearchTerm('');
  };

  const handleClear = () => {
    setSelectedOption(null);
    onSelect(null);
    setSearchTerm('');
  };

  const dataQa = `entity-select-${label.toLowerCase().replace(/\s+/g, '-')}`;
  
  return (
    <div className="mb-3" ref={dropdownRef} data-qa={dataQa}>
      <label className="form-label" data-qa={`${dataQa}-label`}>
        {label}
        {required && <span className="text-danger"> *</span>}
      </label>

      {/* Selected value display */}
      {selectedOption && !isOpen && (
        <div className="d-flex align-items-center gap-2 mb-2" data-qa={`${dataQa}-selected`}>
          <div className="form-control" data-qa={`${dataQa}-selected-value`}>
            {selectedOption.display || selectedOption.name}
          </div>
          <Button
            type="button"
            variant="outline-secondary"
            size="sm"
            onClick={handleClear}
            data-qa={`${dataQa}-clear-button`}
          >
            Clear
          </Button>
        </div>
      )}

      {/* Search input and dropdown */}
      <div className="position-relative">
        <Input
          type="text"
          placeholder={selectedOption ? searchPlaceholder : placeholder}
          value={searchTerm}
          onChange={(e) => {
            setSearchTerm(e.target.value);
            setIsOpen(true);
          }}
          onFocus={() => setIsOpen(true)}
          disabled={isLoading}
          data-qa={`${dataQa}-search-input`}
        />

        {/* Dropdown menu */}
        {isOpen && (
          <div className="position-absolute top-100 start-0 w-100 bg-white border rounded shadow-lg z-3 mt-1" style={{ maxHeight: '300px', overflowY: 'auto' }} data-qa={`${dataQa}-dropdown`}>
            {isLoading ? (
              <div className="p-3 text-center text-muted" data-qa={`${dataQa}-loading`}>Loading...</div>
            ) : filteredOptions.length > 0 ? (
              <>
                {filteredOptions.map((option) => (
                  <button
                    key={option.id}
                    type="button"
                    className="w-100 text-start px-3 py-2 border-0 bg-white hover-bg-light"
                    style={{ cursor: 'pointer' }}
                    onClick={() => handleSelect(option)}
                    onMouseEnter={(e) => {
                      e.currentTarget.classList.add('bg-light');
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.classList.remove('bg-light');
                    }}
                    data-qa={`${dataQa}-option-${option.id}`}
                  >
                    {option.display || option.name}
                  </button>
                ))}
                <div className="border-top">
                  <button
                    type="button"
                    className="w-100 text-start px-3 py-2 border-0 bg-white text-primary fw-bold"
                    style={{ cursor: 'pointer' }}
                    onClick={() => {
                      setIsOpen(false);
                      setSearchTerm('');
                      onCreate();
                    }}
                    data-qa={`${dataQa}-create-new-button`}
                  >
                    + Create New
                  </button>
                </div>
              </>
            ) : (
              <div className="p-3">
                <div className="text-muted mb-2">No results found</div>
                <Button
                  type="button"
                  variant="primary"
                  size="sm"
                  className="w-100"
                  onClick={() => {
                    setIsOpen(false);
                    setSearchTerm('');
                    onCreate();
                  }}
                >
                  + Create New
                </Button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
