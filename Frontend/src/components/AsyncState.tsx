export function LoadingPanel({ label }: { label: string }) {
  return <div className="state-panel">{label}</div>;
}

export function ErrorPanel({ message }: { message: string }) {
  return <div className="state-panel state-panel--error">{message}</div>;
}

export function EmptyPanel({ message }: { message: string }) {
  return <div className="state-panel">{message}</div>;
}

type StatusKind = 'confirmed' | 'due' | 'overdue' | 'neutral';

export function StatusBadge({ label, kind }: { label: string; kind: StatusKind }) {
  const className = kind === 'neutral' ? 'status' : `status status--${kind}`;
  return (
    <span className={className}>
      <span className="status__dot" />
      {label}
    </span>
  );
}
