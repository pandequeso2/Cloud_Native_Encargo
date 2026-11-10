import type { ReactNode } from 'react';
import {
  Loader2,
  AlertCircle,
  FolderOpen,
  CheckCircle2,
  Clock,
  AlertTriangle,
  HelpCircle,
} from 'lucide-react';

export function LoadingPanel({ label = 'Cargando datos…' }: { label?: string }) {
  return (
    <div className="state-panel state-panel--loading">
      <div className="state-panel__spinner">
        <Loader2 size={32} className="spinner-icon animate-spin" />
      </div>
      <div className="state-panel__content">
        <div className="state-panel__title">{label}</div>
        <div className="state-panel__subtitle">Consultando microservicios a través del Gateway…</div>
      </div>
      {/* Esqueletos visuales simulados */}
      <div className="skeleton-grid">
        <div className="skeleton-line skeleton-line--title" />
        <div className="skeleton-line skeleton-line--body" />
        <div className="skeleton-line skeleton-line--body-short" />
      </div>
    </div>
  );
}

export function ErrorPanel({
  message,
  onRetry,
}: {
  message: string;
  onRetry?: () => void;
}) {
  return (
    <div className="state-panel state-panel--error">
      <div className="state-panel__icon-box state-panel__icon-box--error">
        <AlertCircle size={28} />
      </div>
      <div className="state-panel__content">
        <div className="state-panel__title">Ocurrió un inconveniente</div>
        <div className="state-panel__message">{message}</div>
      </div>
      {onRetry && (
        <button type="button" className="btn-secondary state-panel__action" onClick={onRetry}>
          Reintentar
        </button>
      )}
    </div>
  );
}

export function EmptyPanel({
  message,
  subtitle = 'Los registros creados aparecerán aquí automáticamente.',
  action,
}: {
  message: string;
  subtitle?: string;
  action?: ReactNode;
}) {
  return (
    <div className="state-panel state-panel--empty">
      <div className="state-panel__icon-box state-panel__icon-box--empty">
        <FolderOpen size={36} />
      </div>
      <div className="state-panel__title">{message}</div>
      <div className="state-panel__subtitle">{subtitle}</div>
      {action && <div className="state-panel__action-container">{action}</div>}
    </div>
  );
}

export type StatusKind = 'confirmed' | 'due' | 'overdue' | 'neutral' | 'info';

export function StatusBadge({
  label,
  kind,
}: {
  label: string;
  kind: StatusKind;
}) {
  const getIcon = () => {
    switch (kind) {
      case 'confirmed':
        return <CheckCircle2 size={13} />;
      case 'due':
        return <Clock size={13} />;
      case 'overdue':
        return <AlertTriangle size={13} />;
      case 'info':
        return <HelpCircle size={13} />;
      default:
        return null;
    }
  };

  return (
    <span className={`status-badge-modern status-badge-modern--${kind}`}>
      <span className="status-badge-modern__dot" />
      <span className="status-badge-modern__icon">{getIcon()}</span>
      <span className="status-badge-modern__label">{label}</span>
    </span>
  );
}
