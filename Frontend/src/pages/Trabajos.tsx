import { useEffect, useState } from 'react';
import { trabajosApi } from '../api/services';
import type { EstadoTrabajo, Trabajo } from '../types/domain';
import { EmptyPanel, ErrorPanel, LoadingPanel } from '../components/AsyncState';
import { NuevoTrabajoModal } from '../components/NuevoTrabajoModal';
import { EditarTrabajoModal } from '../components/EditarTrabajoModal';
import { useAuthInfo } from '../auth/useAuthInfo';
import {
  FolderKanban,
  Plus,
  Calendar,
  CheckCircle2,
  Clock,
  AlertTriangle,
  Edit2,
  Trash2,
  Search,
  Hash,
  FileText,
} from 'lucide-react';

export function Trabajos() {
  const auth = useAuthInfo();
  const [trabajos, setTrabajos] = useState<Trabajo[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [trabajoEditando, setTrabajoEditando] = useState<Trabajo | null>(null);
  const [busqueda, setBusqueda] = useState('');

  const cargarTrabajos = () => {
    trabajosApi
      .listar()
      .then(setTrabajos)
      .catch((err) => setError(err.message ?? 'No se pudieron cargar los trabajos.'));
  };

  useEffect(() => {
    cargarTrabajos();
  }, []);

  const handleTrabajoCreado = (nuevo: Trabajo) => {
    setTrabajos((prev) => (prev ? [...prev, nuevo] : [nuevo]));
    setModalAbierto(false);
  };

  const handleTrabajoActualizado = (actualizado: Trabajo) => {
    setTrabajos((prev) =>
      prev ? prev.map((item) => (item.idTrabajo === actualizado.idTrabajo ? actualizado : item)) : [actualizado],
    );
    setTrabajoEditando(null);
  };

  const handleTrabajoEliminado = async (idTrabajo: number) => {
    const confirmado = window.confirm('¿Seguro que deseas eliminar este trabajo?');
    if (!confirmado) return;

    try {
      await trabajosApi.eliminar(idTrabajo);
      setTrabajos((prev) => prev?.filter((item) => item.idTrabajo !== idTrabajo) ?? null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo eliminar el trabajo.');
    }
  };

  const trabajosFiltrados = (trabajos ?? []).filter((t) =>
    t.nombreTrabajo.toLowerCase().includes(busqueda.toLowerCase()) ||
    t.estado.toLowerCase().includes(busqueda.toLowerCase()) ||
    String(t.idTrabajo).includes(busqueda)
  );

  const getStatusBadge = (estado: EstadoTrabajo) => {
    switch (estado) {
      case 'EVALUADO':
        return (
          <span className="badge badge--success">
            <CheckCircle2 size={12} /> Evaluado
          </span>
        );
      case 'ENTREGADO':
        return (
          <span className="badge badge--info">
            <Clock size={12} /> Entregado
          </span>
        );
      case 'PENDIENTE':
      default:
        return (
          <span className="badge badge--warning">
            <AlertTriangle size={12} /> Pendiente
          </span>
        );
    }
  };

  return (
    <div className="page-container">
      {/* Encabezado */}
      <header className="page-header">
        <div className="page-header__info">
          <div className="page-header__eyebrow">Planificación Semestral</div>
          <h1 className="page-header__title">Trabajos del Semestre</h1>
          <p className="page-header__subtitle">
            Encargos, proyectos y presentaciones asignadas a los grupos con fechas límite y ponderaciones.
          </p>
        </div>

        {auth?.hasRole('Admin') && (
          <div className="page-header__actions">
            <button className="btn-primary" onClick={() => setModalAbierto(true)}>
              <Plus size={16} />
              <span>Nuevo Trabajo</span>
            </button>
          </div>
        )}
      </header>

      {/* Barra de Filtro y Búsqueda */}
      <div className="toolbar-section">
        <div className="search-bar">
          <Search size={18} className="search-bar__icon" />
          <input
            type="text"
            className="search-bar__input"
            placeholder="Buscar por título, estado (EVALUADO, ENTREGADO, PENDIENTE) o ID…"
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>
        <div className="toolbar-stats">
          <span className="badge badge--info">
            {trabajosFiltrados.length} {trabajosFiltrados.length === 1 ? 'encargo' : 'encargos'}
          </span>
        </div>
      </div>

      {error && <ErrorPanel message={error} onRetry={cargarTrabajos} />}
      {!error && !trabajos && <LoadingPanel label="Cargando trabajos desde el microservicio…" />}

      {trabajos && trabajos.length === 0 && (
        <EmptyPanel
          message="No hay trabajos registrados para este semestre."
          subtitle="Comienza planificando los encargos y evaluaciones para los grupos del taller."
          action={
            auth?.hasRole('Admin') ? (
              <button className="btn-primary" onClick={() => setModalAbierto(true)}>
                <Plus size={16} />
                <span>Crear Primer Trabajo</span>
              </button>
            ) : null
          }
        />
      )}

      {/* Grilla de Tarjetas Modernas de Trabajos */}
      {trabajos && trabajos.length > 0 && (
        <div className="grid-cards">
          {trabajosFiltrados.map((trabajo) => (
            <div key={trabajo.idTrabajo} className="data-card data-card--hover">
              <div className="data-card__body">
                <div className="data-card__top">
                  <div className="data-card__icon-badge data-card__icon-badge--amber">
                    <FolderKanban size={20} />
                  </div>
                  <div className="data-card__chips">
                    <span className="badge badge--neutral">
                      <Hash size={12} /> #{trabajo.idTrabajo}
                    </span>
                    {getStatusBadge(trabajo.estado)}
                  </div>
                </div>

                <h3 className="data-card__title">{trabajo.nombreTrabajo}</h3>

                <div className="data-card__meta-item">
                  <Calendar size={14} className="data-card__meta-icon" />
                  <span className="data-card__meta-text">
                    Fecha límite: <strong>{trabajo.fechaEntrega || 'Sin fecha asignada'}</strong>
                  </span>
                </div>

                <div className="data-card__meta-item">
                  <FileText size={14} className="data-card__meta-icon" />
                  <span className="data-card__meta-text">
                    {trabajo.descripcion || 'Encargo formativo o sumativo del programa de asignatura.'}
                  </span>
                </div>
              </div>

              {auth?.hasRole('Admin') && (
                <div className="data-card__footer">
                  <div className="data-card__admin-actions data-card__admin-actions--full">
                    <button
                      type="button"
                      className="btn-action btn-action--edit"
                      onClick={() => setTrabajoEditando(trabajo)}
                      title="Editar trabajo"
                    >
                      <Edit2 size={14} />
                      <span>Editar</span>
                    </button>
                    <button
                      type="button"
                      className="btn-action btn-action--danger"
                      onClick={() => handleTrabajoEliminado(trabajo.idTrabajo)}
                      title="Eliminar trabajo"
                    >
                      <Trash2 size={14} />
                      <span>Eliminar</span>
                    </button>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Modales */}
      {modalAbierto && (
        <NuevoTrabajoModal
          onClose={() => setModalAbierto(false)}
          onCreated={handleTrabajoCreado}
        />
      )}

      {trabajoEditando && (
        <EditarTrabajoModal
          trabajo={trabajoEditando}
          onClose={() => setTrabajoEditando(null)}
          onUpdated={handleTrabajoActualizado}
        />
      )}
    </div>
  );
}
