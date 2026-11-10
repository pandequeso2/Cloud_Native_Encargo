import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { gruposApi } from '../api/services';
import type { Grupo } from '../types/domain';
import { EmptyPanel, ErrorPanel, LoadingPanel } from '../components/AsyncState';
import { NuevoGrupoModal } from '../components/NuevoGrupoModal';
import { EditarGrupoModal } from '../components/EditarGrupoModal';
import { useAuthInfo } from '../auth/useAuthInfo';
import {
  Users,
  Plus,
  Calendar,
  CheckCircle2,
  AlertCircle,
  Edit2,
  Trash2,
  ArrowRight,
  Search,
  Hash,
  Shield,
  Layers,
} from 'lucide-react';

export function Grupos() {
  const auth = useAuthInfo();
  const [grupos, setGrupos] = useState<Grupo[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [grupoEditando, setGrupoEditando] = useState<Grupo | null>(null);
  const [busqueda, setBusqueda] = useState('');

  const cargarGrupos = () => {
    gruposApi
      .listar()
      .then(setGrupos)
      .catch((err) => setError(err.message ?? 'No se pudieron cargar los grupos.'));
  };

  useEffect(() => {
    cargarGrupos();
  }, []);

  const handleGrupoCreado = (nuevo: Grupo) => {
    setGrupos((prev) => (prev ? [...prev, nuevo] : [nuevo]));
    setModalAbierto(false);
  };

  const handleGrupoActualizado = (actualizado: Grupo) => {
    setGrupos((prev) =>
      prev ? prev.map((item) => (item.idGrupo === actualizado.idGrupo ? actualizado : item)) : [actualizado],
    );
    setGrupoEditando(null);
  };

  const handleGrupoEliminado = async (idGrupo: number) => {
    const confirmado = window.confirm('¿Seguro que deseas eliminar este grupo?');
    if (!confirmado) return;

    try {
      await gruposApi.eliminar(idGrupo);
      setGrupos((prev) => prev?.filter((item) => item.idGrupo !== idGrupo) ?? null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo eliminar el grupo.');
    }
  };

  const gruposFiltrados = (grupos ?? []).filter((g) =>
    g.nombreGrupo.toLowerCase().includes(busqueda.toLowerCase()) ||
    String(g.idGrupo).includes(busqueda)
  );

  return (
    <div className="page-container">
      {/* Encabezado Principal */}
      <header className="page-header">
        <div className="page-header__info">
          <div className="page-header__eyebrow">Gestión Académica</div>
          <h1 className="page-header__title">Grupos de Trabajo</h1>
          <p className="page-header__subtitle">
            Equipos conformados en el taller, control de cupos máximos y vinculación de encargos.
          </p>
        </div>

        {auth?.hasRole('Admin') && (
          <div className="page-header__actions">
            <button className="btn-primary" onClick={() => setModalAbierto(true)}>
              <Plus size={16} />
              <span>Nuevo Grupo</span>
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
            placeholder="Buscar grupo por nombre o número de ID…"
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>
        <div className="toolbar-stats">
          <span className="badge badge--info">
            {gruposFiltrados.length} {gruposFiltrados.length === 1 ? 'grupo registrado' : 'grupos registrados'}
          </span>
        </div>
      </div>

      {error && <ErrorPanel message={error} onRetry={cargarGrupos} />}
      {!error && !grupos && <LoadingPanel label="Cargando grupos desde el microservicio…" />}

      {grupos && grupos.length === 0 && (
        <EmptyPanel
          message="Aún no hay grupos registrados en el taller."
          subtitle="Crea el primer grupo de trabajo para que los estudiantes puedan integrarse."
          action={
            auth?.hasRole('Admin') ? (
              <button className="btn-primary" onClick={() => setModalAbierto(true)}>
                <Plus size={16} />
                <span>Crear Primer Grupo</span>
              </button>
            ) : null
          }
        />
      )}

      {/* Grilla de Tarjetas Modernas de Grupos */}
      {grupos && grupos.length > 0 && (
        <div className="grid-cards">
          {gruposFiltrados.map((grupo) => (
            <div key={grupo.idGrupo} className="data-card data-card--hover">
              <div className="data-card__body">
                <div className="data-card__top">
                  <div className="data-card__icon-badge data-card__icon-badge--blue">
                    <Users size={20} />
                  </div>
                  <div className="data-card__chips">
                    <span className="badge badge--neutral">
                      <Hash size={12} /> ID {grupo.idGrupo}
                    </span>
                    {grupo.grupoLleno ? (
                      <span className="badge badge--danger">
                        <AlertCircle size={12} /> Cupo Lleno
                      </span>
                    ) : (
                      <span className="badge badge--success">
                        <CheckCircle2 size={12} /> Con Cupo
                      </span>
                    )}
                  </div>
                </div>

                <h3 className="data-card__title">
                  <Link to={`/grupos/${grupo.idGrupo}`} className="data-card__title-link">
                    {grupo.nombreGrupo}
                  </Link>
                </h3>

                <div className="data-card__meta-item">
                  <Calendar size={14} className="data-card__meta-icon" />
                  <span className="data-card__meta-text">
                    Creado el {grupo.fechaCreacion || 'Fecha no registrada'}
                  </span>
                </div>

                <div className="data-card__meta-item">
                  <Layers size={14} className="data-card__meta-icon" />
                  <span className="data-card__meta-text">
                    Capacidad máxima: <strong>{grupo.capacidadMaxima} integrantes</strong>
                  </span>
                </div>
              </div>

              <div className="data-card__footer">
                <Link to={`/grupos/${grupo.idGrupo}`} className="btn-action btn-action--view">
                  <span>Ver Detalle</span>
                  <ArrowRight size={14} />
                </Link>

                {auth?.hasRole('Admin') && (
                  <div className="data-card__admin-actions">
                    <button
                      type="button"
                      className="btn-action btn-action--edit"
                      onClick={() => setGrupoEditando(grupo)}
                      title="Editar grupo"
                    >
                      <Edit2 size={14} />
                      <span>Editar</span>
                    </button>
                    <button
                      type="button"
                      className="btn-action btn-action--danger"
                      onClick={() => handleGrupoEliminado(grupo.idGrupo)}
                      title="Eliminar grupo"
                    >
                      <Trash2 size={14} />
                      <span>Eliminar</span>
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modales */}
      {modalAbierto && (
        <NuevoGrupoModal
          onClose={() => setModalAbierto(false)}
          onCreated={handleGrupoCreado}
        />
      )}

      {grupoEditando && (
        <EditarGrupoModal
          grupo={grupoEditando}
          onClose={() => setGrupoEditando(null)}
          onUpdated={handleGrupoActualizado}
        />
      )}
    </div>
  );
}
