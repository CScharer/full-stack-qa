/**
 * Loading spinner component using Bootstrap
 */
export const Loading: React.FC<{ message?: string; 'data-qa'?: string }> = ({ message = 'Loading...', 'data-qa': dataQa = 'loading' }) => {
  return (
    <div className="d-flex flex-column align-items-center justify-content-center p-5" data-qa={dataQa}>
      <div className="spinner-border text-primary mb-3" role="status" data-qa={`${dataQa}-spinner`}>
        <span className="visually-hidden">Loading...</span>
      </div>
      <p className="text-muted" data-qa={`${dataQa}-message`}>{message}</p>
    </div>
  );
};
