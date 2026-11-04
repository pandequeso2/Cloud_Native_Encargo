import { useState, useEffect, type FormEvent } from 'react';
import { comentariosApi } from '../api/services';
import type { Comentario } from '../types/domain';
import '../styles/crearNota.css';

export function GestionComentarios() {
  const [entregaId, setEntregaId] = useState<number | ''>(1);
  const [usuarioId, setUsuarioId] = useState<number | ''>(101);
  const [textoComentario, setTextoComentario] = useState('');
  const [filtroEntregaId, setFiltroEntregaId] = useState<number | 'ALL'>(1);

  const [submitting, setSubmitting] = useState(false);
  const [loading, setLoading] = useState(false);
  const [comentarios, setComentarios] = useState<Comentario[]>([]);
  const [feedback, setFeedback] = useState<{
    type: 'success' | 'error';
    message: string;
    comentario?: Comentario;
  } | null>(null);

  const cargarComentarios = async () => {
    try {
      setLoading(true);
      const data = await comentariosApi.listar();
      // Ordenar del más reciente al más antiguo
      const ordenados = [...data].sort(
        (a, b) => (b.idComentario ?? b.id ?? 0) - (a.idComentario ?? a.id ?? 0),
      );
      setComentarios(ordenados);
    } catch {
      // Mantiene la vista funcional aun si el backend se está iniciando
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarComentarios();
  }, []);

  const handleEntregaChange = (val: string) => {
    const num = val === '' ? '' : Number(val);
    setEntregaId(num);
    if (num !== '' && num > 0 && filtroEntregaId !== 'ALL') {
      setFiltroEntregaId(num);
    }
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setFeedback(null);

    if (entregaId === '' || Number(entregaId) <= 0) {
      setFeedback({
        type: 'error',
        message: 'Ingresa un ID de Entrega / Trabajo válido (mayor a 0).',
      });
      return;
    }

    if (usuarioId === '' || Number(usuarioId) <= 0) {
      setFeedback({
        type: 'error',
        message: 'Ingresa un ID de Usuario / Estudiante válido (mayor a 0).',
      });
      return;
    }

    if (!textoComentario.trim()) {
      setFeedback({
        type: 'error',
        message: 'El texto del comentario no puede estar vacío.',
      });
      return;
    }

    setSubmitting(true);
    try {
      const creado = await comentariosApi.crear({
        entregaId: Number(entregaId),
        usuarioId: Number(usuarioId),
        textoComentario: textoComentario.trim(),
      });

      setFeedback({
        type: 'success',
        message:
          "Comentario guardado en MySQL y emitido exitosamente al tópico 'comentarios-academicos-topic' (Key: entregaId).",
        comentario: creado,
      });

      setTextoComentario('');
      setComentarios((prev) => [creado, ...prev]);
      if (filtroEntregaId !== 'ALL') {
        setFiltroEntregaId(Number(entregaId));
      }
    } catch (err) {
      const errorMsg =
        (err as Error).message ??
        'Ocurrió un error al publicar el comentario a través del API Gateway.';
      setFeedback({ type: 'error', message: errorMsg });
    } finally {
      setSubmitting(false);
    }
  };

  const comentariosFiltrados =
    filtroEntregaId === 'ALL'
      ? comentarios
      : comentarios.filter(
          (c) => (c.entregaId ?? c.idEntrega) === Number(filtroEntregaId),
        );

  return (
    <div className="space-y-6">
      <header className="content__header">
        <div className="content__eyebrow">Event-Driven Architecture & Apache Kafka (Tópico 2)</div>
        <h1 className="text-2xl font-bold tracking-tight text-slate-900">
          Gestión de Comentarios Académicos
        </h1>
        <p className="content__subtitle text-slate-600">
          Publica retroalimentación y consultas asociadas a entregas académicas. Cada comentario se
          persiste en MySQL y se emite en tiempo real al tópico{' '}
          <code className="rounded bg-slate-100 px-1.5 py-0.5 font-mono text-xs text-blue-700">
            comentarios-academicos-topic
          </code>{' '}
          particionado por <code>entregaId</code>.
        </p>
      </header>

      {/* Diagrama de arquitectura del Tópico 2 */}
      <section className="pipeline-diagram rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <div className="pipeline-diagram__title text-xs font-semibold uppercase tracking-wider text-slate-500">
          Flujo de Eventos — Tópico 2 (Comentarios Académicos)
        </div>
        <div className="pipeline-steps flex flex-wrap items-center gap-2">
          <div className="pipeline-step pipeline-step--active">
            <span className="pipeline-step__tag">Client</span>
            <span className="pipeline-step__name">React SPA (/comentarios)</span>
          </div>
          <span className="pipeline-arrow">➔</span>
          <div className="pipeline-step pipeline-step--active">
            <span className="pipeline-step__tag">Port :8095</span>
            <span className="pipeline-step__name">Spring Cloud Gateway (JWT)</span>
          </div>
          <span className="pipeline-arrow">➔</span>
          <div className="pipeline-step pipeline-step--active">
            <span className="pipeline-step__tag">Microservice :8092</span>
            <span className="pipeline-step__name">comentarios_service (MySQL)</span>
          </div>
          <span className="pipeline-arrow">➔</span>
          <div className="pipeline-step pipeline-step--active">
            <span className="pipeline-step__tag">Kafka Topic (Key: entregaId)</span>
            <span className="pipeline-step__name">comentarios-academicos-topic</span>
          </div>
        </div>
      </section>

      {/* Banner de confirmación / error */}
      {feedback && (
        <div
          role="alert"
          className={`notification-banner notification-banner--${feedback.type} flex items-center justify-between rounded-md border p-4`}
        >
          <div>
            <strong>{feedback.type === 'success' ? '✓ Operación exitosa:' : '⚠ Error:'}</strong>{' '}
            <span>{feedback.message}</span>
            {feedback.comentario && (
              <div style={{ marginTop: '0.35rem', fontSize: '0.8rem', opacity: 0.9 }}>
                ID Comentario:{' '}
                <strong>#{feedback.comentario.id ?? feedback.comentario.idComentario}</strong> | Key
                (entregaId):{' '}
                <strong>{feedback.comentario.entregaId ?? feedback.comentario.idEntrega}</strong> |
                Usuario:{' '}
                <strong>#{feedback.comentario.usuarioId ?? feedback.comentario.idProfesor}</strong>
              </div>
            )}
          </div>
          <button
            type="button"
            onClick={() => setFeedback(null)}
            style={{ background: 'none', border: 'none', cursor: 'pointer', fontWeight: 'bold' }}
            aria-label="Cerrar alerta"
          >
            ✕
          </button>
        </div>
      )}

      <div className="crear-nota-grid grid grid-cols-1 gap-8 lg:grid-cols-2">
        {/* Formulario de publicación */}
        <section className="card-panel rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="card-panel__title text-lg font-semibold text-slate-900">
            Nuevo Comentario en Entrega
          </h2>
          <p className="card-panel__description text-sm text-slate-500">
            Envía el comentario hacia el API Gateway (<code>POST /api/v1/comentarios</code>).
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="form-field">
              <label htmlFor="entregaId" className="block text-sm font-medium text-slate-700">
                ID de Entrega / Trabajo (<code>entregaId</code>) *
              </label>
              <input
                id="entregaId"
                type="number"
                min="1"
                required
                value={entregaId}
                onChange={(e) => handleEntregaChange(e.target.value)}
                placeholder="Ej. 1"
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-blue-600 focus:outline-none"
              />
              <span className="form-field__hint text-xs text-slate-500">
                Se utiliza como Key de partición en el tópico{' '}
                <code>comentarios-academicos-topic</code>.
              </span>
            </div>

            <div className="form-field">
              <label htmlFor="usuarioId" className="block text-sm font-medium text-slate-700">
                ID del Usuario / Estudiante (<code>usuarioId</code>) *
              </label>
              <input
                id="usuarioId"
                type="number"
                min="1"
                required
                value={usuarioId}
                onChange={(e) => setUsuarioId(e.target.value === '' ? '' : Number(e.target.value))}
                placeholder="Ej. 101"
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-blue-600 focus:outline-none"
              />
              <span className="form-field__hint text-xs text-slate-500">
                Identificador del autor del comentario (estudiante o docente revisor).
              </span>
            </div>

            <div className="form-field">
              <label htmlFor="textoComentario" className="block text-sm font-medium text-slate-700">
                Texto del Comentario (<code>textoComentario</code>) *
              </label>
              <textarea
                id="textoComentario"
                rows={4}
                required
                value={textoComentario}
                onChange={(e) => setTextoComentario(e.target.value)}
                placeholder="Escribe una observación, retroalimentación técnica o consulta sobre la entrega..."
                style={{
                  padding: '0.65rem 0.85rem',
                  border: '1px solid var(--line, #cbd5e1)',
                  borderRadius: '6px',
                  fontSize: '0.9rem',
                  background: '#fff',
                  resize: 'vertical',
                  fontFamily: 'inherit',
                }}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-blue-600 focus:outline-none"
              />
            </div>

            <button
              type="submit"
              className="btn-primary w-full rounded-md bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-700 disabled:opacity-50"
              disabled={submitting}
              style={{ width: '100%', marginTop: '0.5rem' }}
            >
              {submitting ? 'Publicando en MySQL & Kafka…' : 'Publicar Comentario en Kafka'}
            </button>
          </form>
        </section>

        {/* Línea de tiempo / Feed de conversación académica */}
        <section className="card-panel rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <div className="alerts-header flex items-center justify-between">
            <div>
              <h2 className="card-panel__title text-lg font-semibold text-slate-900" style={{ margin: 0 }}>
                Feed de Conversación Académica
              </h2>
              <div style={{ fontSize: '0.78rem', color: 'var(--ink-soft, #64748b)', marginTop: '0.2rem' }}>
                Tópico Kafka: <code>comentarios-academicos-topic</code>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
              <button
                type="button"
                className="btn-secondary"
                onClick={() =>
                  setFiltroEntregaId((prev) =>
                    prev === 'ALL' ? (entregaId !== '' ? Number(entregaId) : 1) : 'ALL',
                  )
                }
                style={{ fontSize: '0.75rem', padding: '0.3rem 0.6rem' }}
              >
                {filtroEntregaId === 'ALL'
                  ? `Filtrar Entrega #${entregaId || 1}`
                  : 'Ver Todas las Entregas'}
              </button>
              <button
                type="button"
                className="btn-secondary"
                onClick={cargarComentarios}
                disabled={loading}
                style={{ fontSize: '0.75rem', padding: '0.3rem 0.6rem' }}
              >
                {loading ? '…' : '↻ Actualizar'}
              </button>
            </div>
          </div>

          <p className="card-panel__description text-sm text-slate-500">
            {filtroEntregaId === 'ALL'
              ? 'Mostrando la línea de tiempo global de comentarios registrados.'
              : `Mostrando comentarios asociados a la Entrega #${filtroEntregaId}.`}
          </p>

          <div className="alerts-list space-y-3">
            {comentariosFiltrados.length === 0 ? (
              <div className="empty-state rounded-md border border-dashed border-slate-200 p-8 text-center text-slate-400">
                <div>💬 No hay comentarios registrados para esta vista.</div>
                <div style={{ fontSize: '0.78rem', marginTop: '0.4rem', color: '#94a3b8' }}>
                  Publica un comentario usando el formulario para emitir un evento a{' '}
                  <code>comentarios-academicos-topic</code>.
                </div>
              </div>
            ) : (
              comentariosFiltrados.map((item) => {
                const id = item.id ?? item.idComentario;
                const entId = item.entregaId ?? item.idEntrega;
                const usrId = item.usuarioId ?? item.idProfesor;
                const texto = item.textoComentario ?? item.contenido;
                const fechaRaw = item.fechaCreacion ?? item.fechaComentario;
                const fechaFormateada = fechaRaw
                  ? new Date(fechaRaw).toLocaleString('es-CL', {
                      dateStyle: 'short',
                      timeStyle: 'medium',
                    })
                  : 'Recién publicado';

                return (
                  <article
                    key={id ?? `${entId}-${usrId}-${fechaRaw}`}
                    style={{
                      borderLeft: '4px solid #2563eb',
                      background: '#f8fafc',
                      borderTop: '1px solid #e2e8f0',
                      borderRight: '1px solid #e2e8f0',
                      borderBottom: '1px solid #e2e8f0',
                      borderRadius: '0 6px 6px 0',
                      padding: '0.9rem 1rem',
                    }}
                    className="rounded-r-md border border-l-4 border-slate-200 border-l-blue-600 bg-slate-50 p-4"
                  >
                    <div className="alert-card__top flex items-center justify-between">
                      <span
                        style={{
                          fontFamily: 'var(--font-mono, monospace)',
                          fontSize: '0.72rem',
                          fontWeight: 700,
                          background: '#dbeafe',
                          color: '#1e40af',
                          padding: '0.15rem 0.5rem',
                          borderRadius: '4px',
                        }}
                      >
                        KEY · ENTREGA #{entId}
                      </span>
                      <span className="alert-card__time text-xs text-slate-400">
                        {fechaFormateada}
                      </span>
                    </div>

                    <div
                      className="alert-card__msg my-2 text-sm text-slate-800"
                      style={{ marginTop: '0.45rem', marginBottom: '0.5rem', fontSize: '0.9rem' }}
                    >
                      {texto}
                    </div>

                    <div className="alert-card__meta flex flex-wrap gap-4 text-xs text-slate-500">
                      <span>Comentario ID: #{id}</span>
                      <span>Usuario / Autor: #{usrId}</span>
                      <span>Tópico: comentarios-academicos-topic</span>
                    </div>
                  </article>
                );
              })
            )}
          </div>
        </section>
      </div>
    </div>
  );
}

export default GestionComentarios;

