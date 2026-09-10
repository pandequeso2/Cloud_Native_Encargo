import { useEffect, useState } from 'react';
import { trabajosApi } from '../api/services';
import type { EstadoTrabajo, Trabajo } from '../types/domain';
import { EmptyPanel, ErrorPanel, LoadingPanel, StatusBadge } from '../components/AsyncState';
import { NuevoTrabajoModal } from '../components/NuevoTrabajoModal';
import { useAuthInfo } from '../auth/useAuthInfo';

const ESTADO_UI: Record<EstadoTrabajo, { label: string; kind: 'confirmed' | 'due' | 'overdue' }> = {
  EVALUADO: { label: 'Evaluado', kind: 'confirmed' },
  ENTREGADO: { label: 'Entregado', kind: 'due' },
  PENDIENTE: { label: 'Pendiente', kind: 'overdue' },
};

export function Trabajos() {
  const auth = useAuthInfo();
  const [trabajos, setTrabajos] = useState<Trabajo[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [modalAbierto, setModalAbierto] = useState(false);

  useEffect(() => {
    trabajosApi
      .listar()
      .then(setTrabajos)
      .catch((err) => setError(err.message ?? 'No se pudieron cargar los trabajos.'));
  }, []);

  const handleTrabajoCreado = (nuevo: Trabajo) => {
    setTrabajos((prev) => (prev ? [...prev, nuevo] : [nuevo]));
    setModalAbierto(false);
  };

  return (
    <>
      <header className="content__header">
        <div className="content__eyebrow">trabajos</div>
        <h1>Trabajos del semestre</h1>
        <p className="content__subtitle">
          Encargos, presentaciones y otros trabajos asignados a los grupos, con su estado actual.
        </p>
      </header>

      {auth?.hasRole('Admin') && (
        <div className="content__actions">
          <button className="btn-primary" onClick={() => setModalAbierto(true)}>
            + Nuevo trabajo
          </button>
        </div>
      )}

      {error && <ErrorPanel message={error} />}
      {!error && !trabajos && <LoadingPanel label="Cargando trabajos…" />}
      {trabajos && trabajos.length === 0 && <EmptyPanel message="No hay trabajos registrados." />}

      {trabajos && trabajos.length > 0 && (
        <div className="ledger">
          {trabajos.map((trabajo, index) => {
            const estado = ESTADO_UI[trabajo.estado];
            return (
              <div key={trabajo.idTrabajo} className="ledger__row">
                <span className="ledger__index">{String(index + 1).padStart(2, '0')}</span>
                <span>
                  <div className="ledger__title">{trabajo.nombreTrabajo}</div>
                  <div className="ledger__meta">
                    {trabajo.tipoTrabajo} · grupo #{trabajo.idGrupo} · semestre {trabajo.semestre} ·{' '}
                    {trabajo.porcentajeNota}% de la nota
                  </div>
                </span>
                <StatusBadge kind={estado.kind} label={estado.label} />
              </div>
            );
          })}
        </div>
      )}

      {modalAbierto && (
        <NuevoTrabajoModal
          onClose={() => setModalAbierto(false)}
          onCreated={handleTrabajoCreado}
        />
      )}
    </>
  );
}
