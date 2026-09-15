import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { gruposApi } from '../api/services';
import type { Grupo } from '../types/domain';
import { EmptyPanel, ErrorPanel, LoadingPanel, StatusBadge } from '../components/AsyncState';
import { NuevoGrupoModal } from '../components/NuevoGrupoModal';
import { EditarGrupoModal } from '../components/EditarGrupoModal';
import { useAuthInfo } from '../auth/useAuthInfo';

export function Grupos() {
  const auth = useAuthInfo();
  const [grupos, setGrupos] = useState<Grupo[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [grupoEditando, setGrupoEditando] = useState<Grupo | null>(null);

  useEffect(() => {
    gruposApi
      .listar()
      .then(setGrupos)
      .catch((err) => setError(err.message ?? 'No se pudieron cargar los grupos.'));
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
    const confirmado = window.confirm('¿Seguro que quieres eliminar este grupo?');
    if (!confirmado) return;

    try {
      await gruposApi.eliminar(idGrupo);
      setGrupos((prev) => prev?.filter((item) => item.idGrupo !== idGrupo) ?? null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo eliminar el grupo.');
    }
  };

  return (
    <>
      <header className="content__header">
        <div className="content__eyebrow">grupos</div>
        <h1>Grupos de trabajo</h1>
        <p className="content__subtitle">
          Todos los grupos registrados en el taller, con su capacidad y estado de cupo.
        </p>
      </header>

      {/* Solo Admin tiene permiso de escritura sobre /api/v1/grupos en el Gateway
          (ver SecurityConfig.java, CATALOGO_PATHS). Ocultamos el botón para los
          demás roles para no ofrecer una acción que el backend va a rechazar. */}
      {auth?.hasRole('Admin') && (
        <div className="content__actions">
          <button className="btn-primary" onClick={() => setModalAbierto(true)}>
            + Nuevo grupo
          </button>
        </div>
      )}

      {error && <ErrorPanel message={error} />}
      {!error && !grupos && <LoadingPanel label="Cargando grupos…" />}
      {grupos && grupos.length === 0 && <EmptyPanel message="Aún no hay grupos registrados." />}

      {grupos && grupos.length > 0 && (
        <div className="ledger">
          {grupos.map((grupo, index) => (
            <div key={grupo.idGrupo} className="ledger__row">
              <Link to={`/grupos/${grupo.idGrupo}`} className="ledger__row__link">
                <span className="ledger__index">{String(index + 1).padStart(2, '0')}</span>
                <span>
                  <div className="ledger__title">{grupo.nombreGrupo}</div>
                  <div className="ledger__meta">
                    cupo {grupo.capacidadMaxima} · creado {grupo.fechaCreacion}
                  </div>
                </span>
                <StatusBadge
                  kind={grupo.grupoLleno ? 'overdue' : 'confirmed'}
                  label={grupo.grupoLleno ? 'Cupo lleno' : 'Con cupo'}
                />
              </Link>
              {auth?.hasRole('Admin') && (
                <>
                  <button type="button" className="btn-secondary" onClick={() => setGrupoEditando(grupo)}>
                    Editar
                  </button>
                  <button
                    type="button"
                    className="btn-danger"
                    onClick={() => handleGrupoEliminado(grupo.idGrupo)}
                  >
                    Eliminar
                  </button>
                </>
              )}
            </div>
          ))}
        </div>
      )}

      {modalAbierto && (
        <NuevoGrupoModal onClose={() => setModalAbierto(false)} onCreated={handleGrupoCreado} />
      )}

      {grupoEditando && (
        <EditarGrupoModal
          grupo={grupoEditando}
          onClose={() => setGrupoEditando(null)}
          onUpdated={handleGrupoActualizado}
        />
      )}
    </>
  );
}