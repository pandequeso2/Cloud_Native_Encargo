import { useEffect, useState } from 'react';
import { comentariosApi, entregasApi } from '../api/services';
import type { Comentario, Entrega } from '../types/domain';
import { EmptyPanel, ErrorPanel, LoadingPanel } from '../components/AsyncState';

export function Entregas() {
  const [entregas, setEntregas] = useState<Entrega[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [abierta, setAbierta] = useState<number | null>(null);
  const [comentarios, setComentarios] = useState<Record<number, Comentario[]>>({});

  useEffect(() => {
    entregasApi
      .listar()
      .then(setEntregas)
      .catch((err) => setError(err.message ?? 'No se pudieron cargar las entregas.'));
  }, []);

  const toggle = async (idEntrega: number) => {
    if (abierta === idEntrega) {
      setAbierta(null);
      return;
    }
    setAbierta(idEntrega);
    if (!comentarios[idEntrega]) {
      const lista = await comentariosApi.porEntrega(idEntrega);
      setComentarios((prev) => ({ ...prev, [idEntrega]: lista }));
    }
  };

  return (
    <>
      <header className="content__header">
        <div className="content__eyebrow">entregas</div>
        <h1>Entregas registradas</h1>
        <p className="content__subtitle">
          Haz clic en una entrega para ver los comentarios del profesor asociados.
        </p>
      </header>

      {error && <ErrorPanel message={error} />}
      {!error && !entregas && <LoadingPanel label="Cargando entregas…" />}
      {entregas && entregas.length === 0 && <EmptyPanel message="No hay entregas registradas." />}

      {entregas && entregas.length > 0 && (
        <div className="ledger">
          {entregas.map((entrega, index) => (
            <div key={entrega.idEntrega}>
              <button className="ledger__row ledger__row--button" onClick={() => toggle(entrega.idEntrega)}>
                <span className="ledger__index">{String(index + 1).padStart(2, '0')}</span>
                <span>
                  <div className="ledger__title">
                    Grupo #{entrega.idGrupo} · Trabajo #{entrega.idTrabajo}
                  </div>
                  <div className="ledger__meta">entregado el {entrega.fechaEntrega}</div>
                </span>
                <span className="ledger__meta">{entrega.estado}</span>
              </button>

              {abierta === entrega.idEntrega && (
                <div style={{ padding: '0.75rem 0 1rem 3rem' }}>
                  {!comentarios[entrega.idEntrega] && (
                    <p className="content__subtitle">Cargando comentarios…</p>
                  )}
                  {comentarios[entrega.idEntrega]?.length === 0 && (
                    <p className="content__subtitle">Sin comentarios todavía.</p>
                  )}
                  {comentarios[entrega.idEntrega]?.map((comentario) => (
                    <div key={comentario.idComentario} className="comment">
                      <div className="comment__meta">
                        profesor #{comentario.idProfesor} · {comentario.fechaComentario} ·{' '}
                        {comentario.tipoComentario}
                      </div>
                      <div>{comentario.contenido}</div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </>
  );
}
