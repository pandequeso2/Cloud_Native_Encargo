import { useEffect, useState } from 'react';
import { integrantesApi } from '../api/services';
import type { Integrante } from '../types/domain';
import { EmptyPanel, ErrorPanel, LoadingPanel } from '../components/AsyncState';
import { NuevoIntegranteModal } from '../components/NuevoIntegranteModal';
import { useAuthInfo } from '../auth/useAuthInfo';

export function Integrantes() {
  const auth = useAuthInfo();
  const [integrantes, setIntegrantes] = useState<Integrante[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [modalAbierto, setModalAbierto] = useState(false);

  useEffect(() => {
    integrantesApi
      .listar()
      .then(setIntegrantes)
      .catch((err) => setError(err.message ?? 'No se pudieron cargar los integrantes.'));
  }, []);

  const handleIntegranteCreado = (nuevo: Integrante) => {
    setIntegrantes((prev) => (prev ? [...prev, nuevo] : [nuevo]));
    setModalAbierto(false);
  };

  return (
    <>
      <header className="content__header">
        <div className="content__eyebrow">integrantes</div>
        <h1>Integrantes del taller</h1>
        <p className="content__subtitle">
          Personas registradas en los grupos, con su correo y disponibilidad.
        </p>
      </header>

      {auth?.hasRole('Admin') && (
        <div className="content__actions">
          <button className="btn-primary" onClick={() => setModalAbierto(true)}>
            + Nuevo integrante
          </button>
        </div>
      )}

      {error && <ErrorPanel message={error} />}
      {!error && !integrantes && <LoadingPanel label="Cargando integrantes..." />}
      {integrantes && integrantes.length === 0 && <EmptyPanel message="No hay integrantes registrados." />}

      {integrantes && integrantes.length > 0 && (
        <div className="ledger">
          {integrantes.map((integrante, index) => (
            <div key={integrante.idIntegrante} className="ledger__row">
              <span className="ledger__index">{String(index + 1).padStart(2, '0')}</span>
              <span>
                <div className="ledger__title">{integrante.nombre} {integrante.apellido}</div>
                <div className="ledger__meta">
                  {integrante.correoElectronico} · grupo #{integrante.idGrupo}
                </div>
              </span>
              <span className="ledger__meta">{integrante.disponibilidad}</span>
            </div>
          ))}
        </div>
      )}

      {modalAbierto && (
        <NuevoIntegranteModal
          onClose={() => setModalAbierto(false)}
          onCreated={handleIntegranteCreado}
        />
      )}
    </>
  );
}
