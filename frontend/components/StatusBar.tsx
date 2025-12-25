'use client';

interface StatusBarProps {
  message?: string;
  className?: string;
}

export function StatusBar({ message, className = '' }: StatusBarProps) {
  return (
    <div 
      className={`bg-light border-top py-2 px-3 ${className}`}
      style={{ position: 'fixed', bottom: 0, left: 0, right: 0, zIndex: 1000 }}
      data-qa="status-bar"
    >
      <div className="container-fluid">
        <div className="d-flex justify-content-between align-items-center">
          <small className="text-muted" data-qa="status-bar-message">
            {message || 'Ready'}
          </small>
          <small className="text-muted" data-qa="status-bar-timestamp">
            {new Date().toLocaleTimeString()}
          </small>
        </div>
      </div>
    </div>
  );
}
