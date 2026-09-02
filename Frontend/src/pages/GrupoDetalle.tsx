import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { gruposApi } from '../api/services';
import type { GrupoConIntegrantes, GrupoConTrabajo } from '../types/domain';
import { ErrorPanel, LoadingPanel } from '../components/AsyncState';

export function GrupoDetalle() {
  const { idGrupo } = useParams<{ idGrupo: string }>();
  const id = Number(idGrupo);

  const [conIntegrantes, setConIntegrantes] = useState<GrupoConIntegrantes | null>(null);
  const [conTrabajo, setConTrabajo] = useState<GrupoConTrabajo | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([gruposApi.conIntegrantes(id), gruposApi.conTrabajo(id)])
      .then(([integrantes, trabajo]) => {
        setConIntegrantes(integrantes);
        setConTrabajo(trabajo);
      })
      .catch((err) => setError(err.message ?? 'No se pudo cargar el grupo.'));
  }, [id]);

  if (error) return <ErrorPanel message={error} />;
  if (!conIntegrantes) return <LoadingPanel label="Cargando grupo…" />;

  return (
    <>
      <header className="content__header">
        <div className="content__eyebrow">
          <Link to="/grupos">← grupos</Link>
        </div>
        <h1>{conIntegrantes.nombreGrupo}</h1>
        <p className="content__subtitle">
          Cupo {conIntegrantes.capacidadMaxima} · creado el {conIntegrantes.fechaCreacion}
        </p>
      </header>

      <h3 style={{ marginBottom: '0.75rem', fontSize: '1.05rem' }}>Integrantes</h3>
      {conIntegrantes.integrantes.length === 0 ? (
        <p className="content__subtitle">Este grupo aún no tiene integrantes asignados.</p>
      ) : (
        <div className="ledger">
          {conIntegrantes.integrantes.map((integrante, index) => (
            <div key={integrante.idIntegrante} className="ledger__row">
              <span className="ledger__index">{String(index + 1).padStart(2, '0')}</span>
              <span>
                <div className="ledger__title">
                  {integrante.nombre} {integrante.apellido}
                </div>
                <div className="ledger__meta">{integrante.correoElectronico}</div>
              </span>
              <span className="ledger__meta">{integrante.disponibilidad}</span>
            </div>
          ))}
        </div>
      )}

      <h3 style={{ margin: '2rem 0 0.75rem', fontSize: '1.05rem' }}>Trabajo asignado</h3>
      {conTrabajo?.trabajo ? (
        <div className="ledger">
          <div className="ledger__row">
            <span className="ledger__index">01</span>
            <span>
              <div className="ledger__title">{conTrabajo.trabajo.nombreTrabajo}</div>
              <div className="ledger__meta">
                {conTrabajo.trabajo.tipoTrabajo} · semestre {conTrabajo.trabajo.semestre} ·{' '}
                {conTrabajo.trabajo.porcentajeNota}% de la nota
              </div>
            </span>
            <span className="ledger__meta">{conTrabajo.trabajo.estado}</span>
          </div>
        </div>
      ) : (
        <p className="content__subtitle">Este grupo aún no tiene un trabajo asignado.</p>
      )}
    </>
  );
}
