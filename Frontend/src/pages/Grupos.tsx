import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { gruposApi } from '../api/services';
import type { Grupo } from '../types/domain';
import { EmptyPanel, ErrorPanel, LoadingPanel, StatusBadge } from '../components/AsyncState';

export function Grupos() {
  const [grupos, setGrupos] = useState<Grupo[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    gruposApi
      .listar()
      .then(setGrupos)
      .catch((err) => setError(err.message ?? 'No se pudieron cargar los grupos.'));
  }, []);

  return (
    <>
      <header className="content__header">
        <div className="content__eyebrow">grupos</div>
        <h1>Grupos de trabajo</h1>
        <p className="content__subtitle">
          Todos los grupos registrados en el taller, con su capacidad y estado de cupo.
        </p>
      </header>

      {error && <ErrorPanel message={error} />}
      {!error && !grupos && <LoadingPanel label="Cargando grupos…" />}
      {grupos && grupos.length === 0 && <EmptyPanel message="Aún no hay grupos registrados." />}

      {grupos && grupos.length > 0 && (
        <div className="ledger">
          {grupos.map((grupo, index) => (
            <Link key={grupo.idGrupo} to={`/grupos/${grupo.idGrupo}`} className="ledger__row">
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
          ))}
        </div>
      )}
    </>
  );
}
